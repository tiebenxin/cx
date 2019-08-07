package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CallBack4Btn;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import retrofit2.Call;
import retrofit2.Response;

public class SecurityPrivacyActivity extends AppActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private CheckBox mCbFindPhone;
    private CheckBox mCbFindProductNumber;
    private CheckBox mCbVerification;
    private LinearLayout mViewSettingPassword;
    private LinearLayout mViewBlacklist;
    private HeadView mHeadView;
    private UserAction userAction;
    private long uid;
    private int isClick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_privacy);
        initView();
        initEvent();
        initData();
    }


    private void initView() {
        mCbFindPhone = findViewById(R.id.cb_find_phone);
        mCbFindProductNumber = findViewById(R.id.cb_find_product_number);
        mCbVerification = findViewById(R.id.cb_verification);
        mViewSettingPassword = findViewById(R.id.view_setting_password);
        mViewBlacklist = findViewById(R.id.view_blacklist);
        mHeadView =  findViewById(R.id.headView);
    }


    private void initEvent() {
        mViewSettingPassword.setOnClickListener(this);
        mViewBlacklist.setOnClickListener(this);
        mCbFindPhone.setOnCheckedChangeListener(this);
        mCbFindProductNumber.setOnCheckedChangeListener(this);
        mCbVerification.setOnCheckedChangeListener(this);
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


    private void initData(){
        userAction = new UserAction();
        uid = UserAction.getMyId();
        taskUserInfo(uid);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_setting_password:
                Intent intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.view_blacklist:
                go(BlacklistActivity.class);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isClick == 1){
            switch (buttonView.getId()) {
                case R.id.cb_find_phone:
                    if (isChecked) {
                        taskUserMask(1,0);
                    } else {
                        taskUserMask(0,0);
                    }
                    break;
                case R.id.cb_find_product_number:
                    if (isChecked) {
                        taskUserMaskProduct(1,1);
                    } else {
                        taskUserMaskProduct(0,1);
                    }
                    break;
                case R.id.cb_verification:
                    if (isChecked) {
                        taskUserMaskVerification(1,2);
                    } else {
                        taskUserMaskVerification(0,2);
                    }
                    break;
            }
        }
    }

    private void taskUserMask(int switchval,int avatar){
        userAction.userMaskSet(switchval, avatar, new CallBack4Btn<ReturnBean>(mCbFindPhone) {
            @Override
            public void onResp(Call<ReturnBean> call, Response<ReturnBean> response) {
                if(response.body() == null){
                    return;
                }
                ToastUtil.show(SecurityPrivacyActivity.this,response.body().getMsg());
            }
        });
    }

    private void taskUserMaskProduct(int switchval,int avatar){
        userAction.userMaskSet(switchval, avatar, new CallBack4Btn<ReturnBean>(mCbFindProductNumber) {
            @Override
            public void onResp(Call<ReturnBean> call, Response<ReturnBean> response) {
                if(response.body() == null){
                    return;
                }
                ToastUtil.show(SecurityPrivacyActivity.this,response.body().getMsg());
            }
        });
    }


    private void taskUserMaskVerification(int switchval,int avatar){
        userAction.userMaskSet(switchval, avatar, new CallBack4Btn<ReturnBean>(mCbVerification) {
            @Override
            public void onResp(Call<ReturnBean> call, Response<ReturnBean> response) {
                if(response.body() == null){
                    return;
                }
                ToastUtil.show(SecurityPrivacyActivity.this,response.body().getMsg());
            }
        });
    }

    private void taskUserInfo(long uid){
        userAction.getUserInfo4Id(uid, new CallBack<ReturnBean<UserInfo>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
                if(response.body() == null){
                    return;
                }
                UserInfo userInfo = response.body().getData();
                if(userInfo.getPhonefind() == 0){
                    mCbFindPhone.setChecked(false);
                }else{
                    mCbFindPhone.setChecked(true);
                }

                if(userInfo.getImidfind() == 0){
                    mCbFindProductNumber.setChecked(false);
                }else{
                    mCbFindProductNumber.setChecked(true);
                }

                if(userInfo.getFriendvalid() == 0){
                    mCbVerification.setChecked(false);
                }else{
                    mCbVerification.setChecked(true);
                }
                isClick = 1;
            }
        });
    }



}






















