package com.yanlong.im.chat.ui;

import android.arch.lifecycle.ViewModel;

import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.repository.GroupSaveRepository;

import io.realm.RealmResults;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/27 0027
 * @description
 */
public class GroupSaveViewModel extends ViewModel {
    private GroupSaveRepository repository;
    public RealmResults<Group> groups = null;
    public GroupSaveViewModel(){
        repository = new GroupSaveRepository();
        groups = repository.getGroups();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        groups.removeAllChangeListeners();
        groups = null;
        repository.onDestory();
    }
}
