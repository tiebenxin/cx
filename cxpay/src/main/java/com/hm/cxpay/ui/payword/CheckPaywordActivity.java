package com.hm.cxpay.ui.payword;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hm.cxpay.R;
import com.hm.cxpay.dailog.DialogErrorPassword;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.redenvelope.MultiRedPacketActivity;
import com.hm.cxpay.widget.PswView;

import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

/**
 * @类名：零钱->支付密码校验
 * @Date：2019/11/30
 * @by zjy
 * @备注：
 */
public class CheckPaywordActivity extends AppActivity {

    private HeadView headView;
    private ActionbarView actionbar;
    private PswView pswView;
    private Activity activity;
    private DialogErrorPassword dialogErrorPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_psw);
        activity = this;
        initView();
        initData();
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        pswView = findViewById(R.id.psw_view);
        actionbar = headView.getActionbar();

    }

    private void initData() {
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        //监听达到六位密码后返回输入值
        pswView.setOnPasswordChangedListener(new PswView.onPasswordChangedListener() {
            @Override
            public void setPasswordChanged(String password) {
                httpCheckPayword(password);
            }
        });
        showSoftKeyword(pswView);
    }

    /**
     * 发请求->检查支付密码（是否正确）
     */
    private void httpCheckPayword(final String payword) {
        PayHttpUtils.getInstance().checkPayword(payword)
                .compose(RxSchedulers.<BaseResponse>compose())
                .compose(RxSchedulers.<BaseResponse>handleResult())
                .subscribe(new FGObserver<BaseResponse>() {
                    @Override
                    public void onHandleSuccess(BaseResponse baseResponse) {
                        //1 密码正确
                        Intent intent = new Intent();
                        intent.putExtra("payword", payword);
                        setResult(RESULT_OK, intent);
//                        ToastUtil.show(context, "支付密码校验成功！");
                        finish();
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        if (baseResponse.getCode() == -21000) {
                            showPswErrorDialog(true, baseResponse.getMessage());
                        } else if (baseResponse.getCode() == -21001) {
                            showPswErrorDialog(false, baseResponse.getMessage());
                        } else {
                            ToastUtil.show(context, baseResponse.getMessage());
                        }
                    }
                });
    }

    //显示密码错误弹窗
    private void showPswErrorDialog(boolean canRetry, String msg) {
        dialogErrorPassword = new DialogErrorPassword(this, R.style.MyDialogTheme);
        dialogErrorPassword.setCanceledOnTouchOutside(false);
        dialogErrorPassword.setCanRetry(canRetry);
        dialogErrorPassword.setContent(msg);
        dialogErrorPassword.setListener(new DialogErrorPassword.IErrorPasswordListener() {
            @Override
            public void onForget() {
                startActivity(new Intent(activity, ForgetPswStepOneActivity.class).putExtra("from", 1));
            }

            @Override
            public void onTry() {
                pswView.setText("");
                showSoftKeyword(pswView);
            }
        });
        dialogErrorPassword.show();
    }

    @Override
    protected void onDestroy() {
        if (dialogErrorPassword != null) {
            dialogErrorPassword.dismiss();
            dialogErrorPassword = null;
        }
        super.onDestroy();
    }
}
