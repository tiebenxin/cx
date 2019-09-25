package com.yanlong.im.chat.manager;

import com.yanlong.im.utils.socket.MsgBean;

/**
 * @anthor Liszt
 * @data 2019/9/24
 * Description
 */
public class MessageManager {
    private static MessageManager INSTANCE;

    public static MessageManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessageManager();
        }
        return INSTANCE;
    }


    public void onReceive(MsgBean.UniversalMessage msg){

    }


}
