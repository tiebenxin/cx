package net.cb.cb.library.view;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;

import com.umeng.analytics.MobclickAgent;

import net.cb.cb.library.AppConfig;

/***
 * 统一的activity
 * @author jyj
 * @date 2016/12/7
 */
public class AppActivity extends AppCompatActivity {
    public Context context;
    public LayoutInflater inflater;
    public AlertWait alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initFont();
        context = getApplicationContext();
        inflater = getLayoutInflater();
        alert = new AlertWait(this);
        super.onCreate(savedInstanceState);
        //友盟Push后台进行日活统计及多维度推送的必调用方法

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        taskClearNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alert.dismiss4distory();
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


}
