package net.cb.cb.library.bean;

public class AliObsConfigBean {
    private String accessKeyId;
    private String securityToken;
    // private Date expiration;
    private String accessKeySecret;

    private String bucket;
    private String endpoint;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public String getSecurityToken() {
        return securityToken;
    }

/*    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
    public Date getExpiration() {
        return expiration;
    }*/

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }
}
