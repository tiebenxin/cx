package com.yanlong.im.chat.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.ChatMessage;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.chat.bean.MsgNotice;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.server.ChatServer;
import com.yanlong.im.chat.ui.view.ChatItemView;
import com.yanlong.im.pay.ui.MultiRedPacketActivity;
import com.yanlong.im.pay.ui.SingleRedPacketActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.SelectUserActivity;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.EventFindHistory;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.SoftKeyBoardListener;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertTouch;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.MultiListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private net.cb.cb.library.view.MultiListView mtListView;
    private ImageView btnVoice;
    private EditText edtChat;
    private ImageView btnEmj;
    private ImageView btnFunc;
    private GridLayout viewFunc;
    private GridLayout viewEmoji;
    private LinearLayout viewPic;
    private LinearLayout viewCamera;
    private LinearLayout viewRb;
    private LinearLayout viewRbZfb;
    private LinearLayout viewAction;
    private LinearLayout viewTransfer;
    private LinearLayout viewCard;
    private View viewChatBottom;
    private View imgEmojiDel;
    private Button btnSend;

    private Integer font_size;

    public static final String AGM_TOUID = "toUId";
    public static final String AGM_TOGID = "toGId";

    private Gson gson = new Gson();
    private CheckPermission2Util permission2Util=new  CheckPermission2Util();

    private Long toUId = null;
    private String toGid = null;
    //当前页
    //private int indexPage = 0;
    private List<MsgAllBean> msgListData = new ArrayList<>();



