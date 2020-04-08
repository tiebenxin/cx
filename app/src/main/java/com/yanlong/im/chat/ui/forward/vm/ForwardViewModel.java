package com.yanlong.im.chat.ui.forward.vm;

import android.arch.lifecycle.ViewModel;

import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.user.bean.UserInfo;

import io.realm.RealmResults;

/**
 * @author Liszt
 * @date 2020/4/8
 * Description
 */
public class ForwardViewModel extends ViewModel {
    private ForwardRepository repository;
    public RealmResults<Session> sessions;
    public RealmResults<UserInfo> users;


    public ForwardViewModel() {
        repository = new ForwardRepository();
        init();
    }

    private void init() {
        sessions = repository.getSessions();
        users = repository.getUsers();
    }


    /**
     * onResume检查realm状态,避免系统奔溃后，主页重新启动realm对象已被关闭，需重新连接
     */
    public void checkRealmStatus() {
        if (!repository.checkRealmStatus()) {
            init();
        }
    }

    public void onDestroy() {
        repository.onDestroy();
    }

}
