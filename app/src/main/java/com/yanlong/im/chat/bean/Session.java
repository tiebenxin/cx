package com.yanlong.im.chat.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/***
 * 会话
 */
public class Session extends RealmObject {
    @PrimaryKey
    private String sid;
    //会话类型,0:单人,1群
    private int type=0;
    private Long from_uid;
    private String gid;
    private Long up_time;
    private int unread_count=0;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getFrom_uid() {
        return from_uid;
    }

    public void setFrom_uid(Long from_uid) {
        this.from_uid = from_uid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public Long getUp_time() {
        return up_time;
    }

    public void setUp_time(Long up_time) {
        this.up_time = up_time;
    }

    public int getUnread_count() {
        return unread_count;
    }

    public void setUnread_count(int unread_count) {
        this.unread_count = unread_count;
    }
}
