package com.yanlong.im.chat.ui.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.wight.avatar.MultiImageView;

import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.view.StrikeButton;

import java.util.ArrayList;
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
        return viewModel.getSearchFriendsSize() + viewModel.getSearchGroupsSize() + viewModel.getSearchSessionsSize();
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position < viewModel.searchFriends.size()) {//单人
//            return 0;
//        } else if (position < (viewModel.searchFriends.size() + viewModel.getSearchGroupsSize())) {//群
//            return 1;
//        } else {//聊天记录
//            return 2;
//        }
//    }

    //自动生成控件事件
    @Override
    public void onBindViewHolder(final RCViewHolder holder, int position) {
        if (position < viewModel.searchFriends.size()) {//单人
            UserInfo userInfo = viewModel.searchFriends.get(position);
            List<String> head = new ArrayList<String>();
            head.add(userInfo.getHead());
            holder.imgHead.setList(head);
            holder.txtName.setText(userInfo.getName4Show());
            if (userInfo.getName4Show().contains(viewModel.key.getValue())) {
                //1.名称包含关键字，则只显示名称，隐藏描述
                holder.vInfoPanel.setVisibility(View.GONE);
            } else {//2.名称不包含关键字，则说明昵称/微信号包含，显示第二行
                holder.vInfoPanel.setVisibility(View.VISIBLE);
                if (userInfo.getName().contains(viewModel.key.getValue())) {
                    holder.txtInfo.setText("昵称：" + userInfo.getName());
                } else {
                    holder.txtInfo.setText("常信号：" + userInfo.getImid());
                }
            }

        } else if (position < (viewModel.searchFriends.size() + viewModel.getSearchGroupsSize())) {//群
            holder.vInfoPanel.setVisibility(View.VISIBLE);
            Group group = viewModel.searchGroups.get(position - viewModel.searchFriends.size());
            int i = group.getUsers().size();
            i = i > 9 ? 9 : i;
            //头像地址
            List<String> headList = new ArrayList<>();
            String groupName = "";
            int headMaxPosition = Math.min(group.getUsers().size(),14);
            for (int j = 0; j < headMaxPosition; j++) {
                MemberUser userInfo = group.getUsers().get(j);
                if (j < i) {
                    headList.add(userInfo.getHead());
                }
                groupName += StringUtil.getUserName(/*info.getMkName()*/"", userInfo.getMembername(), userInfo.getName(), userInfo.getUid()) + "、";
            }
            if (TextUtils.isEmpty(group.getName())) {
                //去掉最后一个顿号
                groupName = groupName.substring(0, groupName.length() - 1);
                groupName = groupName.length() > 14 ? StringUtil.splitEmojiString2(groupName, 0, 14) : groupName;
                groupName += "的群";
            } else {
                groupName = group.getName();
            }
            holder.imgHead.setList(headList);
            holder.txtName.setText(groupName);
            if (group.getName().contains(viewModel.key.getValue())) {
                //3.群名称包含关键字，则只显示名称，隐藏描述
                holder.vInfoPanel.setVisibility(View.GONE);
            } else {//4.群名称不包含关键字，则说明群成员包含，显示第二行
                holder.vInfoPanel.setVisibility(View.VISIBLE);
                String memeberName = null;
                for (MemberUser user : group.getUsers()) {
                    if (user.getMembername().contains(viewModel.key.getValue())) {
                        memeberName = user.getMembername();
                        break;
                    } else if (user.getName().contains(viewModel.key.getValue())) {
                        memeberName = user.getName();
                        break;
                    }
                }
                holder.txtInfo.setText("包含：" + memeberName);
            }
        } else {//聊天记录
            holder.vInfoPanel.setVisibility(View.VISIBLE);
        }

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

        private View viewIt, vInfoPanel;
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
            vInfoPanel = convertView.findViewById(R.id.ll_info);

        }

    }

}
