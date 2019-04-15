package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.yanlong.im.R;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

public class NewMessageActivity extends AppActivity implements CompoundButton.OnCheckedChangeListener {

    private HeadView mHeadView;
    private CheckBox mCbReceiveMessage;
    private CheckBox mCbMessageInfo;
    private CheckBox mCbMessageVoice;
    private CheckBox mCbMessageShake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        initView();
        initEvent();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mCbReceiveMessage = findViewById(R.id.cb_receive_message);
        mCbMessageInfo = findViewById(R.id.cb_message_info);
        mCbMessageVoice = findViewById(R.id.cb_message_voice);
        mCbMessageShake = findViewById(R.id.cb_message_shake);
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
        mCbReceiveMessage.setOnCheckedChangeListener(this);
        mCbMessageInfo.setOnCheckedChangeListener(this);
        mCbMessageVoice.setOnCheckedChangeListener(this);
        mCbMessageShake.setOnCheckedChangeListener(this);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.cb_receive_message:
                if(isChecked){
                    ToastUtil.show(this,"选中");
                }else{
                    ToastUtil.show(this,"取消选中");
                }
                break;
            case R.id.cb_message_info:
                if(isChecked){
                    ToastUtil.show(this,"选中");
                }else{
                    ToastUtil.show(this,"取消选中");
                }
                break;
            case R.id.cb_message_voice:
                if(isChecked){
                    ToastUtil.show(this,"选中");
                }else{
                    ToastUtil.show(this,"取消选中");
                }
                break;
            case R.id.cb_message_shake:
                if(isChecked){
                    ToastUtil.show(this,"选中");
                }else{
                    ToastUtil.show(this,"取消选中");
                }
                break;
        }
    }
}
