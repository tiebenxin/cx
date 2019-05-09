package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

public class HelpInfoActivity extends AppActivity {


    private HeadView mHeadView;
    private TextView mTvTitle;
    private TextView mTvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_info);
        initView();
        initEvent();
    }

    private void initView() {
        mHeadView =  findViewById(R.id.headView);
        mTvTitle =  findViewById(R.id.tv_title);
        mTvContent =  findViewById(R.id.tv_content);
    }

    private void initEvent(){
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

}
