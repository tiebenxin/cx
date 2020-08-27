package net.cb.cb.library;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;

import net.cb.cb.library.utils.SpUtil;

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
    // 阿里云上传回调
    public static final String UPLOAD_BACK_TEST = "https://e7.callback.zhixun6.com:58181/file-uploaded";
    public static final String UPLOAD_BACK_RELEASE = "https://im-app.zhixun6.com:19009/file-uploaded";
    // 帮助
    public static final String HELP_URL = "https://helper.zhixun6.com:8089/";
    private static boolean isOnline;
    private static String uploadParent;

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

    public static void setUpPath(String name) {
        uploadParent = name;
    }

    public static String getUpPath() {
        if (TextUtils.isEmpty(uploadParent)) {
            int type = SpUtil.getSpUtil().getSPValue("ipType", 0);
            if (type == 0) {
                switch (BuildConfig.BUILD_TYPE) {
                    case "debug":
                        uploadParent = BuildConfig.UPLOAD_DEV;
                        break;
                    case "pre":
                        uploadParent = BuildConfig.UPLOAD_PRE;
                        break;
                    case "release":
                        uploadParent = BuildConfig.UPLOAD_RELEASE;
                        break;
                    default:
                        uploadParent = BuildConfig.UP_PATH;
                        break;
                }
            } else {
                if (type == 1) {
                    uploadParent = BuildConfig.UPLOAD_DEV;
                } else if (type == 2) {
                    uploadParent = BuildConfig.UPLOAD_PRE;
                } else {
                    uploadParent = BuildConfig.UPLOAD_RELEASE;
                }
            }
        }
        return uploadParent;
    }

    public static String getUploadBack() {
        if ("release".equals(BuildConfig.BUILD_TYPE)) {
            return UPLOAD_BACK_RELEASE;
        } else {
            int type = SpUtil.getSpUtil().getSPValue("ipType", 0);
            if (type == 3) {
                return UPLOAD_BACK_RELEASE;
            } else {
                return UPLOAD_BACK_TEST;
            }
        }
    }

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
