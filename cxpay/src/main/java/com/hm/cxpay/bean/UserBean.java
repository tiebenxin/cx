package com.hm.cxpay.bean;


public class UserBean {
    private long uid;
    private int isVerify = 0;//是否认证
    private String realName = "";//真实姓名
    private String cardId = "";//身份证号码

    private int balance = 0;//余额
    private int id = 0;//用户id
    private String identityNo = "";//证件号码
    private String identityType = "";//证件类型 1身份证 2护照
    private int payPwdStat = 0;//支付密码设置状态 1:已设置, 其他情况未设置
    private int realNameStat = 0;//实名认证状态 1:已认证, 其他情况未认证


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

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentityNo() {
        return identityNo;
    }

    public void setIdentityNo(String identityNo) {
        this.identityNo = identityNo;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public int getPayPwdStat() {
        return payPwdStat;
    }

    public void setPayPwdStat(int payPwdStat) {
        this.payPwdStat = payPwdStat;
    }

    public int getRealNameStat() {
        return realNameStat;
    }

    public void setRealNameStat(int realNameStat) {
        this.realNameStat = realNameStat;
    }
}
