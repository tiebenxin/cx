package com.yanlong.im;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.example.nim_lib.config.Preferences;
import com.example.nim_lib.controll.AVChatProfile;
import com.example.nim_lib.ui.VideoActivity;
import com.example.nim_lib.util.PermissionsUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hm.cxpay.bean.BankBean;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.eventbus.IdentifyUserEvent;
import com.hm.cxpay.eventbus.RefreshBalanceEvent;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.utils.DateUtils;
import com.jrmf360.tools.utils.ThreadUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.EnvelopeInfo;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.NotificationConfig;
import com.yanlong.im.chat.bean.P2PAuVideoMessage;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.eventbus.EventMsgSync;
import com.yanlong.im.chat.eventbus.EventRefreshMainMsg;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.task.TaskLoadSavedGroup;
import com.yanlong.im.chat.tcp.TcpConnection;
import com.yanlong.im.chat.ui.MsgMainFragment;
import com.yanlong.im.location.LocationPersimmions;
import com.yanlong.im.location.LocationService;
import com.yanlong.im.location.LocationUtils;
import com.yanlong.im.notify.NotifySettingDialog;
import com.yanlong.im.repository.ApplicationRepository;
import com.yanlong.im.shop.ShopFragemnt;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.EventCheckVersionBean;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.NewVersionBean;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.bean.VersionBean;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.FriendMainFragment;
import com.yanlong.im.user.ui.LoginActivity;
import com.yanlong.im.user.ui.MyFragment;
import com.yanlong.im.user.ui.SplashActivity;
import com.yanlong.im.utils.socket.ExecutorManager;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketUtil;
import com.yanlong.im.utils.update.UpdateManage;
import com.zhaoss.weixinrecorded.CanStampEventWX;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.CanStampEvent;
import net.cb.cb.library.bean.EventLoginOut;
import net.cb.cb.library.bean.EventLoginOut4Conflict;
import net.cb.cb.library.bean.EventNetStatus;
import net.cb.cb.library.bean.EventOnlineStatus;
import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.bean.EventRefreshFriend;
import net.cb.cb.library.bean.EventRunState;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.manager.FileManager;
import net.cb.cb.library.manager.TokenManager;
import net.cb.cb.library.net.NetWorkUtils;
import net.cb.cb.library.net.NetworkReceiver;
import net.cb.cb.library.utils.BadgeUtil;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.IntentUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.NotificationsUtils;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.SpUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.utils.VersionUtil;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.ImageMoveView;
import net.cb.cb.library.view.StrikeButton;
import net.cb.cb.library.view.ViewPagerSlide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import cn.jpush.android.api.JPushInterface;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

import static net.cb.cb.library.utils.SharedPreferencesUtil.SPName.NOTIFICATION;


@Route(path = "/app/MainActivity")
public class MainActivity extends AppActivity {
    public final static String IS_LOGIN = "is_from_login";
    private ViewPagerSlide viewPage;
    private android.support.design.widget.TabLayout bottomTab;

    private Fragment[] fragments;
    private String[] tabs;
    private int[] iconRes;
    private int[] iconHRes;
    private StrikeButton sbmsg;
    private StrikeButton sbfriend;
    private StrikeButton sbme;
    private StrikeButton sbshop;
    private NotifySettingDialog notifyDialog;
    private NetworkReceiver mNetworkReceiver;
    private MsgMainFragment mMsgMainFragment;
    private ImageMoveView mBtnMinimizeVoice;
    Handler mHandler = new Handler();
    // 通话时间
    private int mPassedTime = 0;
    private final int TIME = 1000;
    private long mExitTime;
    private int mHour, mMin, mSecond;
    private EventFactory.VoiceMinimizeEvent mVoiceMinimizeEvent;
    private boolean isActivityStop;
    //定位相关
    private LocationService locService;
    private BDAbstractLocationListener listener;

