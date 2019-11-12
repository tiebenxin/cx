package net.cb.cb.library.event;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2019-11-04
 * @updateAuthor
 * @updateDate
 * @description 事件工厂
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class EventFactory extends BaseEvent {
    /**
     * 重启APP
     */
    public static class RestartAppEvent extends BaseEvent {

    }

    /**
     * 撤回消息关闭图片预览
     */
    public static class ClosePictureEvent extends BaseEvent {
        public String msg_id;
        public String name;
    }

    /**
     * 撤回消息关闭语音播放
     */
    public static class StopVoiceeEvent extends BaseEvent {
        public String msg_id;
    }
}
