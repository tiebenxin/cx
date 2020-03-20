package com.yanlong.im.chat.ui;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.nim_lib.config.Preferences;
import com.example.nim_lib.controll.AVChatProfile;
import com.example.nim_lib.ui.VideoActivity;
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
import com.hm.cxpay.ui.bill.BillDetailActivity;
import com.hm.cxpay.ui.payword.SetPaywordActivity;
import com.hm.cxpay.ui.redenvelope.MultiRedPacketActivity;
import com.hm.cxpay.ui.redenvelope.SingleRedPacketActivity;
import com.hm.cxpay.ui.transfer.TransferActivity;
import com.hm.cxpay.ui.transfer.TransferDetailActivity;
import com.hm.cxpay.utils.UIUtils;
import com.jrmf360.rplib.JrmfRpClient;
import com.jrmf360.rplib.bean.EnvelopeBean;
import com.jrmf360.rplib.bean.GrabRpBean;
import com.jrmf360.rplib.bean.TransAccountBean;
import com.jrmf360.rplib.utils.callback.GrabRpCallBack;
import com.jrmf360.rplib.utils.callback.TransAccountCallBack;
import com.jrmf360.tools.utils.ThreadUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.DoubleUtils;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.yalantis.ucrop.util.FileUtils;
import com.yanlong.im.BuildConfig;
import com.yanlong.im.R;
import com.yanlong.im.adapter.AdapterPopMenu;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.EventSurvivalTimeAdd;
import com.yanlong.im.chat.MsgTagHandler;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.AtMessage;
import com.yanlong.im.chat.bean.BusinessCardMessage;
import com.yanlong.im.chat.bean.ChatMessage;
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
import com.yanlong.im.chat.bean.ReadDestroyBean;
import com.yanlong.im.chat.bean.RedEnvelopeMessage;
import com.yanlong.im.chat.bean.ScrollConfig;
import com.yanlong.im.chat.bean.SendFileMessage;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.ShippedExpressionMessage;
import com.yanlong.im.chat.bean.SingleMeberInfoBean;
import com.yanlong.im.chat.bean.StampMessage;
import com.yanlong.im.chat.bean.TransferMessage;
import com.yanlong.im.chat.bean.UserSeting;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.eventbus.AckEvent;
import com.yanlong.im.chat.eventbus.EventSwitchSnapshot;
import com.yanlong.im.chat.interf.IActionTagClickListener;
import com.yanlong.im.chat.interf.IMenuSelectListener;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.server.ChatServer;
import com.yanlong.im.chat.server.UpLoadService;
import com.yanlong.im.chat.ui.cell.ControllerNewMessage;
import com.yanlong.im.chat.ui.chat.ChatViewModel;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.chat.ui.groupmanager.GroupMemPowerSetActivity;
import com.yanlong.im.chat.ui.view.ChatItemView;
import com.yanlong.im.chat.ui.view.ControllerLinearList;
import com.yanlong.im.location.LocationActivity;
import com.yanlong.im.location.LocationSendEvent;
import com.yanlong.im.pay.action.PayAction;
import com.yanlong.im.pay.bean.SignatureBean;
import com.yanlong.im.pay.ui.record.SingleRedPacketDetailsActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.SelectUserActivity;
import com.yanlong.im.user.ui.ServiceAgreementActivity;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.BurnManager;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.DestroyTimeView;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.GroupHeadImageUtil;
import com.yanlong.im.utils.HtmlTransitonUtils;
import com.yanlong.im.utils.PatternUtil;
import com.yanlong.im.utils.ReadDestroyUtil;
import com.yanlong.im.utils.UserUtil;
import com.yanlong.im.utils.audio.AudioPlayManager;
import com.yanlong.im.utils.audio.AudioRecordManager;
import com.yanlong.im.utils.audio.IAdioTouch;
import com.yanlong.im.utils.audio.IAudioRecord;
import com.yanlong.im.utils.audio.IVoicePlayListener;
import com.yanlong.im.utils.socket.MsgBean;
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
import com.zhaoss.weixinrecorded.activity.RecordedActivity;
import com.zhaoss.weixinrecorded.util.ActivityForwordEvent;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.EventFindHistory;
import net.cb.cb.library.bean.EventGroupChange;
import net.cb.cb.library.bean.EventIsShowRead;
import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.bean.EventSwitchDisturb;
import net.cb.cb.library.bean.EventUpFileLoadEvent;
import net.cb.cb.library.bean.EventUpImgLoadEvent;
import net.cb.cb.library.bean.EventUserOnlineChange;
import net.cb.cb.library.bean.EventVoicePlay;
import net.cb.cb.library.bean.ReturnBean;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmList;
import me.kareluo.ui.OptionMenu;
import me.rosuh.filepicker.config.FilePickerManager;
import retrofit2.Call;
import retrofit2.Response;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static net.cb.cb.library.utils.FileUtils.SIZETYPE_B;

public class ChatActivity extends AppActivity implements IActionTagClickListener {
    private static String TAG = "ChatActivity";
    public final static int MIN_TEXT = 1000;//
    private final int RELINQUISH_TIME = 5;// 5分钟内显示重新编辑
    private final String REST_EDIT = "重新编辑";
    private final String IS_VIP = "1";// (0:普通|1:vip)
    public final static int MIN_UNREAD_COUNT = 15;
    private int MAX_UNREAD_COUNT = 80 * 4;//默认加载最大数据

    private List<String> uidList;


    //返回需要刷新的 8.19 取消自动刷新
    // public static final int REQ_REFRESH = 7779;
    private HeadView2 headView;
    private ActionbarView actionbar;
    private net.cb.cb.library.view.MultiListView mtListView;
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

    private Gson gson = new Gson();
    private CheckPermission2Util permission2Util = new CheckPermission2Util();

    private Long toUId = null;
    private String toGid = null;
    private boolean onlineState = true;//判断网络状态 true在线 false离线
    //当前页
    //private int indexPage = 0;
    private List<MsgAllBean> msgListData = new ArrayList<>();
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
    private TextView tv_ban;
    private String draft;
    private int isFirst;
    private UserInfo mFinfo;// 聊天用户信息，刷新时更新
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
    private RecyclerViewAdapter mAdapter;
    private ChatExtendMenuView viewExtendFunction;
    private Group groupInfo;
    private int currentScrollPosition;

    private ScreenShotListenManager screenShotListenManager;//截屏监听相关
    private boolean isScreenShotListen;//是否监听截屏
    private ControllerLinearList popController;
    //记录软键盘高度
    private String KEY_BOARD = "keyboard_setting";
    //软键盘高度
    private int mKeyboardHeight = 0;

    private ChatViewModel mViewModel = new ChatViewModel();

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
        initObserver();
        initEvent();
        initSurvivaltime4Uid();
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
                    //重置其他状态
                    mViewModel.recoveryOtherValue(mViewModel.isOpenSpeak);
                    showVoice(true);
                } else {//关闭
                    showVoice(false);
                }
            }
        });
    }

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
        taskSessionInfo(false);

        clickAble = true;
        //更新阅后即焚状态
        initSurvivaltimeState();
        sendRead();
        if (AppConfig.isOnline()) {
            checkHasEnvelopeSendFailed();
        }
        isScreenShotListen = checkSnapshotPower();
        if (isScreenShotListen) {
            initScreenShotListener();
        }
        editChat.clearFocus();
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
        AudioPlayManager.getInstance().stopPlay();
        if (currentPlayBean != null) {
            updatePlayStatus(currentPlayBean, 0, ChatEnum.EPlayStatus.NO_PLAY);
        }
        boolean hasClear = taskCleanRead(false);
        boolean hasUpdate = dao.updateMsgRead(toUId, toGid, true);
        boolean hasChange = updateSessionDraftAndAtMessage();
//        LogUtil.getLog().e("===hasClear="+hasClear+"==hasUpdate="+hasUpdate+"==hasChange="+hasChange);
        if (hasClear || hasUpdate || hasChange) {
            MessageManager.getInstance().setMessageChange(true);
            MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, null);
        }
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
        mAdapter.onDestory();
        //关闭窗口，避免内存溢出
        dismissPop();

        List<MsgAllBean> list = msgDao.getMsg4SurvivalTimeAndExit(toGid, toUId);
        EventBus.getDefault().post(new EventSurvivalTimeAdd(null, list));
        //取消监听
        SocketUtil.getSocketUtil().removeEvent(msgEvent);
        EventBus.getDefault().unregister(this);
//        LogUtil.getLog().e(TAG, "onDestroy");
        super.onDestroy();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!msgDao.isMsgLockExist(toGid, toUId)) {
            msgDao.insertOrUpdateMessage(SocketData.createMessageLock(toGid, toUId));
        }
