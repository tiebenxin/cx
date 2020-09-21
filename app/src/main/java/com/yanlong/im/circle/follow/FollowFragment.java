package com.yanlong.im.circle.follow;

import android.view.View;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.luck.picture.lib.tools.DoubleUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.yanlong.im.R;
import com.yanlong.im.circle.adapter.CircleFlowAdapter;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.details.CircleDetailsActivity;
import com.yanlong.im.circle.mycircle.MyFollowActivity;
import com.yanlong.im.databinding.FragmentFollowBinding;
import com.yanlong.im.interf.ICircleClickListener;

import net.cb.cb.library.base.bind.BaseBindMvpFragment;
import net.cb.cb.library.view.YLLinearLayoutManager;

import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-07
 * @updateAuthor
 * @updateDate
 * @description 朋友圈 关注
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class FollowFragment extends BaseBindMvpFragment<FollowPresenter, FragmentFollowBinding> implements FollowView, ICircleClickListener {

    private CircleFlowAdapter mFlowAdapter;
    public static final String IS_OPEN = "is_open";

    protected FollowPresenter createPresenter() {
        return new FollowPresenter(getContext());
    }

    @Override
    public int setLayout() {
        return R.layout.fragment_follow;
    }

    @Override
    public void init() {
        mFlowAdapter = new CircleFlowAdapter(null, false, this);
        bindingView.recyclerFollow.setAdapter(mFlowAdapter);
        bindingView.recyclerFollow.setLayoutManager(new YLLinearLayoutManager(getContext()));
        bindingView.srlFollow.setRefreshFooter(new ClassicsFooter(getActivity()));
        mPresenter.getFollowData();
    }

    @Override
    public void initEvent() {
        bindingView.srlFollow.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                bindingView.srlFollow.finishRefresh();
                bindingView.srlFollow.finishLoadMore();
            }
        });
        mFlowAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (DoubleUtils.isFastDoubleClick()) {
                    return;
                }
                gotoCircleDetailsActivity(false);
            }
        });
        mFlowAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_comment:
                        gotoCircleDetailsActivity(true);
                        break;
                    case R.id.iv_revoke:
                        Postcard postcard = ARouter.getInstance().build(MyFollowActivity.path);
                        postcard.navigation();
                        break;
                    case R.id.iv_header:

                        break;
                }
            }
        });
    }

    private void gotoCircleDetailsActivity(boolean isOpen) {
        Postcard postcard = ARouter.getInstance().build(CircleDetailsActivity.path);
        postcard.withBoolean(IS_OPEN, isOpen);
        postcard.navigation();
    }

    @Override
    public void setFollowData(List<MessageFlowItemBean> list) {
        mFlowAdapter.setNewData(list);
    }

    /**
     * 内容展开、收起
     *
     * @param postion
     * @param type    0：展开、收起 1：详情
     */
    @Override
    public void onClick(int postion, int type) {
        if (type == 0) {
            MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(postion).getData();
            messageInfoBean.setShowAll(!messageInfoBean.isShowAll());
            mFlowAdapter.notifyItemChanged(postion);
        } else {
            if (!DoubleUtils.isFastDoubleClick()) {
                gotoCircleDetailsActivity(false);
            }
        }
    }
}