package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.ClearEditText;
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.MultiListView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class FindFriendActivity extends AppActivity {

    private HeadView mHeadView;
    private ClearEditText mEdtSearch;
    private MultiListView mMtListView;
    private UserAction userAction;
    private List<UserInfo> userInfos;
    private FindFriendAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_frd_grp);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mEdtSearch = findViewById(R.id.edt_search);
        mMtListView = findViewById(R.id.mtListView);
        mMtListView.getLoadView().setStateNormal();
    }

    private void initEvent() {
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

        mEdtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_DOWN:
                            String content = mEdtSearch.getText().toString();

                            if (TextUtils.isEmpty(content)) {
                                ToastUtil.show(FindFriendActivity.this, "请输入关键字");
                            } else {
                                taskFindFriend(content);
                            }
                            return true;
                    }
                }
                return false;
            }
        });
    }


    private void initData() {
        userAction = new UserAction();
        userInfos = new ArrayList<>();
        adapter = new FindFriendAdapter();
        mMtListView.init(adapter);
    }


    private void taskFindFriend(String content) {
        userAction.getUserInfoByImid(content, new CallBack<ReturnBean<UserInfo>>(mMtListView) {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
                if (response.body() == null) {
                    return;
                }
                userInfos.clear();
                userInfos.add(response.body().getData());
                mMtListView.notifyDataSetChange(response);
            }
        });
    }


    class FindFriendAdapter extends RecyclerView.Adapter<FindFriendAdapter.FindFriendHolderView> {

        @Override
        public FindFriendHolderView onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = inflater.inflate(R.layout.item_find_friend, viewGroup, false);
            return new FindFriendHolderView(view);
        }

        @Override
        public void onBindViewHolder(FindFriendHolderView viewHolder, int i) {
            final UserInfo userInfo = userInfos.get(i);
            viewHolder.mImgHead.setImageURI(userInfo.getHead() + "");
            viewHolder.mTxtName.setText(userInfo.getName());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FindFriendActivity.this,UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.ID,userInfo.getUid());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (userInfos != null) {
               return userInfos.size();
            }
            return 0;
        }


        class FindFriendHolderView extends RecyclerView.ViewHolder {
            private SimpleDraweeView mImgHead;
            private TextView mTxtName;

            public FindFriendHolderView(@NonNull View itemView) {
                super(itemView);
                mImgHead = itemView.findViewById(R.id.img_head);
                mTxtName = itemView.findViewById(R.id.txt_name);
            }
        }

    }


}
