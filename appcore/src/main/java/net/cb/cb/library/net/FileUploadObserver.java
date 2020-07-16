package net.cb.cb.library.net;

import io.reactivex.observers.DefaultObserver;

/**
 * Created by LL130386 on 2017/12/22.
 */

public abstract class FileUploadObserver<T> extends DefaultObserver<T> {

    private int currentProgress = 0;

    @Override
    public void onNext(T t) {
        onUpLoadSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        onUpLoadFail(e);
    }

    @Override
    public void onComplete() {
    }

    //监听进度的改变
    public void onProgressChange(long bytesWritten, long contentLength) {
        int progress = (int) (bytesWritten * 100 / contentLength);
        if (progress <= currentProgress || progress > 99) {
            return;
        }
        if (progress == 100) {
            progress -= 1;
        }
        currentProgress = progress;
        onProgress(progress);

    }

    //上传成功的回调
    public abstract void onUpLoadSuccess(T t);

    //上传失败回调
    public abstract void onUpLoadFail(Throwable e);

    //上传进度回调
    public abstract void onProgress(int progress);


}
