package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.utils.CountDownUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.AppActivity;

public class IdentifyingCodeActivity extends AppActivity implements View.OnClickListener {

    private EditText mEtPhoneContent;
    private EditText mEtIdentifyingCodeContent;
    private TextView mTvPassword;
    private Button mBtnLogin;
    private TextView mTvGetVerificationCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identifying_code);
        initView();
        initEvent();
    }

    private void initView() {
        mEtPhoneContent = findViewById(R.id.et_phone_content);
        mEtIdentifyingCodeContent = findViewById(R.id.et_identifying_code_content);
        mTvGetVerificationCode =  findViewById(R.id.tv_get_verification_code);
        mTvPassword = findViewById(R.id.tv_password);
        mBtnLogin = findViewById(R.id.btn_login);
    }

    private void initEvent() {
        mTvPassword.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        mTvGetVerificationCode.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_password:
                Intent intent = new Intent(this,PasswordLoginActivity.class);
                startActivity(intent);
                onBackPressed();
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_get_verification_code:
                initCountDownUtil();
                break;
        }
    }

    private void initCountDownUtil() {
        new CountDownUtil(mTvGetVerificationCode)
                .setCountDownMillis(60_000L)//倒计时60000ms
                .setCountDownColor(R.color.red_600, R.color.green_600)//不同状态字体颜色
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = mEtPhoneContent.getText().toString();
                        if (TextUtils.isEmpty(phone)) {
                            ToastUtil.show(IdentifyingCodeActivity.this, "请填写手机号码");
                            return;
                        }


                    }
                }).start();
    }


    private void login() {
        String phone = mEtPhoneContent.getText().toString();
        String password = mEtIdentifyingCodeContent.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(this, "请输入账号");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.show(this, "请输入验证码");
            return;
        }

    }
}