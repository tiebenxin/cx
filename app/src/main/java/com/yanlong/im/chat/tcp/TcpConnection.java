package com.yanlong.im.chat.tcp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.IntDef;

import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Liszt
 * @date 2020/3/31
 * Description tcp封装类
 */
public class TcpConnection implements Connection {
    private final String TAG = TcpConnection.class.getSimpleName();

    private static TcpConnection INSTANCE;
    protected BroadcastReceiver mNetworkChangeReceiver;
    private static Context context;
    private boolean isRunning;
    private MsgDao msgDao = new MsgDao();
    private int from = 0;//0 是MainActivity,1 其他渠道
    private List<String> logList = new ArrayList<>();//连接日志


    public static TcpConnection getInstance(Context context) {
        TcpConnection.context = context;
        if (INSTANCE == null) {
            INSTANCE = new TcpConnection();
        }
        return INSTANCE;
    }


    private void initNetReceiver() {
        if (context == null) {
            return;
        }
        mNetworkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LogUtil.getLog().d(TAG, "连接LOG-->>>>>网路状态改变" + NetUtil.isNetworkConnected() + "--time=" + System.currentTimeMillis());
                LogUtil.writeLog(TAG + "--连接LOG--" + "网路状态改变--" + NetUtil.isNetworkConnected() + "--time=" + System.currentTimeMillis());

                if (NetUtil.isNetworkConnected()) {//链接成功
                    if (!isRunning) {
                        startConnect(from);
                    } else {
                        if (!SocketUtil.getSocketUtil().isRun()) {
                            SocketUtil.getSocketUtil().startSocket();
                        }
                    }
                } else {//链接失败
//                    stopConnect();
                    SocketUtil.getSocketUtil().stopSocket();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(mNetworkChangeReceiver, intentFilter);
    }

    @Override
    public void startConnect() {
        LogUtil.getLog().d(TAG, "连接LOG--开始连接--" + NetUtil.isNetworkConnected());
        LogUtil.writeLog(TAG + "--连接LOG--" + "开始连接--" + NetUtil.isNetworkConnected() + "--time=" + System.currentTimeMillis());
        this.from = EFrom.DEFAULT;
        if (NetUtil.isNetworkConnected()) {
            taskFixSendState();
            isRunning = true;
            SocketUtil.getSocketUtil().startSocket();
        }
        initNetReceiver();
    }

    //开始链接
    public void startConnect(@EFrom int from) {
        LogUtil.getLog().d(TAG, "连接LOG--开始连接--" + NetUtil.isNetworkConnected());
        LogUtil.writeLog(TAG + "--连接LOG--" + "开始连接--" + NetUtil.isNetworkConnected() + "--time=" + System.currentTimeMillis());
        this.from = from;
        if (NetUtil.isNetworkConnected()) {
            taskFixSendState();
            isRunning = true;
            SocketUtil.getSocketUtil().startSocket();
        }
        initNetReceiver();
    }

    //停止链接
    @Override
    public void stopConnect() {
        LogUtil.getLog().d(TAG, "连接LOG--暂停连接--" + NetUtil.isNetworkConnected());
        LogUtil.writeLog(TAG + "--连接LOG--" + "暂停连接--" + "--time=" + System.currentTimeMillis());
        SocketUtil.getSocketUtil().stop(true);
    }

    //销毁链接
    @Override
    public void destroyConnect() {
        if (from == EFrom.OTHER) {
            return;
        }
        LogUtil.getLog().d(TAG, "连接LOG--销毁连接--" + NetUtil.isNetworkConnected());
        LogUtil.writeLog(TAG + "--连接LOG--" + "销毁连接--" + "--time=" + System.currentTimeMillis());
        SocketUtil.getSocketUtil().endSocket();
        isRunning = false;
        if (context != null && mNetworkChangeReceiver != null) {
            context.unregisterReceiver(mNetworkChangeReceiver);
            mNetworkChangeReceiver = null;
        }
    }

    /***
     * 修改发送状态
     */
    private void taskFixSendState() {
        msgDao.msgSendStateToFail();
    }

    public void updateFrom(@EFrom int from) {
        this.from = from;
    }

    /*
     *启动连接，from
     * */
    @IntDef({EFrom.DEFAULT, EFrom.OTHER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EFrom {
        int DEFAULT = 0; // 默认，由主界面启动连接
        int OTHER = 1; // 其他，由其他界面启动连接
    }

    public synchronized void addLog(String log) {
        if (logList != null && logList.size() < 100) {
            logList.add(log);
        }
    }

    public List<String> getLogList() {
        return logList;
    }

    public void clearLogList() {
        if (logList != null) {
            logList.clear();
        }
    }


}
