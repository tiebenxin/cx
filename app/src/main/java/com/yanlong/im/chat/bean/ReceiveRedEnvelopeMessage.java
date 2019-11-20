package com.yanlong.im.chat.bean;


import java.io.Serializable;

import io.realm.RealmObject;

public class ReceiveRedEnvelopeMessage extends RealmObject implements Serializable {


    private String id;

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }



}
