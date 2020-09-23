package com.yanlong.im.circle.mycircle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.circle.bean.FriendUserBean;
import com.yanlong.im.databinding.ActivityMyFollowBinding;
import com.yanlong.im.databinding.ItemFollowPersonBinding;
import com.yanlong.im.user.ui.UserInfoActivity;

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
 * @类名：关注我的人
 * @Date：2020/9/22
 * @by zjy
 * @备注：
 */

public class FollowMeActivity extends BaseBindActivity<ActivityMyFollowBinding> {

    private CommonRecyclerViewAdapter<FriendUserBean, ItemFollowPersonBinding> mAdapter;
    private List<FriendUserBean> mList;
    private List<FriendUserBean> allData;//全部数据
    private List<FriendUserBean> searchData;//搜索后的数据
    private TempAction action;

    @Override
    protected int setView() {
        return R.layout.activity_my_follow;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mList = new ArrayList<>();
        allData = new ArrayList<>();
        searchData = new ArrayList<>();
        action = new TempAction();
    }

    @Override
    protected void initEvent() {
        setActionBarLeft(bindingView.headView);
        bindingView.headView.setTitle("关注我的人");
    }

    @Override
    protected void loadData() {
        mAdapter = new CommonRecyclerViewAdapter<FriendUserBean, ItemFollowPersonBinding>(this, R.layout.item_follow_person) {
            @Override
            public void bind(ItemFollowPersonBinding binding, FriendUserBean data, int position, RecyclerView.ViewHolder viewHolder) {
                if (mList != null && mList.size() > 0) {
                    FriendUserBean userInfo = mList.get(position);
                    //昵称
                    if(!TextUtils.isEmpty(userInfo.getNickname())){
                        binding.tvName.setText(userInfo.getNickname());
                    }
                    //最新一条说说
                    if(!TextUtils.isEmpty(userInfo.getContent())){
                        binding.tvNote.setText(userInfo.getContent());
                    }
                    //头像
                    if(!TextUtils.isEmpty(userInfo.getAvatar())){
                        RequestOptions mRequestOptions = RequestOptions.centerInsideTransform()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(false)
                                .error(R.drawable.ic_info_head)
                                .centerCrop();
                        Glide.with(getContext())
                                .load(userInfo.getAvatar())
                                .apply(mRequestOptions)
                                .into(binding.ivHeader);
                    }
                    //关注状态   刚进来全部是已关注，1 已关注 2 未关注 3 相互关注
                    if(userInfo.getStat()==3){
                        binding.tvFollow.setText("相互关注");
                        binding.tvFollow.setBackgroundResource(R.drawable.shape_5radius_solid_527ea2);
                    }else if(userInfo.getStat()==1){
                        binding.tvFollow.setText("已关注");
                        binding.tvFollow.setBackgroundResource(R.drawable.shape_5radius_solid_d8d8d8);
                    }else {
                        binding.tvFollow.setText("关注TA");
                        binding.tvFollow.setBackgroundResource(R.drawable.shape_5radius_solid_32b053);
                    }
                    //关注操作
                    binding.tvFollow.setOnClickListener(v -> {
                        if(binding.tvFollow.getText().equals("已关注")){
                            httpCancelFollow(userInfo.getUid(),position,binding.tvFollow);
                        }else if(binding.tvFollow.getText().equals("关注TA")){
                            httpToFollow(userInfo.getUid(),position,binding.tvFollow);
                        }else {
                            ToastUtil.show("已相互关注");
                        }
                    });
                    binding.layoutItem.setOnClickListener(v -> ToastUtil.show("跳转到朋友圈"));
                    binding.ivHeader.setOnClickListener(v -> startActivity(new Intent(getContext(), UserInfoActivity.class)
                            .putExtra(UserInfoActivity.ID, userInfo.getUid())
                            .putExtra(UserInfoActivity.JION_TYPE_SHOW, 1)));

                }
            }
        };
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
                    //当没有搜索关键字的时候恢复数据
                    mAdapter.refreshData(allData);
                }
            }
        });
        bindingView.recyclerView.setLayoutManager(new YLLinearLayoutManager(this));
        bindingView.recyclerView.setAdapter(mAdapter);
        httpGetFollowMe();
    }

    /**
     * 发请求->获取我关注的人列表
     */
    private void httpGetFollowMe() {
        action.httpGetFollowMeList(1, DEFAULT_PAGE_SIZE, new CallBack<ReturnBean<List<FriendUserBean>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<FriendUserBean>>> call, Response<ReturnBean<List<FriendUserBean>>> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    if(response.body().getData()!=null && response.body().getData().size()>0){
                        mList.clear();
                        mList.addAll(response.body().getData());
                        allData.addAll(response.body().getData());
                        mAdapter.setData(mList);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<FriendUserBean>>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("获取关注我的人列表失败");
            }
        });
    }

    /**
     * 发请求->关注
     */
    private void httpToFollow(long uid,int position, TextView tvFollow) {
        action.httpToFollow(uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    ToastUtil.show("关注成功");
                    mList.get(position).setStat(1);
                    mAdapter.notifyItemChanged(position,tvFollow);
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
    private void httpCancelFollow(long uid, int position, TextView tvFollow) {
        action.httpCancelFollow(uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    ToastUtil.show("取消关注成功");
                    mList.get(position).setStat(2);
                    mAdapter.notifyItemChanged(position,tvFollow);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("取消关注失败");
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
            mAdapter.refreshData(searchData);
        }
    }
}