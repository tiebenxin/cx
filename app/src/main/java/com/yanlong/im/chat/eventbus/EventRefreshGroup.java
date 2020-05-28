package com.yanlong.im.chat.eventbus;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/25 0025
 * @description
 */
public class EventRefreshGroup {
    String gid = null;

    public String getGid() {
        return gid == null? "" : gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }
}
