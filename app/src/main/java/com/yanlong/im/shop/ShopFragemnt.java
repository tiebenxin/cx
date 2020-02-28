package com.yanlong.im.shop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yanlong.im.R;

import net.cb.cb.library.view.WebPageActivity;

/**
 * @author Liszt
 * @date 2020/2/26
 * Description
 */
public class ShopFragemnt extends Fragment {
    private String TAG = ShopFragemnt.class.getSimpleName();

    private WebView webView;

    public static ShopFragemnt newInstance() {
        ShopFragemnt fragment = new ShopFragemnt();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView = getView().findViewById(R.id.web_view);
        initContentWeb(webView, "http://sc.zhixun5588.com/");
    }


    private void initContentWeb(WebView webView, String url) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavascriptInterface(getActivity()), "imagelistner");
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

            }
        });
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                return true;
            }
        });
        Log.i(TAG, "装载网页>>>>:" + url);
        webView.loadUrl(url);
    }

    public class JavascriptInterface {
        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            turnToPhotoScan(img);
        }

        /**
         * 跳转到图片查看器
         *
         * @param url
         */
        private void turnToPhotoScan(String url) {
 /*           ArrayList<String> urlPath = new ArrayList<>();
            urlPath.add(url);
            Intent it = new Intent(WebViewPageActivity.this, PhotoScanActivity.class);
            it.putStringArrayListExtra(PhotoScanActivity.KEY_ALL_PICS_PATH, urlPath);
            it.putExtra(PhotoScanActivity.KEY_SHOW_POSITION, 0);
            startActivity(it);*/
        }
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
}
