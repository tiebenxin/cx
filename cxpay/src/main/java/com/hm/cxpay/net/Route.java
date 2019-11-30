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
}
