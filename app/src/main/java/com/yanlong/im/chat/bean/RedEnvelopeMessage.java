package com.yanlong.im.chat.bean;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class RedEnvelopeMessage extends RealmObject {
    @PrimaryKey
    private String msgid;

    private String id;
    // ALIPAY = 0; // 支付宝红包
    private Integer re_type;
    private String comment;

    //红包的状态0:没拆,1拆了
    private int isInvalid = 0;
    //类型:0普通1拼手气
    private int style = 0;

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getIsInvalid() {
        return isInvalid;
    }

    public void setIsInvalid(int isInvalid) {
        this.isInvalid = isInvalid;
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

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
}
