package com.yanlong.im.chat.eventbus;

import com.yanlong.im.user.bean.UserInfo;

/**
 * @anthor Liszt
 * @data 2019/11/5
 * Description
 */
public class EventRefreshUser {
    private UserInfo info;

    public UserInfo getInfo() {
        return info;
    }

    public void setInfo(UserInfo info) {
        this.info = info;
    }
}
