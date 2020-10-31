package com.yanlong.im.adapter;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2019-12-09
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class CommonViewHolder <VB extends ViewDataBinding> extends RecyclerView.ViewHolder {

    private VB mBinding;

    public CommonViewHolder(View itemView) {

        super(itemView);
        mBinding = DataBindingUtil.bind(itemView);
    }

    public VB getBinding() {

        return mBinding;
    }
}
