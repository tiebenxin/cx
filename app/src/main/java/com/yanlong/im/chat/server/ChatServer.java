package com.yanlong.im.chat.server;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.ui.ChatActionActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.MediaBackUtil;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.bean.EventLoginOut4Conflict;
import net.cb.cb.library.bean.EventRefreshFriend;
import net.cb.cb.library.bean.EventRefreshMainMsg;
import net.cb.cb.library.bean.EventUserOnlineChange;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;


import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/***
 * 聊天服务
 */
public class ChatServer extends Service {
    private static final String TAG = "ChatServer";
    private static int SESSION_TYPE = 0;//无会话,1:单人;2群,3静音模式
    private static Long SESSION_FUID;//单人会话id
    private static String SESSION_SID;//会话id
    private MsgDao msgDao = new MsgDao();

    //撤回消息
    private static Map<String ,MsgAllBean> cancelList=new ConcurrentHashMap<>();

    /***
     * 添加测试消息
     * @param msg_id 返回的消息id
     * @param msgBean 要撤回的消息
     */
    public static void addCanceLsit(String msg_id, MsgAllBean msgBean){
        cancelList.put(msg_id,msgBean);
    }

    /***
     * 静音
     */
    public static void setSessionMute(boolean open) {
        if (open) {
            SESSION_TYPE = 3;
        } else {
            SESSION_TYPE = 0;
        }

    }

    /***
     * 无会话
     */
    public static void setSessionNull() {
        if (SESSION_TYPE == 3)
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
        if (SESSION_TYPE == 3)
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
        if (SESSION_TYPE == 3)
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



            for (String msgid:bean.getMsgIdList()){
                //处理撤回消息
                if(cancelList.containsKey(msgid)){
                   MsgAllBean msgAllBean= cancelList.get(msgid);
                    msgDao.msgDel4Cancel(msgid,msgAllBean.getMsgCancel().getMsgidCancel());
                    cancelList.remove(msgid);
                }

            }




        }

        @Override
        public void onMsg(MsgBean.UniversalMessage bean) {

            //   MsgBean.UniversalMessage.WrapMessage msg = bean.getWrapMsg(bean.getWrapMsgCount() - 1);
            for (MsgBean.UniversalMessage.WrapMessage msg : bean.getWrapMsgList()) {
                onMsgbranch(msg);

            }
            //通知界面刷新
            EventBus.getDefault().post(new EventRefreshMainMsg());

        }

