package com.yanlong.im.chat.server;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;

import io.realm.Realm;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/5 0005
 * @description
 */
public class MessageIntentService extends IntentService {
    private Realm realm = null;

    public MessageIntentService() {
        super("MessageIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        MsgBean.UniversalMessage msg = MessageManager.getInstance().poll();
        //子线程
        while (msg !=null){

            msg = MessageManager.getInstance().poll();
        }
    }

    @Override
    public void onDestroy() {
        Log.e("raleigh_test","MessageIntentService")
        super.onDestroy();
        DaoUtil.close(realm);
        realm = null;
    }
}
