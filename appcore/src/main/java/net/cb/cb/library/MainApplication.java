package net.cb.cb.library;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;


public class MainApplication extends MultiDexApplication {
    private Context context;
    private static final String TAG = "MainApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        AppConfig.APP_CONTEXT = context;




        Fresco.initialize(this);

        initOther();
       // initFont();
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


    /***
     * 初始化字体，可以放到启动页中
     */
    private void initFont() {
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_SCAN);
        Float font = sharedPreferencesUtil.get4Json(Float.class);
        if (font != null) {
            AppConfig.setFont(font);
        }
    }

    private void initOther() {


    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


/*    private void initUmeng() {
        UMConfigure.init(context, UMConfigure.DEVICE_TYPE_PHONE, ManifestUtil.getXmlValue(context, "UMENG_SECERT"));
        PlatformConfig.setQQZone(ManifestUtil.getXmlValue(context,"UMENG_QQ_ID"), ManifestUtil.getXmlValue(context, "UMENG_QQ_KEY"));
        PlatformConfig.setWeixin(ManifestUtil.getXmlValue(context,"UMENG_WX_ID"), ManifestUtil.getXmlValue(context, "UMENG_WX_KEY"));

        UMConfigure.setLogEnabled(AppConfig.DEBUG);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        //根据设置来开启推送

        IUmengRegisterCallback test;
        mPushAgent.register(test=new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {


              *//*  DriverInfoBean driverInfo = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.DRIVER_INFO).get4Json(DriverInfoBean.class);
                // LogUtil.getLog().i("", "draverToken>>>:" + driverInfo);
                if (driverInfo == null) {
                    driverInfo = new DriverInfoBean();
                }
                driverInfo.setDeviceToken(deviceToken);
                if (driverInfo.getMachineCode() == null || driverInfo.getMachineCode().length() < 1) {
                    driverInfo.setMachineCode(UUID.randomUUID().toString());

                }
                //注册成功会返回device token
                LogUtil.getLog().i(TAG, "推送开启成功:" + driverInfo.getDeviceToken() + "|" + driverInfo.getMachineCode());
                new SharedPreferencesUtil(SharedPreferencesUtil.SPName.DRIVER_INFO).save2Json(driverInfo);*//*


            }

            @Override
            public void onFailure(String s, String s1) {
                LogUtil.getLog().e(TAG, "推送开启失败:" + s1);
            }
        });
       // test.onSuccess("dafs4e384r3ad3");
        PushAgent.getInstance(context).onAppStart();
        Boolean push = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.PUSH).get4Json(Boolean.class);
        push = push == null ? true : push;
        if (!push) {
            mPushAgent.disable(new IUmengCallback() {
                @Override
                public void onSuccess() {
                    LogUtil.getLog().i(TAG, "推送配置未打开,所以关闭推送 ");
                }

                @Override
                public void onFailure(String s, String s1) {

                }
            });
        }

    }*/

}
