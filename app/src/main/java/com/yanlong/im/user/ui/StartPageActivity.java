package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;

import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.AppActivity;

import java.util.ArrayList;
import java.util.List;

public class StartPageActivity extends AppActivity {
    private final static long TIME = 2000; //启动页时间
    private ConstraintLayout mLayoutGuidance;
    private ViewPager mViewPager;
    private ImageView mBtnStart;
    private ImageView mIvStart;
    private ViewPagerAdapter adapter;
    private int images [] = {R.mipmap.bg_index1,R.mipmap.bg_index2,R.mipmap.bg_index3};
    private List <ImageView> imgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        initView();
        initEvent();
        showPage();
    }

    private void initView() {
        mLayoutGuidance = findViewById(R.id.layout_guidance);
        mViewPager = findViewById(R.id.view_pager);
        mBtnStart = findViewById(R.id.btn_start);
        mIvStart = findViewById(R.id.iv_start);
    }

    private void initViewPager(){
        imgList = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            ImageView imageView = new ImageView(StartPageActivity.this);
            imageView.setImageResource(images[i]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imgList.add(imageView);
        }
        adapter = new ViewPagerAdapter();
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if(i == (images.length - 1)){
                    mBtnStart.setVisibility(View.VISIBLE);
                }else{
                    mBtnStart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    private void startTimer(){
        mLayoutGuidance.setVisibility(View.GONE);
        mIvStart.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        //当计时结束时，跳转至主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goActivity();
            }
        }, TIME);
    }


    private void initEvent(){
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goActivity();
            }
        });

    }


    private void goActivity(){


        startActivity(new Intent(StartPageActivity.this, MainActivity.class));
        onBackPressed();
    }

    private void showPage(){
        SharedPreferencesUtil preferencesUtil = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FIRST_TIME);
        Boolean isFiast = preferencesUtil.get4Json(Boolean.class);
        if(isFiast == null){
            initViewPager();
            preferencesUtil.save2Json(true);
        }else{
            startTimer();
        }
    }



    class ViewPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return imgList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            container.addView(imgList.get(position));
            return imgList.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }


}
