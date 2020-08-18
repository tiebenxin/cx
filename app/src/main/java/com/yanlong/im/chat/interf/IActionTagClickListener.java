package com.yanlong.im.chat.interf;

/**
 * Created by Liszt on 2018/5/16.
 */

public interface IActionTagClickListener {

    //用户
    void clickUser(String userId, String gid);


    //红包或者转账
    void clickEnvelope(String rid);

    //红包或者转账
    void clickTransfer(String rid, String msgId);

    //点击端到端加密
    void clickLock();

    //点击撤消息，重新编辑
    void clickEditAgain(String content);

    //点击添加好哟
    void clickAddFriend(String uid);

}
