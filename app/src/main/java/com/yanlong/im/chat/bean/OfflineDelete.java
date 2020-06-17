package com.yanlong.im.chat.bean;

import com.yanlong.im.user.bean.CollectionInfo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


/**
 * @类名：收藏-离线删除记录表
 * @Date：2020/6/3
 * @by zjy
 * @备注：
 */
public class OfflineDelete extends RealmObject{

    @PrimaryKey
    private String msgId;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

}
