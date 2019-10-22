package com.yanlong.im.user.bean;


public class TokenBean {
    private Long uid;
    private String accessToken;
    private String neteaseAccid;// 网易id
    private String neteaseToken;// 网易token
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

    public String getNeteaseAccid() {
        return neteaseAccid;
    }

    public void setNeteaseAccid(String neteaseAccid) {
        this.neteaseAccid = neteaseAccid;
    }

    public String getNeteaseToken() {
        return neteaseToken;
    }

    public void setNeteaseToken(String neteaseToken) {
        this.neteaseToken = neteaseToken;
    }
}
