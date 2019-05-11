package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.yanlong.im.R;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

public class SecurityPrivacyActivity extends AppActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private CheckBox mCbFindPhone;
    private CheckBox mCbFindProductNumber;
    private CheckBox mCbVerification;
    private LinearLayout mViewSettingPassword;
    private LinearLayout mViewBlacklist;
    private HeadView mHeadView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_privacy);
        initView();
        initEvent();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_setting_password:
                Intent intent = new Intent(this, SetingPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.view_blacklist:

                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_find_phone:
                if (isChecked) {
                    ToastUtil.show(this, "选中");
                } else {
                    ToastUtil.show(this, "取消选中");
                }
                break;
            case R.id.cb_find_product_number:
                if (isChecked) {
                    ToastUtil.show(this, "选中");
                } else {
                    ToastUtil.show(this, "取消选中");
                }
                break;
            case R.id.cb_verification:
                if (isChecked) {
                    ToastUtil.show(this, "选中");
                } else {
                    ToastUtil.show(this, "取消选中");
                }
                break;
        }
    }
}






















