package net.cb.cb.library.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

public class ScreenUtil {
    public static int getScreenHeight(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    public static float sp2Px(Context context, int size) {
        return TypedValue.applyDimension(2, (float)size, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(spValue * fontScale + 0.5F);
    }

    public static float px2Sp(int size, Context context) {
        return TypedValue.applyDimension(0, (float)size, context.getResources().getDisplayMetrics());
    }

    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5F);
    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5F);
    }

    public static int dp2px(Context context, int dp) {
        return (int)TypedValue.applyDimension(1, (float)dp, context.getResources().getDisplayMetrics());
    }

    public static int dpToPx(Resources res, int dp) {
        return (int)TypedValue.applyDimension(0, (float)dp, res.getDisplayMetrics());
    }
}
