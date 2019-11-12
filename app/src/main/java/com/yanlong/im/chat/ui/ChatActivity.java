package com.yanlong.im.chat.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import com.example.nim_lib.config.Preferences;
import com.example.nim_lib.controll.AVChatProfile;
import com.example.nim_lib.event.EventFactory;
import com.example.nim_lib.ui.VideoActivity;
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
import com.luck.picture.lib.view.PopupSelectView;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
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
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.chat.bean.MsgNotice;
import com.yanlong.im.chat.bean.RedEnvelopeMessage;
import com.yanlong.im.chat.bean.ScrollConfig;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.TransferMessage;
import com.yanlong.im.chat.bean.UserSeting;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.interf.IMenuSelectListener;
import com.yanlong.im.chat.manager.MessageManager;
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
import com.yanlong.im.user.ui.MyViewPager;
import com.yanlong.im.user.ui.PageIndicator;
import com.yanlong.im.user.ui.SelectUserActivity;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.GroupHeadImageUtil;
import com.yanlong.im.utils.HtmlTransitonUtils;
import com.yanlong.im.utils.MyDiskCache;
import com.yanlong.im.utils.MyDiskCacheUtils;
import com.yanlong.im.utils.audio.AudioPlayManager;
import com.yanlong.im.utils.audio.AudioRecordManager;
import com.yanlong.im.utils.audio.IAdioTouch;
import com.yanlong.im.utils.audio.IAudioRecord;
import com.yanlong.im.utils.audio.IVoicePlayListener;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;
import com.zhaoss.weixinrecorded.activity.RecordedActivity;
import com.zhaoss.weixinrecorded.util.ActivityForwordEvent;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.EventFindHistory;
import net.cb.cb.library.bean.EventGroupChange;
import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.bean.EventUpImgLoadEvent;
import net.cb.cb.library.bean.EventUserOnlineChange;
import net.cb.cb.library.bean.EventVoicePlay;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.inter.ICustomerItemClick;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.DialogHelper;
import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.IntentUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
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
import net.cb.cb.library.view.MsgEditText;
import net.cb.cb.library.view.MultiListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
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
import retrofit2.Call;
import retrofit2.Response;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ChatActivity extends AppActivity implements ICellEventListener {
    private static String TAG = "ChatActivity";
    public final static int MIN_TEXT = 1000;//
    private final int RELINQUISH_TIME = 5;// 5分钟内显示重新编辑
    private final String REST_EDIT = "重新编辑";

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
    private LinearLayout viewChatRobot, ll_part_chat_video;
    private LinearLayout llChatVideoCall;
    private View viewChatBottom;
    private View viewChatBottomc;
    private View imgEmojiDel;
    private Button btnSend;
    private Button txtVoice;
    private MyViewPager emoji_pager;

    private Integer font_size;

    public static final String AGM_TOUID = "toUId";
    public static final String AGM_TOGID = "toGId";
    public static final String GROUP_CREAT = "creat";

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
    public static final int VIDEO_RP = 9419;
    public static final int REQ_TRANS = 9653;
    // 浮动窗口
    private final int REQUEST_CODE = 100;


    private MessageAdapter messageAdapter;
    private int lastOffset = -1;
    private int lastPosition = -1;
    private boolean isNewAdapter = false;
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
    private String draft;
    private int isFirst;

    // 气泡视图
    private PopupWindow mPopupWindow;// 长按消息弹出气泡PopupWindow
    private int popupWidth;// 气泡宽
    private int popupHeight;// 气泡高
    private ImageView mImgTriangleUp;// 上箭头
    private ImageView mImgTriangleDown;// 下箭头
    private TextView mTxtView1;
    private TextView mTxtView2;
    private TextView mTxtView3;
    private TextView mTxtView4;
    private TextView mTxtDelete;
    private View layoutContent;
    private View mRootView;
    private View mViewLine1;
    private View mViewLine2;
    private View mViewLine3;
    private Map<String, String> mTempImgPath = new HashMap<>();// 用于存放本次会话发送的本地图片路径
    private MsgAllBean currentPlayBean;
    private Session session;
    private boolean isLoadHistory = false;//是否是搜索历史信息

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
                        taskRefreshMessage(false);
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
                            taskRefreshMessage(false);
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
                        taskRefreshMessage(false);
                    }
                    initUnreadCount();
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
                    taskRefreshMessage(false);
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

        if (!isGroup()) {
            return;
        }
        switch (msg.getMsgType()) {

            case DESTROY_GROUP:
                // ToastUtil.show(getApplicationContext(), "销毁群");
//                taskGroupConf();
                taskSessionInfo();
                break;
            case REMOVE_GROUP_MEMBER://退出群
//                taskGroupConf();
                taskSessionInfo();
                break;
            case ACCEPT_BE_GROUP://邀请进群刷新
                if (groupInfo != null) {
                    taskSessionInfo();
                    if (StringUtil.isNotNull(groupInfo.getAvatar())) {
//                        taskGroupConf();
                    } else {
                        if (groupInfo.getUsers().size() >= 9) {
//                            taskGroupConf();
                        } else {
//                            taskGroupConf();
                            GroupHeadImageUtil.creatAndSaveImg(this, groupInfo.getGid());
                        }
                    }
                }
                break;
//            case OTHER_REMOVE_GROUP:
//                creatAndSaveImg(groupInfo.getGid());
//                break;
            case CHANGE_GROUP_META:// 修改群信息
                taskSessionInfo();
                break;
        }

    }

//    private void creatAndSaveImg(String gid) {
//        Group gginfo = msgDao.getGroup4Id(gid);
//        int i = gginfo.getUsers().size();
//        i = i > 9 ? 9 : i;
//        //头像地址
//        String url[] = new String[i];
//        for (int j = 0; j < i; j++) {
//            UserInfo userInfo = gginfo.getUsers().get(j);
////            if (j == i - 1) {
////                name += userInfo.getName();
////            } else {
////                name += userInfo.getName() + "、";
////            }
//            url[j] = userInfo.getHead();
//        }
//        File file = GroupHeadImageUtil.synthesis(getContext(), url);
////        Glide.with(this).load(file)
////                .apply(GlideOptionsUtil.headImageOptions()).into(imgHead);
//
//        MsgDao msgDao = new MsgDao();
//        msgDao.groupHeadImgCreate(gginfo.getGid(), file.getAbsolutePath());
//    }


    private List<View> emojiLayout;
    private MediaPlayer mMediaPlayer;

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
        ll_part_chat_video = findViewById(R.id.ll_part_chat_video);
        llChatVideoCall = findViewById(R.id.ll_chat_video_call);
