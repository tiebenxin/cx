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
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.ui.GroupAddActivity;

import net.cb.cb.library.bean.EventGroupChange;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Response;

public class GroupManageActivity extends AppActivity {

    public static final String AGM_GID = "AGM_GID";
    public static final String PERCENTAGE = "percentage";
    private HeadView mHeadView;
    private LinearLayout mViewGroupTransfer, view_group_add;
    private LinearLayout viewGroupRobot;
    private TextView txtGroupRobot;
    private CheckBox mCkGroupVerif;
    private CheckBox mCkGroupIntimately;
    private MsgAction msgAction;
    private String gid;
    private Group ginfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_manage);
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    private boolean isPercentage;

    private void initView() {

        msgAction = new MsgAction();
        gid = getIntent().getStringExtra(AGM_GID);
        mHeadView = findViewById(R.id.headView);
        mViewGroupTransfer = findViewById(R.id.view_group_transfer);
        view_group_add = findViewById(R.id.view_group_add);
        mCkGroupVerif = findViewById(R.id.ck_group_verif);
        mCkGroupIntimately = findViewById(R.id.ck_group_intimately);
        viewGroupRobot = findViewById(R.id.view_group_robot);
        txtGroupRobot = findViewById(R.id.txt_group_robot);
        isPercentage = getIntent().getBooleanExtra(PERCENTAGE, false);
        if (!isPercentage) {
            view_group_add.setVisibility(View.GONE);
        }
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

        mCkGroupVerif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                taskSetState(gid, null, null, null, isChecked ? 1 : 0);
            }
        });
        //TODO 群成员相互加好友
        mCkGroupIntimately.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                taskSetIntimatelyState(gid, isChecked ? 1 : 0);
            }
        });

        mViewGroupTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupManageActivity.this, GroupSelectUserActivity.class);
                intent.putExtra(GroupSelectUserActivity.GID, gid);
                startActivityForResult(intent, GroupSelectUserActivity.RET_CODE_SELECTUSR);
            }
        });
        view_group_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupManageActivity.this, GroupAddActivity.class).putExtra("gid", gid);
                startActivity(intent);
            }
        });


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshChat(EventGroupChange event) {
        if (event.isNeedLoad()) {
            taskGetInfoNetwork();
        } else {
            taskGetInfo();
        }
    }

    private void taskGetInfoNetwork() {
        msgAction.groupInfo(gid, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (response.body().isOk()) {
                    ginfo = response.body().getData();
                    //群机器人
                    String rname = ginfo.getRobotname();
                    rname = StringUtil.isNotNull(rname) ? rname : "未配置";
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
                    mCkGroupIntimately.setChecked(ginfo.getContactIntimately() == 1);
                    initEvent();
                }
            }
        });
    }


    private void initData() {
        taskGetInfo();
    }

    private void taskGetInfo() {
        msgAction.groupInfo4Db(gid, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (response.body().isOk()) {
                    ginfo = response.body().getData();
                    //群机器人
                    String rname = ginfo.getRobotname();
                    rname = StringUtil.isNotNull(rname) ? rname : "未配置";
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
                    mCkGroupIntimately.setChecked(ginfo.getContactIntimately() == 1);
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

    private void taskSetIntimatelyState(String gid, int i) {
        msgAction.groupSwitchIntimately(gid, i, new CallBack<ReturnBean>() {
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


    private void changeMaster(String gid, String uid, String membername) {
        msgAction.changeMaster(gid, uid, membername, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    ToastUtil.show(context, "转让失败");
                    return;
                }
                ToastUtil.show(context, response.body().getMsg());
                if (response.body().isOk()) {
                    MessageManager.getInstance().notifyGroupChange(true);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GroupSelectUserActivity.RET_CODE_SELECTUSR) {
            if (data == null)
                return;
            String uid = data.getStringExtra(GroupSelectUserActivity.UID);
            if (StringUtil.isNotNull(uid)) {
                String membername = data.getStringExtra(GroupSelectUserActivity.MEMBERNAME);
                changeMaster(gid, uid, membername);
            }

        }

    }
}
