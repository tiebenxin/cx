package com.yanlong.im.chat.bean;

import com.yanlong.im.chat.ChatEnum;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Liszt
 * @date 2020/5/7
 * Description 被回复引用的消息内容
 */
public class QuotedMessage extends RealmObject implements IMsgContent {
    @PrimaryKey
    String msgId;
    long timestamp;
    @ChatEnum.EMessageType
    int msgType;
    long fromUid;//发送者id
    String nickName;//发送者昵称
    String avatar;//发送者头像
    String url;//视频或者图片缩略图
    String msg;//文字内容

    @Override
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getFromUid() {
        return fromUid;
    }

    public void setFromUid(long fromUid) {
        this.fromUid = fromUid;
    }
}
