package com.yanlong.im.chat.bean;

import io.realm.RealmObject;

/**
 * @anthor Liszt
 * @data 2019/8/6
 * Description 小助手消息
 */
public class AssistantMessage extends RealmObject {
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