//        findViewById(R.id.ll_video_recoder).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CommFunKt.rxRequestPermissions(ChatActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, "相机、存储、录音", new Function0<Unit>() {
//                    @Override
//                    public Unit invoke() {
//                        Intent intent=new Intent(ChatActivity.this, VideoRecordActivity.class);
//                        startActivityForResult(intent,101);
//                        return null;
//                    }
//                });
//
//
//            }
//        });
        imgEmojiDel = findViewById(R.id.img_emoji_del);
        btnSend = findViewById(R.id.btn_send);
        txtVoice = findViewById(R.id.txt_voice);
        tv_ban = findViewById(R.id.tv_ban);
        setChatImageBackground();
        addViewPagerEvent();
    }

    private void addViewPagerEvent() {
        emojiLayout = new ArrayList<>();
        View view6 = LayoutInflater.from(ChatActivity.this).inflate(R.layout.part_chat_emoji6, null);
        View view7 = LayoutInflater.from(ChatActivity.this).inflate(R.layout.part_chat_emoji7, null);
        View view8 = LayoutInflater.from(ChatActivity.this).inflate(R.layout.part_chat_emoji8, null);
        View view9 = LayoutInflater.from(ChatActivity.this).inflate(R.layout.part_chat_emoji9, null);
        View view10 = LayoutInflater.from(ChatActivity.this).inflate(R.layout.part_chat_emoji10, null);
        View view11 = LayoutInflater.from(ChatActivity.this).inflate(R.layout.part_chat_emoji11, null);
        emojiLayout.add(view6);
        emojiLayout.add(view7);
        emojiLayout.add(view8);
        emojiLayout.add(view9);
        emojiLayout.add(view10);
        emojiLayout.add(view11);
        emoji_pager.setAdapter(new EmojiAdapter(emojiLayout, edtChat));
        emoji_pager.addOnPageChangeListener(new PageIndicator(ChatActivity.this, (LinearLayout) findViewById(R.id.dot_hor), 6));

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
        taskRefreshMessage(false);

    }


    //自动生成的控件事件
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initEvent() {
        toGid = getIntent().getStringExtra(AGM_TOGID);
        toUId = getIntent().getLongExtra(AGM_TOUID, 0);
        toUId = toUId == 0 ? null : toUId;
        taskSessionInfo();
        if (!TextUtils.isEmpty(toGid)) {
            taskGroupInfo();
        }
        actionbar.getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        if (isGroup()) {
            actionbar.getBtnRight().setVisibility(View.GONE);
            viewChatBottom.setVisibility(View.VISIBLE);
        } else {
            actionbar.getBtnRight().setVisibility(View.VISIBLE);
            viewChatBottom.setVisibility(View.VISIBLE);
//            if (toUId == 1L) {
//                viewChatBottom.setVisibility(View.GONE);
//            } else {
//                viewChatBottom.setVisibility(View.VISIBLE);
//            }
        }

        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                finish();
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
                //test 8.
                String text = edtChat.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    ToastUtil.show(ChatActivity.this, "不能发送空白消息");
                    edtChat.getText().clear();
                    return;
                }
                if (text.startsWith("@000")) {
                    int count = Integer.parseInt(text.split("_")[1]);
                    taskTestSend(count);
                    return;
                }

                int totalSize = text.length();
                if (isGroup() && edtChat.getUserIdList() != null && edtChat.getUserIdList().size() > 0) {
                    if (totalSize > MIN_TEXT) {
                        ToastUtil.show(ChatActivity.this, "@消息长度不能超过" + MIN_TEXT);
                        edtChat.getText().clear();
                        return;
                    }
                    if (edtChat.isAtAll()) {
                        MsgAllBean msgAllbean = SocketData.send4At(toUId, toGid, text, 1, edtChat.getUserIdList());
                        showSendObj(msgAllbean);
                        edtChat.getText().clear();
                        MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllbean);
                    } else {
                        MsgAllBean msgAllbean = SocketData.send4At(toUId, toGid, text, 0, edtChat.getUserIdList());
                        showSendObj(msgAllbean);
                        edtChat.getText().clear();
                        MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllbean);
                    }
                } else {
                    //发送普通消息
                    if (!TextUtils.isEmpty(text)) {
                        int per = totalSize / MIN_TEXT;
                        if (per > 10) {
                            ToastUtil.show(ChatActivity.this, "文本长度不能超过" + 10 * MIN_TEXT);
                            edtChat.getText().clear();
                            return;
                        }
                        if (totalSize <= MIN_TEXT) {//非长文本
                            isSendingHypertext = false;
                            MsgAllBean msgAllbean = SocketData.send4Chat(toUId, toGid, text);
                            showSendObj(msgAllbean);
                            edtChat.getText().clear();
                            MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllbean);
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
                // isFirst解决第一次进来草稿中会有@符号的内容
                if (isGroup() && isFirst != 0) {
                    if (count == 1 && (s.charAt(s.length() - 1) == "@".charAt(0) || s.charAt(s.length() - (s.length() - start)) == "@".charAt(0))) { //添加一个字
                        //跳转到@界面
                        Intent intent = new Intent(ChatActivity.this, GroupSelectUserActivity.class);
                        intent.putExtra(GroupSelectUserActivity.TYPE, 1);
                        intent.putExtra(GroupSelectUserActivity.GID, toGid);
                        startActivityForResult(intent, GroupSelectUserActivity.RET_CODE_SELECTUSR);
                    }
                }
                isFirst++;
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

        // 拍摄
        viewCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                permission2Util.requestPermissions(ChatActivity.this, new CheckPermission2Util.Event() {
                    @Override
                    public void onSuccess() {
//                        PictureSelector.create(ChatActivity.this)
//                                .openCamera(PictureMimeType.ofImage())
//                                .compress(true)
//                                .forResult(PictureConfig.REQUEST_CAMERA);

//                        Intent intent = new Intent(ChatActivity.this, RecordedLocalActivity.class);
//                        startActivityForResult(intent, VIDEO_RP);
                        // 判断是否正在音视频通话
                        if (AVChatProfile.getInstance().isCallIng() || AVChatProfile.getInstance().isCallEstablished()) {
                            if (AVChatProfile.getInstance().isChatType() == AVChatType.VIDEO.getValue()) {
                                ToastUtil.show(ChatActivity.this, getString(R.string.avchat_peer_busy_video));
                            } else {
                                ToastUtil.show(ChatActivity.this, getString(R.string.avchat_peer_busy_voice));
                            }
                        } else {
                            Intent intent = new Intent(ChatActivity.this, RecordedActivity.class);
                            startActivityForResult(intent, VIDEO_RP);
                        }
//                        showDownLoadDialog();
                    }

                    @Override
                    public void onFail() {

                    }
                }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});


            }
        });
        // 相册
        viewPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PictureSelector.create(ChatActivity.this)
