package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yanlong.im.R;
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
    private List<UserInfo> listData;


    //自动寻找控件
    private void findViews() {
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        mtListView = (net.cb.cb.library.view.MultiListView) findViewById(R.id.mtListView);
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
        inttData();
    }

    private void inttData() {
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
        public void onBindViewHolder(RCViewHolder holder, int position) {
            final UserInfo bean = listData.get(position);
            holder.txtName.setText(bean.getName4Show());
            holder.imgHead.setImageURI(bean.getHead());

            holder.txtInfo.setText("想加你为好友");


            holder.btnComit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //  ToastUtil.show(context, "准了");
                    taskFriendAgree(bean.getUid());
                }
            });
            //  holder.txtState.setText("已添加");
            if (true) {
                holder.btnComit.setVisibility(View.VISIBLE);
                holder.txtState.setVisibility(View.GONE);
            } else {
                holder.btnComit.setVisibility(View.GONE);
                holder.txtState.setVisibility(View.VISIBLE);
            }

        }


        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_friend_apply, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private com.facebook.drawee.view.SimpleDraweeView imgHead;
            private TextView txtName;
            private TextView txtInfo;
            private TextView txtState;
            private Button btnComit;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                imgHead = (com.facebook.drawee.view.SimpleDraweeView) convertView.findViewById(R.id.img_head);
                txtName = (TextView) convertView.findViewById(R.id.txt_name);
                txtInfo = (TextView) convertView.findViewById(R.id.txt_info);
                txtState = (TextView) convertView.findViewById(R.id.txt_state);
                btnComit = (Button) convertView.findViewById(R.id.btn_comit);
            }

        }
    }

    private UserAction userAction = new UserAction();

    private void taskGetList() {
        userAction.friendGet4Apply(new CallBack<ReturnBean<List<UserInfo>>>(mtListView) {
            @Override
            public void onResponse(Call<ReturnBean<List<UserInfo>>> call, Response<ReturnBean<List<UserInfo>>> response) {
                if (response.body() == null || !response.body().isOk()) {
                    return;
                }
                listData = response.body().getData();
                mtListView.notifyDataSetChange(response);
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
