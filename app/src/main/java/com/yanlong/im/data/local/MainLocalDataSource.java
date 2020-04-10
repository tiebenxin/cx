package com.yanlong.im.data.local;

import android.util.Log;

import com.google.gson.Gson;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.utils.StringUtil;

import io.realm.Realm;
import io.realm.RealmResults;

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
        updateSessionDetail = new UpdateSessionDetail(realm);
    }

    public void updateSessionDetail(String[] sids) {
        updateSessionDetail.update(sids);
    }

    public String getSessionJson(RealmResults<Session> sessions) {
        return new Gson().toJson(realm.copyFromRealm(sessions));
    }

    /**
     * 获取群信息
     *
     * @param gid
     * @return
     */
    public Group getGroup4Id(String gid) {
        return DaoUtil.findOne(Group.class, "gid", gid);
    }

    public RealmResults<SessionDetail> getSessionMore(String[] sids) {
        return realm.where(SessionDetail.class).in("sid",sids).findAllAsync();
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


    public void deleteAllMsg(Long uid, String gid) {
        //异步线程删除
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    RealmResults<MsgAllBean> list;
                    if (StringUtil.isNotNull(gid)) {
                        list = realm.where(MsgAllBean.class)
                                .beginGroup().equalTo("gid", gid).endGroup()
                                .and()
                                .beginGroup().notEqualTo("msg_type", ChatEnum.EMessageType.LOCK).endGroup()
                                .findAll();
                    } else {
                        list = realm.where(MsgAllBean.class)
                                .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                                .and()
                                .beginGroup().notEqualTo("msg_type", ChatEnum.EMessageType.LOCK).endGroup()
                                .and()
                                .beginGroup().equalTo("from_uid", uid).or().equalTo("to_uid", uid).endGroup()
                                .findAll();
                    }

                    //删除前先把子表数据干掉!!切记
                    if (list != null) {
                        MsgDao msgDao = new MsgDao();
                        for (MsgAllBean msg : list) {
                            msgDao.deleteRealmMsg(msg);
                        }
                        list.deleteAllFromRealm();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DaoUtil.reportException(e);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("raleigh_test", "UpdateSessionDetail executeTransactionAsync Success");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("raleigh_test", "UpdateSessionDetail executeTransactionAsync error" + error.getMessage());
            }
        });

    }

    /**
     * onResume检查realm状态,避免系统奔溃后，主页重新启动realm对象已被关闭，需重新连接
     */
    public boolean checkRealmStatus() {
        boolean result = true;
        if (realm == null || realm.isClosed()) {
            result=false;
            realm = DaoUtil.open();
        }
        return result;
    }

    public void onDestory() {
        updateSessionDetail = null;
        if (realm != null) {
            DaoUtil.close(realm);
        }
    }
}
