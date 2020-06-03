package com.yanlong.im.chat.ui.search;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.yanlong.im.chat.bean.Group;
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
    public RealmResults<SessionDetail> searchSessions = null;
    public Map<String, SessionSearch> sessionSearch = new HashMap<>();

    public void clear() {
        if (searchFriends != null) searchFriends.removeAllChangeListeners();
        if (searchGroups != null) searchGroups.removeAllChangeListeners();
        if (searchSessions != null) searchSessions.removeAllChangeListeners();
        sessionSearch.clear();

        searchFriends = null;
        searchGroups = null;
        searchSessions = null;
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
                sids.add(session.getSid());
                sessionSearch.put(session.getSid(), new SessionSearch(count,session.getGid(), session.getFrom_uid()));
            }
        }
        searchSessions = repository.getSessionDetails(sids.toArray(new String[sids.size()]));
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
        public SessionSearch(long count,String gid,long uid){
            this.count = count;
            this.gid = gid;
            this.uid = uid;
        }
        private long count = 0;
        private String gid;
        private long uid;

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
