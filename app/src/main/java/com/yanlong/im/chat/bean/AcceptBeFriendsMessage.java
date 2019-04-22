package com.yanlong.im.chat.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class AcceptBeFriendsMessage {
    @Id
    private String mid;

    @Generated(hash = 1692509767)
    public AcceptBeFriendsMessage(String mid) {
        this.mid = mid;
    }

    @Generated(hash = 9561989)
    public AcceptBeFriendsMessage() {
    }

    public String getMid() {
        return this.mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }


}
