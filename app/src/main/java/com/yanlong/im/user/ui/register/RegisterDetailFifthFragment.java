package com.yanlong.im.user.ui.register;

import android.view.View;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.RegisterDetailBean;
import com.yanlong.im.databinding.FragmentRegisterFifthBinding;


/**
 * @author Liszt
 * @date 2020/11/16
 * Description
 */
public class RegisterDetailFifthFragment extends BaseRegisterFragment<FragmentRegisterFifthBinding> {

    @Override
    public int getLayoutId() {
        return R.layout.fragment_register_fifth;
    }

    @Override
    public void init() {
        mViewBinding.ivLeft.setVisibility(View.VISIBLE);
        mViewBinding.ivRight.setVisibility(View.GONE);
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
