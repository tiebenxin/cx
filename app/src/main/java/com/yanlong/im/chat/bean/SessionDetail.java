package com.yanlong.im.chat.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/3/27 0027
 * @description
 */
public class SessionDetail extends RealmObject{
    @PrimaryKey
    private String sid;
    private String name; //session名字，群聊即群名，私聊即好友备注或昵称
    private String avatar;//头像
    private String avatarList;//群头像
    private MsgAllBean message;//最后消息
    private String senderName; //发送者名字，群聊
    private String messageContent;//最新消息

    public String getMessageContent() {
        return messageContent==null?"":messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatarList() {
        return avatarList;
    }

    public void setAvatarList(String avatarList) {
        this.avatarList = avatarList;
    }

    public MsgAllBean getMessage() {
        return message;
    }

    public void setMessage(MsgAllBean message) {
        this.message = message;
    }
}
