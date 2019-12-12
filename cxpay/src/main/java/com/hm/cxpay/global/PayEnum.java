package com.hm.cxpay.global;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Liszt
 * @date 2019/12/3
 * Description
 */
public class PayEnum {

    //红包支付方式
    @IntDef({EPayStyle.LOOSE, EPayStyle.BANK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EPayStyle {
        int LOOSE = 0;//零钱支付
        int BANK = 1;//银行卡支付
    }

    //红包类型
    @IntDef({ERedEnvelopeType.NORMAL, ERedEnvelopeType.LUCK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ERedEnvelopeType {
        int NORMAL = 0;//普通红包
        int LUCK = 1;//拼手气
    }

    //红包发送结果
    @IntDef({ESendResult.SUCCESS, ESendResult.FAIL, ESendResult.PENDING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ESendResult {
        int SUCCESS = 0;//成功
        int FAIL = 1;//失败
        int PENDING = 2;//待处理
    }

    //支付结果
    @IntDef({EPayResult.SUCCESS, EPayResult.FAIL, EPayResult.REFUND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EPayResult {
        int SUCCESS = 0;//成功
        int FAIL = 1;//失败
        int REFUND = 2;//成功后退款了
    }

    //支付结果
    @IntDef({EPayResult.SUCCESS, EPayResult.FAIL, EPayResult.REFUND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EEnvelopeOpenResult {
        int SUCCESS = 0;//成功
        int FAIL = 1;//失败
        int REFUND = 2;//成功后退款了
    }


}