/*    private void initChatInfo(){
        toUId = null;
        toGid = null;
        indexPage = 0;
        msgListData.clear();
    }*/

    private boolean isGroup() {
        return StringUtil.isNotNull(toGid);
    }

    //消息监听事件
    private SocketEvent msgEvent = new SocketEvent() {
        @Override
        public void onHeartbeat() {

        }

        @Override
        public void onACK(final MsgBean.AckMessage bean) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bean.getRejectType() == MsgBean.RejectType.NOT_FRIENDS_OR_GROUP_MEMBER) {
                        taskRefreshMessage();

                        //5.23 发送失败,对方拒收
                        MsgAllBean notbean = new MsgAllBean();
                        notbean.setMsg_type(0);
                        notbean.setTimestamp(bean.getTimestamp());
                        notbean.setFrom_uid(UserAction.getMyId());

                        MsgNotice note = new MsgNotice();
                        note.setNote("消息发送成功,但对方已拒收");
                        notbean.setMsgNotice(note);

                        msgListData.add(notbean);
                        mtListView.notifyDataSetChange();
                    } else {
                        taskRefreshMessage();
                    }

                }
            });
        }

        @Override
        public void onMsg(final com.yanlong.im.utils.socket.MsgBean.UniversalMessage msgBean) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //从数据库读取消息
                    taskRefreshMessage();
                }
            });


        }

        @Override
        public void onSendMsgFailure(final MsgBean.UniversalMessage.Builder bean) {
            LogUtil.getLog().e("TAG", "发送失败" + bean.getRequestId());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    ToastUtil.show(context, "发送失败" + bean.getRequestId());
                    MsgAllBean msgAllBean = MsgConversionBean.ToBean(bean.getWrapMsg(0), bean);
                    msgAllBean.setSend_state(1);
                    //  msgAllBean.setMsg_id("重发" + msgAllBean.getRequest_id());
                    ///这里写库
                    msgAllBean.setSend_data(bean.build().toByteArray());
                    DaoUtil.update(msgAllBean);


                    taskRefreshMessage();


                }
            });
        }

        @Override
        public void onLine(boolean state) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //离线就禁止发送之类的
                    ToastUtil.show(getContext(), "离线就禁止发送之类的");
                    //  btnSend.setEnabled(state);
                }
            });
        }
    };


    //自动寻找控件
    private void findViews() {
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        mtListView = (net.cb.cb.library.view.MultiListView) findViewById(R.id.mtListView);
        btnVoice = (ImageView) findViewById(R.id.btn_voice);
        edtChat = (EditText) findViewById(R.id.edt_chat);
        btnEmj = (ImageView) findViewById(R.id.btn_emj);
        btnFunc = (ImageView) findViewById(R.id.btn_func);
        viewFunc = (GridLayout) findViewById(R.id.view_func);
        viewEmoji = (GridLayout) findViewById(R.id.view_emoji);
        viewPic = (LinearLayout) findViewById(R.id.view_pic);
        viewCamera = (LinearLayout) findViewById(R.id.view_camera);
        viewRb = (LinearLayout) findViewById(R.id.view_rb);
        viewRbZfb = (LinearLayout) findViewById(R.id.view_rb_zfb);
        viewAction = (LinearLayout) findViewById(R.id.view_action);
        viewTransfer = (LinearLayout) findViewById(R.id.view_transfer);
        viewCard = (LinearLayout) findViewById(R.id.view_card);
        viewChatBottom = findViewById(R.id.view_chat_bottom);
        imgEmojiDel = findViewById(R.id.img_emoji_del);
        btnSend = findViewById(R.id.btn_send);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permission2Util.onRequestPermissionsResult();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //发送并滑动到列表底部
    private void showSendObj(MsgAllBean msgAllbean) {

        //    msgListData.add(msgAllbean);
        //    notifyData2Buttom();
        taskRefreshMessage();

    }

    //自动生成的控件事件
    private void initEvent() {


        toGid = getIntent().getStringExtra(AGM_TOGID);
        toUId = getIntent().getLongExtra(AGM_TOUID, 0);
        toUId = toUId == 0 ? null : toUId;
        taskSessionInfo();
        actionbar.getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        actionbar.getBtnRight().setVisibility(View.VISIBLE);
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                if (isGroup()) {//群聊,单聊
                    startActivity(new Intent(getContext(), GroupInfoActivity.class)
                            .putExtra(GroupInfoActivity.AGM_GID, toGid)
                    );
                } else {

                    startActivity(new Intent(getContext(), ChatInfoActivity.class)
                            .putExtra(ChatInfoActivity.AGM_FUID, toUId)
                    );

                }

            }
        });

        //设置字体大小
        font_size = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);

        //注册消息监听
        SocketUtil.getSocketUtil().addEvent(msgEvent);
        //发送普通消息
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //发送普通消息
                MsgAllBean msgAllbean = SocketData.send4Chat(toUId, toGid, edtChat.getText().toString());
                showSendObj(msgAllbean);
                edtChat.getText().clear();

            }
        });
        edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btnSend.setVisibility(View.VISIBLE);
                } else {
                    btnSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewFunc.getVisibility() == View.VISIBLE) {
                    hideBt();
                } else {
                    showBtType(0);
                }

            }
        });
        btnEmj.setTag(0);
        btnEmj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewEmoji.getVisibility() == View.VISIBLE) {

                    hideBt();
                    InputUtil.showKeyboard(edtChat);


                    btnEmj.setImageLevel(0);
                } else {


                    showBtType(1);
                    btnEmj.setImageLevel(1);
                }


            }
        });
        //emoji表情处理
        for (int i = 0; i < viewEmoji.getChildCount(); i++) {
            if (viewEmoji.getChildAt(i) instanceof TextView) {
                final TextView tv = (TextView) viewEmoji.getChildAt(i);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        edtChat.getText().insert(edtChat.getSelectionEnd(), tv.getText());
                    }
                });
            }

        }
        imgEmojiDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int keyCode = KeyEvent.KEYCODE_DEL;
                KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
                KeyEvent keyEventUp = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
                edtChat.onKeyDown(keyCode, keyEventDown);
                edtChat.onKeyUp(keyCode, keyEventUp);

            }
        });


        viewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permission2Util.requestPermissions(ChatActivity.this, new CheckPermission2Util.Event() {
                  @Override
                  public void onSuccess() {
                      PictureSelector.create(ChatActivity.this)
                              .openCamera(PictureMimeType.ofImage())
                              .compress(true)
                              .forResult(PictureConfig.CHOOSE_REQUEST);
                  }

                  @Override
                  public void onFail() {

                  }
              },new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE});



            }
        });
        viewPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(ChatActivity.this)

                        .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(false)// 是否可预览图片 true or false
                        .isCamera(false)// 是否显示拍照按钮 ture or false
                        .compress(true)// 是否压缩 true or false
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            }
        });
        //支付宝红包
        viewRbZfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                ToastUtil.show(getContext(), "显示红包");
                if (isGroup()) {
                    intent = new Intent(ChatActivity.this, MultiRedPacketActivity.class);
                } else {
                    intent = new Intent(ChatActivity.this, SingleRedPacketActivity.class);
                }


                startActivity(intent);
            }
        });
        //戳一下
        viewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertTouch alertTouch = new AlertTouch();
                alertTouch.init(ChatActivity.this, "请输入戳一下消息", "确定", R.mipmap.ic_chat_actionme, new AlertTouch.Event() {
                    @Override
                    public void onON() {

                    }

                    @Override
                    public void onYes(String content) {
                        if (!TextUtils.isEmpty(content)) {
                            //发送普通消息
                            MsgAllBean msgAllbean = SocketData.send4action(toUId, toGid, content);
                            showSendObj(msgAllbean);
                        }
                    }
                });
                alertTouch.show();


            }
        });
        //名片
        viewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go(SelectUserActivity.class);
                startActivityForResult(new Intent(getContext(), SelectUserActivity.class), SelectUserActivity.RET_CODE_SELECTUSR);
            }
        });


        mtListView.init(new RecyclerViewAdapter());
        mtListView.getLoadView().setStateNormal();
        mtListView.setEvent(new MultiListView.Event() {


            @Override
            public void onRefresh() {
                //  indexPage++;
                taskMoreMessage();
            }

            @Override
            public void onLoadMore() {

            }

            @Override
            public void onLoadFail() {

            }
        });

        mtListView.getListView().setOnTouchListener(new View.OnTouchListener() {
            int isRun = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        isRun = 1;


                        break;
                    case MotionEvent.ACTION_UP:
                        isRun = 0;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isRun == 1) {
                            isRun = 2;
                            InputUtil.hideKeyboard(edtChat);
                            hideBt();

                            btnEmj.setImageLevel(0);
                        }

                        break;

                }

                return false;
            }
        });

        //处理键盘
        SoftKeyBoardListener kbLinst = new SoftKeyBoardListener(this);
        kbLinst.setOnSoftKeyBoardChangeListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int h) {
                hideBt();
                viewChatBottom.setPadding(0, 0, 0, h);


                btnEmj.setImageLevel(0);
            }

            @Override
            public void keyBoardHide(int h) {
                viewChatBottom.setPadding(0, 0, 0, 0);


            }
        });

        taskRefreshMessage();


    }

    /***
     * 底部显示面板
     */
    private void showBtType(final int type) {

        btnEmj.setImageLevel(0);
        InputUtil.hideKeyboard(edtChat);

        viewFunc.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideBt();
                switch (type) {
                    case 0://功能面板
                        //第二种解决方案

                        viewFunc.setVisibility(View.VISIBLE);

                        break;
                    case 1://emoji面板

                        viewEmoji.setVisibility(View.VISIBLE);

                        break;
                }
            }
        }, 50);
    }

    /***
     * 隐藏底部所有面板
     */
    private void hideBt() {

        viewFunc.setVisibility(View.GONE);
        viewEmoji.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (viewFunc.getVisibility() == View.VISIBLE) {
            viewFunc.setVisibility(View.GONE);
            return;
        }
        if (viewEmoji.getVisibility() == View.VISIBLE) {
            viewEmoji.setVisibility(View.GONE);
            btnEmj.setImageLevel(0);
            return;
        }

        //清理会话数量
        taskCleanRead();
        super.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventExitChat event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        //取消监听
        SocketUtil.getSocketUtil().removeEvent(msgEvent);
        EventBus.getDefault().unregister(this);

        super.onDestroy();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        EventBus.getDefault().register(this);
        findViews();
        initEvent();
    }


    @Override
    protected void onResume() {
        super.onResume();

        //激活当前会话
        if (isGroup()) {
            ChatServer.setSessionGroup(toGid);
        } else {
            ChatServer.setSessionSolo(toUId);
        }


        //刷新页面数据
        if (flag_isHistory) {
            flag_isHistory = false;
            return;
        }
        taskRefreshMessage();
        //刷新群资料
        taskSessionInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //取消激活会话
        ChatServer.setSessionNull();

    }

    private UpFileAction upFileAction = new UpFileAction();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> obt = PictureSelector.obtainMultipleResult(data);
                    String file =obt.get(0).getCompressPath();
                    //1.上传图片
                    upFileAction.upFile(getContext(), new UpFileUtil.OssUpCallback() {
                        @Override
                        public void success(String url) {
                            //2.发送图片

                            MsgAllBean msgAllbean = SocketData.send4Image(toUId, toGid, url);
                            showSendObj(msgAllbean);
                        }

                        @Override
                        public void fail() {
                            //  ToastUtil.show(getContext(),"上传失败");

                        }

                        @Override
                        public void inProgress(long progress, long zong) {

                        }
                    }, file);


                    break;
            }
        } else if (resultCode == SelectUserActivity.RET_CODE_SELECTUSR) {//选择通讯录中的某个人
            String json = data.getStringExtra(SelectUserActivity.RET_JSON);
            UserInfo userInfo = gson.fromJson(json, UserInfo.class);

            MsgAllBean msgAllbean = SocketData.send4card(toUId, toGid, userInfo.getUid(), userInfo.getHead(), userInfo.getName(), "向你推荐一个好人");
            showSendObj(msgAllbean);
        }
    }

    //显示大图
    private void showBigPic(String uri) {
        List<LocalMedia> selectList = new ArrayList<>();
        int pos = 0;


        for (MsgAllBean msgl : msgListData) {
            if (msgl.getMsg_type().intValue() == 4) {

                if (uri.contains(msgl.getImage().getUrl())) {
                    pos = selectList.size();
                }

                LocalMedia lc = new LocalMedia();
                lc.setPath(msgl.getImage().getUrl());
                selectList.add(lc);

            }
        }

        PictureSelector.create(ChatActivity.this).themeStyle(R.style.picture_default_style).openExternalPreview(pos, selectList);

    }


    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        @Override
        public int getItemCount() {
            return msgListData == null ? 0 : msgListData.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder holder, int position) {
            final MsgAllBean msgbean = msgListData.get(position);

            //时间戳合并
            String time = null;
            if (position > 0 && (msgbean.getTimestamp() - msgListData.get(position - 1).getTimestamp()) < (60 * 1000)) { //小于60秒隐藏时间
                time = null;
            } else {
                time = TimeToString.YYYY_MM_DD_HH_MM_SS(msgbean.getTimestamp());
            }
            //----------------------------------------
            //昵称处理
            String nikeName = null;
            if (isGroup()) {//群聊显示昵称

                nikeName = msgbean.getFrom_user().getMkName();
                nikeName = StringUtil.isNotNull(nikeName) ? nikeName : msgbean.getFrom_nickname();


            } else {//单聊不显示昵称
                nikeName = null;
            }
            //----------------------------------------


            //显示数据集
            //  String headico = msgbean.getFrom_user() == null ? "" : msgbean.getFrom_user().getHead();
            //5.30
            String headico = msgbean.getFrom_avatar();
            if (msgbean.isMe()) {
                // headico =
                holder.viewChatItem.setOnHead(null);
            } else {
                //msgbean.getFrom_user().getHead();
                //  headico = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1327564550,2587085231&fm=26&gp=0.jpg";
                holder.viewChatItem.setOnHead(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), UserInfoActivity.class)
                                .putExtra(UserInfoActivity.ID, msgbean.getFrom_uid()));
                    }
                });
            }
            holder.viewChatItem.setShowType(msgbean.getMsg_type(), msgbean.isMe(), headico, nikeName, time);
            switch (msgbean.getMsg_type()) {
                case 0:
                    // holder.viewChatItem.setShowType(0, msgbean.isMe(), null, "昵称", null);
                    if (msgbean.getMsgNotice() != null)
                        holder.viewChatItem.setData0(msgbean.getMsgNotice().getNote());
                    break;
                case 1:


                    holder.viewChatItem.setData1(msgbean.getChat().getMsg());
                    break;
                case 2:

                    holder.viewChatItem.setData2(msgbean.getStamp().getComment());
                    break;

                case 3:
                    holder.viewChatItem.setData3(false, "test红包", msgbean.getRed_envelope().getComment(), null, 0, null);
                    break;

                case 4:
                    holder.viewChatItem.setData4(msgbean.getImage().getUrl(), new ChatItemView.EventPic() {
                        @Override
                        public void onClick(String uri) {
                            ToastUtil.show(getContext(), "大图:" + uri);
                            showBigPic(uri);
                        }
                    });
                    break;
                case 5:
                    holder.viewChatItem.setData5(msgbean.getBusiness_card().getNickname(),
                            msgbean.getBusiness_card().getComment(),
                            msgbean.getBusiness_card().getAvatar(), null, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // ToastUtil.show(getContext(), "添加好友需要详情页面");

                                    startActivity(new Intent(getContext(), UserInfoActivity.class)
                                            .putExtra(UserInfoActivity.ID, msgbean.getBusiness_card().getUid()));
                                }
                            });
                    break;


            }

            //------------------------------------------

            //发送状态处理
            holder.viewChatItem.setErr(msgbean.getSend_state());//
            holder.viewChatItem.setOnErr(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //从数据拉出来,然后再发送
                    MsgAllBean remsg = DaoUtil.findOne(MsgAllBean.class, "request_id", msgbean.getRequest_id());

                    try {
                        MsgBean.UniversalMessage.Builder bean = MsgBean.UniversalMessage.parseFrom(remsg.getSend_data()).toBuilder();
                        SocketUtil.getSocketUtil().sendData4Msg(bean);
                        //点击发送的时候如果要改变成发送中的状态
                        remsg.setSend_state(2);
                        DaoUtil.update(remsg);

                        taskRefreshMessage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });


            //----------------------------------------


        }


        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_chat_com, view, false));
            if (font_size != null)
                holder.viewChatItem.setFont(font_size);
            return holder;
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private com.yanlong.im.chat.ui.view.ChatItemView viewChatItem;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                viewChatItem = (com.yanlong.im.chat.ui.view.ChatItemView) convertView.findViewById(R.id.view_chat_item);
            }
        }
    }


    private void notifyData2Buttom() {
        mtListView.getListView().getAdapter().notifyDataSetChanged();
        mtListView.getListView().smoothScrollToPosition(msgListData.size());
    }

    private MsgAction msgAction = new MsgAction();
    private UserDao userDao = new UserDao();
    private MsgDao msgDao = new MsgDao();

    /***
     * 获取会话信息
     */
    private void taskSessionInfo() {
        String title = "";
        if (isGroup()) {
            Group ginfo = msgDao.getGroup4Id(toGid);
            title = ginfo.getName();
        } else {
            UserInfo finfo = userDao.findUserInfo(toUId);
            title = finfo.getName4Show();
        }

        actionbar.setTitle(title);
    }

    /***
     * 获取最新的
     */
    private void taskRefreshMessage() {

        //  msgListData = msgAction.getMsg4User(toGid, toUId, indexPage);
        msgListData = msgAction.getMsg4User(toGid, toUId, null);
        notifyData2Buttom();
    }

    private boolean flag_isHistory = false;

    /***
     * 查询历史
     * @param history
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void taskFinadHistoryMessage(EventFindHistory history) {
        flag_isHistory = true;
        msgListData = msgAction.getMsg4UserHistory(toGid, toUId, history.getStime());
        //ToastUtil.show(getContext(),"历史"+msgListData.size());

        mtListView.getListView().getAdapter().notifyDataSetChanged();

        mtListView.getListView().smoothScrollToPosition(0);

    }


    /***
     * 加载更多
     */
    private void taskMoreMessage() {

        int addItem = msgListData.size();

        //  msgListData.addAll(0, msgAction.getMsg4User(toGid, toUId, page));
        msgListData.addAll(0, msgAction.getMsg4User(toGid, toUId, msgListData.get(0).getTimestamp()));

        addItem = msgListData.size() - addItem;

        mtListView.notifyDataSetChange();

        ((LinearLayoutManager) mtListView.getListView().getLayoutManager()).scrollToPositionWithOffset(addItem, DensityUtil.dip2px(context, 20f));


    }

    private MsgDao dao = new MsgDao();

    private void taskCleanRead() {
        if (isGroup()) {
            dao.sessionReadClean(toGid, null);
        } else {
            dao.sessionReadClean(null, toUId);
        }

    }


}
