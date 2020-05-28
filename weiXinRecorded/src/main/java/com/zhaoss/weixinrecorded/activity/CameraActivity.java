package com.zhaoss.weixinrecorded.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gl.CameraGLView;
import com.lansosdk.videoeditor.LanSoEditor;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;
import com.libyuv.LibyuvUtil;
import com.muxer.MediaEncoder;
import com.muxer.MediaVideoEncoder;
import com.muxer.RecordHelper;
import com.zhaoss.weixinrecorded.CanStampEventWX;
import com.zhaoss.weixinrecorded.R;
import com.zhaoss.weixinrecorded.util.CameraHelp;
import com.zhaoss.weixinrecorded.util.MyVideoEditor;
import com.zhaoss.weixinrecorded.util.RecordUtil;
import com.zhaoss.weixinrecorded.util.RxJavaUtil;
import com.zhaoss.weixinrecorded.util.ViewUtils;
import com.zhaoss.weixinrecorded.view.LineProgressView;
import com.zhaoss.weixinrecorded.view.RecordView;

import org.greenrobot.eventbus.EventBus;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @version V1.0
 * @createAuthor yangqing
 * @createDate 2019-10-16
 * @updateAuthor（Geoff）
 * @updateDate 2019-11-01
 * @description 小视频、拍照
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class CameraActivity extends BaseActivity {

    private final String TAG =getClass().getSimpleName();
    public static final String INTENT_PATH = "intent_path";
    public static final String INTENT_VIDEO_WIDTH = "intent_width";
    public static final String INTENT_PATH_HEIGHT = "intent_height";
    public static final String INTENT_PATH_TIME = "intent_time";
    public static final String INTENT_DATA_TYPE = "result_data_type";

    public static String DEFAULT_DIR = LanSongFileUtil.PATH_BASE + "video/";

    public static final int RESULT_TYPE_VIDEO = 1;
    public static final int RESULT_TYPE_PHOTO = 2;
    public static final int REQUEST_CODE_KEY = 100;
    public static final int REQUEST_CODE_PREVIEW = 200;
    public static final float MAX_VIDEO_TIME = 18f * 1000;  //最大录制时间
    public static final float MIN_VIDEO_TIME = 1f * 1000;  //最小录制时间
    public static final float MIN_VIDEO_TIME_MEIZU = 3f * 1000;  //最小录制时间

    private final int TYPE_EDIT = 2;// 编辑
    private final int TYPE_PREVIEW = 3;// 预览

    private CameraGLView surfaceView;
    private RecordView recordView;
    private ImageView iv_delete;
    private ImageView iv_next;
    private RelativeLayout layout_change_camera, layout_flash_video;
    private LineProgressView lineProgressView;
    private ImageView iv_delete_back;
    private TextView iv_recorded_edit;
    private TextView editorTextView;
    private TextView tv_hint;
    private View view_back;

    private ArrayList<String> mSegmentList = new ArrayList<>();//分段视频地址
    private ArrayList<String> mAacList = new ArrayList<>();//分段音频地址
    private ArrayList<Long> mTimeList = new ArrayList<>();//分段录制时间

    private AtomicBoolean isRecordVideo = new AtomicBoolean(false);//是否在录制视频
    private AtomicBoolean isShotPhoto = new AtomicBoolean(false); //拍照
    private CameraHelp mCameraHelp = new CameraHelp();
    private SurfaceHolder mSurfaceHolder;
    private MyVideoEditor mVideoEditor = new MyVideoEditor();
    private boolean mPhotoFlg = false;// 小于1秒进入拍照判断

    private int mExecuteCount;//总编译次数
    private float mExecuteProgress;//编译进度
    private long mLastClickTime;
    private RecordUtil.OnPreviewFrameListener mOnPreviewFrameListener;
    private RecordHelper recordHelper;
    private String mp4FilePath;
    private long mRecordTime;
    private long mVideoDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        LanSoEditor.initSDK(this, null);
        LanSongFileUtil.setFileDir(DEFAULT_DIR + System.currentTimeMillis() + "/");
        LibyuvUtil.loadLibrary();

        initUI();
        initData();
        initMediaRecorder();

        EventBus.getDefault().post(new CanStampEventWX(false));
    }

    private void initUI() {
        surfaceView = findViewById(R.id.surfaceView);
        recordView = findViewById(R.id.recordView);
        iv_delete = findViewById(R.id.iv_delete);
        iv_next = findViewById(R.id.iv_next);
        iv_recorded_edit = findViewById(R.id.iv_recorded_edit);
        layout_change_camera = findViewById(R.id.layout_camera_mode);
        lineProgressView = findViewById(R.id.lineProgressView);
        tv_hint = findViewById(R.id.tv_hint);
        layout_flash_video = findViewById(R.id.layout_flash_video);
        iv_delete_back = findViewById(R.id.iv_delete_back);
        view_back = findViewById(R.id.layout_back);

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
                surfaceView.setVideoSize(layoutParams.width, layoutParams.height);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPhotoFlg = false;
        surfaceView.onResume();
    }

    @Override
    protected void onPause() {
        if (recordHelper != null) {
            recordHelper.stopRecording();
        }
        surfaceView.onPause();
        super.onPause();
    }

    private void initMediaRecorder() {
        mCameraHelp.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if (isShotPhoto.get()) {
                    mPhotoFlg = true;
                    isShotPhoto.set(false);
                    shotPhoto(data);
                } else {
                    if (isRecordVideo.get() && mOnPreviewFrameListener != null) {
                        mOnPreviewFrameListener.onPreviewFrame(data);
                    }
                }
            }
        });

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mSurfaceHolder = holder;
                mCameraHelp.openCamera(mContext, Camera.CameraInfo.CAMERA_FACING_BACK, mSurfaceHolder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCameraHelp.release();
            }
        });

        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraHelp.callFocusMode();
            }
        });
        view_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finishVideo(2);
                finish();
            }
        });
        mVideoEditor.setOnProgessListener(new onVideoEditorProgressListener() {
            @Override
            public void onProgress(VideoEditor v, int percent) {
                if (percent == 100) {
                    mExecuteProgress++;
                }
                int pro = (int) (mExecuteProgress / mExecuteCount * 100);
                editorTextView.setText("视频编辑中" + pro + "%");
            }
        });
    }

    private void shotPhoto(final byte[] nv21) {
        RxJavaUtil.run(new RxJavaUtil.OnRxAndroidListener<String>() {
            @Override
            public String doInBackground() throws Throwable {

                boolean isFrontCamera = mCameraHelp.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT;
                int rotation;
                if (isFrontCamera) {
                    rotation = 270;
                } else {
                    rotation = 90;
                }

                byte[] yuvI420 = new byte[nv21.length];
                byte[] tempYuvI420 = new byte[nv21.length];

                int videoWidth = mCameraHelp.getHeight();
                int videoHeight = mCameraHelp.getWidth();

                LibyuvUtil.convertNV21ToI420(nv21, yuvI420, mCameraHelp.getWidth(), mCameraHelp.getHeight());
                LibyuvUtil.compressI420(yuvI420, mCameraHelp.getWidth(), mCameraHelp.getHeight(), tempYuvI420,
                        mCameraHelp.getWidth(), mCameraHelp.getHeight(), rotation, isFrontCamera);

                Bitmap bitmap = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);

                LibyuvUtil.convertI420ToBitmap(tempYuvI420, bitmap, videoWidth, videoHeight);

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

    private void initData() {
        lineProgressView.setMinProgress(MIN_VIDEO_TIME / MAX_VIDEO_TIME);
        recordView.setOnGestureListener(new RecordView.OnGestureListener() {
            @Override
            public void onDown() {
                mLastClickTime = System.currentTimeMillis();
                //长按录像
                isRecordVideo.set(true);
                view_back.setVisibility(View.INVISIBLE);
                mp4FilePath = LanSongFileUtil.DEFAULT_DIR + System.currentTimeMillis() + ".mp4";
                recordHelper = new RecordHelper();
                recordHelper.startRecording(mp4FilePath, mCameraHelp.getWidth(), mCameraHelp.getHeight(), mMediaEncoderListener);
                mRecordTime = System.currentTimeMillis();
                goneRecordLayout();
                runLoopPro();

            }

            @Override
            public void onUp() {
                if (android.os.Build.BRAND.toUpperCase().equals("MEIZU")) {
                    if ((System.currentTimeMillis() - mLastClickTime) < MIN_VIDEO_TIME_MEIZU) {
                        isShotPhoto.set(true);
                    }
                } else {
                    if ((System.currentTimeMillis() - mLastClickTime) < MIN_VIDEO_TIME) {
                        isShotPhoto.set(true);
                    }
                }
                if (isRecordVideo.get()) {
                    isRecordVideo.set(false);
                    upEvent();
                }
                if (recordHelper != null) {
                    recordHelper.stopRecording();
                }
            }

            @Override
            public void onClick() {
                if (mSegmentList.size() == 0) {
                    isShotPhoto.set(true);
                }
            }
        });

        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick2()) {
                    return;
                }
                if (!isFinishing()) {
                    editorTextView = showProgressDialog();
                    mExecuteCount = mSegmentList.size() + 4;
                    finishVideo(TYPE_PREVIEW);
                }
            }
        });
        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layout_flash_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                mCameraHelp.changeFlash();
                if (mCameraHelp.isFlashOpen()) {
//                    layout_flash_video.setImageResource(R.mipmap.video_flash_open);
                } else {
//                    layout_flash_video.setImageResource(R.mipmap.video_flash_close);
                }
            }
        });

        layout_change_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                if (mCameraHelp != null) {
                    if (mCameraHelp.getCameraId() == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        mCameraHelp.openCamera(mContext, Camera.CameraInfo.CAMERA_FACING_FRONT, mSurfaceHolder);
                    } else {
                        mCameraHelp.openCamera(mContext, Camera.CameraInfo.CAMERA_FACING_BACK, mSurfaceHolder);
                    }
                }
