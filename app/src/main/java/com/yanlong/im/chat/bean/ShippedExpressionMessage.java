package com.yanlong.im.chat.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-01-11
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class ShippedExpressionMessage extends RealmObject implements IMsgContent {

    @PrimaryKey
    private String msgId;

    private String id;

    public ShippedExpressionMessage(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getMsgId() {
        return msgId;
    }

    public void setMsgid(String msgid) {
        this.msgId = msgid;
    }
}
