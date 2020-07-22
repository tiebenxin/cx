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
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.ApplyBean;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventRefreshFriend;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @类名：邀请详情界面
 * @Date：2020/7/22
 * @by zjy
 * @备注：
 */
public class InviteDetailsActivity extends AppActivity {

    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private net.cb.cb.library.view.MultiListView mtListView;
    private TextView tvSubmit;//同意入群申请


    public static final String ALL_IDS = "ids";//邀请入群验证通知消息的全部id，从数据库找出此次申请入群用户

    private List<ApplyBean> listData;
    private List<String> ids;
    private MsgDao msgDao;
    private MsgAction msgAction;

    private int needRequestTimes = 0;//需要请求的次数 TODO 同意入群暂无批量接口
    private int realRequestTimes = 0;//实际请求的次数 TODO 同意入群暂无批量接口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_details);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        mtListView = findViewById(R.id.mt_listview);
        tvSubmit = findViewById(R.id.tv_submit);
    }

    private void initData() {
        msgDao = new MsgDao();
        msgAction = new MsgAction();
        listData = new ArrayList<>();
        if(msgDao.getApplysByUid(ids)!=null && msgDao.getApplysByUid(ids).size()>0){
            listData.addAll(msgDao.getApplysByUid(ids));
            needRequestTimes = listData.size();
        }
        mtListView.notifyDataSetChange();
    }

    private void initEvent() {
        ids = getIntent().getStringArrayListExtra(ALL_IDS);
        mtListView.init(new RecyclerViewAdapter());
        mtListView.getLayoutManager().setOrientation(LinearLayoutManager.HORIZONTAL);
        mtListView.getLoadView().setStateNormal();
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onRight() {

            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<listData.size();i++){
                    httpAgreeJoinGroup(listData.get(i));
                }
            }
        });
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_invite_details, view, false));
            return holder;
        }

        @Override
        public int getItemCount() {
            return listData == null ? 0 : listData.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(final RCViewHolder holder, int position) {
            ApplyBean bean = listData.get(position);
            if (CoreEnum.EChatType.GROUP == bean.getChatType()) {
                if(!TextUtils.isEmpty(bean.getNickname())){
                    holder.tvName.setText(bean.getNickname());
                }else {
                    holder.tvName.setText("");
                }
                if(!TextUtils.isEmpty(bean.getAvatar())){
                    Glide.with(context).load(bean.getAvatar())
                            .apply(GlideOptionsUtil.headImageOptions()).into(holder.ivIcon);
                }else {
                    Glide.with(context).load(R.mipmap.ic_info_head)
                            .apply(GlideOptionsUtil.headImageOptions()).into(holder.ivIcon);
                }
            }
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private ImageView ivIcon;
            private TextView tvName;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                ivIcon = convertView.findViewById(R.id.iv_icon);
                tvName = convertView.findViewById(R.id.tv_name);
            }

        }
    }

    /**
     * 发请求->同意入群申请
     * @param bean
     */
    private void httpAgreeJoinGroup(ApplyBean bean) {
        realRequestTimes++;
        msgAction.groupRequest(bean.getAid(), bean.getGid(), bean.getUid() + "", bean.getNickname(), bean.getAvatar(),
                bean.getJoinType(), bean.getInviter() + "", bean.getInviterName(), new CallBack<ReturnBean>() {
                    @Override
                    public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                        if (response.body().isOk()) {
                            bean.setStat(2);
                            msgDao.applyGroup(bean);
                            groupInfo(bean.getGid());
                        } else if (response.body().getCode() == 10005) {//已是群成员
                            bean.setStat(2);
                            msgDao.applyGroup(bean);
                            groupInfo(bean.getGid());
                        } else {
                            ToastUtil.show(getContext(), response.body().getMsg());
                        }
                        //请求完毕
                        if(realRequestTimes==needRequestTimes){
                            finish();
                        }
                    }
                });
    }

    /**
     * 刷新群信息
     * @param gid
     */
    private void groupInfo(String gid) {
        msgAction.groupInfo(gid, true, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (response.body().isOk()) {
                    /********通知更新sessionDetail************************************/
                    //因为msg对象 uid有两个，都得添加
                    List<String> gids = new ArrayList<>();
                    gids.add(gid);
                    //回主线程调用更新session详情
                    if(MyAppLication.INSTANCE().repository!=null)MyAppLication.INSTANCE().repository.updateSessionDetail(gids, null);
                    /********通知更新sessionDetail end************************************/
                }
            }
        });
    }


}
