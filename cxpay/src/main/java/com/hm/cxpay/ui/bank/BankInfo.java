package com.hm.cxpay.ui.bank;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @anthor Liszt
 * @data 2019/11/29
 * Description  银行卡信息
 */
public class BankInfo implements Parcelable {
    String bankCode;//银行编码
    String bankName;//银行名称
    int cardType;//银行卡类别，目前只支持储蓄卡，不支持信用卡
    String cardTypeLabel;//银行卡类别标签，目前只支持储蓄卡，不支持信用卡
    String ownerName;//持卡人姓名

    protected BankInfo(Parcel in) {
        bankCode = in.readString();
        bankName = in.readString();
        cardType = in.readInt();
        cardTypeLabel = in.readString();
        ownerName = in.readString();
    }

    public static final Creator<BankInfo> CREATOR = new Creator<BankInfo>() {
        @Override
        public BankInfo createFromParcel(Parcel in) {
            return new BankInfo(in);
        }

        @Override
        public BankInfo[] newArray(int size) {
            return new BankInfo[size];
        }
    };

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getCardTypeLabel() {
        return cardTypeLabel;
    }

    public void setCardTypeLabel(String cardTypeLabel) {
        this.cardTypeLabel = cardTypeLabel;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bankCode);
        dest.writeString(bankName);
        dest.writeInt(cardType);
        dest.writeString(cardTypeLabel);
        dest.writeString(ownerName);
    }
}
