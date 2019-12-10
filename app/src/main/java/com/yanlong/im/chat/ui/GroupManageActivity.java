package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;

import com.example.nim_lib.ui.BaseBindActivity;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.databinding.ActivityGroupManageBinding;
import com.yanlong.im.user.ui.GroupAddActivity;

import net.cb.cb.library.bean.EventGroupChange;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @version V1.0
 * @createAuthor （Shenxin）
 * @createDate 2019-7-9
 * @updateAuthor （Geoff）
 * @updateDate 2019-12-9
 * @description 群管理
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class GroupManageActivity extends BaseBindActivity<ActivityGroupManageBinding> implements View.OnClickListener {

    public static final String AGM_GID = "AGM_GID";
    public static final String PERCENTAGE = "percentage";
    private MsgAction msgAction;
    private boolean isPercentage;
    private String mGid;
    private Group mGinfo;

    @Override
    protected int setView() {
        return R.layout.activity_group_manage;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        msgAction = new MsgAction();
        mGid = getIntent().getStringExtra(AGM_GID);
        isPercentage = getIntent().getBooleanExtra(PERCENTAGE, false);

        if (!isPercentage) {
            bindingView.viewGroupAdd.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initEvent() {
        bindingView.viewGroupAdd.setOnClickListener(this);
        bindingView.viewGroupTransfer.setOnClickListener(this);
        bindingView.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

        bindingView.ckGroupVerif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                taskSetState(mGid, null, null, null, isChecked ? 1 : 0);
            }
        });
        //TODO 群成员相互加好友
        bindingView.ckGroupIntimately.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                taskSetIntimatelyState(mGid, isChecked ? 1 : 0);
            }
        });
    }

    @Override
    protected void loadData() {
        initData();
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
        msgAction.groupInfo(mGid, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (response.body().isOk()) {
                    mGinfo = response.body().getData();
                    //群机器人
                    String rname = mGinfo.getRobotname();
                    rname = StringUtil.isNotNull(rname) ? rname : "未配置";
                    bindingView.txtGroupRobot.setText(rname);
                    bindingView.viewGroupRobot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getContext(), GroupRobotActivity.class)
                                    .putExtra(GroupRobotActivity.AGM_GID, mGinfo.getGid())
                                    .putExtra(GroupRobotActivity.AGM_RID, mGinfo.getRobotid()));
                        }
                    });
                    //群验证
                    bindingView.ckGroupVerif.setChecked(mGinfo.getNeedVerification() == 1);
                    bindingView.ckGroupIntimately.setChecked(mGinfo.getContactIntimately() == 1);
                    initEvent();
                }
            }
        });
    }

    private void initData() {
        taskGetInfo();
    }

    private void taskGetInfo() {
        msgAction.groupInfo4Db(mGid, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (response.body().isOk()) {
                    mGinfo = response.body().getData();
                    //群机器人
                    String rname = mGinfo.getRobotname();
                    rname = StringUtil.isNotNull(rname) ? rname : "未配置";
                    bindingView.txtGroupRobot.setText(rname);
                    bindingView.viewGroupRobot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getContext(), GroupRobotActivity.class)
                                    .putExtra(GroupRobotActivity.AGM_GID, mGinfo.getGid())
                                    .putExtra(GroupRobotActivity.AGM_RID, mGinfo.getRobotid()));
                        }
                    });
                    //群验证
                    bindingView.ckGroupVerif.setChecked(mGinfo.getNeedVerification() == 1);
                    bindingView.ckGroupIntimately.setChecked(mGinfo.getContactIntimately() == 1);
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
                changeMaster(mGid, uid, membername);
            }

        }

    }

    @Override
    public void onClick(View v) {
        if (ViewUtils.isFastDoubleClick()) {
            return;
        }
        Intent intent;
        switch (v.getId()) {
            case R.id.view_group_transfer:// 群主管理权转让
                intent = new Intent(GroupManageActivity.this, GroupSelectUserActivity.class);
                intent.putExtra(GroupSelectUserActivity.GID, mGid);
                startActivityForResult(intent, GroupSelectUserActivity.RET_CODE_SELECTUSR);
                break;
            case R.id.view_group_add:// 增加群人数上限至1000人
                intent = new Intent(GroupManageActivity.this, GroupAddActivity.class).putExtra("gid", mGid);
                startActivity(intent);
                break;
        }
    }
}
