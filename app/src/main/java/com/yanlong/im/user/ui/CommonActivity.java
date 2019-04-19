package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.ui.ChatFontActivity;

import net.cb.cb.library.utils.VersionUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

public class CommonActivity extends AppActivity implements View.OnClickListener {

    private LinearLayout mViewAccountBinding;
    private LinearLayout mViewNewMessage;
    private LinearLayout mViewSecurityPrivacy;
    private LinearLayout mViewSetingFont;
    private LinearLayout mViewClear;
    private Button mBtnExit;
    private HeadView mHeadView;
    private LinearLayout mViewAboutAs;
    private TextView mTvVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        initView();
        initEvent();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mViewAccountBinding = findViewById(R.id.view_account_binding);
        mViewNewMessage = findViewById(R.id.view_new_message);
        mViewSecurityPrivacy = findViewById(R.id.view_security_privacy);
        mViewSetingFont = findViewById(R.id.view_seting_font);
        mViewClear = findViewById(R.id.view_clear);
        mBtnExit = findViewById(R.id.btn_exit);
        mViewAboutAs =  findViewById(R.id.view_about_as);
        mTvVersion =  findViewById(R.id.tv_version);
        mTvVersion.setText(VersionUtil.getVerName(this));
    }

    private void initEvent() {
        mViewAccountBinding.setOnClickListener(this);
        mViewNewMessage.setOnClickListener(this);
        mViewSecurityPrivacy.setOnClickListener(this);
        mViewSetingFont.setOnClickListener(this);
        mViewClear.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);
        mViewAboutAs.setOnClickListener(this);
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
            case R.id.view_account_binding:
                Intent bindingIntent = new Intent(this, AccountBindingActvity.class);
                startActivity(bindingIntent);
                break;
            case R.id.view_new_message:
                Intent messageIntent = new Intent(this, NewMessageActivity.class);
                startActivity(messageIntent);
                break;
            case R.id.view_security_privacy:
                Intent securityIntent = new Intent(this, SecurityPrivacyActivity.class);
                startActivity(securityIntent);
                break;
            case R.id.view_seting_font:
                Intent fontInent = new Intent(this, ChatFontActivity.class);
                startActivity(fontInent);
                break;
            case R.id.view_clear:


                break;
            case R.id.btn_exit:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                onBackPressed();
                break;
            case R.id.view_about_as:
                Intent aboutIntent = new Intent(this, AboutAsActivity.class);
                startActivity(aboutIntent);
                break;
        }
    }
}
