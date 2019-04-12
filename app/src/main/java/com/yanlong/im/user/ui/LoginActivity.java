package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yanlong.im.R;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.PopupSelectView;

public class LoginActivity extends AppActivity implements View.OnClickListener {

    private SimpleDraweeView mImgHead;
    private TextView mTvPhoneNumber;
    private EditText mEtPhoneContent;
    private TextView mTvIdentifyingCode;
    private Button mBtnLogin;
    private TextView mTvForgetPassword;
    private TextView mTvMore;
    private PopupSelectView popupSelectView;
    private String [] strings = {"切换账号","注册","取消"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initEvent();
    }


    private void initView() {
        mImgHead = findViewById(R.id.img_head);
        mTvPhoneNumber = findViewById(R.id.tv_phone_number);
        mEtPhoneContent = findViewById(R.id.et_phone_content);
        mTvIdentifyingCode = findViewById(R.id.tv_identifying_code);
        mBtnLogin = findViewById(R.id.btn_login);
        mTvForgetPassword = findViewById(R.id.tv_forget_password);
        mTvMore = findViewById(R.id.tv_more);

    }


    private void initEvent() {
        mTvIdentifyingCode.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        mTvForgetPassword.setOnClickListener(this);
        mTvMore.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_identifying_code:
                Intent intent = new Intent(this,IdentifyingCodeActivity.class);
                startActivity(intent);
                onBackPressed();
                break;
            case R.id.btn_login:

                break;
            case R.id.tv_forget_password:
                Intent forgotPasswordIntent = new Intent(this,ForgotPasswordActivity.class);
                startActivity(forgotPasswordIntent);
                break;
            case R.id.tv_more:
                initPopup();
                break;
        }
    }

    private void initPopup(){
        popupSelectView = new PopupSelectView(this,strings);
        popupSelectView.showAtLocation(mImgHead, Gravity.BOTTOM,0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                ToastUtil.show(LoginActivity.this,string);

                popupSelectView.dismiss();
            }
        });
    }
}

