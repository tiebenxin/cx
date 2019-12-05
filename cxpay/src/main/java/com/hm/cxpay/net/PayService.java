package com.hm.cxpay.net;

import com.hm.cxpay.bean.CommonBean;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.bank.BankBean;
import com.hm.cxpay.ui.bank.BankInfo;
import com.hm.cxpay.ui.bank.BindBankInfo;
import com.hm.cxpay.ui.redenvelope.RedDetailsBean;
import com.hm.cxpay.ui.redenvelope.RedSendBean;

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

    //获取用户信息接口
    @POST(Route.GET_USER_INFO)
    Observable<BaseResponse<UserBean>> getUserInfo();

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

    //设置支付密码
    @POST(Route.SET_PAYWORD)
    Observable<BaseResponse> setPayword(@Body RequestBody body);

    //修改支付密码
    @POST(Route.MODIFY_PAYWORD)
    Observable<BaseResponse> modifyPayword(@Body RequestBody body);

    //设置检查密码
    @POST(Route.CHECK_PAYWORD)
    Observable<BaseResponse> checkPayword(@Body RequestBody body);

    //充值
    @POST(Route.TO_RECHARGE)
    Observable<BaseResponse<CommonBean>> toRecharge(@Body RequestBody body);

    //提现
    @POST(Route.TO_WITHDRAW)
    Observable<BaseResponse<CommonBean>> toWithdraw(@Body RequestBody body);

    //获取系统费率
    @POST(Route.GET_RATE)
    Observable<BaseResponse<CommonBean>> getRate();

    //获取红包明细
    @POST(Route.GET_RED_ENVELOPE_DETAILS)
    Observable<BaseResponse<RedDetailsBean>> getRedEnvelopeDetails(@Body RequestBody body);

    //发红包
    @POST(Route.SEND_RED_ENVELOPE)
    Observable<BaseResponse<RedSendBean>> sendRedEnvelope(@Body RequestBody body);


}
