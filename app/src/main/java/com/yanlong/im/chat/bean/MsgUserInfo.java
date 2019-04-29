package com.yanlong.im.chat.bean;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MsgUserInfo extends RealmObject {
    @PrimaryKey
    private Long uid;
    private String name;
    private String head;
    private String mkName;
    private RealmList<MsgAllBean> msgs;

    public RealmList<MsgAllBean> getMsgs() {
        return msgs;
    }

    public void setMsgs(RealmList<MsgAllBean> msgs) {
        this.msgs = msgs;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getMkName() {
        return mkName;
    }

    public void setMkName(String mkName) {
        this.mkName = mkName;
    }
}
