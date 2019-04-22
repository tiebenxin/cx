package com.yanlong.im.chat.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class StampMessage {
    @Id
    private String mid;
    private String comment;
    @Generated(hash = 1480657064)
    public StampMessage(String mid, String comment) {
        this.mid = mid;
        this.comment = comment;
    }
    @Generated(hash = 1137653143)
    public StampMessage() {
    }
    public String getMid() {
        return this.mid;
    }
    public void setMid(String mid) {
        this.mid = mid;
    }
    public String getComment() {
        return this.comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }


}
