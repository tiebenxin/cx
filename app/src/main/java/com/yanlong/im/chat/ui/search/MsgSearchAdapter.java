package com.yanlong.im.chat.ui.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.wight.avatar.MultiImageView;

import net.cb.cb.library.view.StrikeButton;

import java.util.List;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/22 0022
 * @description
 */
public class MsgSearchAdapter extends RecyclerView.Adapter<MsgSearchAdapter.RCViewHolder> {

    public MsgSearchViewModel viewModel;
    private Context context;

    public MsgSearchAdapter(Context context, MsgSearchViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    @Override
    public int getItemCount() {
        return viewModel.getSearchFriendsSize()+viewModel.getSearchGroupsSize()+viewModel.getSearchSessionsSize();
    }

    @Override
    public void onBindViewHolder(@NonNull RCViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            int type = (int) payloads.get(0);
            switch (type) {
                case 0:
                    break;
                case 1:
                    break;
            }
            onBindViewHolder(holder, position);
        }
    }

    //自动生成控件事件
    @Override
    public void onBindViewHolder(final RCViewHolder holder, int position) {

    }

    //加载群头像
    public synchronized void loadGroupHeads(Session bean, MultiImageView imgHead) {
//        Group gginfo = msgDao.getGroup4Id(bean.getGid());
//        if (gginfo != null) {
//            int i = gginfo.getUsers().size();
//            i = i > 9 ? 9 : i;
//            //头像地址
//            List<String> headList = new ArrayList<>();
//            for (int j = 0; j < i; j++) {
//                MemberUser userInfo = gginfo.getUsers().get(j);
//                headList.add(userInfo.getHead());
//            }
//            imgHead.setList(headList);
//        }
    }


    //自动寻找ViewHold
    @Override
    public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
        RCViewHolder holder = new RCViewHolder(LayoutInflater.from(context).inflate(R.layout.item_msg_session, view, false));
        return holder;
    }


    //自动生成ViewHold
    public class RCViewHolder extends RecyclerView.ViewHolder {
        private MultiImageView imgHead;
        private StrikeButton sb;

        private View viewIt;
        private SwipeMenuLayout swipeLayout;
        private TextView txtName;
        private TextView txtInfo;
        private TextView txtTime;
        private final ImageView iv_disturb, iv_disturb_unread;
//            private final TextView tv_num;

        //自动寻找ViewHold
        public RCViewHolder(View convertView) {
            super(convertView);
            imgHead = convertView.findViewById(R.id.img_head);
            swipeLayout = convertView.findViewById(R.id.swipeLayout);
            sb = convertView.findViewById(R.id.sb);
            viewIt = convertView.findViewById(R.id.view_it);
            txtName = convertView.findViewById(R.id.txt_name);
            txtInfo = convertView.findViewById(R.id.txt_info);
            txtTime = convertView.findViewById(R.id.txt_time);
            iv_disturb = convertView.findViewById(R.id.iv_disturb);
//                tv_num = convertView.findViewById(R.id.tv_num);
            iv_disturb_unread = convertView.findViewById(R.id.iv_disturb_unread);
        }

    }

}
