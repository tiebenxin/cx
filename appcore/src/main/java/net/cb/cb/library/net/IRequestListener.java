package net.cb.cb.library.net;

/**
 * @author Liszt
 * @date 2020/4/23
 * Description
 */
public interface IRequestListener {
    void onSuccess(String content);

    void onFailed(String msg);
}
