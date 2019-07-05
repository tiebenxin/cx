package net.cb.cb.library.audio;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;

import java.io.File;

import jaygoo.library.converter.Mp3Converter;

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
        audioPopupWindow.setTimeout(counter);
    }

    @Override
    public void setRecordingTipView() {
        audioPopupWindow.startAudio();
    }

    @Override
    public void setAudioShortTipView() {
        audioPopupWindow.setAudioShort();
    }

    @Override
    public void setCancelTipView() {
        audioPopupWindow.setCancel();
    }

    @Override
    public void destroyTipView() {
        audioPopupWindow.destroy();
    }

    @Override
    public void onStartRecord() {

    }

    @Override
    public void onFinish(final Uri audioPath, final int duration) {
        if (audioPath != null) {
//            Mp3Converter.init(11025,1,0,11025,22,7);
//   //         Mp3Converter.init(  44100,1,0,44100,96,7);
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    //
//                    Mp3Converter.convertMp3(audioPath.getPath(),
//                            Environment.getExternalStorageDirectory()+File.separator+"LQR_AUDIO"+File.separator+"test.mp3");
//                }
//            }).start();

            new UpFileAction().upFile(context, new UpFileUtil.OssUpCallback() {
                @Override
                public void success(String url) {
                    if (callback != null) {
                        callback.getUrl(url, duration);
                    }
                }

                @Override
                public void fail() {
                    ToastUtil.show(context, "发送失败!");
                }

                @Override
                public void inProgress(long progress, long zong) {

                }
            }, audioPath.getPath());
        }

    }

    @Override
    public void onAudioDBChanged(int db) {
        audioPopupWindow.audioDBChanged(db);
    }


    public interface UrlCallback {
        void getUrl(String url, int duration);
    }


}
