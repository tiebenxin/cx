package com.zhaoss.weixinrecorded.activity;

import android.content.Intent;
import android.hardware.SensorManager;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gl.CameraGLView;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.listener.IRecordListener;
import com.muxer.MediaAudioEncoder;
import com.muxer.MediaEncoder;
import com.muxer.MediaMuxerWrapper;
import com.muxer.MediaVideoEncoder;
import com.widgt.CameraCallBack;
import com.widgt.RecordButtonView;
import com.zhaoss.weixinrecorded.CanStampEventWX;
import com.zhaoss.weixinrecorded.R;
import com.zhaoss.weixinrecorded.util.ViewUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

/**
 * @version V1.0
 * @createAuthor yangqing
 * @createDate 2019-10-16
 * @updateAuthor（Geoff）
 * @updateDate 2019-11-01
 * @description 小视频、拍照
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class CameraActivity extends BaseActivity implements CameraCallBack {

    private final String TAG = getClass().getSimpleName();
    public static final String INTENT_PATH = "intent_path";
    public static final String INTENT_VIDEO_WIDTH = "intent_width";
    public static final String INTENT_PATH_HEIGHT = "intent_height";
    public static final String INTENT_PATH_TIME = "intent_time";
    public static final String INTENT_DATA_TYPE = "result_data_type";
    public static final int RESULT_TYPE_VIDEO = 1;
    public static final int RESULT_TYPE_PHOTO = 2;
    public static final int REQUEST_CODE_KEY = 100;
    public static final int REQUEST_CODE_PREVIEW = 200;
    private final static int VIDEO_WIDTH = 1920;
    private final static int VIDEO_HEIGHT = 1080;
    private final static int MAX_VIDEO_LENGTH = 18 * 1000;


    private CameraGLView mCameraView;
    private RecordButtonView recordView;
    private RelativeLayout viewSwitchCamera, viewFlash;
    private String mp4FilePath;

    private InitVideoAttribute initVideoAttribute;
    private String photoPath;
    private MediaMuxerWrapper mMuxer;
    private OrientationEventListener orientationEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        mp4FilePath = LanSongFileUtil.DEFAULT_DIR + System.currentTimeMillis() + ".mp4";
        initUI();
        initListener();
        EventBus.getDefault().post(new CanStampEventWX(false));
    }

    private void initUI() {
        mCameraView = findViewById(R.id.cameraView);
        mCameraView.setVideoSize(VIDEO_WIDTH, VIDEO_HEIGHT);
        recordView = findViewById(R.id.record_view);
        viewSwitchCamera = findViewById(R.id.layout_camera_mode);
        viewFlash = findViewById(R.id.layout_flash_video);
    }

    @Override
    protected void onResume() {
        mCameraView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mCameraView.onPause();
        stopRecording();
        recordView.resetUI(true);
        super.onPause();
    }

    private void initListener() {
        recordView.setListener(new IRecordListener() {
            @Override
            public void takePictures() {
                if (mCameraView != null) {
                    photoPath = LanSongFileUtil.DEFAULT_DIR + System.currentTimeMillis() + ".jpeg";
                    mCameraView.takePhone(photoPath, CameraActivity.this);
                }
            }

            @Override
            public void recordShort(long time) {
                recordView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraActivity.this, "录制时间不能少于1s", Toast.LENGTH_SHORT).show();
                    }
                }, 100);
            }

            @Override
            public void recordStart() {
                startRecording();
            }

            @Override
            public void recordEnd(long time) {
                stopRecording();
//                Intent intentPre = new Intent(mContext, VideoPreviewActivity.class);
//                intentPre.putExtra(INTENT_PATH, mp4FilePath);
//                startActivityForResult(intentPre, REQUEST_CODE_PREVIEW);
            }

            @Override
            public void recordZoom(float zoom) {

            }

            @Override
            public void recordError() {
                finish();
            }

            @Override
            public void onReturn() {
                finish();
            }

            @Override
            public void onCancel() {
                recordView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recordView.resetUI(true);
                    }
                }, 100);
            }

            @Override
            public void onSure() {
                setResult();
            }
        });


        viewFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                if (mCameraView != null) {
                    mCameraView.flash();
                }
            }
        });

        viewSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                if (mCameraView != null) {
                    mCameraView.switchCamera();
                }
            }
        });

        orientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
//                System.out.println(TAG + "--onOrientationChanged--" + orientation);

            }
        };
        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable();
        } else {
            orientationEventListener.disable();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new CanStampEventWX(true));
        if (orientationEventListener != null) {
            orientationEventListener.disable();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_KEY) {
                Intent intent = new Intent();
                intent.putExtra(INTENT_PATH, data.getStringExtra(INTENT_PATH));
                intent.putExtra(INTENT_VIDEO_WIDTH, data.getIntExtra(INTENT_VIDEO_WIDTH, 720));
                intent.putExtra(INTENT_PATH_HEIGHT, data.getIntExtra(INTENT_PATH_HEIGHT, 1080));
                intent.putExtra(INTENT_PATH_TIME, data.getIntExtra(INTENT_PATH_TIME, 10));
                intent.putExtra(INTENT_DATA_TYPE, RESULT_TYPE_VIDEO);
                setResult(RESULT_OK, intent);
                finish();
            } else if (requestCode == 90) {
                boolean result = data.getBooleanExtra("showResult", false);
                if (result) {
                    Intent intent = new Intent();
                    intent.putExtra(INTENT_PATH, data.getStringExtra("showPath"));
                    intent.putExtra(INTENT_DATA_TYPE, RESULT_TYPE_PHOTO);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                }
            } else if (requestCode == REQUEST_CODE_PREVIEW) {
                Intent intentMas = new Intent();
                intentMas.putExtra(CameraActivity.INTENT_PATH, data.getStringExtra(INTENT_PATH));
                intentMas.putExtra(INTENT_VIDEO_WIDTH, mCameraView.getVideoWidth());
                intentMas.putExtra(INTENT_PATH_HEIGHT, mCameraView.getVideoHeight());
                intentMas.putExtra(INTENT_PATH_TIME, data.getIntExtra(INTENT_PATH_TIME, 0));
                intentMas.putExtra(INTENT_DATA_TYPE, RESULT_TYPE_VIDEO);
                setResult(RESULT_OK, intentMas);
                finish();
            }
        } else {
        }
    }

    private void setResult() {
        if (TextUtils.isEmpty(mp4FilePath)) {
            return;
        }
//        int rotate = mCameraView.getRotate();
        int width = mCameraView.getVideoWidth();
        int height = mCameraView.getVideoHeight();
//        int width,height;
//        if (rotate == 90) {
//            width = Math.min(w, h);
//            height = Math.max(w, h);
//        } else {
//            width = Math.max(w, h);
//            height = Math.min(w, h);
//        }

        if (initVideoAttribute == null) {
            initVideoAttribute = new InitVideoAttribute(mp4FilePath).invoke();
        }
        long duration = initVideoAttribute.getDuration();
//        int width = (int) initVideoAttribute.getWidth();
//        int height = (int) initVideoAttribute.getHeight();
        if (duration < 0) {
            return;
        }
        Intent intentMas = new Intent();
        intentMas.putExtra(CameraActivity.INTENT_PATH, mp4FilePath);
        intentMas.putExtra(INTENT_VIDEO_WIDTH, width);
        intentMas.putExtra(INTENT_PATH_HEIGHT, height);
        intentMas.putExtra(INTENT_PATH_TIME, duration);
        intentMas.putExtra(INTENT_DATA_TYPE, RESULT_TYPE_VIDEO);
        setResult(RESULT_OK, intentMas);
        finish();
    }


    /**
     * start resorcing
     * This is a sample project and call this on UI thread to avoid being complicated
     * but basically this should be called on private thread because prepareing
     * of encoder is heavy work
     */
    private void startRecording() {
        try {
            // if you record audio only, ".m4a" is also OK.
            mMuxer = new MediaMuxerWrapper(mp4FilePath);
            if (true) {
                // for video capturing
                new MediaVideoEncoder(mMuxer, mMediaEncoderListener, mCameraView.getVideoWidth(), mCameraView.getVideoHeight());
            }
            if (true) {
                // for audio capturing
                new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
            }
            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            Log.e(TAG, "startCapture:", e);
        }
    }

    /**
     * request stop recording
     */
    private void stopRecording() {
        if (mMuxer != null) {
            mMuxer.stopRecording();
            mMuxer = null;
            // you should not wait here
        }
    }

    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder)
                mCameraView.setVideoEncoder((MediaVideoEncoder) encoder);
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder)
                mCameraView.setVideoEncoder(null);
        }
    };

    @Override
    public void takePhoneSuccess(String imagePath) {
        if (!TextUtils.isEmpty(photoPath)) {
            Intent intent = new Intent(CameraActivity.this, ImageShowActivity.class);
            intent.putExtra("imgpath", imagePath);
            intent.putExtra("from", 1);
            startActivityForResult(intent, 90);
        }
    }


    private static class InitVideoAttribute {
        private long duration;
        private long width;
        private long height;
        private int rotate;
        private final String videoPath;

        public InitVideoAttribute(String video) {
            videoPath = video;
        }

        public InitVideoAttribute invoke() {
            try {
                android.media.MediaMetadataRetriever retriever = new android.media.MediaMetadataRetriever();
                retriever.setDataSource(videoPath);
                String time = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
                if (!TextUtils.isEmpty(time)) {
                    duration = Long.parseLong(time);
                }
                rotate = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
                long w = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                long h = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                if (rotate == 90) {
                    width = Math.min(w, h);
                    height = Math.max(w, h);
                } else {
                    width = Math.max(w, h);
                    height = Math.min(w, h);
                }
            } catch (Exception e) {
            }


            return this;
        }

        public long getDuration() {
            return duration;
        }

        public long getWidth() {
            return width;
        }

        public long getHeight() {
            return height;
        }

        public int getRotate() {
            return rotate;
        }
    }

}

