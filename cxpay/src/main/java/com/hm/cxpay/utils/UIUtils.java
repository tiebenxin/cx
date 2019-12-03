package com.hm.cxpay.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.EditText;

import java.util.UUID;

/**
 * @anthor Liszt
 * @data 2019/11/29
 * Description
 */
public class UIUtils {

    public static Drawable getDrawable(Context context, int drawableId) {
        return ContextCompat.getDrawable(context, drawableId);
    }

    //分 转为 String（元）
    public static String getYuan(long amt) {
        double money = (amt * 1.00) / 100;
        return money + "";
    }

    //String 转为 分
    public static long getFen(String money) {
        double m = Double.parseDouble(money);
        long fen = (long) (m * 100);
        return fen;

    }

    //红包文案
    public static String getRedEnvelopeContent(EditText et) {
        String note = et.getText().toString();
        if (TextUtils.isEmpty(note)) {
            note = "恭喜发财，大吉大利";
        }
        return note;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString()/*.replace("-", "")*/;
    }
}
