package com.yanlong.im.shop;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hm.cxpay.bean.CommonBean;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.dailog.CommonSelectDialog;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.ui.ServiceAgreementActivity;

import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.ToastUtil;

import static android.app.Activity.RESULT_OK;

/**
 * @author Liszt
 * @date 2020/2/26
 * Description
 */
public class ShopFragemnt extends Fragment {

    private WebView webView;
    private Activity activity;
    private CommonSelectDialog.Builder builder;
    private CommonSelectDialog dialogOne;//通用提示选择弹框：实名认证

    private String url = "";//商城地址
    private String payMoney = "";//需要支付的钱
    private String payStatus = "1";// 1 无操作  0 关闭密码框/用户支付失败  含http，即为成功，返回url
    private String authAll = "1";// 来自商城的认证流程： 1 需要完成全部三层认证

    private String YB_COOKIE_URL = "yeepay.com";//清掉易宝的cookie
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

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
        builder = new CommonSelectDialog.Builder(activity);
        initContentWeb(webView);
    }

    private void initContentWeb(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// 设置允许JS弹窗
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setTextZoom(100);//适配某些手机网页显示不全
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.addJavascriptInterface(new JavascriptInterface(), "JsToAndroid");//name需要和JS一致
        webView.addJavascriptInterface(new JavascriptInterface(), "JsGetValue");//name需要和JS一致
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }


            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }
        });
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                return true;
            }
        });
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
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
//            cookieManager.removeSessionCookies(null);//移除
            cookieManager.setCookie(getDomain(YB_COOKIE_URL), "");//指定要修改的cookies
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.flush();
            } else {
                CookieSyncManager.createInstance(activity.getApplicationContext());
                CookieSyncManager.getInstance().sync();
            }
            WebStorage.getInstance().deleteAllData(); //清空WebView的localStorage
            if (!TextUtils.isEmpty(money)) {
                payMoney = money;
                payStatus = "1";
                //支付条件：实名认证+绑定手机+设置支付密码
                if (PayEnvironment.getInstance().getUser() != null) {
                    checkUserStatus(PayEnvironment.getInstance().getUser());
                } else {
                    httpGetUserInfo();
                }
            } else {
                ToastUtil.show("支付金额不能为空");
            }
//            ToastUtil.show("JS调起android成功!"+" 支付金额为"+money);
        }

        @android.webkit.JavascriptInterface
        public String getReturnValue() {
//            LogUtil.getLog().i("TAGG", "查询了1次!  " + payStatus);
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
                            webView.loadUrl(url);
//                            LogUtil.getLog().i("QQ", "重新加载了一次新的url");
                        } else {
//                            ToastUtil.show("商城url地址为空，请联系客服！");
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        super.onHandleError(baseResponse);
//                        ToastUtil.show("商城url地址出错，请联系客服！");
                    }
                });
    }
    /**
     * 请求->商城消费
     */
    private void httpConsumption() {
        PayHttpUtils.getInstance().shopConsumption(payMoney)
                .compose(RxSchedulers.<BaseResponse<CommonBean>>compose())
                .compose(RxSchedulers.<BaseResponse<CommonBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<CommonBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<CommonBean> baseResponse) {
//                        ToastUtil.show("支付成功！");
                        if (baseResponse.getData() != null) {
                            CommonBean bean = baseResponse.getData();
                            if (!TextUtils.isEmpty(bean.getUrl())) {
                                payStatus = bean.getUrl();
                            }
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse<CommonBean> baseResponse) {
                        super.onHandleError(baseResponse);
                        if (baseResponse.getCode() == (-21000)) {
                            ToastUtil.show(activity, "支付密码错误！");
                        } else {
                            ToastUtil.show(activity, baseResponse.getMessage());
                        }
                        //传失败状态给JS
                        payStatus = "0";
                    }
                });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (PayEnvironment.getInstance().getUserId() <= 0) {
                IUser info = UserAction.getMyInfo();
                if (info != null && info.getUid() != null) {
                    PayEnvironment.getInstance().setUserId(info.getUid().longValue());
                }
            }
            TokenBean token = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).get4Json(TokenBean.class);
            if (token == null || TextUtils.isEmpty(token.getBankReqSignKey())) {
                return;
            }
            httpGetUrl();
            payStatus = "1";
        }
    }

    /**
     * 判断：是否实名认证
     */
    private void checkUserStatus(UserBean userBean) {
        //1 已实名认证
        if (userBean.getRealNameStat() == 1) {
            //1-1 直接支付消费
            httpConsumption();
        } else {
            //2 未实名认证
            showIdentifyDialog();
        }
    }

    /**
     * 请求->获取用户信息
     */
    private void httpGetUserInfo() {
        IUser info = UserAction.getMyInfo();
        if (info == null) {
            return;
        }
        PayHttpUtils.getInstance().getUserInfo(info.getUid())
                .compose(RxSchedulers.<BaseResponse<UserBean>>compose())
                .compose(RxSchedulers.<BaseResponse<UserBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<UserBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<UserBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            UserBean userBean = null;
                            if (baseResponse.getData() != null) {
                                userBean = baseResponse.getData();
                            } else {
                                userBean = new UserBean();
                            }
                            PayEnvironment.getInstance().setUser(userBean);
                            checkUserStatus(userBean);
                        } else {
                            ToastUtil.show(activity, baseResponse.getMessage());
                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse<UserBean> baseResponse) {
                        ToastUtil.show(activity, baseResponse.getMessage());
                    }
                });
    }

    /**
     * 实名认证提示弹框
     */
    private void showIdentifyDialog() {
        dialogOne = builder.setTitle("根据国家法律法规要求，你需要进行身份认证后，才能继续使用该功能。")
                .setLeftText("取消")
                .setRightText("去认证")
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消
                        dialogOne.dismiss();
                        payStatus = "0";
                    }
                })
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //从商城跳转到认证，需要额外增加一个步骤，即设置支付密码
                        startActivity(new Intent(activity, ServiceAgreementActivity.class).putExtra("from_shop", authAll));
                        dialogOne.dismiss();
                        payStatus = "0";
                    }
                })
                .build();
        dialogOne.show();
    }

    /**
     * 获取URL的域名
     */
    private String getDomain(String url){
        url = url.replace("http://", "").replace("https://", "");
        if (url.contains("/")) {
            url = url.substring(0, url.indexOf('/'));
        }
        return url;
    }


    // 2.回调方法触发本地选择文件
    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");//图片上传
//        i.setType("file/*");//文件上传
//        i.setType("*/*");//文件上传
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    // 3.选择图片后处理
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            // Uri result = (((data == null) || (resultCode != RESULT_OK)) ? null : data.getData());
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        } else {
            //这里uploadMessage跟uploadMessageAboveL在不同系统版本下分别持有了
            //WebView对象，在用户取消文件选择器的情况下，需给onReceiveValue传null返回值
            //否则WebView在未收到返回值的情况下，无法进行任何操作，文件选择器会失效
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            } else if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(null);
                uploadMessageAboveL = null;
            }
        }
    }

    // 4. 选择内容回调到Html页面
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

}