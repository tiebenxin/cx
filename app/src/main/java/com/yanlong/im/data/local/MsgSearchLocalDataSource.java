package com.yanlong.im.data.local;

import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/22 0022
 * @description
 */
public class MsgSearchLocalDataSource {
    private Realm realm;
    public MsgSearchLocalDataSource() {
        realm = DaoUtil.open();
    }

    /**
     * 搜索好友昵称、备注名
     * @param searchKey
     * @return
     */
    public RealmResults<UserInfo> searchFriends(String searchKey){
        return realm.where(UserInfo.class).like("name",searchKey).or().like("mkName",searchKey)
                .findAll();
    }

    /**
     * 搜索群名 和群成员名
     * @param searchKey
     * @return
     */
    public RealmResults<Group> searchGroups(String searchKey){
        return realm.where(Group.class).like("name",searchKey).or()
                .like("members.membername",searchKey)
                .findAll();
    }
    /**
     * 数据库开始事务处理
     */
    public void beginTransaction() {
        realm.beginTransaction();
    }

    /**
     * 数据库提交事务处理
     */
    public void commitTransaction() {
        realm.commitTransaction();
    }


    /**
     * onResume检查realm状态,避免系统奔溃后，主页重新启动realm对象已被关闭，需重新连接
     */
    public boolean checkRealmStatus() {
        boolean result = true;
        if (realm == null || realm.isClosed()) {
            result = false;
            realm = DaoUtil.open();
        }
        return result;
    }

    public void onDestory() {
        if (realm != null) {
            if (realm != null) {
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                realm.close();
            }
        }
    }

}
