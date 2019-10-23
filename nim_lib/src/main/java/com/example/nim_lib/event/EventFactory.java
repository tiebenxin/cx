package com.example.nim_lib.event;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2019-10-12
 * @updateAuthor
 * @updateDate
 * @description 事件工厂
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class EventFactory {
    /**
     * 语音最小化
     */
    public static class VoiceMinimizeEvent extends BaseEvent {
        public int passedTime;
        public String showTime;
        public boolean isCallEstablished;// 是否接听
    }

    /**
     * 语音最小化
     */
    public static class CloseVoiceMinimizeEvent extends BaseEvent {
        public String operation;// 操作(cancel|hangup|reject)
        public String txt;// 操作加时长
        public int avChatType;// 语音、视频
    }
}
