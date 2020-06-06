package net.cb.cb.library.constant;

import android.text.TextUtils;

import net.cb.cb.library.BuildConfig;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.SpUtil;

/**
 * @author Liszt
 * @date 2020/3/26
 * Description
 */
public class AppHostUtil {
    private static final int HTTP_PORT = 8080;
    public static final int TCP_PORT = 19991;
    private static final String HTTPS = "https://";

    private static String connectHostApi;

    private final static String getConnectHostApi() {
        if (isEmpty()) {
            int type = SpUtil.getSpUtil().getSPValue("ipType", 0);
            if (type == 0) {
                switch (BuildConfig.BUILD_TYPE) {
                    case "debug":
                        connectHostApi = BuildConfig.HOST_DEV;
                        break;
                    case "pre":
                        connectHostApi = BuildConfig.HOST_PRE;
                        break;
                    case "release":
                        connectHostApi = BuildConfig.HOST_RELEASE;
                        break;
                    default:
                        connectHostApi = BuildConfig.API_HOST;
                        break;
                }
//                connectHostApi = BuildConfig.HOST_RELEASE;
//                connectHostApi = "e7.inner.zhixun6.com";//李佳本地联调地址
                connectHostApi = BuildConfig.HOST_DEV;
            } else {
                if (type == 1) {
                    connectHostApi = BuildConfig.HOST_DEV;
                } else if (type == 2) {
                    connectHostApi = BuildConfig.HOST_PRE;
                } else {
                    connectHostApi = BuildConfig.HOST_RELEASE;
                }
            }
        }
        if (isEmpty()) {
            throw new NullPointerException("请检查config.gradle#host配置");
        }
        LogUtil.getLog().i("AppHostUtil", "主机地址：" + connectHostApi);
        return connectHostApi;
    }

    //切换服务器
    public static void setHostUrl(String url) {
        connectHostApi = url;
        NetUtil.getNet().resetHost();
    }

    public final static String getHttpHost() {
        if (isEmpty()) {
            getConnectHostApi();
        }
        return HTTPS + connectHostApi + ":" + HTTP_PORT;
    }

    public final static String getTcpHost() {
        if (isEmpty()) {
            getConnectHostApi();
        }
        return connectHostApi;
    }

    private static boolean isEmpty() {
        return TextUtils.isEmpty(connectHostApi) || connectHostApi.equals("null");
    }

}
