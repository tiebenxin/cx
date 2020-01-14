package net.cb.cb.library.bean;

import net.cb.cb.library.event.BaseEvent;


/**
 * @类名：截屏通知EventBus
 * @Date：2020/1/14
 * @by zjy
 * @备注：
 */

public class EventSendMsgScreenOpen extends BaseEvent {

    private int isOpen;

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }
}
