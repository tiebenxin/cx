package com.luck.picture.lib.audio;

public interface IAudioPlayProgressListener<T> extends IAudioPlayListener {
    void onProgress(int progress,T t);
}