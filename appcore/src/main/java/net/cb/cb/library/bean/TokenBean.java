package net.cb.cb.library.bean;

import com.google.gson.annotations.SerializedName;


/***
 * @author jyj
 * @date 2016/12/22
 */
public class TokenBean {
    private String mode;
    private String identity;
    @SerializedName("ex.token")
    private String token;
    @SerializedName("ex.token_expires")
    private String tokenExpires;
    @SerializedName("ex.service_ticket")
    private String serviceTicket;
    @SerializedName("ex.oauth_access_token_expires")
    private String oauthAccessTokenExpires;

    @SerializedName("ex.oauth_access_token")
    private String oauthAccessToken;
    @SerializedName("ex.oauth_refresh_token")
    private String oauthRefreshToken;

    //刷新的token
    @SerializedName("access_token")
    private String reAccessToken;
    @SerializedName("refresh_token")
    private String reRefreshToken;

    public String getReAccessToken() {
        return reAccessToken;
    }

    public void setReAccessToken(String reAccessToken) {
        this.reAccessToken = reAccessToken;
    }

    public String getReRefreshToken() {
        return reRefreshToken;
    }

    public void setReRefreshToken(String reRefreshToken) {
        this.reRefreshToken = reRefreshToken;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenExpires() {
        return tokenExpires;
    }

    public void setTokenExpires(String tokenExpires) {
        this.tokenExpires = tokenExpires;
    }

    public String getServiceTicket() {
        return serviceTicket;
    }

    public void setServiceTicket(String serviceTicket) {
        this.serviceTicket = serviceTicket;
    }

    public String getOauthAccessTokenExpires() {
        return oauthAccessTokenExpires;
    }

    public void setOauthAccessTokenExpires(String oauthAccessTokenExpires) {
        this.oauthAccessTokenExpires = oauthAccessTokenExpires;
    }

    public String getOauthAccessToken() {
        return oauthAccessToken;
    }

    public void setOauthAccessToken(String oauthAccessToken) {
        this.oauthAccessToken = oauthAccessToken;
    }

    public String getOauthRefreshToken() {
        return oauthRefreshToken;
    }

    public void setOauthRefreshToken(String oauthRefreshToken) {
        this.oauthRefreshToken = oauthRefreshToken;
    }
}
