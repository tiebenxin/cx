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
import com.yanlong.im.utils.PasswordTextWather;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CallBack4Btn;
import net.cb.cb.library.utils.ClickFilter;
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
       /* if(AppConfig.DEBUG){
            mEtPhoneContent.setText("13000000000");
            mEtPasswordContent.setText("123456");
        }*/

    }

    private void initEvent(){
        mEtPasswordContent.addTextChangedListener(new PasswordTextWather(mEtPasswordContent,this));
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
        ClickFilter.onClick(mBtnLogin, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_identifying_code:
                Intent intent = new Intent(this,IdentifyingCodeActivity.class);
                startActivity(intent);
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
        userAction.login(phone, password, UserAction.getDevId(this), new CallBack4Btn<ReturnBean<TokenBean>>(mBtnLogin) {

            @Override
            public void onResp(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if(response.body().isOk()){
                    Intent intent = new Intent(getContext(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    ToastUtil.show(getContext(),response.body().getMsg());
                }
            }
        });
    }

}
