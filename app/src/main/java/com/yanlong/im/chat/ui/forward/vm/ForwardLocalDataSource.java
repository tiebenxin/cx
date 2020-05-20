package com.yanlong.im.chat.ui.forward.vm;

import android.util.Log;

import com.luck.picture.lib.tools.DateUtils;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/3/26 0026
 * @description MainActivity 本地数据源
 */
public class ForwardLocalDataSource {
    private Realm realm = null;

    public ForwardLocalDataSource() {
        realm = DaoUtil.open();
    }

    /**
     * 获取session 列表
     *
     * @return
     */
    public RealmResults<Session> getSession() {
        RealmResults<Session> list = null;
        try {
            String[] orderFiled = {"isTop", "up_time"};
            Sort[] sorts = {Sort.DESCENDING, Sort.DESCENDING};
            list = realm.where(Session.class).sort(orderFiled, sorts).findAll();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.reportException(e);
        }
        return list;
    }

    /**
     * 获取session 列表
     *
     * @return
     */
    public RealmResults<UserInfo> getUsers() {
        RealmResults<UserInfo> list = null;
        try {
            list= realm.where(UserInfo.class).equalTo("uType", 2).sort("tag", Sort.ASCENDING).findAll();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.reportException(e);
        }
        return list;
    }


    /**
     * 数据库开始事务处理
     */
    public void beginTransaction() {
        realm.beginTransaction();
    }

    /**
     * 数据库提交事务处理
     */
    public void commitTransaction() {
        realm.commitTransaction();
    }

    /**
     * onResume检查realm状态,避免系统奔溃后，主页重新启动realm对象已被关闭，需重新连接
     */
    public boolean checkRealmStatus() {
        boolean result = true;
        if (realm == null || realm.isClosed()) {
            result = false;
            realm = DaoUtil.open();
        }
        return result;
    }

    public void onDestory() {
        if (realm != null) {
            DaoUtil.close(realm);
        }
    }
}
