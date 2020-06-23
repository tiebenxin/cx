package com.yanlong.im.chat.server;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

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


    /**
     * 消息分发处理器
     */
    private DispatchMessage dispatch = new OnlineMessage();

    public MessageIntentService() {
        super("MessageIntentService");
    }


    @Override
    public void onDestroy() {
        MessageManager.getInstance().clear();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("CheckResult")
    protected void onHandleIntent(@Nullable Intent intent) {//异步处理方法
        Realm realm = DaoUtil.open();
        if (MessageManager.getInstance().getToDoMsgCount() == 0) return;
        //初始化数据库对象 子线程
        while (MessageManager.getInstance().getToDoMsgCount() > 0) {
            try {
                MsgBean.UniversalMessage bean = MessageManager.getInstance().poll();
                dispatch.dispatch(bean, realm);
                //移除处理过的当前消息
                MessageManager.getInstance().pop();
            } catch (Exception e) {
                LogUtil.writeError(e);
            }
        }
        DaoUtil.close(realm);

    }
}
