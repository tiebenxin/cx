package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.ApplyBean;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
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
    private TextView tvSubmit;//同意入群申请按钮
    private TextView tvInviteName;//邀请人的昵称
    private TextView tvContent;//入群备注内容


    public static final String ALL_INVITE_IDS = "IDS";//邀请入群验证通知消息的全部id，从数据库找出此次申请入群用户
    public static final String REMARK = "REMARK";//邀请入群备注
    public static final String MSG_ID = "MSG_ID";//消息id
    public static final String CONFIRM_STATE = "CONFIRM_STATE";//确认状态(true 去确认/ false 已确认)

    private List<ApplyBean> listData;
    private List<String> ids;
    private MsgDao msgDao;
    private MsgAction msgAction;
    private boolean hadAgree = false;//是否已经同意入群(是否不再申请入群列表中)，若已经同意则不需再调接口

    private int needRequestTimes = 0;//需要请求的次数 TODO 批准同意入群暂无批量接口
    private int realRequestTimes = 0;//实际请求的次数
    private String remark;//备注内容
    private String msgId;//消息id
    private boolean confirmState;//确认状态(true 去确认/ false 已确认)

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
        tvInviteName = findViewById(R.id.tv_invite_name);
        tvContent = findViewById(R.id.tv_content);
    }

    private void initData() {
        if(!TextUtils.isEmpty(remark)){
            tvContent.setText(remark);
        }else {
            tvContent.setText("无");
        }
        msgDao = new MsgDao();
        msgAction = new MsgAction();
        listData = new ArrayList<>();
        //默认情况，从申请入群列表查找用户信息
        if(msgDao.getApplysByUid(ids,1)!=null && msgDao.getApplysByUid(ids,1).size()>0){
            listData.addAll(msgDao.getApplysByUid(ids,1));
            needRequestTimes = listData.size();
            hadAgree = false;
        }else {
            //若申请入群列表不存在用户信息，可能是已经同意，此时需要查最近同意申请入群的用户信息，因为如果有多条邀请入群申请，可以重复点"去确认"跳到此界面，需要展示
            if(msgDao.getApplysByUid(ids,2)!=null && msgDao.getApplysByUid(ids,2).size()>0){
                listData.addAll(msgDao.getApplysByUid(ids,2));
                hadAgree = true;
            }
        }
        //显示邀请人的信息，每个申请人信息中含有邀请人的id和昵称
        if(listData!=null && listData.size()>0){
            if(!TextUtils.isEmpty(listData.get(0).getInviterName())){
                tvInviteName.setText("\""+listData.get(0).getInviterName()+"\"");
            }else {
                tvInviteName.setText("\"未知用户\"");
            }
        }
        mtListView.notifyDataSetChange();
    }

    private void initEvent() {
        ids = getIntent().getStringArrayListExtra(ALL_INVITE_IDS);
        remark = getIntent().getStringExtra(REMARK);
        msgId = getIntent().getStringExtra(MSG_ID);
        confirmState = getIntent().getBooleanExtra(CONFIRM_STATE,false);
        if(confirmState){
            tvSubmit.setBackgroundResource(R.drawable.shape_5radius_solid_32b053);
        }else {
            tvSubmit.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
        }
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
                if(confirmState){ //去确认，按逻辑走
                    if(hadAgree){
                        finish();
                    }else {
                        for(int i=0;i<listData.size();i++){
                            httpAgreeJoinGroup(listData.get(i));
                        }
                    }
                }else { //如果是已确认，仍然允许点击，直接finish
                    finish();
                }
            }
        });
        //邀请人详细信息
        tvInviteName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listData!=null && listData.size()>0){
                    goToUserInfoActivity(listData.get(0).getInviter(),listData.get(0).getGid(),true);
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
                //点击子项跳被邀请用户的信息详情
                holder.layoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToUserInfoActivity(bean.getUid(),bean.getGid(),true);
                    }
                });
            }
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private ImageView ivIcon;
            private TextView tvName;
            private LinearLayout layoutItem;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                ivIcon = convertView.findViewById(R.id.iv_icon);
                tvName = convertView.findViewById(R.id.tv_name);
                layoutItem = convertView.findViewById(R.id.layout_item);
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
                            //TODO 新增->群主或管理员允许通过验证后，需要android端本地通知消息给自己，A邀请了B入群，与IOS一致
                            SocketData.invitePersonLocalNotice(bean.getGid(),bean.getInviter(),bean.getInviterName(),bean.getUid(),bean.getNickname());
                        } else if (response.body().getCode() == 10005) {//已是群成员
                            bean.setStat(2);
                            msgDao.applyGroup(bean);
                            groupInfo(bean.getGid());
                            ToastUtil.show(getContext(), bean.getNickname()+"已经是本群成员");
                        } else {
                            ToastUtil.show(getContext(), response.body().getMsg());
                        }
                        //请求完毕，通知群信息刷新
                        if(realRequestTimes==needRequestTimes){
                            if(!TextUtils.isEmpty(msgId)){
                                msgDao.updateInviteNoticeMsg(msgId);//数据库先更新，入群通知消息改为"已确认"
                                EventFactory.UpdateOneMsgEvent event = new EventFactory.UpdateOneMsgEvent();//通知刷新聊天界面
                                event.setMsgId(msgId);
                                EventBus.getDefault().post(event);
                            }
                            MessageManager.getInstance().notifyGroupChange(true);
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


    /**
     * 跳转到用户信息
     * @param id
     * @param gid
     * @param isGroup
     */
    private void goToUserInfoActivity(Long id, String gid, boolean isGroup) {
        if (ViewUtils.isFastDoubleClick()) {
            return;
        }
        context.startActivity(new Intent(context, UserInfoActivity.class)
                .putExtra(UserInfoActivity.ID, id)
                .putExtra(UserInfoActivity.JION_TYPE_SHOW, 1)
                .putExtra(UserInfoActivity.GID, gid)
                .putExtra(UserInfoActivity.IS_GROUP, isGroup));
    }

}
