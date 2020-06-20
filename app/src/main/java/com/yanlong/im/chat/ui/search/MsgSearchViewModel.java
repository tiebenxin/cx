package com.yanlong.im.chat.ui.search;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.repository.MsgSearchRepository;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/22 0022
 * @description
 */
public class MsgSearchViewModel extends ViewModel {
    private MsgSearchRepository repository = new MsgSearchRepository();
    public MutableLiveData<String> key = new MutableLiveData<String>();
    public RealmResults<UserInfo> searchFriends = null;
    public RealmResults<Group> searchGroups = null;
    public List<SessionDetail> searchSessions = new CopyOnWriteArrayList<>();
    public Map<String, SessionSearch> sessionSearch = new ConcurrentHashMap<>();
    public MutableLiveData<Boolean> isLoadNewRecord = new MutableLiveData<>();
    public int MIN_LIMIT = 4;
    public ThreadPoolExecutor executor = null;
    private RealmResults<Session> allSessions = null;
    //是否进主页显示加载动画
    private boolean isFriendLoadCompleted = false;
    private boolean isGroupLoadCompleted = false;
    private boolean isSessionLoadCompleted = false;
    private AtomicInteger searchCompletedCount = new AtomicInteger(0);


    public void clear() {
        isFriendLoadCompleted = false;
        isGroupLoadCompleted = false;
        isSessionLoadCompleted = false;
        //停止并发任务
        stopConcurrentTask();
        searchCompletedCount.set(0);
        if (searchFriends != null) searchFriends.removeAllChangeListeners();
        if (searchGroups != null) searchGroups.removeAllChangeListeners();
        if (allSessions != null) allSessions.removeAllChangeListeners();
        sessionSearch.clear();

        searchFriends = null;
        searchGroups = null;
        allSessions = null;
        searchSessions.clear();
        isLoadNewRecord.setValue(false);
    }

    /**
     * 是否所有都搜索完了
     *
     * @return
     */
    public boolean isLoadCompleted(MsgSearchAdapter.SearchType type) {
        boolean result = false;
        switch (type) {
            case ALL:
                result = isFriendLoadCompleted && isGroupLoadCompleted && isSessionLoadCompleted;
                break;
            case GROUPS:
                result = isGroupLoadCompleted;
                break;
            case FRIENDS:
                result = isFriendLoadCompleted;
                break;
            case SESSIONS:
                result = isSessionLoadCompleted;
                break;
        }
        return result;

    }

    /**
     * 停止并发任务-session聊天记录查询
     */
    public void stopConcurrentTask() {
        //停止离线任务，
        if (executor != null && executor.getActiveCount() > 0) {
            /**向线程池中所有的线程发出中断(interrupted)。
             * 会尝试interrupt线程池中正在执行的线程
             * 等待执行的线程也会被取消
             * 但是并不能保证一定能成功的interrupt线程池中的线程。可能必须要等待所有正在执行的任务都执行完成了才能退出
             */
            executor.shutdownNow();
            executor = null;
        }
    }

    public void search(String searchKey) {
        if (!TextUtils.isEmpty(searchKey)) {
            /**查询好友*******************************************************************************/
            searchFriends = repository.searchFriends(searchKey, MIN_LIMIT);
            searchFriends.addChangeListener((userInfos, changeSet) -> {
                if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {//初次加载完成刷新
                    isFriendLoadCompleted = true;
                    isLoadNewRecord.setValue(true);

                }
            });
            /**查询群*******************************************************************************/
            searchGroups = repository.searchGroups(searchKey, MIN_LIMIT);
            searchGroups.addChangeListener((userInfos, changeSet) -> {
                if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {//初次加载完成刷新
                    isGroupLoadCompleted = true;
                    isLoadNewRecord.setValue(true);
                }
            });

            /**查询session*******************************************************************************/
            if (executor == null) {
                executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            }
            allSessions = repository.searchSessions();
            if (allSessions != null)
                allSessions.addChangeListener((sessions, changeSet) -> {
                    if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {
                        for (Session session : sessions) {
                            if (sessionSearch.size() >= MIN_LIMIT) {//已经查到4个了，直接不启动并发任务了
                                isSessionLoadCompleted = true;
                                //搜索全部时，最大数量只要查4个就够了，停止并发任务
                                stopConcurrentTask();
                                break;
                            } else {
                                String gid = session.getGid();
                                Long uid = session.getFrom_uid();
                                String sid = session.getSid();
                                //开始并发查询
                                startConcurrentTask(searchKey, gid, uid, sid, MIN_LIMIT, sessions.size());

                            }
                        }
                    }
                });
        }
    }

