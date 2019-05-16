package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.AsyncLayoutInflater;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.TokenBean;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import retrofit2.Call;
import retrofit2.Response;

public class PasswordLoginActivity extends AppActivity implements View.OnClickListener {
    private HeadView mHeadView;
    private EditText mEtPhoneContent;
    private EditText mEtPasswordContent;
    private TextView mTvIdentifyingCode;
    private Button mBtnLogin;
    private UserAction userAction = new UserAction();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passworfd_login);
        initView();
        initEvent();
    }

    private void initView(){
        mHeadView =  findViewById(R.id.headView);
        mEtPhoneContent = findViewById(R.id.et_phone_content);
        mEtPasswordContent =  findViewById(R.id.et_password_content);
        mTvIdentifyingCode =  findViewById(R.id.tv_identifying_code);
        mBtnLogin =  findViewById(R.id.btn_login);
        mHeadView.getActionbar().setTxtRight("注册");
    }

    private void initEvent(){
        if(AppConfig.DEBUG){
            mEtPhoneContent.setText("13111111111");
            mEtPasswordContent.setText("123456");
        }
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                Intent intent = new Intent(PasswordLoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        mTvIdentifyingCode.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_identifying_code:
                Intent intent = new Intent(this,IdentifyingCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                login();
                break;
        }

    }


    private void login(){
        String phone = mEtPhoneContent.getText().toString();
        String password = mEtPasswordContent.getText().toString();
        if(TextUtils.isEmpty(phone)){
            ToastUtil.show(this,"请输入账号");
            return;
        }
        if(TextUtils.isEmpty(password)){
            ToastUtil.show(this,"请输入密码");
            return;
        }
        userAction.login(Long.valueOf(phone), password, UserAction.getDevId(this), new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if(response.body().isOk()){
                    go(MainActivity.class);
                    finish();
                }else{
                    ToastUtil.show(getContext(),response.body().getMsg());
                }
            }
        });
    }

}
