package com.yanlong.im.chat.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ReceiveRedEnvelopeMessage {
    @Id
    private String mid;
    private String id;
    @Generated(hash = 577862038)
    public ReceiveRedEnvelopeMessage(String mid, String id) {
        this.mid = mid;
        this.id = id;
    }
    @Generated(hash = 869448155)
    public ReceiveRedEnvelopeMessage() {
    }
    public String getMid() {
        return this.mid;
    }
    public void setMid(String mid) {
        this.mid = mid;
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }



}
