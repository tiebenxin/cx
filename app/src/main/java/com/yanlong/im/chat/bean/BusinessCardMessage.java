package com.yanlong.im.chat.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class BusinessCardMessage {
    @Id
    private String mid;
    String avatar; // 头像地址
    String nickname; // 昵称
    String comment; // 备注



    @Generated(hash = 117223038)
    public BusinessCardMessage(String mid, String avatar, String nickname,
            String comment) {
        this.mid = mid;
        this.avatar = avatar;
        this.nickname = nickname;
        this.comment = comment;
    }

    @Generated(hash = 799428474)
    public BusinessCardMessage() {
    }



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

    public String getMid() {
        return this.mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }


}
