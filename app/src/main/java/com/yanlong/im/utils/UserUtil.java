package com.yanlong.im.utils;

import android.content.Context;

import com.yanlong.im.user.bean.FriendInfoBean;
import com.yanlong.im.user.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2019-10-08
 * @updateAuthor
 * @updateDate
 * @description 用户处理类
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class UserUtil {

    /**
     * 获取用户的首字母列表
     * @param userList 用户列表
     * @return
     */
    public static List<String> userParseString(List<UserInfo> userList) {
        List<String> list = new ArrayList<>();
        try{
            if (userList != null) {
                for (int i = 0; i < userList.size(); i++) {
                    list.add(userList.get(i).getTag());
                }
            }
        }catch (Exception e){

        }
        return list;

    }

    /**
     * 获取用户的首字母列表
     * @param friendList 用户列表
     * @return
     */
    public static List<String> friendParseString(List<FriendInfoBean> friendList) {
        List<String> list = new ArrayList<>();
        try{
            if (friendList != null) {
                for (int i = 0; i < friendList.size(); i++) {
                    list.add(friendList.get(i).getTag());
                }
            }
        }catch (Exception e){

        }
        return list;

    }
}
