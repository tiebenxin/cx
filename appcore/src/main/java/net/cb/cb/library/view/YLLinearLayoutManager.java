package net.cb.cb.library.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Liszt
 * @date 2019/8/14
 * Description  自定义LinearLayoutManager 避免RecyclerView刷新月结
 */
public class YLLinearLayoutManager extends LinearLayoutManager {
    public YLLinearLayoutManager(Context context) {
        super(context);
    }

    public YLLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public YLLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}
