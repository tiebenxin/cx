package com.yanlong.im.chat.bean;

/**
 * @anthor Liszt
 * @data 2019/8/19
 * Description  滚动位置config
 */
public class ScrollConfig {
    long userId;//当前登录用户
    long uid;//当前单聊uid
    String chatId;//当前群聊id
    int lastPosition;
    int lastOffset;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    public int getLastOffset() {
        return lastOffset;
    }

    public void setLastOffset(int lastOffset) {
        this.lastOffset = lastOffset;
    }
}