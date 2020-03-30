package net.cb.cb.library.base;

import android.os.Bundle;

import net.cb.cb.library.dialog.DialogLoadingProgress;
import net.cb.cb.library.view.AppActivity;

/**
 * @author Liszt
 * @date 2019/8/10
 * Description mvp Activity 基类
 */
public abstract class BaseMvpActivity<M extends IModel, V extends IView, P extends BasePresenter> extends AppActivity implements BaseMvp<M, V, P> {
    protected P presenter;
    private DialogLoadingProgress loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //创建Presenter
        presenter = createPresenter();
        if (presenter != null) {
            //将Model层注册到Presenter中
            presenter.registerModel(createModel());
            //将View层注册到Presenter中
            presenter.registerView(createView());
        }

        if (presenter != null) {
            presenter.onCreate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            //Activity销毁时的调用，让具体实现BasePresenter中onViewDestroy()方法做出决定
            presenter.onDestroy();
        }
    }

    public void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new DialogLoadingProgress(this);
        }
        loadingDialog.show();
    }

    public void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    //activity 是否有效
    public boolean isActivityValid() {
        if (this == null || this.isDestroyed() || this.isFinishing()) {
            return false;
        }
        return true;
    }

}
