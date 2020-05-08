package com.yanlong.im.chat.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Liszt
 * @date 2020/5/7
 * Description 单条回复消息
 */
public class ReplyMessage extends RealmObject implements IMsgContent {
    @PrimaryKey
    String msgId;
    QuotedMessage quotedMessage;
    ChatMessage chatMessage;
    AtMessage atMessage;

    @Override
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public QuotedMessage getQuotedMessage() {
        return quotedMessage;
    }

    public void setQuotedMessage(QuotedMessage quotedMessage) {
        this.quotedMessage = quotedMessage;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public AtMessage getAtMessage() {
        return atMessage;
    }

    public void setAtMessage(AtMessage atMessage) {
        this.atMessage = atMessage;
    }
}
