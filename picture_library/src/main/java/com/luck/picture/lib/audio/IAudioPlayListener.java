package com.luck.picture.lib.audio;

import android.net.Uri;

public interface IAudioPlayListener<T> {
    void onStart(Uri var1, T t);

    void onStop(Uri var1, T t);

    void onComplete(Uri var1, T t);
}