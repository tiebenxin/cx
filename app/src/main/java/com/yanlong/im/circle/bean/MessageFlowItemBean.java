package com.yanlong.im.circle.bean;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-09
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class MessageFlowItemBean<T> {
    private int itemType;
    private T data;

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

    public MessageFlowItemBean() {
    }

    public MessageFlowItemBean(int itemType) {
        this.itemType = itemType;
    }

    public MessageFlowItemBean(int itemType, T data) {
        this.itemType = itemType;
        this.data = data;
    }
}
