package com.yanlong.im.user.bean;

import net.cb.cb.library.base.BaseBean;

public class NewVersionBean extends BaseBean {


    private int id;
    private String platform;
    private String version;
    private String content;
    private String url;
    private int forceUpdate;
    private String publishTime;
    private String minEscapeVersion;//最小不强制升级版本

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(int forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getMinEscapeVersion() {
        return minEscapeVersion;
    }

    public void setMinEscapeVersion(String minEscapeVersion) {
        this.minEscapeVersion = minEscapeVersion;
    }
}
