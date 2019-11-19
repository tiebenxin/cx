package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.GroupImageHead;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.GroupHeadImageUtil;

import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.StrikeButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @类名：消息搜索界面
 * @Date：2019/11/19
 * @by zjy
 * @备注：消息->搜索->跳转到此界面
 */

public class MsgSearchActivity extends AppActivity {

    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private net.cb.cb.library.view.ClearEditText edtSearch;
    private net.cb.cb.library.view.MultiListView mtListView;
    private List<Session> listData;//保存全部会话列表，但是后续会被搜索结果改变其值
    private List<Session> totalData;//保存默认全部会话列表数据不变
    private MsgDao msgDao;
    private UserDao userDao;
    private boolean onlineState = true;//判断网络状态 true在线 false离线

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_frd_grp);
        findViews();
        initEvent();
        getBundle();
    }

    private void findViews() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        edtSearch = findViewById(R.id.edt_search);
        mtListView = findViewById(R.id.mtListView);
        listData = new ArrayList<>();
        totalData = new ArrayList<>();
        msgDao = new MsgDao();
        userDao = new UserDao();
    }

    private void initEvent() {
        actionbar.setTitle("消息搜索");
        edtSearch.setHint("搜索");
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        mtListView.init(new RecyclerViewAdapter());
        mtListView.getLoadView().setStateNormal();
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    taskSearch();
                } else if (event != null && (KeyEvent.KEYCODE_ENTER == event.getKeyCode() || KeyEvent.ACTION_DOWN == event.getAction())) {
                    taskSearch();
                }
                return false;
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtSearch.getText().toString().length() == 0) {
                    //搜索关键字为0的时候，重新显示全部消息
                    listData.clear();
                    listData.addAll(totalData);
                    mtListView.notifyDataSetChange();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    //页面跳转->数据传递
    private void getBundle() {
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().containsKey("conversation_data")) {
                    totalData.addAll((List<Session>) getIntent().getExtras().getSerializable("conversation_data"));
                }
                if (getIntent().getExtras().containsKey("online_state")) {
                    onlineState = getIntent().getExtras().containsKey("online_state");
                }
            }
        }
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<MsgSearchActivity.RecyclerViewAdapter.RCViewHolder> {


        @Override
        public int getItemCount() {
            return listData == null ? 0 : listData.size();
        }

        @Override
        public void onBindViewHolder(@NonNull MsgSearchActivity.RecyclerViewAdapter.RCViewHolder holder, int position, @NonNull List<Object> payloads) {
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
        public void onBindViewHolder(final MsgSearchActivity.RecyclerViewAdapter.RCViewHolder holder, int position) {
            final Session bean = listData.get(position);
            String icon = bean.getAvatar();
            String title = bean.getName();
            MsgAllBean msginfo = bean.getMessage();
            String name = bean.getSenderName();

            String info = "";
            if (msginfo != null) {
                info = msginfo.getMsg_typeStr();
            }
            if (bean.getType() == 0) {//单人
                if (StringUtil.isNotNull(bean.getDraft())) {
                    //                    info = "草稿:" + bean.getDraft();
                    SpannableStringBuilder style = new SpannableStringBuilder();
                    style.append("[草稿]" + bean.getDraft());
                    ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                    style.setSpan(protocolColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.txtInfo.setText(style);
                } else {
                    holder.txtInfo.setText(info);
                }

                Glide.with(getContext()).load(icon)
                        .apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);

            } else if (bean.getType() == 1) {//群
                int type = bean.getMessageType();
                if (type == 0 || type == 1) {
                    if (!TextUtils.isEmpty(bean.getAtMessage()) && !TextUtils.isEmpty(name)) {
                        info = name + bean.getAtMessage();
                    } else {
                        info = name + info;

                    }
                } else {//草稿除外
                    if (!TextUtils.isEmpty(info) && !TextUtils.isEmpty(name)) {
                        info = name + info;
                    }
                }
                switch (type) {
                    case 0:
                        if (StringUtil.isNotNull(bean.getAtMessage())) {
                            if (msginfo != null && msginfo.getMsg_type() == ChatEnum.EMessageType.AT) {
                                SpannableStringBuilder style = new SpannableStringBuilder();
                                style.append("[有人@你]" + info);
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                holder.txtInfo.setText(style);
                            } else {
                                SpannableStringBuilder style = new SpannableStringBuilder();
                                style.append("[有人@你]" + info);
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                holder.txtInfo.setText(style);
                            }
                        }
                        break;
                    case 1:
                        if (StringUtil.isNotNull(bean.getAtMessage())) {
                            if (msginfo == null || msginfo.getMsg_type() == null) {
                                return;
                            }
                            if (msginfo.getMsg_type() == ChatEnum.EMessageType.AT) {
                                SpannableStringBuilder style = new SpannableStringBuilder();
                                style.append("[@所有人]" + info);
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                holder.txtInfo.setText(style);
                            } else {
                                SpannableStringBuilder style = new SpannableStringBuilder();
                                style.append("[@所有人]" + info);
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                holder.txtInfo.setText(style);
                            }
                        }
                        break;
                    case 2:
                        if (StringUtil.isNotNull(bean.getDraft())) {
//                            info = "草稿:" + bean.getDraft();
                            SpannableStringBuilder style = new SpannableStringBuilder();
                            style.append("[草稿]" + bean.getDraft());
                            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                            style.setSpan(protocolColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            holder.txtInfo.setText(style);
                        } else {
                            holder.txtInfo.setText(info);
                        }
                        break;
                    default:
                        holder.txtInfo.setText(info);
                        break;
                }

                if (StringUtil.isNotNull(icon)) {
                    Glide.with(getContext()).load(icon)
                            .apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);
                } else {
                    if (bean.getType() == 1) {
                        String imgUrl = "";
                        try {
                            imgUrl = ((GroupImageHead) DaoUtil.findOne(GroupImageHead.class, "gid", bean.getGid())).getImgHeadUrl();
                        } catch (Exception e) {
                            creatAndSaveImg(bean, holder.imgHead);
                        }

//                        LogUtil.getLog().e("TAG", "----------" + imgUrl.toString());
                        if (StringUtil.isNotNull(imgUrl)) {
                            Glide.with(getContext()).load(imgUrl)
                                    .apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);
                        } else {

                            creatAndSaveImg(bean, holder.imgHead);

                        }
                    } else {
                        Glide.with(getContext()).load(icon)
                                .apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);
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
//                    if (bean.getUnread_count() > 0) {
//                        MessageManager.getInstance().setMessageChange(true);
//                    }

                }
            });
//            holder.viewIt.setBackgroundColor(bean.getIsTop() == 0 ? Color.WHITE : Color.parseColor("#f1f1f1"));
            holder.viewIt.setBackgroundColor(bean.getIsTop() == 0 ? Color.WHITE : Color.parseColor("#ececec"));
            holder.iv_disturb.setVisibility(bean.getIsMute() == 0 ? View.GONE : View.VISIBLE);

        }

        private void creatAndSaveImg(Session bean, ImageView imgHead) {
            Group gginfo = msgDao.getGroup4Id(bean.getGid());
            if (gginfo != null) {
                int i = gginfo.getUsers().size();
                i = i > 9 ? 9 : i;
                //头像地址
                String url[] = new String[i];
                for (int j = 0; j < i; j++) {
                    MemberUser userInfo = gginfo.getUsers().get(j);
                    url[j] = userInfo.getHead();
                }
                File file = GroupHeadImageUtil.synthesis(getContext(), url);
                Glide.with(getContext()).load(file)
                        .apply(GlideOptionsUtil.headImageOptions()).into(imgHead);

                MsgDao msgDao = new MsgDao();
                msgDao.groupHeadImgCreate(gginfo.getGid(), file.getAbsolutePath());
            }
        }


        //自动寻找ViewHold
        @Override
        public MsgSearchActivity.RecyclerViewAdapter.RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            MsgSearchActivity.RecyclerViewAdapter.RCViewHolder holder = new MsgSearchActivity.RecyclerViewAdapter.RCViewHolder(getLayoutInflater().inflate(R.layout.item_msg_session, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private ImageView imgHead;
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

    }

    private void taskSearch() {
        InputUtil.hideKeyboard(edtSearch);
        String key = edtSearch.getText().toString();
        if (key.length() <= 0)
            return;
        List<Session> temp = new ArrayList<>();
        //每次查询，将listData重置为默认数据
        listData.clear();
        listData.addAll(totalData);
        for (Session bean : listData) {
            String title = "";
            String info = "";
            MsgAllBean msginfo;
            if (bean.getType() == 0) {//单人


                UserInfo finfo = userDao.findUserInfo(bean.getFrom_uid());

                title = finfo.getName4Show();

                //获取最后一条消息
                msginfo = msgDao.msgGetLast4FUid(bean.getFrom_uid());
                if (msginfo != null) {
                    info = msginfo.getMsg_typeStr();
                }

            } else if (bean.getType() == 1) {//群
                Group ginfo = msgDao.getGroup4Id(bean.getGid());

                //获取最后一条群消息
                msginfo = msgDao.msgGetLast4Gid(bean.getGid());
                title = /*ginfo.getName()*/msgDao.getGroupName(bean.getGid());
                if (msginfo != null) {
                    info = msginfo.getMsg_typeStr();
                }
            }

            if (title.contains(key) || info.contains(key)) {
                bean.setUnread_count(0);
                temp.add(bean);
            }
        }
        listData = temp;

        mtListView.notifyDataSetChange();
    }


}
