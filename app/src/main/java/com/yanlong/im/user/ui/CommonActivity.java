package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.ui.ChatFontActivity;
import com.yanlong.im.user.action.UserAction;

import net.cb.cb.library.utils.VersionUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertYesNo;
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
    private UserAction userAction = new UserAction();


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
        mViewAboutAs = findViewById(R.id.view_about_as);
        mTvVersion = findViewById(R.id.tv_version);
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
                taskClearMsg();

                break;
            case R.id.btn_exit:
                taskExit();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();

                break;
            case R.id.view_about_as:
                Intent aboutIntent = new Intent(this, AboutAsActivity.class);
                startActivity(aboutIntent);
                break;
        }
    }


    /***
     * 退出
     */
    private void taskExit() {

        userAction.loginOut();
    }

    private MsgAction msgAction = new MsgAction();

    /***
     * 清理消息
     */
    private void taskClearMsg() {
        AlertYesNo alertYesNo=new AlertYesNo();
        alertYesNo.init(this, "清理", "确定清理所有消息?", "确定", "取消", new AlertYesNo.Event() {
            @Override
            public void onON() {

            }

            @Override
            public void onYes() {
                msgAction.msgDelAll();
            }
        });
        alertYesNo.show();
    }

}