    private UserAction userAction = new UserAction();
    private boolean testMe = true;
    private String lastPostLocationTime = "";//最近一次上传用户位置的时间
    private boolean isCreate = false;
    private ShopFragemnt mShowFragment;
    @EMainTab
    private int currentTab = EMainTab.MSG;
    private long firstPressTime = 0;//第一次双击时间
    private boolean isFromLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //创建仓库-仅登录时会创建
        MyAppLication.INSTANCE().createRepository();
        EventBus.getDefault().register(this);
        findViews();
        initEvent();
        isCreate = true;
        doRegisterNetReceiver();
        MyAppLication.INSTANCE().addSessionChangeListener(sessionChangeListener);
    }

    private void checkPermission() {
        SpUtil spUtil = SpUtil.getSpUtil();
        boolean isFist = spUtil.getSPValue(Preferences.IS_FIRST_DIALOG, false);
        if (!isFist) {
            String brand = Build.BRAND;
            brand = brand.toUpperCase();
            if (brand.contains("OPPO")) {
                permissionCheck();
            }
        }
    }


    private void initLocation() {
        lastPostLocationTime = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.POST_LOCATION_TIME).get4Json(String.class);
        //无缓存则直接定位，记录本次上传位置的时间
        if (TextUtils.isEmpty(lastPostLocationTime)) {
            getLocation();
        } else {
            //有缓存则按需求规则，超过24小时再上报用户地理位置信息
            try {
                if (!DateUtils.judgmentDate(lastPostLocationTime, DateUtils.getNowFormatTime(), 24)) {
                    getLocation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 部份手机覆盖安装后，网易云在MyApplicon有时会登录失败，导致部分用户使用不了音视频，所以在首页10秒后在检查网易云是否登录，没登录重新登录
     */
    private void checkNeteaseLogin() {
        if (!isFinishing()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!isFinishing()) {
                            if (NIMClient.getStatus() != StatusCode.LOGINED) {
                                LogUtil.getLog().i(MainActivity.class.getName(), "网易云登录失败，重新登录了:" + NIMClient.getStatus());
                                LogUtil.writeLog(">>>>>>>>>网易云登录失败，重新登录了 状态是: " + NIMClient.getStatus());
                                NIMClient.getService(AuthService.class).logout();// 需要先登出网易登录，在重新登录
                                UserAction userAction = new UserAction();
                                SpUtil spUtil = SpUtil.getSpUtil();
                                String account = spUtil.getSPValue("account", "");
                                String token = spUtil.getSPValue("token", "");
                                userAction.doNeteaseLogin(account, token);
                            } else {
                                LogUtil.getLog().i(MainActivity.class.getName(), "网易云登录成功");
                                LogUtil.writeLog(">>>>>>>>>网易云登录成功>>>>>>>>> ");
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }, 10000);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isCreate) {
            LogUtil.getLog().i("MainActivity", "isCreate=" + isCreate);
            uploadApp();
            checkRosters();
            checkNeteaseLogin();
            checkPermission();
            initLocation();
            getMsgToPC("123456");
        }

    }

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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            reTryExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    //自动寻找控件
    private void findViews() {
        viewPage = findViewById(R.id.viewPage);
        bottomTab = findViewById(R.id.bottom_tab);
//        BurnManager.getInstance().RunTimer();
        mBtnMinimizeVoice = findViewById(R.id.btn_minimize_voice);
    }


    public ViewPagerSlide getViewPage() {
        return viewPage;
    }

    //自动生成的控件事件
    private void initEvent() {
        mMsgMainFragment = MsgMainFragment.newInstance();
        mShowFragment = ShopFragemnt.newInstance();
        fragments = new Fragment[]{mMsgMainFragment, FriendMainFragment.newInstance(), mShowFragment, MyFragment.newInstance()};
        tabs = new String[]{"消息", "通讯录", "商城", "我"};
        iconRes = new int[]{R.mipmap.ic_msg, R.mipmap.ic_frend, R.mipmap.ic_shop, R.mipmap.ic_me};
        iconHRes = new int[]{R.mipmap.ic_msg_h, R.mipmap.ic_frend_h, R.mipmap.ic_shop_h, R.mipmap.ic_me_h};
        viewPage.setCurrentItem(currentTab);
        viewPage.setOffscreenPageLimit(2);
        viewPage.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments[i];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
        bottomTab.setupWithViewPager(viewPage);
        bottomTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == EMainTab.SHOP) {
                    viewPage.setCurrentItem(currentTab);
                    boolean hasToken = check();
                    if (!hasToken) {
                        showLoginDialog();
                    }
                }
                currentTab = tab.getPosition();
                viewPage.setCurrentItem(tab.getPosition());
                for (int i = 0; i < bottomTab.getTabCount(); i++) {
                    View rootView = bottomTab.getTabAt(i).getCustomView();
                    LinearLayout viewItem = rootView.findViewById(R.id.view_item);
                    StrikeButton sb = rootView.findViewById(R.id.sb);
                    TextView txt = rootView.findViewById(R.id.txt);
                    if (i == tab.getPosition()) { // 选中状态
                        sb.setButtonBackground(iconHRes[i]);
                        txt.setTextColor(getResources().getColor(R.color.green_500));
                    } else {// 未选中状态
                        sb.setButtonBackground(iconRes[i]);
                        txt.setTextColor(getResources().getColor(R.color.gray_400));
                    }
                }

                if (tab.getPosition() == EMainTab.ME) {
                    //每次点击检查新版泵
                    EventBus.getDefault().post(new EventCheckVersionBean());
                }
                // 同时点击导航栏跟气泡时，延迟关闭气泡
                if (tab.getPosition() == EMainTab.CONTACT || tab.getPosition() == EMainTab.ME) {
                    if (!isFinishing()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mMsgMainFragment != null) {
                                    mMsgMainFragment.hidePopView();
                                }
                            }
                        }, 100);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //双击第一个TAB采用此方法监听 (不再重写监听器)
                //双击消息，小于0.5s不响应，大于0.5s响应
                if (tab.getPosition() == 0) {
                    long now = System.currentTimeMillis();
                    if ((now - firstPressTime) > 500) {
                        firstPressTime = now;
                    } else {
                        mMsgMainFragment.moveToUnread();
                    }
                }
            }
        });
        // 提供自定义的布局添加Tab
        for (int i = 0; i < fragments.length; i++) {
            View rootView = getLayoutInflater().inflate(R.layout.tab_item, null);
            TextView txt = rootView.findViewById(R.id.txt);
            StrikeButton sb = rootView.findViewById(R.id.sb);
            if (i == EMainTab.SHOP) {
                sb.setSktype(1);
                //设置值
                sb.setNum(0, true);
                sbshop = sb;
            }
            if (i == EMainTab.ME) {
                sb.setSktype(1);
                //设置值
                sb.setNum(0, true);
                sbme = sb;
            }
            if (i == EMainTab.CONTACT) {
                sb.setSktype(1);
                sb.setNum(0, true);
                sbfriend = sb;
            }
            if (i == EMainTab.MSG) {//消息数量
                sbmsg = sb;
            }


            txt.setText(tabs[i]);
            bottomTab.getTabAt(i).setCustomView(rootView);
        }
        //切换
        bottomTab.getTabAt(1).select();
        bottomTab.getTabAt(0).select();


        // 启动聊天服务
        startChatServer();

        mBtnMinimizeVoice.setOnClickListener(new ImageMoveView.OnSingleTapListener() {
            @Override
            public void onClick() {
                mBtnMinimizeVoice.close(MainActivity.this);
                mHandler.removeCallbacks(runnable);
                AVChatProfile.getInstance().setAVMinimize(false);
                IntentUtil.gotoActivity(MainActivity.this, VideoActivity.class);
            }
        });

    }

    /**
     * 在主界面按两次back键退出App
     */
    private void reTryExit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtil.show(getApplicationContext(), "再按一次退出程序");
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    private void doRegisterNetReceiver() {
        if (mNetworkReceiver == null) {
            mNetworkReceiver = new NetworkReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mNetworkReceiver, filter);
        }
    }

    //检测通讯录问题
    private void checkRosters() {
        ExecutorManager.INSTANCE.getNormalThread().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = getIntent();
                    isFromLogin = intent.getBooleanExtra(IS_LOGIN, false);
                    if (isFromLogin) {//从登陆页面过来，从网络获取最新数据
                        taskLoadFriends();
//                    taskLoadSavedGroups();
                    } else {
                        UserDao userDao = new UserDao();
                        boolean hasInit = userDao.isRosterInit();
                        if (!hasInit) {//未初始化，初始化本地通讯录
                            taskLoadFriends();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void taskLoadSavedGroups() {
        new MsgAction().getMySavedGroup(new CallBack<ReturnBean<List<Group>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<Group>>> call, Response<ReturnBean<List<Group>>> response) {
                if (response.body() != null && response.body().getData() != null) {
                    List<Group> groups = response.body().getData();
                    TaskLoadSavedGroup taskGroup = new TaskLoadSavedGroup(groups);
                    taskGroup.execute();
                }
            }
        });
    }

    private void taskLoadFriends() {
        userAction.friendGet4Me(new CallBack<ReturnBean<List<UserInfo>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<UserInfo>>> call, Response<ReturnBean<List<UserInfo>>> response) {
                MessageManager.getInstance().notifyRefreshFriend(true, -1, CoreEnum.ERosterAction.LOAD_ALL_SUCCESS);
            }

            @Override
            public void onFailure(Call<ReturnBean<List<UserInfo>>> call, Throwable t) {
                super.onFailure(call, t);
                MessageManager.getInstance().notifyRefreshFriend(true, -1, CoreEnum.ERosterAction.LOAD_ALL_SUCCESS);
            }
        });
    }

    @Override
    protected void onStop() {

        super.onStop();
        updateNetStatus();
        isActivityStop = true;
        isCreate = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == JPluginPlatformInterface.JPLUGIN_REQUEST_CODE) {
//
//        }
    }

    private void updateNetStatus() {
        if (NetUtil.isNetworkConnected()) {
            EventNetStatus netStatusEvent = new EventNetStatus(CoreEnum.ENetStatus.SUCCESS_ON_NET);
            EventBus.getDefault().post(netStatusEvent);
        }
    }

    @Override
    protected void onDestroy() {
        MyAppLication.INSTANCE().removeSessionChangeListener(sessionChangeListener);
        LogUtil.getLog().i("MainActivity--跟踪--Main", "onDestroy--" + SocketUtil.getSocketUtil().isKeepConnect());
        if (!SocketUtil.getSocketUtil().isKeepConnect()) {
            stopChatService();
        }
        if (mNetworkReceiver != null) {
            unregisterReceiver(mNetworkReceiver);
        }
        AVChatProfile.getInstance().setAVMinimize(false);
        EventBus.getDefault().unregister(this);
        // 关闭浮动窗口
        mBtnMinimizeVoice.close(this);
        mHandler.removeCallbacks(runnable);
//        BurnManager.getInstance().cancel();
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (listener != null) {
            locService.unregisterListener(listener);
        }
        if (locService != null) {
            locService.stop();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventNetStatus(EventNetStatus event) {
        EventFactory.EventNetStatus eventNetStatus = new EventFactory.EventNetStatus(event.getStatus());
        EventBus.getDefault().post(eventNetStatus);
        reportIP(NetWorkUtils.getLocalIpAddress(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityStop = false;
        taskGetMsgNum();
        checkNotificationOK();
        checkPayEnvironmentInit();
        if (AppConfig.isOnline()) {
            checkHasEnvelopeSendFailed();
        }
        checkTokenValid();
    }

    //检测支付环境的初始化
    private void checkPayEnvironmentInit() {
        checkPayToken();
        IUser info = UserAction.getMyInfo();
        if (info != null) {
            PayEnvironment.getInstance().setPhone(info.getPhone());
            PayEnvironment.getInstance().setNick(info.getName());
            if (info.getUid() != null) {
                PayEnvironment.getInstance().setUserId(info.getUid().longValue());
            }
            UserBean bean = PayEnvironment.getInstance().getUser();
            if (bean == null || (bean != null && bean.getUid() != info.getUid().intValue())) {
                httpGetUserInfo(info.getUid());
            }
        }
        PayEnvironment.getInstance().setContext(AppConfig.getContext());
        if (PayEnvironment.getInstance().getBanks() == null) {//初始化银行
            getBankList();
        }
    }

    private void checkPayToken() {
        if (TextUtils.isEmpty(PayEnvironment.getInstance().getToken())) {
            TokenBean token = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).get4Json(TokenBean.class);
            if (token != null) {
                TokenManager.initToken(token.getAccessToken());
                PayEnvironment.getInstance().setToken(token.getAccessToken());
//                CommonInterceptor.headers = Headers.of(TokenManager.TOKEN_KEY, token.getAccessToken());
            }
        }
    }


    private void startChatServer() {
        // 启动聊天服务
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(new Intent(getContext(), ChatServer.class));
//        } else {
//            startService(new Intent(getContext(), ChatServer.class));
//        }
//        startService(new Intent(getContext(), ChatServer.class));
        TcpConnection.getInstance(AppConfig.getContext()).startConnect();

    }

    private void stopChatService() {
//        stopService(new Intent(getContext(), ChatServer.class));
        TcpConnection.getInstance(AppConfig.getContext()).destroyConnect();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventRefreshMainMsg event) {
//        taskGetMsgNum();
        taskGetFriendNum();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshBalance(RefreshBalanceEvent event) {
        httpGetUserInfo(PayEnvironment.getInstance().getUser().getUid());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventLoginOut(EventLoginOut event) {
        if (event.loginType != 1) {
            ToastUtil.show(context, "因长期未登录已过有效期,请重新登录");
        }
        loginoutComment();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventIdentifyUser(IdentifyUserEvent event) {
        httpGetUserInfo(PayEnvironment.getInstance().getUser().getUid());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventLoginOutconflict(EventLoginOut4Conflict event) {
        loginoutComment();
        startActivity(new Intent(getContext(), MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        );

        AlertYesNo alertYesNo = new AlertYesNo();
        alertYesNo.init(this, "提示", event.getMsg(), "确定", null, new AlertYesNo.Event() {
            @Override
            public void onON() {
                startActivity(new Intent(getContext(), LoginActivity.class));
                finish();
            }

            @Override
            public void onYes() {
                startActivity(new Intent(getContext(), LoginActivity.class));
                finish();
            }
        });
        alertYesNo.show();

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRunState(EventRunState event) {
        LogUtil.getLog().i("TAG", ">>>>EventRunState:" + event.getRun());
        if (event.getRun()) {
            startChatServer();
        } else {
            stopChatService();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventOnlineStatus(EventOnlineStatus event) {
        if (event.isOn()) {
//            MessageManager.getInstance().testReceiveMsg();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshFriend(EventRefreshFriend event) {
        if (event.getRosterAction() == CoreEnum.ERosterAction.LOAD_ALL_SUCCESS) {
            taskLoadSavedGroups();
        } else if (event.getRosterAction() == CoreEnum.ERosterAction.REQUEST_FRIEND
                || event.getRosterAction() == CoreEnum.ERosterAction.DEFAULT) {//请求添加为好友 申请进群
            taskGetFriendNum();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void voiceMinimizeEvent(EventFactory.VoiceMinimizeEvent event) {
        mPassedTime = event.passedTime;
        mVoiceMinimizeEvent = event;
        showMinimizeVoiceView(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void stopJPushResumeEvent(EventFactory.StopJPushResumeEvent event) {
        // TODO 处理部分手机收到音视频消息后，多个铃声在播放问题
        JPushInterface.stopPush(this);
        if (!isFinishing()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    JPushInterface.resumePush(MainActivity.this);
                }
            }, 500);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closevoiceMinimizeEvent(EventFactory.CloseVoiceMinimizeEvent event) {
        if (!isFinishing()) {
            // 判断ChatActivity是否到前端显示，不是则更新并发送音视频消息数据
            mBtnMinimizeVoice.close(this);
            mHandler.removeCallbacks(runnable);
            if (event != null) {
                MsgAllBean msgAllbean = null;
                if (event.avChatType == AVChatType.AUDIO.getValue()) {
                    P2PAuVideoMessage message = SocketData.createCallMessage(SocketData.getUUID(), 0, event.operation, event.txt);
                    msgAllbean = SocketData.createMessageBean(event.toUId, event.toGid, ChatEnum.EMessageType.MSG_VOICE_VIDEO, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), message);
                    SocketData.sendAndSaveMessage(msgAllbean);
//                    msgAllbean = SocketData.send4VoiceOrVideo(event.toUId, event.toGid, event.txt, MsgBean.AuVideoType.Audio, event.operation);
                } else if (event.avChatType == AVChatType.VIDEO.getValue()) {
                    P2PAuVideoMessage message = SocketData.createCallMessage(SocketData.getUUID(), 0, event.operation, event.txt);
                    msgAllbean = SocketData.createMessageBean(event.toUId, event.toGid, ChatEnum.EMessageType.MSG_VOICE_VIDEO, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), message);
                    SocketData.sendAndSaveMessage(msgAllbean);
//                    msgAllbean = SocketData.send4VoiceOrVideo(event.toUId, event.toGid, event.txt, MsgBean.AuVideoType.Vedio, event.operation);
                }
                EventRefreshChat eventRefreshChat = new EventRefreshChat();
                eventRefreshChat.isScrollBottom = true;
                EventBus.getDefault().post(eventRefreshChat);
                if (msgAllbean != null) {
                    /********通知更新sessionDetail************************************/
                    //因为msg对象 uid有两个，都得添加
                    List<String> gids = new ArrayList<>();
                    List<Long> uids = new ArrayList<>();
                    //gid存在时，不取uid
                    if (TextUtils.isEmpty(msgAllbean.getGid())) {
                        uids.add(msgAllbean.getFrom_uid());
                        uids.add(msgAllbean.getTo_uid());
                    } else {
                        gids.add(msgAllbean.getGid());
                    }
                    //回主线程调用更新session详情
                    MyAppLication.INSTANCE().repository.updateSessionDetail(gids, uids);
                    /********通知更新sessionDetail end************************************/
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeMinimizeEvent(EventFactory.CloseMinimizeEvent event) {
        if (!isFinishing()) {
            if (event.isClose) {
                mHandler.removeCallbacks(runnable);
            }
            mBtnMinimizeVoice.close(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void restartAppEvent(net.cb.cb.library.event.EventFactory.RestartAppEvent event) {
        if (!isFinishing()) {
            // 处理APP在后台，关闭某个权限后需要重启APP
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendP2PAuVideoDialMessageEvent(EventFactory.SendP2PAuVideoDialMessage event) {

        if (event.avChatType == AVChatType.AUDIO.getValue()) {
            SocketData.send4VoiceOrVideoNotice(event.toUId, event.toGid, MsgBean.AuVideoType.Audio);
        } else if (event.avChatType == AVChatType.VIDEO.getValue()) {
            SocketData.send4VoiceOrVideoNotice(event.toUId, event.toGid, MsgBean.AuVideoType.Vedio);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showVoiceMinimizeEvent(EventFactory.ShowVoiceMinimizeEvent event) {
        LogUtil.getLog().i("VideoActivity", "showVoiceMinimizeEvent：" + event.isStartRunThread);
        showMinimizeVoiceView(event.isStartRunThread);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void videoActivityEvent(EventFactory.VideoActivityEvent event) {
        IntentUtil.gotoActivity(MainActivity.this, VideoActivity.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void msgSync(EventMsgSync event) {
        getMsgToPC(event.getCode());
    }

    /**
     * 显示浮动按钮的音视频时长
     *
     * @param isStartRunThread 是否需要开一个线程计时
     */
    private void showMinimizeVoiceView(boolean isStartRunThread) {
        if (mVoiceMinimizeEvent != null && mVoiceMinimizeEvent.isCallEstablished) {// 是否接听
            if (!mBtnMinimizeVoice.isShown()) {
                mBtnMinimizeVoice.show(MyAppLication.getInstance().getApplicationContext(), getWindow());
            }
            if (!isFinishing() && isStartRunThread) {
                mBtnMinimizeVoice.updateCallTime(mVoiceMinimizeEvent.showTime);
                mHandler.postDelayed(runnable, TIME);
            } else {
                if (mHour > 0) {
                    mBtnMinimizeVoice.updateCallTime(String.format(Locale.CHINESE, "%02d:%02d:%02d", mHour, mMin, mSecond));
                } else {
                    mBtnMinimizeVoice.updateCallTime(String.format(Locale.CHINESE, "%02d:%02d", mMin, mSecond));
                }
            }
        } else {
            mBtnMinimizeVoice.show(MyAppLication.getInstance().getApplicationContext(), getWindow());
            mBtnMinimizeVoice.updateCallTime("等待接听");
        }
    }

    /**
     * 通话计时
     */
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            mPassedTime++;
            mHour = mPassedTime / 3600;
            mMin = mPassedTime % 3600 / 60;
            mSecond = mPassedTime % 60;

            if (!isFinishing()) {
                mHandler.postDelayed(this, TIME);
                if (mHour > 0) {
                    mBtnMinimizeVoice.updateCallTime(String.format(Locale.CHINESE, "%02d:%02d:%02d", mHour, mMin, mSecond));
                } else {
                    mBtnMinimizeVoice.updateCallTime(String.format(Locale.CHINESE, "%02d:%02d", mMin, mSecond));
                }
            }
        }
    };

    public void loginoutComment() {
        IUser userInfo = UserAction.getMyInfo();
        if (userInfo != null) {
            new SharedPreferencesUtil(SharedPreferencesUtil.SPName.IMAGE_HEAD).save2Json(userInfo.getHead() + "");
            new SharedPreferencesUtil(SharedPreferencesUtil.SPName.PHONE).save2Json(userInfo.getPhone() + "");
        }
        userAction.cleanInfo();
        MyAppLication.INSTANCE().destoryRepository();
    }

    private void uploadApp() {
        taskNewVersion();
    }


    private MsgDao msgDao = new MsgDao();

    /***
     * 未读消息
     * @return
     */
    private void taskGetMsgNum() {
        if (sbmsg == null)
            return;
        int num = msgDao.sessionReadGetAll();
        LogUtil.getLog().e("获取session未读数", "num=" + num);
        sbmsg.setNum(num, true);
        BadgeUtil.setBadgeCount(getApplicationContext(), num);
    }

    /***
     * 好友或者群申请数量
     */
    private void taskGetFriendNum() {
        //  ToastUtil.show(getContext(),"更新好友的提示数量");
        int sum = 0;
        sum += msgDao.remidGet("friend_apply");
        // sum+=msgDao.remidGet("friend_apply");
        //  sum+=msgDao.remidGet("friend_apply");
        sbfriend.setNum(sum, true);

    }

    /**
     * 发请求---判断是否需要更新
     */
    private void taskNewVersion() {
        userAction.getNewVersion(StringUtil.getChannelName(context), new CallBack<ReturnBean<NewVersionBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<NewVersionBean>> call, Response<ReturnBean<NewVersionBean>> response) {
                if (response.body() == null || response.body().getData() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    NewVersionBean bean = response.body().getData();
                    UpdateManage updateManage = new UpdateManage(context, MainActivity.this);
                    //强制更新
                    if (response.body().getData().getForceUpdate() != 0) {
                        updateManage.uploadApp(bean.getVersion(), bean.getContent(), bean.getUrl(), true, true);
                    } else {
                        //缓存最新版本
                        SharedPreferencesUtil preferencesUtil = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.NEW_VESRSION);
                        VersionBean versionBean = new VersionBean();
                        versionBean.setVersion(bean.getVersion());
                        preferencesUtil.save2Json(versionBean);
                        //非强制更新（新增一层判断：如果是大版本，则需要直接改为强制更新）
                        if (VersionUtil.isBigVersion(context, bean.getVersion())) {
                            updateManage.uploadApp(bean.getVersion(), bean.getContent(), bean.getUrl(), true, true);
                        } else {
                            updateManage.uploadApp(bean.getVersion(), bean.getContent(), bean.getUrl(), false, true);
                        }
                        //如有新版本，首页底部提示红点
                        if (bean != null && !TextUtils.isEmpty(bean.getVersion())) {
                            if (new UpdateManage(context, MainActivity.this).check(bean.getVersion())) {
                                sbme.setNum(1, true);
                            } else {
                                sbme.setNum(0, true);
                            }
                        }
                    }
                }
            }
        });
    }


    /*
     * 检测通知栏是否开启
     * */
    private void checkNotificationOK() {
        if (canRemindToSetting() && !NotificationsUtils.isNotificationEnabled(MainActivity.this)) {
            LogUtil.getLog().i(MainActivity.class.getSimpleName(), "无推送权限");
            notifyDialog = new NotifySettingDialog(MainActivity.this, R.style.MyDialogTheme);
            notifyDialog.setCancelable(false);
            notifyDialog.create();
            notifyDialog.setTitle("温馨提示");
            notifyDialog.setMessage("由于目前未开通系统通知服务，为不影响使用，将在3秒后前往设置");
            notifyDialog.show();
//            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
//            builder.setTitle("温馨提示");
//            builder.setMessage("由于目前未开通系统通知服务，为不影响使用，将在3秒后前往设置");
//            notifyDialog = builder.create();
//            notifyDialog.show();
            // TODO 解决IllegalArgumentException异常
            if (!isFinishing()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            notifyDialog.dismiss();
                        }
                        NotificationsUtils.toNotificationSetting(MainActivity.this);
                        saveNotifyConfig();
                    }
                }, 3000);
            }
        } else {
            LogUtil.getLog().i(MainActivity.class.getSimpleName(), "有推送权限" + canRemindToSetting());
        }
    }

    /*
     * 已经通知或者不是新版本，允许进入设置
     * */
    private boolean canRemindToSetting() {
        SharedPreferencesUtil sp = new SharedPreferencesUtil(NOTIFICATION);
        if (sp != null) {
            NotificationConfig config = sp.get4Json(NotificationConfig.class, "notify_config");
            if (config != null && userAction.getMyId() != null) {
//                LogUtil.getLog().i(MainActivity.class.getSimpleName(), "oldUid=" + config.getUid() + "--newUid=" + userAction.getMyId());
                if (config.getUid() == userAction.getMyId() && (!isNewVersion(config.getVersion()) || config.isHasNotify())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isNewVersion(String version) {
        if (TextUtils.isEmpty(version)) {
            return false;
        }
        String newVersion = VersionUtil.getVerName(this);
        int[] oldArr = StringUtil.getVersionArr(version);
        int[] newArr = StringUtil.getVersionArr(newVersion);
        LogUtil.getLog().i(MainActivity.class.getSimpleName(), "newVersion=" + newVersion + "--oldVersion=" + version);
        if (oldArr != null && newArr != null && oldArr.length == 3 && newArr.length == 3) {
            if (oldArr[0] < newArr[0]) {
                return true;
            } else {
                if (oldArr[1] < newArr[1]) {
                    return true;
                } else {
                    if (oldArr[2] < newArr[2]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void saveNotifyConfig() {
        NotificationConfig config = new NotificationConfig();
        config.setHasNotify(true);
        config.setUid(userAction.getMyId());
        config.setVersion(VersionUtil.getVerName(this));
        LogUtil.getLog().i(MainActivity.class.getSimpleName(), VersionUtil.getVerName(this));
        SharedPreferencesUtil sp = new SharedPreferencesUtil(NOTIFICATION);
        sp.save2Json(config, "notify_config");
    }

    public boolean isActivityStop() {
        return isActivityStop;
    }


//    private void getSurvivalTimeData() {
//        //延时操作，等待数据库初始化
//        ExecutorManager.INSTANCE.getNormalThread().execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //子线程延时 等待myapplication初始化完成
//                    //查询所有阅后即焚消息加入定时器
//                    List<MsgAllBean> list = new MsgDao().getMsg4SurvivalTime();
//                    if (list != null && list.size() > 0) {
////                        BurnManager.getInstance().addMsgAllBeans(list);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    /**
     * 检查是否开启悬浮窗权限
     * OPPO 手机必须开启程序自动启动或开启悬浮窗权限，程序退到后台才能弹出音视频界面
     */
    private void permissionCheck() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                PermissionsUtil.showPermissionDialog(this);
            }
        } else {
            String brand = android.os.Build.BRAND;
            brand = brand.toUpperCase();
            if (brand.equals("HUAWEI")) {
                if (!PermissionsUtil.checkHuaWeiFloatWindowPermission(this)) {
                    PermissionsUtil.showPermissionDialog(this);
                }
            } else if (brand.equals("MEIZU")) {
                if (!PermissionsUtil.checkMeiZuFloatWindowPermission(this)) {
                    PermissionsUtil.showPermissionDialog(this);
                }
            } else {


            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void posting(CanStampEvent event) {
        if (!isFinishing()) {
            //允许
            MessageManager.getInstance().setCanStamp(event.canStamp);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void posting(CanStampEventWX event) {
        if (!isFinishing()) {
            //允许
            MessageManager.getInstance().setCanStamp(event.canStamp);
        }
    }

    private void checkHasEnvelopeSendFailed() {
        List<EnvelopeInfo> list = msgDao.queryEnvelopeInfoList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                EnvelopeInfo info = list.get(i);
                if (info.getCreateTime() - System.currentTimeMillis() >= TimeToString.DAY) {//超过24小时
                    deleteEnvelopInfo(info);
                }
            }
        }
    }

    //删除临时红包信息
    private void deleteEnvelopInfo(EnvelopeInfo envelopeInfo) {
        msgDao.deleteEnvelopeInfo(envelopeInfo.getRid(), envelopeInfo.getGid(), envelopeInfo.getUid(), false);
        /********通知更新sessionDetail************************************/
        //因为msg对象 uid有两个，都得添加
        List<String> gids = new ArrayList<>();
        List<Long> uids = new ArrayList<>();
        //gid存在时，不取uid
        if (TextUtils.isEmpty(envelopeInfo.getGid())) {
            uids.add(envelopeInfo.getUid());
        } else {
            gids.add(envelopeInfo.getGid());
        }
        //回主线程调用更新session详情
        MyAppLication.INSTANCE().repository.updateSessionDetail(gids, uids);
        /********通知更新sessionDetail end************************************/
    }

    /**
     * 获取 绑定的银行卡列表
     * <p>
     * 备注：主要用于零钱首页更新"我的银行卡" 张数，暂时仅"充值、提现、我的银行卡"返回此界面后需要刷新
     */
    private void getBankList() {
        PayHttpUtils.getInstance().getBankList()
                .compose(RxSchedulers.<BaseResponse<List<BankBean>>>compose())
                .compose(RxSchedulers.<BaseResponse<List<BankBean>>>handleResult())
                .subscribe(new FGObserver<BaseResponse<List<BankBean>>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<List<BankBean>> baseResponse) {
                        List<BankBean> info = baseResponse.getData();
                        if (info != null) {
                            PayEnvironment.getInstance().setBanks(info);
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                    }
                });
    }

    /**
     * 请求零钱红包用户信息,刷新用户余额
     */
    private void httpGetUserInfo(long uid) {
        PayHttpUtils.getInstance().getUserInfo(uid)
                .compose(RxSchedulers.<BaseResponse<UserBean>>compose())
                .compose(RxSchedulers.<BaseResponse<UserBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<UserBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<UserBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            if (baseResponse.getData() != null) {
                                UserBean userBean = baseResponse.getData();
                                PayEnvironment.getInstance().setUser(userBean);
                            }
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse<UserBean> baseResponse) {
                    }
                });
    }

    /*
     *from
     * */
    @IntDef({EMainTab.MSG, EMainTab.CONTACT, EMainTab.SHOP, EMainTab.ME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EMainTab {
        int MSG = 0; // 消息界面
        int CONTACT = 1; // 好友界面
        int SHOP = 2; // 商城界面
        int ME = 3; // 我的界面
    }

    /**
     * 百度地图获取定位信息
     */
    private void getLocation() {
        if (!LocationPersimmions.checkPermissions(this)) {
            return;
        }
        if (!LocationUtils.isLocationEnabled(this)) {
//            ToastUtil.show("请打开定位服务");
            return;
        }
        locService = ((MyAppLication) getApplication()).locationService;
        LocationClientOption mOption = locService.getDefaultLocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        mOption.setCoorType("bd09ll");
        locService.setLocationOption(mOption);
        listener = new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {

                try {
                    if (bdLocation != null && bdLocation.getPoiList() != null) {
                        String city = bdLocation.getCity();
                        String country = bdLocation.getCountry();
                        String lat = bdLocation.getLatitude() + "";
                        String lon = bdLocation.getLongitude() + "";
                        locService.stop();//定位成功后停止定位
                        //请求——>上报用户地理位置信息
                        userAction.postLocation(city, country, lat, lon, new CallBack<ReturnBean>() {
                            @Override
                            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                                super.onResponse(call, response);
                                if (response != null && response.body() != null && response.body().isOk()) {
                                    LogUtil.getLog().i("TAG", "位置信息上报成功");
                                    //缓存本次调用的时间，24小时以内只需要发一次请求
                                    new SharedPreferencesUtil(SharedPreferencesUtil.SPName.POST_LOCATION_TIME).save2Json(DateUtils.getNowFormatTime());
                                }
                            }

                            @Override
                            public void onFailure(Call<ReturnBean> call, Throwable t) {
                                super.onFailure(call, t);
                                LogUtil.getLog().i("TAG", "位置信息上报失败");
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        locService.registerListener(listener);
        locService.start();
    }

    @SuppressLint("CheckResult")
    private void getMsgToPC(String code) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                List<MsgAllBean> msgList = msgDao.getMsgIn3Day();
                Collections.sort(msgList, new Comparator<MsgAllBean>() {
                    @Override
                    public int compare(MsgAllBean o1, MsgAllBean o2) {
                        if (o1 == null || o2 == null || o1.getTimestamp() == null || o2.getTimestamp() == null) {
                            return -1;
                        }
                        if (o1.getTimestamp().longValue() > o2.getTimestamp().longValue()) {
                            return 1;
                        } else if (o1.getTimestamp().longValue() < o2.getTimestamp().longValue()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });
                MsgBean.UniversalMessage message = SocketData.createUniversalMessage(msgList);
                if (message != null) {
                    byte[] bytes = message.toByteArray();
                    if (bytes != null) {
                        System.out.println("PC同步--1--" + bytes.length);
                        File file = FileManager.getInstance().saveMsgFile(bytes);
                        if (file != null) {
//                            parseFile(file);
                            uploadMsgFile(file, code);
                        }
                    }
                }
            }
        });
    }

    private void uploadMsgFile(File file, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        UpFileAction upFileAction = new UpFileAction();
        upFileAction.upFile(UserAction.getMyId() + "", UpFileAction.PATH.PC_MSG, fileName, MainActivity.this, new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
                LogUtil.getLog().i("PC同步消息", "文件上传成功--" + url);
            }

            @Override
            public void fail() {
                LogUtil.getLog().i("PC同步消息", "文件上传失败");
            }

            @Override
            public void inProgress(long progress, long zong) {

            }
        }, file.getAbsolutePath());
    }

    private void showLoginDialog() {
        if (isFinishing()) {
            return;
        }
        DialogCommon dialogLogin = new DialogCommon(this);
        dialogLogin.setContent("请退出重登后使用此功能", true)
                .setTitleAndSure(false, true)
                .setRight("开启")
                .setLeft("拒绝")
                .setListener(new DialogCommon.IDialogListener() {
                    @Override
                    public void onSure() {
                        if (!isFinishing()) {
                            loginoutComment();
                            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(loginIntent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancel() {
                        viewPage.setCurrentItem(EMainTab.MSG);
                    }
                }).show();

    }

    public final boolean check() {
        TokenBean token = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).get4Json(TokenBean.class);
        if (token == null || TextUtils.isEmpty(token.getBankReqSignKey())) {
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public final void parseFile(File file) {
        System.out.println("PC同步--2--文件" + file.length());
        byte[] bytes = FileManager.getInstance().readFileBytes(file);
        if (bytes != null) {
            System.out.println("PC同步--3--" + bytes.length);
            if (bytes != null) {
                try {
                    MsgBean.UniversalMessage message = MsgBean.UniversalMessage.parseFrom(bytes);
                    if (message != null) {


                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public final void updateMsgUnread(int num) {
        LogUtil.getLog().i("MainActivity", "更新消息未读数据：" + num);
        sbmsg.setNum(num, true);
        BadgeUtil.setBadgeCount(getApplicationContext(), num);
    }

    //上报IP
    private void reportIP(String ip) {
//        LogUtil.getLog().i("MainActivity", "上报IP--" + ip);
        if (TextUtils.isEmpty(ip)) {
            return;
        }
        long time = SpUtil.getSpUtil().getSPValue("reportIPTime", 0L);
        if (time <= 0 || !DateUtils.isInHours(time, System.currentTimeMillis(), 4)) {
            userAction.reportIP(ip, new CallBack<ReturnBean>(false) {
                @Override
                public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                    super.onResponse(call, response);
                    if (response != null && response.body() != null && response.body().isOk()) {
                        SpUtil.getSpUtil().putSPValue("reportIPTime", System.currentTimeMillis());
                    }
//                    if (response != null && response.body() != null) {
//                        LogUtil.getLog().i("MainActivity", "上报IP--" + response.body().getMsg());
//                    }
                }
            });
        }
    }

    //检测是否需要更新token
    private void checkTokenValid() {
        if (!isFromLogin) {
            TokenBean token = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).get4Json(TokenBean.class);
            if (token != null) {
                Long uid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.UID).get4Json(Long.class);
                if ((!token.isTokenValid(uid) /*|| token.getBankReqSignKey()==null*/) && NetUtil.isNetworkConnected()) {
                    LogUtil.getLog().i(MainActivity.class.getSimpleName(), "--token=" + token.getAccessToken() + "--uid" + uid);
                    userAction.updateToken(userAction.getDevId(this), new CallBack<ReturnBean<TokenBean>>(false) {
                        @Override
                        public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                            super.onResponse(call, response);
                        }
                    });
                }
            }
        }
    }
}
