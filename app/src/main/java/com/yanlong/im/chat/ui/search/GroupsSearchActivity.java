package com.yanlong.im.chat.ui.search;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.ui.SearchMsgActivity;

import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

/**主页搜索-搜索群
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/17 0017
 * @description
 */
public class GroupsSearchActivity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private net.cb.cb.library.view.ClearEditText edtSearch;
    private net.cb.cb.library.view.MultiListView mtListView;
    private MsgSearchViewModel viewModel;
    private MsgSearchAdapter adapter;
    private String searchKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getViewModelStore(), ViewModelProvider.AndroidViewModelFactory.getInstance(MyAppLication.getInstance())).get(MsgSearchViewModel.class);
        setContentView(R.layout.activity_search_frd_grp);
        findViews();
        initEvent();
        initObserver();
    }
    private void initObserver() {
        viewModel.key.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                viewModel.clear();
                viewModel.searchGroups(s);
            }
        });
        viewModel.isLoadNewRecord.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {//关闭列表加载动画
                    mtListView.getLoadView().setStateNormal();
                    //必须在setEvent后调用
                    mtListView.getSwipeLayout().setEnabled(false);
                } else {//显示列表加载动画
                    mtListView.getLoadView().setStateLoading();
                    //必须在setEvent后调用
                    mtListView.getSwipeLayout().setEnabled(true);
                }
                if(viewModel.isLoadCompleted(MsgSearchAdapter.SearchType.GROUPS)){
                    mtListView.notifyDataSetChange();
                }else{
                    mtListView.getListView().getAdapter().notifyDataSetChanged();
                }
            }
        });
        searchKey = getIntent().getStringExtra(SearchMsgActivity.AGM_SEARCH_KEY);
        viewModel.key.setValue(searchKey);
        edtSearch.setText(viewModel.key.getValue());
        if (!TextUtils.isEmpty(searchKey)) {
            edtSearch.setSelection(searchKey.length());
        }
    }

    private void findViews() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        edtSearch = findViewById(R.id.edt_search);
        mtListView = findViewById(R.id.mtListView);
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
        adapter = new MsgSearchAdapter(this, viewModel,MsgSearchAdapter.SearchType.GROUPS);
        mtListView.init(adapter);
        mtListView.getLoadView().setStateNormal();
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean result = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    String key = edtSearch.getText().toString();
                    viewModel.key.setValue(key);
                    InputUtil.hideKeyboard(GroupsSearchActivity.this);
                    result = true;
                } else if (event != null && (KeyEvent.KEYCODE_ENTER == event.getKeyCode() || KeyEvent.ACTION_DOWN == event.getAction())) {
                    String key = edtSearch.getText().toString();
                    viewModel.key.setValue(key);
                    InputUtil.hideKeyboard(GroupsSearchActivity.this);
                    result = true;
                }
                return result;
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edtSearch.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        searchKey = s.toString();
                        viewModel.key.setValue(searchKey);
                    }
                }, 100);
            }
        });

    }
}
