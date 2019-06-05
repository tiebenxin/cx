package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.GroupJoinBean;
import com.yanlong.im.chat.bean.ReturnGroupInfoBean;
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

public class AddGroupActivity extends AppActivity {
    public static final String GID = "gid";
    private HeadView mHeadView;
    private SimpleDraweeView mSdGroupHead;
    private TextView mTvGroupName;
    private TextView mTvGroupNum;
    private Button mBtnAddGroup;
    private MsgAction msgAction;
    private String gid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        initView();
        initEvent();
        initData();
    }



    private void initView() {
        mHeadView =  findViewById(R.id.headView);
        mSdGroupHead =  findViewById(R.id.sd_group_head);
        mTvGroupName =  findViewById(R.id.tv_group_name);
        mTvGroupNum =  findViewById(R.id.tv_group_num);
        mBtnAddGroup =  findViewById(R.id.btn_add_group);
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
        mBtnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskAddGroup(gid);
            }
        });
    }


    private void initData() {
        msgAction = new MsgAction();
        gid = getIntent().getStringExtra(GID);
        taskGroupInfo(gid);
    }


    private void taskGroupInfo(String gid){
        msgAction.groupInfo(gid, new CallBack<ReturnBean<ReturnGroupInfoBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<ReturnGroupInfoBean>> call, Response<ReturnBean<ReturnGroupInfoBean>> response) {
                if(response.body().isOk()){
                    ReturnGroupInfoBean bean = response.body().getData();
                    mSdGroupHead.setImageURI(bean.getAvatar());
                    mTvGroupName.setText(bean.getName());
                    mTvGroupNum.setText(bean.getMembers().size()+"人");
                }else{
                    ToastUtil.show(AddGroupActivity.this,response.body().getMsg());
                }
            }
        });
    }


    private void taskAddGroup(final String gid){
        UserInfo userInfo = UserAction.getMyInfo();
        Long uid = userInfo.getUid();
        String name = userInfo.getName();
        new MsgAction().joinGroup(gid, uid,name,new CallBack<ReturnBean<GroupJoinBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<GroupJoinBean>> call, Response<ReturnBean<GroupJoinBean>> response) {
                if(response.body() == null){
                    ToastUtil.show(AddGroupActivity.this,"加群失败");
                    return;
                }
                ToastUtil.show(AddGroupActivity.this,response.body().getMsg());
                if(response.body().isOk()){
                    Intent intent = new Intent(AddGroupActivity.this,ChatActivity.class);
                    intent.putExtra(ChatActivity.AGM_TOGID,gid);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }



}
