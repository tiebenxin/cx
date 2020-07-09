package com.hm.cxpay.net;

/**
 * @author Liszt
 * @date 2019/11/28
 * Description
 */
public class Route {

    public static final String BANK = "/bank/v2";

    //查看支持银行url
    public static final String SUPPORT_BANK_URL = "https://changxin.zhixun6.com/bank.html";

    //零钱的用户协议
    public static final String USER_AGREEMENT_OF_PAY = "https://changxin.zhixun6.com/yhxy.html";

    //实名认证
    public static final String URL_USER_AUTH = BANK+"/user/real_name_auth";

    //检测绑定银行卡
    public static final String CHECK_BANK_CARD = BANK+"/user/get_bank_card_info_and_check";

    //申请绑定银行卡
    public static final String APPLY_BIND_BANK_CARD = BANK+"/user/apply_bind_bank_card";

    //获取银行卡管理页面url
    public static final String GET_BIND_BANK_CARD = BANK+"/user/to-bank-cards-mgr";

    //绑定银行卡
    public static final String BIND_BANK = BANK+"/user/bind_bank_card";

    //获取用户信息
    public static final String GET_USER_INFO = BANK+"/user/get_user_info";

    //设置支付密码
    public static final String SET_PAYWORD = BANK+"/user/set_pay_pwd";

    //修改支付密码
    public static final String MODIFY_PAYWORD = BANK+"/user/modify_pay_pwd";

    //忘记支付密码
    public static final String FORGET_PAYWORD = BANK+"/user/modify_pay_pwd_forgot";

    //检查支付密码
    public static final String CHECK_PAYWORD = BANK+"/user/check_pay_pwd";

    //解绑银行卡
//    public static final String UNBIND_BANK_CARD = BANK+"/user/unbind_bank_card";

    //充值
    public static final String TO_RECHARGE = BANK+"/order/to_deposit";

    //提现
    public static final String TO_WITHDRAW = BANK+"/order/to_withdraw_apply";

    //提现
    public static final String GET_RATE = BANK+"/user/get_rate";

    //获取验证码
    public static final String GET_PHONE_CODE = BANK+"/user/get_sms_code_for_bind_phone";

    //获取当前用户IM手机号
    public static final String GET_MY_PHONE = BANK+"/user/get_user_im_phone";

    //绑定手机号
    public static final String BIND_PHONE = BANK+"/user/bind_phone";

    //获取账单明细
    public static final String GET_BILL_DETAILS_LIST = BANK+"/bill/get_trans_list";

    //获取零钱明细
    public static final String GET_CHANGE_DETAILS_LIST = BANK+"/bill/get_balance_trans_list";

    //验证实名信息-忘记密码辅助验证第一步
    public static final String CHECK_REALNAME_INFO = BANK+"/user/check_id_card";

    //绑定银行卡-忘记密码辅助验证第三步
    public static final String BIND_BANK_CARD = BANK+"/user/apply_bind_bank_card_for_check_req";

    //验证短信验证码-忘记密码辅助验证第四步
    public static final String CHECK_CODE = BANK+"/user/verify_sms_code_for_bind_card_check";

    //获取红包明细
    public static final String GET_RED_ENVELOPE_DETAILS = BANK+"/bill/get_red_envelope_list";

    //发送红包
    public static final String SEND_RED_ENVELOPE = BANK+"/order/send_red_envelope";

    //抢红包
    public static final String SNATCH_RED_ENVELOPE = BANK+"/order/snatch_red_envelope";

    //获取单个红包详情
    public static final String GET_RED_ENVELOPE_DETAIL = BANK+"/order/get_red_envelope_detail";

    //拆红包
    public static final String OPEN_RED_ENVELOPE = BANK+"/order/open_red_envelope";

    //发送转账
    public static final String SEND_TRANSFER = BANK+"/order/transfer";

    //获取转账详情
    public static final String GET_TRANSFER_DETAIL = BANK+"/order/get_trans_detail";

    //领取转账
    public static final String RECEIVE_TRANSFER = BANK+"/order/receive_transfer";

    //退还转账
    public static final String RETURN_TRANSFER = BANK+"/order/reject_transfer";

    //商城->获取免登陆商城URL (测试环境)
    public static final String SHOP_GET_URL_DEBUG = BANK+"/store/getStoreAutoLoginUrl";

    //商城->获取免登陆商城URL (正式环境)
    public static final String SHOP_GET_URL_RELEASE = "/bank/store/getStoreAutoLoginUrl";

    //商城->消费
    public static final String SHOP_CONSUMPTION = BANK+"/store/consumer";

    //获取支付密码管理URL
    public static final String GET_PSW_MANAGER = BANK+"/user/to-password-manage";

    //获取领取者信息
    public static final String GET_HIS_USER_INFO = BANK+"/user/get_recv_user_info";
}
