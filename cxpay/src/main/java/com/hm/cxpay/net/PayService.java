package com.hm.cxpay.net;

import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.bank.BankInfo;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.POST;

/**
 * @anthor Liszt
 * @data 2019/11/28
 * Description
 */
public interface PayService {

    //用户认证接口,idNumber 身份证号码；idType 目前只有身份证，传1即可；realName 真实姓名
    @POST(Route.URL_USER_AUTH)
    Observable<BaseResponse> authUserInfo(@Body RequestBody body);

    //银行卡检测接口
    @POST(Route.CHECK_BANK_CARD)
    Observable<BaseResponse<BankInfo>> checkBankCard(@Body RequestBody body);

}
