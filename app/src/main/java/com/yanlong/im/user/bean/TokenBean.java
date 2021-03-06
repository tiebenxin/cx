package com.yanlong.im.user.bean;


import net.cb.cb.library.base.BaseBean;

public class TokenBean extends BaseBean {
    private Long uid;
    private String accessToken;
    private String neteaseAccid;// 网易id
    private String neteaseToken;// 网易token
    public long validTime;//有效时间，有效时间= token获取时间+ 有效期7天的毫秒值
    private String bankReqSignKey;//支付签名
    private boolean deactivating;//是否注销账户
    private int appealState;// 申诉状态 (0:未申诉，1：申诉中)
    private int lockUser;// 用户状态 0正常 1封号

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

    public long getValidTime() {
        return validTime;
    }

    public void setValidTime(long validTime) {
        this.validTime = validTime;
    }

    public String getBankReqSignKey() {
        return bankReqSignKey;
    }

    public void setBankReqSignKey(String bankReqSignKey) {
        this.bankReqSignKey = bankReqSignKey;
    }

    public boolean isDeactivating() {
        return deactivating;
    }

    public void setDeactivating(boolean deactivating) {
        this.deactivating = deactivating;
    }

    public int getLockUser() {
        return lockUser;
    }

    public void setLockUser(int lockUser) {
        this.lockUser = lockUser;
    }

    public int getAppealState() {
        return appealState;
    }

    public void setAppealState(int appealState) {
        this.appealState = appealState;
    }

    /**
     * token是否有效,备注：imId登录问题，未兼容
     */
    public boolean isTokenValid(Long uid) {
        boolean isValid = false;
        if (uid != null && uid.equals(this.uid)) {
            if (System.currentTimeMillis() < this.validTime) {
                isValid = true;
            }
        }

        return isValid;
    }
}
