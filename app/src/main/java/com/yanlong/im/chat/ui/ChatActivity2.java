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
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.baoyz.widget.PullRefreshLayout;
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
import com.yanlong.im.adapter.EmojiAdapter;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.AtMessage;
import com.yanlong.im.chat.bean.BusinessCardMessage;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.GroupConfig;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
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
import com.yanlong.im.pay.action.PayAction;
import com.yanlong.im.pay.bean.SignatureBean;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.MyViewPager;
import com.yanlong.im.user.ui.PageIndicator;
import com.yanlong.im.user.ui.SelectUserActivity;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.DaoUtil;
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
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.DownloadUtil;
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
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.MsgEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
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
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ChatActivity2 extends AppActivity implements ICellEventListener {
    private static String TAG = "ChatActivity";
    public final static int MIN_TEXT = 1000;//

    //返回需要刷新的 8.19 取消自动刷新
    // public static final int REQ_REFRESH = 7779;
    private HeadView headView;
    private ActionbarView actionbar;
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
    private MyViewPager emoji_pager;

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
    private List<MsgAllBean> downloadList = new ArrayList<>();//下载列表
    private Map<String, MsgAllBean> uploadMap = new HashMap<>();//上传列表
    private List<MsgAllBean> uploadList = new ArrayList<>();//上传列表

    //红包和转账
    public static final int REQ_RP = 9653;
    public static final int REQ_TRANS = 9653;


    private MessageAdapter messageAdapter;
    private int lastOffset = -1;
    private int lastPosition = -1;
    private boolean isSoftShow;
    private Map<Integer, View> viewMap = new HashMap<>();
    private boolean needRefresh;
    private List<String> sendTexts;//文本分段发送
    private boolean isSendingHypertext = false;
    private int textPosition;
    private int contactIntimately;
    private String master;
    private TextView tv_ban;
    private ConstraintLayout emoji_pager_con;
    private RecyclerView recyclerView;
    private PullRefreshLayout refreshLayout;
    private LinearLayoutManager layoutManager;


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
                    fixSendTime(bean.getMsgId(0));
                    if (bean.getRejectType() == MsgBean.RejectType.NOT_FRIENDS_OR_GROUP_MEMBER || bean.getRejectType() == MsgBean.RejectType.IN_BLACKLIST) {
                        taskRefreshMessage();
//                        ToastUtil.show(getContext(), "消息发送成功,但对方已拒收");
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
                    if (isSendingHypertext) {
                        if (sendTexts != null && sendTexts.size() > 0 && textPosition != sendTexts.size() - 1) {
                            sendHypertext(sendTexts, textPosition + 1);
                        }
                    }
                }
            });
        }

        @Override
        public void onMsg(final MsgBean.UniversalMessage msgBean) {


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
//                        LogUtil.getLog().i(TAG, "需要刷新");
                        taskRefreshMessage();
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
                    MsgAllBean msgAllBean = MsgConversionBean.ToBean(bean.getWrapMsg(0), bean, true);
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
            case CHANGE_GROUP_META:
                taskSessionInfo();
                break;
        }

    }

    private List<View> emojiLayout;

    //自动寻找控件
    private void findViews() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        recyclerView = findViewById(R.id.recycler_view);
        refreshLayout = findViewById(R.id.view_refresh);
        btnVoice = findViewById(R.id.btn_voice);
        edtChat = findViewById(R.id.edt_chat);
        btnEmj = findViewById(R.id.btn_emj);
        btnFunc = findViewById(R.id.btn_func);
        viewFunc = findViewById(R.id.view_func);
        viewEmoji = findViewById(R.id.view_emoji);
        emoji_pager_con = findViewById(R.id.emoji_pager_con);
        emoji_pager = findViewById(R.id.emoji_pager);
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
        tv_ban = findViewById(R.id.tv_ban);
        setChatImageBackground();
        addViewPagerEvent();
    }

    private void addViewPagerEvent() {
        emojiLayout = new ArrayList<>();
        View view1 = LayoutInflater.from(this).inflate(R.layout.part_chat_emoji, null);
        View view2 = LayoutInflater.from(this).inflate(R.layout.part_chat_emoji2, null);
        emojiLayout.add(view1);
        emojiLayout.add(view2);
        emoji_pager.setAdapter(new EmojiAdapter(emojiLayout, edtChat));
        emoji_pager.addOnPageChangeListener(new PageIndicator(this, (LinearLayout) findViewById(R.id.dot_hor), 2));
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

                if (isGroup() && edtChat.getUserIdList() != null && edtChat.getUserIdList().size() > 0) {
                    String text = edtChat.getText().toString();
                    if (!TextUtils.isEmpty(text)) {
                        int totalSize = text.length();
                        if (totalSize > MIN_TEXT) {
                            ToastUtil.show(ChatActivity2.this, "@消息长度不能超过" + MIN_TEXT);
                            edtChat.getText().clear();
                            return;
                        }
                    }
                    if (edtChat.isAtAll()) {
                        MsgAllBean msgAllbean = SocketData.send4At(toUId, toGid, text, 1, edtChat.getUserIdList());
                        showSendObj(msgAllbean);
                        edtChat.getText().clear();
                    } else {
                        MsgAllBean msgAllbean = SocketData.send4At(toUId, toGid, text, 0, edtChat.getUserIdList());
                        showSendObj(msgAllbean);
                        edtChat.getText().clear();
                    }
                } else {
                    //发送普通消息
                    String text = edtChat.getText().toString();
                    if (!TextUtils.isEmpty(text)) {
                        int totalSize = text.length();
                        int per = totalSize / MIN_TEXT;
                        if (per > 10) {
                            ToastUtil.show(ChatActivity2.this, "文本长度不能超过" + 10 * MIN_TEXT);
                            edtChat.getText().clear();
                            return;
                        }
                        if (totalSize <= MIN_TEXT) {//非长文本
                            isSendingHypertext = false;
                            MsgAllBean msgAllbean = SocketData.send4Chat(toUId, toGid, text);
                            showSendObj(msgAllbean);
                            edtChat.getText().clear();
                        } else {
                            isSendingHypertext = true;//正在分段发送长文本
                            if (totalSize > per * MIN_TEXT) {
                                per = per + 1;
                            }
                            sendTexts = new ArrayList<>();
                            for (int i = 0; i < per; i++) {
                                if (i < per - 1) {
                                    sendTexts.add(StringUtil.splitEmojiString(text, i * MIN_TEXT, (i + 1) * MIN_TEXT));
                                } else {
                                    sendTexts.add(StringUtil.splitEmojiString(text, i * MIN_TEXT, totalSize));
                                }
                            }
                            sendHypertext(sendTexts, 0);
                            edtChat.getText().clear();
                        }
                    }
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
                        Intent intent = new Intent(ChatActivity2.this, GroupSelectUserActivity.class);
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

                if (emoji_pager_con.getVisibility() == View.VISIBLE) {

                    hideBt();
                    InputUtil.showKeyboard(edtChat);


                    btnEmj.setImageLevel(0);
                } else {


                    showBtType(1);
                    btnEmj.setImageLevel(1);
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
                            edtChat.getText().insert(edtChat.getSelectionEnd(), tv.getText());
                        }
                    });
                } else {
                    viewEmojiItem.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int keyCode = KeyEvent.KEYCODE_DEL;
                            KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
                            KeyEvent keyEventUp = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
                            edtChat.onKeyDown(keyCode, keyEventDown);
                            edtChat.onKeyUp(keyCode, keyEventUp);
                        }
                    });
                }
            }
        }

        viewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                permission2Util.requestPermissions(ChatActivity2.this, new CheckPermission2Util.Event() {
                    @Override
                    public void onSuccess() {
                        PictureSelector.create(ChatActivity2.this)
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

                PictureSelector.create(ChatActivity2.this)
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
                alertTouch.init(ChatActivity2.this, "请输入戳一下消息", "确定", R.mipmap.ic_chat_actionme, new AlertTouch.Event() {
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
                permission2Util.requestPermissions(ChatActivity2.this, new CheckPermission2Util.Event() {
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
            public void completeRecord(String file, int duration) {
                VoiceMessage voice = SocketData.createVoiceMessage(SocketData.getUUID(), file, duration);
                MsgAllBean msg = SocketData.sendFileUploadMessagePre(voice.getMsgId(), toUId, toGid, SocketData.getFixTime(), voice, ChatEnum.EMessageType.VOICE);
                msgListData.add(msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyData2Bottom(true);
                    }
                });
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


        initAdapter();//messageAdapter

//        refreshLayout.getLoadView().setStateNormal();
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                taskMoreMessage();
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
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

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
        headView.post(new Runnable() {
            @Override
            public void run() {
                taskDraftGet();
            }
        });


        //9.17 进去后就清理会话的阅读数量
        taskCleanRead();
        EventBus.getDefault().post(new EventRefreshMainMsg());
    }

    private void uploadVoice(String file, final MsgAllBean bean) {
        uploadMap.put(bean.getMsg_id(), bean);
        uploadList.add(bean);
        updateSendStatus(ChatEnum.ESendStatus.SENDING, bean);
        new UpFileAction().upFile(UpFileAction.PATH.VOICE, context, new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
                Log.v(ChatActivity2.class.getSimpleName(), "上传语音成功--" + url);
                VoiceMessage voice = bean.getVoiceMessage();
                voice.setUrl(url);
                SocketData.sendMessage(bean);
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
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messageAdapter);
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

//                        viewEmoji.setVisibility(View.VISIBLE);
                        emoji_pager_con.setVisibility(View.VISIBLE);

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
        recyclerView.postDelayed(new Runnable() {
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
        LogUtil.getLog().i(TAG, "scrollListView--" + messageAdapter.getItemCount() + "--" + Log.getStackTraceString(new Throwable()));
        if (msgListData != null) {
            int length = msgListData.size();//刷新后当前size；
            if (isMustBottom) {
                layoutManager.scrollToPosition(length);
            } else {
                if (lastPosition >= 0 && lastPosition < length) {
                    if (isSoftShow || lastPosition == length - 1 || isCanScrollBottom()) {//允许滑动到底部，或者当前处于底部，canScrollVertically是否能向上 false表示到了底部
//                        recyclerView.scrollToPosition(length);
                        layoutManager.scrollToPosition(length);
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
                            layoutManager.scrollToPosition(length);
                        } else {
                            layoutManager.scrollToPositionWithOffset(lastPosition, lastOffset);
                        }
                    } else {
                        layoutManager.scrollToPosition(length);
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
        emoji_pager_con.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        if (viewFunc.getVisibility() == View.VISIBLE) {
            viewFunc.setVisibility(View.GONE);
            return;
        }
        if (emoji_pager_con.getVisibility() == View.VISIBLE) {
            emoji_pager_con.setVisibility(View.GONE);
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
        EventBus.getDefault().post(new EventRefreshMainMsg());
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
        setContentView(R.layout.activity_chat2);
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
                        MsgAllBean imgMsgBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, SocketData.getFixTime(), imageMessage, ChatEnum.EMessageType.IMAGE);

                        msgListData.add(imgMsgBean);
                        UpLoadService.onAdd(imgMsgId, file, isArtworkMaster, toUId, toGid, -1);
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
            // Log.d("tag", "taskUpImgEvevt 0: ===============>"+event.getMsgId());
            taskRefreshImage(event.getMsgid());
        } else if (event.getState() == -1) {
            //处理失败的情况
//            Log.d("tag", "taskUpImgEvevt -1: ===============>" + event.getMsgId());
            MsgAllBean msgAllbean = (MsgAllBean) event.getMsgAllBean();
            replaceListDataAndNotify(msgAllbean);


        } else if (event.getState() == 1) {
            //  Log.d("tag", "taskUpImgEvevt 1: ===============>"+event.getMsgId());
            MsgAllBean msgAllbean = (MsgAllBean) event.getMsgAllBean();
            replaceListDataAndNotify(msgAllbean);


        } else {
            //  Log.d("tag", "taskUpImgEvevt 2: ===============>"+event.getMsgId());
        }
    }

    private void setChatImageBackground() {
        UserSeting seting = new MsgDao().userSetingGet();
        switch (seting.getImageBackground()) {
            case 1:
                recyclerView.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_100));
                break;
            case 2:
                recyclerView.setBackgroundResource(R.mipmap.bg_image1);
                break;
            case 3:
                recyclerView.setBackgroundResource(R.mipmap.bg_image2);
                break;
            case 4:
                recyclerView.setBackgroundResource(R.mipmap.bg_image3);
                break;
            case 5:
                recyclerView.setBackgroundResource(R.mipmap.bg_image4);
                break;
            case 6:
                recyclerView.setBackgroundResource(R.mipmap.bg_image5);
                break;
            case 7:
                recyclerView.setBackgroundResource(R.mipmap.bg_image6);
                break;
            case 8:
                recyclerView.setBackgroundResource(R.mipmap.bg_image7);
                break;
            case 9:
                recyclerView.setBackgroundResource(R.mipmap.bg_image8);
                break;
            default:
                recyclerView.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_100));
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
            messageAdapter.updateItemAndRefresh(msgAllbean);
            messageAdapter.notifyItemChanged(position, position);
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
                messageAdapter.notifyItemChanged(i, i);
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

        PictureSelector.create(ChatActivity2.this)
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
            LogUtil.getLog().d(TAG, "点击重复发送" + reMsg.getMsg_id() + "--" + reMsg.getTimestamp());
            if (reMsg.getMsg_type() == ChatEnum.EMessageType.IMAGE) {//图片重发处理7.31
                String file = reMsg.getImage().getLocalimg();
                if (!TextUtils.isEmpty(file)) {
                    boolean isArtworkMaster = StringUtil.isNotNull(reMsg.getImage().getOrigin()) ? true : false;
                    ImageMessage image = SocketData.createImageMessage(reMsg.getMsg_id(), file, isArtworkMaster);
                    MsgAllBean imgMsgBean = SocketData.sendFileUploadMessagePre(reMsg.getMsg_id(), toUId, toGid, reMsg.getTimestamp(), image, ChatEnum.EMessageType.IMAGE);
                    replaceListDataAndNotify(imgMsgBean);
                    UpLoadService.onAdd(reMsg.getMsg_id(), file, isArtworkMaster, toUId, toGid, reMsg.getTimestamp());
                    startService(new Intent(getContext(), UpLoadService.class));
                } else {
                    //点击发送的时候如果要改变成发送中的状态
                    reMsg.setSend_state(ChatEnum.ESendStatus.SENDING);
                    DaoUtil.update(reMsg);
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
                    MsgBean.UniversalMessage.Builder bean = MsgBean.UniversalMessage.parseFrom(reMsg.getSend_data()).toBuilder();
                    SocketUtil.getSocketUtil().sendData4Msg(bean);
                    replaceListDataAndNotify(reMsg);
//                                taskRefreshMessage();
                }
            } else {
                //点击发送的时候如果要改变成发送中的状态
                reMsg.setSend_state(ChatEnum.ESendStatus.SENDING);
                DaoUtil.update(reMsg);
                MsgBean.UniversalMessage.Builder bean = MsgBean.UniversalMessage.parseFrom(reMsg.getSend_data()).toBuilder();
                SocketUtil.getSocketUtil().sendData4Msg(bean);
                taskRefreshMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkMoreVoice(int start, MsgAllBean b) {
//        LogUtil.getLog().i("AudioPlayManager", "checkMoreVoice--onCreate=" + onCreate);
        int length = msgListData.size();
        int index = msgListData.indexOf(b);
        if (index < 0) {
            return;
        }
        if (index != start) {//修正一下起始位置
            start = index;
        }
        MsgAllBean message = null;
        int position = -1;
        if (start < length - 1) {
            for (int i = start + 1; i < length; i++) {
                MsgAllBean bean = msgListData.get(i);
                if (bean.getMsg_type() == ChatEnum.EMessageType.VOICE && !bean.isMe() && !bean.isRead()) {
                    message = bean;
                    position = i;
                    break;
                }
            }
        }
//        MsgAllBean bean = msgDao.getNextVoiceMessage(toUId,toGid,b.getTimestamp(),UserAction.getMyInfo().getUid());
        if (message != null) {
            playVoice(message, true, position);
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

    private void playVoice(final MsgAllBean bean, final boolean canAutoPlay, final int position) {
//        LogUtil.getLog().i(TAG, "playVoice--" + position);
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
            if (!bean.isRead() && !bean.isMe()) {
                int len = downloadList.size();
                if (len > 0) {//有下载
                    MsgAllBean msg = downloadList.get(len - 1);
                    updatePlayStatus(msg, 0, ChatEnum.EPlayStatus.NO_PLAY);
                }
                downloadList.add(bean);

                updatePlayStatus(bean, position, ChatEnum.EPlayStatus.DOWNLOADING);
                AudioPlayManager.getInstance().downloadAudio(context, bean, new DownloadUtil.IDownloadVoiceListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        updatePlayStatus(bean, position, ChatEnum.EPlayStatus.NO_PLAY);
                        startPlayVoice(bean, canAutoPlay, position);

                    }

                    @Override
                    public void onDownloading(int progress) {

                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        updatePlayStatus(bean, position, ChatEnum.EPlayStatus.NO_DOWNLOADED);
                    }
                });
            } else {
                int len = downloadList.size();
                if (len > 0) {//有下载
                    MsgAllBean msg = downloadList.get(len - 1);
                    updatePlayStatus(msg, 0, ChatEnum.EPlayStatus.NO_PLAY);
                }
                startPlayVoice(bean, canAutoPlay, position);
            }
        }
    }

    private void updatePlayStatus(MsgAllBean bean, int position, @ChatEnum.EPlayStatus int status) {
//        LogUtil.getLog().i(TAG, "updatePlayStatus--" + status + "--position=" + position);
        bean = amendMsgALlBean(position, bean);
        VoiceMessage voiceMessage = bean.getVoiceMessage();
        if (status == ChatEnum.EPlayStatus.NO_PLAY || status == ChatEnum.EPlayStatus.PLAYING) {//已点击下载，或者正在播
            if (bean.isRead() == false) {
                msgAction.msgRead(bean.getMsg_id(), true);
                bean.setRead(true);
            }
        }
        msgDao.updatePlayStatus(voiceMessage.getMsgId(), status);
        voiceMessage.setPlayStatus(status);
        final MsgAllBean finalBean = bean;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                replaceListDataAndNotify(finalBean);
            }
        });
    }

    private void startPlayVoice(MsgAllBean bean, boolean canAutoPlay, final int position) {
//        LogUtil.getLog().i(TAG, "startPlayVoice--" + "downSize =" + downloadList.size());

        if (downloadList.size() > 1) {
            int size = downloadList.size();
            int p = downloadList.indexOf(bean);
            if (p != size - 1) {
//                LogUtil.getLog().i(TAG, "startPlayVoice--终止下载位置=" + p);
                downloadList.remove(bean);
                return;
            }
        }
        downloadList.remove(bean);

        AudioPlayManager.getInstance().startPlay(context, bean, position, canAutoPlay, new IVoicePlayListener() {
            @Override
            public void onStart(MsgAllBean bean) {
                updatePlayStatus(bean, position, ChatEnum.EPlayStatus.PLAYING);
//                LogUtil.getLog().i("AudioPlayManager", "onStart--" + bean.getVoiceMessage().getUrl());
            }

            @Override
            public void onStop(MsgAllBean bean) {
                updatePlayStatus(bean, position, ChatEnum.EPlayStatus.STOP_PLAY);
//                LogUtil.getLog().i("AudioPlayManager", "onStop--" + bean.getVoiceMessage().getUrl());
            }

            @Override
            public void onComplete(MsgAllBean bean) {
                updatePlayStatus(bean, position, ChatEnum.EPlayStatus.PLAYED);
//                LogUtil.getLog().i("AudioPlayManager", "onComplete--" + bean.getVoiceMessage().getUrl());
            }
        });
    }

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
                    alertYesNo.init(ChatActivity2.this, "删除", "确定删除吗?", "确定", "取消", new AlertYesNo.Event() {
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

    private void notifyData2Bottom(final boolean isScrollBottom) {
        notifyData();
        scrollListView(isScrollBottom);
    }

    private void notifyData() {
        messageAdapter.bindData(msgListData);
        LogUtil.getLog().i(TAG, "刷新数据--" + messageAdapter.getItemCount());
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
            title = msgDao.getGroupName(toGid);
            //6.15 设置右上角点击
            taskGroupConf();
        } else {
            UserInfo finfo = userDao.findUserInfo(toUId);
            title = finfo.getName4Show();
            if (finfo.getLastonline() > 0) {
                actionbar.setTitleMore(TimeToString.getTimeOnline(finfo.getLastonline(), finfo.getActiveType(), true));
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
                actionbar.setTitleMore(TimeToString.getTimeOnline(finfo.getLastonline(), finfo.getActiveType(), true));
            }
            actionbar.setTitle(title);
        }

    }


    /***
     * 获取最新的
     */
    @SuppressLint("CheckResult")
    private void taskRefreshMessage() {
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
                        notifyData2Bottom(true);
                    }
                });

    }

    /***
     * 查询历史
     * @param history
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void taskFinadHistoryMessage(EventFindHistory history) {
        msgListData = msgAction.getMsg4UserHistory(toGid, toUId, history.getStime());
        taskMkName(msgListData);
        notifyData();
        recyclerView.smoothScrollToPosition(0);
    }


    /***
     * 加载更多
     */
    private void taskMoreMessage() {
        int addItem = msgListData.size();
        if (msgListData.size() >= 20) {
            msgListData.addAll(0, msgAction.getMsg4User(toGid, toUId, msgListData.get(0).getTimestamp(), false));
        } else {
            msgListData = msgAction.getMsg4User(toGid, toUId, null, false);
        }
        addItem = msgListData.size() - addItem;
        taskMkName(msgListData);
        notifyData();
        layoutManager.scrollToPositionWithOffset(addItem, DensityUtil.dip2px(context, 20f));


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
            msg.setFrom_nickname(nkname);
            msg.setFrom_avatar(head);
        }
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
            boolean isExited;
            if (config.getIsExit() == 1) {
                isExited = true;
            } else {
                isExited = false;
            }
            setBanView(isExited);
        }
        taskGroupInfo();
    }

    /*
     * 是否已经退出
     * */
    private void setBanView(boolean isExited) {
        actionbar.getBtnRight().setVisibility(isExited ? View.GONE : View.VISIBLE);
        tv_ban.setVisibility(isExited ? VISIBLE : GONE);
        viewChatBottomc.setVisibility(isExited ? GONE : VISIBLE);
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

                    JrmfRpClient.transAccount(ChatActivity2.this, "" + finfo.getUid(), "" + minfo.getUid(), token,
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

                        JrmfRpClient.sendGroupEnvelopeForResult(ChatActivity2.this, "" + toGid, "" + UserAction.getMyId(), token,
                                group.getUsers().size(), minfo.getName(), minfo.getHead(), REQ_RP);
                    } else {

                        UserInfo minfo = UserAction.getMyInfo();
                        JrmfRpClient.sendSingleEnvelopeForResult(ChatActivity2.this, "" + toUId, "" + minfo.getUid(), token,
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
                        JrmfRpClient.openGroupRp(ChatActivity2.this, "" + minfo.getUid(), token, minfo.getName(), minfo.getHead(), rbid, callBack);
                    } else {
                        UserInfo minfo = UserAction.getMyInfo();
                        JrmfRpClient.openSingleRp(ChatActivity2.this, "" + minfo.getUid(), token, minfo.getName(), minfo.getHead(), rbid, callBack);
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

                    JrmfRpClient.openTransDetail(ChatActivity2.this, "" + minfo.getUid(), token,
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
        payAction.SignatureBean(new CallBack<ReturnBean<SignatureBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SignatureBean>> call, Response<ReturnBean<SignatureBean>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    SignatureBean sign = response.body().getData();
                    String token = sign.getSign();
                    UserInfo minfo = UserAction.getMyInfo();
                    JrmfRpClient.openRpDetail(ChatActivity2.this, "" + minfo.getUid(), token, rid, minfo.getName(), minfo.getHead());
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
                contactIntimately = groupInfo.getContactIntimately();
                master = groupInfo.getMaster();

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
                setBanView(!isExit);


            }
        });
    }

    /*
     * 未填充屏幕
     * */
    private boolean isNoFullScreen() {
        if (!recyclerView.canScrollVertically(1) && !recyclerView.canScrollVertically(-1)) {//既不能上滑也不能下滑，即未满屏的情况
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
//            int onCreate = size - 1;
            int height = 0;
            for (int i = lastPosition; i < size - 1; i++) {
//                View view = mtListView.getLayoutManager().findViewByPosition(i);//获取不到不可见item
                View view;
                view = messageAdapter.getItemViewByPosition(i);
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
            }
            if (height + lastOffset <= targetHeight) {
                return true;
            }
        }
        return false;
    }

    private void sendHypertext(List<String> list, int position) {
        if (position == list.size() - 1) {
            isSendingHypertext = false;
        }
        textPosition = position;
        MsgAllBean msgAllbean = SocketData.send4Chat(toUId, toGid, list.get(position));
        showSendObj(msgAllbean);
    }

    private void fixSendTime(String msgId) {
        MsgAllBean bean = uploadMap.get(msgId);
        boolean needRefresh = false;
        if (bean != null) {
            if (uploadList.indexOf(bean) == 0) {
                needRefresh = true;
            }
            uploadMap.remove(msgId);
        }
        if (needRefresh && uploadMap.size() > 0) {
            for (Map.Entry<String, MsgAllBean> entry : uploadMap.entrySet()) {
                MsgAllBean msg = entry.getValue();
                msg.setTimestamp(SocketData.getFixTime());
                DaoUtil.update(msg);
            }
        }
    }
}
