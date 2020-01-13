package net.cb.cb.library.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.R;

/***
 * @author jyj
 * @date 2016/11/29
 */
public class ToastUtil {
    private static Toast toast;
    private static boolean isShow = true;

    public static void show(Context context, String txt) {
        try {
            if (context.getResources().getString(R.string.forward_success).equals(txt)) {// 用于处理转发多条，有禁言的时候，只弹出转发成功提示
                isShow = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isShow = true;
                    }
                }, 1000);
            } else {
                isShow = true;
            }
        } catch (Exception e) {

        }
        if (txt != null && txt.length() > 0) {
            if (toast != null)
                toast.cancel();
            try {
                //  Looper.prepare();
                toast = Toast.makeText(context, txt, Toast.LENGTH_SHORT);
                //    Looper.loop();
            } catch (Exception e) {
                e.printStackTrace();
            }

            toast.show();
        }
    }

    public static void showCenter(Context context, String txt) {
        if (isShow) {// 用于处理转发多条，有禁言的时候，只弹出转发成功提示
            if (txt != null && txt.length() > 0) {
                if (toast != null)
                    toast.cancel();
                try {
                    //  Looper.prepare();
                    toast = Toast.makeText(context, txt, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    //    Looper.loop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                toast.show();
            }
        } else {
            isShow = true;
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
     * 新增->长提示
     *
     * @param context
     * @param txt
     */
    public static void showLong(Context context, String txt) {
        if (txt != null && txt.length() > 0) {
            if (toast != null)
                toast.cancel();
            try {
                //  Looper.prepare();
                toast = Toast.makeText(context, txt, Toast.LENGTH_LONG);
                //    Looper.loop();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //    toast.setGravity(Gravity.CENTER, 0, 0);

            toast.show();
        }

    }


    public static void show(String txt) {
        if (txt != null && txt.length() > 0 && AppConfig.APP_CONTEXT != null) {
            if (toast != null) {
                toast.cancel();
            }
            try {
                toast = Toast.makeText(AppConfig.APP_CONTEXT, txt, Toast.LENGTH_SHORT);
                toast.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
