package com.yanlong.im.user.ui.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.yanlong.im.chat.bean.RegisterDetailBean;

import net.cb.cb.library.base.bind.BaseBindFragment;

/**
 * @author Liszt
 * @date 2020/11/16
 * Description
 */
public abstract class BaseRegisterFragment<SV extends ViewDataBinding> extends BaseBindFragment {

    public IRegisterListener listener;
    public SV mViewBinding;
    public int infoStat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        init();
        initListener();
        return mViewBinding.getRoot();
    }

    public void setListener(IRegisterListener l) {
        listener = l;
    }

    public interface IRegisterListener {
        void onBack();

        void onNext();

        void  onExit();
    }

    public abstract void updateDetailUI(RegisterDetailBean bean);

    public void setInfoStat(int stat) {
        infoStat = stat;
    }
}
