package net.cb.cb.library.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/***
 * @author jyj
 * @date 2016/11/29
 */
public class ToastUtil {
    private static Toast toast;

    public static void show(Context context, String txt) {
        if (txt != null && txt.length() > 0) {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(context, txt, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);

            toast.show();
        }

    }

    public static void show(Context context, int txt) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(context, txt, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
