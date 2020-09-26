package com.luck.picture.lib.audio;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;

import com.luck.picture.lib.utils.DateUtil;

import java.io.File;

public class AudioRecorderUtil implements AudioManager.OnAudioFocusChangeListener {
    private Context context;
    private AudioManager audioManager;
    private MediaRecorder mediaRecorder;
    private AudioFocusRequest audioFocusRequest;
    private String currentAudioFile;
    private long startTime;
    private long timeInterval;
    private boolean isRecording;

    public AudioRecorderUtil(Context context) {
        this.context = context;
    }

    private String getAudioFile() {
        File dir = new File(context.getExternalFilesDir(null).getAbsolutePath(), AudioRecorder.AUDIO_FILE_NAME);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, DateUtil.getCurrTime(DateUtil.DATE_PATTERN_yyyyMMddHHmmss));
        return file.getAbsolutePath();
    }

    public void startRecord() {
        if (isRecording) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        this.audioManager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAcceptsDelayedFocusGain(true)
                    .setWillPauseWhenDucked(true)
                    .setOnAudioFocusChangeListener(this, new Handler())
                    .build();
            audioManager.requestAudioFocus(audioFocusRequest);
        } else {
            audioManager.requestAudioFocus(this, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN);
        }

        try {
            this.audioManager.setMode(0);
            this.mediaRecorder = new MediaRecorder();

            try {
                int bps = 7950;
                this.mediaRecorder.setAudioSamplingRate(8000);
                this.mediaRecorder.setAudioEncodingBitRate(bps);
            } catch (Resources.NotFoundException var3) {
                var3.printStackTrace();
            }
            currentAudioFile = getAudioFile();
            this.mediaRecorder.setAudioChannels(1);
            this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //this.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);//ios不可用识别
            //this.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            this.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);//ios可以识别
            this.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            this.mediaRecorder.setOutputFile(currentAudioFile);
            startTime = System.currentTimeMillis();
            this.mediaRecorder.prepare();
            this.mediaRecorder.start();
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

    public void stopRecord() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioManager.abandonAudioFocusRequest(audioFocusRequest);
            } else {
                audioManager.abandonAudioFocus(this);
            }
            timeInterval = System.currentTimeMillis() - startTime;
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            stopRecord();
        }
    }

    public long getTimeInterval() {
        return timeInterval / 1000;
    }

    public String getFilePath() {
        return currentAudioFile;
    }

}
