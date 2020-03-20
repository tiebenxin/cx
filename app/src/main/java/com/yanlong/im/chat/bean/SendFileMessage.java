package com.yanlong.im.chat.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SendFileMessage extends RealmObject implements IMsgContent{

    @PrimaryKey
    private String msgId;
    private String url; // 下载地址
    private String file_name; // 原始文件名
    private String format; // 格式描述
    private long size = 0; // 文件大小

    //TODO 额外本地属性
    private String localPath;//文件本地路径
    private boolean isFromOther;//是否为从别人那里转发过来的文件 (true 则需要从下载地址里找 false 则需要在自己本地找)

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public boolean isFromOther() {
        return isFromOther;
    }

    public void setFromOther(boolean fromOther) {
        isFromOther = fromOther;
    }
}
