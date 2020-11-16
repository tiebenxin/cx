package com.yanlong.im.user.ui.register;

import android.view.View;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.RegisterDetailBean;
import com.yanlong.im.databinding.FragmentRegisterSecondBinding;

/**
 * @author Liszt
 * @date 2020/11/16
 * Description
 */
public class RegisterDetailSecondFragment extends BaseRegisterFragment<FragmentRegisterSecondBinding> {


    @Override
    public int getLayoutId() {
        return R.layout.fragment_register_second;
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


    @Override
    public void updateDetailUI(RegisterDetailBean bean) {

    }
}
