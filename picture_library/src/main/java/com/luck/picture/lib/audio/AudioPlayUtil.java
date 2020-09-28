package com.luck.picture.lib.audio;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import java.io.File;

/**
 * 包路径：com.hanming.education.audio
 * 类描述：封装播放音频支持暂停 
 * 创建时间：2020/01/13  20:03
 * 修改人：
 * 修改时间：2020/01/13  20:03
 * 修改备注：
 */
public class AudioPlayUtil {
    private static AudioPlayUtil playUtil;//必须 private 防止乱用

    private int position;
    private String file;
    private AnimationDrawable ani;
    private static Context context;
    private boolean play;
    private Uri uri;
    private static IAudioPlayListener mIAudioPlayListener;

    private AudioPlayUtil(Context context, String file, AnimationDrawable ani) {
        this.context = context;
        this.file = file;
        this.ani = ani;
    }

    private AudioPlayUtil(Context context, String file) {
        this.context = context;
        this.file = file;
    }

    //播放语音
    public static void startAudioPlay(Context context, String audioUrl, ImageView imageView) {
        if (TextUtils.isEmpty(audioUrl)) {
            return;
        }
        if (playUtil != null && !playUtil.isSame(audioUrl)) {
            //不是同一个语音 停止上一个
            playUtil.pause();
            playUtil = null;
        }

        if (playUtil == null) {
            playUtil = new AudioPlayUtil(context, audioUrl, (AnimationDrawable) imageView.getBackground());
        }

        playUtil.actAudio();
    }

    public static void startAudioPlay(Context context, String audioUrl, IAudioPlayListener iAudioPlayListener) {
        if (TextUtils.isEmpty(audioUrl)) {
            return;
        }
        mIAudioPlayListener = iAudioPlayListener;
        if (playUtil != null && !playUtil.isSame(audioUrl)) {
            //不是同一个语音 停止上一个
            playUtil.pause();
            playUtil = null;
        }

        if (playUtil == null) {
            playUtil = new AudioPlayUtil(context, audioUrl);
        }

        playUtil.actAudio();
    }

    //停止语音播放
    public static void stopAudioPlay() {
        if (playUtil != null) {
            playUtil.release();
            playUtil = null;
        }
    }


    //当一个界面有多个语音 item时 判断是否同一个语音
    public boolean isSame(String fileStr) {
        if (TextUtils.isEmpty(file)) {
            return false;
        }
        if ((TextUtils.isEmpty(fileStr))) {
            return false;
        }
        if (file.equals(fileStr)) {
            return true;
        }
        return false;
    }

    public void actAudio() {//播放或者暂停
        if (!play) {
            playAudio();
        } else {
            pause();
        }
    }

    public void playAudio() {
        if (TextUtils.isEmpty(file)) {
            return;
        }
        play = true;
        if (file.startsWith("http")) {//网络
            uri = Uri.parse(file);
        } else {//本地文件
            uri = Uri.fromFile(new File(file));
        }
        AudioPlayManager.getInstance().startPlay(context, uri, position, new IAudioPlayListener() {
            @Override
            public void onStart(Uri var1) {
                if (ani != null) {
                    ani.start();
                }
                if (mIAudioPlayListener != null) {
                    mIAudioPlayListener.onStart(var1);
                }
            }

            @Override
            public void onStop(Uri var1) {
                if (ani != null) {
                    ani.stop();
                    ani.selectDrawable(0);
                }
                if (mIAudioPlayListener != null) {
                    mIAudioPlayListener.onStop(var1);
                }
            }

            @Override
            public void onComplete(Uri var1) {
                if (ani != null) {
                    ani.stop();
                    ani.selectDrawable(0);
                }
                if (uri.equals(var1)) {
                    position = 0;
                    play = false;
                }
                if (mIAudioPlayListener != null) {
                    mIAudioPlayListener.onComplete(var1);
                }
            }
        });
    }

    public void pause() {
        position = AudioPlayManager.getInstance().pausePlay();
        play = false;
    }

    public int getCurrentPosition() {
        return AudioPlayManager.getInstance().getCurrentPosition();
    }

    public void stop() {
        AudioPlayManager.getInstance().stopPlay();
    }

    public boolean isPlay() {
        return play;
    }

    public void seekTo(int postion) {
        if (AudioPlayManager.getInstance().getMediaplayer() != null) {
            position = postion;
            AudioPlayManager.getInstance().getMediaplayer().seekTo(postion);
        }
    }

    public void release() {
        AudioPlayManager.getInstance().stopPlay();
        file = null;
        context = null;
        ani = null;
        position = 0;
        play = false;
    }

}
