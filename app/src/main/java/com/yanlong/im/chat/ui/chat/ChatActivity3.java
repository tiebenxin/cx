package com.yanlong.im.chat.ui.chat;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jrmf360.rplib.JrmfRpClient;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.yanlong.im.R;
import com.yanlong.im.adapter.EmojiAdapter;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.ScrollConfig;
import com.yanlong.im.chat.server.ChatServer;
import com.yanlong.im.chat.server.UpLoadService;
import com.yanlong.im.chat.ui.ChatActivity;
import com.yanlong.im.chat.ui.ChatInfoActivity;
import com.yanlong.im.chat.ui.GroupInfoActivity;
import com.yanlong.im.chat.ui.GroupSelectUserActivity;
import com.yanlong.im.chat.ui.cell.FactoryChatCell;
import com.yanlong.im.chat.ui.cell.ICellEventListener;
import com.yanlong.im.chat.ui.cell.MessageAdapter;
import com.yanlong.im.databinding.ActivityChat2Binding;
import com.yanlong.im.pay.bean.SignatureBean;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.ui.PageIndicator;
import com.yanlong.im.user.ui.UserInfoActivity;

import net.cb.cb.library.base.BaseMvpActivity;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.ScreenUtils;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.SoftKeyBoardListener;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.view.ActionbarView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @anthor Liszt
 * @data 2019/9/19
 * Description
 */
