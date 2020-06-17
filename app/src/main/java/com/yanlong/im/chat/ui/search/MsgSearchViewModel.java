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

    public void clear() {
        if (repository.getRealm().isInTransaction()) {
            repository.getRealm().cancelTransaction();
        }
        if (searchFriends != null) searchFriends.removeAllChangeListeners();
        if (searchGroups != null) searchGroups.removeAllChangeListeners();
        if (allSessions != null) allSessions.removeAllChangeListeners();
        sessionSearch.clear();

        searchFriends = null;
        searchGroups = null;
        allSessions = null;
        searchSessions.clear();
        isLoadNewRecord.postValue(true);
    }

    public void search(String key) {
        if (!TextUtils.isEmpty(key)) {
            searchFriends = repository.searchFriends(key, MIN_LIMIT);
            searchFriends.addChangeListener((userInfos, changeSet) -> {
                if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {//初次加载完成刷新
                    isLoadNewRecord.setValue(true);
                }
            });
            searchGroups = repository.searchGroups(key, MIN_LIMIT);
            searchGroups.addChangeListener((userInfos, changeSet) -> {
                if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {//初次加载完成刷新
                    isLoadNewRecord.setValue(true);
                }
            });

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
                                //开始并发查询
                                startConcurrentTask(key, gid, uid, sid);
                            }
                        }
                    }
                });
        }
    }

    public void searchFriends(String key) {
        if (!TextUtils.isEmpty(key)) {
            searchFriends = repository.searchFriends(key, null);
            searchFriends.addChangeListener((userInfos, changeSet) -> {
                if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {//初次加载完成刷新
                    isLoadNewRecord.setValue(true);
                }
            });
        }
    }

    public void searchGroups(String key) {
        if (!TextUtils.isEmpty(key)) {
            searchGroups = repository.searchGroups(key, null);
            searchGroups.addChangeListener((userInfos, changeSet) -> {
                if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {//初次加载完成刷新
                    isLoadNewRecord.setValue(true);
                }
            });
        }
    }

    public void searchSessions(String key) {
        if (!TextUtils.isEmpty(key)) {
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
                                startConcurrentTask(key, gid, uid, sid);
                            }
                        }
                    }
                });
        }
    }

    /**
     * 开始并发查询session聊天记录
     *
     * @param key
     * @param gid
     * @param uid
     * @param sid
     */
    private void startConcurrentTask(String key, String gid, Long uid, String sid) {
        executor.execute(() -> {
            Realm realm = DaoUtil.open();
            try {
                long count = repository.searchMessagesCount(realm, key, gid, uid);
                if (count > 0) {
                    SessionSearch result = new SessionSearch(count, gid, uid);
                    if (count == 1) {//1个消息
                        result.setMsgAllBean(repository.searchMessages(realm, key, gid, uid));
                    }
                    sessionSearch.put(sid, result);
                    searchSessions.add(repository.getSessionDetail(realm, sid));
                    isLoadNewRecord.postValue(true);
                }
            } finally {
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
