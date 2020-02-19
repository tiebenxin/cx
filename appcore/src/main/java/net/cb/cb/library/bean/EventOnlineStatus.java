package net.cb.cb.library.bean;

import net.cb.cb.library.event.BaseEvent;

/**
 * @author Liszt
 * @date 2020/2/17
 * Description
 */
public class EventOnlineStatus extends BaseEvent {
    boolean isOn;

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }
}
