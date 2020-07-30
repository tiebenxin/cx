package com.hm.cxpay.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hm.cxpay.eventbus.PayResultEvent;
import com.hm.cxpay.global.PayEnum;

import net.cb.cb.library.R;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/***
 * 易宝支付web页面
 * @author Liszt
 * @date 2020年6月21日
 */
public class YiBaoWebActivity extends AppActivity {
    private final String TAG = getClass().getSimpleName();
    /**
     * 参数:打开的url
     */
    public static final String AGM_URL = "url";
    /***
     * 参数:界面标题
     */
    public static final String AGM_TITLE = "title";
    private ActionbarView actionbar;
    private HeadView headView;
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webpage);
        EventBus.getDefault().register(this);
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        initEvent();
        initData();
    }

    private void initEvent() {
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                setResult(RESULT_CANCELED);
                finish();
            }

            @Override
            public void onRight() {

            }
        });
    }

    private void initData() {
        Intent it = getIntent();
        if (it != null) {
      /* 	String title = it.getStringExtra(AGM_TITLE);
       	title=title==null?"消息详情":title;
            actionbar.setTitle(title);*/

            String url = it.getStringExtra(AGM_URL);//+"/"+language;
            if (url != null)
                initContentWeb(webView, url);
        }

    }

    private void initContentWeb(WebView webView, String url) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavascriptInterface(this), "imagelistner");
        webView.addJavascriptInterface(new JavascriptInterface(this), "JsToAndroid");//name需要和JS一致
        webView.addJavascriptInterface(new JavascriptInterface(this), "JsGetValue");//name需要和JS一致
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        webView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                LogUtil.getLog().i(TAG, error.toString());
                handler.proceed();// 接受所有网站的证书
            }
        });
        Log.i(TAG, "装载网页>>>>:" + url);
        webView.loadUrl(url);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventPayResult(PayResultEvent event) {
//        if (event.getResult() != 1) {//支付失败不返回
        YiBaoWebActivity.this.finish();
//        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // return super.shouldOverrideUrlLoading(view, url);
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                startActivity(intent);
                return true;
            }
            view.loadUrl(url);
            return true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageFinished(view, url);
            String title = view.getTitle();
            actionbar.setTitle(title);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

        }
    }

    public class JavascriptInterface {
        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }


        @android.webkit.JavascriptInterface
        public void callAndroidMethod(int result) {
            doPayResult(result);
        }

        /**
         * 处理支付结果
         */
        private void doPayResult(int result) {
            Intent intent = new Intent();
            intent.putExtra("result", result);
            setResult(RESULT_OK, intent);
            YiBaoWebActivity.this.finish();
        }
    }
}
