package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yanlong.im.R;

import net.cb.cb.library.utils.IdCardUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

public class IdentityAttestationActitiy extends AppActivity {

    private HeadView mHeadView;
    private EditText mEdName;
    private EditText mEdIdentity;
    private Button mBtnCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actitity_identity_attestation);
        initView();
        initEvent();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mEdName = findViewById(R.id.ed_name);
        mEdIdentity = findViewById(R.id.ed_identity);
        mBtnCommit = findViewById(R.id.btn_commit);
    }

    private void initEvent() {
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        mBtnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
    }


    private void commit() {
        String name = mEdName.getText().toString();
        String identity = mEdIdentity.getText().toString();
        IdCardUtil idCardUtil = new IdCardUtil(identity);
        if(TextUtils.isEmpty(name)){
            ToastUtil.show(this,"请填写姓名");
          return;
        }

        if(TextUtils.isEmpty(identity)){
            ToastUtil.show(this,"请填写身份证号码");
            return;
        }
        if(idCardUtil.isCorrect() != 0){
            ToastUtil.show(this,"身份证号码不合法");
            return;
        }



    }

}
