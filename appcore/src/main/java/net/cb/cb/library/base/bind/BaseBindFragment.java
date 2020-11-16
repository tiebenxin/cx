package net.cb.cb.library.base.bind;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

/**
 * @author Liszt
 * @date 2020/11/16
 * Description
 */
public abstract class BaseBindFragment<SV extends ViewDataBinding> extends Fragment {
    public SV mViewBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        init();
        initListener();
        return mViewBinding.getRoot();
    }

    public abstract int getLayoutId();

    public abstract void init();

    public abstract void initListener();
}
