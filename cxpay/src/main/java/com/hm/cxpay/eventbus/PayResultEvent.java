package com.hm.cxpay.eventbus;

import com.hm.cxpay.global.PayEnum;

/**
 * @anthor Liszt
 * @data 2019/12/9
 * Description 支付结果event
 */
public class PayResultEvent {
    @PayEnum.EPayResult
    int result;//支付结果：0-成功，1-失败，2-成功后退款
    long tradeId;//交易id
    String actionId;
    String errMsg;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public long getTradeId() {
        return tradeId;
    }

    public void setTradeId(long tradeId) {
        this.tradeId = tradeId;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
