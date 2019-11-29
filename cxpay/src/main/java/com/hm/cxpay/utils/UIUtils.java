package com.hm.cxpay.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

/**
 * @anthor Liszt
 * @data 2019/11/29
 * Description
 */
public class UIUtils {

    public static Drawable getDrawable(Context context, int drawableId) {
        return ContextCompat.getDrawable(context, drawableId);
    }
}
