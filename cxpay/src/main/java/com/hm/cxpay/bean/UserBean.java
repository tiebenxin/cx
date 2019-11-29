package com.hm.cxpay.bean;


public class UserBean {
    private long uid;
    private int isVerify = 0;//是否认证
    private String realName;//真实姓名
    private String cardId;//身份证号码

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getIsVerify() {
        return isVerify;
    }

    public void setIsVerify(int isVerify) {
        this.isVerify = isVerify;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
}
