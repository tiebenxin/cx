package com.yanlong.im.circle.mycircle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;

import com.hm.cxpay.widget.refresh.EndlessRecyclerOnScrollListener;
import com.luck.picture.lib.event.EventFactory;
import com.yanlong.im.R;
import com.yanlong.im.circle.adapter.MyTrendsAdapter;
import com.yanlong.im.circle.bean.CircleTrendsBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.databinding.ActivityMyCircleBinding;
import com.yanlong.im.interf.IRefreshListenr;
import com.yanlong.im.user.ui.ComplaintActivity;
import com.yanlong.im.user.ui.UserInfoActivity;

import net.cb.cb.library.base.bind.BaseBindActivity;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.inter.IFriendTrendClickListner;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.DialogHelper;
import net.cb.cb.library.utils.ScreenUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.YLLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.yanlong.im.circle.mycircle.MyFollowActivity.DEFAULT_PAGE_SIZE;

/**
 * @类名：别人的动态(别人的朋友圈)
 * @Date：2020/9/25
 * @by zjy
 * @备注：
 */

public class FriendTrendsActivity extends BaseBindActivity<ActivityMyCircleBinding> {


    public static String POSITION = "position";
    private int page = 1;//默认第一页

    private TempAction action;
    private MyTrendsAdapter adapter;
    private List<MessageInfoBean> mList;
    private long friendUid;//别人的uid
    private int isFollow;//是否关注了该用户
    private int clickPosition;//点击哪一项跳转过来的


    @Override
    protected int setView() {
        return R.layout.activity_my_circle;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        action = new TempAction();
        mList = new ArrayList<>();
        bindingView.layoutFollow.setVisibility(View.VISIBLE);
        bindingView.layoutChat.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initEvent() {
        ImageView ivRight = bindingView.headView.getActionbar().getBtnRight();
        ivRight.setImageResource(R.mipmap.ic_circle_more);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setPadding(ScreenUtil.dip2px(this, 10), 0,
                ScreenUtil.dip2px(this, 10), 0);
        bindingView.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                DialogHelper.getInstance().createFriendTrendDialog(FriendTrendsActivity.this, new IFriendTrendClickListner() {
                    @Override
                    public void clickReport() {
                        //举报
                        Intent intent = new Intent(FriendTrendsActivity.this, ComplaintActivity.class);
                        intent.putExtra(ComplaintActivity.UID, friendUid + "");
                        startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    protected void loadData() {
        friendUid = getIntent().getLongExtra("uid",0);
        clickPosition = getIntent().getIntExtra(POSITION,0);
        httpGetFriendTrends();
        adapter = new MyTrendsAdapter(FriendTrendsActivity.this,mList,2,friendUid);
        bindingView.recyclerView.setAdapter(adapter);
        bindingView.recyclerView.setLayoutManager(new YLLinearLayoutManager(this));

        //加载更多
        bindingView.recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                adapter.setLoadState(adapter.LOADING);
                httpGetFriendTrends();
            }
        });
        //下拉刷新
        bindingView.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                httpGetFriendTrends();
            }
        });
        //置顶->刷新回调
        adapter.setOnRefreshListenr(new IRefreshListenr() {
            @Override
            public void onRefresh() {
                page = 1;
                httpGetFriendTrends();
            }
        });
        bindingView.swipeRefreshLayout.setColorSchemeResources(R.color.c_169BD5);
        bindingView.ivCreateCircle.setVisibility(View.GONE);
        //关注
        bindingView.layoutFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFollow==0){
                    httpToFollow(friendUid);
                }else {
                    httpCancelFollow(friendUid);
                }
            }
        });
        //私聊
        bindingView.layoutChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FriendTrendsActivity.this, UserInfoActivity.class)
                        .putExtra(UserInfoActivity.ID, friendUid));
            }
        });
    }

    /**
     * 发请求->获取好友的动态(说说主页及列表)
     */
    private void httpGetFriendTrends() {
        action.httpGetFriendTrends(page, DEFAULT_PAGE_SIZE,friendUid, new CallBack<ReturnBean<CircleTrendsBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<CircleTrendsBean>> call, Response<ReturnBean<CircleTrendsBean>> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    //1 有数据
                    if(response.body().getData()!=null){
                        CircleTrendsBean bean = response.body().getData();
                        //动态列表
                        if(bean.getMomentList()!=null && bean.getMomentList().size()>0){
                            //1-1 加载更多，则分页数据填充到尾部
                            if (page > 1) {
                                adapter.addMoreList(bean.getMomentList());
                                adapter.setLoadState(adapter.LOADING_MORE);
                            }else {
                                //1-2 第一次加载，若超过3个显示加载更多
                                isFollow = bean.getMyFollow();
                                if(isFollow==0){
                                    bindingView.tvFollow.setText("关注");
                                    adapter.ifFollow(false);
                                }else {
                                    bindingView.tvFollow.setText("已关注");
                                    adapter.ifFollow(true);
                                }
                                mList.clear();
                                mList.addAll(bean.getMomentList());
                                adapter.setTopData(bean);
                                adapter.updateList(mList);
                                if(mList.size()>=EndlessRecyclerOnScrollListener.DEFULT_SIZE_3){
                                    adapter.setLoadState(adapter.LOADING_MORE);
                                }
                            }
                            page++;
                        }else {
                            //2 无数据
                            //2-1 加载更多，当没有数据的时候，提示已经到底了
                            if (page > 1) {
                                adapter.setLoadState(adapter.LOADING_END);
                            } else {
                                //2-2 第一次加载，没有数据则不显示尾部
                                isFollow = bean.getMyFollow();
                                if(isFollow==0){
                                    bindingView.tvFollow.setText("关注");
                                    adapter.ifFollow(false);
                                }else {
                                    bindingView.tvFollow.setText("已关注");
                                    adapter.ifFollow(true);
                                }
                                adapter.setLoadState(adapter.LOADING_GONE);
                            }
                        }
                    }
                }else {
                    ToastUtil.show("获取好友动态失败");
                }
                bindingView.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ReturnBean<CircleTrendsBean>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("获取好友动态失败");
                bindingView.swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 发请求->关注
     */
    private void httpToFollow(long uid) {
        action.httpToFollow(uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    ToastUtil.show("关注成功");
                    bindingView.tvFollow.setText("已关注");
                    isFollow = 1;
                    //操作关注后，回到关注列表需要及时更新
                    EventFactory.UpdateFollowStateEvent event = new EventFactory.UpdateFollowStateEvent();
                    event.type = 1;
                    event.position = clickPosition;
                    event.uid = uid;
                    EventBus.getDefault().post(event);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("关注失败");
            }
        });
    }

    /**
     * 发请求->取消关注
     */
    private void httpCancelFollow(long uid) {
        action.httpCancelFollow(uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    ToastUtil.show("取消关注成功");
                    bindingView.tvFollow.setText("关注");
                    isFollow = 0;
                    //操作关注后，回到关注列表需要及时更新
                    EventFactory.UpdateFollowStateEvent event = new EventFactory.UpdateFollowStateEvent();
                    event.type = 0;
                    event.position = clickPosition;
                    event.uid = uid;
                    EventBus.getDefault().post(event);
                }else {
                    ToastUtil.show("取消关注失败");
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("取消关注失败");
            }
        });
    }

}
