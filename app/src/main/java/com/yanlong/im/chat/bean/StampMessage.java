package com.yanlong.im.chat.bean;


import io.realm.RealmObject;

public class StampMessage extends RealmObject {

    private String comment;


    public String getComment() {
        return this.comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }


}
