package com.yanlong.im.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-03-13
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class HalfLinearLayout extends LinearLayout {
    private int mHeight=0;
    public HalfLinearLayout(Context context) {
        super(context);
    }

    public HalfLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HalfLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HalfLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = getMeasuredHeight();
        if(measuredHeight>mHeight){
            measuredHeight=measuredHeight/2;
            mHeight=measuredHeight;
        }
        setMeasuredDimension(getMeasuredWidth(), measuredHeight);
    }
}