//        Log.i(TAG, "onStart");
        initData();

    }

    private void initData() {
        //9.17 进去后就清理会话的阅读数量,初始化unreadCount
        taskCleanRead(true);
        initViewNewMsg();
        if (!isLoadHistory) {
            taskRefreshMessage(false);
        }
        initUnreadCount();
        initPopupWindow();
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
            if (groupInfo != null) {
                return groupInfo.getScreenshotNotification() == 1;
            }
        } else {
            if (mFinfo != null) {
                return mFinfo.getScreenshotNotification() == 1;
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
        tv_ban = findViewById(R.id.tv_ban);
        viewFaceView = findViewById(R.id.chat_view_faceview);
        viewNewMessage = new ControllerNewMessage(findViewById(R.id.viewNewMessage));
        setChatImageBackground();
        viewExtendFunction = findViewById(R.id.view_extend_menu);
        mtListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //如果bottom小于oldBottom,说明键盘是弹起。
                if (bottom < oldBottom) {
                    //滑动到底部
                    mtListView.scrollToEnd();
                }
            }
        });
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
                    if (isGroup()) {
                        addSurvivalTime(msgAllBean);
                    }
                    if (bean.getRejectType() == MsgBean.RejectType.NOT_FRIENDS_OR_GROUP_MEMBER || bean.getRejectType() == MsgBean.RejectType.IN_BLACKLIST) {
                        taskRefreshMessage(false);
                    } else {
                        if (UpLoadService.getProgress(bean.getMsgId(0)) == null /*|| UpLoadService.getProgress(bean.getMsgId(0)) == 100*/) {//忽略图片上传的刷新,图片上传成功后
                            for (String msgid : bean.getMsgIdList()) {
                                //撤回消息不做刷新
                                if (ChatServer.getCancelList().containsKey(msgid)) {
                                    LogUtil.getLog().i(TAG, "onACK: 收到取消回执,等待刷新列表2");
                                    return;
                                }
                            }
                        }
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
        public void onMsg(final com.yanlong.im.utils.socket.MsgBean.UniversalMessage msgBean) {
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
                            sendRead();
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
                    MsgAllBean msgAllBean = MsgConversionBean.ToBean(bean.getWrapMsg(0), bean, true);
                    if (msgAllBean == null) {
                        return;
                    }
                    if (msgAllBean.getMsg_type().intValue() == ChatEnum.EMessageType.MSG_CANCEL
                            || msgAllBean.getMsg_type().intValue() == ChatEnum.EMessageType.READ) {//取消的指令 已读指令不保存到数据库
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
                    if (isGroup()) {
                        addSurvivalTime(msgAllBean);
                    }
                    if (bean.getRejectType() == MsgBean.RejectType.NOT_FRIENDS_OR_GROUP_MEMBER || bean.getRejectType() == MsgBean.RejectType.IN_BLACKLIST) {
                        taskRefreshMessage(false);
                    } else {
                        if (UpLoadService.getProgress(bean.getMsgId(0)) == null /*|| UpLoadService.getProgress(bean.getMsgId(0)) == 100*/) {//忽略图片上传的刷新,图片上传成功后
                            for (String msgid : bean.getMsgIdList()) {
                                //撤回消息不做刷新
                                if (ChatServer.getCancelList().containsKey(msgid)) {
                                    LogUtil.getLog().i(TAG, "onACK: 收到取消回执,等待刷新列表2");
                                    return;
                                }
                            }
                        }
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
                taskSessionInfo(true);
                break;
            case REMOVE_GROUP_MEMBER://退出群
                taskSessionInfo(true);
                break;
            case ACCEPT_BE_GROUP://邀请进群刷新
                if (groupInfo != null) {
                    taskSessionInfo(true);
                }
                break;
            case CHANGE_GROUP_META:// 修改群信息
                taskSessionInfo(true);
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
            int size = msgListData.size();
            msgListData.add(msgAllbean);
            mtListView.getListView().getAdapter().notifyItemRangeInserted(size, 1);
            // 处理发送失败时位置错乱问题
//            mtListView.getListView().getAdapter().notifyItemRangeChanged(size + 1, msgListData.size() - 1);

            //红包通知 不滚动到底部
            if (msgAllbean.getMsgNotice() != null && (msgAllbean.getMsgNotice().getMsgType() == ChatEnum.ENoticeType.RECEIVE_RED_ENVELOPE
                    || msgAllbean.getMsgNotice().getMsgType() == ChatEnum.ENoticeType.RED_ENVELOPE_RECEIVED_SELF)) {
                return;
            }
            mtListView.scrollToEnd();
        } else {
            taskRefreshMessage(false);
        }
    }

    /**
     * 添加表情、发送自定义表情
     *
     * @version 1.0
     * @createTime 2013-10-22,下午2:16:54
     * @updateTime 2013-10-22,下午2:16:54
     * @createAuthor liujingguo
     * @updateAuthor liujingguo
     * @updateInfo 增加参数 group 表情资源所属组
     */
    protected void sendFace(FaceBean bean) {
        if (FaceView.face_animo.equals(bean.getGroup())) {
            isSendingHypertext = false;

            ShippedExpressionMessage message = SocketData.createFaceMessage(SocketData.getUUID(), bean.getName());
            sendMessage(message, ChatEnum.EMessageType.SHIPPED_EXPRESSION);

        } else if (FaceView.face_emoji.equals(bean.getGroup()) || FaceView.face_lately_emoji.equals(bean.getGroup())) {
            Bitmap bitmap = null;
            if (FaceView.map_FaceEmoji != null) {
                bitmap = BitmapFactory.decodeResource(getResources(), Integer.parseInt(FaceView.map_FaceEmoji.get(bean.getName()).toString()));
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(), bean.getResId());
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, ExpressionUtil.dip2px(this, ExpressionUtil.DEFAULT_SIZE),
                    ExpressionUtil.dip2px(this, ExpressionUtil.DEFAULT_SIZE), true);
            ImageSpan imageSpan = new ImageSpan(ChatActivity.this, bitmap);
            String str = bean.getName();
            SpannableString spannableString = new SpannableString(str);
            spannableString.setSpan(imageSpan, 0, PatternUtil.FACE_EMOJI_LENGTH, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // 插入到光标后位置
            editChat.getText().insert(editChat.getSelectionStart(), spannableString);
        } else if (FaceView.face_custom.equals(bean.getGroup())) {
            if ("add".equals(bean.getName())) {
                if (!ViewUtils.isFastDoubleClick()) {
                    mViewModel.isOpenEmoj.setValue(false);
                    IntentUtil.gotoActivity(this, AddFaceActivity.class);
                }
            } else {
                if (!checkNetConnectStatus()) {
                    return;
                }
                final String imgMsgId = SocketData.getUUID();
                ImageMessage imageMessage = SocketData.createImageMessage(imgMsgId, bean.getPath(), true);
                MsgAllBean msgAllBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, SocketData.getFixTime(), imageMessage, ChatEnum.EMessageType.IMAGE);
                msgListData.add(msgAllBean);
                // 不等于常信小助手
                if (!Constants.CX_HELPER_UID.equals(toUId)) {
                    final ImgSizeUtil.ImageSize img = ImgSizeUtil.getAttribute(bean.getPath());
                    SocketData.send4Image(imgMsgId, toUId, toGid, bean.getServerPath(), true, img, -1);
                }
                notifyData2Bottom(true);
                MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllBean);
            }
        }
    }

    /**
     * 显示草稿内容
     *
     * @param message
     */
    protected void showDraftContent(String message) {
        SpannableString spannableString = ExpressionUtil.getExpressionString(this, ExpressionUtil.DEFAULT_SIZE, message);
        editChat.setText(spannableString);
    }


    //自动生成的控件事件
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initEvent() {
        //读取软键盘高度
        mKeyboardHeight = getSharedPreferences(KEY_BOARD, Context.MODE_PRIVATE).getInt(KEY_BOARD, 0);
        toGid = getIntent().getStringExtra(AGM_TOGID);
        toUId = getIntent().getLongExtra(AGM_TOUID, 0);
        onlineState = getIntent().getBooleanExtra(ONLINE_STATE, true);
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
        taskSessionInfo(false);
        if (!TextUtils.isEmpty(toGid)) {
            taskGroupInfo();
        } else {
            //id不为0且不为客服则获取最新用户信息
            if (toUId != null && !UserUtil.isSystemUser(toUId)) {
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
                    if (toUId == 1L || toUId == 3L) { //文件传输助手跳转(与常信小助手一致)
                        startActivity(new Intent(getContext(), UserInfoActivity.class)
                                .putExtra(UserInfoActivity.ID, toUId)
                                .putExtra(UserInfoActivity.JION_TYPE_SHOW, 1));
                    } else {
                        startActivity(new Intent(getContext(), ChatInfoActivity.class)
                                .putExtra(ChatInfoActivity.AGM_FUID, toUId));
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

                if (!checkNetConnectStatus()) {
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
                    if (editChat.isAtAll()) {
                        AtMessage message = SocketData.createAtMessage(SocketData.getUUID(), text, ChatEnum.EAtType.ALL, editChat.getUserIdList());
                        sendMessage(message, ChatEnum.EMessageType.AT);
                        editChat.getText().clear();

                    } else {
                        AtMessage message = SocketData.createAtMessage(SocketData.getUUID(), text, ChatEnum.EAtType.MULTIPLE, editChat.getUserIdList());
                        sendMessage(message, ChatEnum.EMessageType.AT);
                        editChat.getText().clear();
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
            }
        });

        //语音
        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toVoice();

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

        AudioRecordManager.getInstance(this).setAudioRecordListener(new IAudioRecord(this, headView, new IAudioRecord.UrlCallback() {
            @Override
            public void completeRecord(String file, int duration) {
                if (!checkNetConnectStatus()) {
                    return;
                }
                VoiceMessage voice = SocketData.createVoiceMessage(SocketData.getUUID(), file, duration);
//                MsgAllBean msg = SocketData.sendFileUploadMessagePre(voice.getMsgId(), toUId, toGid, SocketData.getFixTime(), voice, ChatEnum.EMessageType.VOICE);
                MsgAllBean msg = sendMessage(voice, ChatEnum.EMessageType.VOICE, false);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        notifyData2Bottom(true);
//                        MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msg);
//                    }
//                });
                // 不等于常信小助手，需要上传到服务器
                if (!Constants.CX_HELPER_UID.equals(toUId)) {
                    uploadVoice(file, msg);
                } else {
                    //若为常信小助手，不存服务器，只走本地数据库保存，发送状态直接重置为正常，更新数据库
//                    msgDao.fixStataMsg(voice.getMsgId(), ChatEnum.ESendStatus.NORMAL);
                }
//                msgListData.add(msg);
            }
        }));

        mAdapter = new RecyclerViewAdapter();
        mtListView.init(mAdapter);
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
                        saveScrollPosition();
//                        LogUtil.getLog().d("a=", TAG + "当前滑动位置：size = " + msgListData.size() + "--lastPosition=" + lastPosition + "--firstPosition=" + first);
                    }
                } /*else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL || newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int first = layoutManager.findFirstCompletelyVisibleItemPosition();
                        checkScrollFirst(first);
                    }
                }*/
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
                if (isGroup()) {
                    long id = userDao.myInfo().getUid();
                    long masterId = Long.valueOf(groupInfo.getMaster());
                    if (masterId != id) {
                        ToastUtil.show(context, "只有群主才能修改该选项");
                        return;
                    }
//                    if(!groupInfo.isAdmin()&&!groupInfo.isAdministrators()){
//                        ToastUtil.show(context, "只有群主和管理员才能修改该选项");
//                        return;
//                    }
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
            if (msgListData == null) {
                return;
            }
            int position = msgListData.size() - unreadCount;
            if (position >= 0) {
                scrollChatToPosition(position);
            } else {
                scrollChatToPosition(0);
            }
            viewNewMessage.setVisible(false);
            unreadCount = 0;
        });
        initExtendFunctionView();


    }

    private void setPanelHeight(int h, View view) {
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) view.getLayoutParams(); //取控
        if (linearParams.height != h) {
            int minHeight = getResources().getDimensionPixelSize(R.dimen.chat_fuction_panel_height);
            linearParams.height = Math.max(h, minHeight);
            view.setLayoutParams(linearParams);
        }
    }

    private void checkScrollFirst(int first) {
        if (unreadCount > 0 && msgListData != null) {
            int size = msgListData.size();
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
        if (groupInfo == null)
            return;

        startActivity(new Intent(getContext(), GroupRobotActivity.class)
                .putExtra(GroupRobotActivity.AGM_GID, toGid)
                .putExtra(GroupRobotActivity.AGM_RID, groupInfo.getRobotid())
        );
    }

    private void toVoice() {
        //申请权限 7.2
        permission2Util.requestPermissions(ChatActivity.this, new CheckPermission2Util.Event() {
            @Override
            public void onSuccess() {
                if (!checkNetConnectStatus()) {
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
                    ToastUtil.show(getContext(), "留言不能为空");
                }
            }
        });
        alertTouch.show();
        alertTouch.setEdHintOrSize(null, 15);
    }

    private void toTransfer() {
        UserBean user = PayEnvironment.getInstance().getUser();
        if (user != null) {
            if (user.getRealNameStat() != 1) {//未认证
                showIdentifyDialog();
                return;
            } else if (user.getPayPwdStat() != 1) {//未设置支付密码
                showSettingPswDialog();
                return;
            }
        }
        if (mFinfo == null) {
            mFinfo = userDao.findUserInfo(toUId);
        }
        String name = "";
        String avatar = "";
        if (mFinfo != null) {
            name = mFinfo.getName();
            avatar = mFinfo.getHead();
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
            } else if (user.getPayPwdStat() != 1) {//未设置支付密码
                showSettingPswDialog();
                return;
            }
        }
        if (isGroup()) {
            Intent intentMulti = MultiRedPacketActivity.newIntent(ChatActivity.this, toGid, groupInfo.getUsers().size());
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
                    if (!checkNetConnectStatus()) {
                        return;
                    }
                    if (ViewUtils.isFastDoubleClick()) {
                        return;
                    }
                    Intent intent = new Intent(ChatActivity.this, RecordedActivity.class);
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
                }
            }
        });
        viewExtendFunction.bindDate(getItemModels());
    }

    public List<FunctionItemModel> getItemModels() {
        boolean isGroup = isGroup();
        boolean isVip = false;
        boolean isSystemUser = false;
        if (!isGroup) {
            UserInfo userInfo = UserAction.getMyInfo();
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
//            list.add(createItemMode("零钱红包", R.mipmap.ic_chat_rb, ChatEnum.EFunctionId.ENVELOPE_SYS));
        }
        if (!isGroup && !isSystemUser) {
//            list.add(createItemMode("零钱转账", R.mipmap.ic_chat_transfer, ChatEnum.EFunctionId.TRANSFER));
        }
        if (!isGroup && isVip) {
            list.add(createItemMode("视频通话", R.mipmap.ic_chat_video, ChatEnum.EFunctionId.VIDEO_CALL));
        }
        if (!isSystemUser) {
            list.add(createItemMode("云红包", R.mipmap.ic_chat_rb_zfb, ChatEnum.EFunctionId.ENVELOPE_MF));
        }
        list.add(createItemMode("位置", R.mipmap.location_six, ChatEnum.EFunctionId.LOCATION));
        if (!isGroup && !isSystemUser) {
            list.add(createItemMode("戳一下", R.mipmap.ic_chat_action, ChatEnum.EFunctionId.STAMP));
        }
        if (!isSystemUser) {
            list.add(createItemMode("名片", R.mipmap.ic_chat_newfrd, ChatEnum.EFunctionId.CARD));
        }
        if (isGroup) {
            //本人群主
            if (UserAction.getMyId() != null && groupInfo != null && groupInfo.getMaster().equals(UserAction.getMyId().toString())) {
                list.add(createItemMode("群助手", R.mipmap.ic_chat_robot, ChatEnum.EFunctionId.GROUP_ASSISTANT));
            }
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
        mtListView.getListView().scrollToPosition(position);
        currentScrollPosition = position;

        View topView = mtListView.getLayoutManager().getChildAt(currentScrollPosition);
        if (topView != null) {
            //获取与该view的底部的偏移量
            lastOffset = topView.getBottom();
        }
//        System.out.println(TAG + "--scrollChatToPosition--totalSize =" + msgListData.size() + "--currentScrollPosition=" + currentScrollPosition + "--lastOffset=" + lastOffset);
    }

    private void scrollChatToPositionWithOffset(int position, int offset) {
        ((LinearLayoutManager) mtListView.getListView().getLayoutManager()).scrollToPositionWithOffset(position, offset);
        currentScrollPosition = position;
        View topView = mtListView.getLayoutManager().getChildAt(currentScrollPosition);
        if (topView != null) {
            //获取与该view的底部的偏移量
            lastOffset = topView.getBottom();
        }
//        System.out.println(TAG + "--scrollChatToPositionWithOffset--totalSize =" + msgListData.size() + "--currentScrollPosition=" + currentScrollPosition);
    }

    //消息发送撤销消息
    private void sendMessage(IMsgContent message, @ChatEnum.EMessageType int msgType, int position) {
        MsgAllBean msgAllBean = SocketData.createMessageBean(toUId, toGid, msgType, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), message);
        if (msgAllBean != null) {
            SocketData.sendAndSaveMessage(msgAllBean);
            //撤销是最后一条消息，则需要刷新
            if (msgListData != null && position == msgListData.size() - 1) {
                MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllBean);
            }
        }
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
            msgListData.add(msgAllBean);
