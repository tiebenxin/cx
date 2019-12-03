package com.hm.cxpay.net;

/**
 * @anthor Liszt
 * @data 2019/11/28
 * Description
 */
public class Route {
    //查看支持银行url
    public static final String SUPPORT_BANK_URL = "http://baidu.com";

    //零钱的用户协议
    public static final String USER_AGREEMENT_OF_PAY = "http://baidu.com";

    //实名认证
    public static final String URL_USER_AUTH = "/user/real_name_auth";

    //检测绑定银行卡
    public static final String CHECK_BANK_CARD = "/user/get_bank_card_info_and_check";

    //申请绑定银行卡
    public static final String APPLY_BIND_BANK_CARD = "/user/apply_bind_bank_card";

    //获取绑定银行卡
    public static final String GET_BIND_BANK_CARD = "/user/get_my_bank_cards";

    //绑定银行卡
    public static final String BIND_BANK = "/user/bind_bank_card";

    //获取用户信息
    public static final String GET_USER_INFO = "/user/get_user_info";

    //设置支付密码
    public static final String SET_PAYWORD = "/user/set_pay_pwd";

    //检查支付密码
    public static final String CHECK_PAYWORD = "/user/check_pay_pwd";

    //充值
    public static final String TO_RECHARGE = "/order/deposit";

    //获取红包明细
    public static final String GET_RED_ENVELOPE_DETAILS = "/user/get_red_envelope_list";

    //发送红包
    public static final String SEND_RED_ENVELOPE = "/user/send_red_envelope";
}
