package com.yanlong.im.user.dao;

import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import java.util.ArrayList;
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

    /***
     * 纯更新用户信息
     * @param userInfo
     */
    public void updateUserinfo(UserInfo userInfo){
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(userInfo);

                realm.commitTransaction();
        realm.close();
    }


    /**
     * 更新好友状态
     * @param type 0:陌生人或者群友,1:自己,2:通讯录,3黑名单(不区分和陌生人)
     * */
    public void updeteUserUtype(Long uid,int type){
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        UserInfo userInfo = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
        if(userInfo != null){
            userInfo.setuType(type);
            realm.insertOrUpdate(userInfo);
        }
        realm.commitTransaction();
        realm.close();
    }


    /***
     * 如果存在了就不更新
     * @param userInfo
     */
    public void saveUserinfo(UserInfo userInfo){
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        UserInfo u = realm.where(UserInfo.class).equalTo("uid", userInfo.getUid()).findFirst();
        if(u==null){
            realm.insertOrUpdate(userInfo);
        }

        realm.commitTransaction();
        realm.close();
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

    /***
     * 查找是否在好友列表
     * @param userid
     * @return
     */
    public UserInfo findUserInfo4Friend(Long userid) {
        UserInfo res=null;
        Realm realm = DaoUtil.open();
        UserInfo ls = realm.where(UserInfo.class).equalTo("uType", 2).equalTo("uid", userid).findFirst();
        if(ls!=null){
            res=  realm.copyFromRealm(ls);
        }

        realm.close();

        return res;
    }

    /***
     * 清除我的所有好友
     */
    public void friendMeDel(){
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        RealmResults<UserInfo> ls = realm.where(UserInfo.class).equalTo("uType", 2).sort("tag", Sort.ASCENDING).findAll();
        ls.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    /***
     * 更新好友
     * @param list
     */
    public void friendMeUpdate( List<UserInfo> list){
        Realm realm = DaoUtil.open();
        realm.beginTransaction();


        RealmResults<UserInfo> ls = realm.where(UserInfo.class).equalTo("uType", 2).findAll();
        for (UserInfo u:ls) {

            boolean isExt=false;
            for (UserInfo userInfo : list) {
                if(u.getUid().longValue()==userInfo.getUid().longValue()){//在好友列表中
                    isExt=true;

                    //更新用户相关
                    userInfo.toTag();
                    userInfo.setuType(2);

                    realm.copyToRealmOrUpdate(userInfo);


                }


            }
            if(!isExt){//不在好友列表中了,身份改成普通人
                u.setuType(0);
            }



        }
        //更新旧联系人状态
        realm.insertOrUpdate(ls);
        //服务器新加的联系人
        for (UserInfo userInfo : list) {
            boolean isExt=false;
            for (UserInfo u:ls) {
                if(u.getUid().longValue()==userInfo.getUid().longValue()){//在好友列表中
                    isExt=true;
                }
            }

            if(!isExt){
                userInfo.toTag();
                userInfo.setuType(2);
              //  addTemp.add(userInfo);
                realm.insertOrUpdate(userInfo);
            }

        }






        realm.commitTransaction();
        realm.close();



    }

    /***
     * 根据key搜索所有的好友
     */
    public List<UserInfo> searchUser4key(String key) {
        Realm realm = DaoUtil.open();
        List<UserInfo> ret = new ArrayList<>();
        RealmResults<UserInfo> users = realm.where(UserInfo.class).equalTo("uType", 2).and()
                .contains("name", key).or()
                .contains("mkName", key).findAll();
        if (users != null)
            ret = realm.copyFromRealm(users);
        realm.close();
        return ret;
    }


    /***
     * 用户头像,昵称更新
     * @param uid
     * @param head
     * @param name
     */
    public void userHeadNameUpdate(Long uid,String head,String name){
        if(uid==null)
            return;
        Realm realm = DaoUtil.open();

        UserInfo u = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
        if(u!=null){
            if(u.getHead().equals(head)&&u.getName().equals(name)){

            }else {
                realm.beginTransaction();
                u.setHead(head);
                u.setName(name);
                realm.insertOrUpdate(u);
                realm.commitTransaction();
            }

        }


        realm.close();
    }
}
