package com.example.nim_lib.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

/***
 * @author jyj
 * @date 2016/11/29
 */
public class ToastUtil {
    private static Toast toast;
    private static final String TAG = "ToastUtils";
    private static long lastClickTime;
    private static long mDuration = 0;
    private static Context mContext;
    private static String mContent = "";
    private static Toast mToast;
    /**
     * 上限5s
     */
    private static final long SHORT_DURATION_TIMEOUT = 5000;
    /**
     * 下限时间500ms
     */
    private static final long LONG_DURATION_TIMEOUT = 500;
    //默认时间1s
    private static final long DEFAULT_TIME = 1000;
    private static Handler mHandler = new Handler();

    public static void show(Context context, String txt) {
        if (txt != null && txt.length() > 0) {
            if (toast != null)
                toast.cancel();
            try{
              //  Looper.prepare();
                toast = Toast.makeText(context, txt, Toast.LENGTH_SHORT);
            //    Looper.loop();
            }catch (Exception e){
                e.printStackTrace();
            }

        //    toast.setGravity(Gravity.CENTER, 0, 0);

            toast.show();
        }

    }

    public static void show(Context context, int txt) {
        if (toast != null)
            toast.cancel();
        try {
           // Looper.prepare();
            toast = Toast.makeText(context, txt, Toast.LENGTH_SHORT);
           // Looper.loop();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
          toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * @param context
     * @param content  显示内容
     * @param duration 自定义显示时长
     */
    public static void showToastCusttomTime(Context context, String content, long duration) {
        if (ViewUtils.isFastDoubleClick()) return;
        if (context == null || TextUtils.isEmpty(content)) {
            return;
        }
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing())
                return;
        }
        show(context, content, duration);
    }

    private static void show(Context context, String content, long duration) {
        mContext = context;
        mContent = content;
        mDuration = duration;
        LogUtil.getLog().d(TAG, "Show custom toast");
        if (null == mHandler) {
            mHandler = new Handler();
        }
        mHandler.post(showRunnable);
    }

    private static void hide() {
        LogUtil.getLog().d(TAG, "Hide custom toast");
        mDuration = 0;
        if (mToast != null) {
            mToast.cancel();
            if (null != showRunnable) {
                mHandler.removeCallbacks(showRunnable);
                mHandler = null;
                LogUtil.getLog().i("xxffff", "Remove runnable");
            }
        }
    }

    private static Runnable showRunnable = new Runnable() {
        @Override
        public void run() {
            if (mDuration != 0) {
                mToast = Toast.makeText(mContext, mContent, Toast.LENGTH_LONG);
                mToast.show();
            } else {
                LogUtil.getLog().d(TAG, "Hide custom toast in runnable");
                hide();
                return;
            }
            if (mDuration > SHORT_DURATION_TIMEOUT) {
                mDuration = SHORT_DURATION_TIMEOUT;
            } else if (mDuration < LONG_DURATION_TIMEOUT) {
                mDuration = DEFAULT_TIME;
            }
            mHandler.postDelayed(showRunnable, mDuration);
            mDuration = 0;
        }
    };
}
