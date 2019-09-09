package com.yanlong.im.chat.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MsgCancel extends RealmObject implements IMsgContent {
    public static final int MSG_TYPE_DEFAULT = 7897;
    @PrimaryKey
    private String msgid;
    private Long uid;
    private String note;
    private String msgidCancel;
    private Integer msgType = MSG_TYPE_DEFAULT;

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

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

    @Override
    public String getMsgId() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
}
