package net.cb.cb.library.view;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.R;
import net.cb.cb.library.dialog.DialogLoadingProgress;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StatusBarUtils;
import net.cb.cb.library.utils.ThreadUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/***
 * 统一的activity
 * @author jyj
 * @date 2016/12/7
 */
public class AppActivity extends AppCompatActivity {
    public Context context;
    public LayoutInflater inflater;
    public AlertWait alert;
    public Boolean isFirstRequestPermissionsResult = true;//第一次请求权限返回
    DialogLoadingProgress payWaitDialog;

    private Finish mExit = new Finish();

    /**
     * 其他页面退出登录
     */
    private class Finish {
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onExitEvent(String myEvent) {
            //退出登录,关闭其他页面
            if (!isFinishing()) {
                finish();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initFont();
        context = getApplicationContext();
        inflater = getLayoutInflater();
        alert = new AlertWait(this);
        StatusBarUtils.setStatusBarColor(this, getResources().getColor(R.color.white));
        super.onCreate(savedInstanceState);
        //注册关闭其他页面事件
        EventBus.getDefault().register(mExit);
        //友盟Push后台进行日活统计及多维度推送的必调用方法
        if (savedInstanceState != null) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
            EventBus.getDefault().post(new EventFactory.RestartAppEvent());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
        taskClearNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销关闭其他页面事件
        EventBus.getDefault().unregister(mExit);
        alert.dismiss4distory();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object myEvent) {
    }

    //字体缩放倍数
    private static float fontScan = 1.0f;

    public void initFont() {
        if (fontScan == AppConfig.FONT)
            return;

        setFontScan(AppConfig.FONT);
    }

    /***
     * 设置app字体缩放倍率
     * @param fontSize
     */
    public void setFontScan(float fontSize) {
        this.fontScan = fontSize;
        AppConfig.setFont(fontSize);
        Resources resources = getResources();

        resources.getConfiguration().fontScale = fontSize;
        resources.updateConfiguration(resources.getConfiguration(), resources.getDisplayMetrics());
        // this.recreate();

        //  SharedPreferencesUtil sharedPreferencesUtil=new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_SCAN);

        //  sharedPreferencesUtil.save2Json(fontSize);
    }

    /***
     * 清理通知栏
     */
    private void taskClearNotification() {
        NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }


    public Context getContext() {
        return context;
    }

    /***
     * 直接跳转
     * @param c
     */
    public void go(Class c) {
        startActivity(new Intent(context, c));
    }


    public void hideKeyboard() {
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im != null) {
            im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 新增->强制弹出软键盘
     *
     * @param view 备注：延迟任务解决之前无法弹出问题
     */
    public void showSoftKeyword(final View view) {
        if (view == null) {
            return;
        }
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(view, 0);
                }
            }
        }, 100);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 127: {//申请定位权限返回
                Boolean hasPermissions = true;
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        hasPermissions = false;
                    }
                }
//                ToastUtil.show(this,"请打开定位权限");
                LogUtil.getLog().e("=申请定位权限返回=location=hasPermission=s=" + hasPermissions);

                if (!hasPermissions && !isFirstRequestPermissionsResult) {
                    AlertYesNo alertYesNo = new AlertYesNo();
                    alertYesNo.init(this, "提示", "您拒绝了定位权限，打开定位权限吗？", "确定", "取消", new AlertYesNo.Event() {
                        @Override
                        public void onON() {
                        }

                        @Override
                        public void onYes() {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                    alertYesNo.show();
                }
                isFirstRequestPermissionsResult = false;
                break;
            }
        }
    }

    public void showLoadingDialog() {
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                if (payWaitDialog == null) {
                    payWaitDialog = new DialogLoadingProgress(AppActivity.this);
                }
                if (isActivityValid()) {
                    payWaitDialog.setContent("正在加载中...");
                    payWaitDialog.show();
                }
            }
        });

    }

    public void showLoadingDialog(final String s) {
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                if (payWaitDialog == null) {
                    payWaitDialog = new DialogLoadingProgress(AppActivity.this);
                }
                if (isActivityValid()) {
                    payWaitDialog.setContent(s);
                    payWaitDialog.show();
                }
            }
        });

    }

    public void dismissLoadingDialog() {
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                if (isActivityValid() && payWaitDialog != null) {
                    payWaitDialog.dismiss();
                }
            }
        });

    }

    //activity 是否有效
    public boolean isActivityValid() {
        if (this == null || this.isDestroyed() || this.isFinishing()) {
            return false;
        }
        return true;
    }

    public void goWebActivity(Context context, String webUrl) {
        Intent intent = new Intent(context, WebPageActivity.class);
        intent.putExtra(WebPageActivity.AGM_URL, webUrl);
        startActivity(intent);
    }
}
