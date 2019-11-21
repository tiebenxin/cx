//package com.example.nim_lib.config;
//
//import android.content.Context;
//import android.content.pm.ApplicationInfo;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.LocaleList;
//
//import com.example.nim_lib.BuildConfig;
//import com.example.nim_lib.net.NetUtil;
//
//import java.util.Locale;
//
//
///***
// * 公共配置
// */
//public class AppConfig {
//    public static String URL_HOST = "http://127.0.0.1";
//    public static String SOCKET_IP = "127.0.0.1";
//    public static int SOCKET_PORT = 8090;
//    public static Context APP_CONTEXT;
//    public static boolean DEBUG = false;
//    public static float FONT = 1.0f;
//    public static String UP_PATH = "";
//
//    //设置全局字体
//    public static void setFont(float font) {
//        FONT = font;
//    }
//
//    public static void setContext(Context context) {
//        APP_CONTEXT = context;
//    }
//
//    public static Context getContext() {
//        return APP_CONTEXT;
//    }
//
//    public static String getUrlHost() {
//
//        //如果需要调试切换版本,请直接修改debug中的ip等信息
//        switch (BuildConfig.BUILD_TYPE) {
//            case "debug"://测试服
//                AppConfig.DEBUG = true;
//                //---------------------------
//                AppConfig.SOCKET_IP = "yanlong.1616d.top";
//                AppConfig.URL_HOST = "https://" + AppConfig.SOCKET_IP + ":8080";
//                AppConfig.SOCKET_PORT = 19991;
//                AppConfig.UP_PATH = "test-environment";
//
////                AppConfig.SOCKET_IP = "im-app.zhixun6.com";
////                AppConfig.URL_HOST = "https://" + AppConfig.SOCKET_IP + ":8080";
////                AppConfig.SOCKET_PORT = 19991;
////                AppConfig.UP_PATH = "product-environment";
//                break;
//            case "pre": //预发布服  美国 usa-test.1616d.top    香港 hk-test.1616d.top
//                AppConfig.DEBUG = false;
//                //---------------------------
//                AppConfig.SOCKET_IP = "hk-test.1616d.top";
//                AppConfig.URL_HOST = "https://" + AppConfig.SOCKET_IP + ":8080";
//                AppConfig.SOCKET_PORT = 19991;
//                AppConfig.UP_PATH = "development";
//                break;
//            case "release"://正式服
//                AppConfig.DEBUG = false;
//                //---------------------------
//                AppConfig.SOCKET_IP = "im-app.zhixun6.com";
//                AppConfig.URL_HOST = "https://" + AppConfig.SOCKET_IP + ":8080";
//                AppConfig.SOCKET_PORT = 19991;
//                AppConfig.UP_PATH = "product-environment";
//                break;
//        }
//        return URL_HOST;
//    }
//
//    /***
//     * 指定host
//     *
//     * @param urlHost
//     */
//    public static void setUrlHost(String urlHost) {
//        URL_HOST = urlHost;
//        NetUtil.getNet().resetHost();
//    }
//
//    /***
//     * 获取系统语言zh0
//     * @return
//     */
//    public static String getLanguage() {
//        //系统语言
//        Locale locale;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            locale = LocaleList.getDefault().get(0);
//        } else {
//            locale = Locale.getDefault();
//        }
//        String language = locale.getLanguage();
//        return language;
//    }
//
//    /***
//     * 获取系统国家cn
//     * @return
//     */
//    public static String getCountry() {
//        //系统语言
//        Locale locale;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            locale = LocaleList.getDefault().get(0);
//        } else {
//            locale = Locale.getDefault();
//        }
//        String ct = locale.getCountry();
//        return ct;
//    }
//
//    public static boolean isChina() {
//        return AppConfig.getCountry().equals("CN");
//    }
//
//    /**
//     * 获取渠道名
//     *
//     * @return 如果没有获取成功，那么返回值为空
//     */
//    public static String getChannelName() {
//        if (APP_CONTEXT == null) {
//            return null;
//        }
//        String channelName = null;
//        try {
//            PackageManager packageManager = APP_CONTEXT.getPackageManager();
//            if (packageManager != null) {
//                //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
//                ApplicationInfo applicationInfo = packageManager.
//                        getApplicationInfo(APP_CONTEXT.getPackageName(), PackageManager.GET_META_DATA);
//                if (applicationInfo != null) {
//                    if (applicationInfo.metaData != null) {
//                        channelName = String.valueOf(applicationInfo.metaData.get("UMENG_CHANNEL"));
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return channelName;
//    }
//}
