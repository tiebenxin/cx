package net.cb.cb.library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.R;

/***
 * @author jyj
 * @date 2016/11/29
 * @备注 TODO 2020/5/28 zjy 全部改为居中显示,效果统一
 */
public class ToastUtil {
    private static Toast toast;
    private static boolean isShow = true;

    /**
     * 默认显示toast
     * @param context
     * @param txt
     */
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
                toast.setGravity(Gravity.CENTER, 0, 0);
                //    Looper.loop();
            } catch (Exception e) {
                e.printStackTrace();
            }

            toast.show();
        }
    }

    /**
     * 自定义context的居中显示toast
     * @param context
     * @param txt
     */
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

    /**
     * 自定义context的居中显示toast
     * @param context
     * @param txt
     */
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
     * 居中的长提示toast
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }

    /**
     * 系统级context的居中toast
     * @param txt
     */
    public static void show(String txt) {
        if (txt != null && txt.length() > 0 && AppConfig.APP_CONTEXT != null) {
            if (toast != null) {
                toast.cancel();
            }
            try {
                toast = Toast.makeText(AppConfig.APP_CONTEXT, null, Toast.LENGTH_SHORT);
                toast.setText(txt);//修复小米会显示项目名问题如: "常信:点赞成功"
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 自定义风格toast
     * @param context
     * @param txt
     * @param type 0 默认样式  1 收藏样式
     */
    public static void showToast(Context context, String txt,int type) {
        if (toast != null)
            toast.cancel();
        try {
            toast = Toast.makeText(context, txt, Toast.LENGTH_SHORT);
            if(type==0){
                View inflate = ((Activity)context).getLayoutInflater().inflate(R.layout.view_custom_toast, null);
                TextView toast_msg = inflate.findViewById(R.id.txt_msg);
                toast_msg.setText(txt);
                toast.setView(inflate);
            }else {
                View inflate = ((Activity)context).getLayoutInflater().inflate(R.layout.view_collect_toast, null);
                TextView toast_msg = inflate.findViewById(R.id.txt_msg);
                toast_msg.setText(txt);
                toast.setView(inflate);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
