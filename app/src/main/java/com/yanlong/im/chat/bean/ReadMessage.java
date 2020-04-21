package com.yanlong.im.chat.bean;

/**
 * @author Liszt
 * @date 2020/4/21
 * Description
 */
public class ReadMessage implements IMsgContent {
    String msgId;
    long time;

    @Override
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
