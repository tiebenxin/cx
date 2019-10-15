package com.yanlong.im.chat.eventbus;

import com.yanlong.im.chat.bean.MsgAllBean;

import net.cb.cb.library.CoreEnum;

/***
 * 刷新首页的消息
 */
public class EventRefreshMainMsg {
    private int type;//聊天类型，单聊还是群聊
    private Long uid;//刷新用户session
    private String gid;//刷新群聊session
    private String refreshItem;
    private MsgAllBean msgAllBean;//需要刷新最后一条消息

    @CoreEnum.ESessionRefreshTag
    private int refreshTag = CoreEnum.ESessionRefreshTag.ALL;//刷新类型，单个刷新还是全部刷新,默认刷新all


    public int getType() {
        return type;
    }

    public void setType(@CoreEnum.EChatType int type) {
        this.type = type;
    }

    public long getUid() {
        if (uid == null) {
            uid = -1L;
        }
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getRefreshItem() {
        return refreshItem;
    }

    public void setRefreshItem(String refreshItem) {
        this.refreshItem = refreshItem;
    }

    public int getRefreshTag() {
        return refreshTag;
    }

    public void setRefreshTag(@CoreEnum.ESessionRefreshTag int refreshTag) {
        this.refreshTag = refreshTag;
    }

    public MsgAllBean getMsgAllBean() {
        return msgAllBean;
    }

    public void setMsgAllBean(MsgAllBean msgAllBean) {
        this.msgAllBean = msgAllBean;
    }
}
