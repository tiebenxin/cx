package com.yanlong.im.chat.manager;

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


    public void onReceive(){

    }


}
