package com.yanlong.im.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.luck.picture.lib.tools.DateUtils;
import com.yanlong.im.BurnBroadcastReceiver;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.manager.MessageManager;

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
    private RealmResults toBurnMessages;
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
        toBurnMessages = realm.where(MsgAllBean.class)
                .greaterThan("endTime", 0)
                .findAllAsync();
        toBurnMessages.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults>() {
            @Override
            public void onChange(RealmResults realmResults, OrderedCollectionChangeSet changeSet) {
                /*****初始化、新增通知更新*******************************************************************************************/
                if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL
                        || changeSet.getInsertionRanges().length > 0) {
                    //初始化
                    notifyBurnQuene();
                }
            }
        });
        deleteExitSurvival();
    }

    //

    /**
     * 初始化时，删除退出即焚的消息-异步删除
     * 解决强制退出时，删除退出即焚消息
     */
    private void deleteExitSurvival() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<MsgAllBean> toDeletedResults = realm.where(MsgAllBean.class)
                        .equalTo("survival_time", -1).findAll();
                if (toDeletedResults.size() > 0)
                    toDeletedResults.deleteAllFromRealm();
            }
        });
    }


    /**
     * 处理数据
     */
    public void notifyBurnQuene() {
        if (toBurnMessages.size() > 0) {
            //异步删除
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    long currentTime = DateUtils.getSystemTime();
                    RealmResults<MsgAllBean> toDeletedResults = realm.where(MsgAllBean.class)
                            .greaterThan("endTime", 0)
                            .lessThanOrEqualTo("endTime", currentTime).findAll();
                    //复制一份，为了聊天界面的更新-非数据库对象
                    List<MsgAllBean> toDeletedResultsTemp = realm.copyFromRealm(toDeletedResults);

                    Map<String, List<String>> gids = new HashMap<>();
                    Map<Long, List<String>> uids = new HashMap<>();
                    for (MsgAllBean msg : toDeletedResults) {
                        if (TextUtils.isEmpty(msg.getGid())) {
                            Long uid=msg.getFrom_uid();
                            int index=0;
                            while (index<2){
                                if(uid!=-1L){
                                    if (uids.containsKey(msg.getGid())) {
                                        uids.get(uid).add(msg.getMsg_id());
                                    }else{
                                        List<String> msgIds=new ArrayList<>();
                                        msgIds.add(msg.getMsg_id());
                                        uids.put(uid,msgIds);
                                    }
                                }
                                uid=msg.getTo_uid();
                                index++;
                            }


                        } else {
                            if (gids.containsKey(msg.getGid())) {
                                gids.get(msg.getGid()).add(msg.getMsg_id());
                            }else{
                                List<String> msgIds=new ArrayList<>();
                                msgIds.add(msg.getMsg_id());
                                gids.put(msg.getGid(),msgIds);
                            }
                        }
                    }
                    for(String gid :gids.keySet()){
                        updateDetailListener.updateLastSecondDetail(realm, gid,  gids.get(gid).toArray(new String[gids.get(gid).size()]));
                    }
                    for(Long uid :uids.keySet()){
                        updateDetailListener.updateLastSecondDetail(realm, uid,  uids.get(uid).toArray(new String[uids.get(uid).size()]));
                    }
                    if (toDeletedResults.size() > 0) {
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
            });
        }
    }


    private void startBurnAlarm() {
        if (toBurnMessages.size() > 0) {
            //删除之后，剩下来的数据，获取距离最近的阅后即焚时间点
            long nearlyEndTime = toBurnMessages.where().min("endTime").longValue();
            //大于当前时间
            if (nearlyEndTime > System.currentTimeMillis()) {
                if (alarmManager == null)
                    alarmManager = (AlarmManager) MyAppLication.getInstance().getSystemService(Context.ALARM_SERVICE);
                else alarmManager.cancel(pendingIntent);
                if (Build.VERSION.SDK_INT < 19) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, nearlyEndTime, pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, nearlyEndTime, pendingIntent);
                }
            } else {//小于当前时间-得删除了
                notifyBurnQuene();
            }
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
        //异步数据库线程事务中调用，当前即将被删除，更新为不包含当前消息的最新一条消息
        void updateLastSecondDetail(Realm realm, String gid, String[] mgsIds);

        //异步数据库线程事务中调用，当前即将被删除，更新为不包含当前消息的最新一条消息
        void updateLastSecondDetail(Realm realm, Long fromUid,  String[] mgsIds);
    }

}