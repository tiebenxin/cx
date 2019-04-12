package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
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

public class RegisterActivity extends AppActivity implements View.OnClickListener {

    private EditText mEtPhoneContent;
    private EditText mEtIdentifyingCodeContent;
    private Button mBtnRegister;
    private TextView mTvMattersNeedAttention;
    private TextView mTvGetVerificationCode;
    private HeadView mHeadView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initEvent();
    }


    private void initView() {
        mHeadView =  findViewById(R.id.headView);
        mEtPhoneContent = findViewById(R.id.et_phone_content);
        mEtIdentifyingCodeContent = findViewById(R.id.et_identifying_code_content);
        mBtnRegister = findViewById(R.id.btn_register);
        mTvMattersNeedAttention = findViewById(R.id.tv_matters_need_attention);
        mTvGetVerificationCode = findViewById(R.id.tv_get_verification_code);
        initTvMNA();
    }

    private void initEvent() {
        mBtnRegister.setOnClickListener(this);
        mTvMattersNeedAttention.setOnClickListener(this);
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
            case R.id.btn_register:
                register();
                break;
            case R.id.tv_get_verification_code:
                initCountDownUtil();
                break;
        }
    }


    private void initTvMNA() {

        final SpannableStringBuilder style = new SpannableStringBuilder();
        style.append("点击\"注册\"即表示已阅读并同意《用户使用协议》和《隐私权政策》");

        ClickableSpan clickProtocol = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ToastUtil.show(RegisterActivity.this, "点击用户协议");
            }
        };
        style.setSpan(clickProtocol, 15, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_600));
        style.setSpan(protocolColorSpan, 15, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickPolicy = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ToastUtil.show(RegisterActivity.this, "点击隐私政策");
            }
        };
        style.setSpan(clickPolicy, 24, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan policyColorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_600));
        style.setSpan(policyColorSpan, 24, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTvMattersNeedAttention.setText(style);
        mTvMattersNeedAttention.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void initCountDownUtil() {
        String phone = mEtPhoneContent.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(RegisterActivity.this, "请填写手机号码");
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
    }

}
