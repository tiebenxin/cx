package com.yanlong.im.user.ui.register;

import android.view.View;

import com.yanlong.im.R;
import com.yanlong.im.databinding.FragmentRegisterFourthBinding;

/**
 * @author Liszt
 * @date 2020/11/16
 * Description
 */
public class RegisterDetailFourthFragment extends BaseRegisterFragment<FragmentRegisterFourthBinding> {

    @Override
    public int getLayoutId() {
        return R.layout.fragment_register_fourth;
    }

    @Override
    public void init() {
        mViewBinding.ivLeft.setVisibility(View.VISIBLE);
        mViewBinding.ivRight.setVisibility(View.VISIBLE);
    }

    @Override
    public void initListener() {
        mViewBinding.ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });
        mViewBinding.ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onNext();
                }
            }
        });
    }


}
