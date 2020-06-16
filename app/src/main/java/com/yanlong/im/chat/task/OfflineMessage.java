package com.yanlong.im.chat.task;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.utils.LogUtil;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/11 0011
 * @description
 */
public class OfflineMessage extends DispatchMessage {
    //批量处理离线消息，每批消息的容量
    private final int OFFLINE_BATCH_COUNT = 50;

    /**
     * 保存requestId某批消息，保存成功消息的个数，所有接收完成、某个消息失败 后移除（1个消息失败，表示本批所有消息接收失败）
     * 线程安全map
     * ConcurrentHashMap
     * 记录离线消息保存成功的requestId msg_ids，离线消息是任务并发的
     * 并发中，线程不安全的对象会出现数据丢失
     * AtomicInteger 线程安全
     */
    private ConcurrentHashMap<String, AtomicInteger> mSuccessMessageCount = new ConcurrentHashMap<>();
    //用于保存成功数据
    /**
     * 所有离线消息接收完成后释放
     */
    private CopyOnWriteArraySet<String> mSuccessMsgIds = new CopyOnWriteArraySet<>();
    /**
     * 等待更新sessionGid,离线批量更新session
     */
    private CopyOnWriteArraySet<String> mToUpdateSessionGids = new CopyOnWriteArraySet<>();
    /**
     * 等待更新session uid好友,离线批量更新session
     */
    private CopyOnWriteArraySet<Long> mToUpdateSessionUids = new CopyOnWriteArraySet<>();
    /**
     * 它是一个数量无限多的线程池，它所有的线程都是非核心线程
     * 当线程空闲一定时间时就会被系统回收，所以理论上该线程池不会有占用系统资源的无用线程。
     * 适合执行大量耗时小的任务
     */
    private ThreadPoolExecutor executor = null;

    public OfflineMessage() {
        super(true);
    }


