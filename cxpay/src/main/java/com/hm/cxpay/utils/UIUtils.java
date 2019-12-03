package com.hm.cxpay.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.EditText;

/**
 * @anthor Liszt
 * @data 2019/11/29
 * Description
 */
public class UIUtils {

    public static Drawable getDrawable(Context context, int drawableId) {
        return ContextCompat.getDrawable(context, drawableId);
    }

    public static String getYuan(long amt) {
        double money = (amt * 1.00) / 100;
        return money + "元";
    }

    //红包文案
    public static String getRedEnvelopeContent(EditText et) {
        String note = et.getText().toString();
        if (TextUtils.isEmpty(note)) {
            note = "恭喜发财，大吉大利";
        }
        return note;
    }
}
