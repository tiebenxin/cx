package com.yanlong.im.circle.mycircle;

import android.os.Bundle;
import android.view.View;

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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.yanlong.im.circle.mycircle.MyFollowActivity.DEFAULT_PAGE_SIZE;


/**
 * @类名：不看他
 * @Date：2020/10/14
 * @by zjy
 * @备注：
 */

public class NotSeeActivity extends BaseBindActivity<ActivityMyFollowBinding> {

    private int page = 1;//默认第一页

    private MyFollowAdapter adapter;
    private List<FriendUserBean> mList;
    private TempAction action;

    @Override
    protected int setView() {
        return R.layout.activity_my_follow;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mList = new ArrayList<>();
        action = new TempAction();
    }

    @Override
    protected void initEvent() {
        setActionBarLeft(bindingView.headView);
        bindingView.headView.setTitle("不看TA");
        bindingView.viewSearch.setVisibility(View.GONE);
    }

    @Override
    protected void loadData() {
        adapter = new MyFollowAdapter(NotSeeActivity.this,mList,3);
        bindingView.recyclerView.setLayoutManager(new YLLinearLayoutManager(this));
        bindingView.recyclerView.setAdapter(adapter);
        //加载更多
        bindingView.recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                adapter.setLoadState(adapter.LOADING);
                httpGetNotSeeList();
            }
        });
        httpGetNotSeeList();
    }

    /**
     * 发请求->获取我不看的人列表
     */
    private void httpGetNotSeeList() {
        action.httpGetNotSeeList(page, DEFAULT_PAGE_SIZE, new CallBack<ReturnBean<List<FriendUserBean>>>() {
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
                            mList.addAll(response.body().getData());
                            adapter.addMoreList(mList);
                            adapter.setLoadState(adapter.LOADING_MORE);
                        } else {
                            //1-2 第一次加载，若超过8个显示加载更多
                            mList.addAll(response.body().getData());
                            adapter.updateList(mList);
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
                    ToastUtil.show("获取我不看的人列表失败");
                    showNoDataLayout(false);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<FriendUserBean>>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("获取我不看的人列表失败");
                showNoDataLayout(false);
            }
        });
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
}