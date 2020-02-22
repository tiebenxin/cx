package net.cb.cb.library.bean;

/**
 * @author Liszt
 * @date 2020/2/22
 * Description 自定义bugly异常
 */
public class BuglyException extends Exception {
    public BuglyException() {
    }

    public BuglyException(String message) {
        super(message);
    }
}
