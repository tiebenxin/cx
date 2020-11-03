package com.yanlong.im.circle.mycircle;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.luck.picture.lib.event.EventFactory;
import com.yanlong.im.R;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.circle.adapter.MyInteractAdapter;
import com.yanlong.im.circle.bean.InteractMessage;
import com.yanlong.im.databinding.ActivityMyInteractBinding;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.base.bind.BaseBindActivity;
import net.cb.cb.library.view.YLLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @类名：我的互动
 * @Date：2020/9/30
 * @by zjy
 * @备注：/
 */

public class MyInteractActivity extends BaseBindActivity<ActivityMyInteractBinding> {

    private MyInteractAdapter adapter;
    private List<InteractMessage> mList;
    private MsgDao msgDao;
    /***
     * 统一处理mkname
     */
    private Map<Long, UserInfo> userMap = new HashMap<>();
    private UserDao userDao = new UserDao();

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

        List<InteractMessage> daoList = msgDao.getAllInteractMsg();
        if(daoList.size()>0){
            for (InteractMessage interactMessage : daoList) {
                resetName(interactMessage);
            }
            mList.addAll(daoList);
            adapter.updateList(mList);
            showNoDataLayout(false);
        }else {
            showNoDataLayout(true);
        }
        //点击进来则全部已读
        msgDao.setReadedStatus();
        //通知清掉首页广场红点
        EventBus.getDefault().post(new EventFactory.ClearHomePageShowUnreadMsgEvent());
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

    private void resetName(InteractMessage bean) {
        UserInfo userInfo;
        if (userMap.containsKey(bean.getFromUid())) {
            userInfo = userMap.get(bean.getFromUid());
            if (!TextUtils.isEmpty(userInfo.getMkName())) {
                bean.setNickname(userInfo.getMkName());
            }
        } else {
            userInfo = userDao.findUserInfo(bean.getFromUid());
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getMkName())) {
                bean.setNickname(userInfo.getMkName());
                userMap.put(bean.getFromUid(), userInfo);
            }
        }
    }

}