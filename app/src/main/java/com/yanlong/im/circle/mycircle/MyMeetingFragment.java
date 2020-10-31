package com.yanlong.im.circle.mycircle;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hm.cxpay.widget.refresh.EndlessRecyclerOnScrollListener;
import com.yanlong.im.R;
import com.yanlong.im.circle.adapter.MyFollowAdapter;
import com.yanlong.im.circle.bean.FriendUserBean;

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
 * @类名：谁看过我/我看过谁
 * @Date：2020/9/24
 * @by zjy
 * @备注：
 */
public class MyMeetingFragment extends Fragment {

    private int page = 1;//默认第一页
    private int type = 0;//UI区别显示 1 我看过谁 2 谁看过我

    private View view;
    private RecyclerView recyclerView;
    private LinearLayout noDataLayout;
    private MyFollowAdapter adapter;
    private List<FriendUserBean> mList;
    private Activity activity;
    private MyCircleAction action;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_meeting, container, false);
        init(view);
        return view;
    }

    public void init(View view){
        recyclerView = view.findViewById(R.id.recycler_view);
        noDataLayout = view.findViewById(R.id.no_data_layout);
        mList = new ArrayList<>();
        activity = getActivity();
        action = new MyCircleAction();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    public void loadData(){
        adapter = new MyFollowAdapter(activity, mList,type);
        recyclerView.setLayoutManager(new YLLinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
        //加载更多
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                adapter.setLoadState(adapter.LOADING);
                httpGetData();
            }
        });
    }

    /**
     * 发请求->获取谁看过我/我看过谁列表
     */
    private void httpGetData() {
        action.httpGetWhoSeeMeList(page, DEFAULT_PAGE_SIZE, type, new CallBack<ReturnBean<List<FriendUserBean>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<FriendUserBean>>> call, Response<ReturnBean<List<FriendUserBean>>> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    //1 有数据
                    if (response.body().getData() != null && response.body().getData().size() > 0) {
                        //1-1 加载更多，则分页数据填充到尾部
                        if (page > 1) {
                            adapter.addMoreList(response.body().getData());
                            adapter.setLoadState(adapter.LOADING_MORE);
                        } else {
                            //1-2 第一次加载，若超过8个显示加载更多
                            mList.addAll(response.body().getData());
                            adapter.updateList(mList);
                            if (mList.size() >= EndlessRecyclerOnScrollListener.DEFULT_SIZE_8) {
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
                    ToastUtil.show("获取谁看过我/我看过谁列表失败");
                    showNoDataLayout(false);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<FriendUserBean>>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("获取谁看过我/我看过谁列表失败");
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
            recyclerView.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public void onResume() {
        super.onResume();
        mList.clear();
        page = 1;
        httpGetData();
    }
}
