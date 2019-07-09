package com.yanlong.im.chat.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import retrofit2.Call;
import retrofit2.Response;

public class GroupManageActivity extends AppActivity {


    private HeadView mHeadView;
    private LinearLayout mViewGroupTransfer;
    private LinearLayout viewGroupRobot;
    private TextView txtGroupRobot;
    private CheckBox mCkGroupVerif;
    private MsgAction msgAction;
    private String gid;
    private Group ginfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_manage);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        msgAction = new MsgAction();
        gid = getIntent().getStringExtra("gid");
        mHeadView = findViewById(R.id.headView);
        mViewGroupTransfer = findViewById(R.id.view_group_transfer);
        mCkGroupVerif = findViewById(R.id.ck_group_verif);
        viewGroupRobot = findViewById(R.id.view_group_robot);
        txtGroupRobot = findViewById(R.id.txt_group_robot);
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

        mCkGroupVerif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                taskSetState(gid, null, null, null, isChecked ? 1 : 0);
            }
        });

        mViewGroupTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    private void initData(){
        taskGetInfo();
    }

    private void taskGetInfo() {
        msgAction.groupInfo(gid, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (response.body().isOk()) {
                    ginfo = response.body().getData();


                }
            }
        });
    }



    private void taskSetState(String gid, Integer isTop, Integer notNotify, Integer saved, Integer needVerification) {
        msgAction.groupSwitch(gid, isTop, notNotify, saved, needVerification, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {

                if (response.body() == null)
                    return;
                ToastUtil.show(getContext(), response.body().getMsg());
                if (response.body().isOk()) {
                    initEvent();
                }

            }
        });
    }




}
