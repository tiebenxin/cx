package com.yanlong.im.chat.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.ui.UserInfoActivity;

import net.cb.cb.library.view.ActionbarView;

import java.util.ArrayList;
import java.util.List;

/***
 * 搜索群和好友
 */
public class SearchFriendGroupActivity extends Activity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private net.cb.cb.library.view.ClearEditText edtSearch;
    private net.cb.cb.library.view.MultiListView mtListView;
    private List<UserInfo> listDataUser = new ArrayList<>();
    private List<Group> listDataGroup = new ArrayList<>();
    private String key;


    //自动寻找控件
    private void findViews() {
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        edtSearch = (net.cb.cb.library.view.ClearEditText) findViewById(R.id.edt_search);
        mtListView = (net.cb.cb.library.view.MultiListView) findViewById(R.id.mtListView);
    }


    //自动生成的控件事件
    private void initEvent() {
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
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    taskSearch();
                }
                return false;
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_frd_grp);
        findViews();
        initEvent();
    }

    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        @Override
        public int getItemCount() {
            return listDataGroup.size() + listDataUser.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder holder, int position) {
            String name = "";
            String url = "";
            holder.viewTagGroup.setVisibility(View.GONE);
            holder.viewTagFried.setVisibility(View.GONE);
            if (listDataUser.size() > position) {
                if (position == 0) {
                    holder.viewTagGroup.setVisibility(View.GONE);
                    holder.viewTagFried.setVisibility(View.VISIBLE);
                }
                final UserInfo user = listDataUser.get(position);
                name = user.getName4Show();
                url = user.getHead();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), UserInfoActivity.class)
                                .putExtra(UserInfoActivity.ID, user.getUid()));
                    }
                });

            } else {
                if (position == listDataUser.size()) {
                    holder.viewTagGroup.setVisibility(View.VISIBLE);
                    holder.viewTagFried.setVisibility(View.GONE);
                }
                int p = position - listDataUser.size();
                final Group group = listDataGroup.get(p);
                name = group.getName();
                url = group.getAvatar();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), ChatActivity.class)
                                .putExtra(ChatActivity.AGM_TOGID, group.getGid())

                        );

                    }
                });
            }

            holder.txtName.setText(getSpan(name,key));
            holder.imgHead.setImageURI(Uri.parse("" + url));

        }


        private Spannable getSpan(String message, String condition) {
            if (!message.contains(condition)) {
                return new SpannableString(message);
            }
            SpannableString ss = new SpannableString(message);
            int start = message.indexOf(condition);
            int end = start + condition.length();
            ss.setSpan(new ForegroundColorSpan(Color.BLUE), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ss;
        }


        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(getLayoutInflater().inflate(R.layout.item_friend_group, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private LinearLayout viewTagFried;
            private LinearLayout viewTagGroup;
            private LinearLayout viewIt;
            private com.facebook.drawee.view.SimpleDraweeView imgHead;
            private TextView txtName;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                viewTagFried = (LinearLayout) convertView.findViewById(R.id.view_tag_fried);
                viewTagGroup = (LinearLayout) convertView.findViewById(R.id.view_tag_group);
                viewIt = (LinearLayout) convertView.findViewById(R.id.view_it);
                imgHead = (com.facebook.drawee.view.SimpleDraweeView) convertView.findViewById(R.id.img_head);
                txtName = (TextView) convertView.findViewById(R.id.txt_name);
            }

        }
    }

    private MsgAction msgAction = new MsgAction();
    private UserAction userAction = new UserAction();

    private void taskSearch() {
        key = edtSearch.getText().toString();
        if (key.length() <= 0)
            return;
        listDataUser = userAction.searchUser4key(key);
        listDataGroup = msgAction.searchGroup4key(key);
        mtListView.notifyDataSetChange();
    }
}
