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
        public int type;
        public int passedTime;
        public String showTime;
        public boolean isCallEstablished;// 是否接听
    }

    /**
     * 关闭语音最小化并发送一条消息
     */
    public static class CloseVoiceMinimizeEvent extends BaseEvent {
        public String operation;// 操作(cancel|hangup|reject)
        public String txt;// 操作加时长
        public int avChatType;// 语音、视频
        public Long toUId;
        public String toGid;
    }

    /**
     * 关闭语音最小化
     */
    public static class CloseMinimizeEvent extends BaseEvent {
    }

    /**
     * 发送一条通知
     */
    public static class SendP2PAuVideoDialMessage extends BaseEvent {
        public int avChatType;// 语音、视频
        public Long toUId;
        public String toGid;
    }
}
