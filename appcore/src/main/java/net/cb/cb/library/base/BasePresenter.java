package net.cb.cb.library.base;

import java.lang.ref.WeakReference;

/**
 * @anthor Liszt
 * @data 2019/8/10
 * Description
 */
public abstract class BasePresenter<M extends IModel, V extends IView> implements IPresenter<M, V> {
    /**
     * 使用弱引用来防止内存泄漏
     */
    private WeakReference<V> wrf;
    protected M model;

    @Override
    public void registerModel(M model) {
        this.model = model;
    }

    @Override
    public void registerView(V view) {
        wrf = new WeakReference<V>(view);
    }

    @Override
    public V getView() {
        return wrf == null ? null : wrf.get();
    }


    /**
     * 在Activity或Fragment卸载时调用View结束的方法
     */
    @Override
    public void onDestroy() {
        if (wrf != null) {
            wrf.clear();
            wrf = null;
        }
        onViewDestroy();
    }

    @Override
    public void onCreate() {
        onViewStart();
    }

    protected abstract void onViewDestroy();

    protected abstract void onViewStart();
}
