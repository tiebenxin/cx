package com.yanlong.im.chat.bean;

import com.yanlong.im.user.bean.UserInfo;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/***
 * 群
 */
public class Group  extends RealmObject {
    @PrimaryKey
    private String gid;
    private RealmList<UserInfo> users;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public RealmList<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(RealmList<UserInfo> users) {
        this.users = users;
    }
}
