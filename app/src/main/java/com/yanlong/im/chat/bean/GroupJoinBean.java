package com.yanlong.im.chat.bean;

public class GroupJoinBean {

    private boolean pending;//true|false，是否审核中"

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