//                if (mRecordUtil != null) {
//                    if (mCameraHelp.getCameraId() == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                        mRecordUtil.setRotation(90);
//                    } else {
//                        mRecordUtil.setRotation(270);
//                    }
//                }
            }
        });
    }

    public void finishVideo(final int type) {
        switch (type) {
            case TYPE_EDIT:
                Intent intent = new Intent(mContext, EditVideoActivity.class);
                intent.putExtra(INTENT_PATH, mp4FilePath);
                startActivityForResult(intent, REQUEST_CODE_KEY);
                break;
            case TYPE_PREVIEW:
                Intent intentPre = new Intent(mContext, VideoPreviewActivity.class);
                intentPre.putExtra(INTENT_PATH, mp4FilePath);
                startActivityForResult(intentPre, REQUEST_CODE_PREVIEW);
                break;
        }

    }

    private void goneRecordLayout() {
        iv_delete.setVisibility(View.INVISIBLE);
        iv_next.setVisibility(View.INVISIBLE);
        iv_recorded_edit.setVisibility(View.GONE);
    }


    private void upEvent() {
        if (recordHelper != null) {
            recordHelper.stopRecording();
            recordHelper = null;
        }
        initRecorderState(false);
    }

    /**
     * 初始化视频拍摄状态
     */
    private void initRecorderState(boolean isShow) {
        if (isShow) {
            tv_hint.setVisibility(View.VISIBLE);
            recordView.setVisibility(View.VISIBLE);
        } else {
            tv_hint.setVisibility(View.GONE);
        }
        view_back.setVisibility(View.VISIBLE);
        if (lineProgressView.getProgress() * MAX_VIDEO_TIME < MIN_VIDEO_TIME) {
            iv_recorded_edit.setVisibility(View.GONE);
            iv_delete_back.setVisibility(View.VISIBLE);
        } else {
            recordView.setVisibility(View.GONE);
            iv_recorded_edit.setVisibility(View.GONE);
            iv_delete_back.setVisibility(View.GONE);
        }
    }

    /**
     * 清除录制信息
     */
    private void cleanRecord() {
        recordView.initState();
        lineProgressView.cleanSplit();
        mSegmentList.clear();
        mAacList.clear();
        mTimeList.clear();

        mExecuteCount = 0;
        mExecuteProgress = 0;

        iv_delete.setVisibility(View.INVISIBLE);
        iv_next.setVisibility(View.INVISIBLE);
        iv_recorded_edit.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cleanRecord();
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
                    cleanRecord();
                    initRecorderState(true);
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
            cleanRecord();
            initRecorderState(true);
        }
    }

    private void runLoopPro() {

        RxJavaUtil.loop(20, new RxJavaUtil.OnRxLoopListener() {
            @Override
            public Boolean takeWhile() {
//                return mRecordUtil != null && mRecordUtil.isRecording();
                return false;
            }

            @Override
            public void onExecute() {
                long currentTime = System.currentTimeMillis();
                mVideoDuration += currentTime - mRecordTime;
                mRecordTime = currentTime;
                long countTime = mVideoDuration;
                for (long time : mTimeList) {
                    countTime += time;
                }
                if (countTime <= MAX_VIDEO_TIME) {
                    lineProgressView.setProgress(countTime / MAX_VIDEO_TIME);
                    recordView.updateProgress(countTime / MAX_VIDEO_TIME * 360);
//                  tv_hint.setText(countTime/1000+"秒");
                    tv_hint.setVisibility(View.GONE);
                } else {
                    upEvent();
//                    iv_next.callOnClick();
                }
            }

            @Override
            public void onFinish() {
                mSegmentList.add(mp4FilePath);
                mTimeList.add(mVideoDuration);
                initRecorderState(false);
                if (!isShotPhoto.get()) {
                    // 录制完成后进入预览视频
                    if (!mPhotoFlg) {
                        iv_next.callOnClick();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                lineProgressView.removeSplit();
            }
        });
    }

    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder)
                surfaceView.setVideoEncoder((MediaVideoEncoder) encoder);
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder)
                surfaceView.setVideoEncoder(null);
        }
    };
}

