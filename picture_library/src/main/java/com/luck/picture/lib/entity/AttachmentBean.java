package com.luck.picture.lib.entity;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-25
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class AttachmentBean {
// 附件 type 对应 momentType bgUrl 为 视频的第一帧图片 isOrigin 为 是否原文件 duration 时长
// [{"type":1,"url":"http://zx-im-img.zhixun6.com/image/a.png",
// "bgUrl":"http://zx-im-img.zhixun6.com/image/b.png",\n "isOrigin":0,"duration":10},...]

    private int type;//
    private String url;// 文件地址
    private String bgUrl;// 视频的第一帧图片
    private long duration;// 时长
    private int width;// 文件宽
    private int height;// 文件高
    private long size;// 文件大小

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
