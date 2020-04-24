package com.yanlong.im.user.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CollectionInfo extends RealmObject {
    @PrimaryKey
    private String msgId;
    private String imgHead;
    private String name;
    private long collectionTime;
    private int collectionType;
    private String collectionContent;
    private String path;
    private String msgBean;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

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

    public long getCollectionTime() {
        return collectionTime;
    }

    public String getMsgBean() {
        return msgBean;
    }

    public void setMsgBean(String msgBean) {
        this.msgBean = msgBean;
    }

    public void setCollectionTime(long collectionTime) {
        this.collectionTime = collectionTime;
    }

    public int getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(int collectionType) {
        this.collectionType = collectionType;
    }

    public String getCollectionContent() {
        return collectionContent;
    }

    public void setCollectionContent(String collectionContent) {
        this.collectionContent = collectionContent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
