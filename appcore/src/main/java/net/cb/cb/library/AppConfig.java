package net.cb.cb.library;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;

import java.util.Locale;


/***
 * 公共配置
 */
public class AppConfig {
    //    public static String URL_HOST = "http://127.0.0.1";
//    public static String SOCKET_IP = "127.0.0.1";
//    public static int SOCKET_PORT = 8090;
    public static Context APP_CONTEXT;
    public static boolean DEBUG = false;
    public static float FONT = 1.0f;
    public static String UP_PATH = BuildConfig.UP_PATH;
    private static boolean isAppRunning = false;//应用是否正在运行

    // 用户服务协议
    public static final String USER_AGREEMENT = "https://changxin.zhixun6.com/yhxy.html";
    // 隐私政策
    public static final String USER_PRIVACY = "https://changxin.zhixun6.com/yszc.html";
    private static boolean isOnline;

    //设置全局字体
    public static void setFont(float font) {
        FONT = font;
    }

    public static void setContext(Context context) {
        APP_CONTEXT = context;
    }

    public static Context getContext() {
        return APP_CONTEXT;
    }

//    public static String getUrlHost() {
//        return URL_HOST;
//    }

    /***
     * 指定host
     *
     * @param urlHost
     */
//    public static void setUrlHost(String urlHost) {
//        URL_HOST = urlHost;
//        NetUtil.getNet().resetHost();
//    }

    /***
     * 获取系统语言zh0
     * @return
     */
    public static String getLanguage() {
        //系统语言
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        return language;
    }

    /***
     * 获取系统国家cn
     * @return
     */
    public static String getCountry() {
        //系统语言
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        String ct = locale.getCountry();
        return ct;
    }

    public static boolean isChina() {
        return AppConfig.getCountry().equals("CN");
    }

    /**
     * 获取渠道名
     *
     * @return 如果没有获取成功，那么返回值为空
     */
    public static String getChannelName() {
        if (APP_CONTEXT == null) {
            return null;
        }

        String resultData = "";
        try {
            PackageManager packageManager = APP_CONTEXT.getPackageManager();
            if (packageManager != null) {
                //注意此处为ApplicationInfo，因为友盟设置的meta-data是在application标签中
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(APP_CONTEXT.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        //key要与manifest中的配置文件标识一致
                        resultData = applicationInfo.metaData.getString("UMENG_CHANNEL");
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
        if (TextUtils.isEmpty(resultData)) {
            resultData = "";
        }
        return resultData;
    }

    public static void setOnline(boolean online) {
        isOnline = online;
    }

    public static boolean isOnline() {
        return isOnline;
    }

    public static String getString(int id) {
        if (APP_CONTEXT == null) {
            return "";
        }
        return APP_CONTEXT.getString(id);
    }

    public static boolean isAppRuning() {
        return isAppRunning;
    }

    public static void setAppRuning(boolean appRuning) {
        isAppRunning = appRuning;
    }
}
