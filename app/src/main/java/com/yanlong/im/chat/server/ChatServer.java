package com.yanlong.im.chat.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.ui.ChatActionActivity;
import com.yanlong.im.test.bean.Test2Bean;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.bean.EventRefreshMainMsg;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;


import org.greenrobot.eventbus.EventBus;

import java.util.List;

/***
 * 聊天服务
 */
public class ChatServer extends Service {
    private static final String TAG = "ChatServer";
    private static int SESSION_TYPE = 0;//无会话,1:单人;2群,3静音模式
    private static Long SESSION_FUID;//单人会话id
    private static String SESSION_SID;//会话id

    /***
     * 静音
     */
    public static void setSessionMute(boolean open) {
        if(open){
            SESSION_TYPE = 3;
        }else {
            SESSION_TYPE=0;
        }

    }

    /***
     * 无会话
     */
    public static void setSessionNull() {
        if(SESSION_TYPE == 3)
            return;
        SESSION_TYPE = 0;
        SESSION_FUID = null;
        SESSION_SID = null;
    }

    /***
     * 群
     * @param sid
     */
    public static void setSessionGroup(String sid) {
        if(SESSION_TYPE == 3)
            return;
        SESSION_TYPE = 2;
        SESSION_FUID = null;
        SESSION_SID = sid;
    }

    /***
     * 单人
     * @param fuid
     */
    public static void setSessionSolo(Long fuid) {
        if(SESSION_TYPE == 3)
            return;
        SESSION_TYPE = 1;
        SESSION_FUID = fuid;
        SESSION_SID = null;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private SocketEvent msgEvent = new SocketEvent() {
        @Override
        public void onHeartbeat() {

        }

        @Override
        public void onACK(MsgBean.AckMessage bean) {

        }

        @Override
        public void onMsg(MsgBean.UniversalMessage bean) {
            //通知界面刷新
            EventBus.getDefault().post(new EventRefreshMainMsg());

            MsgBean.UniversalMessage.WrapMessage msg = bean.getWrapMsg(bean.getWrapMsgCount() - 1);
            boolean isGroup=StringUtil.isNotNull(msg.getGid());

            //会话已经静音
            Session session =isGroup?DaoUtil.findOne(Session.class, "gid", msg.getGid()): DaoUtil.findOne(Session.class, "from_uid", msg.getFromUid());
            if(session!=null&&session.getIsMute()==1){
                return;
            }
            //-----------------

            if ( isGroup&& SESSION_TYPE == 2 && SESSION_SID.equals(msg.getGid())) { //群
                //当前会话是本群不提示

            } else if (SESSION_TYPE == 1 && SESSION_FUID.longValue() == msg.getFromUid()) {//单人
                if(msg.getMsgType() == MsgBean.MessageType.STAMP){
                    playVibration();
                }

            } else if (SESSION_TYPE == 3) {//静音模式

            } else if(SESSION_TYPE == 0 &&msg.getMsgType() == MsgBean.MessageType.STAMP){//戳一戳
                startActivity(new Intent(getApplicationContext(), ChatActionActivity.class)
                        .putExtra(ChatActionActivity.AGM_DATA, msg.toByteArray())
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                );
            }else {
                palydingdong();
            }



        }

        @Override
        public void onSendMsgFailure(MsgBean.UniversalMessage.Builder bean) {

        }

        @Override
        public void onLine(boolean state) {

        }
    };

    private void palydingdong() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

    }
    //振动
    private void playVibration(){

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(200);
        }
    }


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
