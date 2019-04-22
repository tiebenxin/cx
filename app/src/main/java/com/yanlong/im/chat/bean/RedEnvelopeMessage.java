package com.yanlong.im.chat.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class RedEnvelopeMessage {
    @Id
    private String mid;
    private String id;
    // ALIPAY = 0; // 支付宝红包
    private Integer re_type;
    private String comment;
    @Generated(hash = 1966967648)
    public RedEnvelopeMessage(String mid, String id, Integer re_type,
            String comment) {
        this.mid = mid;
        this.id = id;
        this.re_type = re_type;
        this.comment = comment;
    }
    @Generated(hash = 1125130750)
    public RedEnvelopeMessage() {
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
    public Integer getRe_type() {
        return this.re_type;
    }
    public void setRe_type(Integer re_type) {
        this.re_type = re_type;
    }
    public String getComment() {
        return this.comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }


}
