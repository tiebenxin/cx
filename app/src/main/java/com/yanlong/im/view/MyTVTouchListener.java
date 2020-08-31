package com.yanlong.im.view;

import android.text.Layout;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

/**
 * @author Liszt
 * @date 2020/8/31
 * Description
 */
public class MyTVTouchListener implements View.OnTouchListener {
    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;
    boolean isReturn = false;

    private long mLastActionDownTime = -1;

    public MyTVTouchListener(View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        mOnClickListener = onClickListener;
        mOnLongClickListener = onLongClickListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        TextView tv = (TextView) v;
        CharSequence text = tv.getText();
        if (action == MotionEvent.ACTION_DOWN) {
            mLastActionDownTime = System.currentTimeMillis();
        } else {
            long actionUpTime = System.currentTimeMillis();
            if (actionUpTime - mLastActionDownTime >= ViewConfiguration.getLongPressTimeout() && null != mOnLongClickListener) {
                mOnLongClickListener.onLongClick(v);
                isReturn = true;
                return false;
            }
            if (action == MotionEvent.ACTION_UP) {
                if (isReturn) {
                    isReturn = false;
                    return false;
                }
                if (text instanceof Spanned) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    x -= tv.getTotalPaddingLeft();
                    y -= tv.getTotalPaddingTop();
                    x += tv.getScrollX();
                    y += tv.getScrollY();
                    Layout layout = tv.getLayout();
                    int line = layout.getLineForVertical(y);
                    int off = layout.getOffsetForHorizontal(line, x);
                    ClickableSpan[] link = ((Spanned) text).getSpans(off, off, ClickableSpan.class);
                    if (link.length != 0) {
                        link[0].onClick(tv);
                    } else {
                        //do textview click event
                        if (null != mOnClickListener) {
                            mOnClickListener.onClick(v);
                        }
                    }
                } else {
                    if (null != mOnClickListener) {
                        mOnClickListener.onClick(v);
                    }
                }
            }
        }
        return true;
    }
}
