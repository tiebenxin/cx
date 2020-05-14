package com.yanlong.im.chat.bean;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 收藏->文字实体类
 */
public class CollectChatMessage extends RealmObject implements IMsgContent {
    @PrimaryKey
    private String msgId;

    private String msg; // 消息内容

    public CollectChatMessage() {

    }

    @Override
    public String getMsgId() {
        return msgId;
    }

    public void setMsgid(String msgId) {
        this.msgId = msgId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
