package net.cb.cb.library.bean;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.event.BaseEvent;

import java.util.List;

/**
 * @创建人 shenxin
 * @创建时间 2019/8/2 0002 14:52
 */
public class EventRefreshChat<T> extends BaseEvent {
    public boolean isScrollBottom = false;
    public T object;//需要刷新的消息对象
    public List<T> list;//需要批量刷新的消息对象
    @CoreEnum.ERefreshType
    private int refreshType = CoreEnum.ERefreshType.ALL;//默认全部刷新

    public boolean isScrollBottom() {
        return isScrollBottom;
    }

    public void setScrollBottom(boolean scrollBottom) {
        isScrollBottom = scrollBottom;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public int getRefreshType() {
        return refreshType;
    }

    public void setRefreshType(@CoreEnum.ERefreshType int refreshType) {
        this.refreshType = refreshType;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
