package com.yanlong.im.chat.ui.search;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.chat.ui.SearchMsgActivity;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.wight.avatar.MultiImageView;

import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.view.StrikeButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/22 0022
 * @description
 */
public class MsgSearchAdapter extends RecyclerView.Adapter<MsgSearchAdapter.RCViewHolder> {

    public MsgSearchViewModel viewModel;
    private Context context;
    private SearchType searchType;

    public enum SearchType {
        ALL,//搜索所有
        FRIENDS,//搜索好友
        GROUPS,//搜索群
        SESSIONS//搜索会话
    }

    public MsgSearchAdapter(Context context, MsgSearchViewModel viewModel, SearchType type) {
        this.context = context;
        this.viewModel = viewModel;
        this.searchType = type;
    }

    /**
     * 搜索所有时，检查是否还有更多
     *
     * @param size
     * @return
     */
    public boolean checkAllSearchHasMore(int size) {
        return searchType == SearchType.ALL ? size >= viewModel.MIN_LIMIT : false;
    }

    /**
     * 搜索所有时，最大size为viewModel.MIN_LIMIT - 1 即3
     *
     * @param size
     * @return
     */
    public int getDisplaySize(int size) {
        if (searchType == SearchType.ALL) {
            return size < viewModel.MIN_LIMIT ? size : viewModel.MIN_LIMIT - 1;
        } else {
            return size;
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        switch (searchType) {
            case ALL:
                size = getDisplaySize(viewModel.getSearchFriendsSize()) + getDisplaySize(viewModel.getSearchGroupsSize())
                        + getDisplaySize(viewModel.getSearchSessionsSize());
                break;
            case GROUPS:
                size = getDisplaySize(viewModel.getSearchGroupsSize());
                break;
            case FRIENDS:
                size = getDisplaySize(viewModel.getSearchFriendsSize());
                break;
            case SESSIONS:
                size = getDisplaySize(viewModel.getSearchSessionsSize());
                break;

        }
        return size;
    }

    public boolean isFriendItem(int position) {
        boolean result = false;
        switch (searchType) {
            case ALL:
                result = position < getDisplaySize(viewModel.getSearchFriendsSize());
                break;
            case GROUPS:
                result = false;
                break;
            case FRIENDS:
                result = true;
                break;
            case SESSIONS:
                result = false;
                break;

        }
        return result;
    }

    public boolean isGroupItem(int position) {
        boolean result = false;
        switch (searchType) {
            case ALL:
                result = position < (getDisplaySize(viewModel.getSearchFriendsSize()) +
                        getDisplaySize(viewModel.getSearchGroupsSize()));
                break;
            case GROUPS:
                result = true;
                break;
            case FRIENDS:
                result = false;
                break;
            case SESSIONS:
                result = false;
                break;

        }
        return result;

    }

    /**
     * 搜索关键字标绿色
     *
     * @param text
     * @return
     */
    private SpannableString getSpannableString(String title, String text) {
        int index = text.toLowerCase().indexOf(viewModel.key.getValue().toLowerCase());
        SpannableString sp = null;
        if (TextUtils.isEmpty(title)) {
            sp = new SpannableString(text);
        } else {
            sp = new SpannableString(title + text);
        }
        if (index >= 0 && index < text.length()) {
            if (!TextUtils.isEmpty(title)) {
                index = index + title.length();
            }
            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.green_500));
            sp.setSpan(protocolColorSpan, index, index + viewModel.key.getValue().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return sp;
    }

    //自动生成控件事件
    @Override
    public void onBindViewHolder(final RCViewHolder holder, int position) {
        int firstPosition = 0;
        int lastPostion = 0;
        if (isFriendItem(position)) {//单人

            firstPosition = 0;
            lastPostion = getDisplaySize(viewModel.getSearchFriendsSize()) - 1;
            holder.tvMore.setVisibility(position == lastPostion && checkAllSearchHasMore(viewModel.getSearchFriendsSize()) ?
                    View.VISIBLE : View.GONE);
            holder.tvMore.setText("更多联系人");
            holder.tvTitle.setText("联系人");


            UserInfo userInfo = viewModel.searchFriends.get(position);
            List<String> head = new ArrayList<String>();
            head.add(userInfo.getHead());
            holder.imgHead.setList(head);
            holder.txtName.setText(getSpannableString(null, userInfo.getName4Show()));
            /*****好友 昵称/微信号包含****************************************************************************/
            if (userInfo.getName4Show().toLowerCase().contains(viewModel.key.getValue().toLowerCase())) {
                //1.名称包含关键字，则只显示名称，隐藏描述
                holder.vInfoPanel.setVisibility(View.GONE);
            } else {//2.名称不包含关键字，则说明昵称/微信号包含，显示第二行
                holder.vInfoPanel.setVisibility(View.VISIBLE);
                if (userInfo.getName().toLowerCase().contains(viewModel.key.getValue().toLowerCase())) {
                    holder.txtInfo.setText(getSpannableString("昵称：", userInfo.getName()));
                } else {
                    holder.txtInfo.setText(getSpannableString("常信号：", userInfo.getImid()));
                }
            }
            holder.viewIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ChatActivity.class)
                            .putExtra(ChatActivity.AGM_TOUID, userInfo.getUid())
                    );
                }
            });
            holder.tvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, FriendsSearchActivity.class)
                            .putExtra(SearchMsgActivity.AGM_SEARCH_KEY, viewModel.key.getValue())
                    );
                }
            });
        } else if (isGroupItem(position)) {

            /*****群 昵称/成员包含****************************************************************************/
            holder.tvTitle.setText("群聊");
            firstPosition = getDisplaySize(viewModel.getSearchFriendsSize());
            lastPostion = firstPosition + getDisplaySize(viewModel.getSearchGroupsSize()) - 1;
            holder.tvMore.setVisibility(position == lastPostion && checkAllSearchHasMore(viewModel.getSearchGroupsSize()) ?
                    View.VISIBLE : View.GONE);
            holder.tvMore.setText("更多群聊");

            holder.vInfoPanel.setVisibility(View.VISIBLE);
            Group group = viewModel.searchGroups.get(position - getDisplaySize(viewModel.getSearchFriendsSize()));
            int i = group.getUsers().size();
            i = i > 9 ? 9 : i;
            //头像地址
            List<String> headList = new ArrayList<>();
            String groupName = "";
            int headMaxPosition = Math.min(group.getUsers().size(), 14);
            for (int j = 0; j < headMaxPosition; j++) {
                MemberUser userInfo = group.getUsers().get(j);
                if (j < i) {
                    headList.add(userInfo.getHead());
                }
                groupName += StringUtil.getUserName(/*info.getMkName()*/"", userInfo.getMembername(), userInfo.getName(), userInfo.getUid()) + "、";
            }
            if (TextUtils.isEmpty(group.getName())) {
                //去掉最后一个顿号
                groupName = groupName.substring(0, groupName.length() - 1);
                groupName = groupName.length() > 14 ? StringUtil.splitEmojiString2(groupName, 0, 14) : groupName;
                groupName += "的群";
            } else {
                groupName = group.getName();
            }
            holder.imgHead.setList(headList);
            holder.txtName.setText(getSpannableString(null, groupName));
            if (groupName.toLowerCase().contains(viewModel.key.getValue().toLowerCase())) {
                //3.群名称包含关键字，则只显示名称，隐藏描述
                holder.vInfoPanel.setVisibility(View.GONE);
            } else {//4.群名称不包含关键字，则说明群成员包含，显示第二行
                holder.vInfoPanel.setVisibility(View.VISIBLE);
                String memeberName = null;
                for (MemberUser user : group.getUsers()) {
                    if (user.getMembername().toLowerCase().contains(viewModel.key.getValue().toLowerCase())) {
                        memeberName = user.getMembername();
                        break;
                    } else if (user.getName().toLowerCase().contains(viewModel.key.getValue().toLowerCase())) {
                        memeberName = user.getName();
                        break;
                    }
                }
                holder.txtInfo.setText(getSpannableString("包含：", memeberName));
            }
            holder.viewIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ChatActivity.class)
                            .putExtra(ChatActivity.AGM_TOGID, group.getGid())
                    );
                }
            });
            holder.tvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, GroupsSearchActivity.class)
                            .putExtra(SearchMsgActivity.AGM_SEARCH_KEY, viewModel.key.getValue())
                    );
                }
            });
        } else {
            /*****聊天记录 内容包含****************************************************************************/
            holder.tvTitle.setText("聊天记录");
            firstPosition = getDisplaySize(viewModel.getSearchFriendsSize()) + getDisplaySize(viewModel.getSearchGroupsSize());
            lastPostion = getItemCount() - 1;
            holder.tvMore.setVisibility(position == lastPostion && checkAllSearchHasMore(viewModel.getSearchSessionsSize()) ?
                    View.VISIBLE : View.GONE);
            holder.tvMore.setText("更多聊天记录");

            holder.vInfoPanel.setVisibility(View.VISIBLE);
            int p = position - getDisplaySize(viewModel.getSearchFriendsSize()) - getDisplaySize(viewModel.getSearchGroupsSize());
            SessionDetail sessionDetail = viewModel.searchSessions.get(p);

            //头像地址
            List<String> headList = new ArrayList<>();
            if (StringUtil.isNotNull(sessionDetail.getAvatar())) {
                headList.add(sessionDetail.getAvatar());
            } else {
                List<String> avatarList = null;
                String avatarListString = sessionDetail.getAvatarList();
                if (avatarListString != null) {
                    avatarList = Arrays.asList(avatarListString.split(","));
                }
                if (avatarList == null || avatarList.size() == 0) {//没有头像，设个默认的，否则会出现头像为全灰色
                    avatarList = new ArrayList<>();
                    avatarList.add("");
                }
                headList.addAll(avatarList);
            }
            holder.imgHead.setList(headList);
            holder.txtName.setText(sessionDetail.getName());


            if (viewModel.sessionSearch.containsKey(sessionDetail.getSid())) {
                MsgSearchViewModel.SessionSearch sessionSearch = viewModel.sessionSearch.get(sessionDetail.getSid());
                if (sessionSearch.getCount() == 1) {//1条记录，直接进入聊天界面
                    String msg = SocketData.getMsg(sessionSearch.getMsgAllBean(), viewModel.key.getValue());
                    hightKey(holder.txtInfo, msg, sessionSearch.getMsgAllBean().getMsg_typeTitle());
                    holder.viewIt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, ChatActivity.class)
                                    .putExtra(ChatActivity.AGM_TOGID, sessionSearch.getGid())
                                    .putExtra(ChatActivity.AGM_TOUID, sessionSearch.getUid())
                                    .putExtra(ChatActivity.SEARCH_TIME, sessionSearch.getMsgAllBean().getTimestamp())
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            );
                        }
                    });

                } else {//多于1条聊天记录，进入搜索详情
                    holder.txtInfo.setText(sessionSearch.getCount() + "条相关的聊天记录");
                    holder.viewIt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, SearchMsgActivity.class)
                                    .putExtra(SearchMsgActivity.AGM_GID, sessionSearch.getGid())
                                    .putExtra(SearchMsgActivity.AGM_FUID, sessionSearch.getUid())
                                    .putExtra(SearchMsgActivity.AGM_SEARCH_KEY, viewModel.key.getValue())
                            );
                        }
                    });
                }
                holder.tvMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, SessionSearchActivity.class)
                                .putExtra(SearchMsgActivity.AGM_SEARCH_KEY, viewModel.key.getValue())
                        );
                    }
                });
            } else {
                holder.txtInfo.setText("");
                holder.viewIt.setOnClickListener(null);
                holder.tvMore.setOnClickListener(null);
            }
        }

        //第一个显示标题
        holder.tvTitle.setVisibility(position == firstPosition ? View.VISIBLE : View.GONE);
        holder.vTitleLine.setVisibility(position == firstPosition ? View.VISIBLE : View.GONE);
        //最后一项，显示底部灰色条
        holder.vBottomLine.setVisibility(position == lastPostion ? View.VISIBLE : View.GONE);

    }

    /**
     * 高亮显示搜索关键字
     * 超出一行，原则上让搜索关键字显示在中间，已经到字尾了，就以字尾显示
     *
     * @param tvContent
     * @param msg
     */
    private void hightKey(TextView tvContent, String msg, String title) {
        String key = viewModel.key.getValue();
        final int index = msg.toLowerCase().indexOf(key.toLowerCase());
        if (index >= 0) {
            int mindex = title.length() + index;
            SpannableString style = new SpannableString(title + msg);
            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.green_500));
            style.setSpan(protocolColorSpan, mindex, mindex + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            showMessage(tvContent, msg, style);
        } else {
            showMessage(tvContent, msg, new SpannableString(msg));
        }

        if (tvContent.getLayout() == null) {
            //getLayout() 开始会为null,post显示后会重新加载
            tvContent.post(new Runnable() {
                @Override
                public void run() {
                    showEllipsis(tvContent, msg, key, index, title);
                }
            });
        } else {
            showEllipsis(tvContent, msg, key, index, title);
        }
    }

    /**
     * 多于一行被隐藏处理
     *
     * @param tvContent
     * @param msg
     * @param key
     * @param index
     */
    private void showEllipsis(TextView tvContent, String msg, String key, int index, String title) {
        try {
            if (tvContent.getLayout() == null) return;
            //被隐藏的字数
            int ellipsisCount = tvContent.getLayout().getEllipsisCount(0);
            //显示的字数
            int showCount = msg.length() - ellipsisCount;
            if (showCount > 0 && showCount < index) {//超出文本了
                //原则上让搜索关键字显示在中间，已经到字尾了，就以字尾显示
                String subMsg = msg.substring(Math.min(index - showCount / 2, msg.length() - showCount + 1));
                title = title + "...";
                //下标数+三个点...的位置，不直接拼字符串，防止key中包含...
                int mindex = title.length() + subMsg.toLowerCase().indexOf(key.toLowerCase());

                SpannableString style = new SpannableString(title + subMsg);
                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.green_500));
                style.setSpan(protocolColorSpan, mindex, mindex + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                showMessage(tvContent, subMsg, style);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 显示表情内容
     *
     * @param message
     */
    protected void showMessage(TextView txtInfo, String message, SpannableString spannableString) {
        if (spannableString == null) {
            spannableString = ExpressionUtil.getExpressionString(context, ExpressionUtil.DEFAULT_SMALL_SIZE, message);
        } else {
            spannableString = ExpressionUtil.getExpressionString(context, ExpressionUtil.DEFAULT_SMALL_SIZE, spannableString);
        }
        txtInfo.setText(spannableString, TextView.BufferType.SPANNABLE);

    }

    //自动寻找ViewHold
    @Override
    public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
        RCViewHolder holder = new RCViewHolder(LayoutInflater.from(context).inflate(R.layout.item_msg_search, view, false));
        return holder;
    }


    //自动生成ViewHold
    public class RCViewHolder extends RecyclerView.ViewHolder {
        private MultiImageView imgHead;
        private StrikeButton sb;

        private View viewIt, vInfoPanel, vBottomLine, vTitleLine;
        private TextView txtName;
        private TextView txtInfo, tvTitle, tvMore;
//            private final TextView tv_num;

        //自动寻找ViewHold
        public RCViewHolder(View convertView) {
            super(convertView);
            tvMore = convertView.findViewById(R.id.txt_more);
            tvTitle = convertView.findViewById(R.id.tvTitle);
            vBottomLine = convertView.findViewById(R.id.vBottomLine);
            vTitleLine = convertView.findViewById(R.id.vTitleLine);
            imgHead = convertView.findViewById(R.id.img_head);
            sb = convertView.findViewById(R.id.sb);
            viewIt = convertView.findViewById(R.id.view_it);
            txtName = convertView.findViewById(R.id.txt_name);
            txtInfo = convertView.findViewById(R.id.txt_info);
            vInfoPanel = convertView.findViewById(R.id.ll_info);
        }

    }

}
