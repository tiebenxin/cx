package net.cb.cb.library.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.bean.ProxyException;
import net.cb.cb.library.constant.AppHostUtil;
import net.cb.cb.library.net.IRequestListener;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;


/**
 * Created by Administrator on 2016/11/23.
 */

public class NetUtil {
    private static NetUtil net;
    private static OkHttpClient httpClient;
    private static Retrofit retrofit;

    private static void init() {
        net = new NetUtil();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(12, TimeUnit.SECONDS);//设置连接超时时间
        builder.readTimeout(6, TimeUnit.SECONDS);//设置读取超时时间
        builder.writeTimeout(6, TimeUnit.SECONDS);//设置写入超时时间
        builder.addInterceptor(new NetIntrtceptor());
        if (AppConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
                    new HttpLoggingInterceptor.Logger() {
                        @Override
                        public void log(String message) {
                            if (AppConfig.DEBUG) {
//                            Log.e("h===","收到响应1: " + message);
                                if (message != null) {
                                    if (message.contains("http") || message.contains("data") || message.contains("Data")
                                            || message.contains("=") || message.contains("{")) {
                                        Log.e("h===", "收到响应2===" + message);
                                    }
                                }
                            }
                        }
                    }
            );
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        } else {

        }
        //加证书
        // builder.sslSocketFactory(Ssl.getCertificates(),Ssl.getTrustManager());
        builder.sslSocketFactory(createSSLSocketFactory());

