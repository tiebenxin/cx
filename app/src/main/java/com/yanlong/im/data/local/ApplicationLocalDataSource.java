package com.yanlong.im.data.local;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.yanlong.im.MyAppLication;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.BurnManager;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

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
    private BurnManager burnManager = null;

    public ApplicationLocalDataSource() {
        realm = DaoUtil.open();
        updateSessionDetail = new UpdateSessionDetail(realm);
        burnManager = new BurnManager(realm, new BurnManager.UpdateDetailListener() {
            @Override
            public void update(String[] gids, Long[] uids) {
                updateSessionDetail.update(gids, uids);
            }

            @Override
            public void updateLastSecondDetail(Realm realm, String gid, String[] mgsIds) {
                //异步数据库线程事务中调用，当前即将被删除，更新为不包含当前消息的最新一条消息
                updateSessionDetail.updateLastDetail(realm, gid, mgsIds);
            }

            @Override
            public void updateLastSecondDetail(Realm realm, Long fromUid, String[] mgsIds) {
                //异步数据库线程事务中调用，当前即将被删除，更新为不包含消息mgsIds
                updateSessionDetail.updateLastDetail(realm, fromUid, mgsIds);
            }
        });
    }

    /**
     * 通知更新阅后即焚队列
     */
    public void notifyBurnQuene() {
        burnManager.notifyBurnQuene();
    }

    public Realm getRealm() {
        return realm;
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
     * 更新指定一些消息对应的session详情
     *
     * @param
     */
    public void updateSessionDetail(String[] gids, Long[] uids) {
        updateSessionDetail.update(gids, uids);
    }

    /**
     * 因个人信息更改
     * 更新我自己的session会话的所有群聊
     */
    public void updateSelfGroup() {
        updateSessionDetail.updateSelfGroup();
    }

    /**
     * 清除会话详情的内容
     *
     * @param
     */
    public void clearContent(String[] gids, Long[] uids) {
        updateSessionDetail.clearContent(gids, uids);
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
        boolean isNeedResetFriendTag = MyAppLication.getInstance().getSharedPreferences(SharedPreferencesUtil.SPName.USER_SETTING.toString(), Context.MODE_PRIVATE)
                .getBoolean("isNeedResetFriendTag", true);
        if (isNeedResetFriendTag) {
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
                    if (userInfos != null && userInfos.size() > 0) {
                        for (UserInfo userInfo : userInfos) {
                            userInfo.setTag(UserInfo.FRIEND_NUMBER_TAG);
                        }
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    //记住，下次不用再重置了
                    MyAppLication.getInstance().getSharedPreferences(SharedPreferencesUtil.SPName.USER_SETTING.toString(), Context.MODE_PRIVATE)
                            .edit().putBoolean("isNeedResetFriendTag", false).apply();
                }
            });
        }
        String[] orderFiled = {"tag", "pinyin"};
        Sort[] sorts = {Sort.ASCENDING, Sort.ASCENDING};
        return realm.where(UserInfo.class)
                .beginGroup().equalTo("uType", 2).or().equalTo("uType", 4).endGroup()
                .sort(orderFiled, sorts).limit(limit).findAllAsync();
    }

    /**
     * 保存当前会话退出即焚消息，endTime到数据库-自动会加入焚队列，存入数据库
     */
    public void saveExitSurvivalMsg(String gid, Long userid) {
        DaoUtil.executeTransactionAsync(realm, new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (!TextUtils.isEmpty(gid)) {
                    RealmResults<MsgAllBean> list = realm.where(MsgAllBean.class)
                            .beginGroup().equalTo("gid", gid).endGroup()
                            .and()
                            .beginGroup().lessThan("survival_time", 0).endGroup()
                            .findAll();
                    //更新为当前时间删除（batch update批量更新大数据会报错-慎用）
                    for (MsgAllBean msg : list) {
                        msg.setStartTime(System.currentTimeMillis());
                        msg.setEndTime(System.currentTimeMillis());
                    }
                } else {
                    if (userid != null) {
                        RealmResults<MsgAllBean> list = realm.where(MsgAllBean.class)
                                .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                                .and()
                                .beginGroup().equalTo("to_uid", userid).or().equalTo("from_uid", userid).endGroup()
                                .and()
                                .beginGroup().lessThan("survival_time", 0).endGroup()
                                .findAll();
                        //更新为当前时间删除（batch update批量更新大数据会报错-慎用）
                        for (MsgAllBean msg : list) {
                            msg.setStartTime(System.currentTimeMillis());
                            msg.setEndTime(System.currentTimeMillis());
                        }
                    }
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
            }
        });

    }

    /**
     * 删除某个session 所有消息
     *
     * @param uid
     * @param gid
     */
    public void deleteAllMsg(String sid, Long uid, String gid) {
        List<String> gids = new ArrayList<>();
        List<Long> uids = new ArrayList<>();
        if (!TextUtils.isEmpty(gid)) {
            gids.add(gid);
        } else {
            if (uid != null) {
                uids.add(uid);
            }
        }
        //异步线程删除
        DaoUtil.executeTransactionAsync(realm, new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    //删除某个session detial
                    SessionDetail sessionDetail = realm.where(SessionDetail.class).equalTo("sid", sid).findFirst();
                    if (sessionDetail != null) sessionDetail.deleteFromRealm();
                    //删除某个session所有消息
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
//                LogUtil.getLog().i("msgDao", "deleteAllMsg--删除成功");
                if (MyAppLication.INSTANCE().repository != null)
                    MyAppLication.INSTANCE().repository.updateSessionDetail(gids, uids);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
//                LogUtil.getLog().i("msgDao", error.getMessage());
            }
        });
    }

    /**
     * 标记会话已读未读
     *
     * @param
     */
    public void markSessionRead(String sid, int read, String msgId) {
        updateSessionDetail.markSessionRead(sid, read, msgId);
    }

    /**
     * 修改消息已读，未读
     *
     * @param
     */
    public void updateMsgRead(String sid, String msgId, int read) {
        updateSessionDetail.updateMsgRead(sid, msgId, read);
    }


    /**
     * 批量删除消息
     */
    public void deleteMsgList(List<MsgAllBean> list) {
        List<String> gids = new ArrayList<>();
        List<Long> uids = new ArrayList<>();
        //异步线程删除
        DaoUtil.executeTransactionAsync(realm, new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int len = list.size();
                MsgDao msgDao = new MsgDao();
                for (int i = 0; i < len; i++) {
                    MsgAllBean bean = list.get(i);
                    MsgAllBean msg = realm.where(MsgAllBean.class).equalTo("msg_id", bean.getMsg_id()).findFirst();
                    if (msg != null) {
                        //gid存在时，不取uid
                        if (TextUtils.isEmpty(msg.getGid())) {
                            uids.add(msg.getFrom_uid());
                            uids.add(msg.getTo_uid());
                        } else {
                            gids.add(msg.getGid());
                        }
                        msgDao.deleteRealmMsg(msg);
                        msg.deleteFromRealm();
                    }
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
//                LogUtil.getLog().i("msgDao", "deleteMsgList-删除成功");
                if (MyAppLication.INSTANCE().repository != null)
                    MyAppLication.INSTANCE().repository.updateSessionDetail(gids, uids);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
//                LogUtil.getLog().i("msgDao", error.getMessage());
            }
        });
    }

    /**
     * 更新session中所有未读消息未已读
     *
     * @param
     */
    public void updateMsgRead(String gid, Long uid) {
        DaoUtil.executeTransactionAsync(realm, new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<MsgAllBean> realmResults = null;
                if (!TextUtils.isEmpty(gid)) {
                    realmResults = realm.where(MsgAllBean.class)
                            .beginGroup().equalTo("gid", gid).endGroup()
                            .and()
                            .beginGroup().equalTo("isRead", false).endGroup()
                            .findAll();
                } else {
                    realmResults = realm.where(MsgAllBean.class)
                            .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                            .and()
                            .beginGroup().equalTo("from_uid", uid).or().equalTo("to_uid", uid).endGroup()
                            .and()
                            .beginGroup().equalTo("isRead", false).endGroup()
                            .findAll();
                }
                if (realmResults != null) {
                    for (MsgAllBean bean : realmResults) {
                        bean.setRead(true);
                    }
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                LogUtil.getLog().i("msgDao", "updateMsgRead-success");

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                LogUtil.getLog().i("msgDao", error.getMessage());

            }
        });
    }


    public void beginTransaction() {
        realm.beginTransaction();
    }

    public void commitTransaction() {
        realm.commitTransaction();
    }

    public void onDestroy() {
        if (realm != null) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            realm.close();
        }
        burnManager.onDestroy();
        realm = null;
        updateSessionDetail = null;
        burnManager = null;
    }
}
