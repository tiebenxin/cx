package com.yanlong.im.circle.bean;

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
    private T data;
    private long refreshTime;//本地刷新时间，推荐列表需要

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
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

    public MessageFlowItemBean(int itemType, T data) {
        this.itemType = itemType;
        this.data = data;
    }

    public MessageFlowItemBean(int itemType, T data, long time) {
        this.itemType = itemType;
        this.data = data;
        this.refreshTime = time;
    }
}
