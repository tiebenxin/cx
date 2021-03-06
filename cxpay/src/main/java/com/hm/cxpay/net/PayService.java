package com.hm.cxpay.net;

import com.hm.cxpay.bean.BillBean;
import com.hm.cxpay.bean.CommonBean;
import com.hm.cxpay.bean.TransferDetailBean;
import com.hm.cxpay.bean.TransferResultBean;
import com.hm.cxpay.bean.UrlBean;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.bean.BankInfo;
import com.hm.cxpay.bean.BindBankInfo;
import com.hm.cxpay.bean.EnvelopeDetailBean;
import com.hm.cxpay.bean.GrabEnvelopeBean;
import com.hm.cxpay.bean.OpenEnvelopeBean;
import com.hm.cxpay.bean.RedDetailsBean;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * @author Liszt
 * @date 2019/11/28
 * Description
 */
public interface PayService {

    //用户认证接口,idNumber 身份证号码；idType 目前只有身份证，传1即可；realName 真实姓名
    @POST(Route.URL_USER_AUTH)
    Observable<BaseResponse> authUserInfo(@Body RequestBody body, @QueryMap Map<String, String> map);

    //获取用户信息接口
    @POST(Route.GET_USER_INFO)
    Observable<BaseResponse<UserBean>> getUserInfo(@QueryMap Map<String, String> map);

    //银行卡检测接口
    @POST(Route.CHECK_BANK_CARD)
    Observable<BaseResponse<BankInfo>> checkBankCard(@Body RequestBody body, @QueryMap Map<String, String> map);

    //申请绑定银行卡
    @POST(Route.APPLY_BIND_BANK_CARD)
    Observable<BaseResponse<BindBankInfo>> applyBindBankCard(@Body RequestBody body, @QueryMap Map<String, String> map);

    //获取绑定银行卡列表
    @POST(Route.GET_BIND_BANK_CARD)
    Observable<BaseResponse<UrlBean>> getBankUrl(@QueryMap Map<String, String> map);

    //绑定银行卡
    @POST(Route.BIND_BANK)
    Observable<BaseResponse> bindBank(@Body RequestBody body, @QueryMap Map<String, String> map);

    //设置支付密码
    @POST(Route.SET_PAYWORD)
    Observable<BaseResponse> setPayword(@Body RequestBody body, @QueryMap Map<String, String> map);

    //修改支付密码
    @POST(Route.MODIFY_PAYWORD)
    Observable<BaseResponse> modifyPayword(@Body RequestBody body, @QueryMap Map<String, String> map);

    //忘记支付密码
    @POST(Route.FORGET_PAYWORD)
    Observable<BaseResponse> forgetPayword(@Body RequestBody body, @QueryMap Map<String, String> map);

    //设置检查密码
    @POST(Route.CHECK_PAYWORD)
    Observable<BaseResponse> checkPayword(@Body RequestBody body, @QueryMap Map<String, String> map);

    //解绑银行卡
//    @POST(Route.UNBIND_BANK_CARD)
//    Observable<BaseResponse> deleteBankcard(@Body RequestBody body, @QueryMap Map<String, String> map);

    //充值
    @POST(Route.TO_RECHARGE)
    Observable<BaseResponse<UrlBean>> toRecharge(@Body RequestBody body, @QueryMap Map<String, String> map);

    //提现
    @POST(Route.TO_WITHDRAW)
    Observable<BaseResponse<UrlBean>> toWithdraw(@Body RequestBody body, @QueryMap Map<String, String> map);

    //获取系统费率
    @POST(Route.GET_RATE)
    Observable<BaseResponse<CommonBean>> getRate(@QueryMap Map<String, String> map);

    //绑定手机-获取验证码
    @POST(Route.GET_PHONE_CODE)
    Observable<BaseResponse> getCode(@Body RequestBody body, @QueryMap Map<String, String> map);

    //绑定手机-获取当前用户IM手机号
    @POST(Route.GET_MY_PHONE)
    Observable<BaseResponse<CommonBean>> getMyPhone(@QueryMap Map<String, String> map);

    //绑定手机号
    @POST(Route.BIND_PHONE)
    Observable<BaseResponse> bindPhoneNum(@Body RequestBody body, @QueryMap Map<String, String> map);

