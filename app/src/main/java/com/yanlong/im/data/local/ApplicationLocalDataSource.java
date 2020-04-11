package com.yanlong.im.data.local;

import android.content.Context;

import com.yanlong.im.MyAppLication;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.utils.SharedPreferencesUtil;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/4/8 0008
 * @description
 */
public class ApplicationLocalDataSource {
    private Realm realm = null;
    private UpdateSessionDetail updateSessionDetail = null;

    public ApplicationLocalDataSource() {
        realm = DaoUtil.open();
        updateSessionDetail = new UpdateSessionDetail(realm);
    }

    public Realm getRealm() {
        return realm;
    }

    /**
     * 更新全部 session详情
     */
    public void updateSessionDetail(int limit) {
        updateSessionDetail.update(limit);
    }

    /**
     * 更新指定主键的
     *
     * @param sids
     */
    public void updateSessionDetail(String[] sids) {
        updateSessionDetail.update(sids);
    }

    /**
     * 获取session 列表-异步
     *
     * @return
     */
    public RealmResults<Session> getSessions(int limit) {
        String[] orderFiled = {"isTop", "up_time"};
        Sort[] sorts = {Sort.DESCENDING, Sort.DESCENDING};
        return realm.where(Session.class).sort(orderFiled, sorts).limit(limit).findAllAsync();
    }

    /**
     * 获取session 列表-异步
     *
     * @return
     */
    public RealmResults<UserInfo> getFriends(int limit) {
        boolean isNeedResetFriendTag= MyAppLication.getInstance().getSharedPreferences(SharedPreferencesUtil.SPName.USER_SETTING.toString(), Context.MODE_PRIVATE)
                .getBoolean("isNeedResetFriendTag",true);
        if(isNeedResetFriendTag){
            //兼容老用户，需要第一次将tage-#更改为a,方便排序
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    //批量更改
                    RealmResults<UserInfo> userInfos = realm.where(UserInfo.class)
                            .equalTo("tag", "#")
                            .and()
                            .beginGroup().equalTo("uType", 2).or().equalTo("uType", 4).endGroup()
                            .findAll();
                    //批量更新
                    if (userInfos != null && userInfos.size() > 0) userInfos.setString("tag", UserInfo.FRIEND_NUMBER_TAG);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    //记住，下次不用再重置了
                    MyAppLication.getInstance().getSharedPreferences(SharedPreferencesUtil.SPName.USER_SETTING.toString(), Context.MODE_PRIVATE)
                            .edit().putBoolean("isNeedResetFriendTag",false).apply();
                }
            });
        }

        return realm.where(UserInfo.class)
                .beginGroup().equalTo("uType", 2).or().equalTo("uType", 4).endGroup()
                .sort("tag", Sort.ASCENDING).limit(limit).findAllAsync();
    }

    public void onDestory() {
        if (realm != null) {
            DaoUtil.close(realm);
        }
        realm = null;
        updateSessionDetail = null;
    }
}
