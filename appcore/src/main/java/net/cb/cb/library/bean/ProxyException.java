package net.cb.cb.library.bean;

/**
 * @author Liszt
 * @date 2020/2/22
 * Description 网络代理异常，反抓包
 */
public class ProxyException extends Exception {
    public ProxyException() {
    }

    public ProxyException(String message) {
        super(message);
    }
}
