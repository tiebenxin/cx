package com.yanlong.im;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.WebView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.nim_lib.config.Preferences;
import com.example.nim_lib.controll.AVChatProfile;
import com.example.nim_lib.controll.AVChatSoundPlayer;
import com.example.nim_lib.ui.VideoActivity;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jrmf360.tools.JrmfClient;
import com.kye.net.NetRequestHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.util.NIMUtil;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.server.MessageIntentService;
import com.yanlong.im.controll.AVChatKit;
import com.yanlong.im.location.LocationService;
import com.yanlong.im.repository.ApplicationRepository;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.ChatBitmapCache;
import com.yanlong.im.utils.EmojBitmapCache;
import com.yanlong.im.utils.IVolleyInitImp;
import com.yanlong.im.utils.LogcatHelper;
import com.yanlong.im.utils.MyDiskCacheController;
import com.yanlong.im.utils.MyDiskCacheUtils;
import com.yanlong.im.utils.MyException;
import com.yanlong.im.view.face.FaceView;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.MainApplication;
import net.cb.cb.library.bean.EventRunState;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.utils.AppFrontBackHelper;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.SpUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.UpLoadUtils;
import net.cb.cb.library.utils.VersionUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import io.realm.Realm;
import io.realm.RealmResults;

public class MyAppLication extends MainApplication {

    private static final String TAG = "MyAppLication";
    private final String U_APP_KEY = "5d53659c570df3d281000225";

    public LocationService locationService;
    //    public Vibrator mVibrator;
    //全局数据仓库
    public ApplicationRepository repository;
    public Handler handler = new Handler();


    @Override
    public void onCreate() {
        super.onCreate();
        initNim();
        AppConfig.setContext(getApplicationContext());
        ///推送处理
        initUPushPre();

        if (!getApplicationContext().getPackageName().equals(getCurrentProcessName())) {
            return;
        }
        initBuildType();
        //初始化日志:开启本地日志
        LogUtil.getLog().init(/*AppConfig.DEBUG*/true);

        //初始化数据库
        Realm.init(getApplicationContext());
        //初始化应用仓库
        createRepository();
        initWeixinConfig();
        initRunstate();
        initRedPacket();
        LogcatHelper.getInstance(this).start();
        initException();
        initUploadUtils();
        if ("release".equals(BuildConfig.BUILD_TYPE)) {
            initBugly();
        }
        initCache();
        // 初始化表情
        FaceView.initFaceMap();
        initLocation();//初始化定位
        initARouter();//初始化路由
        initVolley();
        HandleWebviewCrash();
    }

    /**
     * TODO 处理商城shopfragment里Webview造成多进程的异常 bugly #108910 (经查证,仅努比亚 NX563J会频繁报这个异常)
     */
    private void HandleWebviewCrash() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getCurrentProcessName();
            if (!"com.yanlong.im".equals(processName)) {//判断不等于默认进程名称
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }

    private Intent messageIntentService = null;

    public void startMessageIntentService() {
        if (messageIntentService == null) {
            messageIntentService = new Intent(this, MessageIntentService.class);
        }
//        LogUtil.getLog().i("Liszt_test", "接收到消息-启动服务");
        startService(messageIntentService);
    }

