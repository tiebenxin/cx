package com.yanlong.im.user.dao;

import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

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
    public UserInfo findUserInfo(Long userid) {
        return DaoUtil.findOne(UserInfo.class, "uid", userid);
    }

    /***
     * 所有好友
     * @return
     */
    public List<UserInfo> friendGetAll(){
        List<UserInfo> res;
        Realm realm = DaoUtil.open();
        RealmResults<UserInfo> ls = realm.where(UserInfo.class).equalTo("uType", 2).sort("tag", Sort.ASCENDING).findAll();
        res=  realm.copyFromRealm(ls);
        realm.close();

        return res;

    }
}
