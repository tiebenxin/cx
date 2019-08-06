package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yanlong.im.R;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

/**
 * @创建人 shenxin
 * @创建时间 2019/8/6 0006 14:03
 */
public class RegisterUserNameActivity extends AppActivity {

    private HeadView headView;
    private EditText etSetingUserName;
    private Button btnCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user_name);
        initView();
        initEvent();
    }


    private void initView() {
        headView =  findViewById(R.id.headView);
        etSetingUserName =  findViewById(R.id.et_seting_user_name);
        btnCommit =  findViewById(R.id.btn_commit);
    }

    private void initEvent(){
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
                setUserName();
            }
        });
    }


    private void setUserName(){
        String userName = etSetingUserName.getText().toString();
        if(TextUtils.isEmpty(userName)){
            ToastUtil.show(context,"请输入昵称");
            return;
        }
        ToastUtil.show(context,"修改完成");

    }


}
