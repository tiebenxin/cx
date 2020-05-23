package com.yanlong.im.repository;


import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.data.local.ChatLocalDataSource;
import com.yanlong.im.user.bean.UserInfo;

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
