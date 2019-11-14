package com.yanlong.im.notify;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yanlong.im.user.ui.SplashActivity;

import net.cb.cb.library.utils.LogUtil;

import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * 自定义JPush message 接收器,包括操作tag/alias的结果返回(仅仅包含tag/alias新接口部分)
 * */
public class MyJPushMessageReceiver extends JPushMessageReceiver {

    @Override
    public void onTagOperatorResult(Context context,JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onTagOperatorResult(context,jPushMessage);
        super.onTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onCheckTagOperatorResult(Context context,JPushMessage jPushMessage){
        TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context,jPushMessage);
        super.onCheckTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context,jPushMessage);
        super.onAliasOperatorResult(context, jPushMessage);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context,jPushMessage);
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);
//        String title = notificationMessage.notificationTitle;
//        LogUtil.getLog().v("MyJPushMessageReceiver",title+"");
//
//        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//        String text = km.inKeyguardRestrictedInputMode() ? "锁屏了" : "屏幕亮着的";
//        if (km.inKeyguardRestrictedInputMode()) {
//            //判断是否锁屏
//            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP, "bright");
//            wl.acquire(); // 点亮屏幕
//            wl.release(); // 释放
//
//
//            Intent alarmIntent = new Intent(context, MessageActivity.class);
//            //在广播中启动Activity的context可能不是Activity对象，所以需要添加NEW_TASK的标志，否则启动时可能会报错。
//            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(alarmIntent); //启动显示锁屏消息的activity
//        }
    }


    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
        LogUtil.getLog().i("MyJPushMessageReceiver","onNotifyMessageOpened");
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
