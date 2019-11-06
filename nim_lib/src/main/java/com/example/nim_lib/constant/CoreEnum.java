package com.example.nim_lib.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*
 * 常量封装类，类似于枚举
 * */
public class CoreEnum {

    /*
     *语音通话类型
     * */
    @IntDef({VoiceType.WAIT, VoiceType.RECEIVE, VoiceType.CALLING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VoiceType {
        int WAIT = 0; // 呼叫
        int RECEIVE = 1; // 接收
        int CALLING = 2; // 通话
    }

    @IntDef({ENetStatus.SUCCESS_ON_NET, ENetStatus.ERROR_ON_NET, ENetStatus.SUCCESS_ON_SERVER, ENetStatus.ERROR_ON_SERVER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ENetStatus {
        int SUCCESS_ON_NET = 0;//本地网络正常
        int ERROR_ON_NET = 1;//本地网络错误
        int SUCCESS_ON_SERVER = 2;//服务器正常
        int ERROR_ON_SERVER = 3;//服务器错误
    }
}
