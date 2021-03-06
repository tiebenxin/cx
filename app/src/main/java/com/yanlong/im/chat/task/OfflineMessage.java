package com.yanlong.im.chat.task;

import android.text.TextUtils;

import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.manager.excutor.ExecutorManager;
import net.cb.cb.library.utils.LogUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;

/**
 * 离线消息是一批一批接收，当一批接收完成后，才会请求下一批
 *
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/11 0011
 * @description
 */
public class OfflineMessage extends DispatchMessage {
//    //批量处理离线消息，每批消息的容量
//    private final int OFFLINE_BATCH_COUNT = getOfflineCount()+1;

    /**
     * 记录当前批消息的已处理完成个数
     * 并发中，线程不安全的对象会出现数据丢失
     * AtomicInteger 线程安全
     */
    private AtomicInteger mBatchCompletedCount = new AtomicInteger();
    /**
     * 记录当前批消息的重复消息个数
     * AtomicInteger 线程安全
     */
    private AtomicInteger mBatchRepeatCount = new AtomicInteger();
    //用于保存成功数据
    /**
     * 所有离线消息接收完成后释放
     */
    private CopyOnWriteArraySet<String> mBatchSuccessMsgIds = new CopyOnWriteArraySet<>();
    /**
     * 等待更新sessionGid,离线批量更新session
     */
    private CopyOnWriteArraySet<String> mBatchToUpdateSessionGids = new CopyOnWriteArraySet<>();
    /**
     * 等待更新session uid好友,离线批量更新session
     */
    private CopyOnWriteArraySet<Long> mBatchToUpdateSessionUids = new CopyOnWriteArraySet<>();
    /**
     * 它是一个数量无限多的线程池，它所有的线程都是非核心线程
     * 当线程空闲一定时间时就会被系统回收，所以理论上该线程池不会有占用系统资源的无用线程。
     * 适合执行大量耗时小的任务
     */
//    private ThreadPoolExecutor executor = null;

    /**
     * 当前请求的id
     * 为了处理并发任务无法真正停止的问题，因为接受离线是单批接收的，所以每批量消息在执行时唯一
     * 退出登录时，清除掉requestid,以停止后面的任务
     */
    private String currentRequestId = null;

    public OfflineMessage() {
        super(true);
    }


    @Override
    public void clear() {
        currentRequestId = null;
        repository.clear();
        //停止离线任务，
//        if (executor != null && executor.getActiveCount() > 0) {
//            /**向线程池中所有的线程发出中断(interrupted)。
//             * 会尝试interrupt线程池中正在执行的线程
//             * 等待执行的线程也会被取消
//             * 但是并不能保证一定能成功的interrupt线程池中的线程。可能必须要等待所有正在执行的任务都执行完成了才能退出
//             */
//            executor.shutdownNow();
//            executor = null;
//        }
        ExecutorManager.INSTANCE.getOfflineThread().shutdown();
        mBatchSuccessMsgIds.clear();
        mBatchCompletedCount.set(0);
        mBatchRepeatCount.set(0);
        mBatchToUpdateSessionUids.clear();
        mBatchToUpdateSessionGids.clear();
    }

    /**
     * 过滤消息-不接收或不重复接收
     *
     * @param wrapMessage
     * @return
     */
    @Override
    public boolean filter(MsgBean.UniversalMessage.WrapMessage wrapMessage) {
        boolean result = false;
        if (wrapMessage.getMsgType() == MsgBean.MessageType.UNRECOGNIZED) {
            result = true;
        }
        /******丢弃离线消息-执行过双向删除，在指令之前的消息 2020/4/28****************************************/
        if (isBeforeHistoryCleanMessage(wrapMessage)) {
            result = true;
        }
        if (mBatchSuccessMsgIds.contains(wrapMessage.getMsgId())) {//已经保存过了
            result = true;
        } else {
            //收集gid和uid,用于最后更新session, 已读不需要更新Session时间
            if (wrapMessage.getMsgType() != MsgBean.MessageType.READ && wrapMessage.getMsgType() != MsgBean.MessageType.REPORT_GEO_POSITION && wrapMessage.getMsgType() != MsgBean.MessageType.HISTORY_CLEAN) {
                collectBatchMessageGidAndUids(wrapMessage.getGid(), wrapMessage.getFromUid(), wrapMessage.getToUid());
            }
        }
        LogUtil.getLog().i(TAG, "消息LOG--离线--filter=" + result);
        return false;
    }