public class ChatActivity3 extends BaseMvpActivity<ChatModel, ChatView, ChatPresenter> implements ICellEventListener, ChatView {
    public static final String AGM_TOUID = "toUId";
    public static final String AGM_TOGID = "toGId";
    private ChatModel mChatModel;
    private boolean isGroup;
    private LinearLayoutManager layoutManager;
    private MessageAdapter adapter;
    private ActivityChat2Binding ui;
    private ActionbarView actionbar;
    private String gid;
    private long uid;
    private Integer font_size;
    private int lastPosition;
    private int lastOffset;
    private boolean isSoftShow;
    private List<View> emojiLayout;
    private final CheckPermission2Util permission2Util = new CheckPermission2Util();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_chat2);
        init();
    }

    private void init() {
        initIntent();
        initEvent();
        intAdapter();
        initUIAndListener();
    }

    private void initEvent() {
        presenter.registerIMListener();
    }


    @Override
    protected void onStart() {
        super.onStart();
        presenter.checkLockMessage();
        presenter.loadAndSetData();
        presenter.initUnreadCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //激活当前会话
        setCurrentSession();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //取消激活会话
        ChatServer.setSessionNull();

    }

    private void setCurrentSession() {
        if (isGroup) {
            ChatServer.setSessionGroup(gid);
        } else {
            ChatServer.setSessionSolo(uid);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        presenter.unregisterIMListener();
        super.onDestroy();
    }

    private void initIntent() {
        gid = getIntent().getStringExtra(AGM_TOGID);
        uid = getIntent().getLongExtra(AGM_TOUID, 0);
        uid = uid == 0 ? -1L : uid;
        isGroup = StringUtil.isNotNull(gid);
        mChatModel.init(gid, uid);
        presenter.init(this);
    }

    @Override
    public ChatModel createModel() {
        mChatModel = new ChatModel();
        return mChatModel;
    }

    @Override
    public ChatView createView() {
        return this;
    }

    @Override
    public ChatPresenter createPresenter() {
        return new ChatPresenter();
    }

    private void intAdapter() {
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ui.recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(context, this, isGroup);
        adapter.setCellFactory(new FactoryChatCell(context, adapter, this));
        ui.recyclerView.setAdapter(adapter);
    }

    @Override
    public void initUIAndListener() {
        actionbar = ui.headView.getActionbar();
        addViewPagerEvent();
        font_size = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        actionbar.getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        if (isGroup) {
            actionbar.getBtnRight().setVisibility(View.GONE);
            ui.viewChatBottom.setVisibility(View.VISIBLE);
        } else {
            actionbar.getBtnRight().setVisibility(View.VISIBLE);
            if (uid == 1L) {
                ui.viewChatBottom.setVisibility(View.GONE);
            } else {
                ui.viewChatBottom.setVisibility(View.VISIBLE);
            }
        }

        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                if (isGroup) {//群聊,单聊
                    toGroupInfoActivity();
                } else {
                    if (uid == 1L) {
                        toUserInfoActivity();
                    } else {
                        toChatInfoActivity();
                    }
                }
            }
        });

        //发送普通消息
        ui.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //test 8.21测试发送
                // if(AppConfig.DEBUG){
                presenter.doSendText(ui.edtChat, isGroup);
            }
        });
        ui.edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ui.btnSend.setVisibility(View.VISIBLE);
                } else {
                    ui.btnSend.setVisibility(View.GONE);
                }
                if (isGroup && !mChatModel.isHaveDraft()) {
                    if (count == 1 && (s.charAt(s.length() - 1) == "@".charAt(0) || s.charAt(s.length() - (s.length() - start)) == "@".charAt(0))) { //添加一个字
                        //跳转到@界面
                        Intent intent = new Intent(ChatActivity3.this, GroupSelectUserActivity.class);
                        intent.putExtra(GroupSelectUserActivity.TYPE, 1);
                        intent.putExtra(GroupSelectUserActivity.GID, gid);
                        startActivityForResult(intent, GroupSelectUserActivity.RET_CODE_SELECTUSR);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ui.btnFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ui.viewFuncRoot.viewFunc.getVisibility() == View.VISIBLE) {
                    hideBt();
                } else {
                    showBtType(0);
                }
            }
        });
        ui.btnEmj.setTag(0);
        ui.btnEmj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ui.viewEmojiPager.emojiPagerCon.getVisibility() == View.VISIBLE) {
                    hideBt();
                    InputUtil.showKeyboard(ui.edtChat);
                    ui.btnEmj.setImageLevel(0);
                } else {
                    showBtType(1);
                    ui.btnEmj.setImageLevel(1);
                }
            }
        });

        //todo  emoji表情处理
        for (int j = 0; j < emojiLayout.size(); j++) {

            GridLayout viewEmojiItem = (GridLayout) emojiLayout.get(j).findViewById(R.id.view_emoji);
            for (int i = 0; i < viewEmojiItem.getChildCount(); i++) {
                if (viewEmojiItem.getChildAt(i) instanceof TextView) {
                    final TextView tv = (TextView) viewEmojiItem.getChildAt(i);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ui.edtChat.getText().insert(ui.edtChat.getSelectionEnd(), tv.getText());
                        }
                    });
                } else {
                    viewEmojiItem.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int keyCode = KeyEvent.KEYCODE_DEL;
                            KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
                            KeyEvent keyEventUp = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
                            ui.edtChat.onKeyDown(keyCode, keyEventDown);
                            ui.edtChat.onKeyUp(keyCode, keyEventUp);
                        }
                    });
                }
            }
        }

        //处理键盘
        SoftKeyBoardListener kbLinst = new SoftKeyBoardListener(this);
        kbLinst.setOnSoftKeyBoardChangeListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int h) {
                hideBt();
                ui.viewChatBottom.setPadding(0, 0, 0, h);


                ui.btnEmj.setImageLevel(0);
                scrollBottom();
                isSoftShow = true;
            }

            @Override
            public void keyBoardHide(int h) {
                ui.viewChatBottom.setPadding(0, 0, 0, 0);
                isSoftShow = false;
            }
        });

        ui.viewFuncRoot.viewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                permission2Util.requestPermissions(ChatActivity3.this, new CheckPermission2Util.Event() {
                    @Override
                    public void onSuccess() {
                        PictureSelector.create(ChatActivity3.this)
                                .openCamera(PictureMimeType.ofImage())
                                .compress(true)
                                .forResult(PictureConfig.REQUEST_CAMERA);
                    }

                    @Override
                    public void onFail() {

                    }
                }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});


            }
        });

        ui.viewFuncRoot.viewPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(ChatActivity3.this)
                        .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(false)// 是否可预览图片 true or false
                        .isCamera(false)// 是否显示拍照按钮 ture or false
                        .compress(true)// 是否压缩 true or false
                        .isGif(true)
                        .selectArtworkMaster(true)
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            }
        });

        //支付宝红包
        ui.viewFuncRoot.viewRbZfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.sendRb();
            }
        });
        ui.viewFuncRoot.viewTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.doTrans();
            }
        });

    }

    /***
     * 隐藏底部所有面板
     */
    private void hideBt() {
        ui.viewFuncRoot.viewFunc.setVisibility(View.GONE);
        ui.viewEmojiPager.emojiPagerCon.setVisibility(View.GONE);
    }


    private void addViewPagerEvent() {
        emojiLayout = new ArrayList<>();
        View view1 = LayoutInflater.from(this).inflate(R.layout.part_chat_emoji, null);
        View view2 = LayoutInflater.from(this).inflate(R.layout.part_chat_emoji2, null);
        emojiLayout.add(view1);
        emojiLayout.add(view2);
        ui.viewEmojiPager.emojiPager.setAdapter(new EmojiAdapter(emojiLayout, ui.edtChat));
        ui.viewEmojiPager.emojiPager.addOnPageChangeListener(new PageIndicator(this, (LinearLayout) findViewById(R.id.dot_hor), 2));
    }

    /***
     * 底部显示面板
     */
    private void showBtType(final int type) {
        ui.btnEmj.setImageLevel(0);
        InputUtil.hideKeyboard(ui.edtChat);
        showVoice(false);
        ui.viewFuncRoot.viewFunc.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideBt();
                switch (type) {
                    case 0://功能面板
                        //第二种解决方案
                        ui.viewFuncRoot.viewFunc.setVisibility(View.VISIBLE);
                        break;
                    case 1://emoji面板
                        ui.viewEmojiPager.emojiPagerCon.setVisibility(View.VISIBLE);
                        break;
                    case 2://语音
                        showVoice(true);
                        break;
                }
                //滚动到结尾 7.5
                scrollBottom();
            }
        }, 50);
    }

    private void showVoice(boolean show) {
        if (show) {//开启语音
            ui.txtVoice.setVisibility(View.VISIBLE);
            ui.edtChat.setVisibility(View.GONE);
        } else {//关闭语音
            ui.txtVoice.setVisibility(View.GONE);
            ui.edtChat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initUnreadCount(String s) {
        actionbar.setTxtLeft(s, R.drawable.shape_unread_bg);
    }

    @Override
    public void replaceListDataAndNotify(MsgAllBean bean) {
        int position = mChatModel.getListData().indexOf(bean);
        if (position >= 0 && position < mChatModel.getListData().size()) {
            adapter.updateItemAndRefresh(bean);
            adapter.notifyItemChanged(position, position);
        }
    }

    @Override
    public void startUploadServer(MsgAllBean bean, String file, boolean isOrigin) {
        UpLoadService.onAdd(bean.getMsg_id(), file, isOrigin, mChatModel.getUid(), mChatModel.getGid(), bean.getTimestamp());
        startService(new Intent(getContext(), UpLoadService.class));
    }

    private void toUserInfoActivity() {
        startActivity(new Intent(getContext(), UserInfoActivity.class).putExtra(UserInfoActivity.ID, uid).putExtra(UserInfoActivity.JION_TYPE_SHOW, 1));
    }

    private void toGroupInfoActivity() {
        startActivity(new Intent(getContext(), GroupInfoActivity.class).putExtra(GroupInfoActivity.AGM_GID, gid));
    }

    private void toChatInfoActivity() {
        startActivity(new Intent(getContext(), ChatInfoActivity.class).putExtra(ChatInfoActivity.AGM_FUID, uid));
    }

    @Override
    public void onEvent(int type, MsgAllBean message, Object... args) {

    }

    @Override
    public void setAndRefreshData(List<MsgAllBean> l) {
        adapter.bindData(l);
        ui.recyclerView.scrollToPosition(adapter.getItemCount() - 1);

    }

    /*
     * @param isMustBottom 是否必须滑动到底部
     * */
    @Override
    public void scrollListView(boolean isMustBottom) {
        if (mChatModel.getListData() != null) {
            int length = mChatModel.getListData().size();//刷新后当前size；
            if (isMustBottom) {
                ui.recyclerView.scrollToPosition(length);
            } else {
                if (lastPosition >= 0 && lastPosition < length) {
                    if (isSoftShow || lastPosition == length - 1 || isCanScrollBottom()) {//允许滑动到底部，或者当前处于底部，canScrollVertically是否能向上 false表示到了底部
                        ui.recyclerView.scrollToPosition(length);
                    }
                } else {
                    SharedPreferencesUtil sp = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.SCROLL);
                    if (sp != null) {
                        ScrollConfig config = sp.get4Json(ScrollConfig.class, "scroll_config");
                        if (config != null) {
                            if (config.getUserId() == UserAction.getMyId()) {
                                if (config.getUid() > 0 && config.getUid() == uid) {
                                    lastPosition = config.getLastPosition();
                                    lastOffset = config.getLastOffset();
                                } else if (!TextUtils.isEmpty(config.getChatId()) && !TextUtils.isEmpty(gid) && config.getChatId().equals(gid)) {
                                    lastPosition = config.getLastPosition();
                                    lastOffset = config.getLastOffset();
                                }
                            }
                        }
                    }
                    if (lastPosition >= 0 && lastPosition < length) {
                        if (isSoftShow || lastPosition == length - 1 || isCanScrollBottom()) {//允许滑动到底部，或者当前处于底部
                            ui.recyclerView.scrollToPosition(length);
                        } else {
                            layoutManager.scrollToPositionWithOffset(lastPosition, lastOffset);
                        }
                    } else {
                        ui.recyclerView.scrollToPosition(length);
                    }
                }
            }
        }
    }

    @Override
    public void notifyDataAndScrollBottom(boolean isScrollBottom) {
        adapter.notifyDataSetChanged();
        scrollListView(isScrollBottom);
    }

    /*
     * 判断是否滑动过屏幕一般高度
     * */
    private boolean isCanScrollBottom() {
        if (isNoFullScreen()) {
            return true;
        }
        if (lastPosition < 0) {
            SharedPreferencesUtil sp = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.SCROLL);
            if (sp != null) {
                ScrollConfig config = sp.get4Json(ScrollConfig.class, "scroll_config");
                if (config != null) {
                    if (config.getUserId() == UserAction.getMyId()) {
                        if (config.getUid() > 0 && config.getUid() == uid) {
                            lastPosition = config.getLastPosition();
                            lastOffset = config.getLastOffset();
                        } else if (!TextUtils.isEmpty(config.getChatId()) && config.getChatId().equals(gid)) {
                            lastPosition = config.getLastPosition();
                            lastOffset = config.getLastOffset();
                        }
                    }
                }
            }
        }

        if (lastPosition >= 0) {
            int targetHeight = ScreenUtils.getScreenHeight(this) / 2;//屏幕一般高度
            int size = mChatModel.getListData().size();
//            int onCreate = size - 1;
            int height = 0;
            for (int i = lastPosition; i < size - 1; i++) {
//                View view = mtListView.getLayoutManager().findViewByPosition(i);//获取不到不可见item
                View view = adapter.getItemViewByPosition(i);
                if (view == null) {
                    break;
                }
                int w = View.MeasureSpec.makeMeasureSpec(ScreenUtils.getScreenWidth(this), View.MeasureSpec.EXACTLY);
                int h = View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.UNSPECIFIED);
                view.measure(w, h);
                if (height + lastOffset < targetHeight) {
                    height += view.getMeasuredHeight();
                } else {
                    //当滑动距离高于屏幕高度的一般，终止当前循环
                    break;
                }
//                LogUtil.getLog().i(ChatActivity.class.getSimpleName(), "isCanScrollBottom -- lastPosition=" + lastPosition + "--height=" + height);
            }
            if (height + lastOffset <= targetHeight) {
                return true;
            }
        }
        return false;
    }

    /*
     * 未填充屏幕
     * */
    private boolean isNoFullScreen() {
        if (!ui.recyclerView.canScrollVertically(1) && !ui.recyclerView.canScrollVertically(-1)) {//既不能上滑也不能下滑，即未满屏的情况
            return true;
        }
        return false;
    }

    private void scrollBottom() {
        ui.recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollListView(true);
            }
        }, 100);

    }

}
