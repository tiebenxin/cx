package com.yanlong.im.circle.bean;

import androidx.annotation.Nullable;

import net.cb.cb.library.base.BaseBean;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-09
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class MessageFlowItemBean<T> extends BaseBean {
    private int itemType;
    private MessageInfoBean data;
    private long refreshTime;//本地刷新时间，推荐列表需要

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public MessageInfoBean getData() {
        return data;
    }

    public void setData(MessageInfoBean data) {
        this.data = data;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public MessageFlowItemBean() {
    }

    public MessageFlowItemBean(int itemType) {
        this.itemType = itemType;
    }

    public MessageFlowItemBean(int itemType, MessageInfoBean data) {
        this.itemType = itemType;
        this.data = data;
    }

    public MessageFlowItemBean(int itemType, MessageInfoBean data, long time) {
        this.itemType = itemType;
        this.data = data;
        this.refreshTime = time;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            if (data == null || obj == null) {
                return false;
            }
            if (data.getId() == null || ((MessageFlowItemBean) obj).getData().getId() == null) {
                return false;
            }
            if (data.getId().longValue() == ((MessageFlowItemBean) obj).getData().getId().longValue()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
