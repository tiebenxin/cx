package com.yanlong.im;


import android.util.Log;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.BuildConfig;
import net.cb.cb.library.MainApplication;
import net.cb.cb.library.bean.EventRunState;
import net.cb.cb.library.utils.AppFrontBackHelper;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;


import com.jrmf360.tools.JrmfClient;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;

import org.android.agoo.huawei.HuaWeiRegister;
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
                AppConfig.SOCKET_IP = "yanlong.1616d.top";
                AppConfig.URL_HOST = "https://" + AppConfig.SOCKET_IP + ":8080";
                AppConfig.DEBUG = true;
                AppConfig.SOCKET_PORT = 19991;
               // break;
            case "pre": //美国 usa-test.1616d.top    香港 hk-test.1616d.top
                AppConfig.DEBUG = false;
                AppConfig.SOCKET_IP = "hk-test.1616d.top";
                AppConfig.URL_HOST = "https://" + AppConfig.SOCKET_IP + ":8080";
                AppConfig.SOCKET_PORT = 19991;
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
        initRedPacket();
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


    private void initUPush() {
        UMConfigure.init(this, "5d284fab3fc19520bf000ec9",
                "Umeng", UMConfigure.DEVICE_TYPE_PHONE,
                "dfaeeefa090961c33bb804bdd5436797");
        UMConfigure.setLogEnabled(AppConfig.DEBUG);


        //获取消息推送代理示例
        final PushAgent mPushAgent = PushAgent.getInstance(this);
        //设置通知栏显示数量
        mPushAgent.setDisplayNotificationNumber(1);
        //   mPushAgent.setNotificationClickHandler(notificationClickHandler);

        //注册推送服务，每次调用register方法都会回调该接口
        new SharedPreferencesUtil(SharedPreferencesUtil.SPName.DEV_ID).clear();
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.i("youmeng", "注册成功：deviceToken：-------->  " + deviceToken);
                new SharedPreferencesUtil(SharedPreferencesUtil.SPName.DEV_ID).save2Json(deviceToken);

                //每次启动,一定要开启这个
                mPushAgent.enable(new IUmengCallback() {
                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "PushAgent推送开启成功" );
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        Log.e(TAG, "PushAgent推送开启失败:"+s+s1 );
                    }
                });

            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("youmeng", "注册失败：-------->  " + "s:" + s + ",s1:" + s1);
                new SharedPreferencesUtil(SharedPreferencesUtil.SPName.DEV_ID).clear();
            }
        });


        //注册小米推送
        MiPushRegistar.register(getApplicationContext(), "2882303761518011485", "5411801194485");
        //注册华为推送
        HuaWeiRegister.register(this);
    }


    private void initWeixinConfig() {
        PlatformConfig.setWeixin("wx84ecce93acb0e78f", "63293f55248912676fccdfe59515ed42");
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


//    UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
//        @Override
//        public void dealWithCustomAction(Context context, UMessage msg) {
//            Log.v("PushUtil","跳转");
//            Intent intent = new Intent(context, MainActivity.class);
//            context.startActivity(intent);
//
//        }
//    };

}
