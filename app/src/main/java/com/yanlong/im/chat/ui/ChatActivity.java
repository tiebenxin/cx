package com.yanlong.im.chat.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

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
import com.yalantis.ucrop.util.FileUtils;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.BusinessCardMessage;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.GroupConfig;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.chat.bean.MsgNotice;
import com.yanlong.im.chat.bean.RedEnvelopeMessage;
import com.yanlong.im.chat.bean.ScrollConfig;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.TransferMessage;
import com.yanlong.im.chat.bean.UserSeting;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.interf.IMenuSelectListener;
import com.yanlong.im.chat.server.ChatServer;
import com.yanlong.im.chat.server.UpLoadService;
import com.yanlong.im.chat.ui.cell.FactoryChatCell;
import com.yanlong.im.chat.ui.cell.ICellEventListener;
import com.yanlong.im.chat.ui.cell.MessageAdapter;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.chat.ui.view.ChatItemView;
import com.yanlong.im.pay.action.PayAction;
import com.yanlong.im.pay.bean.SignatureBean;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.SelectUserActivity;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.HtmlTransitonUtils;
import com.yanlong.im.utils.audio.AudioPlayManager;
import com.yanlong.im.utils.audio.AudioRecordManager;
import com.yanlong.im.utils.audio.IAdioTouch;
import com.yanlong.im.utils.audio.IAudioRecord;
import com.yanlong.im.utils.audio.IVoicePlayListener;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.EventFindHistory;
import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.bean.EventRefreshMainMsg;
import net.cb.cb.library.bean.EventUpImgLoadEvent;
import net.cb.cb.library.bean.EventUserOnlineChange;
import net.cb.cb.library.bean.EventVoicePlay;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.AnimationPic;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.RunUtils;
import net.cb.cb.library.utils.ScreenUtils;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.SoftKeyBoardListener;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertTouch;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.MlistAdapter;
import net.cb.cb.library.view.MsgEditText;
import net.cb.cb.library.view.MultiListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmList;
import me.kareluo.ui.OptionMenu;
import me.kareluo.ui.OptionMenuView;
import me.kareluo.ui.PopupMenuView;
import retrofit2.Call;
import retrofit2.Response;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class ChatActivity extends AppActivity implements ICellEventListener {
    private static String TAG = "ChatActivity";
    //返回需要刷新的 8.19 取消自动刷新
    // public static final int REQ_REFRESH = 7779;
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private net.cb.cb.library.view.MultiListView mtListView;
    private ImageView btnVoice;
    private MsgEditText edtChat;
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


    private MessageAdapter messageAdapter;
    private int currentPager;
    private int lastOffset = -1;
    private int lastPosition = -1;
    private boolean isNewAdapter = false;
    private int preTotalSize = 0;//刷新前，总item数
    private boolean isSoftShow;
    private Map<Integer, View> viewMap = new HashMap<>();
    private boolean needRefresh;


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

                        ToastUtil.show(getContext(), "消息发送成功,但对方已拒收");
                    } else {

                        if (UpLoadService.getProgress(bean.getMsgId(0)) == null /*|| UpLoadService.getProgress(bean.getMsgId(0)) == 100*/) {//忽略图片上传的刷新,图片上传成功后
                            for (String msgid : bean.getMsgIdList()) {
                                //撤回消息不做刷新
                                if (ChatServer.getCancelList().containsKey(msgid)) {
                                    Log.i(TAG, "onACK: 收到取消回执,等待刷新列表2");
                                    return;
                                }

                            }


                            taskRefreshMessage();
//                            LogUtil.getLog().i(ChatActivity.class.getSimpleName(), "taskRefreshMessage");
                        }
                    }
                }
            });
        }

        @Override
        public void onMsg(final com.yanlong.im.utils.socket.MsgBean.UniversalMessage msgBean) {


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    needRefresh = false;

                    for (MsgBean.UniversalMessage.WrapMessage msg : msgBean.getWrapMsgList()) {
                        //8.7 是属于这个会话就刷新
                        if (!needRefresh) {


                            if (isGroup()) {
                                needRefresh = msg.getGid().equals(toGid);

                            } else {
                                needRefresh = msg.getFromUid() == toUId.longValue();
                            }


                            if (msg.getMsgType() == MsgBean.MessageType.OUT_GROUP) {//提出群的消息是以个人形式发的

                                needRefresh = msg.getOutGroup().getGid().equals(toGid);
                            }

                            if (msg.getMsgType() == MsgBean.MessageType.REMOVE_GROUP_MEMBER) {//提出群的消息是以个人形式发的

                                needRefresh = msg.getRemoveGroupMember().getGid().equals(toGid);
                            }

                        }


                        onMsgbranch(msg);

                    }


                    //从数据库读取消息
                    if (needRefresh) {
                        LogUtil.getLog().i(TAG, "需要刷新");
                        taskRefreshMessage();

//                        if (isSoftShow || lastPosition == msgListData.size() - 1 || lastPosition == -1) {
//                            taskRefreshMessage();
//                            needRefresh = false;
//                        }
                    }

                }
            });


        }

        @Override
        public void onSendMsgFailure(final MsgBean.UniversalMessage.Builder bean) {
            LogUtil.getLog().e("TAG", "发送失败" + bean.getRequestId());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //撤回处理
                    if (bean.getWrapMsg(0).getMsgType() == MsgBean.MessageType.CANCEL) {
                        ToastUtil.show(getContext(), "撤回失败");
                        return;
                    }


                    //ToastUtil.show(context, "发送失败" + bean.getRequestId());
                    MsgAllBean msgAllBean = MsgConversionBean.ToBean(bean.getWrapMsg(0), bean);
                    if (msgAllBean.getMsg_type().intValue() == ChatEnum.EMessageType.MSG_CENCAL) {//取消的指令不保存到数据库
                        return;
                    }
                    msgAllBean.setSend_state(ChatEnum.ESendStatus.ERROR);
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
            case ACCEPT_BE_GROUP://邀请进群刷新
                taskGroupConf();
                break;
            case CHANGE_GROUP_NAME:
                taskSessionInfo();
                break;


        }

    }

    //自动寻找控件
    private void findViews() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        mtListView = findViewById(R.id.mtListView);
        btnVoice = findViewById(R.id.btn_voice);
        edtChat = findViewById(R.id.edt_chat);
        btnEmj = findViewById(R.id.btn_emj);
        btnFunc = findViewById(R.id.btn_func);
        viewFunc = findViewById(R.id.view_func);
        viewEmoji = findViewById(R.id.view_emoji);
        viewPic = findViewById(R.id.view_pic);
        viewCamera = findViewById(R.id.view_camera);
        viewRb = findViewById(R.id.view_rb);
        viewRbZfb = findViewById(R.id.view_rb_zfb);
        viewAction = findViewById(R.id.view_action);
        viewTransfer = findViewById(R.id.view_transfer);
        viewCard = findViewById(R.id.view_card);
        viewChatBottom = findViewById(R.id.view_chat_bottom);
        viewChatBottomc = findViewById(R.id.view_chat_bottom_c);
        viewChatRobot = findViewById(R.id.view_chat_robot);
        imgEmojiDel = findViewById(R.id.img_emoji_del);
        btnSend = findViewById(R.id.btn_send);
        txtVoice = findViewById(R.id.txt_voice);
        setChatImageBackground();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permission2Util.onRequestPermissionsResult();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //发送并滑动到列表底部
    private void showSendObj(MsgAllBean msgAllbean) {

        //    msgListData.add(msgAllbean);
        //    notifyData2Bottom();
        taskRefreshMessage();

    }

    //自动生成的控件事件
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initEvent() {


        toGid = getIntent().getStringExtra(AGM_TOGID);
        toUId = getIntent().getLongExtra(AGM_TOUID, 0);
        toUId = toUId == 0 ? null : toUId;
        taskSessionInfo();
        actionbar.getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        if (isGroup()) {
            actionbar.getBtnRight().setVisibility(View.GONE);
            viewChatBottom.setVisibility(View.VISIBLE);
        } else {
            actionbar.getBtnRight().setVisibility(View.VISIBLE);
            if (toUId == 1L) {
                viewChatBottom.setVisibility(View.GONE);
            } else {
                viewChatBottom.setVisibility(View.VISIBLE);

            }
        }

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
                    if (toUId == 1L) {
                        startActivity(new Intent(getContext(), UserInfoActivity.class)
                                .putExtra(UserInfoActivity.ID, toUId)
                                .putExtra(UserInfoActivity.JION_TYPE_SHOW, 1));
                    } else {
                        startActivity(new Intent(getContext(), ChatInfoActivity.class)
                                .putExtra(ChatInfoActivity.AGM_FUID, toUId)
                        );
                    }

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
                //test 8.21测试发送
                // if(AppConfig.DEBUG){
                String txt = edtChat.getText().toString();
                if (txt.startsWith("@000")) {
                    int count = Integer.parseInt(txt.split("_")[1]);
                    taskTestSend(count);
                    return;
                }
                //  }
                //--------

                if (isGroup() && edtChat.getUserIdList() != null && edtChat.getUserIdList().size() > 0) {
                    if (edtChat.isAtAll()) {
                        MsgAllBean msgAllbean = SocketData.send4At(toUId, toGid, edtChat.getText().toString(), 1, edtChat.getUserIdList());
                        showSendObj(msgAllbean);
                        edtChat.getText().clear();
                    } else {
                        MsgAllBean msgAllbean = SocketData.send4At(toUId, toGid, edtChat.getText().toString(), 0, edtChat.getUserIdList());
                        showSendObj(msgAllbean);
                        edtChat.getText().clear();
                    }
                } else {
                    //发送普通消息
                    MsgAllBean msgAllbean = SocketData.send4Chat(toUId, toGid, edtChat.getText().toString());
                    showSendObj(msgAllbean);
                    edtChat.getText().clear();
                }
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

                if (isGroup() && !dao.isSaveDraft(toGid)) {
                    if (count == 1 && (s.charAt(s.length() - 1) == "@".charAt(0) || s.charAt(s.length() - (s.length() - start)) == "@".charAt(0))) { //添加一个字
                        //跳转到@界面
                        Intent intent = new Intent(ChatActivity.this, GroupSelectUserActivity.class);
                        intent.putExtra(GroupSelectUserActivity.TYPE, 1);
                        intent.putExtra(GroupSelectUserActivity.GID, toGid);
                        startActivityForResult(intent, GroupSelectUserActivity.RET_CODE_SELECTUSR);

                    }
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
                                .forResult(PictureConfig.REQUEST_CAMERA);
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
//            @Override
//            public void getUrl(final String url, final int duration) {
//                if (!TextUtils.isEmpty(url)) {
//                    //处理ui放在线程
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            // alert.dismiss();
//                            //发送语音消息
//                            MsgAllBean msgAllbean = SocketData.send4Voice(toUId, toGid, url, duration);
//                            showSendObj(msgAllbean);
//                        }
//                    });
//                }
//            }


            @Override
            public void completeRecord(String file, int duration) {
                VoiceMessage voice = SocketData.createVoiceMessage(SocketData.getUUID(), file, duration);
                MsgAllBean msg = SocketData.sendFileUploadMessagePre(voice.getMsgid(), toUId, toGid, voice, ChatEnum.EMessageType.VOICE);
//                replaceListDataAndNotify(msg);
                msgListData.add(msg);
                notifyData2Bottom(true);

                uploadVoice(file, msg);
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
            //viewFunc.removeView(viewTransfer);
            viewChatRobot.setVisibility(View.INVISIBLE);


        } else {

            viewFunc.removeView(viewChatRobot);
        }
        viewFunc.removeView(viewRb);
        //test 6.26
        viewFunc.removeView(viewTransfer);

        if (!isNewAdapter) {
            mtListView.init(new RecyclerViewAdapter());
        } else {
            initAdapter();//messageAdapter
        }
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

        mtListView.getListView().setOnScrollListener(new RecyclerView.OnScrollListener() {
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


        //处理键盘
        SoftKeyBoardListener kbLinst = new SoftKeyBoardListener(this);
        kbLinst.setOnSoftKeyBoardChangeListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int h) {
                hideBt();
                viewChatBottom.setPadding(0, 0, 0, h);


                btnEmj.setImageLevel(0);
                showEndMsg();
                isSoftShow = true;
            }

            @Override
            public void keyBoardHide(int h) {
                viewChatBottom.setPadding(0, 0, 0, 0);
                isSoftShow = false;
            }
        });


        //6.15 先加载完成界面,后刷数据
        actionbar.post(new Runnable() {
            @Override
            public void run() {
//                taskRefreshMessage();
                taskDraftGet();
            }
        });


    }

    private void uploadVoice(String file, final MsgAllBean bean) {
        updateSendStatus(ChatEnum.ESendStatus.SENDING, bean);
        new UpFileAction().upFile(UpFileAction.PATH.VOICE, context, new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
//                if (callback != null) {
//                    Log.v("AudioUploadPath", url + "");
//                    callback.getUrl(url, duration);
//                }
//                发送语音消息
//                MsgAllBean msgAllbean = SocketData.send4Voice(toUId, toGid, url, duration);
                Log.v(ChatActivity.class.getSimpleName(), "上传语音成功--" + url);
                VoiceMessage voice = bean.getVoiceMessage();
                voice.setUrl(url);
                SocketData.sendMessage(bean);
//                showSendObj(bean);
            }

            @Override
            public void fail() {
                updateSendStatus(ChatEnum.ESendStatus.ERROR, bean);
//                ToastUtil.show(context, "发送失败!");
            }

            @Override
            public void inProgress(long progress, long zong) {
            }
        }, file);
    }

    private void updateSendStatus(@ChatEnum.ESendStatus int status, MsgAllBean bean) {
        bean.setSend_state(status);
        msgDao.fixStataMsg(bean.getMsg_id(), status);
        replaceListDataAndNotify(bean);
    }

    private void taskTestSend(final int count) {
        ToastUtil.show(getContext(), "连续发送" + count + "测试开始");
        new RunUtils(new RunUtils.Enent() {
            @Override
            public void onRun() {

                try {
                    for (int i = 1; i <= count; i++) {
                        if (i % 10 == 0)
                            SocketData.send4Chat(toUId, toGid, "连续测试发送" + i + "-------");
                        else
                            SocketData.send4Chat(toUId, toGid, "连续测试发送" + i);

                        if (i % 100 == 0)
                            Thread.sleep(2 * 1000);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMain() {
                notifyData2Bottom(false);
            }
        }).run();
    }

    private void saveScrollPosition() {
        if (lastPosition > 0) {
            SharedPreferencesUtil sp = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.SCROLL);
            ScrollConfig config = new ScrollConfig();
            config.setUserId(UserAction.getMyId());
            if (toUId == null) {
                config.setChatId(toGid);
            } else {
                config.setUid(toUId);
            }
            config.setLastPosition(lastPosition);
            config.setLastOffset(lastPosition);
            if (msgListData != null) {
                config.setTotalSize(msgListData.size());
            }
            sp.save2Json(config, "scroll_config");
        }

    }

    private void clearScrollPosition() {
        SharedPreferencesUtil sp = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.SCROLL);
        sp.clear();
    }


    private void initAdapter() {
        messageAdapter = new MessageAdapter(this, this, isGroup());
        FactoryChatCell factoryChatCell = new FactoryChatCell(this, messageAdapter, this);
        messageAdapter.setCellFactory(factoryChatCell);
        mtListView.init(messageAdapter);
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
                scrollListView(true);
            }
        }, 100);

    }

    /*
     * @param isMustBottom 是否必须滑动到底部
     * */
    private void scrollListView(boolean isMustBottom) {
        if (msgListData != null) {
            int length = msgListData.size();//刷新后当前size；
            if (isMustBottom) {
                mtListView.getListView().scrollToPosition(length);
            } else {
                if (lastPosition >= 0 && lastPosition < length) {
                    //!mtListView.getListView().canScrollVertically(1) 不怎么有效
                    if (isSoftShow || lastPosition == length - 1 || isCanScrollBottom()) {//允许滑动到底部，或者当前处于底部，canScrollVertically是否能向上 false表示到了底部
                        mtListView.getListView().scrollToPosition(length);
                    } else {
//                        LogUtil.getLog().i(ChatActivity.class.getSimpleName(), "scrollListView -- lastPosition=" + lastPosition + "--lastOffset=" + lastOffset);
//                        mtListView.getLayoutManager().scrollToPositionWithOffset(lastPosition, lastOffset);
                    }
                } else {
                    SharedPreferencesUtil sp = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.SCROLL);
                    if (sp != null) {
                        ScrollConfig config = sp.get4Json(ScrollConfig.class, "scroll_config");
                        if (config != null) {
                            if (config.getUserId() == UserAction.getMyId()) {
                                if (toUId != null && config.getUid() > 0 && config.getUid() == toUId) {
                                    lastPosition = config.getLastPosition();
                                    lastOffset = config.getLastOffset();
                                } else if (!TextUtils.isEmpty(config.getChatId()) && !TextUtils.isEmpty(toGid) && config.getChatId().equals(toGid)) {
                                    lastPosition = config.getLastPosition();
                                    lastOffset = config.getLastOffset();
                                }
                            }
                        }
                    }
                    if (lastPosition >= 0 && lastPosition < length) {
                        if (isSoftShow || lastPosition == length - 1 || isCanScrollBottom()) {//允许滑动到底部，或者当前处于底部
                            mtListView.getListView().scrollToPosition(length);
                        } else {

                            mtListView.getLayoutManager().scrollToPositionWithOffset(lastPosition, lastOffset);
                        }
                    } else {
                        mtListView.getListView().scrollToPosition(length);
                    }
                }
            }
        }
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
        Log.v(TAG, "onBackPressed");
        clearScrollPosition();
        super.onBackPressed();
        //oppo 手机 调用 onBackPressed不会finish
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventExitChat event) {
        onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventUserOnlineChange event) {
        updateUserOnlineStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventCheckVoice(EventVoicePlay event) {
        checkMoreVoice(event.getPosition(), (MsgAllBean) event.getBean());
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        taskDarftSet();
        //取消监听
        SocketUtil.getSocketUtil().removeEvent(msgEvent);
        EventBus.getDefault().unregister(this);
        Log.v(TAG, "onDestroy");
        super.onDestroy();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        EventBus.getDefault().register(this);
        findViews();
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!msgDao.isMsgLockExist(toGid, toUId)) {
            msgDao.insertOrUpdateMessage(msgAction.createMessageLock(toGid, toUId));
        }
        initData();

    }

    private void initData() {
        taskRefreshMessage();
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
                case PictureConfig.REQUEST_CAMERA:
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> obt = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia localMedia : obt) {
                        String file = localMedia.getCompressPath();

                        final boolean isArtworkMaster = requestCode == PictureConfig.REQUEST_CAMERA ? true : data.getBooleanExtra(PictureConfig.IS_ARTWORK_MASTER, false);
                        boolean isGif = FileUtils.isGif(file);
                        if (isArtworkMaster || isGif) {
                            //  Toast.makeText(this,"原图",Toast.LENGTH_LONG).show();
                            file = localMedia.getPath();
                        }
                        //1.上传图片
                        // alert.show();
                        final String imgMsgId = SocketData.getUUID();
                        ImageMessage imageMessage = SocketData.createImageMessage(imgMsgId, "file://" + file, isArtworkMaster);
                        MsgAllBean imgMsgBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, imageMessage, ChatEnum.EMessageType.IMAGE);

                        msgListData.add(imgMsgBean);
                        UpLoadService.onAdd(imgMsgId, file, isArtworkMaster, toUId, toGid);
                        startService(new Intent(getContext(), UpLoadService.class));
                    }
                    notifyData2Bottom(true);


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
                case GroupSelectUserActivity.RET_CODE_SELECTUSR:
                    String uid = data.getStringExtra(GroupSelectUserActivity.UID);
                    String name = data.getStringExtra(GroupSelectUserActivity.MEMBERNAME);
                    if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(name)) {
                        edtChat.addAtSpan(null, name, Long.valueOf(uid));
                    }
                    break;

            }
        } else if (resultCode == SelectUserActivity.RET_CODE_SELECTUSR) {//选择通讯录中的某个人
            String json = data.getStringExtra(SelectUserActivity.RET_JSON);
            UserInfo userInfo = gson.fromJson(json, UserInfo.class);

            MsgAllBean msgAllbean = SocketData.send4card(toUId, toGid, userInfo.getUid(), userInfo.getHead(), userInfo.getName(), "向你推荐一个人");
            showSendObj(msgAllbean);
        }/* else if (resultCode == REQ_REFRESH) {//刷新返回时需要刷新聊天列表数据
            mks.clear();
            taskRefreshMessage();
        }*/
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void taskRefreshMessageEvent(EventRefreshChat event) {
        taskRefreshMessage();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void taskUpImgEvevt(EventUpImgLoadEvent event) {
//        Log.d("tag", "taskUpImgEvevt 0: ===============>" + event.getState());
        if (event.getState() == 0) {
            // Log.d("tag", "taskUpImgEvevt 0: ===============>"+event.getMsgid());
            taskRefreshImage(event.getMsgid());
        } else if (event.getState() == -1) {
            //处理失败的情况
            Log.d("tag", "taskUpImgEvevt -1: ===============>" + event.getMsgid());
            MsgAllBean msgAllbean = (MsgAllBean) event.getMsgAllBean();
            replaceListDataAndNotify(msgAllbean);


        } else if (event.getState() == 1) {
            //  Log.d("tag", "taskUpImgEvevt 1: ===============>"+event.getMsgid());
            MsgAllBean msgAllbean = (MsgAllBean) event.getMsgAllBean();
            replaceListDataAndNotify(msgAllbean);


        } else {
            //  Log.d("tag", "taskUpImgEvevt 2: ===============>"+event.getMsgid());
        }
    }

    private void setChatImageBackground() {
        UserSeting seting = new MsgDao().userSetingGet();
        switch (seting.getImageBackground()) {
            case 1:
                mtListView.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_100));
                break;
            case 2:
                mtListView.setBackgroundResource(R.mipmap.bg_image1);
                break;
            case 3:
                mtListView.setBackgroundResource(R.mipmap.bg_image2);
                break;
            case 4:
                mtListView.setBackgroundResource(R.mipmap.bg_image3);
                break;
            case 5:
                mtListView.setBackgroundResource(R.mipmap.bg_image4);
                break;
            case 6:
                mtListView.setBackgroundResource(R.mipmap.bg_image5);
                break;
            case 7:
                mtListView.setBackgroundResource(R.mipmap.bg_image6);
                break;
            case 8:
                mtListView.setBackgroundResource(R.mipmap.bg_image7);
                break;
            case 9:
                mtListView.setBackgroundResource(R.mipmap.bg_image8);
                break;
            default:
                mtListView.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_100));
                break;
        }

    }


    /***
     * 替换listData中的某条消息并且刷新
     * @param msgAllbean
     */
    private void replaceListDataAndNotify(MsgAllBean msgAllbean) {

        if (msgListData == null)
            return;

        int position = msgListData.indexOf(msgAllbean);
        if (position >= 0 && position < msgListData.size()) {
            if (!isNewAdapter) {
                msgListData.set(position, msgAllbean);
            } else {
                messageAdapter.updateItemAndRefresh(msgAllbean);
            }
            Log.i(TAG, "replaceListDataAndNotify: 只刷新" + position);
            mtListView.getListView().getAdapter().notifyItemChanged(position, position);
//            LogUtil.getLog().i("replaceListDataAndNotify", "position=" + position);
        }
    }

    /***
     * 更新图片需要的进度
     * @param msgid
     */
    private void taskRefreshImage(String msgid) {
        if (msgListData == null)
            return;
        for (int i = 0; i < msgListData.size(); i++) {
            if (msgListData.get(i).getMsg_id().equals(msgid)) {
                // Log.d("xxxx", "taskRefreshImage: "+msgid);
                mtListView.getListView().getAdapter().notifyItemChanged(i, i);
            }
        }

    }

    //显示大图
    private void showBigPic(String msgid, String uri) {
        List<LocalMedia> selectList = new ArrayList<>();
        int pos = 0;

        List<MsgAllBean> listdata = msgAction.getMsg4UserImg(toGid, toUId);
        for (MsgAllBean msgl : listdata) {

            if (msgid.equals(msgl.getMsg_id())) {
                pos = selectList.size();
            }

            LocalMedia lc = new LocalMedia();
            lc.setCutPath(msgl.getImage().getThumbnailShow());
            lc.setCompressPath(msgl.getImage().getPreviewShow());
            lc.setPath(msgl.getImage().getOriginShow());
            // Log.d("tag", "---showBigPic: "+msgl.getImage().getSize());
            lc.setSize(msgl.getImage().getSize());
            lc.setWidth(new Long(msgl.getImage().getWidth()).intValue());
            lc.setHeight(new Long(msgl.getImage().getHeight()).intValue());
            selectList.add(lc);

        }

        PictureSelector.create(ChatActivity.this)
                .themeStyle(R.style.picture_default_style)
                .isGif(true)
                .openExternalPreview1(pos, selectList);

    }


    @Override
    public void onEvent(int type, MsgAllBean message, Object... args) {
        if (message == null) {
            return;
        }
        switch (type) {
            case ChatEnum.ECellEventType.TXT_CLICK:
                break;
            case ChatEnum.ECellEventType.IMAGE_CLICK:
                showBigPic(message.getMsg_id(), message.getImage().getThumbnailShow());
                break;
            case ChatEnum.ECellEventType.RED_ENVELOPE_CLICK:
                if (args[0] != null && args[0] instanceof RedEnvelopeMessage) {
                    RedEnvelopeMessage red = (RedEnvelopeMessage) args[0];
                    //8.15 红包状态修改
                    boolean invalid = red.getIsInvalid() == 0 ? false : true;
                    if ((invalid || message.isMe()) && red.getStyle() == MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.NORMAL_VALUE) {//已领取或者是自己的,看详情,"拼手气的话自己也能抢"
                        taskPayRbDetail(message, red.getId());
                    } else {
                        taskPayRbGet(message, message.getFrom_uid(), red.getId());
                    }

                }
                break;
            case ChatEnum.ECellEventType.CARD_CLICK:
                if (args[0] != null && args[0] instanceof BusinessCardMessage) {

                    BusinessCardMessage cardMessage = (BusinessCardMessage) args[0];
                    //自己的不跳转
                    if (cardMessage.getUid().longValue() != UserAction.getMyId().longValue())
                        startActivity(new Intent(getContext(), UserInfoActivity.class)
                                .putExtra(UserInfoActivity.ID, cardMessage.getUid()));
                }

                break;

            case ChatEnum.ECellEventType.LONG_CLICK:
                List<OptionMenu> menus = (List<OptionMenu>) args[0];
                View view = (View) args[1];
                IMenuSelectListener listener = (IMenuSelectListener) args[2];
                if (view != null && menus != null && menus.size() > 0) {
                    showPop(view, menus, message, listener);
                }
                break;
            case ChatEnum.ECellEventType.TRANSFER_CLICK:
                if (args[0] != null && args[0] instanceof TransferMessage) {
                    TransferMessage transfer = (TransferMessage) args[0];
                    tsakTransGet(transfer.getId());
                }
                break;
            case ChatEnum.ECellEventType.AVATAR_CLICK:
                toUserInfoActivity(message);
                break;
            case ChatEnum.ECellEventType.RESEND_CLICK:
                resendMessage(message);
                break;
            case ChatEnum.ECellEventType.AVATAR_LONG_CLICK:
                edtChat.addAtSpan("@", message.getFrom_nickname(), message.getFrom_uid());
                break;
            case ChatEnum.ECellEventType.VOICE_CLICK:
//                playVoice();
                break;

        }

    }

    //跳转UserInfoActivity
    private void toUserInfoActivity(MsgAllBean message) {
        startActivity(new Intent(getContext(), UserInfoActivity.class)
                .putExtra(UserInfoActivity.ID, message.getFrom_uid())
                .putExtra(UserInfoActivity.JION_TYPE_SHOW, 1)
                .putExtra(UserInfoActivity.GID, toGid)
                .putExtra(UserInfoActivity.MUC_NICK, message.getFrom_nickname()));
    }

    //重新发送消息
    private void resendMessage(MsgAllBean msgBean) {
        //从数据拉出来,然后再发送
        MsgAllBean reMsg = DaoUtil.findOne(MsgAllBean.class, "msg_id", msgBean.getMsg_id());

        try {
            if (reMsg.getMsg_type() == ChatEnum.EMessageType.IMAGE) {//图片重发处理7.31
                String file = reMsg.getImage().getLocalimg();
                if (!TextUtils.isEmpty(file)) {
                    boolean isArtworkMaster = StringUtil.isNotNull(reMsg.getImage().getOrigin()) ? false : true;
                    ImageMessage image = SocketData.createImageMessage(reMsg.getMsg_id(), file, isArtworkMaster);
                    MsgAllBean imgMsgBean = SocketData.sendFileUploadMessagePre(reMsg.getMsg_id(), toUId, toGid, image, ChatEnum.EMessageType.IMAGE);
                    replaceListDataAndNotify(imgMsgBean);
                    UpLoadService.onAdd(reMsg.getMsg_id(), file, isArtworkMaster, toUId, toGid);
                    startService(new Intent(getContext(), UpLoadService.class));
                } else {
                    //点击发送的时候如果要改变成发送中的状态
                    reMsg.setSend_state(ChatEnum.ESendStatus.SENDING);
                    DaoUtil.update(reMsg);
                    LogUtil.getLog().d(TAG, "点击重复发送" + reMsg.getMsg_id());
                    MsgBean.UniversalMessage.Builder bean = MsgBean.UniversalMessage.parseFrom(reMsg.getSend_data()).toBuilder();
                    SocketUtil.getSocketUtil().sendData4Msg(bean);
                    taskRefreshMessage();
                }
            } else if (reMsg.getMsg_type() == ChatEnum.EMessageType.VOICE) {
                String url = reMsg.getVoiceMessage().getLocalUrl();
                if (!TextUtils.isEmpty(url)) {
                    reMsg.setSend_state(ChatEnum.ESendStatus.PRE_SEND);
                    replaceListDataAndNotify(reMsg);
                    uploadVoice(url, reMsg);
                } else {
                    //点击发送的时候如果要改变成发送中的状态
                    reMsg.setSend_state(ChatEnum.ESendStatus.SENDING);
                    DaoUtil.update(reMsg);
                    LogUtil.getLog().d(TAG, "点击重复发送" + reMsg.getMsg_id());
                    MsgBean.UniversalMessage.Builder bean = MsgBean.UniversalMessage.parseFrom(reMsg.getSend_data()).toBuilder();
                    SocketUtil.getSocketUtil().sendData4Msg(bean);
                    replaceListDataAndNotify(reMsg);
//                                taskRefreshMessage();
                }

            } else {
                //点击发送的时候如果要改变成发送中的状态
                reMsg.setSend_state(ChatEnum.ESendStatus.SENDING);
                DaoUtil.update(reMsg);
                LogUtil.getLog().d(TAG, "点击重复发送" + reMsg.getMsg_id());
                MsgBean.UniversalMessage.Builder bean = MsgBean.UniversalMessage.parseFrom(reMsg.getSend_data()).toBuilder();
                SocketUtil.getSocketUtil().sendData4Msg(bean);
                taskRefreshMessage();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        @Override
        public int getItemCount() {
            return msgListData == null ? 0 : msgListData.size();
        }


        @Override
        public void onBindViewHolder(@NonNull RCViewHolder holder, int position, @NonNull List<Object> payloads) {
            if (payloads == null || payloads.isEmpty()) {
                onBindViewHolder(holder, position);
            } else {
//                Log.d("sss", "onBindViewHolderpayloads: " + position);
                final MsgAllBean msgbean = msgListData.get(position);
                //菜单
                final List<OptionMenu> menus = new ArrayList<>();

                //只更新单条处理

                switch (msgbean.getMsg_type()) {
                    case ChatEnum.EMessageType.IMAGE:
                        Integer pg = null;
                        pg = UpLoadService.getProgress(msgbean.getMsg_id());

                        holder.viewChatItem.setErr(msgbean.getSend_state());
                        holder.viewChatItem.setImgageProg(pg);

                        if (msgbean.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
                            menus.add(new OptionMenu("转发"));
                            menus.add(new OptionMenu("删除"));
                        }

                        break;
                    default:
                        onBindViewHolder(holder, position);
                        break;
                }

                itemLongClick(holder, msgbean, menus);

            }
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder holder, final int position) {
//            LogUtil.getLog().i(ChatActivity.class.getSimpleName(), "onBindViewHolder--position=" + position);
            viewMap.put(position, holder.itemView);
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
            // UserInfo fusinfo =msgbean.getFrom_user();
            String headico = msgbean.getFrom_avatar();//fusinfo.getHead();
            if (isGroup()) {//群聊显示昵称

                //6.14 这里有性能问题
                nikeName = StringUtil.isNotNull(nikeName) ? nikeName : msgbean.getFrom_nickname();//fusinfo.getName();


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

            if (isGroup()) {
                holder.viewChatItem.setHeadOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        edtChat.addAtSpan("@", msgbean.getFrom_user().getName(), msgbean.getFrom_uid());
                        return true;
                    }
                });
            }

            //显示数据集

            if (msgbean.isMe()) {
                // headico =
                holder.viewChatItem.setOnHead(null);
            } else {

                final String finalNikeName = nikeName;
                holder.viewChatItem.setOnHead(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), UserInfoActivity.class)
                                .putExtra(UserInfoActivity.ID, msgbean.getFrom_uid())
                                .putExtra(UserInfoActivity.JION_TYPE_SHOW, 1)
                                .putExtra(UserInfoActivity.GID, toGid)
                                .putExtra(UserInfoActivity.MUC_NICK, finalNikeName));
                    }
                });
            }
            holder.viewChatItem.setShowType(msgbean.getMsg_type(), msgbean.isMe(), headico, nikeName, time);
            //发送状态处理
            holder.viewChatItem.setErr(msgbean.getSend_state());//

            //菜单
            final List<OptionMenu> menus = new ArrayList<>();
            switch (msgbean.getMsg_type()) {
                case 0:
                    if (msgbean.getMsgNotice() != null) {
                        holder.viewChatItem.setData0(msgbean.getMsgNotice().getNote());
                        if (msgbean.getMsgNotice().getMsgType() == MsgNotice.MSG_TYPE_DEFAULT || msgbean.getMsgNotice().getMsgType() == 17) {
                            holder.viewChatItem.setData0(msgbean.getMsgNotice().getNote());
                        } else {
                            holder.viewChatItem.setData0(new HtmlTransitonUtils().getSpannableString(ChatActivity.this,
                                    msgbean.getMsgNotice().getNote(), msgbean.getMsgNotice().getMsgType()));
                        }
                        //8.22 如果是红包消息类型则显示红包图
                        if (msgbean.getMsgNotice().getMsgType() != null && (msgbean.getMsgNotice().getMsgType() == 7 || msgbean.getMsgNotice().getMsgType() == 8 || msgbean.getMsgNotice().getMsgType() == 17)) {
                            holder.viewChatItem.showBroadcastIcon(true, null);
                        }

                    }
                    break;
                case ChatEnum.EMessageType.MSG_CENCAL:
                    if (msgbean.getMsgCancel() != null) {
                        if (msgbean.getMsgCancel().getMsgType() == MsgNotice.MSG_TYPE_DEFAULT) {
                            holder.viewChatItem.setData0(msgbean.getMsgCancel().getNote());
                        } else {
                            holder.viewChatItem.setData0(new HtmlTransitonUtils().getSpannableString(ChatActivity.this,
                                    msgbean.getMsgCancel().getNote(), msgbean.getMsgCancel().getMsgType()));
                        }
                    }
                    break;
                case 1:

                    menus.add(new OptionMenu("复制"));
                    menus.add(new OptionMenu("转发"));
                    menus.add(new OptionMenu("删除"));
                    holder.viewChatItem.setData1(msgbean.getChat().getMsg());
                    break;
                case 2:

                    menus.add(new OptionMenu("删除"));
                    holder.viewChatItem.setData2(msgbean.getStamp().getComment());
                    break;

                case 3:
                    menus.add(new OptionMenu("删除"));
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

                            /*if (!isInvalid) {//红包没拆,先检查已经领完没
                                taskPayRbCheck(msgbean, rid);

                            }*/


                            if ((isInvalid || msgbean.isMe()) && style == MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.NORMAL_VALUE) {//已领取或者是自己的,看详情,"拼手气的话自己也能抢"
                                //ToastUtil.show(getContext(), "红包详情");
                                taskPayRbDetail(msgbean, rid);

                            } else {
                                taskPayRbGet(msgbean, touid, rid);
                            }
                        }
                    });
                    break;

                case 4:

                    menus.add(new OptionMenu("转发"));
                    menus.add(new OptionMenu("删除"));
                    Integer pg = null;
                    pg = UpLoadService.getProgress(msgbean.getMsg_id());


                    holder.viewChatItem.setData4(msgbean.getImage(), msgbean.getImage().getThumbnailShow(), new ChatItemView.EventPic() {
                        @Override
                        public void onClick(String uri) {
                            //  ToastUtil.show(getContext(), "大图:" + uri);
                            showBigPic(msgbean.getMsg_id(), uri);
                        }
                    }, pg);
                    // holder.viewChatItem.setImgageProg(pg);
                    break;
                case 5:

                    menus.add(new OptionMenu("删除"));
                    holder.viewChatItem.setData5(msgbean.getBusiness_card().getNickname(),
                            msgbean.getBusiness_card().getComment(),
                            msgbean.getBusiness_card().getAvatar(), null, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // ToastUtil.show(getContext(), "添加好友需要详情页面");
                                    if (msgbean.getBusiness_card().getUid().longValue() != UserAction.getMyId().longValue())
                                        startActivity(new Intent(getContext(), UserInfoActivity.class)
                                                .putExtra(UserInfoActivity.ID, msgbean.getBusiness_card().getUid()));
                                }
                            });
                    break;
                case 6:
                    menus.add(new OptionMenu("删除"));
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
                case 7://语音消息

                    menus.add(new OptionMenu("删除"));
                    final VoiceMessage vm = msgbean.getVoiceMessage();
                    String url = msgbean.isMe() ? vm.getLocalUrl() : vm.getUrl();
                    holder.viewChatItem.setData7(vm.getTime(), msgbean.isRead(), AudioPlayManager.getInstance().isPlay(Uri.parse(url)), vm.getPlayStatus(), new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            playVoice(msgbean, position);
                        }
                    });


                    break;
                case 8:
                    menus.add(new OptionMenu("复制"));
                    menus.add(new OptionMenu("转发"));
                    menus.add(new OptionMenu("删除"));
                    holder.viewChatItem.setDataAt(msgbean.getAtMessage().getMsg());
                    break;
                case ChatEnum.EMessageType.ASSISTANT:
                    holder.viewChatItem.setDataAssistant(msgbean.getAssistantMessage().getMsg());
                    break;


            }

            //------------------------------------------


            holder.viewChatItem.setOnErr(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //从数据拉出来,然后再发送
                    resendMessage(msgbean);
                }
            });
            itemLongClick(holder, msgbean, menus);

        }

        /***
         * 长按操作
         * @param holder
         * @param msgbean
         * @param menus
         */
        private void itemLongClick(final RCViewHolder holder, final MsgAllBean msgbean, final List<OptionMenu> menus) {
            holder.viewChatItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    holder.viewChatItem.selectTextBubble(true);
                    // ToastUtil.show(getContext(),"长按");
                    if (msgbean.getMsg_type() == ChatEnum.EMessageType.VOICE) {//为语音单独处理
                        menus.clear();
                        menus.add(new OptionMenu("删除"));
                        if (msgDao.userSetingGet().getVoicePlayer() == 0) {

                            menus.add(0, new OptionMenu("听筒播放"));
                        } else {
                            menus.add(0, new OptionMenu("扬声器播放"));
                        }
                    }

                    if (msgbean.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
                        if (msgbean.getFrom_uid() != null && msgbean.getFrom_uid().longValue() == UserAction.getMyId().longValue() && msgbean.getMsg_type() != ChatEnum.EMessageType.RED_ENVELOPE) {
                            if (System.currentTimeMillis() - msgbean.getTimestamp() < 2 * 60 * 1000) {//两分钟内可以删除
                                boolean isExist = false;
                                for (OptionMenu optionMenu : menus) {
                                    if (optionMenu.getTitle().equals("撤回")) {
                                        isExist = true;
                                    }
                                }

                                if (!isExist) {
                                    menus.add(new OptionMenu("撤回"));
                                }

                            }
                        }

                        showPop(v, menus, msgbean, new IMenuSelectListener() {
                            @Override
                            public void onSelected() {
                                holder.viewChatItem.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.viewChatItem.selectTextBubble(false);
                                    }
                                }, 100);
                            }
                        });
                    }

                    return true;
                }
            });
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

    private void playVoice(MsgAllBean msgBean, int position) {
        List<MsgAllBean> list = new ArrayList<>();
        boolean isAutoPlay = false;
        if (!msgBean.isMe() && !msgBean.isRead()) {
//            isAutoPlay = true;
            list.add(msgBean);
            int length = msgListData.size();
            if (position < length - 1) {
                for (int i = position + 1; i < length; i++) {
                    MsgAllBean bean = msgListData.get(i);
                    if (bean.getMsg_type() == ChatEnum.EMessageType.VOICE && !bean.isMe() && !bean.isRead()) {
                        list.add(bean);
                    }
                }
            }
            if (list.size() > 1) {
                isAutoPlay = true;
            }
        } else {
            list.add(msgBean);
        }
        playVoice(msgBean, isAutoPlay, position);
//        playVoice(list, msgBean.getVoiceMessage(), isAutoPlay);

//        设置为已读
        if (!isAutoPlay) {
            if (msgBean.isRead() == false) {
                msgAction.msgRead(msgBean.getMsg_id(), true);
                msgBean.setRead(true);
                notifyData();
            }
        }

    }

    private void checkMoreVoice(int start, MsgAllBean b) {
//        LogUtil.getLog().i("AudioPlayManager", "checkMoreVoice--start=" + start);
        int length = msgListData.size();
        MsgAllBean message = null;
        int position = -1;
        if (start < length - 1) {
            for (int i = start + 1; i < length; i++) {
                MsgAllBean bean = msgListData.get(i);
//                MsgAllBean bean = msgDao.getNextVoiceMessage(toUId,toGid,b.getTimestamp(),UserAction.getMyInfo().getUid());
                if (bean.getMsg_type() == ChatEnum.EMessageType.VOICE && !bean.isMe() && !bean.isRead()) {
                    message = bean;
                    position = i;
                    break;
                }
            }
        }

        if (message != null) {
            playVoice(message, true, position);
//            LogUtil.getLog().i("AudioPlayManager", "playVoice--position=" + position);
        }

    }

    //修正msgBean, 确保msgListData中是最新的数据
    private MsgAllBean amendMsgALlBean(int position, MsgAllBean bean) {
        if (msgListData != null && position < msgListData.size()) {
            MsgAllBean msg = msgListData.get(position);
            if (msg.getMsg_id().equals(bean.getMsg_id())) {
                return msg;
            } else {
                int p = msgListData.indexOf(bean);
                if (p >= 0) {
                    return msgListData.get(p);
                }
            }
        }
        return bean;
    }

    private void playVoice(MsgAllBean bean, final boolean canAutoPlay, final int position) {
        VoiceMessage vm = bean.getVoiceMessage();
        if (vm == null || TextUtils.isEmpty(vm.getUrl())) {
            return;
        }
        String url = "";
        if (bean.isMe()) {
            url = vm.getLocalUrl();
        } else {
            url = vm.getUrl();
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (AudioPlayManager.getInstance().isPlay(Uri.parse(url))) {
            AudioPlayManager.getInstance().stopPlay();
        } else {
            AudioPlayManager.getInstance().startPlay(context, bean, position, canAutoPlay, new IVoicePlayListener() {
                @Override
                public void onStart(MsgAllBean bean) {
                    bean = amendMsgALlBean(position, bean);
                    if (bean.isRead() == false) {
                        msgAction.msgRead(bean.getMsg_id(), true);
                        bean.setRead(true);
                    }
                    VoiceMessage voiceMessage = bean.getVoiceMessage();
                    voiceMessage.setPlayStatus(ChatEnum.EPlayStatus.PLAYING);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyData();
                        }
                    });
                    LogUtil.getLog().i("AudioPlayManager", "onStart--" + bean.getVoiceMessage().getUrl());
                }

                @Override
                public void onStop(MsgAllBean bean) {
                    bean = amendMsgALlBean(position, bean);
                    VoiceMessage voiceMessage = bean.getVoiceMessage();
                    voiceMessage.setPlayStatus(ChatEnum.EPlayStatus.STOP_PLAY);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyData();
                        }
                    });

                }

                @Override
                public void onComplete(MsgAllBean bean) {
                    bean = amendMsgALlBean(position, bean);
                    VoiceMessage voiceMessage = bean.getVoiceMessage();
                    voiceMessage.setPlayStatus(ChatEnum.EPlayStatus.PLAYED);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyData();
                        }
                    });

                }
            });
        }
    }

