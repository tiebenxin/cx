package com.yanlong.im.chat.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nim_lib.config.Preferences;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yanlong.im.MainViewModel;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.chat.ui.search.MsgSearchActivity;
import com.yanlong.im.repository.MainRepository;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.wight.avatar.MultiImageView;

import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.EllipsizedTextView;
import net.cb.cb.library.view.StrikeButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/4/1 0001
 * @description
 */
public class MsgMainFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    private View mHeaderView;
    public View viewNetwork;
    //记录当前要删除的项
    private SwipeMenuLayout currentDelSwipeLayout = null;
    private Context context;
    private MainViewModel viewModel;

    public MsgMainFragmentAdapter(Context context, MainViewModel viewModel, View headerView) {
        this.context = context;
        this.viewModel = viewModel;
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup view, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER)
            return new HeadViewHolder(mHeaderView);
        RCViewHolder holder = new RCViewHolder(LayoutInflater.from(context).inflate(R.layout.item_msg_session, view, false));
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) return TYPE_NORMAL;
        if (position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() { // TODO　增加文件头，默认的位置加1
        return viewModel.getSessionSize() + 1;
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
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    //自动生成控件事件
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        try {
            if (viewHolder instanceof RCViewHolder) {
                RCViewHolder holder = (RCViewHolder) viewHolder;
                if (viewModel.isNeedCloseSwipe.getValue()) {
                    holder.swipeLayout.quickClose();
                }
                final Session bean = viewModel.getSession().get(position - 1);
                String sid = bean.getSid();
                String icon = "";
                String title = "";
                MsgAllBean msginfo = null;
                String name = "";
                List<String> avatarList = null;
                String info = "";
                if (viewModel.sessionMoresPositions.containsKey(bean.getSid())) {
                    Integer index = viewModel.sessionMoresPositions.get(bean.getSid());
                    if (index != null && index >= 0) {
                        //从session详情对象中获取
                        icon = viewModel.sessionMores.get(index).getAvatar();
                        title = viewModel.sessionMores.get(index).getName();
                        msginfo = viewModel.sessionMores.get(index).getMessage();
                        name = viewModel.sessionMores.get(index).getSenderName();
                        String avatarListString = viewModel.sessionMores.get(index).getAvatarList();
                        if (avatarListString != null) {
                            avatarList = Arrays.asList(avatarListString.split(","));
                        }
                        if (name == null) name = "";
                        info = viewModel.sessionMores.get(index).getMessageContent();
                    }
                }

                // 头像集合
                List<String> headList = new ArrayList<>();


                int type = bean.getMessageType();
                if (bean.getType() == 0) {//单人
                    if (type == ChatEnum.ESessionType.ENVELOPE_FAIL) {
                        SpannableString style = new SpannableString("[红包发送失败]" + info);
                        ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.red_all_notify));
                        style.setSpan(protocolColorSpan, 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        showMessage(holder.txtInfo, info, style);
                    } else {
                        if (StringUtil.isNotNull(bean.getDraft())) {
                            SpannableString style = new SpannableString("[草稿]" + bean.getDraft());
                            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.red_all_notify));
                            style.setSpan(protocolColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            showMessage(holder.txtInfo, bean.getDraft(), style);
                        } else {
                            showMessage(holder.txtInfo, info, null);
                        }
                    }
                    headList.add(icon);
                    holder.imgHead.setList(headList);

                } else if (bean.getType() == 1) {//群
                    if (type == 0) {
                        info = name + info;
                    } else if (type == 1) {
                        info = name + info;
                    } else if (msginfo != null && (ChatEnum.EMessageType.CHANGE_SURVIVAL_TIME + "").equals(msginfo.getMsg_type() + "")) {
                        //阅后即焚不通知 不显示谁发的 肯定是群主修改的
                        // info=info;
                    } else if (!TextUtils.isEmpty(info) && !TextUtils.isEmpty(name)) {//草稿除外
                        info = name + info;
                    }
                    // 处理公告...问题
                    info = info.replace("\r\n", "  ");

                    switch (type) {
                        case 0:
                        case 1: {
                            if (StringUtil.isNotNull(bean.getAtMessage())) {
                                SpannableString style = new SpannableString("[有人@我]" + info);
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.red_all_notify));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                showMessage(holder.txtInfo, info, style);
                            } else {
                                showMessage(holder.txtInfo, info, null);
                            }
                        }
                        break;
                        case 2:
                            if (StringUtil.isNotNull(bean.getDraft())) {
                                SpannableString style = new SpannableString("[草稿]" + bean.getDraft());
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.red_all_notify));
                                style.setSpan(protocolColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                showMessage(holder.txtInfo, bean.getDraft(), style);
                            } else {
                                showMessage(holder.txtInfo, info, null);

                            }
                            break;
                        case 3:
                            SpannableString style = new SpannableString("[红包发送失败]" + info);
                            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.red_all_notify));
                            style.setSpan(protocolColorSpan, 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            showMessage(holder.txtInfo, info, style);
                            break;
                        case 4:
                            try {
                                int count = new MainRepository().getRemindCount(Preferences.GROUP_FRIEND_APPLY, bean.getGid());
                                if (count > 0) {
                                    if (count > 99) {
                                        count = 99;
                                    }
                                    SpannableString styleJoin = new SpannableString("[" + count + "条进群申请]" + info);
                                    ForegroundColorSpan joinSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.red_all_notify));
                                    if (count < 10) {
                                        styleJoin.setSpan(joinSpan, 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    } else if (count < 100) {
                                        styleJoin.setSpan(joinSpan, 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    }
                                    showMessage(holder.txtInfo, info, styleJoin);
                                } else {
                                    showMessage(holder.txtInfo, info, null);
                                }
                            } catch (Exception e) {
                                showMessage(holder.txtInfo, info, null);
                            }
                            break;
                        default:
                            showMessage(holder.txtInfo, info, null);
                            break;
                    }

                    if (StringUtil.isNotNull(icon)) {
                        headList.add(icon);
                        holder.imgHead.setList(headList);
                    } else {
                        if (avatarList == null || avatarList.size() == 0) {//没有头像，设个默认的，否则会出现头像为全灰色
                            avatarList = new ArrayList<>();
                            avatarList.add("");
                        }
                        holder.imgHead.setList(avatarList);
                    }
                }

                holder.txtName.setText(title);
                if (bean.isSystemUser()) {
                    //系统会话
                    holder.txtName.setTextColor(context.getResources().getColor(R.color.blue_title));
                    holder.usertype_tv.setVisibility(View.VISIBLE);
                } else {
                    holder.txtName.setTextColor(context.getResources().getColor(R.color.black));
                    holder.usertype_tv.setVisibility(View.GONE);
                }
                setUnreadCountOrDisturb(holder, bean, msginfo);

                holder.txtTime.setText(TimeToString.getTimeWx(bean.getUp_time()));


                holder.viewIt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ViewUtils.isFastDoubleClick()) {
                            return;
                        }
                        try {
                            context.startActivity(new Intent(context, ChatActivity.class)
                                    .putExtra(ChatActivity.AGM_TOUID, bean.getFrom_uid())
                                    .putExtra(ChatActivity.AGM_TOGID, bean.getGid())
                                    .putExtra(ChatActivity.ONLINE_STATE, viewModel.onlineState.getValue())
                            );
                        } catch (Exception e) {

                        }
                    }
                });
                holder.btnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.swipeLayout.quickClose();
                        //删除数据
                        viewModel.currentDeleteSid.setValue(sid);
                    }
                });
                MsgAllBean finalMsginfo = msginfo;
                holder.btnMarkRead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //标记已读未读
                        holder.swipeLayout.quickClose();
                        if (bean != null) {
                            int read = 0;
                            if (bean.getIsMute() == 1) {
                                if (finalMsginfo != null) {
                                    if (!finalMsginfo.isRead() || bean.getMarkRead() > 0) {
                                        read = 0;
                                    } else {
                                        read = 1;
                                    }
                                    if (MyAppLication.INSTANCE().repository != null) {
                                        MyAppLication.INSTANCE().repository.markSessionRead(sid, read, finalMsginfo.getMsg_id());
                                    }
                                }
                            } else {
                                read = (bean.getMarkRead() + bean.getUnread_count()) > 0 ? 0 : 1;
                                if (finalMsginfo != null) {
                                    if (MyAppLication.INSTANCE().repository != null) {
                                        MyAppLication.INSTANCE().repository.markSessionRead(sid, read, finalMsginfo.getMsg_id());
                                    }
                                }
                            }

                        }
                    }
                });
                holder.viewIt.setBackgroundColor(bean.getIsTop() == 0 ? Color.WHITE : Color.parseColor("#F2F2F2"));
                holder.iv_disturb.setVisibility(bean.getIsMute() == 0 ? View.INVISIBLE : View.VISIBLE);
            } else if (viewHolder instanceof HeadViewHolder) {
                HeadViewHolder headHolder = (HeadViewHolder) viewHolder;
                headHolder.edtSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, MsgSearchActivity.class);
                        intent.putExtra("online_state", viewModel.onlineState.getValue());
                        context.startActivity(intent);
                    }
                });
                viewNetwork = headHolder.viewNetwork;
            }
        } catch (Exception e) {
        }

    }

    private void setUnreadCountOrDisturb(RCViewHolder holder, Session bean, MsgAllBean msg) {
        holder.sb.setButtonBackground(R.color.transparent);
        if (bean.getIsMute() == 1) {
            if (msg != null && (!msg.isRead() || bean.getMarkRead() == 1)) {
                holder.iv_disturb_unread.setVisibility(View.VISIBLE);
                holder.iv_disturb_unread.setBackgroundResource(R.drawable.shape_disturb_unread_bg);
                holder.sb.setVisibility(View.GONE);
                holder.btnMarkRead.setText("标记已读");
            } else {
                holder.btnMarkRead.setText("标记未读");
                holder.iv_disturb_unread.setVisibility(View.GONE);
                holder.sb.setVisibility(View.VISIBLE);
                holder.sb.setNum(bean.getUnread_count(), false);
            }
        } else {
            int count = bean.getUnread_count() + bean.getMarkRead();
            holder.iv_disturb_unread.setVisibility(View.GONE);
            holder.sb.setVisibility(View.VISIBLE);
            holder.sb.setNum(count, false);
            if (count > 0) {
                holder.btnMarkRead.setText("标记已读");
            } else {
                holder.btnMarkRead.setText("标记未读");
            }
        }
    }

    /**
     * 富文本显示最后一條内容
     *
     * @param txtInfo
     * @param message
     * @param spannableString
     */
    protected void showMessage(TextView txtInfo, String message, SpannableString spannableString) {
        if (spannableString == null) {
//                if (StringUtil.isNotNull(message) && message.startsWith("@所有人  ")) {
//                    message = message.replace("@所有人  ", "");
//                }
            spannableString = ExpressionUtil.getExpressionString(context, ExpressionUtil.DEFAULT_SMALL_SIZE, message);
        } else {
            spannableString = ExpressionUtil.getExpressionString(context, ExpressionUtil.DEFAULT_SMALL_SIZE, spannableString);
        }
        txtInfo.setText(spannableString, TextView.BufferType.SPANNABLE);
//            txtInfo.invalidate();
    }

    public class RCViewHolder extends RecyclerView.ViewHolder {
        private MultiImageView imgHead;
        private StrikeButton sb;

        private View viewIt;
        private Button btnDel;
        private SwipeMenuLayout swipeLayout;
        private TextView txtName;
        private EllipsizedTextView txtInfo;
        private TextView txtTime;
        private final ImageView iv_disturb, iv_disturb_unread;
        //            private final TextView tv_num;
        private TextView usertype_tv;
        private Button btnMarkRead;

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
//                tv_num = convertView.findViewById(R.id.tv_num);
            iv_disturb_unread = convertView.findViewById(R.id.iv_disturb_unread);
            usertype_tv = convertView.findViewById(R.id.usertype_tv);
            btnMarkRead = convertView.findViewById(R.id.btn_read);
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

}
