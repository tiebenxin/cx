package com.yanlong.im.user.bean;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import net.cb.cb.library.base.BaseBean;

/**
 * @author Liszt
 * @date 2020/5/23
 * Description
 */
public class DeviceBean extends BaseBean {
    String createTime;
    String detail;
    String device;
    String lastUpdate;
    String name;
    long uid;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
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

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
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
}
