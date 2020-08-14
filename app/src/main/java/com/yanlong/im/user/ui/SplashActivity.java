package com.yanlong.im.user.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.utils.ApkUtils;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.RunUtils;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.view.AppActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppActivity {
    private static final String TAG = "SplashActivity";
    private UserAction userAction = new UserAction();
    private final static long TIME = 200; //启动页时间
    private int images[] = {R.mipmap.bg_index1, R.mipmap.bg_index2, R.mipmap.bg_index3};
    private List<ImageView> imgList;
    private String phone;
    Handler handler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        //外部app启动本应用本解析参数  分享到本app
        ApkUtils.startThisApp(this);

        //6.27 如果已经启动则不在启动这个页面,解决点击推送不能唤起appbug
//        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
//            finish();
//            return;
//        }

        setContentView(R.layout.activity_start_page);
        initView();
        initEvent();
        showPage();
    }


    private void initView() {
        phone = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.PHONE).get4Json(String.class);
    }


    private void startTimer() {
        //当计时结束时，跳转至主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferencesUtil preferencesUtil = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FIRST_TIME);
                Boolean isFirst = preferencesUtil.get4Json(Boolean.class);
                if (isFirst == null) {
                    goActivity(true);
                } else {
                    goActivity(false);
                }

            }
        }, TIME);
    }


    private void initEvent() {

    }


    private void updateToken(final boolean isFirst) {
        LogUtil.getLog().d("a=", SplashActivity.class.getSimpleName() + "--更新token");
        new RunUtils(new RunUtils.Enent() {
            String devId;

            @Override
            public void onRun() {
                devId = UserAction.getDevId(getContext());
            }

            @Override
            public void onMain() {
                userAction.updateToken(devId, new Callback<ReturnBean<TokenBean>>() {
                    @Override
                    public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ReturnBean<TokenBean>> call, Throwable t) {
                        LogUtil.getLog().i("youmeng", "SplashActivity---->updateToken---->onFailure");
                        if (isFirst) {
                            startActivity(new Intent(SplashActivity.this, SelectLoginActivity.class));
                            finish();
                        } else {
                            if (TextUtils.isEmpty(phone)) {
                                startActivity(new Intent(SplashActivity.this, PasswordLoginActivity.class));
                                finish();
                            } else {
                                go(LoginActivity.class);
                                finish();
                            }
                        }
                    }
                });
            }
        }).run();

    }


    private void goActivity(boolean isFirst) {
        //同步使用友盟设备号,如果同步失败使用自己设备号
        TokenBean token = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).get4Json(TokenBean.class);
        Long uid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.UID).get4Json(Long.class);
        if (token != null) {
            if ((!token.isTokenValid(uid) /*|| token.getBankReqSignKey()==null*/) && NetUtil.isNetworkConnected()) {
                updateToken(isFirst);
            } else {
                userAction.login4tokenNotNet(token);
                //6.17 无网处理
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                // 提前将全屏切换为非全屏状态，解决从全屏进入非全屏标题栏闪动的问题
                finish();
            }
        } else {
            if (TextUtils.isEmpty(phone)) {
                startActivity(new Intent(SplashActivity.this, SelectLoginActivity.class));//PasswordLoginActivity.class
                // 提前将全屏切换为非全屏状态，解决从全屏进入非全屏标题栏闪动的问题
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                // 提前将全屏切换为非全屏状态，解决从全屏进入非全屏标题栏闪动的问题
                finish();
            }
        }
    }


    private void showPage() {
        new CheckPermission2Util().requestPermissions(this, new CheckPermission2Util.Event() {
            @Override
            public void onSuccess() {
                LogUtil.getLog().d("a=", "Splash请求权限成功");
                startTimer();
            }

            @Override
            public void onFail() {
                LogUtil.getLog().d("a=", "Splash请求权限失败");
                startTimer();
            }
        }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.READ_PHONE_STATE});
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtil.getLog().d("a=", "Splash请求权限失败");
        startTimer();
    }
}
