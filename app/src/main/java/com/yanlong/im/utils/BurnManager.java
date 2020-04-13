package com.yanlong.im.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.luck.picture.lib.tools.DateUtils;
import com.yanlong.im.BurnBroadcastReceiver;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.manager.MessageManager;

import net.cb.cb.library.CoreEnum;

import java.util.List;

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

    private void initPendingIntent() {
        Intent intent = new Intent(MyAppLication.getInstance(), BurnBroadcastReceiver.class);
        intent.setAction("com.yanlong.im.burn.action");
        pendingIntent = PendingIntent.getBroadcast(MyAppLication.getInstance(), 0, intent, 0);
    }

    public BurnManager(Realm realm) {
        this.realm = realm;
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
            final boolean[] isDeleted = new boolean[1];
            //异步删除
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    long currentTime = DateUtils.getSystemTime();
                    RealmResults<MsgAllBean> toDeletedResults = realm.where(MsgAllBean.class)
                            .greaterThan("endTime", 0)
                            .lessThanOrEqualTo("endTime", currentTime).findAll();
                    if (toDeletedResults.size() > 0) {
                        isDeleted[0] = true;
                        List<MsgAllBean> tempList = realm.copyFromRealm(toDeletedResults);
                        //批量删除 已到阅后即焚时间
                        toDeletedResults.deleteAllFromRealm();
                        //通知更新聊天界面
                        MessageManager.getInstance().notifyRefreshChat(tempList, CoreEnum.ERefreshType.DELETE);
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    if (isDeleted[0]) {//有数据删除
                        //通知更新主页
                        MessageManager.getInstance().notifyRefreshMsg(CoreEnum.EChatType.PRIVATE, 0L, "", CoreEnum.ESessionRefreshTag.ALL, null);
                    }
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
}