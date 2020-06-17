package com.yanlong.im.chat.ui.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.nim_lib.config.Preferences;
import com.example.nim_lib.controll.AVChatProfile;
import com.example.nim_lib.ui.VideoActivity;
import com.example.nim_lib.util.GlideUtil;
import com.google.gson.Gson;
import com.hm.cxpay.bean.CxEnvelopeBean;
import com.hm.cxpay.bean.CxTransferBean;
import com.hm.cxpay.bean.EnvelopeDetailBean;
import com.hm.cxpay.bean.GrabEnvelopeBean;
import com.hm.cxpay.bean.TransferDetailBean;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.dailog.DialogDefault;
import com.hm.cxpay.dailog.DialogEnvelope;
import com.hm.cxpay.eventbus.NoticeReceiveEvent;
import com.hm.cxpay.eventbus.TransferSuccessEvent;
import com.hm.cxpay.global.PayEnum;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.BindPhoneNumActivity;
import com.hm.cxpay.ui.bill.BillDetailActivity;
import com.hm.cxpay.ui.payword.SetPaywordActivity;
import com.hm.cxpay.ui.redenvelope.MultiRedPacketActivity;
import com.hm.cxpay.ui.redenvelope.SingleRedPacketActivity;
import com.hm.cxpay.ui.transfer.TransferActivity;
import com.hm.cxpay.ui.transfer.TransferDetailActivity;
import com.jrmf360.rplib.JrmfRpClient;
import com.jrmf360.rplib.bean.EnvelopeBean;
import com.jrmf360.rplib.bean.GrabRpBean;
import com.jrmf360.rplib.utils.callback.GrabRpCallBack;
import com.jrmf360.tools.utils.ThreadUtil;
import com.luck.picture.lib.PicturePreviewActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.DoubleUtils;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropMulti;
import com.yalantis.ucrop.util.FileUtils;
import com.yanlong.im.BuildConfig;
import com.yanlong.im.MainActivity;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.adapter.AdapterPopMenu;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.AdMessage;
import com.yanlong.im.chat.bean.AtMessage;
import com.yanlong.im.chat.bean.BalanceAssistantMessage;
import com.yanlong.im.chat.bean.BusinessCardMessage;
import com.yanlong.im.chat.bean.ChatMessage;
import com.yanlong.im.chat.bean.CollectAtMessage;
import com.yanlong.im.chat.bean.CollectChatMessage;
import com.yanlong.im.chat.bean.CollectImageMessage;
import com.yanlong.im.chat.bean.CollectLocationMessage;
import com.yanlong.im.chat.bean.CollectSendFileMessage;
import com.yanlong.im.chat.bean.CollectShippedExpressionMessage;
import com.yanlong.im.chat.bean.CollectVideoMessage;
import com.yanlong.im.chat.bean.CollectVoiceMessage;
import com.yanlong.im.chat.bean.EnvelopeInfo;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.GroupConfig;
import com.yanlong.im.chat.bean.IMsgContent;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.LocationMessage;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgCancel;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.chat.bean.MsgNotice;
import com.yanlong.im.chat.bean.OfflineCollect;
import com.yanlong.im.chat.bean.QuotedMessage;
import com.yanlong.im.chat.bean.ReadDestroyBean;
import com.yanlong.im.chat.bean.ReadMessage;
import com.yanlong.im.chat.bean.RedEnvelopeMessage;
import com.yanlong.im.chat.bean.ReplyMessage;
import com.yanlong.im.chat.bean.ScrollConfig;
import com.yanlong.im.chat.bean.SendFileMessage;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.ShippedExpressionMessage;
import com.yanlong.im.chat.bean.SingleMeberInfoBean;
import com.yanlong.im.chat.bean.StampMessage;
import com.yanlong.im.chat.bean.TransferMessage;
import com.yanlong.im.chat.bean.TransferNoticeMessage;
import com.yanlong.im.chat.bean.UserSeting;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.bean.WebMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.eventbus.AckEvent;
import com.yanlong.im.chat.eventbus.EventCollectImgOrVideo;
import com.yanlong.im.chat.eventbus.EventSwitchSnapshot;
import com.yanlong.im.chat.interf.IActionTagClickListener;
import com.yanlong.im.chat.interf.IMenuSelectListener;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.server.UpLoadService;
import com.yanlong.im.chat.ui.ChatInfoActivity;
import com.yanlong.im.chat.ui.FileDownloadActivity;
import com.yanlong.im.chat.ui.GroupInfoActivity;
import com.yanlong.im.chat.ui.GroupRobotActivity;
import com.yanlong.im.chat.ui.GroupSelectUserActivity;
import com.yanlong.im.chat.ui.VideoPlayActivity;
import com.yanlong.im.chat.ui.cell.ChatCellBase;
import com.yanlong.im.chat.ui.cell.ControllerNewMessage;
import com.yanlong.im.chat.ui.cell.FactoryChatCell;
import com.yanlong.im.chat.ui.cell.ICellEventListener;
import com.yanlong.im.chat.ui.cell.MessageAdapter;
import com.yanlong.im.chat.ui.cell.OnControllerClickListener;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.chat.ui.view.ControllerLinearList;
import com.yanlong.im.dialog.ForwardDialog;
import com.yanlong.im.dialog.LockDialog;
import com.yanlong.im.location.LocationActivity;
import com.yanlong.im.location.LocationSendEvent;
import com.yanlong.im.pay.action.PayAction;
import com.yanlong.im.pay.bean.SignatureBean;
import com.yanlong.im.pay.ui.record.SingleRedPacketDetailsActivity;
import com.yanlong.im.repository.ApplicationRepository;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.CollectionInfo;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.CollectionActivity;
import com.yanlong.im.user.ui.SelectUserActivity;
import com.yanlong.im.user.ui.ServiceAgreementActivity;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.ApkUtils;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.DestroyTimeView;
import com.yanlong.im.utils.GroupHeadImageUtil;
import com.yanlong.im.utils.PatternUtil;
import com.yanlong.im.utils.ReadDestroyUtil;
import com.yanlong.im.utils.UserUtil;
import com.yanlong.im.utils.audio.AudioPlayManager;
import com.yanlong.im.utils.audio.AudioRecordManager;
import com.yanlong.im.utils.audio.IAdioTouch;
import com.yanlong.im.utils.audio.IAudioRecord;
import com.yanlong.im.utils.audio.IVoicePlayListener;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SendList;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;
import com.yanlong.im.view.CustomerEditText;
import com.yanlong.im.view.HeadView2;
import com.yanlong.im.view.face.AddFaceActivity;
import com.yanlong.im.view.face.FaceView;
import com.yanlong.im.view.face.FaceViewPager;
import com.yanlong.im.view.face.ShowBigFaceActivity;
import com.yanlong.im.view.face.bean.FaceBean;
import com.yanlong.im.view.function.ChatExtendMenuView;
import com.yanlong.im.view.function.FunctionItemModel;
import com.zhaoss.weixinrecorded.activity.CameraActivity;
import com.zhaoss.weixinrecorded.util.ActivityForwordEvent;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.EventFileRename;
import net.cb.cb.library.bean.EventGroupChange;
import net.cb.cb.library.bean.EventIsShowRead;
import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.bean.EventSwitchDisturb;
import net.cb.cb.library.bean.EventUpFileLoadEvent;
import net.cb.cb.library.bean.EventUpImgLoadEvent;
import net.cb.cb.library.bean.EventUserOnlineChange;
import net.cb.cb.library.bean.EventVoicePlay;
import net.cb.cb.library.bean.GroupStatusChangeEvent;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.bean.VideoSize;
import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.dialog.DialogEnvelopePast;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.inter.ICustomerItemClick;
import net.cb.cb.library.manager.Constants;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.DeviceUtils;
import net.cb.cb.library.utils.DialogHelper;
import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.FileConfig;
import net.cb.cb.library.utils.GetImgUtils;
import net.cb.cb.library.utils.GsonUtils;
import net.cb.cb.library.utils.ImgSizeUtil;
import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.IntentUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.RunUtils;
import net.cb.cb.library.utils.ScreenShotListenManager;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.SoftKeyBoardListener;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertTouch;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.MultiListView;
import net.cb.cb.library.view.WebPageActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;
import me.kareluo.ui.OptionMenu;
import me.rosuh.filepicker.config.FilePickerManager;
import retrofit2.Call;
import retrofit2.Response;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static net.cb.cb.library.utils.FileUtils.SIZETYPE_B;

public class ChatActivity extends AppActivity implements IActionTagClickListener, ICellEventListener {
    private static String TAG = "ChatActivity";
    public final static int MIN_TEXT = 1000;//
    private final int RELINQUISH_TIME = 5;// 5分钟内显示重新编辑
    private final String REST_EDIT = "重新编辑";
    private final String IS_VIP = "1";// (0:普通|1:vip)
    public final static int MIN_UNREAD_COUNT = 15;
    private int MAX_UNREAD_COUNT = 80 * 4;//默认加载最大数据
    private List<String> uidList;
    private LinearLayout layoutInput;//输入框布局->这里需要拿到其高度
    private int layoutInputHeight = 0;//输入框布局高度

    //返回需要刷新的 8.19 取消自动刷新
    // public static final int REQ_REFRESH = 7779;
    private HeadView2 headView;
    private ActionbarView actionbar;
    private MultiListView mtListView;
    private ImageView btnVoice;
    private CustomerEditText editChat;
    private ImageView btnEmj;
    private ImageView btnFunc;
    private View viewChatBottom;
    private View viewChatBottomc;
    private Button btnSend;
    private Button txtVoice;
    // 表情控件视图
    protected FaceView viewFaceView;

    private Integer font_size;

    public static final String AGM_TOUID = "toUId";
    public static final String AGM_TOGID = "toGId";
    public static final String GROUP_CREAT = "creat";
    public static final String ONLINE_STATE = "if_online";
    public static final String SEARCH_TIME = "search_time";


    private Gson gson = new Gson();
    private CheckPermission2Util permission2Util = new CheckPermission2Util();

    private Long toUId = null;
    private String toGid = null;
    private boolean onlineState = true;//判断网络状态 true在线 false离线
    //当前页
    //private int indexPage = 0;
    private List<MsgAllBean> downloadList = new ArrayList<>();//下载列表
    private Map<String, MsgAllBean> uploadMap = new HashMap<>();//上传列表
    private List<MsgAllBean> uploadList = new ArrayList<>();//上传列表

    //红包和转账
    public static final int REQ_RP = 9653;
    public static final int VIDEO_RP = 9419;
    public static final int REQUEST_RED_ENVELOPE = 1 << 2;
//    public static final int REQUEST_TRANSFER = 1 << 3;

    private int lastOffset = -1;
    private int lastPosition = -1;
    private boolean isSoftShow;
    private boolean needRefresh;
    private List<String> sendTexts;//文本分段发送
    private boolean isSendingHypertext = false;
    private int textPosition;
    private int contactIntimately;
    private String master = "";
    private TextView tvBan;
    private String draft;
    private int isFirst;
    private SingleMeberInfoBean singleMeberInfoBean;// 单个群成员信息，主要查看是否被单人禁言

    // 气泡视图
    private PopupWindow mPopupWindow;// 长按消息弹出气泡PopupWindow
    private int popupWidth;// 气泡宽
    private int popupHeight;// 气泡高
    private ImageView mImgTriangleUp;// 上箭头
    private ImageView mImgTriangleDown;// 下箭头
    private View mRootView;
    private MsgAllBean currentPlayBean;
    private Session session;
    private boolean isLoadHistory = false;//是否是搜索历史信息
    private ReadDestroyUtil util = new ReadDestroyUtil();
    private int survivaltime;
    private DestroyTimeView destroyTimeView;
    private ControllerNewMessage viewNewMessage;
    private int unreadCount;
    private MessageAdapter mAdapter;
    private ChatExtendMenuView viewExtendFunction;
    private int currentScrollPosition;

    private ScreenShotListenManager screenShotListenManager;//截屏监听相关
    private boolean isScreenShotListen;//是否监听截屏
    private LinearLayout llMore;
    private ImageView ivDelete;
    private ImageView ivForward;
    private ControllerLinearList popController;
    //记录软键盘高度
    private String KEY_BOARD = "keyboard_setting";
    //软键盘高度
    private int mKeyboardHeight = 0;

    private ChatViewModel mViewModel = new ChatViewModel();

    private IAudioRecord audioRecord;
    private IAdioTouch audioTouchListener;

    private ApplicationRepository.SessionChangeListener sessionChangeListener = new ApplicationRepository.SessionChangeListener() {
        @Override
        public void init(RealmResults<Session> sessions, List<String> sids) {
            updateUnReadCount();
        }

        @Override
        public void delete(int[] positions) {
            updateUnReadCount();
        }

        @Override
        public void insert(int[] positions, List<String> sids) {
            updateUnReadCount();
        }

        @Override
        public void update(int[] positions, List<String> sids) {
            updateUnReadCount();
        }
    };
    private MsgAllBean replayMsg;
    private boolean isReplying;
    private ControllerReplyMessage viewReplyMessage;
    private long searchTime;

