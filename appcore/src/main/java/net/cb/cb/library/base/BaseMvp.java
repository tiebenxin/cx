package net.cb.cb.library.base;

/**
 * @anthor Liszt
 * @data 2019/8/10
 * Description
 */
public interface BaseMvp<M extends IModel, V extends IView, P extends BasePresenter> {
    M createModel();

    V createView();

    P createPresenter();
}
