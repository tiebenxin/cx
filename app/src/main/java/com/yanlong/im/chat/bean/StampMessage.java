package com.yanlong.im.chat.bean;


import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class StampMessage extends RealmObject implements IMsgContent, Serializable {
    @PrimaryKey
    private String msgid;
    private String comment;


    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String getMsgId() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
}
