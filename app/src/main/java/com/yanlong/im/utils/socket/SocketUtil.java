package com.yanlong.im.utils.socket;

import android.accounts.NetworkErrorException;
import android.util.Log;

import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.bean.EventLoginOut;
import net.cb.cb.library.bean.EventLoginOut4Conflict;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

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

            if (bean.getRejectType() == MsgBean.RejectType.ACCEPTED) {//接收到发送的消息了
                LogUtil.getLog().d(TAG, ">>>>>保存[发送]的消息到数据库 ");


                SocketData.msgSave4Me(bean);

            } else {
                LogUtil.getLog().d(TAG, ">>>>>ack被拒绝 :" + bean.getRejectType());

                SocketData.msgSave4MeFail(bean);
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
            LogUtil.getLog().d(TAG, ">>>>>保存[收到]的消息到数据库 " + bean.getToUid());
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
    private int isRun = 0;//0:没运行1:启动中:2运行中
    //线程版本
    private long threadVer = 0;

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
            LogUtil.getLog().i(TAG, ">>>>>>添加消息监听");
            eventLists.add(event);
        }

    }

    /***
     * 移除监听
     * @param event
     */
    public void removeEvent(SocketEvent event) {
        LogUtil.getLog().i(TAG, ">>>>>>移除消息监听");
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
        if (isRun())
            return;
        //线程版本+1
        // threadVer++;
        setRunState(1);
        try {
            if (socketChannel == null || !socketChannel.isConnected()) {
                connect();
            }
        } catch (Exception e) {
            setRunState(0);
            e.printStackTrace();
            stop();
        }
    }

    /***
     * 停止
     */
    public void stop() {
        if (!isRun())
            return;

        setRunState(0);
        //结束发送列队
        SendList.endList();

        //关闭信道
        try {
            socketChannel.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socketChannel = null;
        }

        LogUtil.getLog().d(TAG, ">>>>关闭连接-------------------------");

    }

    //6.20 强制结束
    public void stop2() {

        setRunState(0);
        //结束发送列队
        SendList.endList();

        //关闭信道
        try {
            socketChannel.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socketChannel = null;
        }

        LogUtil.getLog().d(TAG, ">>>>关闭连接-------------------------");

    }

    //鉴权失败
    private boolean isAuthFail = false;

    //重连检测时长
    private long recontTime = 5 * 1000;
    //心跳步长
    private long heartbeatStep = 10 * 1000;
