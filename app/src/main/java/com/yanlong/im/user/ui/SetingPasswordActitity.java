package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.yanlong.im.R;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.ClearEditText;
import net.cb.cb.library.view.HeadView;

/**
 * @创建人 shenxin
 * @创建时间 2019/8/6 0006 15:25
 */
public class SetingPasswordActitity extends AppActivity {

    private HeadView headView;
    private ClearEditText edPassword;
    private ClearEditText edVerifyPassword;
    private Button btnCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting_password);
        initView();
        initEvent();
    }


    private void initView() {
        headView = findViewById(R.id.headView);
        edPassword = findViewById(R.id.ed_password);
        edVerifyPassword = findViewById(R.id.ed_verify_password);
        btnCommit = findViewById(R.id.btn_commit);
    }

    private void initEvent() {
        headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
    }

    private void commit() {
        String password = edPassword.getText().toString();
        String nextPassword = edVerifyPassword.getText().toString();
        if(TextUtils.isEmpty(password)){
            ToastUtil.show(context,"请填写密码");
            return;
        }
        if(TextUtils.isEmpty(nextPassword)){
            ToastUtil.show(context,"请填写验证密码");
            return;
        }
        if(!password.equals(nextPassword)){
            ToastUtil.show(context,"两次密码不一致");
            return;
        }
    }


}
