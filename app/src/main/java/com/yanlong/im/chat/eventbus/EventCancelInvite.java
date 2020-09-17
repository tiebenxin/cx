package com.yanlong.im.chat.eventbus;

import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.event.BaseEvent;

import java.util.List;

/**
 * @类名：撤销入群邀请
 * @Date：2020/9/11
 * @by zjy
 * @备注：
 */
public class EventCancelInvite extends BaseEvent {

    private List<UserInfo> userInfoList;

    public List<UserInfo> getUserInfoList() {
        return userInfoList;
    }

    public void setUserInfoList(List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }
}
