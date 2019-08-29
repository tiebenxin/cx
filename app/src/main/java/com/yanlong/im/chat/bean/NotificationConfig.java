package com.yanlong.im.chat.bean;

/**
 * @anthor Liszt
 * @data 2019/8/28
 * Description 通知config信息
 */
public class NotificationConfig {
    boolean hasNotify;
    String version;
    long uid;

    public boolean isHasNotify() {
        return hasNotify;
    }

    public void setHasNotify(boolean hasNotify) {
        this.hasNotify = hasNotify;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }
}
