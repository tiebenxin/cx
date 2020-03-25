package com.yanlong.im.shop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.hm.cxpay.bean.CommonBean;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.widget.PswView;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.WebPageActivity;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * @author Liszt
 * @date 2020/2/26
 * Description
 */
public class ShopFragemnt extends Fragment {

    private WebView webView;
    private Activity activity;
    private AlertDialog checkPaywordDialog;

    private String url = "";//商城地址
    private String payMoney = "";//需要支付的钱
    private String payStatus = "1";// 1 无操作  0 关闭密码框/用户支付失败  含http，即为成功，返回url

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
        activity = getActivity();
        httpGetUrl();
    }

    private void initContentWeb(WebView webView, String url) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// 设置允许JS弹窗
        webView.addJavascriptInterface(new JavascriptInterface(), "JsToAndroid");//name需要和JS一致
        webView.addJavascriptInterface(new JavascriptInterface(), "JsGetValue");//name需要和JS一致
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
        webView.loadUrl(url);
        //返回键支持网页内回退
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                        webView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public class JavascriptInterface {

        @android.webkit.JavascriptInterface
        public void callAndroidMethod(String money) {
            if (!TextUtils.isEmpty(money)) {
                payMoney = money;
                showCheckPaywordDialog();
            } else {
                ToastUtil.show("支付金额不能为空");
            }
//            ToastUtil.show("JS调起android成功!"+" 支付金额为"+money);
        }

        @android.webkit.JavascriptInterface
        public String getReturnValue(){
            LogUtil.getLog().i("TAGG","查询了1次!  "+payStatus);
            return payStatus;
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

    /**
     * 获取免登陆商城URL
     */
    private void httpGetUrl() {
        PayHttpUtils.getInstance().getShopUrl()
                .compose(RxSchedulers.<BaseResponse>compose())
                .compose(RxSchedulers.<BaseResponse>handleResult())
                .subscribe(new FGObserver<BaseResponse>() {
                    @Override
                    public void onHandleSuccess(BaseResponse baseResponse) {
                        if (!TextUtils.isEmpty(baseResponse.getData().toString())) {
                            url = baseResponse.getData().toString();
                            initContentWeb(webView, url);
                        } else {
                            ToastUtil.show("商城url地址为空，请联系客服！");
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        super.onHandleError(baseResponse);
                        ToastUtil.show("商城url地址出错，请联系客服！");
                    }
                });
    }

    /**
     * 提示弹框->校验支付密码(特殊样式，暂不复用)
     */
    private void showCheckPaywordDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setCancelable(false);
        checkPaywordDialog = dialogBuilder.create();
        //获取界面
        View dialogView = LayoutInflater.from(activity).inflate(com.hm.cxpay.R.layout.dialog_check_payword, null);
        //初始化控件
        ImageView ivClose = dialogView.findViewById(com.hm.cxpay.R.id.iv_close);
        TextView tvTitle = dialogView.findViewById(com.hm.cxpay.R.id.temp_text_two);
        final PswView pswView = dialogView.findViewById(com.hm.cxpay.R.id.psw_view);
        //显示和点击事件
        tvTitle.setVisibility(View.GONE);
        //关闭弹框
        ivClose.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPaywordDialog.dismiss();
                payStatus = "0";
            }
        });
        //输入支付密码
        pswView.setOnPasswordChangedListener(new PswView.onPasswordChangedListener() {
            @Override
            public void setPasswordChanged(String payword) {
                httpCheckPayword(payword, pswView);
            }
        });
        //展示界面
        checkPaywordDialog.show();
        //强制唤起软键盘
        showSoftKeyword(pswView);
        //解决dialog里edittext不响应键盘的问题
        checkPaywordDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //解决圆角shape背景无效问题
        Window window = checkPaywordDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //相关配置
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        WindowManager manager = window.getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        //设置宽高，高度自适应，宽度屏幕0.8
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.width = (int) (metrics.widthPixels * 0.8);
        checkPaywordDialog.getWindow().setAttributes(lp);
        checkPaywordDialog.setContentView(dialogView);
    }

    /**
     * 新增->强制弹出软键盘
     *
     * @param view 备注：延迟任务解决之前无法弹出问题
     */
    public void showSoftKeyword(final View view) {
        if (view == null) {
            return;
        }
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.requestFocus();
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(view, 0);
                }
            }
        }, 100);

    }

    /**
     * 发请求->商城检查支付密码（是否正确）
     */
    private void httpCheckPayword(final String payword, final PswView pswView) {
        PayHttpUtils.getInstance().checkShopPayword(payword, payMoney)
                .compose(RxSchedulers.<BaseResponse<CommonBean>>compose())
                .compose(RxSchedulers.<BaseResponse<CommonBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<CommonBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<CommonBean> baseResponse) {
                        ToastUtil.show("支付成功！");
                        if(checkPaywordDialog.isShowing()){
                            checkPaywordDialog.dismiss();
                        }
                        if(baseResponse.getData()!=null){
                            CommonBean bean = baseResponse.getData();
                            if(!TextUtils.isEmpty(bean.getUrl())){
                                payStatus = bean.getUrl();
                            }
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse<CommonBean> baseResponse) {
                        super.onHandleError(baseResponse);
                        if (baseResponse.getCode() == (-21000)) {
                            ToastUtil.show(activity, "支付密码错误！");
                        }else {
                            ToastUtil.show(activity, baseResponse.getMessage());
                        }
                        //传失败状态给JS
                        payStatus = "0";
                        pswView.clear();
                    }
                });
    }
}
