package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hm.cxpay.dailog.CommonSelectDialog;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.ApplyBean;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgNotice;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @类名：撤销入群消息->批量移除用户
 * @Date：2020/9/11
 * @by zjy
 * @备注：
 */
public class InviteRemoveActivity extends AppActivity {

    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private RecyclerView rcView;
    private RecyclerViewAdapter adapter;
    private List<UserInfo> dataList;
    private List<ApplyBean> userList;
    private String gid;
    private CommonSelectDialog dialogOne;//是否撤销提示弹框
    private CommonSelectDialog.Builder builder;

    public static final String USER_LIST = "USER_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_remove);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        rcView = findViewById(R.id.rc_view);
        builder = new CommonSelectDialog.Builder(InviteRemoveActivity.this);
    }

    private void initEvent() {
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onRight() {

            }
        });
    }

    private void initData() {
        dataList = new Gson().fromJson(getIntent().getStringExtra(USER_LIST), new TypeToken<List<UserInfo>>() {}.getType());
        gid = getIntent().getStringExtra("gid");
        Group group = new MsgDao().groupNumberGet(gid);
        //
        if(group.getUsers()!=null && group.getUsers().size()>0){

        }
        if(dataList!=null && dataList.size()>0){
            List<String> idList = new ArrayList<>();
            for(UserInfo userInfo : dataList){
                idList.add(userInfo.getUid()+"");
            }
//            userList = new MsgDao().getApplysByUid
        }
        adapter = new RecyclerViewAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcView.setLayoutManager(layoutManager);
        rcView.setAdapter(adapter);
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_invite_remove, view, false));
            return holder;
        }

        @Override
        public int getItemCount() {
            return userList == null ? 0 : userList.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(final RCViewHolder holder, int position) {
            ApplyBean bean = userList.get(position);

            if(!TextUtils.isEmpty(bean.getAvatar())){
                Glide.with(context).load(bean.getAvatar())
                        .apply(GlideOptionsUtil.headImageOptions()).into(holder.ivIcon);
            }else {
                Glide.with(context).load(R.mipmap.ic_info_head)
                        .apply(GlideOptionsUtil.headImageOptions()).into(holder.ivIcon);
            }
            if(!TextUtils.isEmpty(bean.getNickname())){
                holder.tvName.setText(bean.getNickname());
            }else {
                holder.tvName.setText("");
            }
            //移除群成员
            holder.tvRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelInviteDialog(bean,position);
                }
            });

        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private ImageView ivIcon;
            private TextView tvName;
            private TextView tvRemove;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                ivIcon = convertView.findViewById(R.id.iv_icon);
                tvName = convertView.findViewById(R.id.tv_name);
                tvRemove = convertView.findViewById(R.id.tv_remove);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageManager.getInstance().notifyGroupChange(true);
    }

    private void cancelInviteDialog(ApplyBean bean,int position) {
        dialogOne = builder.setTitle("是否将该用户移出群聊？")
                .setLeftText("取消")
                .setRightText("移出群聊")
                .setLeftOnClickListener(v -> {
                    dialogOne.dismiss();
                })
                .setRightOnClickListener(v -> {
                    String name = "";
                    String rname = "";
                    if (!TextUtils.isEmpty(bean.getNickname())) {
                        name = bean.getNickname();
                    }
                    //撤销邀请
                    rname = "<font id='" + bean.getUid() + "'>" + bean.getNickname() + "</font>";
                    String finalName = rname;//被删除人的昵称
                    new MsgAction().httpCancelInvite(gid,name,bean.getUid(), new CallBack<ReturnBean>() {
                        @Override
                        public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                            if (response.body() == null) {
                                return;
                            } else {
                                if (response.body().isOk()) {
                                    String mid = SocketData.getUUID();
                                    MsgNotice note = new MsgNotice();
                                    note.setMsgid(mid);
                                    note.setMsgType(3);
                                    note.setNote("你将\"" + finalName + "\"移出群聊");
                                    new MsgDao().noteMsgAddRb(mid, UserAction.getMyId(), gid, note);
                                    dialogOne.dismiss();
                                    userList.remove(position);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ReturnBean> call, Throwable t) {
                            ToastUtil.show(t.getMessage());
                        }
                    });
                })
                .build();
        dialogOne.show();
    }

}
