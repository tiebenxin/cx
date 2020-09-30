package net.cb.cb.library.view.recycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import net.cb.cb.library.R;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.view.LoadView;
import net.cb.cb.library.view.MaterialDrawable;
import net.cb.cb.library.view.NewPullRefreshLayout;
import net.cb.cb.library.view.YLLinearLayoutManager;

/**
 * @author Liszt
 * @date 2020/8/28
 * Description 更换chat界面，只有下拉加载更多功能
 */
public class MultiRecyclerView extends LinearLayout {

    private View viewRoot;
    private LoadView loadView;
    private NewPullRefreshLayout pullRefreshLayout;
    private RecyclerView recyclerView;
    private YLLinearLayoutManager layoutManager;
    private IRefreshListener listener;
    private RecyclerView.Adapter mAdapter;

    public MultiRecyclerView(Context context) {
        this(context, null);
    }

    public MultiRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        viewRoot = LayoutInflater.from(getContext()).inflate(R.layout.layout_refresh_recycler, this, true);
        loadView = viewRoot.findViewById(R.id.load_view);
        pullRefreshLayout = viewRoot.findViewById(R.id.swipe_view);
        recyclerView = viewRoot.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        pullRefreshLayout.setRefreshDrawable(new MaterialDrawable(getContext(), pullRefreshLayout));
        pullRefreshLayout.setEnabled(false);
        layoutManager = new YLLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setItemAnimator(null);
        loadView.setVisibility(GONE);

        // 下拉刷新
        pullRefreshLayout.setOnRefreshListener(new NewPullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (listener != null) {
                    listener.onRefresh();
                }
            }
        });

        //上拉加载更多，略
    }

    public void setListener(IRefreshListener l) {
        pullRefreshLayout.setEnabled(true);
        listener = l;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        recyclerView.setAdapter(mAdapter);
    }

    public RecyclerView getListView() {
        return recyclerView;
    }

    public LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }

    public NewPullRefreshLayout getSwipeLayout() {
        return pullRefreshLayout;
    }


    /**
     * 滑到列表底部
     */
    public void scrollToEnd() {
        if (layoutManager == null || mAdapter == null) {
            return;
        }
        LogUtil.getLog().i("scroll", "scrollToEnd");
        layoutManager.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, Integer.MIN_VALUE);
    }

}
