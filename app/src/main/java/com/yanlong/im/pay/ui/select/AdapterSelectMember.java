package com.yanlong.im.pay.ui.select;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.base.AbstractRecyclerAdapter;
import net.cb.cb.library.utils.ViewUtils;

/**
 * @author Liszt
 * @date 2020/8/20
 * Description
 */
public class AdapterSelectMember extends AbstractRecyclerAdapter {
    private Context context;
    private final Group group;

    public AdapterSelectMember(Context ctx, Group g) {
        super(ctx);
        context = ctx;
        group = g;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new RCViewMucHolder(mInflater.inflate(R.layout.item_select_all, parent, false));
        } else {
            return new RCViewHolder(mInflater.inflate(R.layout.item_msg_friend, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof RCViewHolder) {
            MemberUser info = (MemberUser) mBeanList.get(position - 1);
            RCViewHolder viewHolder = (RCViewHolder) holder;
            viewHolder.bindData(info, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return mBeanList != null ? mBeanList.size() + 1 : 0;
    }

    public MemberUser getUserByPosition(int position) {
        if (position < getItemCount() - 1) {
//            return viewModel.users.get(position);
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
            ivSelect.setVisibility(View.VISIBLE);
        }

        public void bindData(final MemberUser bean, final int position) {
            txtType.setText(bean.getTag());
            Glide.with(context).load(bean.getHead())
                    .apply(GlideOptionsUtil.headImageOptions()).into(imgHead);

            txtName.setText(bean.getShowName());
            txtTime.setVisibility(View.GONE);

            if (position > 1) {
                MemberUser lastBean = getUserByPosition(position - 2);
                if (lastBean.getTag().equals(bean.getTag())) {
                    viewType.setVisibility(View.GONE);
                } else {
                    viewType.setVisibility(View.VISIBLE);
                }
            } else if (position == 1) {
                viewType.setVisibility(View.VISIBLE);
            } else {
                viewType.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ViewUtils.isFastDoubleClick()) {
                        return;
                    }
                }
            });

            if (MsgForwardActivity.isSingleSelected) {
                ivSelect.setVisibility(View.GONE);
            } else {
                ivSelect.setVisibility(View.VISIBLE);

                boolean hasSelect = MsgForwardActivity.findMoreSessionBeanList(bean.getUid(), "");
                if (hasSelect) {
                    bean.setChecked(true);
                    ivSelect.setSelected(true);
                } else {
                    bean.setChecked(false);
                    ivSelect.setSelected(false);
                }
            }
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


                }
            });
        }

        private void bindData(Group group) {
            if (group == null) {
                return;
            }

        }
    }
}
