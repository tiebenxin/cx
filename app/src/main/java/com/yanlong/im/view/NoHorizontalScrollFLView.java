package com.yanlong.im.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * @author Liszt
 * @date 2020/9/10
 * Description 禁止响应横向手势的FrameLayout
 */
public class NoHorizontalScrollFLView extends FrameLayout {
    public NoHorizontalScrollFLView(Context context) {
        this(context, null);
    }

    public NoHorizontalScrollFLView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoHorizontalScrollFLView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return false;
    }
}
