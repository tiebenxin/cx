package com.yanlong.im.chat.bean;


import com.yanlong.im.user.bean.CollectionInfo;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


/**
 * @类名：收藏-离线收藏记录表
 * @Date：2020/6/3
 * @by zjy
 * @备注：
 */
public class OfflineCollect extends RealmObject{

    @PrimaryKey
    private String msgId;
    private CollectionInfo collectionInfo;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public CollectionInfo getCollectionInfo() {
        return collectionInfo;
    }

    public void setCollectionInfo(CollectionInfo collectionInfo) {
        this.collectionInfo = collectionInfo;
    }
}
