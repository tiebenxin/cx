package com.yanlong.im.user.bean;

import com.yanlong.im.chat.ChatEnum;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 收藏消息结构 + 本地收藏列表
 */
public class CollectionInfo extends RealmObject {
    @PrimaryKey
    private String msgId;//消息id
    private String createTime;//收藏时间
    private String data;//具体消息，JSON数据 TODO 改为最新的类
    private String fromGid;//来源：群组id
    private String fromGroupName;//来源：群组名称
    private long fromUid;//来源：用户id
    private String fromUsername;//来源：用户名称
    private long id;//数据id
    @ChatEnum.EMessageType
    private int type;//消息类型

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFromGid() {
        return fromGid;
    }

    public void setFromGid(String fromGid) {
        this.fromGid = fromGid;
    }

    public String getFromGroupName() {
        return fromGroupName;
    }

    public void setFromGroupName(String fromGroupName) {
        this.fromGroupName = fromGroupName;
    }

    public long getFromUid() {
        return fromUid;
    }

    public void setFromUid(long fromUid) {
        this.fromUid = fromUid;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
