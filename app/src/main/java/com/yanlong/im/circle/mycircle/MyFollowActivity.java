package com.yanlong.im.circle.mycircle;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hm.cxpay.widget.refresh.EndlessRecyclerOnScrollListener;
import com.yanlong.im.R;
import com.yanlong.im.circle.adapter.MyFollowAdapter;
import com.yanlong.im.circle.bean.FriendUserBean;
import com.yanlong.im.databinding.ActivityMyFollowBinding;

import net.cb.cb.library.base.bind.BaseBindActivity;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.YLLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-07
 * @updateAuthor
 * @updateDate
 * @description 我关注的人
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
@Route(path = MyFollowActivity.path)
public class MyFollowActivity extends BaseBindActivity<ActivityMyFollowBinding> {

    public static final String path = "/mycircle/MyFollowActivity";
    public static final int DEFAULT_PAGE_SIZE = 20;//默认分页请求数量

    private int page = 1;//默认第一页
    private boolean isSearchMode = false;//是否处于搜索模式，搜索模式不允许上拉加载更多

    private MyFollowAdapter adapter;
    private List<FriendUserBean> mList;
    private List<FriendUserBean> allData;//全部数据
    private List<FriendUserBean> searchData;//搜索后的数据
    private MyCircleAction action;

    @Override
    protected int setView() {
        return R.layout.activity_my_follow;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mList = new ArrayList<>();
        allData = new ArrayList<>();
        searchData = new ArrayList<>();
        action = new MyCircleAction();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void initEvent() {
        setActionBarLeft(bindingView.headView);
    }

    @Override
    protected void loadData() {
        adapter = new MyFollowAdapter(MyFollowActivity.this,mList,0);
        //搜索过滤
        bindingView.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = s.toString();
                searchName(content);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(bindingView.editSearch.getText().toString())){
                    //不处于搜索模式的时候恢复数据
                    adapter.updateList(allData);
                    isSearchMode = false;
                    if(allData.size()>=8){
                        adapter.setLoadState(adapter.LOADING_MORE);
                    }
                }else {
                    //搜索模式的时候不允许加载更多
                    isSearchMode = true;
                    adapter.setLoadState(adapter.LOADING_GONE);
                }
            }
        });
        bindingView.recyclerView.setLayoutManager(new YLLinearLayoutManager(this));
        bindingView.recyclerView.setAdapter(adapter);
        //加载更多
        bindingView.recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                if(!isSearchMode){
                    adapter.setLoadState(adapter.LOADING);
                    httpGetMyFollow();
                }
            }
        });
    }

    /**
     * 发请求->获取我关注的人列表
     */
    private void httpGetMyFollow() {
        action.httpGetMyFollowList(page, DEFAULT_PAGE_SIZE, new CallBack<ReturnBean<List<FriendUserBean>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<FriendUserBean>>> call, Response<ReturnBean<List<FriendUserBean>>> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    //1 有数据
                    if(response.body().getData()!=null && response.body().getData().size()>0) {
                        //1-1 加载更多，则分页数据填充到尾部
                        if (page > 1) {
                            allData.addAll(response.body().getData());
                            adapter.addMoreList(response.body().getData());
                            adapter.addMoreOldFollowList(response.body().getData(),false);
                            adapter.setLoadState(adapter.LOADING_MORE);
                        } else {
                            //1-2 第一次加载，若超过8个显示加载更多
                            allData.addAll(response.body().getData());
                            mList.addAll(response.body().getData());
                            adapter.updateList(mList);
                            adapter.setOldFollowList(mList,false);
                            if(mList.size()>=EndlessRecyclerOnScrollListener.DEFULT_SIZE_8){
                                adapter.setLoadState(adapter.LOADING_MORE);
                            }
                        }
                        showNoDataLayout(false);
                        page++;
                    } else {
                        //2 无数据
                        //2-1 加载更多，当没有数据的时候，提示已经到底了
                        if (page > 1) {
                            adapter.setLoadState(adapter.LOADING_END);
                            showNoDataLayout(false);
                        } else {
                            //2-2 第一次加载，没有数据则不显示尾部
                            adapter.setLoadState(adapter.LOADING_GONE);
                            showNoDataLayout(true);
                        }
                    }
                }else {
                    ToastUtil.show("获取我关注的人列表失败");
                    showNoDataLayout(false);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<FriendUserBean>>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("获取我关注的人列表失败");
                showNoDataLayout(false);
            }
        });
    }

    /**
     * 搜索关键字
     * @param name
     */
    private void searchName(String name) {
        if (!TextUtils.isEmpty(name) && allData.size()>0) {
            searchData.clear();
            for (FriendUserBean bean : allData) {
                if (!TextUtils.isEmpty(bean.getNickname()) && bean.getNickname().contains(name)) {
                    searchData.add(bean);
                }
            }
            adapter.updateList(searchData);
        }
    }

    /**
     * 是否显示无数据占位图
     * @param ifShow
     */
    private void showNoDataLayout(boolean ifShow) {
        if (ifShow) {
            bindingView.recyclerView.setVisibility(View.GONE);
            bindingView.noDataLayout.setVisibility(View.VISIBLE);
        } else {
            bindingView.recyclerView.setVisibility(View.VISIBLE);
            bindingView.noDataLayout.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        allData.clear();
        mList.clear();
        page = 1;
        httpGetMyFollow();
    }
}