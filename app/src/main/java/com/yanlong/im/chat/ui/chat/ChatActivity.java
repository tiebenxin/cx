package com.yanlong.im.chat.ui.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nim_lib.config.Preferences;
import com.example.nim_lib.controll.AVChatProfile;
import com.example.nim_lib.ui.VideoActivity;
import com.example.nim_lib.util.GlideUtil;
import com.google.gson.Gson;
import com.hm.cxpay.bean.CxEnvelopeBean;
import com.hm.cxpay.bean.CxTransferBean;
import com.hm.cxpay.bean.EnvelopeDetailBean;
import com.hm.cxpay.bean.FromUserBean;
import com.hm.cxpay.bean.GrabEnvelopeBean;
import com.hm.cxpay.bean.TransferDetailBean;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.dailog.CommonSelectDialog;
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
import com.hm.cxpay.ui.bill.BillDetailActivity;
import com.hm.cxpay.ui.redenvelope.MultiRedPacketActivity;
import com.hm.cxpay.ui.redenvelope.SingleRedPacketActivity;
import com.hm.cxpay.ui.transfer.TransferActivity;
import com.hm.cxpay.ui.transfer.TransferDetailActivity;
import com.luck.picture.lib.PicturePreviewActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DateUtils;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
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
import com.yanlong.im.chat.eventbus.EventCancelInvite;
import com.yanlong.im.chat.eventbus.EventCollectImgOrVideo;
import com.yanlong.im.chat.eventbus.EventShowDialog;
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
import com.yanlong.im.chat.ui.SearchMsgActivity;
import com.yanlong.im.chat.ui.cell.ChatCellBase;
import com.yanlong.im.chat.ui.cell.ControllerNewMessage;
import com.yanlong.im.chat.ui.cell.FactoryChatCell;
import com.yanlong.im.chat.ui.cell.ICellEventListener;
import com.yanlong.im.chat.ui.cell.MessageAdapter;
import com.yanlong.im.chat.ui.cell.OnControllerClickListener;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.dialog.LockDialog;
import com.yanlong.im.location.LocationActivity;
import com.yanlong.im.location.LocationSendEvent;
import com.yanlong.im.pay.ui.record.SingleRedPacketDetailsActivity;
import com.yanlong.im.pay.ui.select.ViewAllowMemberActivity;
import com.yanlong.im.repository.ApplicationRepository;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.CollectionInfo;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.CollectionActivity;
import com.yanlong.im.user.ui.FriendVerifyActivity;
import com.yanlong.im.user.ui.InviteRemoveActivity;
import com.yanlong.im.user.ui.SelectUserActivity;
import com.yanlong.im.user.ui.ServiceAgreementActivity;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.user.ui.image.PreviewMediaActivity;
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
import com.yanlong.im.view.face.FaceConstans;
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
import net.cb.cb.library.base.BaseTcpActivity;
import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.EventFileRename;
import net.cb.cb.library.bean.EventGroupChange;
import net.cb.cb.library.bean.EventIsShowRead;
import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.bean.EventUpFileLoadEvent;
import net.cb.cb.library.bean.EventUpImgLoadEvent;
import net.cb.cb.library.bean.EventUserOnlineChange;
import net.cb.cb.library.bean.EventVoicePlay;
import net.cb.cb.library.bean.FileBean;
import net.cb.cb.library.bean.GroupStatusChangeEvent;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.bean.VideoSize;
import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.dialog.DialogCommon2;
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
import net.cb.cb.library.utils.RxJavaUtil;
import net.cb.cb.library.utils.ScreenShotListenManager;
import net.cb.cb.library.utils.ScreenUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.SoftKeyBoardListener;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ThreadUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertTouch;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.WebPageActivity;
import net.cb.cb.library.view.recycler.IRefreshListener;
import net.cb.cb.library.view.recycler.MultiRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;
import me.kareluo.ui.OptionMenu;
import me.rosuh.filepicker.config.FilePickerManager;
import retrofit2.Call;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static net.cb.cb.library.utils.FileUtils.SIZETYPE_B;

public class ChatActivity extends BaseTcpActivity implements IActionTagClickListener, ICellEventListener {
    private static String TAG = "ChatActivity";
    public final static int MIN_TEXT = 1000;//
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
    private MultiRecyclerView mtListView;
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

    public static final String AGM_TOUID = "toUId";
    public static final String AGM_TOGID = "toGId";
    public static final String GROUP_CREAT = "creat";
    public static final String ONLINE_STATE = "if_online";
    public static final String SEARCH_TIME = "search_time";
    public static final String SEARCH_KEY = "search_key";

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
    private List<MsgAllBean> deleteList = new ArrayList<>();//已经删除列表。阅后即焚

    //红包和转账
    public static final int REQ_RP = 9653;
    public static final int VIDEO_RP = 9419;
    public static final int REQUEST_RED_ENVELOPE = 1 << 2;
    public static final int REQUEST_TRANSFER = 1 << 3;
    public static final int REQUEST_FORWORD = 1 << 4;

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
    //    private ImageView mImgTriangleDown;// 下箭头
//    private LinearLayout mLlContent;
    private RecyclerView mRecyclerBubble;
    private RelativeLayout mRlDown, mRlUp;
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
    //    private ControllerLinearList popController;
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
    private GetImgUtils.ImgBean latestImg;//获取最新拍摄/截屏加入的图片
    private String latestUrl = "";//最新加入的图片url
    private long currentTradeId;
    private String searchKey;

    private boolean showCancel = false;//长按气泡是否显示撤回选项
    private boolean timeLimit = true;//这条消息撤回是否有2分钟时间限制
    private ImageView ivCollection;

