package com.yanlong.im.circle.mycircle;

import android.os.Bundle;
import android.view.View;

import com.yanlong.im.R;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.circle.adapter.MyInteractAdapter;
import com.yanlong.im.circle.bean.InteractMessage;
import com.yanlong.im.databinding.ActivityMyInteractBinding;

import net.cb.cb.library.base.bind.BaseBindActivity;
import net.cb.cb.library.view.YLLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;


/**
 * @类名：我的互动
 * @Date：2020/9/30
 * @by zjy
 * @备注：
 */

public class MyInteractActivity extends BaseBindActivity<ActivityMyInteractBinding> {

    private MyInteractAdapter adapter;
    private List<InteractMessage> mList;
    private MsgDao msgDao;

    @Override
    protected int setView() {
        return R.layout.activity_my_interact;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mList = new ArrayList<>();
        msgDao = new MsgDao();
    }

    @Override
    protected void initEvent() {
        setActionBarLeft(bindingView.headView);
    }

    @Override
    protected void loadData() {
        adapter = new MyInteractAdapter(MyInteractActivity.this,mList);
        bindingView.recyclerView.setLayoutManager(new YLLinearLayoutManager(this));
        bindingView.recyclerView.setAdapter(adapter);
        if(msgDao.getAllInteractMsg()!=null && msgDao.getAllInteractMsg().size()>0){
            mList.addAll(msgDao.getAllInteractMsg());
            adapter.updateList(mList);
            showNoDataLayout(false);
        }else {
            showNoDataLayout(true);
        }
        //点击进来则全部已读
        msgDao.setReadedStatus();
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