//package com.example.nim_lib.net;
//
//
//import android.content.Context;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.util.Log;
//
//import net.cb.cb.library.AppConfig;
//
//import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//
///**
// * Created by Administrator on 2016/11/23.
// */
//
//public class NetUtil {
//    private static NetUtil net;
//    private static OkHttpClient httpClient;
//    private static Retrofit retrofit;
//
//    private static void init() {
//        net = new NetUtil();
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.addInterceptor(new NetIntrtceptor());
//        if (AppConfig.DEBUG) {
//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
//                    new HttpLoggingInterceptor.Logger() {
//                        @Override
//                        public void log(String message) {
//                            if (AppConfig.DEBUG) {
////                            Log.e("h===","收到响应1: " + message);
//                                if(message!=null){
//                                    if(message.contains("http")||message.contains("data")||message.contains("Data")
//                                            ||message.contains("=")||message.contains("{")){
//                                        Log.e("h===","收到响应2==="+message);
//                                    }
//                                }
//                            }
//                        }
//                    }
//            );
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//            builder.addInterceptor(logging);
//        } else {
//
//        }
//        //加证书
//        // builder.sslSocketFactory(Ssl.getCertificates(),Ssl.getTrustManager());
//
//
//        httpClient = builder.build();
//
//
//        retrofit = retrofit == null ? new Retrofit.Builder()
//                .baseUrl(AppConfig.getUrlHost())
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(httpClient)
//
//                .build() : retrofit;
//
//
//    }
//
//    /***
//     * 永久设定host
//     */
//    public static void resetHost() {
//        retrofit = new Retrofit.Builder()
//                .baseUrl(AppConfig.getUrlHost())
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(httpClient)
//
//                .build();
//
//    }
//
//    /***
//     * 1.创建,临时指定host
//     *
//     * @param host
//     * @param service
//     * @param <T>
//     * @return
//     */
//    public <T> T create(String host, Class<T> service) {
//        if (host != null) {
//            Retrofit newRT = new Retrofit.Builder()
//                    .baseUrl(host)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(httpClient)
//                    .build();
//
//            return newRT.create(service);
//        }
//
//        return retrofit.create(service);
//
//    }
//
//    /***
//     * 1. 创建,使用默认的host
//     *
//     * @param service
//     * @param <T>
//     * @return
//     */
//    public <T> T create(Class<T> service) {
//        return create(null, service);
//    }
//
//
//    /***
//     * 2.执行
//     *
//     * @param call
//     * @param callBack
//     * @param <T>
//     */
//    public <T> Call exec(Call<T> call, final Callback<T> callBack) {
//
//        Callback<T> cb = new CallBack<T>() {
//            @Override
//            public void onResponse(Call<T> call, Response<T> response) {
//                if (response == null) {
//                    callBack.onFailure(call, new Throwable());
//                    return;
//                }
//
//                callBack.onResponse(call, response);
//            }
//
//            @Override
//            public void onFailure(Call<T> call, Throwable t) {
//                callBack.onFailure(call, t);
//            }
//        };
//        call.enqueue(cb);
//        return call;
//    }
//
//    /***
//     * 执行并缓存
//     *
//     * @param cacheName
//     * @param call
//     * @param callBack
//     * @param <T>
//     */
//    public <T> void exec2Cache(String cacheName, Call<T> call, Callback<T> callBack) {
//        new NetCacheUtil().doCache(cacheName, call, callBack);
//    }
//
//
//    public static NetUtil getNet() {
//
//        if (net == null)
//            init();
//        return net;
//    }
//
//    private NetUtil() {
//    }
//
//    /***
//     * 网络连接检测
//     *
//     * @return
//     */
//    public static boolean isNetworkConnected() {
//        Context context = AppConfig.APP_CONTEXT;
//        ConnectivityManager cm = (ConnectivityManager) context
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = cm.getActiveNetworkInfo();
//        return info != null && info.isConnected();
//    }
//
//}
