package com.example.nim_lib.ui;

import android.Manifest;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nim_lib.R;
import com.example.nim_lib.action.VideoAction;
import com.example.nim_lib.bean.ReturnBean;
import com.example.nim_lib.bean.TokenBean;
import com.example.nim_lib.config.AVChatConfigs;
import com.example.nim_lib.config.AppConfig;
import com.example.nim_lib.config.Preferences;
import com.example.nim_lib.constant.AVChatExitCode;
import com.example.nim_lib.constant.CoreEnum;
import com.example.nim_lib.controll.AVChatController;
import com.example.nim_lib.controll.AVChatProfile;
import com.example.nim_lib.controll.AVChatSoundPlayer;
import com.example.nim_lib.event.EventFactory;
import com.example.nim_lib.module.AVChatTimeoutObserver;
import com.example.nim_lib.module.AVSwitchListener;
import com.example.nim_lib.module.SimpleAVChatStateObserver;
import com.example.nim_lib.net.CallBack;
import com.example.nim_lib.net.RunUtils;
import com.example.nim_lib.permission.BaseMPermission;
import com.example.nim_lib.receiver.PhoneCallStateObserver;
import com.example.nim_lib.util.GlideUtil;
import com.example.nim_lib.util.ScreenUtil;
import com.example.nim_lib.util.SharedPreferencesUtil;
import com.example.nim_lib.util.ToastUtil;
import com.example.nim_lib.util.ViewUtils;
import com.example.nim_lib.util.flyn.Eyes;
import com.example.nim_lib.widgets.ToggleListener;
import com.example.nim_lib.widgets.ToggleState;
import com.example.nim_lib.widgets.ToggleView;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatControlCommand;
import com.netease.nimlib.sdk.avchat.constant.AVChatEventType;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatAudioFrame;
import com.netease.nimlib.sdk.avchat.model.AVChatCalleeAckEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatCommonEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatControlEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoFrame;
import com.netease.nimlib.sdk.avchat.video.AVChatSurfaceViewRenderer;
import com.netease.nrtc.video.render.IVideoRender;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2019-10-16
 * @updateAuthor
 * @updateDate
 * @description 视频通话
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class VideoActivity extends AppCompatActivity implements View.OnClickListener, ToggleListener, AVSwitchListener {

    private boolean destroyRTC = false;
    private AVChatConfigs avChatConfigs;
    // state
    private boolean surfaceInit = false;
    private boolean videoInit = false;
    private boolean shouldEnableToggle = false;
    public boolean canSwitchCamera = false;
    private boolean isInSwitch = false;
    private boolean isPeerVideoOff = false;
    private boolean isLocalVideoOff = false;
    private boolean localPreviewInSmallSize = true;
    private boolean isRecordWarning = false;
    private boolean isInReceiveing = false;
    private boolean mIsInComingCall = false;// is incoming call or outgoing call
    // 电话是否接通
    private boolean isCallEstablished = false;
    private Long toUId = null;
    private String toGid = null;

    // data
    private TouchZoneCallback touchZoneCallback;
    // 聊天数据
    private AVChatData avChatData;
    private String account;
    private String displayName;
    // 显示在大图像的用户id
    private String largeAccount;
    // 显示在小图像的用户id
    private String smallAccount;

    // move
    private int lastX, lastY;
    private int inX, inY;
    private Rect paddingRect;

    // constant
    private static final int PEER_CLOSE_CAMERA = 0;
    private static final int LOCAL_CLOSE_CAMERA = 1;
    private static final int AUDIO_TO_VIDEO_WAIT = 2;
    private static final int TOUCH_SLOP = 10;
    private static final String TAG = VideoActivity.class.getSimpleName();

    //render
    private AVChatSurfaceViewRenderer smallRender;
    private AVChatSurfaceViewRenderer largeRender;
    private IVideoRender remoteRender;
    private IVideoRender localRender;

    private final String[] BASIC_PERMISSIONS = new String[]{Manifest.permission.CAMERA,};
    private int state; // calltype 音频或视频
    private boolean isReleasedVideo = false;

    /**
     * surface view
     */
    private LinearLayout largeSizePreviewLayout;
    private FrameLayout smallSizePreviewFrameLayout;
    private LinearLayout smallSizePreviewLayout;
    private ImageView smallSizePreviewCoverImg;//stands for peer or local close camera
    private TextView largeSizePreviewCoverLayout;//stands for peer or local close camera
    private ToggleView switchCameraToggle;
    private TextView txtVideoTime;
    private CheckBox cbChangeVoice;
    private CheckBox cbConvertCamera;
    private ImageView imgMinimizeVideo;
    private View touchLayout;

    // 音视频控制器：用于实现音视频拨打接听，音视频切换的具体功能实现
    private AVChatController mAVChatController;
    private View viewVideo;
    // 网易ID
    private String mNeteaseaccId;
    // 0 呼叫 1 接收  2通话
    private int mVoiceType;
    // 用户姓名、头像
    private String mUserName = "", mUserHeadSculpture = "";
    // 通话类型
    private int mAVChatType;
    // 通话时间
    private int mPassedTime = 0;
    // 等待时间
    private int mWaitTime = 0;
    // 显示等待消息时间
    private int mShowWaitTime = 0;
    Handler mHandler = new Handler();
    // 设置28秒没接弹出提示
    private final int WAIT_TIME = 28;
    // 设置提示消息多少秒后消失
    private final int SHOW_WAIT_TIME = 10;
    // 1秒刷新
    private final int TIME = 1000;
    // 收到接听后是否第一次开始倒计时，默认是
    private boolean mIsFistCountDown = true;
    private VideoAction mVideoAction;
    private String mRoomId; // 网易房间id
    private Long mFriend;// 通话接收人uid
    private boolean isFirstFlg = true;// 第一次发送消息标记

    private ImageView imgHeadPortrait;
    private TextView txtName;
    private TextView txtWaitMsg;
    private TextView txtLifeTime;
    private LinearLayout layoutVoiceWait;
    private LinearLayout layoutInvitationVoice;
    private LinearLayout layoutAudio;
    private View layoutVideoRoot;
    private LinearLayout layoutVoiceIng;
    private ImageView imgRefuse;
    private ImageView imgAnswer;
    private ImageView imgHandUp2;
    private ImageView imgHandUp;
    private ImageView imgCancle;
    private ImageView imgMinimize;
    private TextView txtMessage;
    private CheckBox cbMute;
    private RelativeLayout layoutVoice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        setStatusBarColor(R.color.color_707);
        findSurfaceView();
        findView();
        onEvent();
        init();
        initData();
        registerObserves(true);
    }

    public void setStatusBarColor(int color) {
        Eyes.setStatusBarColor(this, getResources().getColor(color));
        Eyes.setStatusBarLightMode(this, getResources().getColor(color));
    }

    private void findView() {
        viewVideo = findViewById(R.id.layout_video);
        imgHeadPortrait = findViewById(R.id.img_head_portrait);
        txtName = findViewById(R.id.txt_name);
        imgCancle = findViewById(R.id.img_cancle);
        imgMinimize = findViewById(R.id.img_minimize);

        txtWaitMsg = findViewById(R.id.txt_wait_msg);
        layoutVoiceWait = findViewById(R.id.layout_voice_wait);
        layoutInvitationVoice = findViewById(R.id.layout_invitation_voice);
        layoutAudio = findViewById(R.id.layout_audio);
        layoutVideoRoot = findViewById(R.id.avchat_surface_layout);
        layoutVoiceIng = findViewById(R.id.layout_voice_ing);
        imgRefuse = findViewById(R.id.img_refuse);
        imgAnswer = findViewById(R.id.img_answer);
        imgHandUp2 = findViewById(R.id.img_hand_up2);
        imgHandUp = findViewById(R.id.img_hand_up);
        layoutVoice = findViewById(R.id.layout_voice);
        txtLifeTime = findViewById(R.id.txt_life_time);
        cbMute = findViewById(R.id.cb_mute);
        txtMessage = findViewById(R.id.txt_message);
    }

    private void onEvent() {
        imgAnswer.setOnClickListener(this);
        imgRefuse.setOnClickListener(this);
        imgHandUp.setOnClickListener(this);
        imgHandUp2.setOnClickListener(this);
        imgCancle.setOnClickListener(this);
        imgMinimize.setOnClickListener(this);
        cbChangeVoice.setOnClickListener(this);
        cbConvertCamera.setOnClickListener(this);
        imgMinimizeVideo.setOnClickListener(this);
        imgMinimize.setOnClickListener(this);
        cbMute.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ViewUtils.isFastDoubleClick()) {
            return;
        }
        // 取消、拒绝、挂断
        if (v.getId() == R.id.img_cancle || v.getId() == R.id.img_hand_up
                || v.getId() == R.id.img_refuse || v.getId() == R.id.img_hand_up2) {
            if (avChatData != null) {
                mAVChatController.hangUp2(avChatData.getChatId(), AVChatExitCode.HANGUP, mAVChatType, toUId);
                if (v.getId() == R.id.img_cancle) {
                    sendEventBus("cancle", AVChatExitCode.CANCEL);
                } else if ((v.getId() == R.id.img_hand_up|| v.getId() == R.id.img_hand_up2) && toUId != null && toUId != 0) {
                    sendEventBus("hangup", AVChatExitCode.HANGUP);
                }
            } else {
                Toast.makeText(VideoActivity.this, R.string.avchat_cancel, Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (v.getId() == R.id.img_answer) {// 接听
            if (avChatData != null) {
                receiveInComingCall();
            }
        } else if (v.getId() == R.id.cb_hands_free) {// 免提
            mAVChatController.toggleSpeaker();
        } else if (v.getId() == R.id.cb_change_voice) {// 视频切换到语音
            mAVChatController.switchVideoToAudio(avChatData.getChatId(), this);
        } else if (v.getId() == R.id.img_minimize) {// 语音最小化
            sendVoiceMinimizeEventBus();
            // 退到后台不显
            moveTaskToBack(true);
        } else if (v.getId() == R.id.cb_convert_camera) {// 摄像头切换
            mAVChatController.switchCamera();
        } else if (v.getId() == R.id.img_minimize_video) {// 视频最小化

        } else if (v.getId() == R.id.cb_mute) {// 音频开关
            mAVChatController.toggleMute();
        }
    }

    private void sendVoiceMinimizeEventBus() {
        EventFactory.VoiceMinimizeEvent event = new EventFactory.VoiceMinimizeEvent();
        event.passedTime = mPassedTime;
        event.isCallEstablished = isCallEstablished;
        event.showTime = txtLifeTime.getText().toString();
        EventBus.getDefault().post(event);
    }

    private void init() {
        AppConfig.setContext(getApplicationContext());
        TokenBean tokenBean = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).get4Json(TokenBean.class);
        mVideoAction = new VideoAction(tokenBean.getAccessToken());

        this.smallRender = new AVChatSurfaceViewRenderer(this);
        this.largeRender = new AVChatSurfaceViewRenderer(this);
        mAVChatController = new AVChatController(this, mVideoAction);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            avChatData = (AVChatData) bundle.getSerializable(Preferences.AVCHATDATA);
            mNeteaseaccId = bundle.getString(Preferences.NETEASEACC_ID);
            mUserName = bundle.getString(Preferences.USER_NAME);
            mUserHeadSculpture = bundle.getString(Preferences.USER_HEAD_SCULPTURE);
            mVoiceType = bundle.getInt(Preferences.VOICE_TYPE);
            mAVChatType = bundle.getInt(Preferences.AVCHA_TTYPE);
            toUId = bundle.getLong(Preferences.TOUID);
            toGid = bundle.getString(Preferences.TOGID);

            mRoomId = bundle.getString(Preferences.ROOM_ID);
            mFriend = bundle.getLong(Preferences.FRIEND);
            if (avChatData != null) {
                mIsInComingCall = true;
                account = avChatData.getAccount();

                initLargeSurfaceView(avChatData.getAccount());
                initSmallSurfaceView();
            }
        }
//        if (mAVChatType == AVChatType.VIDEO.getValue()) {
//            if (!mIsInComingCall) {
////            initLargeSurfaceView(mNeteaseaccId);// TODO 进来打开摄像
////                outGoingCalling(AVChatType.VIDEO);
//
////                auVideoDial(toUId, mAVChatType, mRoomId = AVChatController.getUUID(), AVChatType.VIDEO);
//            } else {
//                initLargeSurfaceView(avChatData.getAccount());
//                initSmallSurfaceView();
//            }
//        }
        switch (mVoiceType) {
            case CoreEnum.VoiceType.WAIT:
                layoutVoiceWait.setVisibility(View.VISIBLE);
                AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.CONNECTING);
                txtWaitMsg.setText(getString(R.string.avchat_wait_recieve1));
                if (mAVChatType == AVChatType.AUDIO.getValue()) {
//                    outGoingCalling(AVChatType.AUDIO);
                    auVideoDial(toUId, mAVChatType, mRoomId = AVChatController.getUUID(), AVChatType.AUDIO);
                } else {
//                    outGoingCalling(AVChatType.VIDEO);
                    auVideoDial(toUId, mAVChatType, mRoomId = AVChatController.getUUID(), AVChatType.VIDEO);
                }
                break;
            case CoreEnum.VoiceType.RECEIVE:
                if (mAVChatType == AVChatType.VIDEO.getValue()) {
                    imgAnswer.setImageResource(R.drawable.receive_video_selector);
                    txtWaitMsg.setText(getString(R.string.avchat_audio_to_video_invitation));
                } else {
                    txtWaitMsg.setText(R.string.avchat_audio_invitation);
                }
                AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.PEER_REJECT);
                layoutInvitationVoice.setVisibility(View.VISIBLE);
                layoutVoiceWait.setVisibility(View.GONE);
                break;
            case CoreEnum.VoiceType.CALLING:
                if (mAVChatType == AVChatType.VIDEO.getValue()) {
                    layoutAudio.setVisibility(View.GONE);
                    layoutInvitationVoice.setVisibility(View.GONE);
                    layoutVideoRoot.setVisibility(View.VISIBLE);
                } else {
                    layoutVideoRoot.setVisibility(View.GONE);
                    layoutVoiceIng.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void initData() {
        txtName.setText(mUserName);
        Glide.with(this).load(mUserHeadSculpture)
                .apply(GlideUtil.headImageOptions()).into(imgHeadPortrait);
        registerObserves(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 注册通话中观察者
     *
     * @param register
     */
    private void registerObserves(boolean register) {
        AVChatManager.getInstance().observeAVChatState(avchatStateObserver, register);
        AVChatManager.getInstance().observeHangUpNotification(callHangupObserver, register);
        AVChatManager.getInstance().observeControlNotification(callControlObserver, register);
        AVChatManager.getInstance().observeCalleeAckNotification(callAckObserver, register);
        AVChatTimeoutObserver.getInstance().observeTimeoutNotification(timeoutObserver, register, register, this);
        PhoneCallStateObserver.getInstance().observeAutoHangUpForLocalPhone(autoHangUpForLocalPhoneObserver, register);
    }

    @Override
    protected void onDestroy() {
        AVChatSoundPlayer.instance().stop();
        super.onDestroy();
        registerObserves(false);
        if (!isFinishing()) {
            mHandler.removeCallbacks(runnable);
            mHandler.removeCallbacks(runnableWait);
            mHandler.removeCallbacks(runnableShowWait);
        }
    }

    @Override
    public void toggleOn(View v) {
        onClick(v);
    }

    @Override
    public void toggleOff(View v) {
        onClick(v);
    }

    @Override
    public void toggleDisable(View v) {

    }

    /**
     * 通话计时
     */
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            mPassedTime++;
            Log.d("1212", "" + mPassedTime);
            int hour = mPassedTime / 3600;
            int min = mPassedTime % 3600 / 60;
            int second = mPassedTime % 60;
            if (!isFinishing()) {
                mHandler.postDelayed(this, TIME);
                if (hour > 0) {
                    txtVideoTime.setText(String.format(Locale.CHINESE, "%02d:%02d:%02d", hour, min, second));
                    txtLifeTime.setText(String.format(Locale.CHINESE, "%02d:%02d:%02d", hour, min, second));
                } else {
                    txtVideoTime.setText(String.format(Locale.CHINESE, "%02d:%02d", min, second));
                    txtLifeTime.setText(String.format(Locale.CHINESE, "%02d:%02d", min, second));
                }
            }
        }
    };

    /**
     * 等待计时
     */
    Runnable runnableWait = new Runnable() {

        @Override
        public void run() {
            mWaitTime++;
            if (!isCallEstablished) {
                if (mWaitTime >= WAIT_TIME) {
                    mHandler.removeCallbacks(runnableWait);
                    txtMessage.setVisibility(View.VISIBLE);
                    if (!isFinishing()) {
                        mHandler.postDelayed(runnableShowWait, TIME);
                    }
                } else {
                    if (!isFinishing()) {
                        mHandler.postDelayed(runnableWait, TIME);
                    }
                }
            }
        }
    };

    /**
     * 显示等待消息计时
     */
    Runnable runnableShowWait = new Runnable() {

        @Override
        public void run() {
            mShowWaitTime++;
            if (!isCallEstablished) {
                if (mShowWaitTime >= SHOW_WAIT_TIME) {
                    mHandler.removeCallbacks(runnableShowWait);
                    txtMessage.setVisibility(View.GONE);
                } else {
                    if (!isFinishing()) {
                        mHandler.postDelayed(runnableShowWait, TIME);
                    }
                }
            }
        }
    };

    /**
     * ****************************** 通话过程状态监听 监听器 **********************************
     */
    private SimpleAVChatStateObserver avchatStateObserver = new SimpleAVChatStateObserver() {
        @Override
        public void onAVRecordingCompletion(String account, String filePath) {
            Log.d(TAG, "onAVRecordingCompletion -> " + account);
//            if (account != null && filePath != null && filePath.length() > 0) {
//                String msg = "音视频录制已结束, " + "账号：" + account + " 录制文件已保存至：" + filePath;
//                Toast.makeText(AVChatActivity.this, msg, Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(AVChatActivity.this, "录制已结束.", Toast.LENGTH_SHORT).show();
//            }
//            if (state == AVChatType.VIDEO.getValue()) {
//                avChatVideoUI.resetRecordTip();
//            } else {
//                avChatAudioUI.resetRecordTip();
//            }
        }

        @Override
        public void onAudioRecordingCompletion(String filePath) {
            Log.d(TAG, "onAudioRecordingCompletion -> " + filePath);
//            if (filePath != null && filePath.length() > 0) {
//                String msg = "音频录制已结束, 录制文件已保存至：" + filePath;
//                Toast.makeText(AVChatActivity.this, msg, Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(AVChatActivity.this, "录制已结束.", Toast.LENGTH_SHORT).show();
//            }
//            if (state == AVChatType.AUDIO.getValue()) {
//                avChatAudioUI.resetRecordTip();
//            } else {
//                avChatVideoUI.resetRecordTip();
//            }
        }

        @Override
        public void onLowStorageSpaceWarning(long availableSize) {
            Log.d(TAG, "onLowStorageSpaceWarning -> " + availableSize);
//            if (state == AVChatType.VIDEO.getValue()) {
//                avChatVideoUI.showRecordWarning();
//            } else {
//                avChatAudioUI.showRecordWarning();
//            }
        }

        @Override
        public void onJoinedChannel(int code, String audioFile, String videoFile, int i) {
            Log.d(TAG, "audioFile -> " + audioFile + " videoFile -> " + videoFile);
//            handleWithConnectServerResult(code);
        }

        @Override
        public void onUserJoined(String account) {
            Log.d(TAG, "onUserJoin -> " + account);
//            Log.d("1212", "onUserJoin:" + account);
            mIsInComingCall = true;
//            if (state == AVChatType.VIDEO.getValue()) {
            if (mAVChatType == AVChatType.VIDEO.getValue()) {
//                layoutAudio.setVisibility(View.GONE);
//                layoutInvitationVoice.setVisibility(View.GONE);
                layoutVoice.setVisibility(View.GONE);
                layoutVideoRoot.setVisibility(View.VISIBLE);
                initLargeSurfaceView(account);
            }
        }

        @Override
        public void onUserLeave(String account, int event) {
            Log.d(TAG, "onUserLeave -> " + account);
//            manualHangUp(AVChatExitCode.HANGUP);
            finish();
        }

        @Override
        public void onCallEstablished() {
            Log.d(TAG, "onCallEstablished");
//            //移除超时监听
            AVChatTimeoutObserver.getInstance().observeTimeoutNotification(timeoutObserver, false, mIsInComingCall, VideoActivity.this);
//            if (avChatController.getTimeBase() == 0)
//                avChatController.setTimeBase(SystemClock.elapsedRealtime());

            if (state == AVChatType.AUDIO.getValue()) {
//                showAudioInitLayout();
            } else {
                // 接通以后，自己是小屏幕显示图像，对方是大屏幕显示图像
                initSmallSurfaceView();
//                showVideoInitLayout();
                findSurfaceView();
            }
            isCallEstablished = true;
        }

        @Override
        public boolean onVideoFrameFilter(AVChatVideoFrame frame, boolean maybeDualInput) {
//            if (faceU != null) {
//                faceU.effect(frame.data, frame.width, frame.height, FaceU.VIDEO_FRAME_FORMAT.I420);
//            }

            return true;
        }

        @Override
        public boolean onAudioFrameFilter(AVChatAudioFrame frame) {
            return true;
        }

    };

    private void sendEventBus(String operation, int operationType) {
        EventFactory.CloseVoiceMinimizeEvent event = new EventFactory.CloseVoiceMinimizeEvent();
        event.avChatType = mAVChatType;
        event.operation = operation;
        if (operationType == AVChatExitCode.CANCEL) {
            event.txt = "已取消";
        } else if (operationType == AVChatExitCode.REJECT) {
            event.txt = "已拒绝";
        } else if (operationType == AVChatExitCode.HANGUP) {
            event.txt = "通话时长 " + txtVideoTime.getText().toString();
        }
        event.toGid = toGid;
        event.toUId = toUId;
        // 临时处理自己取消情况
        if (operationType != AVChatExitCode.HANGUP || !TextUtils.isEmpty(txtVideoTime.getText().toString())) {
            EventBus.getDefault().post(event);
        }
    }

    /**
     * 通话过程中，收到对方挂断电话
     */
    Observer<AVChatCommonEvent> callHangupObserver = new Observer<AVChatCommonEvent>() {
        @Override
        public void onEvent(AVChatCommonEvent avChatHangUpInfo) {
//            avChatData = avChatController.getAvChatData();
            Log.i(TAG, "对方挂断电话");
            if (avChatData != null && avChatData.getChatId() == avChatHangUpInfo.getChatId()) {
                hangUpByOther(AVChatExitCode.HANGUP);
                // toUId != null 主叫挂断不需要在发送消息
                if (isFirstFlg && toUId != null && toUId != 0) {
                    isFirstFlg = false;
                    sendEventBus("hangup", AVChatExitCode.HANGUP);
                }
                // 接收方通知后端已挂断
                if (mFriend != null && mFriend != 0) {
                    mAVChatController.auVideoHandup(mFriend, mAVChatType, mRoomId);
                }
                if (!isFinishing()) {
                    mHandler.removeCallbacks(runnable);
                }
//                cancelCallingNotifier();
//                // 如果是incoming call主叫方挂断，那么通知栏有通知
                if (mIsInComingCall && !isCallEstablished) {
//                    activeMissCallNotifier();
                }
            }

        }
    };

    /**
     * 注册/注销网络通话被叫方的响应（接听、拒绝、忙）
     *
     * @param observer 观察者， 参数为接收到的网络通话的通知消息
     * @param register {@code true} 注册监听，{@code false} 注销监听
     */
    private Observer<AVChatCalleeAckEvent> callAckObserver = new Observer<AVChatCalleeAckEvent>() {
        @Override
        public void onEvent(AVChatCalleeAckEvent ackInfo) {
            AVChatSoundPlayer.instance().stop();
            if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_BUSY) {
                // 对方正在忙
                Log.i(TAG, "对方正在忙");
                hangUpByOther(AVChatExitCode.PEER_BUSY);
            } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_REJECT) {
                // 对方拒绝接听
                Log.i(TAG, "对方拒绝接听");
                hangUpByOther(AVChatExitCode.REJECT);
                if (isFirstFlg && toUId != null && toUId != 0) {
                    isFirstFlg = false;
                    sendEventBus("reject", AVChatExitCode.REJECT);
                }
            } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_AGREE) {
                // 对方同意接听
                Log.i(TAG, "对方同意接听" + ackInfo.getAccount());
                if (!isFinishing()) {
                    // 会收到多次同意，所以需要判断是否为第一次
                    if (mIsFistCountDown) {
                        mIsFistCountDown = false;
                        mHandler.postDelayed(runnable, TIME);
                    }
                    if (!TextUtils.isEmpty(mRoomId)) {
                        auVideoKeepAlive(mRoomId);
                    }
                    if (mAVChatType == AVChatType.VIDEO.getValue()) {
                        onAudioToVideoAgree(ackInfo.getAccount());
                    } else {
                        onAudioAgree();
                    }
                }
            }
        }
    };

    /**
     * 监听音视频模式切换通知, 对方音视频开关通知
     */
    Observer<AVChatControlEvent> callControlObserver = new Observer<AVChatControlEvent>() {
        @Override
        public void onEvent(AVChatControlEvent netCallControlNotification) {
            Log.i(TAG, "通话结束");
//            hangUpByOther(AVChatExitCode.HANGUP);
            handleCallControl(netCallControlNotification);
        }
    };

    /**
     * 来电超时，未接听
     */
    Observer<Integer> timeoutObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer integer) {
            manualHangUp(AVChatExitCode.CANCEL);
            // 电超时通知后端已挂断
            if (mFriend != null && mFriend != 0) {
                mAVChatController.auVideoHandup(mFriend, mAVChatType, mRoomId);
            }
            // 来电超时，自己未接听
            if (mIsInComingCall) {
//                activeMissCallNotifier();
            }
            finish();
        }
    };

    /**
     * 处理音视频切换请求和对方音视频开关通知
     *
     * @param notification
     */
    private void handleCallControl(AVChatControlEvent notification) {
        if (AVChatManager.getInstance().getCurrentChatId() != notification.getChatId()) {
            return;
        }
        switch (notification.getControlCommand()) {
            case AVChatControlCommand.SWITCH_AUDIO_TO_VIDEO:
//                incomingAudioToVideo();
                break;
            case AVChatControlCommand.SWITCH_AUDIO_TO_VIDEO_AGREE:
                // 对方同意切成视频啦
                state = AVChatType.VIDEO.getValue();
                onAudioToVideoAgree(notification.getAccount());
                break;
            case AVChatControlCommand.SWITCH_AUDIO_TO_VIDEO_REJECT:
//                rejectAudioToVideo();
//                Toast.makeText(AVChatActivity.this, R.string.avchat_switch_video_reject, Toast.LENGTH_SHORT).show();
                break;
            case AVChatControlCommand.SWITCH_VIDEO_TO_AUDIO:
                onVideoToAudio();
                break;
            case AVChatControlCommand.NOTIFY_VIDEO_OFF:
                // 收到对方关闭画面通知
                if (state == AVChatType.VIDEO.getValue()) {
//                    avChatVideoUI.peerVideoOff();
                }
                break;
            case AVChatControlCommand.NOTIFY_VIDEO_ON:
                // 收到对方打开画面通知
//                if (state == AVChatType.VIDEO.getValue()) {
//                    avChatVideoUI.peerVideoOn();
//                }
                break;
            default:
//                Toast.makeText(this, "对方发来指令值：" + notification.getControlCommand(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onVideoToAudio() {
        state = AVChatType.AUDIO.getValue();
        layoutVoiceWait.setVisibility(View.GONE);
        layoutInvitationVoice.setVisibility(View.GONE);
        txtWaitMsg.setVisibility(View.GONE);

        layoutVoice.setVisibility(View.VISIBLE);
        layoutAudio.setVisibility(View.VISIBLE);
        layoutVoiceIng.setVisibility(View.VISIBLE);

        layoutVideoRoot.setVisibility(View.GONE);
//        showAudioInitLayout();
//
//        muteToggle.toggle(muteOn ? ToggleState.ON : ToggleState.OFF);
//        speakerToggle.toggle(speakerOn ? ToggleState.ON : ToggleState.OFF);
//        recordToggle.setSelected(avChatController.isRecording());
//
//        showRecordView(avChatController.isRecording(), isRecordWarning);

        //关闭视频
        if (AVChatManager.getInstance().isAutoPublishVideo()) {
            AVChatManager.getInstance().stopVideoPreview();
            AVChatManager.getInstance().disableVideo();
        }
        // 判断是否静音，扬声器是否开启，对界面相应控件进行显隐处理。
//        onVideoToAudio(AVChatManager.getInstance().isLocalAudioMuted(),
//                AVChatManager.getInstance().speakerEnabled(),
//                avChatData != null ? avChatData.getAccount() : account);
    }

    @Override
    public void onAudioToVideo() {

    }

    @Override
    public void onReceiveAudioToVideoAgree() {

    }

    // touch zone
    public interface TouchZoneCallback {
        void onTouch();
    }

    /**
     * 视频切换为音频时，禁音与扬声器按钮状态
     *
     * @param muteOn
     * @param speakerOn
     * @param account
     */
    public void onVideoToAudio(boolean muteOn, boolean speakerOn, String account) {

        this.account = account;
//        showAudioInitLayout();
//
//        muteToggle.toggle(muteOn ? ToggleState.ON : ToggleState.OFF);
//        speakerToggle.toggle(speakerOn ? ToggleState.ON : ToggleState.OFF);
//        recordToggle.setSelected(avChatController.isRecording());
//
//        showRecordView(avChatController.isRecording(), isRecordWarning);

        //关闭视频
        AVChatManager.getInstance().stopVideoPreview();
        AVChatManager.getInstance().disableVideo();
    }

    /**
     * 主动挂断
     *
     * @param exitCode
     */
    private void manualHangUp(int exitCode) {
        if (mAVChatType == AVChatType.VIDEO.getValue()) {
            releaseVideo();
        }
        onHangUp(exitCode);
    }

    /**
     * 被对方挂断
     *
     * @param exitCode
     */
    private void hangUpByOther(int exitCode) {
        if (exitCode == AVChatExitCode.PEER_BUSY) {
            if (avChatData != null) {
                showQuitToast(AVChatExitCode.PEER_BUSY);
                mAVChatController.hangUp2(avChatData.getChatId(), AVChatExitCode.HANGUP, mAVChatType, toUId);
            }
        } else {
            if (mAVChatType == AVChatType.VIDEO.getValue()) {
                releaseVideo();
            }
            onHangUp(exitCode);
        }
    }

    /**
     * 收到挂断通知，自己的处理
     *
     * @param exitCode
     */
    public void onHangUp(int exitCode) {
        if (destroyRTC) {
            return;
        }
        AVChatManager.getInstance().disableRtc();
        destroyRTC = true;
        showQuitToast(exitCode);
        finish();
    }

    private void releaseVideo() {
        if (isReleasedVideo) {
            return;
        }
        isReleasedVideo = true;
        AVChatManager.getInstance().stopVideoPreview();
        AVChatManager.getInstance().disableVideo();
    }

    /**
     * 显示退出toast
     *
     * @param code
     */
    public void showQuitToast(int code) {
        switch (code) {
            case AVChatExitCode.NET_CHANGE: // 网络切换
            case AVChatExitCode.NET_ERROR: // 网络异常
            case AVChatExitCode.CONFIG_ERROR: // 服务器返回数据错误
                Toast.makeText(VideoActivity.this, R.string.avchat_net_error_then_quit, Toast.LENGTH_SHORT).show();
                break;
            case AVChatExitCode.REJECT:
                Toast.makeText(VideoActivity.this, R.string.avchat_call_reject, Toast.LENGTH_SHORT).show();
                break;
            case AVChatExitCode.PEER_HANGUP:
            case AVChatExitCode.HANGUP:
//                if (isCallEstablish.get()) {
                Toast.makeText(VideoActivity.this, R.string.avchat_call_finish, Toast.LENGTH_SHORT).show();
//                }
                break;
            case AVChatExitCode.PEER_BUSY:
                Toast.makeText(VideoActivity.this, R.string.avchat_peer_busy, Toast.LENGTH_SHORT).show();
                break;
            case AVChatExitCode.PROTOCOL_INCOMPATIBLE_PEER_LOWER:
                Toast.makeText(VideoActivity.this, R.string.avchat_peer_protocol_low_version, Toast.LENGTH_SHORT).show();
                break;
            case AVChatExitCode.PROTOCOL_INCOMPATIBLE_SELF_LOWER:
                Toast.makeText(VideoActivity.this, R.string.avchat_local_protocol_low_version, Toast.LENGTH_SHORT).show();
                break;
            case AVChatExitCode.INVALIDE_CHANNELID:
                Toast.makeText(VideoActivity.this, R.string.avchat_invalid_channel_id, Toast.LENGTH_SHORT).show();
                break;
            case AVChatExitCode.LOCAL_CALL_BUSY:
                Toast.makeText(VideoActivity.this, R.string.avchat_local_call_busy, Toast.LENGTH_SHORT).show();
                break;
            case AVChatExitCode.CANCEL:
                Toast.makeText(VideoActivity.this, R.string.avchat_no_pickup_call, Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    Observer<Integer> autoHangUpForLocalPhoneObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer integer) {
            hangUpByOther(AVChatExitCode.PEER_BUSY);
        }
    };

    /**
     * 对方接受后更新UI
     */
    private void onAudioAgree() {
        AVChatSoundPlayer.instance().stop();
        if (mAVChatType == AVChatType.VIDEO.getValue()) {
            layoutAudio.setVisibility(View.GONE);
            layoutInvitationVoice.setVisibility(View.GONE);
            layoutVideoRoot.setVisibility(View.VISIBLE);
        } else {
            layoutVoiceIng.setVisibility(View.VISIBLE);
            txtWaitMsg.setVisibility(View.GONE);
            layoutVoiceWait.setVisibility(View.GONE);
            layoutInvitationVoice.setVisibility(View.GONE);
        }
    }

    /**
     * 对方同意切成视频啦 刷新界面
     *
     * @param largeAccount
     */
    public void onAudioToVideoAgree(String largeAccount) {
        Log.d(TAG, "对方账号" + largeAccount);
//        showVideoInitLayout();
        findSurfaceView();
        account = largeAccount;

//        muteToggle.toggle(AVChatManager.getInstance().isLocalAudioMuted() ? ToggleState.ON : ToggleState.OFF);
//        closeCameraToggle.toggle(ToggleState.OFF);
//        switchCameraToggle.off(false);
//        recordToggle.setEnabled(true);
//        recordToggle.setSelected(avChatController.isRecording());

        //打开视频
        isReleasedVideo = false;
        smallRender = new AVChatSurfaceViewRenderer(this);
        largeRender = new AVChatSurfaceViewRenderer(this);

        //打开视频
        AVChatManager.getInstance().enableVideo();
        AVChatManager.getInstance().startVideoPreview();

        initSmallSurfaceView();
        // 是否在发送视频 即摄像头是否开启
        if (AVChatManager.getInstance().isLocalVideoMuted()) {
            AVChatManager.getInstance().muteLocalVideo(false);
            localVideoOn();
        }

//        initLargeSurfaceView(largeAccount);
//        showRecordView(avChatController.isRecording(), isRecordWarning);
    }

    /**
     * 对方打开了摄像头
     */
    private void localVideoOn() {
        isLocalVideoOff = false;
        if (localPreviewInSmallSize) {
            smallSizePreviewCoverImg.setVisibility(View.GONE);
        } else {
            largeSizePreviewCoverLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 对方打开了摄像头
     */
    public void peerVideoOn() {
        isPeerVideoOff = false;
        if (localPreviewInSmallSize) {
            largeSizePreviewCoverLayout.setVisibility(View.GONE);
        } else {
            smallSizePreviewCoverImg.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        surfaceViewFixBefore43(smallSizePreviewLayout, largeSizePreviewLayout);
    }

    private void surfaceViewFixBefore43(ViewGroup front, ViewGroup back) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (back.getChildCount() > 0) {
                View child = back.getChildAt(0);
                back.removeView(child);
                back.addView(child);
            }

            if (front.getChildCount() > 0) {
                View child = front.getChildAt(0);
                front.removeView(child);
                front.addView(child);
            }
        }
    }

    /**
     * ********************** surface 初始化 **********************
     */
    private void findSurfaceView() {
        if (surfaceInit) {
            return;
        }
        View surfaceView = findViewById(R.id.avchat_surface_layout);
        if (surfaceView != null) {
            touchLayout = surfaceView.findViewById(R.id.touch_zone);
            touchLayout.setOnTouchListener(touchListener);

            smallSizePreviewFrameLayout = surfaceView.findViewById(R.id.small_size_preview_layout);
            smallSizePreviewLayout = surfaceView.findViewById(R.id.small_size_preview);
            smallSizePreviewCoverImg = surfaceView.findViewById(R.id.smallSizePreviewCoverImg);
            smallSizePreviewFrameLayout.setOnTouchListener(smallPreviewTouchListener);

            largeSizePreviewLayout = surfaceView.findViewById(R.id.large_size_preview);
            largeSizePreviewCoverLayout = surfaceView.findViewById(R.id.notificationLayout);
            txtVideoTime = surfaceView.findViewById(R.id.txt_video_time);
            cbChangeVoice = surfaceView.findViewById(R.id.cb_change_voice);
            cbConvertCamera = surfaceView.findViewById(R.id.cb_convert_camera);
            imgMinimizeVideo = surfaceView.findViewById(R.id.img_minimize_video);
            switchCameraToggle = new ToggleView(cbConvertCamera, ToggleState.OFF, this);
            surfaceInit = true;
        }
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP && touchZoneCallback != null) {
                touchZoneCallback.onTouch();
            }

            return true;
        }
    };

    private View.OnTouchListener smallPreviewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = x;
                    lastY = y;
                    int[] p = new int[2];
                    smallSizePreviewFrameLayout.getLocationOnScreen(p);
                    inX = x - p[0];
                    inY = y - p[1];

                    break;
                case MotionEvent.ACTION_MOVE:
                    final int diff = Math.max(Math.abs(lastX - x), Math.abs(lastY - y));
                    if (diff < TOUCH_SLOP)
                        break;

                    if (paddingRect == null) {
                        paddingRect = new Rect(ScreenUtil.dip2px(10), ScreenUtil.dip2px(20), ScreenUtil.dip2px(10),
                                ScreenUtil.dip2px(70));
                    }

                    int destX, destY;
                    if (x - inX <= paddingRect.left) {
                        destX = paddingRect.left;
                    } else if (x - inX + v.getWidth() >= ScreenUtil.screenWidth - paddingRect.right) {
                        destX = ScreenUtil.screenWidth - v.getWidth() - paddingRect.right;
                    } else {
                        destX = x - inX;
                    }

                    if (y - inY <= paddingRect.top) {
                        destY = paddingRect.top;
                    } else if (y - inY + v.getHeight() >= ScreenUtil.screenHeight - paddingRect.bottom) {
                        destY = ScreenUtil.screenHeight - v.getHeight() - paddingRect.bottom;
                    } else {
                        destY = y - inY;
                    }

                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.getLayoutParams();
                    params.gravity = Gravity.NO_GRAVITY;
                    params.leftMargin = destX;
                    params.topMargin = destY;
                    v.setLayoutParams(params);

                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.max(Math.abs(lastX - x), Math.abs(lastY - y)) <= 5) {
                        if (largeAccount == null || smallAccount == null) {
                            return true;
                        }
                        String temp;
                        switchRender(smallAccount, largeAccount);
                        temp = largeAccount;
                        largeAccount = smallAccount;
                        smallAccount = temp;
                        switchAndSetLayout();
                    }

                    break;
            }

            return true;
        }
    };

    /**
     * 大图像surface view 初始化
     */
    public void initLargeSurfaceView(String account) {
        // 设置画布，加入到自己的布局中，用于呈现视频图像
        // account 要显示视频的用户帐号
        largeAccount = account;
        if (!mIsInComingCall) {// AVChatKit.getAccount()
//            viewVideo.setVisibility(View.INVISIBLE);
            AVChatManager.getInstance().setupLocalVideoRender(largeRender, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
        } else {
//            viewVideo.setVisibility(View.VISIBLE);
            Log.i(TAG, "account：" + account);
            AVChatManager.getInstance().setupRemoteVideoRender(account, largeRender, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
        }
        addIntoLargeSizePreviewLayout(largeRender);
        remoteRender = largeRender;
    }

    /**
     * 小图像surface view 初始化
     */
    public void initSmallSurfaceView() {
        smallAccount = account;
        smallSizePreviewFrameLayout.setVisibility(View.VISIBLE);

        // 设置画布，加入到自己的布局中，用于呈现视频图像
        AVChatManager.getInstance().setupLocalVideoRender(null, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
        AVChatManager.getInstance().setupLocalVideoRender(smallRender, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
        addIntoSmallSizePreviewLayout(smallRender);

        smallSizePreviewFrameLayout.bringToFront();
        localRender = smallRender;
        localPreviewInSmallSize = true;
    }

    /**
     * 大图像surface添加到largeSizePreviewLayout
     *
     * @param surfaceView
     */
    private void addIntoLargeSizePreviewLayout(SurfaceView surfaceView) {
        if (surfaceView.getParent() != null) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        }
        largeSizePreviewLayout.removeAllViews();
        largeSizePreviewLayout.addView(surfaceView);
        surfaceView.setZOrderMediaOverlay(false);
        largeSizePreviewCoverLayout.setVisibility(View.GONE);
    }

    /**
     * 小图像surface添加到smallSizePreviewLayout
     *
     * @param surfaceView
     */
    private void addIntoSmallSizePreviewLayout(SurfaceView surfaceView) {
        smallSizePreviewCoverImg.setVisibility(View.GONE);
        if (surfaceView.getParent() != null) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        }
        smallSizePreviewLayout.removeAllViews();
        smallSizePreviewLayout.addView(surfaceView);
        surfaceView.setZOrderMediaOverlay(true);
        smallSizePreviewLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 大小图像显示切换
     *
     * @param user1
     * @param user2
     */
    private void switchRender(String user1, String user2) {
        String remoteId = TextUtils.equals(user1, AVChatProfile.getAccount()) ? user2 : user1;

        if (remoteRender == null && localRender == null) {
            localRender = smallRender;
            remoteRender = largeRender;
        }

        //交换
        IVideoRender render = localRender;
        localRender = remoteRender;
        remoteRender = render;


        //断开SDK视频绘制画布
        AVChatManager.getInstance().setupLocalVideoRender(null, false, 0);
        AVChatManager.getInstance().setupRemoteVideoRender(remoteId, null, false, 0);

        //重新关联上画布
        AVChatManager.getInstance().setupLocalVideoRender(localRender, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
        AVChatManager.getInstance().setupRemoteVideoRender(remoteId, remoteRender, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);

    }

    /**
     * 摄像头切换时，布局显隐
     */
    private void switchAndSetLayout() {
        localPreviewInSmallSize = !localPreviewInSmallSize;
        largeSizePreviewCoverLayout.setVisibility(View.GONE);
        smallSizePreviewCoverImg.setVisibility(View.GONE);
        if (isPeerVideoOff) {
            peerVideoOff();
        }
        if (isLocalVideoOff) {
            localVideoOff();
        }
    }

    /**
     * 本地关闭了摄像头
     */
    private void localVideoOff() {
        isLocalVideoOff = true;
        if (localPreviewInSmallSize)
            closeSmallSizePreview();
        else
            showNotificationLayout(LOCAL_CLOSE_CAMERA);
    }

    /**
     * 对方关闭了摄像头
     */
    public void peerVideoOff() {
        isPeerVideoOff = true;
        if (localPreviewInSmallSize) { //local preview in small size layout, then peer preview should in large size layout
            showNotificationLayout(PEER_CLOSE_CAMERA);
        } else {  // peer preview in small size layout
            closeSmallSizePreview();
        }
    }

    /**
     * 关闭小窗口
     */
    private void closeSmallSizePreview() {
        smallSizePreviewCoverImg.setVisibility(View.VISIBLE);
    }

    /**
     * 界面提示
     *
     * @param closeType
     */
    private void showNotificationLayout(int closeType) {
        if (largeSizePreviewCoverLayout == null) {
            return;
        }
        TextView textView = largeSizePreviewCoverLayout;
        switch (closeType) {
            case PEER_CLOSE_CAMERA:
                textView.setText(R.string.avchat_peer_close_camera);
                break;
            case LOCAL_CLOSE_CAMERA:
                textView.setText(R.string.avchat_local_close_camera);
                break;
            case AUDIO_TO_VIDEO_WAIT:
                textView.setText(R.string.avchat_audio_to_video_wait);
                break;
            default:
                return;
        }
        largeSizePreviewCoverLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 接听来电 告知服务器，以便通知其他端
     */
    private void receiveInComingCall() {
        mAVChatController.receiveInComingCall(avChatData.getChatId(), avChatData.getChatType(), avChatConfigs,
                new AVChatCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (!isFinishing()) {
                            mHandler.postDelayed(runnable, TIME);
                        }
                        onAudioAgree();
                    }

                    @Override
                    public void onFailed(int code) {
                        if (code == -1) {
                            Toast.makeText(VideoActivity.this, "本地音视频启动失败", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VideoActivity.this, "建立连接失败", Toast.LENGTH_SHORT).show();
                        }
                        handleAcceptFailed();
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        Toast.makeText(VideoActivity.this, "onException", Toast.LENGTH_LONG).show();
                        handleAcceptFailed();
                    }
                });
    }

    /**
     * 拨打音视频
     *
     * @param callTypeEnum
     * @param friend       通话接收人uid
     * @param roomId       网易房间id
     */
    private void outGoingCalling(final AVChatType callTypeEnum, Long friend, String roomId) {
        mAVChatController.outGoingCalling(mNeteaseaccId, callTypeEnum, largeRender, avChatConfigs, friend, roomId,
                new AVChatCallback<AVChatData>() {
                    @Override
                    public void onSuccess(AVChatData chatData) {
                        avChatData = chatData;

                        List<String> deniedPermissions = BaseMPermission.getDeniedPermissions(VideoActivity.this, BASIC_PERMISSIONS);
                        if (deniedPermissions != null && !deniedPermissions.isEmpty()) {
//                    showNoneCameraPermissionView(true);
                            return;
                        }
                        canSwitchCamera = true;
                        if (mAVChatType == AVChatType.VIDEO.getValue()) {
                            initLargeSurfaceView(avChatData.getAccount());
                        }
                        // 记录等待时间
                        if (!isFinishing()) {
                            mHandler.postDelayed(runnableWait, TIME);
                        }
//                        Toast.makeText(VoiceCallActivity.this, "onSuccess", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailed(int code) {
                        Toast.makeText(VideoActivity.this, AVChatExitCode.getCodeString(code), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        Toast.makeText(VideoActivity.this, "onException", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * 拒绝来电
     */
    private void handleAcceptFailed() {
        if (destroyRTC) {
            return;
        }
        if (avChatData.getChatType() == AVChatType.VIDEO) {
            AVChatManager.getInstance().stopVideoPreview();
            AVChatManager.getInstance().disableVideo();
        }
        AVChatManager.getInstance().disableRtc();
        finish();
        destroyRTC = true;
//        closeSessions(AVChatExitCode.CANCEL);
    }

    /**
     * 点对点语音发起(已完成)
     *
     * @param friend     通话接收人uid
     * @param type       通话类型(1:音频|2:视频)
     * @param roomId     网易房间id
     * @param aVChatType
     */
    public void auVideoDial(Long friend, int type, String roomId, AVChatType aVChatType) {
        new RunUtils(new RunUtils.Enent() {
            @Override
            public void onRun() {

            }

            @Override
            public void onMain() {
                mVideoAction.auVideoDial(friend, type, roomId, new CallBack<ReturnBean>() {
                    @Override
                    public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                        super.onResponse(call, response);
                        if (response.body() != null && response.body().isOk()) {
                            if (aVChatType == AVChatType.VIDEO) {
                                outGoingCalling(AVChatType.VIDEO, friend, roomId);
                            } else {
                                outGoingCalling(AVChatType.AUDIO, friend, roomId);
                            }
                        } else {
                            ToastUtil.show(VideoActivity.this, response.body().getMsg());
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnBean> call, Throwable t) {
                        super.onFailure(call, t);
                        ToastUtil.show(VideoActivity.this, t.getMessage());
                    }
                });
            }
        }).run();
    }

    /**
     * 音视频状态保活(已完成)
     *
     * @param roomId 网易房间id
     */
    public void auVideoKeepAlive(String roomId) {
        new RunUtils(new RunUtils.Enent() {
            @Override
            public void onRun() {

            }

            @Override
            public void onMain() {
                mVideoAction.auVideoKeepAlive(roomId, new CallBack<ReturnBean>() {
                    @Override
                    public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                        super.onResponse(call, response);
                    }

                    @Override
                    public void onFailure(Call<ReturnBean> call, Throwable t) {
                        super.onFailure(call, t);
                    }
                });
            }
        }).run();
    }
}

