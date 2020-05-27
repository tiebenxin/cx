package net.cb.cb.library.bean;

import net.cb.cb.library.event.BaseEvent;

/***
 * 退出聊天界面
 */
public class EventExitChat extends BaseEvent {
    private String gid = null;
    private Long uid = null;
    public EventExitChat(){

    }
    public EventExitChat(String gid,Long uid){
        this.gid = gid;
        this.uid = uid;

    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }
}
