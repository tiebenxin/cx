package com.hm.cxpay.bean;

/**
 * @类名：通用实体类
 * @Date：2019/12/3
 * @by zjy
 * @备注：
 */
public class CommonBean {

    //充值bean类
    private int code;//状态码(1:成功 2:失败 99:处理中)
    //系统费率bean类 (单位都是分)
    private int minAmt;//最低提现金额
    private int minFee;//TODO 最低费用 后端所加，暂时没用到
    private String rate ="";//费率
    private int serviceFee;//服务费
    //账单bean类
    private long amt;//金额
    private long balance;
    private String bankCardInfo;
    private int billType;
    private long bzId;
    private long createTime;
    private long fee;
    private int income;
    private int luck;
    private String note;
    private OtherUserBean otherUser;
    private int stat;
    private long statConfirmTime;
    private int toGroup;
    private long tradeId;
    private int tradeType;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getMinAmt() {
        return minAmt;
    }

    public void setMinAmt(int minAmt) {
        this.minAmt = minAmt;
    }

    public int getMinFee() {
        return minFee;
    }

    public void setMinFee(int minFee) {
        this.minFee = minFee;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public int getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(int serviceFee) {
        this.serviceFee = serviceFee;
    }

    public long getAmt() {
        return amt;
    }

    public void setAmt(long amt) {
        this.amt = amt;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public String getBankCardInfo() {
        return bankCardInfo;
    }

    public void setBankCardInfo(String bankCardInfo) {
        this.bankCardInfo = bankCardInfo;
    }

    public int getBillType() {
        return billType;
    }

    public void setBillType(int billType) {
        this.billType = billType;
    }

    public long getBzId() {
        return bzId;
    }

    public void setBzId(long bzId) {
        this.bzId = bzId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = luck;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public OtherUserBean getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(OtherUserBean otherUser) {
        this.otherUser = otherUser;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public long getStatConfirmTime() {
        return statConfirmTime;
    }

    public void setStatConfirmTime(long statConfirmTime) {
        this.statConfirmTime = statConfirmTime;
    }

    public int getToGroup() {
        return toGroup;
    }

    public void setToGroup(int toGroup) {
        this.toGroup = toGroup;
    }

    public long getTradeId() {
        return tradeId;
    }

    public void setTradeId(long tradeId) {
        this.tradeId = tradeId;
    }

    public int getTradeType() {
        return tradeType;
    }

    public void setTradeType(int tradeType) {
        this.tradeType = tradeType;
    }


    public static class OtherUserBean {

        private String avatar;
        private String nickname;
        private long uid;

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

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }
    }
}
