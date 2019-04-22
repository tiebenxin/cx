package com.yanlong.im.chat.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ChatMessage {
    @Id
    private String mid;
    private String msg; // 消息内容

    public ChatMessage() {
    }

    @Generated(hash = 2078218857)
    public ChatMessage(String mid, String msg) {
        this.mid = mid;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMid() {
        return this.mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }


}
