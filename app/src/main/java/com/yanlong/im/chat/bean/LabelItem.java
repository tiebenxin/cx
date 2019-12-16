package com.yanlong.im.chat.bean;

/**
 * @author Liszt
 * @date 2019/12/14
 * Description
 */
public class LabelItem  {
//    @PrimaryKey
//    long tradeId;//订单id
    String label = "";
    String value = "";
//
//    public long getTradeId() {
//        return tradeId;
//    }
//
//    public void setTradeId(long tradeId) {
//        this.tradeId = tradeId;
//    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
