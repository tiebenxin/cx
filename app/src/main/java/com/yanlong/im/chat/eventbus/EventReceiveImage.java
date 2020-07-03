package com.yanlong.im.chat.eventbus;

import net.cb.cb.library.event.BaseEvent;

/**
 * @author Liszt
 * @date 2020/7/2
 * Description
 */
public class EventReceiveImage extends BaseEvent {
    private String gid;
    private long toUid;

    public EventReceiveImage(String gid, long uid) {
        this.gid = gid;
        this.toUid = uid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public long getToUid() {
        return toUid;
    }

    public void setToUid(long toUid) {
        this.toUid = toUid;
    }
}
