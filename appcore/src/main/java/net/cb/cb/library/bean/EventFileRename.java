package net.cb.cb.library.bean;

import net.cb.cb.library.event.BaseEvent;


/**
 * @类名：事件-设置文件重命名
 * @Date：2019/12/25
 * @by zjy
 * @备注：
 */

public class EventFileRename extends BaseEvent {

    private Object msgAllBean;

    public Object getMsgAllBean() {
        return msgAllBean;
    }

    public void setMsgAllBean(Object msgAllBean) {
        this.msgAllBean = msgAllBean;
    }
}
