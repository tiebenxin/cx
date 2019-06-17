package com.yanlong.im.chat.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class GroupAccept extends RealmObject {
    @PrimaryKey
    private String aid;
    private Long uid;
    private String uname;
    private String head;
    private String gid;
    private String groupName;

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getGroupName() {
        groupName=groupName==null?"":groupName;
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }
}