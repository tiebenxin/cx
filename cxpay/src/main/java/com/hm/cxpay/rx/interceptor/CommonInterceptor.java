package com.hm.cxpay.rx.interceptor;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;


import com.hm.cxpay.global.PayEnvironment;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.manager.TokenManager;
import net.cb.cb.library.utils.LogUtil;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 所有请求的公共参数
 * <p>
 * Created by Liszt on 2018/4/11
 */

public class CommonInterceptor implements Interceptor {
    public CommonInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //wifi开启代理的情况下，取消请求
        if (isWifiProxy(AppConfig.getContext())) {
            LogUtil.getLog().e("CommonInterceptor--网络代理", "token==" + PayEnvironment.getInstance().getToken());
            LogUtil.writeLog("CommonInterceptor--网络代理--token=" + PayEnvironment.getInstance().getToken() + "--time=" + System.currentTimeMillis());
            chain.call().cancel();
        }
        Request oldRequest = chain.request();
        /**
         * 公共参数
         */
        HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()
                .newBuilder()
                .scheme(oldRequest.url().scheme())
                .host(oldRequest.url().host());

        /**
         * 新的请求
         */
        Request.Builder requestBuilder = oldRequest.newBuilder()
                .method(oldRequest.method(), oldRequest.body())
                .url(authorizedUrlBuilder.build());
        requestBuilder.header("Content-Type", "application/json;charset=utf-8");


        /**
         * 已经登陆的用户 带上X-Access-Token
         */
        if (!TextUtils.isEmpty(PayEnvironment.getInstance().getToken())) {
            requestBuilder.header(TokenManager.TOKEN_KEY, PayEnvironment.getInstance().getToken());
        }
        return chain.proceed(requestBuilder.build());
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
