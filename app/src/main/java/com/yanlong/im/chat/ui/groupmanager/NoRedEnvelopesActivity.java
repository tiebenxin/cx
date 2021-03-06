package com.yanlong.im.chat.ui.groupmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nim_lib.config.Preferences;
import com.example.nim_lib.ui.BaseBindActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.NoRedEnvelopesBean;
import com.yanlong.im.chat.eventbus.EventRefreshGroup;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.ui.GroupSelectUserActivity;
import com.yanlong.im.databinding.ActivityNoredEnvelopesBinding;
import com.yanlong.im.databinding.ItemNoredEnvelopesBinding;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.bean.EventGroupChange;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.IntentUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.AlertYesNo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2019-12-10
 * @updateAuthor
 * @updateDate
 * @description 禁止领取零钱红包
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class NoRedEnvelopesActivity extends BaseBindActivity<ActivityNoredEnvelopesBinding> {

    public static final int REUQEST_CODE_200 = 200;

    private CommonRecyclerViewAdapter<UserInfo, ItemNoredEnvelopesBinding> mViewAdapter;
    private List<UserInfo> mList = new ArrayList<>();
    private List<Long> mUsers = new ArrayList<>();
    private String mGid;
    private MsgAction mMsgAction;
    private Gson mGosn = new Gson();
    private Group mGroupInfo;

    @Override
    protected int setView() {
        return R.layout.activity_nored_envelopes;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mViewAdapter = new CommonRecyclerViewAdapter<UserInfo, ItemNoredEnvelopesBinding>(this, R.layout.item_nored_envelopes) {

            @Override
            public void bind(ItemNoredEnvelopesBinding binding, UserInfo userInfo,
                             int position, RecyclerView.ViewHolder viewHolder) {
                if (position == mList.size() - 1) {
                    binding.viewLine.setVisibility(View.GONE);
                } else {
                    binding.viewLine.setVisibility(View.VISIBLE);
                }
                Glide.with(NoRedEnvelopesActivity.this).load(userInfo.getHead())
                        .apply(GlideOptionsUtil.headImageOptions()).into(binding.imgHead);
                binding.txtName.setText(userInfo.getName4Show());
                binding.txtCancleManager.setOnClickListener(o -> {
                    if (ViewUtils.isFastDoubleClick()) {
                        return;
                    }
                    if (isAdministrators(UserAction.getMyId()) && isAdministrators(mList.get(position).getUid())) {
                        ToastUtil.showCenter(context, "暂无权限操作");
                    } else {
                        showDialog(userInfo.getName4Show(), position);
                    }
                });
            }
        };

        bindingView.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bindingView.recyclerView.setAdapter(mViewAdapter);

        mViewAdapter.setData(mList);
    }

    @Override
    protected void initEvent() {
        setActionBarLeft(bindingView.headView);
        bindingView.txtSetupMember.setOnClickListener(o -> {
            if (ViewUtils.isFastDoubleClick()) {
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt(GroupSelectUserActivity.TYPE, 3);
            bundle.putString(GroupSelectUserActivity.GID, mGid);
            mUsers.clear();
            if (mList != null && mList.size() > 0) {
                for (UserInfo userInfo : mList) {
                    if (userInfo.getuType() != 1) {
                        mUsers.add(userInfo.getUid());
                    }
                }
                bundle.putString(GroupSelectUserActivity.UIDS, mGosn.toJson(mUsers));
            }
            IntentUtil.gotoActivityForResult(NoRedEnvelopesActivity.this, GroupSelectUserActivity.class, bundle, REUQEST_CODE_200);
        });
    }

    @Override
    protected void loadData() {
        mGid = getIntent().getStringExtra(GroupSelectUserActivity.GID);
        mMsgAction = new MsgAction();
        getCantOpenUpRedMembers();
        taskGroupInfo(mGid);
    }

    private void showDialog(String name, final int position) {
        AlertYesNo dialog = new AlertYesNo();
        dialog.init(this, getString(R.string.dialog_message_title), "确定要移除\"" + name + "\"领取零钱红包", getString(R.string.dialog_btn_confrim),
                getString(R.string.dialog_btn_cancle), new AlertYesNo.Event() {
                    @Override
                    public void onON() {

                    }

                    @Override
                    public void onYes() {
                        mUsers.clear();
                        mUsers.add(mList.get(position).getUid());
                        toggleOpenUpRedEnvelope(mUsers, null, mList.get(position), false);
                    }
                });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REUQEST_CODE_200) {
                String value = data.getStringExtra(Preferences.DATA);
                List<UserInfo> list = new Gson().fromJson(value, new TypeToken<List<UserInfo>>() {
                }.getType());

                if (list != null && list.size() > 0) {
                    mUsers.clear();
                    for (UserInfo userInfo : list) {
                        mUsers.add(userInfo.getUid());
                    }
                    toggleOpenUpRedEnvelope(mUsers, list, null, true);
                }
            }
        }
    }

    /**
     * 开关群成员禁领红包
     *
     * @param uidList  用户集合
     * @param list     添加用户列表
     * @param userInfo 移除用户
     * @param isAdd    增加还是移除
     */
    private void toggleOpenUpRedEnvelope(List<Long> uidList, List<UserInfo> list, UserInfo userInfo, boolean isAdd) {

        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("uidList", uidList);
        params.put("gid", mGid);
        params.put("ops", isAdd ? 1 : -1);
        mMsgAction.toggleOpenUpRedEnvelope(params, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() != null && response.body().isOk()) {
                    if (isAdd) {
                        mList.addAll(list);
                        // 本地创建一个通知
                        SocketData.createMsgRedEnvelopesOfNotice(mGid, isAdd, list);
                    } else {
                        // 本地创建一个通知
                        List<UserInfo> userInfos = new ArrayList<>();
                        userInfos.add(userInfo);
                        SocketData.createMsgRedEnvelopesOfNotice(mGid, isAdd, userInfos);
                        mList.remove(userInfo);
                    }
                    // 移除自己更新群信息
                    MessageManager.getInstance().notifyGroupChange(true);
                    mViewAdapter.notifyDataSetChanged();
                } else {
                    if (isAdd) {
                        ToastUtil.show(NoRedEnvelopesActivity.this, "禁止领取零钱红包失败");
                    } else {
                        ToastUtil.show(NoRedEnvelopesActivity.this, "移除失败");
                    }
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    /**
     * 获取禁领红包群成员列表
     */
    private void getCantOpenUpRedMembers() {
        mMsgAction.getCantOpenUpRedMembers(mGid, new CallBack<ReturnBean<List<NoRedEnvelopesBean>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<NoRedEnvelopesBean>>> call, Response<ReturnBean<List<NoRedEnvelopesBean>>> response) {
                super.onResponse(call, response);
                if (response.body() != null && response.body().isOk()) {
                    List<NoRedEnvelopesBean> list = response.body().getData();
                    if (list != null) {
                        parseMemberUser(list);
                    }
                } else {
                    ToastUtil.show(NoRedEnvelopesActivity.this, "获取禁领红包群成员列表失败");
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<NoRedEnvelopesBean>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    /**
     * 获取群信息
     *
     * @param gid
     */
    private void taskGroupInfo(String gid) {
        mMsgAction.groupInfo(gid, true, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (response.body().isOk()) {
                    mGroupInfo = response.body().getData();
                } else {
                    ToastUtil.show(NoRedEnvelopesActivity.this, response.body().getMsg());
                }
            }
        });
    }

    /**
     * 判断是否是管理员
     *
     * @return
     */
    private boolean isAdministrators(Long uid) {
        boolean isManager = false;
        if (mGroupInfo != null && mGroupInfo.getViceAdmins() != null && mGroupInfo.getViceAdmins().size() > 0) {
            for (Long user : mGroupInfo.getViceAdmins()) {
                if (user.equals(uid)) {
                    isManager = true;
                    break;
                }
            }
        }
        return isManager;
    }

    /**
     * 实体类转换
     *
     * @return
     */
    private void parseMemberUser(List<NoRedEnvelopesBean> listMember) {
        mList.clear();
        for (NoRedEnvelopesBean bean : listMember) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUid(bean.getUid());
            userInfo.setHead(bean.getAvatar());
            userInfo.setName(bean.getNickname());
            mList.add(userInfo);
        }
        mViewAdapter.notifyDataSetChanged();
    }

    /**
     * 更新列表
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshGroup(EventRefreshGroup event) {
        if (event != null && event.getGid().equals(mGid)) {
            getCantOpenUpRedMembers();
        }

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
