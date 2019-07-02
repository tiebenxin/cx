package com.yanlong.im.chat.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class VoiceMessage extends RealmObject {
    @PrimaryKey
    private String msgid;
    private String url;
    private int time;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
}
