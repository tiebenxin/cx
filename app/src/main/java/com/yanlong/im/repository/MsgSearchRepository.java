package com.yanlong.im.repository;

import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.data.local.MsgSearchLocalDataSource;
import com.yanlong.im.user.bean.UserInfo;

import io.realm.RealmResults;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/22 0022
 * @description
 */
public class MsgSearchRepository {
    private MsgSearchLocalDataSource localDataSource;
    public MsgSearchRepository(){
        localDataSource = new MsgSearchLocalDataSource();
    }
    /**
     * 搜索好友昵称、备注名
     * @param searchKey
     * @return
     */
    public RealmResults<UserInfo> searchFriends(String searchKey){
        return localDataSource.searchFriends(searchKey);
    }
    /**
     * 搜索群名 和群成员名
     * @param searchKey
     * @return
     */
    public RealmResults<Group> searchGroups(String searchKey){
        return localDataSource.searchGroups(searchKey);
    }
    /**
     * 搜索所有session
     *
     * @return
     */
    public RealmResults<SessionDetail> searchSessions() {
        return localDataSource.searchSessions();
    }
    /**
     * 搜索聊天记录
     *
     *
     * @param searchKey
     * @return
     */
    public long searchMessagesCount(String searchKey,String gid,long uid) {
        return localDataSource.searchMessagesCount(searchKey,gid,uid);
    }
    public void onDestory() {
        localDataSource.onDestory();
    }
}
