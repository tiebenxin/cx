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
    private String name;
    private String avatar;

    private String master;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }
}
