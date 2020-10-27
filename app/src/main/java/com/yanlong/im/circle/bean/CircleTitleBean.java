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
public class CircleTitleBean extends BaseBean {
    private String title;
    private String content;
    private long size;
    private int width;
    private int height;
    private boolean isCheck;

    public CircleTitleBean(String title, boolean check) {
        this.title = title;
        this.isCheck = check;
    }

    public CircleTitleBean(String content) {
        this.content = content;
    }

    public CircleTitleBean(String content, long size, int width, int height) {
        this.content = content;
        this.size = size;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
