package com.luck.picture.lib.audio;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.math.BigDecimal;

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
    private Context context;
    private ProgressBar mProgressBar;
    private boolean play, isPause;
    private Uri uri;
    private IAudioPlayListener mIAudioPlayListener;

    private AudioPlayUtil(Context context, String file, AnimationDrawable ani, ProgressBar progressBar, IAudioPlayListener listener) {
        this.context = context;
        this.file = file;
        this.ani = ani;
        this.mProgressBar = progressBar;
        this.mIAudioPlayListener = listener;
    }

    private AudioPlayUtil(Context context, String file, IAudioPlayListener listener) {
        this.context = context;
        this.file = file;
        this.mIAudioPlayListener = listener;
    }

    //播放语音
    public static void startAudioPlay(Context context, String audioUrl, ImageView imageView, ProgressBar progressBar) {
        if (TextUtils.isEmpty(audioUrl)) {
            return;
        }
        if (playUtil != null && !playUtil.isSame(audioUrl)) {
            //不是同一个语音 停止上一个
            playUtil.pause();
            playUtil.clearProgressBar();
            playUtil = null;
        }

        if (playUtil == null) {
            playUtil = new AudioPlayUtil(context, audioUrl, (AnimationDrawable) imageView.getBackground(), progressBar, null);
        }
        playUtil.actAudio();
    }

    public static void startAudioPlay(Context context, String audioUrl, ProgressBar progressBar, ImageView imageView,
                                      IAudioPlayListener iAudioPlayListener) {
        if (TextUtils.isEmpty(audioUrl)) {
            return;
        }
        if (playUtil != null && !playUtil.isSame(audioUrl)) {
            //不是同一个语音 停止上一个
            playUtil.pause();
            playUtil.clearProgressBar();
            playUtil = null;
        }

        if (playUtil == null) {
            if (imageView != null) {
                playUtil = new AudioPlayUtil(context, audioUrl, (AnimationDrawable) imageView.getBackground(),
                        progressBar, iAudioPlayListener);
            } else {
                playUtil = new AudioPlayUtil(context, audioUrl, iAudioPlayListener);
            }
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

    public void clearProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setProgress(0);
            mProgressBar = null;
        }
        if (ani != null) {
            ani = null;
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
                if (mProgressBar != null) {
                    setSeekBar();
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
        isPause = true;
    }

    public static int getCurrentPosition() {
        return AudioPlayManager.getInstance().getCurrentPosition();
    }

    public static int getDuration() {
        return AudioPlayManager.getInstance().getDuration();
    }

    public void stop() {
        AudioPlayManager.getInstance().stopPlay();
    }

    public boolean isPlay() {
        return play;
    }

    public void setPlayStatus(boolean isPlay) {
        if (playUtil != null) {
            play = isPlay;
        }
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

    public static int getProgressValue() {
        int i = 0;
        if (getDuration() > 0) {
            double progress = (getCurrentPosition() / Double.parseDouble(getDuration() + "")) * 100;
            BigDecimal bd = new BigDecimal(progress);
            i = bd.setScale(1, BigDecimal.ROUND_HALF_UP).intValue();
        }
        return i;
    }

    private void setSeekBar() {
        new Thread() {
            @Override
            public void run() {
                while (play) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    } finally {
                        if (getDuration() > 0) {
                            double progress = (getCurrentPosition() / Double.parseDouble(getDuration() + "")) * 100;
                            BigDecimal bd = new BigDecimal(progress);
                            if (mProgressBar != null) {
                                mProgressBar.setProgress(bd.setScale(1, BigDecimal.ROUND_HALF_UP).intValue());
                            } else {
                                play = false;
                            }
                        } else {
                            if (!isPause && mProgressBar != null) {
                                mProgressBar.setProgress(0);
                            }
                        }
                    }
                }
            }
        }.start();
    }
}
