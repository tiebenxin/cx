package com.yanlong.im.repository;


import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.data.local.ApplicationLocalDataSource;
import com.yanlong.im.user.bean.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/4/8 0008
 * @description application仓库
 */
public class ApplicationRepository {
    private ApplicationLocalDataSource localDataSource;
    //会话消息
    public RealmResults<Session> sessions = null;
    //通讯录好友
    public RealmResults<UserInfo> friends = null;
    private List<SessionChangeListener> mSessionChangeListeners = new ArrayList<>();
    //session 会话分页
    private final int PAGE_COUNT = 100;
    private int currentCount = 0;//currentCount是PAGE_COUNT倍数

    //通讯录分页
    private final int FRIEND_PAGE_COUNT = 1000;
    private int currentFriendCount = 0;//currentFriendCount是FRIEND_PAGE_COUNT倍数

    //session sid/position 解决主页频繁刷新问题
    public Map<String, Integer> sessionSidPositons = new HashMap<>();

    public ApplicationRepository() {
        localDataSource = new ApplicationLocalDataSource();
        loadMoreFriends();
        loadMoreSessions();
        localDataSource.updateSessionDetail(PAGE_COUNT);
    }

    /**
     * 通知更新阅后即焚队列
     */
    public void notifyBurnQuene() {
        localDataSource.notifyBurnQuene();
    }

    public void addSessionChangeListener(SessionChangeListener sessionChangeListener) {
        mSessionChangeListeners.add(sessionChangeListener);
    }

    public void removeSessionChangeListener(SessionChangeListener sessionChangeListener) {
        if (mSessionChangeListeners.contains(sessionChangeListener))
            mSessionChangeListeners.remove(sessionChangeListener);
    }

    public synchronized void loadMoreSessions() {
        //是PAGE_COUNT的倍数才加载
        currentCount = currentCount + PAGE_COUNT;
        if (sessions != null) sessions.removeAllChangeListeners();
        sessions = localDataSource.getSessions(currentCount);
        /**集合通知OrderedRealmCollectionChangeListener
         * 该对象保存有关受删除，插入和更改影响的索引的信息。
         *
         * 前两个删除和插入记录已添加到集合中或从集合中删除的对象的索引。在将对象添加到Realm或Realm删除对象时会考虑到这一点。
         * 对于RealmResults，当您过滤特定值并且对象已更改以使其现在与查询匹配或不再匹配时，这也适用。
         */
        sessions.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Session>>() {
            @Override
            public void onChange(RealmResults<Session> sessions, OrderedCollectionChangeSet changeSet) {
                currentCount = sessions.size();
                int sessionIndex = 0;
                /***** 异步查询第一次返回。*******************************************************************************************/
                {
                    if (changeSet == null || changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {
//                    notifyDataSetChanged();
                        sessionSidPositons.clear();
                        ArrayList<String> sids = new ArrayList<String>();
                        sids.clear();
                        sessionIndex = 0;
                        for (Session session : sessions) {
                            sids.add(session.getSid());
                            sessionSidPositons.put(session.getSid(), sessionIndex);
                            sessionIndex++;
                        }

                        //1.更新detail
                        if (sids.size() > 0) {
                            localDataSource.updateSessionDetail(sids.toArray(new String[sids.size()]));
                        }
                        //通知监听器
                        for (SessionChangeListener sessionChangeListener : mSessionChangeListeners) {
                            sessionChangeListener.init(sessions, sids);
                        }
                        return;
                    }
                }
                //更新位置信息
                if (changeSet.getDeletionRanges().length > 0 || changeSet.getInsertionRanges().length > 0) {
                    sessionSidPositons.clear();
                    sessionIndex = 0;
                    for (Session session : sessions) {
                        sessionSidPositons.put(session.getSid(), sessionIndex);
                        sessionIndex++;
                    }
                }

                /*****删除了数据，对于删除，必须以相反的顺序通知适配器。*******************************************************************************************/
                {

//                    notifyItemRangeRemoved(range.startIndex, range.length);
                    if (changeSet.getDeletions().length > 0) {
                        //1.删除-不需要更新detail
                        //2.通知监听器
                        for (SessionChangeListener listener : mSessionChangeListeners) {
                            listener.delete(changeSet.getDeletions());
                        }
                    }
                }

                /*****增加了数据*******************************************************************************************/
                {

                    int[] insertions = changeSet.getInsertions();
                    ArrayList<String> sids = new ArrayList<String>();
                    //获取更新信息
                    for (int position : insertions) {
                        sids.add(sessions.get(position).getSid());
//                    notifyItemRangeInserted(range.startIndex, range.length);
                    }

                    if (insertions.length > 0) {
                        //1.更新增加数据的detail详情
                        localDataSource.updateSessionDetail(sids.toArray(new String[sids.size()]));
                        //2.通知监听器
                        for (SessionChangeListener listener : mSessionChangeListeners) {
                            listener.insert(insertions, sids);
                        }
                    }

                }
                /*****数据更改*******************************************************************************************/
                {
                    int[] modifications = changeSet.getChanges();
                    ArrayList<String> sids = new ArrayList<String>();
                    //获取更新信息
                    for (int position : modifications) {
                        sids.add(sessions.get(position).getSid());
//                    notifyItemRangeChanged(range.startIndex, range.length);
                    }

                    if (modifications.length > 0) {
                        //1.更新增加更改的detail详情
                        localDataSource.updateSessionDetail(sids.toArray(new String[sids.size()]));
                        //2.通知监听器
                        for (SessionChangeListener listener : mSessionChangeListeners) {
                            listener.update(modifications, sids);
                        }
                    }
                }
            }
        });
    }

