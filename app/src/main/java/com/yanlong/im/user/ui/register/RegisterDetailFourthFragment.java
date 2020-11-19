package com.yanlong.im.user.ui.register;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.RegisterDetailBean;
import com.yanlong.im.databinding.FragmentRegisterFourthBinding;
import com.yanlong.im.user.action.UserAction;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;

import retrofit2.Call;
import retrofit2.Response;

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
        mViewBinding.ivRight.setVisibility(View.INVISIBLE);
        if (infoStat == 2) {
            mViewBinding.ivBack.setVisibility(View.VISIBLE);
        } else {
            mViewBinding.ivBack.setVisibility(View.GONE);
        }
    }

    @Override
    public void initListener() {
        mViewBinding.ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputUtil.hideKeyboard(mViewBinding.etNick);
                if (listener != null) {
                    listener.onBack();
                }
            }
        });
        mViewBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputUtil.hideKeyboard(mViewBinding.etNick);
                if (listener != null) {
                    listener.onExit();
                }
//                mViewBinding.etNick.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }, 10);

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


        //进入常信
        mViewBinding.tvGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                String nick = mViewBinding.etNick.getText().toString().trim();

                if (!TextUtils.isEmpty(nick)) {
                    uploadInfo(nick);
                } else {
                    ToastUtil.show("请输入昵称");
                }
            }
        });
    }

    @Override
    public void updateDetailUI(RegisterDetailBean bean) {
        if (bean == null || mViewBinding == null) {
            return;
        }
        if (!TextUtils.isEmpty(bean.getNick())) {
            mViewBinding.etNick.setText(bean.getNick());
            mViewBinding.etNick.setSelection(bean.getNick().length());
        }
    }

    private void uploadInfo(String nick) {
        if (getActivity() == null) {
            return;
        }
        RegisterDetailBean bean = ((RegisterDetailActivity) getActivity()).getDetailBean();
        new UserAction().updateMyInfo(null, null, nick, bean.getSex(), bean.getBirthday(), bean.getLocation(), null, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    ToastUtil.show("注册成功");
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
    }
}
