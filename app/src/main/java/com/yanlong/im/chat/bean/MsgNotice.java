package com.yanlong.im.chat.bean;

import io.realm.RealmObject;

/***
 * 类型为0的
 * 通知消息
 */
public class MsgNotice extends RealmObject {
    private Long uid;
    private String note;

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
}
