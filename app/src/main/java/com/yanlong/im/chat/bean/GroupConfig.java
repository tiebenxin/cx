package com.yanlong.im.chat.bean;

import io.realm.Realm;
import io.realm.RealmObject;

/***
 * 群配置
 */
public class GroupConfig extends RealmObject {
    private String gid;
    //群解散
    private int isExit;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public int getIsExit() {
        return isExit;
    }

    public void setIsExit(int isExit) {
        this.isExit = isExit;
    }
}
