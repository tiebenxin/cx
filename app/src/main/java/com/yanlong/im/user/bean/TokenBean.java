package com.yanlong.im.user.bean;

import com.google.gson.annotations.SerializedName;

public class TokenBean {
    private String uid;
    @SerializedName("access_token")
    private String accessToken;
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getUid() {
        return uid;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getAccessToken() {
        return accessToken;
    }

}
