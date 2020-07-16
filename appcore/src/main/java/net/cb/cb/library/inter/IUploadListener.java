package net.cb.cb.library.inter;

/**
 * @author Liszt
 * @date 2020/7/15
 * Description
 */
public interface IUploadListener {
    void onSuccess(Object result);

    void onFailed();

    void onProgress(int progress);
}
