package com.yanlong.im.chat.eventbus;

import net.cb.cb.library.event.BaseEvent;

/**
 * @author Liszt
 * @date 2020/3/3
 * Description 收到ack，event
 */
public class AckEvent<T> extends BaseEvent {
    private T t;

    public T getData() {
        return t;
    }

    public void setData(T t) {
        this.t = t;
    }
}