    private CommonSelectDialog.Builder builder;
    private CommonSelectDialog dialogOne;//注销弹框
    private CommonSelectDialog dialogTwo;//批量收藏提示弹框
    private CommonSelectDialog dialogThree;//批量转发提示弹框
    private CommonSelectDialog dialogFour;//单选转发/收藏失效消息提示弹框
    private CommonSelectDialog dialogFive;//是否撤销提示弹框
    private CommonSelectDialog dialogSix;//成员已经离开群聊提示弹框
    private CommonSelectDialog dialogSeven;//你没有权限提示弹框
    private Activity activity;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        initIntent();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        findViews();
        initEvent();
        initObserver();
        getOftenUseFace();
        activity = this;
        builder = new CommonSelectDialog.Builder(activity);
    }


    private Runnable mPanelRecoverySoftInputModeRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewExtendFunction.getVisibility() == VISIBLE)
                viewExtendFunction.setVisibility(View.GONE);
            if (viewFaceView.getVisibility() == VISIBLE)
                viewFaceView.setVisibility(View.GONE);
            //设置改SoftInput模式为：顶起输入框
            setWindowSoftMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    };


    private void initObserver() {
        long delayMillis = 100;
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
                            lastPosition = -1;
                            lastOffset = -1;
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
//                    setPanelHeight(mKeyboardHeight, viewFaceView);
                    //虚拟键盘弹出,需更改SoftInput模式为：不顶起输入框
                    if (mViewModel.isInputText.getValue())
                        setWindowSoftMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
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
                            lastPosition = -1;
                            lastOffset = -1;
                        }
                    }, 100);
                } else {//关闭
                    btnEmj.setImageLevel(0);
                    if (mViewModel.isOpenValue()) {//有事件触发
                        if (mViewModel.isInputText.getValue()) {//无其他功能触发，则弹出输入框
                            /*******输入框弹出键盘，pos tDelayed关闭面板*****************************************/
//                       //更改SoftInput模式为：不顶起输入框
                            setWindowSoftMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
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
//                    setPanelHeight(mKeyboardHeight, viewExtendFunction);
                    //虚拟键盘弹出,需更改SoftInput模式为：不顶起输入框
                    if (mViewModel.isInputText.getValue())
                        setWindowSoftMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                    //因为面板有延迟执行，所以必须执行该方法
                    viewFaceView.setVisibility(View.GONE);
                    viewExtendFunction.setVisibility(View.VISIBLE);
                    //重置其他状态
                    mViewModel.recoveryOtherValue(mViewModel.isOpenFuction);
                    mtListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mtListView.scrollToEnd();
                            lastPosition = -1;
                            lastOffset = -1;
                        }
                    }, 100);
                } else {//关闭
                    if (mViewModel.isOpenValue()) {//有事件触发
                        if (mViewModel.isInputText.getValue()) {//无其他功能触发，则弹出输入框
                            /*******输入框弹出键盘，pos tDelayed关闭面板*****************************************/
//                       //更改SoftInput模式为：不顶起输入框
                            setWindowSoftMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
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
                    mViewModel.isInputText.setValue(true);
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
                    boolean forbid = mViewModel.groupInfo.getStat() == ChatEnum.EGroupStatus.BANED;
                    setBanView(!isEixt(), forbid);
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
            // 封号
            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {
                setBanView(false, false);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 是否还在群里
     *
     * @return
     */
    private boolean isEixt() {
        boolean isExit = false;
        if (isGroup()) {
            for (MemberUser uifo : mViewModel.groupInfo.getUsers()) {
                if (uifo.getUid() == UserAction.getMyId().longValue()) {
                    isExit = true;
                    break;
                }
            }
        } else {
            isExit = true;
        }
        return isExit;
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtil.getLog().i(TAG, "ChatActivity-异常销毁了");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.getLog().i(TAG, "ChatActivity-onStop");
        MyAppLication.INSTANCE().removeSessionChangeListener(sessionChangeListener);
//        AudioPlayManager2.getInstance().stopPlay();
        stopRecordVoice();
        if (currentPlayBean != null) {
            updatePlayStatus(currentPlayBean, 0, ChatEnum.EPlayStatus.NO_PLAY);
        }
        boolean hasClear = taskCleanRead(false);
        if (MyAppLication.INSTANCE().repository != null) {
            MyAppLication.INSTANCE().repository.updateMsgRead(toGid, toUId);
        }
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
        LogUtil.getLog().i(TAG, "ChatActivity-onStop");
        AudioPlayManager.getInstance().stopPlay();
        //释放adapter资源
        mAdapter.onDestroy();
        mViewModel.onDestroy();
        AudioRecordManager.getInstance(this).destroy();
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
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
        //释放空间
        if (popGuessUWant != null) {
            popGuessUWant.dismiss();
            popGuessUWant = null;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.dealBurnMessage();
        initActionBarLoading();
        SocketUtil.getSocketUtil().addEvent(msgEvent);
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
        session = dao.sessionGet(toGid, toUId);
        taskCleanRead(true);
        updateSessionDraftAndAtMessage();
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
        latestImg = GetImgUtils.getLatestPhoto(ChatActivity.this);
        if (latestImg != null) {
            //30秒内展示，超过不显示，url直接置空
            if (DateUtils.dateDiffer(latestImg.mTime) <= 30) {
                latestUrl = latestImg.imgUrl;
                //未展示过需要显示，展示过则不再显示(直接用SharedPreference缓存，不再加入到数据库)
                SharedPreferencesUtil spGuessYouLike = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.GUESS_YOU_LIKE);
                String saveUrl = spGuessYouLike.getString("current_img_url");
                //第二次判断缓存，若存在该url，则代表展示过，不再重复显示
                if (!TextUtils.isEmpty(saveUrl)) {
                    if (saveUrl.equals(latestUrl)) {
                        latestUrl = "";
                    }
                } else {
                    //第一次判断缓存，url必定为空，则展示
                }
            } else {
                latestUrl = "";
            }
        }
        //是否展示图片
        if (!TextUtils.isEmpty(latestUrl)) {
            return true;
        } else {
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
        RxJavaUtil.run(new RxJavaUtil.OnRxAndroidListener<Integer>() {
            @Override
            public Integer doInBackground() throws Throwable {
                //有io操作
                return DeviceUtils.getTotalRam();
            }

            @Override
            public void onFinish(Integer result) {
                int ramSize = 2;
                if (result != null) {
                    ramSize = result;
                }
                if (ramSize >= 2) {
                    MAX_UNREAD_COUNT = 80 * 8;
                } else {
                    MAX_UNREAD_COUNT = 80 * 4;
                }
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

            @Override
            public void onError(Throwable e) {

            }
        });
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
        ivCollection = findViewById(R.id.iv_collection);
        layoutInput = findViewById(R.id.layout_input);
        mtListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //如果bottom小于oldBottom,说明键盘是弹起。
                if (bottom < oldBottom && oldBottom - bottom == mKeyboardHeight) {
                    //滑动到底部
                    mtListView.scrollToEnd();
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
                    if (bean.getRejectType() == MsgBean.RejectType.NOT_FRIENDS_OR_GROUP_MEMBER || bean.getRejectType() == MsgBean.RejectType.IN_BLACKLIST) {
                        taskRefreshMessage(false);
                    } else {
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
                                if (toUId != null) { //TODO bugly #324411
                                    if (msg.getFromUid() == toUId.longValue()) {
                                        needRefresh = true;
                                    }
                                }
                            }

                            if (msg.getMsgType() == MsgBean.MessageType.OUT_GROUP) {//提出群的消息是以个人形式发的
                                needRefresh = msg.getOutGroup().getGid().equals(toGid);
                            }
                            if (msg.getMsgType() == MsgBean.MessageType.REMOVE_GROUP_MEMBER) {//提出群的消息是以个人形式发的
                                needRefresh = msg.getRemoveGroupMember().getGid().equals(toGid);
                            }
                        }
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
                    if (bean.getRejectType() == MsgBean.RejectType.NOT_FRIENDS_OR_GROUP_MEMBER || bean.getRejectType() == MsgBean.RejectType.IN_BLACKLIST) {
                        taskRefreshMessage(false);
                    } else {
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
            mAdapter.notifyItemRangeInserted(size, 1);
            //红包通知 不滚动到底部
            if (msgAllbean.getMsgNotice() != null && (msgAllbean.getMsgNotice().getMsgType() == ChatEnum.ENoticeType.RECEIVE_RED_ENVELOPE
                    || msgAllbean.getMsgNotice().getMsgType() == ChatEnum.ENoticeType.RED_ENVELOPE_RECEIVED_SELF
                    || msgAllbean.getMsgNotice().getMsgType() == ChatEnum.ENoticeType.RECEIVE_SYS_ENVELOPE
                    || msgAllbean.getMsgNotice().getMsgType() == ChatEnum.ENoticeType.SYS_ENVELOPE_RECEIVED_SELF)) {
                return;
            }
            clearScrollPosition();
            lastPosition = -1;
            lastOffset = -1;
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
        if (FaceConstans.face_animo.equals(bean.getGroup())) {
            isSendingHypertext = false;

            ShippedExpressionMessage message = SocketData.createFaceMessage(SocketData.getUUID(), bean.getName());
            sendMessage(message, ChatEnum.EMessageType.SHIPPED_EXPRESSION);

        } else if (FaceConstans.face_emoji.equals(bean.getGroup()) || FaceConstans.face_lately_emoji.equals(bean.getGroup())) {
            editChat.addEmojSpan(bean.getName());
        } else if (FaceConstans.face_custom.equals(bean.getGroup())) {
            if ("add".equals(bean.getName())) {
                if (!ViewUtils.isFastDoubleClick()) {
                    mViewModel.isOpenEmoj.setValue(false);
                    IntentUtil.gotoActivity(this, AddFaceActivity.class);
                }
            } else {
                if (!checkNetConnectStatus(0)) {
                    return;
                }
                ImageMessage imageMessage = SocketData.createImageMessage(SocketData.getUUID(), bean.getPath(), true);
                sendMessage(imageMessage, ChatEnum.EMessageType.IMAGE, true);
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
        if (toUId != null) {
            toUId = toUId == 0 ? null : toUId;
        }
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
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                onBackPressed();
                AudioPlayManager.getInstance().stopPlay();
            }

            @Override
            public void onRight() {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                if (mAdapter != null && mAdapter.isShowCheckBox()) {
                    mAdapter.showCheckBox(false, true);
                    mAdapter.clearSelectedMsg();
                    showViewMore(false);
                    mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());
                    return;
                }
                // 封号
                if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {
                    ToastUtil.show(getResources().getString(R.string.user_disable_message));
                    return;
                }
                if (isGroup()) {//群聊,单聊
                    startActivity(new Intent(getContext(), GroupInfoActivity.class)
                            .putExtra(GroupInfoActivity.AGM_GID, toGid)
                    );
                    AudioPlayManager.getInstance().stopPlay();
                } else {
                    if (mViewModel.toUId == 1L || mViewModel.toUId == 3L || (mViewModel.userInfo != null && mViewModel.userInfo.getuType() == ChatEnum.EUserType.ASSISTANT)) { //文件传输助手跳转(与常信小助手一致)
                        startActivity(new Intent(getContext(), UserInfoActivity.class)
                                .putExtra(UserInfoActivity.ID, mViewModel.toUId)
                                .putExtra(UserInfoActivity.JION_TYPE_SHOW, 1));
                    } else {
                        startActivity(new Intent(getContext(), ChatInfoActivity.class)
                                .putExtra(ChatInfoActivity.AGM_FUID, mViewModel.toUId));
                    }
                    AudioPlayManager.getInstance().stopPlay();

                }

            }
        });


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
                {
                    setWindowSoftMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
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
                    // TODO　#133404 java.lang.SecurityException　先申请权限在访问
                    permission2Util.requestPermissions(ChatActivity.this, new CheckPermission2Util.Event() {
                        @Override
                        public void onSuccess() {
                            if (checkCurrentImg()) {
                                showPopupWindow(v);
                            }
                        }

                        @Override
                        public void onFail() {

                        }
                    }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
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
                if (!isNoSendUser()) {
                    uploadVoice(file, msg);
                }
            }
        }));

        mAdapter = new MessageAdapter(this, this, isGroup(), mtListView);
        mAdapter.setCellFactory(new FactoryChatCell(this, mAdapter, this));
        mAdapter.setTagListener(this);
        mAdapter.setHasStableIds(true);
        mAdapter.setReadStatus(checkIsRead());
        mtListView.setAdapter(mAdapter);
//        mtListView.setAnimation(null);

//        mtListView.getLoadView().setStateNormal();
        mtListView.setListener(new IRefreshListener() {
            @Override
            public void onRefresh() {
                taskMoreMessage();
            }

            @Override
            public void loadMore() {

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
                            mViewModel.recoveryPartValue(null);
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
                // 封号
                if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {
                    ToastUtil.show(getResources().getString(R.string.user_disable_message));
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
                    List<MsgAllBean> msgList = new ArrayList<>();
                    msgList.addAll(mAdapter.getSelectedMsg());
                    int len = msgList.size();
                    if (len > 0) {
                        showDeleteDialog(msgList);
                    }
                }
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
                } else {
                    int size = mAdapter.getSelectedMsg().size();
                    if (size > 0) {
                        if (size > 30) {
                            showMoreMsgDialog();
                        } else {
                            List<MsgAllBean> dataList = new ArrayList<>();
                            dataList.addAll(mAdapter.getSelectedMsg());
                            filterMessageValid(dataList, 1);
                        }
                    }
                }

            }
        });

        //批量收藏
        ivCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                if (mAdapter == null || mAdapter.getSelectedMsg() == null) {
                    return;
                } else {
                    if (mAdapter.getSelectedMsg().size() > 0) {
                        filterMessageValid(mAdapter.getSelectedMsg(), 2);
                    }
                }
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

    private void setWindowSoftMode(int softInputAdjustResize) {
        getWindow().setSoftInputMode(softInputAdjustResize);
    }

    private void initActionBarLoading() {
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
    }

    private void initIntent() {
        if (getIntent() != null) {
            toGid = getIntent().getStringExtra(AGM_TOGID);
            toUId = getIntent().getLongExtra(AGM_TOUID, 0);
            searchTime = getIntent().getLongExtra(SEARCH_TIME, 0);
            searchKey = getIntent().getStringExtra(SEARCH_KEY);
        }
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

    private void checkScrollFirst(int first) {
        if (unreadCount > 0 && mAdapter != null && mAdapter.getItemCount() > 0) {
            int size = mAdapter.getItemCount();
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
        if (checkUserStatus()) {
            ToastUtil.show(getResources().getString(R.string.to_disable_message));
            return;
        }
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
            public void onClickItemCancel() {

            }
        });
    }

    /**
     * 判断对方用户是否被封号
     *
     * @return true， 被封， false 未被封
     */
    private boolean checkUserStatus() {
        boolean status = false;
        if (mViewModel.userInfo != null) {
            // 封号
            if (UserUtil.getUserStatus(mViewModel.userInfo.getLockedstatus())) {
                status = true;
            }
        }
        return status;
    }

    //是否自己被封，true， 被封， false 未被封
    private boolean isSelfLock() {
        return UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE;
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
                if (TextUtils.isEmpty(content)) {
                    //发送戳一戳消息
                    content = "快来聊天";
                }
                StampMessage message = SocketData.createStampMessage(SocketData.getUUID(), content);
                sendMessage(message, ChatEnum.EMessageType.STAMP);
            }
        });
        alertTouch.show();
        alertTouch.setEdHintOrSize("快来聊天", 20);
    }

    private void toTransfer() {
        UserBean user = PayEnvironment.getInstance().getUser();
        if (user == null || user.getRealNameStat() != 1) {
            showIdentifyDialog();
            return;
        }
        String name = "";
        String avatar = "";
        if (mViewModel.userInfo != null) {
            name = mViewModel.userInfo.getName4Show();
            avatar = mViewModel.userInfo.getHead();
        }
        Intent intent = TransferActivity.newIntent(ChatActivity.this, toUId, name, avatar);
        startActivityForResult(intent, REQUEST_TRANSFER);
    }

    private void toSystemEnvelope() {
        UserBean user = PayEnvironment.getInstance().getUser();
        if (user == null || user.getRealNameStat() != 1) {//未认证
            showIdentifyDialog();
            return;
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
                .maxSelectNum(9)
                .maxVideoSelectNum(1)
                .compress(true)// 是否压缩 true or false
                .isGif(true)
                .selectArtworkMaster(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调 code
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
                        if (checkUserStatus()) {
                            ToastUtil.show(getString(R.string.friend_disable_message));
                            return;
                        } else if (isSelfLock()) {
                            ToastUtil.show(getString(R.string.user_disable_message));
                            return;
                        }
                        if (mViewModel.userInfo != null) {
                            if (mViewModel.userInfo.getFriendDeactivateStat() == -1) {
                                showLogOutDialog(-1);
                                return;
                            } else if (mViewModel.userInfo.getFriendDeactivateStat() == 1) {
                                showLogOutDialog(1);
                                return;
                            }
                        }
                        toSystemEnvelope();
                        break;
                    case ChatEnum.EFunctionId.TRANSFER:
                        if (checkUserStatus()) {
                            ToastUtil.show(getString(R.string.friend_disable_message));
                            return;
                        } else if (isSelfLock()) {
                            ToastUtil.show(getString(R.string.user_disable_message));
                            return;
                        }
                        if (mViewModel.userInfo != null) {
                            if (mViewModel.userInfo.getFriendDeactivateStat() == -1) {
                                showLogOutDialog(-1);
                                return;
                            } else if (mViewModel.userInfo.getFriendDeactivateStat() == 1) {
                                showLogOutDialog(1);
                                return;
                            }
                        }
                        toTransfer();
                        break;
                    case ChatEnum.EFunctionId.VIDEO_CALL:
                        toVideoCall();
                        break;
                    case ChatEnum.EFunctionId.LOCATION:
                        toLocation();
                        break;
                    case ChatEnum.EFunctionId.STAMP:
                        toStamp();
                        break;
                    case ChatEnum.EFunctionId.GROUP_STAMP:
                        if (!isAdmin() && !isAdministrators()) {
                            ToastUtil.show(context, "只有群主和管理员才能使用该功能");
                            return;
                        }
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

        if (!isGroup && isVip) {
            list.add(createItemMode("视频通话", R.mipmap.ic_chat_video, ChatEnum.EFunctionId.VIDEO_CALL));
        }
        list.add(createItemMode("位置", R.mipmap.location_six, ChatEnum.EFunctionId.LOCATION));

//        if (!isGroup && !isSystemUser) {
//            list.add(createItemMode("零钱红包", R.mipmap.ic_chat_rb, ChatEnum.EFunctionId.ENVELOPE_SYS));
//        }
//        if (!isGroup && !isSystemUser) {
//            list.add(createItemMode("零钱转账", R.mipmap.ic_chat_transfer, ChatEnum.EFunctionId.TRANSFER));
//        }
        list.add(createItemMode("收藏", R.mipmap.ic_chat_collect, ChatEnum.EFunctionId.COLLECT));
//        if (isGroup) {
//            list.add(createItemMode("零钱红包", R.mipmap.ic_chat_rb, ChatEnum.EFunctionId.ENVELOPE_SYS));
//        }
        if (!isGroup) { //单聊，且对方不为客服小助手，显示戳一下
            if (!isSystemUser) {
                list.add(createItemMode("戳一下", R.mipmap.ic_chat_action, ChatEnum.EFunctionId.STAMP));
            }
        } else {
            list.add(createItemMode("戳一下", R.mipmap.ic_chat_action, ChatEnum.EFunctionId.GROUP_STAMP));
        }
        if (isGroup) {
            //本人群主
            if (UserAction.getMyId() != null && mViewModel.groupInfo != null && mViewModel.groupInfo.getMaster().equals(UserAction.getMyId().toString())) {
                list.add(createItemMode("群助手", R.mipmap.ic_chat_robot, ChatEnum.EFunctionId.GROUP_ASSISTANT));
            }
        }
        if (!isSystemUser) {
            list.add(createItemMode("名片", R.mipmap.ic_chat_newfrd, ChatEnum.EFunctionId.CARD));
        }
        list.add(createItemMode("文件", R.mipmap.ic_chat_file, ChatEnum.EFunctionId.FILE));
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
        mtListView.getListView().smoothScrollToPosition(position);
        currentScrollPosition = position;
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
        mAdapter.notifyDataSetChanged();
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
        int sendStatus = ChatEnum.ESendStatus.PRE_SEND;
        if (isNoSendUser()) {//常信小助手
            sendStatus = ChatEnum.ESendStatus.NORMAL;
        } else {
            if (isUploadType(msgType)) {
                sendStatus = ChatEnum.ESendStatus.PRE_SEND;
            }
        }
        MsgAllBean msgAllBean = SocketData.createMessageBean(toUId, toGid, msgType, sendStatus, SocketData.getFixTime(), message);
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
        int sendStatus = ChatEnum.ESendStatus.PRE_SEND;
        if (isNoSendUser()) {//常信小助手
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
        if (isNoSendUser()) {//常信小助手
            sendStatus = ChatEnum.ESendStatus.NORMAL;
            canSend = false;
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
        if (ViewUtils.isFastDoubleClick()) {
            return super.onKeyDown(keyCode, event);
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
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
            ToastUtil.show("你已被禁止领取该群红包");
            check = false;
        } else if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {//自己被封，不能领取
            ToastUtil.show(getString(R.string.user_disable_message));
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
     * 根据uid判断别人是否为群主或管理员
     *
     * @param uid
     * @return 主要用于撤回消息的权限
     */
    private boolean isHeAdmins(Long uid) {
        if (mViewModel.groupInfo != null) {
            //若没有群主
            if (!StringUtil.isNotNull(mViewModel.groupInfo.getMaster())) {
                return false;
            } else {
                if (mViewModel.groupInfo.getMaster().equals("" + uid)) {
                    return true;
                } else {
                    if (mViewModel.groupInfo.getViceAdmins() != null && mViewModel.groupInfo.getViceAdmins().size() > 0) {
                        for (Long user : mViewModel.groupInfo.getViceAdmins()) {
                            if (user.equals(uid)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
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
                        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 自己被封号
                            ToastUtil.show(getString(R.string.user_disable_message));
                            return;
                        }
                        UserInfo userInfo = userDao.findUserInfo(toUId);//查询对方是否被封号
                        if (userInfo != null) {
                            // 封号
                            if (UserUtil.getUserStatus(userInfo.getLockedstatus())) {
                                ToastUtil.show(getResources().getString(R.string.to_disable_message));
                                return;
                            }
                            if (userInfo.getuType() == ChatEnum.EUserType.BLACK) {
                                ToastUtil.show(getResources().getString(R.string.is_black_can_not_call));
                                return;
                            }
                            // 对方被注销
                            if (userInfo.getFriendDeactivateStat() != 0) {
                                int status = userInfo.getFriendDeactivateStat();
                                String content = "";
                                if (status == -1) {
                                    content = "该账号已注销，无法接通";
                                } else if (status == 1) {
                                    content = "该账号正在注销中，无法接通";
                                }
                                //给自己发一条本地通知消息
                                MsgNotice notice = SocketData.createMsgNotice(SocketData.getUUID(), ChatEnum.ENoticeType.FRIEND_DEACTIVATE, content);
                                MsgAllBean msgAllBean = SocketData.createMessageBean(userInfo.getUid(), "", ChatEnum.EMessageType.NOTICE, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), notice);
                                SocketData.saveMessage(msgAllBean);
                                taskRefreshMessage(false);
                                return;
                            }
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
        }, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE});
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
                    if (!isNoSendUser()) {
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
            setWindowSoftMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
            } else {
                if (lastPosition >= 0 && lastPosition < length) {
                    if (isSoftShow || lastPosition == length - 1 || isCanScrollBottom()) {//允许滑动到底部，或者当前处于底部，canScrollVertically是否能向上 false表示到了底部
                        mtListView.scrollToEnd();
                    } else {
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
                            mtListView.scrollToEnd();
                        } else {
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
                            mtListView.scrollToEnd();
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
            mAdapter.clearSelectedMsg();
            showViewMore(false);
            mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());
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
        if (!TextUtils.isEmpty(searchKey)) {
            startActivity(new Intent(getContext(), SearchMsgActivity.class).putExtra(SearchMsgActivity.AGM_GID, toGid).putExtra(SearchMsgActivity.AGM_FUID, toUId)
                    .putExtra(SearchMsgActivity.FROM, 1).putExtra(SearchMsgActivity.AGM_SEARCH_KEY, searchKey));
        }
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventExitChat event) {
        if (!TextUtils.isEmpty(event.getGid()) && !TextUtils.isEmpty(toGid) && event.getGid().equals(toGid)) {//PC端操作,退群
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (TextUtils.isEmpty(toGid) && toUId != null && event.getUid() != null && event.getUid().longValue() == toUId.longValue()) {//PC端操作,删除好友
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
        if (transferBean != null && transferBean.getTradeId() != currentTradeId /*&& transferBean.getOpType() == PayEnum.ETransferOpType.TRANS_SEND*/) {
            LogUtil.getLog().i("转账", "TransferSuccessEvent--" + event.getBean().getActionId() + "--" + event.getBean().getTradeId());
            currentTradeId = transferBean.getTradeId();
            if (transferBean.getOpType() == PayEnum.ETransferOpType.TRANS_RECEIVE || transferBean.getOpType() == PayEnum.ETransferOpType.TRANS_REJECT
                    || transferBean.getOpType() == PayEnum.ETransferOpType.TRANS_PAST) {
                if (!TextUtils.isEmpty(transferBean.getMsgJson())) {
                    MsgAllBean msg = GsonUtils.getObject(transferBean.getMsgJson(), MsgAllBean.class);
                    if (msg.getMsg_type() == ChatEnum.EMessageType.TRANSFER) {
                        TransferMessage preTransfer = msg.getTransfer();
                        preTransfer.setOpType(transferBean.getOpType());
                        replaceListDataAndNotify(msg);
                    }
                }
                long uid = 0;
                if (transferBean.getOpType() == PayEnum.ETransferOpType.TRANS_RECEIVE && UserAction.getMyId() != null) {
                    uid = UserAction.getMyId().longValue();
                }
                msgDao.updateTransferStatus(transferBean.getTradeId() + "", transferBean.getOpType(), uid);
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
            if (toUId != null && bean.uid == toUId.longValue()) {
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
            while (mmr.getFrameAtTime() == null) {
                Thread.sleep(1000);
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
                    if (dataType == CameraActivity.RESULT_TYPE_VIDEO) {
//                        if (!checkNetConnectStatus()) {
                        String file = data.getStringExtra(CameraActivity.INTENT_PATH);
                        LogUtil.getLog().i(TAG, "--视频Chat--" + file);
                        int height = data.getIntExtra(CameraActivity.INTENT_PATH_HEIGHT, 0);
                        int width = data.getIntExtra(CameraActivity.INTENT_VIDEO_WIDTH, 0);
                        long time = data.getLongExtra(CameraActivity.INTENT_PATH_TIME, 0L);
                        boolean isLocalTake = data.getBooleanExtra(CameraActivity.INTENT_LOCAL_TAKE, false);// 用于判断极速秒传是否需要调用fileCheck接口
//                        String videoBg = data.getStringExtra(CameraActivity.INTENT_PATH_BG);
                        //app内拍摄的视频经检查已经实现了自动压缩
                        VideoMessage videoMessage = SocketData.createVideoMessage(SocketData.getUUID(), "file://" + file, getVideoAttBitmap(file), false, time, width, height, file);
                        videoMsgBean = sendMessage(videoMessage, ChatEnum.EMessageType.MSG_VIDEO, false);
                        // 不等于常信小助手，需要上传到服务器
                        if (!isNoSendUser()) {
                            UpLoadService.onAddVideo(this.context, videoMsgBean, false, isLocalTake);
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
                        if (!isNoSendUser()) {
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
//                            imgMsgBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, SocketData.getFixTime(), imageMessage, ChatEnum.EMessageType.PHOTO);
                            // 不等于常信小助手，需要上传到服务器
                            if (!isNoSendUser()) {
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
                case REQUEST_RED_ENVELOPE:
                    CxEnvelopeBean envelopeBean = data.getParcelableExtra("envelope");
                    if (envelopeBean != null && currentTradeId != envelopeBean.getTradeId()) {
                        currentTradeId = envelopeBean.getTradeId();
                        RealmList<MemberUser> allowUses = null;
                        if (envelopeBean.getAllowUses() != null && envelopeBean.getAllowUses().size() > 0) {
                            allowUses = convertMemberList(envelopeBean.getAllowUses());
                        }
                        int envelopeStatus = PayEnum.EEnvelopeStatus.NORMAL;
                        boolean permission = true;
                        if (isGroup() && allowUses != null && UserAction.getMyId() != null) {
                            MemberUser user = new MemberUser();
                            user.setUid(UserAction.getMyId().longValue());
                            user.init(toGid);
                            if (!allowUses.contains(user)) {
//                                envelopeStatus = PayEnum.EEnvelopeStatus.NO_ALLOW;
                                permission = false;
                            }
                        }
                        RedEnvelopeMessage message = SocketData.createSystemRbMessage(SocketData.getUUID(), envelopeBean.getTradeId(), envelopeBean.getActionId(), envelopeBean.getMessage(),
                                MsgBean.RedEnvelopeType.SYSTEM.getNumber(), envelopeBean.getEnvelopeType(), envelopeBean.getSign(), allowUses, envelopeStatus, permission);
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
                            if (!mViewModel.isInputText.getValue())
                                mViewModel.isInputText.setValue(true);
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
                                if (!isNoSendUser()) {
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
                                if (!isNoSendUser()) {
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
                case REQUEST_TRANSFER:
                    if (data != null) {
                        CxTransferBean transferBean = data.getParcelableExtra("transfer");
                        if (transferBean != null && transferBean.getTradeId() != currentTradeId) {
                            LogUtil.getLog().i("转账", "TransferSuccessEvent--" + transferBean.getActionId() + "--" + transferBean.getTradeId());
                            currentTradeId = transferBean.getTradeId();
                            if (transferBean.getOpType() == PayEnum.ETransferOpType.TRANS_RECEIVE || transferBean.getOpType() == PayEnum.ETransferOpType.TRANS_REJECT
                                    || transferBean.getOpType() == PayEnum.ETransferOpType.TRANS_PAST) {
                                if (!TextUtils.isEmpty(transferBean.getMsgJson())) {
                                    MsgAllBean msg = GsonUtils.getObject(transferBean.getMsgJson(), MsgAllBean.class);
                                    if (msg.getMsg_type() == ChatEnum.EMessageType.TRANSFER) {
                                        TransferMessage preTransfer = msg.getTransfer();
                                        preTransfer.setOpType(transferBean.getOpType());
                                        replaceListDataAndNotify(msg);
                                    }
                                }
                                long tuid = 0;
                                if (transferBean.getOpType() == PayEnum.ETransferOpType.TRANS_RECEIVE && UserAction.getMyId() != null) {
                                    tuid = UserAction.getMyId().longValue();
                                }
                                msgDao.updateTransferStatus(transferBean.getTradeId() + "", transferBean.getOpType(), tuid);
                            }
                            TransferMessage message = SocketData.createTransferMessage(SocketData.getUUID(), transferBean.getTradeId(), transferBean.getAmount(), transferBean.getInfo(), transferBean.getSign(), transferBean.getOpType());
                            sendMessage(message, ChatEnum.EMessageType.TRANSFER);
                        }
                    }
                    break;
                case REQUEST_FORWORD:
                    if (mAdapter != null) {
                        mAdapter.clearSelectedMsg();
                        hideMultiSelect(ivForward);
                    }
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

    private RealmList<MemberUser> convertMemberList(List<FromUserBean> list) {
        if (!isGroup() || mViewModel.groupInfo == null || list == null) {
            return null;
        }
        int len = list.size();
        if (len > 0) {
            String[] memberIds = new String[len];
            for (int i = 0; i < len; i++) {
                FromUserBean bean = list.get(i);
                memberIds[i] = toGid + bean.getUid();
            }
            List<MemberUser> members = msgDao.getMembers(toGid, memberIds);
            RealmList<MemberUser> allowUsers = new RealmList<>();
            allowUsers.addAll(members);
            return allowUsers;
        }
        return null;

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
            if (!isNoSendUser()) {
                UpLoadService.onAddVideo(this.context, videoMsgBean, false, false);
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
                mtListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        taskRefreshMessage(false);
                    }
                }, 100);
            }
            initUnreadCount();
        }
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
            replaceListDataAndNotify(msgAllbean);
        } else {
        }
    }

    //刷新某一条消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshOneMsg(EventFactory.UpdateOneMsgEvent event) {
        taskRefreshMessage(false);
    }

    //撤销入群邀请
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cancelInvite(EventCancelInvite event) {
        cancelInviteDialog(event.getUserInfoList());
    }

    //我是普通成员，没有权限撤销
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventShowDialog(EventShowDialog event) {
        if (event.getType() == 1) {
            if (activity == null || activity.isFinishing()) {
                return;
            }
            dialogSeven = builder.setTitle("没有操作权限")
                    .setShowLeftText(false)
                    .setRightText("确定")
                    .setRightOnClickListener(v -> {
                        dialogSeven.dismiss();
                    })
                    .build();
            dialogSeven.show();
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
        try {
            if (mAdapter == null && mAdapter.getItemCount() <= 0) {
                return;
            }
            int position = mAdapter.updateMessage(msgAllbean);
            if (position >= 0) {
                mAdapter.notifyItemChanged(position, position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /***
     * 替换listData中的某条消息并且刷新
     * @param msgAllbean
     * @param loose 是否刷新
     */
    private void replaceListDataAndNotify(MsgAllBean msgAllbean, boolean loose) {
        if (mAdapter == null || mAdapter.getItemCount() <= 0) {
            return;
        }
        int position = mAdapter.updateMessage(msgAllbean);
        if (position >= 0) {
            if (loose) {
                mAdapter.notifyItemChanged(position, position);
            }
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
                mAdapter.notifyItemChanged(i, i);
            }
        }
    }

    /**
     * 显示大图
     *
     * @param msgId
     */
    private void scanImageAndVideo(String msgId) {
        ArrayList<LocalMedia> selectList = new ArrayList<>();
        List<LocalMedia> temp = new ArrayList<>();
        int pos = 0;
        List<MsgAllBean> listdata = msgAction.getMsg4UserImg(toGid, toUId);
        for (int i = 0; i < listdata.size(); i++) {
            MsgAllBean msgl = listdata.get(i);
            if (msgId.equals(msgl.getMsg_id())) {
                pos = i;
            }
            LocalMedia lc = new LocalMedia();
            //发送状态正常，则允许收藏 (阅后即焚改为允许收藏)
            if (msgl.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
                lc.setCanCollect(true);
            }
            lc.setMsg_id(msgl.getMsg_id());
            if (msgl.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO) {
                lc.setMimeType(PictureConfig.TYPE_VIDEO);
                String localUrl = msgl.getVideoMessage().getLocalUrl();
                if (StringUtil.isNotNull(localUrl)) {
                    File file = new File(localUrl);
                    if (file.exists()) {
                        lc.setVideoLocalUrl(localUrl);
                    }
                }
                lc.setVideoUrl(msgl.getVideoMessage().getUrl());
                lc.setVideoBgUrl(msgl.getVideoMessage().getBg_url());
                lc.setWidth((int) msgl.getVideoMessage().getWidth());
                lc.setHeight((int) msgl.getVideoMessage().getHeight());
                lc.setDuration(msgl.getVideoMessage().getDuration());
            } else {
                lc.setMimeType(PictureConfig.TYPE_IMAGE);
                lc.setCutPath(msgl.getImage().getThumbnailShow());
                lc.setCompressPath(msgl.getImage().getPreviewShow());
                lc.setPath(msgl.getImage().getOriginShow());
                lc.setSize(msgl.getImage().getSize());
                lc.setWidth(new Long(msgl.getImage().getWidth()).intValue());
                lc.setHeight(new Long(msgl.getImage().getHeight()).intValue());
                lc.setHasRead(msgl.getImage().isReadOrigin());
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
            if (msgId.equals(selectList.get(i).getMsg_id())) {
                pos = i;
                break;
            }
        }
        Intent intent = new Intent(this, PreviewMediaActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", selectList);
        bundle.putInt("position", pos);
        intent.putExtra(PictureConfig.GID, toGid);
        intent.putExtra(PictureConfig.TO_UID, toUId);
        intent.putExtras(bundle);
        startActivity(intent);
//        PictureSelector.create(ChatActivity.this)
//                .themeStyle(R.style.picture_default_style)
//                .isGif(true)
//                .openExternalPreview1(pos, selectList, toGid, toUId, PictureConfig.FROM_DEFAULT, "");

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
                    UpLoadService.onAddVideo(this.context, msgAllBean, false, false);
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
        if (mAdapter != null && mAdapter.isShowCheckBox()) {
            return;
        }
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
        if (mAdapter != null && mAdapter.isShowCheckBox()) {
            return;
        }
        long tradeId = StringUtil.getLong(rid);
        if (tradeId > 0) {
            Intent intent = SingleRedPacketDetailsActivity.newIntent(ChatActivity.this, tradeId, 1);
            startActivity(intent);
        }
    }

    @Override
    public void clickTransfer(String rid, String msgId) {
        if (mAdapter != null && mAdapter.isShowCheckBox()) {
            return;
        }
        long tradeId = StringUtil.getLong(rid);
        if (tradeId > 0) {
            MsgAllBean msgAllBean = msgDao.getMsgByRid(tradeId);
            httpGetTransferDetail(rid, PayEnum.ETransferOpType.TRANS_SEND, msgAllBean);
        }
    }

    @Override
    public void clickLock() {
        if (ViewUtils.isFastDoubleClick()) {
            return;
        }
        if (mAdapter != null && mAdapter.isShowCheckBox()) {
            return;
        }
        showLockDialog();
    }

    @Override
    public void clickEditAgain(String content) {
        if (ViewUtils.isFastDoubleClick()) {
            return;
        }
        if (mAdapter != null && mAdapter.isShowCheckBox()) {
            return;
        }
        //br标签替换为换行，存之前将换行替换为br标签
        content = content.replace("<br>", "\n");
        showDraftContent(editChat.getText().toString() + content);
        editChat.setSelection(editChat.getText().length());
        //虚拟键盘弹出,需更改SoftInput模式为：不顶起输入框
        if (!mViewModel.isOpenValue()) {
            setWindowSoftMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        mViewModel.isInputText.setValue(true);

    }

    @Override
    public void clickAddFriend(String uid) {
        if (mAdapter != null && mAdapter.isShowCheckBox()) {
            return;
        }
        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
            ToastUtil.show(getResources().getString(R.string.user_disable_message));
            return;
        }
        try {
            Long userId = Long.parseLong(uid);
            if (userId != null && userId.longValue() > 0) {
                toSendVerifyActivity(userId);
            }
        } catch (Exception e) {

        }
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
        //会导致播放不能暂停
//        if (AudioPlayManager2.getInstance().isPlayingVoice()) {
//            AudioPlayManager2.getInstance().stopPlay();
//        }
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
            try {
                if (AudioPlayManager.getInstance().getCurrentMsg() != null) {
                    AudioPlayManager.getInstance().stopPlay();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

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
                        ThreadUtil.getInstance().runMainThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.show("语音下载失败");
                            }
                        });
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

    private synchronized void updatePlayStatus(MsgAllBean bean, int position, @ChatEnum.EPlayStatus int status) {
        bean = amendMsgALlBean(position, bean);
        if (bean == null || bean.getVoiceMessage() == null) {
            return;
        }
        VoiceMessage voiceMessage = bean.getVoiceMessage();
        boolean isRead = false;
        if (status == ChatEnum.EPlayStatus.NO_PLAY || status == ChatEnum.EPlayStatus.PLAYING) {//已点击下载，或者正在播
            if (bean.isRead() == false) {
                isRead = true;
                bean.setRead(true);
            }
        }
        msgDao.updatePlayStatus(voiceMessage.getMsgId(), status, isRead);
        voiceMessage.setPlayStatus(status);
        final MsgAllBean finalBean = bean;
        mtListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.getLog().i("语音LOG", "updatePlayStatus--msgId=" + finalBean.getMsg_id() + "--status=" + status);
                replaceListDataAndNotify(finalBean);

            }
        }, 10);

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
        try {
            menus = initMenus(msgbean);
            AdapterPopMenu adapterPopMenu = new AdapterPopMenu(menus, this);
            int spanCount;
            if (menus.size() == 1) {
                spanCount = 1;
            } else if (menus.size() == 2) {
                spanCount = 2;
            } else if (menus.size() == 3) {
                spanCount = 3;
            } else {
                spanCount = 4;
            }
            // 默认一行高度
            int spacing = ScreenUtil.dip2px(this, 30);
            if (menus.size() > 4) {// 设置两行高度
                spacing = ScreenUtil.dip2px(this, 80);
            }
            mRecyclerBubble.setLayoutManager(new GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false));
            mRecyclerBubble.setAdapter(adapterPopMenu);
            adapterPopMenu.setListener(new AdapterPopMenu.IMenuClickListener() {
                @Override
                public void onClick(OptionMenu menu) {
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                    onBubbleClick((String) menu.getTitle(), msgbean);
                }
            });
            // 获取ActionBar位置，判断消息是否到顶部
            // 获取ListView在屏幕顶部的位置
            int[] location = new int[2];
            mtListView.getLocationOnScreen(location);
            // 获取View在屏幕的位置
            int[] locationView = new int[2];
            v.getLocationOnScreen(locationView);
            if (mPopupWindow != null && mPopupWindow.isShowing()) mPopupWindow.dismiss();
            mPopupWindow = null;

            mPopupWindow = new PopupWindow(mRootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
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
                    // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss`
                }
            });

            popupWindowDismiss(listener);
            // 当View Y轴的位置小于ListView Y轴的位置时 气泡向下弹出来，否则向上弹出
            if (v.getMeasuredHeight() >= mtListView.getMeasuredHeight() && locationView[1] < (location[1])) {
                // 内容展示完，向上弹出
                if (locationView[1] < 0 && (v.getMeasuredHeight() - Math.abs(locationView[1]) < mtListView.getMeasuredHeight())) {
                    mRlUp.setVisibility(VISIBLE);
                    mImgTriangleUp.setVisibility(VISIBLE);
                    mRlDown.setVisibility(GONE);
                    setArrowLocation(v, 1, msgbean.isMe(), menus.size(), msgbean.getMsg_type());
                    mPopupWindow.showAsDropDown(v);
                } else {
                    // 中间弹出
                    mRlUp.setVisibility(GONE);
                    mImgTriangleUp.setVisibility(GONE);
                    mRlDown.setVisibility(VISIBLE);
                    setArrowLocation(v, 2, msgbean.isMe(), menus.size(), msgbean.getMsg_type());
                    showPopupWindowUp(v, 1);
                }
            } else if (locationView[1] < (location[1] + spacing)) {
                mRlUp.setVisibility(VISIBLE);
                mImgTriangleUp.setVisibility(VISIBLE);
                mRlDown.setVisibility(GONE);
                setArrowLocation(v, 1, msgbean.isMe(), menus.size(), msgbean.getMsg_type());
                mPopupWindow.showAsDropDown(v);
            } else {
                mRlUp.setVisibility(GONE);
                mImgTriangleUp.setVisibility(GONE);
                mRlDown.setVisibility(VISIBLE);
                setArrowLocation(v, 2, msgbean.isMe(), menus.size(), msgbean.getMsg_type());
                showPopupWindowUp(v, 2);
            }
        } catch (Exception e) {
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
                //发送状态正常，则允许收藏 (阅后即焚改为允许收藏)
                if (sendStatus != ChatEnum.ESendStatus.ERROR) {
                    menus.add(new OptionMenu("收藏"));
                }
                break;
            case ChatEnum.EMessageType.VOICE:
                if (msgDao.userSetingGet().getVoicePlayer() == 0) {
                    menus.add(0, new OptionMenu("听筒播放"));
                } else {
                    menus.add(0, new OptionMenu("扬声器播放"));
                }
                //发送状态正常，则允许收藏 (阅后即焚改为允许收藏)
                if (sendStatus != ChatEnum.ESendStatus.ERROR) {
                    menus.add(new OptionMenu("收藏"));
                }
                break;
            case ChatEnum.EMessageType.LOCATION:
            case ChatEnum.EMessageType.IMAGE:
            case ChatEnum.EMessageType.MSG_VIDEO:
            case ChatEnum.EMessageType.SHIPPED_EXPRESSION:
            case ChatEnum.EMessageType.FILE:
                //发送状态正常，则允许收藏 (阅后即焚改为允许收藏)
                if (sendStatus != ChatEnum.ESendStatus.ERROR) {
                    menus.add(new OptionMenu("收藏"));
                }
                break;
        }
        if (sendStatus == ChatEnum.ESendStatus.NORMAL && type != ChatEnum.EMessageType.MSG_VOICE_VIDEO) {
            if (isGroup()) {
                //如果是群聊，先确保该消息类型允许被撤回，状态正常
                if (mViewModel.groupInfo != null && mViewModel.groupInfo.getStat() == ChatEnum.EGroupStatus.NORMAL && !filterCancel(msgAllBean.getMsg_type()) && !isAtBanedCancel(msgAllBean)) {
                    //如果我是群主，可撤回所有消息，无时间限制
                    if (isAdmin()) {
                        showCancel = true;
                        timeLimit = false;
                    } else if (isAdministrators()) {
                        //如果我是群管理，且这条消息是自己发的，允许撤回，默认有时间限制
                        if (msgAllBean.getFrom_uid() != null && msgAllBean.getFrom_uid().longValue() == UserAction.getMyId().longValue()) {
                            showCancel = true;
                            timeLimit = true;
                        } else {
                            //如果这条消息为除自己以外，其他群管理/群主发的，则无权撤回其他管理层的消息；如果是普通群员的消息，我可以任意时间撤回
                            if (isHeAdmins(msgAllBean.getFrom_uid())) {
                                showCancel = false;
                            } else {
                                showCancel = true;
                                timeLimit = false;
                            }
                        }
                    } else {
                        //如果我是普通群员，且这条消息是自己发的，允许撤回，默认有时间限制
                        if (msgAllBean.getFrom_uid() != null && msgAllBean.getFrom_uid().longValue() == UserAction.getMyId().longValue()) {
                            showCancel = true;
                            timeLimit = true;
                        } else {
                            showCancel = false;
                        }
                    }
                }
            } else {
                //单聊旧逻辑不变
                if (msgAllBean.getFrom_uid() != null && msgAllBean.getFrom_uid().longValue() == UserAction.getMyId().longValue() && !filterCancel(msgAllBean.getMsg_type()) && !isAtBanedCancel(msgAllBean)) {
                    showCancel = true;
                    timeLimit = true;
                } else {
                    showCancel = false;
                }
            }
            //展示撤回选项逻辑
            if (showCancel) {
                //是否有2分钟限制
                if (timeLimit) {
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
                } else {
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
        menus.add(new OptionMenu("删除"));
        menus.add(new OptionMenu("多选"));
        return menus;
    }

    private boolean filterCancel(int msgType) {
        if (msgType == ChatEnum.EMessageType.RED_ENVELOPE || msgType == ChatEnum.EMessageType.TRANSFER) {
            return true;
        }
        return false;
    }

    //是否禁止转发，仅文本，图片，@，视频，位置，表情，文件消息支持转发，其他均禁止
    public boolean isBanForward(@ChatEnum.EMessageType int type) {
        if (type == ChatEnum.EMessageType.VOICE || type == ChatEnum.EMessageType.STAMP || type == ChatEnum.EMessageType.RED_ENVELOPE || type == ChatEnum.EMessageType.MSG_VOICE_VIDEO
                || type == ChatEnum.EMessageType.BUSINESS_CARD || type == ChatEnum.EMessageType.ASSISTANT_PROMOTION || type == ChatEnum.EMessageType.TRANSFER
                || type == ChatEnum.EMessageType.TRANSFER_NOTICE || type == ChatEnum.EMessageType.ASSISTANT_NEW || type == ChatEnum.EMessageType.ASSISTANT) {
            return true;
        }
        return false;
    }

    //是否禁止回复
    public boolean isBanReply(@ChatEnum.EMessageType int type) {
        if (type == ChatEnum.EMessageType.STAMP || type == ChatEnum.EMessageType.RED_ENVELOPE
                || type == ChatEnum.EMessageType.MSG_VOICE_VIDEO /*|| type == ChatEnum.EMessageType.BUSINESS_CARD*/ || type == ChatEnum.EMessageType.LOCATION
                || type == ChatEnum.EMessageType.SHIPPED_EXPRESSION || type == ChatEnum.EMessageType.WEB || type == ChatEnum.EMessageType.BALANCE_ASSISTANT ||
                type == ChatEnum.EMessageType.ASSISTANT_PROMOTION || type == ChatEnum.EMessageType.ASSISTANT || type == ChatEnum.EMessageType.TRANSFER
                || type == ChatEnum.EMessageType.ASSISTANT_NEW || type == ChatEnum.EMessageType.TRANSFER_NOTICE || !isEixt()) {
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

        mRlUp = mRootView.findViewById(R.id.rl_up);
        mImgTriangleUp = mRootView.findViewById(R.id.img_triangle_up);
        mRlDown = mRootView.findViewById(R.id.rl_down);
        mRecyclerBubble = mRootView.findViewById(R.id.recycler_bubble);
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
            // 封号
            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {
                ToastUtil.show(getResources().getString(R.string.user_disable_message));
                return;
            }
            if (msgbean.getMsg_type() == ChatEnum.EMessageType.IMAGE || msgbean.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO
                    || msgbean.getMsg_type() == ChatEnum.EMessageType.FILE) {
                ArrayList<FileBean> list = new ArrayList<>();
                FileBean fileBean = new FileBean();
                if (msgbean.getImage() != null) {
                    fileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgbean.getImage().getPreview()));
                    fileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgbean.getImage().getPreview()));
                } else if (msgbean.getVideoMessage() != null) {
                    FileBean itemFileBean = new FileBean();
                    itemFileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgbean.getVideoMessage().getBg_url()));
                    itemFileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgbean.getVideoMessage().getBg_url()));
                    list.add(itemFileBean);
                    fileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgbean.getVideoMessage().getUrl()));
                    fileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgbean.getVideoMessage().getUrl()));
                } else if (msgbean.getSendFileMessage() != null) {
                    fileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgbean.getSendFileMessage().getUrl()));
                    fileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgbean.getSendFileMessage().getUrl()));
                }
                list.add(fileBean);
                UpFileUtil.getInstance().batchFileCheck(list, new CallBack<ReturnBean<List<String>>>() {
                    @Override
                    public void onResponse(Call<ReturnBean<List<String>>> call, Response<ReturnBean<List<String>>> response) {
                        super.onResponse(call, response);
                        if (response.body() != null && response.body().isOk()) {
                            if (response.body().getData() != null) {
                                List<String> data = response.body().getData();
                                int size = data.size();
                                if (msgbean.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO) {
                                    if (size == list.size()) {
                                        onRetransmission(msgbean);
                                    } else if (size == 1) {
                                        String md5 = data.get(0);
                                        String url = "";
                                        for (int i = 0; i < list.size(); i++) {
                                            FileBean bean = list.get(i);
                                            if (bean.getMd5().equals(md5)) {
                                                url = bean.getUrl();
                                            }
                                        }
                                        if (!TextUtils.isEmpty(url) && PictureMimeType.isVideoType(url)) {
                                            onRetransmission(msgbean);
                                        } else {
                                            showMsgFailDialog();
                                        }
                                    } else {
                                        showMsgFailDialog();
                                    }
                                } else {
                                    if (size == list.size()) {
                                        onRetransmission(msgbean);
                                    } else {
                                        showMsgFailDialog();
                                    }
                                }
                            } else {
                                showMsgFailDialog();
                            }
                        } else {
                            showMsgFailDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnBean<List<String>>> call, Throwable t) {
                        super.onFailure(call, t);
                        showMsgFailDialog();
                    }
                });
            } else {
                onRetransmission(msgbean);
            }
        } else if ("撤回".equals(value)) {
            // 封号
            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {
                ToastUtil.show(getResources().getString(R.string.user_disable_message));
                return;
            }
            onRecall(msgbean);
        } else if ("扬声器播放".equals(value)) {
            msgDao.userSetingVoicePlayer(0);
        } else if ("回复".equals(value)) {
            // 封号
            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {
                ToastUtil.show(getResources().getString(R.string.user_disable_message));
                return;
            }
            onAnswer(msgbean);
        } else if ("多选".equals(value)) {
            // 封号
            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {
                ToastUtil.show(getResources().getString(R.string.user_disable_message));
                return;
            }
            onMore(msgbean);
            if (mViewModel.isInputText.getValue()) {
                mViewModel.isInputText.setValue(false);
            }
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
        mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());
    }

    private void changeRightBtn(boolean isShow) {
        if (isShow) {
            if (survivaltime != 0) {
                headView.getActionbar().getRightImage().setVisibility(VISIBLE);
            }
            actionbar.getBtnRight().setVisibility(View.VISIBLE);
            setDisturb();
            actionbar.setTxtRight("");
        } else {
            headView.getActionbar().getRightImage().setVisibility(GONE);
            actionbar.getBtnRight().setVisibility(GONE);
            actionbar.showDisturb(false);
            actionbar.setTxtRight("取消");
        }
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
        int myType = 0;//我的身份
        if (isAdmin()) {
            myType = 1;
        } else if (isAdministrators()) {
            myType = 2;
        }
        MsgCancel cancel = SocketData.createCancelMsg(msgBean, UserAction.getMyId().longValue(), myType);
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
            setWindowSoftMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (!mViewModel.isInputText.getValue())
            mViewModel.isInputText.setValue(true);
        if (!mViewModel.isReplying.getValue())
            mViewModel.isReplying.setValue(true);
        viewReplyMessage.setMessage(bean);
        mtListView.scrollToEnd();
    }

    //单条消息收藏
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
        //单条收藏，限制支持的类型
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
        } else {
            //2 无网收藏
            //2-1 如果本地收藏列表不存在这条数据，收藏到列表，并保存收藏操作记录
            if (msgDao.findLocalCollection(msgbean.getMsg_id()) == null) {
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

    //消息类批量转换成收藏类
    private List<CollectionInfo> convertCollectBean(List<MsgAllBean> msgAllBeanList) {
        List<CollectionInfo> list = new ArrayList<>();//批量保存
        for (int i = 0; i < msgAllBeanList.size(); i++) {
            if (msgAllBeanList.get(i) != null) {
                //状态正常且满足可收藏类型
                if (msgAllBeanList.get(i).getSend_state() == ChatEnum.ESendStatus.ERROR) {
                    break;
                }
                if (msgAllBeanList.get(i).getMsg_type() == ChatEnum.EMessageType.TEXT || msgAllBeanList.get(i).getMsg_type() == ChatEnum.EMessageType.AT
                        || msgAllBeanList.get(i).getMsg_type() == ChatEnum.EMessageType.VOICE || msgAllBeanList.get(i).getMsg_type() == ChatEnum.EMessageType.LOCATION
                        || msgAllBeanList.get(i).getMsg_type() == ChatEnum.EMessageType.IMAGE || msgAllBeanList.get(i).getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO
                        || msgAllBeanList.get(i).getMsg_type() == ChatEnum.EMessageType.FILE || msgAllBeanList.get(i).getMsg_type() == ChatEnum.EMessageType.SHIPPED_EXPRESSION) {
                    String fromUsername = "";//用户名称
                    String fromGid = "";//群组id
                    String fromGroupName = "";//群组名称
                    MsgAllBean msgbean = msgAllBeanList.get(i);
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
                    }
                    collectionInfo.setFromUid(msgbean.getFrom_uid());
                    collectionInfo.setFromUsername(fromUsername);
                    collectionInfo.setType(SocketData.getMessageType(msgbean.getMsg_type()).getNumber());//收藏类型统一改为protobuf类型
                    collectionInfo.setFromGid(fromGid);
                    collectionInfo.setFromGroupName(fromGroupName);
                    collectionInfo.setMsgId(msgbean.getMsg_id());//不同表，id相同
                    collectionInfo.setCreateTime(System.currentTimeMillis() + "");//收藏时间是现在系统时间
                    list.add(collectionInfo);
                }
            }
        }
        return list;
    }


    /**
     * 设置显示在v上方(以v的左边距为开始位置)
     *
     * @param v
     * @param gravity
     */
    public void showPopupWindowUp(View v, int gravity) {
        // 重新获取自身的长宽高
        mRootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupWidth = mRootView.getMeasuredWidth();
        popupHeight = mRootView.getMeasuredHeight();

        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int x = (location[0] + v.getWidth() / 2) - popupWidth / 2;
        if (gravity == 1) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, x, dm.heightPixels / 2);
        } else {
            //在控件上方显示
            mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, x, location[1] - popupHeight);
        }
    }

    /**
     * 设置气泡箭头的位置
     *
     * @param v
     * @param gravity     1显示向上箭头 2显示向下箭头
     * @param isMe
     * @param itemCount   选项个数
     * @param messageType
     */
    private void setArrowLocation(View v, int gravity, boolean isMe, int itemCount, int messageType) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(v.getWidth() - ScreenUtil.dip2px(this, 6),
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.rl_up);
        if (isMe) {
            if (itemCount < 4) {
                params.setMargins(0, 0, ScreenUtil.dip2px(this, 52), 0);
            }
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.setMargins(0, 0, ScreenUtil.dip2px(this, 52), 0);
        } else {
            if (itemCount < 4) {
                params.setMargins(ScreenUtil.dip2px(this, 52), 0, 0, 0);
            }
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.setMargins(ScreenUtil.dip2px(this, 52), 0, 0, 0);
        }
        mRecyclerBubble.setLayoutParams(params);
        if (gravity == 1) {
            mRlUp.setLayoutParams(layoutParams);
        } else {
            //在控件上方显示
            layoutParams.addRule(RelativeLayout.BELOW, R.id.recycler_bubble);
            mRlDown.setLayoutParams(layoutParams);
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
        if (mAdapter.getMsgList() != null && mAdapter.getItemCount() > 0) {
            //调用该方法，有面板或软键盘弹出时，会使列表跳转到第一项
            mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());
        }
        mtListView.getSwipeLayout().setRefreshing(false);
    }

    private MsgAction msgAction = new MsgAction();
    private UserAction userAction = new UserAction();
    private UserDao userDao = new UserDao();
    private MsgDao msgDao = new MsgDao();


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
                    } else {
                        LogUtil.getLog().i(TAG, "发送已读--msgID=" + bean.getMsg_id() + "--无效--time=" + bean.getTimestamp());
                        LogUtil.writeLog(TAG + "--发送已读--msgID=" + bean.getMsg_id() + "--无效--time=" + bean.getTimestamp());
                    }
                }
            }
        } else if (!TextUtils.isEmpty(toGid)) {
            MsgAllBean bean = msgDao.msgGetLast4Gid(toGid);
            if (bean != null) {
                if (bean.getRead() == 0) {
                    if (MessageManager.getInstance().isReadTimeValid(toGid, bean.getTimestamp())) {
                        MessageManager.getInstance().addReadTime(toGid, bean.getTimestamp());
                        LogUtil.getLog().i(TAG, "发送已读同步--msgID=" + bean.getMsg_id() + "--time=" + bean.getTimestamp());
                        SocketData.sendMultiTerminalSync(toGid);
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
                        sendRead();
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
                        if (len <= 15) {
                            mtListView.setStackFromEnd(false);
                        } else {
                            mtListView.setStackFromEnd(true);
                        }
                        try {
                            if (deleteList.size() > 0 && (list.contains(deleteList.get(0)) || list.contains(deleteList.get(deleteList.size() - 1)))) {
                                list.removeAll(deleteList);
                            }
                            deleteList.clear();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mAdapter.bindData(list, false);
                        mAdapter.setReadStatus(checkIsRead());
                        notifyData2Bottom(isScrollBottom);
                        if (len == 0 && lastPosition > len - 1) {//历史数据被清除了
                            lastPosition = 0;
                            lastOffset = 0;
                            clearScrollPosition();
                        }
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
                    if (StringUtil.isNotNull(gname)) {
                        userInfo.setName(gname);
                    }
                }
            }
            if (msg.getFrom_uid() != null && msg.getFrom_uid().longValue() == UserAction.getMyId().longValue()) {
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
        if (session != null && (session.getUnread_count() > 0 || session.getMarkRead() > 0)) {
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
        LogUtil.getLog().i(TAG, "updateSessionDraftAndAtMessage");
        boolean hasChange = false;
        if (session != null && (!TextUtils.isEmpty(session.getAtMessage()) ||
                session.getMessageType() == ChatEnum.ESessionType.NEW_JOIN_GROUP)) {
            hasChange = true;
            dao.updateSessionAtMsg(toGid, toUId);
            if (session.getMessageType() == ChatEnum.ESessionType.NEW_JOIN_GROUP) {// 更新申请进群条数
                dao.clearRemidCount(Preferences.GROUP_FRIEND_APPLY, toGid);
            }
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
            hasChange = true;
            dao.sessionDraft(toGid, toUId, df);
            draft = df;
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
        // 是否被封号
        boolean isDisable = UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE ? true : false;
        actionbar.getBtnRight().setVisibility(isExited || isForbid ? View.GONE : View.VISIBLE);
        tvBan.setVisibility(isExited || isForbid || isDisable ? VISIBLE : GONE);
        if (isExited) {
            tvBan.setText("你已经被移除群聊，无法发送消息");
        } else if (isForbid) {
            tvBan.setText(AppConfig.getString(R.string.group_forbid));
        } else if (isDisable) {
            tvBan.setText(getResources().getString(R.string.user_disable_message));
        }
        boolean isSelecting = false;
        if (mAdapter != null) {
            if (mAdapter.isShowCheckBox()) {
                isSelecting = true;
            } else {
                llMore.setVisibility(GONE);
            }
        }
        viewChatBottomc.setVisibility(isExited || isForbid || isDisable || isSelecting ? GONE : VISIBLE);
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
        changeRightBtn(!b);
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

    /***
     * 红包是否已经被抢,红包改为失效
     * @param rid
     */
    private void updateEnvelopeDetail(MsgAllBean msgAllBean, String rid, int reType, String token, int envelopeStatus, int canReview) {
        if (envelopeStatus != PayEnum.EEnvelopeStatus.NORMAL) {
            msgAllBean.getRed_envelope().setIsInvalid(1);
            msgAllBean.getRed_envelope().setEnvelopStatus(envelopeStatus);
        }
        if (!TextUtils.isEmpty(token)) {
            msgAllBean.getRed_envelope().setAccessToken(token);
        }
        if (canReview == 1) {
            msgAllBean.getRed_envelope().setCanReview(canReview);
        }
        msgDao.updateEnvelopeDetail(rid, envelopeStatus, reType, token, canReview);
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                replaceListDataAndNotify(msgAllBean);
            }
        });
    }

    //抢红包后，更新红包token
    private void updateEnvelopeToken(MsgAllBean msgAllBean, final String rid, int reType, String token, int envelopeStatus) {
        if (!TextUtils.isEmpty(token)) {
            msgAllBean.getRed_envelope().setAccessToken(token);
            msgAllBean.getRed_envelope().setEnvelopStatus(envelopeStatus);
            if (envelopeStatus > 0) {
                msgAllBean.getRed_envelope().setIsInvalid(1);
            }
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
                            userInfo.setuType(UserUtil.getUserType(userInfo.getStat()));
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
            if (type == 0) {
                ToastUtil.show(this, "网络连接不可用，请稍后重试");
            }
            isOk = false;
        } else {
            isOk = SocketUtil.getSocketUtil().getOnlineState();
            if (!isOk) {
                if (type == 0) {
                    ToastUtil.show(this, "连接已断开，请稍后再试");
                }
            }
        }
        return isOk;
    }

    //抢红包，获取token
    public void grabRedEnvelope(MsgAllBean msgBean, long rid, int reType, final int envelopeStatus) {
        String from = "";
        if (isGroup()) {
            from = toGid;
        } else {
            if (msgBean != null && msgBean.getFrom_uid() != null) {
                from = msgBean.getFrom_uid().longValue() + "";
            }
        }
        if (TextUtils.isEmpty(from)) {
            return;
        }
        PayHttpUtils.getInstance().grabRedEnvelope(rid, from)
                .compose(RxSchedulers.<BaseResponse<GrabEnvelopeBean>>compose())
                .compose(RxSchedulers.<BaseResponse<GrabEnvelopeBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<GrabEnvelopeBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<GrabEnvelopeBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            GrabEnvelopeBean bean = baseResponse.getData();
                            int status = envelopeStatus;
                            if (bean != null) {
                                if (status == PayEnum.EEnvelopeStatus.NORMAL) {
                                    status = getGrabEnvelopeStatus(bean.getStat());
                                }
                                updateEnvelopeToken(msgBean, rid + "", reType, bean.getAccessToken(), status);
                                showEnvelopeDialog(bean.getAccessToken(), status, msgBean, reType);
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

    //抢定向红包，获取token
    public void grabRedEnvelopeNoAllow(MsgAllBean msgBean, long rid, int reType, final int envelopeStatus) {
        String from = "";
        if (isGroup()) {
            from = toGid;
        } else {
            if (msgBean != null && msgBean.getFrom_uid() != null) {
                from = msgBean.getFrom_uid().longValue() + "";
            }
        }
        if (TextUtils.isEmpty(from)) {
            return;
        }
        PayHttpUtils.getInstance().grabRedEnvelope(rid, from)
                .compose(RxSchedulers.<BaseResponse<GrabEnvelopeBean>>compose())
                .compose(RxSchedulers.<BaseResponse<GrabEnvelopeBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<GrabEnvelopeBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<GrabEnvelopeBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            GrabEnvelopeBean bean = baseResponse.getData();
                            int status = envelopeStatus;
                            if (bean != null) {
                                if (status == PayEnum.EEnvelopeStatus.NORMAL) {
                                    status = getGrabEnvelopeStatus(bean.getStat());
                                }
                                updateEnvelopeToken(msgBean, rid + "", reType, bean.getAccessToken(), status);
                                getEnvelopeDetail(rid, bean.getAccessToken(), envelopeStatus, msgBean, msgBean.isMe() ? true : false, false);
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
        } else if (stat == 4) {//未领到，出错了
            status = PayEnum.EEnvelopeStatus.ERROR;
        }
        return status;
    }

    //获取拆红包后，红包状态
    private int getOpenEnvelopeStatus(int stat) {
        int status = PayEnum.EEnvelopeStatus.NORMAL;
        if (stat == 0) {//1 正常待领取状态
            status = PayEnum.EEnvelopeStatus.NORMAL;
        } else if (stat == 1) {//1 领取
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

    //获取拆红包后，红包状态
    private int getOpenEnvelopeStatus(EnvelopeDetailBean bean) {
        int status = PayEnum.EEnvelopeStatus.NORMAL;
        //过期
        if (PayEnvironment.getInstance().getFixTime() * 1000 - bean.getTime() >= TimeToString.DAY) {
            status = PayEnum.EEnvelopeStatus.PAST;
            return status;
        }
        if (bean.getType() == 0) {//普通红包
            if (bean.getRecvList() != null) {
                int size = bean.getRecvList().size();
                if (size > 0) {
                    int count = bean.getCnt();
                    if (count == size) {
                        return PayEnum.EEnvelopeStatus.RECEIVED_FINISHED;
                    }
                }
            }
        } else {//拼手气红包
            if (bean.getRecvList() != null) {
                int size = bean.getRecvList().size();
                if (size > 0) {
                    int count = bean.getCnt();
                    if (count == size) {
                        return PayEnum.EEnvelopeStatus.RECEIVED_FINISHED;
                    }
                }
            }
        }
        return status;
    }

    private void showEnvelopeDialog(String token, int status, MsgAllBean msgBean, int reType) {
        DialogEnvelope dialogEnvelope = new DialogEnvelope(ChatActivity.this, com.hm.cxpay.R.style.MyDialogTheme);
        dialogEnvelope.setEnvelopeListener(new DialogEnvelope.IEnvelopeListener() {
            @Override
            public void onOpen(long rid, int envelopeStatus, boolean isLast) {
                //TODO: 开红包后，先发送领取红包消息给服务端，然后更新红包状态，最后保存领取红包通知消息到本地
                taskPayRbCheck(msgBean, rid + "", reType, token, getOpenEnvelopeStatus(envelopeStatus));
                if (envelopeStatus == 1) {//抢到了
                    if (!msgBean.isMe()) {
                        SocketData.sendReceivedEnvelopeMsg(msgBean.getFrom_uid(), toGid, rid + "", reType, isLast);//发送抢红包消息
                    }
                    MsgNotice message = SocketData.createMsgNoticeOfRb(SocketData.getUUID(), msgBean.getFrom_uid(), toGid, rid + "");
                    sendMessage(message, ChatEnum.EMessageType.NOTICE, false);
                }
            }

            @Override
            public void viewRecord(long rid, String token, int style) {
                getRedEnvelopeDetail(msgBean, rid, token, reType, style == 0, false);
            }

            @Override
            public void viewAllowUser() {
                Intent intent = ViewAllowMemberActivity.newIntent(ChatActivity.this, msgBean.getGid(), MessageManager.getInstance().getMemberIds(msgBean.getRed_envelope().getAllowUsers()));
                startActivity(intent);
            }
        });
        RedEnvelopeMessage message = msgBean.getRed_envelope();
        dialogEnvelope.setInfo(token, status, msgBean.getFrom_avatar(), msgBean.getFrom_nickname(), getEnvelopeId(message.getId(), message.getTraceId()), message.getComment(), message.getStyle());
        dialogEnvelope.show();
    }

    //获取红包详情
    public void getRedEnvelopeDetail(MsgAllBean msgBean, long rid, String token, int reType, boolean isNormalStyle, boolean hasPermission) {
        if (TextUtils.isEmpty(token) && (msgBean != null && !msgBean.isMe())) {
            String from = "";
            if (isGroup()) {
                from = toGid;
            } else {
                if (msgBean != null && msgBean.getFrom_uid() != null) {
                    from = msgBean.getFrom_uid().longValue() + "";
                }
            }
            if (TextUtils.isEmpty(from)) {
                return;
            }
            PayHttpUtils.getInstance().grabRedEnvelope(rid, from)
                    .compose(RxSchedulers.<BaseResponse<GrabEnvelopeBean>>compose())
                    .compose(RxSchedulers.<BaseResponse<GrabEnvelopeBean>>handleResult())
                    .subscribe(new FGObserver<BaseResponse<GrabEnvelopeBean>>() {
                        @Override
                        public void onHandleSuccess(BaseResponse<GrabEnvelopeBean> baseResponse) {
                            if (baseResponse.isSuccess()) {
                                GrabEnvelopeBean bean = baseResponse.getData();
                                if (bean != null) {
                                    if (isNormalStyle) {//普通玩法红包需要保存
                                        taskPayRbCheck(msgBean, rid + "", reType, bean.getAccessToken(), getGrabEnvelopeStatus(bean.getStat()));
                                    }
                                    getEnvelopeDetail(rid, token, msgBean.getRed_envelope().getEnvelopStatus(), msgBean, true, hasPermission);
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
            getEnvelopeDetail(rid, token, msgBean.getRed_envelope().getEnvelopStatus(), msgBean, hasPermission, hasPermission);
        }
    }

    private void getEnvelopeDetail(long rid, String token, int envelopeStatus, MsgAllBean msgBean, boolean isAllow, boolean hasPermission) {
        PayHttpUtils.getInstance().getEnvelopeDetail(rid, token, 0)
                .compose(RxSchedulers.<BaseResponse<EnvelopeDetailBean>>compose())
                .compose(RxSchedulers.<BaseResponse<EnvelopeDetailBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<EnvelopeDetailBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<EnvelopeDetailBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            EnvelopeDetailBean bean = baseResponse.getData();
                            if (bean != null) {
                                if (!hasPermission && (bean.getRecvList() != null && bean.getRecvList().size() > 0)) {
                                    updateEnvelopeDetail(msgBean, rid + "", msgBean.getRed_envelope().getRe_type(), token, envelopeStatus, 1);
                                } else {
                                    if (envelopeStatus == PayEnum.EEnvelopeStatus.NORMAL && envelopeStatus != getOpenEnvelopeStatus(bean)) {
                                        taskPayRbCheck(msgBean, rid + "", msgBean.getRed_envelope().getRe_type(), token, getOpenEnvelopeStatus(bean));
                                    }
                                }
                                bean.setChatType(isGroup() ? 1 : 0);
                                bean.setEnvelopeStatus(envelopeStatus);
                                if (!isAllow && (bean.getRecvList() != null && bean.getRecvList().size() <= 0)) {

                                } else {
                                    Intent intent = SingleRedPacketDetailsActivity.newIntent(ChatActivity.this, bean);
                                    startActivity(intent);
                                }
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
     * 获取账单详情
     */
    private void httpGetTransferDetail(String tradeId, int opType, MsgAllBean msgBean) {
        if (opType == PayEnum.ETransferOpType.TRANS_SEND && isSelfLock()) {
            ToastUtil.show(getString(R.string.user_disable_message));
            return;
        }
        showLoadingDialog();
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
                            initTransferMkName(detailBean);
                            Intent intent;
                            boolean isMe = false;
                            if (msgBean != null) {
                                isMe = msgBean.isMe();
                                if (opType == PayEnum.ETransferOpType.TRANS_SEND) {
                                    intent = TransferDetailActivity.newIntent(ChatActivity.this, detailBean, tradeId, isMe, GsonUtils.optObject(msgBean));
                                    if (opType < detailBean.getStat()) {
                                        int type = getTransferOpType(detailBean.getStat());
                                        msgDao.updateTransferStatus(tradeId, type, 0);
                                        replaceListDataAndNotify(msgBean);
                                    }
                                } else {
                                    intent = TransferDetailActivity.newIntent(ChatActivity.this, detailBean, tradeId, isMe);
                                }
                            } else {
                                intent = TransferDetailActivity.newIntent(ChatActivity.this, detailBean, tradeId, false);
                            }
                            startActivityForResult(intent, REQUEST_TRANSFER);
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse<TransferDetailBean> baseResponse) {
                        dismissLoadingDialog();
                        ToastUtil.show(context, baseResponse.getMessage());
                    }
                });
    }

    private void initTransferMkName(TransferDetailBean detailBean) {
        if (detailBean != null && detailBean.getPayUser() != null) {
            UserInfo userInfo = userDao.findUserInfo(detailBean.getPayUser().getUid());
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getMkName())) {
                detailBean.getPayUser().setNickname(userInfo.getMkName());
            }
        }
        if (detailBean != null && detailBean.getRecvUser() != null) {
            UserInfo userInfo = userDao.findUserInfo(detailBean.getRecvUser().getUid());
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getMkName())) {
                detailBean.getRecvUser().setNickname(userInfo.getMkName());
            }
        }
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
        String content = "你有一个" + time + " 金额为" + money + "的红包已扣款未发送成功,是否重新发送此红包？";
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
                            message = SocketData.createRbMessage(SocketData.getUUID(), info.getRid(), info.getComment(), info.getReType(), info.getEnvelopeStyle(), info.getAllowUsers());
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
        String content = "你有一个" + time + " 金额为" + money + "的红包未发送成功。已自动退回零钱红包账户";
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

    //选择文件(最大可选改为9个)
    private void toSelectFile() {
        FilePickerManager.INSTANCE
                .from(this)
                .maxSelectable(9)
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
        if (mAdapter.getItemCount() - 1 <= 15) {
            mtListView.setStackFromEnd(false);
        }
        mAdapter.notifyItemRemoved(position);//删除刷新
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
        deleteList.addAll(list);
        mAdapter.removeMsgList(list);
        if (mAdapter.getItemCount() - list.size() <= 15) {
            mtListView.setStackFromEnd(false);
        }
        removeUnreadCount(list.size());
        notifyData();
//        mAdapter.notifyDataSetChanged();
        //有面板，则滑到底部
        if (mViewModel.isInputText.getValue() || mViewModel.isOpenEmoj.getValue() || mViewModel.isOpenFuction.getValue()) {
            mtListView.scrollToEnd();
        } else {
            scrollListView(false);
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
        userAction.getSingleMemberInfo(toGid, Integer.parseInt(UserAction.getMyId() + ""), new CallBack<ReturnBean<SingleMeberInfoBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SingleMeberInfoBean>> call, Response<ReturnBean<SingleMeberInfoBean>> response) {
                super.onResponse(call, response);
                if (response != null && response.body() != null && response.body().isOk()) {
                    singleMeberInfoBean = response.body().getData();
                    //1 是否被单人禁言
                    if (singleMeberInfoBean.getShutUpDuration() == 0) {
                        //2 该群是否全员禁言
                        if (mViewModel.groupInfo != null && (mViewModel.groupInfo.getWordsNotAllowed() == 0
                                || mViewModel.groupInfo.getMaster().equals(UserAction.getMyId().toString()))) {
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

    private void toForward(List<MsgAllBean> list) {
        if (list != null && list.size() > 0) {
            onForwardActivity(ChatEnum.EForwardMode.ONE_BY_ONE, new Gson().toJson(list));
        }
        mAdapter.clearSelectedMsg();
        hideMultiSelect(ivForward);
    }

    //隐藏多选功能
    private void hideMultiSelect(ImageView iv) {
        if (iv == null) {
            return;
        }
        iv.postDelayed(new Runnable() {
            @Override
            public void run() {
                showViewMore(false);
                mAdapter.showCheckBox(false, true);
                mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());
            }
        }, 100);
    }

    /**
     * 多选转发
     */
    private void onForwardActivity(@ChatEnum.EForwardMode int model, String json) {
        if (TextUtils.isEmpty(json)) {
            return;
        }
        Intent intent = MsgForwardActivity.newIntent(this, model, json);
        startActivityForResult(intent, REQUEST_FORWORD);
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
            if (!isNoSendUser()) {
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
        try {
            switch (type) {
                case ChatEnum.ECellEventType.TXT_CLICK:
                    break;
                case ChatEnum.ECellEventType.IMAGE_CLICK:
                    if (args[0] != null && args[0] instanceof ImageMessage) {
                        ImageMessage image = (ImageMessage) args[0];
                        scanImageAndVideo(message.getMsg_id());
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
                    AudioPlayManager.getInstance().stopPlay();
                    break;
                case ChatEnum.ECellEventType.CARD_CLICK:
                    if (args[0] != null && args[0] instanceof BusinessCardMessage) {
                        BusinessCardMessage card = (BusinessCardMessage) args[0];
                        if (card.getUid() != null && card.getUid().longValue() != UserAction.getMyId().longValue()) { //TODO bugly #324411
                            if (isGroup() && !master.equals(card.getUid().toString())) {
                                startActivity(new Intent(getContext(), UserInfoActivity.class).putExtra(UserInfoActivity.ID,
                                        card.getUid()).putExtra(UserInfoActivity.IS_BUSINESS_CARD, contactIntimately));
                            } else {
                                startActivity(new Intent(getContext(), UserInfoActivity.class).putExtra(UserInfoActivity.ID, card.getUid()));
                            }
                        }
                    }
                    AudioPlayManager.getInstance().stopPlay();
                    break;
                case ChatEnum.ECellEventType.RED_ENVELOPE_CLICK:
                    clickEnvelope(message, message.getRed_envelope());
                    AudioPlayManager.getInstance().stopPlay();
                    break;
                case ChatEnum.ECellEventType.LONG_CLICK:
                    List<OptionMenu> menus = (List<OptionMenu>) args[0];
                    View v = (View) args[1];
                    IMenuSelectListener listener = (IMenuSelectListener) args[2];
//                    if (message.getMsg_type() == ChatEnum.EMessageType.TRANSFER_NOTICE) {
//                        return;
//                    }
                    showPop(v, menus, message, listener);
                    break;
                case ChatEnum.ECellEventType.TRANSFER_CLICK:
                    if (args[0] == null) {
                        return;
                    }
                    TransferMessage transfer = (TransferMessage) args[0];
                    UserBean userBean = PayEnvironment.getInstance().getUser();
                    if (userBean == null || userBean.getRealNameStat() != 1) {//未认证
                        showIdentifyDialog();
                        return;
                    }
                    httpGetTransferDetail(transfer.getId(), transfer.getOpType(), message);
                    AudioPlayManager.getInstance().stopPlay();
                    break;
                case ChatEnum.ECellEventType.AVATAR_CLICK:
                    if (isGroup() && !MessageManager.getInstance().isGroupValid(mViewModel.groupInfo)) {
                        return;
                    }
                    toUserInfoActivity(message.getFrom_uid());
                    AudioPlayManager.getInstance().stopPlay();
                    break;
                case ChatEnum.ECellEventType.AVATAR_LONG_CLICK:
                    if (isGroup()) {
                        if (!MessageManager.getInstance().isGroupValid(mViewModel.groupInfo)) {
                            return;
                        }
                        doAtInput(message);

                        //弹出软键盘
                        if (!mViewModel.isOpenValue()) //没有事件触发，设置改SoftInput模式为：顶起输入框
                            setWindowSoftMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
                    AudioPlayManager.getInstance().stopPlay();
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
                    AudioPlayManager.getInstance().stopPlay();
                    break;
                case ChatEnum.ECellEventType.MAP_CLICK:
                    LocationActivity.openActivity(ChatActivity.this, true, message);
                    AudioPlayManager.getInstance().stopPlay();
                    break;
                case ChatEnum.ECellEventType.FILE_CLICK:
                    if (args[0] == null) {
                        return;
                    }
                    SendFileMessage fileMessage = (SendFileMessage) args[0];
                    clickFile(message, fileMessage);
                    AudioPlayManager.getInstance().stopPlay();
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
                    AudioPlayManager.getInstance().stopPlay();
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
                                ToastUtil.show("你需要找的消息时间太久远了，请在消息记录中继续往上翻");
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
                        AudioPlayManager.getInstance().stopPlay();
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
                            ApkUtils.goBrowsable(ChatActivity.this, adMessage.getWebUrl());
                        }
                        AudioPlayManager.getInstance().stopPlay();
                    }
                    break;
                case ChatEnum.ECellEventType.SELECT_CLICK:
                    if (args[0] != null) {
                        List<MsgAllBean> selectList = (List<MsgAllBean>) args[0];
                        if (selectList.size() <= 0) {
                            ivForward.setEnabled(false);
                            ivDelete.setEnabled(false);
                            ivCollection.setEnabled(false);
                            ivForward.setAlpha(0.6f);
                            ivDelete.setAlpha(0.6f);
                            ivCollection.setAlpha(0.6f);
                        } else {
                            ivForward.setEnabled(true);
                            ivDelete.setEnabled(true);
                            ivCollection.setEnabled(true);
                            ivForward.setAlpha(1f);
                            ivDelete.setAlpha(1f);
                            ivCollection.setAlpha(1f);
                        }
                    }
                    break;
                case ChatEnum.ECellEventType.MULTI_CLICK:
                    break;
            }
        } catch (Exception e) {

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
            scanImageAndVideo(msg.getMsg_id());
        }
    }

    private void clickEnvelope(MsgAllBean msg, RedEnvelopeMessage rb) {
        final String rid = rb.getId();
        final int style = msg.getRed_envelope().getStyle();
        int reType = rb.getRe_type().intValue();//红包类型
        if (reType == MsgBean.RedEnvelopeType.SYSTEM_VALUE) {//零钱红包
            UserBean userBean = PayEnvironment.getInstance().getUser();
            if (userBean == null || userBean.getRealNameStat() != 1) {//未认证
                showIdentifyDialog();
                return;
            }
            int envelopeStatus = rb.getEnvelopStatus();

            if (envelopeStatus == PayEnum.EEnvelopeStatus.NORMAL && !checkCanOpenUpRedEnv()) {
//                ToastUtil.show(ChatActivity.this, "你已被禁止领取该群红包");
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
//            if (isGroup() && rb.getAllowUsers() != null && rb.getAllowUsers().size() > 0) {
//                MemberUser user = MessageManager.getInstance().userToMember(UserAction.getMyInfo(), toGid);
//                if (!rb.getAllowUsers().contains(user)) {
//                    envelopeStatus = PayEnum.EEnvelopeStatus.NO_ALLOW;
//                }
//            }
            boolean isNormalStyle = style == MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.NORMAL_VALUE;
            if (envelopeStatus == PayEnum.EEnvelopeStatus.NORMAL) {
                if (!rb.isHasPermission()) {
                    if (TextUtils.isEmpty(rb.getAccessToken())) {
                        grabRedEnvelopeNoAllow(msg, tradeId, reType, envelopeStatus);
                    } else {
                        boolean isAllow = false;
                        if (msg.isMe()) {
                            isAllow = true;
                        } else {
                            if (rb.getCanReview() == 1) {
                                isAllow = true;
                            }
                        }
                        getEnvelopeDetail(tradeId, rb.getAccessToken(), envelopeStatus, msg, isAllow, rb.isHasPermission());
                    }
                } else {
                    if (msg.isMe() && isNormalStyle && !isGroup()) {
                        getRedEnvelopeDetail(msg, tradeId, rb.getAccessToken(), reType, isNormalStyle, rb.isHasPermission());
                    } else {
                        grabRedEnvelope(msg, tradeId, reType, envelopeStatus);
                    }
                }
            } else if (envelopeStatus == PayEnum.EEnvelopeStatus.RECEIVED) {
                if (!rb.isHasPermission()) {
                    showEnvelopeDialog(rb.getAccessToken(), envelopeStatus, msg, reType);
                } else {
                    getRedEnvelopeDetail(msg, tradeId, rb.getAccessToken(), reType, isNormalStyle, rb.isHasPermission());

                }
            } else if (envelopeStatus == PayEnum.EEnvelopeStatus.RECEIVED_FINISHED || envelopeStatus == PayEnum.EEnvelopeStatus.RECEIVED_UNDONE) {
                if (msg.isMe() || !rb.isHasPermission()) {
                    getRedEnvelopeDetail(msg, tradeId, rb.getAccessToken(), reType, isNormalStyle, rb.isHasPermission());
                } else {
                    showEnvelopeDialog(rb.getAccessToken(), envelopeStatus, msg, reType);
                }
            } else if (envelopeStatus == PayEnum.EEnvelopeStatus.PAST) {
                if (msg.isMe() || !rb.isHasPermission()) {
                    getRedEnvelopeDetail(msg, tradeId, rb.getAccessToken(), reType, isNormalStyle, rb.isHasPermission());
                } else {
                    showEnvelopeDialog(rb.getAccessToken(), envelopeStatus, msg, reType);
                }
            } else if (envelopeStatus == PayEnum.EEnvelopeStatus.ERROR) {
                if (msg.isMe() || !rb.isHasPermission()) {
                    getRedEnvelopeDetail(msg, tradeId, rb.getAccessToken(), reType, isNormalStyle, rb.isHasPermission());
                } else {
                    showEnvelopeDialog(rb.getAccessToken(), envelopeStatus, msg, reType);
                }
            }
        }
    }

    public void showLockDialog() {
        LockDialog lockDialog = new LockDialog(this, R.style.MyDialogNoFadedTheme);
        lockDialog.setCancelable(true);
        lockDialog.setCanceledOnTouchOutside(true);
        lockDialog.create();
        lockDialog.show();
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
        LogUtil.getLog().i("ChatActivity", "未读数：" + num);
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
                            //正常情况data为null，若data不为null则代表有"源文件不存在"的情况
                            if (response.body().getData() == null) {
                                ToastUtil.showToast(ChatActivity.this, "已收藏", 1);
                                msgDao.addLocalCollection(collectionInfo);//添加到本地收藏列表
                            } else {
                                showMsgFailDialog();
                            }
                        } else {
                            ToastUtil.showToast(ChatActivity.this, response.body().getMsg(), 1);
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnBean> call, Throwable t) {
                        super.onFailure(call, t);
                        ToastUtil.showToast(ChatActivity.this, "收藏失败", 1);
                    }
                });
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
                            if (list != null) {
                                if (list.size() < 10) {
                                    mtListView.setStackFromEnd(false);
                                } else {
                                    mtListView.setStackFromEnd(true);
                                }
                            }
                            mAdapter.bindData(list, false);
                            mAdapter.setReadStatus(checkIsRead());
//                            notifyData();
                            //TODO：此时滚动会引起索引越界
                            mtListView.getListView().smoothScrollToPosition(0);
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
                            //小于10M，跳详情，自动下载+打开  TODO 新增需求改为全部先跳详情后，再自动下载并打开
                            if (fileMessage.getSize() < 10485760) {
                                Intent intent = new Intent(ChatActivity.this, FileDownloadActivity.class);
                                intent.putExtra("file_msg", new Gson().toJson(message));//直接整个MsgAllBean转JSON后传过去，方便后续刷新聊天消息
                                intent.putExtra("auto_download", true);//是否自动下载
                                startActivity(intent);
                            } else {
                                //大于10M，跳详情，用户自行选择手动下载
                                Intent intent = new Intent(ChatActivity.this, FileDownloadActivity.class);
                                intent.putExtra("file_msg", new Gson().toJson(message));
                                intent.putExtra("auto_download", false);
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
                        //小于10M，跳详情，自动下载+打开  TODO 新增需求改为全部先跳详情后，再自动下载并打开
                        if (fileMessage.getSize() < 10485760) {
                            Intent intent = new Intent(ChatActivity.this, FileDownloadActivity.class);
                            intent.putExtra("file_msg", new Gson().toJson(message));//直接整个MsgAllBean转JSON后传过去，方便后续刷新聊天消息
                            intent.putExtra("auto_download", true);//是否自动下载
                            startActivity(intent);
                        } else {
                            //大于10M，跳详情，用户自行选择手动下载
                            Intent intent = new Intent(ChatActivity.this, FileDownloadActivity.class);
                            intent.putExtra("file_msg", new Gson().toJson(message));
                            intent.putExtra("auto_download", false);
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
     *
     * @param view
     */
    private void showPopupWindow(View view) {
        //布局、view初始化、点击事件
        if (popGuessUWant != null && popGuessUWant.isShowing()) {
            popGuessUWant.dismiss();
        } else {
            View contentView = LayoutInflater.from(ChatActivity.this).inflate(
                    R.layout.layout_pop_guess_u_want_send, null);
            ImageView ivPic = contentView.findViewById(R.id.iv_want_send_pic);
            LinearLayout layoutPop = contentView.findViewById(R.id.layout_pop);
            //显示图片，并缓存地址
            Glide.with(ChatActivity.this).load(latestUrl).apply(GlideUtil.defImageOptions1()).into(ivPic);
            SharedPreferencesUtil spGuessYouLike = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.GUESS_YOU_LIKE);
            spGuessYouLike.saveString("current_img_url", latestUrl);

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
            popGuessUWant.showAtLocation(view, Gravity.RIGHT | Gravity.BOTTOM, DensityUtil.dip2px(getContext(), 5),//偏移调整右侧5dp
                    DensityUtil.dip2px(getContext(), 240) + layoutInputHeight + DensityUtil.dip2px(getContext(), 3));//偏移调整居下3dp
            //点击跳到预览界面
            layoutPop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<LocalMedia> previewList = new ArrayList<>();
                    LocalMedia localMedia = new LocalMedia();
                    localMedia.setPath(latestUrl);
                    previewList.add(localMedia);
                    PictureSelector.create(ChatActivity.this)
                            .openGallery(PictureMimeType.ofAll())
                            .compress(true);//复用，直接跳图片选择器的预览界面会崩溃，需要先初始化
                    previewImage(previewList, previewList, 0);
                    overridePendingTransition(com.luck.picture.lib.R.anim.a5, 0);
                    //跳到预览后关闭弹框
                    if (popGuessUWant != null && popGuessUWant.isShowing()) {
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
                        ApkUtils.goBrowsable(ChatActivity.this, downloadUrl);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
        dialogDownload.show();
    }


    //跳图片预览
    private void previewImage(List<LocalMedia> previewImages, List<LocalMedia> selectedImages, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PictureConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) previewImages);
        bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, (Serializable) selectedImages);
        bundle.putBoolean(PictureConfig.EXTRA_BOTTOM_PREVIEW, true);
        bundle.putInt(PictureConfig.EXTRA_POSITION, position);
        bundle.putInt(PictureConfig.FROM_WHERE, PictureConfig.FROM_GUESS_YOU_LIKE);//跳转来源 0 默认 1 猜你想要 2 收藏详情
        Intent intent = new Intent(ChatActivity.this, PicturePreviewActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, PictureConfig.PREVIEW_FROM_CHAT);
    }

    @PayEnum.ETransferOpType
    private int getTransferOpType(int stat) {
        switch (stat) {
            case 2:
                return PayEnum.ETransferOpType.TRANS_RECEIVE;
            case 3:
                return PayEnum.ETransferOpType.TRANS_REJECT;
            case 4:
                return PayEnum.ETransferOpType.TRANS_PAST;
            default:
                return PayEnum.ETransferOpType.TRANS_SEND;
        }
    }

    //是否是不需要真发送到服务器的用户，如常信小助手或零钱小助手
    private boolean isNoSendUser() {
        if (TextUtils.isEmpty(toGid) && toUId != null && (Constants.CX_HELPER_UID.equals(toUId) || Constants.CX_BALANCE_UID.equals(toUId))) {
            return true;
        }
        return false;
    }

    private void showDeleteDialog(List<MsgAllBean> msgList) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        DialogCommon dialogDelete = new DialogCommon(this);
        dialogDelete.setTitleAndSure(false, true)
                .setContent("确定删除？", true)
                .setLeft("取消")
                .setRight("删除")
                .setListener(new DialogCommon.IDialogListener() {
                    @Override
                    public void onSure() {
                        if (MyAppLication.INSTANCE().repository != null) {
                            MyAppLication.INSTANCE().repository.deleteMsgList(msgList);
                        }
                        mAdapter.removeMsgList(msgList);
                        mAdapter.clearSelectedMsg();
                        notifyData();
                        hideMultiSelect(ivDelete);
                    }

                    @Override
                    public void onCancel() {
                    }
                }).show();
    }

    /**
     * 注销提示弹框
     */
    private void showLogOutDialog(int type) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        String content = "";
        if (type == 1) {
            content = "该账号正在注销中，为了保障你的资金安全，\n暂时无法交易";
        } else if (type == -1) {
            content = "该账号已注销，为了保障你的资金安全，\n暂时无法交易";
        }
        dialogOne = builder.setTitle(content)
                .setShowLeftText(false)
                .setRightText("确定")
                .setRightOnClickListener(v -> dialogOne.dismiss())
                .build();
        dialogOne.show();
    }

    /**
     * 批量收藏提示弹框
     *
     * @param type 0 默认提示  1 包含收藏成功文案
     */
    private void showCollectListDialog(int type) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        String content;
        if (type == 1) {
            content = "收藏成功\n\n你所选的消息包含了不支持收藏的类型\n或已失效，系统已自动过滤此类型消息。";
        } else {
            content = "你所选的消息包含了不支持收藏的类型\n或已失效，系统已自动过滤此类型消息。";
        }
        dialogTwo = builder.setTitle(content)
                .setShowLeftText(false)
                .setRightText("确定")
                .setRightOnClickListener(v -> {
                    dialogTwo.dismiss();
                })
                .build();
        dialogTwo.show();
    }

    /**
     * 批量转发提示弹框
     */
    private void showForwardListDialog(List<MsgAllBean> list) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        dialogThree = builder.setTitle("你所选的消息包含了不支持转发的类型\n或已失效，系统已自动过滤此类型消息。")
                .setShowLeftText(true)
                .setRightText("继续发送")
                .setLeftText("取消")
                .setRightOnClickListener(v -> {
                    toForward(list);
                    dialogThree.dismiss();
                })
                .setLeftOnClickListener(v ->
                        dialogThree.dismiss()
                )
                .build();
        dialogThree.show();
    }

    /**
     * 单选转发/收藏失效消息提示弹框
     */
    private void showMsgFailDialog() {
        if (activity == null || activity.isFinishing()) { //TODO #284439
            return;
        }
        dialogFour = builder.setTitle("你所选的消息已失效")
                .setShowLeftText(false)
                .setRightText("确定")
                .setRightOnClickListener(v -> {
                    dialogFour.dismiss();
                })
                .build();
        dialogFour.show();
    }

    /**
     * 是否撤销提示弹框
     */
    private void cancelInviteDialog(List<UserInfo> list) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        int oldNum;//邀请了几个人
        oldNum = list.size();
        List<UserInfo> filterList = new ArrayList<>();
        //1 先过滤掉已经被移除的群员
        //查找出该群所有成员
        Group group = new MsgDao().groupNumberGet(toGid);
        if (group.getUsers() != null && group.getUsers().size() > 0) {
            //查找邀请入群的成员是否仍在群中
            for (UserInfo userInfo : list) {
                //如果邀请入群的成员仍在群中，获取其头像，下个界面需要显示
                if (new MsgDao().inThisGroup(toGid, userInfo.getUid().longValue())) {
                    for (MemberUser user : group.getUsers()) {
                        if (userInfo.getUid().longValue() == user.getUid()) {
                            //找到并更新头像
                            if (!TextUtils.isEmpty(user.getHead())) {
                                userInfo.setHead(user.getHead());
                            } else {
                                userInfo.setHead("");
                            }
                            break;
                        }
                    }
                } else {
                    //如果这个成员已经不在群中，需要过滤
                    filterList.add(userInfo);
                }
            }
            //循环查找完后，过滤掉已经被移除的群员，此时list为最新值
            if (filterList.size() > 0) {
                list.removeAll(filterList);
            }
        }
        //"该成员已离开群聊"
        if (list.size() == 0) {
            String notice;
            if (oldNum == 1) {
                notice = "该成员已离开群聊";
            } else {
                notice = "成员已离开群聊";
            }
            dialogSix = builder.setTitle(notice)
                    .setShowLeftText(false)
                    .setRightText("确定")
                    .setRightOnClickListener(v -> {
                        dialogSix.dismiss();
                    })
                    .build();
            dialogSix.show();
        } else if (list.size() == 1) {
            if (oldNum > 1) {
                Intent intent = new Intent(ChatActivity.this, InviteRemoveActivity.class);
                intent.putExtra(InviteRemoveActivity.USER_LIST, new Gson().toJson(list));
                intent.putExtra("gid", toGid);
                startActivity(intent);
            } else {
                dialogFive = builder.setTitle("将" + list.get(0).getName() + "移出群聊？")
                        .setShowLeftText(true)
                        .setLeftText("取消")
                        .setRightText("移出群聊")
                        .setLeftOnClickListener(v -> {
                            dialogFive.dismiss();
                        })
                        .setRightOnClickListener(v -> {
                            String name = "";
                            String rname = "";
                            if (list.get(0) != null) {
                                if (!TextUtils.isEmpty(list.get(0).getName())) {
                                    name = list.get(0).getName();
                                }
                            }
                            //撤销邀请
                            for (UserInfo userInfo : list) {
                                rname += "<font id='" + userInfo.getUid() + "'>" + userInfo.getName() + "</font>";
                            }
                            String finalName = rname;//被删除人的昵称
                            msgAction.httpCancelInvite(toGid, name, list.get(0).getUid(), new CallBack<ReturnBean>() {
                                @Override
                                public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                                    if (response.body() == null) {
                                        return;
                                    } else {
                                        if (response.body().isOk()) {
                                            String mid = SocketData.getUUID();
                                            MsgNotice note = new MsgNotice();
                                            note.setMsgid(mid);
                                            note.setMsgType(3);
                                            note.setNote("你将\"" + finalName + "\"移出群聊");
                                            dao.noteMsgAddRb(mid, UserAction.getMyId(), toGid, note);
                                            taskGroupInfo();
                                            taskRefreshMessage(false);
                                        } else {
                                            if (!TextUtils.isEmpty(response.body().getMsg())) {
                                                ToastUtil.show(response.body().getMsg());
                                            }
                                        }
                                        dialogFive.dismiss();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ReturnBean> call, Throwable t) {
                                    ToastUtil.show(t.getMessage());
                                }
                            });
                        })
                        .build();
                dialogFive.show();
            }

        } else {
            Intent intent = new Intent(ChatActivity.this, InviteRemoveActivity.class);
            intent.putExtra(InviteRemoveActivity.USER_LIST, new Gson().toJson(list));
            intent.putExtra("gid", toGid);
            startActivity(intent);
        }

    }

    private void toSendVerifyActivity(Long uid) {
        IUser myInfo = UserAction.getMyInfo();
        if (myInfo == null) {
            return;
        }
        UserInfo userInfo = userAction.getUserInfoInLocal(uid);
        if (userInfo != null && userInfo.getuType() == ChatEnum.EUserType.FRIEND) {
            if (uid != null) {
                toUserInfoActivity(uid);
            }
        } else {
            String content = "我是" + myInfo.getName();
            Intent intent = new Intent(ChatActivity.this, FriendVerifyActivity.class);
            intent.putExtra(FriendVerifyActivity.CONTENT, content);
            intent.putExtra(FriendVerifyActivity.USER_ID, uid);
            if (userInfo != null) {
                intent.putExtra(FriendVerifyActivity.NICK_NAME, userInfo.getName());
            }
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void switchAppStatus(boolean isRun) {
        if (!isRun) {
            onlineState = false;
        }
        if (msgEvent == null) {
            return;
        }
        LogUtil.getLog().i(TAG, "连接LOG->>>>switchAppStatus--注册监听:" + "--time=" + System.currentTimeMillis());
        if (isRun) {
            SocketUtil.getSocketUtil().addEvent(msgEvent);
        } else {
            SocketUtil.getSocketUtil().removeEvent(msgEvent);
        }
    }

    /**
     * 批量收藏  (流程有变化，过滤掉不支持类型后，先调接口，再弹框，目前需求只显示一次)
     *
     * @param list     已过滤后的数据
     * @param isNormal 正常类型true 含有不支持类型false
     */
    public void toCollectList(List<MsgAllBean> list, boolean isNormal) {
        if (list.size() > 0) {
            List<CollectionInfo> dataList = convertCollectBean(list);
            if (dataList != null && dataList.size() > 0) {
                //1 有网收藏
                if (checkNetConnectStatus(1)) {
                    msgAction.offlineAddCollections(dataList, new CallBack<ReturnBean>() {
                        @Override
                        public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                            super.onResponse(call, response);
                            if (response.body() == null) {
                                return;
                            }
                            if (response.body().isOk()) {
                                if (isNormal) {
                                    // data!=null代表有"源文件不存在"情况，提示弹框
                                    if (response.body().getData() != null) {
                                        showCollectListDialog(1);
                                    } else {
                                        ToastUtil.show("收藏成功");
                                    }
                                } else {
                                    //用户选过不支持的类型，因此无论如何都要提示弹框
                                    showCollectListDialog(1);
                                }
                            } else {
                                if (!TextUtils.isEmpty(response.body().getMsg())) {
                                    ToastUtil.show(response.body().getMsg() + "");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ReturnBean> call, Throwable t) {
                            super.onFailure(call, t);
                            ToastUtil.show("收藏失败");
                        }
                    });
                } else {
                    //2 无网收藏
                    //2-1 如果本地收藏列表不存在这条数据，收藏到列表，并保存收藏操作记录
                    for (CollectionInfo info : dataList) {
                        if (msgDao.findLocalCollection(info.getMsgId()) == null) {
                            msgDao.addLocalCollection(info);//保存到本地收藏列表
                            OfflineCollect offlineCollect = new OfflineCollect();
                            offlineCollect.setMsgId(info.getMsgId());
                            offlineCollect.setCollectionInfo(info);
                            msgDao.addOfflineCollectRecord(offlineCollect);//保存到离线收藏记录表
                        }
                    }
                    //2-2 如果本地收藏列表存在这条数据，无需再重复收藏，不做任何操作
                    if (isNormal) {
                        ToastUtil.show("收藏成功");//离线提示
                    } else {
                        //用户选过不支持的类型，因此无论如何都要提示弹框
                        showCollectListDialog(1);
                    }
                }
            }
            mAdapter.clearSelectedMsg();
            hideMultiSelect(ivCollection);
        }
    }


    /**
     * 过滤多选转发消息
     *
     * @param sourList  源数据
     * @param isOverdue 是否已经过滤掉了过期文件(是否含有不存在文件)
     */
    @SuppressLint("CheckResult")
    private void filterMsgForward(final List<MsgAllBean> sourList, boolean isOverdue) {
        int totalSize = mAdapter.getSelectedMsg().size();
        int sourLen = sourList.size();
        Observable.just(0)
                .map(new Function<Integer, List<MsgAllBean>>() {
                    @Override
                    public List<MsgAllBean> apply(Integer integer) throws Exception {
                        if (sourLen > 0) {
                            String[] msgIds = new String[sourLen];
                            for (int i = 0; i < sourLen; i++) {
                                MsgAllBean msgAllBean = sourList.get(i);
                                msgIds[i] = msgAllBean.getMsg_id();
                            }
                            return msgDao.filterMsgForForward(msgIds);
                        } else {
                            return new ArrayList<MsgAllBean>();
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<MsgAllBean>>empty())
                .subscribe(new Consumer<List<MsgAllBean>>() {
                    @Override
                    public void accept(List<MsgAllBean> list) throws Exception {
                        if (list != null) {
                            int len = list.size();
                            if (len > 0) {
                                //只要含有一个正常类型消息
                                if (len == totalSize) {
                                    if (isOverdue) {
                                        showForwardListDialog(list);//存在不支持类型或失效的转发
                                    } else {
                                        toForward(list);//正常类型转发
                                    }
                                } else if (len < totalSize) {
                                    showForwardListDialog(list);//存在不支持类型或失效的转发
                                }
                            } else {
                                //全为不支持类型或失效消息
                                showValidMsgDialog(false);
                            }
                        } else {
                            showValidMsgDialog(false);
                        }
                    }
                });
    }

    /**
     * 过滤多选收藏消息
     *
     * @param sourList  源数据
     * @param isOverdue 是否已经过滤掉了过期文件(是否含有不存在文件)
     */
    @SuppressLint("CheckResult")
    private void filterMsgCollection(final List<MsgAllBean> sourList, boolean isOverdue) {
        int totalSize = mAdapter.getSelectedMsg().size();
        int sourLen = sourList.size();
        Observable.just(0)
                .map(new Function<Integer, List<MsgAllBean>>() {
                    @Override
                    public List<MsgAllBean> apply(Integer integer) throws Exception {
                        if (sourLen > 0) {
                            String[] msgIds = new String[sourLen];
                            for (int i = 0; i < sourLen; i++) {
                                MsgAllBean msgAllBean = sourList.get(i);
                                msgIds[i] = msgAllBean.getMsg_id();
                            }
                            return msgDao.filterMsgForCollection(msgIds);
                        } else {
                            return new ArrayList<MsgAllBean>();
                        }

                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<MsgAllBean>>empty())
                .subscribe(new Consumer<List<MsgAllBean>>() {
                    @Override
                    public void accept(List<MsgAllBean> list) throws Exception {
                        if (list != null) {
                            int len = list.size();
                            if (len > 0) {
                                //只要含有一个正常类型消息
                                if (len == totalSize) {
                                    if (isOverdue) {
                                        toCollectList(list, false);//存在不支持类型或失效的收藏
                                    } else {
                                        toCollectList(list, true);//正常类型收藏
                                    }
                                } else if (len < totalSize) {
                                    toCollectList(list, false);//存在不支持类型或失效的收藏
                                }
                            } else {
                                //全为不支持类型或失效消息
                                showValidMsgDialog(true);
                            }
                        } else {
                            showValidMsgDialog(true);
                        }
                    }
                });
    }

    private void showValidMsgDialog(boolean clear) {
        DialogCommon2 dialogValid = new DialogCommon2(this);
        dialogValid.setContent("你选的消息包含不支持消息或已失效", true)
                .setButtonTxt("确定")
                .hasTitle(false)
                .setListener(new DialogCommon2.IDialogListener() {
                    @Override
                    public void onClick() {
                        dialogValid.dismiss();
                        if (clear) {
                            mAdapter.clearSelectedMsg();
                            hideMultiSelect(ivCollection);
                        }
                    }
                }).show();
    }

    //检测图片，语音，文件，视频消息是否过期，过期则过滤。 action 1 转发，2收藏
    @SuppressLint("CheckResult")
    private void filterMessageValid(List<MsgAllBean> sourList, int action) {
        int sourLen = sourList.size();
        Observable.just(0)
                .map(new Function<Integer, List<MsgAllBean>>() {
                    @Override
                    public List<MsgAllBean> apply(Integer integer) throws Exception {
                        if (sourLen > 0) {
                            String[] msgIds = new String[sourLen];
                            for (int i = 0; i < sourLen; i++) {
                                MsgAllBean msgAllBean = sourList.get(i);
                                msgIds[i] = msgAllBean.getMsg_id();
                            }
                            List<MsgAllBean> uploadMessages = msgDao.getUploadMessage(msgIds);
                            if (uploadMessages != null) {
                                int len = uploadMessages.size();
                                if (len > 0) {
                                    ArrayList<FileBean> fileBeans = new ArrayList<>();
                                    Map<String, MsgAllBean> filterMsgList = new HashMap<>();
                                    for (int i = 0; i < len; i++) {
                                        MsgAllBean msgAllBean = uploadMessages.get(i);
                                        String md5 = "";
                                        String url = "";
                                        if (msgAllBean.getImage() != null) {
                                            md5 = UpFileUtil.getInstance().getFilePathMd5(msgAllBean.getImage().getPreview());
                                            url = UpFileUtil.getInstance().getFileUrl(msgAllBean.getImage().getPreview());
                                        } else if (msgAllBean.getVideoMessage() != null) {
                                            //添加第一帧背景图
                                            FileBean fileBean = new FileBean();
                                            fileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgAllBean.getVideoMessage().getBg_url()));
                                            fileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgAllBean.getVideoMessage().getBg_url()));
                                            fileBeans.add(fileBean);
                                            //视频源文件
                                            md5 = UpFileUtil.getInstance().getFilePathMd5(msgAllBean.getVideoMessage().getUrl());
                                            url = UpFileUtil.getInstance().getFileUrl(msgAllBean.getVideoMessage().getUrl());
                                        } else if (msgAllBean.getSendFileMessage() != null) {
                                            md5 = UpFileUtil.getInstance().getFilePathMd5(msgAllBean.getSendFileMessage().getUrl());
                                            url = UpFileUtil.getInstance().getFileUrl(msgAllBean.getSendFileMessage().getUrl());
                                        }
                                        if (!TextUtils.isEmpty(md5)) {
                                            FileBean fileBean = new FileBean();
                                            fileBean.setMd5(md5);
                                            fileBean.setUrl(url);
                                            fileBeans.add(fileBean);
                                            filterMsgList.put(md5, msgAllBean);
                                        }
                                    }

                                    if (fileBeans.size() > 0) {
                                        UpFileUtil.getInstance().batchFileCheck(fileBeans, new CallBack<ReturnBean<List<String>>>() {
                                            @Override
                                            public void onResponse(Call<ReturnBean<List<String>>> call, Response<ReturnBean<List<String>>> response) {
                                                super.onResponse(call, response);
                                                if (response != null && response.body() != null) {
                                                    ReturnBean returnButton = response.body();
                                                    if (returnButton != null && returnButton.isOk()) {
                                                        List<String> urls = response.body().getData();
                                                        int size = urls.size();
                                                        if (size == fileBeans.size()) {
                                                            //都未过期
                                                            if (action == 1) {
                                                                filterMsgForward(sourList, false);
                                                            } else if (action == 2) {
                                                                filterMsgCollection(sourList, false);
                                                            }
                                                        } else {
                                                            for (int i = 0; i < size; i++) {
                                                                String md5 = urls.get(i);
                                                                filterMsgList.remove(md5);
                                                            }
                                                            if (filterMsgList.size() > 0) {
                                                                Iterator iterator = filterMsgList.keySet().iterator();
                                                                while (iterator.hasNext()) {
                                                                    MsgAllBean bean = filterMsgList.get(iterator.next().toString());
                                                                    sourList.remove(bean);
                                                                }
                                                            }
                                                            if (action == 1) {
                                                                filterMsgForward(sourList, true);//是否过滤掉了过期文件
                                                            } else if (action == 2) {
                                                                filterMsgCollection(sourList, true);
                                                            }
                                                        }
                                                    } else {//都是过期的
                                                        if (filterMsgList.size() > 0) {
                                                            Iterator iterator = filterMsgList.keySet().iterator();
                                                            while (iterator.hasNext()) {
                                                                MsgAllBean bean = filterMsgList.get(iterator.next().toString());
                                                                sourList.remove(bean);
                                                            }
                                                        }
                                                        if (action == 1) {
                                                            filterMsgForward(sourList, true);
                                                        } else if (action == 2) {
                                                            filterMsgCollection(sourList, true);
                                                        }
                                                    }
                                                } else {
                                                    if (action == 1) {
                                                        ToastUtil.show("转发失败");
//                                                        filterMsgForward(sourList);
                                                    } else if (action == 2) {
                                                        ToastUtil.show("收藏失败");
//                                                        filterMsgCollection(sourList);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ReturnBean<List<String>>> call, Throwable t) {
                                                super.onFailure(call, t);
                                                if (action == 1) {
                                                    ToastUtil.show("转发失败");
//                                                        filterMsgForward(sourList);
                                                } else if (action == 2) {
                                                    ToastUtil.show("收藏失败");
//                                                        filterMsgCollection(sourList);
                                                }
                                            }
                                        });
                                    } else {
                                        if (action == 1) {
                                            filterMsgForward(sourList, false);
                                        } else if (action == 2) {
                                            filterMsgCollection(sourList, false);
                                        }
                                    }
                                } else {
                                    if (action == 1) {
                                        filterMsgForward(sourList, false);
                                    } else if (action == 2) {
                                        filterMsgCollection(sourList, false);
                                    }
                                }
                            } else {
                                if (action == 1) {
                                    filterMsgForward(sourList, false);
                                } else if (action == 2) {
                                    filterMsgCollection(sourList, false);
                                }
                            }
                        }
                        return null;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<MsgAllBean>>empty())
                .subscribe(new Consumer<List<MsgAllBean>>() {
                    @Override
                    public void accept(List<MsgAllBean> list) throws Exception {
                    }
                });
    }

    //多选转发消息过多
    private void showMoreMsgDialog() {
        DialogCommon2 dialogValid = new DialogCommon2(this);
        dialogValid.setContent("转发消息不能超过30条", true)
                .setButtonTxt("确定")
                .hasTitle(false)
                .setListener(new DialogCommon2.IDialogListener() {
                    @Override
                    public void onClick() {
                        dialogValid.dismiss();
                    }
                }).show();
    }

}
