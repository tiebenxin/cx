package com.yanlong.im.repository;

import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.data.local.MainLocalDataSource;

import io.realm.RealmResults;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/3/26 0026
 * @description MainActivity 数据仓库
 */
public class MainRepository {
    private MainLocalDataSource localDataSource;

    public MainRepository() {
        localDataSource = new MainLocalDataSource();
    }

    /**
     * 获取群信息
     *
     * @param gid
     * @return
     */
    public Group getGroup4Id(String gid) {
        return localDataSource.getGroup4Id(gid);
    }

    /**
     * 获取session 列表
     *
     * @return
     */
    public RealmResults<Session> getSesisons() {
        return localDataSource.getSession();
    }

    /**
     * 获取session 详情
     *
     * @return
     */
    public RealmResults<SessionDetail> getSessionMore() {
        return localDataSource.getSessionMore();
    }

    public String getSessionJson(RealmResults<Session> sessions) {
        return localDataSource.getSessionJson(sessions);
    }

    /**
     * 更新详情
     */
    public void updateSessionDetail() {
        localDataSource.updateSessionDetail();
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

    public void deleteAllMsg(Long uid, String gid) {
        localDataSource.deleteAllMsg(uid, gid);
    }
}
