package com.yanlong.im.chat.eventbus;

import net.cb.cb.library.event.BaseEvent;

/**
 * @类名：消息同步通知
 * @Date：2020/4/7
 * @by zjy
 * @备注：
 */
public class EventMsgSync extends BaseEvent {

    public EventMsgSync(String code) {
        this.code = code;
    }

    String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
