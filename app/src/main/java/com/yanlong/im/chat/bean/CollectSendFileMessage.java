package com.yanlong.im.chat.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 收藏->文件实体类
 */
public class CollectSendFileMessage extends RealmObject implements IMsgContent{

    @PrimaryKey
    private String msgId;
    private String fileURL; // 下载地址
    private String fileName; // 原始文件名
    private String fileFormat; // 格式描述
    private long fileSize = 0; // 文件大小

    //TODO 额外本地属性
    private String localPath;//文件本地路径
    private boolean isFromOther;//true 从别人那里转发过来的文件，则需从下载地址里找  false 本地文件/自己转发自己的文件，则需要在本地路径找
    private String realFileRename;//若存在文件重名，此值为新名称；若不重名，则为原文件名

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
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

    public String getRealFileRename() {
        return realFileRename;
    }

    public void setRealFileRename(String realFileRename) {
        this.realFileRename = realFileRename;
    }
}
