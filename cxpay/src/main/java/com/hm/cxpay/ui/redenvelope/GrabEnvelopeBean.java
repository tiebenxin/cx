package com.hm.cxpay.ui.redenvelope;

/**
 * @anthor Liszt
 * @data 2019/12/9
 * Description 抢红包接口bean
 */
public class GrabEnvelopeBean {
    String accessToken;//用户授权访问凭证,拆红包、详情等接口需上送
    int stat;//1:正常 2:已领完 3:已过期 4:其他(bug?已领取过...)
}
