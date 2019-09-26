package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.chat.dao.MsgDao;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/***
 * 保存的群聊
 */
public class GroupSaveActivity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private net.cb.cb.library.view.MultiListView mtListView;
    private List<Group> groupInfoBeans;
    MsgDao msgDao = new MsgDao();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_save);
        findViews();
        initEvent();
        initData();
    }

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

        mtListView.getLoadView().setStateNormal();
    }

    private void initData() {
        groupInfoBeans = new ArrayList<>();
        mtListView.init(new RecyclerViewAdapter());
        taskMySaved();
    }


    private void taskMySaved() {
        new MsgAction().getMySaved(new CallBack<ReturnBean<List<Group>>>(mtListView) {
            @Override
            public void onResponse(Call<ReturnBean<List<Group>>> call, Response<ReturnBean<List<Group>>> response) {
                if (response.body() == null || !response.body().isOk()) {
                    mtListView.getLoadView().setStateNoData(R.mipmap.ic_nodate);
                    return;
                }
                groupInfoBeans.addAll(response.body().getData());
                mtListView.notifyDataSetChange(response);
            }
        });
    }


    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        @Override
        public int getItemCount() {
            return null == groupInfoBeans ? 0 : groupInfoBeans.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder holder, int position) {
            final Group groupInfoBean = groupInfoBeans.get(position);
            //holder.imgHead.setImageURI(groupInfoBean.getAvatar() + "");
            holder.txtName.setText(/*groupInfoBean.getName()*/msgDao.getGroupName(groupInfoBean.getGid()));
           // holder.imgHead.setImageURI(groupInfoBean.getAvatar() + "");
            String imageHead= groupInfoBean.getAvatar();
            if (imageHead!=null&&!imageHead.isEmpty()&& StringUtil.isNotNull(imageHead)){
                Glide.with(context).load(imageHead)
                        .apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);
            }else{
                    String url= msgDao.groupHeadImgGet(groupInfoBean.getGid());
                    Glide.with(context).load(url)
                        .apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);
            }
           // holder.txtName.setText(groupInfoBean.getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  star(ChatActivity.class);
                    startActivity(new Intent(getContext(), ChatActivity.class)
                            .putExtra(ChatActivity.AGM_TOGID, groupInfoBean.getGid())
                    );
                }
            });


            if (getItemCount() == (position + 1)) {
                holder.txtNum.setText(getItemCount() + "个群聊");
                holder.txtNum.setVisibility(View.VISIBLE);
            } else {
                holder.txtNum.setVisibility(View.GONE);
            }
        }


        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_group_save, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private ImageView imgHead;
            private TextView txtName;
            private TextView txtNum;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                imgHead = convertView.findViewById(R.id.img_head);
                txtName = convertView.findViewById(R.id.txt_name);
                txtNum = convertView.findViewById(R.id.txt_num);
            }

        }
    }

}