    public void searchFriends(String searchKey) {
        if (!TextUtils.isEmpty(searchKey)) {
            searchFriends = repository.searchFriends(searchKey, null);
            searchFriends.addChangeListener((userInfos, changeSet) -> {
                if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {//初次加载完成刷新
                    isFriendLoadCompleted = true;
                    isLoadNewRecord.setValue(true);

                }
            });
        }
    }

    public void searchGroups(String searchKey) {
        if (!TextUtils.isEmpty(searchKey)) {
            searchGroups = repository.searchGroups(searchKey, null);
            searchGroups.addChangeListener((userInfos, changeSet) -> {
                if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {//初次加载完成刷新
                    isGroupLoadCompleted = true;
                    isLoadNewRecord.setValue(true);

                }
            });
        }
    }

    public void searchSessions(String searchKey) {
        if (!TextUtils.isEmpty(searchKey)) {
            if (executor == null) {
                executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            }
            allSessions = repository.searchSessions();
            if (allSessions != null)
                allSessions.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Session>>() {
                    @Override
                    public void onChange(RealmResults<Session> sessions, OrderedCollectionChangeSet changeSet) {
                        if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {
                            for (Session session : sessions) {
                                String gid = session.getGid();
                                Long uid = session.getFrom_uid();
                                String sid = session.getSid();
                                startConcurrentTask(searchKey, gid, uid, sid, null, sessions.size());
                            }
                        }
                    }
                });
        }
    }


    /**
     * 开始并发查询-session聊天记录
     *
     * @param searchKey
     * @param gid
     * @param uid
     * @param sid
     */
    private void startConcurrentTask(String searchKey, String gid, Long uid, String sid, Integer maxCount, int totalSession) {
        if (executor != null) executor.execute(() -> {
            Realm realm = DaoUtil.open();
            try {
                long count = repository.searchMessagesCount(realm, searchKey, gid, uid);
                //并发线程自增
                if (searchKey.equals(key.getValue())) {
                    searchCompletedCount.getAndIncrement();
                    if (count > 0) {
                        //避免任务未中断成功，还在执行上一个搜索的任务，这里做下过滤
                        SessionSearch result = new SessionSearch(count, gid, uid);
                        if (count == 1) {//1个消息
                            result.setMsgAllBean(repository.searchMessages(realm, searchKey, gid, uid));
                        }
                        SessionDetail sessionDeta = repository.getSessionDetail(realm, sid);
                        if (searchKey.equals(key.getValue())) {
                            //避免任务未中断成功，还在执行上一个搜索的任务，这里做下过滤
                            sessionSearch.put(sid, result);
                            searchSessions.add(sessionDeta);
                            if (maxCount != null && sessionSearch.size() >= maxCount) {
                                isSessionLoadCompleted = true;
                                //搜索全部时，最大数量只要查4个就够了，停止并发任务
                                stopConcurrentTask();
                            }
                            isLoadNewRecord.postValue(true);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (searchCompletedCount.get() >= totalSession) {//查完了所有session
                    isSessionLoadCompleted = true;
                    isLoadNewRecord.postValue(true);
                }
                DaoUtil.close(realm);
            }
        });
    }

    public int getSearchFriendsSize() {
        return searchFriends == null ? 0 : searchFriends.size();
    }

    public int getSearchGroupsSize() {
        return searchGroups == null ? 0 : searchGroups.size();
    }

    public int getSearchSessionsSize() {
        return searchSessions == null ? 0 : searchSessions.size();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clear();
        repository.onDestory();

        repository = null;
        sessionSearch = null;
    }

    public class SessionSearch {
        public SessionSearch(long count, String gid, long uid) {
            this.count = count;
            this.gid = gid;
            this.uid = uid;
        }

        private long count = 0;
        private String gid;
        private long uid;
        private MsgAllBean msgAllBean = null;


        public MsgAllBean getMsgAllBean() {
            return msgAllBean;
        }

        public void setMsgAllBean(MsgAllBean msgAllBean) {
            this.msgAllBean = msgAllBean;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }
    }
}
