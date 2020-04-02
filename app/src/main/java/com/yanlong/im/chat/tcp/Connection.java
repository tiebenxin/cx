package com.yanlong.im.chat.tcp;

/**
 * @author Liszt
 * @date 2020/4/1
 * Description
 */
public interface Connection {
    void startConnect();

    void stopConnect();

    void destroyConnect();
}
