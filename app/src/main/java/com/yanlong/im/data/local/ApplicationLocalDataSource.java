package com.yanlong.im.data.local;

import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.utils.DaoUtil;

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
        updateSessionDetail=new UpdateSessionDetail(realm);
    }

    public Realm getRealm() {
        return realm;
    }

    /**
     * 更新全部
     */
    public void updateSessionDetail() {
        updateSessionDetail.update();
    }

    /**
     * 更新指定主键的
     * @param sids
     */
    public void updateSessionDetail(String[] sids){
        updateSessionDetail.update(sids);
    }

    /**
     * 获取session 列表-异步
     *
     * @return
     */
    public RealmResults<Session> getSessions() {
        String[] orderFiled = {"isTop", "up_time"};
        Sort[] sorts = {Sort.DESCENDING, Sort.DESCENDING};
        return realm.where(Session.class).sort(orderFiled, sorts).findAllAsync();
    }

    public void onDestory() {
        if (realm != null) {
            DaoUtil.close(realm);
        }
    }
}
