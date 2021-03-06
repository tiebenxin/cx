package com.yanlong.im.chat.bean;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/***
 * 类型为0的
 * 通知消息
 */
public class MsgNotice extends RealmObject implements IMsgContent {
    public static final int MSG_TYPE_DEFAULT = 7897;

    @PrimaryKey
    private String msgid;
    private Long uid;
    private String note;
    private Integer msgType = MSG_TYPE_DEFAULT;
    private String remark;//申请入群备注
    private int joinGroupType;//申请入群方式  0 扫码 1 正常邀请
    private RealmList<String> ids;//申请入群aid，方便查询入群申请记录

    public Integer getMsgType() {
        return msgType;
    }

    //7,8,17为红包消息类型, 通知消息类型
    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getJoinGroupType() {
        return joinGroupType;
    }

    public void setJoinGroupType(int joinGroupType) {
        this.joinGroupType = joinGroupType;
    }

    public RealmList<String> getIds() {
        return ids;
    }

    public void setIds(RealmList<String> ids) {
        this.ids = ids;
    }
}