//                        .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                        .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
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
                            //发送戳一戳消息
                            MsgAllBean msgAllbean = SocketData.send4action(toUId, toGid, content);
                            showSendObj(msgAllbean);
                            MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllbean);
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
            public void completeRecord(String file, int duration) {
                VoiceMessage voice = SocketData.createVoiceMessage(SocketData.getUUID(), file, duration);
                MsgAllBean msg = SocketData.sendFileUploadMessagePre(voice.getMsgId(), toUId, toGid, SocketData.getFixTime(), voice, ChatEnum.EMessageType.VOICE);
                msgListData.add(msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyData2Bottom(true);
                        MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msg);
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
        //短视频
        ll_part_chat_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permission2Util.requestPermissions(ChatActivity.this, new CheckPermission2Util.Event() {
                    @Override
                    public void onSuccess() {
//                        Intent intent = new Intent(ChatActivity.this, RecordedActivity.class);
//                        startActivityForResult(intent, VIDEO_RP);


//                        PictureSelector.create(ChatActivity.this)
//                                .openCamera(PictureMimeType.ofVideo())
//                                .compress(true)
//                                .forResult(PictureConfig.REQUEST_CAMERA);
                    }

                    //                  @Override
                    public void onFail() {

                    }
                }, new String[]{Manifest.permission.CAMERA});

            }
        });

        // 视频通话
        llChatVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBt();
                DialogHelper.getInstance().createSelectDialog(ChatActivity.this, new ICustomerItemClick() {
                    @Override
                    public void onClickItemVideo() {// 视频
                        gotoVideoActivity();
                    }

                    @Override
                    public void onClickItemVoice() {// 语音
                        gotoVoiceActivity();
                    }

                    @Override
                    public void onClickItemCancle() {

                    }
                });
            }
        });


        if (isGroup()) {//去除群的控件
            viewFunc.removeView(viewAction);
            //viewFunc.removeView(viewTransfer);
            viewChatRobot.setVisibility(View.INVISIBLE);
            viewFunc.removeView(llChatVideoCall);
        } else {
            viewFunc.removeView(viewChatRobot);
        }
        viewFunc.removeView(ll_part_chat_video);
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
                        dismissPop();
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
                        System.out.println(TAG + "当前滑动位置：lastPosition=" + lastPosition);
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


        //9.17 进去后就清理会话的阅读数量
        taskCleanRead();
    }

    //是否是常信小助手用户
    public boolean isAssitanceUser() {
        if (toUId != null && toUId == 1L) {
            return true;
        }
        return false;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 进入视频通话
     */
    private void gotoVideoActivity() {
        permission2Util.requestPermissions(ChatActivity.this, new CheckPermission2Util.Event() {
            @Override
            public void onSuccess() {
                if (NetUtil.isNetworkConnected()) {
                    if (userDao != null) {
                        UserInfo userInfo = userDao.findUserInfo(toUId);
                        if (userInfo != null) {
                            EventFactory.CloseMinimizeEvent event = new EventFactory.CloseMinimizeEvent();
                            event.isClose = true;
                            EventBus.getDefault().post(event);
                            Bundle bundle = new Bundle();
                            bundle.putString(Preferences.USER_HEAD_SCULPTURE, userInfo.getHead());
                            bundle.putString(Preferences.USER_NAME, userInfo.getName());
                            bundle.putString(Preferences.NETEASEACC_ID, userInfo.getNeteaseAccid());
                            bundle.putInt(Preferences.VOICE_TYPE, CoreEnum.VoiceType.WAIT);
                            bundle.putInt(Preferences.AVCHA_TTYPE, AVChatType.VIDEO.getValue());
                            bundle.putString(Preferences.TOGID, toGid);
                            bundle.putLong(Preferences.TOUID, toUId);
                            IntentUtil.gotoActivity(ChatActivity.this, VideoActivity.class, bundle);// TODO bundle
                        }
                    }
                } else {
                    showNetworkDialog();
                }
            }

            @Override
            public void onFail() {

            }
        }, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
    }

    /**
     * 进入语音通话
     */
    private void gotoVoiceActivity() {
        permission2Util.requestPermissions(ChatActivity.this, new CheckPermission2Util.Event() {
            @Override
            public void onSuccess() {
                if (NetUtil.isNetworkConnected()) {
                    if (userDao != null) {
                        UserInfo userInfo = userDao.findUserInfo(toUId);
                        if (userInfo != null) {
                            EventFactory.CloseMinimizeEvent event = new EventFactory.CloseMinimizeEvent();
                            event.isClose = true;
                            EventBus.getDefault().post(event);
                            Bundle bundle = new Bundle();
                            bundle.putString(Preferences.USER_HEAD_SCULPTURE, userInfo.getHead());
                            bundle.putString(Preferences.USER_NAME, userInfo.getName());
                            bundle.putString(Preferences.NETEASEACC_ID, userInfo.getNeteaseAccid());
                            bundle.putInt(Preferences.VOICE_TYPE, CoreEnum.VoiceType.WAIT);
                            bundle.putInt(Preferences.AVCHA_TTYPE, AVChatType.AUDIO.getValue());
                            bundle.putString(Preferences.TOGID, toGid);
                            bundle.putLong(Preferences.TOUID, toUId);
                            IntentUtil.gotoActivity(ChatActivity.this, VideoActivity.class, bundle);
                        }
                    }
                } else {
                    showNetworkDialog();
                }
            }

            @Override
            public void onFail() {

            }
        }, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
    }

    /**
     * 显示网络错误提示
     */
    private void showNetworkDialog() {
        AlertYesNo alertYesNo = new AlertYesNo();
        alertYesNo.init(ChatActivity.this, null, "当前网络不可用，请检查你的网络设置", "确定", null, new AlertYesNo.Event() {
            @Override
            public void onON() {

            }

            @Override
            public void onYes() {

            }
        });
        alertYesNo.show();
    }

    private void uploadVoice(String file, final MsgAllBean bean) {
        uploadMap.put(bean.getMsg_id(), bean);
        uploadList.add(bean);
        updateSendStatus(ChatEnum.ESendStatus.SENDING, bean);
        new UpFileAction().upFile(UpFileAction.PATH.VOICE, context, new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
                Log.v(ChatActivity.class.getSimpleName(), "上传语音成功--" + url);
                VoiceMessage voice = bean.getVoiceMessage();
                voice.setUrl(url);
                SocketData.sendAndSaveMessage(bean);
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
            config.setLastOffset(lastOffset);
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
            InputUtil.showKeyboard(edtChat);
            edtChat.requestFocus();
        }
    }

    private void showVoice(boolean show) {
        if (show) {//开启语音
            txtVoice.setVisibility(View.VISIBLE);
            btnVoice.setImageDrawable(getResources().getDrawable(R.mipmap.ic_chat_kb));
            edtChat.setVisibility(View.GONE);
            btnSend.setVisibility(GONE);
            btnFunc.setVisibility(VISIBLE);
        } else {//关闭语音
            txtVoice.setVisibility(View.GONE);
            btnVoice.setImageDrawable(getResources().getDrawable(R.mipmap.ic_chat_vio));
            edtChat.setVisibility(View.VISIBLE);
            if (StringUtil.isNotNull(edtChat.getText().toString())) {
                btnSend.setVisibility(VISIBLE);
            } else {
                btnSend.setVisibility(GONE);
            }
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
        if (isLoadHistory) {
            return;
        }
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
        if (isLoadHistory) {
            isLoadHistory = false;
        }
//        System.out.println(TAG + "scrollListView");
        if (msgListData != null) {
            int length = msgListData.size();//刷新后当前size；
            if (isMustBottom) {
                mtListView.getListView().scrollToPosition(length);
            } else {
                if (lastPosition >= 0 && lastPosition < length) {
                    if (isSoftShow || lastPosition == length - 1 || isCanScrollBottom()) {//允许滑动到底部，或者当前处于底部，canScrollVertically是否能向上 false表示到了底部
                        mtListView.getListView().scrollToPosition(length);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventCheckVoice(ActivityForwordEvent event) {
        PictureSelector.create(ChatActivity.this)
                .openCamera(PictureMimeType.ofImage())
                .compress(true)
                .forResult(PictureConfig.REQUEST_CAMERA);
    }

    private String[] strings = new String[]{"拍照", "录制视频"};

    private void showDownLoadDialog() {
        final PopupSelectView popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                if (postsion == 0) {
                    PictureSelector.create(ChatActivity.this)
                            .openCamera(PictureMimeType.ofImage())
                            .compress(true)
                            .forResult(PictureConfig.REQUEST_CAMERA);
                } else if (postsion == 1) {
                    Intent intent = new Intent(ChatActivity.this, RecordedActivity.class);
                    startActivityForResult(intent, VIDEO_RP);
                } else {

                }
                popupSelectView.dismiss();

            }
        });
        popupSelectView.showAtLocation(findViewById(R.id.ll_big_bg), Gravity.BOTTOM, 0, 0);

    }


    @Override
    protected void onStop() {
        super.onStop();
        AudioPlayManager.getInstance().stopPlay();
        if (currentPlayBean != null) {
            updatePlayStatus(currentPlayBean, 0, ChatEnum.EPlayStatus.NO_PLAY);
        }
        boolean hasUpdate = dao.updateMsgRead(toUId, toGid, true);
        boolean hasChange = updateSessionDraftAndAtMessage();
        if (hasUpdate || hasChange) {
            MessageManager.getInstance().setMessageChange(true);
            MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, null);
        } /*else {
            MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, null);
        }*/
    }

    @Override
    protected void onDestroy() {
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
            msgDao.insertOrUpdateMessage(SocketData.createMessageLock(toGid, toUId));
        }
        initData();

    }

    private void initData() {
        if (!isLoadHistory) {
            taskRefreshMessage(false);
        }
        initUnreadCount();
        initPopupWindow();

        // 只有Vip才显示视频通话
//        UserInfo userInfo = UserAction.getMyInfo();
//        if (userInfo != null && !"1".equals(userInfo.getVip())) {
//            viewFunc.removeView(llChatVideoCall);
//        }
    }

    private void initUnreadCount() {
        new RunUtils(new RunUtils.Enent() {
            String s = "";

            @Override
            public void onRun() {
                long count = msgDao.getUnreadCount(toGid, toUId);
                if (count > 0 && count <= 99) {
                    s = count + "";
                } else if (count > 99) {
                    s = 99 + "+";
                }
            }

            @Override
            public void onMain() {
                if (s.contains("+")) {
                    actionbar.setTxtLeft(s, R.drawable.shape_unread_oval_bg, DensityUtil.sp2px(ChatActivity.this, 5));
                } else {
                    actionbar.setTxtLeft(s, R.drawable.shape_unread_bg, DensityUtil.sp2px(ChatActivity.this, 5));
                }
            }
        }).run();

    }

    private boolean clickAble = false;

    @Override
    protected void onResume() {
        super.onResume();
        //激活当前会话
        if (isGroup()) {
            MessageManager.getInstance().setSessionGroup(toGid);
        } else {
            MessageManager.getInstance().setSessionSolo(toUId);
        }
        //刷新群资料
        taskSessionInfo();
        clickAble = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //取消激活会话
        MessageManager.getInstance().setSessionNull();

    }

    private UpFileAction upFileAction = new UpFileAction();

    private String getVideoAtt(String mUri) {
        VideoMessage videoMessage = new VideoMessage();
        String duration = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
//                HashMap<String, String> headers = null;
//                if (headers == null)
//                {
//                    headers = new HashMap<String, String>();
//                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
//                }
                FileInputStream inputStream = new FileInputStream(new File(mUri).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());
//                mmr.setDataSource(mUri, headers);
            } else {
                //mmr.setDataSource(mFD, mOffset, mLength);
            }
            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)

        } catch (Exception ex) {
            Log.e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return duration;
    }

    private String getVideoAttWeith(String mUri) {
        VideoMessage videoMessage = new VideoMessage();
        String width = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
//                HashMap<String, String> headers = null;
//                if (headers == null)
//                {
//                    headers = new HashMap<String, String>();
//                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
//                }
                FileInputStream inputStream = new FileInputStream(new File(mUri).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());
//                mmr.setDataSource(mUri, headers);
            } else {
                //mmr.setDataSource(mFD, mOffset, mLength);
            }
            width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽

        } catch (Exception ex) {
            Log.e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return width;
    }

    private String getVideoAttHeigh(String mUri) {
        VideoMessage videoMessage = new VideoMessage();
        String height = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                FileInputStream inputStream = new FileInputStream(new File(mUri).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());
//                mmr.setDataSource(mUri, headers);
            } else {
                //mmr.setDataSource(mFD, mOffset, mLength);
            }
            height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高

        } catch (Exception ex) {
            Log.e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return height;
    }

    private String getVideoAttBitmap(String mUri) {
        VideoMessage videoMessage = new VideoMessage();
        File file = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                FileInputStream inputStream = new FileInputStream(new File(mUri).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());
//                mmr.setDataSource(mUri, headers);
            } else {
                //mmr.setDataSource(mFD, mOffset, mLength);
            }
            file = GroupHeadImageUtil.save2File(mmr.getFrameAtTime());
        } catch (Exception ex) {
            Log.e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return file.getAbsolutePath();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case VIDEO_RP:
                    int dataType = data.getIntExtra(RecordedActivity.INTENT_DATA_TYPE, RecordedActivity.RESULT_TYPE_VIDEO);
                    MsgAllBean videoMsgBean = null;
                    if (dataType == RecordedActivity.RESULT_TYPE_VIDEO) {
                        String file = data.getStringExtra(RecordedActivity.INTENT_PATH);
                        int height = data.getIntExtra(RecordedActivity.INTENT_PATH_HEIGHT, 0);
                        int width = data.getIntExtra(RecordedActivity.INTENT_VIDEO_WIDTH, 0);
                        int time = data.getIntExtra(RecordedActivity.INTENT_PATH_TIME, 0);
                        final boolean isArtworkMaster = requestCode == PictureConfig.REQUEST_CAMERA ? true : data.getBooleanExtra(PictureConfig.IS_ARTWORK_MASTER, false);
                        final String imgMsgId = SocketData.getUUID();
                        VideoMessage videoMessage = new VideoMessage();
                        videoMessage.setHeight(height);
                        videoMessage.setWidth(width);
                        videoMessage.setDuration(time);
                        videoMessage.setBg_url(getVideoAttBitmap(file));
                        videoMessage.setLocalUrl(file);
                        Log.e("TAG", videoMessage.toString() + videoMessage.getHeight() + "----" + videoMessage.getWidth() + "----" + videoMessage.getDuration() + "----" + videoMessage.getBg_url() + "----");
                        VideoMessage videoMessageSD = SocketData.createVideoMessage(imgMsgId, "file://" + file, videoMessage.getBg_url(), false, videoMessage.getDuration(), videoMessage.getWidth(), videoMessage.getHeight(), file);

//                        Log.e("TAG",videoMessage.toString()+videoMessage.getHeight()+"----"+videoMessage.getWidth()+"----"+videoMessage.getDuration()+"----"+videoMessage.getBg_url()+"----");
//                        VideoMessage videoMessageSD = SocketData.createVideoMessage(imgMsgId, "file://" + file, videoMessage.getBg_url(),false,videoMessage.getDuration(),videoMessage.getWidth(),videoMessage.getHeight(),file);
//                        MsgAllBean imgMsgBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, System.currentTimeMillis(), videoMessageSD, ChatEnum.EMessageType.MSG_VIDEO);
                        videoMsgBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, SocketData.getFixTime(), videoMessageSD, ChatEnum.EMessageType.MSG_VIDEO);
                        msgListData.add(videoMsgBean);
                        UpLoadService.onAddVideo(this.context, imgMsgId, file, videoMessage.getBg_url(), isArtworkMaster, toUId, toGid, time, videoMessageSD);
                        startService(new Intent(getContext(), UpLoadService.class));

                    } else if (dataType == RecordedActivity.RESULT_TYPE_PHOTO) {
                        String photoPath = data.getStringExtra(RecordedActivity.INTENT_PATH);
                        String file = photoPath;

                        final boolean isArtworkMaster = requestCode == PictureConfig.REQUEST_CAMERA ? true : data.getBooleanExtra(PictureConfig.IS_ARTWORK_MASTER, false);
                        boolean isGif = FileUtils.isGif(file);
                        if (isArtworkMaster || isGif) {
                            //  Toast.makeText(this,"原图",Toast.LENGTH_LONG).show();
                            file = photoPath;
                        }
                        //1.上传图片
                        // alert.show();
                        final String imgMsgId = SocketData.getUUID();
                        // 记录本次上传图片的ID跟本地路径
                        mTempImgPath.put(imgMsgId, "file://" + file);
                        ImageMessage imageMessage = SocketData.createImageMessage(imgMsgId, "file://" + file, isArtworkMaster);
                        videoMsgBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, SocketData.getFixTime(), imageMessage, ChatEnum.EMessageType.IMAGE);
                        msgListData.add(videoMsgBean);
                        UpLoadService.onAdd(imgMsgId, file, isArtworkMaster, toUId, toGid, -1);
                        startService(new Intent(getContext(), UpLoadService.class));
                    }
                    notifyData2Bottom(true);
                    MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, videoMsgBean);

                    break;
                case PictureConfig.REQUEST_CAMERA:
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> obt = PictureSelector.obtainMultipleResult(data);
                    MsgAllBean imgMsgBean = null;
                    for (LocalMedia localMedia : obt) {
                        String file = localMedia.getCompressPath();
                        if (StringUtil.isNotNull(file)) {
                            final boolean isArtworkMaster = requestCode == PictureConfig.REQUEST_CAMERA ? true : data.getBooleanExtra(PictureConfig.IS_ARTWORK_MASTER, false);
                            boolean isGif = FileUtils.isGif(file);
                            if (isArtworkMaster || isGif) {
                                //  Toast.makeText(this,"原图",Toast.LENGTH_LONG).show();
                                file = localMedia.getPath();
                            }
                            //1.上传图片
                            // alert.show();
                            final String imgMsgId = SocketData.getUUID();
                            // 记录本次上传图片的ID跟本地路径
                            mTempImgPath.put(imgMsgId, "file://" + file);
                            ImageMessage imageMessage = SocketData.createImageMessage(imgMsgId, "file://" + file, isArtworkMaster);
                            imgMsgBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, SocketData.getFixTime(), imageMessage, ChatEnum.EMessageType.IMAGE);
                            msgListData.add(imgMsgBean);
                            UpLoadService.onAdd(imgMsgId, file, isArtworkMaster, toUId, toGid, -1);
                            startService(new Intent(getContext(), UpLoadService.class));
                        } else {
//                            ToastUtil.show(this, "图片已损坏，请重新选择");

                            String videofile = localMedia.getPath();
                            if (null != videofile) {
                                //                            int height = data.getIntExtra(RecordedActivity.INTENT_PATH_HEIGHT, 0);
//                            int width = data.getIntExtra(RecordedActivity.INTENT_VIDEO_WIDTH, 0);
//                            int time = data.getIntExtra(RecordedActivity.INTENT_PATH_TIME, 0);
                                final boolean isArtworkMaster = requestCode == PictureConfig.REQUEST_CAMERA ? true : data.getBooleanExtra(PictureConfig.IS_ARTWORK_MASTER, false);
                                final String imgMsgId = SocketData.getUUID();
                                VideoMessage videoMessage = new VideoMessage();
//                                videoMessage.setHeight(Long.parseLong(getVideoAttWeith(videofile)));
//                                videoMessage.setWidth(Long.parseLong(getVideoAttHeigh(videofile)));
                                videoMessage.setHeight(Long.parseLong(getVideoAttHeigh(videofile)));
                                videoMessage.setWidth(Long.parseLong(getVideoAttWeith(videofile)));
                                videoMessage.setDuration(Long.parseLong(getVideoAtt(videofile)));
                                videoMessage.setBg_url(getVideoAttBitmap(videofile));
                                videoMessage.setLocalUrl(videofile);
                                Log.e("TAG", videoMessage.toString() + videoMessage.getHeight() + "----" + videoMessage.getWidth() + "----" + videoMessage.getDuration() + "----" + videoMessage.getBg_url() + "----");
                                VideoMessage videoMessageSD = SocketData.createVideoMessage(imgMsgId, "file://" + videofile, videoMessage.getBg_url(), false, videoMessage.getDuration(), videoMessage.getWidth(), videoMessage.getHeight(), videofile);
                                imgMsgBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, SocketData.getFixTime(), videoMessageSD, ChatEnum.EMessageType.MSG_VIDEO);
                                msgListData.add(imgMsgBean);
                                UpLoadService.onAddVideo(this.context, imgMsgId, videofile, videoMessage.getBg_url(), isArtworkMaster, toUId, toGid, videoMessage.getDuration(), videoMessageSD);
                                startService(new Intent(getContext(), UpLoadService.class));
                            } else {
                                ToastUtil.show(this, "文件已损坏，请重新选择");
                            }
                        }
                    }
                    MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, imgMsgBean);
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
                        MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllbean);
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

            MsgAllBean msgAllbean = SocketData.send4card(toUId, toGid, userInfo.getUid(), userInfo.getHead(), userInfo.getName(), userInfo.getImid());
            showSendObj(msgAllbean);
            MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllbean);
        }/* else if (resultCode == REQ_REFRESH) {//刷新返回时需要刷新聊天列表数据
            mks.clear();
            taskRefreshMessage();
        }*/
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshChat(EventGroupChange event) {
        if (event.isNeedLoad()) {
            taskGroupInfo();
        } else {
            taskSessionInfo();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EtaskRefreshMessagevent(EventRefreshChat event) {
        taskRefreshMessage(event.isScrollBottom);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void taskUpImgEvevt(EventUpImgLoadEvent event) {
//        Log.d("tag", "taskUpImgEvevt state: ===============>" + event.getState() + "--msgId==" + event.getMsgid() );
        if (event.getState() == 0) {
            // Log.d("tag", "taskUpImgEvevt 0: ===============>"+event.getMsgId());
            taskRefreshImage(event.getMsgid());
        } else if (event.getState() == -1) {
            //处理失败的情况
//            Log.d("tag", "taskUpImgEvevt -1: ===============>" + event.getMsgId());
            MsgAllBean msgAllbean = (MsgAllBean) event.getMsgAllBean();
            replaceListDataAndNotify(msgAllbean, true);
        } else if (event.getState() == 1) {
            //  Log.d("tag", "taskUpImgEvevt 1: ===============>"+event.getMsgId());
            MsgAllBean msgAllbean = (MsgAllBean) event.getMsgAllBean();
//            if (msgAllbean.getVideoMessage()!=null&&msgAllbean.getVideoMessage().getLocalUrl()!=null){
//                                        MsgDao dao = new MsgDao();
//                        dao.fixVideoLocalUrl(msgAllbean.getMsg_id(), msgAllbean.getVideoMessage().getLocalUrl());
//            }
            replaceListDataAndNotify(msgAllbean);
        } else {
            //  Log.d("tag", "taskUpImgEvevt 2: ===============>"+event.getMsgId());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void stopVoiceeEvent(net.cb.cb.library.event.EventFactory.StopVoiceeEvent event) {
        // 对方撤回时，停止语音播放
        if (event != null) {
            if (event.msg_id.equals(AudioPlayManager.getInstance().msg_id)) {
                AudioPlayManager.getInstance().stopPlay();
            }
        }
    }

    private void setChatImageBackground() {
        UserSeting seting = new MsgDao().userSetingGet();
        if (seting == null) {
            mtListView.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_100));
            return;
        }
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
     * 替换listData中的某条消息并且刷新
     * @param msgAllbean
     */
    private void replaceListDataAndNotify(MsgAllBean msgAllbean, boolean loose) {

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
            lc.setMsg_id(msgid);
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
                    showPop(view, menus, message, listener, null);
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
//        if (!NetUtil.isNetworkConnected()) {
//            return;
//        }
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
                    taskRefreshMessage(false);
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
            } else if (reMsg.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO) {
                //todo 重新上传视频
                String url = reMsg.getVideoMessage().getLocalUrl();
                if (!TextUtils.isEmpty(url)) {

                    VideoMessage videoMessage = reMsg.getVideoMessage();
                    Log.e("TAG", videoMessage.toString() + videoMessage.getHeight() + "----" + videoMessage.getWidth() + "----" + videoMessage.getDuration() + "----" + videoMessage.getBg_url() + "----");
                    VideoMessage videoMessageSD = SocketData.createVideoMessage(reMsg.getMsg_id(), "file://" + url, videoMessage.getBg_url(), false, videoMessage.getDuration(), videoMessage.getWidth(), videoMessage.getHeight(), url);
                    MsgAllBean imgMsgBeanReSend = SocketData.sendFileUploadMessagePre(reMsg.getMsg_id(), toUId, toGid, SocketData.getFixTime(), videoMessageSD, ChatEnum.EMessageType.MSG_VIDEO);
                    replaceListDataAndNotify(imgMsgBeanReSend);
//                    msgListData.add(imgMsgBeanReSend);
                    UpLoadService.onAddVideo(this.context, reMsg.getMsg_id(), url, videoMessage.getBg_url(), false, toUId, toGid, videoMessage.getDuration(), videoMessageSD);
                    startService(new Intent(getContext(), UpLoadService.class));

                } else {
                    //点击发送的时候如果要改变成发送中的状态
                    reMsg.setSend_state(ChatEnum.ESendStatus.SENDING);
                    DaoUtil.update(reMsg);
                    MsgBean.UniversalMessage.Builder bean = MsgBean.UniversalMessage.parseFrom(reMsg.getSend_data()).toBuilder();
                    SocketUtil.getSocketUtil().sendData4Msg(bean);
                    taskRefreshMessage(false);

//                    EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
//                    // upProgress.setProgress(100);
//                    eventUpImgLoadEvent.setMsgid(reMsg.getMsg_id());
//                    eventUpImgLoadEvent.setState(1);
//                    eventUpImgLoadEvent.setUrl(url);
//                    EventBus.getDefault().post(eventUpImgLoadEvent);

                }
            } else {
                //点击发送的时候如果要改变成发送中的状态
                reMsg.setSend_state(ChatEnum.ESendStatus.SENDING);
                DaoUtil.update(reMsg);
                MsgBean.UniversalMessage.Builder bean = MsgBean.UniversalMessage.parseFrom(reMsg.getSend_data()).toBuilder();
                SocketUtil.getSocketUtil().sendData4Msg(bean);
                taskRefreshMessage(false);
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
                        LogUtil.getLog().i(TAG, "更新进度--msgId=" + msgbean.getMsg_id() + "--progress=" + pg);

                        holder.viewChatItem.setImageProgress(pg);
                        holder.viewChatItem.setErr(msgbean.getSend_state());
//                        holder.viewChatItem.updateSendStatusAndProgress(msgbean.getSend_state(), pg);

                        if (msgbean.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
                            menus.add(new OptionMenu("转发"));
                            menus.add(new OptionMenu("删除"));
                        } else {
                            menus.add(new OptionMenu("删除"));
                        }

                        break;
                    case ChatEnum.EMessageType.VOICE:
                        holder.viewChatItem.updateVoice(msgbean);

                        if (msgbean.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
                            menus.add(new OptionMenu("转发"));
                            menus.add(new OptionMenu("删除"));
                        }

//                        LogUtil.getLog().i(TAG, "刷新语音updateVoice" + "--position=" + position);
                        break;
                    case ChatEnum.EMessageType.MSG_VIDEO:
                        Integer pgVideo = null;
                        pgVideo = UpLoadService.getProgress(msgbean.getMsg_id());
                        LogUtil.getLog().i(TAG, "更新进度--msgId=" + msgbean.getMsg_id() + "--progress=" + pgVideo);
                        holder.viewChatItem.setErr(msgbean.getSend_state());
                        holder.viewChatItem.setImageProgress(pgVideo);

                        if (msgbean.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
                            menus.add(new OptionMenu("转发"));
                            menus.add(new OptionMenu("删除"));
                            holder.viewChatItem.setVideoIMGShow(true);
                        } else if (msgbean.getSend_state() == ChatEnum.ESendStatus.SENDING) {
                            holder.viewChatItem.setVideoIMGShow(false);
                        } else {
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
                if (StringUtil.isNotNull(msgbean.getFrom_group_nickname())) {
                    nikeName = StringUtil.isNotNull(nikeName) ? nikeName : msgbean.getFrom_group_nickname();
                } else {
                    nikeName = StringUtil.isNotNull(nikeName) ? nikeName : msgbean.getFrom_nickname();
                }
                //fusinfo.getName();
//                nikeName = StringUtil.isNotNull(nikeName) ? nikeName : msgbean.getFrom_nickname();//fusinfo.getName();


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
                        String name = msgDao.getUsername4Show(toGid, msgbean.getFrom_uid());
                        String txt = edtChat.getText().toString().trim();
                        if (!txt.contains("@" + name)) {
                            if (!TextUtils.isEmpty(name)) {
                                edtChat.addAtSpan("@", name, msgbean.getFrom_uid());
                            } else {
                                name = TextUtils.isEmpty(msgbean.getFrom_group_nickname()) ? msgbean.getFrom_nickname() : msgbean.getFrom_group_nickname();
                                edtChat.addAtSpan("@", name, msgbean.getFrom_uid());
                            }
                            scrollListView(true);
                        }
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
                case ChatEnum.EMessageType.NOTICE:
                    if (msgbean.getMsgNotice() != null) {
//                        holder.viewChatItem.setData0(msgbean.getMsgNotice().getNote());
                        if (msgbean.getMsgNotice().getMsgType() == MsgNotice.MSG_TYPE_DEFAULT
                                || msgbean.getMsgNotice().getMsgType() == ChatEnum.ENoticeType.RED_ENVELOPE_RECEIVED_SELF
                                || msgbean.getMsgNotice().getMsgType() == ChatEnum.ENoticeType.BLACK_ERROR) {
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
                case ChatEnum.EMessageType.MSG_CENCAL:// 撤回消息
                    if (msgbean.getMsgCancel() != null) {
                        // 发送消息小于5分钟显示 重新编辑
                        Long mss = System.currentTimeMillis() - msgbean.getTimestamp();
                        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
                        String content = "";
                        Integer msgType = 0;
                        if (msgbean.getStamp() != null) {
                            content = msgbean.getStamp().getComment();
                            if (StringUtil.isNotNull(msgbean.getStamp().getMsgId())) {
                                msgType = Integer.parseInt(msgbean.getStamp().getMsgId());// MsgId存放的是撤回的消息类型
                            }
                        }
                        // 是文本且小于5分钟 显示重新编辑
                        if ((msgType == ChatEnum.EMessageType.TEXT || msgType == ChatEnum.EMessageType.AT)
                                && minutes < RELINQUISH_TIME && StringUtil.isNotNull(content)) {
                            onRestEdit(holder, msgbean.getMsgCancel().getNote(), content, msgbean.getTimestamp());
                        } else {
                            if (msgbean.getMsgCancel().getMsgType() == MsgNotice.MSG_TYPE_DEFAULT) {
                                holder.viewChatItem.setData0(msgbean.getMsgCancel().getNote());
                            } else {
                                holder.viewChatItem.setData0(new HtmlTransitonUtils().getSpannableString(ChatActivity.this,
                                        msgbean.getMsgCancel().getNote(), msgbean.getMsgCancel().getMsgType()));
                            }
                        }
                    }
                    break;
                case ChatEnum.EMessageType.TEXT:
                    menus.add(new OptionMenu("复制"));
                    menus.add(new OptionMenu("转发"));
                    menus.add(new OptionMenu("删除"));
                    holder.viewChatItem.setData1(msgbean.getChat().getMsg());
                    break;
                case ChatEnum.EMessageType.STAMP:

                    menus.add(new OptionMenu("删除"));
                    holder.viewChatItem.setData2(msgbean.getStamp().getComment());
                    break;

                case ChatEnum.EMessageType.RED_ENVELOPE:
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

                case ChatEnum.EMessageType.IMAGE:
                    if (msgbean.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
                        menus.add(new OptionMenu("转发"));
                        menus.add(new OptionMenu("删除"));
                    } else {
                        menus.add(new OptionMenu("删除"));
                    }
                    Integer pg = null;
                    pg = UpLoadService.getProgress(msgbean.getMsg_id());


                    holder.viewChatItem.setData4(msgbean.getImage(), msgbean.getImage().getThumbnailShow(), new ChatItemView.EventPic() {
                        @Override
                        public void onClick(String uri) {
                            //  ToastUtil.show(getContext(), "大图:" + uri);
                            showBigPic(msgbean.getMsg_id(), uri);
                        }
                    }, pg);
                    // holder.viewChatItem.setImageProgress(pg);
                    break;

                case ChatEnum.EMessageType.MSG_VIDEO:
                    if (msgbean.getSend_state() == ChatEnum.ESendStatus.SENDING) {
//                        holder.viewChatItem.setVideoIMGShow(false);
                    } else if (msgbean.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
                        menus.add(new OptionMenu("转发"));
                        menus.add(new OptionMenu("删除"));
                        holder.viewChatItem.setVideoIMGShow(true);
                        Log.e("TAG", "2");
                    } else {
                        menus.add(new OptionMenu("删除"));
                    }

                    Integer pgVideo = null;
                    pgVideo = UpLoadService.getProgress(msgbean.getMsg_id());

                    holder.viewChatItem.setDataVideo(msgbean.getVideoMessage(), msgbean.getVideoMessage().getUrl(), new ChatItemView.EventPic() {
                        @Override
                        public void onClick(String uri) {
                            //  ToastUtil.show(getContext(), "大图:" + uri);
//                            showBigPic(msgbean.getMsg_id(), uri);
                            // 判断是否正在音视频通话
                            if (AVChatProfile.getInstance().isCallIng() || AVChatProfile.getInstance().isCallEstablished()) {
                                if (AVChatProfile.getInstance().isChatType() == AVChatType.VIDEO.getValue()) {
                                    ToastUtil.show(ChatActivity.this, getString(R.string.avchat_peer_busy_video));
                                } else {
                                    ToastUtil.show(ChatActivity.this, getString(R.string.avchat_peer_busy_voice));
                                }
                            } else if (clickAble) {
                                clickAble = false;
                                String localUrl = msgbean.getVideoMessage().getLocalUrl();
                                if (StringUtil.isNotNull(localUrl)) {
                                    File file = new File(localUrl);
                                    if (file.exists()) {
//                                        downVideo(msgbean, msgbean.getVideoMessage());
                                        Log.e("TAG", file.getAbsolutePath());
//                                        Intent intent = new Intent(ChatActivity.this, VideoPlayActivity.class);
//                                        intent.putExtra("videopath", localUrl);
//                                        intent.putExtra("videomsg", new Gson().toJson(msgbean));
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                                        startActivity(intent);

                                    } else {
//                                        downVideo(msgbean, msgbean.getVideoMessage());
                                        localUrl = msgbean.getVideoMessage().getUrl();
                                    }
                                } else {
//                                    downVideo(msgbean, msgbean.getVideoMessage());
                                    localUrl = msgbean.getVideoMessage().getUrl();
                                }
                                Intent intent = new Intent(ChatActivity.this, VideoPlayActivity.class);
                                intent.putExtra("videopath", localUrl);
//                                intent.putExtra("videopath", localUrl);
                                intent.putExtra("videomsg", new Gson().toJson(msgbean));
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);

                            }
                        }
                    }, pgVideo);
                    // holder.viewChatItem.setImageProgress(pg);
                    break;
                case ChatEnum.EMessageType.BUSINESS_CARD:

                    menus.add(new OptionMenu("删除"));
                    holder.viewChatItem.setData5(msgbean.getBusiness_card().getNickname(),
                            msgbean.getBusiness_card().getComment(),
                            msgbean.getBusiness_card().getAvatar(), "个人名片", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // ToastUtil.show(getContext(), "添加好友需要详情页面");
                                    //不是自己的名片，才可以点击
                                    if (msgbean.getBusiness_card().getUid().longValue() != UserAction.getMyId().longValue()) {
                                        if (isGroup() && !master.equals(msgbean.getBusiness_card().getUid().toString())) {
                                            startActivity(new Intent(getContext(), UserInfoActivity.class)
                                                    .putExtra(UserInfoActivity.ID, msgbean.getBusiness_card().getUid())
                                                    .putExtra(UserInfoActivity.IS_BUSINESS_CARD, contactIntimately));

                                        } else {
                                            startActivity(new Intent(getContext(), UserInfoActivity.class)
                                                    .putExtra(UserInfoActivity.ID, msgbean.getBusiness_card().getUid()));
                                        }
                                    }
                                }
                            });
                    break;
                case ChatEnum.EMessageType.TRANSFER:
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
                case ChatEnum.EMessageType.VOICE://语音消息

                    menus.add(new OptionMenu("删除"));
                    final VoiceMessage vm = msgbean.getVoiceMessage();
                    String url = msgbean.isMe() ? vm.getLocalUrl() : vm.getUrl();
                    holder.viewChatItem.setData7(vm.getTime(), msgbean.isRead(), AudioPlayManager.getInstance().isPlay(Uri.parse(url)), vm.getPlayStatus(), new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            // 判断是否正在音视频通话
                            if (AVChatProfile.getInstance().isCallIng() || AVChatProfile.getInstance().isCallEstablished()) {
                                if (AVChatProfile.getInstance().isChatType() == AVChatType.VIDEO.getValue()) {
                                    ToastUtil.show(ChatActivity.this, getString(R.string.avchat_peer_busy_video));
                                } else {
                                    ToastUtil.show(ChatActivity.this, getString(R.string.avchat_peer_busy_voice));
                                }
                            } else {
                                playVoice(msgbean, position);
                            }
                        }
                    });


                    break;
                case ChatEnum.EMessageType.AT:
                    menus.add(new OptionMenu("复制"));
                    menus.add(new OptionMenu("转发"));
                    menus.add(new OptionMenu("删除"));
                    holder.viewChatItem.setDataAt(msgbean.getAtMessage().getMsg());
                    break;
                case ChatEnum.EMessageType.ASSISTANT:
                    holder.viewChatItem.setDataAssistant(msgbean.getAssistantMessage().getMsg());
                    break;
                case ChatEnum.EMessageType.MSG_VOICE_VIDEO:
                    menus.add(new OptionMenu("删除"));
                    holder.viewChatItem.setDataVoiceOrVideo(msgbean.getP2PAuVideoMessage().getDesc(), msgbean.getP2PAuVideoMessage().getAv_type(), new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (msgbean.getP2PAuVideoMessage().getAv_type() == MsgBean.AuVideoType.Audio.getNumber()) {
                                gotoVoiceActivity();
                            } else {
                                gotoVideoActivity();
                            }
                        }
                    });
                    break;
                case ChatEnum.EMessageType.LOCK:
