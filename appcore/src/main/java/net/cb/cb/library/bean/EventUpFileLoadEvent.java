package net.cb.cb.library.bean;

import net.cb.cb.library.event.BaseEvent;

/***
 * 文件上传事件
 */
public class EventUpFileLoadEvent extends BaseEvent {
    private String msgid;
    private String url;
    private int state;//0:上传中,1成功-1:失败
    private Object msgAllBean;

    public Object getMsgAllBean() {
        return msgAllBean;
    }

    public void setMsgAllBean(Object msgAllBean) {
        this.msgAllBean = msgAllBean;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
