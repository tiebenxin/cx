package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckUtil;
import net.cb.cb.library.utils.CountDownUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import retrofit2.Call;
import retrofit2.Response;

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
        final String phone = mEtPhoneContent.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(ForgotPasswordActivity.this, "请填写手机号码");
            return;
        }
        if(CheckUtil.isMobileNO(phone)){
            ToastUtil.show(this,"手机号不合法");
        }

        CountDownUtil.getTimer(60, mTvGetVerificationCode, "发送验证码", this, new CountDownUtil.CallTask() {
            @Override
            public void task() {
                taskGetSms(Long.valueOf(phone));
            }
        });
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
        if(!CheckUtil.isMobileNO(phone)){
            ToastUtil.show(this,"手机号不合法");
            return;
        }
        toNext(phone);
    }


    private void toNext(String phone) {
        Intent intent = new Intent(this, ForgotPasswordNextActivity.class);
        intent.putExtra(ForgotPasswordNextActivity.PHONE, phone);
        startActivity(intent);
    }

    private void taskGetSms(Long phone) {
        new UserAction().smsCaptchaGet(phone, "password", new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(ForgotPasswordActivity.this, response.body().getMsg());
            }
        });
    }


}
