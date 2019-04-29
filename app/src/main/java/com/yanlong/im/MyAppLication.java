package com.yanlong.im;


import android.util.Log;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.BuildConfig;
import net.cb.cb.library.MainApplication;
import net.cb.cb.library.utils.LogUtil;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.yanlong.im.utils.DaoUtil;

import io.realm.Realm;

public class MyAppLication extends MainApplication {

    private static final String TAG = "MyAppLication";


    @Override
    public void onCreate() {
        super.onCreate();
        switch (BuildConfig.BUILD_TYPE) {
            case "debug":

                AppConfig.URL_HOST = "http://192.168.10.229:8080";
                AppConfig.DEBUG = true;
                AppConfig.SOCKET_IP="192.168.10.110";
                AppConfig.SOCKET_PORT=19991;
                //AppConfig.SOCKET_IP="192.168.10.88";
               // AppConfig.SOCKET_PORT=21;

                break;
            case "pre":
                AppConfig.DEBUG = true;
                AppConfig.URL_HOST = "https://baidu.net";

                break;
            case "release":
                //test 后面这里改false
                AppConfig.DEBUG = false;
                AppConfig.URL_HOST = "https://baidu.com";

                break;
        }



        LogUtil.getLog().init(AppConfig.DEBUG);
      //初始化数据库
        Realm.init(getApplicationContext());


        initUPush();
    }

    private void initUPush(){
        UMConfigure.init(this,"5cad45f33fc195e947000b4d",
                "umeng",UMConfigure.DEVICE_TYPE_PHONE,"f731980514bd5a9ad50eee9a1fbc8907");

        //获取消息推送代理示例
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //设置通知栏显示数量
        mPushAgent.setDisplayNotificationNumber(2);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.i(TAG,"注册成功：deviceToken：-------->  " + deviceToken);
            }
            @Override
            public void onFailure(String s, String s1) {
                Log.e(TAG,"注册失败：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });
    }

}
