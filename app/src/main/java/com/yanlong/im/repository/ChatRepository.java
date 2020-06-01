package com.yanlong.im.repository;


import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.data.local.ChatLocalDataSource;
import com.yanlong.im.user.bean.UserInfo;

import io.realm.RealmResults;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/23 0023
 * @description
 */
public class ChatRepository {
    private ChatLocalDataSource localDataSource;
    public ChatRepository() {
        localDataSource = new ChatLocalDataSource();
    }
    /**
     * 获取群信息
     * @param gid
     * @return
     */
    public Group getGroup(String gid){
        return localDataSource.getGroup(gid);
    }

    /**
     * 获取好友信息
     * @param uid
     * @return
     */
    public UserInfo getFriend(Long uid){
        return localDataSource.getFriend(uid);
    }

    /**
     * 异步获取好友发送的待焚消息
     */
    public RealmResults<MsgAllBean> getToAddBurnForDBMsgs(String toGid, Long toUid) {
        return localDataSource.getToAddBurnForDBMsgsAsync(toGid, toUid);
    }

    /**
     * 阅后即焚消息批量添加到数据库，异步事务
     * @param toGid
     * @param toUid
     */
    public void dealToBurnMsgs(String toGid, Long toUid){
        localDataSource.dealToBurnMsgs(toGid,toUid);
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
