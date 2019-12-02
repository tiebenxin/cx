package com.hm.cxpay.net;

import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.bank.BankBean;
import com.hm.cxpay.ui.bank.BankInfo;
import com.hm.cxpay.ui.bank.BindBankInfo;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
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

    //申请绑定银行卡
    @POST(Route.APPLY_BIND_BANK_CARD)
    Observable<BaseResponse<BindBankInfo>> applyBindBankCard(@Body RequestBody body);

    //获取绑定银行卡列表
    @POST(Route.GET_BIND_BANK_CARD)
    Observable<BaseResponse<List<BankBean>>> getBindBankCardList();

    //绑定银行卡
    @POST(Route.BIND_BANK)
    Observable<BaseResponse> bindBank(@Body RequestBody body);

}
