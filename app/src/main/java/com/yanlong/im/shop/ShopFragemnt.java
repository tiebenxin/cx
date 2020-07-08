package com.yanlong.im.shop;

import android.app.Activity;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.hm.cxpay.bean.CommonBean;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.dailog.CommonSelectDialog;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.BindPhoneNumActivity;
import com.hm.cxpay.ui.payword.SetPaywordActivity;
import com.hm.cxpay.widget.PswView;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.ui.ServiceAgreementActivity;

import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.ToastUtil;

import static android.content.Context.INPUT_METHOD_SERVICE;

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
    private CommonSelectDialog dialogTwo;//通用提示选择弹框：是否绑定手机号

    private String url = "";//商城地址
    private String payMoney = "";//需要支付的钱
    private String payStatus = "1";// 1 无操作  0 关闭密码框/用户支付失败  含http，即为成功，返回url
    private String authAll = "1";// 来自商城的认证流程： 1 需要完成全部三层认证

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
                        ToastUtil.show("支付成功！");
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
        }
    }

    /**
     * 三层判断：是否实名认证->是否绑定手机号->是否设置支付密码
     */
    private void checkUserStatus(UserBean userBean) {
        //1 已实名认证
        if (userBean.getRealNameStat() == 1) {
            //2 已完成绑定手机号
            /*if (userBean.getPhoneBindStat() == 1) {
                //3 已设置支付密码
                if (userBean.getPayPwdStat() == 1) {
                    showCheckPaywordDialog();
                } else {
                    //未设置支付密码
                    showSetPaywordDialog();
                }
            } else {
                //未绑定手机号
                showBindPhoneNumDialog();
            }*/
            httpConsumption();
        } else {
            //未实名认证->分三步走流程(1 同意->2 实名认证->3 绑定手机号->4 新增一个步骤设置支付密码)
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
                    }
                })
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //从商城跳转到认证，需要额外增加一个步骤，即设置支付密码
                        startActivity(new Intent(activity, ServiceAgreementActivity.class).putExtra("from_shop", authAll));
                        dialogOne.dismiss();
                    }
                })
                .build();
        dialogOne.show();
    }
}