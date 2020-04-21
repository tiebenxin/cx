package net.cb.cb.library.bean;

import net.cb.cb.library.event.BaseEvent;

/**
 * 图片编辑：图片消息上传状态
 */
public class EventUploadImg extends BaseEvent {
    private int state;//0:上传中,1成功-1:失败
    private Object msgAllBean;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Object getMsgAllBean() {
        return msgAllBean;
    }

    public void setMsgAllBean(Object msgAllBean) {
        this.msgAllBean = msgAllBean;
    }
}
