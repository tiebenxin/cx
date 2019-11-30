package com.hm.cxpay.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hm.cxpay.R;
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
public class CheckPayPswActivity extends AppActivity {

    private HeadView headView;
    private ActionbarView actionbar;
    private PswView pswView;
    private Context activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkpsw);
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
                ToastUtil.show(CheckPayPswActivity.this, "支付密码是" + password);
                //TODO 判断是支付密码是否正确
                boolean pswIsTrue = false;
                if (pswIsTrue) {
                    //1 密码正确
                    ToastUtil.show(activity, "密码正确");
                } else {
                    //2 密码不正确
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                    dialogBuilder.setCancelable(false);
                    final AlertDialog dialog = dialogBuilder.create();
                    //获取界面
                    View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_payword_error, null);
                    //初始化控件
                    TextView tvTryAgain = dialogView.findViewById(R.id.tv_try_again);
                    TextView tvForgetPsw = dialogView.findViewById(R.id.tv_forget_psw);
                    //重试
                    tvTryAgain.setOnClickListener(new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            pswView.setText("");
                        }
                    });
                    //忘记密码
                    tvForgetPsw.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO 跳 忘记密码
                            ToastUtil.show(activity, "忘记密码");
                        }
                    });
                    //展示界面
                    dialog.show();
                    //解决圆角shape背景无效问题
                    Window window = dialog.getWindow();
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    //设置宽高
                    WindowManager.LayoutParams lp = window.getAttributes();
                    lp.height = DensityUtil.dip2px(activity, 139);
                    lp.width = DensityUtil.dip2px(activity, 277);
                    dialog.getWindow().setAttributes(lp);
                    dialog.setContentView(dialogView);
                }
            }
        });
    }

}
