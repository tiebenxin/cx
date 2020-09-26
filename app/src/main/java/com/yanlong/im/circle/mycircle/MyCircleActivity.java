package com.yanlong.im.circle.mycircle;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityMyCircleBinding;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserBean;

import net.cb.cb.library.base.bind.BaseBindActivity;

/**
 * @类名：我的动态(我的朋友圈)
 * @Date：2020/9/25
 * @by zjy
 * @备注：
 */

public class MyCircleActivity extends BaseBindActivity<ActivityMyCircleBinding> {

    private UserBean userBean;


    @Override
    protected int setView() {
        return R.layout.activity_my_circle;
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
        showTopLayout();
    }

    //展示头部数据
    private void showTopLayout() {
        userBean = (UserBean) new UserAction().getMyInfo();
        if(userBean!=null){
            //头像 昵称 常信号 关注 被关注 看过我
            if(!TextUtils.isEmpty(userBean.getHead())){
                Glide.with(MyCircleActivity.this)
                        .load(userBean.getHead())
                        .into(bindingView.ivHeader);
            }
            if(!TextUtils.isEmpty(userBean.getName())){
                bindingView.tvName.setText(userBean.getName());
            }else {
                bindingView.tvName.setText("未知用户名");
            }
            if(!TextUtils.isEmpty(userBean.getImid())){
                bindingView.tvImid.setText("常信号："+userBean.getImid());
            }
        }
    }




    /**
     * 是否显示无数据占位图
     *
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
