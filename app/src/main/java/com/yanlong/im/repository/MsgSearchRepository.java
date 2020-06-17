package com.yanlong.im.repository;

import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.data.local.MsgSearchLocalDataSource;
import com.yanlong.im.user.bean.UserInfo;

import java.util.List;

import io.realm.Realm;
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
    public RealmResults<UserInfo> searchFriends(String searchKey,Integer limit){
        return localDataSource.searchFriends(searchKey,limit);
    }
    /**
     * 搜索群名 和群成员名
     * @param searchKey
     * @return
     */
    public RealmResults<Group> searchGroups(String searchKey,Integer limit){
        return localDataSource.searchGroups(searchKey,limit);
    }
    /**
     * 搜索所有session
     *
     * @return
     */
    public RealmResults<Session> searchSessions(Realm realm,long timeStamp, int limit) {
        return localDataSource.searchSessions(realm,timeStamp,limit);
    }
    /**
     * 获取满足条件的sessionDetail
     *
     * @return
     */
    public List<SessionDetail> getSessionDetails(Realm realm,String[] sids) {
        return localDataSource.getSessionDetails(realm,sids);
    }
    /**
     * 搜索聊天记录
     *
     *
     * @param searchKey
     * @return
     */
    public long searchMessagesCount(Realm realm,String searchKey,String gid,long uid) {
        return localDataSource.searchMessagesCount(realm,searchKey,gid,uid);
    }
    /**
     * 搜索聊天记录匹配数量为1时的消息
     *
     * @param key
     * @return
     */
    public MsgAllBean searchMessages(Realm realm,String key, String gid, long uid) {
        return localDataSource.searchMessages(realm,key, gid, uid);
    }
    public void onDestory() {
        localDataSource.onDestory();
    }
    public Realm getRealm() {
        return localDataSource.getRealm();
    }
}
