package com.yanlong.im.chat.eventbus;

import net.cb.cb.library.event.BaseEvent;

/**
 * @author Liszt
 * @date 2020/1/15
 * Description
 */
public class EventSwitchSnapshot extends BaseEvent {
    int flag;
    long uid;
    String gid;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }
}
