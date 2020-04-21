package net.cb.cb.library.bean;

import net.cb.cb.library.event.BaseEvent;

/**
 * 图片编辑：创建图片消息+上传+转发
 */
public class EventCreateImgAndSend extends BaseEvent {
    private String newPicPath;

    public EventCreateImgAndSend(String newPicPath) {
        this.newPicPath = newPicPath;
    }

    public String getNewPicPath() {
        return newPicPath;
    }

}
