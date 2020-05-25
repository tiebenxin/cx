package com.yanlong.im.user.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivitySafetyCenterBinding;
import com.yanlong.im.user.ui.freeze.FreezeAccountActivity;
import com.yanlong.im.user.ui.logout.LogoutAccountActivity;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

/**
 * @author Liszt
 * @date 2020/5/20
 * Description 安全中心
 */
public class SafetyCenterActivity extends AppActivity {

    private ActivitySafetyCenterBinding ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_safety_center);

        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onRight() {
            }
        });
        //设置登录密码
        ui.viewSettingPswOfLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //设置支付密码
        ui.viewSettingPswOfPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //找回支付密码
        ui.viewSettingPswOfPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //管理登录设备
        ui.viewManagerDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go(DeviceManagerActivity.class);

            }
        });

        //冻结账号
        ui.viewFreezeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go(FreezeAccountActivity.class);
            }
        });

        //申诉账号
        ui.viewAppealAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //注销账号
        ui.viewLogoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go(LogoutAccountActivity.class);
            }
        });
    }

    public void go(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }
}