    @Override
    public void clear() {
        mSuccessMsgIds.clear();
        mSuccessMessageCount.clear();
        //停止离线任务，
        if (executor != null && executor.getActiveCount() > 0) {
            executor.shutdown();
            try {   // (所有的任务都结束的时候，返回TRUE)
                if (!executor.awaitTermination(5 * 1000, TimeUnit.MILLISECONDS)) {
                    // 超时的时候向线程池中所有的线程发出中断(interrupted)。
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                // awaitTermination方法被中断的时候也中止线程池中全部的线程的执行。
                executor.shutdownNow();
            }
            executor = null;
        }
    }

    /**
     * 过滤消息-不接收或不重复接收
     *
     * @param wrapMessage
     * @return
     */
    @Override
    public boolean filter(MsgBean.UniversalMessage.WrapMessage wrapMessage) {
        if (wrapMessage.getMsgType() == MsgBean.MessageType.UNRECOGNIZED) {
            return true;
        }
        /******丢弃离线消息-执行过双向删除，在指令之前的消息 2020/4/28****************************************/
        if (isBeforeHistoryCleanMessage(wrapMessage)) {
            return true;
        }
        if (mSuccessMsgIds.contains(wrapMessage.getMsgId())) {//已经保存过了
            return true;
        } else {
            //收集gid和uid,用于最后更新session
            collectBatchMessageGidAndUids(wrapMessage.getGid(), wrapMessage.getFromUid(), wrapMessage.getToUid());
        }
        return false;
    }

    /**
     * 处理离线消息
     *
     * @param bean
     */
    @Override
    public void dispatch(MsgBean.UniversalMessage bean, Realm realm) {
        try {
            List<MsgBean.UniversalMessage.WrapMessage> msgList = bean.getWrapMsgList();
            if (msgList != null && msgList.size() > 0) {
                Log.e("raleigh_test", "start recevie Offline batchCount="+msgList.size());
                mSuccessMessageCount.put(bean.getRequestId(), new AtomicInteger());
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
                SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(bean.getRequestId(), null, bean.getMsgFrom(), false, SocketData.isEnough(0)), null, bean.getRequestId());
            }
        } catch (Exception e) {
            DaoUtil.reportException(e);
        }
    }


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
        if (executor == null)
            executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.execute(() -> {
            Realm realm = DaoUtil.open();
            try {
                for (int i = mIndex; i < max; i++) {
                    MsgBean.UniversalMessage.WrapMessage wrapMessage = msgList.get(i);
                    //是否为本批消息的最后一条消息,并发的只能取数量
                    boolean isLastMessage = mSuccessMessageCount.get(requestId).get() == msgList.size();
                    boolean toDOResult = false;
                    //开始处理消息
                    toDOResult = handlerMessage(realm, wrapMessage, requestId, msgFrom == 1, msgList.size(),
                            isLastMessage);
                    if (toDOResult) {
                        //成功，递增成功数量
                        pushSuccessCount(requestId);
                        //临时保存
                        mSuccessMsgIds.add(wrapMessage.getMsgId());
                    } else { //有一个失败，表示接收全部失败
                        //清除数量
                        clearSuccessCount(requestId);
                    }
                }
                //检测本批的所有消息是否已经接收完成
                checkBatchMessageCompleted(realm, requestId, msgList.size(), msgFrom);
            } catch (Exception e) {
                Log.e("raleigh_test", "startConcurrentTask e=" + e.getMessage());
                //检测所有离线消息是否接收完成
                checkReceivedAllOfflineCompleted(realm, msgList.size(), false);
                //清除数量
                clearSuccessCount(requestId);
                LogUtil.writeError(e);
            } finally {
                DaoUtil.close(realm);
            }

        });
    }

    /**
     * 检测本批消息是否已经接收完成
     *
     * @param requestId
     * @param batchTotalCount 本批消息的总数量
     * @param msgFrom         离线或在线消息
     */
    private void checkBatchMessageCompleted(Realm realm, String requestId, int batchTotalCount, int msgFrom) {
        if (mSuccessMessageCount.containsKey(requestId)) {
            if (mSuccessMessageCount.get(requestId).get() == batchTotalCount) {
                Log.e("raleigh_test", "checkBatchMessageCompleted Success");
                //刷新session
                //全部保存成功，消息回执
                SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(requestId, null, msgFrom, false, SocketData.isEnough(batchTotalCount)), null, requestId);
                //检测所有离线消息是否接收完成
                checkReceivedAllOfflineCompleted(realm, batchTotalCount, true);
                //清除数量
                clearSuccessCount(requestId);
            }
        } else {//被移除了，说明至少有一个消息接收失败
            Log.e("raleigh_test", "checkBatchMessageCompleted Failed");
            //检测所有离线消息是否接收完成
            checkReceivedAllOfflineCompleted(realm, batchTotalCount, false);
        }
    }

    /**
     * 检测接收完所有离线消息
     *
     * @param realm
     * @param batchMsgCount 本次批量消息数量
     * @param isSuccess     保存成功
     */
    public void checkReceivedAllOfflineCompleted(Realm realm, int batchMsgCount, boolean isSuccess) {
        //本次离线消息是否接收完成
        boolean isReceivedOfflineCompleted = SocketData.isEnough(batchMsgCount);
        if (isReceivedOfflineCompleted) {//离线消息接收完了
            Log.e("raleigh_test", "ReceivedOfflineCompleted batchMsgCount="+batchMsgCount);
            //清空双向清除数据
            if (repository.historyCleanMsg.size() > 0) {
                repository.historyCleanMsg.clear();
            }
            //更正离线已读消息-已读状态、未读数量、阅后即焚
            repository.updateOfflineReadMsg(realm);

            if (isSuccess && SocketUtil.getSocketUtil().getOnLineState()) {
                //有网，保存完成，且是最后一批离线，清除
                //本次离线消息是否接收完成
                mSuccessMsgIds.clear();
            }
        }
        Log.e("raleigh_test", "updateSessionsWhenBatchCompleted isSuccess="+isSuccess);
        //更新所有的session
        updateSessionsWhenBatchCompleted(realm);
    }

    /**
     * 收集本批消息的gid和UId用于更新session，更新session后移除
     *
     * @param gid
     * @param fromUid
     */
    private void collectBatchMessageGidAndUids(String gid, Long fromUid, Long toUid) {
        if (TextUtils.isEmpty(gid)) {
            long friendUid = fromUid;
            boolean isFromSelf = UserAction.getMyId() != null && fromUid == UserAction.getMyId().intValue();
            if (fromUid == -1 || isFromSelf) {//-1表示系统消息
                friendUid = toUid;
            }
            if (UserAction.getMyId() != null && friendUid != UserAction.getMyId().intValue()) {
                mToUpdateSessionUids.add(isFromSelf ? toUid : fromUid);
            }
        } else {
            mToUpdateSessionGids.add(gid);
        }
    }

    /**
     * 本批消息接收完成后，更新本批消息中相关的session
     *
     * @param realm
     */
    private void updateSessionsWhenBatchCompleted(Realm realm) {
        while (mToUpdateSessionUids.iterator().hasNext()) {
            Long uid = mToUpdateSessionUids.iterator().next();
            mToUpdateSessionUids.remove(uid);
            repository.offlineUpdateSession(realm, null, uid);
        }

        while (mToUpdateSessionGids.iterator().hasNext()) {
            String gid = mToUpdateSessionGids.iterator().next();
            mToUpdateSessionGids.remove(gid);
            repository.offlineUpdateSession(realm, gid, null);
        }
    }


    /**
     * 清除成功数-接收完一批后
     *
     * @param requestId
     */
    private synchronized void clearSuccessCount(String requestId) {
        if (mSuccessMessageCount.containsKey(requestId)) {
            mSuccessMessageCount.remove(requestId);
        }
    }

    /**
     * 递增记录成功数据 -每个消息成功时记录
     *
     * @param requestId
     */
    private synchronized void pushSuccessCount(String requestId) {
        if (mSuccessMessageCount.containsKey(requestId)) {
            mSuccessMessageCount.get(requestId).getAndIncrement();
        }
    }

    /**
     * 是否是双向清除之前的消息
     * 是则丢弃该消息
     *
     * @param wrapMessage
     */
    private boolean isBeforeHistoryCleanMessage(MsgBean.UniversalMessage.WrapMessage wrapMessage) {
        boolean result = false;
        if (TextUtils.isEmpty(wrapMessage.getGid()) && repository.historyCleanMsg.size() > 0) {//单聊
            long timestamp = wrapMessage.getTimestamp();
            long fromUid = wrapMessage.getFromUid();
            long toUid = wrapMessage.getToUid();
            //接收到的消息是否在双向清除之前-对方发的或我发的
            if (repository.historyCleanMsg.containsKey(fromUid) && repository.historyCleanMsg.get(fromUid) >= timestamp) {
                if (repository.historyCleanMsg.get(fromUid) >= timestamp) {
                    result = true;
                }
            }
            if (repository.historyCleanMsg.containsKey(toUid) && repository.historyCleanMsg.get(toUid) >= timestamp) {
                if (repository.historyCleanMsg.get(toUid) >= timestamp) {
                    result = true;
                }
            }
        }
        return result;
    }
}
