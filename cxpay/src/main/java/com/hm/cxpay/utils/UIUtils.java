package com.hm.cxpay.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
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
        long fen = 0;
        if (TextUtils.isEmpty(money)) {
            return fen;
        }
        try {
            double m = Double.parseDouble(money);
            fen = (long) (m * 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fen;

    }

    //红包文案
    public static String getRedEnvelopeContent(EditText et) {
        String note = et.getText().toString().trim();
        if (TextUtils.isEmpty(note)) {
            note = "恭喜发财，大吉大利";
        }
        return note;
    }

    //获取红包个数
    public static int getRedEnvelopeCount(String count) {
        int c = 0;
        if (TextUtils.isEmpty(count)) {
            return c;
        }
        try {
            if (!TextUtils.isEmpty(count)) {
                c = Integer.parseInt(count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return c;
    }

    //获取红包个数
    public static int getRedEnvelopeCount(Editable et) {
        int c = 0;
        if (et == null) {
            return c;
        }
        String count = et.toString().trim();
        try {
            if (!TextUtils.isEmpty(count)) {
                c = Integer.parseInt(count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return c;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString()/*.replace("-", "")*/;
    }
}
