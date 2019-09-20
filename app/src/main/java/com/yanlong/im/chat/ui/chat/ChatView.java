package com.yanlong.im.chat.ui.chat;

import com.yanlong.im.chat.bean.MsgAllBean;

import net.cb.cb.library.base.IView;

import java.util.List;

/**
 * @anthor Liszt
 * @data 2019/9/19
 * Description
 */
public interface ChatView extends IView {

     void setAndRefreshData(List<MsgAllBean> l);

    void initUIAndListener();
}
