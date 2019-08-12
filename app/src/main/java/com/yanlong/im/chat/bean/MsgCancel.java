package com.yanlong.im.chat.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MsgCancel extends RealmObject {
    @PrimaryKey
    private String msgid;
    private Long uid;
    private String note;
    private String msgidCancel;


    public String getMsgidCancel() {
        return msgidCancel;
    }

    public void setMsgidCancel(String msgidCancel) {
        this.msgidCancel = msgidCancel;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
}
