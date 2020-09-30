package com.yanlong.im.circle.details;

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
public class CircleDetailsPresenter extends BasePresenter<CircleDetailsModel, CircleDetailsView> {

    CircleDetailsPresenter(Context context) {
        super(context);
    }

    @Override
    public CircleDetailsModel bindModel() {
        return new CircleDetailsModel();
    }

}