//            if (msgType != ChatEnum.EMessageType.MSG_CANCEL) {
//                showSendObj(msgAllBean);
//            }
            MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllBean);
        }
    }

    //消息发送
    private void sendMessage(IMsgContent message, @ChatEnum.EMessageType int msgType) {
        MsgAllBean msgAllBean = SocketData.createMessageBean(toUId, toGid, msgType, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), message);
        if (msgAllBean != null) {
            if (!filterMessage(message)) {
                SocketData.sendAndSaveMessage(msgAllBean, false);
            } else {
                SocketData.sendAndSaveMessage(msgAllBean);
            }
            //cancel消息发送前不需要更新
            if (msgType != ChatEnum.EMessageType.MSG_CANCEL) {
                showSendObj(msgAllBean);
            }
            MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllBean);
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
            SocketData.sendAndSaveMessage(msgAllBean, canSend);
            showSendObj(msgAllBean);
            MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllBean);
        } else {
            SocketData.saveMessage(msgAllBean);
            showSendObj(msgAllBean);
            MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllBean);
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
        if (Constants.CX_HELPER_UID.equals(toUId) || Constants.CX_BALANCE_UID.equals(toUId)
                || Constants.CX_FILE_HELPER_UID.equals(toUId)) {
            isSend = false;
        }
        return isSend;
    }


    private void initSurvivaltimeState() {
        survivaltime = userDao.getReadDestroy(toUId, toGid);
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
        if (groupInfo != null && groupInfo.getCantOpenUpRedEnv() == 1) {
            check = false;
        }
        return check;
    }

    private boolean isAdmin() {
        if (groupInfo == null || !StringUtil.isNotNull(groupInfo.getMaster()))
            return false;
        return groupInfo.getMaster().equals("" + UserAction.getMyId());
    }

    /**
     * 判断是否是管理员
     *
     * @return
     */
    private boolean isAdministrators() {
        boolean isManager = false;
        if (groupInfo.getViceAdmins() != null && groupInfo.getViceAdmins().size() > 0) {
            for (Long user : groupInfo.getViceAdmins()) {
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
//                    imgMsgBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, SocketData.getFixTime(), imageMessage, ChatEnum.EMessageType.IMAGE);
                    imgMsgBean = sendMessage(imageMessage, ChatEnum.EMessageType.IMAGE, false);
//                    msgListData.add(imgMsgBean);
                    // 不等于常信小助手
                    if (!Constants.CX_HELPER_UID.equals(toUId)) {
//                        UpLoadService.onAddImage(imgMsgId, file, isArtworkMaster, toUId, toGid, -1);
                        UpLoadService.onAddImage(imgMsgBean, file, isArtworkMaster);
                        startService(new Intent(getContext(), UpLoadService.class));
                    }
                }

                MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, imgMsgBean);
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
        if (msgListData != null) {
            int length = msgListData.size();//刷新后当前size
            if (isMustBottom) {
                mtListView.scrollToEnd();
            } else {
                if (lastPosition >= 0 && lastPosition < length) {
                    if (isSoftShow || lastPosition == length - 1 || isCanScrollBottom()) {//允许滑动到底部，或者当前处于底部，canScrollVertically是否能向上 false表示到了底部
                        scrollChatToPosition(length);
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
                            scrollChatToPositionWithOffset(lastPosition, lastOffset);
                        }
                    } else {
                        if (currentScrollPosition > 0) {
                            scrollChatToPositionWithOffset(currentScrollPosition, lastPosition);
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
        onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventUserOnlineChange event) {
        if (toUId != null && !isGroup() && event.getUid() == toUId.intValue()) {
            updateUserOnlineStatus();
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
//        mtListView.notifyDataSetChange();
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
                if (groupInfo != null) {
                    groupInfo.setScreenshotNotification(event.getFlag());
                }
                if (event.getFlag() == 1) {
                    isScreenShotListen = true;
                    initScreenShotListener();
                } else {
                    stopScreenShotListener();
                }
            }
        } else {
            if (toUId != null && toUId.longValue() == event.getUid()) {
                if (mFinfo != null) {
                    mFinfo.setScreenshotNotification(event.getFlag());
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

    //单聊获取已读阅后即焚消息
    private void initSurvivaltime4Uid() {
        if (!isGroup()) {
            List<MsgAllBean> list = msgDao.getMsg4SurvivalTimeAndRead(toUId);
            addSurvivalTimeForList(list);
        }
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


    private String getVideoAtt(String mUri) {
        String duration = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                FileInputStream inputStream = new FileInputStream(new File(mUri).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());
            }
            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)

        } catch (Exception ex) {
            LogUtil.getLog().e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return duration;
    }

    private String getVideoAttWidth(String mUri) {
        String width = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                FileInputStream inputStream = new FileInputStream(new File(mUri).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());
            } else {
            }
            width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽

        } catch (Exception ex) {
            LogUtil.getLog().e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return width;
    }

    private String getVideoAttHeigh(String mUri) {
        String height = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                FileInputStream inputStream = new FileInputStream(new File(mUri).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());
            } else {
            }
            height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高

        } catch (Exception ex) {
            LogUtil.getLog().e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return height;
    }

    private String getVideoAttBitmap(String mUri) {
        File file = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                FileInputStream inputStream = new FileInputStream(new File(mUri).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());
            } else {
            }
            file = GroupHeadImageUtil.save2File(mmr.getFrameAtTime());
        } catch (Exception ex) {
            LogUtil.getLog().e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return file.getAbsolutePath();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case VIDEO_RP:
                    int dataType = data.getIntExtra(RecordedActivity.INTENT_DATA_TYPE, RecordedActivity.RESULT_TYPE_VIDEO);
                    MsgAllBean videoMsgBean = null;
                    if (dataType == RecordedActivity.RESULT_TYPE_VIDEO) {
//                        if (!checkNetConnectStatus()) {
//                            return;
//                        }
                        String file = data.getStringExtra(RecordedActivity.INTENT_PATH);
                        int height = data.getIntExtra(RecordedActivity.INTENT_PATH_HEIGHT, 0);
                        int width = data.getIntExtra(RecordedActivity.INTENT_VIDEO_WIDTH, 0);
                        int time = data.getIntExtra(RecordedActivity.INTENT_PATH_TIME, 0);
//                        final boolean isArtworkMaster = requestCode == PictureConfig.REQUEST_CAMERA ? true : data.getBooleanExtra(PictureConfig.IS_ARTWORK_MASTER, false);
//                        final String imgMsgId = SocketData.getUUID();
//                        VideoMessage videoMessage = new VideoMessage();
//                        videoMessage.setHeight(height);
//                        videoMessage.setHeight(height);
//                        videoMessage.setWidth(width);
//                        videoMessage.setDuration(time);
//                        videoMessage.setBg_url(getVideoAttBitmap(file));
//                        videoMessage.setLocalUrl(file);
//                        LogUtil.getLog().e("TAG", videoMessage.toString() + videoMessage.getHeight() + "----" + videoMessage.getWidth() + "----" + videoMessage.getDuration() + "----" + videoMessage.getBg_url() + "----");
                        VideoMessage videoMessage = SocketData.createVideoMessage(SocketData.getUUID(), "file://" + file, getVideoAttBitmap(file), false, time, width, height, file);
                        videoMsgBean = sendMessage(videoMessage, ChatEnum.EMessageType.MSG_VIDEO, false);
//                        videoMsgBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, SocketData.getFixTime(), videoMessageSD, ChatEnum.EMessageType.MSG_VIDEO);
                        // 不等于常信小助手，需要上传到服务器
                        if (!Constants.CX_HELPER_UID.equals(toUId)) {
//                            UpLoadService.onAddVideo(this.context, imgMsgId, file, videoMessage.getBg_url(), isArtworkMaster, toUId, toGid, time, videoMessageSD, false);
                            UpLoadService.onAddVideo(this.context, videoMsgBean, false);
                            startService(new Intent(getContext(), UpLoadService.class));
                        } else {
                            //若为常信小助手，不存服务器，只走本地数据库保存，发送状态直接重置为正常，更新数据库
//                            msgDao.fixStataMsg(imgMsgId, ChatEnum.ESendStatus.NORMAL);
                        }
//                        msgListData.add(videoMsgBean);
                    } else if (dataType == RecordedActivity.RESULT_TYPE_PHOTO) {
                        if (!checkNetConnectStatus()) {
                            return;
                        }
                        String photoPath = data.getStringExtra(RecordedActivity.INTENT_PATH);
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
//                        videoMsgBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, SocketData.getFixTime(), imageMessage, ChatEnum.EMessageType.IMAGE);
                        // 不等于常信小助手，需要上传到服务器
                        if (!Constants.CX_HELPER_UID.equals(toUId)) {
//                            UpLoadService.onAddImage(imgMsgId, file, isArtworkMaster, toUId, toGid, -1);
                            UpLoadService.onAddImage(videoMsgBean, file, isArtworkMaster);
                            startService(new Intent(getContext(), UpLoadService.class));
                        } else {
                            //若为常信小助手，不存服务器，只走本地数据库保存，发送状态直接重置为正常，更新数据库
//                            msgDao.fixStataMsg(imgMsgId, ChatEnum.ESendStatus.NORMAL);
                        }
//                        msgListData.add(videoMsgBean);
                    }
//                    notifyData2Bottom(true);
//                    MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, videoMsgBean);
                    break;
                case PictureConfig.REQUEST_CAMERA:
                case PictureConfig.CHOOSE_REQUEST:
                    if (!checkNetConnectStatus()) {
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
//                                UpLoadService.onAddImage(imgMsgId, file, isArtworkMaster, toUId, toGid, -1);
                                UpLoadService.onAddImage(imgMsgBean, file, isArtworkMaster);
                                startService(new Intent(getContext(), UpLoadService.class));
                            } else {
                                //若为常信小助手，不存服务器，只走本地数据库保存，发送状态直接重置为正常，更新数据库
//                                msgDao.fixStataMsg(imgMsgId, ChatEnum.ESendStatus.NORMAL);
                            }
//                            msgListData.add(imgMsgBean);

                        } else {
                            String videofile = localMedia.getPath();
                            if (null != videofile) {
                                long length = ImgSizeUtil.getVideoSize(videofile);
                                long duration = Long.parseLong(getVideoAtt(videofile));
                                // 大于50M、5分钟不发送
                                if (ImgSizeUtil.formetFileSize(length) > 50) {
                                    ToastUtil.show(this, "不能选择超过50M的视频");
                                    continue;
                                }
                                if (duration > 5 * 60000) {
                                    ToastUtil.show(this, "不能选择超过5分钟的视频");
                                    continue;
                                }
//                                final boolean isArtworkMaster = requestCode == PictureConfig.REQUEST_CAMERA ? true : data.getBooleanExtra(PictureConfig.IS_ARTWORK_MASTER, false);
//                                final String imgMsgId = SocketData.getUUID();
//                                VideoMessage videoMessage = new VideoMessage();
//                                videoMessage.setHeight(Long.parseLong(getVideoAttHeigh(videofile)));
//                                videoMessage.setWidth(Long.parseLong(getVideoAttWidth(videofile)));
//                                videoMessage.setDuration(duration);
//                                videoMessage.setBg_url(getVideoAttBitmap(videofile));
//                                videoMessage.setLocalUrl(videofile);
//                                LogUtil.getLog().e("TAG", videoMessage.toString() + videoMessage.getHeight() + "----" + videoMessage.getWidth() + "----" + videoMessage.getDuration() + "----" + videoMessage.getBg_url() + "----");
//                                VideoMessage videoMessageSD = SocketData.createVideoMessage(imgMsgId, "file://" + videofile, videoMessage.getBg_url(), false, videoMessage.getDuration(), videoMessage.getWidth(), videoMessage.getHeight(), videofile);
//                                imgMsgBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, SocketData.getFixTime(), videoMessageSD, ChatEnum.EMessageType.MSG_VIDEO);
                                VideoMessage videoMessage = SocketData.createVideoMessage(SocketData.getUUID(), "file://" + videofile, getVideoAttBitmap(videofile), false, duration, Long.parseLong(getVideoAttWidth(videofile)), Long.parseLong(getVideoAttHeigh(videofile)), videofile);
                                videoMsgBean = sendMessage(videoMessage, ChatEnum.EMessageType.MSG_VIDEO, false);

                                // 不等于常信小助手，需要上传到服务器
                                if (!Constants.CX_HELPER_UID.equals(toUId)) {
//                                    UpLoadService.onAddVideo(this.context, imgMsgId, videofile, videoMessage.getBg_url(), isArtworkMaster, toUId, toGid,
//                                            videoMessage.getDuration(), videoMessageSD, false);
                                    UpLoadService.onAddVideo(this.context, videoMsgBean, false);
                                    startService(new Intent(getContext(), UpLoadService.class));
                                } else {
                                    //若为常信小助手，不存服务器，只走本地数据库保存，发送状态直接重置为正常，更新数据库
//                                    msgDao.fixStataMsg(imgMsgId, ChatEnum.ESendStatus.NORMAL);
                                }
//                                msgListData.add(imgMsgBean);
                            } else {
                                ToastUtil.show(this, "文件已损坏，请重新选择");
                            }
                        }
                    }
                    MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, imgMsgBean);
                    notifyData2Bottom(true);

                    break;
                case REQ_RP://红包
                    LogUtil.writeEnvelopeLog("云红包回调了");
                    LogUtil.getLog().e("云红包回调了");
                    EnvelopeBean envelopeInfo = JrmfRpClient.getEnvelopeInfo(data);
                    if (!checkNetConnectStatus()) {
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
                    break;
                case FilePickerManager.REQUEST_CODE:
                    //断网提示
                    if (!checkNetConnectStatus()) {
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
//                                imgMsgBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, SocketData.getFixTime(), imageMessage, ChatEnum.EMessageType.IMAGE);
                                imgMsgBean = sendMessage(imageMessage, ChatEnum.EMessageType.IMAGE, false);
                                // 不等于常信小助手，需要上传到服务器
                                if (!Constants.CX_HELPER_UID.equals(toUId)) {
//                                    UpLoadService.onAddImage(imgMsgId, filePath, false, toUId, toGid, -1);
                                    UpLoadService.onAddImage(imgMsgBean, filePath, false);
                                    startService(new Intent(getContext(), UpLoadService.class));
                                } else {
                                    //若为常信小助手，不存服务器，只走本地数据库保存，发送状态直接重置为正常，更新数据库
                                    msgDao.fixStataMsg(imgMsgId, ChatEnum.ESendStatus.NORMAL);
                                }
                                msgListData.add(imgMsgBean);
                            } else if (net.cb.cb.library.utils.FileUtils.isVideo(fileFormat)) {
                                long length = ImgSizeUtil.getVideoSize(filePath);
                                long duration = Long.parseLong(getVideoAtt(filePath));
                                // 大于50M、5分钟不发送
                                if (ImgSizeUtil.formetFileSize(length) > 50) {
                                    ToastUtil.show(this, "不能选择超过50M的视频");
                                    continue;
                                }
                                if (duration > 5 * 60000) {
                                    ToastUtil.show(this, "不能选择超过5分钟的视频");
                                    continue;
                                }
//                                final boolean isArtworkMaster = requestCode == PictureConfig.REQUEST_CAMERA ? true : data.getBooleanExtra(PictureConfig.IS_ARTWORK_MASTER, false);
//                                final String imgMsgId = SocketData.getUUID();
//                                VideoMessage videoMessage = new VideoMessage();
//                                videoMessage.setHeight(Long.parseLong(getVideoAttHeigh(filePath)));
//                                videoMessage.setWidth(Long.parseLong(getVideoAttWidth(filePath)));
//                                videoMessage.setDuration(duration);
//                                videoMessage.setBg_url(getVideoAttBitmap(filePath));
//                                videoMessage.setLocalUrl(filePath);
//                                LogUtil.getLog().e("TAG", videoMessage.toString() + videoMessage.getHeight() + "----" + videoMessage.getWidth() + "----" + videoMessage.getDuration() + "----" + videoMessage.getBg_url() + "----");
//                                VideoMessage videoMessageSD = SocketData.createVideoMessage(imgMsgId, "file://" + filePath, videoMessage.getBg_url(), false, videoMessage.getDuration(), videoMessage.getWidth(), videoMessage.getHeight(), filePath);
//                                imgMsgBean = SocketData.sendFileUploadMessagePre(imgMsgId, toUId, toGid, SocketData.getFixTime(), videoMessageSD, ChatEnum.EMessageType.MSG_VIDEO);

                                VideoMessage videoMessage = SocketData.createVideoMessage(SocketData.getUUID(), "file://" + filePath, getVideoAttBitmap(filePath), false, duration, Long.parseLong(getVideoAttWidth(filePath)), Long.parseLong(getVideoAttHeigh(filePath)), filePath);
                                videoMsgBean = sendMessage(videoMessage, ChatEnum.EMessageType.MSG_VIDEO, false);
                                // 不等于常信小助手，需要上传到服务器
                                if (!Constants.CX_HELPER_UID.equals(toUId)) {
//                                    UpLoadService.onAddVideo(this.context, imgMsgId, filePath, videoMessage.getBg_url(), isArtworkMaster, toUId, toGid,
//                                            videoMessage.getDuration(), videoMessageSD, false);
                                    UpLoadService.onAddVideo(this.context, videoMsgBean, false);
                                    startService(new Intent(getContext(), UpLoadService.class));
                                } else {
                                    //若为常信小助手，不存服务器，只走本地数据库保存，发送状态直接重置为正常，更新数据库
//                                    msgDao.fixStataMsg(imgMsgId, ChatEnum.ESendStatus.NORMAL);
                                }
//                                msgListData.add(imgMsgBean);
                            } else {
                                //创建文件消息，本地预先准备好这条文件消息，等文件上传成功后刷新
                                SendFileMessage fileMessage = SocketData.createFileMessage(fileMsgId, filePath, "", fileName, new Double(fileSize).longValue(), fileFormat,false);
                                fileMsgBean = sendMessage(fileMessage, ChatEnum.EMessageType.FILE, false);
//                                fileMsgBean = SocketData.sendFileUploadMessagePre(fileMsgId, toUId, toGid, SocketData.getFixTime(), fileMessage, ChatEnum.EMessageType.FILE);
                                // 若不为常信小助手，消息需要上传到服务端
                                if (!Constants.CX_HELPER_UID.equals(toUId)) {
//                                    UpLoadService.onAddFile(this.context, fileMsgId, filePath, fileName, new Double(fileSize).longValue(), fileFormat, toUId, toGid, -1);
                                    UpLoadService.onAddFile(this.context, fileMsgBean);
                                    startService(new Intent(getContext(), UpLoadService.class));
                                } else {
                                    //若为常信小助手，不存服务器，只走本地数据库保存，发送状态直接重置为正常，更新数据库
                                    msgDao.fixStataMsg(fileMsgId, ChatEnum.ESendStatus.NORMAL);
                                }
                                msgListData.add(fileMsgBean);
                            }
                        } else {
                            ToastUtil.show("文件不存在或已被删除");
                        }
                    }
                    //刷新首页消息列表
                    MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, fileMsgBean);
                    notifyData2Bottom(true);
                    break;

            }
        } else if (resultCode == SelectUserActivity.RET_CODE_SELECTUSR) {//选择通讯录中的某个人
            if (!checkNetConnectStatus()) {
                return;
            }
            String json = data.getStringExtra(SelectUserActivity.RET_JSON);
            UserInfo userInfo = gson.fromJson(json, UserInfo.class);
            BusinessCardMessage cardMessage = SocketData.createCardMessage(SocketData.getUUID(), userInfo.getHead(), userInfo.getName(), userInfo.getImid(), userInfo.getUid());
            sendMessage(cardMessage, ChatEnum.EMessageType.BUSINESS_CARD);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshChat(EventGroupChange event) {
        if (event.isNeedLoad()) {
            taskGroupInfo();
        } else {
            taskSessionInfo(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EtaskRefreshMessagevent(EventRefreshChat event) {
        int type = event.getRefreshType();
        if (type == CoreEnum.ERefreshType.ALL) {
            taskRefreshMessage(event.isScrollBottom);
        } else if (type == CoreEnum.ERefreshType.DELETE) {
            if (event.getObject() != null && event.getObject() instanceof MsgAllBean) {
                deleteMsg((MsgAllBean) event.getObject());
            } else if (event.getList() != null) {
                deleteMsgList(event.getList());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventSwitchDisturb(EventSwitchDisturb event) {
        taskSessionInfo(true);
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
    public void freshUserStateEvent(net.cb.cb.library.event.EventFactory.FreshUserStateEvent event) {
        // 只有Vip才显示视频通话
        viewExtendFunction.bindDate(getItemModels());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void taskUpImgEvevt(EventUpImgLoadEvent event) {
//        LogUtil.getLog().d("tag", "taskUpImgEvevt state: ===============>" + event.getState() + "--msgId==" + event.getMsgid() );
        if (event.getState() == 0) {
            // LogUtil.getLog().d("tag", "taskUpImgEvevt 0: ===============>"+event.getMsgId());
            taskRefreshImage(event.getMsgid());
        } else if (event.getState() == -1) {
            //处理失败的情况
//            LogUtil.getLog().d("tag", "taskUpImgEvevt -1: ===============>" + event.getMsgId());
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
            SocketData.sendAndSaveMessage(msgAllbean);
            replaceListDataAndNotify(msgAllbean);
        } else {
            //  LogUtil.getLog().d("tag", "taskUpImgEvevt 2: ===============>"+event.getMsgId());
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void locationSendEvent(LocationSendEvent event) {
        LocationMessage message = SocketData.createLocationMessage(SocketData.getUUID(), event.message);

//        LogUtil.getLog().e("====location=message=="+GsonUtils.optObject(message));
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

//            taskRefreshImage(event.getMsgid());
//            taskRefreshMessage(true);
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
            msgListData.set(position, msgAllbean);
            LogUtil.getLog().i(TAG, "replaceListDataAndNotify: 只刷新" + position);
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
            msgListData.set(position, msgAllbean);
            LogUtil.getLog().i(TAG, "replaceListDataAndNotify: 只刷新" + position);
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
                // LogUtil.getLog().d("xxxx", "taskRefreshImage: "+msgid);
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
     * @param message
     */
    private void toUserInfoActivity(MsgAllBean message) {
        startActivity(new Intent(getContext(), UserInfoActivity.class)
                .putExtra(UserInfoActivity.ID, message.getFrom_uid())
                .putExtra(UserInfoActivity.JION_TYPE_SHOW, 1)
                .putExtra(UserInfoActivity.GID, toGid)
                .putExtra(UserInfoActivity.MUC_NICK, message.getFrom_nickname()));
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
        } else if (mFinfo != null) {
            name = mFinfo.getName4Show();
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
//                    MsgAllBean imgMsgBean = SocketData.sendFileUploadMessagePre(reMsg.getMsg_id(), toUId, toGid, reMsg.getTimestamp(), image, ChatEnum.EMessageType.IMAGE);
//                    replaceListDataAndNotify(imgMsgBean);
//                    UpLoadService.onAddImage(reMsg.getMsg_id(), file, isArtworkMaster, toUId, toGid, reMsg.getTimestamp());
                    MsgAllBean imgMsgBean = sendMessage(image, ChatEnum.EMessageType.IMAGE, false);
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
                    MsgAllBean msgAllBean = sendMessage(videoMessageSD, ChatEnum.EMessageType.MSG_VIDEO, false);
//                    MsgAllBean imgMsgBeanReSend = SocketData.sendFileUploadMessagePre(reMsg.getMsg_id(), toUId, toGid, SocketData.getFixTime(), videoMessageSD, ChatEnum.EMessageType.MSG_VIDEO);
                    replaceListDataAndNotify(msgAllBean);

                    if (!TextUtils.isEmpty(videoMessage.getBg_url())) {
                        // 当预览图清空掉时重新获取
                        File file = new File(videoMessage.getBg_url());
                        if (file == null || !file.exists()) {
                            videoMessage.setBg_url(getVideoAttBitmap(url));
                        }
                    }
//                    UpLoadService.onAddVideo(this.context, reMsg.getMsg_id(), url, videoMessage.getBg_url(), false, toUId, toGid,
//                            videoMessage.getDuration(), videoMessageSD, false);
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
                if (!checkNetConnectStatus()) {
                    return;
                }
                //群文件重发，判断是否被禁言
                if(isGroup()){
                    getSingleMemberInfo(reMsg);
                }else {
                    resendFileMsg(reMsg);
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

    }

    @Override
    public void clickTransfer(String rid, String msgId) {
    }

    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {
        int unread = 0;
        boolean isSelected = false;
        //msg_id,计时器 将计时器绑定到数据
        private Map<String, Disposable> mTimers = new HashMap<>();
        /********为保证Key-value两个值都是唯一，使用两个map 存储，查找删除方便********************************************************************/
        //position，msg_id 记住位置对应的Msg_id,用来找Position和保证mMsgIdPositions的position 唯一
        private Map<Integer, String> mPositionMsgIds = new HashMap<>();
        //msg_id，position 用来找MsgId对应的position ,保证MsgId 唯一
        private Map<String, Integer> mMsgIdPositions = new HashMap<>();

        /****************************************************************************/

        public void onDestory() {
            //清除计时器，避免内存溢出
            for (Disposable timer : mTimers.values()) {
                timer.dispose();
                timer = null;
            }
            mTimers.clear();
            mTimers = null;
        }

        private synchronized void bindTimer(final String msgId, final boolean isMe, final long startTime, final long endTime) {
            try {
                if (mTimers.containsKey(msgId)) {
                    return;
                }
                long nowTimeMillis = DateUtils.getSystemTime();
                long period = 0;
                long start = 1;
                int COUNT = 12;
                if (nowTimeMillis < endTime) {//当前时间还在倒计时结束前
                    long distance = startTime - nowTimeMillis;//和现在时间相差的毫秒数
                    //四舍五入
                    period = Math.round(Double.valueOf(endTime - startTime) / COUNT);
                    if (distance < 0) {//开始时间小于现在，已经开始了
                        start = -distance / period;
                    }
                    start = Math.max(1, start);
                    //延迟initialDelay个unit单位后，以period为周期，依次发射count个以start为初始值并递增的数字。
                    //eg:发送数字1~10，每间隔200毫秒发射一个数据 intervalRange(1, 10, 0, 200, TimeUnit.MILLISECONDS);
                    //发送数字0~11，每间隔period/COUNT毫秒发射一个数据,延迟distance毫秒
                    Disposable timer = Flowable.intervalRange(start, COUNT - start + 1, 0, period, TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnNext(new Consumer<Long>() {
                                @Override
                                public void accept(Long index) throws Exception {
                                    try {
                                        long time = nowTimeMillis - DateUtils.getSystemTime();
                                        String name = "icon_st_" + Math.min(COUNT, index + 1);
                                        int id = context.getResources().getIdentifier(name, "mipmap", context.getPackageName());
                                        updateSurvivalTimeImage(msgId, id, isMe);
                                        LogUtil.getLog().i("CountDownView", "isME=" + index);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).doOnComplete(new Action() {
                                @Override
                                public void run() throws Exception {
                                    updateSurvivalTimeImage(msgId, R.mipmap.icon_st_12, isMe);
                                }
                            }).subscribe();
                    mTimers.put(msgId, timer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void updateSurvivalTimeImage(String msgId, int id, boolean isMe) {
            if (mMsgIdPositions.containsKey(msgId)) {
                int position = mMsgIdPositions.get(msgId);
                ChatItemView chatItemView = ((ChatItemView) mtListView.getLayoutManager().findViewByPosition(position));
                if (chatItemView != null) {
                    if (isMe)
                        chatItemView.viewMeSurvivalTime
                                .setImageBitmap(BitmapFactory.decodeResource(context.getResources(), id));
                    else
                        chatItemView.viewOtSurvivalTime
                                .setImageBitmap(BitmapFactory.decodeResource(context.getResources(), id));
                }
            }
        }

        void setUnreadCount(int count) {
            unread = count;
            notifyDataSetChanged();
        }

        //设置选择模式
        void setSelected(boolean flag) {
            isSelected = flag;
            notifyDataSetChanged();
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

        @Override
        public int getItemCount() {
            return msgListData == null ? 0 : msgListData.size();
        }


        @Override
        public void onBindViewHolder(@NonNull RCViewHolder holder, int position, @NonNull List<Object> payloads) {
            holder.viewChatItem.recoveryOtUnreadView();
            if (payloads == null || payloads.isEmpty()) {
                onBindViewHolder(holder, position);
            } else {
//                LogUtil.getLog().d("sss", "onBindViewHolderpayloads: " + position);
                final MsgAllBean msgbean = msgListData.get(position);
                savePositions(msgbean.getMsg_id(), position);
                //菜单
                final List<OptionMenu> menus = new ArrayList<>();
                LogUtil.getLog().d("SurvivalTime", "单条刷新");

                if (!isGroup()) {
                    if (msgbean.isMe()) {
                        addSurvivalTimeAndRead(msgbean);
                    } else {
                        addSurvivalTime(msgbean);
                    }
                } else {
                    addSurvivalTime(msgbean);
                }

                //如果是群聊不打开阅读
                if (!isGroup()) {
                    if (msgbean.getRead() == 1 && checkIsRead() && msgbean.isMe()) {
                        holder.viewChatItem.setDataRead(msgbean.getSend_state(), msgbean.getReadTime());
                    }
                }
                if (msgbean.getSurvival_time() > 0 && msgbean.getStartTime() > 0 && msgbean.getEndTime() > 0) {
//                    LogUtil.getLog().i("CountDownView", msgbean.getMsg_id() + "---");
                    //阅后即焚
                    holder.viewChatItem.setDataSurvivalTimeShow(msgbean.getSurvival_time(), false);
                    bindTimer(msgbean.getMsg_id(), msgbean.isMe(), msgbean.getStartTime(), msgbean.getEndTime());
                } else {
                    holder.viewChatItem.setDataSurvivalTimeShow(msgbean.getSurvival_time(), true);
                }

                //只更新单条处理
                switch (msgbean.getMsg_type()) {
                    case ChatEnum.EMessageType.IMAGE:
                        Integer pg = null;
                        pg = UpLoadService.getProgress(msgbean.getMsg_id());
                        LogUtil.getLog().i(TAG, "更新进度--msgId=" + msgbean.getMsg_id() + "--progress=" + pg);

                        holder.viewChatItem.setImageProgress(pg);
                        holder.viewChatItem.setErr(msgbean.getSend_state(), false);
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
                        holder.viewChatItem.setErr(msgbean.getSend_state(), false);
                        if (msgbean.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
                            menus.add(new OptionMenu("转发"));
                            menus.add(new OptionMenu("删除"));
                        }

                        break;
                    case ChatEnum.EMessageType.MSG_VIDEO:
                        Integer pgVideo = null;
                        pgVideo = UpLoadService.getProgress(msgbean.getMsg_id());
                        LogUtil.getLog().i(TAG, "更新进度--msgId=" + msgbean.getMsg_id() + "--progress=" + pgVideo);
                        holder.viewChatItem.setErr(msgbean.getSend_state(), false);
                        holder.viewChatItem.setImageProgress(pgVideo);

                        if (msgbean.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
                            menus.add(new OptionMenu("转发"));
                            holder.viewChatItem.setVideoIMGShow(true);
                        } else if (msgbean.getSend_state() == ChatEnum.ESendStatus.SENDING) {
                            holder.viewChatItem.setVideoIMGShow(false);
                        } else if (msgbean.getSend_state() == ChatEnum.ESendStatus.ERROR) {
                            holder.viewChatItem.setVideoIMGShow(true);
                        }
                        menus.add(new OptionMenu("删除"));
                        break;
                    case ChatEnum.EMessageType.FILE:
                        Integer pgFile = null;
                        pgFile = UpLoadService.getProgress(msgbean.getMsg_id());
                        LogUtil.getLog().i(TAG, "更新进度--msgId=" + msgbean.getMsg_id() + "--progress=" + pgFile);

                        holder.viewChatItem.setFileProgress(pgFile);
                        holder.viewChatItem.setErr(msgbean.getSend_state(), false);
//                        holder.viewChatItem.updateSendStatusAndProgress(msgbean.getSend_state(), pg);

                        if (msgbean.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
                            menus.add(new OptionMenu("转发"));
                            menus.add(new OptionMenu("删除"));
                        } else {
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

        /**
         * 保存msgid位置
         *
         * @param msgId
         * @param position
         */
        private void savePositions(String msgId, int position) {
            //已经有MsgId包含该位置，则删除上一个，保证唯一性，更新时
            if (mMsgIdPositions.containsValue(position)) {
                mMsgIdPositions.remove(mPositionMsgIds.get(position));
            }
            //mPositionMsgIds只记录，不处理
            mPositionMsgIds.put(position, msgId);
            mMsgIdPositions.put(msgId, position);
        }


        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder holder, final int position) {
            holder.viewChatItem.recoveryOtUnreadView();
            final MsgAllBean msgbean = msgListData.get(position);
            savePositions(msgbean.getMsg_id(), position);
            if (!isGroup()) {
                if (msgbean.isMe()) {
                    addSurvivalTimeAndRead(msgbean);
                } else {
                    addSurvivalTime(msgbean);
                }
            } else {
                addSurvivalTime(msgbean);
            }

            //时间戳合并
            String time = null;
            if (position > 0 && (msgbean.getTimestamp() - msgListData.get(position - 1).getTimestamp()) < (60 * 1000)) { //小于60秒隐藏时间
                time = null;
            } else {
                time = TimeToString.getTimeWx(msgbean.getTimestamp());
            }
            //昵称处理
            String nikeName = null;
            String headico = msgbean.getFrom_avatar();
            if (isGroup()) {//群聊显示昵称
                nikeName = msgbean.getFrom_nickname();
            }

            if (isGroup()) {
                holder.viewChatItem.setHeadOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //TODO:优先显示群备注
                        String name = msgDao.getGroupMemberName(toGid, msgbean.getFrom_uid(), null, null);
//                        if (TextUtils.isEmpty(name)) {
//                            name = msgDao.getUsername4Show(toGid, msgbean.getFrom_uid());
//                        }
                        String txt = editChat.getText().toString().trim();
                        if (!txt.contains("@" + name)) {
                            if (!TextUtils.isEmpty(name)) {
                                editChat.addAtSpan("@", name, msgbean.getFrom_uid());
                            } else {
                                name = TextUtils.isEmpty(msgbean.getFrom_group_nickname()) ? msgbean.getFrom_nickname() : msgbean.getFrom_group_nickname();
                                editChat.addAtSpan("@", name, msgbean.getFrom_uid());
                            }
                            mtListView.scrollToEnd();
                        }
                        return true;
                    }
                });
            }

            //显示数据集

            if (msgbean.isMe()) {
                holder.viewChatItem.setOnHead(null);
            } else {
                final String finalNikeName = nikeName;
                holder.viewChatItem.setOnHead(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ViewUtils.isFastDoubleClick()) {
                            return;
                        }
                        //TODO:优先显示群备注、查询最新的在本群的昵称
                        String name = "";
                        if (isGroup()) {
                            name = msgDao.getGroupMemberName2(toGid, msgbean.getFrom_uid());
                        } else if (mFinfo != null) {
                            name = mFinfo.getName4Show();
                        }
                        startActivity(new Intent(getContext(), UserInfoActivity.class)
                                .putExtra(UserInfoActivity.ID, msgbean.getFrom_uid())
                                .putExtra(UserInfoActivity.JION_TYPE_SHOW, 1)
                                .putExtra(UserInfoActivity.GID, toGid)
                                .putExtra(UserInfoActivity.IS_GROUP, isGroup())
                                .putExtra(UserInfoActivity.MUC_NICK, name));
                    }
                });
            }
            holder.viewChatItem.setShowType(msgbean.getMsg_type(), msgbean.isMe(), headico, nikeName, time, isGroup());
            if (unread >= MIN_UNREAD_COUNT) {
                if (position == getItemCount() - unread) {
                    holder.viewChatItem.showNew(true);
//                    holder.viewChatItem.showNew(false);
                } else {
                    holder.viewChatItem.showNew(false);
                }
            } else {
                holder.viewChatItem.showNew(false);
            }
            holder.viewChatItem.isSelectedShow(isSelected);
            //发送状态处理
            if (ChatEnum.EMessageType.MSG_VIDEO == msgbean.getMsg_type() || ChatEnum.EMessageType.IMAGE == msgbean.getMsg_type() ||
                    ChatEnum.EMessageType.FILE == msgbean.getMsg_type() ||
                    Constants.CX_HELPER_UID.equals(toUId) || Constants.CX_FILE_HELPER_UID.equals(toUId)) {
                holder.viewChatItem.setErr(msgbean.getSend_state(), false);
            } else {
                holder.viewChatItem.setErr(msgbean.getSend_state(), true);
            }

            //设置已读
            if (!isGroup()) {
                if (msgbean.getRead() == 1 && checkIsRead() && msgbean.isMe()) {
                    holder.viewChatItem.setDataRead(msgbean.getSend_state(), msgbean.getReadTime());
                }
            }

            if (msgbean.getSurvival_time() > 0 && msgbean.getStartTime() > 0 && msgbean.getEndTime() > 0) {
                holder.viewChatItem.setDataSurvivalTimeShow(msgbean.getSurvival_time(), false);
//                LogUtil.getLog().i("CountDownView", msgbean.getMsg_id() + "---");
                bindTimer(msgbean.getMsg_id(), msgbean.isMe(), msgbean.getStartTime(), msgbean.getEndTime());
            } else {
                holder.viewChatItem.setDataSurvivalTimeShow(msgbean.getSurvival_time(), true);
            }
//            LogUtil.getLog().d("getSend_state", msgbean.getSurvival_time() + "----" + msgbean.getMsg_id());
            //设置阅后即焚图标显示


            //菜单
            final List<OptionMenu> menus = new ArrayList<>();
            switch (msgbean.getMsg_type()) {
                case ChatEnum.EMessageType.NOTICE:
                    if (msgbean.getMsgNotice() != null) {
                        MsgNotice notice = msgbean.getMsgNotice();
                        if (notice.getMsgType() == MsgNotice.MSG_TYPE_DEFAULT
                                || notice.getMsgType() == ChatEnum.ENoticeType.RED_ENVELOPE_RECEIVED_SELF
                                || notice.getMsgType() == ChatEnum.ENoticeType.SYS_ENVELOPE_RECEIVED_SELF
                                || notice.getMsgType() == ChatEnum.ENoticeType.BLACK_ERROR) {
                            holder.viewChatItem.setData0(notice.getNote());
                        } else {
                            if (notice.getMsgType() == ChatEnum.ENoticeType.SYS_ENVELOPE_RECEIVED || notice.getMsgType() == ChatEnum.ENoticeType.RECEIVE_SYS_ENVELOPE
                                    || notice.getMsgType() == ChatEnum.ENoticeType.SNAPSHOT_SCREEN) {
                                holder.viewChatItem.setNoticeString(Html.fromHtml(notice.getNote(), null,
                                        new MsgTagHandler(AppConfig.getContext(), true, msgid, ChatActivity.this)));
                            } else {
                                holder.viewChatItem.setData0(new HtmlTransitonUtils().getSpannableString(ChatActivity.this, notice.getNote(), notice.getMsgType()));
                            }
                        }
                        //8.22 如果是红包消息类型则显示红包图
                        if (notice.getMsgType() != null && (notice.getMsgType() == ChatEnum.ENoticeType.RED_ENVELOPE_RECEIVED
                                || notice.getMsgType() == ChatEnum.ENoticeType.RECEIVE_RED_ENVELOPE
                                || notice.getMsgType() == ChatEnum.ENoticeType.RED_ENVELOPE_RECEIVED_SELF
                                || notice.getMsgType() == ChatEnum.ENoticeType.SYS_ENVELOPE_RECEIVED
                                || notice.getMsgType() == ChatEnum.ENoticeType.RECEIVE_SYS_ENVELOPE
                                || notice.getMsgType() == ChatEnum.ENoticeType.SYS_ENVELOPE_RECEIVED_SELF)) {
                            holder.viewChatItem.showBroadcastIcon(true, null);
                        }

                    }
                    break;
                case ChatEnum.EMessageType.MSG_CANCEL:// 撤回消息
                    if (msgbean.getMsgCancel() != null) {
                        // 发送消息小于5分钟显示 重新编辑
                        Long mss = System.currentTimeMillis() - msgbean.getTimestamp();
                        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
                        String content = msgbean.getMsgCancel().getCancelContent();
                        Integer msgType = msgbean.getMsgCancel().getCancelContentType();
                        boolean isCustoerFace = false;
                        if (!TextUtils.isEmpty(content) && content.length() == PatternUtil.FACE_CUSTOMER_LENGTH) {// 自定义表情不给重新编辑
                            Pattern patten = Pattern.compile(PatternUtil.PATTERN_FACE_CUSTOMER, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
                            Matcher matcher = patten.matcher(content);
                            if (matcher.matches()) {
                                isCustoerFace = true;
                            }
                        }
                        // 是文本且小于5分钟 显示重新编辑
                        if (msgType != null && (msgType == ChatEnum.EMessageType.TEXT || msgType == ChatEnum.EMessageType.AT)
                                && minutes < RELINQUISH_TIME && !TextUtils.isEmpty(content) && !isCustoerFace) {
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
                    holder.viewChatItem.setData1(msgbean.getChat().getMsg(), menus, font_size);
                    break;
                case ChatEnum.EMessageType.SHIPPED_EXPRESSION:// 动漫表情
                    holder.viewChatItem.showBigFace(msgbean.getShippedExpressionMessage().getId(), menus, new ChatItemView.EventPic() {
                        @Override
                        public void onClick(String uri) {
                            if (ViewUtils.isFastDoubleClick()) {
                                return;
                            }
                            Bundle bundle = new Bundle();
                            bundle.putString(Preferences.DATA, uri);
                            IntentUtil.gotoActivity(ChatActivity.this, ShowBigFaceActivity.class, bundle);
                        }
                    });
                    break;
                case ChatEnum.EMessageType.STAMP:

                    menus.add(new OptionMenu("删除"));
                    holder.viewChatItem.setData2(msgbean.getStamp().getComment());
                    break;

                case ChatEnum.EMessageType.RED_ENVELOPE:
                    menus.add(new OptionMenu("删除"));
                    RedEnvelopeMessage rb = msgbean.getRed_envelope();
                    Boolean isInvalid = rb.getIsInvalid() == 0 ? false : true;
                    String info = getEnvelopeInfo(rb.getEnvelopStatus());
                    if (rb.getEnvelopStatus() == PayEnum.EEnvelopeStatus.PAST) {
                        isInvalid = true;
                    }
                    String title = msgbean.getRed_envelope().getComment();
                    final String rid = rb.getId();
                    final Long touid = msgbean.getFrom_uid();
                    final int style = msgbean.getRed_envelope().getStyle();
                    String type = null;
                    int reType = rb.getRe_type().intValue();//红包类型
                    if (reType == MsgBean.RedEnvelopeType.MFPAY_VALUE) {
                        type = "云红包";
                    } else if (reType == MsgBean.RedEnvelopeType.SYSTEM_VALUE) {
                        type = "零钱红包";
                    }

                    holder.viewChatItem.setData3(isInvalid, title, info, type, R.color.transparent, reType, new ChatItemView.EventRP() {
                        @Override
                        public void onClick(boolean isInvalid, int reType) {
                            if (reType == MsgBean.RedEnvelopeType.MFPAY_VALUE) {//魔方红包
                                if (isInvalid || (msgbean.isMe() && style == MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.NORMAL_VALUE)) {//已领取或者是自己的,看详情,"拼手气的话自己也能抢"
                                    //ToastUtil.show(getContext(), "红包详情");
                                    taskPayRbDetail(msgbean, rid);
                                } else {
                                    if (checkCanOpenUpRedEnv()) {
                                        taskPayRbGet(msgbean, touid, rid);
                                    } else {
                                        ToastUtil.show(ChatActivity.this, "您已被禁止领取该群红包");
                                    }
                                }
                            } else if (reType == MsgBean.RedEnvelopeType.SYSTEM_VALUE) {//零钱红包
                                if (!checkCanOpenUpRedEnv()) {
                                    ToastUtil.show(ChatActivity.this, "您已被禁止领取该群红包");
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
                                    if (msgbean.isMe() && isNormalStyle) {
                                        getRedEnvelopeDetail(msgbean, tradeId, rb.getAccessToken(), reType, isNormalStyle);
                                    } else {
                                        if (!TextUtils.isEmpty(rb.getAccessToken())) {
                                            showEnvelopeDialog(rb.getAccessToken(), envelopeStatus, msgbean, reType);
                                        } else {
                                            grabRedEnvelope(msgbean, tradeId, reType);
                                        }
                                    }
                                } else if (envelopeStatus == PayEnum.EEnvelopeStatus.RECEIVED) {
                                    getRedEnvelopeDetail(msgbean, tradeId, rb.getAccessToken(), reType, isNormalStyle);
                                } else if (envelopeStatus == PayEnum.EEnvelopeStatus.RECEIVED_FINISHED) {
                                    if (msgbean.isMe()) {
                                        getRedEnvelopeDetail(msgbean, tradeId, rb.getAccessToken(), reType, isNormalStyle);
                                    } else {
                                        showEnvelopeDialog(rb.getAccessToken(), envelopeStatus, msgbean, reType);
                                    }
                                } else if (envelopeStatus == PayEnum.EEnvelopeStatus.PAST) {
                                    if (msgbean.isMe()) {
                                        getRedEnvelopeDetail(msgbean, tradeId, rb.getAccessToken(), reType, isNormalStyle);
                                    } else {
                                        showEnvelopeDialog(rb.getAccessToken(), envelopeStatus, msgbean, reType);
                                    }
                                }
//                                if (isInvalid || (msgbean.isMe() && isNormalStyle)) {//已领取或者是自己的,看详情,"拼手气的话自己也能抢"
//                                    getRedEnvelopeDetail(msgbean, tradeId, rb.getAccessToken(), reType, isNormalStyle);
//                                } else {
//                                    if (!TextUtils.isEmpty(rb.getAccessToken())) {
//                                        showEnvelopeDialog(rb.getAccessToken(), 1, msgbean, reType);
//                                    } else {
//                                        grabRedEnvelope(msgbean, tradeId, reType);
//                                    }
//                                }
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
                        menus.add(new OptionMenu("删除"));
                    } else if (msgbean.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
                        menus.add(new OptionMenu("转发"));
                        menus.add(new OptionMenu("删除"));
                        holder.viewChatItem.setVideoIMGShow(true);
                        LogUtil.getLog().e("TAG", "2");
                    } else {
                        menus.add(new OptionMenu("删除"));
                    }

                    Integer pgVideo = null;
                    pgVideo = UpLoadService.getProgress(msgbean.getMsg_id());
                    // 等于常信小助手
                    if (Constants.CX_HELPER_UID.equals(toUId)) {
                        pgVideo = 100;
                    }

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
                                    if (!file.exists()) {
                                        localUrl = msgbean.getVideoMessage().getUrl();
                                    }
                                } else {
                                    localUrl = msgbean.getVideoMessage().getUrl();
                                }
                                Intent intent = new Intent(ChatActivity.this, VideoPlayActivity.class);
                                intent.putExtra("videopath", localUrl);
                                intent.putExtra("videomsg", new Gson().toJson(msgbean));
                                intent.putExtra("msg_id", msgbean.getMsg_id());
                                intent.putExtra("bg_url", msgbean.getVideoMessage().getBg_url());
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
                    String infoTs = getTransferInfo(ts.getComment(), ts.getOpType(), msgbean.isMe(), msgbean.getTo_user().getName());
                    String titleTs = "¥" + UIUtils.getYuan(ts.getTransaction_amount());
                    String typeTs = "零钱转账";
                    int tranType = 0;//转账类型
                    holder.viewChatItem.setData6(ts.getOpType(), titleTs, infoTs, typeTs, R.color.transparent, tranType, new ChatItemView.EventRP() {
                        @Override
                        public void onClick(boolean isInvalid, int tranType) {
                            showLoadingDialog();
                            httpGetTransferDetail(ts.getId(), ts.getOpType(), msgbean);
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
                            // 只有Vip才可以视频通话
                            UserInfo userInfo = UserAction.getMyInfo();
                            if (userInfo != null && IS_VIP.equals(userInfo.getVip())) {
                                if (msgbean.getP2PAuVideoMessage().getAv_type() == MsgBean.AuVideoType.Audio.getNumber()) {
                                    gotoVideoActivity(AVChatType.AUDIO.getValue());
                                } else {
                                    gotoVideoActivity(AVChatType.VIDEO.getValue());
                                }
                            }
                        }
                    });
                    break;
                case ChatEnum.EMessageType.LOCK:
//                    holder.viewChatItem.setLock(msgbean.getChat().getMsg());
                    holder.viewChatItem.setLock(new HtmlTransitonUtils().getSpannableString(ChatActivity.this, msgbean.getChat().getMsg(), ChatEnum.ENoticeType.LOCK));
                    break;
                case ChatEnum.EMessageType.CHANGE_SURVIVAL_TIME:
                    LogUtil.getLog().d("CHANGE_SURVIVAL_TIME", msgbean.getMsg_id() + "------" + msgbean.getChangeSurvivalTimeMessage().getSurvival_time());
                    if (msgbean.getMsgCancel() != null) {
//                        holder.viewChatItem.setReadDestroy(msgbean.getMsgCancel().getNote());
                        holder.viewChatItem.setReadDestroy(msgbean);
                    }
                    break;
                case ChatEnum.EMessageType.BALANCE_ASSISTANT:
                    holder.viewChatItem.setBalanceMsg(msgbean.getBalanceAssistantMessage(), new ChatItemView.EventBalance() {
                        @Override
                        public void onClick(long tradeId, int detailType) {
                            if (detailType == MsgBean.BalanceAssistantMessage.DetailType.RED_ENVELOPE_VALUE) {//红包详情
                                Intent intent = SingleRedPacketDetailsActivity.newIntent(ChatActivity.this, tradeId, 1);
                                startActivity(intent);
                            } else if (detailType == MsgBean.BalanceAssistantMessage.DetailType.TRANS_VALUE) {//订单详情
                                BillDetailActivity.jumpToBillDetail(ChatActivity.this, tradeId + "");
                            }
                        }
                    });
                    break;
                case ChatEnum.EMessageType.LOCATION:
                    menus.add(new OptionMenu("转发"));
                    menus.add(new OptionMenu("删除"));
                    holder.viewChatItem.setDataLocation(msgbean.getLocationMessage(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LocationActivity.openActivity(ChatActivity.this, true, msgbean);
                        }
                    });
                    break;
                case ChatEnum.EMessageType.FILE: //文件消息
                    if (msgbean.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
                        menus.add(new OptionMenu("转发"));
                        menus.add(new OptionMenu("删除"));
                    } else {
                        menus.add(new OptionMenu("删除"));
                    }
                    Integer filePg = UpLoadService.getProgress(msgbean.getMsg_id());
                    //发送失败状态下，不展现之前的进度，点击重新下载
                    if (msgbean.getSend_state() != ChatEnum.ESendStatus.ERROR) {
                        if (filePg != null) {
                            holder.viewChatItem.setFileProgress(filePg);
                        }
                    }
                    SendFileMessage fileMessage = msgbean.getSendFileMessage();
                    //UI显示和点击事件
                    holder.viewChatItem.setDataFile(fileMessage, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //1 如果是我发的文件
                            if (msgbean.isMe()) {
                                //2 判断是否为转发
                                //若是转发，则需要先从下载路径里找，有则直接打开，没有则需要下载
                                if(fileMessage.isFromOther()){
                                    if (net.cb.cb.library.utils.FileUtils.fileIsExist(FileConfig.PATH_DOWNLOAD + fileMessage.getFile_name())) {
                                        openAndroidFile(FileConfig.PATH_DOWNLOAD + fileMessage.getFile_name());
                                    } else {
                                        if (!TextUtils.isEmpty(fileMessage.getUrl())) {
                                            Intent intent = new Intent(ChatActivity.this, FileDownloadActivity.class);
                                            intent.putExtra("file_name", fileMessage.getFile_name());
                                            intent.putExtra("file_format", fileMessage.getFormat());
                                            intent.putExtra("file_url", fileMessage.getUrl());
                                            startActivity(intent);
                                        } else {
                                            ToastUtil.show("文件下载地址错误，请联系客服");
                                        }
                                    }
                                }else {
                                    //若不是转发，则为本地文件，从本地路径里找，有则打开，没有提示文件已被删除
                                    if (net.cb.cb.library.utils.FileUtils.fileIsExist(fileMessage.getLocalPath())) {
                                        openAndroidFile(fileMessage.getLocalPath());
                                    } else {
                                        ToastUtil.show("文件不存在或者已被删除");
                                    }
                                }
                            } else {
                                //如果是别人发的文件
                                //从下载路径里找，若存在该文件，则直接打开；否则需要下载
                                if (net.cb.cb.library.utils.FileUtils.fileIsExist(FileConfig.PATH_DOWNLOAD + fileMessage.getFile_name())) {
                                    openAndroidFile(FileConfig.PATH_DOWNLOAD + fileMessage.getFile_name());
                                } else {
                                    if (!TextUtils.isEmpty(fileMessage.getUrl())) {
                                        Intent intent = new Intent(ChatActivity.this, FileDownloadActivity.class);
                                        intent.putExtra("file_name", fileMessage.getFile_name());
                                        intent.putExtra("file_format", fileMessage.getFormat());
                                        intent.putExtra("file_url", fileMessage.getUrl());
                                        startActivity(intent);
                                    } else {
                                        ToastUtil.show("文件下载地址错误，请联系客服");
                                    }

                                }
                            }
                        }
                    });
                    break;
                case ChatEnum.EMessageType.WEB:
                    holder.viewChatItem.setShareWeb(msgbean, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    break;
            }

            holder.viewChatItem.setOnErr(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!DoubleUtils.isFastDoubleClick()) {
                        //从数据拉出来,然后再发送
                        resendMessage(msgbean);
                    }
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
                        if (ViewUtils.isFastDoubleClick()) {
                            return;
                        }

                        showDraftContent(editChat.getText().toString() + restContent);
                        editChat.setSelection(editChat.getText().length());
                        mViewModel.isInputText.setValue(true);
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
                    if (!ViewUtils.isFastDoubleClick()) {//防止触发两次  移除父类才能添加
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
                        dismissPop();
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
        if (sendStatus == ChatEnum.ESendStatus.NORMAL && !isBanForward(type)) {
            menus.add(new OptionMenu("转发"));
        }
        menus.add(new OptionMenu("删除"));
        switch (type) {
            case ChatEnum.EMessageType.TEXT:
                menus.add(0, new OptionMenu("复制"));
                break;
            case ChatEnum.EMessageType.IMAGE:
            case ChatEnum.EMessageType.MSG_VIDEO:
                break;
            case ChatEnum.EMessageType.VOICE:
                if (msgDao.userSetingGet().getVoicePlayer() == 0) {
                    menus.add(0, new OptionMenu("听筒播放"));
                } else {
                    menus.add(0, new OptionMenu("扬声器播放"));
                }
                break;
        }
        if (sendStatus == ChatEnum.ESendStatus.NORMAL && type != ChatEnum.EMessageType.MSG_VOICE_VIDEO) {
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
        return menus;
    }

    //是否禁止转发
    public boolean isBanForward(@ChatEnum.EMessageType int type) {
        if (type == ChatEnum.EMessageType.VOICE || type == ChatEnum.EMessageType.STAMP || type == ChatEnum.EMessageType.RED_ENVELOPE
                || type == ChatEnum.EMessageType.MSG_VOICE_VIDEO || type == ChatEnum.EMessageType.BUSINESS_CARD) {
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
        Intent intent = MsgForwardActivity.newIntent(this, ChatEnum.EForwardMode.DEFAULT, new Gson().toJson(msgbean));
        startActivity(intent);
//        startActivity(new Intent(getContext(), MsgForwardActivity.class)
////                .putExtra(MsgForwardActivity.AGM_JSON, new Gson().toJson(msgbean)));
    }

    /**
     * 撤回
     *
     * @param msgBean
     */
//    private void onRecall(final MsgAllBean msgbean) {
//        String msg = "";
//        Integer msgType = 0;
//        if (msgbean.getChat() != null) {
//            msg = msgbean.getChat().getMsg();
//        } else if (msgbean.getAtMessage() != null) {
//            msg = msgbean.getAtMessage().getMsg();
//        }
//        msgType = msgbean.getMsg_type();
//        MsgAllBean msgAllbean = SocketData.send4CancelMsg(toUId, toGid, msgbean.getMsg_id(), msg, msgType);
//        MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllbean);
//    }
    private void onRecall(final MsgAllBean msgBean) {
        int position = msgListData.indexOf(msgBean);
        MsgCancel cancel = SocketData.createCancelMsg(msgBean);
        if (cancel != null) {
            sendMessage(cancel, ChatEnum.EMessageType.MSG_CANCEL, position);
        }
    }


    //回复
    private void onAnswer(MsgAllBean msgbean) {
        LogUtil.getLog().e("===回复=====");
        switch (msgbean.getMsg_type()) {
            case ChatEnum.EMessageType.TEXT:
                break;
            case ChatEnum.EMessageType.IMAGE:
                break;
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
//        mtListView.notifyDataSetChange();
        if (msgListData != null) {
            mtListView.getListView().getAdapter().notifyItemRangeChanged(0, msgListData.size());
        }
        mtListView.getSwipeLayout().setRefreshing(false);
    }

    private MsgAction msgAction = new MsgAction();
    private UserDao userDao = new UserDao();
    private MsgDao msgDao = new MsgDao();
    private PayAction payAction = new PayAction();

    /***
     * 获取会话信息
     */
    private void taskSessionInfo(boolean needRefresh) {
        String title = "";
        if (isGroup()) {
            if (needRefresh || groupInfo == null) {
                groupInfo = msgDao.getGroup4Id(toGid);
            }
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
                    actionbar.setNumber(memberCount, true);
//                    title = title + "(" + memberCount + ")";
                } else {
                    actionbar.setNumber(0, false);//消息数为0则不显示
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
            mFinfo = userDao.findUserInfo(toUId);
            if (mFinfo == null && toUId == 100121L) {
                mFinfo = new UserInfo();
                mFinfo.setUid(100121L);
                mFinfo.setName("常信客服");
            }
            if (mFinfo != null) {
                title = mFinfo.getName4Show();
                if (mFinfo.getLastonline() > 0) {
                    // 客服不显示时间状态
                    if (onlineState && !UserUtil.isSystemUser(toUId)) {
                        actionbar.setTitleMore(TimeToString.getTimeOnline(mFinfo.getLastonline(), mFinfo.getActiveType(), true), true);
                    } else {
                        actionbar.setTitleMore(TimeToString.getTimeOnline(mFinfo.getLastonline(), mFinfo.getActiveType(), true), false);
                    }
                }
            }
        }
        actionbar.setChatTitle(title);
        setDisturb();
    }

    private void setDisturb() {
        Session session = dao.sessionGet(toGid, toUId);
        int disturb = 0;
        if (session == null) {
            if (isGroup()) {
                Group group = dao.getGroup4Id(toGid);
                if (group != null && group.getNotNotify() != null) {
                    disturb = group.getNotNotify();
                }
            } else {
                UserInfo info = userDao.findUserInfo(toUId);
                if (info != null && info.getDisturb() != null) {
                    disturb = info.getDisturb();
                }
            }
        } else {
            disturb = session.getIsMute();
        }
        actionbar.showDisturb(disturb == 1);

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
                // 客服不显示时间状态
                if (onlineState && !UserUtil.isSystemUser(toUId)) {
                    actionbar.setTitleMore(TimeToString.getTimeOnline(finfo.getLastonline(), finfo.getActiveType(), true), true);
                } else {
                    actionbar.setTitleMore(TimeToString.getTimeOnline(finfo.getLastonline(), finfo.getActiveType(), true), false);
                }
            }
            actionbar.setChatTitle(title);
        }

    }


    private String msgid;

    public void sendRead() {
        //发送已读回执
        if (TextUtils.isEmpty(toGid)) {
            MsgAllBean bean = msgDao.msgGetLast4FromUid(toUId);
            if (bean != null) {
//                LogUtil.getLog().e("===sendRead==msg=====" + bean.getMsg_id() + "===msgid=" + msgid + "==bean.getRead()=" + bean.getRead() + "==bean.getTimestamp()=" + bean.getTimestamp());
                if (bean.getRead() == 0) {
//                if ((TextUtils.isEmpty(msgid) || !msgid.equals(bean.getMsg_id())) && bean.getRead() == 0) {
                    msgid = bean.getMsg_id();
//                    LogUtil.getLog().e("=sendRead=2=msg="+ bean.getMsg_id());
                    SocketData.send4Read(toUId, bean.getTimestamp());
                    msgDao.setRead(msgid);
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
        if (msgListData != null && msgListData.size() > 0) {
            length = msgListData.size();
            MsgAllBean bean = msgListData.get(length - 1);
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
//                            list = msgAction.getMsg4User(toGid, toUId, null, 80);

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
                        fixLastPosition(msgListData, list);
                        msgListData = list;
                        int len = list.size();
                        if (len == 0 && lastPosition > len - 1) {//历史数据被清除了
                            lastPosition = 0;
                            lastOffset = 0;
                            clearScrollPosition();
                        }
                        notifyData2Bottom(isScrollBottom);
                        //单聊发送已读消息
                        sendRead();
                    }
                });

    }

    private void fixLastPosition(List<MsgAllBean> msgListData, List<MsgAllBean> list) {
        if (msgListData != null && list != null) {
            int len1 = msgListData.size();
            int len2 = list.size();
            if (currentScrollPosition > 0) {
                if (len1 < len2) {
                    int diff = len2 - len1;
                    currentScrollPosition += diff;
                }
            }

            if (lastPosition >= msgListData.size() - 3) {
                lastPosition = len2 - 1;
            }
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
        if (msgListData.size() >= 20) {
            msgListData.addAll(0, msgAction.getMsg4User(toGid, toUId, msgListData.get(0).getTimestamp(), false));
        } else {
            msgListData = msgAction.getMsg4User(toGid, toUId, null, false);
        }
        addItem = msgListData.size() - addItem;
        taskMkName(msgListData);
        notifyData();
        scrollChatToPositionWithOffset(addItem, DensityUtil.dip2px(context, 20f));


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
            if (msg.getMsg_type() == ChatEnum.EMessageType.NOTICE || msg.getMsg_type() == ChatEnum.EMessageType.MSG_CANCEL || msg.getMsg_type() == ChatEnum.EMessageType.LOCK) {  //通知类型的不处理
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
            if (/*!isGroup() &&*/ StringUtil.isNotNull(userInfo.getMkName())) {
                nkname = userInfo.getMkName();
            }

            head = userInfo.getHead();


//            LogUtil.getLog().d("tak", "taskName: " + nkname);

            msg.setFrom_nickname(nkname);
            msg.setFrom_avatar(head);


        }
//        this.msgListData = msgListData;

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
            if (isFirst) {
                MessageManager.getInstance().setMessageChange(true);
                MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, null);
            }
            return true;
        }
        return false;
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
        if (isGroup() && !MessageManager.getInstance().isGroupValid(groupInfo)) {//无效群，不存草稿
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
                            setBanView(isExited);
                        }
                    }
                });

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
                                MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllbean);
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
                    if (!isActivityValid()) {
                        return;
                    }
                    SignatureBean sign = response.body().getData();
                    String token = sign.getSign();
                    UserInfo minfo = UserAction.getMyInfo();
                    JrmfRpClient.openRpDetail(ChatActivity.this, "" + minfo.getUid(), token, rid, minfo.getName(), minfo.getHead());
                }
            }
        });

    }

    /***
     * 红包是否已经被抢,红包改为失效
     * @param rid
     */
    private void taskPayRbCheck(MsgAllBean msgAllBean, String rid, int reType, String token, int envelopeStatus) {
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
    private void updateEnvelopeToken(MsgAllBean msgAllBean, final String rid, int reType, String token, int envelopeStatus) {
        if (!TextUtils.isEmpty(token)) {
            msgAllBean.getRed_envelope().setAccessToken(token);
            msgAllBean.getRed_envelope().setEnvelopStatus(envelopeStatus);
        }
        replaceListDataAndNotify(msgAllBean);
        msgDao.redEnvelopeOpen(rid, envelopeStatus, reType, token);


    }


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
        if (!isGroup()) {
            return;
        }
        msgAction.groupInfo(toGid, true, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
//                if (response.body() == null)
//                    return;

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
                viewExtendFunction.bindDate(getItemModels());
                taskSessionInfo(true);
            }

            @Override
            public void onFailure(Call<ReturnBean<Group>> call, Throwable t) {
//                super.onFailure(call, t);
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
                viewExtendFunction.bindDate(getItemModels());
                taskSessionInfo(false);
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
                            userDao.updateUserinfo(userInfo);//本地更新对方数据
                            taskSessionInfo(true);
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
                int size = msgListData.size();
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
     * 检查是否显示已读
     */
    private boolean checkIsRead() {
        UserInfo userInfo = userDao.findUserInfo(toUId);
        if (userInfo == null) {
            return false;
        }
        int friendMasterRead = userInfo.getMasterRead();
        int friendRead = userInfo.getFriendRead();
        int myRead = userInfo.getMyRead();

        UserInfo myUserInfo = userDao.myInfo();
        int masterRead = myUserInfo.getMasterRead();
        if (friendMasterRead == 1 && friendRead == 1 && myRead == 1 && masterRead == 1) {
            return true;
        } else {
            return false;
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
                    msgDao.noteMsgAddSurvivaltime(groupInfo.getUsers().get(0).getUid(), gid);
                } else {
                    ToastUtil.show(response.body().getMsg());
                }
            }
        });
    }

    /**
     * 添加阅读即焚消息到队列
     */
    public void addSurvivalTime(MsgAllBean msgbean) {
        if (msgbean == null || BurnManager.getInstance().isContainMsg(msgbean) || msgbean.getSend_state() != ChatEnum.ESendStatus.NORMAL) {
            return;
        }
        if (msgbean.getSurvival_time() > 0 && msgbean.getEndTime() == 0) {
            long date = DateUtils.getSystemTime();
            msgDao.setMsgEndTime((date + msgbean.getSurvival_time() * 1000), date, msgbean.getMsg_id());
            msgbean.setEndTime(date + msgbean.getSurvival_time() * 1000);
            msgbean.setStartTime(date);
            EventBus.getDefault().post(new EventSurvivalTimeAdd(msgbean, null));
            LogUtil.getLog().d("SurvivalTime", "设置阅后即焚消息时间1----> end:" + (date + msgbean.getSurvival_time() * 1000) + "---msgid:" + msgbean.getMsg_id());
        }
    }

    public void addSurvivalTimeAndRead(MsgAllBean msgbean) {
        if (msgbean == null || BurnManager.getInstance().isContainMsg(msgbean) || msgbean.getSend_state() != ChatEnum.ESendStatus.NORMAL) {
            return;
        }
        if (msgbean.getSurvival_time() > 0 && msgbean.getEndTime() == 0 && msgbean.getRead() == 1) {
            long date = DateUtils.getSystemTime();
            msgDao.setMsgEndTime((date + msgbean.getSurvival_time() * 1000), date, msgbean.getMsg_id());
            msgbean.setEndTime(date + msgbean.getSurvival_time() * 1000);
            msgbean.setStartTime(date);
            EventBus.getDefault().post(new EventSurvivalTimeAdd(msgbean, null));
            LogUtil.getLog().d("SurvivalTime", "设置阅后即焚消息时间2----> end:" + (date + msgbean.getSurvival_time() * 1000) + "---msgid:" + msgbean.getMsg_id());
        }
    }


    public void addSurvivalTimeForList(List<MsgAllBean> list) {
        if (list == null && list.size() == 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            MsgAllBean msgbean = list.get(i);
            if (msgbean.getSurvival_time() > 0 && msgbean.getEndTime() == 0) {
                long date = DateUtils.getSystemTime();
                msgDao.setMsgEndTime((date + msgbean.getSurvival_time() * 1000), date, msgbean.getMsg_id());
                msgbean.setEndTime(date + msgbean.getSurvival_time() * 1000);
                msgbean.setStartTime(date);
                LogUtil.getLog().d("SurvivalTime", "设置阅后即焚消息时间3----> end:" + (date + msgbean.getSurvival_time() * 1000) + "---msgid:" + msgbean.getMsg_id());
            }
        }
        EventBus.getDefault().post(new EventSurvivalTimeAdd(null, list));
    }

    /*
     * 发送消息前，需要检测网络连接状态，网络不可用，不能发送
     * 每条消息发送前，需要检测，语音和小视频录制之前，仍需要检测
     * */
    public boolean checkNetConnectStatus() {
        boolean isOk;
        if (!NetUtil.isNetworkConnected()) {
            ToastUtil.show(this, "网络连接不可用，请稍后重试");
            isOk = false;
        } else {
            isOk = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.CONN_STATUS).get4Json(Boolean.class);
            if (!isOk) {
                ToastUtil.show(this, "连接已断开，请稍后再试");
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
                        super.onHandleError(baseResponse);
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
    public void getRedEnvelopeDetail(MsgAllBean msgBean, long rid, String token, int reType, boolean isNormalStyle) {
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
                            super.onHandleError(baseResponse);
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
                        super.onHandleError(baseResponse);
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(false);//取消点击外部消失弹窗
        final AlertDialog dialog = dialogBuilder.create();
        View dialogView = LayoutInflater.from(this).inflate(com.hm.cxpay.R.layout.dialog_identify, null);
        TextView tvCancel = dialogView.findViewById(com.hm.cxpay.R.id.tv_cancel);
        TextView tvIdentify = dialogView.findViewById(com.hm.cxpay.R.id.tv_identify);
        //取消
        tvCancel.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //去认证(需要先同意协议)
        tvIdentify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ServiceAgreementActivity.class));
                dialog.dismiss();
            }
        });
        //展示界面
        dialog.show();
        //解决圆角shape背景无效问题
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //相关配置
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        WindowManager manager = window.getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        //设置宽高，高度自适应，宽度屏幕0.8
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.width = (int) (metrics.widthPixels * 0.8);
        dialog.getWindow().setAttributes(lp);
        dialog.setContentView(dialogView);
    }

    public void showSettingPswDialog() {
        DialogDefault dialogSettingPayPsw = new DialogDefault(this, R.style.MyDialogTheme);
        dialogSettingPayPsw
                .setTitleAndSure(true, false)
                .setTitle("温馨提示")
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
                        super.onHandleError(baseResponse);
                        ToastUtil.show(context, baseResponse.getMessage());
                    }
                });

    }

    public String getTransferInfo(String info, int opType, boolean isMe, String nick) {
        String result = "";
        if (opType == PayEnum.ETransferOpType.TRANS_SEND) {
            if (TextUtils.isEmpty(info)) {
                if (isMe) {
                    result = "转账给" + nick;
                } else {
                    result = "转账给你";
                }
            } else {
                result = info;

            }
        } else if (opType == PayEnum.ETransferOpType.TRANS_RECEIVE) {
            if (TextUtils.isEmpty(info)) {
                if (isMe) {
                    result = "已收款";
                } else {
                    result = "已被领取";
                }
            } else {
                if (isMe) {
                    result = "已收款-" + info;
                } else {
                    result = "已被领取-" + info;
                }
            }
        } else if (opType == PayEnum.ETransferOpType.TRANS_REJECT) {
            if (TextUtils.isEmpty(info)) {
                if (isMe) {
                    result = "已退款";
                } else {
                    result = "已被退款";
                }
            } else {
                if (isMe) {
                    result = "已退款-" + info;
                } else {
                    result = "已被退款-" + info;
                }
            }
        } else if (opType == PayEnum.ETransferOpType.TRANS_PAST) {
            if (TextUtils.isEmpty(info)) {
                if (isMe) {
                    result = "已过期";
                } else {
                    result = "已过期";
                }
            } else {
                if (isMe) {
                    result = "已过期-" + info;
                } else {
                    result = "已过期-" + info;
                }
            }
        }
        return result;
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
        envelopeInfo.setAmount(StringUtil.getLong(bean.getEnvelopeAmount()));
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
        MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, null);
    }

    //删除临时红包信息
    private void deleteEnvelopInfo(EnvelopeInfo envelopeInfo) {
        msgDao.deleteEnvelopeInfo(envelopeInfo.getRid(), toGid, toUId, true);
        MsgAllBean lastMsg = null;
        if (msgListData != null) {
            int len = msgListData.size();
            lastMsg = msgListData.get(len - 1);
        }
        MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, lastMsg);
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
//        LogUtil.getLog().i("SurvivalTime", "deleteMsg:" + bean.getMsg_id());
        if (msgListData == null) {
            return;
        }
        int position = msgListData.indexOf(bean);
        if (position < 0) {
            return;
        }
        msgListData.remove(bean);
        mtListView.getListView().getAdapter().notifyItemRemoved(position);//删除刷新
        removeUnreadCount(1);
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
//        LogUtil.getLog().d("SurvivalTime", "deleteMsgList size=" + list.size());
        if (msgListData == null || list == null) {
            return;
        }
        msgListData.removeAll(list);
        removeUnreadCount(list.size());
//        mtListView.notifyDataSetChange();
        notifyData();
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
                Toast.makeText(context, "没有找到对应的程序", Toast.LENGTH_SHORT).show();
            }
//            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastUtil.show("附件不能打开，请下载相关软件！");
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
                    if(singleMeberInfoBean.getShutUpDuration()==0){
                        //2 该群是否全员禁言
                        if(groupInfo.getWordsNotAllowed()==0){
                            resendFileMsg(reMsg);
                        }else {
                            ToastUtil.showCenter(ChatActivity.this, "本群全员禁言中");
                        }
                    }else {
                        ToastUtil.showCenter(ChatActivity.this, "你已被禁言，暂时无法发送文件");
                    }
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<SingleMeberInfoBean>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show(ChatActivity.this, t.getMessage());
                //2 该群是否全员禁言
                if(groupInfo.getWordsNotAllowed()==0){
                    toSelectFile();
                }else {
                    ToastUtil.show("全员禁言中，无法发送文件消息!");
                }
            }
        });
    }

    /**
     * 发送文件
     * @param reMsg
     */
    private void resendFileMsg(MsgAllBean reMsg){
        //文件仍然存在，则重发
        if (net.cb.cb.library.utils.FileUtils.fileIsExist(reMsg.getSendFileMessage().getLocalPath())) {
            SendFileMessage fileMessage = SocketData.createFileMessage(reMsg.getMsg_id(), reMsg.getSendFileMessage().getLocalPath(), reMsg.getSendFileMessage().getUrl(), reMsg.getSendFileMessage().getFile_name(), reMsg.getSendFileMessage().getSize(), reMsg.getSendFileMessage().getFormat(),false);
            MsgAllBean fileMsgBean = sendMessage(fileMessage, ChatEnum.EMessageType.FILE, false);
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
}
