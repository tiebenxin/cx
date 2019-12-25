package com.hm.cxpay.bean;

import net.cb.cb.library.base.BaseBean;

/**
 * @author Liszt
 * @date 2019/12/25
 * Description
 */
public class TransferDetailBean extends BaseBean {
    private long amt;//金额
    private String bankCardInfo = "";//银行卡信息，转账发起人才能返回
    private int income = 0;//1 收入 其他支出
    private String note = "";
    private FromUserBean payUser;//支付者用户信息
    private long recvTime;//领取时间
    private FromUserBean recvUser;//接受者用户信息
    private int refundWay;
    private long rejectTime;//退还时间
    private int stat;//1未领取 2已领取 3已拒收 4已过期
    private long transTime;//转账时间

    public long getAmt() {
        return amt;
    }

    public void setAmt(long amt) {
        this.amt = amt;
    }

    public String getBankCardInfo() {
        return bankCardInfo;
    }

    public void setBankCardInfo(String bankCardInfo) {
        this.bankCardInfo = bankCardInfo;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public FromUserBean getPayUser() {
        return payUser;
    }

    public void setPayUser(FromUserBean payUser) {
        this.payUser = payUser;
    }

    public long getRecvTime() {
        return recvTime;
    }

    public void setRecvTime(long recvTime) {
        this.recvTime = recvTime;
    }

    public FromUserBean getRecvUser() {
        return recvUser;
    }

    public void setRecvUser(FromUserBean recvUser) {
        this.recvUser = recvUser;
    }

    public int getRefundWay() {
        return refundWay;
    }

    public void setRefundWay(int refundWay) {
        this.refundWay = refundWay;
    }

    public long getRejectTime() {
        return rejectTime;
    }

    public void setRejectTime(long rejectTime) {
        this.rejectTime = rejectTime;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public long getTransTime() {
        return transTime;
    }

    public void setTransTime(long transTime) {
        this.transTime = transTime;
    }
}
