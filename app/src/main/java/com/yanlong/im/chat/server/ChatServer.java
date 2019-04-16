package com.yanlong.im.chat.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.utils.LogUtil;

/***
 * 聊天服务
 */
public class ChatServer extends Service {
    private static final String TAG = "ChatServer";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       if(!SocketUtil.getSocketUtil().isRun()) {

           LogUtil.getLog().d(TAG,">>>启动socket");

           new Thread(new Runnable() {
               @Override
               public void run() {
                   SocketUtil.getSocketUtil().reconnection();
               }
           }).start();

       }else{

           LogUtil.getLog().d(TAG,">>>已经启动socket");
       }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketUtil.getSocketUtil().stop();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
