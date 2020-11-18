package com.yanlong.im.user.ui.register;

import android.text.TextUtils;
import android.view.View;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.RegisterDetailBean;
import com.yanlong.im.databinding.FragmentRegisterFourthBinding;

import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.ToastUtil;

/**
 * @author Liszt
 * @date 2020/11/16
 * Description 昵称
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
                String nick = mViewBinding.etNick.getText().toString();
                if (!TextUtils.isEmpty(nick)) {
                    ((RegisterDetailActivity) getActivity()).getDetailBean().setNick(nick);
                    InputUtil.hideKeyboard(mViewBinding.etNick);
                    if (listener != null) {
                        listener.onNext();
                    }
                } else {
                    ToastUtil.show("请输入昵称");
                }
            }
        });
    }

    @Override
    public void updateDetailUI(RegisterDetailBean bean) {
        if (bean == null) {
            return;
        }
        if (!TextUtils.isEmpty(bean.getNick())) {
            mViewBinding.etNick.setText(bean.getNick());
        }
    }
}
