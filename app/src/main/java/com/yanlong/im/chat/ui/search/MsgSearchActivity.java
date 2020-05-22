package com.yanlong.im.chat.ui.search;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.PatternUtil;
import com.yanlong.im.wight.avatar.MultiImageView;

import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.StrikeButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final String TYPE_FACE = "[动画表情]";
    private MsgSearchViewModel viewModel = new MsgSearchViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_frd_grp);
        findViews();
        getIntentData();
        initEvent();
        initObserver();
    }
    private void initObserver(){
        viewModel.key.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                viewModel.search(s);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSoftKeyword(edtSearch);
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
    private void getIntentData() {
        if (getIntent() != null) {
            onlineState = getIntent().getBooleanExtra("online_state", true);
            if (getIntent().getStringExtra("conversition_data") != null) {
                String json = getIntent().getStringExtra("conversition_data");
                totalData.addAll(new Gson().fromJson(json, new TypeToken<List<Session>>() {
                }.getType()));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestory(this);
    }
}
