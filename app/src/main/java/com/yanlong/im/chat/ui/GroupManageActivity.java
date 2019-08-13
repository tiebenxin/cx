package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.user.ui.CommonSetingActivity;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import retrofit2.Call;
import retrofit2.Response;

public class GroupManageActivity extends AppActivity {

    public static final String AGM_GID = "AGM_GID";
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
        initData();
    }

    private void initView() {
        msgAction = new MsgAction();
        gid = getIntent().getStringExtra(AGM_GID);
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
                Intent intent = new Intent(GroupManageActivity.this,GroupSelectUserActivity.class);
                intent.putExtra(GroupSelectUserActivity.GID,gid);
                startActivityForResult(intent,GroupSelectUserActivity.RET_CODE_SELECTUSR);
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
                    //群机器人
                    String rname=ginfo.getRobotname();
                    rname= StringUtil.isNotNull(rname)?rname:"未配置";
                    txtGroupRobot.setText(rname);
                    viewGroupRobot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getContext(), GroupRobotActivity.class)
                                    .putExtra(GroupRobotActivity.AGM_GID, ginfo.getGid())
                                    .putExtra(GroupRobotActivity.AGM_RID, ginfo.getRobotid()));
                        }
                    });
                    //群验证
                    mCkGroupVerif.setChecked(ginfo.getNeedVerification() == 1);
                    initEvent();
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


    private void changeMaster(String gid,String uid,String membername){
        msgAction.changeMaster(gid, uid,membername, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if(response.body() == null){
                    ToastUtil.show(context,"转让失败");
                    return;
                }
                ToastUtil.show(context,response.body().getMsg());
                if(response.body().isOk()){
                    finish();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GroupSelectUserActivity.RET_CODE_SELECTUSR){
            if(data==null)
                return;
            String uid = data.getStringExtra(GroupSelectUserActivity.UID);
            if(StringUtil.isNotNull(uid)){
                String membername = data.getStringExtra(GroupSelectUserActivity.MEMBERNAME);
                changeMaster(gid,uid,membername);
            }

        }

    }
}
