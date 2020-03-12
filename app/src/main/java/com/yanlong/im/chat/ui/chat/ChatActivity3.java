package com.yanlong.im.chat.ui.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.ScrollConfig;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.server.UpLoadService;
import com.yanlong.im.chat.ui.ChatInfoActivity;
import com.yanlong.im.chat.ui.GroupInfoActivity;
import com.yanlong.im.chat.ui.GroupSelectUserActivity;
import com.yanlong.im.chat.ui.cell.ControllerNewMessage;
import com.yanlong.im.chat.ui.cell.FactoryChatCell;
import com.yanlong.im.chat.ui.cell.ICellEventListener;
import com.yanlong.im.chat.ui.cell.MessageAdapter;
import com.yanlong.im.databinding.ActivityChat2Binding;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.PatternUtil;
import com.yanlong.im.utils.audio.AudioRecordManager;
import com.yanlong.im.utils.audio.IAdioTouch;
import com.yanlong.im.utils.audio.IAudioRecord;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.view.face.FaceViewPager;
import com.yanlong.im.view.face.bean.FaceBean;
import com.yanlong.im.view.function.ChatExtendMenuView;

import net.cb.cb.library.base.BaseMvpActivity;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.ScreenUtils;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.SoftKeyBoardListener;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.NewPullRefreshLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * @author Liszt
 * @date 2019/9/19
 * Description 聊天界面（优化版）
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
    private long uid = -1;
    private Integer font_size;
    private int lastPosition;
    private int lastOffset;
    private boolean isSoftShow;
    private List<View> emojiLayout;
    private final CheckPermission2Util permission2Util = new CheckPermission2Util();
    private int survivalTime;
    private ControllerNewMessage viewNewMessage;

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
        survivalTime = new UserDao().getReadDestroy(uid, gid);
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
        MessageManager.getInstance().setSessionNull();

    }

    private void setCurrentSession() {
        if (isGroup) {
            MessageManager.getInstance().setSessionGroup(gid);
        } else {
            MessageManager.getInstance().setSessionSolo(uid);
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initUIAndListener() {
        actionbar = ui.headView.getActionbar();
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
                presenter.doSendText(ui.edtChat, isGroup, survivalTime);
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
                ui.btnFunc.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ui.viewExtendMenu.getVisibility() == View.VISIBLE) {
                            hideBt();
                        } else {
                            showBtType(ChatEnum.EShowType.FUNCTION);
                        }
                    }
                }, 100);

            }
        });
        ui.btnEmj.setTag(0);
        ui.btnEmj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ui.viewFace.getVisibility() == View.VISIBLE) {
                    hideBt();
                    InputUtil.showKeyboard(ui.edtChat);
                    changeEmojiLevel(0);
                } else {
                    showBtType(ChatEnum.EShowType.EMOJI);
                    changeEmojiLevel(1);
                }
            }
        });

        // 表情点击事件
        ui.viewFace.setOnItemClickListener(new FaceViewPager.FaceClickListener() {

            @Override
            public void OnItemClick(FaceBean bean) {
                presenter.sendFace(bean);
                ui.viewFace.addOftenUseFace(bean);
            }
        });
        // 删除表情按钮
        ui.viewFace.setOnDeleteListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int selection = ui.edtChat.getSelectionStart();
                String msg = ui.edtChat.getText().toString().trim();
                if (selection >= 1) {
                    if (selection >= PatternUtil.FACE_EMOJI_LENGTH) {
                        String emoji = msg.substring(selection - PatternUtil.FACE_EMOJI_LENGTH, selection);
                        if (PatternUtil.isExpression(emoji)) {
                            ui.edtChat.getText().delete(selection - PatternUtil.FACE_EMOJI_LENGTH, selection);
                            return;
                        }
                    }
                    ui.edtChat.getText().delete(selection - 1, selection);
                }
            }
        });


        //处理键盘
        SoftKeyBoardListener kbLinst = new SoftKeyBoardListener(this);
        kbLinst.setOnSoftKeyBoardChangeListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int h) {
                hideBt();
                ui.viewChatBottom.setPadding(0, 0, 0, h);
                changeEmojiLevel(0);
                scrollBottom();
                isSoftShow = true;
            }

            @Override
            public void keyBoardHide(int h) {
                ui.viewChatBottom.setPadding(0, 0, 0, 0);
                isSoftShow = false;
            }
        });

        //语音
        ui.btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //申请权限 7.2
                permission2Util.requestPermissions(ChatActivity3.this, new CheckPermission2Util.Event() {
                    @Override
                    public void onSuccess() {
                        startVoiceUI(null);
                    }

                    @Override
                    public void onFail() {

                    }
                }, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});

            }
        });

        ui.txtVoice.setOnTouchListener(new IAdioTouch(this, new IAdioTouch.MTouchListener() {
            @Override
            public void onDown() {
                ui.txtVoice.setText("松开 结束");
                ui.txtVoice.setBackgroundResource(R.drawable.bg_edt_chat2);
                ui.btnVoice.setEnabled(false);
                ui.btnEmj.setEnabled(false);
                ui.btnFunc.setEnabled(false);

            }

            @Override
            public void onMove() {
                //   txtVoice.setText("滑动 取消");
                //  txtVoice.setBackgroundResource(R.drawable.bg_edt_chat2);
            }

            @Override
            public void onUp() {
                ui.txtVoice.setText("按住 说话");
                ui.txtVoice.setBackgroundResource(R.drawable.bg_edt_chat);
                ui.btnVoice.setEnabled(true);
                ui.btnEmj.setEnabled(true);
                ui.btnFunc.setEnabled(true);
            }
        }));

        AudioRecordManager.getInstance(this).setAudioRecordListener(new IAudioRecord(this, ui.headView, new IAudioRecord.UrlCallback() {
            @Override
            public void completeRecord(String file, int duration) {
                VoiceMessage voice = SocketData.createVoiceMessage(SocketData.getUUID(), file, duration);
                MsgAllBean msg = SocketData.sendFileUploadMessagePre(voice.getMsgId(), uid, gid, SocketData.getFixTime(), voice, ChatEnum.EMessageType.VOICE);
                mChatModel.getListData().add(msg);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyAndScrollBottom();
                    }
                });
                presenter.uploadVoice(file, msg);
            }
        }));

        ui.viewRefresh.setOnRefreshListener(new NewPullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadAndSetMoreData();
                ui.viewRefresh.setRefreshing(false);
            }
        });

        ui.recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {

                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        //获取可视的第一个view
                        lastPosition = layoutManager.findLastVisibleItemPosition();
                        View topView = layoutManager.getChildAt(lastPosition);
                        if (topView != null) {
                            //获取与该view的底部的偏移量
                            lastOffset = topView.getBottom();
                        }

                        saveScrollPosition();
                    }
                }
            }
        });
        viewNewMessage = new ControllerNewMessage(ui.viewNewMessage);
