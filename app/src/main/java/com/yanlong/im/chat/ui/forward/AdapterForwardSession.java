package com.yanlong.im.chat.ui.forward;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.base.AbstractRecyclerAdapter;

/**
 * @anthor Liszt
 * @data 2019/8/10
 * Description
 */
public class AdapterForwardSession extends AbstractRecyclerAdapter {

    private UserDao userDao;
    private MsgDao msgDao;
    private IForwardListener listener;

    public AdapterForwardSession(Context ctx) {
        super(ctx);
    }

    public void initDao(UserDao user, MsgDao msg) {
        userDao = user;
        msgDao = msg;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RCViewHolder(mInflater.inflate(R.layout.item_msg_forward, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        RCViewHolder viewHolder = (RCViewHolder) holder;
        viewHolder.bindData((com.yanlong.im.chat.bean.Session) mBeanList.get(position));
    }

    //自动生成ViewHold
    class RCViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout viewIt;
        private com.facebook.drawee.view.SimpleDraweeView imgHead;
        private TextView txtName;

        //自动寻找ViewHold
        public RCViewHolder(View convertView) {
            super(convertView);
            viewIt = convertView.findViewById(R.id.view_it);
            imgHead = convertView.findViewById(R.id.img_head);
            txtName = convertView.findViewById(R.id.txt_name);
        }

        public void bindData(final com.yanlong.im.chat.bean.Session bean) {
            String icon = "";
            String title = "";
            boolean isGroup = false;

            if (bean.getType() == 0) {//单人
                userDao = new UserDao();
                UserInfo finfo = userDao.findUserInfo(bean.getFrom_uid());
                if (finfo != null) {
                    icon = finfo.getHead();
                    title = finfo.getName4Show();
                }
            } else if (bean.getType() == 1) {//群
                isGroup = true;
                msgDao = new MsgDao();
                Group ginfo = msgDao.getGroup4Id(bean.getGid());
                if (ginfo != null) {
                    icon = ginfo.getAvatar();
                    //获取最后一条群消息
                    title = ginfo.getName();
                } else {

                }
            }
            imgHead.setImageURI(Uri.parse(icon));
            txtName.setText(title);

            final String finalTitle = title;
            final String finalIcon = icon;

            final boolean finalIsGroup = isGroup;
            viewIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onForward(finalIsGroup ? -1L : bean.getFrom_uid(), bean.getGid(), finalIcon, finalTitle);
                    }
                }
            });

        }

    }

    public void setForwardListener(IForwardListener l) {
        listener = l;
    }
}