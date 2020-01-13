package com.hm.cxpay.rx;

/**
 * @author Liszt
 * @date 2019/11/27
 * Description
 */
public class PayHostUtils {
    //    private static final String PORT_8888 = ":8888";//http端口
    private static final String PORT_9898 = ":9898";//http端口
    private static final String PORT_19991 = ":8080";//http端口
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
            private static final String HOST = "yanlong.1616d.top";//https 路径
    private static final String HOST_TEST = "test.zhixun6.com";//https 路径


    //外网正式服  https
    public static String getHttpsUrl() {
//        return HTTPS + HOST_TEST + PORT_9898;
        return HTTPS + HOST + PORT_19991;
    }

}