        httpClient = builder.build();
        LogUtil.getLog().i("NetUtil", "--init-" + AppHostUtil.getHttpHost());
        retrofit = retrofit == null ? new Retrofit.Builder()
//                .baseUrl(AppConfig.getUrlHost())
                .baseUrl(AppHostUtil.getHttpHost())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build() : retrofit;


    }

    /***
     * 永久设定host
     */
    public static void resetHost() {
        if (httpClient != null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(AppHostUtil.getHttpHost())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient)
                    .build();
        } else {
            init();
        }
    }

    /***
     * 1.创建,临时指定host
     *
     * @param host
     * @param service
     * @param <T>
     * @return
     */
    public <T> T create(String host, Class<T> service) {
        if (host != null && httpClient != null) {
            Retrofit newRT = new Retrofit.Builder()
                    .baseUrl(host)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient)
                    .build();

            return newRT.create(service);
        }
        if (retrofit == null) {
            init();
        }
        return retrofit.create(service);
    }

    /***
     * 1. 创建,使用默认的host
     *
     * @param service
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service) {
        return create(null, service);
    }

    /***
     * 同步请求
     *
     * @param call
     * @param <T>
     */
    public <T> Response<T> execute(Call<T> call) {
        //开启了代理，直接返回失败
        if (isWifiProxy(AppConfig.getContext())) {
            LogUtil.getLog().e("NetUtil", "网络代理异常，请求失败");
            try {
                LogUtil.writeLog("NetUtil" + "--网络代理--token=" + NetIntrtceptor.headers.get("X-Access-Token") + "--time=" + System.currentTimeMillis());
            } catch (Exception e) {
            }
            return null;
        } else {
            try {
                return call.execute();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /***
     * 2.执行
     *
     * @param call
     * @param callBack
     * @param <T>
     */
    public <T> Call exec(Call<T> call, final Callback<T> callBack) {
        //开启了代理，直接返回失败
        if (isWifiProxy(AppConfig.getContext())) {
            LogUtil.getLog().e("NetUtil", "网络代理异常，请求失败");
            try {
                LogUtil.writeLog("NetUtil" + "--网络代理--token=" + NetIntrtceptor.headers.get("X-Access-Token") + "--time=" + System.currentTimeMillis());
            } catch (Exception e) {
            }
            callBack.onFailure(call, new ProxyException("网络代理异常，请求失败"));
            return call;
        }
        Callback<T> cb = new CallBack<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response == null) {
                    callBack.onFailure(call, new Throwable());
                    return;
                }

                callBack.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callBack.onFailure(call, t);
            }
        };
        call.enqueue(cb);
        return call;
    }

    /***
     * 执行并缓存
     *
     * @param cacheName
     * @param call
     * @param callBack
     * @param <T>
     */
    public <T> void exec2Cache(String cacheName, Call<T> call, Callback<T> callBack) {
        new NetCacheUtil().doCache(cacheName, call, callBack);
    }


    public static NetUtil getNet() {
        if (net == null)
            init();
        return net;
    }

    private NetUtil() {
    }

    /***
     * 网络连接检测
     *
     * @return
     */
    public static boolean isNetworkConnected() {
        Context context = AppConfig.APP_CONTEXT;
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            return info != null && info.isConnected();
        } else {
            return false;
        }
    }

    /***
     * 网络连接检测
     *
     * @return
     */
    public static boolean isNetworkAvailable() {
        Context context = AppConfig.APP_CONTEXT;
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            LogUtil.getLog().i("Liszt_test", "--连接LOG--网路状态--isConnected=" + (info != null ? info.isConnected() : null) + "--isAvailable=" + (info != null ? info.isAvailable() : null));
            LogUtil.writeLog("Liszt_test" + "--连接LOG--网路状态--isConnected=" + (info != null ? info.isConnected() : null) + "--isAvailable=" + (info != null ? info.isAvailable() : null));
            return info != null && info.isAvailable();
        } else {
            LogUtil.getLog().i("Liszt_test", "--连接LOG--网路状态false,context=null");
            LogUtil.writeLog("Liszt_test" + "--连接LOG--网路状态false,context=null");
            return false;
        }
    }

    /**
     * 判断网络连接类型
     *
     * @param context
     * @return
     */
    public static String getNetworkType(Context context) {
        String netWorkState = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                //网络状态为wifi
                netWorkState = "WIFI";
                return netWorkState;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                //网络状态为手机
                //判断手机网络类型是2g , 3g, 以及4g
                int type = telephonyManager.getNetworkType();
                switch (type) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        netWorkState = "2G";
                        return netWorkState;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        netWorkState = "3G";
                        return netWorkState;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        netWorkState = "4G";
                        return netWorkState;
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    default:
                        netWorkState = "unknown network type";
                        return netWorkState;
                }

            }
        }
        return "";
    }

    //是否开启了网络代理
    private boolean isWifiProxy(Context context) {
        final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyAddress;
        int proxyPort;
        if (IS_ICS_OR_LATER) {
            proxyAddress = System.getProperty("http.proxyHost");
            String portStr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        } else {
            proxyAddress = android.net.Proxy.getHost(context);
            proxyPort = android.net.Proxy.getPort(context);
        }
        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
    }


    //获取网络IP
    public void requestIP(final IRequestListener listener) {
        String url = "http://im-app.zhixun6.com:58888/getIp";
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(url).build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    String result = response.body().string();
                    listener.onSuccess(result);
                }
            }
        });

    }

    /**
     * 生成安全套接字工厂，用于https请求的证书跳过
     */
    public static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return ssfFactory;
    }

    /**
     * 用于信任所有证书
     */
    static class TrustAllCerts implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
                throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public UpFileServer getUpFileServer() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(12, TimeUnit.SECONDS);//设置连接超时时间
        builder.readTimeout(12, TimeUnit.SECONDS);//设置读取超时时间;
        builder.writeTimeout(12, TimeUnit.SECONDS);//设置写入超时时间;
        builder.addInterceptor(new NetIntrtceptor());
        if (AppConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
                    new HttpLoggingInterceptor.Logger() {
                        @Override
                        public void log(String message) {
                            if (AppConfig.DEBUG) {
//                            Log.e("h===","收到响应1: " + message);
                                if (message != null) {
                                    if (message.contains("http") || message.contains("data") || message.contains("Data")
                                            || message.contains("=") || message.contains("{")) {
                                        Log.e("h===", "收到响应2===" + message);
                                    }
                                }
                            }
                        }
                    }
            );
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        } else {

        }
        builder.sslSocketFactory(createSSLSocketFactory());

        OkHttpClient httpClient = builder.build();
        retrofit = new Retrofit.Builder()
                .baseUrl(AppHostUtil.getHttpHost())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build();

        // 初始化Retrofit
        return retrofit.create(UpFileServer.class);
    }

}
