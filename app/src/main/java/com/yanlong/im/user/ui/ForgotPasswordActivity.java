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
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

public class ForgotPasswordActivity extends AppActivity implements View.OnClickListener {
    private EditText mEtPhoneContent;
    private EditText mEtIdentifyingCodeContent;
    private TextView mTvGetVerificationCode;
    private Button mBtnNext;
    private HeadView mHeadView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initView();
        initEvent();
    }


    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mEtPhoneContent = findViewById(R.id.et_phone_content);
        mEtIdentifyingCodeContent = findViewById(R.id.et_identifying_code_content);
        mBtnNext = findViewById(R.id.btn_next);
        mTvGetVerificationCode = findViewById(R.id.tv_get_verification_code);

    }

    private void initEvent() {
        mBtnNext.setOnClickListener(this);
        mTvGetVerificationCode.setOnClickListener(this);
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                register();
                break;
            case R.id.tv_get_verification_code:
                initCountDownUtil();
                break;
        }
    }


    private void initCountDownUtil() {
        String phone = mEtPhoneContent.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(ForgotPasswordActivity.this, "请填写手机号码");
            return;
        }
        new CountDownUtil(mTvGetVerificationCode)
                .setCountDownMillis(60_000L)//倒计时60000ms
                .setCountDownColor(R.color.red_600, R.color.green_600)//不同状态字体颜色
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                }).start();
    }

    private void register() {
        String phone = mEtPhoneContent.getText().toString();
        String password = mEtIdentifyingCodeContent.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(this, "请输入手机号");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.show(this, "请输入验证码");
            return;
        }

        toNext(phone);
    }


    private void toNext(String phone) {
        Intent intent = new Intent(this, ForgotPasswordNextActivity.class);
        intent.putExtra(ForgotPasswordNextActivity.PHONE, phone);
        startActivity(intent);
    }


}
