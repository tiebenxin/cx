package com.yanlong.im.chat.ui.forward;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.ui.ChatActivity;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.UserInfoActivity;

import net.cb.cb.library.base.AbstractRecyclerAdapter;
import net.cb.cb.library.utils.TimeToString;

/**
 * @anthor Liszt
 * @data 2019/8/12
 * Description
 */
public class AdapterForwardRoster extends AbstractRecyclerAdapter {
    private UserDao userDao;
    private MsgDao msgDao;
    private IForwardRosterListener listener;

    public AdapterForwardRoster(Context ctx) {
        super(ctx);
    }

    public void initDao(UserDao user, MsgDao msg) {
        userDao = user;
        msgDao = msg;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new RCViewMucHolder(mInflater.inflate(R.layout.item_select_muc, parent, false));
        } else {
            return new RCViewHolder(mInflater.inflate(R.layout.item_msg_friend, parent, false));

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof RCViewHolder) {
            UserInfo info = (UserInfo) mBeanList.get(position);
            RCViewHolder viewHolder = (RCViewHolder) holder;
            viewHolder.bindData(info, position);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
    }

//    @Override
//    public int getItemCount() {
//        return mBeanList != null ? mBeanList.size() + 1 : 0;
//    }

    public void setForwardListener(IForwardRosterListener l) {
        listener = l;
    }

    public UserInfo getUserByPosition(int position) {
        if (position < getItemCount()) {
            return (UserInfo) mBeanList.get(position);
        }
        return null;
    }

    //自动生成ViewHold
    public class RCViewHolder extends RecyclerView.ViewHolder {
        private TextView txtType;
        private com.facebook.drawee.view.SimpleDraweeView imgHead;
        private TextView txtName;
        private TextView txtTime;
        private View viewType;

        //自动寻找ViewHold
        public RCViewHolder(View convertView) {
            super(convertView);
            txtType = convertView.findViewById(R.id.txt_type);
            imgHead = convertView.findViewById(R.id.img_head);
            txtName = convertView.findViewById(R.id.txt_name);
            txtTime = convertView.findViewById(R.id.txt_time);
            viewType = convertView.findViewById(R.id.view_type);

        }

        public void bindData(final UserInfo bean, final int position) {
            txtType.setText(bean.getTag());
            imgHead.setImageURI(Uri.parse("" + bean.getHead()));
            txtName.setText(bean.getName4Show());
            if (bean.getLastonline() > 0) {
                txtTime.setText(TimeToString.getTimeOnline(bean.getLastonline(), bean.getActiveType()));
                txtTime.setVisibility(View.VISIBLE);
            } else {
                txtTime.setVisibility(View.GONE);
            }


            UserInfo lastBean = getUserByPosition(position - 1);
            if (lastBean.getTag().equals(bean.getTag())) {
                viewType.setVisibility(View.GONE);
            } else {
                viewType.setVisibility(View.VISIBLE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener == null) {
                        return;
                    }
                    listener.onForward(bean.getUid(), "", bean.getHead(), bean.getName4Show());

                }
            });
        }

    }


    //自动生成ViewHold
    public class RCViewMucHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_root;

        public RCViewMucHolder(@NonNull View itemView) {
            super(itemView);
            ll_root = itemView.findViewById(R.id.ll_root);
            ll_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener == null) {
                        return;
                    }
                    listener.onSelectMuc();
                }
            });
        }
    }

}
