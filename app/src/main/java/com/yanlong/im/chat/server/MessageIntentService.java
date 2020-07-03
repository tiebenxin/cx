package com.yanlong.im.chat.server;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.yanlong.im.MyAppLication;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.task.DispatchMessage;
import com.yanlong.im.chat.task.OnlineMessage;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;

import net.cb.cb.library.utils.LogUtil;

import io.realm.Realm;

/**
 * 消息处理IntentService 处理完成，会自动stopservice
 *
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/5 0005
 * @description
 */
public class MessageIntentService extends IntentService {
    private int restartCount = 0;//重启动次数
    /**
     * 消息分发处理器
     */
    private DispatchMessage dispatch = OnlineMessage.getInstance();

    public MessageIntentService() {
        super("MessageIntentService");
    }


    @Override
    public void onDestroy() {
//        MessageManager.getInstance().clear();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("CheckResult")
    protected void onHandleIntent(@Nullable Intent intent) {//异步处理方法
        LogUtil.getLog().d("Liszt_test", "消息LOG--onHandleIntent");
        synchronized (this) {
            if (MessageManager.getInstance().getToDoMsgCount() == 0 && restartCount < 3) {
                //TODO:不影响消息接收，两批消息共用一次onHandleIntent
                LogUtil.getLog().d("Liszt_test", "消息LOG-无数据队列-不影响消息接收-重启动");
                restartCount++;
                try {
                    Thread.sleep(100);
                    startService(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LogUtil.getLog().i("Liszt_test", "消息LOG--在线--睡眠出错");
                }
                return;
            }
            restartCount = 0;
            Realm realm = DaoUtil.open();
            //初始化数据库对象 子线程
            while (MessageManager.getInstance().getToDoMsgCount() > 0) {
                try {
                    MsgBean.UniversalMessage bean = MessageManager.getInstance().poll();
                    dispatch.dispatch(bean, realm);
                    //移除处理过的当前消息
                } catch (Exception e) {
                    LogUtil.writeError(e);
                }
            }
            DaoUtil.close(realm);
        }
    }
}
