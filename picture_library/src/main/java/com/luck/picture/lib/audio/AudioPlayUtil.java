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
    private int recyclerviewPosition = -1;// 语音播放的位置
    private String file;
    private AnimationDrawable ani;
    private Context context;
    private ProgressBar mProgressBar;
    private boolean play, isPause;
    private static Uri uri;
    private static IAudioPlayProgressListener mIAudioPlayListener;
    private static Object currentMsg;
    private static boolean isCompleted = false;

    private AudioPlayUtil(Context context, String file, AnimationDrawable ani, ProgressBar progressBar,
                          int recPosition, IAudioPlayProgressListener listener) {
        this.context = context;
        this.file = file;
        this.ani = ani;
        this.mProgressBar = progressBar;
        this.mIAudioPlayListener = listener;
        this.recyclerviewPosition = recPosition;
    }

    private AudioPlayUtil(Context context, String file, Object ob, IAudioPlayProgressListener listener) {
        this.context = context;
        this.file = file;
        this.currentMsg = ob;
        this.mIAudioPlayListener = listener;
    }

    //播放语音
    public static void startAudioPlay(Context context, String audioUrl, ImageView imageView,
                                      ProgressBar progressBar, int recPosition) {
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
            playUtil = new AudioPlayUtil(context, audioUrl,
                    (AnimationDrawable) imageView.getBackground(), progressBar, recPosition, null);
        }
        playUtil.actAudio();
    }

    public static void startAudioPlay(Context context, String audioUrl, ProgressBar progressBar, ImageView imageView,
                                      IAudioPlayProgressListener iAudioPlayListener) {
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
                        progressBar, -1, iAudioPlayListener);
            } else {
                playUtil = new AudioPlayUtil(context, audioUrl, null, iAudioPlayListener);
            }
        }
        playUtil.actAudio();
    }

    //停止语音播放
    public static void stopAudioPlay() {
        if (!isCompleted) {
            if (playUtil != null) {
                playUtil.clearProgressBar();
                if (mIAudioPlayListener != null && uri != null) {
                    mIAudioPlayListener.onStop(uri, currentMsg);
                }
                playUtil.release();
                playUtil = null;
            }
        }
    }

    public static void completeAudioPlay() {
        if (playUtil != null) {
            isCompleted = true;
            playUtil.clearProgressBar();
            if (mIAudioPlayListener != null) {
                mIAudioPlayListener.onComplete(uri, currentMsg);
            }
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
        isCompleted = false;
        if (file.startsWith("http")) {//网络
            uri = Uri.parse(file);
        } else {//本地文件
            uri = Uri.fromFile(new File(file));
        }
        AudioPlayManager2.getInstance().startPlay(context, uri, position, new IAudioPlayListener() {
            @Override
            public void onStart(Uri var1, Object msg) {
                if (ani != null) {
                    ani.start();
                }
                setSeekBar();
//                if (mProgressBar != null) {
//                    setSeekBar();
//                }
                if (mIAudioPlayListener != null) {
                    mIAudioPlayListener.onStart(var1, currentMsg);
                }
            }

            @Override
            public void onStop(Uri var1, Object msg) {
                if (ani != null) {
                    ani.stop();
                    ani.selectDrawable(0);
                }
                if (!isCompleted && mIAudioPlayListener != null) {
                    mIAudioPlayListener.onStop(var1, currentMsg);
                }
            }

            @Override
            public void onComplete(Uri var1, Object msg) {
                if (ani != null) {
                    ani.stop();
                    ani.selectDrawable(0);
                }
                if (uri.equals(var1)) {
                    position = 0;
                    play = false;
                }
                isCompleted = false;
                if (mIAudioPlayListener != null) {
                    mIAudioPlayListener.onComplete(var1, currentMsg);
                }
            }
        });
    }

    public void pause() {
        position = AudioPlayManager2.getInstance().pausePlay();
        play = false;
        isPause = true;
    }

    public static int getCurrentPosition() {
        return AudioPlayManager2.getInstance().getCurrentPosition();
    }

    public static int getDuration() {
        return AudioPlayManager2.getInstance().getDuration();
    }

    public void stop() {
        AudioPlayManager2.getInstance().stopPlay();
    }

    public static boolean isPlay() {
        if (playUtil != null) {
            return playUtil.play;
        } else {
            return false;
        }
    }

    public static int getRecyclerviewPosition() {
        if (playUtil != null) {
            return playUtil.recyclerviewPosition;
        } else {
            return -1;
        }
    }

    public void setPlayStatus(boolean isPlay) {
        if (playUtil != null) {
            play = isPlay;
        }
    }

    public void seekTo(int postion) {
        if (AudioPlayManager2.getInstance().getMediaplayer() != null) {
            position = postion;
            AudioPlayManager2.getInstance().getMediaplayer().seekTo(postion);
        }
    }

    public void release() {
        AudioPlayManager2.getInstance().stopPlay();
        file = null;
        context = null;
        ani = null;
        position = 0;
        play = false;
        uri = null;
        currentMsg = null;
        mIAudioPlayListener = null;
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
                            if (mIAudioPlayListener != null) {
                                mIAudioPlayListener.onProgress(bd.setScale(1, BigDecimal.ROUND_HALF_UP).intValue(), currentMsg);
                            }
                            if (mProgressBar != null) {
                                mProgressBar.setProgress(bd.setScale(1, BigDecimal.ROUND_HALF_UP).intValue());
                            } else {
//                                play = false;
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

    public static void startAudioPlay(Context context, String audioUrl, Object ob, IAudioPlayProgressListener iAudioPlayListener) {
        if (TextUtils.isEmpty(audioUrl)) {
            return;
        }
        if (playUtil != null && !playUtil.isSame(audioUrl)) {
            //不是同一个语音 停止上一个
            stopAudioPlay();
        }

        if (playUtil == null) {
            playUtil = new AudioPlayUtil(context, audioUrl, ob, iAudioPlayListener);
        }
        playUtil.actAudio();
    }
}
