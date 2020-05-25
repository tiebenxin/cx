package com.yanlong.im.chat.ui.search;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.repository.MsgSearchRepository;
import com.yanlong.im.user.bean.UserInfo;

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

    public void search(String key){
        searchFriends = repository.searchFriends(key);
        searchGroups = repository.searchGroups(key);
//        repository.searchSessions().addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Session>>() {
//            @Override
//            public void onChange(RealmResults<SessionDetail> sessions, OrderedCollectionChangeSet changeSet) {
//                if(changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL){
//                    for(SessionDetail session : sessions){
//                        long count=repository.searchMessagesCount(key,session.getGid(),session.getFrom_uid());
//                        if(count>0){
//                            searchSessions
//                        }
//                    }
//
//                }else if(changeSet.getInsertions().length > 0){//有新增
//                    for(int position: changeSet.getInsertions()){
//                        SessionDetail session = sessions.get(position);
//                        long count=repository.searchMessagesCount(key,session.getGid(),session.getFrom_uid());
//                    }
//                }
//            }
//        });
    }

    public int getSearchFriendsSize(){
        return searchFriends == null ? 0 : searchFriends.size();
    }
    public int getSearchGroupsSize(){
        return searchGroups == null ? 0 : searchGroups.size();
    }
    public int getSearchSessionsSize(){
        return searchSessions == null ? 0 : searchSessions.size();
    }

    public void onDestory(LifecycleOwner owner) {
        key.removeObservers(owner);
        repository.onDestory();
        repository = null;
        if(searchFriends!=null)searchFriends.removeAllChangeListeners();
        if(searchGroups!=null)searchGroups.removeAllChangeListeners();
        if(searchSessions!=null)searchSessions.removeAllChangeListeners();
    }
}
