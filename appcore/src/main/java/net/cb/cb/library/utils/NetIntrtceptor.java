package net.cb.cb.library.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.MainApplication;
import net.cb.cb.library.bean.BuglyException;
import net.cb.cb.library.bean.EventLoginOut;
import net.cb.cb.library.constant.BuglyTag;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/***
 * @author jyj
 * @date 2016/11/29
 */
public class NetIntrtceptor implements Interceptor {
    private static final String TAG = "NetIntrtceptor";
    private static MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
    public static Headers headers = Headers.of();

    // Bugly登录异常标签
    private final int BUGLY_TAG_LOGIN = 139070;

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (AppConfig.DEBUG) {
            LogUtil.getLog().i(TAG, "<<进入拦截器");
        }
        //wifi开启代理的情况下，取消请求
//        if (isWifiProxy(AppConfig.getContext())) {
//            try {
//                LogUtil.getLog().e(TAG + "--网络代理", "token==" + headers.get("X-Access-Token"));
//                LogUtil.writeLog(TAG + "--网络代理--token=" + headers.get("X-Access-Token") + "--time=" + System.currentTimeMillis());
//            } catch (Exception e) {
//            }
//            chain.call().cancel();
//        }
//        Headers.Builder builder = new Headers.Builder();
//        builder.add("X-Access-Token", headers.get("X-Access-Token"))
//                .add("cli-platform", "Android")
//                .add("cli-version", AppConfig.getVersionName());
        Request request = chain.request().newBuilder()
                .headers(headers)
                .addHeader("cli-platform", "Android")
                .addHeader("cli-version", AppConfig.getVersionName())
                .build();
        request = interceptor4Front(chain, request);
        Response resp = chain.proceed(request);
        resp = interceptor4After(resp);
        return resp;
    }

    //前拦截
    private Request interceptor4Front(Chain chain, Request request) {
        String url = request.url().encodedPath();
        //post自动追加platform 参数
        RequestBody reqbody = request.body();
        if (request.method().equals("POST")) {
            String json = "";
            if (reqbody instanceof FormBody) {
                Gson gson = new GsonBuilder().create();
                FormBody gb = (FormBody) reqbody;

                Map<String, Object> objs = new HashMap<>();

                for (int i = 0; gb != null && i < gb.size(); i++) {
                    //放在一个map里面,然后转json

                    if (gb.name(i).startsWith("@")) {//直接存对象
                        objs.put(gb.name(i).substring(1), gson.fromJson(gb.value(i), Object.class));

                    } else {
                        objs.put(gb.name(i), gb.value(i));
                    }


                }

                json = gson.toJson(objs);

                RequestBody nbody = RequestBody.create(mediaType, json);
                request = request.newBuilder()
                        .method(request.method(), nbody)
                        .build();
            }


        }

        return request;

    }

    //后拦截
    private Response interceptor4After(Response resp) {

        switch (resp.code()) {
            case 200:
                //resp.body().string()

                break;
            case 401:
                LogUtil.getLog().e(TAG, "<<拦截器:401 url:" + resp.request().url().url().toString());
                // 上报后的Crash会显示该标签
                CrashReport.setUserSceneTag(MainApplication.getInstance().getApplicationContext(), BUGLY_TAG_LOGIN);
                // 上传异常数据,BuglyLog最多20字节
                BuglyLog.i(BuglyTag.BUGLY_TAG_3, "401：" + resp.message());
                CrashReport.postCatchedException(new BuglyException());
                EventBus.getDefault().post(new EventLoginOut());
                if (headers != null) {
                    LogUtil.writeLog("网络请求401" + "--token=" + headers.get("X-Access-Token") + "--time=" + System.currentTimeMillis());
                }
                break;
            case 403:
                LogUtil.getLog().e(TAG, "<<拦截器:403 url:" + resp.request().url().url().toString());
                // 上报后的Crash会显示该标签
                CrashReport.setUserSceneTag(MainApplication.getInstance().getApplicationContext(), BUGLY_TAG_LOGIN);
                // 上传异常数据
                BuglyLog.e(BuglyTag.BUGLY_TAG_3, "403：" + resp.message() /*+ " " + resp.body()*/);
                CrashReport.postCatchedException(new BuglyException());
                EventBus.getDefault().post(new EventLoginOut());
                if (headers != null) {
                    LogUtil.writeLog("网络请求403" + "--token=" + headers.get("X-Access-Token") + "--time=" + System.currentTimeMillis());
                }
                break;
            case 404:
                LogUtil.getLog().e(TAG, "<<拦截器:404 url:" + resp.request().url().url().toString());
                break;
            case 500:
                LogUtil.getLog().e(TAG, "<<拦截器:500 url:" + resp.request().url().url().toString());
                break;
        }

        return resp;
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

}
