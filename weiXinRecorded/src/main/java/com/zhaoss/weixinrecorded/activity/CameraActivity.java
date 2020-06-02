package com.zhaoss.weixinrecorded.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lansosdk.videoeditor.LanSoEditor;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.listener.IRecordListener;
import com.widgt.RecordButtonView;
import com.zhaoss.weixinrecorded.CanStampEventWX;
import com.zhaoss.weixinrecorded.R;
import com.zhaoss.weixinrecorded.util.CameraHelp;
import com.zhaoss.weixinrecorded.util.CameraUtil;
import com.zhaoss.weixinrecorded.util.MyVideoEditor;
import com.zhaoss.weixinrecorded.util.RxJavaUtil;
import com.zhaoss.weixinrecorded.util.ViewUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * @version V1.0
 * @createAuthor yangqing
 * @createDate 2019-10-16
 * @updateAuthor（Geoff）
 * @updateDate 2019-11-01
 * @description 小视频、拍照
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class CameraActivity extends BaseActivity implements TextureView.SurfaceTextureListener {

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


    private TextureView surfaceView;
    private RecordButtonView recordView;
    private RelativeLayout viewSwitchCamera, viewFlash;
    private CameraHelp mCameraHelp = new CameraHelp();
    private String mp4FilePath;
    private MediaRecorder mMediaRecorder;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private MyVideoEditor mVideoEditor = new MyVideoEditor();


    //录制出错的回调
    private MediaRecorder.OnErrorListener onErrorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            try {
                if (mMediaRecorder != null) {
                    mMediaRecorder.reset();
                }
            } catch (Exception e) {
                Toast.makeText(CameraActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };
    private Camera mCamera;
    private int mRotationDegree;
    @RecorderStatus
    private int mStatus = RecorderStatus.RELEASED;//录制状态
    private int mFps;
    private SurfaceTexture mSurfaceTexture;

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            if (takePhone) {
                takePhone = false;
                shotPhoto(bytes);
            }
        }
    };
    private boolean takePhone;
    private InitVideoAttribute initVideoAttribute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        LanSoEditor.initSDK(this, null);

        mp4FilePath = LanSongFileUtil.DEFAULT_DIR + System.currentTimeMillis() + ".mp4";
        initUI();
        initListener();
        EventBus.getDefault().post(new CanStampEventWX(false));
    }

    private void initUI() {
        surfaceView = findViewById(R.id.surfaceView);
        recordView = findViewById(R.id.record_view);
        viewSwitchCamera = findViewById(R.id.layout_camera_mode);
        viewFlash = findViewById(R.id.layout_flash_video);
        surfaceView.setSurfaceTextureListener(this);
        surfaceView.post(new Runnable() {
            @Override
            public void run() {
                int width = surfaceView.getWidth();
                int height = surfaceView.getHeight();
                float viewRatio = width * 1f / height;
                float videoRatio = 9f / 16f;
                ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
                if (viewRatio > videoRatio) {
                    layoutParams.width = width;
                    layoutParams.height = (int) (width / viewRatio);
                } else {
                    layoutParams.width = (int) (height * viewRatio);
                    layoutParams.height = height;
                }
                surfaceView.setLayoutParams(layoutParams);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void shotPhoto(final byte[] nv21) {
        RxJavaUtil.run(new RxJavaUtil.OnRxAndroidListener<String>() {
            @Override
            public String doInBackground() throws Throwable {

                boolean isFrontCamera = mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
                int rotation;
                if (isFrontCamera) {
                    rotation = 270;
                } else {
                    rotation = 90;
                }

                byte[] yuvI420 = new byte[nv21.length];
                byte[] tempYuvI420 = new byte[nv21.length];
                int videoWidth;
                int videoHeight;
                if (rotation == 90) {
                    videoWidth = Math.min(VIDEO_WIDTH, VIDEO_HEIGHT);
                    videoHeight = Math.max(VIDEO_WIDTH, VIDEO_HEIGHT);
                } else {
                    videoWidth = Math.max(VIDEO_WIDTH, VIDEO_HEIGHT);
                    videoHeight = Math.min(VIDEO_WIDTH, VIDEO_HEIGHT);
                }
                Bitmap bitmap = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);
                String photoPath = LanSongFileUtil.DEFAULT_DIR + System.currentTimeMillis() + ".jpeg";
                FileOutputStream fos = new FileOutputStream(photoPath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();

                return photoPath;
            }

            @Override
            public void onFinish(String result) {
                closeProgressDialog();
                Intent intent = new Intent(CameraActivity.this, ImageShowActivity.class);
                intent.putExtra("imgpath", result);
                intent.putExtra("from", 1);
                startActivityForResult(intent, 90);
            }

            @Override
            public void onError(Throwable e) {
                closeProgressDialog();
                Toast.makeText(getApplicationContext(), "图片截取失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void initListener() {
        recordView.setListener(new IRecordListener() {
            @Override
            public void takePictures() {
                takePhone = true;
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
                startRecord();
            }

            @Override
            public void recordEnd(long time) {
                System.out.println("视频录制--duration=" + time);
                stopRecord();
            }

            @Override
            public void recordZoom(float zoom) {

            }

            @Override
            public void recordError() {

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
                initCamera(mCameraId);
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
                mCameraHelp.changeFlash(mCamera);
            }
        });

        viewSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                switchCamera();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraHelp != null) {
            mCameraHelp.release();
        }
        EventBus.getDefault().post(new CanStampEventWX(true));
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
                    releaseCamera();
                }
            } else if (requestCode == REQUEST_CODE_PREVIEW) {
                Intent intentMas = new Intent();
                intentMas.putExtra(CameraActivity.INTENT_PATH, data.getStringExtra(INTENT_PATH));
                intentMas.putExtra(INTENT_VIDEO_WIDTH, mCameraHelp.getHeight());
                intentMas.putExtra(INTENT_PATH_HEIGHT, mCameraHelp.getWidth());
                intentMas.putExtra(INTENT_PATH_TIME, data.getIntExtra(INTENT_PATH_TIME, 0));
                intentMas.putExtra(INTENT_DATA_TYPE, RESULT_TYPE_VIDEO);
                setResult(RESULT_OK, intentMas);
                finish();
            }
        } else {
            releaseCamera();
        }
    }

    private void setResult() {
        if (TextUtils.isEmpty(mp4FilePath)) {
            return;
        }
        if (initVideoAttribute == null) {
            initVideoAttribute = new InitVideoAttribute(mp4FilePath).invoke();
        }
        long duration = initVideoAttribute.getDuration();
        int width = (int) initVideoAttribute.getWidth();
        int height = (int) initVideoAttribute.getHeight();
        if (duration <= 0) {
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

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        mSurfaceTexture = surfaceTexture;
        initCamera(mCameraId);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }


    /**
     * 初始化相机
     */
    private void initCamera(int cameraId) {
        System.out.println(TAG + "--initCamera");
        if (mSurfaceTexture == null) return;
        if (mCamera != null) {
            releaseCamera();
        }

        mCamera = Camera.open(cameraId);
        if (mCamera == null) {
            Toast.makeText(this, "没有可用相机", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] bytes, Camera camera) {
                    mPreviewCallback.onPreviewFrame(bytes, camera);
                }
            });
            mRotationDegree = CameraUtil.getCameraDisplayOrientation(this, cameraId);
            mCamera.setDisplayOrientation(mRotationDegree);
            setCameraParameter(mCamera);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放相机
     */
    private void releaseCamera() {
        System.out.println(TAG + "--releaseCamera");
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 设置相机的参数
     *
     * @param camera
     */
    private void setCameraParameter(Camera camera) {
        System.out.println(TAG + "--setCameraParameter");
        if (camera == null) return;
        Camera.Parameters parameters = camera.getParameters();
        //获取相机支持的>=20fps的帧率，用于设置给MediaRecorder
        //因为获取的数值是*1000的，所以要除以1000
        List<int[]> previewFpsRange = parameters.getSupportedPreviewFpsRange();
        for (int[] ints : previewFpsRange) {
            if (ints[0] >= 20000) {
                mFps = ints[0] / 1000;
                break;
            }
        }
        //设置聚焦模式
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }


        //设置预览尺寸,因为预览的尺寸和最终是录制视频的尺寸无关，所以我们选取最大的数值
        //通常最大的是手机的分辨率，这样可以让预览画面尽可能清晰并且尺寸不变形，前提是TextureView的尺寸是全屏或者接近全屏
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        parameters.setPreviewSize(supportedPreviewSizes.get(0).width, supportedPreviewSizes.get(0).height);
        //缩短Recording启动时间
        parameters.setRecordingHint(true);
//        parameters.setRotation(mRotationDegree);
        //是否支持影像稳定能力，支持则开启
        if (parameters.isVideoStabilizationSupported())
            parameters.setVideoStabilization(true);
        camera.setParameters(parameters);
    }

    /**
     * 初始化MediaRecorder
     */
    private void initMediaRecorder() {
        System.out.println(TAG + "--initMediaRecorder");
        //如果是处于release状态，那么只有重新new一个进入initial状态
        //否则其他状态都可以通过reset()方法回到initial状态
        if (mStatus == RecorderStatus.RELEASED) {
            mMediaRecorder = new MediaRecorder();
        } else {
            mMediaRecorder.reset();
        }
        //设置选择角度，顺时针方向，因为默认是逆向90度的，这样图像就是正常显示了,这里设置的是观看保存后的视频的角度
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setOrientationHint(mRotationDegree);
//        if (isCameraFrontFacing()) {
//            mMediaRecorder.setOrientationHint(270);
//        } else {
//            mMediaRecorder.setOrientationHint(90);
//        }
        mMediaRecorder.setOnErrorListener(onErrorListener);
        //采集声音来源、mic是麦克风
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //采集图像来源、
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //设置编码参数
//        setProfile();
        setConfig();

        mMediaRecorder.setPreviewDisplay(new Surface(mSurfaceTexture));
        //设置输出的文件路径
        mMediaRecorder.setOutputFile(mp4FilePath);
    }


    /**
     * 释放MediaRecorder
     */
    private void releaseMediaRecorder() {
        System.out.println(TAG + "--releaseMediaRecorder");
        if (mMediaRecorder != null) {
            if (mStatus == RecorderStatus.RELEASED) return;
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mStatus = RecorderStatus.RELEASED;
            //停止计时
//            chronometer.stop();
        }
    }

    /**
     * 自定义MediaRecorder的录制参数
     */
    private void setConfig() {
        System.out.println(TAG + "--MediaRecorder--setConfig");
        //设置封装格式 默认是MP4
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        //音频编码
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //图像编码
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //声道
        mMediaRecorder.setAudioChannels(1);
        //设置最大录像时间 单位：毫秒
        mMediaRecorder.setMaxDuration(MAX_VIDEO_LENGTH);
        //设置最大录制的大小50M 单位，字节
        mMediaRecorder.setMaxFileSize(50 * 1024 * 1024);
        //再用44.1Hz采样率
        mMediaRecorder.setAudioEncodingBitRate(22050);
        //设置帧率，该帧率必须是硬件支持的，可以通过Camera.CameraParameter.getSupportedPreviewFpsRange()方法获取相机支持的帧率
        mMediaRecorder.setVideoFrameRate(mFps);
        //设置码率
        mMediaRecorder.setVideoEncodingBitRate(500 * 1024 * 8);
        //设置视频尺寸，通常搭配码率一起使用，可调整视频清晰度
        mMediaRecorder.setVideoSize(VIDEO_WIDTH, VIDEO_HEIGHT);
        mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    System.out.println(TAG + "--视频录制超时");
                    stopRecord();
                    recordView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recordView.recordEnd();
                        }
                    }, 100);
                }
            }
        });
    }


    @IntDef({RecorderStatus.INITIAL, RecorderStatus.INITIALIZED, RecorderStatus.RECORDING, RecorderStatus.RELEASED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RecorderStatus {
        int INITIAL = 0; // 默认
        int INITIALIZED = 1; // 好友搜索界面
        int RECORDING = 2; // 正在录制
        int RELEASED = 3; // 释放
    }


    /**
     * 开始录制
     */
    private void startRecord() {
        System.out.println(TAG + "--startRecord");
        if (mCamera == null) {
            initCamera(mCameraId);
        }
        mCamera.unlock();
        initMediaRecorder();
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mStatus = RecorderStatus.RECORDING;
    }

    /**
     * 停止录制
     */
    private void stopRecord() {
        releaseMediaRecorder();
        releaseCamera();
        try {
            if (isCameraFrontFacing()) {
                initVideoAttribute = new InitVideoAttribute(mp4FilePath).invoke();
                System.out.println(TAG + "--rotate=" + initVideoAttribute.getRotate());
//                String tempPath = LanSongFileUtil.DEFAULT_DIR + System.currentTimeMillis() + ".mp4";
//                mVideoEditor.rotateMp4(mp4FilePath, tempPath);
//                delete(mp4FilePath);
//                mp4FilePath = tempPath;
//                initVideoAttribute = new InitVideoAttribute(tempPath).invoke();
//                System.out.println(TAG + "--rotate=" + initVideoAttribute.getRotate());
            }
        } catch (Exception e) {


        }
    }

    public void switchCamera() {
        System.out.println(TAG + "--switchCamera");
        if (Camera.getNumberOfCameras() < 2) {
            Toast.makeText(CameraActivity.this, "无前置摄像头", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            initCamera(mCameraId);
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            initCamera(mCameraId);
        }
        initMediaRecorder();
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

    private boolean isCameraFrontFacing() {
        return mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    private void delete(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}

