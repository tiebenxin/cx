package com.yanlong.im.chat.eventbus;

import net.cb.cb.library.event.BaseEvent;

/**
 * @类名：通用展示弹框触发事件
 * @Date：2020/9/15
 * @by zjy
 * @备注：     type 1 你没有权限
 */
public class EventShowDialog extends BaseEvent {

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
