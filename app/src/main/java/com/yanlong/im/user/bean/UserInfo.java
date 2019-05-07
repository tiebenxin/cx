package com.yanlong.im.user.bean;

import com.yanlong.im.chat.bean.MsgAllBean;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserInfo extends RealmObject {
    @PrimaryKey
    private Long uid;
    private String name;
    private String head;
    private String mkName;
 //   private RealmList<MsgAllBean> msgs;

    //用户类型 0:陌生人或者群友,1:自己,2:通讯录
    private Integer uType;

    public Integer getuType() {
        return uType;
    }

    public void setuType(Integer uType) {
        this.uType = uType;
    }


    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getMkName() {
        return mkName;
    }

    public void setMkName(String mkName) {
        this.mkName = mkName;
    }
}
