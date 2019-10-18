package com.yanlong.im;


import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.jrmf360.tools.JrmfClient;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.yanlong.im.utils.LogcatHelper;
import com.yanlong.im.utils.MyDiskCacheController;
import com.yanlong.im.utils.MyDiskCacheUtils;
import com.yanlong.im.utils.MyException;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.BuildConfig;
import net.cb.cb.library.MainApplication;
import net.cb.cb.library.bean.EventRunState;
import net.cb.cb.library.utils.AppFrontBackHelper;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.UpLoadUtils;
import net.cb.cb.library.utils.VersionUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import cn.jpush.android.api.JPushInterface;
import io.realm.Realm;

public class MyAppLication extends MainApplication {

    private static final String TAG = "MyAppLication";

    private final String U_APP_KEY = "5d53659c570df3d281000225";

    @Override
    public void onCreate() {
        super.onCreate();
        AppConfig.setContext(getApplicationContext());
        ///推送处理
        initUPushPre();

        if (!getApplicationContext().getPackageName().equals(getCurrentProcessName())) {
            return;
        }

        //如果需要调试切换版本,请直接修改debug中的ip等信息
        switch (BuildConfig.BUILD_TYPE) {
            case "debug"://测试服
                AppConfig.DEBUG = true;
                //---------------------------
                AppConfig.SOCKET_IP = "yanlong.1616d.top";
                AppConfig.URL_HOST = "https://" + AppConfig.SOCKET_IP + ":8080";
                AppConfig.SOCKET_PORT = 19991;
                AppConfig.UP_PATH = "test-environment";

//                AppConfig.SOCKET_IP = "im-app.zhixun6.com";
//                AppConfig.URL_HOST = "https://" + AppConfig.SOCKET_IP + ":8080";
//                AppConfig.SOCKET_PORT = 19991;
//                AppConfig.UP_PATH = "product-environment";
                break;
            case "pre": //预发布服  美国 usa-test.1616d.top    香港 hk-test.1616d.top
                AppConfig.DEBUG = false;
                //---------------------------
                AppConfig.SOCKET_IP = "hk-test.1616d.top";
                AppConfig.URL_HOST = "https://" + AppConfig.SOCKET_IP + ":8080";
                AppConfig.SOCKET_PORT = 19991;
                AppConfig.UP_PATH = "development";
                break;
            case "release"://正式服
                AppConfig.DEBUG = false;
                //---------------------------
                AppConfig.SOCKET_IP = "im-app.zhixun6.com";
                AppConfig.URL_HOST = "https://" + AppConfig.SOCKET_IP + ":8080";
                AppConfig.SOCKET_PORT = 19991;
                AppConfig.UP_PATH = "product-environment";
                break;
        }
        //初始化日志
        LogUtil.getLog().init(AppConfig.DEBUG);
        //初始化数据库
        Realm.init(getApplicationContext());

        // initUPush();

        //--------------------------
        initWeixinConfig();
        initRunstate();
        initRedPacket();
        LogcatHelper.getInstance(this).start();
//        initException();
        initUploadUtils();
        initBugly();
        initCache();
    }

    private void initCache() {
        MyDiskCacheUtils.getInstance().setDiskController(new MyDiskCacheController());
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
        //设置为测试环境
        JrmfClient.isDebug(AppConfig.DEBUG);
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
        PlatformConfig.setWeixin("wxf84321334bcb8c56", "cd084970cc54cbae19782c761bc2885f");
    }

    //初始化运行状态
    private void initRunstate() {
        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(this, new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {
                //应用切到前台处理
                LogUtil.getLog().d(TAG, "--->应用切到前台处理");
                EventRunState enent = new EventRunState();
                enent.setRun(true);
                EventBus.getDefault().post(enent);
            }

            @Override
            public void onBack() {
                //应用切到后台处理
                LogUtil.getLog().d(TAG, "--->应用切到后台处理");
                EventRunState enent = new EventRunState();
                enent.setRun(false);
                EventBus.getDefault().post(enent);
            }
        });
    }

}
