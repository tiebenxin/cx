package com.yanlong.im.chat.eventbus;

import com.yanlong.im.user.bean.UserInfo;

/**
 * @anthor Liszt
 * @data 2019/11/5
 * Description 登陆成功后,获取自己信息后通知刷新MyFragment
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
