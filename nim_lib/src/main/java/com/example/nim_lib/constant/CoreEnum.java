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
}
