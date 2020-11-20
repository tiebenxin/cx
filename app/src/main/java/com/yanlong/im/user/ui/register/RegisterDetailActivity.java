package com.yanlong.im.user.ui.register;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.RegisterDetailBean;
import com.yanlong.im.databinding.ActivityRegisterDetailBinding;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserBean;

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
    private boolean isFromRegister;
    private int infoStat = 0;

    @Override
    protected int setView() {
        return R.layout.activity_register_detail;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initInfoStat();
        fragments = new BaseRegisterFragment[]{initFirstFragment(), /*initSecondFragment(),*/ initThirdFragment(), initFourthFragment()/*, initFifthFragment()*/};
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
        fragment.setInfoStat(infoStat);
        return fragment;
    }

//    private RegisterDetailSecondFragment initSecondFragment() {
//        RegisterDetailSecondFragment fragment = new RegisterDetailSecondFragment();
//        fragment.setListener(this);
//        return fragment;
//    }

    private RegisterDetailThirdFragment initThirdFragment() {
        RegisterDetailThirdFragment fragment = new RegisterDetailThirdFragment();
        fragment.setListener(this);
        fragment.setInfoStat(infoStat);
        return fragment;
    }

    private RegisterDetailFourthFragment initFourthFragment() {
        RegisterDetailFourthFragment fragment = new RegisterDetailFourthFragment();
        fragment.setListener(this);
        fragment.setInfoStat(infoStat);
        return fragment;
    }

//    private RegisterDetailFifthFragment initFifthFragment() {
//        RegisterDetailFifthFragment fragment = new RegisterDetailFifthFragment();
//        fragment.setListener(this);
//        fragment.setInfoStat(infoStat);
//        return fragment;
//    }

    @Override
    public void onBackPressed() {
        if (currentStep > EStepPosition.FIRST) {
            currentStep = currentStep - 1;
            bindingView.viewPager.setCurrentItem(currentStep);
            fragments[currentStep].updateDetailUI(mDetailBean);
        } else {
            //不是新用户未设置
            if (infoStat != 1) {
                finish();
            }
        }
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void loadData() {
        Intent intent = getIntent();
        isFromRegister = intent.getBooleanExtra("isFromRegister", false);
        if (!isFromRegister) {
            UserBean myInfo = (UserBean) UserAction.getMyInfo();
            if (myInfo != null) {
                infoStat = myInfo.getInfoStat();
                //老用户未设置
                if (infoStat == 2) {
                    if (!TextUtils.isEmpty(myInfo.getHead())) {
                        mDetailBean.setAvatar(myInfo.getHead());
                    }
                    mDetailBean.setSex(myInfo.getSex());
                    if (!TextUtils.isEmpty(myInfo.getName())) {
                        mDetailBean.setNick(myInfo.getName());
                    }
                    if (myInfo.getBirthday() != -1) {
                        mDetailBean.setBirthday(myInfo.getBirthday());
                    }
                    if (!TextUtils.isEmpty(myInfo.getLocation())) {
                        mDetailBean.setLocation(myInfo.getLocation());
                    }
                    fragments[currentStep].updateDetailUI(mDetailBean);
                }
            }
        }
    }

    private void initInfoStat() {
        UserBean myInfo = (UserBean) UserAction.getMyInfo();
        if (myInfo != null) {
            infoStat = myInfo.getInfoStat();
        }
    }

    @Override
    public void onBack() {
        if (currentStep == 0) {
            onBackPressed();
        } else {
            currentStep = currentStep - 1;
            bindingView.viewPager.setCurrentItem(currentStep);
            fragments[currentStep].updateDetailUI(mDetailBean);
        }
    }

    @Override
    public void onNext() {
        currentStep = currentStep + 1;
        bindingView.viewPager.setCurrentItem(currentStep);
        fragments[currentStep].updateDetailUI(mDetailBean);
    }

    @Override
    public void onExit() {
        finish();
    }

    //去除身高
    @IntDef({EStepPosition.FIRST, EStepPosition.SECOND, EStepPosition.THIRD/*, EStepPosition.FOURTH, EStepPosition.FIFTH*/})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EStepPosition {
        int FIRST = 0; //第一步：性别，生日
        int SECOND = 1; //第二步：身高
        int THIRD = 2; //第三步：所在地
//        int FOURTH = 3; //第四步：昵称
//        int FIFTH = 4; //第五步：头像
    }

    public RegisterDetailBean getDetailBean() {
        return mDetailBean;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragments != null) {
            fragments[currentStep].onActivityResult(requestCode, resultCode, data);
        }
    }
}
