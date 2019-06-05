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
import com.yanlong.im.user.action.UserAction;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckUtil;
import net.cb.cb.library.utils.CountDownUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.ClearEditText;
import net.cb.cb.library.view.HeadView;

import retrofit2.Call;
import retrofit2.Response;

public class RegisterActivity extends AppActivity implements View.OnClickListener {

    private ClearEditText mEtPhoneContent;
    private EditText mEtIdentifyingCodeContent;
    private ClearEditText mEtPasswordContent;
    private ClearEditText mEtNextPasswordContent;
    private Button mBtnRegister;
    private TextView mTvMattersNeedAttention;
    private TextView mTvGetVerificationCode;
    private HeadView mHeadView;
    private UserAction userAction;
    private EditText mEtNicknameContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CountDownUtil.cancelTimer();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mEtPhoneContent = findViewById(R.id.et_phone_content);
        mEtIdentifyingCodeContent = findViewById(R.id.et_identifying_code_content);
        mBtnRegister = findViewById(R.id.btn_register);
        mTvMattersNeedAttention = findViewById(R.id.tv_matters_need_attention);
        mTvGetVerificationCode = findViewById(R.id.tv_get_verification_code);
        mEtPasswordContent = findViewById(R.id.et_password_content);
        mEtNextPasswordContent = findViewById(R.id.et_next_password_content);
        mEtNicknameContent = findViewById(R.id.et_nickname_content);
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

    private void initData() {
        userAction = new UserAction();
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
        final String phone = mEtPhoneContent.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(RegisterActivity.this, "请填写手机号码");
            return;
        }
        if(!CheckUtil.isMobileNO(phone)){
            ToastUtil.show(this,"手机号不合法");
            return;
        }

        CountDownUtil.getTimer(60, mTvGetVerificationCode, "发送验证码", this, new CountDownUtil.CallTask() {
            @Override
            public void task() {
                taskGetSms(phone);
            }
        });

    }

    private void register() {
        String phone = mEtPhoneContent.getText().toString();
        String code = mEtIdentifyingCodeContent.getText().toString();
        String password = mEtPasswordContent.getText().toString();
        String nextPassword = mEtNextPasswordContent.getText().toString();
        String nikename = mEtNicknameContent.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(this, "请输入手机号");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.show(this, "请输入密码");
            return;
        }
        if (TextUtils.isEmpty(nextPassword)) {
            ToastUtil.show(this, "请再次输入密码");
            return;
        }
        if (!password.equals(nextPassword)) {
            ToastUtil.show(this, "两次输入密码不一致");
            return;
        }
        if (TextUtils.isEmpty(code)) {
            ToastUtil.show(this, "请输入验证码");
            return;
        }
        if (TextUtils.isEmpty(nikename)) {
            ToastUtil.show(this, "请输入昵称");
            return;
        }
        if(!CheckUtil.isMobileNO(phone)){
            ToastUtil.show(this,"手机号不合法");
            return;
        }

        taskRegister(phone, password, code,nikename);

    }


    private void taskGetSms(String phone) {
        userAction.smsCaptchaGet(phone, "register", new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(RegisterActivity.this, response.body().getMsg());
            }
        });
    }

    private void taskRegister(String phone, String password, String captcha,String nickname) {
        userAction.register(phone, password, captcha,nickname, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(RegisterActivity.this, response.body().getMsg());
                if(response.body().isOk()){
                    finish();
                }

            }
        });
    }




}
