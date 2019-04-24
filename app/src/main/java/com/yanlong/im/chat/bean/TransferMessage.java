package com.yanlong.im.chat.bean;


import io.realm.RealmObject;

public class TransferMessage extends RealmObject {

    private String id; // 转账流水号
    private Double transaction_amount; // 转账金额
    private String comment; // 备注信息

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
