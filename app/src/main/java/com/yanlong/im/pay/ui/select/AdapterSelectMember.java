package com.yanlong.im.pay.ui.select;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hm.cxpay.bean.FromUserBean;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.wight.avatar.MultiImageView;

import net.cb.cb.library.base.AbstractRecyclerAdapter;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Liszt
 * @date 2020/8/20
 * Description
 */
public class AdapterSelectMember extends AbstractRecyclerAdapter {
    private Context context;
    private final Group group;
    private List<MemberUser> selectList = new ArrayList<>();
    private int currentMode = -1;//默认无选择，0 选择了所有人，1 选择群成员
    private int MAX = 5;
    private IEditAvatarListener listener;

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
        } else if (holder instanceof RCViewMucHolder) {
            RCViewMucHolder headHolder = (RCViewMucHolder) holder;
            headHolder.bindData(group);
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
            return (MemberUser) mBeanList.get(position);
        }
        return null;
    }

    //改变选择模式
    public void switchSelectMode(int mode, MemberUser user) {
        if (currentMode != mode) {
            currentMode = mode;
            if (mode == 0) {
                if (selectList.size() > 0) {
                    selectList.clear();
                    if (listener != null) {
                        listener.clear();
                    }
                }
            } else if (mode == 1) {
                if (selectList.size() > 0) {
                    selectList.clear();
                }
                selectList.add(user);
                if (listener != null) {
                    listener.add(user);
                }
            }
            notifyDataSetChanged();
        } else {
            if (mode == 1) {
                if (selectList.contains(user)) {
                    selectList.remove(user);
                    if (listener != null) {
                        listener.remove(user);
                    }
                } else {
                    if (selectList.size() < MAX) {
                        selectList.add(user);
                        if (listener != null) {
                            listener.add(user);
                        }
                    } else {
                        ToastUtil.show(getContext(), "最多选择5人");
                    }
                }
            }
        }
    }

    public void switchModel(int model) {
        currentMode = model;
    }


    public int getMode() {
        if (currentMode == -1) {
            return 0;
        } else {
            return currentMode;
        }
    }

    public void removeMember(MemberUser user) {
        if (currentMode == 1) {
            if (selectList.contains(user)) {
                selectList.remove(user);
                notifyDataSetChanged();
            }
        }
    }

    public void setListener(IEditAvatarListener l) {
        listener = l;
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
                    if (!ivSelect.isSelected() && selectList.size() < MAX) {
                        ivSelect.setSelected(!ivSelect.isSelected());
                    } else if (ivSelect.isSelected()) {
                        ivSelect.setSelected(!ivSelect.isSelected());
                    }
                    switchSelectMode(1, bean);
                }
            });
            if (selectList.contains(bean)) {
                ivSelect.setSelected(true);
            } else {
                ivSelect.setSelected(false);
            }
        }
    }

    public ArrayList<FromUserBean> getSelectList() {
        ArrayList<FromUserBean> list = null;
        if (selectList != null && selectList.size() > 0) {
            list = new ArrayList();
            for (MemberUser user : selectList) {
                FromUserBean bean = new FromUserBean();
                bean.setUid(user.getUid());
                bean.setAvatar(user.getHead());
                bean.setNickname(!TextUtils.isEmpty(user.getMembername()) ? user.getMembername() : user.getName());
                list.add(bean);
            }
        }
        return list;
    }

    public void setSelectList(List<MemberUser> memberUsers) {
        selectList = memberUsers;
        currentMode = 1;
        notifyDataSetChanged();
    }


    //自动生成ViewHold
    public class RCViewMucHolder extends RecyclerView.ViewHolder {
        private final LinearLayout ll_root;
        private final ImageView ivSelect;
        private final MultiImageView ivAvatar;


        public RCViewMucHolder(@NonNull View itemView) {
            super(itemView);
            ll_root = itemView.findViewById(R.id.ll_root);
            ivSelect = itemView.findViewById(R.id.iv_select);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ll_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ViewUtils.isFastDoubleClick()) {
                        return;
                    }
                    if (ivSelect.isSelected()) {
                        switchModel(-1);
                        ivSelect.setSelected(false);
                    } else {
                        switchSelectMode(0, null);
                        ivSelect.setSelected(true);
                    }
                }
            });
        }

        private void bindData(Group group) {
            if (group == null) {
                return;
            }
            if (group != null) {
                int i = group.getUsers().size();
                i = i > 9 ? 9 : i;
                //头像地址
                List<String> headList = new ArrayList<>();
                for (int j = 0; j < i; j++) {
                    MemberUser userInfo = group.getUsers().get(j);
                    headList.add(userInfo.getHead());
                }
                ivAvatar.setList(headList);
            }
            ivSelect.setSelected(currentMode == 0);
        }
    }
}
