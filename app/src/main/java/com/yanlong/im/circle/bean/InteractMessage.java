package com.yanlong.im.circle.bean;

import com.yanlong.im.chat.bean.IMsgContent;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


/**
 * @类名：朋友圈互动消息实体类
 * @Date：2020/10/12
 * @by zjy
 * @备注：
 */
public class InteractMessage extends RealmObject implements IMsgContent {
    @PrimaryKey
    private String msgId;
    private long momentId;//说说id
    private long momentUid;//说说发布人uid
    private long interactId;//互动id
    private String resource;//资源
    private String content;//内容
    private int interactType;//动作类型 0关注 1点赞 2评论 3回复 4投票 5删除评论
    private int resourceType;//资源类型 0文本 1图片 2语音 3视频

    //本地新增字段
    private boolean hadRead = false;//是否已读
    private boolean greyColor= false;//点击后，是否置灰
    private String avatar;//谁的头像(对我操作)
    private String nickname;//谁的昵称(对我操作)
    private long fromUid;//谁的uid(对我操作)
    private long timeStamp;//时间戳

    @Override
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public long getMomentId() {
        return momentId;
    }

    public void setMomentId(long momentId) {
        this.momentId = momentId;
    }

    public long getMomentUid() {
        return momentUid;
    }

    public void setMomentUid(long momentUid) {
        this.momentUid = momentUid;
    }

    public long getInteractId() {
        return interactId;
    }

    public void setInteractId(long interactId) {
        this.interactId = interactId;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getInteractType() {
        return interactType;
    }

    public void setInteractType(int interactType) {
        this.interactType = interactType;
    }

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public boolean isHadRead() {
        return hadRead;
    }

    public void setHadRead(boolean hadRead) {
        this.hadRead = hadRead;
    }

    public boolean isGreyColor() {
        return greyColor;
    }

    public void setGreyColor(boolean greyColor) {
        this.greyColor = greyColor;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getFromUid() {
        return fromUid;
    }

    public void setFromUid(long fromUid) {
        this.fromUid = fromUid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
