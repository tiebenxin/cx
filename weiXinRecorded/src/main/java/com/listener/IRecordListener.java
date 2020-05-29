package com.listener;

/**
 * @author Liszt
 * @date 2020/5/29
 * Description
 */
public interface IRecordListener {
    void takePictures();

    void recordShort(long time);

    void recordStart();

    void recordEnd(long time);

    void recordZoom(float zoom);

    void recordError();

    void onReturn();

    void onCancel();

    void onSure();
}