    //获取账单明细
    @POST(Route.GET_BILL_DETAILS_LIST)
    Observable<BaseResponse<BillBean>> getBillDetailsList(@Body RequestBody body, @QueryMap Map<String, String> map);

    //获取零钱明细
    @POST(Route.GET_CHANGE_DETAILS_LIST)
    Observable<BaseResponse<BillBean>> getChangeDetailsList(@Body RequestBody body, @QueryMap Map<String, String> map);

    //验证实名信息-忘记密码辅助验证第一步
    @POST(Route.CHECK_REALNAME_INFO)
    Observable<BaseResponse<CommonBean>> checkRealNameInfo(@Body RequestBody body, @QueryMap Map<String, String> map);

    //绑定银行卡-忘记密码辅助验证第三步
    @POST(Route.BIND_BANK_CARD)
    Observable<BaseResponse<CommonBean>> bindBankCard(@Body RequestBody body, @QueryMap Map<String, String> map);

    //验证短信验证码-忘记密码辅助验证第四步
    @POST(Route.CHECK_CODE)
    Observable<BaseResponse<CommonBean>> checkCode(@Body RequestBody body, @QueryMap Map<String, String> map);


    //获取红包明细
    @POST(Route.GET_RED_ENVELOPE_DETAILS)
    Observable<BaseResponse<RedDetailsBean>> getRedEnvelopeDetails(@Body RequestBody body, @QueryMap Map<String, String> map);

    //发红包
    @POST(Route.SEND_RED_ENVELOPE)
    Observable<BaseResponse<UrlBean>> sendRedEnvelope(@Body RequestBody body, @QueryMap Map<String, String> map);

    //抢红包
    @POST(Route.SNATCH_RED_ENVELOPE)
    Observable<BaseResponse<GrabEnvelopeBean>> grabRedEnvelope(@Body RequestBody body, @QueryMap Map<String, String> map);

    //拆红包
    @POST(Route.OPEN_RED_ENVELOPE)
    Observable<BaseResponse<OpenEnvelopeBean>> openRedEnvelope(@Body RequestBody body, @QueryMap Map<String, String> map);

    //查看红包记录详情
    @POST(Route.GET_RED_ENVELOPE_DETAIL)
    Observable<BaseResponse<EnvelopeDetailBean>> getEnvelopeDetail(@Body RequestBody body, @QueryMap Map<String, String> map);

    //发送转账
    @POST(Route.SEND_TRANSFER)
    Observable<BaseResponse<UrlBean>> sendTransfer(@Body RequestBody body, @QueryMap Map<String, String> map);

    //获取转账详情
    @POST(Route.GET_TRANSFER_DETAIL)
    Observable<BaseResponse<TransferDetailBean>> getTransferDetail(@Body RequestBody body, @QueryMap Map<String, String> map);

    //领取转账
    @POST(Route.RECEIVE_TRANSFER)
    Observable<BaseResponse<TransferResultBean>> receiveTransfer(@Body RequestBody body, @QueryMap Map<String, String> map);

    //拒收转账
    @POST(Route.RETURN_TRANSFER)
    Observable<BaseResponse<TransferResultBean>> returnTransfer(@Body RequestBody body, @QueryMap Map<String, String> map);

    //商城->获取免登陆商城URL (测试环境)
    @POST(Route.SHOP_GET_URL_DEBUG)
    Observable<BaseResponse> getShopUrlDebug(@QueryMap Map<String, String> map);

    //商城->获取免登陆商城URL (正式环境)
    @POST(Route.SHOP_GET_URL_RELEASE)
    Observable<BaseResponse> getShopUrlRelease(@QueryMap Map<String, String> map);

    //商城->消费
    @POST(Route.SHOP_CONSUMPTION)
    Observable<BaseResponse<CommonBean>> shopConsumption(@Body RequestBody body,@QueryMap Map<String, String> map);

    //获取支付密码管理界面
    @POST(Route.GET_PSW_MANAGER)
    Observable<BaseResponse<UrlBean>> getPasswordManager( @QueryMap Map<String, String> map);

    //获取领取者信息接口
    @POST(Route.GET_HIS_USER_INFO)
    Observable<BaseResponse<String>> getHisInfo(@Body RequestBody body,@QueryMap Map<String, String> map);

}
