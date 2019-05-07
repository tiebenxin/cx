package com.yanlong.im.user.dao;

import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

public class UserDao {

    /***
     * 获取自己的信息
     * @return
     */
    public UserInfo myInfo() {
        return DaoUtil.findOne(UserInfo.class, "uType", 1);
    }

    public void updateUserinfo(UserInfo userInfo){
        DaoUtil.update(userInfo);
    }


    /***
     * 根据id获取用户的信息
     * @param userid
     * @return
     */
    public UserInfo findUserInfo(String userid) {
        return DaoUtil.findOne(UserInfo.class, "uid", userid);
    }
}