    //猜你想要发送的图片
    private PopupWindow popGuessUWant;//点击+号后展示的弹框
    private List<GetImgUtils.ImgBean> latestImgList;//获取最新拍摄/截屏加入的图片
    private String latestUrl = "";//最新加入的图片url

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        Window window = getWindow();
//
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //标题栏
        window.setStatusBarColor(getResources().getColor(R.color.blue_title));
        //底部导航栏
//        window.setNavigationBarColor(getResources().getColor(R.color.red_100));

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        findViews();
        initEvent();
        initObserver();
        getOftenUseFace();
    }


    private Runnable mPanelRecoverySoftInputModeRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewExtendFunction.getVisibility() == VISIBLE)
                viewExtendFunction.setVisibility(View.GONE);
            if (viewFaceView.getVisibility() == VISIBLE)
                viewFaceView.setVisibility(View.GONE);
            //设置改SoftInput模式为：顶起输入框
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    };


    private void initObserver() {
        long delayMillis = 500;
        mViewModel.isInputText.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean value) {
                if (value) {//打开
                    editChat.requestFocus();
                    InputUtil.showKeyboard(editChat);
                    //重置其他状态
                    mViewModel.recoveryOtherValue(mViewModel.isInputText);
                    mtListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mtListView.scrollToEnd();
                        }
                    }, delayMillis);
                } else {//关闭
                    //清除焦点
                    editChat.clearFocus();
                    // 关闭软键盘
                    InputUtil.hideKeyboard(editChat);
                }
            }
        });

        mViewModel.isOpenEmoj.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean value) {
                handler.removeCallbacks(mPanelRecoverySoftInputModeRunnable);
                if (value) {//打开
                    setPanelHeight(mKeyboardHeight, viewFaceView);
                    //虚拟键盘弹出,需更改SoftInput模式为：不顶起输入框
                    if (mViewModel.isInputText.getValue())
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                    btnEmj.setImageLevel(1);
                    //因为面板有延迟执行，所以必须执行该方法
                    viewExtendFunction.setVisibility(View.GONE);
                    viewFaceView.setVisibility(View.VISIBLE);
                    //重置其他状态
                    mViewModel.recoveryOtherValue(mViewModel.isOpenEmoj);
                    editChat.requestFocus();
                    //定位光标
                    editChat.setSelection(editChat.getSelectionEnd());
                    mtListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mtListView.scrollToEnd();
                        }
                    }, 100);
                } else {//关闭
                    btnEmj.setImageLevel(0);
                    if (mViewModel.isOpenValue()) {//有事件触发
                        if (mViewModel.isInputText.getValue()) {//无其他功能触发，则弹出输入框
                            /*******输入框弹出键盘，pos tDelayed关闭面板*****************************************/
//                       //更改SoftInput模式为：不顶起输入框
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                            editChat.requestFocus();
                            InputUtil.showKeyboard(editChat);
                            handler.postDelayed(mPanelRecoverySoftInputModeRunnable, delayMillis);
                        } else {//其他功能触发，非输入框触发，直接关闭当前面板
                            viewFaceView.setVisibility(View.GONE);
                        }
                    } else {//聊天时界面滑动，关闭面板
                        viewFaceView.setVisibility(View.GONE);
                    }
                }
            }
        });

        mViewModel.isOpenFuction.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean value) {
                handler.removeCallbacks(mPanelRecoverySoftInputModeRunnable);
                if (value) {//打开
                    setPanelHeight(mKeyboardHeight, viewExtendFunction);
                    //虚拟键盘弹出,需更改SoftInput模式为：不顶起输入框
                    if (mViewModel.isInputText.getValue())
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                    //因为面板有延迟执行，所以必须执行该方法
                    viewFaceView.setVisibility(View.GONE);
                    viewExtendFunction.setVisibility(View.VISIBLE);
                    //重置其他状态
                    mViewModel.recoveryOtherValue(mViewModel.isOpenFuction);
                    mtListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mtListView.scrollToEnd();
                        }
                    }, 100);
                } else {//关闭
                    if (mViewModel.isOpenValue()) {//有事件触发
                        if (mViewModel.isInputText.getValue()) {//无其他功能触发，则弹出输入框
                            /*******输入框弹出键盘，pos tDelayed关闭面板*****************************************/
//                       //更改SoftInput模式为：不顶起输入框
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                            editChat.requestFocus();
                            InputUtil.showKeyboard(editChat);
                            handler.postDelayed(mPanelRecoverySoftInputModeRunnable, delayMillis);
                        } else {//其他功能触发，非输入框触发，直接关闭当前面板
                            viewExtendFunction.setVisibility(View.GONE);
                        }
                    } else {//聊天时界面滑动，关闭面板
                        viewExtendFunction.setVisibility(View.GONE);
                    }
                }
            }
        });
        mViewModel.isOpenSpeak.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean value) {
                if (value) {//打开
                    //保存原有草稿
                    originalText = editChat.getText().toString();
                    if (originalText.length() > 0) {
                        editChat.setText("");
                    }
                    //重置其他状态
                    mViewModel.recoveryOtherValue(mViewModel.isOpenSpeak);
                    showVoice(true);
                } else {//关闭
                    //显示草稿
                    if (!TextUtils.isEmpty(originalText)) {
                        showDraftContent(originalText);
                    }
                    showVoice(false);
                }
            }
        });

        mViewModel.isReplying.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean value) {
                if (value) {
                    viewReplyMessage.setVisible(true);
                } else {
                    viewReplyMessage.setVisible(false);
                }
            }
        });

    }

    private RealmObjectChangeListener<RealmModel> groupInfoChangeListener = new RealmObjectChangeListener<RealmModel>() {
        @Override
        public void onChange(RealmModel realmModel, @javax.annotation.Nullable ObjectChangeSet changeSet) {
            if (changeSet.isDeleted()) {//对象被删除，退出聊天界面
                finish();
            } else {//字段被修改
                refreshUI();
            }
        }
    };
    private RealmObjectChangeListener<RealmModel> userInfoChangeListener = new RealmObjectChangeListener<RealmModel>() {
        @Override
        public void onChange(RealmModel realmModel, @javax.annotation.Nullable ObjectChangeSet changeSet) {
            if (changeSet.isDeleted()) {//对象被删除，退出聊天界面
                finish();
            } else {//字段被修改
                refreshUI();
            }
        }
    };

    /***
     * 获取会话信息
     */
    private void refreshUI() {
        try {
            String title = "";
            if (isGroup()) {
                if (mViewModel.groupInfo != null) {
                    contactIntimately = mViewModel.groupInfo.getContactIntimately();
                    master = mViewModel.groupInfo.getMaster();
                    if (!TextUtils.isEmpty(mViewModel.groupInfo.getName())) {
                        title = mViewModel.groupInfo.getName();
                    } else {
                        title = "群聊";
                    }

                    //显示成员数量，数量为0则不显示
                    int memberCount = mViewModel.groupInfo.getUsers() == null ? 0 : mViewModel.groupInfo.getUsers().size();
                    actionbar.setNumber(memberCount, memberCount > 0);
                    //如果自己不在群里面
                    boolean isExit = false;
                    for (MemberUser uifo : mViewModel.groupInfo.getUsers()) {
                        if (uifo.getUid() == UserAction.getMyId().longValue()) {
                            isExit = true;
                        }
                    }
                    boolean forbid = mViewModel.groupInfo.getStat() == ChatEnum.EGroupStatus.BANED;
                    setBanView(!isExit, forbid);
                    //6.15 设置右上角点击
                    taskGroupConf();

                }
            } else {
                if (mViewModel.userInfo != null) {
                    title = mViewModel.userInfo.getName4Show();
                    if (mViewModel.userInfo.getLastonline() > 0) {
                        // 客服不显示时间状态
                        if (onlineState && !UserUtil.isSystemUser(mViewModel.toUId) && mViewModel.userInfo.getuType() != ChatEnum.EUserType.ASSISTANT) {
                            actionbar.setTitleMore(TimeToString.getTimeOnline(mViewModel.userInfo.getLastonline(), mViewModel.userInfo.getActiveType(), true), true);
                        } else {
                            actionbar.setTitleMore(TimeToString.getTimeOnline(mViewModel.userInfo.getLastonline(), mViewModel.userInfo.getActiveType(), true), false);
                        }
                    }
                }
            }
            actionbar.setChatTitle(title);
            setDisturb();
            initSurvivaltimeState();
            viewExtendFunction.bindDate(getItemModels());
        } catch (Exception e) {
        }
    }

    private String originalText = "";

    @Override
    protected void onResume() {
        super.onResume();
        if (mViewModel != null) mViewModel.checkRealmStatus();
        //激活当前会话
        if (isGroup()) {
            MessageManager.getInstance().setSessionGroup(toGid);
        } else {
            MessageManager.getInstance().setSessionSolo(toUId);
        }
        //刷新群资料
        refreshUI();
        clickAble = true;
        //更新阅后即焚状态
        initSurvivaltimeState();
        if (AppConfig.isOnline()) {
            checkHasEnvelopeSendFailed();
        }
        isScreenShotListen = checkSnapshotPower();
        if (isScreenShotListen) {
            initScreenShotListener();
        }
        editChat.clearFocus();
        resumeRecord();
    }

    private void resumeRecord() {
        if (audioTouchListener != null) {
            audioTouchListener.restartRecord();
        }
        AudioRecordManager.getInstance(this).resumeRecord();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //取消激活会话
        MessageManager.getInstance().setSessionNull();
        saveOftenUseFace();
        // 注销监听
        stopScreenShotListener();
    }


    @Override
    protected void onStop() {
        super.onStop();
        MyAppLication.INSTANCE().removeSessionChangeListener(sessionChangeListener);
        AudioPlayManager.getInstance().stopPlay();
        stopRecordVoice();
        if (currentPlayBean != null) {
            updatePlayStatus(currentPlayBean, 0, ChatEnum.EPlayStatus.NO_PLAY);
        }
        boolean hasClear = taskCleanRead(false);
        boolean hasUpdate = dao.updateMsgRead(toUId, toGid, true);
        boolean hasChange = updateSessionDraftAndAtMessage();
        if (hasChange) {
            saveReplying(draft);
        }
    }

    //停止录音
    private void stopRecordVoice() {
        if (audioTouchListener != null) {
            audioTouchListener.cancelRecord();
        }
        if (audioRecord != null) {
            audioRecord.cancelRecord();
        }
        AudioRecordManager.getInstance(this).cancelRecord();
        txtVoice.setText("按住 说话");
        txtVoice.setBackgroundResource(R.drawable.bg_edt_chat);
        btnVoice.setEnabled(true);
        btnEmj.setEnabled(true);
        btnFunc.setEnabled(true);
    }

    private void stopScreenShotListener() {
//        LogUtil.getLog().i(TAG, "截屏--stop");
        if (screenShotListenManager != null) {
            screenShotListenManager.stopListen();
            isScreenShotListen = false;
            screenShotListenManager = null;
        }
    }

    @Override
    protected void onDestroy() {
        //释放adapter资源
        mAdapter.onDestroy();
        mViewModel.onDestory();
        //关闭窗口，避免内存溢出
        dismissPop();
        //保存退出即焚消息
        if (MyAppLication.INSTANCE().repository != null) {
            MyAppLication.INSTANCE().repository.saveExitSurvivalMsg(toGid, toUId);
        }
        //停止图片文件等上传
        if (!AppConfig.isAppRuning()) {
            UpLoadService.stopUpload();
        }
        //取消监听
        SocketUtil.getSocketUtil().removeEvent(msgEvent);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        //释放空间
        if (popGuessUWant != null) {
            popGuessUWant.dismiss();
            popGuessUWant = null;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        MyAppLication.INSTANCE().addSessionChangeListener(sessionChangeListener);
        if (!msgDao.isMsgLockExist(toGid, toUId)) {
            msgDao.insertOrUpdateMessage(SocketData.createMessageLock(toGid, toUId));
        }
        initData();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initIntent();
        initData();
    }

    private void initData() {
        //9.17 进去后就清理会话的阅读数量,初始化unreadCount
        taskCleanRead(true);
        initViewNewMsg();
        if (!isLoadHistory) {
            taskRefreshMessage(false);
        } else {
            loadHistoryMessage();
        }
        initUnreadCount();
        initPopupWindow();
        initReplyMsg();
    }

    private boolean checkCurrentImg() {
        latestImgList = GetImgUtils.getLatestPhoto(ChatActivity.this);
        if(latestImgList!=null && latestImgList.size()>0){
            GetImgUtils.ImgBean bean = latestImgList.get(0);
            //30秒内展示，超过不显示，url直接置空
            if(DateUtils.dateDiffer(bean.mTime)<=30){
                latestUrl = bean.imgUrl;
                //未展示过需要显示，展示过则不再显示(直接用SharedPreference缓存，不再加入到数据库)
                SharedPreferencesUtil spGuessYouLike = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.GUESS_YOU_LIKE);
                String saveUrl = spGuessYouLike.getString("current_img_url");
                //第二次判断缓存，若存在该url，则代表展示过，不再重复显示
                if(!TextUtils.isEmpty(saveUrl)){
                    if(saveUrl.equals(latestUrl)){
                        latestUrl = "";
                    }
                }else {
                    //第一次判断缓存，url必定为空，则展示
                }
            }else {
                latestUrl = "";
            }
        }
        //是否展示图片
        if(!TextUtils.isEmpty(latestUrl)){
            return true;
        }else {
            return false;
        }
    }

    private void initReplyMsg() {
        replayMsg = msgDao.getReplyingMsg(toGid, toUId);
        if (replayMsg != null) {
            isReplying = true;
            mViewModel.isReplying.setValue(true);
            viewReplyMessage.setMessage(replayMsg);
        }
    }

    private void initViewNewMsg() {
        int ramSize = DeviceUtils.getTotalRam();
        if (ramSize >= 2) {
            MAX_UNREAD_COUNT = 80 * 8;
        } else {
            MAX_UNREAD_COUNT = 80 * 4;
        }
//        viewNewMessage.setVisible(false);
//        mAdapter.setUnreadCount(0);
        if (unreadCount >= MIN_UNREAD_COUNT && unreadCount < MAX_UNREAD_COUNT) {
            viewNewMessage.setVisible(true);
            viewNewMessage.setCount(unreadCount);
            mAdapter.setUnreadCount(unreadCount);
        } else if (unreadCount >= MAX_UNREAD_COUNT) {
            unreadCount = MAX_UNREAD_COUNT;
            viewNewMessage.setVisible(true);
            viewNewMessage.setCount(unreadCount);
            mAdapter.setUnreadCount(unreadCount);
        } else {
            viewNewMessage.setVisible(false);
            mAdapter.setUnreadCount(0);
        }
    }

    //检测是否有截屏权限
    private boolean checkSnapshotPower() {
        if (isGroup()) {
            //群被封，全禁言，单个禁言，无截屏权限
            if (mViewModel.groupInfo == null || mViewModel.groupInfo.getStat() != ChatEnum.EGroupStatus.NORMAL || (!isAdmin() && mViewModel.groupInfo.getWordsNotAllowed() == 1)
                    || (singleMeberInfoBean != null && singleMeberInfoBean.getShutUpDuration() == 1) || mViewModel.groupInfo.getScreenshotNotification() == 0) {
                return false;
            } else {
                return true;
            }
        } else {
            if (mViewModel.userInfo != null) {
                return mViewModel.userInfo.getScreenshotNotification() == 1;
            }
        }
        return false;
    }

    //自动寻找控件
    private void findViews() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        mtListView = findViewById(R.id.mtListView);
        btnVoice = findViewById(R.id.btn_voice);
        editChat = findViewById(R.id.edit_chat);
        btnEmj = findViewById(R.id.btn_emj);
        btnFunc = findViewById(R.id.btn_func);
        viewChatBottom = findViewById(R.id.view_chat_bottom);
        viewChatBottomc = findViewById(R.id.view_chat_bottom_c);
        btnSend = findViewById(R.id.btn_send);
        txtVoice = findViewById(R.id.txt_voice);
        tvBan = findViewById(R.id.tv_ban);
        viewFaceView = findViewById(R.id.chat_view_faceview);
        viewNewMessage = new ControllerNewMessage(findViewById(R.id.viewNewMessage));
        setChatImageBackground();
        viewExtendFunction = findViewById(R.id.view_extend_menu);
        llMore = findViewById(R.id.ll_more);
        ivDelete = findViewById(R.id.iv_delete);
        ivForward = findViewById(R.id.iv_forward);
        layoutInput = findViewById(R.id.layout_input);
        mtListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //如果bottom小于oldBottom,说明键盘是弹起。
                if (bottom < oldBottom) {
//                    mViewModel.isInputText.setValue(true);
                    //滑动到底部
//                    mtListView.scrollToEnd();
                } else if (bottom > oldBottom && bottom - oldBottom == mKeyboardHeight) {//软键盘关闭，键盘右上角
                    mViewModel.isInputText.setValue(false);
                }
            }
        });
        //被回复消息显示控件
        viewReplyMessage = new ControllerReplyMessage(findViewById(R.id.viewReply));
    }


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
                    // TODO #41806 java.lang.IndexOutOfBoundsException
                    if (bean.getMsgIdList() != null && bean.getMsgIdList().size() > 0) {
                        fixSendTime(bean.getMsgId(0));
                    }
                    //群聊自己发送的消息直接加入阅后即焚队列
                    MsgAllBean msgAllBean = msgDao.getMsgById(bean.getMsgId(0));
                    if (bean.getRejectType() == MsgBean.RejectType.NOT_FRIENDS_OR_GROUP_MEMBER || bean.getRejectType() == MsgBean.RejectType.IN_BLACKLIST) {
                        taskRefreshMessage(false);
                    } else {
//                        if (UpLoadService.getProgress(bean.getMsgId(0)) == null /*|| UpLoadService.getProgress(bean.getMsgId(0)) == 100*/) {//忽略图片上传的刷新,图片上传成功后
//                            for (String msgid : bean.getMsgIdList()) {
//                                //撤回消息不做刷新
//                                if (ChatServer.getCancelList().containsKey(msgid)) {
//                                    LogUtil.getLog().i(TAG, "onACK: 收到取消回执,等待刷新列表2");
//                                    return;
//                                }
//                            }
//                        }
                        taskRefreshMessage(false);

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
                        if (msg.getMsgType() == MsgBean.MessageType.ACTIVE_STAT_CHANGE) {//
                            continue;
                        }
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
                    //从数据库读取消息，修改未通过eventbus来刷新
//                    if (needRefresh && !hasData()) {
//                        taskRefreshMessage(false);
//                    }
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
                    MsgAllBean msgAllBean = SendList.getMsgFromSendSequence(bean.getRequestId());
                    if (msgAllBean == null) {
                        msgAllBean = MsgConversionBean.ToBean(bean.getWrapMsg(0), bean, true);
                    }
                    if (msgAllBean == null || msgAllBean.getMsg_type() == null) {
                        return;
                    }
                    //过滤不需要存储消息
                    if (!SocketData.filterNoSaveMsgForFailed(msgAllBean.getMsg_type().intValue())) {
                        return;
                    }
                    msgAllBean.setSend_state(ChatEnum.ESendStatus.ERROR);
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
                    if (state) {
                        actionbar.getGroupLoadBar().setVisibility(GONE);
                        //联网后，显示单聊标题底部在线状态
                        if (!isGroup() && !UserUtil.isSystemUser(toUId)) {
                            actionbar.getTxtTitleMore().setVisibility(VISIBLE);
                        }
                        checkHasEnvelopeSendFailed();
                    } else {
                        actionbar.getGroupLoadBar().setVisibility(VISIBLE);
                        //断网后，隐藏单聊标题底部在线状态
                        if (!isGroup()) {
                            actionbar.getTxtTitleMore().setVisibility(GONE);

                        }
                    }
                    onlineState = state;
                }
            });
        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doAckEvent(AckEvent event) {
        Object data = event.getData();
        if (data instanceof MsgAllBean) {
            LogUtil.getLog().i(TAG, "收到回执--MsgAllBean");
            MsgAllBean msgAllBean = (MsgAllBean) data;
            fixSendTime(msgAllBean.getMsg_id());
            replaceListDataAndNotify(msgAllBean);
            sendRead();
        } else if (data instanceof MsgBean.AckMessage) {
            LogUtil.getLog().i(TAG, "收到回执--AckMessage");
            MsgBean.AckMessage bean = (MsgBean.AckMessage) data;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO #41806 java.lang.IndexOutOfBoundsException
                    if (bean.getMsgIdList() != null && bean.getMsgIdList().size() > 0) {
                        fixSendTime(bean.getMsgId(0));
                    }
                    //群聊自己发送的消息直接加入阅后即焚队列
                    MsgAllBean msgAllBean = msgDao.getMsgById(bean.getMsgId(0));
                    if (bean.getRejectType() == MsgBean.RejectType.NOT_FRIENDS_OR_GROUP_MEMBER || bean.getRejectType() == MsgBean.RejectType.IN_BLACKLIST) {
                        taskRefreshMessage(false);
                    } else {
//                        if (UpLoadService.getProgress(bean.getMsgId(0)) == null /*|| UpLoadService.getProgress(bean.getMsgId(0)) == 100*/) {//忽略图片上传的刷新,图片上传成功后
//                            for (String msgid : bean.getMsgIdList()) {
//                                //撤回消息不做刷新
//                                if (ChatServer.getCancelList().containsKey(msgid)) {
//                                    LogUtil.getLog().i(TAG, "onACK: 收到取消回执,等待刷新列表2");
//                                    return;
//                                }
//                            }
//                        }
                        taskRefreshMessage(false);
                    }
                }
            });
        }
        //是否是长文本消息
        if (isSendingHypertext) {
            if (sendTexts != null && sendTexts.size() > 0 && textPosition != sendTexts.size() - 1) {
                sendHypertext(sendTexts, textPosition + 1);
            }
        }
    }


    //消息的分发
    public void onMsgbranch(MsgBean.UniversalMessage.WrapMessage msg) {

        if (!isGroup()) {
            return;
        }
        if (!msg.getGid().equals(toGid)) {
            return;
        }
        switch (msg.getMsgType()) {
            case DESTROY_GROUP:
//                refreshUI();
                break;
            case REMOVE_GROUP_MEMBER://退出群
//                refreshUI();
                break;
            case ACCEPT_BE_GROUP://邀请进群刷新
//                refreshUI();
                break;
            case CHANGE_GROUP_META:// 修改群信息
//                refreshUI();
                break;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permission2Util.onRequestPermissionsResult();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //发送并滑动到列表底部
    private synchronized void showSendObj(MsgAllBean msgAllbean) {
        if (msgAllbean.getMsg_type() != ChatEnum.EMessageType.MSG_CANCEL) {
            int size = mAdapter.getItemCount();
            mAdapter.addMessage(msgAllbean);
            mtListView.getListView().getAdapter().notifyItemRangeInserted(size, 1);
            //红包通知 不滚动到底部
            if (msgAllbean.getMsgNotice() != null && (msgAllbean.getMsgNotice().getMsgType() == ChatEnum.ENoticeType.RECEIVE_RED_ENVELOPE
                    || msgAllbean.getMsgNotice().getMsgType() == ChatEnum.ENoticeType.RED_ENVELOPE_RECEIVED_SELF
                    || msgAllbean.getMsgNotice().getMsgType() == ChatEnum.ENoticeType.RECEIVE_SYS_ENVELOPE
                    || msgAllbean.getMsgNotice().getMsgType() == ChatEnum.ENoticeType.SYS_ENVELOPE_RECEIVED_SELF)) {
                return;
            }
            mtListView.scrollToEnd();
        } else {
            taskRefreshMessage(false);
        }
    }

    /**
     * 添加表情、发送自定义表情
     */
    protected void sendFace(FaceBean bean) {
        if (!checkNetConnectStatus(0)) {
            return;
        }
        if (FaceView.face_animo.equals(bean.getGroup())) {
            isSendingHypertext = false;

            ShippedExpressionMessage message = SocketData.createFaceMessage(SocketData.getUUID(), bean.getName());
            sendMessage(message, ChatEnum.EMessageType.SHIPPED_EXPRESSION);

        } else if (FaceView.face_emoji.equals(bean.getGroup()) || FaceView.face_lately_emoji.equals(bean.getGroup())) {
            editChat.addEmojSpan(bean.getName());
        } else if (FaceView.face_custom.equals(bean.getGroup())) {
            if ("add".equals(bean.getName())) {
                if (!ViewUtils.isFastDoubleClick()) {
                    mViewModel.isOpenEmoj.setValue(false);
                    IntentUtil.gotoActivity(this, AddFaceActivity.class);
                }
            } else {
                if (!checkNetConnectStatus(0)) {
                    return;
                }
                final String imgMsgId = SocketData.getUUID();
                ImageMessage imageMessage = SocketData.createImageMessage(imgMsgId, bean.getPath(), true);
                MsgAllBean msgAllBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, SocketData.getFixTime(), imageMessage, ChatEnum.EMessageType.IMAGE);
                mAdapter.addMessage(msgAllBean);
                // 不等于常信小助手
                if (!Constants.CX_HELPER_UID.equals(toUId)) {
                    final ImgSizeUtil.ImageSize img = ImgSizeUtil.getAttribute(bean.getPath());
                    SocketData.send4Image(imgMsgId, toUId, toGid, bean.getServerPath(), true, img, -1);
                }
                notifyData2Bottom(true);
            }
        }
    }

    /**
     * 显示草稿内容
     *
     * @param message
     */
    protected void showDraftContent(String message) {
        editChat.showDraftContent(message);
        if (message.length() > 0) editChat.setSelection(editChat.getText().length());
    }


    //自动生成的控件事件
    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initEvent() {
        //读取软键盘高度
        mKeyboardHeight = getSharedPreferences(KEY_BOARD, Context.MODE_PRIVATE).getInt(KEY_BOARD, 0);
        initIntent();
        //预先网络监听
        if (onlineState) {
            actionbar.getGroupLoadBar().setVisibility(GONE);
            //联网后，显示单聊标题底部在线状态
            if (!isGroup() && !UserUtil.isSystemUser(toUId)) {
                actionbar.getTxtTitleMore().setVisibility(VISIBLE);
            }
        } else {
            actionbar.getGroupLoadBar().setVisibility(VISIBLE);
            //断网后，隐藏单聊标题底部在线状态
            if (!isGroup()) {
                actionbar.getTxtTitleMore().setVisibility(GONE);
            }
        }
        toUId = toUId == 0 ? null : toUId;
        refreshUI();
        if (!TextUtils.isEmpty(mViewModel.toGid)) {
            taskGroupInfo();
        } else {
            //id不为0且不为客服则获取最新用户信息
            if (!UserUtil.isSystemUser(mViewModel.toUId) && (mViewModel.userInfo != null && mViewModel.userInfo.getuType() != ChatEnum.EUserType.ASSISTANT)) {
                httpGetUserInfo();
            }
        }
        actionbar.getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        if (isGroup()) {
            actionbar.getBtnRight().setVisibility(View.GONE);
            viewChatBottom.setVisibility(View.VISIBLE);
        } else {
            actionbar.getBtnRight().setVisibility(View.VISIBLE);
            viewChatBottom.setVisibility(View.VISIBLE);
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
                    if (mViewModel.toUId == 1L || mViewModel.toUId == 3L || (mViewModel.userInfo != null && mViewModel.userInfo.getuType() == ChatEnum.EUserType.ASSISTANT)) { //文件传输助手跳转(与常信小助手一致)
                        startActivity(new Intent(getContext(), UserInfoActivity.class)
                                .putExtra(UserInfoActivity.ID, mViewModel.toUId)
                                .putExtra(UserInfoActivity.JION_TYPE_SHOW, 1));
                    } else {
                        startActivity(new Intent(getContext(), ChatInfoActivity.class)
                                .putExtra(ChatInfoActivity.AGM_FUID, mViewModel.toUId));
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
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }

                if (!checkNetConnectStatus(0)) {
                    return;
                }
                //test 8.
                String text = editChat.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    ToastUtil.show(ChatActivity.this, "不能发送空白消息");
                    editChat.getText().clear();
                    return;
                }

                try {
                    if (AppConfig.DEBUG) {
                        if (text.startsWith("@000_")) { //文字测试
                            int count = Integer.parseInt(text.split("_")[1]);
                            taskTestSend(count);
                            return;
                        }
                        if (text.startsWith("@111_")) {//图片测试
                            int count = Integer.parseInt(text.split("_")[1]);
                            taskTestImage(count);
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                int totalSize = text.length();
                if (isGroup() && editChat.getUserIdList() != null && editChat.getUserIdList().size() > 0) {
                    if (totalSize > MIN_TEXT) {
                        ToastUtil.show(ChatActivity.this, "@消息长度不能超过" + MIN_TEXT);
                        editChat.getText().clear();
                        return;
                    }
                    if (isReplying && replayMsg != null) {
                        int atType = editChat.isAtAll() ? ChatEnum.EAtType.ALL : ChatEnum.EAtType.MULTIPLE;
                        ReplyMessage message = SocketData.createReplyMessage(replayMsg, SocketData.getUUID(), text, atType, editChat.getUserIdList());
                        sendMessage(message, ChatEnum.EMessageType.REPLY);
                        editChat.getText().clear();
                    } else {
                        if (editChat.isAtAll()) {
                            AtMessage message = SocketData.createAtMessage(SocketData.getUUID(), text, ChatEnum.EAtType.ALL, editChat.getUserIdList());
                            sendMessage(message, ChatEnum.EMessageType.AT);
                            editChat.getText().clear();

                        } else {
                            AtMessage message = SocketData.createAtMessage(SocketData.getUUID(), text, ChatEnum.EAtType.MULTIPLE, editChat.getUserIdList());
                            sendMessage(message, ChatEnum.EMessageType.AT);
                            editChat.getText().clear();
                        }
                    }
                } else {
                    //发送普通消息
                    if (!TextUtils.isEmpty(text)) {
                        int per = totalSize / MIN_TEXT;
                        if (per > 10) {
                            ToastUtil.show(ChatActivity.this, "文本长度不能超过" + 10 * MIN_TEXT);
                            editChat.getText().clear();
                            return;
                        }
                        if (isReplying && replayMsg != null) {
                            //回复消息不支持长文本
                            if (totalSize > MIN_TEXT) {
                                ToastUtil.show(ChatActivity.this, "回复消息长度不能超过" + MIN_TEXT);
                                editChat.getText().clear();
                                return;
                            }
                            int atType = ChatEnum.EAtType.DEFAULT;
                            ReplyMessage message = SocketData.createReplyMessage(replayMsg, SocketData.getUUID(), text, atType, editChat.getUserIdList());
                            sendMessage(message, ChatEnum.EMessageType.REPLY);
                            editChat.getText().clear();
                        } else {
                            if (totalSize <= MIN_TEXT) {//非长文本
                                isSendingHypertext = false;
                                ChatMessage message = SocketData.createChatMessage(SocketData.getUUID(), text);
                                sendMessage(message, ChatEnum.EMessageType.TEXT);
                                editChat.getText().clear();
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
                                editChat.getText().clear();
                            }
                        }
                    }
                }
                clearReply();
            }
        });
        editChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!mViewModel.isOpenValue()) //没有事件触发，设置改SoftInput模式为：顶起输入框
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                if (!mViewModel.isInputText.getValue())
                    mViewModel.isInputText.setValue(true);
                return false;
            }
        });
        editChat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });
        editChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btnSend.setVisibility(View.VISIBLE);
                } else {
                    btnSend.setVisibility(GONE);
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

                mtListView.scrollToEnd();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean orignilValue = mViewModel.isOpenFuction.getValue();
                if (orignilValue) {//已经打开了面板，再次点击->打开输入框
                    mViewModel.isInputText.setValue(true);
                } else {//未打开面板->打开功能面板
                    mViewModel.isOpenFuction.setValue(true);
                    if(checkCurrentImg()){
                        showPopupWindow(v);
                    }
                }
            }
        });
        btnEmj.setTag(0);
        btnEmj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean orignilValue = mViewModel.isOpenEmoj.getValue();
                if (orignilValue) {//已经打开了面板，再次点击->打开输入框
                    mViewModel.isInputText.setValue(true);
                } else {//未打开面板->打开功能面板
                    mViewModel.isOpenEmoj.setValue(true);
                }
            }
        });

        // 表情点击事件
        viewFaceView.setOnItemClickListener(new FaceViewPager.FaceClickListener() {

            @Override
            public void OnItemClick(FaceBean bean) {
                sendFace(bean);
                viewFaceView.addOftenUseFace(bean);
            }
        });
        // 删除表情按钮
        viewFaceView.setOnDeleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int selection = editChat.getSelectionStart();
                    String msg = editChat.getText().toString().trim();
                    if (selection >= 1) {
                        if (selection >= PatternUtil.FACE_EMOJI_LENGTH) {
                            String emoji = msg.substring(selection - PatternUtil.FACE_EMOJI_LENGTH, selection);
                            if (PatternUtil.isExpression(emoji)) {
                                editChat.getText().delete(selection - PatternUtil.FACE_EMOJI_LENGTH, selection);
                                return;
                            }
                        }
                        editChat.getText().delete(selection - 1, selection);
                    }
                } catch (Exception e) {
                    LogUtil.writeError(e);
                }
            }
        });

        //语音
        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toVoice();

            }
        });

        txtVoice.setOnTouchListener(audioTouchListener = new IAdioTouch(this, new IAdioTouch.MTouchListener() {
            @Override
            public void onDown() {
                txtVoice.setText("松开 结束");
                txtVoice.setBackgroundResource(R.drawable.bg_edt_chat2);

                btnVoice.setEnabled(false);
                btnEmj.setEnabled(false);
                btnFunc.setEnabled(false);

                MessageManager.getInstance().setCanStamp(false);
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

                MessageManager.getInstance().setCanStamp(true);
            }
        }));

        AudioRecordManager.getInstance(this).setAudioRecordListener(audioRecord = new IAudioRecord(this, headView, new IAudioRecord.UrlCallback() {
            @Override
            public void completeRecord(String file, int duration) {
                if (!checkNetConnectStatus(0)) {
                    return;
                }
                VoiceMessage voice = SocketData.createVoiceMessage(SocketData.getUUID(), file, duration);
                MsgAllBean msg = sendMessage(voice, ChatEnum.EMessageType.VOICE, false);
                // 不等于常信小助手，需要上传到服务器
                if (!Constants.CX_HELPER_UID.equals(toUId)) {
                    uploadVoice(file, msg);
                }
            }
        }));

        mAdapter = new MessageAdapter(this, this, isGroup(), mtListView);
        mAdapter.setCellFactory(new FactoryChatCell(this, mAdapter, this));
        mAdapter.setTagListener(this);
        mAdapter.setHasStableIds(true);
        mAdapter.setReadStatus(checkIsRead());
        mtListView.init(mAdapter);
        mtListView.setAnimation(null);

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
                            //恢复所有状态
                            mViewModel.recoveryOtherValue(null);
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
//                    LinearLayoutManager layoutManager = mtListView.getLayoutManager();
                    if (layoutManager != null) {
                        //获取可视的第一个view
                        lastPosition = layoutManager.findLastVisibleItemPosition();
//                        int first = layoutManager.findFirstCompletelyVisibleItemPosition();
//                        checkScrollFirst(first);
                        View topView = layoutManager.getChildAt(lastPosition);
                        if (topView != null) {
                            //获取与该view的底部的偏移量
                            lastOffset = topView.getBottom();
                        }
                        if (currentScrollPosition > 0) {
                            currentScrollPosition = -1;
                        }
                        LogUtil.getLog().i(TAG, "scroll--lastPosition=" + lastPosition + "--lastOff=" + lastOffset);
                        saveScrollPosition();
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (unreadCount > MIN_UNREAD_COUNT) {
                    int first = mtListView.getLayoutManager().findFirstCompletelyVisibleItemPosition();
                    checkScrollFirst(first);
                }
            }

        });

        //处理键盘
        SoftKeyBoardListener kbLinst = new SoftKeyBoardListener(this);
        kbLinst.setOnSoftKeyBoardChangeListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int h) {
                isSoftShow = true;
                int maxHeigh = getResources().getDimensionPixelSize(R.dimen.chat_fuction_panel_max_height);
                //每次保存软键盘的高度
                if (mKeyboardHeight != h && h <= maxHeigh) {
                    SharedPreferences sharedPreferences = getSharedPreferences(KEY_BOARD, Context.MODE_PRIVATE);
                    sharedPreferences.edit().putInt(KEY_BOARD, h).apply();
                    mKeyboardHeight = h;
                }
            }

            @Override
            public void keyBoardHide(int h) {
                isSoftShow = false;
            }
        });

        //6.15 先加载完成界面,后刷数据
        actionbar.post(new Runnable() {
            @Override
            public void run() {
                taskDraftGet();
            }
        });

        headView.getActionbar().getRightImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userAction.getMyInfo() == null) {
                    return;
                }
                if (isGroup()) {
                    if (!isAdmin() && !isAdministrators()) {
                        ToastUtil.show(context, "只有群主才能修改该选项");
                        return;
                    }
                }
                destroyTimeView = new DestroyTimeView(ChatActivity.this);
                destroyTimeView.initView();
                destroyTimeView.setPostion(survivaltime);
                destroyTimeView.setListener(new DestroyTimeView.OnClickItem() {
                    @Override
                    public void onClickItem(String content, int survivaltime) {
                        if (ChatActivity.this.survivaltime != survivaltime) {
                            util.setImageViewShow(survivaltime, headView.getActionbar().getRightImage());
                            if (isGroup()) {
                                changeSurvivalTime(toGid, survivaltime);
                            } else {
                                taskSurvivalTime(toUId, survivaltime);
                            }
                        }
                    }
                });
            }
        });

        viewNewMessage.setClickListener(() -> {
            if (mAdapter == null || mAdapter.getItemCount() <= 0) {
                return;
            }
            int position = mAdapter.getItemCount() - unreadCount;
            if (position >= 0) {
                scrollChatToPosition(position);
            } else {
                scrollChatToPosition(0);
            }
            viewNewMessage.setVisible(false);
            unreadCount = 0;
        });
        initExtendFunctionView();

        //批量删除
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter != null && mAdapter.getSelectedMsg() != null) {
                    List<MsgAllBean> msgList = mAdapter.getSelectedMsg();
                    int len = msgList.size();
                    if (len > 0) {
                        mAdapter.removeMsgList(msgList);
                        msgDao.deleteMsgList(msgList);
                        notifyData();
                    }
                }
                ivDelete.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showViewMore(false);
                        mAdapter.showCheckBox(false, true);
                    }
                }, 100);


            }
        });
        //批量转发
        ivForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                if (mAdapter == null || mAdapter.getSelectedMsg() == null) {
                    return;
                }
                showForwardDialog();
            }
        });

        viewReplyMessage.setClickListener(new OnControllerClickListener() {
            @Override
            public void onClick() {
                //取消回复
                mViewModel.isReplying.setValue(false);
                clearReply();
                isReplying = false;
                replayMsg = null;
            }
        });
    }

    private void initIntent() {
        toGid = getIntent().getStringExtra(AGM_TOGID);
        toUId = getIntent().getLongExtra(AGM_TOUID, 0);
        searchTime = getIntent().getLongExtra(SEARCH_TIME, 0);
        if (searchTime > 0) {
            isLoadHistory = true;
        }
        onlineState = getIntent().getBooleanExtra(ONLINE_STATE, true);
        mViewModel.init(toGid, toUId);
        mViewModel.loadData(groupInfoChangeListener, userInfoChangeListener);
    }

    //清除回复状态
    private void clearReply() {
        if (isReplying && replayMsg != null) {
            updateReplying();
            isReplying = false;
            replayMsg = null;
            mViewModel.isReplying.setValue(false);
        }
    }

    //设置键盘高度
    private void setPanelHeight(int h, View view) {
//        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) view.getLayoutParams(); //取控
//        if (linearParams.height != h) {
//            int minHeight = getResources().getDimensionPixelSize(R.dimen.chat_fuction_panel_height);
//            linearParams.height = Math.max(h, minHeight);
//            view.setLayoutParams(linearParams);
//        }
    }

    private void checkScrollFirst(int first) {
        if (unreadCount > 0 && mAdapter != null && mAdapter.getItemCount() > 0) {
            int size = mAdapter.getItemCount();
//            LogUtil.getLog().d("a=", TAG + "checkScrollFirst：size = " + size + "--unreadCount=" + unreadCount + "--firstPosition=" + first);
            if (first <= size - unreadCount + 1) {
                mtListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewNewMessage.setVisible(false);
                    }
                }, 50);
            }
        }
    }

    private void toLocation() {
        LocationActivity.openActivity(ChatActivity.this, false, null);
    }

    private void toVideoCall() {
        //重置所有状态值
        mViewModel.recoveryOtherValue(null);
        DialogHelper.getInstance().createSelectDialog(ChatActivity.this, new ICustomerItemClick() {
            @Override
            public void onClickItemVideo() {// 视频
                gotoVideoActivity(AVChatType.VIDEO.getValue());
            }

            @Override
            public void onClickItemVoice() {// 语音
                gotoVideoActivity(AVChatType.AUDIO.getValue());
            }

            @Override
            public void onClickItemCancle() {

            }
        });
    }

    private void toGroupRobot() {
        if (mViewModel.groupInfo == null)
            return;
        startActivity(new Intent(getContext(), GroupRobotActivity.class)
                .putExtra(GroupRobotActivity.AGM_GID, toGid)
                .putExtra(GroupRobotActivity.AGM_RID, mViewModel.groupInfo.getRobotid())
        );
    }

    private void toVoice() {
        //申请权限 7.2
        permission2Util.requestPermissions(ChatActivity.this, new CheckPermission2Util.Event() {
            @Override
            public void onSuccess() {
                if (!checkNetConnectStatus(0)) {
                    return;
                }
                startVoice(null);
            }

            @Override
            public void onFail() {

            }
        }, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    private void toCard() {
        startActivityForResult(new Intent(getContext(), SelectUserActivity.class), SelectUserActivity.RET_CODE_SELECTUSR);
    }

    private void toStamp() {
        AlertTouch alertTouch = new AlertTouch();
        alertTouch.init(ChatActivity.this, "请输入戳一下消息", "确定", R.mipmap.ic_chat_actionme, new AlertTouch.Event() {
            @Override
            public void onON() {

            }

            @Override
            public void onYes(String content) {
                if (!TextUtils.isEmpty(content)) {
                    //发送戳一戳消息
//                            MsgAllBean msgAllbean = SocketData.send4action(toUId, toGid, content);
//                            showSendObj(msgAllbean);
//                            MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllbean);
                    StampMessage message = SocketData.createStampMessage(SocketData.getUUID(), content);
                    sendMessage(message, ChatEnum.EMessageType.STAMP);
                } else {
                    ToastUtil.show(getContext(), "戳一下内容不能为空");
                }
            }
        });
        alertTouch.setContent("快来聊天");
        alertTouch.show();
        alertTouch.setEdHintOrSize(null, 20);
    }

    private void toTransfer() {
        UserBean user = PayEnvironment.getInstance().getUser();
        if (user != null) {
            if (user.getRealNameStat() != 1) {//未认证
                showIdentifyDialog();
                return;
            } /*else if (user.getPhoneBindStat() != 1) {//未绑定手机
                showBindPhoneDialog();
                return;
            } else if (user.getPayPwdStat() != 1) {//未设置支付密码
                showSettingPswDialog();
                return;
            }*/
        }
        String name = "";
        String avatar = "";
        if (mViewModel.userInfo != null) {
            name = mViewModel.userInfo.getName();
            avatar = mViewModel.userInfo.getHead();
        }
        Intent intent = TransferActivity.newIntent(ChatActivity.this, toUId, name, avatar);
        startActivity(intent);
    }

    private void toSystemEnvelope() {

        UserBean user = PayEnvironment.getInstance().getUser();
        if (user != null) {
            if (user.getRealNameStat() != 1) {//未认证
                showIdentifyDialog();
                return;
            }
        }
        if (isGroup()) {
            Intent intentMulti = MultiRedPacketActivity.newIntent(ChatActivity.this, toGid, mViewModel.groupInfo.getUsers().size());
            startActivityForResult(intentMulti, REQUEST_RED_ENVELOPE);
        } else {
            Intent intentMulti = SingleRedPacketActivity.newIntent(ChatActivity.this, toUId);
            startActivityForResult(intentMulti, REQUEST_RED_ENVELOPE);
        }
    }

    private void toGallery() {
        PictureSelector.create(ChatActivity.this)
//                        .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(false)// 是否可预览图片 true or false
                .isCamera(false)// 是否显示拍照按钮 ture or false
                .maxVideoSelectNum(1)
                .compress(true)// 是否压缩 true or false
                .isGif(true)
                .selectArtworkMaster(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    private void toCamera() {
        permission2Util.requestPermissions(ChatActivity.this, new CheckPermission2Util.Event() {
            @Override
            public void onSuccess() {
                // 判断是否正在音视频通话
                if (AVChatProfile.getInstance().isCallIng() || AVChatProfile.getInstance().isCallEstablished()) {
                    if (AVChatProfile.getInstance().isChatType() == AVChatType.VIDEO.getValue()) {
                        ToastUtil.show(ChatActivity.this, getString(R.string.avchat_peer_busy_video));
                    } else {
                        ToastUtil.show(ChatActivity.this, getString(R.string.avchat_peer_busy_voice));
                    }
                } else {
                    if (!checkNetConnectStatus(0)) {
                        return;
                    }
                    if (ViewUtils.isFastDoubleClick()) {
                        return;
                    }
//                    Intent intent = new Intent(ChatActivity.this, RecordedActivity.class);
//                    startActivityForResult(intent, VIDEO_RP);
                    Intent intent = new Intent(ChatActivity.this, CameraActivity.class);
                    startActivityForResult(intent, VIDEO_RP);
                }
            }

            @Override
            public void onFail() {

            }
        }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
    }

    //初始化拓展功能栏
    private void initExtendFunctionView() {
        viewExtendFunction.setListener(new ChatExtendMenuView.OnFunctionListener() {
            @Override
            public void onClick(int id) {
                switch (id) {
                    case ChatEnum.EFunctionId.GALLERY:
                        toGallery();
                        break;
                    case ChatEnum.EFunctionId.TAKE_PHOTO:
                        toCamera();
                        break;
                    case ChatEnum.EFunctionId.ENVELOPE_SYS:
                        toSystemEnvelope();
                        break;
                    case ChatEnum.EFunctionId.TRANSFER:
                        toTransfer();
                        break;
                    case ChatEnum.EFunctionId.VIDEO_CALL:
                        toVideoCall();
                        break;
                    case ChatEnum.EFunctionId.ENVELOPE_MF:
                        taskPayRb();
                        break;
                    case ChatEnum.EFunctionId.LOCATION:
                        toLocation();
                        break;
                    case ChatEnum.EFunctionId.STAMP:
                        toStamp();
                        break;
                    case ChatEnum.EFunctionId.CARD:
                        toCard();
                        break;
                    case ChatEnum.EFunctionId.GROUP_ASSISTANT:
                        toGroupRobot();
                        break;
                    case ChatEnum.EFunctionId.FILE:
                        toSelectFile();
                        break;
                    case ChatEnum.EFunctionId.COLLECT:
                        if (ViewUtils.isFastDoubleClick()) {
                            return;
                        }
                        //区分是单聊还是群聊，把转发需要的参数携带过去
                        Intent intent = new Intent(ChatActivity.this, CollectionActivity.class);
                        intent.putExtra("from", CollectionActivity.FROM_CHAT);
                        if (isGroup()) {
                            if (mViewModel.groupInfo == null) return;
                            intent.putExtra("is_group", true);
                            intent.putExtra("group_head", mViewModel.groupInfo.getAvatar());
                            intent.putExtra("group_id", mViewModel.groupInfo.getGid());
                            intent.putExtra("group_name", msgDao.getGroupName(mViewModel.groupInfo.getGid()));
                        } else {
                            if (mViewModel.userInfo == null) return;
                            intent.putExtra("is_group", false);
                            intent.putExtra("user_head", mViewModel.userInfo.getHead());
                            intent.putExtra("user_id", mViewModel.userInfo.getUid());
                            intent.putExtra("user_name", mViewModel.userInfo.getName4Show());
                        }
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    public List<FunctionItemModel> getItemModels() {
        boolean isGroup = isGroup();
        boolean isVip = false;
        boolean isSystemUser = false;
        if (!isGroup) {
            IUser userInfo = UserAction.getMyInfo();
            if (userInfo != null && IS_VIP.equals(userInfo.getVip())) {
                isVip = true;
            }
            if (UserUtil.isSystemUser(toUId)) {
                isSystemUser = true;
            }
        }
        List<FunctionItemModel> list = new ArrayList<>();
        list.add(createItemMode("相册", R.mipmap.ic_chat_pic, ChatEnum.EFunctionId.GALLERY));
        list.add(createItemMode("拍摄", R.mipmap.ic_chat_pt, ChatEnum.EFunctionId.TAKE_PHOTO));
        if (!isSystemUser) {
            list.add(createItemMode("零钱红包", R.mipmap.ic_chat_rb, ChatEnum.EFunctionId.ENVELOPE_SYS));
        }
        if (!isGroup && !isSystemUser) {
            list.add(createItemMode("零钱转账", R.mipmap.ic_chat_transfer, ChatEnum.EFunctionId.TRANSFER));
        }
        if (!isGroup && isVip) {
            list.add(createItemMode("视频通话", R.mipmap.ic_chat_video, ChatEnum.EFunctionId.VIDEO_CALL));
        }
        if (!isSystemUser) {
            list.add(createItemMode("云红包", R.mipmap.ic_chat_rb_zfb, ChatEnum.EFunctionId.ENVELOPE_MF));
        }
        list.add(createItemMode("位置", R.mipmap.location_six, ChatEnum.EFunctionId.LOCATION));
        list.add(createItemMode("收藏", R.mipmap.ic_chat_collect, ChatEnum.EFunctionId.COLLECT));
        if (!isGroup && !isSystemUser) {
            list.add(createItemMode("戳一下", R.mipmap.ic_chat_action, ChatEnum.EFunctionId.STAMP));
        }
        if (isGroup) {
            //本人群主
            if (UserAction.getMyId() != null && mViewModel.groupInfo != null && mViewModel.groupInfo.getMaster().equals(UserAction.getMyId().toString())) {
                list.add(createItemMode("群助手", R.mipmap.ic_chat_robot, ChatEnum.EFunctionId.GROUP_ASSISTANT));
            }
        }
        list.add(createItemMode("文件", R.mipmap.ic_chat_file, ChatEnum.EFunctionId.FILE));
        if (!isSystemUser) {
            list.add(createItemMode("名片", R.mipmap.ic_chat_newfrd, ChatEnum.EFunctionId.CARD));
        }
        return list;
    }

    public FunctionItemModel createItemMode(String name, int drawableId, @ChatEnum.EFunctionId int functionId) {
        FunctionItemModel model = new FunctionItemModel();
        model.setName(name);
        model.setDrawableId(drawableId);
        model.setId(functionId);
        return model;
    }

    private void scrollChatToPosition(int position) {
        LogUtil.getLog().i(TAG, "scrollChatToPosition--" + position);
        mtListView.getLayoutManager().scrollToPosition(position);
        currentScrollPosition = position;
//        initLastPosition();
        View topView = mtListView.getLayoutManager().getChildAt(currentScrollPosition);
        if (topView != null) {
            //获取与该view的底部的偏移量
            lastOffset = topView.getBottom();
        }
    }

    private void scrollChatToPositionWithOffset(int position, int offset) {
        LogUtil.getLog().i(TAG, "scrollChatToPositionWithOffset--" + position + "--offset=" + offset);
        ((LinearLayoutManager) mtListView.getListView().getLayoutManager()).scrollToPositionWithOffset(position, offset);
        currentScrollPosition = position;
//        initLastPosition();
        View topView = mtListView.getLayoutManager().getChildAt(currentScrollPosition);
        if (topView != null) {
            //获取与该view的底部的偏移量
            lastOffset = topView.getBottom();
        }
    }

    //消息发送撤销消息
    private void sendMessage(IMsgContent message, @ChatEnum.EMessageType int msgType, int position) {
        MsgAllBean msgAllBean = SocketData.createMessageBean(toUId, toGid, msgType, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), message);
        if (msgAllBean != null) {
            SocketData.sendAndSaveMessage(msgAllBean);
        }
        mtListView.getListView().getAdapter().notifyDataSetChanged();
    }

    //消息发送
    private void sendMessageTest(IMsgContent message, @ChatEnum.EMessageType int msgType) {
        LogUtil.getLog().i("MessageManager", "发送msgId=" + message.getMsgId());
        MsgAllBean msgAllBean = SocketData.createMessageBean(toUId, toGid, msgType, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), message);
        if (msgAllBean != null) {
            if (!filterMessage(message)) {
                SocketData.sendAndSaveMessage(msgAllBean, false);
            } else {
                SocketData.sendAndSaveMessage(msgAllBean);
            }
            //cancel消息发送前不需要更新
            mAdapter.addMessage(msgAllBean);
        }
    }

    //消息发送
    private void sendMessage(IMsgContent message, @ChatEnum.EMessageType int msgType) {
        MsgAllBean msgAllBean = SocketData.createMessageBean(toUId, toGid, msgType, ChatEnum.ESendStatus.PRE_SEND, SocketData.getFixTime(), message);
        if (msgAllBean != null) {
            if (!filterMessage(message)) {
                SocketData.sendAndSaveMessage(msgAllBean, false);
            } else {
                SocketData.sendAndSaveMessage(msgAllBean);
            }
            //cancel消息发送前不需要更新,read消息不需要add
            if (msgType != ChatEnum.EMessageType.MSG_CANCEL && msgType != ChatEnum.EMessageType.READ) {
                showSendObj(msgAllBean);
            }
        }
    }

    //消息发送，canSend--是否需要发送，图片，视频，语音，文件等
    private MsgAllBean sendMessage(IMsgContent message, @ChatEnum.EMessageType int msgType, boolean canSend) {
        int sendStatus = ChatEnum.ESendStatus.NORMAL;
        if (TextUtils.isEmpty(toGid) && toUId != null && Constants.CX_HELPER_UID.equals(toUId)) {//常信小助手
            sendStatus = ChatEnum.ESendStatus.NORMAL;
        } else {
            if (isUploadType(msgType)) {
                sendStatus = ChatEnum.ESendStatus.PRE_SEND;
            }
        }
        MsgAllBean msgAllBean = SocketData.createMessageBean(toUId, toGid, msgType, sendStatus, SocketData.getFixTime(), message);
        if (msgAllBean != null && canSend) {
            if (filterMessage(message) || isUploadType(msgAllBean.getMsg_type())) {
                msgAllBean.setIsLocal(0);
            } else {
                msgAllBean.setIsLocal(1);
            }
            SocketData.sendAndSaveMessage(msgAllBean, canSend);
            showSendObj(msgAllBean);
        } else {
            SocketData.saveMessage(msgAllBean);
            showSendObj(msgAllBean);
        }
        return msgAllBean;
    }

    //消息发送，canSend--是否需要发送，图片，视频，语音，文件等
    private MsgAllBean sendMessageFromResend(IMsgContent message, @ChatEnum.EMessageType int msgType, boolean canSend) {
        int sendStatus = ChatEnum.ESendStatus.NORMAL;
        if (TextUtils.isEmpty(toGid) && toUId != null && Constants.CX_HELPER_UID.equals(toUId)) {//常信小助手
            sendStatus = ChatEnum.ESendStatus.NORMAL;
        } else {
            if (isUploadType(msgType)) {
                sendStatus = ChatEnum.ESendStatus.PRE_SEND;
            }
        }
        MsgAllBean msgAllBean = SocketData.createMessageBean(toUId, toGid, msgType, sendStatus, SocketData.getFixTime(), message);
        if (msgAllBean != null && canSend) {
            SocketData.sendAndSaveMessage(msgAllBean, canSend);
            replaceListDataAndNotify(msgAllBean);
        } else {
            SocketData.saveMessage(msgAllBean);
            replaceListDataAndNotify(msgAllBean);
        }
        return msgAllBean;
    }

    //是否是需要上传的消息类型：图片，语音，视频，文件等
    private boolean isUploadType(int msgType) {
        if (msgType == ChatEnum.EMessageType.IMAGE || msgType == ChatEnum.EMessageType.VOICE || msgType == ChatEnum.EMessageType.MSG_VIDEO || msgType == ChatEnum.EMessageType.FILE) {
            return true;
        }
        return false;

    }

    private boolean filterMessage(IMsgContent message) {
        boolean isSend = true;
        //常信小助手不需要发送到后台(文件传输助手除了文件以外暂时也不需要传到后台)
        if (Constants.CX_HELPER_UID.equals(toUId) || Constants.CX_BALANCE_UID.equals(toUId)) {
            isSend = false;
        }
        return isSend;
    }


    private void initSurvivaltimeState() {
        if (isGroup()) {
            if (mViewModel.groupInfo != null) survivaltime = mViewModel.groupInfo.getSurvivaltime();
        } else {
            if (mViewModel.userInfo != null) survivaltime = mViewModel.userInfo.getDestroy();
        }
        util.setImageViewShow(survivaltime, headView.getActionbar().getRightImage());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 检查是否能领取红包
     *
     * @return
     */
    private boolean checkCanOpenUpRedEnv() {
        boolean check = true;
        if (mViewModel.groupInfo != null && mViewModel.groupInfo.getCantOpenUpRedEnv() == 1) {
            check = false;
        }
        return check;
    }

    private boolean isAdmin() {
        if (mViewModel.groupInfo == null || !StringUtil.isNotNull(mViewModel.groupInfo.getMaster()))
            return false;
        return mViewModel.groupInfo.getMaster().equals("" + UserAction.getMyId());
    }

    /**
     * 判断是否是管理员
     *
     * @return
     */
    private boolean isAdministrators() {
        boolean isManager = false;
        if (mViewModel.groupInfo != null && mViewModel.groupInfo.getViceAdmins() != null && mViewModel.groupInfo.getViceAdmins().size() > 0) {
            for (Long user : mViewModel.groupInfo.getViceAdmins()) {
                if (user.equals(UserAction.getMyId())) {
                    isManager = true;
                    break;
                }
            }
        }
        return isManager;
    }

    /**
     * 进入音视频通话
     *
     * @param aVChatType
     */
    private void gotoVideoActivity(int aVChatType) {
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
                            if (!TextUtils.isEmpty(userInfo.getMkName())) {
                                bundle.putString(Preferences.USER_NAME, userInfo.getMkName());
                            } else {
                                bundle.putString(Preferences.USER_NAME, userInfo.getName());
                            }
                            bundle.putString(Preferences.NETEASEACC_ID, userInfo.getNeteaseAccid());
                            bundle.putInt(Preferences.VOICE_TYPE, CoreEnum.VoiceType.WAIT);
                            bundle.putInt(Preferences.AVCHA_TTYPE, aVChatType);
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
                LogUtil.getLog().e(ChatActivity.class.getSimpleName(), "上传语音成功--" + url);
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
                        if (i % 10 == 0) {
                            ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), "连续测试发送" + i + "-------");
                            sendMessageTest(chatMessage, ChatEnum.EMessageType.TEXT);
                        } else {
                            ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), "连续测试发送" + i);
                            sendMessageTest(chatMessage, ChatEnum.EMessageType.TEXT);
                        }

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

    //图片测试逻辑
    private void taskTestImage(int count) {
//        ToastUtil.show(getContext(), "内部指令，请重新输入");
//        editChat.setText("");
//        return;

        String file = "/storage/emulated/0/changXin/zgd123.jpg";
        File f = new File(file);
        if (!f.exists()) {
            ToastUtil.show(getContext(), "图片不存在，请在changXin文件夹下构建 zgd123.jpg 图片");
            return;
        }
        ToastUtil.show(getContext(), "连续发送" + count + "图片测试开始");
        try {
            for (int i = 1; i <= count; i++) {
                MsgAllBean imgMsgBean = null;
                if (StringUtil.isNotNull(file)) {
                    final boolean isArtworkMaster = false;
                    final String imgMsgId = SocketData.getUUID();
                    // 记录本次上传图片的ID跟本地路径
                    //:使用file:
                    // 路径会使得检测本地路径不存在
                    ImageMessage imageMessage = SocketData.createImageMessage(imgMsgId, /*"file://" +*/ file, isArtworkMaster);
                    imgMsgBean = sendMessage(imageMessage, ChatEnum.EMessageType.IMAGE, false);
                    // 不等于常信小助手
                    if (!Constants.CX_HELPER_UID.equals(toUId)) {
                        UpLoadService.onAddImage(imgMsgBean, file, isArtworkMaster);
                        startService(new Intent(getContext(), UpLoadService.class));
                    }
                }

                notifyData2Bottom(true);

                if (i % 10 == 0) {
                    Thread.sleep(2 * 1000);
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
            if (mAdapter != null) {
                config.setTotalSize(mAdapter.getItemCount());
            }
            sp.save2Json(config, "scroll_config");
        }

    }

    private void clearScrollPosition() {
        SharedPreferencesUtil sp = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.SCROLL);
        sp.clear();
    }

    /***
     * 开始语音
     */
    private void startVoice(Boolean open) {
        if (open == null) {
            open = txtVoice.getVisibility() == View.GONE ? true : false;
        }
        if (open) {
            mViewModel.isOpenSpeak.setValue(true);
        } else {
            showVoice(false);
            //设置改SoftInput模式为：顶起输入框
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            //弹起输入框
            mViewModel.isInputText.setValue(true);
        }
    }

    private void showVoice(boolean show) {
        if (show) {//开启语音
            txtVoice.setVisibility(View.VISIBLE);
            btnVoice.setImageDrawable(getResources().getDrawable(R.mipmap.ic_chat_kb));
            editChat.setVisibility(View.INVISIBLE);
            btnSend.setVisibility(GONE);
            btnFunc.setVisibility(VISIBLE);
        } else {//关闭语音
            txtVoice.setVisibility(View.GONE);
            btnVoice.setImageDrawable(getResources().getDrawable(R.mipmap.ic_chat_vio));
            editChat.setVisibility(View.VISIBLE);
            if (StringUtil.isNotNull(editChat.getText().toString())) {
                btnSend.setVisibility(VISIBLE);
            } else {
                btnSend.setVisibility(GONE);
            }
        }
    }

    /*
     * @param isMustBottom 是否必须滑动到底部
     * */
    private void scrollListView(boolean isMustBottom) {
        if (isLoadHistory) {
            isLoadHistory = false;
        }
        if (mAdapter != null) {
            int length = mAdapter.getItemCount();//刷新后当前size
            LogUtil.getLog().i(TAG, "scrollListView--" + "lastPosition=" + lastPosition + "--totalLength--" + length);
            if (isMustBottom || lastPosition == -1) {
                mtListView.scrollToEnd();
                lastPosition = length - 1;
//                initLastPosition();
            } else {
                if (lastPosition >= 0 && lastPosition < length) {
                    if (isSoftShow || lastPosition == length - 1 || isCanScrollBottom()) {//允许滑动到底部，或者当前处于底部，canScrollVertically是否能向上 false表示到了底部
                        scrollChatToPosition(length);
                    } else {
//                        scrollChatToPosition(lastPosition);
                        if (lastOffset == -1) {
                            scrollChatToPosition(lastPosition);
                        } else {
                            scrollChatToPositionWithOffset(lastPosition, lastOffset);
                        }
                    }
                } else {
                    SharedPreferencesUtil sp = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.SCROLL);
                    if (sp != null) {
                        ScrollConfig config = sp.get4Json(ScrollConfig.class, "scroll_config");
                        if (config != null) {
                            if (config.getUserId() == UserAction.getMyId()) {
                                if (toUId != null && config.getUid() > 0 && config.getUid() == toUId.intValue()) {
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
                            scrollChatToPosition(length);
                        } else {
//                            scrollChatToPosition(lastPosition);
                            if (lastOffset < 0) {
                                scrollChatToPosition(lastPosition);
                            } else {
                                scrollChatToPositionWithOffset(lastPosition, lastOffset);
                            }
                        }
                    } else {
                        if (currentScrollPosition > 0) {
                            scrollChatToPosition(currentScrollPosition);
                        } else {
                            scrollChatToPosition(length);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        //清理会话数量
//        taskCleanRead(false);//不一定执行
        LogUtil.getLog().e(TAG, "onBackPressed");
        if (mAdapter != null && mAdapter.isShowCheckBox()) {
            mAdapter.showCheckBox(false, true);
            showViewMore(false);
            return;
        }
        clearScrollPosition();
        super.onBackPressed();
        if (mViewModel.isOpenFuction.getValue()) {
            mViewModel.isOpenFuction.setValue(false);
            return;
        }
        if (mViewModel.isOpenEmoj.getValue()) {
            mViewModel.isOpenEmoj.setValue(false);
            return;
        }
        //oppo 手机 调用 onBackPressed不会finish
        finish();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventExitChat event) {
        if (!TextUtils.isEmpty(event.getGid()) && !TextUtils.isEmpty(toGid) && event.getGid().equals(toGid)) {//PC端操作,退群
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (TextUtils.isEmpty(toGid) && event.getUid() != null && event.getUid().longValue() == toUId.longValue()) {//PC端操作,删除好友
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            onBackPressed();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventUserOnlineChange event) {
        if (toUId != null && !isGroup()) {
            if (event.getObject() instanceof UserInfo) {
                UserInfo info = (UserInfo) event.getObject();
                if (info != null && info.getUid().intValue() == toUId.intValue()) {
                    updateUserOnlineStatus(info);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventGroupStatusChange(GroupStatusChangeEvent event) {
        if (isGroup()) {
            if (event.getData() instanceof Group) {
                Group group = (Group) event.getData();
                if (group != null && group.getGid().equals(toGid)) {
                    boolean forbid = group.getStat() == ChatEnum.EGroupStatus.BANED;
                    setBanView(false, forbid);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventCheckVoice(EventVoicePlay event) {
        checkMoreVoice(event.getPosition(), (MsgAllBean) event.getBean());
    }

    //转账成功。发送IM消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventTransferSuccess(TransferSuccessEvent event) {
        CxTransferBean transferBean = event.getBean();
        if (transferBean != null) {
            if (transferBean.getOpType() == PayEnum.ETransferOpType.TRANS_RECEIVE || transferBean.getOpType() == PayEnum.ETransferOpType.TRANS_REJECT
                    || transferBean.getOpType() == PayEnum.ETransferOpType.TRANS_PAST) {
                if (!TextUtils.isEmpty(transferBean.getMsgJson())) {
                    MsgAllBean msg = GsonUtils.getObject(transferBean.getMsgJson(), MsgAllBean.class);
                    TransferMessage preTransfer = msg.getTransfer();
                    preTransfer.setOpType(transferBean.getOpType());
                    replaceListDataAndNotify(msg);
                }
                msgDao.updateTransferStatus(transferBean.getTradeId() + "", transferBean.getOpType());
            }
            TransferMessage message = SocketData.createTransferMessage(SocketData.getUUID(), transferBean.getTradeId(), transferBean.getAmount(), transferBean.getInfo(), transferBean.getSign(), transferBean.getOpType());
            sendMessage(message, ChatEnum.EMessageType.TRANSFER);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventNoticeReceive(NoticeReceiveEvent event) {
        if (!TextUtils.isEmpty(event.getTradeId())) {
            TransferNoticeMessage transferNoticeMessage = SocketData.createTransferNoticeMessage(SocketData.getUUID(), event.getTradeId());
            sendMessage(transferNoticeMessage, ChatEnum.EMessageType.TRANSFER_NOTICE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void faceUpdateEvent(EventFactory.FaceUpdateEvent event) {
        viewFaceView.getFaceData(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventCheckVoice(ActivityForwordEvent event) {
        PictureSelector.create(ChatActivity.this)
                .openCamera(PictureMimeType.ofImage())
                .compress(true)
                .forResult(PictureConfig.REQUEST_CAMERA);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventIsShowRead(EventIsShowRead event) {
        //自己的更新不需要管
        if (toUId == null || event.getUid() != toUId.longValue()) {
            return;
        }
        int switchType = event.getSwitchType();
        int result = event.getResult();
        if (mViewModel.userInfo == null) {
            return;
        }
        if (switchType == EventIsShowRead.EReadSwitchType.SWITCH_FRIEND) {
            mViewModel.userInfo.setFriendRead(result);
        } else if (switchType == EventIsShowRead.EReadSwitchType.SWITCH_MASTER) {
            mViewModel.userInfo.setMasterRead(result);
        }
        mAdapter.setReadStatus(checkIsRead());
        notifyData();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setingReadDestroy(ReadDestroyBean bean) {
        if (TextUtils.isEmpty(bean.gid)) {
            if (bean.uid == toUId.longValue()) {
                survivaltime = bean.survivaltime;
                util.setImageViewShow(survivaltime, headView.getActionbar().getRightImage());
            }
        } else {
            if (bean.gid.equals(toGid)) {
                survivaltime = bean.survivaltime;
                util.setImageViewShow(survivaltime, headView.getActionbar().getRightImage());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventSnapshot(EventSwitchSnapshot event) {
        if (isGroup()) {
            if (toGid != null && toGid.equals(event.getGid())) {
                if (mViewModel.groupInfo != null) {
                    mViewModel.groupInfo.setScreenshotNotification(event.getFlag());
                }
                if (event.getFlag() == 1) {
                    isScreenShotListen = true;
                    initScreenShotListener();
                } else {
                    stopScreenShotListener();
                }
            }
        } else {
            if (mViewModel.toUId == event.getUid()) {
                if (mViewModel.userInfo != null) {
                    mViewModel.userInfo.setScreenshotNotification(event.getFlag());
                }
                if (event.getFlag() == 1) {
                    isScreenShotListen = true;
                    initScreenShotListener();
                } else {
                    stopScreenShotListener();
                }
            }
        }
    }

    /**
     * 保存经常使用表情
     */
    private void saveOftenUseFace() {
        viewFaceView.saveOftenUseFace();
    }

    /**
     * 获取经常使用表情列表
     */
    private void getOftenUseFace() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                viewFaceView.getOftenUseFace();
            }
        }).start();
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


    private String getVideoAttBitmap(String mUri) {
        String path = "";
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                FileInputStream inputStream = new FileInputStream(new File(mUri).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());
            } else {
            }
            File file = GroupHeadImageUtil.save2File(mmr.getFrameAtTime());
            if (file != null) {
                path = file.getAbsolutePath();
            }
        } catch (Exception ex) {
            LogUtil.getLog().e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case VIDEO_RP:
                    int dataType = data.getIntExtra(CameraActivity.INTENT_DATA_TYPE, CameraActivity.RESULT_TYPE_VIDEO);
                    MsgAllBean videoMsgBean = null;
                    if (dataType == RecordedActivity.RESULT_TYPE_VIDEO) {
//                        if (!checkNetConnectStatus()) {
                        String file = data.getStringExtra(CameraActivity.INTENT_PATH);
                        LogUtil.getLog().i(TAG, "--视频Chat--" + file);
                        int height = data.getIntExtra(CameraActivity.INTENT_PATH_HEIGHT, 0);
                        int width = data.getIntExtra(CameraActivity.INTENT_VIDEO_WIDTH, 0);
                        long time = data.getLongExtra(CameraActivity.INTENT_PATH_TIME, 0L);
//                        String videoBg = data.getStringExtra(CameraActivity.INTENT_PATH_BG);
                        //app内拍摄的视频经检查已经实现了自动压缩
                        VideoMessage videoMessage = SocketData.createVideoMessage(SocketData.getUUID(), "file://" + file, getVideoAttBitmap(file), false, time, width, height, file);
                        videoMsgBean = sendMessage(videoMessage, ChatEnum.EMessageType.MSG_VIDEO, false);
                        // 不等于常信小助手，需要上传到服务器
                        if (!Constants.CX_HELPER_UID.equals(toUId)) {
                            UpLoadService.onAddVideo(this.context, videoMsgBean, false);
                            startService(new Intent(getContext(), UpLoadService.class));
                        }
                    } else if (dataType == CameraActivity.RESULT_TYPE_PHOTO) {
                        if (!checkNetConnectStatus(0)) {
                            return;
                        }
                        String photoPath = data.getStringExtra(CameraActivity.INTENT_PATH);
                        String file = photoPath;

                        final boolean isArtworkMaster = requestCode == PictureConfig.REQUEST_CAMERA ? true : data.getBooleanExtra(PictureConfig.IS_ARTWORK_MASTER, false);
                        boolean isGif = FileUtils.isGif(file);
                        if (isArtworkMaster || isGif) {
                            file = photoPath;
                        }
                        //1.上传图片
                        final String imgMsgId = SocketData.getUUID();
                        if (TextUtils.isEmpty(file)) {
                            ToastUtil.show("图片异常,请重新选择");
                            return;
                        }
                        ImageMessage imageMessage = SocketData.createImageMessage(imgMsgId, /*"file://" + */file, isArtworkMaster);
                        videoMsgBean = sendMessage(imageMessage, ChatEnum.EMessageType.IMAGE, false);
                        // 不等于常信小助手，需要上传到服务器
                        if (!Constants.CX_HELPER_UID.equals(toUId)) {
                            UpLoadService.onAddImage(videoMsgBean, file, isArtworkMaster);
                            startService(new Intent(getContext(), UpLoadService.class));
                        }
                    }
                    break;
                case PictureConfig.REQUEST_CAMERA:
                case PictureConfig.CHOOSE_REQUEST:
                case PictureConfig.PREVIEW_FROM_CHAT:
                    if (!checkNetConnectStatus(0)) {
                        return;
                    }
                    // 图片选择结果回调
                    List<LocalMedia> obt = PictureSelector.obtainMultipleResult(data);
                    if (obt != null && obt.size() > 0) {
                        LogUtil.getLog().e("=图片选择结果回调===" + GsonUtils.optObject(obt.get(0)));
                    }
                    MsgAllBean imgMsgBean = null;
                    for (LocalMedia localMedia : obt) {
                        String file = localMedia.getCompressPath();
                        if (StringUtil.isNotNull(file)) {
                            final boolean isArtworkMaster = requestCode == PictureConfig.REQUEST_CAMERA ? true : data.getBooleanExtra(PictureConfig.IS_ARTWORK_MASTER, false);
                            boolean isGif = FileUtils.isGif(file);
                            if (isArtworkMaster || isGif) {
                                file = localMedia.getPath();
                            }
                            //1.上传图片
                            final String imgMsgId = SocketData.getUUID();
                            ImageMessage imageMessage = SocketData.createImageMessage(imgMsgId, /*"file://" +*/ file, isArtworkMaster);//TODO:使用file://路径会使得检测本地路径不存在
                            imgMsgBean = sendMessage(imageMessage, ChatEnum.EMessageType.IMAGE, false);
//                            imgMsgBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, SocketData.getFixTime(), imageMessage, ChatEnum.EMessageType.IMAGE);
                            // 不等于常信小助手，需要上传到服务器
                            if (!Constants.CX_HELPER_UID.equals(toUId)) {
                                UpLoadService.onAddImage(imgMsgBean, file, isArtworkMaster);
                                startService(new Intent(getContext(), UpLoadService.class));
                            }

                        } else {
                            String videofile = localMedia.getPath();
                            sendVideo(videofile);
                        }
                    }
                    notifyData2Bottom(true);

                    break;
                case REQ_RP://红包
                    LogUtil.writeEnvelopeLog("云红包回调了");
                    LogUtil.getLog().e("云红包回调了");
                    EnvelopeBean envelopeInfo = JrmfRpClient.getEnvelopeInfo(data);
                    if (!checkNetConnectStatus(0)) {
                        if (envelopeInfo != null) {
                            saveMFEnvelope(envelopeInfo);
                        }
                        return;
                    }
                    if (envelopeInfo != null) {
                        //  ToastUtil.show(getContext(), "红包的回调" + envelopeInfo.toString());
                        String info = envelopeInfo.getEnvelopeMessage();
                        String rid = envelopeInfo.getEnvelopesID();
                        LogUtil.writeEnvelopeLog("rid=" + rid);
                        LogUtil.getLog().e("rid=" + rid);
                        MsgBean.RedEnvelopeMessage.RedEnvelopeStyle style = MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.NORMAL;
                        if (envelopeInfo.getEnvelopeType() == 1) {//拼手气
                            style = MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.LUCK;
                        }
                        RedEnvelopeMessage message = SocketData.createRbMessage(SocketData.getUUID(), envelopeInfo.getEnvelopesID(), envelopeInfo.getEnvelopeMessage(), MsgBean.RedEnvelopeType.MFPAY.getNumber(), style.getNumber());
                        sendMessage(message, ChatEnum.EMessageType.RED_ENVELOPE);
                    }
                    break;

                case REQUEST_RED_ENVELOPE:
                    CxEnvelopeBean envelopeBean = data.getParcelableExtra("envelope");
                    if (envelopeBean != null) {
                        RedEnvelopeMessage message = SocketData.createSystemRbMessage(SocketData.getUUID(), envelopeBean.getTradeId(), envelopeBean.getActionId(),
                                envelopeBean.getMessage(), MsgBean.RedEnvelopeType.SYSTEM.getNumber(), envelopeBean.getEnvelopeType(), envelopeBean.getSign());
                        sendMessage(message, ChatEnum.EMessageType.RED_ENVELOPE);
                    }
                    break;
                case GroupSelectUserActivity.RET_CODE_SELECTUSR:
                    String uid = data.getStringExtra(GroupSelectUserActivity.UID);
                    String name = data.getStringExtra(GroupSelectUserActivity.MEMBERNAME);
                    if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(name)) {
                        editChat.addAtSpan(null, name, Long.valueOf(uid));
                    }
                    editChat.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editChat.requestFocus();
                            InputUtil.showKeyboard(editChat);
                            if (!mViewModel.isInputText.getValue()) mViewModel.isInputText.setValue(true);
                        }
                    }, 100);
                    break;
                case FilePickerManager.REQUEST_CODE:
                    //断网提示
                    if (!checkNetConnectStatus(0)) {
                        return;
                    }
                    MsgAllBean fileMsgBean = null;
                    //拿到选中的文件路径集合
                    List<String> filePathList = FilePickerManager.INSTANCE.obtainData();
                    for (String filePath : filePathList) {
                        if (StringUtil.isNotNull(filePath)) {
                            //生成随机uuid、获取文件名、文件大小
                            double fileSize = net.cb.cb.library.utils.FileUtils.getFileOrFilesSize(filePath, SIZETYPE_B);
                            String fileName = net.cb.cb.library.utils.FileUtils.getFileName(filePath);
                            if (fileSize > 104857600) {
                                ToastUtil.showLong(this, "文件最大不能超过100M，请重新选择!\n" + "异常文件: " + fileName);
                                continue;
                            }
                            if (fileSize == 0) {
                                ToastUtil.showLong(this, "文件大小不能为0KB，请重新选择!\n" + "异常文件: " + fileName);
                                continue;
                            }
                            String fileMsgId = SocketData.getUUID();
                            String fileFormat = net.cb.cb.library.utils.FileUtils.getFileSuffix(fileName);
                            //如果是图片或者视频，按原有旧的方式打开，不调用第三方程序列表
                            if (net.cb.cb.library.utils.FileUtils.isImage(fileFormat)) {
                                //1.上传图片
                                final String imgMsgId = SocketData.getUUID();
                                ImageMessage imageMessage = SocketData.createImageMessage(imgMsgId, /*"file://" +*/ filePath, false);//TODO:使用file://路径会使得检测本地路径不存在
                                imgMsgBean = sendMessage(imageMessage, ChatEnum.EMessageType.IMAGE, false);
                                // 不等于常信小助手，需要上传到服务器
                                if (!Constants.CX_HELPER_UID.equals(toUId)) {
                                    UpLoadService.onAddImage(imgMsgBean, filePath, false);
                                    startService(new Intent(getContext(), UpLoadService.class));
                                }
                            } else if (net.cb.cb.library.utils.FileUtils.isVideo(fileFormat)) {
                                sendVideo(filePath);
                            } else {
                                //创建文件消息，本地预先准备好这条文件消息，等文件上传成功后刷新
                                SendFileMessage fileMessage = SocketData.createFileMessage(fileMsgId, filePath, "", fileName, new Double(fileSize).longValue(), fileFormat, false);
                                fileMsgBean = sendMessage(fileMessage, ChatEnum.EMessageType.FILE, false);
                                // 若不为常信小助手，消息需要上传到服务器
                                if (!Constants.CX_HELPER_UID.equals(toUId)) {
                                    UpLoadService.onAddFile(this.context, fileMsgBean);
                                    startService(new Intent(getContext(), UpLoadService.class));
                                }
                            }
                        } else {
                            ToastUtil.show("文件不存在或已被删除");
                        }
                    }
                    //刷新首页消息列表
                    notifyData2Bottom(true);
                    break;

            }
        } else if (resultCode == SelectUserActivity.RET_CODE_SELECTUSR) {//选择通讯录中的某个人
            if (!checkNetConnectStatus(0)) {
                return;
            }
            String json = data.getStringExtra(SelectUserActivity.RET_JSON);
            UserInfo userInfo = gson.fromJson(json, UserInfo.class);
            BusinessCardMessage cardMessage = SocketData.createCardMessage(SocketData.getUUID(), userInfo.getHead(), userInfo.getName(), userInfo.getImid(), userInfo.getUid());
            sendMessage(cardMessage, ChatEnum.EMessageType.BUSINESS_CARD);
        }
    }

    private void sendVideo(String videofile) {
        MsgAllBean videoMsgBean;
        if (null != videofile) {
            VideoSize videoSize = ImgSizeUtil.getVideoAttribute(videofile);
            if (videoSize == null) {
                ToastUtil.show(this, "视频处理失败");
//                                    return;
            }
            long duration = videoSize.getDuration();
            VideoMessage videoMessage = SocketData.createVideoMessage(SocketData.getUUID(), "file://" + videofile, videoSize.getBgUrl(), false, duration, videoSize.getWidth(), videoSize.getHeight(), videofile);
            videoMsgBean = sendMessage(videoMessage, ChatEnum.EMessageType.MSG_VIDEO, false);
            // 不等于常信小助手，需要上传到服务器
            if (!Constants.CX_HELPER_UID.equals(toUId)) {
                UpLoadService.onAddVideo(this.context, videoMsgBean, false);
                startService(new Intent(getContext(), UpLoadService.class));
            }
        } else {
            ToastUtil.show(this, "文件已损坏，请重新选择");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshInfo(EventGroupChange event) {
        if (event.isNeedLoad()) {
            taskGroupInfo();
        } else {
//            refreshUI();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EtaskRefreshMessagevent(EventRefreshChat event) {
        int type = event.getRefreshType();
        if (type == CoreEnum.ERefreshType.ALL) {
            taskRefreshMessage(event.isScrollBottom);
        } else if (type == CoreEnum.ERefreshType.DELETE) {
            dismissPop();
            if (event.getObject() != null && event.getObject() instanceof MsgAllBean) {
                deleteMsg((MsgAllBean) event.getObject());
            } else if (event.getList() != null) {
                deleteMsgList(event.getList());
            }
        } else if (type == CoreEnum.ERefreshType.ADD) {
            if (event.getObject() != null && event.getObject() instanceof MsgAllBean) {
                taskRefreshMessage(false);
//                MsgAllBean bean = (MsgAllBean) event.getObject();
//                if (isMsgFromCurrentChat(bean.getGid(), bean.getFrom_uid())) {
//                    addMsg(bean);
//                    sendRead(bean);
//                }
            } /*else if (event.getList() != null) {
                addMsg(event.getList());
            }*/
            initUnreadCount();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventSwitchDisturb(EventSwitchDisturb event) {
//        refreshUI();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventSwitchDisturb(EventFactory.ToastEvent event) {
        if (!TextUtils.isEmpty(event.value)) {
            ToastUtil.showCenter(this, event.value);
        } else {
            ToastUtil.showCenter(this, getString(R.string.group_you_forbidden_words));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void freshUserStateEvent(EventFactory.FreshUserStateEvent event) {
        // 只有Vip才显示视频通话
        viewExtendFunction.bindDate(getItemModels());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void taskUpImgEvevt(EventUpImgLoadEvent event) {
        if (event.getState() == 0) {
            taskRefreshImage(event.getMsgid());
        } else if (event.getState() == -1) {
            //处理失败的情况
            if (!isFinishing()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MsgAllBean msgAllbean = (MsgAllBean) event.getMsgAllBean();
                        replaceListDataAndNotify(msgAllbean, true);
                    }
                }, 800);
            }
        } else if (event.getState() == 1) {
            MsgAllBean msgAllbean = (MsgAllBean) event.getMsgAllBean();
            LogUtil.getLog().d("tag", "taskUpImgEvevt 1: ===============>" + msgAllbean.getImage());
//            SocketData.sendAndSaveMessage(msgAllbean);
            replaceListDataAndNotify(msgAllbean);
        } else {
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void stopVoiceeEvent(EventFactory.StopVoiceeEvent event) {
        // 对方撤回时，停止语音播放
        if (event != null) {
            if (event.msg_id.equals(AudioPlayManager.getInstance().msg_id)) {
                AudioPlayManager.getInstance().stopPlay();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void locationSendEvent(LocationSendEvent event) {
        LocationMessage message = SocketData.createLocationMessage(SocketData.getUUID(), event.message);
        sendMessage(message, ChatEnum.EMessageType.LOCATION);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void taskUpFileEvent(EventUpFileLoadEvent event) {
        //上传中：更新文件上传进度
        if (event.getState() == 0) {
            taskRefreshImage(event.getMsgid());
        } else if (event.getState() == -1) {
            //上传失败或成功均刷新
            if (!isFinishing()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MsgAllBean msgAllbean = (MsgAllBean) event.getMsgAllBean();
                        replaceListDataAndNotify(msgAllbean, true);
                    }
                }, 800);
            }
        } else if (event.getState() == 1) {
            //已完成：更新文件上传进度，同时拿最新的数据
            MsgAllBean msgAllbean = (MsgAllBean) event.getMsgAllBean();
            replaceListDataAndNotify(msgAllbean);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshFileRename(EventFileRename event) {
        MsgAllBean msgAllbean = (MsgAllBean) event.getMsgAllBean();
        replaceListDataAndNotify(msgAllbean, true);
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
        if (mAdapter == null && mAdapter.getItemCount() <= 0) {
            return;
        }
        int position = mAdapter.updateMessage(msgAllbean);
        if (position >= 0) {
            mtListView.getListView().getAdapter().notifyItemChanged(position, position);
        }

    }

    /***
     * 替换listData中的某条消息并且刷新
     * @param msgAllbean
     */
    private void replaceListDataAndNotify(MsgAllBean msgAllbean, boolean loose) {
        if (mAdapter == null || mAdapter.getItemCount() <= 0) {
            return;
        }
        int position = mAdapter.updateMessage(msgAllbean);
        if (position >= 0) {
            mtListView.getListView().getAdapter().notifyItemChanged(position, position);
        }
    }


    /***
     * 更新图片需要的进度
     * @param msgid
     */
    private void taskRefreshImage(String msgid) {
        if (mAdapter == null || mAdapter.getItemCount() <= 0)
            return;
        int len = mAdapter.getItemCount();
        for (int i = 0; i < len; i++) {
            if (mAdapter.getMessage(i).getMsg_id().equals(msgid)) {
                mtListView.getListView().getAdapter().notifyItemChanged(i, i);
            }
        }
    }

    /**
     * 显示大图
     *
     * @param msgid
     * @param uri
     */
    private void showBigPic(String msgid, String uri) {
        List<LocalMedia> selectList = new ArrayList<>();
        List<LocalMedia> temp = new ArrayList<>();
        int pos = 0;
        List<MsgAllBean> listdata = msgAction.getMsg4UserImg(toGid, toUId);
        for (int i = 0; i < listdata.size(); i++) {
            MsgAllBean msgl = listdata.get(i);
            if (msgid.equals(msgl.getMsg_id())) {
                pos = i;
            }

            LocalMedia lc = new LocalMedia();
            lc.setCutPath(msgl.getImage().getThumbnailShow());
            lc.setCompressPath(msgl.getImage().getPreviewShow());
            lc.setPath(msgl.getImage().getOriginShow());
            lc.setSize(msgl.getImage().getSize());
            lc.setWidth(new Long(msgl.getImage().getWidth()).intValue());
            lc.setHeight(new Long(msgl.getImage().getHeight()).intValue());
            lc.setMsg_id(msgl.getMsg_id());
            //发送状态正常，且未开启阅后即焚，则允许收藏
            if (msgl.getSend_state() != ChatEnum.ESendStatus.ERROR && msgl.getSurvival_time() == 0) {
                lc.setCanCollect(true);
            }
            temp.add(lc);
        }
        int size = temp.size();
        //取中间100张
        if (size <= 100) {
            selectList.addAll(temp);
        } else {
            if (pos - 50 <= 0) {//取前面
                selectList.addAll(temp.subList(0, 100));
            } else if (pos + 50 >= size) {//取后面
                selectList.addAll(temp.subList(size - 100, size));
            } else {//取中间
                selectList.addAll(temp.subList(pos - 50, pos + 50));
            }
        }

        pos = 0;
        for (int i = 0; i < selectList.size(); i++) {
            if (msgid.equals(selectList.get(i).getMsg_id())) {
                pos = i;
                break;
            }
        }
        PictureSelector.create(ChatActivity.this)
                .themeStyle(R.style.picture_default_style)
                .isGif(true)
                .openExternalPreview1(pos, selectList);

    }


    /**
     * 跳转UserInfoActivity
     *
     * @param uid
     */
    private void toUserInfoActivity(long uid) {
        String name = "";
        if (isGroup()) {
            name = msgDao.getGroupMemberName2(toGid, uid);
        } else if (mViewModel.userInfo != null) {
            name = mViewModel.userInfo.getName4Show();
        }
        startActivity(new Intent(getContext(), UserInfoActivity.class)
                .putExtra(UserInfoActivity.ID, uid)
                .putExtra(UserInfoActivity.JION_TYPE_SHOW, 1)
                .putExtra(UserInfoActivity.GID, toGid)
                .putExtra(UserInfoActivity.MUC_NICK, name));
    }

    /**
     * 重新发送消息
     *
     * @param msgBean
     */
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
                    MsgAllBean imgMsgBean = sendMessageFromResend(image, ChatEnum.EMessageType.IMAGE, false);
                    UpLoadService.onAddImage(imgMsgBean, file, isArtworkMaster);
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
                reMsg.setSend_state(ChatEnum.ESendStatus.SENDING);
                if (!TextUtils.isEmpty(url)) {
                    VideoMessage videoMessage = reMsg.getVideoMessage();
                    LogUtil.getLog().e("TAG", videoMessage.toString() + videoMessage.getHeight() + "----" + videoMessage.getWidth() + "----" + videoMessage.getDuration() + "----" + videoMessage.getBg_url() + "----");
                    VideoMessage videoMessageSD = SocketData.createVideoMessage(reMsg.getMsg_id(), "file://" + url, videoMessage.getBg_url(), false, videoMessage.getDuration(), videoMessage.getWidth(), videoMessage.getHeight(), url);
                    MsgAllBean msgAllBean = sendMessageFromResend(videoMessageSD, ChatEnum.EMessageType.MSG_VIDEO, false);
                    if (!TextUtils.isEmpty(videoMessage.getBg_url())) {
                        // 当预览图清空掉时重新获取
                        File file = new File(videoMessage.getBg_url());
                        if (file == null || !file.exists()) {
                            videoMessage.setBg_url(getVideoAttBitmap(url));
                        }
                    }
                    UpLoadService.onAddVideo(this.context, msgAllBean, false);
                    startService(new Intent(getContext(), UpLoadService.class));

                } else {
                    //点击发送的时候如果要改变成发送中的状态
                    DaoUtil.update(reMsg);
                    MsgBean.UniversalMessage.Builder bean = MsgBean.UniversalMessage.parseFrom(reMsg.getSend_data()).toBuilder();
                    SocketUtil.getSocketUtil().sendData4Msg(bean);
                    taskRefreshMessage(false);
                }
            } else if (reMsg.getMsg_type() == ChatEnum.EMessageType.FILE) { //文件消息失败重发机制
                if (!checkNetConnectStatus(0)) {
                    return;
                }
                //群文件重发，判断是否被禁言
                if (isGroup()) {
                    getSingleMemberInfo(reMsg);
                } else {
                    resendFileMsg(reMsg);
                }
            } else {
                if (reMsg.getSend_data() == null && reMsg.getMsgContent() != null) {
                    sendMessageFromResend(reMsg.getMsgContent(), reMsg.getMsg_type(), true);
                } else {
                    //点击发送的时候如果要改变成发送中的状态
                    reMsg.setSend_state(ChatEnum.ESendStatus.SENDING);
                    DaoUtil.update(reMsg);
                    MsgBean.UniversalMessage.Builder bean = MsgBean.UniversalMessage.parseFrom(reMsg.getSend_data()).toBuilder();
                    SocketUtil.getSocketUtil().sendData4Msg(bean);
                    taskRefreshMessage(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void clickUser(String userId, String gid) {
        try {
            long user = Long.valueOf(userId);
            if (user > 0) {
                toUserInfoActivity(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void clickEnvelope(String rid) {
        long tradeId = StringUtil.getLong(rid);
        if (tradeId > 0) {
            Intent intent = SingleRedPacketDetailsActivity.newIntent(ChatActivity.this, tradeId, 1);
            startActivity(intent);
        }
    }

    @Override
    public void clickTransfer(String rid, String msgId) {
        long tradeId = StringUtil.getLong(rid);
        if (tradeId > 0) {
            MsgAllBean msgAllBean = msgDao.getMsgById(msgId);
            if (msgAllBean != null) {
                showLoadingDialog();
                httpGetTransferDetail(rid, PayEnum.ETransferOpType.TRANS_SEND, msgAllBean);
            }
        }
    }

    @Override
    public void clickLock() {
        if (ViewUtils.isFastDoubleClick()) {
            return;
        }
        showLockDialog();
    }

    @Override
    public void clickEditAgain(String content) {
        if (ViewUtils.isFastDoubleClick()) {
            return;
        }

        //br标签替换为换行，存之前将换行替换为br标签
        content = content.replace("<br>", "\n");
        showDraftContent(editChat.getText().toString() + content);
        editChat.setSelection(editChat.getText().length());
        //虚拟键盘弹出,需更改SoftInput模式为：不顶起输入框
        if (!mViewModel.isOpenValue()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        mViewModel.isInputText.setValue(true);

    }

    private String getEnvelopeInfo(@PayEnum.EEnvelopeStatus int envelopStatus) {
        String info = "";
        switch (envelopStatus) {
            case PayEnum.EEnvelopeStatus.NORMAL:
                info = "领取红包";
                break;
            case PayEnum.EEnvelopeStatus.RECEIVED:
                info = "已领取";
                break;
            case PayEnum.EEnvelopeStatus.RECEIVED_FINISHED:
                info = "已被领完";
                break;
            case PayEnum.EEnvelopeStatus.PAST:
                info = "已过期";
                break;
        }
        return info;
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
            int length = mAdapter.getItemCount();
            if (position < length - 1) {
                for (int i = position + 1; i < length; i++) {
                    MsgAllBean bean = mAdapter.getMessage(i);
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
        int length = mAdapter.getItemCount();
        int index = mAdapter.getPosition(b);
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
                MsgAllBean bean = mAdapter.getMessage(i);
                if (bean.getMsg_type() == ChatEnum.EMessageType.VOICE && !bean.isMe() && !isVoiceRead(bean)) {
                    message = bean;
                    position = i;
                    break;
                }
            }
        }
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
        if (mAdapter != null && position < mAdapter.getItemCount()) {
            MsgAllBean msg = mAdapter.getMessage(position);
            if (msg.getMsg_id().equals(bean.getMsg_id())) {
                return msg;
            } else {
                int p = mAdapter.getPosition(bean);
                if (p >= 0) {
                    return mAdapter.getMessage(p);
                }
            }
        }
        return bean;
    }

    private void playVoice(final MsgAllBean bean, final boolean canAutoPlay,
                           final int position) {
//        LogUtil.getLog().i(TAG, "playVoice--" + position);
        VoiceMessage vm = bean.getVoiceMessage();
        if (vm == null || (TextUtils.isEmpty(vm.getUrl()) && TextUtils.isEmpty(vm.getLocalUrl()))) {
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

    private void updatePlayStatus(MsgAllBean bean, int position,
                                  @ChatEnum.EPlayStatus int status) {
//        LogUtil.getLog().i(TAG, "updatePlayStatus--" + status + "--position=" + position);
        bean = amendMsgALlBean(position, bean);
        if (bean == null || bean.getVoiceMessage() == null) {
            return;
        }
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

        if (ChatEnum.EPlayStatus.PLAYING == status) {
            MessageManager.getInstance().setCanStamp(false);
        } else if (ChatEnum.EPlayStatus.STOP_PLAY == status || ChatEnum.EPlayStatus.PLAYED == status) {
            MessageManager.getInstance().setCanStamp(true);
        }
    }

    private void startPlayVoice(MsgAllBean bean, boolean canAutoPlay, final int position) {
        if (downloadList.size() > 1) {
            int size = downloadList.size();
            int p = downloadList.indexOf(bean);
            if (p != size - 1) {
                downloadList.remove(bean);
                return;
            }
        }
        downloadList.remove(bean);

        AudioPlayManager.getInstance().startPlay(context, bean, position, canAutoPlay, new IVoicePlayListener() {
            @Override
            public void onStart(MsgAllBean bean) {
                updatePlayStatus(bean, position, ChatEnum.EPlayStatus.PLAYING);
            }

            @Override
            public void onStop(MsgAllBean bean) {
                updatePlayStatus(bean, position, ChatEnum.EPlayStatus.STOP_PLAY);
            }

            @Override
            public void onComplete(MsgAllBean bean) {
                updatePlayStatus(bean, position, ChatEnum.EPlayStatus.PLAYED);
            }
        });
    }

    /***
     * 长按的气泡处理
     * @param v
     * @param menus
     * @param msgbean
     */
    private void showPop(View v, List<OptionMenu> menus, final MsgAllBean msgbean, final IMenuSelectListener listener) {
        if (popController == null) {
            return;
        }
        menus = initMenus(msgbean);
        AdapterPopMenu adapterPopMenu = new AdapterPopMenu(menus, this);
        popController.setAdapter(adapterPopMenu);
        adapterPopMenu.setListener(new AdapterPopMenu.IMenuClickListener() {
            @Override
            public void onClick(OptionMenu menu) {
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
                onBubbleClick((String) menu.getTitle(), msgbean);
            }
        });

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
        if (mPopupWindow != null && mPopupWindow.isShowing()) mPopupWindow.dismiss();
        mPopupWindow = null;
        mPopupWindow = new PopupWindow(mRootView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置弹窗外可点击
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        //popwindow不获取焦点
        mPopupWindow.setFocusable(false);
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
        if (v.getMeasuredHeight() >= mtListView.getMeasuredHeight() && locationView[1] < location[1]) {
            // 内容展示完，向上弹出
            if (locationView[1] < 0 && (v.getMeasuredHeight() - Math.abs(locationView[1]) < mtListView.getMeasuredHeight())) {
                mImgTriangleUp.setVisibility(VISIBLE);
                mImgTriangleDown.setVisibility(GONE);
                mPopupWindow.showAsDropDown(v);
            } else {
                // 中间弹出
                mImgTriangleUp.setVisibility(GONE);
                mImgTriangleDown.setVisibility(VISIBLE);
                showPopupWindowUp(v, 1);
            }
        } else if (locationView[1] < location[1]) {
            mImgTriangleUp.setVisibility(VISIBLE);
            mImgTriangleDown.setVisibility(GONE);
            mPopupWindow.showAsDropDown(v);
        } else {
            mImgTriangleUp.setVisibility(GONE);
            mImgTriangleDown.setVisibility(VISIBLE);
            showPopupWindowUp(v, 2);
        }
    }

    private List<OptionMenu> initMenus(MsgAllBean msgAllBean) {
        int type = msgAllBean.getMsg_type();
        int sendStatus = msgAllBean.getSend_state();
        List<OptionMenu> menus = new ArrayList<>();
        if (sendStatus == ChatEnum.ESendStatus.NORMAL && !isBanReply(type)) {
            menus.add(new OptionMenu("回复"));
        }
        if (sendStatus == ChatEnum.ESendStatus.NORMAL && !isBanForward(type)) {
            menus.add(new OptionMenu("转发"));
        }
        switch (type) {
            case ChatEnum.EMessageType.TEXT:
            case ChatEnum.EMessageType.AT:
                menus.add(0, new OptionMenu("复制"));
                //发送状态正常，且未开启阅后即焚，则允许收藏
                if (sendStatus != ChatEnum.ESendStatus.ERROR && msgAllBean.getSurvival_time() == 0) {
                    menus.add(new OptionMenu("收藏"));
                }
                break;
            case ChatEnum.EMessageType.VOICE:
                if (msgDao.userSetingGet().getVoicePlayer() == 0) {
                    menus.add(0, new OptionMenu("听筒播放"));
                } else {
                    menus.add(0, new OptionMenu("扬声器播放"));
                }
                //发送状态正常，且未开启阅后即焚，则允许收藏
                if (sendStatus != ChatEnum.ESendStatus.ERROR && msgAllBean.getSurvival_time() == 0) {
                    menus.add(new OptionMenu("收藏"));
                }
                break;
            case ChatEnum.EMessageType.LOCATION:
            case ChatEnum.EMessageType.IMAGE:
            case ChatEnum.EMessageType.MSG_VIDEO:
            case ChatEnum.EMessageType.SHIPPED_EXPRESSION:
            case ChatEnum.EMessageType.FILE:
                //发送状态正常，且未开启阅后即焚，则允许收藏
                if (sendStatus != ChatEnum.ESendStatus.ERROR && msgAllBean.getSurvival_time() == 0) {
                    menus.add(new OptionMenu("收藏"));
                }
                break;
        }
        if (sendStatus == ChatEnum.ESendStatus.NORMAL && type != ChatEnum.EMessageType.MSG_VOICE_VIDEO) {
            if (!isGroupBanCancel()) {
                if (msgAllBean.getFrom_uid() != null && msgAllBean.getFrom_uid().longValue() == UserAction.getMyId().longValue() && msgAllBean.getMsg_type() != ChatEnum.EMessageType.RED_ENVELOPE && !isAtBanedCancel(msgAllBean)) {
                    if (System.currentTimeMillis() - msgAllBean.getTimestamp() < 2 * 60 * 1000) {//两分钟内可以删除
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
        }
        menus.add(new OptionMenu("删除"));
        return menus;
    }

    //是否禁止转发
    public boolean isBanForward(@ChatEnum.EMessageType int type) {
        if (type == ChatEnum.EMessageType.VOICE || type == ChatEnum.EMessageType.STAMP || type == ChatEnum.EMessageType.RED_ENVELOPE
                || type == ChatEnum.EMessageType.MSG_VOICE_VIDEO || type == ChatEnum.EMessageType.BUSINESS_CARD || type == ChatEnum.EMessageType.ASSISTANT_PROMOTION /*|| type == ChatEnum.EMessageType.REPLY*/) {
            return true;
        }
        return false;
    }

    //是否禁止回复
    public boolean isBanReply(@ChatEnum.EMessageType int type) {
        if (/*type == ChatEnum.EMessageType.VOICE ||*/ type == ChatEnum.EMessageType.STAMP || type == ChatEnum.EMessageType.RED_ENVELOPE
                || type == ChatEnum.EMessageType.MSG_VOICE_VIDEO /*|| type == ChatEnum.EMessageType.BUSINESS_CARD*/ || type == ChatEnum.EMessageType.LOCATION
                || type == ChatEnum.EMessageType.SHIPPED_EXPRESSION || type == ChatEnum.EMessageType.WEB || type == ChatEnum.EMessageType.BALANCE_ASSISTANT ||
                type == ChatEnum.EMessageType.ASSISTANT_PROMOTION) {
            return true;
        }
        return false;
    }

    /**
     * 初始化PopupWindow
     */
    private void initPopupWindow() {
        mRootView = getLayoutInflater().inflate(R.layout.view_chat_pop, null, false);
        //获取自身的长宽高
        mRootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupWidth = mRootView.getMeasuredWidth();
        popupHeight = mRootView.getMeasuredHeight();

        mImgTriangleUp = mRootView.findViewById(R.id.img_triangle_up);
        mImgTriangleDown = mRootView.findViewById(R.id.img_triangle_down);
        LinearLayout llContent = mRootView.findViewById(R.id.ll_content);
        popController = new ControllerLinearList(llContent);
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
        } else if ("回复".equals(value)) {
            onAnswer(msgbean);
        } else if ("多选".equals(value)) {
            onMore(msgbean);
        } else if ("收藏".equals(value)) {
            onCollect(msgbean);
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
        alertYesNo.init(ChatActivity.this, "删除", "确认删除该条消息?", "确定", "取消", new AlertYesNo.Event() {
            @Override
            public void onON() {

            }

            @Override
            public void onYes() {
                msgDao.msgDel4MsgId(msgbean.getMsg_id());
                deleteMsg(msgbean);
            }
        });
        alertYesNo.show();
    }

    private void onMore(final MsgAllBean msgBean) {
        showViewMore(true);
        mAdapter.getSelectedMsg().add(msgBean);
        mAdapter.showCheckBox(true, true);
    }

    /**
     * 转发
     *
     * @param msgbean
     */
    private void onRetransmission(final MsgAllBean msgbean) {
        Intent intent = MsgForwardActivity.newIntent(this, ChatEnum.EForwardMode.DEFAULT, new Gson().toJson(msgbean));
        startActivity(intent);
    }

    /**
     * 撤回
     *
     * @param msgBean
     */
    private void onRecall(final MsgAllBean msgBean) {
        int position = mAdapter.getPosition(msgBean);
        MsgCancel cancel = SocketData.createCancelMsg(msgBean);
        if (cancel != null) {
            sendMessage(cancel, ChatEnum.EMessageType.MSG_CANCEL, position);
        }
        if (msgBean.getMsg_type() == ChatEnum.EMessageType.VOICE) {
            AudioPlayManager.getInstance().stopPlay();
        }
    }


    //回复
    private void onAnswer(MsgAllBean bean) {
        isReplying = true;
        replayMsg = bean;
        if (MessageManager.getInstance().isFromSelf(bean.getFrom_uid())) {

        } else {
            if (mViewModel.userInfo != null) {
                replayMsg.setFrom_nickname(mViewModel.userInfo.getName());
            }
        }
        if (isGroup() && !MessageManager.getInstance().isFromSelf(bean.getFrom_uid())) {
            doAtInput(bean);
        }
        //弹出软键盘
        if (!mViewModel.isOpenValue()) //没有事件触发，设置改SoftInput模式为：顶起输入框
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (!mViewModel.isInputText.getValue())
            mViewModel.isInputText.setValue(true);
        if (!mViewModel.isReplying.getValue())
            mViewModel.isReplying.setValue(true);
        viewReplyMessage.setMessage(bean);
        mtListView.scrollToEnd();
    }

    //收藏
    private void onCollect(MsgAllBean msgbean) {
        String fromUsername = "";//用户名称
        String fromGid = "";//群组id
        String fromGroupName = "";//群组名称
        if (!TextUtils.isEmpty(msgbean.getFrom_nickname())) {
            fromUsername = msgbean.getFrom_nickname();
        } else {
            fromUsername = "";
        }
        if (!TextUtils.isEmpty(msgbean.getGid())) {
            fromGid = msgbean.getGid();
        } else {
            fromGid = "";
        }
        if (msgbean.getGroup() != null) {
            if (!TextUtils.isEmpty(msgbean.getGroup().getName())) {
                fromGroupName = msgbean.getGroup().getName();
            } else {
                fromGroupName = msgDao.getGroupName(msgbean.getGid());//没有群名称，拿自动生成的群昵称给后台
            }
        }
        CollectionInfo collectionInfo = new CollectionInfo();
        //区分不同消息类型，转换成新的收藏消息结构，作为data传过去
        if (msgbean.getMsg_type() == ChatEnum.EMessageType.TEXT) {
            collectionInfo.setData(new Gson().toJson(convertCollectBean(ChatEnum.EMessageType.TEXT, msgbean)));
        } else if (msgbean.getMsg_type() == ChatEnum.EMessageType.IMAGE) {
            collectionInfo.setData(new Gson().toJson(convertCollectBean(ChatEnum.EMessageType.IMAGE, msgbean)));
        } else if (msgbean.getMsg_type() == ChatEnum.EMessageType.SHIPPED_EXPRESSION) {
            collectionInfo.setData(new Gson().toJson(convertCollectBean(ChatEnum.EMessageType.SHIPPED_EXPRESSION, msgbean)));
        } else if (msgbean.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO) {
            collectionInfo.setData(new Gson().toJson(convertCollectBean(ChatEnum.EMessageType.MSG_VIDEO, msgbean)));
        } else if (msgbean.getMsg_type() == ChatEnum.EMessageType.VOICE) {
            collectionInfo.setData(new Gson().toJson(convertCollectBean(ChatEnum.EMessageType.VOICE, msgbean)));
        } else if (msgbean.getMsg_type() == ChatEnum.EMessageType.LOCATION) {
            collectionInfo.setData(new Gson().toJson(convertCollectBean(ChatEnum.EMessageType.LOCATION, msgbean)));
        } else if (msgbean.getMsg_type() == ChatEnum.EMessageType.AT) {
            collectionInfo.setData(new Gson().toJson(convertCollectBean(ChatEnum.EMessageType.AT, msgbean)));
        } else if (msgbean.getMsg_type() == ChatEnum.EMessageType.FILE) {
            CollectSendFileMessage msg = (CollectSendFileMessage) convertCollectBean(ChatEnum.EMessageType.FILE, msgbean);
            collectionInfo.setData(new Gson().toJson(msg));
            //暂时只对文件进行本地化存储，列表里没有本地路径不方便判断是否下载，避免每次进详情都要下一次
//            msgDao.saveCollectFileMsg(msg);
        }
        collectionInfo.setFromUid(msgbean.getFrom_uid());
        collectionInfo.setFromUsername(fromUsername);
        collectionInfo.setType(SocketData.getMessageType(msgbean.getMsg_type()).getNumber());//收藏类型统一改为protobuf类型
        collectionInfo.setFromGid(fromGid);
        collectionInfo.setFromGroupName(fromGroupName);
        collectionInfo.setMsgId(msgbean.getMsg_id());//不同表，id相同
        collectionInfo.setCreateTime(System.currentTimeMillis() + "");//收藏时间是现在系统时间
        //1 有网收藏
        if (checkNetConnectStatus(1)) {
            httpCollect(collectionInfo);
        }else {
            //2 无网收藏
            //2-1 如果本地收藏列表不存在这条数据，收藏到列表，并保存收藏操作记录
            if(msgDao.findLocalCollection(msgbean.getMsg_id())==null){
                msgDao.addLocalCollection(collectionInfo);//保存到本地收藏列表
                OfflineCollect offlineCollect = new OfflineCollect();
                offlineCollect.setMsgId(msgbean.getMsg_id());
                offlineCollect.setCollectionInfo(collectionInfo);
                msgDao.addOfflineCollectRecord(offlineCollect);//保存到离线收藏记录表
            }
            //2-2 如果本地收藏列表存在这条数据，无需再重复收藏，不做任何操作
            ToastUtil.show("已收藏");//离线提示
        }
    }


    /**
     * 设置显示在v上方(以v的左边距为开始位置)
     *
     * @param v
     */
    public void showPopupWindowUp(View v, int gravity) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        if (gravity == 1) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, dm.heightPixels / 2);
        } else {
            //在控件上方显示
            mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
        }
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
        LogUtil.getLog().i(TAG, "刷新数据");
//        mtListView.notifyDataSetChange();
        if (mAdapter.getMsgList() != null && mAdapter.getItemCount() > 0) {
            //调用该方法，有面板或软键盘弹出时，会使列表跳转到第一项
            mtListView.getListView().getAdapter().notifyItemRangeChanged(0, mAdapter.getItemCount());
        }
        mtListView.getSwipeLayout().setRefreshing(false);
    }

    private MsgAction msgAction = new MsgAction();
    private UserAction userAction = new UserAction();
    private UserDao userDao = new UserDao();
    private MsgDao msgDao = new MsgDao();
    private PayAction payAction = new PayAction();


    private void setDisturb() {
        int disturb = 0;
        if (isGroup()) {
            if (mViewModel.groupInfo != null) disturb = mViewModel.groupInfo.getNotNotify();
        } else {
            if (mViewModel.userInfo != null) disturb = mViewModel.userInfo.getDisturb();
        }
        actionbar.showDisturb(disturb == 1);
    }

    /***
     * 获取会话信息
     */
    private void updateUserOnlineStatus(UserInfo userInfo) {
        String title = "";
        if (!isGroup()) {
            title = userInfo.getName4Show();
            if (userInfo.getLastonline() > 0) {
                // 客服不显示时间状态
                if (onlineState && !UserUtil.isSystemUser(toUId) && userInfo.getuType() != ChatEnum.EUserType.ASSISTANT) {
                    actionbar.setTitleMore(TimeToString.getTimeOnline(userInfo.getLastonline(), userInfo.getActiveType(), true), true);
                } else {
                    actionbar.setTitleMore(TimeToString.getTimeOnline(userInfo.getLastonline(), userInfo.getActiveType(), true), false);
                }
            }
            actionbar.setChatTitle(title);
        }
    }

    public synchronized void sendRead() {
        //发送已读回执,不要检测是否已读开关关闭，不然会影响阅后即焚功能
        if (TextUtils.isEmpty(toGid) && !UserUtil.isBanSendUser(toUId)/* && checkIsRead()*/) {
            MsgAllBean bean = msgDao.msgGetLast4FromUid(toUId);
            if (bean != null) {
                if (bean.getRead() == 0) {
                    if (MessageManager.getInstance().isReadTimeValid(toUId, bean.getTimestamp())) {
                        MessageManager.getInstance().addReadTime(toUId, bean.getTimestamp());
                        LogUtil.getLog().i(TAG, "发送已读--msgID=" + bean.getMsg_id() + "--time=" + bean.getTimestamp());
                        ReadMessage read = SocketData.createReadMessage(SocketData.getUUID(), bean.getTimestamp());
                        sendMessage(read, ChatEnum.EMessageType.READ);
                    }
                }
            }
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
        if (mAdapter != null) {
            mAdapter.setUnreadCount(unreadCount);
        }
        dismissPop();
        long time = -1L;
        int length = 0;
        if (mAdapter != null && mAdapter.getItemCount() > 0) {
            length = mAdapter.getItemCount();
            MsgAllBean bean = mAdapter.getMessage(length - 1);
            if (bean != null && bean.getTimestamp() != null) {
                time = bean.getTimestamp();
            }
        }
        final long finalTime = time;
        if (length < 80) {
            length += 80;
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
                            if (unreadCount <= 80) {
                                list = msgAction.getMsg4User(toGid, toUId, null, unreadCount + 80);
                            } else if (unreadCount > 80 && unreadCount <= MAX_UNREAD_COUNT) {
                                list = msgAction.getMsg4User(toGid, toUId, null, unreadCount + 80);
                            } else if (unreadCount > MAX_UNREAD_COUNT) {
                                list = msgAction.getMsg4User(toGid, toUId, null, MAX_UNREAD_COUNT);
                            } else {
                                list = msgAction.getMsg4User(toGid, toUId, null, 80);
                            }
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
                        if (mAdapter != null) {
                            fixLastPosition(mAdapter.getMsgList(), list);
                        }
                        int len = list.size();
//                        if (mAdapter != null) {
//                            list = isRetainAll(mAdapter.getMsgList(), list);
//                        }
//                        int len2 = list.size();
//                        if (len2 < len) {
//                            addMsg(list);
//                        } else {
//                            mAdapter.bindData(list, false);
//                            mAdapter.setReadStatus(checkIsRead());
//                            notifyData2Bottom(isScrollBottom);
//                        }

                        mAdapter.bindData(list, false);
                        mAdapter.setReadStatus(checkIsRead());
                        notifyData2Bottom(isScrollBottom);
                        if (len == 0 && lastPosition > len - 1) {//历史数据被清除了
                            lastPosition = 0;
                            lastOffset = 0;
                            clearScrollPosition();
                        }
                        //单聊发送已读消息
                        sendRead();
                    }
                });

    }

    private void fixLastPosition(List<MsgAllBean> msgList, List<MsgAllBean> list) {
        if (msgList != null && list != null) {
            int len1 = msgList.size();
            int len2 = list.size();
            if (currentScrollPosition > 0) {
                if (len1 < len2) {
                    int diff = len2 - len1;
                    currentScrollPosition += diff;
                }
            }

            if (lastPosition >= msgList.size() - 3) {
                lastPosition = len2 - 1;
            }
            LogUtil.getLog().i(TAG, "scroll--fixLastPosition=" + lastPosition);

        }


    }

    private void dismissPop() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }


    /***
     * 查询历史
     * @param history
     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void taskFinadHistoryMessage(EventFindHistory history) {
//        isLoadHistory = true;
//        List<MsgAllBean> listTemp = msgAction.getMsg4UserHistory(toGid, toUId, history.getStime());
//        taskMkName(listTemp);
//        mAdapter.bindData(listTemp, false);
//        notifyData();
//        mtListView.getListView().smoothScrollToPosition(0);
//
//    }


    /***
     * 加载更多
     */
    private void taskMoreMessage() {
        int addItem = mAdapter.getItemCount();
        if (addItem >= 20) {
            mAdapter.addMessageList(0, msgAction.getMsg4User(toGid, toUId, mAdapter.getMessage(0).getTimestamp(), false));
        } else {
            mAdapter.setMessageList(msgAction.getMsg4User(toGid, toUId, null, false));
        }
        addItem = mAdapter.getItemCount() - addItem;
        taskMkName(mAdapter.getMsgList());
        notifyData();
        scrollChatToPositionWithOffset(addItem, DensityUtil.dip2px(context, 20f));
    }

    /***
     * 统一处理mkname
     */
    private Map<String, UserInfo> mks = new HashMap<>();

    /***
     * 获取统一的昵称
     * @param list
     */
    private void taskMkName(List<MsgAllBean> list) {
        mks.clear();
        for (MsgAllBean msg : list) {
            resetName(msg);
        }
    }

    private void resetName(MsgAllBean msg) {
        if (msg.getMsg_type() == ChatEnum.EMessageType.NOTICE || msg.getMsg_type() == ChatEnum.EMessageType.MSG_CANCEL || msg.getMsg_type() == ChatEnum.EMessageType.LOCK) {  //通知类型的不处理
            return;
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
                    String gname = msgDao.getGroupMemberName(toGid, msg.getFrom_uid(), null, null);//获取对方最新的群昵称
//                    MsgAllBean gmsg = msgDao.msgGetLastGroup4Uid(toGid, msg.getFrom_uid());
//                    if (gmsg != null) {
//                        gname = gmsg.getFrom_group_nickname();
//                    }
                    if (StringUtil.isNotNull(gname)) {
                        userInfo.setName(gname);
                    }
                }
            }
            if (msg.getFrom_uid().longValue() == UserAction.getMyId().longValue()) {
                //自己发送的消息,用本地实时头像
                userInfo.setHead(UserAction.getMyInfo().getHead());
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

    private MsgDao dao = new MsgDao();

    /***
     * 清理已读
     */
    private boolean taskCleanRead(boolean isFirst) {
        Session session = StringUtil.isNotNull(toGid) ? DaoUtil.findOne(Session.class, "gid", toGid) :
                DaoUtil.findOne(Session.class, "from_uid", toUId);
        if (session != null && session.getUnread_count() > 0) {
            if (isFirst) {
                unreadCount = session.getUnread_count();
            }
            dao.sessionReadClean(session);
            return true;
        }
        return false;
    }

    /***
     * 获取草稿
     */
    private void taskDraftGet() {
        session = dao.sessionGet(toGid, toUId);
        if (session == null) {
            isFirst++;
            return;
        }

        draft = session.getDraft();
        if (StringUtil.isNotNull(draft)) {
            //设置完草稿之后清理掉草稿 防止@功能不能及时弹出
            showDraftContent(session.getDraft());

        }
        // isFirst解决第一次进来草稿中会有@符号的内容
        isFirst++;
    }

    /***
     * 更新session草稿和at消息
     *
     */
    private boolean updateSessionDraftAndAtMessage() {
        boolean hasChange = false;
        if (session != null && !TextUtils.isEmpty(session.getAtMessage())) {
            hasChange = true;
            dao.updateSessionAtMsg(toGid, toUId);
        }
        if (checkAndSaveDraft()) {
            hasChange = true;
        }
        return hasChange;
    }

    private boolean checkAndSaveDraft() {
        if (isGroup() && mViewModel.groupInfo != null && !MessageManager.getInstance().isGroupValid(mViewModel.groupInfo)) {//无效群，不存草稿
            return false;
        }
        String df = editChat.getText().toString().trim();
        boolean hasChange = false;
        if (!TextUtils.isEmpty(draft)) {
//            if (TextUtils.isEmpty(df) || !draft.equals(df)) {
            hasChange = true;
            dao.sessionDraft(toGid, toUId, df);
            draft = df;
//            }
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
    @SuppressLint("CheckResult")
    private void taskGroupConf() {
        if (!isGroup()) {
            return;
        }
        Observable.just(0)
                .map(new Function<Integer, GroupConfig>() {
                    @Override
                    public GroupConfig apply(Integer integer) throws Exception {
                        return dao.groupConfigGet(toGid);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<GroupConfig>empty())
                .subscribe(new Consumer<GroupConfig>() {
                    @Override
                    public void accept(GroupConfig config) throws Exception {
                        if (config != null) {
                            boolean isExited;
                            if (config.getIsExit() == 1) {
                                isExited = true;
                            } else {
                                isExited = false;
                            }
                            boolean forbid = false;
                            if (mViewModel.groupInfo != null) {
                                forbid = mViewModel.groupInfo.getStat() == ChatEnum.EGroupStatus.BANED;
                            }
                            setBanView(isExited, forbid);
                        }
                    }
                });

    }

    /*
     * 是否已经退出,是否被封
     * */
    private void setBanView(boolean isExited, boolean isForbid) {
        if (isExited || isForbid) {
            // 关闭软键盘
            InputUtil.hideKeyboard(editChat);
        }
        actionbar.getBtnRight().setVisibility(isExited || isForbid ? View.GONE : View.VISIBLE);
        tvBan.setVisibility(isExited || isForbid ? VISIBLE : GONE);
        if (isExited) {
            tvBan.setText("你已经被移除群聊，无法发送消息");
        } else if (isForbid) {
            tvBan.setText(AppConfig.getString(R.string.group_forbid));
        }
        viewChatBottomc.setVisibility(isExited || isForbid ? GONE : VISIBLE);
        llMore.setVisibility(GONE);
    }

    /*
     * 显示或取消多选
     * */
    private void showViewMore(boolean b) {
        if (b) {
            tvBan.setVisibility(GONE);
            viewChatBottomc.setVisibility(GONE);
            llMore.setVisibility(VISIBLE);
        } else {
            tvBan.setVisibility(GONE);
            viewChatBottomc.setVisibility(VISIBLE);
            llMore.setVisibility(GONE);
        }
    }


    /***
     * 发红包
     */
    private void taskPayRb() {
        IUser info = UserAction.getMyInfo();
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
                        Group group = msgDao.getGroup4Id(toGid);
                        int totalSize = 0;
                        if (group != null && group.getUsers() != null) {
                            totalSize = group.getUsers().size();
                        }
                        JrmfRpClient.sendGroupEnvelopeForResult(ChatActivity.this, "" + toGid, "" + UserAction.getMyId(), token,
                                totalSize, info.getName(), info.getHead(), REQ_RP);
                    } else {
                        JrmfRpClient.sendSingleEnvelopeForResult(ChatActivity.this, "" + toUId, "" + info.getUid(), token,
                                info.getName(), info.getHead(), REQ_RP);
                    }
                    LogUtil.writeEnvelopeLog("准备发红包");

                }
            }
        });
    }

    /***
     * 红包收
     */
    private void taskPayRbGet(final MsgAllBean msgbean, final Long toUId, final String rbid) {
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
                                MsgAllBean msgAllbean = SocketData.send4RbRev(toUId, toGid, rbid, MsgBean.RedEnvelopeType.MFPAY_VALUE);
                                showSendObj(msgAllbean);
                                /********通知更新sessionDetail************************************/
                                //因为msg对象 uid有两个，都得添加
                                List<String> gids = new ArrayList<>();
                                List<Long> uids = new ArrayList<>();
                                //gid存在时，不取uid
                                if (TextUtils.isEmpty(msgAllbean.getGid())) {
                                    uids.add(msgAllbean.getTo_uid());
                                    uids.add(msgAllbean.getFrom_uid());
                                } else {
                                    gids.add(msgAllbean.getGid());
                                }
                                //回主线程调用更新session详情
                                if (MyAppLication.INSTANCE().repository != null)
                                    MyAppLication.INSTANCE().repository.updateSessionDetail(gids, uids);
                                /********通知更新sessionDetail end************************************/
                                taskPayRbCheck(msgbean, rbid, MsgBean.RedEnvelopeType.MFPAY_VALUE, "", PayEnum.EEnvelopeStatus.RECEIVED);
                            }
                            if (envelopeStatus == 2 || envelopeStatus == 3) {
                                taskPayRbCheck(msgbean, rbid, MsgBean.RedEnvelopeType.MFPAY_VALUE, "", PayEnum.EEnvelopeStatus.RECEIVED);
                            }
                        }
                    };
                    if (!isActivityValid()) {
                        return;
                    }
                    if (isGroup()) {
                        IUser minfo = UserAction.getMyInfo();
                        JrmfRpClient.openGroupRp(ChatActivity.this, "" + minfo.getUid(), token,
                                minfo.getName(), minfo.getHead(), rbid, callBack);
                    } else {
                        IUser minfo = UserAction.getMyInfo();
                        JrmfRpClient.openSingleRp(ChatActivity.this, "" + minfo.getUid(), token,
                                minfo.getName(), minfo.getHead(), rbid, callBack);
                    }

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
                    if (!isActivityValid()) {
                        return;
                    }
                    SignatureBean sign = response.body().getData();
                    String token = sign.getSign();
                    IUser minfo = UserAction.getMyInfo();
                    JrmfRpClient.openRpDetail(ChatActivity.this, "" + minfo.getUid(), token, rid, minfo.getName(), minfo.getHead());
                }
            }
        });

    }

    /***
     * 红包是否已经被抢,红包改为失效
     * @param rid
     */
    private void taskPayRbCheck(MsgAllBean msgAllBean, String rid, int reType, String token,
                                int envelopeStatus) {
        if (envelopeStatus != PayEnum.EEnvelopeStatus.NORMAL) {
            msgAllBean.getRed_envelope().setIsInvalid(1);
            msgAllBean.getRed_envelope().setEnvelopStatus(envelopeStatus);
        }
        if (!TextUtils.isEmpty(token)) {
            msgAllBean.getRed_envelope().setAccessToken(token);
        }
        msgDao.redEnvelopeOpen(rid, envelopeStatus, reType, token);
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                replaceListDataAndNotify(msgAllBean);
            }
        });
    }

    //抢红包后，更新红包token
    private void updateEnvelopeToken(MsgAllBean msgAllBean, final String rid, int reType, String
            token, int envelopeStatus) {
        if (!TextUtils.isEmpty(token)) {
            msgAllBean.getRed_envelope().setAccessToken(token);
            msgAllBean.getRed_envelope().setEnvelopStatus(envelopeStatus);
        }
        msgDao.redEnvelopeOpen(rid, envelopeStatus, reType, token);
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                replaceListDataAndNotify(msgAllBean);
            }
        });
    }


    /***
     * 获取群信息
     */
    private void taskGroupInfo() {
        if (!isGroup()) {
            return;
        }
        msgAction.groupInfo(toGid, true, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (mViewModel.groupInfo == null)
                    mViewModel.loadData(groupInfoChangeListener, userInfoChangeListener);
            }

            @Override
            public void onFailure(Call<ReturnBean<Group>> call, Throwable t) {
            }
        });
    }

    /**
     * 发请求->获取部分好友信息
     */
    private void httpGetUserInfo() {
        if (uidList == null) {
            uidList = new ArrayList<>();
            uidList.add(toUId + "");
        }
        msgAction.getUserInfo(new Gson().toJson(uidList), new CallBack<ReturnBean<List<UserInfo>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<UserInfo>>> call, Response<ReturnBean<List<UserInfo>>> response) {
                if (response.body() == null) {
                    return;
                } else {
                    if (response.body().isOk() && response.body().getData() != null && response.body().getData().size() > 0) {
                        List<UserInfo> userInfoList = new ArrayList<>();
                        userInfoList.addAll(response.body().getData());
                        if (userInfoList.get(0) != null) {
                            UserInfo userInfo = userInfoList.get(0);
                            userInfo.setuType(ChatEnum.EUserType.FRIEND);//TODO 记得设置类型为好友
                            userInfo.toTag();
                            userDao.updateUserinfo(userInfo);//本地更新对方数据
                        }
                    }
                }
                ToastUtil.show(getContext(), response.body().getMsg());
            }

            @Override
            public void onFailure(Call<ReturnBean<List<UserInfo>>> call, Throwable t) {
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
            if (mtListView != null) {
                int size = mAdapter.getItemCount();
                if (lastPosition >= size - 3) {
                    return true;
                }
            }
        }

        return false;
    }

    private void sendHypertext(List<String> list, int position) {
        if (position == list.size() - 1) {
            isSendingHypertext = false;
        }
        textPosition = position;
        ChatMessage message = SocketData.createChatMessage(SocketData.getUUID(), list.get(position));
        sendMessage(message, ChatEnum.EMessageType.TEXT);
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


    /**
     * 设置单聊阅后即焚时间
     */
    private void taskSurvivalTime(long friend, int survivalTime) {
        msgAction.setSurvivalTime(friend, survivalTime, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    ChatActivity.this.survivaltime = survivalTime;
                    userDao.updateReadDestroy(friend, survivalTime);
                    msgDao.noteMsgAddSurvivaltime(toUId, null);
                }
            }
        });
    }

    /**
     * 设置群聊阅后即焚时间
     */
    private void changeSurvivalTime(String gid, int survivalTime) {
        msgAction.changeSurvivalTime(gid, survivalTime, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    ChatActivity.this.survivaltime = survivalTime;
                    userDao.updateGroupReadDestroy(gid, survivalTime);
                    msgDao.noteMsgAddSurvivaltime(mViewModel.groupInfo.getUsers().get(0).getUid(), gid);
                } else {
                    ToastUtil.show(response.body().getMsg());
                }
            }
        });
    }

    /*
     * 发送消息前，需要检测网络连接状态，网络不可用，不能发送
     * 每条消息发送前，需要检测，语音和小视频录制之前，仍需要检测
     * type=0 默认提示 type=1 仅获取断网状态/不提示
     * */
    public boolean checkNetConnectStatus(int type) {
        boolean isOk;
        if (!NetUtil.isNetworkConnected()) {
            if(type==0){
                ToastUtil.show(this, "网络连接不可用，请稍后重试");
            }
            isOk = false;
        } else {
            isOk = SocketUtil.getSocketUtil().getOnLineState();
            if (!isOk) {
                if(type==0){
                    ToastUtil.show(this, "连接已断开，请稍后再试");
                }
            }
        }
        return isOk;
    }

    //抢红包，获取token
    public void grabRedEnvelope(MsgAllBean msgBean, long rid, int reType) {
        PayHttpUtils.getInstance().grabRedEnvelope(rid)
                .compose(RxSchedulers.<BaseResponse<GrabEnvelopeBean>>compose())
                .compose(RxSchedulers.<BaseResponse<GrabEnvelopeBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<GrabEnvelopeBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<GrabEnvelopeBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            GrabEnvelopeBean bean = baseResponse.getData();
                            if (bean != null) {
                                int status = getGrabEnvelopeStatus(bean.getStat());
                                updateEnvelopeToken(msgBean, rid + "", reType, bean.getAccessToken(), status);
                                showEnvelopeDialog(bean.getAccessToken(), status, msgBean, reType);
//                                if (bean.getStat() == 1) {//1 未领取
//                                    showEnvelopeDialog(bean.getAccessToken(), bean.getStat(), msgBean, reType);
//                                } else {
//
//                                }
                            }
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        if (baseResponse.getCode() == -21000) {
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }
                    }
                });
    }

    //获取抢红包后，红包状态
    private int getGrabEnvelopeStatus(int stat) {
        int status = PayEnum.EEnvelopeStatus.NORMAL;
        if (stat == 1) {//1 未领取
            status = PayEnum.EEnvelopeStatus.NORMAL;
        } else if (stat == 2) {//已领完
            status = PayEnum.EEnvelopeStatus.RECEIVED_FINISHED;
        } else if (stat == 3) {//已过期
            status = PayEnum.EEnvelopeStatus.PAST;
        } else if (stat == 4) {//领到
            status = PayEnum.EEnvelopeStatus.RECEIVED;
        }
        return status;
    }

    //获取拆红包后，红包状态
    private int getOpenEnvelopeStatus(int stat) {
        int status = PayEnum.EEnvelopeStatus.RECEIVED;
        if (stat == 1) {//1 领取
            status = PayEnum.EEnvelopeStatus.RECEIVED;
        } else if (stat == 2) {//已领完
            status = PayEnum.EEnvelopeStatus.RECEIVED_FINISHED;
        } else if (stat == 3) {//已过期
            status = PayEnum.EEnvelopeStatus.PAST;
        } else if (stat == 4) {//领到
            status = PayEnum.EEnvelopeStatus.RECEIVED;
        }
        return status;
    }

    private void showEnvelopeDialog(String token, int status, MsgAllBean msgBean, int reType) {
        DialogEnvelope dialogEnvelope = new DialogEnvelope(ChatActivity.this, com.hm.cxpay.R.style.MyDialogTheme);
        dialogEnvelope.setEnvelopeListener(new DialogEnvelope.IEnvelopeListener() {
            @Override
            public void onOpen(long rid, int envelopeStatus) {
                //TODO: 开红包后，先发送领取红包消息给服务端，然后更新红包状态，最后保存领取红包通知消息到本地
                taskPayRbCheck(msgBean, rid + "", reType, token, getOpenEnvelopeStatus(envelopeStatus));
                if (envelopeStatus == 1) {//抢到了
                    if (!msgBean.isMe()) {
                        SocketData.sendReceivedEnvelopeMsg(msgBean.getFrom_uid(), toGid, rid + "", reType);//发送抢红包消息
                    }
                    MsgNotice message = SocketData.createMsgNoticeOfRb(SocketData.getUUID(), msgBean.getFrom_uid(), toGid, rid + "");
                    sendMessage(message, ChatEnum.EMessageType.NOTICE, false);
                }
            }

            @Override
            public void viewRecord(long rid, String token, int style) {
                getRedEnvelopeDetail(msgBean, rid, token, reType, style == 0);
            }
        });
        RedEnvelopeMessage message = msgBean.getRed_envelope();
        dialogEnvelope.setInfo(token, status, msgBean.getFrom_avatar(), msgBean.getFrom_nickname(), getEnvelopeId(message.getId(), message.getTraceId()), message.getComment(), message.getStyle());
        dialogEnvelope.show();
    }

    //获取红包详情
    public void getRedEnvelopeDetail(MsgAllBean msgBean, long rid, String token, int reType,
                                     boolean isNormalStyle) {
        if (TextUtils.isEmpty(token)) {
            PayHttpUtils.getInstance().grabRedEnvelope(rid)
                    .compose(RxSchedulers.<BaseResponse<GrabEnvelopeBean>>compose())
                    .compose(RxSchedulers.<BaseResponse<GrabEnvelopeBean>>handleResult())
                    .subscribe(new FGObserver<BaseResponse<GrabEnvelopeBean>>() {
                        @Override
                        public void onHandleSuccess(BaseResponse<GrabEnvelopeBean> baseResponse) {
                            if (baseResponse.isSuccess()) {
                                GrabEnvelopeBean bean = baseResponse.getData();
                                if (bean != null) {
                                    if (isNormalStyle) {//普通玩法红包需要保存
                                        taskPayRbCheck(msgBean, rid + "", reType, bean.getAccessToken(), PayEnum.EEnvelopeStatus.NORMAL);
                                    }
                                    getEnvelopeDetail(rid, token, msgBean.getRed_envelope().getEnvelopStatus());
                                }
                            } else {
                                ToastUtil.show(getContext(), baseResponse.getMessage());
                            }
                        }

                        @Override
                        public void onHandleError(BaseResponse baseResponse) {
                            if (baseResponse.getCode() == -21000) {
                            } else {
                                ToastUtil.show(getContext(), baseResponse.getMessage());
                            }
                        }
                    });
        } else {
            getEnvelopeDetail(rid, token, msgBean.getRed_envelope().getEnvelopStatus());
        }
    }

    private void getEnvelopeDetail(long rid, String token, int envelopeStatus) {
        PayHttpUtils.getInstance().getEnvelopeDetail(rid, token, 0)
                .compose(RxSchedulers.<BaseResponse<EnvelopeDetailBean>>compose())
                .compose(RxSchedulers.<BaseResponse<EnvelopeDetailBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<EnvelopeDetailBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<EnvelopeDetailBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            EnvelopeDetailBean bean = baseResponse.getData();
                            if (bean != null) {
                                bean.setChatType(isGroup() ? 1 : 0);
                                bean.setEnvelopeStatus(envelopeStatus);
                                Intent intent = SingleRedPacketDetailsActivity.newIntent(ChatActivity.this, bean);
                                startActivity(intent);
                            }
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        if (baseResponse.getCode() == -21000) {
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }
                    }
                });
    }

    public long getEnvelopeId(String rid, long tradeId) {
        long result = tradeId;
        if (tradeId == 0 && !TextUtils.isEmpty(rid)) {
            try {
                result = Long.parseLong(rid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 实名认证提示弹框
     */
    private void showIdentifyDialog() {
        DialogDefault dialogIdentify = new DialogDefault(this, R.style.MyDialogTheme);
        dialogIdentify
                .setTitleAndSure(true, true)
                .setTitle("温馨提示")
                .setContent("根据国家法律法规要求，你需要进行身份认证后，才能继续使用该功能。", true)
                .setLeft("取消")
                .setRight("去认证")
                .setListener(new DialogDefault.IDialogListener() {
                    @Override
                    public void onSure() {
                        startActivity(new Intent(context, ServiceAgreementActivity.class));

                    }

                    @Override
                    public void onCancel() {

                    }
                });
        dialogIdentify.show();
    }

    /**
     * 手机认证提示弹框
     */
    private void showBindPhoneDialog() {
        DialogDefault dialogIdentify = new DialogDefault(this, R.style.MyDialogTheme);
        dialogIdentify
                .setTitleAndSure(false, true)
                .setTitle("温馨提示")
                .setContent("发红包之前，必须完成手机认证", true)
                .setLeft("取消")
                .setRight("去认证")
                .setListener(new DialogDefault.IDialogListener() {
                    @Override
                    public void onSure() {
                        startActivity(new Intent(ChatActivity.this, BindPhoneNumActivity.class));
                    }

                    @Override
                    public void onCancel() {

                    }
                });
        dialogIdentify.show();
    }

    public void showSettingPswDialog() {
        DialogDefault dialogSettingPayPsw = new DialogDefault(this, R.style.MyDialogTheme);
        dialogSettingPayPsw
                .setTitleAndSure(false, false)
                .setTitle("温馨提示")
                .setContent("您还没有设置支付密码，请设置支付密码后在进行操作", true)
                .setLeft("设置支付密码")
                .setRight("取消")
                .setListener(new DialogDefault.IDialogListener() {
                    @Override
                    public void onSure() {
                        startActivity(new Intent(ChatActivity.this, SetPaywordActivity.class));

                    }

                    @Override
                    public void onCancel() {

                    }
                });
        dialogSettingPayPsw.show();

    }

    /**
     * 获取账单详情
     */
    private void httpGetTransferDetail(String tradeId, int opType, MsgAllBean msgBean) {
        PayHttpUtils.getInstance().getTransferDetail(tradeId)
                .compose(RxSchedulers.<BaseResponse<TransferDetailBean>>compose())
                .compose(RxSchedulers.<BaseResponse<TransferDetailBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<TransferDetailBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<TransferDetailBean> baseResponse) {
                        if (baseResponse.getData() != null) {
                            dismissLoadingDialog();
                            //如果当前页有数据
                            TransferDetailBean detailBean = baseResponse.getData();
                            Intent intent;
                            if (opType == PayEnum.ETransferOpType.TRANS_SEND) {
                                intent = TransferDetailActivity.newIntent(ChatActivity.this, detailBean, tradeId, msgBean.isMe(), GsonUtils.optObject(msgBean));
                            } else {
                                intent = TransferDetailActivity.newIntent(ChatActivity.this, detailBean, tradeId, msgBean.isMe());
                            }
                            startActivity(intent);
                        } else {

                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse<TransferDetailBean> baseResponse) {
                        dismissLoadingDialog();
                        ToastUtil.show(context, baseResponse.getMessage());
                    }
                });
    }

    private void checkHasEnvelopeSendFailed() {
        EnvelopeInfo envelopeInfo = msgDao.queryEnvelopeInfo(toGid, toUId == null ? 0 : toUId.longValue());
        if (envelopeInfo != null) {
            long createTime = envelopeInfo.getCreateTime();
            if (createTime - System.currentTimeMillis() >= TimeToString.DAY) {//超过24小时
                showEnvelopePastDialog(envelopeInfo);
                deleteEnvelopInfo(envelopeInfo);
            } else {
                // TODO 处理#50702 android.view.WindowManager$BadTokenException
                if (!isFinishing()) {
                    showSendEnvelopeDialog(envelopeInfo);
                }
            }
        }
    }


    private void showSendEnvelopeDialog(EnvelopeInfo info) {
        DialogCommon dialogCommon = new DialogCommon(this);
        dialogCommon.setCanceledOnTouchOutside(false);
        String time = TimeToString.getEnvelopeTime(info.getCreateTime());
        String money = info.getAmount() * 1.00 / 100 + "元";
        String content = "您有一个" + time + " 金额为" + money + "的红包已扣款未发送成功,是否重新发送此红包？";
        dialogCommon.setTitleAndSure(true, true)
                .setTitle("温馨提示")
                .setContent(content, false)
                .setLeft("取消发送")
                .setRight("重发红包")
                .setListener(new DialogCommon.IDialogListener() {
                    @Override
                    public void onSure() {
                        RedEnvelopeMessage message = null;
                        deleteEnvelopInfo(info);
                        if (info.getReType() == 0) {
                            message = SocketData.createRbMessage(SocketData.getUUID(), info.getRid(), info.getComment(), info.getReType(), info.getEnvelopeStyle());
                        } else {
//                            message = SocketData.creat(SocketData.getUUID(),info.getRid(),info.getComment(),info.getReType(),info.getEnvelopeStyle());
                        }
                        if (message != null) {
                            sendMessage(message, ChatEnum.EMessageType.RED_ENVELOPE);
                        }
                    }

                    @Override
                    public void onCancel() {
                        deleteEnvelopInfo(info);
                    }
                });
        dialogCommon.show();
    }

    private void showEnvelopePastDialog(EnvelopeInfo info) {
        DialogEnvelopePast dialogCommon = new DialogEnvelopePast(this);
        dialogCommon.setCanceledOnTouchOutside(false);
        String time = TimeToString.MM_DD_HH_MM2(info.getCreateTime());
        String money = info.getAmount() * 1.00 / 100 + "元";
        String content = "您有一个" + time + " 金额为" + money + "的红包未发送成功。已自动退回云红包账户";
        dialogCommon.setContent(content)
                .setListener(new DialogEnvelopePast.IDialogListener() {
                    @Override
                    public void onSure() {
                    }

                    @Override
                    public void onCancel() {

                    }
                });
        dialogCommon.show();
    }

    private void saveMFEnvelope(EnvelopeBean bean) {
        EnvelopeInfo envelopeInfo = new EnvelopeInfo();
        envelopeInfo.setRid(bean.getEnvelopesID());
        envelopeInfo.setAmount(StringUtil.getYuanToLong(bean.getEnvelopeAmount()));
        envelopeInfo.setComment(bean.getEnvelopeMessage());
        envelopeInfo.setReType(0);//0 MF  1 SYS
        MsgBean.RedEnvelopeMessage.RedEnvelopeStyle style = MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.NORMAL;
        if (bean.getEnvelopeType() == 1) {//拼手气
            style = MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.LUCK;
        }
        envelopeInfo.setEnvelopeStyle(style.getNumber());
        envelopeInfo.setCreateTime(System.currentTimeMillis());
        envelopeInfo.setGid(toGid);
        envelopeInfo.setUid(toUId == null ? 0 : toUId.longValue());
        envelopeInfo.setSendStatus(0);
        envelopeInfo.setSign("");
        msgDao.updateEnvelopeInfo(envelopeInfo);
    }

    //删除临时红包信息
    private void deleteEnvelopInfo(EnvelopeInfo envelopeInfo) {
        msgDao.deleteEnvelopeInfo(envelopeInfo.getRid(), toGid, toUId, true);
        MsgAllBean lastMsg = null;
        if (mAdapter != null && mAdapter.getItemCount() > 0) {
            int len = mAdapter.getItemCount();
            lastMsg = mAdapter.getMessage(len - 1);
        }
    }

    /**
     * 注册截屏监听
     */
    private void initScreenShotListener() {
        if (screenShotListenManager != null) {
            screenShotListenManager.startListen();
            return;
        }
        screenShotListenManager = ScreenShotListenManager.newInstance(ChatActivity.this);
        screenShotListenManager.setListener(
                new ScreenShotListenManager.OnScreenShotListener() {
                    public void onShot(String imagePath) {
                        LogUtil.getLog().i(TAG, "截屏--回调了--onShot" + "GID=" + toGid + "--UID=" + toUId);
                        if (checkSnapshotPower()) {
                            if (isGroup()) {
                                SocketData.sendSnapshotMsg(null, toGid);
                            } else {
                                SocketData.sendSnapshotMsg(toUId, null);
                            }
                            MsgNotice notice = SocketData.createMsgNoticeOfSnapshot(SocketData.getUUID());
                            sendMessage(notice, ChatEnum.EMessageType.NOTICE, false);
                        }
                    }
                }
        );

        if (Build.VERSION.SDK_INT > 22) {
            List<String> permissionList = new ArrayList<>();
            // 检查权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                // 开始截图监听
                screenShotListenManager.startListen();
            }
            if (permissionList != null && (permissionList.size() != 0)) {
                ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 0);
            }
        } else {
            // 开始截图监听
            screenShotListenManager.startListen();
        }
    }

    //选择文件(限制最大5个，与网络请求并发数一致)
    private void toSelectFile() {
        FilePickerManager.INSTANCE
                .from(this)
                .maxSelectable(5)
                .forResult(FilePickerManager.REQUEST_CODE);
    }


    //删除单条消息
    private void deleteMsg(MsgAllBean bean) {
        if (mAdapter == null) {
            return;
        }
        int position = mAdapter.getPosition(bean);
        if (position < 0) {
            return;
        }
        if (bean.getMsg_type() == ChatEnum.EMessageType.VOICE) {
            AudioPlayManager.getInstance().stopPlay();
        }
        mAdapter.removeItem(bean);
        mtListView.getListView().getAdapter().notifyItemRemoved(position);//删除刷新
        removeUnreadCount(1);
        fixLastPosition(-1);
    }

    private void removeUnreadCount(int num) {
        if (unreadCount > 0) {
            unreadCount = unreadCount - num;
            if (unreadCount > 0) {
//                viewNewMessage.setCount(unreadCount);
                mAdapter.setUnreadCount(unreadCount);
            } else {
                viewNewMessage.setVisible(false);
                unreadCount = 0;
                mAdapter.setUnreadCount(0);
            }
        }
    }

    //删除单条消息
    private void deleteMsgList(List<MsgAllBean> list) {
        if (mAdapter == null || mAdapter.getItemCount() <= 0 || list == null) {
            return;
        }
        mAdapter.removeMsgList(list);
        removeUnreadCount(list.size());
        notifyData();
        //有面板，则滑到底部
        if (mViewModel.isInputText.getValue() || mViewModel.isOpenEmoj.getValue() || mViewModel.isOpenFuction.getValue()) {
            mtListView.scrollToEnd();
        }
    }


    /**
     * 选择已有程序打开文件
     *
     * @param filepath
     * @备注 todo 暂时只有2个地方用到，后续如果用的地方较多，再抽取到工具类FileUtil
     */
    public void openAndroidFile(String filepath) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            File file = new File(filepath);
            Uri uri = null;
            // 7.0行为变更适配，加上文件权限，通过FileProvider在应用中共享文件
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".app", file);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(file);
            }
            intent.setDataAndType(uri, net.cb.cb.library.utils.FileUtils.getMIMEType(file));//设置类型
            if (context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(context, "不支持打开此类型文件，请下载相关软件", Toast.LENGTH_SHORT).show();
            }
//            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastUtil.show("附件不能打开，请下载相关软件");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取单个群成员信息
     */
    private void getSingleMemberInfo(MsgAllBean reMsg) {
        new UserAction().getSingleMemberInfo(toGid, Integer.parseInt(UserAction.getMyId() + ""), new CallBack<ReturnBean<SingleMeberInfoBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SingleMeberInfoBean>> call, Response<ReturnBean<SingleMeberInfoBean>> response) {
                super.onResponse(call, response);
                if (response != null && response.body() != null && response.body().isOk()) {
                    singleMeberInfoBean = response.body().getData();
                    //1 是否被单人禁言
                    if (singleMeberInfoBean.getShutUpDuration() == 0) {
                        //2 该群是否全员禁言
                        if (mViewModel.groupInfo != null && mViewModel.groupInfo.getWordsNotAllowed() == 0) {
                            resendFileMsg(reMsg);
                        } else {
                            ToastUtil.showCenter(ChatActivity.this, "本群全员禁言中");
                        }
                    } else {
                        ToastUtil.showCenter(ChatActivity.this, "你已被禁言，暂时无法发送文件");
                    }
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<SingleMeberInfoBean>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show(ChatActivity.this, t.getMessage());
                //2 该群是否全员禁言
                if (mViewModel.groupInfo.getWordsNotAllowed() == 0) {
                    toSelectFile();
                } else {
                    ToastUtil.show("全员禁言中，无法发送文件消息!");
                }
            }
        });
    }

    private void showForwardDialog() {
        ForwardDialog forwardDialog = new ForwardDialog(this);
        forwardDialog.setCancelable(true);
        forwardDialog.setListener(new ForwardDialog.IForwardListener() {
            @Override
            public void onOneForward() {
                List<MsgAllBean> list = mAdapter.getSelectedMsg();
                if (list != null) {
                    int len = list.size();
                    if (len > 0) {
                        onForwardActivity(ChatEnum.EForwardMode.ONE_BY_ONE, new Gson().toJson(list));
                    }
                }
            }

            @Override
            public void onMergeForward() {
                List<MsgAllBean> list = mAdapter.getSelectedMsg();
                if (list != null) {
                    int len = list.size();
                    if (len > 0) {
                        onForwardActivity(ChatEnum.EForwardMode.MERGE, new Gson().toJson(list));
                    }
                }
            }

            @Override
            public void onCancel() {

            }
        });
        forwardDialog.show();
    }

    /**
     * 多选转发
     */
    private void onForwardActivity(@ChatEnum.EForwardMode int model, String json) {
        if (TextUtils.isEmpty(json)) {
            return;
        }
        Intent intent = MsgForwardActivity.newIntent(this, model, json);
        startActivity(intent);
        startActivity(new Intent(getContext(), MsgForwardActivity.class));

    }


    /**
     * 发送文件
     *
     * @param reMsg
     */
    private void resendFileMsg(MsgAllBean reMsg) {
        //文件仍然存在，则重发
        if (net.cb.cb.library.utils.FileUtils.fileIsExist(reMsg.getSendFileMessage().getLocalPath())) {
            SendFileMessage fileMessage = SocketData.createFileMessage(reMsg.getMsg_id(), reMsg.getSendFileMessage().getLocalPath(), reMsg.getSendFileMessage().getUrl(), reMsg.getSendFileMessage().getFile_name(), reMsg.getSendFileMessage().getSize(), reMsg.getSendFileMessage().getFormat(), false);
            MsgAllBean fileMsgBean = sendMessageFromResend(fileMessage, ChatEnum.EMessageType.FILE, false);
            replaceListDataAndNotify(fileMsgBean);
            // 若不为常信小助手，消息需要上传到服务端
            if (!Constants.CX_HELPER_UID.equals(toUId)) {
                UpLoadService.onAddFile(this.context, fileMsgBean);
                startService(new Intent(getContext(), UpLoadService.class));
            } else {
                //若为常信小助手，不存服务器，只走本地数据库保存，发送状态直接重置为正常，更新数据库
                msgDao.fixStataMsg(reMsg.getMsg_id(), ChatEnum.ESendStatus.NORMAL);
            }
        } else {
            ToastUtil.show("文件不存在或已被删除");
        }
    }

    @Override
    public void onEvent(int type, MsgAllBean message, Object... args) {
        if (ViewUtils.isFastDoubleClick()) {
            return;
        }
//        if (mViewModel.isInputText.getValue()) {
//            mViewModel.isInputText.setValue(false);
//        }
        switch (type) {
            case ChatEnum.ECellEventType.TXT_CLICK:
                break;
            case ChatEnum.ECellEventType.IMAGE_CLICK:
                if (args[0] != null && args[0] instanceof ImageMessage) {
                    ImageMessage image = (ImageMessage) args[0];
                    showBigPic(message.getMsg_id(), image.getThumbnailShow());
                }
                break;
            case ChatEnum.ECellEventType.VOICE_CLICK:
                if (AVChatProfile.getInstance().isCallIng() || AVChatProfile.getInstance().isCallEstablished()) {
                    if (AVChatProfile.getInstance().isChatType() == AVChatType.VIDEO.getValue()) {
                        ToastUtil.show(ChatActivity.this, getString(R.string.avchat_peer_busy_video));
                    } else {
                        ToastUtil.show(ChatActivity.this, getString(R.string.avchat_peer_busy_voice));
                    }
                } else {
                    int position = (int) args[1];
                    playVoice(message, position);
                }
                break;
            case ChatEnum.ECellEventType.VIDEO_CLICK:
                clickVideo(message);
                break;
            case ChatEnum.ECellEventType.CARD_CLICK:
                if (args[0] != null && args[0] instanceof BusinessCardMessage) {
                    BusinessCardMessage card = (BusinessCardMessage) args[0];
                    if (card.getUid().longValue() != UserAction.getMyId().longValue()) {
                        if (isGroup() && !master.equals(card.getUid().toString())) {
                            startActivity(new Intent(getContext(), UserInfoActivity.class).putExtra(UserInfoActivity.ID,
                                    card.getUid()).putExtra(UserInfoActivity.IS_BUSINESS_CARD, contactIntimately));
                        } else {
                            startActivity(new Intent(getContext(), UserInfoActivity.class).putExtra(UserInfoActivity.ID, card.getUid()));
                        }
                    }
                }
                break;
            case ChatEnum.ECellEventType.RED_ENVELOPE_CLICK:
                clickEnvelope(message, message.getRed_envelope());
                break;
            case ChatEnum.ECellEventType.LONG_CLICK:
                List<OptionMenu> menus = (List<OptionMenu>) args[0];
                View v = (View) args[1];
                IMenuSelectListener listener = (IMenuSelectListener) args[2];
                showPop(v, menus, message, listener);
                break;
            case ChatEnum.ECellEventType.TRANSFER_CLICK:
                if (args[0] == null) {
                    return;
                }
                TransferMessage transfer = (TransferMessage) args[0];
                UserBean userBean = PayEnvironment.getInstance().getUser();
                if (userBean != null && userBean.getRealNameStat() != 1) {//未认证
                    showIdentifyDialog();
                    return;
                }
                showLoadingDialog();
                httpGetTransferDetail(transfer.getId(), transfer.getOpType(), message);
                break;
            case ChatEnum.ECellEventType.AVATAR_CLICK:
                if (isGroup() && !MessageManager.getInstance().isGroupValid(mViewModel.groupInfo)) {
                    return;
                }
                toUserInfoActivity(message.getFrom_uid());
                break;
            case ChatEnum.ECellEventType.AVATAR_LONG_CLICK:
                if (isGroup()) {
                    if (!MessageManager.getInstance().isGroupValid(mViewModel.groupInfo)) {
                        return;
                    }
                    doAtInput(message);

                    //弹出软键盘
                    if (!mViewModel.isOpenValue()) //没有事件触发，设置改SoftInput模式为：顶起输入框
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    if (!mViewModel.isInputText.getValue())
                        mViewModel.isInputText.setValue(true);
                }
                break;
            case ChatEnum.ECellEventType.VOICE_VIDEO_CALL:
                // 只有Vip才可以视频通话
                IUser userInfo = UserAction.getMyInfo();
                if (userInfo != null && IS_VIP.equals(userInfo.getVip())) {
                    if (message.getP2PAuVideoMessage().getAv_type() == MsgBean.AuVideoType.Audio.getNumber()) {
                        gotoVideoActivity(AVChatType.AUDIO.getValue());
                    } else {
                        gotoVideoActivity(AVChatType.VIDEO.getValue());
                    }
                }
                break;
            case ChatEnum.ECellEventType.BALANCE_ASSISTANT_CLICK:
                if (args[0] == null) {
                    return;
                }
                BalanceAssistantMessage balance = (BalanceAssistantMessage) args[0];
                if (balance.getDetailType() == MsgBean.BalanceAssistantMessage.DetailType.RED_ENVELOPE_VALUE) {//红包详情
                    Intent intent = SingleRedPacketDetailsActivity.newIntent(ChatActivity.this, balance.getTradeId(), 1);
                    startActivity(intent);
                } else if (balance.getDetailType() == MsgBean.BalanceAssistantMessage.DetailType.TRANS_VALUE) {//订单详情
                    BillDetailActivity.jumpToBillDetail(ChatActivity.this, balance.getTradeId() + "");
                }
                break;
            case ChatEnum.ECellEventType.MAP_CLICK:
                LocationActivity.openActivity(ChatActivity.this, true, message);
                break;
            case ChatEnum.ECellEventType.FILE_CLICK:
                if (args[0] == null) {
                    return;
                }
                SendFileMessage fileMessage = (SendFileMessage) args[0];
                clickFile(message, fileMessage);
                break;
            case ChatEnum.ECellEventType.EXPRESS_CLICK:
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                if (args[0] == null) {
                    return;
                }
                String uri = (String) args[0];
                Bundle bundle = new Bundle();
                bundle.putString(Preferences.DATA, uri);
                IntentUtil.gotoActivity(ChatActivity.this, ShowBigFaceActivity.class, bundle);
                break;
            case ChatEnum.ECellEventType.RESEND_CLICK:
                if (isGroup() && !MessageManager.getInstance().isGroupValid(mViewModel.groupInfo)) {
                    ToastUtil.show(this, "该群已被封，不能重发");
                    return;
                }
                resendMessage(message);
                break;
            case ChatEnum.ECellEventType.REPLY_CLICK:
                if (args[0] != null && args[0] instanceof QuotedMessage) {
                    QuotedMessage quotedMessage = (QuotedMessage) args[0];
                    MsgAllBean bean = msgDao.getMsgById(quotedMessage.getMsgId());
                    if (bean != null && mAdapter != null) {
                        int position = mAdapter.getPosition(bean);
                        if (position >= 0) {
                            scrollChatToPosition(position);
                        } else {
                            ToastUtil.show("消息不存在");
                        }
                    } else {
                        ToastUtil.show("消息不存在");
                    }
                }
                break;
            case ChatEnum.ECellEventType.WEB_CLICK:
                if (args[0] != null && args[0] instanceof WebMessage) {
                    WebMessage webMessage = (WebMessage) args[0];
                    Intent intent = new Intent(ChatActivity.this, WebPageActivity.class);
                    intent.putExtra(WebPageActivity.AGM_URL, webMessage.getWebUrl());
                    startActivity(intent);
                }
                break;
            case ChatEnum.ECellEventType.AD_CLICK:
                if (args[0] != null && args[0] instanceof AdMessage) {
                    AdMessage adMessage = (AdMessage) args[0];
                    if (!TextUtils.isEmpty(adMessage.getAppId())) {
                        if (!TextUtils.isEmpty(adMessage.getWebUrl()) && !ApkUtils.isApkInstalled(ChatActivity.this, adMessage.getAppId())) {
                            showDownloadAppDialog(adMessage.getWebUrl());
                        } else if (ApkUtils.isApkInstalled(ChatActivity.this, adMessage.getAppId())) {
                            ApkUtils.startSchemeApp(ChatActivity.this, adMessage.getAppId(), "");
                        } else {
                            if (!TextUtils.isEmpty(adMessage.getSchemeUrl())) {
                                ApkUtils.startSchemeApp(ChatActivity.this, adMessage.getAppId(), adMessage.getSchemeUrl());
                            }
                        }
                    } else if (!TextUtils.isEmpty(adMessage.getWebUrl())) {
                        goBrowsable(adMessage.getWebUrl());
                    }
                }
                break;
            case ChatEnum.ECellEventType.MULTI_CLICK:
                break;
        }

    }

    @Override
    public ChatCellBase getChatCellBase(int position) {
        return mtListView.getListView().findViewHolderForAdapterPosition(position) == null ? null : ((ChatCellBase) mtListView.getListView().findViewHolderForAdapterPosition(position));
    }

    private void clickFile(MsgAllBean message, SendFileMessage fileMessage) {
        CheckFileMsg(message, fileMessage);
    }

    private void doAtInput(MsgAllBean message) {
        String name = msgDao.getGroupMemberName(toGid, message.getFrom_uid(), null, null);
        String txt = editChat.getText().toString().trim();
        if (!txt.contains("@" + name)) {
            if (!TextUtils.isEmpty(name)) {
                editChat.addAtSpan("@", name, message.getFrom_uid());
            } else {
                name = TextUtils.isEmpty(message.getFrom_group_nickname()) ? message.getFrom_nickname() : message.getFrom_group_nickname();
                editChat.addAtSpan("@", name, message.getFrom_uid());
            }
            mtListView.scrollToEnd();
        }
    }

    private void clickVideo(MsgAllBean msg) {
        if (AVChatProfile.getInstance().isCallIng() || AVChatProfile.getInstance().isCallEstablished()) {
            if (AVChatProfile.getInstance().isChatType() == AVChatType.VIDEO.getValue()) {
                ToastUtil.show(ChatActivity.this, getString(R.string.avchat_peer_busy_video));
            } else {
                ToastUtil.show(ChatActivity.this, getString(R.string.avchat_peer_busy_voice));
            }
        } else if (clickAble) {
            clickAble = false;
            String localUrl = msg.getVideoMessage().getLocalUrl();
            if (StringUtil.isNotNull(localUrl)) {
                File file = new File(localUrl);
                if (!file.exists()) {
                    localUrl = msg.getVideoMessage().getUrl();
                }
            } else {
                localUrl = msg.getVideoMessage().getUrl();
            }
            //发送状态正常，且未开启阅后即焚，则允许收藏
            boolean canCollect = false;
            if (msg.getSend_state() != ChatEnum.ESendStatus.ERROR && msg.getSurvival_time() == 0) {
                canCollect = true;
            }
            Intent intent = new Intent(ChatActivity.this, VideoPlayActivity.class);
            intent.putExtra("videopath", localUrl);
            intent.putExtra("videomsg", new Gson().toJson(msg));
            intent.putExtra("msg_id", msg.getMsg_id());
            intent.putExtra("bg_url", msg.getVideoMessage().getBg_url());
            intent.putExtra("can_collect", canCollect);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);

        }
    }

    private void clickEnvelope(MsgAllBean msg, RedEnvelopeMessage rb) {
        Boolean isInvalid = rb.getIsInvalid() == 0 ? false : true;
        String info = getEnvelopeInfo(rb.getEnvelopStatus());
        if (rb.getEnvelopStatus() == PayEnum.EEnvelopeStatus.PAST) {
            isInvalid = true;
        }
        final String rid = rb.getId();
        final Long touid = msg.getFrom_uid();
        final int style = msg.getRed_envelope().getStyle();
        int reType = rb.getRe_type().intValue();//红包类型
        if (reType == MsgBean.RedEnvelopeType.MFPAY_VALUE) {//魔方红包
            if (isInvalid || (msg.isMe() && style == MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.NORMAL_VALUE)) {//已领取或者是自己的,看详情,"拼手气的话自己也能抢"
                taskPayRbDetail(msg, rid);
            } else {
                if (checkCanOpenUpRedEnv()) {
                    taskPayRbGet(msg, touid, rid);
                } else {
                    ToastUtil.show(ChatActivity.this, "你已被禁止领取该群红包");
                }
            }
        } else if (reType == MsgBean.RedEnvelopeType.SYSTEM_VALUE) {//零钱红包
            UserBean userBean = PayEnvironment.getInstance().getUser();
            if (userBean != null && userBean.getRealNameStat() != 1) {//未认证
                showIdentifyDialog();
                return;
            }
            if (!checkCanOpenUpRedEnv()) {
                ToastUtil.show(ChatActivity.this, "你已被禁止领取该群红包");
                return;
            }
            long tradeId = rb.getTraceId();
            if (tradeId == 0 && !TextUtils.isEmpty(rid)) {
                try {
                    tradeId = Long.parseLong(rid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (tradeId == 0) {
                ToastUtil.show(ChatActivity.this, "无红包id");
                return;
            }
            int envelopeStatus = rb.getEnvelopStatus();
            boolean isNormalStyle = style == MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.NORMAL_VALUE;
            if (envelopeStatus == PayEnum.EEnvelopeStatus.NORMAL) {
                if (msg.isMe() && isNormalStyle) {
                    getRedEnvelopeDetail(msg, tradeId, rb.getAccessToken(), reType, isNormalStyle);
                } else {
                    if (!TextUtils.isEmpty(rb.getAccessToken())) {
                        showEnvelopeDialog(rb.getAccessToken(), envelopeStatus, msg, reType);
                    } else {
                        grabRedEnvelope(msg, tradeId, reType);
                    }
                }
            } else if (envelopeStatus == PayEnum.EEnvelopeStatus.RECEIVED) {
                getRedEnvelopeDetail(msg, tradeId, rb.getAccessToken(), reType, isNormalStyle);
            } else if (envelopeStatus == PayEnum.EEnvelopeStatus.RECEIVED_FINISHED) {
                if (msg.isMe()) {
                    getRedEnvelopeDetail(msg, tradeId, rb.getAccessToken(), reType, isNormalStyle);
                } else {
                    showEnvelopeDialog(rb.getAccessToken(), envelopeStatus, msg, reType);
                }
            } else if (envelopeStatus == PayEnum.EEnvelopeStatus.PAST) {
                if (msg.isMe()) {
                    getRedEnvelopeDetail(msg, tradeId, rb.getAccessToken(), reType, isNormalStyle);
                } else {
                    showEnvelopeDialog(rb.getAccessToken(), envelopeStatus, msg, reType);
                }
            }
        }
    }

    //群聊是否可以cancel
    private boolean isGroupBanCancel() {
        if (isGroup()) {
            if (mViewModel.groupInfo != null && mViewModel.groupInfo.getStat() != ChatEnum.EGroupStatus.NORMAL) {
                return true;
            }
        }
        return false;
    }

    public void showLockDialog() {
        LockDialog lockDialog = new LockDialog(this, R.style.MyDialogNoFadedTheme);
        lockDialog.setCancelable(true);
        lockDialog.setCanceledOnTouchOutside(true);
        lockDialog.create();
        lockDialog.show();
    }


    private boolean hasData() {
        if (mAdapter == null) {
            return false;
        }
        MsgAllBean bean;
        if (isGroup()) {
            bean = msgDao.msgGetLast4Gid(toGid);
        } else {
            bean = msgDao.msgGetLast4FUid(toUId);
        }
        if (bean != null && mAdapter.getPosition(bean) >= 0) {
            return true;
        }
        return false;
    }

    //添加单条消息
    private void addMsg(MsgAllBean bean) {
        if (mAdapter == null) {
            return;
        }
        resetName(bean);
        int position = mAdapter.getItemCount();
        mAdapter.addMessage(bean);
        mtListView.getListView().getAdapter().notifyItemRangeInserted(position, 1);
        fixLastPosition(1);
        scrollListView(false);
    }

    //添加单条消息
    private void addMsg(List<MsgAllBean> list) {
        if (mAdapter == null) {
            return;
        }
        int position = mAdapter.getItemCount();
        mAdapter.addMessageList(position, list);
        mtListView.getListView().getAdapter().notifyItemRangeInserted(position, list.size());//删除刷新
        fixLastPosition(list.size());
        scrollListView(false);
    }

    //是否消息来自当前会话
    public boolean isMsgFromCurrentChat(String gid, Long fromUid) {
        if (!TextUtils.isEmpty(gid)) {
            if (TextUtils.isEmpty(toGid)) {
                return false;
            }
            if (gid.equals(toGid)) {
                return true;
            }
        } else {
            if (fromUid == null || toUId == null) {
                return false;
            }
            if (fromUid.longValue() == toUId.longValue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新底部未读数
     */
    private void updateUnReadCount() {
        LogUtil.getLog().i("未读数", "onChange");
        RealmResults<Session> sessionList = MyAppLication.INSTANCE().getSessions().where().greaterThan("unread_count", 0)
                .limit(100).findAll();
        if (sessionList != null) {
            Number unreadCount = sessionList.where().sum("unread_count");
            if (unreadCount != null) {
                updateMsgUnread(unreadCount.intValue());
            } else {
                updateMsgUnread(0);
            }
        } else {
            updateMsgUnread(0);
        }
    }

    public final void updateMsgUnread(int num) {
        LogUtil.getLog().i("MainActivity", "更新消息未读数据：" + num);
        if (num > 99) {
            actionbar.setTxtLeft("99+", R.drawable.shape_unread_oval_bg, DensityUtil.sp2px(ChatActivity.this, 5));
        } else if (num > 0 && num <= 99) {
            actionbar.setTxtLeft(num + "", R.drawable.shape_unread_bg, DensityUtil.sp2px(ChatActivity.this, 5));
        } else {
            actionbar.setTxtLeft("", R.drawable.shape_unread_bg, DensityUtil.sp2px(ChatActivity.this, 5));
        }
//        BadgeUtil.setBadgeCount(getApplicationContext(), num);
    }


    /**
     * 下载文件 + 打开
     *
     * @param sendFileMessage
     */
    private void DownloadFile(SendFileMessage sendFileMessage) {
        String fileMsgId = "";
        String fileUrl = "";
        String fileName = "";

        //获取文件消息id
        if (!TextUtils.isEmpty(sendFileMessage.getMsgId())) {
            fileMsgId = sendFileMessage.getMsgId();
        }

        //显示文件名
        if (!TextUtils.isEmpty(sendFileMessage.getFile_name())) {
            fileName = sendFileMessage.getFile_name();
            //若有同名文件，则重命名，保存最终真实文件名，如123.txt若有重名则依次保存为123(1).txt 123(2).txt
            //若没有同名文件，则按默认新文件来保存
            fileName = net.cb.cb.library.utils.FileUtils.getFileRename(fileName);
        }

        //获取url，自动开始下载文件，并打开
        if (!TextUtils.isEmpty(sendFileMessage.getUrl())) {
            fileUrl = sendFileMessage.getUrl();
            //指定下载路径文件夹，若不存在则创建
            File fileDir = new File(FileConfig.PATH_DOWNLOAD);
            if (!fileDir.exists()) {
                fileDir.mkdir();
            }
            File file = new File(fileDir, fileName);
            try {
                String finalFileMsgId = fileMsgId;
                String fileNewName = fileName;
                DownloadUtil.get().downLoadFile(fileUrl, file, new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        ToastUtil.showLong(ChatActivity.this, "下载成功! \n文件已保存：" + FileConfig.PATH_DOWNLOAD + "目录下");
                        //下载成功
                        //1 本地数据库刷新：保存一个新增属性-真实文件名，主要用于多个同名文件区分保存，防止重名，方便用户点击打开重名文件
                        MsgAllBean reMsg = DaoUtil.findOne(MsgAllBean.class, "msg_id", finalFileMsgId);
                        reMsg.getSendFileMessage().setRealFileRename(fileNewName);
                        DaoUtil.update(reMsg);
                        //2 列表数据刷新：如出现重名，显示新的名字，方便用户点击打开重名文件
                        sendFileMessage.setRealFileRename(fileNewName);
                        replaceListDataAndNotify(reMsg, true);
                        //直接打开
                        openAndroidFile(FileConfig.PATH_DOWNLOAD + fileNewName);
                    }

                    @Override
                    public void onDownloading(int progress) {

                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        ToastUtil.show("文件下载失败");
                    }
                });

            } catch (Exception e) {
                ToastUtil.show("文件下载失败");
            }
        }
    }

    /**
     * 检查是否显示已读
     */
    private boolean checkIsRead() {
        if (mViewModel.userInfo == null) {
            return false;
        }
        int friendMasterRead = mViewModel.userInfo.getMasterRead();
        int friendRead = mViewModel.userInfo.getFriendRead();
        int myRead = mViewModel.userInfo.getMyRead();

        IUser myUserInfo = UserAction.getMyInfo();
        int masterRead = myUserInfo == null ? 1 : myUserInfo.getMasterRead();
        if (friendMasterRead == 1 && friendRead == 1 && myRead == 1 && masterRead == 1) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 发请求->收藏
     *
     * @param collectionInfo
     */
    private void httpCollect(CollectionInfo collectionInfo) {
        msgAction.collectMsg(collectionInfo.getData(), collectionInfo.getFromUid(), collectionInfo.getFromUsername(),
                collectionInfo.getType(), collectionInfo.getFromGid(), collectionInfo.getFromGroupName(), collectionInfo.getMsgId(),
                new CallBack<ReturnBean>() {
                    @Override
                    public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                        super.onResponse(call, response);
                        if (response.body() == null) {
                            return;
                        }
                        if (response.body().isOk()) {
                            ToastUtil.showToast(ChatActivity.this, "已收藏", 1);
                            msgDao.addLocalCollection(collectionInfo);//添加到本地收藏列表
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnBean> call, Throwable t) {
                        super.onFailure(call, t);
                        ToastUtil.showToast(ChatActivity.this, "收藏失败", 1);
                    }
                });
    }

    private void initLastPosition() {
        if (mtListView != null) {
            lastPosition = ((LinearLayoutManager) mtListView.getListView().getLayoutManager()).findLastVisibleItemPosition();
        }
    }


    private synchronized void fixLastPosition(int len) {
        lastPosition = lastPosition + len;
        LogUtil.getLog().i(TAG, "scroll--fixLastPosition=" + lastPosition);
    }

    //存储正在回复消息,回复内容被content
    private void saveReplying(String content) {
        if (isReplying && replayMsg != null && !TextUtils.isEmpty(content)) {
            Realm realm = DaoUtil.open();
            try {
                //实时从数据库查，再更改，否则影响阅后即焚字段
                MsgAllBean msgAllBean = realm.where(MsgAllBean.class).equalTo("msg_id", replayMsg.getMsg_id()).findFirst();
                realm.beginTransaction();
                msgAllBean.setIsReplying(1);
                realm.commitTransaction();
            } catch (Exception e) {
            } finally {
                DaoUtil.close(realm);
            }
        }
    }

    //发送成功后删除回复消息
    private void updateReplying() {
        if (replayMsg != null) {
            Realm realm = DaoUtil.open();
            try {
//                //实时从数据库查，再更改，否则影响阅后即焚字段
                MsgAllBean msgAllBean = realm.where(MsgAllBean.class).equalTo("msg_id", replayMsg.getMsg_id()).findFirst();
                realm.beginTransaction();
                msgAllBean.setIsReplying(0);
                realm.commitTransaction();
            } catch (Exception e) {
            } finally {
                DaoUtil.close(realm);
            }
        }
    }

    //转换成新的收藏消息结构
    private RealmObject convertCollectBean(int type, MsgAllBean msgAllBean) {
        if (type == ChatEnum.EMessageType.TEXT) {
            CollectChatMessage collectChatMessage = new CollectChatMessage();
            collectChatMessage.setMsgid(msgAllBean.getChat().getMsgId());
            collectChatMessage.setMsg(msgAllBean.getChat().getMsg());
            return collectChatMessage;
        }
        if (type == ChatEnum.EMessageType.IMAGE) {
            CollectImageMessage collectImageMessage = new CollectImageMessage();
            collectImageMessage.setMsgid(msgAllBean.getImage().getMsgId());
            collectImageMessage.setOrigin(msgAllBean.getImage().getOrigin());
            collectImageMessage.setPreview(msgAllBean.getImage().getPreview());
            collectImageMessage.setThumbnail(msgAllBean.getImage().getThumbnail());
            collectImageMessage.setWidth(msgAllBean.getImage().getWidth());
            collectImageMessage.setHeight(msgAllBean.getImage().getHeight());
            collectImageMessage.setSize(msgAllBean.getImage().getSize());
            collectImageMessage.setLocalimg(msgAllBean.getImage().getLocalimg());
            return collectImageMessage;
        }
        if (type == ChatEnum.EMessageType.SHIPPED_EXPRESSION) {
            CollectShippedExpressionMessage collectShippedExpressionMessage = new CollectShippedExpressionMessage();
            collectShippedExpressionMessage.setMsgId(msgAllBean.getShippedExpressionMessage().getMsgId());
            collectShippedExpressionMessage.setExpression(msgAllBean.getShippedExpressionMessage().getId());
            return collectShippedExpressionMessage;
        }
        if (type == ChatEnum.EMessageType.MSG_VIDEO) {
            CollectVideoMessage collectVideoMessage = new CollectVideoMessage();
            collectVideoMessage.setMsgId(msgAllBean.getVideoMessage().getMsgId());
            collectVideoMessage.setVideoDuration(msgAllBean.getVideoMessage().getDuration());
            collectVideoMessage.setVideoBgURL(msgAllBean.getVideoMessage().getBg_url());
            collectVideoMessage.setVideoURL(msgAllBean.getVideoMessage().getUrl());
            collectVideoMessage.setWidth(msgAllBean.getVideoMessage().getWidth());
            collectVideoMessage.setHeight(msgAllBean.getVideoMessage().getHeight());
            collectVideoMessage.setSize(msgAllBean.getVideoMessage().getDuration());//旧消息没有和这个字段
            return collectVideoMessage;
        }
        if (type == ChatEnum.EMessageType.VOICE) {
            CollectVoiceMessage collectVoiceMessage = new CollectVoiceMessage();
            collectVoiceMessage.setMsgId(msgAllBean.getVoiceMessage().getMsgId());
            collectVoiceMessage.setVoiceURL(msgAllBean.getVoiceMessage().getUrl());
            collectVoiceMessage.setVoiceDuration(msgAllBean.getVoiceMessage().getTime());
            collectVoiceMessage.setLocalUrl(msgAllBean.getVoiceMessage().getLocalUrl());
            return collectVoiceMessage;
        }
        if (type == ChatEnum.EMessageType.LOCATION) {
            CollectLocationMessage collectLocationMessage = new CollectLocationMessage();
            collectLocationMessage.setMsgId(msgAllBean.getLocationMessage().getMsgId());
            collectLocationMessage.setLat(msgAllBean.getLocationMessage().getLatitude());
            collectLocationMessage.setLon(msgAllBean.getLocationMessage().getLongitude());
            collectLocationMessage.setAddr(msgAllBean.getLocationMessage().getAddress());
            collectLocationMessage.setAddressDesc(msgAllBean.getLocationMessage().getAddressDescribe());
            collectLocationMessage.setImg(msgAllBean.getLocationMessage().getImg());
            return collectLocationMessage;
        }
        if (type == ChatEnum.EMessageType.AT) {
            CollectAtMessage collectAtMessage = new CollectAtMessage();
            collectAtMessage.setMsgId(msgAllBean.getAtMessage().getMsgId());
            collectAtMessage.setMsg(msgAllBean.getAtMessage().getMsg());
            return collectAtMessage;
        }
        if (type == ChatEnum.EMessageType.FILE) {
            CollectSendFileMessage collectSendFileMessage = new CollectSendFileMessage();
            collectSendFileMessage.setMsgId(msgAllBean.getSendFileMessage().getMsgId());
            collectSendFileMessage.setFileURL(msgAllBean.getSendFileMessage().getUrl());
            collectSendFileMessage.setFileName(msgAllBean.getSendFileMessage().getFile_name());
            collectSendFileMessage.setFileFormat(msgAllBean.getSendFileMessage().getFormat());
            collectSendFileMessage.setFileSize(msgAllBean.getSendFileMessage().getSize());
            if (!TextUtils.isEmpty(msgAllBean.getSendFileMessage().getLocalPath())) {
                collectSendFileMessage.setCollectLocalPath(msgAllBean.getSendFileMessage().getLocalPath());
            }
            return collectSendFileMessage;
        } else {
            return null;
        }
    }

    @SuppressLint("CheckResult")
    private void loadHistoryMessage() {
        if (searchTime <= 0) {
            return;
        }
        Observable.just(0)
                .map(new Function<Integer, List<MsgAllBean>>() {
                    @Override
                    public List<MsgAllBean> apply(Integer integer) throws Exception {
                        List<MsgAllBean> list = msgAction.getMsg4UserHistory(toGid, toUId, searchTime);
                        taskMkName(list);
                        return list;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<MsgAllBean>>empty())
                .subscribe(new Consumer<List<MsgAllBean>>() {
                    @Override
                    public void accept(List<MsgAllBean> list) throws Exception {
                        searchTime = 0;
                        if (mAdapter != null) {
                            mAdapter.bindData(list, false);
                            mAdapter.setReadStatus(checkIsRead());
                            notifyData();
                            //TODO：此时滚动会引起索引越界
//                            mtListView.getListView().smoothScrollToPosition(0);
                        }
                    }
                });
    }


    //文件点击逻辑
    private void CheckFileMsg(MsgAllBean message, SendFileMessage fileMessage) {
        //1 我发的文件
        if (message.isMe()) {
            //1-1 若存在本地路径，则为本地文件
            if (!TextUtils.isEmpty(fileMessage.getLocalPath())) {
                //从本地路径找，存在，则打开；不存在，则提示已删除
                if (net.cb.cb.library.utils.FileUtils.fileIsExist(fileMessage.getLocalPath())) {
                    openAndroidFile(fileMessage.getLocalPath());
                } else {
                    ToastUtil.show("文件不存在或者已被删除");
                }
            } else {
                //1-2 若不存在本地路径，可能为 [PC端我的账号发的] 或者 [转发他人文件消息]
                //从下载路径里找，若存在该文件，则直接打开
                if (net.cb.cb.library.utils.FileUtils.fileIsExist(FileConfig.PATH_DOWNLOAD + fileMessage.getFile_name())) {
                    openAndroidFile(FileConfig.PATH_DOWNLOAD + fileMessage.getFile_name());
                } else {
                    //若不存在该文件，则需要重新下载
                    if (!TextUtils.isEmpty(fileMessage.getUrl())) {
                        if (fileMessage.getSize() != 0) {
                            //小于10M，自动下载+打开
                            if (fileMessage.getSize() < 10485760) {
                                DownloadFile(fileMessage);
                            } else {
                                //大于10M，跳详情，用户自行选择手动下载
                                Intent intent = new Intent(ChatActivity.this, FileDownloadActivity.class);
                                intent.putExtra("file_msg", new Gson().toJson(message));//直接整个MsgAllBean转JSON后传过去，方便后续刷新聊天消息
                                startActivity(intent);
                            }
                        }
                    } else {
                        ToastUtil.show("文件下载地址错误，请联系客服");
                    }
                }
            }
        } else {
            //2 别人发的文件
            //从下载路径里找，若存在该文件，则直接打开
            if (net.cb.cb.library.utils.FileUtils.fileIsExist(FileConfig.PATH_DOWNLOAD + fileMessage.getFile_name())) {
                //是否含有重名的情况，若有则打开的是真实文件路径
                if (!TextUtils.isEmpty(fileMessage.getRealFileRename())) {
                    openAndroidFile(FileConfig.PATH_DOWNLOAD + fileMessage.getRealFileRename());
                } else {
                    openAndroidFile(FileConfig.PATH_DOWNLOAD + fileMessage.getFile_name());
                }
            } else {
                //若不存在该文件，则需要重新下载
                if (!TextUtils.isEmpty(fileMessage.getUrl())) {
                    if (fileMessage.getSize() != 0) {
                        //小于10M，自动下载+打开
                        if (fileMessage.getSize() < 10485760) {
                            DownloadFile(fileMessage);
                        } else {
                            //大于10M，跳详情，用户自行选择手动下载
                            Intent intent = new Intent(ChatActivity.this, FileDownloadActivity.class);
                            intent.putExtra("file_msg", new Gson().toJson(message));//直接整个MsgAllBean转JSON后传过去，方便后续刷新聊天消息
                            startActivity(intent);
                        }
                    }
                } else {
                    ToastUtil.show("文件下载地址错误，请联系客服");
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void collectImgOrVideo(EventCollectImgOrVideo event) {
        if (!TextUtils.isEmpty(event.getMsgId())) {
            MsgAllBean msgAllBean = msgDao.getMsgById(event.getMsgId());
            if (msgAllBean != null) {
                onCollect(msgAllBean);
            }
        }
    }

    /**
     * 猜你要发送的图片
     * @param view
     */
    private void showPopupWindow(View view) {
        //布局、view初始化、点击事件
        if(popGuessUWant!=null && popGuessUWant.isShowing()){
            popGuessUWant.dismiss();
        }else {
            View contentView = LayoutInflater.from(ChatActivity.this).inflate(
                    R.layout.layout_pop_guess_u_want_send, null);
            ImageView ivPic = contentView.findViewById(R.id.iv_want_send_pic);
            LinearLayout layoutPop = contentView.findViewById(R.id.layout_pop);
            //显示图片，并缓存地址
            Glide.with(ChatActivity.this).load(latestUrl).apply(GlideUtil.defImageOptions1()).into(ivPic);
            SharedPreferencesUtil spGuessYouLike = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.GUESS_YOU_LIKE);
            spGuessYouLike.saveString("current_img_url",latestUrl);

            popGuessUWant = new PopupWindow(contentView,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popGuessUWant.setTouchable(true);
            popGuessUWant.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            popGuessUWant.setBackgroundDrawable(new ColorDrawable());
            //测量输入框高度
            int width = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            int height = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            layoutInput.measure(width, height);
            layoutInputHeight = layoutInput.getMeasuredHeight();

            //右下角为原点，向上偏移距离，由于+号被顶上去了，需要包括扩展面板的高度(固定值240)+测量出的输入框的高度
            popGuessUWant.showAtLocation(view, Gravity.RIGHT|Gravity.BOTTOM, DensityUtil.dip2px(getContext(),5),//偏移调整右侧5dp
                    DensityUtil.dip2px(getContext(),240)+layoutInputHeight+DensityUtil.dip2px(getContext(),3));//偏移调整居下3dp
            //点击跳到预览界面
            layoutPop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<LocalMedia> previewList=new ArrayList<>();
                    LocalMedia localMedia = new LocalMedia();
                    localMedia.setPath(latestUrl);
                    previewList.add(localMedia);
                    PictureSelector.create(ChatActivity.this)
                            .openGallery(PictureMimeType.ofAll())
                            .compress(true);//复用，直接跳图片选择器的预览界面会崩溃，需要先初始化
                    previewImage(previewList,previewList, 0);
                    overridePendingTransition(com.luck.picture.lib.R.anim.a5, 0);
                    //跳到预览后关闭弹框
                    if(popGuessUWant!=null && popGuessUWant.isShowing()){
                        popGuessUWant.dismiss();
                    }
                }
            });
        }
    }


    /**
     * 下载app dialog
     */
    private void showDownloadAppDialog(String downloadUrl) {
        DialogDefault dialogDownload = new DialogDefault(this, R.style.MyDialogTheme);
        dialogDownload.setTitleAndSure(false, true)
                .setContent("您尚未下载此应用，点击确定下载应用并安装。", true)
                .setLeft("取消")
                .setRight("确定")
                .setListener(new DialogDefault.IDialogListener() {
                    @Override
                    public void onSure() {
                        goBrowsable(downloadUrl);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
        dialogDownload.show();
    }

    private void goBrowsable(String downloadUrl) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(downloadUrl);
        intent.setData(content_url);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        startActivity(intent);
    }

    //跳图片预览
    private void previewImage(List<LocalMedia> previewImages,List<LocalMedia> selectedImages, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PictureConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) previewImages);
        bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, (Serializable) selectedImages);
        bundle.putBoolean(PictureConfig.EXTRA_BOTTOM_PREVIEW, true);
        bundle.putInt(PictureConfig.EXTRA_POSITION,position);
        bundle.putInt(PictureConfig.FROM_WHERE,1);//跳转来源 0 默认 1 猜你想要
        Intent intent = new Intent(ChatActivity.this,PicturePreviewActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, PictureConfig.PREVIEW_FROM_CHAT);
    }

}
