package com.yanlong.im.data.local;

import android.util.Log;

import com.luck.picture.lib.tools.DateUtils;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.utils.DaoUtil;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/3/26 0026
 * @description MainActivity 本地数据源
 */
public class MainLocalDataSource {
    private Realm realm = null;
    private UpdateSessionDetail updateSessionDetail = null;

    public MainLocalDataSource() {
        realm = DaoUtil.open();
        updateSessionDetail=new UpdateSessionDetail(realm);
    }
    public void updateSessionDetail(){
        updateSessionDetail.update();
    }

    /**
     * 获取session 列表
     *
     * @return
     */
    public RealmResults<Session> getSession() {
        RealmResults<Session> list = null;
        try {
            String [] orderFiled = {"isTop","up_time"};
            Sort [] sorts = {Sort.DESCENDING, Sort.DESCENDING};
            list = realm.where(Session.class).sort(orderFiled,sorts).findAll();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.reportException(e);
        }
        Log.e("raleigh_test","getSession"+list.size()+",t="+ DateUtils.timeStamp2Date(DateUtils.getSystemTime(),null));
        return list;
    }
    public RealmResults<SessionDetail> getSessionMore(){
       return  realm.where(SessionDetail.class).findAllAsync();
    }



    /**
     * 数据库开始事务处理
     */
    public void beginTransaction(){
        realm.beginTransaction();
    }
    /**
     * 数据库提交事务处理
     */
    public void commitTransaction(){
        realm.commitTransaction();
    }
    public void onDestory() {
        updateSessionDetail=null;
        if (realm != null) {
            DaoUtil.close(realm);
        }
    }
}
