package com.yanlong.im.chat.bean;

import android.text.TextUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 收藏->短视频实体类
 */
public class CollectVideoMessage extends RealmObject implements IMsgContent {
    @PrimaryKey
    private String msgId;
    private long videoDuration;//短视频时长
    private String videoBgURL;//短视频第一帧背景图
    private long width;
    private long height;
    private long size;//短视频大小
    private String videoURL;//短视频url
    private boolean isReadOrigin = false;
    private String localUrl;

    public long getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(long videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getVideoBgURL() {
        return videoBgURL;
    }

    public void setVideoBgURL(String videoBgURL) {
        this.videoBgURL = videoBgURL;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getLocalUrl() {
        return !TextUtils.isEmpty(localUrl) ? localUrl : "";
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    @Override
    public String toString() {
        return "VideoMessage{" +
                "msgId='" + msgId + '\'' +
                ", duration=" + videoDuration +
                ", bg_url='" + videoBgURL + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", url='" + videoURL + '\'' +
                ", isReadOrigin=" + isReadOrigin +
                ", localUrl='" + localUrl + '\'' +
                '}';
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

    public boolean isReadOrigin() {
        return isReadOrigin;
    }

    public void setReadOrigin(boolean readOrigin) {
        isReadOrigin = readOrigin;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Override
    public String getMsgId() {
        return msgId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
