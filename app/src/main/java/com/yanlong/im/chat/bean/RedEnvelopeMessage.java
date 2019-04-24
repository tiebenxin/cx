package com.yanlong.im.chat.bean;

import io.realm.RealmObject;

public class RedEnvelopeMessage extends RealmObject {

    private String id;
    // ALIPAY = 0; // 支付宝红包
    private Integer re_type;
    private String comment;

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
