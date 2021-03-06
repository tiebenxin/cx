package com.yanlong.im.user.dao;

import android.os.Build;
import android.text.TextUtils;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.PhoneBean;
import com.yanlong.im.user.bean.UserBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.OnlineBean;
import net.cb.cb.library.manager.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class UserDao {

    /***
     * 获取自己的信息
     * @return
     */
    public UserBean myInfo() {
        return DaoUtil.findOne(UserBean.class, "uType", 1);
    }

    /***
     * 纯更新用户信息
     * @param userInfo
     */
    public void updateUserinfo(UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        if (TextUtils.isEmpty(userInfo.getTag())) {
            userInfo.toTag();
        }
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(userInfo);
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }


    /***
     * 纯更新用户信息
     * @param userInfo
     */
    public void updateUserBean(UserBean userInfo) {
        if (userInfo == null) {
            return;
        }
        if (TextUtils.isEmpty(userInfo.getTag())) {
            userInfo.toTag();
        }
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(userInfo);
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }


    /**
     * 更新好友状态
     *
     * @param type 0:陌生人或者群友,1:自己,2:通讯录,3黑名单(不区分和陌生人)
     */
    public void updateUserUtype(Long uid, int type) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            UserInfo userInfo = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
            if (userInfo != null) {
                userInfo.setuType(type);
                realm.insertOrUpdate(userInfo);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    /***
     * 更新单聊阅后即焚状态
     */
    public void updateReadDestroy(Long uid, int type) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            UserInfo userInfo = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
            if (userInfo != null) {
                userInfo.setDestroy(type);
                realm.insertOrUpdate(userInfo);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
        }
    }


    /***
     * 更新群阅后即焚状态
     */
    public void updateGroupReadDestroy(String gid, int type) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
            if (group != null) {
                group.setSurvivaltime(type);
                realm.insertOrUpdate(group);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
        }

    }

    /**
     * 获取阅后即焚状态
     */
    public int getReadDestroy(Long uid, String gid) {
        Realm realm = DaoUtil.open();
        int survivaltime;
        try {
            if (TextUtils.isEmpty(gid)) {
                UserInfo userInfo = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
                if (userInfo == null) {
                    return 0;
                } else {
                    survivaltime = userInfo.getDestroy();

                    return survivaltime;
                }
            } else {
                Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
                if (group == null) {
                    return 0;
                } else {
                    survivaltime = group.getSurvivaltime();
                    return survivaltime;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            realm.close();
        }
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
     * 获取UserInfo 表中自己的用户信息
     * @param userid
     * @return
     */
    public UserInfo findUserInfoOfMe(Long userid) {
        UserInfo info = null;
        Realm realm = DaoUtil.open();
        try {
            UserInfo u = realm.where(UserInfo.class)
                    .equalTo("uid", userid)
                    .and()
                    .equalTo("uType", 1)
                    .findFirst();
            if (u != null) {
                info = realm.copyFromRealm(u);
            }
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return info;
    }

    /***
     * 根据id获取用户的信息
     * @param userid
     * @return
     */
    public UserBean findUserBean(Long userid) {
        return DaoUtil.findOne(UserBean.class, "uid", userid);
    }

    /***
     * 根据网易id获取用户的信息
     * @param neteaseAccid
     * @return
     */
    public UserInfo findUserInfo(String neteaseAccid) {
        return DaoUtil.findOne(UserInfo.class, "neteaseAccid", neteaseAccid);
    }

    /***
     * 所有好友
     * @return
     */
    public List<UserInfo> friendGetAll(Boolean isNeedKefu) {
        List<UserInfo> res = null;
        Realm realm = DaoUtil.open();
        try {
            RealmResults<UserInfo> ls;
            if (!isNeedKefu) {
                ls = realm.where(UserInfo.class)
                        .beginGroup().equalTo("uType", 2).endGroup()
                        .and()
                        .beginGroup().notEqualTo("uid", Constants.CX888_UID).endGroup()
                        .and()
                        .beginGroup().notEqualTo("uid", Constants.CX_BALANCE_UID).endGroup()
                        .sort("tag", Sort.ASCENDING).findAll();
            } else {
                ls = realm.where(UserInfo.class).equalTo("uType", 2).sort("tag", Sort.ASCENDING).findAll();
            }
            res = realm.copyFromRealm(ls);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return res;
    }

    /***
     * 获取能有效转发用户
     * @return
     */
    public List<UserInfo> getForwardUserValid() {
        List<UserInfo> res = null;
        Realm realm = DaoUtil.open();
        try {
            RealmResults<UserInfo> ls;
            ls = realm.where(UserInfo.class)
                    .beginGroup().equalTo("uType", 2).endGroup()
                    .or()
                    .beginGroup().equalTo("uType", 4).endGroup()
                    .and()
                    .beginGroup().notEqualTo("uid", Constants.CX888_UID).endGroup()
                    .and()
                    .beginGroup().notEqualTo("uid", Constants.CX_BALANCE_UID).endGroup()
                    .and()
                    .beginGroup().notEqualTo("uid", Constants.CX_HELPER_UID).endGroup()
                    .sort("tag", Sort.ASCENDING).findAll();

            res = realm.copyFromRealm(ls);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return res;

    }

    /***
     * 所有通讯录好友，系统用户
     * @return
     */
    public List<UserInfo> getAllUserInBook() {
        List<UserInfo> res = null;
        Realm realm = DaoUtil.open();
        try {
            RealmResults<UserInfo> ls = realm.where(UserInfo.class)
                    .beginGroup().equalTo("uType", 2).or().equalTo("uType", 4).endGroup().sort("tag", Sort.ASCENDING).findAll();
            res = realm.copyFromRealm(ls);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return res;
    }

    /***
     * 查找是否在好友列表
     * @param userId
     * @return
     */
    public UserInfo findUserInfo4Friend(Long userId) {
        UserInfo res = null;
        Realm realm = DaoUtil.open();
        try {
            UserInfo ls = realm.where(UserInfo.class).equalTo("uType", 2).equalTo("uid", userId).findFirst();
            if (ls != null) {
                res = realm.copyFromRealm(ls);
            }
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return res;
    }

    /***
     * 清除我的所有好友
     */
    public void friendMeDel() {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            RealmResults<UserInfo> ls = realm.where(UserInfo.class).equalTo("uType", 2).sort("tag", Sort.ASCENDING).findAll();
            ls.deleteAllFromRealm();
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    /***
     * 更新好友
     * @param list
     */
    public void friendMeUpdate(List<UserInfo> list) {
        Realm realm = DaoUtil.open();
        IUser user = UserAction.getMyInfo();
        if (user == null) {
            return;
        }
        try {
            realm.beginTransaction();
            RealmResults<UserInfo> ls = realm.where(UserInfo.class).beginGroup().equalTo("uType", 2).or().equalTo("stat", 9).endGroup().findAll();
            for (UserInfo u : ls) {
                boolean isExt = false;
                for (UserInfo userInfo : list) {
                    if (u.getUid().longValue() == userInfo.getUid().longValue()) {//在好友列表中
                        isExt = true;

                        //更新用户相关
                        userInfo.toTag();
                        if (userInfo.getStat() == 9) {
                            userInfo.setuType(ChatEnum.EUserType.ASSISTANT);
                            userInfo.setLastonline(System.currentTimeMillis());
                        } else {
                            userInfo.setuType(ChatEnum.EUserType.FRIEND);
                        }
                        //服务器用户最后在线时间小于本地最后在线时间，则不更新最后在线时间
                        if (u.getLastonline() != null && userInfo != null && userInfo.getLastonline() < u.getLastonline()) {
                            userInfo.setLastonline(u.getLastonline());
                        }
                        //文件传输助手
                        if (user.getUid().longValue() == userInfo.getUid().longValue()) {
                            long uid = userInfo.getUid().longValue();
                            userInfo.setUid(-uid);
                        }
                        realm.copyToRealmOrUpdate(userInfo);
                    }
                }
                if (!isExt) {//不在好友列表中了,身份改成普通人
                    u.setuType(0);
                }
            }
            //更新旧联系人状态
            realm.insertOrUpdate(ls);
            //服务器新加的联系人
            for (UserInfo userInfo : list) {
                boolean isExt = false;
                for (UserInfo u : ls) {
                    if (u.getUid().longValue() == userInfo.getUid().longValue()) {//在好友列表中
                        isExt = true;
                    }
                }
                if (!isExt) {
                    userInfo.toTag();
                    if (userInfo.getStat() == 9) {
                        userInfo.setuType(ChatEnum.EUserType.ASSISTANT);
                        userInfo.setLastonline(System.currentTimeMillis());
                    } else {
                        userInfo.setuType(ChatEnum.EUserType.FRIEND);
                    }
                    //文件传输助手
                    if (user.getUid().longValue() == userInfo.getUid().longValue()) {
                        long uid = userInfo.getUid().longValue();
                        userInfo.setUid(-uid);
                    }
                    realm.insertOrUpdate(userInfo);
                }
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    /***
     * 更新好友
     * @param list
     */
    public void updateRoster(List<UserInfo> list, List<Long> newUserIds) {
        Realm realm = DaoUtil.open();
        try {
            boolean hasBeganTransaction = false;
            //本地数据库中的所有好友
            RealmResults<UserInfo> ls = realm.where(UserInfo.class).beginGroup().equalTo("uType", 2).or().equalTo("stat", 9).endGroup().findAll();
            //需要筛出本地好友中已经不是好友的数据
            if (ls != null) {
                List<Long> localUserIds = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    localUserIds = ls.stream().map(UserInfo::getUid).collect(Collectors.toList());
                    if (localUserIds != null) {
                        newUserIds.removeAll(localUserIds);
                    }
                    if (newUserIds.size() > 0) {
                        //本地不在好友列表中的数据
                        Long[] outUserIds = newUserIds.toArray(new Long[newUserIds.size()]);
                        RealmResults<UserInfo> outUsers = ls.where().in("uid", outUserIds).findAll();
                        hasBeganTransaction = true;
                        realm.beginTransaction();
                        if (outUsers != null) {
                            for (UserInfo info : outUsers) {
                                info.setuType(0);
                            }
                        }
                    }
                } else {
                    List<UserInfo> totalUsers = realm.copyFromRealm(ls);
                    if (totalUsers != null) {
                        totalUsers.removeAll(list);
                        if (totalUsers.size() > 0) {
                            for (UserInfo info : totalUsers) {
                                info.setuType(0);
                            }
                            hasBeganTransaction = true;
                            realm.beginTransaction();
                            realm.insertOrUpdate(totalUsers);
                        }
                    }
                }
            }
            if (!hasBeganTransaction) {
                realm.beginTransaction();
            }
            realm.insertOrUpdate(list);
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    /***
     * 根据key搜索所有的好友
     */
    public List<UserInfo> searchUser4key(String key) {
        Realm realm = DaoUtil.open();
        List<UserInfo> ret = null;
        try {
            ret = new ArrayList<>();
            RealmResults<UserInfo> users = realm.where(UserInfo.class)
                    .equalTo("uType", 2).and()
                    .beginGroup()
                    .contains("name", key).or()
                    .contains("mkName", key).or()
                    .contains("pinyinHead", key)
                    .endGroup().findAll();
            if (users != null)
                ret = realm.copyFromRealm(users);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return ret;
    }


    /***
     * 用户头像,昵称更新
     * @param uid
     * @param head
     * @param name
     */
    public boolean userHeadNameUpdate(Long uid, String head, String name) {
        boolean hasChange = false;
        if (uid == null)
            return false;
        Realm realm = DaoUtil.open();
        try {
            UserInfo u = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
            if (u != null) {
                if (u.getHead().equals(head) && u.getName().equals(name)) {

                } else {
                    hasChange = true;
                    realm.beginTransaction();
                    u.setHead(head);
                    u.setName(name);
                    realm.insertOrUpdate(u);
                    realm.commitTransaction();
                }
            }
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return hasChange;
    }


    /**
     * 更新好友在线状态
     *
     * @param type 0:不在线,1:在线
     * @param time 离线需要更新离线时间
     */
    public UserInfo updateUserOnlineStatus(Long uid, int type, long time) {
        UserInfo info = null;
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            UserInfo user = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
            if (user != null) {
                if (user != null) {
                    user.setActiveType(type);
                    if (type == CoreEnum.ESureType.NO) {
                        user.setLastonline(time);
                    }
                    info = realm.copyFromRealm(user);
                    realm.insertOrUpdate(user);
                }
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return info;
    }

    /***
     * 更新好友
     * @param list
     */
    public void updateUsersOnlineStatus(List<OnlineBean> list) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            if (list != null && list.size() > 0) {
                int len = list.size();
                for (int i = 0; i < len; i++) {
                    OnlineBean bean = list.get(i);
                    if (bean == null) {
                        continue;
                    }
                    UserInfo user = realm.where(UserInfo.class).equalTo("uid", bean.getUid()).findFirst();
                    if (user == null) {//拉黑用户数据库没有？
                        continue;
                    }
                    UserInfo userInfo = realm.copyFromRealm(user);
                    if (userInfo == null) {
                        continue;
                    }
                    if (bean.getLastonline() > userInfo.getLastonline()) {//更新数据时间大于本地数据时间，才更新
                        userInfo.setLastonline(bean.getLastonline());
                    }
                    userInfo.setActiveType(bean.getActiveType());
                    realm.insertOrUpdate(userInfo);
                }
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    /***
     * 删除指定好友
     */
    public void deleteUser(long uid) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            UserInfo userInfo = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
            userInfo.deleteFromRealm();
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }


    /***
     * 检测通讯录是否已经初始化，所有通讯录好友，系统用户
     * @return
     */
    public boolean isRosterInit() {
        boolean isInit = false;
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            UserInfo ls = realm.where(UserInfo.class).beginGroup().equalTo("uType", 2).or().equalTo("uType", 4).endGroup().findFirst();
            if (ls != null) {
                isInit = true;
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return isInit;
    }

    /**
     * 更新好友在线状态
     *
     * @param type 0:不在线,1:在线
     */
    public void updateUserLockRedEnvelope(Long uid, int type) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            UserInfo user = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
            UserInfo userInfo = realm.copyFromRealm(user);
            if (userInfo != null) {
                userInfo.setLockCloudRedEnvelope(type);
                realm.insertOrUpdate(userInfo);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    public boolean isUserExist(Long uid) {
        boolean result = false;
        Realm realm = DaoUtil.open();
        try {
            UserInfo user = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
            if (user != null) {
                result = true;
            }
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return result;
    }

    /**
     * 更新好友截屏通知开关
     *
     * @param type 0:未开启,1:开启
     */
    public void updateUserSnapshot(long uid, int type) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            UserInfo user = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
            if (user != null) {
                user.setScreenshotNotification(type);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    public void getMemberUserName(List<MemberUser> users) {
        if (users == null || users.size() < 1) {
            return;
        }
        Realm realm = DaoUtil.open();
        try {
            int len = users.size();
            for (int i = 0; i < len; i++) {
                MemberUser member = users.get(i);
                UserInfo user = realm.where(UserInfo.class).equalTo("uid", member.getUid()).findFirst();
                if (user != null && !TextUtils.isEmpty(user.getMkName())) {
                    member.setMarkerName(user.getMkName());
                }
            }
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    public List<PhoneBean> getLocaPhones() {
        List<PhoneBean> res = null;
        Realm realm = DaoUtil.open();
        try {
            RealmResults<PhoneBean> ls = realm.where(PhoneBean.class).findAll();
            res = realm.copyFromRealm(ls);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return res;
    }

    public void updateLocaPhones(List<PhoneBean> list) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            RealmResults<PhoneBean> ls = realm.where(PhoneBean.class).findAll();
            ls.deleteAllFromRealm();
            realm.insertOrUpdate(list);
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    /**
     * 更新用户注销状态
     *
     * @param type 0 取消注销 1 注销中 -1 完成注销
     */
    public void updateUserDeactivateValue(long uid, int type) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            UserInfo user = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
            if (user != null) {
                user.setFriendDeactivateStat(type);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }
}
