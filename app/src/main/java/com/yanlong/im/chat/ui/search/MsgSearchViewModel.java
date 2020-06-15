package com.yanlong.im.chat.ui.search;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.repository.MsgSearchRepository;
import com.yanlong.im.user.bean.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    }

    public void search(String key) {
        searchFriends = repository.searchFriends(key);
        searchGroups = repository.searchGroups(key);
        RealmResults<Session> sessions = repository.searchSessions();
        //单独用 list,保证顺序
        List<String> sids = new ArrayList<>();
        for (Session session : sessions) {
            long count = repository.searchMessagesCount(key, session.getGid(), session.getFrom_uid());
            if (count > 0) {
                SessionSearch result = new SessionSearch(count, session.getGid(), session.getFrom_uid());
                if (count == 1) {//1个消息
                    result.setMsgAllBean(repository.searchMessages(key, session.getGid(), session.getFrom_uid()));
                }
                sids.add(session.getSid());
                sessionSearch.put(session.getSid(), result);
                if (sids.size() > 20) {
                    sids.clear();
                    searchSessions.addAll(repository.getSessionDetails(sids.toArray(new String[sids.size()])));
                    isLoadNewRecord.setValue(true);
                }
            }
        }
        if (sids.size() > 0) {
            searchSessions.addAll(repository.getSessionDetails(sids.toArray(new String[sids.size()])));
            isLoadNewRecord.setValue(true);
        }
        sids.clear();
        sids = null;
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
