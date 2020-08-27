package com.yanlong.im.user.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityFriendVerifyBinding;
import com.yanlong.im.user.action.UserAction;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @author Liszt
 * @date 2019/9/29
 * Description 好友验证页面
 */
public class FriendVerifyActivity extends AppActivity {
    public final static String CONTENT = "content";
    public final static String USER_ID = "user_id";
    public final static String USER_NOTE = "user_note";
    public final static String NICK_NAME = "nick_name";

    private ActivityFriendVerifyBinding ui;
    private Long userId;
    private String content;
    private String userNote;// 通讯录名字
    private String mNickName;// 昵称

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_friend_verify);
        Intent intent = getIntent();
        content = intent.getStringExtra(CONTENT);
        userId = intent.getLongExtra(USER_ID, -1L);
        userNote = intent.getStringExtra(USER_NOTE);
        mNickName = intent.getStringExtra(NICK_NAME);
        if (!TextUtils.isEmpty(content)) {
            ui.etTxt.setText(content);
        }
        if (!TextUtils.isEmpty(userNote)) {
            ui.etNote.setHint(userNote);
        } else if (!TextUtils.isEmpty(mNickName)) {
            ui.etNote.setHint(mNickName);
        }
        ui.headView.getActionbar().setTxtRight("发送");
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                setResult(RESULT_CANCELED);
                finish();
            }

            @Override
            public void onRight() {
                String t = ui.etTxt.getText().toString();
                if (!TextUtils.isEmpty(t)) {
                    content = t;
                }
                taskAddFriend(userId, content);
            }
        });
        ui.etNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && TextUtils.isEmpty(ui.etNote.getText().toString())) {
                    if (!TextUtils.isEmpty(userNote)) {
                        ui.etNote.setText(userNote);
                    } else if (!TextUtils.isEmpty(mNickName)) {
                        ui.etNote.setText(mNickName);
                    }
                }
            }
        });
    }

    private void taskAddFriend(Long userId, String sayHi) {
        if (userId <= 0) {
            ToastUtil.show(this, "无效用户");
        }
        String contactName = "";
        if (TextUtils.isEmpty(ui.etNote.getText().toString())) {
            if (!TextUtils.isEmpty(userNote)) {
                contactName = userNote;
            } else if (!TextUtils.isEmpty(mNickName)) {
                contactName = mNickName;
            }
        } else {
            contactName = ui.etNote.getText().toString().trim();
        }
        new UserAction().friendApply(userId, sayHi, contactName, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(FriendVerifyActivity.this, response.body().getMsg());
                if (response.body().isOk()) {
                    ToastUtil.show(context, "好友请求发送成功");
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }
}
