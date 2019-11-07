package com.example.nim_lib.controll;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.example.nim_lib.R;

import java.io.IOException;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2019-11-07
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class PlayerManager {
    /**
     * 外放模式
     */
    public static final int MODE_SPEAKER = 0;
    /**
     * 听筒模式
     */
    public static final int MODE_EARPIECE = 2;

    private static PlayerManager playerManager;

    private MediaPlayer mediaPlayer;

    private AudioManager audioManager;

    private Context context;

    public static PlayerManager getManager() {
        if (playerManager == null) {
            synchronized (PlayerManager.class) {
                playerManager = new PlayerManager();
            }
        }
        return playerManager;
    }

    public void init(Context context) {
        this.context = context;
        initBeepSound();
    }

    private static final float BEEP_VOLUME = 0.10f;

    private void initBeepSound() {
        // The volume on STREAM_SYSTEM is not adjustable, and users found it
        // too loud,
        // so we now play on the music stream.
        ((Activity) context).setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnCompletionListener(beepListener);

        AssetFileDescriptor file = context.getResources().openRawResourceFd(R.raw.audio_video_hint);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            mediaPlayer.prepare();
        } catch (IOException e) {
            mediaPlayer = null;
        }
    }

    public void play(int mode) {
        if (mediaPlayer != null) {
            if (MODE_EARPIECE == mode) {
                audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);// 把模式调成听筒放音模式
                audioManager.setSpeakerphoneOn(false);
            } else {
                audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(true);// 把模式调成外放模式
            }
            mediaPlayer.start();
        }
    }

    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
}
