package com.hm.cxpay.ui.redenvelope;

/**
 * @author Liszt
 * @date 2019/12/10
 * Description 红包领取记录
 */
public class EnvelopeReceiverBean {
    long amt;//领取金额
    int bestLuck;//是否手气最佳：1是0否
    FromUserBean imUserInfo;//领取用户信息
    long time;//领取时间

    public long getAmt() {
        return amt;
    }

    public void setAmt(long amt) {
        this.amt = amt;
    }

    public int getBestLuck() {
        return bestLuck;
    }

    public void setBestLuck(int bestLuck) {
        this.bestLuck = bestLuck;
    }

    public FromUserBean getImUserInfo() {
        return imUserInfo;
    }

    public void setImUserInfo(FromUserBean imUserInfo) {
        this.imUserInfo = imUserInfo;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
