package com.yanlong.im.user.ui;

import android.os.Bundle;

import com.yanlong.im.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

public class GroupAddActivity extends AppActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add);
        initView();
        initEvent();
    }
    private HeadView headView_groupadd;
    private void initView() {
        headView_groupadd=findViewById(R.id.headView_groupadd);
    }


    private void initEvent() {
        headView_groupadd.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
    }
}
