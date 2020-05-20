package com.yanlong.im.chat.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 收藏->表情实体类
 */
public class CollectShippedExpressionMessage extends RealmObject implements IMsgContent {

    @PrimaryKey
    private String msgId;

    private String expression;

    public CollectShippedExpressionMessage(){

    }

    @Override
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
