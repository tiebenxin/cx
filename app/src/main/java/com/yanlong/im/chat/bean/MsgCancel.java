package com.yanlong.im.chat.bean;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class MsgCancel extends RealmObject implements IMsgContent {
    public static final int MSG_TYPE_DEFAULT = 7897;
    @PrimaryKey
    private String msgid;
    private Long uid;//被撤回人的uid(原来就有这个字段，证实一直没使用)
    private String note;
    private String msgidCancel;//被撤回消息的id
    private String cancelContent;// 撤回内容
    private Integer cancelContentType;// 撤回内容类型
    private Integer msgType = MSG_TYPE_DEFAULT;
    private int role;//1群主 2群管理
    private String alterantive_name;// 被撤回人的昵称

    @Ignore
    private long time;//源撤销消息时间

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public String getCancelContent() {
        return cancelContent;
    }

    public void setCancelContent(String cancelContent) {
        this.cancelContent = cancelContent;
    }

    public Integer getCancelContentType() {
        return cancelContentType;
    }

    public void setCancelContentType(Integer cancelContentType) {
        this.cancelContentType = cancelContentType;
    }

    public String getMsgidCancel() {
        return msgidCancel;
    }

    public void setMsgidCancel(String msgidCancel) {
        this.msgidCancel = msgidCancel;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String getMsgId() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getAlterantive_name() {
        return alterantive_name;
    }

    public void setAlterantive_name(String alterantive_name) {
        this.alterantive_name = alterantive_name;
    }
}
