package com.yanlong.im.chat.ui.forward.vm;

import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.user.bean.UserInfo;

import io.realm.RealmResults;

/**
 * @author Liszt
 * @date 2020/4/8
 * Description
 */
public class ForwardRepository {
    private ForwardLocalDataSource localDataSource;

    public ForwardRepository() {
        localDataSource = new ForwardLocalDataSource();
    }

    public RealmResults<Session> getSessions() {
        return localDataSource.getSession();
    }

    public RealmResults<UserInfo> getUsers() {
        return localDataSource.getUsers();
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

    public void onDestroy() {
        localDataSource.onDestory();
    }

    /**
     * onResume检查realm状态,避免系统奔溃后，主页重新启动realm对象已被关闭，需重新连接
     */
    public boolean checkRealmStatus() {
        return localDataSource.checkRealmStatus();
    }
}
