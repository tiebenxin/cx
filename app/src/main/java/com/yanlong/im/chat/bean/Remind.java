package com.yanlong.im.chat.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/***
 * 红点通知
 */
public class Remind extends RealmObject {
    @PrimaryKey
    private String remid_type;//friend_apply:申请包括群和好友,me_update:系统更新
    private Integer number;
    private long uid;// 需要根据uid来判断是显示新的申请红点

    public String getRemid_type() {
        return remid_type;
    }

    public void setRemid_type(String remid_type) {
        this.remid_type = remid_type;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }
}
