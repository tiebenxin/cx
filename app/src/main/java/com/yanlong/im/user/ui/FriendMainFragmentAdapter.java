package com.yanlong.im.user.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yanlong.im.FriendViewModel;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.ui.GroupSaveActivity;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.UserUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.StrikeButton;

import java.util.List;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/4/11 0011
 * @description
 */
public class FriendMainFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private FriendViewModel viewModel;

    public FriendMainFragmentAdapter(Context context, FriendViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    @Override
    public int getItemCount() {
        return viewModel.getFriendSize() + 2;
    }

    //自动生成控件事件
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RCViewFuncHolder) {
            final RCViewFuncHolder hd = (RCViewFuncHolder) holder;
            hd.viewAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // ToastUtil.show(getContext(), "添加朋友");
                    //清除红点值
                    viewModel.clearRemindCount("friend_apply");
                    hd.sbApply.setNum(0, false);
                    context.startActivity(new Intent(context, FriendApplyAcitvity.class));
                }
            });
            hd.viewAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                        ToastUtil.show(context.getResources().getString(R.string.user_disable_message));
                        return;
                    }
                    context.startActivity(new Intent(context, FriendAddAcitvity.class));
                }
            });
            hd.viewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // ToastUtil.show(getContext(), "群消息");
                    context.startActivity(new Intent(context, GroupSaveActivity.class));
                }
            });
            hd.viewMatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // ToastUtil.show(getContext(), "匹配");
                    if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                        ToastUtil.show(context.getResources().getString(R.string.user_disable_message));
                        return;
                    }
                    context.startActivity(new Intent(context, FriendMatchActivity.class));
                }
            });
            hd.sbApply.setNum(viewModel.getRemindCount("friend_apply"), false);
        } else if (holder instanceof RCViewBtnHolder) {
            final RCViewBtnHolder hd = (RCViewBtnHolder) holder;
            hd.friend_numb_tv.setText("共" + viewModel.getFriendSize() + "位联系人");
        } else if (holder instanceof RCViewHolder) {
            final UserInfo bean = viewModel.getFriends().get(position - 1);
            RCViewHolder hd = (RCViewHolder) holder;
            hd.txtType.setText(bean.getTag());
            //      hd.imgHead.setImageURI(Uri.parse("" + bean.getHead()));

            Glide.with(context).load(bean.getHead())
                    .apply(GlideOptionsUtil.headImageOptions()).into(hd.imgHead);

            hd.txtName.setText(bean.getName4Show());
            hd.viewLine.setVisibility(View.VISIBLE);
            if (bean.isSystemUser()) {
                hd.txtName.setTextColor(context.getResources().getColor(R.color.blue_title));
                hd.txtTime.setVisibility(View.GONE);
                hd.usertype_tv.setVisibility(View.VISIBLE);
            } else {
                hd.txtName.setTextColor(context.getResources().getColor(R.color.black));
                hd.txtTime.setVisibility(View.VISIBLE);
                hd.usertype_tv.setVisibility(View.GONE);

                if (bean.getLastonline() > 0) {
                    hd.txtTime.setText(TimeToString.getTimeOnline(bean.getLastonline(), bean.getActiveType(), false));
                    hd.txtTime.setVisibility(View.VISIBLE);
                } else {
                    hd.txtTime.setVisibility(View.GONE);
                }
            }

            //相同的字母，隐藏横排字母-匹配上一个项的字母，是否相同
            if (position != 1 && viewModel.getFriends().get(position - 2).getTag().equals(bean.getTag())) {
                //相同的字母，不显示横排字母
                hd.viewType.setVisibility(View.GONE);
            } else {
                //不同的字母，显示横排字母
                hd.viewType.setVisibility(View.VISIBLE);
            }
            //不同字母，隐藏项的底部横线-匹配下一个字母的项是否相同
            if (position == getItemCount() - 2 || !viewModel.getFriends().get(position).getTag().equals(bean.getTag())) {
                hd.viewLine.setVisibility(View.GONE);
            } else {
                hd.viewLine.setVisibility(View.VISIBLE);
            }

            hd.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.getuType() == ChatEnum.EUserType.ASSISTANT) {
                        context.startActivity(new Intent(context, ChatActivity.class)
                                .putExtra(ChatActivity.AGM_TOUID, bean.getUid()));
                    } else {
                        context.startActivity(new Intent(context, UserInfoActivity.class)
                                .putExtra(UserInfoActivity.ID, bean.getUid()));
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        onBindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        int type = 0;
        if (position == 0) {//顶部-4个功能菜单
            type = 0;
        } else if (position == getItemCount() - 1) {//底部-联系人数量
            type = 1;
        } else {
            type = 2;
        }
        return type;
    }

    //自动寻找ViewHold
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup view, int i) {
        if (i == 0) {
            RCViewFuncHolder holder = new RCViewFuncHolder(LayoutInflater.from(context).inflate(R.layout.item_msg_friend_fun, view, false));
            return holder;
        }
        if (i == 1) {
            RCViewBtnHolder holder = new RCViewBtnHolder(LayoutInflater.from(context).inflate(R.layout.item_msg_btn, view, false));
            return holder;
        } else {
            RCViewHolder holder = new RCViewHolder(LayoutInflater.from(context).inflate(R.layout.item_msg_friend, view, false));
            return holder;
        }
    }

    //自动生成ViewHold
    public class RCViewHolder extends RecyclerView.ViewHolder {
        private TextView txtType;
        private ImageView imgHead;
        private TextView txtName;
        private TextView txtTime;
        private View viewType;
        private TextView usertype_tv;
        private View viewLine;

        //自动寻找ViewHold
        public RCViewHolder(View convertView) {
            super(convertView);
            txtType = convertView.findViewById(R.id.txt_type);
            imgHead = convertView.findViewById(R.id.img_head);
            txtName = convertView.findViewById(R.id.txt_name);
            txtTime = convertView.findViewById(R.id.txt_time);
            viewType = convertView.findViewById(R.id.view_type);
            usertype_tv = convertView.findViewById(R.id.usertype_tv);
            viewLine = convertView.findViewById(R.id.view_line);
        }

    }


    //自动生成ViewHold
    public class RCViewFuncHolder extends RecyclerView.ViewHolder {
        private LinearLayout viewAdd;
        private LinearLayout viewAddFriend;
        private LinearLayout viewMatch;
        private LinearLayout viewGroup;
        private StrikeButton sbApply;

        //自动寻找ViewHold
        public RCViewFuncHolder(View convertView) {
            super(convertView);
            viewAdd = convertView.findViewById(R.id.view_add);
            viewAddFriend = convertView.findViewById(R.id.view_add_friend);
            viewMatch = convertView.findViewById(R.id.view_match);
            viewGroup = convertView.findViewById(R.id.view_group);
            sbApply = convertView.findViewById(R.id.sb_apply);
        }
    }

    //自动生成ViewHold
    public class RCViewBtnHolder extends RecyclerView.ViewHolder {
        private TextView friend_numb_tv;

        //自动寻找ViewHold
        public RCViewBtnHolder(View convertView) {
            super(convertView);
            friend_numb_tv = convertView.findViewById(R.id.friend_numb_tv);
        }
    }
}
