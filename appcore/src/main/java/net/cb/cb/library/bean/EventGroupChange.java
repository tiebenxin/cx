package net.cb.cb.library.bean;

import net.cb.cb.library.event.BaseEvent;

/**
 * @author Liszt
 * @date 2019/10/24
 * Description
 */
public class EventGroupChange extends BaseEvent {
    private boolean isNeedLoad = false;//是否需要加载新的群数据
    private boolean isNeedRefresh = false;//是否刷新聊天界面

    public boolean isNeedLoad() {
        return isNeedLoad;
    }

    public void setNeedLoad(boolean needLoad) {
        isNeedLoad = needLoad;
    }

    public boolean isNeedRefresh() {
        return isNeedRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {
        isNeedRefresh = needRefresh;
    }
}
