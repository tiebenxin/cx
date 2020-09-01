package net.cb.cb.library.view.recycler;

/**
 * @author Liszt
 * @date 2020/8/28
 * Description 刷新listener
 */
public interface IRefreshListener {
    //下拉刷新
    void onRefresh();

    //上拉加载更多
    void loadMore();
}
