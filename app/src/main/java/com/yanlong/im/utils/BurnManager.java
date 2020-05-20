package com.yanlong.im.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;

import com.yanlong.im.BurnBroadcastReceiver;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.CoreEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @创建人 shenxin
 * @创建时间 2019/10/21 0021 10:51
 * 阅后即焚管理类
 */
public class BurnManager {
    //需要焚的消息-不包括退出即焚
    private RealmResults toBurnMessages = null;
    private Realm realm;
    private AlarmManager alarmManager = null;
    private PendingIntent pendingIntent = null;
    private UpdateDetailListener updateDetailListener = null;

    private void initPendingIntent() {
        Intent intent = new Intent(MyAppLication.getInstance(), BurnBroadcastReceiver.class);
        intent.setAction("com.yanlong.im.burn.action");
        pendingIntent = PendingIntent.getBroadcast(MyAppLication.getInstance(), 0, intent, 0);
    }

    public BurnManager(Realm realm, UpdateDetailListener updateDetailListener) {
        this.realm = realm;
        this.updateDetailListener = updateDetailListener;
        initPendingIntent();
        //异步加载
        initBurnQueue();
        //删除退出即焚数据
        deleteExitSurvival();
    }

    /**
     * 初始化
     */
    private void initBurnQueue() {
        toBurnMessages = realm.where(MsgAllBean.class)
                .greaterThan("endTime", 0)
                .findAllAsync();
        toBurnMessages.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults>() {
            @Override
            public void onChange(RealmResults realmResults, OrderedCollectionChangeSet changeSet) {
                /*****初始化、新增通知更新*******************************************************************************************/
                if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL
                        || changeSet.getInsertions().length > 0 || changeSet.getChanges().length > 0) {
                    //初始化
                    notifyBurnQuene();
                }
            }
        });
    }

    //

    private Handler handler = new Handler();

    /**
     * 初始化时，删除退出即焚的消息-异步删除
     * 解决强制退出时，删除退出即焚消息
     */
    private void deleteExitSurvival() {
        Map<String, List<String>> gids = new HashMap<>();
        Map<Long, List<String>> uids = new HashMap<>();
        /**
         * 有些手机启动报错deleteAllFromRealm java.lang.IllegalStateException Must be in a write transaction
         * 这里做一下延迟1秒再操作
         */
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<MsgAllBean> toDeletedResults = realm.where(MsgAllBean.class)
                        .equalTo("survival_time", -1).findAll();
                //保存待删除的gids和uids,以及msgId
                getGidsAndUids(toDeletedResults, gids, uids);
                //更新session详情
                for (String gid : gids.keySet()) {
                    updateDetailListener.updateLastSecondDetail(realm, gid, gids.get(gid).toArray(new String[gids.get(gid).size()]));
                }
                for (Long uid : uids.keySet()) {
                    updateDetailListener.updateLastSecondDetail(realm, uid, uids.get(uid).toArray(new String[uids.get(uid).size()]));
                }
                //删除消息
                if (toDeletedResults.size() > 0)
                    toDeletedResults.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {//失败了重试
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        deleteExitSurvival();
                    }
                }, 1000);

            }
        });
    }


    /**
     * 处理数据
     */
    public void notifyBurnQuene() {
        if (toBurnMessages == null) {
            initBurnQueue();
            return;
        }
        if (toBurnMessages != null && toBurnMessages.size() > 0) {
            Map<String, List<String>> gids = new HashMap<>();
            Map<Long, List<String>> uids = new HashMap<>();
            //异步删除
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    long currentTime = SocketData.getFixTime();
                    RealmResults<MsgAllBean> toDeletedResults = realm.where(MsgAllBean.class)
                            .greaterThan("endTime", 0)
                            .lessThanOrEqualTo("endTime", currentTime).findAll();

                    //复制一份，为了聊天界面的更新-非数据库对象
                    List<MsgAllBean> toDeletedResultsTemp = realm.copyFromRealm(toDeletedResults);
                    //保存待删除的gids和uids,以及msgId
                    getGidsAndUids(toDeletedResults, gids, uids);
                    //更新session详情
                    for (String gid : gids.keySet()) {
                        updateDetailListener.updateLastSecondDetail(realm, gid, gids.get(gid).toArray(new String[gids.get(gid).size()]));
                    }
                    for (Long uid : uids.keySet()) {
                        updateDetailListener.updateLastSecondDetail(realm, uid, uids.get(uid).toArray(new String[uids.get(uid).size()]));
                    }
                    if (toDeletedResults.size() > 0) {
                        //删除前先把子表数据干掉!!切记
                        MsgDao msgDao = new MsgDao();
                        for (MsgAllBean msg : toDeletedResults) {
                            msgDao.deleteRealmMsg(msg);
                        }
                        //批量删除 已到阅后即焚时间
                        toDeletedResults.deleteAllFromRealm();
                        /**
                         * 通知更新聊天界面
                         * 因为聊天界面删除的非数据库对象，可以提前通知，若为数据库对象，需在OnSuccess方法中
                         */
                        MessageManager.getInstance().notifyRefreshChat(toDeletedResultsTemp, CoreEnum.ERefreshType.DELETE);
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    startBurnAlarm();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {//重试
                    notifyBurnQuene();
                }
            });
        }
    }

    /**
     * 获取Gids And Uids
     *
     * @param toDeletedResults
     * @param gids
     * @param uids
     */
    private void getGidsAndUids(RealmResults<MsgAllBean> toDeletedResults, Map<String, List<String>> gids, Map<Long, List<String>> uids) {
        for (MsgAllBean msg : toDeletedResults) {
            if (TextUtils.isEmpty(msg.getGid())) {//单聊
                Long uid = msg.getFrom_uid();
                int index = 0;
                while (index < 2) {//进行两次取值，To_uid、from_uid
                    if (uid != -1L) {
                        if (uids.containsKey(uid)) {
                            uids.get(uid).add(msg.getMsg_id());
                        } else {
                            List<String> msgIds = new ArrayList<>();
                            msgIds.add(msg.getMsg_id());
                            uids.put(uid, msgIds);
                        }
                    }
                    uid = msg.getTo_uid();
                    index++;
                }
            } else {//群聊
                if (gids.containsKey(msg.getGid())) {
                    gids.get(msg.getGid()).add(msg.getMsg_id());
                } else {
                    List<String> msgIds = new ArrayList<>();
                    msgIds.add(msg.getMsg_id());
                    gids.put(msg.getGid(), msgIds);
                }
            }
        }
    }


    private void startBurnAlarm() {
        try {
            if (pendingIntent == null) {//销毁了数据仓库
                return;
            }
            if (toBurnMessages == null) {
                initBurnQueue();
                return;
            }
            if (toBurnMessages != null && toBurnMessages.size() > 0) {
                //删除之后，剩下来的数据，获取距离最近的阅后即焚时间点
                long nearlyEndTime = toBurnMessages.where().min("endTime").longValue();
                long currentTime = SocketData.getFixTime();
                //大于当前时间
                if (nearlyEndTime > currentTime) {
                    if (alarmManager == null)
                        alarmManager = (AlarmManager) MyAppLication.getInstance().getSystemService(Context.ALARM_SERVICE);
                    else alarmManager.cancel(pendingIntent);
                    //矫正一次时间,防止用户自定义设置时间

                    long distance = nearlyEndTime - currentTime;
                    long startTime = System.currentTimeMillis() + (distance > 0 ? distance : 0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
                    }
                } else {//小于当前时间-得删除了
                    notifyBurnQuene();
                }
            }
        } catch (Exception e) {
        }
    }

    public void onDestory() {
        toBurnMessages.removeAllChangeListeners();
        //取消定时闹钟
        if (alarmManager != null) alarmManager.cancel(pendingIntent);
        pendingIntent = null;
        alarmManager = null;
        toBurnMessages = null;
    }

    public interface UpdateDetailListener {
        void update(String[] gids, Long[] uids);

        //异步数据库线程事务中调用，当前即将被删除，更新为不包含当前消息的最新一条消息
        void updateLastSecondDetail(Realm realm, String gid, String[] mgsIds);

        //异步数据库线程事务中调用，当前即将被删除，更新为不包含当前消息的最新一条消息
        void updateLastSecondDetail(Realm realm, Long fromUid, String[] mgsIds);
    }

}