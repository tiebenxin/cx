package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.TokenBean;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.PopupSelectView;

import retrofit2.Call;
import retrofit2.Response;

import static com.yanlong.im.user.ui.IdentifyingCodeActivity.PHONE;

public class LoginActivity extends AppActivity implements View.OnClickListener {

    private SimpleDraweeView mImgHead;
    private TextView mTvPhoneNumber;
    private EditText mEtPasswordContent;
    private TextView mTvIdentifyingCode;
    private Button mBtnLogin;
    private TextView mTvForgetPassword;
    private TextView mTvMore;
    private PopupSelectView popupSelectView;
    private String[] strings = {"切换账号", "注册", "取消"};
    private String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initEvent();
        initData();
    }


    private void initView() {
        mImgHead = findViewById(R.id.img_head);
        mTvPhoneNumber = findViewById(R.id.tv_phone_number);
        mEtPasswordContent = findViewById(R.id.et_password_content);
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

    private void initData() {
        phone = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.PHONE).get4Json(String.class);
        String imageHead = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.IMAGE_HEAD).get4Json(String.class);
        mTvPhoneNumber.setText(phone);
        mImgHead.setImageURI(imageHead+"");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_identifying_code:
                Intent intent = new Intent(this, IdentifyingCodeActivity.class);
                intent.putExtra(PHONE,phone);
                startActivity(intent);
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_forget_password:
                Intent forgotPasswordIntent = new Intent(this, ForgotPasswordActivity.class);
                startActivity(forgotPasswordIntent);
                break;
            case R.id.tv_more:
                initPopup();
                break;
        }
    }

    private void initPopup() {
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(mImgHead, Gravity.BOTTOM, 0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                switch (postsion) {
                    case 0:
                        go(PasswordLoginActivity.class);
                        break;
                    case 1:
                        go(RegisterActivity.class);
                        break;
                }
                popupSelectView.dismiss();
            }
        });
    }


    private void login() {
        String password = mEtPasswordContent.getText().toString();
        String phone = mTvPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(this, "请输入账号");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.show(this, "请输入密码");
            return;
        }
        new UserAction().login(Long.valueOf(phone), password, UserAction.getDevId(this), new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if(response.body() == null){
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
}

