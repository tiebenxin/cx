package com.yanlong.im.circle.bean;

import net.cb.cb.library.base.BaseBean;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-18
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class MessageInfoBean extends BaseBean {
    private String imgHead;
    private String name;
    private String time;
    private String content;
    private boolean isShowAll;
    private boolean isRowsMore;// 是否超过3行

    public String getImgHead() {
        return imgHead;
    }

    public void setImgHead(String imgHead) {
        this.imgHead = imgHead;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isShowAll() {
        return isShowAll;
    }

    public void setShowAll(boolean showAll) {
        isShowAll = showAll;
    }

    public boolean isRowsMore() {
        return isRowsMore;
    }

    public void setRowsMore(boolean rowsMore) {
        isRowsMore = rowsMore;
    }
}
