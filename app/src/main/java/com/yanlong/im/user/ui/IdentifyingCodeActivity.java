package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.SmsBean;
import com.yanlong.im.user.bean.TokenBean;

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

public class IdentifyingCodeActivity extends AppActivity implements View.OnClickListener {
    public final static String PHONE = "phone";
    private EditText mEtPhoneContent;
    private EditText mEtIdentifyingCodeContent;
    private TextView mTvPassword;
    private Button mBtnLogin;
    private TextView mTvGetVerificationCode;
    private UserAction userAction;
    private HeadView mHeadView;
    private String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identifying_code);
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
        mEtPhoneContent = findViewById(R.id.et_phone_content);
        mEtIdentifyingCodeContent = findViewById(R.id.et_identifying_code_content);
        mTvGetVerificationCode = findViewById(R.id.tv_get_verification_code);
        mTvPassword = findViewById(R.id.tv_password);
        mBtnLogin = findViewById(R.id.btn_login);
        mHeadView = findViewById(R.id.headView);
        phone = getIntent().getStringExtra(PHONE);
        if(!TextUtils.isEmpty(phone)){
            mEtPhoneContent.setText(phone);
        }

    }

    private void initEvent() {
        mTvPassword.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
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
            case R.id.tv_password:
                Intent intent = new Intent(this, PasswordLoginActivity.class);
                startActivity(intent);
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
        final String phone = mEtPhoneContent.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(IdentifyingCodeActivity.this, "请填写手机号码");
            return;
        }
        if (!CheckUtil.isMobileNO(phone)) {
            ToastUtil.show(this, "手机号不合法");
            return;
        }
        CountDownUtil.getTimer(60, mTvGetVerificationCode, "发送验证码", this, new CountDownUtil.CallTask() {
            @Override
            public void task() {
                taskGetSms(phone);
            }
        });
    }


    private void login() {
        String phone = mEtPhoneContent.getText().toString();
        String code = mEtIdentifyingCodeContent.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(this, "请输入账号");
            return;
        }
        if (TextUtils.isEmpty(code)) {
            ToastUtil.show(this, "请输入验证码");
            return;
        }
        if (!CheckUtil.isMobileNO(phone)) {
            ToastUtil.show(this, "手机号不合法");
            return;
        }
        userAction.login4Captch(phone, code, UserAction.getDevId(this), new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    Intent intent = new Intent(getContext(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    ToastUtil.show(getContext(), response.body().getMsg());
                }
            }
        });

    }


    private void taskGetSms(String phone) {
        userAction.smsCaptchaGet(phone, "login", new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(IdentifyingCodeActivity.this, response.body().getMsg());
            }
        });
    }

}