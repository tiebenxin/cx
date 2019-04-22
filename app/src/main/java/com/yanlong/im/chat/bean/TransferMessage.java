package com.yanlong.im.chat.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TransferMessage {
    @Id
    private String mid;
    private String id; // 转账流水号
    private Double transaction_amount; // 转账金额
    private String comment; // 备注信息
    @Generated(hash = 425009586)
    public TransferMessage(String mid, String id, Double transaction_amount,
            String comment) {
        this.mid = mid;
        this.id = id;
        this.transaction_amount = transaction_amount;
        this.comment = comment;
    }
    @Generated(hash = 783525562)
    public TransferMessage() {
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
    public Double getTransaction_amount() {
        return this.transaction_amount;
    }
    public void setTransaction_amount(Double transaction_amount) {
        this.transaction_amount = transaction_amount;
    }
    public String getComment() {
        return this.comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

   
}
