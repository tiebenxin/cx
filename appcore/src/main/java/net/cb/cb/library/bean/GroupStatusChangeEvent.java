package net.cb.cb.library.bean;

import net.cb.cb.library.event.BaseEvent;

/**
 * @author Liszt
 * @date 2020/4/9
 * Description 群属性变化通知,data 为 Group
 */
public class GroupStatusChangeEvent extends BaseEvent {
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
