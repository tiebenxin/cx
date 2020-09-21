package com.yanlong.im.circle.recommend;

import com.yanlong.im.R;
import com.yanlong.im.databinding.FragmentRecommendBinding;

import net.cb.cb.library.base.bind.BaseBindMvpFragment;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-07
 * @updateAuthor
 * @updateDate
 * @description 朋友圈 推荐
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class RecommendFragment extends BaseBindMvpFragment<RecommendPresenter, FragmentRecommendBinding> implements RecommendView {

    @Override
    protected RecommendPresenter createPresenter() {
        return new RecommendPresenter(getContext());
    }

    @Override
    public int setLayout() {
        return R.layout.fragment_recommend;
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {

    }
}