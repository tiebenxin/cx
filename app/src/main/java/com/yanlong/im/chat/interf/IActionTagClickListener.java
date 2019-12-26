package com.yanlong.im.chat.interf;

import java.util.ArrayList;

/**
 * Created by LL130386 on 2018/5/16.
 */

public interface IActionTagClickListener {

    void clickUser(String userId);

    void clickValidation(String msgId, String userIds);//群主确认

    void clickCancelInvite(ArrayList<String> userIds);//撤销邀请

    void sendVerify(String userId);//发送好友验证

}