//                    holder.viewChatItem.setLock(msgbean.getChat().getMsg());
                    holder.viewChatItem.setLock(new HtmlTransitonUtils().getSpannableString(ChatActivity.this, msgbean.getChat().getMsg(), ChatEnum.ENoticeType.LOCK));
                    break;


            }

            holder.viewChatItem.setOnErr(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //从数据拉出来,然后再发送
                    resendMessage(msgbean);
                }
            });
            itemLongClick(holder, msgbean, menus);

        }

        /**
         * 重新编辑
         *
         * @param holder
         * @param value
         * @param restContent 撤回內容
         * @param timesTamp   撤回時間
         */
        private void onRestEdit(RCViewHolder holder, String value, String restContent, Long timesTamp) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            value = value + "  " + REST_EDIT;
            int startIndex = value.indexOf(REST_EDIT);
            int endIndex = startIndex + REST_EDIT.length();

            builder.append(value);
            //设置部分文字点击事件
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // 大于5分钟后不可撤回
                    Long mss = System.currentTimeMillis() - timesTamp;
                    long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
                    if (minutes >= RELINQUISH_TIME) {
                        ToastUtil.show(ChatActivity.this, "重新编辑不能超过5分钟");
                    } else {
                        showVoice(false);
                        InputUtil.showKeyboard(edtChat);
                        edtChat.setText(edtChat.getText().toString() + restContent);
                        edtChat.requestFocus();
                        edtChat.setSelection(edtChat.getText().length());
                    }
                }

                @Override
                public void updateDrawState(@androidx.annotation.NonNull TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            };
            builder.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(Color.parseColor("#276baa"));
            builder.setSpan(protocolColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.viewChatItem.setData0(builder);
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
                        if (msgbean.getFrom_uid() != null && msgbean.getFrom_uid().longValue() == UserAction.getMyId().longValue() && msgbean.getMsg_type() != ChatEnum.EMessageType.RED_ENVELOPE && !isAtBanedCancel(msgbean)) {
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
                    }, holder);
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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            MsgDao dao = new MsgDao();
            dao.fixVideoLocalUrl(bundle.getString("msgid"), bundle.getString("url"));

        }
    };

    private void downVideo(final MsgAllBean msgAllBean, final VideoMessage videoMessage) {

        final File appDir = new File(getExternalCacheDir().getAbsolutePath() + "/Mp4/");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        final String fileName = MyDiskCache.getFileNmae(msgAllBean.getVideoMessage().getUrl()) + ".mp4";
        final File fileVideo = new File(appDir, fileName);
//        videoMessage.setLocalUrl(fileVideo.getAbsolutePath());
        new Thread() {
            @Override
            public void run() {
                try {

                    DownloadUtil.get().download(msgAllBean.getVideoMessage().getUrl(), appDir.getAbsolutePath(), fileName, new DownloadUtil.OnDownloadListener() {
                        @Override
                        public void onDownloadSuccess(File file) {
                            Intent intent = new Intent(ChatActivity.this, VideoPlayActivity.class);
                            intent.putExtra("videopath", fileVideo.getAbsolutePath());
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("msgid", msgAllBean.getVideoMessage().getMsgId());
                            bundle.putString("url", fileVideo.getAbsolutePath());
                            message.setData(bundle);
                            handler.sendMessage(message);
                            videoMessage.setLocalUrl(fileVideo.getAbsolutePath());
//                        msgAllBean.setVideoMessage(videoMessage);
//                        MsgAllBean imgMsgBean = SocketData.sendFileUploadMessagePre(reMsg.getMsg_id(), toUId, toGid, reMsg.getTimestamp(), image, ChatEnum.EMessageType.IMAGE);
//                        VideoMessage videoMessageSD = SocketData.createVideoMessage(imgMsgId, "file://" + file, videoMessage.getBg_url(),false,videoMessage.getDuration(),videoMessage.getWidth(),videoMessage.getHeight(),file);
                            startActivity(intent);
                            MyDiskCacheUtils.getInstance().putFileNmae(appDir.getAbsolutePath(), fileVideo.getAbsolutePath());
                        }

                        @Override
                        public void onDownloading(int progress) {

                        }

                        @Override
                        public void onDownloadFailed(Exception e) {

                        }
                    });

                } catch (Exception e) {

                }

            }
        }.start();

    }

    //是否禁止撤销at消息,群主自己发的群公告，不能撤消
    private boolean isAtBanedCancel(MsgAllBean bean) {
        if (bean.getMsg_type() == ChatEnum.EMessageType.AT) {
            AtMessage message = bean.getAtMessage();
            if (message.getAt_type() == ChatEnum.EAtType.ALL && message.getUid().size() == 0) {
                return true;
            }
        }
        return false;
    }

    private void playVoice(MsgAllBean msgBean, int position) {
        currentPlayBean = msgBean;
        List<MsgAllBean> list = new ArrayList<>();
        boolean isAutoPlay = false;
        if (!msgBean.isMe() && !isVoiceRead(msgBean)) {
            list.add(msgBean);
            int length = msgListData.size();
            if (position < length - 1) {
                for (int i = position + 1; i < length; i++) {
                    MsgAllBean bean = msgListData.get(i);
                    if (bean.getMsg_type() == ChatEnum.EMessageType.VOICE && !bean.isMe() && !isVoiceRead(bean)) {
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
                if (bean.getMsg_type() == ChatEnum.EMessageType.VOICE && !bean.isMe() && !isVoiceRead(bean)) {
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

    private boolean isVoiceRead(MsgAllBean bean) {
        VoiceMessage voice = bean.getVoiceMessage();
        if (voice != null && voice.getPlayStatus() != ChatEnum.EPlayStatus.NO_DOWNLOADED) {
            return true;
        }
        return false;

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
            if (bean.getVoiceMessage().getPlayStatus() == ChatEnum.EPlayStatus.NO_DOWNLOADED && !bean.isMe()) {
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
//                notifyData();
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
                         final IMenuSelectListener listener, RecyclerViewAdapter.RCViewHolder holder) {
        //禁止滑动
//        mtListView.getListView().setNestedScrollingEnabled(true);

        initPopWindowEvent(msgbean);
        setMessageType(menus);

        // 重新获取自身的长宽高
        mRootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupWidth = mRootView.getMeasuredWidth();
        popupHeight = mRootView.getMeasuredHeight();

        // 获取ActionBar位置，判断消息是否到顶部
        // 获取ListView在屏幕顶部的位置
        int[] location = new int[2];
        mtListView.getLocationOnScreen(location);
        // 获取View在屏幕的位置
        int[] locationView = new int[2];
        v.getLocationOnScreen(locationView);

        mPopupWindow = new PopupWindow(mRootView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置弹窗外可点击
        mPopupWindow.setTouchable(true);
        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        popupWindowDismiss(listener);
        // 当View Y轴的位置小于ListView Y轴的位置时 气泡向下弹出来，否则向上弹出
        if (locationView[1] < location[1]) {
            mImgTriangleUp.setVisibility(VISIBLE);
            mImgTriangleDown.setVisibility(GONE);
            mPopupWindow.showAsDropDown(v);
        } else {
            mImgTriangleUp.setVisibility(GONE);
            mImgTriangleDown.setVisibility(VISIBLE);
            showPopupWindowUp(v);
        }
    }

    /**
     * 初始化PopupWindow
     */
    private void initPopupWindow() {
        mRootView = getLayoutInflater().inflate(R.layout.view_chat_bubble, null);
        //获取自身的长宽高
        mRootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupWidth = mRootView.getMeasuredWidth();
        popupHeight = mRootView.getMeasuredHeight();

        mImgTriangleUp = mRootView.findViewById(R.id.img_triangle_up);
        mImgTriangleDown = mRootView.findViewById(R.id.img_triangle_down);
        layoutContent = mRootView.findViewById(R.id.layout_content);
        mTxtView1 = mRootView.findViewById(R.id.txt_value1);
        mTxtView2 = mRootView.findViewById(R.id.txt_value2);
        mTxtView3 = mRootView.findViewById(R.id.txt_value3);
        mTxtView4 = mRootView.findViewById(R.id.txt_value4);
        mTxtDelete = mRootView.findViewById(R.id.txt_delete);
        mViewLine1 = mRootView.findViewById(R.id.view_line1);
        mViewLine2 = mRootView.findViewById(R.id.view_line2);
        mViewLine3 = mRootView.findViewById(R.id.view_line3);
    }

    private void initPopWindowEvent(final MsgAllBean msgbean) {
        mTxtView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null) mPopupWindow.dismiss();
                onBubbleClick(mTxtView1.getText().toString(), msgbean);
            }
        });
        mTxtView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null) mPopupWindow.dismiss();
                onBubbleClick(mTxtView2.getText().toString(), msgbean);
            }
        });
        mTxtView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null) mPopupWindow.dismiss();
                onBubbleClick(mTxtView3.getText().toString(), msgbean);
            }
        });
        mTxtView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null) mPopupWindow.dismiss();
                onBubbleClick(mTxtView4.getText().toString(), msgbean);
            }
        });
        mTxtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null) mPopupWindow.dismiss();
                onBubbleClick(mTxtDelete.getText().toString(), msgbean);
            }
        });
    }

    /**
     * 气泡点击事件处理
     *
     * @param value
     * @param msgbean
     */
    private void onBubbleClick(String value, MsgAllBean msgbean) {
        if ("复制".equals(value)) {
            onCopy(msgbean);
        } else if ("删除".equals(value)) {
            onDelete(msgbean);
        } else if ("听筒播放".equals(value)) {
            msgDao.userSetingVoicePlayer(1);
        } else if ("转发".equals(value)) {
            onRetransmission(msgbean);
        } else if ("撤回".equals(value)) {
            onRecall(msgbean);
        } else if ("扬声器播放".equals(value)) {
            msgDao.userSetingVoicePlayer(0);
        }
    }

    /**
     * 复制
     *
     * @param msgbean
     */
    private void onCopy(MsgAllBean msgbean) {
        String txt = "";
        if (msgbean.getMsg_type() == ChatEnum.EMessageType.AT) {
            txt = msgbean.getAtMessage().getMsg();
        } else {
            txt = msgbean.getChat().getMsg();
        }
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText(txt, txt);
        cm.setPrimaryClip(mClipData);
    }

    /**
     * 删除
     *
     * @param msgbean
     */
    private void onDelete(final MsgAllBean msgbean) {
        AlertYesNo alertYesNo = new AlertYesNo();
        alertYesNo.init(ChatActivity.this, "删除", "确定删除吗?", "确定", "取消", new AlertYesNo.Event() {
            @Override
            public void onON() {

            }

            @Override
            public void onYes() {
                msgDao.msgDel4MsgId(msgbean.getMsg_id());
                msgListData.remove(msgbean);
                MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, null);
                notifyData();
            }
        });
        alertYesNo.show();
    }

    /**
     * 转发
     *
     * @param msgbean
     */
    private void onRetransmission(final MsgAllBean msgbean) {
        startActivity(new Intent(getContext(), MsgForwardActivity.class)
                .putExtra(MsgForwardActivity.AGM_JSON, new Gson().toJson(msgbean)));
    }

    /**
     * 撤回
     *
     * @param msgbean
     */
    private void onRecall(final MsgAllBean msgbean) {
        String msg = "";
        Integer msgType = 0;
        if (msgbean.getChat() != null) {
            msg = msgbean.getChat().getMsg();
        } else if (msgbean.getAtMessage() != null) {
            msg = msgbean.getAtMessage().getMsg();
        }
        msgType = msgbean.getMsg_type();
        MsgAllBean msgAllbean = SocketData.send4CancelMsg(toUId, toGid, msgbean.getMsg_id(), msg, msgType);
        MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllbean);

    }

    /**
     * 设置不同的消息类型弹出对应气泡
     *
     * @param menus
     */
    private void setMessageType(List<OptionMenu> menus) {
        if (menus.size() == 1) {
            layoutContent.setVisibility(GONE);
            mTxtDelete.setVisibility(VISIBLE);
            mTxtDelete.setText(menus.get(0).getTitle());
        } else if (menus.size() == 2) {
            layoutContent.setVisibility(VISIBLE);
            mTxtDelete.setVisibility(GONE);
            mTxtView1.setVisibility(VISIBLE);
            mTxtView2.setVisibility(GONE);
            mTxtView3.setVisibility(GONE);
            mTxtView4.setVisibility(VISIBLE);
            mViewLine1.setVisibility(VISIBLE);
            mViewLine2.setVisibility(GONE);
            mViewLine3.setVisibility(GONE);
            mTxtView1.setText(menus.get(0).getTitle());
            mTxtView4.setText(menus.get(1).getTitle());
        } else if (menus.size() == 3) {
            layoutContent.setVisibility(VISIBLE);
            mTxtDelete.setVisibility(GONE);
            mTxtView1.setVisibility(VISIBLE);
            mTxtView2.setVisibility(VISIBLE);
            mTxtView3.setVisibility(GONE);
            mTxtView4.setVisibility(VISIBLE);
            mViewLine1.setVisibility(VISIBLE);
            mViewLine2.setVisibility(VISIBLE);
            mViewLine3.setVisibility(GONE);
            mTxtView1.setText(menus.get(0).getTitle());
            mTxtView2.setText(menus.get(1).getTitle());
            mTxtView4.setText(menus.get(2).getTitle());
        } else if (menus.size() == 4) {
            layoutContent.setVisibility(VISIBLE);
            mTxtDelete.setVisibility(GONE);
            mTxtView1.setVisibility(VISIBLE);
            mTxtView2.setVisibility(VISIBLE);
            mTxtView3.setVisibility(VISIBLE);
            mTxtView4.setVisibility(VISIBLE);
            mViewLine1.setVisibility(VISIBLE);
            mViewLine2.setVisibility(VISIBLE);
            mViewLine3.setVisibility(VISIBLE);
            mTxtView1.setText(menus.get(0).getTitle());
            mTxtView2.setText(menus.get(1).getTitle());
            mTxtView3.setText(menus.get(2).getTitle());
            mTxtView4.setText(menus.get(3).getTitle());
        }
    }

    /**
     * 设置显示在v上方(以v的左边距为开始位置)
     *
     * @param v
     */
    public void showPopupWindowUp(View v) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //在控件上方显示
        mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
    }

    /**
     * 恢复气泡的默认背景颜色
     *
     * @param listener
     */
    public void popupWindowDismiss(final IMenuSelectListener listener) {
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (listener != null) {
                    listener.onSelected();
                }
            }
        });
    }


    private void notifyData2Bottom(boolean isScrollBottom) {
        notifyData();
        scrollListView(isScrollBottom);
    }

    private void notifyData() {
        if (isNewAdapter) {
            messageAdapter.bindData(msgListData);
        }
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
            groupInfo = msgDao.getGroup4Id(toGid);
            if (groupInfo != null) {
                if (!TextUtils.isEmpty(groupInfo.getName())) {
                    title = groupInfo.getName();
                } else {
                    title = "群聊";
                }
                int memberCount = 0;
                if (groupInfo.getUsers() != null) {
                    memberCount = groupInfo.getUsers().size();
                }
                if (memberCount > 0) {
                    title = title + "(" + memberCount + ")";
                }

                //如果自己不在群里面
                boolean isExit = false;
                for (MemberUser uifo : groupInfo.getUsers()) {
                    if (uifo.getUid() == UserAction.getMyId().longValue()) {
                        isExit = true;
                    }
                }
                setBanView(!isExit);
            }
            //6.15 设置右上角点击
            taskGroupConf();

        } else {
            UserInfo finfo = userDao.findUserInfo(toUId);
            if (finfo == null && toUId == 100121L) {
                finfo = new UserInfo();
                finfo.setUid(100121L);
                finfo.setName("常信客服");
            }
            title = finfo.getName4Show();
            if (finfo.getLastonline() > 0) {
                actionbar.setTitleMore(TimeToString.getTimeOnline(finfo.getLastonline(), finfo.getActiveType(), true));
            }
        }
        actionbar.setTitle(title);
        setDisturb();
    }

    private void setDisturb() {
        Session session = dao.sessionGet(toGid, toUId);
        if (session == null) {
            return;
        }
        actionbar.showDisturb(session.getIsMute() == 1);

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
    private void taskRefreshMessage(boolean isScrollBottom) {
        if (needRefresh) {
            needRefresh = false;
        }

        dismissPop();
//        Log.d("1212", "taskRefreshMessage()");
//        System.out.println(TAG + "--taskRefreshMessage");
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
                        onBusPicture();
                        int len = list.size();
                        if (len == 0 && lastPosition > len - 1) {//历史数据被清除了
                            lastPosition = 0;
                            lastOffset = 0;
                            clearScrollPosition();
                        }
                        notifyData2Bottom(isScrollBottom);
//                        notifyData();
                    }
                });

    }

    private void dismissPop() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * TODO 当本次有本地发送图片时，用本地图片路径展示，是为了解决发图片之后，在发内容第一次会闪一下重新加载问题，
     * TODO 问题原因是第一次加载本地路径，图片上传成功后加载的是服务器路径
     */
    private void onBusPicture() {
        if (mTempImgPath != null && mTempImgPath.size() > 0 && msgListData != null) {
            for (MsgAllBean bean : msgListData) {
                for (String key : mTempImgPath.keySet()) {
                    if (bean.getMsg_id().equals(key)) {
                        bean.getImage().setThumbnail(mTempImgPath.get(key));
                    }
                }
            }
        }
    }

    /***
     * 查询历史
     * @param history
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void taskFinadHistoryMessage(EventFindHistory history) {
        isLoadHistory = true;
        msgListData = msgAction.getMsg4UserHistory(toGid, toUId, history.getStime());
//        ToastUtil.show(getContext(), "历史" + msgListData.size());
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
        } else {
            msgListData = msgAction.getMsg4User(toGid, toUId, null, false);
        }

        addItem = msgListData.size() - addItem;
        taskMkName(msgListData);
        onBusPicture();
        notifyData();
//        LogUtil.getLog().i(ChatActivity.class.getSimpleName(), "size=" + msgListData.size());

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
            if (msg.getMsg_type() == ChatEnum.EMessageType.NOTICE || msg.getMsg_type() == ChatEnum.EMessageType.MSG_CENCAL || msg.getMsg_type() == ChatEnum.EMessageType.LOCK || msg.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO) {  //通知类型的不处理
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


//            Log.d("tak", "taskName: " + nkname);

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
        Session session = StringUtil.isNotNull(toGid) ? DaoUtil.findOne(Session.class, "gid", toGid) :
                DaoUtil.findOne(Session.class, "from_uid", toUId);
        if (session != null && session.getUnread_count() > 0) {
            dao.sessionReadClean(session);
            MessageManager.getInstance().setMessageChange(true);
            MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, null);
        }
    }

    /***
     * 获取草稿
     */
    private void taskDraftGet() {
        session = dao.sessionGet(toGid, toUId);
        if (session == null)
            return;
        draft = session.getDraft();
        if (StringUtil.isNotNull(draft)) {
            //设置完草稿之后清理掉草稿 防止@功能不能及时弹出
            edtChat.setText(session.getDraft());
//            dao.sessionDraft(toGid, toUId, "");`

        }
        // isFirst解决第一次进来草稿中会有@符号的内容
        isFirst++;
    }

    /***
     * 更新session草稿和at消息
     *
     */
    private boolean updateSessionDraftAndAtMessage() {
        boolean hasChange = checkAndSaveDraft();
        if (session != null && !TextUtils.isEmpty(session.getAtMessage())) {
            hasChange = true;
            dao.updateSessionAtMsg(toGid, toUId);
        }
        return hasChange;
    }

    private boolean checkAndSaveDraft() {
        if (isGroup() && !MessageManager.getInstance().isGroupValid(groupInfo)) {//无效群，不存草稿
            return false;
        }
        String df = edtChat.getText().toString().trim();
        boolean hasChange = false;
        if (!TextUtils.isEmpty(draft)) {
            if (TextUtils.isEmpty(df) || !draft.equals(df)) {
                hasChange = true;
                dao.sessionDraft(toGid, toUId, df);
                draft = df;
            }
        } else {
            if (!TextUtils.isEmpty(df)) {
                hasChange = true;
                dao.sessionDraft(toGid, toUId, df);
                draft = df;
            }
        }
        return hasChange;
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
                                    MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllbean);
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
        UserInfo info = UserAction.getMyInfo();
        if (info != null && info.getLockCloudRedEnvelope() == 1) {//红包功能被锁定
            ToastUtil.show(this, "您的云红包功能已暂停使用，如有疑问请咨询官方客服号");
            return;
        }
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
                            //0 正常状态未领取，1 红包已经被领取，2 红包失效不能领取，3 红包未失效但已经被领完，4 普通红包并且用户点击自己红包
                            int envelopeStatus = grabRpBean.getEnvelopeStatus();
                            if (envelopeStatus == 0 && grabRpBean.isHadGrabRp()) {
                                MsgAllBean msgAllbean = SocketData.send4RbRev(toUId, toGid, rbid);
                                showSendObj(msgAllbean);
                                MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllbean);
                                taskPayRbCheck(msgbean, rbid);
                            }
                            if (envelopeStatus == 2 || envelopeStatus == 3) {
                                taskPayRbCheck(msgbean, rbid);
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
     * 红包是否已经被抢,红包改为失效
     * @param rid
     */
    private void taskPayRbCheck(MsgAllBean msgAllBean, final String rid) {
        msgAllBean.getRed_envelope().setIsInvalid(1);
        msgDao.redEnvelopeOpen(rid, true);
        replaceListDataAndNotify(msgAllBean);
    }

    private Group groupInfo;

    //获取群资料
    private MemberUser getGroupInfo(long uid) {
        if (groupInfo == null)
            return null;
        List<MemberUser> users = groupInfo.getUsers();
        for (MemberUser uinfo : users) {
            if (uinfo.getUid() == uid) {
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

//                groupInfo = response.body().getData();
                groupInfo = msgDao.getGroup4Id(toGid);
                if (groupInfo != null) {
                    contactIntimately = groupInfo.getContactIntimately();
                    master = groupInfo.getMaster();
                }

                if (groupInfo == null) {//取不到群信息了
                    groupInfo = new Group();
                    groupInfo.setMaster("");
                    groupInfo.setUsers(new RealmList<MemberUser>());
                }

                if (groupInfo.getMaster().equals(UserAction.getMyId().toString())) {//本人群主
                    viewChatRobot.setVisibility(View.VISIBLE);
                } else {
                    viewFunc.removeView(viewChatRobot);
                }
                taskSessionInfo();
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
//            int onCreate = size - 1;
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

    private void sendHypertext(List<String> list, int position) {
        if (position == list.size() - 1) {
            isSendingHypertext = false;
        }
        textPosition = position;
        MsgAllBean msgAllbean = SocketData.send4Chat(toUId, toGid, list.get(position));
        showSendObj(msgAllbean);
        MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllbean);
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
