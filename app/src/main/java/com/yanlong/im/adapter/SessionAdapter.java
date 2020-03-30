package com.yanlong.im.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.ui.ChatActivity;
import com.yanlong.im.interf.ISessionListener;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.wight.avatar.MultiImageView;

import net.cb.cb.library.base.AbstractRecyclerAdapter;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.view.StrikeButton;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Liszt
 * @date 2019/9/25
 * Description
 */
public class SessionAdapter extends AbstractRecyclerAdapter<Session> {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    private UserDao userDao;
    private MsgDao msgDao = new MsgDao();
    private final ISessionListener listener;
    private View viewNetwork;

    public SessionAdapter(Context ctx, ISessionListener l) {
        super(ctx);
        listener = l;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof RCViewHolder) {
            RCViewHolder viewHolder = (RCViewHolder) holder;
            viewHolder.bindData(mBeanList.get(position));
        } else if (holder instanceof HeadViewHolder) {
            HeadViewHolder viewHolder = (HeadViewHolder) holder;
            viewNetwork = viewHolder.viewNetwork;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            int type = (int) payloads.get(0);
            switch (type) {
                case 0:
                    break;
                case 1:
                    break;
            }
            onBindViewHolder(holder, position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeadViewHolder(mInflater.inflate(R.layout.view_head_main_message, parent, false));
        } else {
            return new RCViewHolder(mInflater.inflate(R.layout.item_msg_session, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_NORMAL;
        }
    }

    //自动生成ViewHold
    public class RCViewHolder extends RecyclerView.ViewHolder {
        private MultiImageView imgHead;
        private StrikeButton sb;
        private View viewIt;
        private Button btnDel;
        private SwipeMenuLayout swipeLayout;
        private TextView txtName;
        private TextView txtInfo;
        private TextView txtTime;
        private final ImageView iv_disturb, iv_disturb_unread;
        private TextView usertype_tv;


        //自动寻找ViewHold
        public RCViewHolder(View convertView) {
            super(convertView);
            imgHead = convertView.findViewById(R.id.img_head);
            swipeLayout = convertView.findViewById(R.id.swipeLayout);
            sb = convertView.findViewById(R.id.sb);
            viewIt = convertView.findViewById(R.id.view_it);
            btnDel = convertView.findViewById(R.id.btn_del);
            txtName = convertView.findViewById(R.id.txt_name);
            txtInfo = convertView.findViewById(R.id.txt_info);
            txtTime = convertView.findViewById(R.id.txt_time);
            iv_disturb = convertView.findViewById(R.id.iv_disturb);
            iv_disturb_unread = convertView.findViewById(R.id.iv_disturb_unread);
            usertype_tv = convertView.findViewById(R.id.usertype_tv);
        }

        public void bindData(final Session bean) {
            String icon = bean.getAvatar();
            String title = bean.getName();
            MsgAllBean msginfo = bean.getMessage();
            String name = bean.getSenderName();
            // 头像集合
            List<String> headList = new ArrayList<>();
            String info = "";
            if (msginfo != null) {
                info = msginfo.getMsg_typeStr();
            }
            int type = bean.getMessageType();

            if (bean.getType() == 0) {//单人
                if (type == ChatEnum.ESessionType.ENVELOPE_FAIL) {
                    SpannableString style = new SpannableString("[红包发送失败]" + info);
                    ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                    style.setSpan(protocolColorSpan, 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    showMessage(txtInfo, info, style);
                } else {
                    if (StringUtil.isNotNull(bean.getDraft())) {
                        SpannableString style = new SpannableString("[草稿]" + bean.getDraft());
                        ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                        style.setSpan(protocolColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        showMessage(txtInfo, bean.getDraft(), style);
                    } else {
                        // 判断是否是动画表情
                        showMessage(txtInfo, info, null);
                    }
                }
                headList.add(icon);
                imgHead.setList(headList);

            } else if (bean.getType() == 1) {//群
                if (type == 0) {
                    if (!TextUtils.isEmpty(bean.getAtMessage()) && !TextUtils.isEmpty(name)) {
                        info = name + bean.getAtMessage();
                    } else {
                        info = name + info;

                    }
                } else if (type == 1) {
                    if (!TextUtils.isEmpty(bean.getAtMessage()) && !TextUtils.isEmpty(name)) {
                        info = bean.getAtMessage();
                        if (StringUtil.isNotNull(info) && info.startsWith("@所有人")) {
                            info = info.replace("@所有人", "");
                        }
                        info = name + info;
                    } else {
                        info = name + info;
                    }
                } else if (msginfo != null && (ChatEnum.EMessageType.CHANGE_SURVIVAL_TIME + "").equals(msginfo.getMsg_type() + "")) {
                    //阅后即焚不通知 不显示谁发的 肯定是群主修改的
                    // info=info;
                } else if (!TextUtils.isEmpty(info) && !TextUtils.isEmpty(name)) {//草稿除外
                    if (msginfo != null && (ChatEnum.EMessageType.AT + "").equals(msginfo.getMsg_type() + "")
                            && StringUtil.isNotNull(info) && info.startsWith("@所有人")) {
                        info = info.replace("@所有人", "");
                    }
                    info = name + info;
                }
                // 处理公告...问题
                info = info.replace("\r\n", "  ");

                switch (type) {
                    case 0:
                        if (StringUtil.isNotNull(bean.getAtMessage())) {
                            if (msginfo != null && msginfo.getMsg_type() == ChatEnum.EMessageType.AT) {
                                SpannableString style = new SpannableString("[有人@我]" + info);
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                showMessage(txtInfo, info, style);
                            } else {
                                SpannableString style = new SpannableString("[有人@我]" + info);
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                showMessage(txtInfo, info, style);
                            }
                        }
                        break;
                    case 1:
                        if (StringUtil.isNotNull(bean.getAtMessage())) {
                            if (msginfo != null && msginfo.getMsg_type() == ChatEnum.EMessageType.AT) {
                                SpannableString style = new SpannableString("[有人@我]" + info);
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                showMessage(txtInfo, info, style);
                            } else {
                                SpannableString style = new SpannableString("[@所有人]" + info);
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                showMessage(txtInfo, info, style);
                            }
                        }
                        break;
                    case 2:
                        if (StringUtil.isNotNull(bean.getDraft())) {
                            SpannableString style = new SpannableString("[草稿]" + bean.getDraft());
                            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                            style.setSpan(protocolColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            showMessage(txtInfo, bean.getDraft(), style);
                        } else {
                            // 判断是否是动画表情
                            if (msginfo != null && msginfo.getMsg_type() == ChatEnum.EMessageType.SHIPPED_EXPRESSION) {
                                txtInfo.setText(msginfo.getFrom_nickname() + ":" + info);
                            } else {
                                showMessage(txtInfo, info, null);
                            }
                        }
                        break;
                    case 3:
                        SpannableString style = new SpannableString("[红包发送失败]" + info);
                        ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                        style.setSpan(protocolColorSpan, 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        showMessage(txtInfo, info, style);
                        break;
                    default:
                        // 判断是否是动画表情
                        if (msginfo != null && msginfo.getMsg_type() == ChatEnum.EMessageType.SHIPPED_EXPRESSION) {
                            txtInfo.setText(msginfo.getFrom_nickname() + ":" + info);
                        } else {
                            showMessage(txtInfo, info, null);
                        }
                        break;
                }


//                LogUtil.getLog().e("TAG", icon.toString());
                if (StringUtil.isNotNull(icon)) {
                    headList.add(icon);
                    imgHead.setList(headList);
                } else {
                    if (bean.getAvatarList() != null && bean.getAvatarList().size() > 0) {
                        imgHead.setList(bean.getAvatarList());
                    } else {
                        loadGroupHeads(bean, imgHead);
                    }
                }

            }


            txtName.setText(title);
            if (bean.isSystemUser()) {
                //系统会话
                txtName.setTextColor(getContext().getResources().getColor(R.color.blue_title));
                usertype_tv.setVisibility(View.VISIBLE);
            } else {
                txtName.setTextColor(getContext().getResources().getColor(R.color.black));
                usertype_tv.setVisibility(View.GONE);
            }

            setUnreadCountOrDisturb(this, bean, msginfo);

            txtTime.setText(TimeToString.getTimeWx(bean.getUp_time()));
            viewIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getContext().startActivity(new Intent(getContext(), ChatActivity.class)
                            .putExtra(ChatActivity.AGM_TOUID, bean.getFrom_uid())
                            .putExtra(ChatActivity.AGM_TOGID, bean.getGid())
                    );
                }
            });
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swipeLayout.quickClose();
                    if (listener != null) {
                        listener.deleteSession(bean.getFrom_uid(), bean.getGid());
                    }
                }
            });
            viewIt.setBackgroundColor(bean.getIsTop() == 0 ? Color.WHITE : Color.parseColor("#ececec"));
            iv_disturb.setVisibility(bean.getIsMute() == 0 ? View.GONE : View.VISIBLE);
        }

        private void setUnreadCountOrDisturb(RCViewHolder holder, Session bean, MsgAllBean msg) {
            holder.sb.setButtonBackground(R.color.transparent);
            if (bean.getIsMute() == 1) {
                if (msg != null && !msg.isRead()) {
                    holder.iv_disturb_unread.setVisibility(View.VISIBLE);
                    holder.iv_disturb_unread.setBackgroundResource(R.drawable.shape_disturb_unread_bg);
                    holder.sb.setVisibility(View.GONE);
                } else {
                    holder.iv_disturb_unread.setVisibility(View.GONE);
                    holder.sb.setVisibility(View.VISIBLE);
                    holder.sb.setNum(bean.getUnread_count(), false);
                }
            } else {
                holder.iv_disturb_unread.setVisibility(View.GONE);
                holder.sb.setVisibility(View.VISIBLE);
                holder.sb.setNum(bean.getUnread_count(), false);
            }
        }

        private void loadGroupHeads(Session bean, MultiImageView imgHead) {
            Group gginfo = msgDao.getGroup4Id(bean.getGid());
            if (gginfo != null) {
                int i = gginfo.getUsers().size();
                i = i > 9 ? 9 : i;
                //头像地址
                List<String> headList = new ArrayList<>();
                for (int j = 0; j < i; j++) {
                    MemberUser userInfo = gginfo.getUsers().get(j);
                    headList.add(userInfo.getHead());
                }
                imgHead.setList(headList);
            }
        }

        private void showMessage(TextView txtInfo, String message, SpannableString spannableString) {
            if (spannableString == null) {
                if (StringUtil.isNotNull(message) && message.startsWith("@所有人  ")) {
                    message = message.replace("@所有人  ", "");
                }
                spannableString = ExpressionUtil.getExpressionString(getContext(), ExpressionUtil.DEFAULT_SMALL_SIZE, message);
            } else {
                spannableString = ExpressionUtil.getExpressionString(getContext(), ExpressionUtil.DEFAULT_SMALL_SIZE, spannableString);
            }
            txtInfo.setText(spannableString, TextView.BufferType.SPANNABLE);
        }

    }


    public class HeadViewHolder extends RecyclerView.ViewHolder {

        private net.cb.cb.library.view.ClearEditText edtSearch;
        private View viewNetwork;

        public HeadViewHolder(View convertView) {
            super(convertView);
            edtSearch = convertView.findViewById(R.id.edt_search);
            viewNetwork = convertView.findViewById(R.id.view_network);
        }
    }

    public View getViewNetwork() {
        return viewNetwork;
    }
}
