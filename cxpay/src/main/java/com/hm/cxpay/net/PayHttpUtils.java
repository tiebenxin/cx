package com.hm.cxpay.net;

import com.hm.cxpay.rx.data.BaseResponse;

import io.reactivex.Observable;

/**
 * @anthor Liszt
 * @data 2019/11/28
 * Description
 */
public class PayHttpUtils {

    private static PayHttpUtils instance;

    public static PayHttpUtils getInstance() {
        if (instance == null) {
            instance = new PayHttpUtils();
        }
        return instance;
    }

    //用户认证
    public Observable<BaseResponse> authUserInfo(String idNum, String realName) {
        return HttpChannel.getInstance().getPayService().authUserInfo(idNum, 1, realName);
    }
}
