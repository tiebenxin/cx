package com.hm.cxpay.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hm.cxpay.R;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

/**
 * @类名：零钱->设置支付密码
 * @Date：2019/11/30
 * @by zjy
 * @备注：
 */
public class SetPayPswActivity extends AppActivity {

    private HeadView headView;
    private ActionbarView actionbar;
    private EditText etPassword;//密码输入框
    private EditText etConfirmPassword;//确认密码输入框
    private TextView tvSubmit;//确认提交
    private Context activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_paypsw);
        activity = this;
        initView();
        initData();
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        etPassword = findViewById(R.id.et_payword);
        etConfirmPassword = findViewById(R.id.et_confirm_payword);
        tvSubmit = findViewById(R.id.tv_submit);
        actionbar = headView.getActionbar();

    }

    private void initData() {
        //密码、确认密码默认隐藏明文
        TransformationMethod method =  PasswordTransformationMethod.getInstance();
        etPassword.setTransformationMethod(method);
        etConfirmPassword.setTransformationMethod(method);
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        //确认提交
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1. 必填不为空
                if (!TextUtils.isEmpty(etPassword.getText().toString()) && !TextUtils.isEmpty(etConfirmPassword.getText().toString())) {
                    //2. 密码必须为6位数字
                    if (etPassword.getText().toString().length() == 6) {
                        //3. 密码和确认密码必须一致
                        if (etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                            //TODO 发请求
//                            sendAddChangePayWordRequest();
                        } else {
                            ToastUtil.show(activity,"两次输入密码必须一致");
                        }
                    } else {
                        ToastUtil.show(activity,"密码必须为6位数字");
                    }
                } else {
                    ToastUtil.show(activity,"必填项不能为空");
                }
            }
        });

    }

}
