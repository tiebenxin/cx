package net.cb.cb.library.bean;

import net.cb.cb.library.event.BaseEvent;

/***
 * @author jyj
 * @date 2017/7/24
 */
public class EventUserOnlineChange extends BaseEvent {
//    long uid;//在线状态变更的用户id
    private Object object;

//    public long getUid() {
//        return uid;
//    }
//
//    public void setUid(long uid) {
//        this.uid = uid;
//    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
