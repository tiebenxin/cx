package com.yanlong.im.utils.socket;

import android.accounts.NetworkErrorException;
import android.text.TextUtils;

import com.hm.cxpay.global.PayEnvironment;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.eventbus.AckEvent;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.tcp.SocketEndException;
import com.yanlong.im.chat.tcp.TcpConnection;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.MainApplication;
import net.cb.cb.library.bean.BuglyException;
import net.cb.cb.library.bean.EventLoginOut;
import net.cb.cb.library.constant.AppHostUtil;
import net.cb.cb.library.constant.BuglyTag;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.manager.excutor.ExecutorManager;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;

import org.greenrobot.eventbus.EventBus;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.NoConnectionPendingException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SocketUtil {
    private static final String TAG = "SocketUtil";
    private static SocketUtil socketUtil;
    // Bugly发送回执异常标签
    public static final int BUGLY_TAG_SEND_DATA = 139067;
    // Bugly异常登录标签
    public static final int BUGLY_TAG_LOGIN = 139070;
    private AsyncPacketWriter writer;

    //重连检测时长
    private long recontTime = 5 * 1000;
    //心跳步长
    private long heartbeatStep = 30 * 1000;
    private boolean keepConnect = false;//是否保持连接
    private boolean isMainLive = false;//是否主界面存活

    private static List<SocketEvent> eventLists = new CopyOnWriteArrayList<>();
    //事件分发
    private static SocketEvent event = new SocketEvent() {
        @Override
        public void onHeartbeat() {

        }

        @Override
        public void onACK(MsgBean.AckMessage bean) {
            SocketData.setPreServerAckTime(bean.getTimestamp());
            boolean isAccepted = false;
            MsgAllBean msgAllBean = null;
            LogUtil.getLog().d(TAG, ">>>>>接受回执--size=" + bean.getMsgIdCount());
            if (bean.getRejectType() == MsgBean.RejectType.ACCEPTED) {//接收到发送的消息了
                LogUtil.getLog().d(TAG, ">>>>>消息发送成功");
                msgAllBean = SocketData.updateMsgSendStatusByAck(bean, true);
                if (msgAllBean == null) {
                    SocketData.msgSave4Me(bean);
                } else {
                    isAccepted = true;
                }
                // 保存对方封号提示语，每发一条消息都会保存
                if (!TextUtils.isEmpty(bean.getDesc()) && !bean.getDesc().contains("拉黑")) {
                    isAccepted = false;
                    MsgAllBean msg = SocketData.createMsgBeanOfNotice(bean, msgAllBean, ChatEnum.ENoticeType.SEAL_ACCOUNT);
                    //收到直接存表
                    if (msg != null) {
                        DaoUtil.update(msg);
                    }
                }
            } else {
                LogUtil.getLog().d(TAG, ">>>>>ack被拒绝 :" + bean.getRejectType());
                LogUtil.writeLog(">>>>>ack被拒绝 :" + bean.getRejectType());
                msgAllBean = SocketData.updateMsgSendStatusByAck(bean, false);
                if (msgAllBean == null) {
                    SocketData.msgSave4MeFail(bean);
                }
                if (bean.getRejectType() == MsgBean.RejectType.NOT_FRIENDS_OR_GROUP_MEMBER) {
                    MsgAllBean msg = SocketData.createMsgBeanOfNotice(bean, msgAllBean, ChatEnum.ENoticeType.NO_FRI_ERROR);
                    //收到直接存表
                    if (msg != null) {
                        DaoUtil.update(msg);
                    }
                } else if (bean.getRejectType() == MsgBean.RejectType.IN_BLACKLIST) {
                    MsgAllBean msg = SocketData.createMsgBeanOfNotice(bean, msgAllBean, ChatEnum.ENoticeType.BLACK_ERROR);
                    //收到直接存表
                    if (msg != null) {
                        DaoUtil.update(msg);
                    }
                } else if (bean.getRejectType() == MsgBean.RejectType.WORDS_NOT_ALLOWED) {
                    MsgAllBean msg = SocketData.createMsgBeanOfNotice(bean, msgAllBean, ChatEnum.ENoticeType.FORBIDDEN_WORDS_SINGE);
                    //收到直接存表
                    if (msg != null) {
                        DaoUtil.update(msg);
                    }
                    if (msgAllBean != null && MessageManager.getInstance().isMsgFromCurrentChat(msgAllBean.getGid(), msgAllBean.getFrom_uid())) {
                        EventFactory.ToastEvent toastEvent = new EventFactory.ToastEvent();
                        toastEvent.value = bean.getDesc();
                        EventBus.getDefault().post(toastEvent);
                    }
                } else if (bean.getRejectType() == MsgBean.RejectType.FRIEND_FROZEN) {//账号被冻结
                    MsgAllBean msg = SocketData.createMsgBeanOfNotice(bean, msgAllBean, ChatEnum.ENoticeType.FREEZE_ACCOUNT);
                    //收到直接存表
                    if (msg != null) {
                        DaoUtil.update(msg);
                    }
                } else if (bean.getRejectType() == MsgBean.RejectType.RATE_LIMIT) {//服务端有限流，测试代码自动发送消息时会引起此问题
                    LogUtil.getLog().d(TAG, "消息发送失败--服务端限流--requestId=" + bean.getRequestId());
//                    System.out.println("Socket--消息发送失败--服务端限流---requestId=" + bean.getRequestId());
                }
            }

            if (isAccepted && msgAllBean != null) {
                if (msgAllBean.getMsg_type() != null && msgAllBean.getMsg_type() == ChatEnum.EMessageType.READ) {
                    return;
                }
                notifyAck(msgAllBean);
            } else {
                notifyAck(bean);
            }
        }


        @Override
        public void onMsg(MsgBean.UniversalMessage bean) {
            //保存消息和处理回执
            //在线离线消息不需要发送回执, 索引越界？？？？？
            int count = bean.getWrapMsgCount();
            if (count > 0 && bean.getWrapMsg(0).getMsgType() != MsgBean.MessageType.ACTIVE_STAT_CHANGE) {
                LogUtil.writeLog("--收到请求--requestId=" + bean.getRequestId() + " 条数：" + count);
                LogUtil.getLog().i(TAG, "--消息LOG--requestId=" + bean.getRequestId() + " 条数：" + count + "--type=" + bean.getWrapMsg(0).getMsgType());
//                if (count == 1) {//单条消息直接回执，多条消息待消息存成功后再回执
//                    SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(bean.getRequestId(), null, bean.getMsgFrom(), false, true), null, bean.getRequestId());
//                    LogUtil.writeLog("--发送回执1--requestId=" + bean.getRequestId() + " msgType:" + bean.getWrapMsg(0).getMsgType() + "--msgTypeValue=" + bean.getWrapMsg(0).getMsgTypeValue() + " msgID:" + bean.getWrapMsg(0).getMsgId());
//                }
            }
            MessageManager.getInstance().onReceive(bean);
            for (SocketEvent ev : eventLists) {
                if (ev != null) {
                    ev.onMsg(bean);
                }
            }
        }

        @Override
        public void onSendMsgFailure(MsgBean.UniversalMessage.Builder bean) {
            LogUtil.getLog().e(TAG, ">>>>>发送失败了" + bean.getRequestId());
            try {
                LogUtil.writeLog("--发送失败了--requestId=" + bean.getRequestId() + "--msgType=" + bean.getWrapMsg(0).getMsgType());
            } catch (Exception e) {

            }
            for (SocketEvent ev : eventLists) {
                if (ev != null) {
                    ev.onSendMsgFailure(bean);
                }
                //这里可以做为空,自动移除
            }
        }

        @Override
        public void onLine(boolean state) {
            LogUtil.getLog().e(TAG, ">>>>>在线状态" + state);
            //保存连接状态到本地
            new SharedPreferencesUtil(SharedPreferencesUtil.SPName.CONN_STATUS).save2Json(state);
            for (SocketEvent ev : eventLists) {
                if (ev != null) {
                    ev.onLine(state);
                }
            }
            MessageManager.getInstance().notifyOnlineStatus(state);

        }
    };
    //正在运行
    private int isRun = 0;//0:没运行1:启动中:2运行中
    //线程版本
    private long threadVer = 0;


    private Runnable heartRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                while (isRun()) {
                    if (System.currentTimeMillis() - heartbeatTime > heartbeatStep * 3.5) {//心跳超时
                        //重启
                        stop(true);
                    } else {
                        LogUtil.getLog().d(TAG, ">>>发送心跳包-----");
                        sendData(SocketPacket.getPackage(SocketPacket.DataType.PROTOBUF_HEARTBEAT, SocketPacket.P_HEART), null, "");
                    }
                    Thread.sleep(heartbeatStep);
                }
                LogUtil.getLog().d(TAG, ">>>心跳线程结束------");
            } catch (Exception e) {
                LogUtil.getLog().d(TAG, ">>>心跳线程异常------");
            }
        }
    };
    private ScheduledFuture<?> heardSchedule;

    public boolean isRun() {
        return isRun > 0;
    }

    /***
     * 改变运行状态
     * @param state
     */
    private void setRunState(int state) {
        isRun = state;
        if (isRun == 0) {
            event.onLine(false);
        }
        if (isRun == 2) {
            event.onLine(true);
        }
    }

    /***
     * 获取在线状态
     * @return
     */
    public boolean getOnLineState() {
        return isRun == 2;
    }

    public static SocketEvent getEvent() {
        return event;
    }

    /**
     * 添加消息监听
     *
     * @param event
     */
    public void addEvent(SocketEvent event) {
        if (!eventLists.contains(event)) {
            eventLists.add(event);
        }

    }

    public void addEvent(SocketEvent event, int index) {
        if (!eventLists.contains(event)) {
            eventLists.add(index, event);
        }

    }

    /***
     * 移除监听
     * @param event
     */
    public void removeEvent(SocketEvent event) {
        eventLists.remove(event);
    }

    private SocketUtil() {
    }

    public static SocketUtil getSocketUtil() {
        if (socketUtil == null) {
            socketUtil = new SocketUtil();
        }
        return socketUtil;
    }

    /***
     * 启动
     */
    private void run() {
        if (isRun()) {
            return;
        }
        //无网络，不连接
        if (!NetUtil.isNetworkConnected()) {
            setRunState(0);
            return;
        }
        setRunState(1);
        try {
            if (socketChannel == null || !socketChannel.isConnected()) {
                connect();
            }
        } catch (Exception e) {
            LogUtil.writeLog(TAG + "--连接LOG--" + "连接异常" + e.getMessage());
            setRunState(0);
            e.printStackTrace();
            stop(true);
        }
    }

    /**
     * 停止
     *
     * @param isClearSendList 是否清除缓存队列中的数据
     */
    public void stop(boolean isClearSendList) {
        if (!isRun()) {
            return;
        }

        setRunState(0);
        //结束发送列队
        if (isClearSendList) {
            SendList.endList();
        }
        //关闭信道
        try {
            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socketChannel = null;
        }
        LogUtil.getLog().d(TAG, "连接LOG >>>>关闭连接 1------time=" + System.currentTimeMillis());
        LogUtil.writeLog(TAG + "--连接LOG--" + "关闭连接");

    }

    //6.20 强制结束
    public void stop2() {
        setRunState(0);
        if (heardSchedule != null) {
            heardSchedule.cancel(true);
        }
        //结束发送列队
        SendList.endList();
        //关闭信道
        try {
            if (socketChannel != null) {
                socketChannel.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socketChannel = null;
        }
        LogUtil.getLog().d(TAG, "连接LOG >>>>关闭连接 2---------time=" + System.currentTimeMillis());
    }


    /***
     * 心跳线程
     */
    private void heartbeatThread() {
        LogUtil.getLog().d(TAG, ">>>心跳线程启动---------------");
        heardSchedule = ExecutorManager.INSTANCE.getTimerThread().schedule(heartRunnable, heartbeatStep, TimeUnit.MILLISECONDS);
    }

    //队列遍历步长:必须小于每条消息重发时长
    private long sendListStep = 2 * 1000;

    /***
     * 发送队列线程
     */
    private void sendListThread() {
        LogUtil.getLog().d(TAG, ">>>发送队列线程启动---------------");
        ExecutorManager.INSTANCE.getNormalThread().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isRun() /*&& indexVer == threadVer*/) {
                        SendList.loopList();
                        Thread.sleep(sendListStep);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LogUtil.getLog().d(TAG, ">>>队列异常run: " + e.getMessage());
                }
            }
        });
    }


    private boolean isStart = false;

    /***
     * 启动，纳入线程池管理，连接速度无影响
     */
    public void startSocket() {
        if (isStart) {
            LogUtil.getLog().i(TAG, ">>>>> 当前正在运行");
            return;
        }
        isStart = true;
        ExecutorManager.INSTANCE.getSocketThread().execute(new Runnable() {
            @Override
            public void run() {
                LogUtil.getLog().i(TAG, ">>>>>检查socketChannel 空: " + (socketChannel == null));
                if (socketChannel != null) {
                    LogUtil.getLog().i(TAG, ">>>>>检查socketChannel 已连接:" + socketChannel.isConnected());
                }

                while (isStart) {
                    LogUtil.getLog().i(TAG, ">>>>>服务器链接检查isRun: " + isRun);
//                    LogUtil.getLog().i(TAG, ">>>>>服务器链接socketChannel: " + socketChannel);
                    if (socketChannel != null) {
                        LogUtil.getLog().i(TAG, ">>>>>服务器链接isConnected: " + socketChannel.isConnected());
                    }
                    if ((socketChannel == null || !socketChannel.isConnected()) && isRun == 0) {//没有启动,就执行启动
                        //线程版本+1
                        threadVer++;
                        SocketUtil.this.run();
                        LogUtil.getLog().i(TAG, ">>>>>新线程结束");
                    } else {//已经启动了
                    }

                    try {
                        Thread.sleep(recontTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /***
     * 结束socket，暂不纳入线程池管理，关闭太慢了
     */
    public void endSocket() {
        isStart = false;
//        ExecutorManager.INSTANCE.getSocketThread().execute(new Runnable() {
//
//            @Override
//            public void run() {
//                stop2();
//                clearThread();
//            }
//        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                stop2();
                clearThread();
            }
        }).start();
    }

    /**
     * 发送原始字节,无事务处理,用来做心跳,鉴权之类的
     *
     * @param data
     * @param msgTag
     * @param requetId 回执发送失败，需要重新发送，发送没报异常则清除队列不在重发
     */
    public void sendData(final byte[] data, final MsgBean.UniversalMessage.Builder msgTag, String requetId) {
        if (!isRun())
            return;
        if (data == null) {
            return;
        }
        if (writer == null) {
            return;
        }
        writer.write(data, msgTag, requetId);
    }

    /***
     * 发送消息,有事务处理,用来做普通消息,红包,等业务
     * @param msg
     */
    public void sendData4Msg(MsgBean.UniversalMessage.Builder msg) {
//        LogUtil.getLog().e("=sendData4Msg=msg=="+msg);
        //添加到消息队中监听
        SendList.addSendList(msg.getRequestId(), msg);
        sendData(SocketPacket.getPackage(SocketPacket.DataType.PROTOBUF_MSG, msg.build().toByteArray()), msg, msg.getRequestId());
    }


    //1.
    private SSLSocketChannel2 socketChannel;
//  private SocketChannel socketChannel;


    /***
     * 链接
     */
    private void connect() throws Exception {
        socketChannel = new SSLSocketChannel2(SocketChannel.open());
        //socketChannel =  SocketChannel.open();
        writer = new AsyncPacketWriter(socketChannel);
        socketChannel.configureBlocking(false);

        LogUtil.getLog().d(TAG, "连接LOG " + AppHostUtil.getTcpHost() + ":" + AppHostUtil.TCP_PORT + "--time=" + System.currentTimeMillis());
        if (!socketChannel.connect(new InetSocketAddress(AppHostUtil.getTcpHost(), AppHostUtil.TCP_PORT))) {
            //不断地轮询连接状态，直到完成连
            LogUtil.getLog().d(TAG, "连接LOG>>>链接中" + "--time=" + System.currentTimeMillis());
            long ttime = System.currentTimeMillis();
            try {
                while (!socketChannel.finishConnect()) {
                    //在等待连接的时间里,为什么睡眠200ms？？？？？
                    //TODO：取消线程睡眠。2020.5.12
//                Thread.sleep(200);
                    long connTime = System.currentTimeMillis() - ttime;
                    if (connTime > 2 * 1000) {
                        LogUtil.getLog().d(TAG, ">>>链接中超时");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.writeLog(TAG + "--连接LOG--" + "链接失败:finishConnect出错");
                connect();
                return;
            }

            LogUtil.getLog().d(TAG + "--连接LOG", ">>>链接成功，总耗时=" + (System.currentTimeMillis() - ttime) + "--time=" + System.currentTimeMillis());
            if (!socketChannel.isConnected()) {
                LogUtil.getLog().e(TAG, "\n>>>>链接失败:链接不上,线程ver" + threadVer);
                LogUtil.writeLog(TAG + "--连接LOG--" + "链接失败:链接不上");
                throw new NetworkErrorException();
            }

            //3.
            long ctime = System.currentTimeMillis();
            if (socketChannel.tryTLS(1) == 0) {
                socketChannel.socket().close();
                socketChannel.close();
                socketChannel = null;
                LogUtil.getLog().e(TAG, "\n>>>>链接失败:校验证书失败,线程ver" + threadVer);
                LogUtil.writeLog(TAG + "--连接LOG--" + "鉴权失败");
                //证书问题
                throw new NetworkErrorException();

            } else {
                LogUtil.getLog().d(TAG + "--连接LOG", "\n>>>>鉴权成功,总耗时=" + (System.currentTimeMillis() - ctime));
                receive();
                //发送认证请求
                TcpConnection.getInstance(AppConfig.getContext()).addLog(System.currentTimeMillis() + "--Socket-开始鉴权");
                sendData(SocketData.msg4Auth(), null, "");
            }
        }

    }

    /***
     * 接收
     */
    private void receive() {
        ExecutorManager.INSTANCE.getReadThread().execute(new Runnable() {
            private long indexVer = threadVer;

            @Override
            public void run() {
                //限制版本控制
                try {
                    //8.6先加大接收容量
                    ByteBuffer readBuf = ByteBuffer.allocate(1024 * 8);//最大 65536 ，65536/1024=64kb，倍数小于64
                    int data_size = 0;
                    List<byte[]> temp = new ArrayList<>();
                    while (isRun() && (indexVer == threadVer)) {
                        data_size = socketChannel.read(readBuf);
                        if (data_size > 0) {
                            readBuf.flip();
                            //当次数据
                            byte[] data = new byte[data_size];
                            readBuf.get(data, 0, data_size);
                            if (data.length < 1024) {
                                LogUtil.getLog().d(TAG, "<<<<<接收数据: " + SocketPacket.bytesToHex(data));
                            }
                            LogUtil.getLog().d(TAG, "<<<<<接收数据总大小: " + data.length);

                            if (SocketPacket.isHead(data)) {//收到包头
                                LogUtil.getLog().d(TAG, ">>>接收数据: 是包头");
                                temp.clear();//每次收到包头把之前的缓存清理
                                byte[] ex = doPackage(data);//没处理完的断包
                                if (ex != null) {
                                    if (!SocketPacket.isHead(ex)) {//下个断包是否是包头不是就抛掉
                                        LogUtil.getLog().d(TAG, ">>抛掉错误数据" + SocketPacket.bytesToHex(ex));
                                    }
                                    temp.add(ex);
                                    LogUtil.getLog().d(TAG, ">>>[包头]剩余数据长度" + ex.length);
                                }
                            } else {//收到包体
                                LogUtil.getLog().d(TAG, ">>>接收数据: 是包体");
                                if (temp.size() > 0) {
                                    byte[] oldpk = SocketPacket.listToBytes(temp);
                                    LogUtil.getLog().d(TAG, ">>>上一个包大小" + oldpk.length);
                                    temp.clear();
                                    byte[] epk = SocketPacket.byteMergerAll(oldpk, data);//合成的新包
                                    LogUtil.getLog().d(TAG, ">>>合成包大小" + epk.length);
                                    byte[] ex = doPackage(epk);
                                    if (ex != null) {
                                        temp.add(ex);
                                        LogUtil.getLog().d(TAG, ">>>[包体]剩余数据长度" + ex.length);
                                    }
                                } else {//如果没有包头缓存,同样抛掉包体
                                    LogUtil.getLog().d(TAG, ">>>抛掉包体错误数据" + SocketPacket.bytesToHex(data));
                                }
                            }
                            LogUtil.getLog().d(TAG, ">>>当前缓冲区数: " + temp.size());
                            readBuf.clear();
                        } else {
                            // LogUtil.getLog().d(TAG, "<<<<<接收缓存: "+ data_size);
                        }
                        Thread.sleep(50);
                    }
                } catch (Exception e) {
                    if (e instanceof SocketEndException) {
                        LogUtil.getLog().e(TAG, "SocketEndException==连接已中断");
                        LogUtil.writeLog("连接LOG" + "--SocketEndException--连接被服务器中断");
                        stop(true);
                    } else {
                        e.printStackTrace();
                        LogUtil.getLog().e(TAG, "==getClass==" + e.getClass() + "===>>>接收异常run:===" + e.getMessage() + "===getLocalizedMessage=" + e.getLocalizedMessage());
                        LogUtil.writeLog("连接LOG--接收数据异常" + e.getMessage() + "===getLocalizedMessage=" + e.getLocalizedMessage());
                        stop(true);
                        if (isStart) {
                            startSocket();
                        }
                    }
                }
                setRunState(0);
                LogUtil.getLog().d(TAG, ">>>接收结束");
            }
        });

    }

    private int testindex = 0;
    private long heartbeatTime = 0;

    /***
     * 拆包和包处理
     * @param data
     */
    private byte[] doPackage(byte[] data) {
        byte[] ex = null;//额外数据
        if (data.length < 4) {
            return data;
        }
        int len = 8 + SocketPacket.getLength(data);//包长
        if (data.length < len) {//不能解析完整包
            ex = data;
        } else {//有一个以上完整的包
            List<byte[]> ls = SocketPacket.bytesToLists(data, len);
            byte[] indexData = ls.get(0);
            SocketPacket.DataType type = SocketPacket.getType(indexData);//类型
            //数据处理
            switch (type) {
                case PROTOBUF_MSG:
                    final MsgBean.UniversalMessage pmsg = SocketData.msgConversion(indexData);
                    if (pmsg == null) {
                        return null;
                    }
                    LogUtil.getLog().i(TAG, ">>>-----<处理消息 长度:" + indexData.length + " rid:" + pmsg.getRequestId());
                    heartbeatTime = System.currentTimeMillis();
                    //调试时不用吧onMsg放在线程里,这里为了优化分发的效率才如此处理
                    event.onMsg(pmsg);
                    break;
                case PROTOBUF_HEARTBEAT:
                    LogUtil.getLog().i(TAG, ">>>-----<收到心跳" + testindex);
                    heartbeatTime = System.currentTimeMillis();
                    testindex++;
                    break;
                case AUTH:
                    LogUtil.getLog().i(TAG, "连接LOG >>>-----<收到鉴权" + "--time=" + System.currentTimeMillis());
                    TcpConnection.getInstance(AppConfig.getContext()).addLog(System.currentTimeMillis() + "--Socket-成功鉴权");
                    MsgBean.AuthResponseMessage ruthmsg = SocketData.authConversion(indexData);
                    LogUtil.getLog().i(TAG, ">>>-----<鉴权" + ruthmsg.getAccepted());
                    //-------------------------------------------------------------------------test
                    if (ruthmsg.getAccepted() != 1) {//鉴权失败直接停止
                        stop(true);
                        // 上报后的Crash会显示该标签
                        CrashReport.setUserSceneTag(MainApplication.getInstance().getApplicationContext(), BUGLY_TAG_LOGIN);
                        // 上传异常数据
                        CrashReport.putUserData(MainApplication.getInstance().getApplicationContext(), BuglyTag.BUGLY_TAG_3, "鉴权失败退出登录");
                        BuglyLog.e(BuglyTag.BUGLY_TAG_3, "鉴权失败退出登录");
                        CrashReport.postCatchedException(new BuglyException());
                        //6.20 鉴权失败退出登录
                        EventBus.getDefault().post(new EventLoginOut());
                    } else {
                        SocketData.setPreServerAckTime(ruthmsg.getTimestamp());
                        setRunState(2);
                        //开始心跳
                        heartbeatTime = System.currentTimeMillis();
                        PayEnvironment.getInstance().initTime(ruthmsg.getTimestamp(), heartbeatTime);
                        heartbeatThread();
                        sendRequestForOffline();
                        //开始启动消息重发队列
                        sendListThread();
                    }
                    LogUtil.writeLog(TcpConnection.getInstance(AppConfig.getContext()).getLogList().toString());
                    LogUtil.getLog().d(TAG + "--连接LOG", "总耗时=" + TcpConnection.getInstance(AppConfig.getContext()).getLogList().toString());
                    TcpConnection.getInstance(AppConfig.getContext()).clearLogList();
                    break;
                case ACK:
                    MsgBean.AckMessage ackmsg = SocketData.ackConversion(indexData);
                    LogUtil.getLog().i(TAG, ">>>-----<收到回执" + ackmsg.getRequestId());
                    event.onACK(ackmsg);
                    //这里处理回执的事情
                    break;
                case REQUEST_MSG:
                    break;
                case OTHER:
                    LogUtil.getLog().i(TAG, ">>>-----<收到其他数据包");
                    break;
            }

            if (ls.size() > 1) {//多个包情况
                return doPackage(ls.get(1));
            }
        }
        return ex;
    }

    //发送请求离线的数据
    private void sendRequestForOffline() {
        sendData(SocketData.requestOffline(), null, "");
    }

    public static void notifyAck(Object o) {
        AckEvent event = new AckEvent();
        event.setData(o);
        EventBus.getDefault().post(event);
    }

    public boolean isKeepConnect() {
        return keepConnect;
    }

    public void setKeepConnect(boolean keepConnect) {
        LogUtil.getLog().i("跟踪", "keep--" + keepConnect);
        this.keepConnect = keepConnect;
    }

    public boolean isMainLive() {
        LogUtil.getLog().i("跟踪", "main-live--" + isMainLive);
        return isMainLive;
    }

    public void setMainLive(boolean mainLive) {
        isMainLive = mainLive;
    }

    //终止socket相关线程任务
    public void clearThread() {
        LogUtil.getLog().i(TAG, "clearThread--终止SocketThread");
        //取消心跳线程
        if (heardSchedule != null) {
            heardSchedule.cancel(true);
        }
        ExecutorManager.INSTANCE.getWriteThread().shutdown();
        ExecutorManager.INSTANCE.getReadThread().shutdown();
        ExecutorManager.INSTANCE.getSocketThread().shutdown();
    }
}
