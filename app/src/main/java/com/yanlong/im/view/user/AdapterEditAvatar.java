package com.yanlong.im.view.user;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.base.AbstractRecyclerAdapter;
import net.cb.cb.library.base.AbstractViewHolder;

/**
 * @author Liszt
 * @date 2020/8/25
 * Description 头像增删Adapter
 */
public class AdapterEditAvatar extends AbstractRecyclerAdapter<EditAvatarBean> {
    public AdapterEditAvatar(Context ctx) {
        super(ctx);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EditAvatarBean user = mBeanList.get(position);
        AvatarHolder avatarHolder = (AvatarHolder) holder;
        avatarHolder.bindData(user);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AvatarHolder(mInflater.inflate(R.layout.item_group_create_top, parent, false));
    }

    //自动生成ViewHold
    public class AvatarHolder extends AbstractViewHolder<EditAvatarBean> {
        private ImageView ivAvatar;

        //自动寻找ViewHold
        public AvatarHolder(View convertView) {
            super(convertView);
            ivAvatar = convertView.findViewById(R.id.img_head);
        }

        @Override
        public void bindData(EditAvatarBean bean) {
            if (bean.getUser() != null) {
                if (bean.getDeleteCount() == 2) {
                    Glide.with(getContext()).load(bean.getUser().getHead()).apply(GlideOptionsUtil.headImageOptions()).into(ivAvatar);
                }else if (bean.getDeleteCount() == 1){
                    ivAvatar.setAlpha(0.6f);
                }
            }
        }
    }
}