//        viewNewMessage.setClickListener(() -> {
//            if (mChatModel.getListData() == null) {
//                return;
//            }
//            int position = mChatModel.getListData() .size() - unreadCount;
//            if (position >= 0) {
//                scrollChatToPosition(position);
//            } else {
//                scrollChatToPosition(0);
//            }
//            viewNewMessage.setVisible(false);
//            unreadCount = 0;
//        });

        //6.15 先加载完成界面,后刷数据
        actionbar.post(new Runnable() {
            @Override
            public void run() {
                presenter.setAndClearDraft();
            }
        });
        initExtendFunctionView();


        //9.17 进去后就清理会话的阅读数量
        mChatModel.clearUnreadCount();
        MessageManager.getInstance().notifyRefreshMsg();

    }


    @Override
    public void setDraft(String draft) {
        ui.edtChat.setText(draft);
    }

    private void saveScrollPosition() {
        if (lastPosition > 0) {
            SharedPreferencesUtil sp = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.SCROLL);
            ScrollConfig config = new ScrollConfig();
            config.setUserId(UserAction.getMyId());
            if (uid <= 0) {
                config.setChatId(gid);
            } else {
                config.setUid(uid);
            }
            config.setLastPosition(lastPosition);
            config.setLastOffset(lastOffset);
            if (mChatModel.getTotalSize() > 0) {
                config.setTotalSize(mChatModel.getTotalSize());
            }
            sp.save2Json(config, "scroll_config");
        }
    }

    /*
     * notifyAndScrollBottom
     * */
    public void notifyAndScrollBottom() {
        if (adapter != null) {
            adapter.bindData(mChatModel.getListData(), false);
        }
        scrollListView(true);
    }


    /***
     * 隐藏底部所有面板
     */
    public void hideBt() {
        ui.viewExtendMenu.setVisibility(View.GONE);
        ui.viewFace.setVisibility(View.GONE);

    }

    @Override
    public void insertEditContent(CharSequence charSequence) {
        ui.edtChat.getText().insert(ui.edtChat.getSelectionStart(), charSequence);
    }

    @Override
    public void changeEmojiLevel(int level) {
        ui.btnEmj.setImageLevel(level);
    }

    @Override
    public void addAtSpan(String maskText, String showText, long uid) {
        ui.edtChat.addAtSpan(maskText, showText, uid);
    }

    /***
     * 底部显示面板
     */
    private void showBtType(final int type) {
        changeEmojiLevel(0);
        InputUtil.hideKeyboard(ui.edtChat);
        showVoice(false);
        ui.viewExtendMenu.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideBt();
                switch (type) {
                    case ChatEnum.EShowType.FUNCTION://功能面板
                        ui.viewExtendMenu.setVisibility(View.VISIBLE);
                        break;
                    case ChatEnum.EShowType.EMOJI://emoji面板
                        ui.viewFace.setVisibility(View.VISIBLE);
                        break;
                    case ChatEnum.EShowType.VOICE://语音
                        showVoice(true);
                        break;
                }
                //滚动到结尾
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
        actionbar.setTxtLeft(s, R.drawable.shape_unread_bg, DensityUtil.sp2px(ChatActivity3.this, 5));
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
        UpLoadService.onAddImage(bean, file, isOrigin);
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
        adapter.bindData(l, false);
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

    @Override
    public void bindData(List<MsgAllBean> l) {
        if (adapter != null) {
            adapter.bindData(l, true);
        }
    }

    @Override
    public void scrollToPositionWithOff(int position, int offset) {
        layoutManager.scrollToPositionWithOffset(position, offset);
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

    /*
     * 是否已经退出
     * */
    @Override
    public void setBanView(boolean isExited) {
        actionbar.getBtnRight().setVisibility(isExited ? View.GONE : View.VISIBLE);
        ui.tvBan.setVisibility(isExited ? VISIBLE : GONE);
        ui.viewChatBottomC.setVisibility(isExited ? GONE : VISIBLE);
    }

    @Override
    public void setRobotView(boolean isMaster) {
        //群信息可能有变化，群主显示机器人
        initExtendData();
    }

    /*
     * taskSessionInfo
     * */
    @Override
    public void initTitle() {
        String title = "";
        if (mChatModel.isGroup()) {
            title = mChatModel.getGroupName();
            presenter.taskGroupConf();
        } else {
            UserInfo info = mChatModel.getUserInfo();
            title = info.getName4Show();
            if (info.getLastonline() > 0) {
                if (NetUtil.isNetworkConnected()) {
                    actionbar.setTitleMore(TimeToString.getTimeOnline(info.getLastonline(), info.getActiveType(), true), true);
                } else {
                    actionbar.setTitleMore(TimeToString.getTimeOnline(info.getLastonline(), info.getActiveType(), true), false);
                }
            }
        }
        actionbar.setChatTitle(title);
    }

    @Override
    public void updateOnlineStatus() {
        String title = "";
        if (!isGroup) {
            UserInfo info = mChatModel.getUserInfo();
            title = info.getName4Show();
            if (info.getLastonline() > 0) {
                if (NetUtil.isNetworkConnected()) {
                    actionbar.setTitleMore(TimeToString.getTimeOnline(info.getLastonline(), info.getActiveType(), true), true);
                } else {
                    actionbar.setTitleMore(TimeToString.getTimeOnline(info.getLastonline(), info.getActiveType(), true), false);
                }
            }
            actionbar.setTitle(title);
        }
    }

    @Override
    public void addAndShowSendMessage(MsgAllBean bean) {
        if (bean.getMsg_type() != ChatEnum.EMessageType.MSG_CANCEL) {
            int size = mChatModel.getListData().size();
            mChatModel.getListData().add(bean);
            adapter.addMessage(bean);
            adapter.notifyItemRangeInserted(size, 1);
            // 处理发送失败时位置错乱问题
//            adapter.notifyItemRangeChanged(size + 1, size - 1);

            //红包通知 不滚动到底部
            if (bean.getMsgNotice() != null && (bean.getMsgNotice().getMsgType() == ChatEnum.ENoticeType.RECEIVE_RED_ENVELOPE
                    || bean.getMsgNotice().getMsgType() == ChatEnum.ENoticeType.RED_ENVELOPE_RECEIVED_SELF)) {
                return;
            }
            scrollListView(true);
        } else {
            presenter.loadAndSetData();
        }
    }

    @Override
    public void startUploadService() {
        startService(new Intent(getContext(), UpLoadService.class));
    }

    @Override
    public void startVoiceUI(Boolean open) {
        if (open == null) {
            open = ui.txtVoice.getVisibility() == View.GONE ? true : false;
        }
        if (open) {
            showBtType(ChatEnum.EShowType.VOICE);
        } else {
            showVoice(false);
            hideBt();
            InputUtil.showKeyboard(ui.edtChat);
            ui.edtChat.requestFocus();
        }
    }

    //初始化拓展功能栏
    private void initExtendFunctionView() {
        ui.viewExtendMenu.setListener(new ChatExtendMenuView.OnFunctionListener() {
            @Override
            public void onClick(int id) {
                switch (id) {
                    case ChatEnum.EFunctionId.GALLERY:
                        presenter.toGallery();
                        break;
                    case ChatEnum.EFunctionId.TAKE_PHOTO:
                        presenter.toCamera();
                        break;
                    case ChatEnum.EFunctionId.ENVELOPE_SYS:
                        presenter.toSystemEnvelope();
                        break;
                    case ChatEnum.EFunctionId.TRANSFER:
                        presenter.toTransfer();
                        break;
                    case ChatEnum.EFunctionId.VIDEO_CALL:
                        presenter.toVideoCall();
                        break;
                    case ChatEnum.EFunctionId.ENVELOPE_MF:
                        presenter.taskPayRb();
                        break;
                    case ChatEnum.EFunctionId.LOCATION:
                        presenter.toLocation();
                        break;
                    case ChatEnum.EFunctionId.STAMP:
                        presenter.toStamp();
                        break;
                    case ChatEnum.EFunctionId.CARD:
                        presenter.toCard();
                        break;
                    case ChatEnum.EFunctionId.GROUP_ASSISTANT:
                        presenter.toGroupRobot();
                        break;
                    case ChatEnum.EFunctionId.FILE:
                        break;
                }
            }
        });
        initExtendData();
    }

    //初始化底边拓展栏数据
    private void initExtendData() {
        ui.viewExtendMenu.bindDate(mChatModel.getItemModels());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }
}
