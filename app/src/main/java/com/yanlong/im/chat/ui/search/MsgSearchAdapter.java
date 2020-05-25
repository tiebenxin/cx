package com.yanlong.im.chat.ui.search;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.chat.ui.SearchMsgActivity;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.wight.avatar.MultiImageView;

import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.view.StrikeButton;

import java.util.ArrayList;
import java.util.Arrays;
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
            holder.viewIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ChatActivity.class)
                            .putExtra(ChatActivity.AGM_TOUID, userInfo.getUid())
                    );
                }
            });

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
            holder.viewIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ChatActivity.class)
                            .putExtra(ChatActivity.AGM_TOGID, group.getGid())
                    );
                }
            });
        } else {//聊天记录
            holder.vInfoPanel.setVisibility(View.VISIBLE);
            int p=position - viewModel.searchFriends.size() - viewModel.getSearchGroupsSize();
            SessionDetail sessionDetail=viewModel.searchSessions.get(p);

            //头像地址
            List<String> headList = new ArrayList<>();
            if (StringUtil.isNotNull(sessionDetail.getAvatar())) {
                headList.add(sessionDetail.getAvatar());
            } else {
                List<String > avatarList = null;
                String avatarListString = sessionDetail.getAvatarList();
                if (avatarListString != null) {
                    avatarList = Arrays.asList(avatarListString.split(","));
                }
                if (avatarList == null || avatarList.size() == 0) {//没有头像，设个默认的，否则会出现头像为全灰色
                    avatarList = new ArrayList<>();
                    avatarList.add("");
                }
                headList.addAll(avatarList);
            }

            holder.imgHead.setList(headList);
            holder.txtName.setText(sessionDetail.getName());
            if(viewModel.sessionSearch.containsKey(sessionDetail.getSid())){
                MsgSearchViewModel.SessionSearch sessionSearch = viewModel.sessionSearch.get(sessionDetail.getSid());
                holder.txtInfo.setText(sessionSearch.getCount()+"条相关的聊天记录");
                holder.viewIt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, SearchMsgActivity.class)
                                .putExtra(SearchMsgActivity.AGM_GID, sessionSearch.getGid())
                                .putExtra(SearchMsgActivity.AGM_FUID, sessionSearch.getUid())
                                .putExtra(SearchMsgActivity.AGM_SEARCH_KEY, viewModel.key.getValue())
                        );
                    }
                });
            }else{
                holder.txtInfo.setText("0条相关的聊天记录");
                holder.viewIt.setOnClickListener(null);
            }
        }

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
        private TextView txtName;
        private TextView txtInfo;
//            private final TextView tv_num;

        //自动寻找ViewHold
        public RCViewHolder(View convertView) {
            super(convertView);
            imgHead = convertView.findViewById(R.id.img_head);
            sb = convertView.findViewById(R.id.sb);
            viewIt = convertView.findViewById(R.id.view_it);
            txtName = convertView.findViewById(R.id.txt_name);
            txtInfo = convertView.findViewById(R.id.txt_info);
            vInfoPanel = convertView.findViewById(R.id.ll_info);
        }

    }

}
