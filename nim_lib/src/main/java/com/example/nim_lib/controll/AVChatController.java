package com.example.nim_lib.controll;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.nim_lib.config.AVChatConfigs;
import com.example.nim_lib.constant.AVChatExitCode;
import com.example.nim_lib.module.AVSwitchListener;
import com.example.nim_lib.util.LogUtil;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatControlCommand;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatNotifyOption;
import com.netease.nimlib.sdk.avchat.video.AVChatCameraCapturer;
import com.netease.nimlib.sdk.avchat.video.AVChatSurfaceViewRenderer;
import com.netease.nimlib.sdk.avchat.video.AVChatVideoCapturerFactory;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2019-10-18
 * @updateAuthor
 * @updateDate
 * @description 音视频控制器：用于实现音视频拨打接听，音视频切换的具体功能实现
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class AVChatController {

    private Context context;
    private final String TAG = AVChatController.class.getName();

    public AVChatController(Context c) {
        context = c;
    }

    /**
     * 挂断
     *
     * @param chatId     网易ID
     * @param type
     * @param avChatType AVChatType.VIDEO AVChatType.AUDIO\
     */
    public void hangUp2(long chatId, int type, AVChatType avChatType) {
        if ((type == AVChatExitCode.HANGUP || type == AVChatExitCode.PEER_NO_RESPONSE
                || type == AVChatExitCode.CANCEL || type == AVChatExitCode.REJECT)) {
            AVChatManager.getInstance().hangUp2(chatId, new AVChatCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    AVChatProfile.getInstance().setAVChatting(false);
                    AVChatSoundPlayer.instance(context).stop();
                    if (context != null && !((Activity) context).isFinishing()) {
                        ((Activity) context).finish();
                    }
                    Log.d(TAG, "onSuccess");
                }

                @Override
                public void onFailed(int i) {
                    Log.d(TAG, "onSuccess");
                }

                @Override
                public void onException(Throwable throwable) {
                    Log.d(TAG, "throwable");
                }
            });
        }
        if (avChatType == AVChatType.VIDEO) {
            // 如果是视频通话，关闭视频模块
            AVChatManager.getInstance().disableVideo();
            // 如果是视频通话，需要先关闭本地预览
            AVChatManager.getInstance().stopVideoPreview();
        }
        //销毁音视频引擎和释放资源
        AVChatManager.getInstance().disableRtc();
    }

    /**
     * 拨打音视频
     *
     * @param account       网易ID
     * @param callTypeEnum  VIDEO、VOICE
     * @param videoCapturer
     * @param largeRender
     * @param avChatConfigs
     * @param callBack
     */
    public void outGoingCalling(String account, final AVChatType callTypeEnum, AVChatCameraCapturer videoCapturer,
                                AVChatSurfaceViewRenderer largeRender, AVChatConfigs avChatConfigs, AVChatCallback<AVChatData> callBack) {
        AVChatNotifyOption notifyOption = new AVChatNotifyOption();
        // 附加字段
        notifyOption.extendMessage = "extra_data";
        // 是否兼容WebRTC模式
//        notifyOption.webRTCCompat = webrtcCompat;
//        //默认forceKeepCalling为true，开发者如果不需要离线持续呼叫功能可以将forceKeepCalling设为false
//        notifyOption.forceKeepCalling = false;
        // 开启音视频引擎
        AVChatManager.getInstance().enableRtc();

        if (avChatConfigs == null) {
            avChatConfigs = new AVChatConfigs(context);
            // 设置自己需要的可选参数
            AVChatManager.getInstance().setParameters(avChatConfigs.getAvChatParameters());
        }
        // 视频通话
        if (callTypeEnum == AVChatType.VIDEO) {
            // 激活视频模块
            AVChatManager.getInstance().enableVideo();

            // 创建视频采集模块并且设置到系统中
            if (videoCapturer == null) {
                videoCapturer = AVChatVideoCapturerFactory.createCameraCapturer(true, true);
                AVChatManager.getInstance().setupVideoCapturer(videoCapturer);
            }

            if (largeRender == null) {
                largeRender = new AVChatSurfaceViewRenderer(context);
                // 设置本地预览画布
                AVChatManager.getInstance().setupLocalVideoRender(largeRender, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
            }

            // 开始视频预览
            AVChatManager.getInstance().startVideoPreview();
        }
        // 呼叫
        AVChatManager.getInstance().call2(account, callTypeEnum, notifyOption, callBack);
    }

    /**
     * 接听来电 告知服务器，以便通知其他端
     *
     * @param chatId        网易ID
     * @param callTypeEnum  VIDEO、VOICE
     * @param videoCapturer
     * @param avChatConfigs
     * @param callback
     */
    public void receiveInComingCall(long chatId, final AVChatType callTypeEnum, AVChatCameraCapturer videoCapturer,
                                    AVChatConfigs avChatConfigs, AVChatCallback<Void> callback) {
        // 开启音视频引擎
        AVChatManager.getInstance().enableRtc();
        if (videoCapturer == null) {
            videoCapturer = AVChatVideoCapturerFactory.createCameraCapturer(true, true);
            AVChatManager.getInstance().setupVideoCapturer(videoCapturer);
        }
        if (avChatConfigs == null) {
            avChatConfigs = new AVChatConfigs(context);
            //设置自己需要的可选参数
            AVChatManager.getInstance().setParameters(avChatConfigs.getAvChatParameters());
        }

        if (callTypeEnum == AVChatType.VIDEO) {
            // 激活视频模块
            AVChatManager.getInstance().enableVideo();
            // 开启视频预览
            AVChatManager.getInstance().startVideoPreview();
        }

        AVChatManager.getInstance().accept2(chatId, callback);
    }

    /**
     * 拒绝来电
     *
     * @param avChatType VIDEO、VOICE
     */
    public void handleAcceptFailed(AVChatType avChatType) {
        if (avChatType == AVChatType.VIDEO) {
            AVChatManager.getInstance().stopVideoPreview();
            AVChatManager.getInstance().disableVideo();
        }
        AVChatManager.getInstance().disableRtc();
        if (!((Activity) context).isFinishing()) {
            ((Activity) context).finish();
        }
//        closeSessions(AVChatExitCode.CANCEL);
    }

    /**
     * 设置扬声器是否开启
     */
    public void toggleSpeaker() {
        AVChatManager.getInstance().setSpeaker(!AVChatManager.getInstance().speakerEnabled());
    }

    /**
     * 音频开关
     */
    public void toggleMute() {
        if (!AVChatManager.getInstance().isLocalAudioMuted()) { // isMute是否处于静音状态
            // 关闭音频
            AVChatManager.getInstance().muteLocalAudio(true);
        } else {
            // 打开音频
            AVChatManager.getInstance().muteLocalAudio(false);
        }
    }

    /**
     * ********************* 音视频切换 ***********************
     */

    /**
     * 发送视频切换为音频命令
     *
     * @param chatId
     * @param avSwitchListener
     */
    public void switchVideoToAudio(long chatId, final AVSwitchListener avSwitchListener) {
        AVChatManager.getInstance().sendControlCommand(chatId, AVChatControlCommand.SWITCH_VIDEO_TO_AUDIO, new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                LogUtil.getLog().d(TAG, "videoSwitchAudio onSuccess");
                //关闭视频
                AVChatManager.getInstance().stopVideoPreview();
                AVChatManager.getInstance().disableVideo();

                // 界面布局切换。
                avSwitchListener.onVideoToAudio();
            }

            @Override
            public void onFailed(int code) {
                LogUtil.getLog().d(TAG, "videoSwitchAudio onFailed");
            }

            @Override
            public void onException(Throwable exception) {
                LogUtil.getLog().d(TAG, "videoSwitchAudio onException");
            }
        });
    }

}
