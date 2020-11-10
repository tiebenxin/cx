package com.yanlong.im.view;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.nim_lib.ui.BaseBindActivity;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityWebBinding;
import com.yanlong.im.user.ui.FeedbackActivity;
import com.yanlong.im.user.ui.HelpActivity;
import com.yanlong.im.user.ui.UploadLogActivity;

import net.cb.cb.library.utils.IntentUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-08-27
 * @updateAuthor
 * @updateDate
 * @description Web h5展示
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class WebActivity extends BaseBindActivity<ActivityWebBinding> {
    private static final String TAG = "WebPageActivity";
    // 参数:打开的url
    public static final String AGM_URL = "url";
    // 参数:界面标题
    public static final String AGM_TITLE = "title";

    @Override
    protected int setView() {
        return R.layout.activity_web;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        // TODO 解决#291424 java.lang.RuntimeException
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(this);
            if (!"com.yanlong.im".equals(processName)) {// 判断不等于默认进程名称
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }

    @Override
    protected void initEvent() {
        bindingView.headView.getActionbar().getBtnRight().setVisibility(View.VISIBLE);
        bindingView.headView.getActionbar().getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        bindingView.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                startActivity(new Intent(WebActivity.this, UploadLogActivity.class));
            }
        });
    }

    @Override
    protected void loadData() {
        String title = getIntent().getStringExtra(AGM_TITLE);
        String url = getIntent().getStringExtra(AGM_URL);
        if (!TextUtils.isEmpty(title)) {
            bindingView.headView.getActionbar().setTitle(title);
        }
        initContentWeb();
        if (!TextUtils.isEmpty(url)) {
            bindingView.webView.loadUrl(url);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && bindingView.webView.canGoBack()) {
            bindingView.webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initContentWeb() {
        bindingView.webView.getSettings().setJavaScriptEnabled(true);
        bindingView.webView.addJavascriptInterface(new JavascriptInterface(), "JsToAndroid");//name需要和JS一致
        bindingView.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                bindingView.progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    bindingView.progressBar.setVisibility(View.GONE);
                }
            }
        });
        bindingView.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                LogUtil.getLog().i(TAG, error.toString());
                handler.proceed();// 接受所有网站的证书
            }
        });
    }

    public String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }

    public class JavascriptInterface {

        @android.webkit.JavascriptInterface
        public void callAndroidMethod() {
            IntentUtil.gotoActivity(WebActivity.this, FeedbackActivity.class);
        }
    }
}