        public void onMsgbranch(MsgBean.UniversalMessage.WrapMessage msg) {
            LogUtil.getLog().d(TAG, "<<<<<<<<<<收到类型:" + msg.getMsgType());


            LogUtil.getLog().d(TAG, "<<<<<<<<<<收到:" + msg);

            if (msg.getMsgType() == MsgBean.MessageType.UNRECOGNIZED) {
                return;
            }

            taskUpUserinfo(msg);

            switch (msg.getMsgType()) {
                case REQUEST_FRIEND:

                    //    ToastUtil.show(getApplicationContext(), "请求加好友消息");

                    msgDao.remidCount("friend_apply");
                    EventBus.getDefault().post(new EventRefreshMainMsg());
                    EventBus.getDefault().post(new EventRefreshFriend());
                    return;
                case ACCEPT_BE_FRIENDS:
                    // ToastUtil.show(AppConfig.APP_CONTEXT, "接收好友请求");

                    EventBus.getDefault().post(new EventRefreshFriend());
                    return;

                case REMOVE_FRIEND:
                    //  ToastUtil.show(getApplicationContext(), "删除好友消息");
                    EventBus.getDefault().post(new EventRefreshFriend());
                    return;
                case REQUEST_GROUP://群主会收到成员进群的请求的通知
                    msgDao.remidCount("friend_apply");
                    //  ToastUtil.show(getApplicationContext(), "请求入群");

                    for (MsgBean.GroupNoticeMessage ntm : msg.getRequestGroup().getNoticeMessageList()) {

                        msgDao.groupAcceptAdd(msg.getRequestGroup().getJoinType().getNumber(), msg.getRequestGroup().getInviter(), msg.getRequestGroup().getInviterName(), msg.getGid(), ntm.getUid(), ntm.getNickname(), ntm.getAvatar());
                    }


                    EventBus.getDefault().post(new EventRefreshMainMsg());
                    EventBus.getDefault().post(new EventRefreshFriend());
                    return;
                case ACCEPT_BE_GROUP://群主会收到成员已经进群的消息
                    //  ToastUtil.show(getApplicationContext(), "接受入群请求");
                    return;

                case CHANGE_GROUP_MASTER:
                    // ToastUtil.show(getApplicationContext(), "转让群");
                    return;
                case CHANGE_GROUP_NAME:
                    //  ToastUtil.show(getApplicationContext(), "修改群名");
                    msgDao.groupNameUpadte(msg.getGid(), msg.getChangeGroupName().getName());

                    return;
                case CHANGE_GROUP_ANNOUNCEMENT:
                    //  ToastUtil.show(getApplicationContext(), "修改群公告");
                    return;
                case DESTROY_GROUP:
                    // ToastUtil.show(getApplicationContext(), "销毁群");
                    String gname = msg.getDestroyGroup().getName();
                    String icon = msg.getDestroyGroup().getAvatar();
                    msgDao.groupExit(msg.getGid(), gname, icon, 1);
                    return;
                case REMOVE_GROUP_MEMBER:
                    //  ToastUtil.show(getApplicationContext(), "删除群成员");
                    String gname2 = msg.getRemoveGroupMember().getName();
                    String icon2 = msg.getRemoveGroupMember().getAvatar();
                    //6.19 依旧保持不禁用右上角更高
                    //msgDao.groupExit(msg.getRemoveGroupMember().getGid(),gname2,icon2,1);
                    return;
                case OUT_GROUP://退出群

                    return;
                case CONFLICT:
                    // ToastUtil.show(getApplicationContext(), "账号已经被登录");
                    EventBus.getDefault().post(new EventLoginOut4Conflict());
                    return;
                case AT:
                    if (updateAtMessage(msg)) {
                        return;
                    } else {
                        break;
                    }
                case ACTIVE_STAT_CHANGE:
                    updateUserOnlineStatus(msg);
                    EventRefreshFriend event = new EventRefreshFriend();
                    event.setLocal(true);
                    EventBus.getDefault().post(event);
                    EventBus.getDefault().post(new EventUserOnlineChange());
                    return;
                case ASSISTANT:
                    break;
                case CANCEL:
                    //撤回消息

                    String gid = msg.getGid();
                    if (!StringUtil.isNotNull(gid)) {
                        gid = null;
                    }
                    long fuid = msg.getFromUid();
                    msgDao.sessionReadUpdate(gid, fuid, true);
                    msgDao.msgDel4Cancel(msg.getMsgId(),msg.getCancel().getMsgId());

                    return;
            }


            boolean isGroup = StringUtil.isNotNull(msg.getGid());

            //会话已经静音
            Session session = isGroup ? DaoUtil.findOne(Session.class, "gid", msg.getGid()) : DaoUtil.findOne(Session.class, "from_uid", msg.getFromUid());
            if (session != null && session.getIsMute() == 1) {
                return;
            }
            //-----------------

            if (isGroup && SESSION_TYPE == 2 && SESSION_SID.equals(msg.getGid())) { //群
                //当前会话是本群不提示

            } else if (SESSION_TYPE == 1 && SESSION_FUID.longValue() == msg.getFromUid()) {//单人
                if (msg.getMsgType() == MsgBean.MessageType.STAMP) {
                    playVibration();
                }

            } else if (SESSION_TYPE == 3) {//静音模式

            } else if (SESSION_TYPE == 0 && msg.getMsgType() == MsgBean.MessageType.STAMP) {//戳一戳
                startActivity(new Intent(getApplicationContext(), ChatActionActivity.class)
                        .putExtra(ChatActionActivity.AGM_DATA, msg.toByteArray())
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                );
            } else {
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
    private UserDao userDao = new UserDao();

    /***
     * 更新用户头像等资料
     * @param msg
     */
    private void taskUpUserinfo(MsgBean.UniversalMessage.WrapMessage msg) {
        if (msg.getMsgType().getNumber() > 100) {//通知类消息
            return;
        }

        // msg.getMembername();
        userDao.userHeadNameUpdate(msg.getFromUid(), msg.getAvatar(), msg.getNickname());
        //需要更新通讯录的展示吗?
        EventRefreshFriend event = new EventRefreshFriend();
        event.setLocal(true);
        EventBus.getDefault().post(event);

    }

    private boolean updateAtMessage(MsgBean.UniversalMessage.WrapMessage msg) {
        boolean isAt = false;

        MsgDao msgDao = new MsgDao();
        String gid = msg.getGid();
        String message = msg.getAt().getMsg();
        int atType = msg.getAt().getAtType().getNumber();
        if (atType == 0) {
            List<Long> list = msg.getAt().getUidList();
            if (list == null)
                isAt = false;

            Long uid = UserAction.getMyId();
            for (int i = 0; i < list.size(); i++) {
                if (uid.equals(list.get(i))) {
                    Log.v(TAG, "有人@我" + uid);
                    msgDao.atMessage(gid, message, atType);
                    palydingdong();
                    isAt = true;
                }
            }
        } else {
            Log.v(TAG, "@所有人");
            msgDao.atMessage(gid, message, atType);
            palydingdong();
            isAt = true;
        }
        return isAt;
    }


    private long playTimeOld = 0;

    private void palydingdong() {
        if (System.currentTimeMillis() - playTimeOld < 500) {
            return;
        }
        playTimeOld = System.currentTimeMillis();


        MediaBackUtil.palydingdong(getApplicationContext());

    }

    private long playVBTimeOld = 0;

    //振动
    private void playVibration() {
        if (System.currentTimeMillis() - playVBTimeOld < 500) {
            return;
        }
        playVBTimeOld = System.currentTimeMillis();

        MediaBackUtil.playVibration(getApplicationContext(), 200);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        LogUtil.getLog().e(TAG, ">>>启动socket,服务已经开启-----------------------------------");
        //SocketUtil.getSocketUtil().stop();

        SocketUtil.getSocketUtil().startSocket();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketUtil.getSocketUtil().removeEvent(msgEvent);
        SocketUtil.getSocketUtil().endSocket();
        unregisterReceiver(mNetworkChangeReceiver);
        LogUtil.getLog().e(TAG, ">>>>>网路状态取消,服务已经关闭-----------------------------------");
    }

    protected BroadcastReceiver mNetworkChangeReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.getLog().d(TAG, ">>>>>网路状态监听");
        SocketUtil.getSocketUtil().addEvent(msgEvent);
        //注册广播用于监听网络状态改变
        mNetworkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LogUtil.getLog().d(TAG, ">>>>>网路状态改变" + NetUtil.isNetworkConnected());
                if (NetUtil.isNetworkConnected()) {//链接成功
                    onStartCommand(null, 0, 0);
                } else {//链接失败
                    SocketUtil.getSocketUtil().stop();

                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mNetworkChangeReceiver, intentFilter);

    }

    private void updateUserOnlineStatus(MsgBean.UniversalMessage.WrapMessage msg) {
        long fromUid = msg.getFromUid();
        MsgBean.ActiveStatChangeMessage message = msg.getActiveStatChange();
        if (message == null) {
            return;
        }
        fetchTimeDiff(message.getTimestamp());
        UserDao userDao = new UserDao();
        userDao.updateUserOnlineStatus(fromUid, message.getActiveTypeValue(), message.getTimestamp());

    }

    private void fetchTimeDiff(long timestamp) {
        long current = System.currentTimeMillis();//本地系统当前时间
        TimeToString.DIFF_TIME = timestamp - current;
//        LogUtil.getLog().i("服务器时间与本地时间差值=", TimeToString.DIFF_TIME + "");
    }
}
