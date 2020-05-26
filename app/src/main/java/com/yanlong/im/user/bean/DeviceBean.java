package com.yanlong.im.user.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import net.cb.cb.library.base.BaseBean;

/**
 * @author Liszt
 * @date 2020/5/23
 * Description
 */
public class DeviceBean extends BaseBean implements Parcelable, Comparable {
    long createTime;
    String detail;
    String device;
    long lastUpdate;
    String name;
    long uid;

    protected DeviceBean(Parcel in) {
        createTime = in.readLong();
        detail = in.readString();
        device = in.readString();
        lastUpdate = in.readLong();
        name = in.readString();
        uid = in.readLong();
    }

    public static final Creator<DeviceBean> CREATOR = new Creator<DeviceBean>() {
        @Override
        public DeviceBean createFromParcel(Parcel in) {
            return new DeviceBean(in);
        }

        @Override
        public DeviceBean[] newArray(int size) {
            return new DeviceBean[size];
        }
    };

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        DeviceBean bean = (DeviceBean) obj;
        if (!TextUtils.isEmpty(device) && !TextUtils.isEmpty(bean.getDevice())) {
            if (device.equalsIgnoreCase(bean.getDevice())) {
                return true;
            }
        }
        return false;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(createTime);
        parcel.writeString(detail);
        parcel.writeString(device);
        parcel.writeLong(lastUpdate);
        parcel.writeString(name);
        parcel.writeLong(uid);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return -1;
        }
        DeviceBean bean = (DeviceBean) o;
        return (int) (this.lastUpdate - bean.getLastUpdate());
    }
}
