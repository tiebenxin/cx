package com.yanlong.im.data.local;

import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.utils.DaoUtil;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/27 0027
 * @description
 */
public class GroupSaveLocalDataSource {
    private Realm realm = null;

    public GroupSaveLocalDataSource() {
        realm = DaoUtil.open();
    }

    /**
     * 获取已保存的群聊
     * @return
     */
    public RealmResults<Group> getGroups(){
       return realm.where(Group.class).equalTo("saved", 1).findAll();
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
