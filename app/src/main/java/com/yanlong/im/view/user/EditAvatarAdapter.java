package com.yanlong.im.view.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.utils.GlideOptionsUtil;

import java.util.List;

/**
 * @author Liszt
 * @date 2020/8/26
 * Description
 */
public class EditAvatarAdapter extends BaseAdapter {

    private List<EditAvatarBean> mList;
    private final Context context;
    private final LayoutInflater inflater;

    public EditAvatarAdapter(Context con) {
        context = con;
        inflater = LayoutInflater.from(con);
    }

    public void bindData(List<EditAvatarBean> l) {
        mList = l;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EditAvatarBean bean = mList.get(position);
        AvatarHolder holder;
        if (convertView == null) {
            holder = new AvatarHolder(inflater.inflate(R.layout.item_group_create_top, parent));
            convertView = holder.getView();
            convertView.setTag(holder);
        } else {
            holder = (AvatarHolder) convertView.getTag();
        }
        holder.bindData(bean);
        return convertView;
    }

    class AvatarHolder {
        private final View root;
        private final ImageView ivAvatar;

        public AvatarHolder(View v) {
            root = v;
            ivAvatar = v.findViewById(R.id.img_head);

        }

        public View getView() {
            return root;
        }

        public void bindData(EditAvatarBean bean) {
            if (bean.getUser() != null) {
                if (bean.getDeleteCount() == 2) {
                    Glide.with(context).load(bean.getUser().getHead()).apply(GlideOptionsUtil.headImageOptions()).into(ivAvatar);
                } else if (bean.getDeleteCount() == 1) {
                    ivAvatar.setAlpha(0.6f);
                }
            }
        }
    }
}
