package com.yanlong.im.chat.bean;


import io.realm.RealmObject;

public class ChatMessage extends RealmObject {

    private String msg; // 消息内容

    public ChatMessage() {

    }




    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }




}
