package com.yanlong.im.circle.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @类名：通用viewPager适配器
 * @Date：2020/9/24
 * @by zjy
 * @备注：
 */
public class CommonPagerAdapter extends FragmentPagerAdapter {
    //存储所有的fragment
    private List<Fragment> list;

    public CommonPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public int getCount() {
        return list.size();
    }

}
