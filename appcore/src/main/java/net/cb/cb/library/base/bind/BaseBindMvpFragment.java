package net.cb.cb.library.base.bind;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-07
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public abstract class BaseBindMvpFragment<P extends BasePresenter, SV extends ViewDataBinding> extends Fragment implements IBaseView {
    protected P mPresenter;
    protected SV bindingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        bindingView = DataBindingUtil.inflate(inflater, setLayout(), container, false);
        mPresenter = createPresenter();
        bindPresenter();
        init();
        initEvent();
        return bindingView.getRoot();
    }

    public abstract int setLayout();

    public abstract void init();

    public abstract void initEvent();

    protected abstract P createPresenter();

    private void bindPresenter() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter.unbind();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setResultFinish(int resultCode) {
        setResultFinish(null, resultCode);
    }

    @Override
    public void setResultFinish(Intent intent, int resultCode) {
        if (intent != null) {
            getActivity().setResult(resultCode, intent);
        } else {
            getActivity().setResult(resultCode);
        }
        getActivity().finish();
    }

    @Override
    public void delayTime(Runnable runnable, long delayMillis) {
        getActivity().getWindow().getDecorView().postDelayed(runnable, delayMillis);
    }

    public Postcard getPostcard(String path) {
        return ARouter.getInstance().build(path);
    }

    public void toActivity(String path) {
        toActivity(getPostcard(path), false);
    }

    public void toActivity(String path, boolean doFinish) {
        toActivity(getPostcard(path), doFinish);
    }

    public void toActivity(Postcard postcard, boolean doFinish) {
        postcard.navigation();
        if (doFinish) {
            getActivity().finish();
        }
    }

    public void toActivityWithCallback(Activity activity, String path, int requestCode) {
        toActivityWithCallback(activity, getPostcard(path), requestCode);
    }

    public void toActivityWithCallback(Activity activity, Postcard postcard, int requestCode) {
        postcard.navigation(activity, requestCode);
    }

    public void notifyShow(){

    }
}
