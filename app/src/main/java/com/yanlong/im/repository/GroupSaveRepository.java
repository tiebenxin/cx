package com.yanlong.im.repository;

import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.data.local.GroupSaveLocalDataSource;

import io.realm.RealmResults;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/27 0027
 * @description
 */
public class GroupSaveRepository {
    private GroupSaveLocalDataSource localDataSource;
    public GroupSaveRepository() {
        localDataSource = new GroupSaveLocalDataSource();
    }
    /**
     * 获取已保存的群聊
     * @return
     */
    public RealmResults<Group> getGroups(){
        return localDataSource.getGroups();
    }
    /**
     * 数据库开始事务处理
     */
    public void beginTransaction() {
        localDataSource.beginTransaction();
    }

    /**
     * 数据库提交事务处理
     */
    public void commitTransaction() {
        localDataSource.commitTransaction();
    }

    public void onDestory() {
        localDataSource.onDestory();
    }
    /**
     * onResume检查realm状态,避免系统奔溃后，主页重新启动realm对象已被关闭，需重新连接
     */
    public boolean checkRealmStatus(){
        return localDataSource.checkRealmStatus();
    }
}
