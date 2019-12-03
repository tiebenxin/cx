package com.hm.cxpay.global;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @anthor Liszt
 * @data 2019/12/3
 * Description
 */
public class PayEnum {

    @IntDef({EPayStyle.LOOSE, EPayStyle.BANK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EPayStyle {
        int LOOSE = 0;//零钱支付
        int BANK = 1;//银行卡支付
    }

}
