package com.yanlong.im.interf;

import com.yanlong.im.circle.bean.MessageInfoBean;

/**
 * @类名：刷新回调 + 头部返回/右侧更多点击事件
 * @Date：2020/9/29
 * @by zjy
 * @备注：
 */
public interface IRefreshListenr {
    void onRefresh(MessageInfoBean bean);
    void onLeftClick();
    void onRightClick();
}