//    private void playVoice(List<MsgAllBean> list, VoiceMessage vm, boolean isAutoPlay) {
//        if (AudioPlayManager.getInstance().isPlay(Uri.parse(vm.getUrl()))) {
//            AudioPlayManager.getInstance().stopPlay();
//        } else {
//            AudioPlayManager.getInstance().startPlay(context, isAutoPlay, list, new IVoicePlayListener() {
//                @Override
//                public void onStart(MsgAllBean bean) {
//                    if (bean.isRead() == false) {
//                        msgAction.msgRead(bean.getMsg_id(), true);
//                        bean.setRead(true);
//                    }
//                    VoiceMessage voiceMessage = bean.getVoiceMessage();
//                    voiceMessage.setPlayStatus(ChatEnum.EPlayStatus.PLAYING);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            notifyData();
//                        }
//                    });
//                    LogUtil.getLog().i("AudioPlayManager", "onStart--" + bean.getVoiceMessage().getUrl());
//                }
//
//                @Override
//                public void onStop(MsgAllBean bean) {
//                    VoiceMessage voiceMessage = bean.getVoiceMessage();
//                    voiceMessage.setPlayStatus(ChatEnum.EPlayStatus.STOP_PLAY);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            notifyData();
//                        }
//                    });
//                    LogUtil.getLog().i("AudioPlayManager", "onStop--" + bean.getVoiceMessage().getUrl());
//
//                }
//
//                @Override
//                public void onComplete(MsgAllBean bean) {
//                    VoiceMessage voiceMessage = bean.getVoiceMessage();
//                    voiceMessage.setPlayStatus(ChatEnum.EPlayStatus.PLAYED);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            notifyData();
//                        }
//                    });
//                    LogUtil.getLog().i("AudioPlayManager", "onComplete--" + bean.getVoiceMessage().getUrl());
//                }
//
////                @Override
////                public void onReadyToNext() {
////
////                }
//            });
//        }
//    }

    /***
     * 长按的气泡处理
     * @param v
     * @param menus
     * @param msgbean
     */
    private void showPop(View v, List<OptionMenu> menus, final MsgAllBean msgbean,
                         final IMenuSelectListener listener) {
        //禁止滑动
        //mtListView.getListView().setNestedScrollingEnabled(true);

        final PopupMenuView menuView = new PopupMenuView(getContext());
        menuView.setMenuItems(menus);
        menuView.setOnMenuClickListener(new OptionMenuView.OnOptionMenuClickListener() {
            @Override
            public boolean onOptionMenuClick(int position, OptionMenu menu) {
                //放开滑动
                // mtListView.getListView().setNestedScrollingEnabled(true);
                if (listener != null) {
                    listener.onSelected();
                }


                if (menu.getTitle().equals("删除")) {

                    AlertYesNo alertYesNo = new AlertYesNo();
                    alertYesNo.init(ChatActivity.this, "删除", "确定删除吗?", "确定", "取消", new AlertYesNo.Event() {
                        @Override
                        public void onON() {

                        }

                        @Override
                        public void onYes() {
                            msgDao.msgDel4MsgId(msgbean.getMsg_id());
                            msgListData.remove(msgbean);
                            notifyData();
                        }
                    });
                    alertYesNo.show();


                } else if (menu.getTitle().equals("转发")) {
                    /*  */
                    startActivity(new Intent(getContext(), MsgForwardActivity.class)
                            .putExtra(MsgForwardActivity.AGM_JSON, new Gson().toJson(msgbean))
                    );

                } else if (menu.getTitle().equals("复制")) {//只有文本
                    String txt = "";
                    if (msgbean.getMsg_type() == ChatEnum.EMessageType.AT) {
                        txt = msgbean.getAtMessage().getMsg();
                    } else {
                        txt = msgbean.getChat().getMsg();
                    }
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText(txt, txt);
                    cm.setPrimaryClip(mClipData);

                } else if (menu.getTitle().equals("听筒播放")) {
                    msgDao.userSetingVoicePlayer(1);
                } else if (menu.getTitle().equals("扬声器播放")) {
                    msgDao.userSetingVoicePlayer(0);
                } else if (menu.getTitle().equals("撤回")) {


                    SocketData.send4CancelMsg(toUId, toGid, msgbean.getMsg_id());


                }
                menuView.dismiss();
                return true;
            }
        });

        menuView.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (listener != null) {
                    listener.onSelected();
                }
                menuView.dismiss();

            }
        });

        menuView.show(v);
    }

    private void notifyData2Bottom(boolean isScrollBottom) {
        notifyData();
        scrollListView(isScrollBottom);
    }

    private void notifyData() {
        if (isNewAdapter) {
            messageAdapter.bindData(msgListData);
        }
//        LogUtil.getLog().i(ChatActivity.class.getSimpleName(), "msgListData的size=" + msgListData.size());
        mtListView.notifyDataSetChange();
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
            if (finfo.getLastonline() > 0) {
                actionbar.setTitleMore(TimeToString.getTimeOnline(finfo.getLastonline(), finfo.getActiveType()));
            }


        }
        actionbar.setTitle(title);

    }

    /***
     * 获取会话信息
     */
    private void updateUserOnlineStatus() {
        String title = "";
        if (!isGroup()) {
            UserInfo finfo = userDao.findUserInfo(toUId);
            title = finfo.getName4Show();
            if (finfo.getLastonline() > 0) {
                actionbar.setTitleMore(TimeToString.getTimeOnline(finfo.getLastonline(), finfo.getActiveType()));
            }
            actionbar.setTitle(title);
        }

    }


    /***
     * 获取最新的
     */
    @SuppressLint("CheckResult")
    private void taskRefreshMessage() {
        //  msgListData = msgAction.getMsg4User(toGid, toUId, indexPage);
        if (needRefresh) {
            needRefresh = false;
        }
        long time = -1L;
        int length = 0;
        if (msgListData != null && msgListData.size() > 0) {
            length = msgListData.size();
            MsgAllBean bean = msgListData.get(length - 1);
            if (bean != null && bean.getTimestamp() != null) {
                time = bean.getTimestamp();
            }
        }
//        preTotalSize = length;
        final long finalTime = time;
        if (length < 20) {
            length += 20;
        }
        final int finalLength = length;
        Observable.just(0)
                .map(new Function<Integer, List<MsgAllBean>>() {
                    @Override
                    public List<MsgAllBean> apply(Integer integer) throws Exception {
                        List<MsgAllBean> list = null;
                        if (finalTime > 0) {
                            list = msgAction.getMsg4User(toGid, toUId, null, finalLength);
                        } else {
                            list = msgAction.getMsg4User(toGid, toUId, null, 20);
                        }
                        taskMkName(list);
                        return list;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<MsgAllBean>>empty())
                .subscribe(new Consumer<List<MsgAllBean>>() {
                    @Override
                    public void accept(List<MsgAllBean> list) throws Exception {
                        msgListData = list;
                        int len = list.size();
                        if (len == 0 && lastPosition > len - 1) {//历史数据被清除了
                            lastPosition = 0;
                            lastOffset = 0;
                            clearScrollPosition();
                        }
                        notifyData2Bottom(false);
//                        notifyData();
                    }
                });

    }

    /***
     * 获取最新的
     */
    @SuppressLint("CheckResult")
    private void taskNewMessage() {
        //  msgListData = msgAction.getMsg4User(toGid, toUId, indexPage);
        synchronized (ChatActivity.class) {
//            LogUtil.getLog().i(ChatActivity.class.getSimpleName(), "taskNewMessage");
            needRefresh = false;
            long time = -1L;
            int length = 0;
            if (msgListData != null && msgListData.size() > 0) {
                length = msgListData.size();
                MsgAllBean bean = msgListData.get(length - 1);
                if (bean != null && bean.getTimestamp() != null) {
                    time = bean.getTimestamp();
                }
            } else {
                return;
            }
//        preTotalSize = length;
            final long finalTime = time;
            if (length < 20) {
                length += 20;
            }
            final int finalLength = length;
            Observable.just(0)
                    .map(new Function<Integer, List<MsgAllBean>>() {
                        @Override
                        public List<MsgAllBean> apply(Integer integer) throws Exception {
                            List<MsgAllBean> list = null;
                            if (finalTime > 0) {
                                list = msgAction.getMsg4User(toGid, toUId, finalTime, true);
                            }
                            taskMkName(list);
                            return list;
                        }
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(Observable.<List<MsgAllBean>>empty())
                    .subscribe(new Consumer<List<MsgAllBean>>() {
                        @Override
                        public void accept(List<MsgAllBean> list) throws Exception {
                            if (list != null && list.size() > 0) {

                                msgListData.addAll(list);
                                int len = msgListData.size();
//                            notifyDataRangeChange(len - list.size(), list.size());
                                mtListView.notifyDataSetChange();
//                                LogUtil.getLog().i(ChatActivity.class.getSimpleName(), "刷新完成--notifyDataSetChange");

                            }
                        }
                    });
        }

    }

    private void notifyDataRangeChange(int start, int size) {
        ((MlistAdapter) mtListView.getListView().getAdapter()).notifyItemRangeChange(start, size);
    }

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

        notifyData();

        mtListView.getListView().smoothScrollToPosition(0);

    }


    /***
     * 加载更多
     */
    private void taskMoreMessage() {

        int addItem = msgListData.size();

        //  msgListData.addAll(0, msgAction.getMsg4User(toGid, toUId, page));
        if (msgListData.size() >= 20) {
            msgListData.addAll(0, msgAction.getMsg4User(toGid, toUId, msgListData.get(0).getTimestamp(), false));
//            currentPager++;

        } else {
            msgListData = msgAction.getMsg4User(toGid, toUId, null, false);
//            currentPager = 0;
        }

        addItem = msgListData.size() - addItem;
        taskMkName(msgListData);
        notifyData();
        LogUtil.getLog().i(ChatActivity.class.getSimpleName(), "size=" + msgListData.size());

        ((LinearLayoutManager) mtListView.getListView().getLayoutManager()).scrollToPositionWithOffset(addItem, DensityUtil.dip2px(context, 20f));


    }

    /***
     * 统一处理mkname
     */
    private Map<String, UserInfo> mks = new HashMap<>();

    /***
     * 获取统一的昵称
     * @param msgListData
     */
    private void taskMkName(List<MsgAllBean> msgListData) {
        mks.clear();
        for (MsgAllBean msg : msgListData) {
            if (msg.getMsg_type() == ChatEnum.EMessageType.NOTICE || msg.getMsg_type() == ChatEnum.EMessageType.MSG_CENCAL || msg.getMsg_type() == ChatEnum.EMessageType.LOCK) {  //通知类型的不处理
                continue;
            }
            String k = msg.getFrom_uid() + "";
            String nkname = "";
            String head = "";

            UserInfo userInfo;
            if (mks.containsKey(k)) {
                userInfo = mks.get(k);
            } else {
                userInfo = msg.getFrom_user();

                if (userInfo == null) {
                    userInfo = new UserInfo();
                    userInfo.setName(StringUtil.isNotNull(msg.getFrom_group_nickname()) ? msg.getFrom_group_nickname() : msg.getFrom_nickname());
                    userInfo.setHead(msg.getFrom_avatar());
                } else {
                    if (isGroup()) {
                        String gname = "";//获取对方最新的群昵称
                        MsgAllBean gmsg = msgDao.msgGetLastGroup4Uid(toGid, msg.getFrom_uid());
                        if (gmsg != null) {
                            gname = gmsg.getFrom_group_nickname();
                        }


                        if (StringUtil.isNotNull(gname)) {
                            userInfo.setName(gname);
                        }
                    }


                }
                mks.put(k, userInfo);
            }


            nkname = userInfo.getName();


            if (StringUtil.isNotNull(userInfo.getMkName())) {
                nkname = userInfo.getMkName();
            }

            head = userInfo.getHead();


            Log.d("tak", "taskName: " + nkname);

            msg.setFrom_nickname(nkname);
            msg.setFrom_avatar(head);


        }
//        this.msgListData = msgListData;

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
            //设置完草稿之后清理掉草稿 防止@功能不能及时弹出
            edtChat.setText(session.getDraft());
            dao.sessionDraft(toGid, toUId, "");
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
    private void taskPayRbGet(final MsgAllBean msgbean, final Long toUId, final String rbid) {
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
                            taskPayRbCheck(msgbean, rbid);
                            if (grabRpBean.getEnvelopeStatus() == 0 && grabRpBean.isHadGrabRp()) {
                                // ToastUtil.show(getContext(), "抢到了红包" + grabRpBean.toString());
                                //taskPayRbCheck(msgbean, rbid);
                                MsgAllBean msgAllbean = SocketData.send4RbRev(toUId, toGid, rbid);
                                showSendObj(msgAllbean);


                            }
                            if (grabRpBean.getEnvelopeStatus() == 3 && !grabRpBean.isHadGrabRp()) {//红包抢完了

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
    private void taskPayRbDetail(final MsgAllBean msgAllBean, final String rid) {
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

    /***
     * 红包是否已经被抢
     * @param rid
     */
    private void taskPayRbCheck(MsgAllBean msgAllBean, final String rid) {


        msgAllBean.getRed_envelope().setIsInvalid(1);
        msgDao.redEnvelopeOpen(rid, true);
        replaceListDataAndNotify(msgAllBean);
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
        return null;
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

    /*
     * 未填充屏幕
     * */
    private boolean isNoFullScreen() {
        if (!mtListView.getListView().canScrollVertically(1) && !mtListView.getListView().canScrollVertically(-1)) {//既不能上滑也不能下滑，即未满屏的情况
            return true;
        }
        return false;
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
                        if (config.getUid() > 0 && config.getUid() == toUId) {
                            lastPosition = config.getLastPosition();
                            lastOffset = config.getLastOffset();
                        } else if (!TextUtils.isEmpty(config.getChatId()) && config.getChatId().equals(toGid)) {
                            lastPosition = config.getLastPosition();
                            lastOffset = config.getLastOffset();
                        }
                    }
                }
            }
        }

        if (lastPosition >= 0) {
            int targetHeight = ScreenUtils.getScreenHeight(this) / 2;//屏幕一般高度
            int size = msgListData.size();
//            int start = size - 1;
            int height = 0;
            for (int i = lastPosition; i < size - 1; i++) {
//                View view = mtListView.getLayoutManager().findViewByPosition(i);//获取不到不可见item
                View view;
                if (isNewAdapter) {
                    view = messageAdapter.getItemViewByPosition(i);
                } else {
                    view = getViewByPosition(i);
                }
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

    //TODO:有问题，重新刷新数据size后，前面添加的item，位置会变更，若在刷新数据的时候清理，则，后面不可见的item获取不到
    private View getViewByPosition(int position) {
        if (!viewMap.isEmpty()) {
            return viewMap.get(position);
        }
        return null;
    }


}