    /**
     * 初始化数据仓库--已登录的户
     * 1.已登录的用户-在application onCreate中创建
     * 2.刚登录用户-在MainActivity onCreate中创建
     * 3.退出登录时，销毁数据仓库
     */
    public void createRepository() {
        if (repository == null) {
            //同步使用友盟设备号,如果同步失败使用自己设备号
            TokenBean token = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).get4Json(TokenBean.class);
            if (token != null) {//用户已经登录
                repository = new ApplicationRepository();
            }
        }
    }

    /**
     * 销毁用户仓库
     * 1.退出登录
     * 2.Application终止
     */
    public void destoryRepository() {
        if (repository != null) {
            repository.onDestroy();
            repository = null;
        }
        //停止消息处理service,不再接受消息
        if (messageIntentService != null) {
            stopService(messageIntentService);
        }
        MessageManager.getInstance().stopOfflineTask();
    }


    public static MyAppLication INSTANCE() {
        return (MyAppLication) instance;
    }

    /**
     * 获取所有session会话列表数据
     *
     * @return
     */
    public RealmResults<Session> getSessions() {
        return repository == null ? null : repository.getSesisons();
    }

    /**
     * 获取所有通讯录好友
     *
     * @return
     */
    public RealmResults<UserInfo> getFriends() {
        return repository == null ? null : repository.getFriends();
    }

    /**
     * sessions对象是否已经加载
     *
     * @return
     */
    public boolean iSSessionsLoad() {
        boolean result = false;
        if (repository != null && repository.getSesisons().isLoaded()) {
            result = true;
        }
        return result;
    }

    public void addSessionChangeListener(ApplicationRepository.SessionChangeListener sessionChangeListener) {
        if (repository != null) repository.addSessionChangeListener(sessionChangeListener);
    }

    public void removeSessionChangeListener(ApplicationRepository.SessionChangeListener sessionChangeListener) {
        if (repository != null) repository.removeSessionChangeListener(sessionChangeListener);
    }

    public void addFriendChangeListener(ApplicationRepository.FriendChangeListener friendChangeListener) {
        if (repository != null) repository.addFriendChangeListener(friendChangeListener);
    }

    public void removeFriendChangeListener(ApplicationRepository.FriendChangeListener friendChangeListener) {
        if (repository != null) repository.removeFriendChangeListener(friendChangeListener);
    }

    private void initBuildType() {
        switch (BuildConfig.BUILD_TYPE) {
            case "debug":
                AppConfig.DEBUG = true;
                break;
            case "pre":
            case "release":
                AppConfig.DEBUG = false;
                break;
        }
    }

    /**
     * 初始化网易云信
     */
    private void initNim() {
        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录） 必须放到主Application中
        SDKOptions options = new SDKOptions();
        // TODO 初始化SDK时配置SDKOptions - disableAwake为true来禁止后台进程唤醒UI进程, 设置了以后，程序最小化后通知栏将不会显示语音的通知
        // 避免Fatal Exception: android.app.RemoteServiceException: Context.startForegroundService() did not then call Service.startForeground()
        options.disableAwake = true;
        NIMClient.init(this, getLoginInfo(), options);
        LogUtil.getLog().d(TAG, "NIMClient.init()");
        // 以下逻辑只在主进程初始化时执行
        if (NIMUtil.isMainProcess(this)) {
            AVChatSoundPlayer.setContext(this);
            AVChatKit.getInstance().init(this);
        }
    }

    private void initCache() {
        MyDiskCacheUtils.getInstance().setDiskController(new MyDiskCacheController()).setContext(this);
    }

    private void initBugly() {
        String packageName = this.getPackageName();
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setAppChannel(StringUtil.getChannelName(this));
        strategy.setAppVersion(VersionUtil.getVerName(this));
        strategy.setAppPackageName(this.getPackageName());
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        CrashReport.initCrashReport(this, "7780d7e928", false, strategy);

    }

    private void initVolley() {
        // 初始化新网络框架请求 用于文件断点续传
        NetRequestHelper.getInstance().init(this, new IVolleyInitImp());
    }

    /**
     * 获取网易云账号跟Toekn
     *
     * @return
     */
    private LoginInfo getLoginInfo() {
        SpUtil spUtil = SpUtil.getSpUtil();
        String account = spUtil.getSPValue("account", "");
        String token = spUtil.getSPValue("token", "");
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    private void initUploadUtils() {
        UpLoadUtils.getInstance().init(this);
    }


    /**
     * 获取当前进程名
     */
    private String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService
                (Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }

    /*
     异常捕获
      */
    private void initException() {
        MyException myException = MyException.getInstance();
        myException.init(getApplicationContext());
    }

    /***
     * 初始化红包
     */
    private void initRedPacket() {
        //改为正式环境
        JrmfClient.isDebug(false);
        /*** 需要在Manifest.xml文件*（JRMF_PARTNER_ID）和* 红包名称（JRMF_PARTNER*/
        JrmfClient.init(this);
        com.jrmf360.tools.utils.LogUtil.init(AppConfig.DEBUG);

    }

    private void initUPushPre() {
        UMConfigure.init(this, "5d53659c570df3d281000225",
                "Umeng", UMConfigure.DEVICE_TYPE_PHONE,
                "f045bf243689c2363d5714b781ce556e");
        UMConfigure.setLogEnabled(AppConfig.DEBUG);

        //极光推送初始化
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        MiPushClient.getRegId(getApplicationContext());

    }

    private void initWeixinConfig() {
        PlatformConfig.setWeixin("wxdfd2898507cc4f94", "f5de400d1457c4e75f8719867e2e4810");
    }

    /**
     * 初始化运行状态
     */
    private void initRunstate() {
        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(this, new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {
                //应用切到前台处理
                AppConfig.setAppRuning(true);
                EventRunState enent = new EventRunState();
                enent.setRun(true);
                EventBus.getDefault().post(enent);
                // 打开浮动窗口权限时，重新显示音视频浮动按钮
                if (AVChatProfile.getInstance().isAVMinimize()) {
                    EventFactory.ShowVoiceMinimizeEvent event = new EventFactory.ShowVoiceMinimizeEvent();
                    event.isStartRunThread = false;
                    EventBus.getDefault().post(event);
                } else {
                    // 音视频从后台切回前台时判断是否需要打开音视频界面
                    if (VideoActivity.returnVideoActivity) {
                        VideoActivity.returnVideoActivity = false;
                        EventBus.getDefault().post(new EventFactory.VideoActivityEvent());
                    } else if (AVChatProfile.getInstance().isCallIng() &&
                            !StringUtil.isForeground(getApplicationContext(), VideoActivity.class.getName())
                            && !AVChatProfile.getInstance().isCallEstablished()) {// 正在拨打电话&没有显示音视频界面&电话没有接通
                        gotoVideoActivity();
                    }
                }
            }

            @Override
            public void onBack() {
                //应用切到后台处理
                AppConfig.setAppRuning(false);

                EventRunState enent = new EventRunState();
                enent.setRun(false);
                EventBus.getDefault().post(enent);
                // 隐藏音视频浮动按钮
                if (AVChatProfile.getInstance().isAVMinimize()) {
                    EventFactory.CloseMinimizeEvent event = new EventFactory.CloseMinimizeEvent();
                    event.isClose = false;
                    EventBus.getDefault().post(event);
                }
            }
        });
    }

    public void initARouter() {
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);

    }

    @Override
    public void onTerminate() {
        //清除表情缓存
        EmojBitmapCache.getInstance().clear();
        ChatBitmapCache.getInstance().clear();

        //清除仓库对象
        destoryRepository();
        handler = null;
        super.onTerminate();
    }

    //初始化定位sdk，建议在Application中创建
    private void initLocation() {
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        locationService = new LocationService(getApplicationContext());
//        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
    }

    /**
     * 重新打开音视频接听界面
     */
    private void gotoVideoActivity() {
        if (AVChatKit.getInstance().getaVChatData() != null && AVChatKit.getInstance().getUserInfo() != null) {
            AVChatData data = AVChatKit.getInstance().getaVChatData();
            UserInfo userInfo = AVChatKit.getInstance().getUserInfo();
            String extra = data.getExtra();
            LogUtil.getLog().e(TAG, "Extra Message->" + extra);

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Preferences.AVCHATDATA, data);
            if (userInfo != null) {
                intent.putExtra(Preferences.USER_HEAD_SCULPTURE, userInfo.getHead());
                if (!TextUtils.isEmpty(userInfo.getMkName())) {
                    intent.putExtra(Preferences.USER_NAME, userInfo.getMkName());
                } else {
                    intent.putExtra(Preferences.USER_NAME, userInfo.getName());
                }
            }
            if (!TextUtils.isEmpty(extra)) {
                try {
                    Map<String, String> map = new Gson().fromJson(extra, Map.class);
                    String roomId = map.get("roomId");
                    Long friend = Long.parseLong(map.get("friend"));
                    intent.putExtra(Preferences.ROOM_ID, roomId);
                    intent.putExtra(Preferences.FRIEND, friend);
                } catch (JsonSyntaxException exception) {

                }
            }
            intent.putExtra(Preferences.VOICE_TYPE, CoreEnum.VoiceType.RECEIVE);
            intent.putExtra(Preferences.AVCHA_TTYPE, data.getChatType().getValue());
            intent.setClass(this, VideoActivity.class);
            // TODO oppo 必须要改开机自启动，或开启悬浮窗权限才能生效 ，文章地址：https://www.jianshu.com/p/5f6d8379533b
            startActivity(intent);
        }
    }
}