    public Realm getRealm() {
        return localDataSource.getRealm();
    }

    /**
     * 获取session 列表-异步
     *
     * @return
     */
    public RealmResults<Session> getSesisons() {
        return sessions;
    }

    public synchronized void loadMoreFriends() {
        //是PAGE_COUNT的倍数才加载
        currentFriendCount = currentFriendCount + FRIEND_PAGE_COUNT;
        friends = localDataSource.getFriends(currentFriendCount);
        friends.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<UserInfo>>() {
            @Override
            public void onChange(RealmResults<UserInfo> userInfos, OrderedCollectionChangeSet changeSet) {
                currentFriendCount = userInfos.size();
            }
        });
    }

    /**
     * 更新指定主键的
     *
     * @param sids
     */
    public void updateSessionDetail(String[] sids) {
        localDataSource.updateSessionDetail(sids);
    }

    /**
     * 保存当前会话退出即焚消息，endTime到数据库-自动会加入焚队列，存入数据库
     */
    public void saveExitSurvivalMsg(String gid, Long userid) {
        localDataSource.saveExitSurvivalMsg(gid, userid);
    }

    /**
     * 更新指定一些消息对应的session详情
     *
     * @param
     */
    public void updateSessionDetail(String[] gids, Long[] uids) {
        //回主线程调用更新session详情
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                //更新Detail详情
                localDataSource.updateSessionDetail(gids, uids);
            }
        });
    }

    /**
     * 清除会话详情的内容
     */
    public void clearSessionDetailContent(List<String> gids, List<Long> uids) {
        //回主线程调用清除session详情
        //更新Detail详情
        localDataSource.clearContent(gids.toArray(new String[gids.size()]), uids.toArray(new Long[uids.size()]));
    }

    /**
     * 更新指定一些消息对应的session详情
     *
     * @param
     */
    public void updateSessionDetail(List<String> gids, List<Long> uids) {
        updateSessionDetail(gids.toArray(new String[gids.size()]), uids.toArray(new Long[uids.size()]));
    }

    public RealmResults<UserInfo> getFriends() {
        return friends;
    }

    public void deleteSession(String sid) {
        if (sessionSidPositons.containsKey(sid)) {
            Session session = sessions.get(sessionSidPositons.get(sid));
            String gid = session.getGid();
            Long uid = session.getFrom_uid();
            localDataSource.beginTransaction();
            session.deleteFromRealm();
            localDataSource.commitTransaction();
            localDataSource.deleteAllMsg(sid, uid, gid);
        }
    }

    public void deleteSession(Long uid, String gid) {
        if (TextUtils.isEmpty(gid)) {
            Session session = sessions.where().equalTo("from_uid", uid).findFirst();
            if (session != null && !TextUtils.isEmpty(session.getSid())) {
                String sid = session.getSid();
                localDataSource.beginTransaction();
                session.deleteFromRealm();
                localDataSource.commitTransaction();
                localDataSource.deleteAllMsg(sid, uid, gid);
            }
        } else {
            Session session = sessions.where().equalTo("gid", gid).findFirst();
            if (session != null && !TextUtils.isEmpty(session.getSid())) {
                String sid = session.getSid();
                localDataSource.beginTransaction();
                session.deleteFromRealm();
                localDataSource.commitTransaction();
                localDataSource.deleteAllMsg(sid, uid, gid);
            }
        }

    }

    public void onDestory() {
        sessions.removeAllChangeListeners();
        mSessionChangeListeners.clear();
        localDataSource.onDestory();
        friends.removeAllChangeListeners();
        sessions = null;
        friends = null;

        mSessionChangeListeners = null;
        localDataSource = null;
    }

    public interface SessionChangeListener {
        //第一次初始化数据，因分页加载，session对象会更新，使用sessions对象时，每次在init方法里重新赋值
        void init(RealmResults<Session> sessions, List<String> sids);

        //有数据删除
        void delete(int[] positions);

        //有数据新增
        void insert(int[] positions, List<String> sids);

        //有数据更新
        void update(int[] positions, List<String> sids);
    }
}


