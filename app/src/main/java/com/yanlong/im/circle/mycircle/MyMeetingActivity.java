package com.yanlong.im.circle.mycircle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;

import com.yanlong.im.R;
import com.yanlong.im.circle.adapter.CommonPagerAdapter;
import com.yanlong.im.databinding.ActivityMyMeetingBinding;

import net.cb.cb.library.base.bind.BaseBindActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @类名：谁看过我(我的遇见)
 * @Date：2020/9/24
 * @by zjy
 * @备注：
 */
public class MyMeetingActivity extends BaseBindActivity<ActivityMyMeetingBinding> implements ViewPager.OnPageChangeListener {

    private List<Fragment> fragments;
    private MyMeetingFragment FGWhoSeeMe;//谁看过我
    private MyMeetingFragment FGISeeMe;//我看过谁
    private CommonPagerAdapter adapter;

    @Override
    protected int setView() {
        return R.layout.activity_my_meeting;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        fragments = new ArrayList<>();
        FGWhoSeeMe = new MyMeetingFragment();
        FGISeeMe = new MyMeetingFragment();
        fragments.add(FGWhoSeeMe);
        fragments.add(FGISeeMe);
        adapter = new CommonPagerAdapter(getSupportFragmentManager(),fragments);
        bindingView.viewpager.setAdapter(adapter);
        bindingView.viewpager.addOnPageChangeListener(this);
        //初始设置默认选中第一个
        selectFragment(0);
    }

    @Override
    protected void initEvent() {
        setActionBarLeft(bindingView.headView);
        //点击TAB切换fragment
        bindingView.tvWhoSeeMe.setOnClickListener(v-> selectFragment(0));
        bindingView.tvISeeWho.setOnClickListener(v-> selectFragment(1));
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {
        //左右滑动切换fragment
        if(position==0){
            selectFragment(0);
        }else {
            selectFragment(1);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    //更新选中状态
    public void selectFragment(int position){
        bindingView.viewpager.setCurrentItem(position);
        if(position == 0){
            bindingView.tvWhoSeeMe.setTextColor(getResources().getColor(R.color.c_343434));
            bindingView.tvWhoSeeMe.setTextSize(TypedValue.COMPLEX_UNIT_SP,21);
            bindingView.tvISeeWho.setTextColor(getResources().getColor(R.color.c_767676));
            bindingView.tvISeeWho.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        }else if(position == 1){
            bindingView.tvWhoSeeMe.setTextColor(getResources().getColor(R.color.c_767676));
            bindingView.tvWhoSeeMe.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            bindingView.tvISeeWho.setTextColor(getResources().getColor(R.color.c_343434));
            bindingView.tvISeeWho.setTextSize(TypedValue.COMPLEX_UNIT_SP,21);
        }
    }
}
