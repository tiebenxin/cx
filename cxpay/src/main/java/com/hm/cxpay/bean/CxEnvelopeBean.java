package com.hm.cxpay.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.hm.cxpay.global.PayEnum;

import net.cb.cb.library.base.BaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Liszt
 * @date 2019/12/9
 * Description 常信红包封装类
 */
public class CxEnvelopeBean extends BaseBean implements Parcelable {
    private String actionId = "";//actionId == rid
    //    @PayEnum.ESendResult
//    int resultType; //红包支付结果
    private long createTime = 0;//创建时间
    private long tradeId = 0;//交易id
    @PayEnum.ERedEnvelopeType
    private int envelopeType; // 红包类型,0-普通红包；1-拼手气红包
    String message = "";//默认恭喜发财，好运连连
    private int envelopeAmount = 0;//红包个数
    private String sign = "";//签名
    private List<FromUserBean> allowUses;

    public CxEnvelopeBean() {

    }

    protected CxEnvelopeBean(Parcel in) {
        actionId = in.readString();
        createTime = in.readLong();
        tradeId = in.readLong();
        envelopeType = in.readInt();
        message = in.readString();
        envelopeAmount = in.readInt();
        sign = in.readString();
        allowUses = in.readArrayList(FromUserBean.class.getClassLoader());

    }

    public static final Creator<CxEnvelopeBean> CREATOR = new Creator<CxEnvelopeBean>() {
        @Override
        public CxEnvelopeBean createFromParcel(Parcel in) {
            return new CxEnvelopeBean(in);
        }

        @Override
        public CxEnvelopeBean[] newArray(int size) {
            return new CxEnvelopeBean[size];
        }
    };

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

//    public int getResultType() {
//        return resultType;
//    }
//
//    public void setResultType(int resultType) {
//        this.resultType = resultType;
//    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getTradeId() {
        return tradeId;
    }

    public void setTradeId(long tradeId) {
        this.tradeId = tradeId;
    }

    public int getEnvelopeType() {
        return envelopeType;
    }

    public void setEnvelopeType(int envelopeType) {
        this.envelopeType = envelopeType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getEnvelopeAmount() {
        return envelopeAmount;
    }

    public void setEnvelopeAmount(int envelopeAmount) {
        this.envelopeAmount = envelopeAmount;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public List<FromUserBean> getAllowUses() {
        return allowUses;
    }

    public void setAllowUses(List<FromUserBean> allowUses) {
        this.allowUses = allowUses;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(actionId);
        dest.writeLong(createTime);
        dest.writeLong(tradeId);
        dest.writeInt(envelopeType);
        dest.writeString(message);
        dest.writeInt(envelopeAmount);
        dest.writeString(sign);
        dest.writeList(allowUses);

    }
}
