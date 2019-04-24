package com.yanlong.im.chat.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.test.bean.Test2Bean;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.utils.LogUtil;



import java.util.List;

/***
 * 聊天服务
 */
public class ChatServer extends Service {
    private static final String TAG = "ChatServer";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private SocketEvent msgEvent = new SocketEvent() {
        @Override
        public void onHeartbeat() {

        }

        @Override
        public void onMsg(MsgBean.UniversalMessage bean) {
            MsgAllBean saveBean = MsgConversionBean.ToBean(bean);
            Log.d(TAG, "onMsgDB: " + bean.getMsgId());
            //收到直接存表
            //先存外再存里
            DaoUtil.save(saveBean);

          //  Log.d(TAG, "onMsgDB: " + list.size());

        }

        @Override
        public void onAck() {

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!SocketUtil.getSocketUtil().isRun()) {

            LogUtil.getLog().d(TAG, ">>>启动socket");

            new Thread(new Runnable() {
                @Override
                public void run() {

                    SocketUtil.getSocketUtil().addEvent(msgEvent);
                    SocketUtil.getSocketUtil().reconnection();
                }
            }).start();

        } else {

            LogUtil.getLog().d(TAG, ">>>已经启动socket");
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
