package com.yanlong.im.user.ui.register;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.IntDef;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.RegisterDetailBean;
import com.yanlong.im.databinding.ActivityRegisterDetailBinding;

import net.cb.cb.library.base.bind.BaseBindActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Liszt
 * @date 2020/11/16
 * Description
 */
@SuppressLint("Registered")
public class RegisterDetailActivity extends BaseBindActivity<ActivityRegisterDetailBinding> implements BaseRegisterFragment.IRegisterListener {
    private int currentStep = EStepPosition.FIRST;
    private BaseRegisterFragment[] fragments;
    private RegisterDetailBean mDetailBean = new RegisterDetailBean();

    @Override
    protected int setView() {
        return R.layout.activity_register_detail;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        fragments = new BaseRegisterFragment[]{initFirstFragment(), initSecondFragment(), initThirdFragment(), initFourthFragment(), initFifthFragment()};
        bindingView.viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments[i];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
        bindingView.viewPager.setCurrentItem(currentStep);
    }

    private RegisterDetailFirstFragment initFirstFragment() {
        RegisterDetailFirstFragment fragment = new RegisterDetailFirstFragment();
        fragment.setListener(this);
        return fragment;
    }

    private RegisterDetailSecondFragment initSecondFragment() {
        RegisterDetailSecondFragment fragment = new RegisterDetailSecondFragment();
        fragment.setListener(this);
        return fragment;
    }

    private RegisterDetailThirdFragment initThirdFragment() {
        RegisterDetailThirdFragment fragment = new RegisterDetailThirdFragment();
        fragment.setListener(this);
        return fragment;
    }

    private RegisterDetailFourthFragment initFourthFragment() {
        RegisterDetailFourthFragment fragment = new RegisterDetailFourthFragment();
        fragment.setListener(this);
        return fragment;
    }

    private RegisterDetailFifthFragment initFifthFragment() {
        RegisterDetailFifthFragment fragment = new RegisterDetailFifthFragment();
        fragment.setListener(this);
        return fragment;
    }


    @Override
    protected void initEvent() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onBack() {
        currentStep = currentStep - 1;
        bindingView.viewPager.setCurrentItem(currentStep);
        fragments[currentStep].updateDetailUI(mDetailBean);
    }

    @Override
    public void onNext() {
        currentStep = currentStep + 1;
        bindingView.viewPager.setCurrentItem(currentStep);
        fragments[currentStep].updateDetailUI(mDetailBean);
    }

    @IntDef({EStepPosition.FIRST, EStepPosition.SECOND, EStepPosition.THIRD, EStepPosition.FOURTH, EStepPosition.FIFTH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EStepPosition {
        int FIRST = 0; //第一步：性别，生日
        int SECOND = 1; //第二步：身高
        int THIRD = 2; //第三步：所在地
        int FOURTH = 3; //第四步：昵称
        int FIFTH = 4; //第五步：头像
    }

    public RegisterDetailBean getDetailBean() {
        return mDetailBean;
    }

//    public void updateDetailBean(RegisterDetailBean bean) {
//        if (bean != null) {
//            mDetailBean = bean;
//        }
//    }
}
