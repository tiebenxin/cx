package com.muxer;

import android.util.Log;

import java.io.IOException;

public class RecordHelper {
    public final String TAG = getClass().getSimpleName() + "--视频录制";
    private static final boolean DEBUG = true;    // TODO set false on release

    /**
     * muxer for audio/video recording
     */
    private MediaMuxerWrapper mMuxer;


    public void startRecording(String file, int width, int height, MediaEncoder.MediaEncoderListener listener) {
        if (DEBUG)
            Log.v(TAG, "startRecording:file=" + file + "--width=" + width + "--height=" + height);

        try {
            mMuxer = new MediaMuxerWrapper(file);

            if (true) {
                // for video capturing
                new MediaVideoEncoder(mMuxer, listener, width, height);
            }
            if (true) {
                // for audio capturing
                new MediaAudioEncoder(mMuxer, listener);
            }
            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * request stop recording
     */
    public void stopRecording() {
        if (DEBUG) Log.v(TAG, "stopRecording:mMuxer=" + mMuxer);
        if (mMuxer != null) {
            mMuxer.stopRecording();
            mMuxer = null;
            // you should not wait here
        }
    }


}