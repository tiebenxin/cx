package net.cb.cb.library.bean;

import net.cb.cb.library.base.BaseBean;

/**
 * @author Liszt
 * @date 2020/5/26
 * Description
 */
public class VideoSize extends BaseBean {
    long size;//字节大小
    long duration;//视频时长
    String bgUrl;//第一帧图片路径
    long width;
    long height;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }
}
