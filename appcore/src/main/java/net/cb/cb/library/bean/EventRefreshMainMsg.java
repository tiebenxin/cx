package net.cb.cb.library.bean;

import net.cb.cb.library.CoreEnum;

/***
 * 刷新首页的消息
 */
public class EventRefreshMainMsg {
    private int type;//聊天类型，单聊还是群聊
    private Long uid;
    private String gid;
    private String refreshItem;

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
}
