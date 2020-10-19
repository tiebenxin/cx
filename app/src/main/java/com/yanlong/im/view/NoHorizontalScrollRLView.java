package com.yanlong.im.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author Liszt
 * @date 2020/9/10
 * Description 禁止响应横向手势的RelativeLayout
 */
public class NoHorizontalScrollRLView extends RelativeLayout {
    public NoHorizontalScrollRLView(Context context) {
        this(context, null);
    }

    public NoHorizontalScrollRLView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoHorizontalScrollRLView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return false;
    }
}
