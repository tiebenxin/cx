package com.yanlong.im.chat.bean;

import android.content.Context;

import net.cb.cb.library.base.BaseBean;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-01-16
 * @updateAuthor
 * @updateDate
 * @description 用于视屏重发
 * @copyright copyright(c)2020 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class VideoUploadBean extends BaseBean {

    private Context context;
    private String id;
    private String file;
    private String bgUrl;
    private Boolean isOriginal;
    private Long toUId;
    private String toGid;
    private long time;
    private VideoMessage videoMessage;
    private int sendNum = 0;

    public VideoUploadBean(Context context,String id,String file,String bgUrl,Boolean isOriginal,Long toUId,String toGid,
                           long time,VideoMessage videoMessage,int sendNum){
        this.context = context;
        this.id = id;
        this.file = file;
        this.bgUrl = bgUrl;
        this.isOriginal = isOriginal;
        this.toUId = toUId;
        this.toGid = toGid;
        this.time = time;
        this.videoMessage = videoMessage;
        this.sendNum = sendNum;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getId() {
        return id;
    }

    public int getSendNum() {
        return sendNum;
    }

    public void setSendNum(int sendNum) {
        this.sendNum = sendNum;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public Boolean getOriginal() {
        return isOriginal;
    }

    public void setOriginal(Boolean original) {
        isOriginal = original;
    }

    public Long getToUId() {
        return toUId;
    }

    public void setToUId(Long toUId) {
        this.toUId = toUId;
    }

    public String getToGid() {
        return toGid;
    }

    public void setToGid(String toGid) {
        this.toGid = toGid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public VideoMessage getVideoMessage() {
        return videoMessage;
    }

    public void setVideoMessage(VideoMessage videoMessage) {
        this.videoMessage = videoMessage;
    }
}
