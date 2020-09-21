package net.cb.cb.library.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020/3/30
 * @updateAuthor
 * @updateDate
 * @description 朋友圈 推荐、关注
 * @copyright copyright(c)2020 ChangSha hanming Technology Co., Ltd. Inc. All rights reserved.
 */
public class CircleViewPager extends ViewPager {

    private Map<Integer, Integer> mMap = new HashMap<>(2);
    private int currentPage = 0;

    public CircleViewPager(Context context) {
        super(context);
    }

    public CircleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mMap.clear();
        // 重置高度
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        // 屏幕的高度减去底部与标题的高度等于内容高度
        int heightPixel = outMetrics.heightPixels;// - (int) getContext().getResources().getDimension(R.dimen.qb_px_308);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            if (heightPixel < child.getMeasuredHeight()) {
                mMap.put(i, child.getMeasuredHeight());
            } else {
                mMap.put(i, heightPixel);
            }
        }
        int height = 0;
        if (mMap.size() > 0 && mMap.containsKey(currentPage)) {
            height = mMap.get(currentPage);
        }
        //得到ViewPager的MeasureSpec，使用固定值和MeasureSpec.EXACTLY，
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 在切换tab的时候，重置ViewPager的高度
     *
     * @param current
     */
    public void resetHeight(int current) {
        this.currentPage = current;
        MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
        if (mMap.size() > 0 && mMap.containsKey(currentPage)) {
            if (params == null) {
                params = new MarginLayoutParams(LayoutParams.MATCH_PARENT, mMap.get(current));
            } else {
                params.height = mMap.get(current);
            }
            setLayoutParams(params);
        }
    }
}