//private boolean heartbeatStart=false;

    /***
     * 心跳线程
     */
    private void heartbeatThread() {
       /* if(heartbeatStart){
            return;
        }
        heartbeatStart=true;*/

        LogUtil.getLog().d(TAG, ">>>心跳线程启动---------------");
        new Thread(new Runnable() {
            //限制版本控制
            private long indexVer = threadVer;

            @Override
            public void run() {
                try {
                    while (isRun() && indexVer == threadVer) {
                        // while (heartbeatStart){
                        if (System.currentTimeMillis() - heartbeatTime > heartbeatStep * 1.5) {//心跳超时
                            //重启
                            stop();

                        } else {
                            sendData(SocketData.getPakage(SocketData.DataType.PROTOBUF_HEARTBEAT, null), null);

                        }


                        Thread.sleep(heartbeatStep);

                    }
                    LogUtil.getLog().d(TAG, ">>>心跳线程结束---------------");
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
                    while (isRun() && indexVer == threadVer) {
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


    private boolean isStart = false;

    /***
     * 启动
     */
    public void startSocket() {
        if (isStart) {
            LogUtil.getLog().i(TAG, ">>>>> 当前正在运行");
            return;
        }
        isStart = true;

        new Thread(new Runnable() {

            @Override
            public void run() {


                LogUtil.getLog().i(TAG, ">>>>>检查socketChannel 空: " + (socketChannel == null));
                if (socketChannel != null)
                    LogUtil.getLog().i(TAG, ">>>>>检查socketChannel 已连接:" + socketChannel.isConnected());
                LogUtil.getLog().i(TAG, ">>>>>检查运行状态:" + isRun);
                LogUtil.getLog().i(TAG, ">>>>>检查运行线程版本:" + threadVer);


                while (isStart) {
                    LogUtil.getLog().i(TAG, ">>>>>服务器链接检查isRun: " + isRun);
                    LogUtil.getLog().i(TAG, ">>>>>服务器链接socketChannel: " + socketChannel);
                    if (socketChannel != null)
                        LogUtil.getLog().i(TAG, ">>>>>服务器链接isConnected: " + socketChannel.isConnected());
                    if ((socketChannel == null || !socketChannel.isConnected()) && isRun == 0) {//没有启动,就执行启动

                        //线程版本+1
                        threadVer++;
                        LogUtil.getLog().i(TAG, ">>>>>新线程版本:" + threadVer);
                        SocketUtil.this.run();
                        LogUtil.getLog().i(TAG, ">>>>>新线程结束");

                    } else {//已经启动了
                        LogUtil.getLog().i(TAG, ">>>>>跳过当前线程版本:" + threadVer);
                    }

                    try {
                        Thread.sleep(recontTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();
    }

    /***
     * 结束socket
     */
    public void endSocket() {
        isStart = false;
        //    System.out.println(">>>endSocket:isRun"+isRun);
        new Thread(new Runnable() {
            @Override
            public void run() {
                stop2();
            }
        }).start();

        //    System.out.println(">>>endSocket:isRun"+isRun);

    }

    /***
     * 发送原始字节,无事务处理,用来做心跳,鉴权之类的
     * @param data
     */
    public void sendData(final byte[] data, final MsgBean.UniversalMessage.Builder msgTag) {
        if (!isRun())
            return;
        if (data == null)
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    ByteBuffer writeBuf = ByteBuffer.wrap(data);

                    LogUtil.getLog().i(TAG, ">>>发送长度:" + data.length);
                    LogUtil.getLog().i(TAG, ">>>发送:" + SocketData.bytesToHex(data));
                    socketChannel.write(writeBuf);

                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.getLog().e(TAG, ">>>发送失败" + SocketData.bytesToHex(data));
                    //取消发送队列,返回失败
                    if (msgTag != null) {
                        SendList.removeSendList(msgTag.getRequestId());
                    }

                    stop();
                    startSocket();
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


    //1.
    private SSLSocketChannel2 socketChannel;
    //  private SocketChannel socketChannel;


    /***
     * 链接
     */
    private void connect() throws Exception {

        //2.
        socketChannel = new SSLSocketChannel2(SocketChannel.open());
        //socketChannel =  SocketChannel.open();


        socketChannel.configureBlocking(false);


        //---------------------------------------------链接中
        LogUtil.getLog().d(TAG, "\n\n>>>>socket===============>>>" + AppConfig.SOCKET_IP + ":" + AppConfig.SOCKET_PORT + "\n\n");
        if (!socketChannel.connect(new InetSocketAddress(AppConfig.SOCKET_IP, AppConfig.SOCKET_PORT))) {
            //不断地轮询连接状态，直到完成连
            System.out.println(">>>链接中");
            long ttime = System.currentTimeMillis();
            while (!socketChannel.finishConnect()) {

                //在等待连接的时间里
                Thread.sleep(200);
                System.out.println(">>>链接进行" + (System.currentTimeMillis() - ttime));
                if (System.currentTimeMillis() - ttime > 2 * 1000) {
                    System.out.print(">>>链接中超时");
                    break;
                }

            }
            System.out.println(">>>链接执行完毕");
            if (!socketChannel.isConnected()) {
                LogUtil.getLog().e(TAG, "\n>>>>链接失败:链接不上,线程ver" + threadVer);
                throw new NetworkErrorException();
            }


            //----------------------------------------------------

            //3.
            if (socketChannel.tryTLS(1) == 0) {
                socketChannel.close();
                socketChannel = null;
                LogUtil.getLog().e(TAG, "\n>>>>链接失败:校验证书失败,线程ver" + threadVer);
                //证书问题
                throw new NetworkErrorException();

            } else {
                LogUtil.getLog().d(TAG, "\n>>>>链接成功:线程ver" + threadVer);
                receive();
                //发送认证请求
                sendData(SocketData.msg4Auth(), null);
            }


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
                    while (isRun() && (indexVer == threadVer)) {
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

                    stop();

                    startSocket();
                }
                setRunState(0);
                LogUtil.getLog().d(TAG, ">>>接收结束");

            }
        }).start();


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
                    heartbeatTime = System.currentTimeMillis();
                    testindex++;
                    break;
                case AUTH:
                    LogUtil.getLog().i(TAG, ">>>-----<收到鉴权");

                    MsgBean.AuthResponseMessage ruthmsg = SocketData.authConversion(indexData);
                    LogUtil.getLog().i(TAG, ">>>-----<鉴权" + ruthmsg.getAccepted());
                    //-------------------------------------------------------------------------test
                    if (ruthmsg.getAccepted() != 1) {//鉴权失败直接停止
                        isAuthFail = true;
                        stop();
                        //6.20 鉴权失败退出登录
                        EventBus.getDefault().post(new EventLoginOut());
                    } else {
                        setRunState(2);

                        //开始心跳
                        heartbeatTime = System.currentTimeMillis();
                        heartbeatThread();
                        //开始启动消息重发队列
                        sendListThread();


                    }


                    break;
                case ACK:

                    MsgBean.AckMessage ackmsg = SocketData.ackConversion(indexData);
                    LogUtil.getLog().i(TAG, ">>>-----<收到回执" + ackmsg.getRequestId());
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
