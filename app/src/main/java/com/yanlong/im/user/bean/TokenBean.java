package com.yanlong.im.user.bean;


public class TokenBean {
    private Long uid;
    private String accessToken;
    public void setUid(Long uid) {
        this.uid = uid;
    }
    public Long getUid() {
        return uid;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getAccessToken() {
        return accessToken;
    }

}
