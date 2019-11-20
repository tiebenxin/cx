package com.yanlong.im.chat.bean;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @anthor Liszt
 * @data 2019/8/6
 * Description 小助手消息
 */
public class AssistantMessage extends RealmObject implements IMsgContent, Serializable {
    @PrimaryKey
    private String msgId;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
