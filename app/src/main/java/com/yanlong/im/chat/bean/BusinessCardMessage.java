package com.yanlong.im.chat.bean;


import io.realm.RealmObject;

public class BusinessCardMessage extends RealmObject {

    String avatar; // 头像地址
    String nickname; // 昵称
    String comment; // 备注





    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }




}
