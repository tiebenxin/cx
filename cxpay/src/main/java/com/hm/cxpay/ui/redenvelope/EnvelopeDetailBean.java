package com.hm.cxpay.ui.redenvelope;

import java.util.List;

/**
 * @author Liszt
 * @date 2019/12/10
 * Description 查看红包详情
 */
public class EnvelopeDetailBean {
    long amt;//红包总金额
    int cnt; //红包个数
    long finishTime;//红包全部领完时间
    FromUserBean imUserInfo;//发红包者用户信息
    String note;//红包备注：恭喜发财，大吉大利
    long remainAmt;//剩余金额
    int remainCnt;//红包个数
    long time;//红包发送时间
    int type;//红包类型：0 普通红包，1拼手气红包
    List<EnvelopeReceiverBean> recvList;//领取记录

    public long getAmt() {
        return amt;
    }

    public void setAmt(long amt) {
        this.amt = amt;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public FromUserBean getImUserInfo() {
        return imUserInfo;
    }

    public void setImUserInfo(FromUserBean imUserInfo) {
        this.imUserInfo = imUserInfo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getRemainAmt() {
        return remainAmt;
    }

    public void setRemainAmt(long remainAmt) {
        this.remainAmt = remainAmt;
    }

    public int getRemainCnt() {
        return remainCnt;
    }

    public void setRemainCnt(int remainCnt) {
        this.remainCnt = remainCnt;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<EnvelopeReceiverBean> getRecvList() {
        return recvList;
    }

    public void setRecvList(List<EnvelopeReceiverBean> recvList) {
        this.recvList = recvList;
    }
}
