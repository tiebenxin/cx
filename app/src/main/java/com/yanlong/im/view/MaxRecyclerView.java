package com.yanlong.im.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.yanlong.im.R;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-03-13
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class MaxRecyclerView extends RecyclerView {

    public MaxRecyclerView(@NonNull Context context) {
        super(context);
    }

    public MaxRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = getMeasuredHeight();
        int maxHeight=getContext().getResources().getDimensionPixelSize(R.dimen.chat_fuction_panel_height);
        if(measuredHeight>maxHeight){
            measuredHeight=maxHeight;
        }
        setMeasuredDimension(getMeasuredWidth(), measuredHeight);
    }
}
