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
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.dailog.ChangeSelectDialog;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.BindPhoneNumActivity;
import com.hm.cxpay.ui.LooseChangeActivity;
import com.hm.cxpay.ui.payword.SetPaywordActivity;
import com.hm.cxpay.widget.PswView;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.ui.ServiceAgreementActivity;

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
    private ChangeSelectDialog.Builder builder;
    private ChangeSelectDialog dialogOne;//通用提示选择弹框：实名认证
    private ChangeSelectDialog dialogTwo;//通用提示选择弹框：是否绑定手机号

    private String url = "";//商城地址
    private String payMoney = "";//需要支付的钱
    private String payStatus = "1";// 1 无操作  0 关闭密码框/用户支付失败  含http，即为成功，返回url
    private String authAll = "1";// 来自商城的认证流程： 1 需要完成全部三层认证
    private String authOnce = "0";// 来自商城的认证流程：0 仅认证一次

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
        builder = new ChangeSelectDialog.Builder(activity);
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
     * 提示弹框->校验支付密码
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
                        if (checkPaywordDialog.isShowing()) {
                            checkPaywordDialog.dismiss();
                        }
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
                        pswView.clear();
                    }
                });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(PayEnvironment.getInstance().getUserId()<=0){
                UserInfo info = UserAction.getMyInfo();
                if (info!=null && info.getUid() != null) {
                    PayEnvironment.getInstance().setUserId(info.getUid().longValue());
                }
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
            if (userBean.getPhoneBindStat() == 1) {
                //3 已设置支付密码
                if(userBean.getPayPwdStat() == 1){
                    showCheckPaywordDialog();
                }else {
                    //未设置支付密码
                    showSetPaywordDialog();
                }
            } else {
                //未绑定手机号
                showBindPhoneNumDialog();
            }
        } else {
            //未实名认证->分三步走流程(1 同意->2 实名认证->3 绑定手机号->4 新增一个步骤设置支付密码)
            showIdentifyDialog();
        }
    }

    /**
     * 请求->获取用户信息
     */
    private void httpGetUserInfo() {
        UserInfo info = UserAction.getMyInfo();
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
                        startActivity(new Intent(activity, ServiceAgreementActivity.class).putExtra("from_shop",authAll));
                        dialogOne.dismiss();
                    }
                })
                .build();
        dialogOne.show();
    }

    /**
     * 是否绑定手机号弹框
     */
    private void showBindPhoneNumDialog() {
        dialogTwo = builder.setTitle("您还没有绑定手机号码\n请先绑定后再进行操作。")
                .setLeftText("取消")
                .setRightText("去绑定")
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消
                        dialogTwo.dismiss();
                    }
                })
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //去绑定
                        startActivity(new Intent(activity, BindPhoneNumActivity.class).putExtra("from_shop",authOnce));
                        dialogTwo.dismiss();
                    }
                })
                .build();
        dialogTwo.show();
    }

    /**
     * 检测到未设置支付密码弹框 (特殊样式，暂不复用)
     */
    private void showSetPaywordDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setCancelable(false);
        final AlertDialog dialog = dialogBuilder.create();
        //获取界面
        View dialogView = LayoutInflater.from(activity).inflate(com.hm.cxpay.R.layout.dialog_set_payword, null);
        //初始化控件
        TextView tvSet = dialogView.findViewById(com.hm.cxpay.R.id.tv_set);
        TextView tvExit = dialogView.findViewById(com.hm.cxpay.R.id.tv_exit);
        //去设置
        tvSet.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(activity, SetPaywordActivity.class).putExtra("from_shop",authOnce));

            }
        });
        //取消
        tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //展示界面
        dialog.show();
        //解决圆角shape背景无效问题
        Window window = dialog.getWindow();
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
        dialog.getWindow().setAttributes(lp);
        dialog.setContentView(dialogView);
    }
}