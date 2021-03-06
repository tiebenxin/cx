package com.yanlong.im.chat.bean;

import androidx.annotation.Nullable;

import net.cb.cb.library.manager.Constants;
import net.cb.cb.library.utils.LogUtil;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/***
 * 会话
 */
public class Session extends RealmObject implements Comparable<Session> {
    @PrimaryKey
    private String sid;
    //会话类型,0:单人,1群
    private int type = 0;
    private Long from_uid;
    private String gid;
    private Long up_time = 0L;//更新时间
    private int unread_count = 0;
    //草稿
    private String draft;
    //是否置顶
    private int isTop = 0;
    //是否静音，是，免打扰
    private int isMute = 0;
    // 1000普通消息  0.@我单个人 1.@所有人 2.草稿 3 红包发送失败 4、申请进群通知
    private int messageType = 1000;

    private String atMessage;

    @Ignore
    private Boolean isSelect = false;//本地字段

    private int markRead = 0;//标记已读0，未读1

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
        return isMute;
    }

    public void setIsMute(int isMute) {
        this.isMute = isMute;
    }

    public int getIsTop() {
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
        return up_time == null ? 0 : up_time;
    }

    public void setUp_time(Long up_time) {
        this.up_time = up_time;
    }

    public int getUnread_count() {
        return unread_count;
    }

    public void setUnread_count(int unread_count) {
//        LogUtil.getLog().i("未读数", "设置未读数count=" + unread_count + "--gid=" + gid + "--uid=" + from_uid);
        this.unread_count = unread_count;
    }


    public void setName(String name) {

    }


    public void setMessage(MsgAllBean message) {

    }


    public void setAvatar(String avatar) {
    }


    public void setSenderName(String senderName) {
    }

    public Boolean getSelect() {
        return isSelect;
    }

    public void setSelect(Boolean select) {
        isSelect = select;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Session) {
            if (((Session) obj).getType() == 1) {
                if (((Session) obj).getGid().equals(this.gid)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (((Session) obj).getFrom_uid() != null && from_uid != null) {
                    if (((Session) obj).getFrom_uid().equals(this.from_uid)) {//Long 类型不能用==
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public int compareTo(Session o) {
        if (o != null && o.up_time != null && this != null && this.up_time != null) {
            //降序
            if (o.up_time.longValue() > this.up_time.longValue()) {
                return 1;
            } else if (o.up_time.longValue() < this.up_time.longValue()) {
                return -1;
            } else {
                return 0;
            }
        } else if (o == null || o.up_time == null) {
            return -1;
        } else if (this == null || this.up_time == null) {
            return 1;
        } else {
            return 0;
        }
    }

    public int getMarkRead() {
        return markRead;
    }

    public void setMarkRead(int read) {
        this.markRead = read;
    }

    //判断该用户是否官方系统用户
    public boolean isSystemUser() {
        if (type != 0 || from_uid == null) {
            return false;
        }
        if (from_uid.equals(Constants.CX888_UID) || from_uid.equals(Constants.CX999_UID) || from_uid.equals(Constants.CX_HELPER_UID) || from_uid.equals(Constants.CX_BALANCE_UID)) {
            return true;
        }
        return false;
    }
}
