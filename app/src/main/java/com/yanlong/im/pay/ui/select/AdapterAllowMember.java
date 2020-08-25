package com.yanlong.im.pay.ui.select;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.base.AbstractRecyclerAdapter;

/**
 * @author Liszt
 * @date 2020/8/25
 * Description
 */
public class AdapterAllowMember extends AbstractRecyclerAdapter {
    public AdapterAllowMember(Context ctx) {
        super(ctx);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RCViewHolder(mInflater.inflate(R.layout.item_msg_friend, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof RCViewHolder) {
            MemberUser info = (MemberUser) mBeanList.get(position);
            RCViewHolder viewHolder = (RCViewHolder) holder;
            viewHolder.bindData(info, position);
        }
    }

    public MemberUser getUserByPosition(int position) {
        if (position < getItemCount() - 1) {
            return (MemberUser) mBeanList.get(position);
        }
        return null;
    }

    //自动生成ViewHold
    public class RCViewHolder extends RecyclerView.ViewHolder {
        private TextView txtType;
        private ImageView imgHead, ivSelect;
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
            ivSelect = convertView.findViewById(R.id.iv_select);
//            ivSelect.setVisibility(View.VISIBLE);
        }

        public void bindData(final MemberUser bean, final int position) {
            txtType.setText(bean.getTag());
            Glide.with(getContext()).load(bean.getHead()).apply(GlideOptionsUtil.headImageOptions()).into(imgHead);
            txtName.setText(bean.getShowName());
            txtTime.setVisibility(View.GONE);
            if (position > 0) {
                MemberUser lastBean = getUserByPosition(position - 1);
                if (lastBean.getTag().equals(bean.getTag())) {
                    viewType.setVisibility(View.GONE);
                } else {
                    viewType.setVisibility(View.VISIBLE);
                }
            } else if (position == 0) {
                viewType.setVisibility(View.VISIBLE);
            } else {
                viewType.setVisibility(View.GONE);
            }

        }

    }
}
