package com.hm.cxpay.bean;

/**
 * @类名：通用实体类
 * @Date：2019/12/3
 * @by zjy
 * @备注：
 */
public class CommonBean {

    //充值
    private int code = 0;//状态码(1:成功 2:失败 99:处理中)
    //提现获取系统费率(单位都是分)
    private int minAmt =0;//最低提现金额
    private int minFee =0;//TODO 最低费用 后端所加，暂时没用到
    private String rate ="";//费率
    private int serviceFee =0;//服务费


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
}
