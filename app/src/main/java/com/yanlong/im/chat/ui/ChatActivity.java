package com.yanlong.im.chat.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioRecord;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import com.jrmf360.rplib.JrmfRpClient;
import com.jrmf360.rplib.bean.EnvelopeBean;
import com.jrmf360.rplib.bean.GrabRpBean;
import com.jrmf360.rplib.bean.TransAccountBean;
import com.jrmf360.rplib.utils.callback.GrabRpCallBack;
import com.jrmf360.rplib.utils.callback.TransAccountCallBack;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.ChatMessage;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.GroupConfig;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.chat.bean.MsgNotice;
import com.yanlong.im.chat.bean.ReceiveRedEnvelopeMessage;
import com.yanlong.im.chat.bean.RedEnvelopeMessage;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.TransferMessage;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.server.ChatServer;
import com.yanlong.im.chat.ui.view.ChatItemView;
import com.yanlong.im.pay.action.PayAction;
import com.yanlong.im.pay.bean.SignatureBean;
import com.yanlong.im.pay.ui.MultiRedPacketActivity;
import com.yanlong.im.pay.ui.SingleRedPacketActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.SelectUserActivity;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.audio.AudioPlayManager;
import com.yanlong.im.utils.audio.AudioRecordManager;
import com.yanlong.im.utils.audio.IAdioTouch;
import com.yanlong.im.utils.audio.IAudioPlayListener;
import com.yanlong.im.utils.audio.IAudioRecord;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.EventFindHistory;
import net.cb.cb.library.bean.EventRefreshMainMsg;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.AnimationPic;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.SoftKeyBoardListener;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.TouchUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertTouch;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.MultiListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppActivity {
    //返回需要刷新的
    private static final int REQ_REFRESH = 7779;
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
    private LinearLayout viewChatRobot;
    private View viewChatBottom;
    private View viewChatBottomc;
    private View imgEmojiDel;
    private Button btnSend;
    private Button txtVoice;

    private Integer font_size;

    public static final String AGM_TOUID = "toUId";
    public static final String AGM_TOGID = "toGId";

    private Gson gson = new Gson();
    private CheckPermission2Util permission2Util = new CheckPermission2Util();

    private Long toUId = null;
    private String toGid = null;
    //当前页
    //private int indexPage = 0;
    private List<MsgAllBean> msgListData = new ArrayList<>();

    //红包和转账
    public static final int REQ_RP = 9653;
    public static final int REQ_TRANS = 9653;


    //语音的动画
    private AnimationPic animationPic = new AnimationPic();

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
                    for (MsgBean.UniversalMessage.WrapMessage msg : msgBean.getWrapMsgList()) {
                        onMsgbranch(msg);

                    }


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


                    //ToastUtil.show(context, "发送失败" + bean.getRequestId());
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
                    // ToastUtil.show(getContext(), "离线就禁止发送之类的");
                    //  btnSend.setEnabled(state);
                }
            });
        }
    };

    //消息的分发
    public void onMsgbranch(MsgBean.UniversalMessage.WrapMessage msg) {

        switch (msg.getMsgType()) {

            case DESTROY_GROUP:
                // ToastUtil.show(getApplicationContext(), "销毁群");
                taskGroupConf();
            case REMOVE_GROUP_MEMBER://退出群
                taskGroupConf();
                break;
            case CHANGE_GROUP_NAME:
                taskSessionInfo();
                break;

        }


    }

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
        viewChatBottomc = findViewById(R.id.view_chat_bottom_c);
        viewChatRobot = findViewById(R.id.view_chat_robot);
        imgEmojiDel = findViewById(R.id.img_emoji_del);
        btnSend = findViewById(R.id.btn_send);

        txtVoice = findViewById(R.id.txt_voice);


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
        if (isGroup()) {
            actionbar.getBtnRight().setVisibility(View.GONE);
        } else {
            actionbar.getBtnRight().setVisibility(View.VISIBLE);
        }
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                if (isGroup()) {//群聊,单聊
                    startActivityForResult(new Intent(getContext(), GroupInfoActivity.class)
                            .putExtra(GroupInfoActivity.AGM_GID, toGid), REQ_REFRESH
                    );
                } else {

                    startActivityForResult(new Intent(getContext(), ChatInfoActivity.class)
                            .putExtra(ChatInfoActivity.AGM_FUID, toUId), REQ_REFRESH
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
                }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});


            }
        });
        viewPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PictureSelector.create(ChatActivity.this)
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
        viewRbZfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //   ToastUtil.show(getContext(), "显示红包");
                taskPayRb();


            }
        });
        viewTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTrans();
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
                        } else {
                            ToastUtil.show(getContext(), "留言不能为空");
                        }
                    }
                });
                alertTouch.show();
                alertTouch.setEdHintOrSize(null, 15);


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

        //语音
        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //申请权限 7.2
                permission2Util.requestPermissions(ChatActivity.this, new CheckPermission2Util.Event() {
                    @Override
                    public void onSuccess() {
                        startVoice(null);
                    }

                    @Override
                    public void onFail() {

                    }
                }, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});

            }
        });

        txtVoice.setOnTouchListener(new IAdioTouch(this, new IAdioTouch.MTouchListener() {
            @Override
            public void onDown() {
                txtVoice.setText("松开 结束");
                txtVoice.setBackgroundResource(R.drawable.bg_edt_chat2);

                btnVoice.setEnabled(false);
                btnEmj.setEnabled(false);
                btnFunc.setEnabled(false);

            }

            @Override
            public void onMove() {
                //   txtVoice.setText("滑动 取消");
                //  txtVoice.setBackgroundResource(R.drawable.bg_edt_chat2);
            }

            @Override
            public void onUp() {
                txtVoice.setText("按住 说话");
                txtVoice.setBackgroundResource(R.drawable.bg_edt_chat);

                btnVoice.setEnabled(true);
                btnEmj.setEnabled(true);
                btnFunc.setEnabled(true);

                //  alert.show();

            }
        }));


        AudioRecordManager.getInstance(this).setAudioRecordListener(new IAudioRecord(this, headView, new IAudioRecord.UrlCallback() {
            @Override
            public void getUrl(final String url, final int duration) {
                if (!TextUtils.isEmpty(url)) {
                    //处理ui放在线程
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // alert.dismiss();
                            //发送语音消息
                            MsgAllBean msgAllbean = SocketData.send4Voice(toUId, toGid, url, duration);
                            showSendObj(msgAllbean);
                        }
                    });


                }
            }


        }));

        //群助手
        viewChatRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  ToastUtil.show(getContext(),"群助手");
                if (groupInfo == null)
                    return;

                startActivity(new Intent(getContext(), GroupRobotActivity.class)
                        .putExtra(GroupRobotActivity.AGM_GID, toGid)
                        .putExtra(GroupRobotActivity.AGM_RID, groupInfo.getRobotid())
                );
            }
        });


        if (isGroup()) {//去除群的控件
            viewFunc.removeView(viewAction);
            viewFunc.removeView(viewTransfer);
            viewFunc.removeView(viewTransfer);
            viewChatRobot.setVisibility(View.INVISIBLE);


        } else {

            viewFunc.removeView(viewChatRobot);
        }
        viewFunc.removeView(viewRb);
        //test 6.26
        // viewFunc.removeView(viewTransfer);


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
                            //7.5

                            InputUtil.hideKeyboard(edtChat);
                            hideBt();
                            btnEmj.setImageLevel(0);
                        } else if (isRun == 0) {
                            isRun = 1;
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
                showEndMsg();
            }

            @Override
            public void keyBoardHide(int h) {
                viewChatBottom.setPadding(0, 0, 0, 0);


            }
        });


        //6.15 先加载完成界面,后刷数据
        actionbar.post(new Runnable() {
            @Override
            public void run() {
                taskRefreshMessage();
                taskDraftGet();
            }
        });


    }

    /***
     * 开始语音
     */
    private void startVoice(Boolean open) {
        if (open == null) {
            open = txtVoice.getVisibility() == View.GONE ? true : false;
        }


        if (open) {

            showBtType(2);
        } else {
            showVoice(false);
            hideBt();

        }


    }

    private void showVoice(boolean show) {
        if (show) {//开启语音
            txtVoice.setVisibility(View.VISIBLE);
            edtChat.setVisibility(View.GONE);
        } else {//关闭语音
            txtVoice.setVisibility(View.GONE);
            edtChat.setVisibility(View.VISIBLE);
        }
    }


    /***
     * 底部显示面板
     */
    private void showBtType(final int type) {

        btnEmj.setImageLevel(0);
        InputUtil.hideKeyboard(edtChat);
        showVoice(false);
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
                    case 2://语音
                        showVoice(true);
                        break;
                }
                //滚动到结尾 7.5
                showEndMsg();
            }
        }, 50);
    }

    private void showEndMsg() {
        mtListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mtListView.getListView().scrollToPosition(msgListData.size());
            }
        }, 100);

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
        AudioPlayManager.getInstance().stopPlay();
        super.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventExitChat event) {
        finish();
    }

    @Override
    protected void onStop() {
        taskDarftSet();
        super.onStop();
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
       /* if (flag_isHistory) {
            flag_isHistory = false;
            return;
        }*/
        //6.15 取消每次刷新
        //  taskRefreshMessage();
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
                    for(LocalMedia localMedia:obt){
                        String file = localMedia.getCompressPath();
                        boolean isArtworkMaster = data.getBooleanExtra(PictureConfig.IS_ARTWORK_MASTER, false);
                        if (isArtworkMaster) {
                            //  Toast.makeText(this,"原图",Toast.LENGTH_LONG).show();
                            file = localMedia.getPath();
                        }
                        //1.上传图片
                        // alert.show();
                        final String imgMsgId = SocketData.getUUID();
                        MsgAllBean imgMsgBean = SocketData.send4ImagePre(imgMsgId, toUId, toGid, "file://" + file);
                        imgMsgBean.setSend_state(2);
                        msgListData.add(imgMsgBean);
                        notifyData2Buttom();
                        upFileAction.upFile(getContext(), new UpFileUtil.OssUpCallback() {
                            @Override
                            public void success(final String url) {
                                //2.发送图片
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //alert.dismiss();
                                        MsgAllBean msgAllbean = SocketData.send4Image(imgMsgId, toUId, toGid, url);
                                        // showSendObj(msgAllbean);
                                    }
                                });


                            }

                            @Override
                            public void fail() {
                                //alert.dismiss();
                                ToastUtil.show(getContext(), "上传失败,请稍候重试");

                            }

                            @Override
                            public void inProgress(long progress, long zong) {

                            }
                        }, file);
                    }




                    break;
                case REQ_RP://红包
                    EnvelopeBean envelopeInfo = JrmfRpClient.getEnvelopeInfo(data);
                    if (envelopeInfo != null) {
                        //  ToastUtil.show(getContext(), "红包的回调" + envelopeInfo.toString());
                        String info = envelopeInfo.getEnvelopeMessage();
                        String rid = envelopeInfo.getEnvelopesID();

                        MsgBean.RedEnvelopeMessage.RedEnvelopeStyle style = MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.NORMAL;
                        if (envelopeInfo.getEnvelopeType() == 1) {//拼手气
                            style = MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.LUCK;
                        }


                        MsgAllBean msgAllbean = SocketData.send4Rb(toUId, toGid, rid, info, style);
                        showSendObj(msgAllbean);


                    }
                    break;


            }
        } else if (resultCode == SelectUserActivity.RET_CODE_SELECTUSR) {//选择通讯录中的某个人
            String json = data.getStringExtra(SelectUserActivity.RET_JSON);
            UserInfo userInfo = gson.fromJson(json, UserInfo.class);

            MsgAllBean msgAllbean = SocketData.send4card(toUId, toGid, userInfo.getUid(), userInfo.getHead(), userInfo.getName(), "向你推荐一个人");
            showSendObj(msgAllbean);
        } else if (requestCode == REQ_REFRESH) {//刷新返回时需要刷新聊天列表数据
            mks.clear();
            taskRefreshMessage();
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

        PictureSelector.create(ChatActivity.this)
                .themeStyle(R.style.picture_default_style)
                .isGif(true)
                .openExternalPreview1(pos, selectList);

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
                time = TimeToString.getTimeWx(msgbean.getTimestamp());
            }
            //----------------------------------------
            //昵称处理
            String nikeName = null;
            //5.30
            String headico = msgbean.getFrom_avatar();
            if (isGroup()) {//群聊显示昵称

                //6.14 这里有性能问题
                //  nikeName = msgbean.getFrom_user().getMkName();
                nikeName = StringUtil.isNotNull(nikeName) ? nikeName : msgbean.getFrom_nickname();


            } else {//单聊不显示昵称
                nikeName = null;
            }
            //----------------------------------------
          /*  //7.16 群资料头像昵称统一
            if (isGroup()) {
                UserInfo bean = getGroupInfo(msgbean.getFrom_uid());
                if (bean != null) {
                    nikeName = bean.getMembername();
                    headico = bean.getHead();
                }

            }*/


            //显示数据集

            if (msgbean.isMe()) {
                // headico =
                holder.viewChatItem.setOnHead(null);
            } else {

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


                    RedEnvelopeMessage rb = msgbean.getRed_envelope();


                    Boolean isInvalid = rb.getIsInvalid() == 0 ? false : true;
                    String info = isInvalid ? "已领取" : "领取红包";
                    String title = msgbean.getRed_envelope().getComment();
                    final String rid = rb.getId();
                    final Long touid = msgbean.getFrom_uid();
                    final int style = msgbean.getRed_envelope().getStyle();
                    String type = null;
                    if (rb.getRe_type().intValue() == MsgBean.RedEnvelopeMessage.RedEnvelopeType.MFPAY_VALUE) {
                        type = "云红包";
                    }


                    holder.viewChatItem.setData3(isInvalid, title, info, type, R.color.transparent, new ChatItemView.EventRP() {
                        @Override
                        public void onClick(boolean isInvalid) {
                            if ((isInvalid || msgbean.isMe()) && style == MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.NORMAL_VALUE) {//已领取或者是自己的,看详情,"拼手气的话自己也能抢"
                                //ToastUtil.show(getContext(), "红包详情");
                                taskPayRbDatail(rid);

                            } else {
                                taskPayRbGet(touid, rid);
                            }
                        }
                    });
                    break;

                case 4:
                    holder.viewChatItem.setData4(msgbean.getImage().getUrl(), new ChatItemView.EventPic() {
                        @Override
                        public void onClick(String uri) {
                            //  ToastUtil.show(getContext(), "大图:" + uri);
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
                case 6:

                    TransferMessage ts = msgbean.getTransfer();


                    Boolean isInvalidTs = false;
                    String infoTs = ts.getComment();
                    String titleTs = ts.getTransaction_amount() + "元";
                    final String tsId = ts.getId();
                    String typeTs = "好友转账";


                    holder.viewChatItem.setData6(isInvalidTs, titleTs, infoTs, typeTs, R.color.transparent, new ChatItemView.EventRP() {
                        @Override
                        public void onClick(boolean isInvalid) {
                            tsakTransGet(tsId);
                        }
                    });


                    break;
                case 7:
                    final VoiceMessage vm = msgbean.getVoiceMessage();


                    holder.viewChatItem.setData7(vm.getTime(), msgbean.isRead(), AudioPlayManager.getInstance().isPlay(Uri.parse(vm.getUrl())), new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {

                            if (AudioPlayManager.getInstance().isPlay(Uri.parse(vm.getUrl()))) {
                                AudioPlayManager.getInstance().stopPlay();

                            } else {
                                AudioPlayManager.getInstance().startPlay(context, Uri.parse(vm.getUrl()), new IAudioPlayListener() {
                                    @Override
                                    public void onStart(Uri var1) {

                                        mtListView.getListView().getAdapter().notifyDataSetChanged();


                                    }

                                    @Override
                                    public void onStop(Uri var1) {

                                        mtListView.getListView().getAdapter().notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onComplete(Uri var1) {

                                        mtListView.getListView().getAdapter().notifyDataSetChanged();
                                    }
                                });
                            }

                            //设置为已读
                            if (msgbean.isRead() == false) {
                                msgAction.msgRead(msgbean.getMsg_id(), true);
                                msgbean.setRead(true);
                                mtListView.getListView().getAdapter().notifyDataSetChanged();
                            }


                        }
                    });

                    if (AudioPlayManager.getInstance().isPlay(Uri.parse(vm.getUrl()))) {

                        //  animationPic.start(msgbean.getMsg_id(),vim);
                    } else {
                        // animationPic.stop(msgbean.getMsg_id(),vim);
                    }


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
        mtListView.getListView().scrollToPosition(msgListData.size());
    }

    private void notifyData() {
        mtListView.getListView().getAdapter().notifyDataSetChanged();
    }

    private MsgAction msgAction = new MsgAction();
    private UserDao userDao = new UserDao();
    private MsgDao msgDao = new MsgDao();
    private PayAction payAction = new PayAction();

    /***
     * 获取会话信息
     */
    private void taskSessionInfo() {
        String title = "";
        if (isGroup()) {
            Group ginfo = msgDao.getGroup4Id(toGid);
            title = ginfo.getName();
            //6.15 设置右上角点击
            taskGroupConf();

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

        taskMkName(msgListData);
        mtListView.notifyDataSetChange();
    }

    //  private boolean flag_isHistory = false;

    /***
     * 查询历史
     * @param history
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void taskFinadHistoryMessage(EventFindHistory history) {
        //   flag_isHistory = true;
        msgListData = msgAction.getMsg4UserHistory(toGid, toUId, history.getStime());
        //ToastUtil.show(getContext(),"历史"+msgListData.size());
        taskMkName(msgListData);
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
        taskMkName(msgListData);
        mtListView.notifyDataSetChange();

        ((LinearLayoutManager) mtListView.getListView().getLayoutManager()).scrollToPositionWithOffset(addItem, DensityUtil.dip2px(context, 20f));


    }

    /***
     * 统一处理mkname
     */
    private Map<String, String> mks = new HashMap<>();

    /***
     * 获取统一的昵称
     * @param msgListData
     */
    private void taskMkName(List<MsgAllBean> msgListData) {
        for (MsgAllBean msg : msgListData) {
            if (msg.getMsg_type() == 0) {  //通知类型的不处理
                continue;
            }
            String k = msg.getFrom_uid() + "";
            if (mks.containsKey(k)) {
                String v = mks.get(k);
                if (StringUtil.isNotNull(v))
                    msg.setFrom_nickname(v);
            } else {

                String v = "";
                if (msg.getFrom_uid().longValue() == UserAction.getMyId().longValue()) {
                    Group ginfo = msgDao.getGroup4Id(toGid);
                    if (ginfo != null)
                        v = ginfo.getMygroupName();
                } else {
                    UserInfo userInfo = msg.getFrom_user();
                    if (userInfo != null) {
                        v = userInfo.getMkName();
                    }
                }
                mks.put(k, v);
                if (StringUtil.isNotNull(v))
                    msg.setFrom_nickname(v);
            }


        }
        this.msgListData = msgListData;

    }

    private MsgDao dao = new MsgDao();

    /***
     * 清理已读
     */
    private void taskCleanRead() {
        if (isGroup()) {
            dao.sessionReadClean(toGid, null);
        } else {
            dao.sessionReadClean(null, toUId);
        }

    }

    /***
     * 获取草稿
     */
    private void taskDraftGet() {
        Session session = dao.sessionGet(toGid, toUId);
        if (session == null)
            return;
        if (StringUtil.isNotNull(session.getDraft())) {
            edtChat.setText(session.getDraft());
        }
    }

    /***
     * 设置草稿
     */
    private void taskDarftSet() {
        String df = edtChat.getText().toString();
        dao.sessionDraft(toGid, toUId, df);
        EventBus.getDefault().post(new EventRefreshMainMsg());

    }

    /***
     * 获取群配置,并显示更多按钮
     */
    private void taskGroupConf() {
        if (!isGroup()) {
            return;
        }
        GroupConfig config = dao.groupConfigGet(toGid);
        if (config != null) {
            actionbar.getBtnRight().setVisibility(config.getIsExit() == 1 ? View.GONE : View.VISIBLE);

        }
        taskGroupInfo();
    }

    /***
     * 转账
     */
    private void taskTrans() {
        payAction.SignatureBean(new CallBack<ReturnBean<SignatureBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SignatureBean>> call, Response<ReturnBean<SignatureBean>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    SignatureBean sign = response.body().getData();
                    String token = sign.getSign();
                    UserInfo finfo = userDao.findUserInfo(toUId);
                    UserInfo minfo = UserAction.getMyInfo();

                    JrmfRpClient.transAccount(ChatActivity.this, "" + finfo.getUid(), "" + minfo.getUid(), token,
                            minfo.getName(), minfo.getHead(), finfo.getName4Show(), finfo.getHead(), new TransAccountCallBack() {
                                @Override
                                public void transResult(TransAccountBean transAccountBean) {
                                    String rid = transAccountBean.getTransferOrder();
                                    String info = transAccountBean.getTransferDesc();
                                    String money = transAccountBean.getTransferAmount();
                                    //设置转账消息
                                    MsgAllBean msgAllbean = SocketData.send4Trans(toUId, rid, info, money);
                                    showSendObj(msgAllbean);

                                }
                            });
                }
            }
        });
    }

    /***
     * 发红包
     */
    private void taskPayRb() {
        payAction.SignatureBean(new CallBack<ReturnBean<SignatureBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SignatureBean>> call, Response<ReturnBean<SignatureBean>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    SignatureBean sign = response.body().getData();
                    String token = sign.getSign();
                    if (isGroup()) {
                        UserInfo minfo = UserAction.getMyInfo();
                        Group group = msgDao.getGroup4Id(toGid);

                        JrmfRpClient.sendGroupEnvelopeForResult(ChatActivity.this, "" + toGid, "" + UserAction.getMyId(), token,
                                group.getUsers().size(), minfo.getName(), minfo.getHead(), REQ_RP);
                    } else {

                        UserInfo minfo = UserAction.getMyInfo();
                        JrmfRpClient.sendSingleEnvelopeForResult(ChatActivity.this, "" + toUId, "" + minfo.getUid(), token,
                                minfo.getName(), minfo.getHead(), REQ_RP);
                    }

                }
            }
        });
    }

    /***
     * 红包收
     */
    private void taskPayRbGet(final Long toUId, final String rbid) {
        //红包开记录 test

        //  MsgAllBean msgAllbean = SocketData.send4RbRev(toUId, toGid, rbid);
        //    showSendObj(msgAllbean);

        //test over
        payAction.SignatureBean(new CallBack<ReturnBean<SignatureBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SignatureBean>> call, Response<ReturnBean<SignatureBean>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    SignatureBean sign = response.body().getData();
                    String token = sign.getSign();

                    GrabRpCallBack callBack = new GrabRpCallBack() {
                        @Override
                        public void grabRpResult(GrabRpBean grabRpBean) {
                            if (grabRpBean.isHadGrabRp()) {
                                // ToastUtil.show(getContext(), "抢到了红包" + grabRpBean.toString());
                                MsgAllBean msgAllbean = SocketData.send4RbRev(toUId, toGid, rbid);
                                showSendObj(msgAllbean);
                            }
                        }
                    };
                    if (isGroup()) {
                        UserInfo minfo = UserAction.getMyInfo();
                        JrmfRpClient.openGroupRp(ChatActivity.this, "" + minfo.getUid(), token,
                                minfo.getName(), minfo.getHead(), rbid, callBack);
                    } else {

                        UserInfo minfo = UserAction.getMyInfo();
                        JrmfRpClient.openSingleRp(ChatActivity.this, "" + minfo.getUid(), token,
                                minfo.getName(), minfo.getHead(), rbid, callBack);
                    }

                }
            }
        });
    }

    /**
     * 收转账
     */
    private void tsakTransGet(final String rbid) {

        payAction.SignatureBean(new CallBack<ReturnBean<SignatureBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SignatureBean>> call, Response<ReturnBean<SignatureBean>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    SignatureBean sign = response.body().getData();
                    String token = sign.getSign();
                    UserInfo minfo = UserAction.getMyInfo();

                    JrmfRpClient.openTransDetail(ChatActivity.this, "" + minfo.getUid(), token,
                            rbid, new TransAccountCallBack() {
                                @Override
                                public void transResult(TransAccountBean transAccountBean) {
                                    if (transAccountBean.getTransferStatus().equals(1)) {//收钱成功
                                        //改变收钱状态

                                    } else if (transAccountBean.getTransferStatus().equals(0)) {//收到转账信息

                                    } else {//退回
                                        //改变收钱状态
                                    }
                                    transAccountBean.getTransferOrder();
                                }
                            });

                }
            }
        });
    }

    /***
     * 红包详情
     * @param rid
     */
    private void taskPayRbDatail(final String rid) {
     /*   if (!isGroup()) {
            return;
        }*/
        payAction.SignatureBean(new CallBack<ReturnBean<SignatureBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SignatureBean>> call, Response<ReturnBean<SignatureBean>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    SignatureBean sign = response.body().getData();
                    String token = sign.getSign();


                    // if (isGroup()) {
                    UserInfo minfo = UserAction.getMyInfo();
                    JrmfRpClient.openRpDetail(ChatActivity.this, "" + minfo.getUid(), token, rid, minfo.getName(), minfo.getHead());
                   /* } else {
                        ToastUtil.show(getContext(), "单人没有红包详情");

                    }*/

                }
            }
        });

    }

    private Group groupInfo;

    //获取群资料
    private UserInfo getGroupInfo(long uid) {
        if (groupInfo == null)
            return null;
        List<UserInfo> users = groupInfo.getUsers();
        for (UserInfo uinfo : users) {
            if (uinfo.getUid().longValue() == uid) {
                return uinfo;
            }
        }
        return  null;
    }

    /***
     * 获取群信息
     */
    private void taskGroupInfo() {
        msgAction.groupInfo(toGid, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (response.body() == null)
                    return;

                groupInfo = response.body().getData();
                if (groupInfo == null) {//取不到群信息了
                    groupInfo = new Group();
                    groupInfo.setMaster("");
                    groupInfo.setUsers(new RealmList<UserInfo>());
                }

                if (groupInfo.getMaster().equals(UserAction.getMyId().toString())) {//本人群主
                    viewChatRobot.setVisibility(View.VISIBLE);
                } else {
                    viewFunc.removeView(viewChatRobot);
                }

                //如果自己不在群里面
                boolean isExit = false;
                for (UserInfo uifo : groupInfo.getUsers()) {
                    if (uifo.getUid().longValue() == UserAction.getMyId().longValue()) {
                        isExit = true;
                    }

                }
                if (!isExit) {
                    actionbar.getBtnRight().setVisibility(View.GONE);
                } else {
                    actionbar.getBtnRight().setVisibility(View.VISIBLE);
                }


            }
        });
    }


}
