package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import retrofit2.Call;
import retrofit2.Response;

public class NewMessageActivity extends AppActivity implements CompoundButton.OnCheckedChangeListener {

    private HeadView mHeadView;
    private CheckBox mCbReceiveMessage;
    private CheckBox mCbMessageInfo;
    private CheckBox mCbMessageVoice;
    private CheckBox mCbMessageShake;
    private UserAction userAction;
    private long uid;
    private int isClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mCbReceiveMessage = findViewById(R.id.cb_receive_message);
        mCbMessageInfo = findViewById(R.id.cb_message_info);
        mCbMessageVoice = findViewById(R.id.cb_message_voice);
        mCbMessageShake = findViewById(R.id.cb_message_shake);
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
        mCbReceiveMessage.setOnCheckedChangeListener(this);
        mCbMessageInfo.setOnCheckedChangeListener(this);
        mCbMessageVoice.setOnCheckedChangeListener(this);
        mCbMessageShake.setOnCheckedChangeListener(this);
    }

    private void initData() {
        userAction = new UserAction();
        uid = UserAction.getMyId();
        taskUserInfo(uid);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isClick == 1) {
            switch (buttonView.getId()) {
                case R.id.cb_receive_message:
                    if (isChecked) {
                        taskUserMask(1,4);
                    } else {
                        taskUserMask(0,4);
                    }
                    break;
                case R.id.cb_message_info:
                    if (isChecked) {
                        taskUserMask(1,5);
                    } else {
                        taskUserMask(0,5);
                    }
                    break;
                case R.id.cb_message_voice:
                    if (isChecked) {
                        ToastUtil.show(this, "选中");
                    } else {
                        ToastUtil.show(this, "取消选中");
                    }
                    break;
                case R.id.cb_message_shake:
                    if (isChecked) {
                        ToastUtil.show(this, "选中");
                    } else {
                        ToastUtil.show(this, "取消选中");
                    }
                    break;
            }
        }
    }


    private void taskUserMask(int switchval, int avatar) {
        userAction.userMaskSet(switchval, avatar, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(NewMessageActivity.this, response.body().getMsg());
            }
        });
    }

    private void taskUserInfo(long uid) {
        userAction.getUserInfo4Id(uid, new CallBack<ReturnBean<UserInfo>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
                if (response.body() == null) {
                    return;
                }
                UserInfo userInfo = response.body().getData();
                if (userInfo.getMessagenotice() == 0) {
                    mCbReceiveMessage.setChecked(false);
                } else {
                    mCbReceiveMessage.setChecked(true);
                }

                if (userInfo.getDisplaydetail() == 0) {
                    mCbMessageInfo.setChecked(false);
                } else {
                    mCbMessageInfo.setChecked(true);
                }
                isClick = 1;
            }
        });
    }


}
