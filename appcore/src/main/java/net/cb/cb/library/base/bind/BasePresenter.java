package net.cb.cb.library.base.bind;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import net.cb.cb.library.dialog.DialogLoadingProgress;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-07
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public abstract class BasePresenter<M extends BaseModel, V extends IBaseView> implements IPresenter<V> {
    public M mModel;
    protected V mView;
    protected Context mContext;
    private DialogLoadingProgress loadingDialog;

    public BasePresenter(Context context) {
        this.mContext = context;
        this.mModel = bindModel();
    }

    public abstract M bindModel();

    @Override
    public void attachView(@NonNull V view) {
        mView = view;
    }

    @Override
    public void detachView() {
        this.mView = null;
    }

    @Override
    public void unbind() {
        this.loadingDialog = null;
        this.mModel = null;
        this.mContext = null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void showLoading(boolean cancelable) {
        if (loadingDialog == null) {
            loadingDialog = new DialogLoadingProgress(mContext);
        }
        loadingDialog.show();
    }

    public void hideLoading() {
        if (this.loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
