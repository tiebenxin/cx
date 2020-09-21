package net.cb.cb.library.base.bind;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020/9/7
 * @updateAuthor
 * @updateDate
 * @description mvp基类
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public abstract class BaseBindMvpActivity<P extends BasePresenter, SV extends ViewDataBinding> extends BaseBindActivity implements IBaseView {

    public P mPresenter;

    protected abstract P createPresenter();

    protected SV bindingView;

    @Override
    public void setContentView(int layoutResID) {
        bindingView = DataBindingUtil.inflate(getLayoutInflater(), layoutResID, null, false);
        super.setContentView(bindingView.getRoot());
    }

    private void bindPresenter() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter.unbind();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = createPresenter();
        bindPresenter();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setResultFinish(int resultCode) {
        setResultFinish(null, resultCode);
    }

    @Override
    public void setResultFinish(Intent intent, int resultCode) {
        if (intent != null) {
            setResult(resultCode, intent);
        } else {
            setResult(resultCode);
        }
        finish();
    }

    @Override
    public void delayTime(Runnable runnable, long delayMillis) {
        getWindow().getDecorView().postDelayed(runnable, delayMillis);
    }
}
