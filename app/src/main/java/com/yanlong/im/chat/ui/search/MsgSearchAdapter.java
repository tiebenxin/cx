package com.yanlong.im.chat.ui.search;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.PatternUtil;
import com.yanlong.im.wight.avatar.MultiImageView;

import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.view.StrikeButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/22 0022
 * @description
 */
public class MsgSearchAdapter extends RecyclerView.Adapter<MsgSearchAdapter.RCViewHolder> {

    @Override
    public int getItemCount() {
        return listData == null ? 0 : listData.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RCViewHolder holder, int position, @NonNull List<Object> payloads) {
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

    //自动生成控件事件
    @Override
    public void onBindViewHolder(final RCViewHolder holder, int position) {
        final Session bean = listData.get(position);

        String icon = "";
        String title = "";
        MsgAllBean msginfo = null;
        String name = "";
        List<String> avatarList = null;
        String info = "";
        if (sessionMoresPositions.containsKey(bean.getSid())) {
            Integer index = sessionMoresPositions.get(bean.getSid());
            if (index != null && index >= 0) {
                //从session详情对象中获取
                icon = sessionDetails.get(index).getAvatar();
                title = sessionDetails.get(index).getName();
                msginfo = sessionDetails.get(index).getMessage();
                name = sessionDetails.get(index).getSenderName();
                String avatarListString = sessionDetails.get(index).getAvatarList();
                if (avatarListString != null) {
                    avatarList = Arrays.asList(avatarListString.split(","));
                }
                if (name == null) name = "";
                info = sessionDetails.get(index).getMessageContent();
            }
        }

        // 头像集合
        List<String> headList = new ArrayList<>();


        if (bean.getType() == 0) {//单人
            if (StringUtil.isNotNull(bean.getDraft())) {
                SpannableString style = new SpannableString("[草稿]" + bean.getDraft());
                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                style.setSpan(protocolColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                showMessage(holder.txtInfo, bean.getDraft(), style, msginfo == null && TextUtils.isEmpty(bean.getDraft()));
            } else {
                // 判断是否是动画表情
                if (info.length() == PatternUtil.FACE_CUSTOMER_LENGTH) {
                    Pattern patten = Pattern.compile(PatternUtil.PATTERN_FACE_CUSTOMER, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
                    Matcher matcher = patten.matcher(info);
                    if (matcher.matches()) {
                        holder.txtInfo.setText(TYPE_FACE);
                    } else {
                        showMessage(holder.txtInfo, info, null, msginfo == null && TextUtils.isEmpty(bean.getDraft()));
                    }
                } else {
                    showMessage(holder.txtInfo, info, null, msginfo == null && TextUtils.isEmpty(bean.getDraft()));
                }
            }
            headList.add(icon);
            holder.imgHead.setList(headList);

        } else if (bean.getType() == 1) {//群
            int type = bean.getMessageType();
            if (type == 0 || type == 1) {
                info = name + info;
            } else {//草稿除外
                if (!TextUtils.isEmpty(info) && !TextUtils.isEmpty(name)) {
                    info = name + info;
                }
            }
            switch (type) {
                case 0:
                case 1: {
                    SpannableString style = new SpannableString("[有人@我]" + info);
                    ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                    style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    showMessage(holder.txtInfo, info, style, msginfo == null && TextUtils.isEmpty(bean.getDraft()));
                }
                break;
                case 2:
                    if (StringUtil.isNotNull(bean.getDraft())) {
                        SpannableString style = new SpannableString("[草稿]" + bean.getDraft());
                        ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                        style.setSpan(protocolColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        showMessage(holder.txtInfo, bean.getDraft(), style, msginfo == null && TextUtils.isEmpty(bean.getDraft()));
                    } else {
                        // 判断是否是动画表情
                        Pattern patten = Pattern.compile(PatternUtil.PATTERN_FACE_CUSTOMER, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
                        Matcher matcher = patten.matcher(info);
                        if (matcher.find()) {
                            info = info.substring(0, info.indexOf("["));
                            holder.txtInfo.setText(info + " " + TYPE_FACE);
                        } else {
                            showMessage(holder.txtInfo, info, null, msginfo == null && TextUtils.isEmpty(bean.getDraft()));
                        }
                    }
                    break;
                default:
                    // 判断是否是动画表情
                    Pattern patten = Pattern.compile(PatternUtil.PATTERN_FACE_CUSTOMER, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
                    Matcher matcher = patten.matcher(info);
                    if (matcher.find()) {
                        info = info.substring(0, info.indexOf("["));
                        holder.txtInfo.setText(info + " " + TYPE_FACE);
                    } else {
                        showMessage(holder.txtInfo, info, null, msginfo == null && TextUtils.isEmpty(bean.getDraft()));
                    }
                    break;
            }

            if (StringUtil.isNotNull(icon)) {
                headList.add(icon);
                holder.imgHead.setList(headList);
            } else {
                if (avatarList != null && avatarList.size() > 0) {
                    holder.imgHead.setList(avatarList);
                } else {
                    loadGroupHeads(bean, holder.imgHead);
                }
            }
        }

        holder.txtName.setText(title);
        holder.txtTime.setText(TimeToString.getTimeWx(bean.getUp_time()));
        //搜索界面，默认不显示红点
        holder.iv_disturb_unread.setVisibility(View.GONE);
        holder.sb.setVisibility(View.GONE);
        //搜索界面，不允许item横向滑动删除
        holder.swipeLayout.setSwipeEnable(false);

        holder.viewIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChatActivity.class)
                        .putExtra(ChatActivity.AGM_TOUID, bean.getFrom_uid())
                        .putExtra(ChatActivity.AGM_TOGID, bean.getGid())
                        .putExtra(ChatActivity.ONLINE_STATE, onlineState)
                );
            }
        });
//            holder.viewIt.setBackgroundColor(bean.getIsTop() == 0 ? Color.WHITE : Color.parseColor("#f1f1f1"));
        holder.viewIt.setBackgroundColor(bean.getIsTop() == 0 ? Color.WHITE : Color.parseColor("#ececec"));
        holder.iv_disturb.setVisibility(bean.getIsMute() == 0 ? View.GONE : View.VISIBLE);

    }

    //加载群头像
    public synchronized void loadGroupHeads(Session bean, MultiImageView imgHead) {
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


    //自动寻找ViewHold
    @Override
    public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
        MsgSearchActivity.RecyclerViewAdapter.RCViewHolder holder = new MsgSearchActivity.RecyclerViewAdapter.RCViewHolder(getLayoutInflater().inflate(R.layout.item_msg_session, view, false));
        return holder;
    }


    //自动生成ViewHold
    public class RCViewHolder extends RecyclerView.ViewHolder {
        private MultiImageView imgHead;
        private StrikeButton sb;

        private View viewIt;
        private SwipeMenuLayout swipeLayout;
        private TextView txtName;
        private TextView txtInfo;
        private TextView txtTime;
        private final ImageView iv_disturb, iv_disturb_unread;
//            private final TextView tv_num;

        //自动寻找ViewHold
        public RCViewHolder(View convertView) {
            super(convertView);
            imgHead = convertView.findViewById(R.id.img_head);
            swipeLayout = convertView.findViewById(R.id.swipeLayout);
            sb = convertView.findViewById(R.id.sb);
            viewIt = convertView.findViewById(R.id.view_it);
            txtName = convertView.findViewById(R.id.txt_name);
            txtInfo = convertView.findViewById(R.id.txt_info);
            txtTime = convertView.findViewById(R.id.txt_time);
            iv_disturb = convertView.findViewById(R.id.iv_disturb);
//                tv_num = convertView.findViewById(R.id.tv_num);
            iv_disturb_unread = convertView.findViewById(R.id.iv_disturb_unread);
        }

    }

    /**
     * 显示草稿内容
     *
     * @param message
     */
    protected void showMessage(TextView txtInfo, String message, SpannableString spannableString, boolean msgIsClear) {
        if (msgIsClear) {
            txtInfo.setText("");
        } else {
            if (spannableString == null) {
                spannableString = ExpressionUtil.getExpressionString(getContext(), ExpressionUtil.DEFAULT_SMALL_SIZE, message);
            } else {
                spannableString = ExpressionUtil.getExpressionString(getContext(), ExpressionUtil.DEFAULT_SMALL_SIZE, spannableString);
            }
            txtInfo.setText(spannableString, TextView.BufferType.SPANNABLE);
        }
    }


}
