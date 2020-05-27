package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.wight.avatar.MultiImageView;

import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/***
 * 保存的群聊
 */
public class GroupSaveActivity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private net.cb.cb.library.view.MultiListView mtListView;

    private GroupSaveViewModel viewModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_save);
        findViews();
        initEvent();
        mtListView.init(new RecyclerViewAdapter());
        viewModel = new GroupSaveViewModel();
        if(viewModel.groups!=null){
            viewModel.groups.addChangeListener(new RealmChangeListener<RealmResults<Group>>() {
                @Override
                public void onChange(RealmResults<Group> groups) {
                    mtListView.getListView().getAdapter().notifyDataSetChanged();
                }
            });
        }
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


//    private void taskMySaved() {
//        new MsgAction().getMySaved(new CallBack<ReturnBean<List<Group>>>(mtListView) {
//            @Override
//            public void onResponse(Call<ReturnBean<List<Group>>> call, Response<ReturnBean<List<Group>>> response) {
//                if (response.body() == null || !response.body().isOk()) {
//                    mtListView.getLoadView().setStateNoData(R.mipmap.ic_nodate);
//                    return;
//                }
//                groupInfoBeans.clear();
//                groupInfoBeans.addAll(response.body().getData());
////                mtListView.notifyDataSetChange(response);
//                mtListView.notifyDataSetChange();
//            }
//        });
//    }


    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        @Override
        public int getItemCount() {
            return null == viewModel.groups ? 0 : viewModel.groups.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder holder, int position) {
            MsgDao msgDao = new MsgDao();
            final Group groupInfoBean = viewModel.groups.get(position);
            if (StringUtil.isNotNull(groupInfoBean.getName())) {
                holder.txtName.setText(groupInfoBean.getName());
            } else {
                holder.txtName.setText(msgDao.getGroupName(groupInfoBean));
            }
            String imageHead = groupInfoBean.getAvatar();

            if (imageHead != null && !imageHead.isEmpty() && StringUtil.isNotNull(imageHead)) {
                //头像地址
                List<String> headList = new ArrayList<>();
                headList.add(imageHead);
                holder.imgHead.setList(headList);
            } else {
                loadGroupHeads(groupInfoBean, holder.imgHead);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ViewUtils.isFastDoubleClick()){
                        return;
                    }
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

        /**
         * 加载群头像
         *
         * @param imgHead
         */
        public synchronized void loadGroupHeads(Group group, MultiImageView imgHead) {
            if (group != null) {
                int i = group.getUsers().size();
                i = i > 9 ? 9 : i;
                //头像地址
                List<String> headList = new ArrayList<>();
                for (int j = 0; j < i; j++) {
                    MemberUser userInfo = group.getUsers().get(j);
                    headList.add(userInfo.getHead());
                }
                imgHead.setList(headList);
            }
        }

//        private void creatAndSaveImg(Group bean, ImageView imgHead) {
//            Group gginfo = bean;
//            int i = gginfo.getUsers().size();
//            i = i > 9 ? 9 : i;
//            //头像地址
//            String url[] = new String[i];
//            for (int j = 0; j < i; j++) {
//                MemberUser userInfo = gginfo.getUsers().get(j);
////            if (j == i - 1) {
////                name += userInfo.getName();
////            } else {
////                name += userInfo.getName() + "、";
////            }
//                url[j] = userInfo.getHead();
//            }
//            File file = GroupHeadImageUtil.synthesis(getContext(), url);
//            Glide.with(context).load(file)
//                    .apply(GlideOptionsUtil.headImageOptions()).into(imgHead);
//
//            MsgDao msgDao = new MsgDao();
//            msgDao.groupHeadImgCreate(gginfo.getGid(), file.getAbsolutePath());
//        }

        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private MultiImageView imgHead;
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
