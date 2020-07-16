package net.cb.cb.library.net;

import com.jrmf360.tools.utils.ThreadUtil;
import io.reactivex.Observer;

/**
 * Created by LL130386 on 2017/12/22.
 */

public abstract class FileDownloadObserver<T> implements Observer<T> {

    @Override
    public void onNext(final T t) {
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                onSuccess(t);
            }
        });
    }

    @Override
    public void onError(Throwable e) {
        onFailed(e);
    }

    @Override
    public void onComplete() {
        onProgress(100);
    }

    //监听进度的改变
    public void onProgressChange(long bytesWritten, long contentLength) {
        onProgress((int) (bytesWritten * 100 / contentLength));
    }

    //上传成功的回调
    public abstract void onSuccess(T t);

    //上传失败回调
    public abstract void onFailed(Throwable e);

    //上传进度回调
    public abstract void onProgress(int progress);


}
