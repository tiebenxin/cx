package com.yanlong.im.chat.ui.search;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

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
    private final String TYPE_FACE = "[动画表情]";
    private MsgSearchViewModel viewModel = new MsgSearchViewModel();
    private MsgSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_frd_grp);
        findViews();
        initEvent();
        initObserver();
    }
    private void initObserver(){
        viewModel.key.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                viewModel.clear();
                viewModel.search(s);
                mtListView.getListView().getAdapter().notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        adapter = new MsgSearchAdapter(this,viewModel);
        mtListView.init(adapter);
        mtListView.getLoadView().setStateNormal();
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    String key = edtSearch.getText().toString();
                    viewModel.key.setValue(key);
                    InputUtil.hideKeyboard(MsgSearchActivity.this);
                } else if (event != null && (KeyEvent.KEYCODE_ENTER == event.getKeyCode() || KeyEvent.ACTION_DOWN == event.getAction())) {
                    String key = edtSearch.getText().toString();
                    viewModel.key.setValue(key);
                    InputUtil.hideKeyboard(MsgSearchActivity.this);
                }
                return false;
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestory(this);
    }
}