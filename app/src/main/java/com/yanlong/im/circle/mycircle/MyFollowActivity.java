package com.yanlong.im.circle.mycircle;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.databinding.ActivityMyFollowBinding;
import com.yanlong.im.databinding.ItemFollowPersonBinding;

import net.cb.cb.library.base.bind.BaseBindActivity;
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

    private CommonRecyclerViewAdapter<String, ItemFollowPersonBinding> mAdapter;
    private List<String> mList = new ArrayList<>();

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
    protected void loadData() {
        mAdapter = new CommonRecyclerViewAdapter<String, ItemFollowPersonBinding>(this, R.layout.item_follow_person) {
            @Override
            public void bind(ItemFollowPersonBinding binding, String data, int position, RecyclerView.ViewHolder viewHolder) {

            }
        };
        for (int i = 0; i < 20; i++) {
            mList.add(i + "");
        }
        bindingView.recyclerView.setLayoutManager(new YLLinearLayoutManager(this));
        mAdapter.setData(mList);
        bindingView.recyclerView.setAdapter(mAdapter);
    }
}