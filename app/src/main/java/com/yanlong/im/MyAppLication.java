package com.yanlong.im;


import android.util.Log;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.BuildConfig;
import net.cb.cb.library.MainApplication;
import net.cb.cb.library.bean.EventRunState;
import net.cb.cb.library.utils.AppFrontBackHelper;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;

import org.android.agoo.xiaomi.MiPushRegistar;
import org.greenrobot.eventbus.EventBus;

import io.realm.Realm;

public class MyAppLication extends MainApplication {

    private static final String TAG = "MyAppLication";


    @Override
    public void onCreate() {
        super.onCreate();
        switch (BuildConfig.BUILD_TYPE) {
            case "debug":

                AppConfig.URL_HOST = "http://192.168.10.229:8080";
              //  AppConfig.URL_HOST = "http://192.168.10.110:18080";
                AppConfig.DEBUG = true;
               // AppConfig.SOCKET_IP = "192.168.10.110";
                // AppConfig.SOCKET_PORT = 19991;
                //AppConfig.SOCKET_IP="192.168.10.88";
                // AppConfig.SOCKET_PORT=21;
                AppConfig.SOCKET_IP = "192.168.10.229";
                AppConfig.SOCKET_PORT = 19991;

                break;
            case "pre":
                AppConfig.DEBUG = true;
                AppConfig.URL_HOST = "https://baidu.net";

                break;
            case "release":

                AppConfig.DEBUG = false;
                AppConfig.URL_HOST = "https://baidu.com";

                break;
        }
        //初始化数据库
        Realm.init(getApplicationContext());
        initUPush();
        initWeixinConfig();
        initRunstate();
    }


    private void initUPush() {
        UMConfigure.init(this, "5cdf7aab4ca357f3f600055f",
                "Umeng", UMConfigure.DEVICE_TYPE_PHONE,
                "8dd38f8da115dcf6441ce3922f30a2ac");

        MiPushRegistar.register(this,"bMsFYycwSstKDv19Mx9zxQ==", "5411801194485");

        //获取消息推送代理示例
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //设置通知栏显示数量
        mPushAgent.setDisplayNotificationNumber(2);
        //注册推送服务，每次调用register方法都会回调该接口
        new SharedPreferencesUtil(SharedPreferencesUtil.SPName.DEV_ID).clear();
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.i(TAG, "注册成功：deviceToken：-------->  " + deviceToken);
                new SharedPreferencesUtil(SharedPreferencesUtil.SPName.DEV_ID).save2Json(deviceToken);

            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e(TAG, "注册失败：-------->  " + "s:" + s + ",s1:" + s1);
                new SharedPreferencesUtil(SharedPreferencesUtil.SPName.DEV_ID).clear();
            }
        });
    }



    private void initWeixinConfig() {
        PlatformConfig.setWeixin("wx84ecce93acb0e78f", "63293f55248912676fccdfe59515ed42");
    }

    //初始化运行状态
    private void initRunstate(){

        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register( this, new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {
                //应用切到前台处理
                LogUtil.getLog().d(TAG,"--->应用切到前台处理");
                EventRunState enent=new EventRunState();
                enent.setRun(true);
                EventBus.getDefault().post(enent );

            }

            @Override
            public void onBack() {
                //应用切到后台处理
                LogUtil.getLog().d(TAG,"--->应用切到后台处理");
                EventRunState enent=new EventRunState();
                enent.setRun(false);
                EventBus.getDefault().post(enent );

            }
        });
    }


}
