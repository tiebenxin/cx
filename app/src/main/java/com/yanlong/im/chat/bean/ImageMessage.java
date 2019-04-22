package com.yanlong.im.chat.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ImageMessage {
    @Id
    private String mid;
    private String url;



    @Generated(hash = 1241603683)
    public ImageMessage(String mid, String url) {
        this.mid = mid;
        this.url = url;
    }

    @Generated(hash = 48598060)
    public ImageMessage() {
    }



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMid() {
        return this.mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }


}
