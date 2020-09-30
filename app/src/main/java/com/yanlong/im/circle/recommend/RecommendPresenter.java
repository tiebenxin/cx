package com.yanlong.im.circle.recommend;

import android.content.Context;

import net.cb.cb.library.base.bind.BasePresenter;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020/9/7 0007
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class RecommendPresenter extends BasePresenter<RecommendModel, RecommendView> {

    RecommendPresenter(Context context) {
        super(context);
    }

    @Override
    public RecommendModel bindModel() {
        return new RecommendModel();
    }

}
