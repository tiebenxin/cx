package com.yanlong.im.utils.socket;

import android.util.Log;

import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.utils.LogUtil;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SocketUtil {
    private static final String TAG = "SocketUtil";
    private static SocketUtil socketUtil;

    private static List<SocketEvent> eventLists = new CopyOnWriteArrayList<>();
    //事件分发
    private static SocketEvent event = new SocketEvent() {
        @Override
        public void onHeartbeat() {

        }

        @Override
        public void onACK(MsgBean.AckMessage bean) {

            if(bean.getRejectType()== MsgBean.RejectType.ACCEPTED) {//接收到发送的消息了
                LogUtil.getLog().d(TAG, ">>>>>保存[发送]的消息到数据库 " );


                SocketData.msgSave4Me(bean);

           }else{
                LogUtil.getLog().d(TAG, ">>>>>ack被拒绝 :"+bean.getRejectType() );
            }


            for (SocketEvent ev : eventLists) {
                if (ev != null) {
                    ev.onACK(bean);
                }
                //这里可以做为空,自动移除

            }


        }

        @Override
        public void onMsg(MsgBean.UniversalMessage bean) {
            //保存消息和处理回执
            LogUtil.getLog().d(TAG, ">>>>>保存[收到]的消息到数据库 "+bean.getToUid());
            SocketData.magSaveAndACK(bean);

            for (SocketEvent ev : eventLists) {
                if (ev != null) {
                    ev.onMsg(bean);
                }
                //这里可以做为空,自动移除

            }


        }

        @Override
        public void onSendMsgFailure(MsgBean.UniversalMessage.Builder bean) {
            LogUtil.getLog().e(TAG, ">>>>>发送失败了" + bean.getRequestId());
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
            for (SocketEvent ev : eventLists) {
                if (ev != null) {
                    ev.onLine(state);
                }
            }

        }
    };
    //正在运行
    private boolean isRun = false;
    //线程版本
    private long threadVer = 0;

    public boolean isRun() {
        return isRun;
    }

    /***
     * 改变运行状态
     * @param state
     */
    private void setRunState(boolean state){
        isRun=state;
        event.onLine(state);
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
        eventLists.add(event);
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
    public void run() {
        if (isRun)
            return;
        //线程版本+1
        threadVer++;
        setRunState(true) ;
        try {
            if (socketChannel == null || !socketChannel.isConnected()) {
                connect();

            }

        } catch (Exception e) {
            setRunState(false);
            e.printStackTrace();
            LogUtil.getLog().e(TAG, e.getMessage());

        }

    }

    /***
     * 停止
     */
    public void stop() {
        if (!isRun)
            return;

        setRunState(false) ;
        //关闭信道
        try {
            socketChannel.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //鉴权失败
    private boolean isAuthFail = false;
    //重试3次
    private int recontNum = 30;
    //当前重试
    private int reconIndex = 0;
    private long recontTime = 15 * 1000;
    //心跳步长
    private long heartbeatStep = 30 * 1000;

    /***
     * 心跳线程
     */
    private void heartbeatThread() {
        LogUtil.getLog().d(TAG, ">>>心跳线程启动---------------");
        new Thread(new Runnable() {
            //限制版本控制
            private long indexVer = threadVer;

            @Override
            public void run() {
                try {
                    while (isRun && indexVer == threadVer) {
                        sendData(SocketData.getPakage(SocketData.DataType.PROTOBUF_HEARTBEAT, null), null);

                        Thread.sleep(heartbeatStep);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LogUtil.getLog().d(TAG, ">>>心跳异常run: " + e.getMessage());
                }
            }
        }).start();


    }

    //队列遍历步长:必须小于每条消息重发时长
    private long sendListStep = 2 * 1000;

    /***
     * 发送队列线程
     */
    private void sendListThread() {
        LogUtil.getLog().d(TAG, ">>>发送队列线程启动---------------");
        new Thread(new Runnable() {
            //限制版本控制
            private long indexVer = threadVer;

            @Override
            public void run() {
                try {
                    while (isRun && indexVer == threadVer) {
                        SendList.loopList();

                        Thread.sleep(sendListStep);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LogUtil.getLog().d(TAG, ">>>队列异常run: " + e.getMessage());
                }
            }
        }).start();


    }

    /***
     * 重连
     */
    public void reconnection() {
        isAuthFail = false;
        try {


            reconIndex = 0;
            if (isRun) {
                // return;
                stop();
            }
            //小于最大重连次数,或者非鉴权失败
            while (reconIndex <= recontNum && !isAuthFail) {
                long time = System.currentTimeMillis();//执行时间

                reconIndex++;

                if (socketChannel != null && socketChannel.isConnected()) {
                    break;
                }
                LogUtil.getLog().e(TAG, "重试次数" + reconIndex);
                run();
                time = System.currentTimeMillis() - time;
                time = time >= recontTime ? 0 : recontTime - time;
                Thread.sleep(time);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * 发送原始字节,无事务处理,用来做心跳,鉴权之类的
     * @param data
     */
    public void sendData(final byte[] data, final MsgBean.UniversalMessage.Builder msgTag) {
        if (!isRun)
            return;
        if (data == null)
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    ByteBuffer writeBuf = ByteBuffer.wrap(data);
                    //    while (writeBuf.hasRemaining()) {
                    LogUtil.getLog().i(TAG, ">>>发送长度:" + data.length);
                    LogUtil.getLog().i(TAG, ">>>发送:" + SocketData.bytesToHex(data));
                    socketChannel.write(writeBuf);
                    //  }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.getLog().e(TAG, ">>>发送失败" + SocketData.bytesToHex(data));
                    //取消发送队列,返回失败
                    if (msgTag != null) {
                        SendList.removeSendList(msgTag.getRequestId());
                    }

                    reconnection();
                }
            }
        }).start();


    }

    /***
     * 发送消息,有事务处理,用来做普通消息,红包,等业务
     * @param msg
     */
    public void sendData4Msg(MsgBean.UniversalMessage.Builder msg) {
        //添加到消息队中监听
        SendList.addSendList(msg.getRequestId(), msg);
        sendData(SocketData.getPakage(SocketData.DataType.PROTOBUF_MSG, msg.build().toByteArray()), msg);
    }


    private SocketChannel socketChannel;


    /***
     * 链接
     */
    public void connect() throws Exception {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);


        //---------------------------------------------链接中
        LogUtil.getLog().d(TAG, "\n\n>>>>socket===============>>>" + AppConfig.SOCKET_IP+":"+AppConfig.SOCKET_PORT+"\n\n");
        if (!socketChannel.connect(new InetSocketAddress(AppConfig.SOCKET_IP, AppConfig.SOCKET_PORT))) {
            //不断地轮询连接状态，直到完成连
            System.out.println(">>>链接中");
            while (!socketChannel.finishConnect()) {
                //在等待连接的时间里
                Thread.sleep(200);
            }


            //----------------------------------------------------
            LogUtil.getLog().d(TAG, "\n>>>>链接成功:线程ver" + threadVer);


            receive();

            //发送认证请求
            sendData(SocketData.msg4Auth(), null);

        }

    }

    /***
     * 接收
     */
    private void receive() {

        new Thread(new Runnable() {
            //限制版本控制
            private long indexVer = threadVer;

            @Override
            public void run() {
                try {

                    ByteBuffer readBuf = ByteBuffer.allocate(1024);
                    int data_size = 0;
                    List<byte[]> temp = new ArrayList<>();
                    while (isRun && (indexVer == threadVer)) {
                        data_size = socketChannel.read(readBuf);
                        if (data_size > 0) {
                            readBuf.flip();
                            //当次数据
                            byte[] data = new byte[data_size];
                            readBuf.get(data, 0, data_size);

                            LogUtil.getLog().d(TAG, ">>>接收数据: " + SocketData.bytesToHex(data));

                            if (SocketData.isHead(data)) {//收到包头
                                LogUtil.getLog().d(TAG, ">>>接收数据: 是包头");
                                temp.clear();//每次收到包头把之前的缓存清理
                                byte[] ex = doPackage(data);//没处理完的断包
                                if (ex != null) {
                                    if (!SocketData.isHead(ex)) {//下个断包是否是包头不是就抛掉
                                        LogUtil.getLog().d(TAG, ">>抛掉错误数据" + SocketData.bytesToHex(ex));
                                    }

                                    temp.add(ex);
                                }

                            } else {//收到包体
                                LogUtil.getLog().d(TAG, ">>>接收数据: 是包体");
                                if (temp.size() > 0) {
                                    byte[] oldpk = SocketData.listToBytes(temp);
                                    temp.clear();
                                    byte[] epk = SocketData.byteMergerAll(oldpk, data);//合成的新包

                                    byte[] ex = doPackage(epk);
                                    if (ex != null) {
                                        temp.add(ex);
                                    }
                                } else {//如果没有包头缓存,同样抛掉包体
                                    LogUtil.getLog().d(TAG, ">>>抛掉包体错误数据" + SocketData.bytesToHex(data));
                                }


                            }

                            LogUtil.getLog().d(TAG, ">>>当前缓冲区数: " + temp.size());

                            readBuf.clear();
                        }
                        Thread.sleep(200);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.getLog().d(TAG, ">>>接收异常run: " + e.getMessage());
                    reconnection();
                }
                LogUtil.getLog().d(TAG, ">>>接收结束");

            }
        }).start();


    }

    private int testindex = 0;

    /***
     * 拆包和包处理
     * @param data
     */
    private byte[] doPackage(byte[] data) {
        byte[] ex = null;//额外数据
        if (data.length < 4) {
            return data;
        }


        int len = 4 + SocketData.getLength(data);//包长
        if (data.length < len) {//不能解析完整包
            ex = data;
        } else {//有一个以上完整的包
            List<byte[]> ls = SocketData.bytesToLists(data, len);

            byte[] indexData = ls.get(0);

            SocketData.DataType type = SocketData.getType(indexData);//类型
            //数据处理
            switch (type) {
                case PROTOBUF_MSG:
                    MsgBean.UniversalMessage pmsg = SocketData.msgConversion(indexData);
                    LogUtil.getLog().i(TAG, ">>>-----<收到消息 长度:" + indexData.length + " rid:" + pmsg.getRequestId());

                    event.onMsg(pmsg);
                    break;
                case PROTOBUF_HEARTBEAT:
                    LogUtil.getLog().i(TAG, ">>>-----<收到心跳" + testindex);
                    testindex++;
                    break;
                case AUTH:
                    LogUtil.getLog().i(TAG, ">>>-----<收到鉴权");

                    MsgBean.AuthResponseMessage ruthmsg = SocketData.authConversion(indexData);
                    LogUtil.getLog().i(TAG, ">>>-----<鉴权" + ruthmsg.getAccepted());
                    //-------------------------------------------------------------------------test
                    if (!ruthmsg.getAccepted()){//鉴权失败直接停止
                        isAuthFail = true;
                        stop();
                    } else {
                        //开始心跳
                        heartbeatThread();
                        //开始启动消息重发队列
                        sendListThread();
                    }


                    break;
                case ACK:

                    MsgBean.AckMessage ackmsg = SocketData.ackConversion(indexData);
                    LogUtil.getLog().i(TAG, ">>>-----<收到回执"+ackmsg.getRequestId());
                    event.onACK(ackmsg);
                    //这里处理回执的事情
                    break;
                case OTHER:
                    LogUtil.getLog().i(TAG, ">>>-----<收到其他数据包");
                    break;
            }

            //---------------------------------
            if (ls.size() > 1) {//多个包情况
                return doPackage(ls.get(1));
            }


        }


        return ex;
    }


}
