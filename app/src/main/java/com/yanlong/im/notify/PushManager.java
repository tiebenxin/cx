package com.yanlong.im.notify;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.utils.SharedPreferencesUtil;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

/**
 * @anthor Liszt
 * @data 2019/8/23
 * Description
 */
public class PushManager {
    public final String TAG = PushManager.class.getSimpleName();
    private static PushManager INSTANCE;

    public static PushManager newInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PushManager();
        }
        return INSTANCE;
    }

    public void initUPush(Context context) {
        UMConfigure.init(context, "5d284fab3fc19520bf000ec9",
                "Umeng", UMConfigure.DEVICE_TYPE_PHONE,
                "dfaeeefa090961c33bb804bdd5436797");
        UMConfigure.setLogEnabled(AppConfig.DEBUG);


        //获取消息推送代理示例
        final PushAgent mPushAgent = PushAgent.getInstance(context);
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
                        Log.e(TAG, "PushAgent推送开启成功");
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        Log.e(TAG, "PushAgent推送开启失败:" + s + s1);
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
        MiPushRegistar.register(context, "2882303761518011485", "5411801194485");
        //注册华为推送
        HuaWeiRegister.register((Application) context);
    }

}