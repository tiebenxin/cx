package com.yanlong.im.pay.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yanlong.im.R;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import java.util.ArrayList;
import java.util.List;

public class RedpacketRecordActivity extends AppActivity {


    private ActionbarView mActionBar;
    private SimpleDraweeView mSdImageHead;
    private TextView mTvSelectDate;
    private TextView mTvUserName;
    private TextView mTvMoney;
    private TabLayout mTabLayout;
    private ViewPager mViewPage;
    List<Fragment> fragments;
    String[] tabTiles = new String[]{"我收到的", "我发出的"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_packet_record);
        initView();
        initViewpager();
        initEvent();
    }

    private void initView() {
        mActionBar = findViewById(R.id.action_bar);
        mSdImageHead = findViewById(R.id.sd_image_head);
        mTvSelectDate = findViewById(R.id.tv_select_date);
        mTvUserName = findViewById(R.id.tv_user_name);
        mTvMoney = findViewById(R.id.tv_money);
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPage = findViewById(R.id.viewPage);
    }

    private void initViewpager() {
        fragments = new ArrayList<>();
        fragments.add(RedpacketRecordFragment.newInstance());
        fragments.add(RedpacketRecordFragment.newInstance());
        mViewPage.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabTiles[position];
            }
        });
        mTabLayout.setupWithViewPager(mViewPage);
    }

    private void initEvent(){
        mActionBar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        mTvSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(RedpacketRecordActivity.this,"选择时间");
            }
        });
    }


}
