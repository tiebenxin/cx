package com.hm.cxpay.ui.redenvelope;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @anthor Liszt
 * @data 2019/12/3
 * Description 发送红包结果bean
 */
public class RedSendBean implements Parcelable {
    String actionId;
    int code;//1:成功 2:失败 99:处理中 客户端暂不考虑[1010:需要下一步验证
    long createTime;
    String errMsg;
    long tradeId;

    protected RedSendBean(Parcel in) {
        actionId = in.readString();
        code = in.readInt();
        createTime = in.readLong();
        errMsg = in.readString();
        tradeId = in.readLong();
    }

    public static final Creator<RedSendBean> CREATOR = new Creator<RedSendBean>() {
        @Override
        public RedSendBean createFromParcel(Parcel in) {
            return new RedSendBean(in);
        }

        @Override
        public RedSendBean[] newArray(int size) {
            return new RedSendBean[size];
        }
    };

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public long getTradeId() {
        return tradeId;
    }

    public void setTradeId(long tradeId) {
        this.tradeId = tradeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(actionId);
        dest.writeInt(code);
        dest.writeLong(createTime);
        dest.writeString(errMsg);
        dest.writeLong(tradeId);
    }
}
