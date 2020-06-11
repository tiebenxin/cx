package com.yanlong.im.chat.task;

import android.os.Build;

import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.utils.LogUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/11 0011
 * @description
 */
public class OfflineMessage extends DispatchMessage {
    //线程池最大并发数量
    private final int THREAD_POOL_SIZE = 5;
    //批量处理离线消息，每批消息的容量
    private final int OFFLINE_BATCH_COUNT = 50;

    //记录离线消息保存成功的requestId msg_ids，离线消息是任务并发的
    public Map<String, List<String>> mSuccessOfflineMessageCount = new HashMap<>();
    /**
     * newFixedThreadPool与cacheThreadPool差不多，也是能reuse就用，但不能随时建新的线程
     * 任意时间点，最多只能有固定数目的活动线程存在，此时如果有新的线程要建立，只能放在另外的队列中等待，直到当前的线程中某个线程终止直接被移出池子
     */
    private ThreadPoolExecutor offlineMsgExecutor = null;


    @Override
    public void clear() {
        mSuccessOfflineMessageCount.clear();
        //停止离线任务，
        if (offlineMsgExecutor != null && offlineMsgExecutor.getActiveCount() > 0) {
            offlineMsgExecutor.shutdown();
            try {   // (所有的任务都结束的时候，返回TRUE)
                if (!offlineMsgExecutor.awaitTermination(5 * 1000, TimeUnit.MILLISECONDS)) {
                    // 超时的时候向线程池中所有的线程发出中断(interrupted)。
                    offlineMsgExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                // awaitTermination方法被中断的时候也中止线程池中全部的线程的执行。
                offlineMsgExecutor.shutdownNow();
            }
            offlineMsgExecutor = null;
        }
    }

    /**
     * 处理离线消息
     *
     * @param bean
     */
    @Override
    public void dispatch(MsgBean.UniversalMessage bean, Realm realm) {
        LogUtil.writeLog("dispatchMsg start" + bean.getRequestId());
        try {
            List<MsgBean.UniversalMessage.WrapMessage> msgList = bean.getWrapMsgList();
            if (msgList != null && msgList.size() > 0) {
                mSuccessOfflineMessageCount.put(bean.getRequestId(), new ArrayList<>());
                int index = 0;
                int page = OFFLINE_BATCH_COUNT;//每次处理消息的数量，可自己调整，以提高接收离线消息的速度,太小也会过慢，启动任务需要时间
                int max = Math.min(msgList.size(), page);
                while (index < msgList.size()) {
                    /**开启并发任务******************************************/
                    startConcurrentTask(msgList, index, max, bean.getRequestId(), bean.getMsgFrom());
                    index = max;
                    max = Math.min(msgList.size(), index + page);
                }
            } else {//空消息 回执
                SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(bean.getRequestId(), null, bean.getMsgFrom(), false, true), null, bean.getRequestId());
            }
        } catch (Exception e) {
            DaoUtil.reportException(e);
        }
    }
    private int lastsize=0;
    List<Integer> msgIds2 = new ArrayList<>();
    /**
     * 开启接收离线消息的并发任务
     * 一个任务最多OFFLINE_BATCH_COUNT个消息处理
     *
     * @param msgList
     * @param mIndex
     * @param max
     * @param requestId
     * @param msgFrom
     */
    private void startConcurrentTask(List<MsgBean.UniversalMessage.WrapMessage> msgList, int mIndex, int max,
                                     String requestId, int msgFrom) {
        if(offlineMsgExecutor == null) offlineMsgExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        offlineMsgExecutor.execute(() -> {
            Realm realm = DaoUtil.open();
            try {
                for (int i = mIndex; i < max; i++) {
                    MsgBean.UniversalMessage.WrapMessage wrapMessage = msgList.get(i);

                    if (mSuccessOfflineMessageCount.containsKey(requestId)){
                        mSuccessOfflineMessageCount.get(requestId).add(wrapMessage.getMsgId());
                    }
                    msgIds2.add(i);

                    if(lastsize==0){
                        lastsize = msgIds2.size();
                    }else if(lastsize == msgIds2.size()){
                        LogUtil.writeLog("dispatchMsg  i=" + i + ",msgIds2=" + msgIds2.size());
                        lastsize = msgIds2.size();
                    }else{
                        lastsize = msgIds2.size();
                    }
                    //是否为本批消息的最后一条消息,并发的只能取数量
                    boolean isLastMessage = mSuccessOfflineMessageCount.get(requestId).size() == 0;
                    //开始处理消息
                    boolean toDOResult = toDoMsg(realm, wrapMessage, requestId, msgFrom == 1, msgList.size(),
                            isLastMessage);
                    if (toDOResult) {//成功，保存记录
                        if (mSuccessOfflineMessageCount.containsKey(requestId))
                            mSuccessOfflineMessageCount.get(requestId).remove(wrapMessage.getMsgId());
                    } else { //有一个失败，表示接收全部失败
                        mSuccessOfflineMessageCount.remove(requestId);
                    }

                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    msgIds2.sort(new Comparator<Integer>() {
                        @Override
                        public int compare(Integer o1, Integer o2) {
                            return o1<o2?1:0;
                        }
                    });
                    LogUtil.writeLog("dispatchMsg  max i="+ msgIds2.get(msgIds2.size()-1)+",size="+msgIds2.size());
                }
                if (mSuccessOfflineMessageCount.containsKey(requestId)) {
                    if (mSuccessOfflineMessageCount.get(requestId).size() == 0) {
                        LogUtil.writeLog("dispatchMsg end success");
                        //接收完离线消息的处理
                        recieveOfflineFinished(realm, msgFrom == 1, msgList.size());
                        //全部保存成功，消息回执
//                    SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(requestId, null, msgFrom, false, true), null, requestId);
                        mSuccessOfflineMessageCount.remove(requestId);
                    }
                } else {
                    LogUtil.writeLog("dispatchMsg end error");
                    //接收完离线消息的处理
                    recieveOfflineFinished(realm, msgFrom == 1, msgList.size());
                    mSuccessOfflineMessageCount.remove(requestId);
                }
            } catch (Exception e) {
                LogUtil.writeLog("e" + e.getMessage());
                LogUtil.writeError(e);
            } finally {
                DaoUtil.close(realm);
            }

        });
    }
    /**
     * 接收完离线消息
     *
     * @param realm
     * @param isOfflineMsg  是否是离线消息
     * @param batchMsgCount 本次批量消息数量
     */
    public void recieveOfflineFinished(Realm realm, boolean isOfflineMsg, int batchMsgCount) {
        //本次离线消息是否接收完成
        boolean isReceivedOfflineCompleted = SocketData.isEnough(batchMsgCount);
        if (isOfflineMsg && isReceivedOfflineCompleted) {//离线消息接收完了
            //清空双向清除数据
            if (repository.historyCleanMsg.size() > 0) {
                repository.historyCleanMsg.clear();
            }
            //更正离线已读消息-已读状态、未读数量、阅后即焚
            repository.updateOfflineReadMsg(realm);
        }
    }


}
