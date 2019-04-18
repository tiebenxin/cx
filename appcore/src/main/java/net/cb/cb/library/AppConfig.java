package net.cb.cb.library;

import android.content.Context;
import android.os.Build;
import android.os.LocaleList;

import net.cb.cb.library.utils.NetUtil;

import java.util.Locale;


/***
 * 公共配置
 */
public class AppConfig {
    public static String URL_HOST="http://127.0.0.1";
    public static String SOCKET_IP="127.0.0.1";
    public static int SOCKET_PORT=8090;
    public static Context APP_CONTEXT;
    public static boolean DEBUG=false;
    public static float FONT =1.0f;

    //设置全局字体
    public static void setFont(float font) {
        FONT = font;
    }

    public static void setContext(Context context) {
        APP_CONTEXT = context;
    }

    public static String getUrlHost() {
        return URL_HOST;
    }

    /***
     * 指定host
     *
     * @param urlHost
     */
    public static void setUrlHost(String urlHost) {
        URL_HOST = urlHost;
        NetUtil.getNet().resetHost();
    }

    /***
     * 获取系统语言zh0
+-+.0     * @return
     */
    public static String getLanguage(){
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
    public static String getCountry(){
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

    public static boolean isChina(){
        return AppConfig.getCountry().equals("CN");
    }

}
