package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.TokenBean;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.view.AppActivity;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartPageActivity extends AppActivity {
    private static final String TAG = "StartPageActivity";
    private UserAction userAction = new UserAction();
    private final static long TIME = 200; //启动页时间
    private ConstraintLayout mLayoutGuidance;
    private ViewPager mViewPager;
    private ImageView mBtnStart;
    private ImageView mIvStart;
    private ViewPagerAdapter adapter;
    private int images[] = {R.mipmap.bg_index1, R.mipmap.bg_index2, R.mipmap.bg_index3};
    private List<ImageView> imgList;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initView();
        initEvent();
        showPage();
    }

    private void initView() {
        phone = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.PHONE).get4Json(String.class);
        mLayoutGuidance = findViewById(R.id.layout_guidance);
        mViewPager = findViewById(R.id.view_pager);
        mBtnStart = findViewById(R.id.btn_start);
        mIvStart = findViewById(R.id.iv_start);
    }

    private void initViewPager() {
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
                if (i == (images.length - 1)) {
                    mBtnStart.setVisibility(View.VISIBLE);
                } else {
                    mBtnStart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    private void startTimer() {
        mLayoutGuidance.setVisibility(View.GONE);
        mIvStart.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        //当计时结束时，跳转至主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goActivity(false);
            }
        }, TIME);
    }


    private void initEvent() {
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goActivity(true);
            }
        });
    }


    private void updateToken(final boolean isFlast) {
        userAction.login4token(UserAction.getDevId(getContext()), new Callback<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if (isFlast) {
                    startActivity(new Intent(StartPageActivity.this, SelectLoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(StartPageActivity.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<TokenBean>> call, Throwable t) {
                if (isFlast) {
                    startActivity(new Intent(StartPageActivity.this, SelectLoginActivity.class));
                    finish();
                } else {
                    if (TextUtils.isEmpty(phone)) {
                        startActivity(new Intent(StartPageActivity.this, PasswordLoginActivity.class));
                        finish();
                    } else {
                        go(LoginActivity.class);
                        finish();
                    }
                }
            }
        });
    }


    private void goActivity(boolean isFlast) {
        //同步使用友盟设备号,如果同步失败使用自己设备号
        if (NetUtil.isNetworkConnected()) {
            updateToken(isFlast);
        } else {
            TokenBean token = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).get4Json(TokenBean.class);
            if (token != null) {
                //6.17 无网处理
                userAction.login4tokenNotNet(token);
                startActivity(new Intent(StartPageActivity.this, MainActivity.class));
                finish();
            } else {
                if (TextUtils.isEmpty(phone)) {
                    startActivity(new Intent(StartPageActivity.this, PasswordLoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(StartPageActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }
    }

    private void showPage() {
        SharedPreferencesUtil preferencesUtil = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FIRST_TIME);
        Boolean isFiast = preferencesUtil.get4Json(Boolean.class);
        if (isFiast == null) {
            initViewPager();
            preferencesUtil.save2Json(true);
        } else {
            startTimer();
        }
    }


    class ViewPagerAdapter extends PagerAdapter {

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
