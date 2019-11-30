package com.hm.cxpay.ui.bank;

/**
 * @anthor Liszt
 * @data 2019/11/30
 * Description  绑定银行卡基本数据
 */
public class BankBean {
    String bankName;//银行名
    String cardNo;//银行卡号
    int id;//id
    String logo;//银行logo
    int seqNo;//排序号，升序

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }
}
