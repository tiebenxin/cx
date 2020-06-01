package com.yanlong.im.chat.eventbus;

import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.event.BaseEvent;

/**
 * 收藏显示的大图
 */
public class EventCollectImgOrVideo extends BaseEvent {
    private String msgId;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
