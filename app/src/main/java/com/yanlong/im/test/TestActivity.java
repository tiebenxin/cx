package com.yanlong.im.test;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.StrikeButton;

public class TestActivity extends AppActivity {
    public static final String AGM_ID = "id";
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private android.support.v4.view.ViewPager viewPage;
    private android.support.design.widget.TabLayout bottomTab;
    private Fragment[] fragments;
    private String[] tabs;

    //自动寻找控件
    private void findViews() {
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        viewPage = (android.support.v4.view.ViewPager) findViewById(R.id.viewPage);
        bottomTab = (android.support.design.widget.TabLayout) findViewById(R.id.bottom_tab);
    }


    //自动生成的控件事件
    private void initEvent() {
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        fragments = new Fragment[]{OtherFragment.newInstance(),FontFragment.newInstance(), BtnFragment.newInstance(), ItemFragment.newInstance()};
        tabs = new String[]{"其他","字体", "按钮", "列表"};
        viewPage.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments[i];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });

        bottomTab.setupWithViewPager(viewPage);
        bottomTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //  viewPage.setCurrentItem(tab.getPosition());
                // Tab 选中之后，改变各个Tab的状态
                for (int i = 0; i < bottomTab.getTabCount(); i++) {
                    View rootView = bottomTab.getTabAt(i).getCustomView();
                    LinearLayout viewItem = (LinearLayout) rootView.findViewById(R.id.view_item);
                    StrikeButton sb = (net.cb.cb.library.view.StrikeButton) rootView.findViewById(R.id.sb);
                    TextView txt = (TextView) rootView.findViewById(R.id.txt);
                    if (i == tab.getPosition()) { // 选中状态
                        sb.setButtonBackground(R.mipmap.ic_me_h);
                        txt.setTextColor(getResources().getColor(R.color.green_500));
                        //  txt.setText("text");
                    } else {// 未选中状态
                        sb.setButtonBackground(R.mipmap.ic_me);
                        txt.setTextColor(getResources().getColor(R.color.gray_400));
                        //  txt.setText("text选中");
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // 提供自定义的布局添加Tab
        for (int i = 0; i < fragments.length; i++) {
            View rootView = getLayoutInflater().inflate(R.layout.tab_item, null);
            TextView txt = (TextView) rootView.findViewById(R.id.txt);
            StrikeButton sb = (net.cb.cb.library.view.StrikeButton) rootView.findViewById(R.id.sb);
            sb.setNum(i-1);

            txt.setText(tabs[i]);
            bottomTab.getTabAt(i).setCustomView(rootView);
        }
        //切换
        bottomTab.getTabAt(1).select();
        bottomTab.getTabAt(0).select();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViews();
        initEvent();
    }


}
