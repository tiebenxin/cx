package net.cb.cb.library.bean;

import net.cb.cb.library.CoreEnum;

/**
 * @anthor Liszt
 * @data 2019/9/19
 * Description
 */
public class EventNetStatus {
    @CoreEnum.ENetStatus
    private int status;

    public EventNetStatus(@CoreEnum.ENetStatus int value) {
        status = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
