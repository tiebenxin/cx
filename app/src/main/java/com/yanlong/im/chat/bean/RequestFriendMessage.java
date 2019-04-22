package com.yanlong.im.chat.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class RequestFriendMessage {
    @Id
    private String mid;
    private String say_hi; // 招呼语
    @Generated(hash = 440670321)
    public RequestFriendMessage(String mid, String say_hi) {
        this.mid = mid;
        this.say_hi = say_hi;
    }
    @Generated(hash = 98958159)
    public RequestFriendMessage() {
    }
    public String getMid() {
        return this.mid;
    }
    public void setMid(String mid) {
        this.mid = mid;
    }
    public String getSay_hi() {
        return this.say_hi;
    }
    public void setSay_hi(String say_hi) {
        this.say_hi = say_hi;
    }

}
