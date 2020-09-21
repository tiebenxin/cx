package com.yanlong.im.circle.details;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.luck.picture.lib.tools.DoubleUtils;
import com.yanlong.im.R;
import com.yanlong.im.circle.CircleCommentDialog;
import com.yanlong.im.circle.LocationCircleActivity;
import com.yanlong.im.circle.adapter.CircleFlowAdapter;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.follow.FollowFragment;
import com.yanlong.im.databinding.ActivityCircleDetailsBinding;
import com.yanlong.im.interf.ICircleClickListener;

import net.cb.cb.library.base.bind.BaseBindMvpActivity;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.YLLinearLayoutManager;

import java.util.ArrayList;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-07
 * @updateAuthor
 * @updateDate
 * @description 朋友圈 详情
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
@Route(path = CircleDetailsActivity.path)
public class CircleDetailsActivity extends BaseBindMvpActivity<CircleDetailsPresenter, ActivityCircleDetailsBinding>
        implements CircleDetailsView, ICircleClickListener {
    public static final String path = "/circle/details/CircleDetailsActivity";

    private CircleFlowAdapter mFlowAdapter;

    @Override
    protected CircleDetailsPresenter createPresenter() {
        return new CircleDetailsPresenter(getContext());
    }

    @Override
    protected int setView() {
        return R.layout.activity_circle_details;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mFlowAdapter = new CircleFlowAdapter(null, true, this);
        bindingView.recyclerFollow.setAdapter(mFlowAdapter);
        bindingView.recyclerFollow.setLayoutManager(new YLLinearLayoutManager(getContext()));
        mPresenter.getFollowData();
    }

    @Override
    public void initEvent() {
        bindingView.headView.getActionbar().getRightImage().setImageResource(R.mipmap.ic_chat_bubble_ysq);
        bindingView.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        mFlowAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (DoubleUtils.isFastDoubleClick()) {
                    return;
                }
                switch (view.getId()) {
                    case R.id.iv_comment:
                        showCommentDialog();
                        break;
                    case R.id.tv_location:
                        if (NetUtil.isNetworkConnected()) {
                            Postcard postcard = ARouter.getInstance().build(LocationCircleActivity.path);
                            postcard.navigation();
                        } else {
                            ToastUtil.show("当前网络不可用，请检查你的网络设置");
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void loadData() {
        boolean isOpen = getIntent().getBooleanExtra(FollowFragment.IS_OPEN, false);
        if (isOpen) {
            showCommentDialog();
        }
    }

    /**
     * 显示评论输入框
     */
    private void showCommentDialog() {
        new CircleCommentDialog(CircleDetailsActivity.this, new CircleCommentDialog.OnMessageListener() {
            @Override
            public void OnMessage(String msg) {
            }
        }).show();
    }

    @Override
    public void setFollowData(ArrayList<MessageFlowItemBean> list) {
        mFlowAdapter.setNewData(list);
    }

    @Override
    public void onClick(int postion, int type) {
        if (type == 0) {
            MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(postion).getData();
            messageInfoBean.setShowAll(!messageInfoBean.isShowAll());
            mFlowAdapter.notifyItemChanged(postion);
        }
    }
}