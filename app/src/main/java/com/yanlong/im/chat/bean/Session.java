package com.yanlong.im.chat.bean;

import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.dao.UserDao;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/***
 * 会话
 */
public class Session extends RealmObject {
    @PrimaryKey
    private String sid;
    //会话类型,0:单人,1群
    private int type = 0;
    private Long from_uid;
    private String gid;
    private Long up_time;
    private int unread_count = 0;
    //草稿
    private String draft;
    //是否置顶
    private int isTop = 0;
    //是否静音，是，免打扰
    private int isMute = 0;
    //  0.单个人 1.所有人 2.草稿
    private int messageType = 1000;

    private String atMessage;

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getAtMessage() {
        return atMessage;
    }

    public void setAtMessage(String atMessage) {
        this.atMessage = atMessage;
    }

    public String getDraft() {
        return draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }

    public int getIsMute() {
        // int isMute =0;

        try {
            isMute = type == 0 ? new UserDao().findUserInfo(from_uid).getDisturb() : new MsgDao().getGroup4Id(gid).getNotNotify();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isMute;
    }

    public void setIsMute(int isMute) {
        this.isMute = isMute;
    }

    public int getIsTop() {
        // int isTop=0;
        try {
            isTop = type == 0 ? new UserDao().findUserInfo(from_uid).getIstop() : new MsgDao().getGroup4Id(gid).getIsTop();
        } catch (Exception e) {
            //  e.printStackTrace();
        }

        return isTop;
    }

    public void setIsTop(int isTop) {
        this.isTop = isTop;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getFrom_uid() {
        return from_uid == null ? -1 : from_uid;
    }

    public void setFrom_uid(Long from_uid) {
        this.from_uid = from_uid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public Long getUp_time() {
        return up_time;
    }

    public void setUp_time(Long up_time) {
        this.up_time = up_time;
    }

    public int getUnread_count() {
        return unread_count;
    }

    public void setUnread_count(int unread_count) {
        this.unread_count = unread_count;
    }
}
