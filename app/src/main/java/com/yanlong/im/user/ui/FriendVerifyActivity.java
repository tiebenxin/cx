package com.yanlong.im.user.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;

import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityFriendVerifyBinding;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

/**
 * @anthor Liszt
 * @data 2019/9/29
 * Description 好友验证页面
 */
public class FriendVerifyActivity extends AppActivity {
    public final static String CONTENT = "content";

    private ActivityFriendVerifyBinding ui;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_friend_verify);
        Intent intent = getIntent();
        String content = intent.getStringExtra(CONTENT);
        if (!TextUtils.isEmpty(content)) {
            ui.etTxt.setText(content);
        }

        ui.headView.getActionbar().setTxtRight("发送");
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {

            }

            @Override
            public void onRight() {

            }
        });
    }
}
