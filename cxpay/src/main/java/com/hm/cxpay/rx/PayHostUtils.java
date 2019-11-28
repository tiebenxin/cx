package com.hm.cxpay.rx;

/**
 * @anthor Liszt
 * @data 2019/11/27
 * Description
 */
public class PayHostUtils {
//    private static final String PORT_8888 = ":8888";//http端口
    private static final String PORT_8888 = ":9898";//http端口
//    private static final String HOST = "192.168.10.112";//http路径  测试服地址
    private static final String HOST = "192.168.10.229";//http路径
    private static final String HTTP = "http://";

    public static String getHttpUrl() {
        return HTTP + HOST + PORT_8888;
    }
}
