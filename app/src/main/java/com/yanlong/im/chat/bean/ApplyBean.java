package com.yanlong.im.chat.bean;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.utils.StringUtil;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * author : zgd
 * date   : 2019/11/2610:27
 * 申请加好友 和 申请入群
 */

public class ApplyBean extends RealmObject {
    @PrimaryKey
    private String aid;//主键

    @CoreEnum.EChatType
    private int chatType = 0;//申请类似 个人 进群

    //个人
    private long uid; //
    private String nickname; // 昵称
    private String alias;   //好友备注
    private String avatar; // 头像
    private String sayHi;// 申请说明
    private int stat = 1; //好友状态 或 群状态 1申请 2同意 3拒绝 即隐藏删除
    private String phone;// 申请人手机号

    //群
    private String gid; // 群gid
    private String groupName; // 群名称
    private int joinType; //  加入类型 进群方式（0:扫码 1:被动）
    private long inviter; // 被要求人 id
    private String inviterName; // 邀请人昵称
    private long time; //


    public String getInviterName() {
        return inviterName;
    }

    public void setInviterName(String inviterName) {
        this.inviterName = inviterName;
    }

    public int getJoinType() {
        return joinType;
    }

    public void setJoinType(int joinType) {
        this.joinType = joinType;
    }

    public long getInviter() {
        return inviter;
    }

    public void setInviter(long inviter) {
        this.inviter = inviter;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGroupName() {
        groupName = groupName == null ? "" : groupName;
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getGid() {
        return gid == null ? "" : gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getSayHi() {
        return sayHi;
    }

    public void setSayHi(String sayHi) {
        this.sayHi = sayHi;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getNickname() {
        return nickname;// TODO　不能直接先取好友备注，新的申请列表显示会有问题，因为通讯匹配默认会设置好友备注
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof ApplyBean) {
            ApplyBean objBean = (ApplyBean) obj;
            if (TextUtils.isEmpty(aid) || TextUtils.isEmpty(objBean.getAid())) {
                return false;
            }
            if (aid.equals(objBean.getAid())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "ApplyBean{" +
                "aid='" + aid + '\'' +
                ", chatType=" + chatType +
                ", uid=" + uid +
                ", nickname='" + nickname + '\'' +
                ", alias='" + alias + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sayHi='" + sayHi + '\'' +
                ", stat=" + stat +
                ", gid='" + gid + '\'' +
                ", groupName='" + groupName + '\'' +
                ", joinType=" + joinType +
                ", inviter=" + inviter +
                ", inviterName='" + inviterName + '\'' +
                ", time=" + time +
                '}';
    }
}
