package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.GroupAccept;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.bean.EventRefreshFriend;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/***
 * 新的朋友
 */
public class FriendApplyAcitvity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private net.cb.cb.library.view.MultiListView mtListView;
    private List listData;


    //自动寻找控件
    private void findViews() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        mtListView = findViewById(R.id.mtListView);
    }


    //自动生成的控件事件
    private void initEvent() {
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

        mtListView.init(new RecyclerViewAdapter());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_apply);
        findViews();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        taskGetList();
    }

    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {


        @Override
        public int getItemCount() {
            return listData == null ? 0 : listData.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(final RCViewHolder holder, int position) {

            if (listData.get(position) instanceof UserInfo) {
                final UserInfo bean = (UserInfo) listData.get(position);
                holder.txtName.setText(bean.getName4Show());
                holder.imgHead.setImageURI(bean.getHead());

                if (TextUtils.isEmpty(bean.getSayHi())) {
                    holder.txtInfo.setText("想加你为好友");
                } else {
                    holder.txtInfo.setText(bean.getSayHi());
                }

                holder.btnComit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //  ToastUtil.show(context, "准了");
                        taskFriendAgree(bean.getUid());
                    }
                });

                holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FriendApplyAcitvity.this, UserInfoActivity.class);
                        intent.putExtra(UserInfoActivity.ID, bean.getUid());
                        intent.putExtra(UserInfoActivity.SAY_HI, bean.getSayHi());
                        intent.putExtra(UserInfoActivity.IS_APPLY, 1);
                        startActivity(intent);
                    }
                });
                holder.mBtnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.mSwipeLayout.quickClose();
                        taskDelRequestFriend(bean.getUid());
                    }
                });


            } else if (listData.get(position) instanceof GroupAccept) {
                final GroupAccept bean = (GroupAccept) listData.get(position);
                holder.txtName.setText(bean.getUname());
                holder.imgHead.setImageURI(bean.getHead());

                holder.txtInfo.setText("申请进群:" + bean.getGroupName());


                holder.btnComit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //  ToastUtil.show(context, "准了");
                        taskRequest(bean);
                    }
                });
                holder.mBtnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.mSwipeLayout.quickClose();
                        taskGroupRequestDelect(bean);
                    }
                });
            }

            //  holder.txtState.setText("已添加");

            holder.btnComit.setVisibility(View.VISIBLE);
            holder.txtState.setVisibility(View.GONE);

        }


        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_friend_apply, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private SimpleDraweeView imgHead;
            private TextView txtName;
            private TextView txtInfo;
            private TextView txtState;
            private Button btnComit;
            private SwipeMenuLayout mSwipeLayout;
            private Button mBtnDel;
            private LinearLayout mLayoutItem;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                mLayoutItem = convertView.findViewById(R.id.layout_item);
                imgHead = convertView.findViewById(R.id.img_head);
                txtName = convertView.findViewById(R.id.txt_name);
                txtInfo = convertView.findViewById(R.id.txt_info);
                txtState = convertView.findViewById(R.id.txt_state);
                btnComit = convertView.findViewById(R.id.btn_comit);
                mSwipeLayout = convertView.findViewById(R.id.swipeLayout);
                mBtnDel = convertView.findViewById(R.id.btn_del);
            }

        }
    }

    private UserAction userAction = new UserAction();
    private MsgAction msgAction = new MsgAction();
    private MsgDao msgDao = new MsgDao();

    private void taskGetList() {


        userAction.friendGet4Apply(new CallBack<ReturnBean<List<UserInfo>>>(mtListView) {
            @Override
            public void onResponse(Call<ReturnBean<List<UserInfo>>> call, Response<ReturnBean<List<UserInfo>>> response) {
                if (response.body() == null || !response.body().isOk()) {
                    return;
                }
                //群申请
                listData = msgDao.groupAccept();
                listData.addAll(response.body().getData());
                mtListView.notifyDataSetChange(response);
            }
        });
    }

    private void taskGroupRequestDelect(GroupAccept bean) {
        msgAction.groupRequestDelect(bean.getAid());
        taskGetList();
    }

    private void taskDelRequestFriend(Long uid) {
        userAction.delRequestFriend(uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(getContext(), response.body().getMsg());
                if (response.body().isOk()) {
                    taskGetList();
                }

            }
        });
    }

    private void taskRequest(GroupAccept accept) {
        msgAction.groupRequest(accept.getAid(), accept.getGid(), accept.getUid() + "", accept.getUname(),accept.getHead(),
                accept.getJoinType(), accept.getInviter() + "",accept.getInviterName(),new CallBack<ReturnBean>() {
                    @Override
                    public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                        if (response.body().isOk()) {
                            taskGetList();

                        }
                        ToastUtil.show(getContext(), response.body().getMsg());
                    }
                });
    }


    private void taskFriendAgree(Long uid) {
        userAction.friendAgree(uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }

                ToastUtil.show(getContext(), response.body().getMsg());
                if (response.body().isOk()) {
                    EventBus.getDefault().post(new EventRefreshFriend());
                    taskGetList();
                } else {
                    // ToastUtil.show(getContext(),response.body().getMsg());
                }
            }
        });
    }


}
