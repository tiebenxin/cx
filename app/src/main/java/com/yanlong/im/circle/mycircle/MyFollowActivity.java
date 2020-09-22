package com.yanlong.im.circle.mycircle;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.databinding.ActivityMyFollowBinding;
import com.yanlong.im.databinding.ItemFollowPersonBinding;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.base.bind.BaseBindActivity;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.YLLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

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

    private CommonRecyclerViewAdapter<UserInfo, ItemFollowPersonBinding> mAdapter;
    private List<UserInfo> mList = new ArrayList<>();

    @Override
    protected int setView() {
        return R.layout.activity_my_follow;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected void initEvent() {
        setActionBarLeft(bindingView.headView);
    }

    @Override
    protected void loadData() { //TODO 还差2个接口未提供
        mAdapter = new CommonRecyclerViewAdapter<UserInfo, ItemFollowPersonBinding>(this, R.layout.item_follow_person) {
            @Override
            public void bind(ItemFollowPersonBinding binding, UserInfo data, int position, RecyclerView.ViewHolder viewHolder) {
                if (mList != null && mList.size() > 0) {
                    UserInfo userInfo = mList.get(position);
                    //昵称
                    if(!TextUtils.isEmpty(userInfo.getName())){
                        binding.tvName.setText(userInfo.getName());
                    }
                    //签名
                    if(!TextUtils.isEmpty(userInfo.getDescribe())){
                        binding.tvNote.setText(userInfo.getDescribe());
                    }
                    //头像
                    if(!TextUtils.isEmpty(userInfo.getHead())){
                        RequestOptions mRequestOptions = RequestOptions.centerInsideTransform()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(false)
                                .error(R.drawable.ic_info_head)
                                .centerCrop();
                        Glide.with(getContext())
                                .load(userInfo.getHead())
                                .apply(mRequestOptions)
                                .into(binding.ivHeader);
                    }
                    //关注状态
                    if(userInfo.getStat()==0){
                        binding.tvFollow.setText("关注TA");
                        binding.tvFollow.setBackgroundResource(R.drawable.shape_5radius_solid_32b053);
                    }else if(userInfo.getStat()==1){
                        binding.tvFollow.setText("已关注");
                        binding.tvFollow.setBackgroundResource(R.drawable.shape_5radius_solid_d8d8d8);
                    }else {
                        binding.tvFollow.setText("相互关注");
                        binding.tvFollow.setBackgroundResource(R.drawable.shape_5radius_solid_527ea2);
                    }
                    //关注操作
                    binding.tvFollow.setOnClickListener(v -> httpToFollow());
                    //搜索过滤
                    bindingView.editSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String content = s.toString();
                            if (TextUtils.isEmpty(content)) {
//                                adapter.setList(listData);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                }
            }
        };
        bindingView.recyclerView.setLayoutManager(new YLLinearLayoutManager(this));
        bindingView.recyclerView.setAdapter(mAdapter);
        httpGetMyFollow();
    }

    /**
     * 获取我关注的人列表
     */
    private void httpGetMyFollow() {
        ToastUtil.show("接口未提供：获取列表");
        String head = "https://himg.bdimg.com/sys/portraitn/item/beaf7a68756461786961736869776fff26";
        //模拟数据 TODO 接口未提供
        UserInfo userOne = new UserInfo();
        userOne.setName("未关注用户");
        userOne.setHead(head);
        userOne.setDescribe("未关注用户个性签名");
        userOne.setStat(0);// 0 未关注 1 已关注 2 相互关注

        UserInfo userTwo = new UserInfo();
        userTwo.setName("已关注用户");
        userTwo.setHead(head);
        userTwo.setDescribe("已关注用户个性签名");
        userTwo.setStat(1);// 0 未关注 1 已关注 2 相互关注

        UserInfo userThree = new UserInfo();
        userThree.setName("互相关注用户");
        userThree.setHead(head);
        userThree.setDescribe("互相关注用户个性签名");
        userThree.setStat(2);// 0 未关注 1 已关注 2 相互关注

        mList.add(userOne);
        mList.add(userTwo);
        mList.add(userThree);
        mAdapter.setData(mList);
    }

    /**
     * 关注操作
     */
    private void httpToFollow() {
        ToastUtil.show("接口未提供：关注操作");
    }

    /**
     * 搜索关键字
     * @param name
     */
//    private void searchName(String name) {
//        if (!TextUtils.isEmpty(name)) {
//            seacchData.clear();
//            for (FriendInfoBean bean : listData) {
//                if (bean.getNickname().contains(name)) {
//                    seacchData.add(bean);
//                }
//            }
//            adapter.setList(seacchData);
//            bindingView.mtListView.notifyDataSetChange();
//        }
//    }
}