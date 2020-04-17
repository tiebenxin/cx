package com.yanlong.im.utils.audio;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;


import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;


public class IAudioRecord implements IAudioRecordListener {
    private Context context;
    private View view;
    private AudioPopupWindow audioPopupWindow;
    private UrlCallback callback;

    /**
     * @param view        参照物view
     * @param urlCallback 返回上传音频url回调
     */
    public IAudioRecord(Context context, View view, UrlCallback urlCallback) {
        this.context = context;
        this.view = view;
        this.callback = urlCallback;
    }


    @Override
    public void initTipView() {
        audioPopupWindow = new AudioPopupWindow(context);
        audioPopupWindow.showPopup(view);
    }

    @Override
    public void setTimeoutTipView(int counter) {
        if (audioPopupWindow != null) {
            audioPopupWindow.setTimeout(counter);
        }
    }

    @Override
    public void setRecordingTipView() {
        if (audioPopupWindow != null) {
            audioPopupWindow.startAudio();
        }
    }

    @Override
    public void setAudioShortTipView() {
        if (audioPopupWindow != null) {
            audioPopupWindow.setAudioShort();
        }
    }

    @Override
    public void setCancelTipView() {
        if (audioPopupWindow != null) {
            audioPopupWindow.setCancel();
        }
    }

    @Override
    public void destroyTipView() {
        if (audioPopupWindow != null) {
            audioPopupWindow.destroy();
        }
    }

    @Override
    public void onStartRecord() {

    }

    @Override
    public void onFinish(final Uri audioPath, final int duration) {
        if (audioPath != null) {
            callback.completeRecord(audioPath.getPath(), duration);
        }

    }

    @Override
    public void onAudioDBChanged(int db) {
        if (audioPopupWindow != null) {
            audioPopupWindow.audioDBChanged(db);
        }
    }

    @Override
    public void cancelRecord() {
        if (audioPopupWindow != null) {
            audioPopupWindow.destroy();
        }
    }


    public interface UrlCallback {
//        void getUrl(String url, int duration);

        //录制完成
        void completeRecord(String file, int duration);

    }


}
