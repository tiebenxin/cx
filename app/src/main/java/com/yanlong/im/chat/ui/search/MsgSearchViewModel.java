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
import java.util.List;
import java.util.Map;

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
    public List<SessionDetail> searchSessions = new ArrayList<>();
    public Map<String, SessionSearch> sessionSearch = new HashMap<>();
    public MutableLiveData<Boolean> isLoadNewRecord = new MutableLiveData<>();

    public void clear() {
        if (searchFriends != null) searchFriends.removeAllChangeListeners();
        if (searchGroups != null) searchGroups.removeAllChangeListeners();
        sessionSearch.clear();

        searchFriends = null;
        searchGroups = null;
        searchSessions.clear();
        isLoadNewRecord.postValue(true);
    }

    public void search(String key) {
        if (!TextUtils.isEmpty(key)) {
            searchFriends = repository.searchFriends(key);
            searchGroups = repository.searchGroups(key);
            searchFriends.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<UserInfo>>() {
                @Override
                public void onChange(RealmResults<UserInfo> userInfos, OrderedCollectionChangeSet changeSet) {
                    if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {//初次加载完成刷新
                        isLoadNewRecord.setValue(true);
                    }
                }
            });
            searchGroups.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Group>>() {
                @Override
                public void onChange(RealmResults<Group> userInfos, OrderedCollectionChangeSet changeSet) {
                    if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {//初次加载完成刷新
                        isLoadNewRecord.setValue(true);
                    }
                }
            });

            DaoUtil.executeTransactionAsync(repository.getRealm(), new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    int limit = 20;
                    long timeStamp = System.currentTimeMillis();
                    RealmResults<Session> sessions = repository.searchSessions(realm, timeStamp, limit);
                    //单独用 list,保证顺序
                    List<String> sids = new ArrayList<>();
                    while (sessions.size() > 0) {
                        for (Session session : sessions) {
                            long count = repository.searchMessagesCount(realm, key, session.getGid(), session.getFrom_uid());
                            if (count > 0) {
                                SessionSearch result = new SessionSearch(count, session.getGid(), session.getFrom_uid());
                                if (count == 1) {//1个消息
                                    result.setMsgAllBean(repository.searchMessages(realm, key, session.getGid(), session.getFrom_uid()));
                                }
                                sids.add(session.getSid());
                                sessionSearch.put(session.getSid(), result);
                            }
                        }
                        if (sids.size() > 0) {
                            searchSessions.addAll(repository.getSessionDetails(realm, sids.toArray(new String[sids.size()])));
                            isLoadNewRecord.postValue(true);
                        }
                        sids.clear();
                        timeStamp = sessions.get(sessions.size() - 1).getUp_time();
                        sessions = repository.searchSessions(realm, timeStamp, limit);
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {

                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    error.printStackTrace();

                }
            });
        }
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