    /**
     * 处理离线消息
     *
     * @param bean
     */
    @Override
    public synchronized void dispatch(MsgBean.UniversalMessage bean, Realm realm1) {
        currentRequestId = bean.getRequestId();
        if (mBatchCompletedCount.get() != 0) mBatchCompletedCount.set(0);
//        if (executor == null)
//            executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        /**开启并发异步任务******************************************/
        ExecutorManager.INSTANCE.getOfflineThread().execute(new Runnable() {
            @Override
            public void run() {
                List<MsgBean.UniversalMessage.WrapMessage> msgList = bean.getWrapMsgList();
                if (msgList != null && msgList.size() > 0) {
                    int totalSize = msgList.size();
                    Realm realm = DaoUtil.open();
                    String requestId = bean.getRequestId();
                    boolean isOfflineMsg = bean.getMsgFrom() == 1;
                    try {
                        for (int i = 0; i < totalSize; i++) {
                            if (currentRequestId == null) {
                                mBatchCompletedCount.set(0);
                                mBatchSuccessMsgIds.clear();
                                break;
                            }
                            MsgBean.UniversalMessage.WrapMessage wrapMessage = msgList.get(i);
//                            LogUtil.writeLog("dispatch--离线消息--" + "msgId=" + wrapMessage.getMsgId() + "--msgType=" + wrapMessage.getMsgType() + "--gid=" + wrapMessage.getGid() + "--fromUid=" + wrapMessage.getFromUid());
                            //是否为本批消息的最后一条消息,并发的只能取数量
                            boolean isLastMessage = mBatchCompletedCount.get() == msgList.size();
                            boolean toDOResult = false;
                            //开始处理消息
                            if (mBatchSuccessMsgIds.contains(wrapMessage.getMsgId())) {
                                mBatchRepeatCount.getAndIncrement();
                                toDOResult = true;
                            } else {
                                toDOResult = handlerMessage(realm, wrapMessage, requestId, isOfflineMsg, msgList.size(), isLastMessage);
                            }
                            if (toDOResult) {
                                //临时保存
                                if (!mBatchSuccessMsgIds.contains(wrapMessage.getMsgId())) {
                                    mBatchSuccessMsgIds.add(wrapMessage.getMsgId());
                                }
                            }
                            //处理完成数量自增,需在mBatchSuccessMsgIds add后，因并发，会出现mBatchSuccessMsgIds的size少于mBatchCompletedCount，所以得放在其后
                            mBatchCompletedCount.getAndIncrement();
                        }
                    } catch (Exception e) {
                        LogUtil.writeError(e);
                    } finally {//本批消息处理完成
                        //检测本批的所有消息是否已经接收完成,currentRequestId为空，表示退出了登录
                        if (currentRequestId != null)
                            checkBatchMessageCompleted(realm, requestId, msgList.size(), bean.getMsgFrom());
                        DaoUtil.close(realm);
                    }
                } else {//空消息 回执
//                    LogUtil.writeLog("--发送回执2离线--requestId=" + bean.getRequestId() + "--count=" + bean.getWrapMsgCount());
                    SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(bean.getRequestId(), null, bean.getMsgFrom(), false, SocketData.isEnough(0)), null, bean.getRequestId());
                }

            }
        });
//        executor.execute(() -> {
//
//        });

    }

    /**
     * 检测本批消息是否已经接收完成
     *
     * @param batchTotalCount 本批消息的总数量
     */
    private void checkBatchMessageCompleted(Realm realm, String requestId, int batchTotalCount, int msgFrom) {
        if (mBatchCompletedCount.get() == batchTotalCount) {//全部处理完成
            mBatchCompletedCount.set(0);
            if (mBatchSuccessMsgIds.size() + mBatchRepeatCount.get() >= batchTotalCount) {//全部成功
                //批量保存消息对象
                boolean result = repository.insertOfflineMessages(realm);
                if (currentRequestId != null) {
                    if (result) {
                        //全部保存成功，消息回执
                        MessageManager.getInstance().setReceiveOffline(false);
//                        LogUtil.writeLog("--发送回执2离线--requestId=" + requestId + "--count=" + batchTotalCount);
                        SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(requestId, null, msgFrom, false, SocketData.isEnough(batchTotalCount)), null, requestId);
                        //在线，表示能回执成功，清除掉MsgId
                        if (SocketUtil.getSocketUtil().getOnlineState())
                            mBatchSuccessMsgIds.clear();
                    } else if (!repository.hasValidOfflineMessage()) {
                        //无有效离线消息直接发送回执
                        MessageManager.getInstance().setReceiveOffline(false);
//                        LogUtil.writeLog("--发送回执2离线--requestId=" + requestId + "--count=" + batchTotalCount);
                        SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(requestId, null, msgFrom, false, SocketData.isEnough(batchTotalCount)), null, requestId);
                    }
                    //更新所有的session
                    updateSessionsWhenBatchCompleted(realm);
                    //检测所有离线消息是否接收完成
                    checkReceivedAllOfflineCompleted(realm, batchTotalCount, true);
                }
            } else {//说明至少有一个消息接收失败
                //更新所有的session
                updateSessionsWhenBatchCompleted(realm);
                //检测所有离线消息是否接收完成
                checkReceivedAllOfflineCompleted(realm, batchTotalCount, false);
            }
            mBatchRepeatCount.set(0);
            MessageManager.getInstance().notifyRefreshChat();
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
            //清空双向清除数据
//            if (repository.historyCleanMsg.size() > 0) {
//                repository.historyCleanMsg.clear();
//            }
            //更正离线已读消息-已读状态、未读数量、阅后即焚
            repository.updateOfflineReadMsg(realm);
            //更新离线双向清除
            repository.updateOfflineHistoryClearMsg(realm);
            if (isSuccess && SocketUtil.getSocketUtil().getOnlineState()) {
                //有网，保存完成，且是最后一批离线，清除
                //本次离线消息是否接收完成
                mBatchSuccessMsgIds.clear();
            }
        }
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
                mBatchToUpdateSessionUids.add(isFromSelf ? toUid : fromUid);
            }
        } else {
            mBatchToUpdateSessionGids.add(gid);
        }
    }

    /**
     * 本批消息接收完成后，更新本批消息中相关的session
     *
     * @param realm
     */
    private void updateSessionsWhenBatchCompleted(Realm realm) {
        while (mBatchToUpdateSessionUids.iterator().hasNext()) {
            Long uid = mBatchToUpdateSessionUids.iterator().next();
            mBatchToUpdateSessionUids.remove(uid);
            repository.offlineUpdateSession(realm, null, uid);
        }

        while (mBatchToUpdateSessionGids.iterator().hasNext()) {
            String gid = mBatchToUpdateSessionGids.iterator().next();
            mBatchToUpdateSessionGids.remove(gid);
            repository.offlineUpdateSession(realm, gid, null);
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
