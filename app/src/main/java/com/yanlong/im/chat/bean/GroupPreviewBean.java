package com.yanlong.im.chat.bean;

import net.cb.cb.library.base.BaseBean;

import java.util.List;

/**
 * @author Liszt
 * @date 2020/9/15
 * Description 分组浏览bean
 */
public class GroupPreviewBean extends BaseBean {
    String time;
    List<MsgAllBean> msgAllBeans;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<MsgAllBean> getMsgAllBeans() {
        return msgAllBeans;
    }

    public void setMsgAllBeans(List<MsgAllBean> msgAllBeans) {
        this.msgAllBeans = msgAllBeans;
    }
}
