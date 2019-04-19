package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.utils.VersionUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

public class AboutAsActivity extends AppActivity {

    private HeadView mHeadView;
    private ImageView mIvLogo;
    private TextView mTvVersionNumber;
    private LinearLayout mLlCheckVersions;
    private LinearLayout mLlService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_as);
        initView();
        initEvent();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mIvLogo = findViewById(R.id.iv_logo);
        mTvVersionNumber = findViewById(R.id.tv_version_number);
        mLlCheckVersions = findViewById(R.id.ll_check_versions);
        mLlService = findViewById(R.id.ll_service);
        mTvVersionNumber.setText("夸夸聊     "+VersionUtil.getVerName(this));

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

        mLlCheckVersions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mLlService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
