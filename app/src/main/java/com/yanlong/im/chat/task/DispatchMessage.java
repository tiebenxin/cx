package com.yanlong.im.chat.task;

import android.text.TextUtils;

import com.tencent.bugly.crashreport.CrashReport;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.repository.MessageRepository;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.constant.BuglyTag;
import net.cb.cb.library.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

import static com.yanlong.im.utils.socket.SocketData.oldMsgId;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/10 0010
 * @description
 */
public class DispatchMessage {
    private final String TAG = DispatchMessage.class.getSimpleName();
    // Bugly数据保存异常标签
    private final int BUGLY_TAG_SAVE_DATA = 139066;

    private MessageRepository repository = new MessageRepository();

    //记录离线消息保存成功的requestId msg_ids，离线消息是任务并发的
    private Map<String, List<String>> saveOfflineMessageSuccessCount = new HashMap<>();
    /**
     * newFixedThreadPool与cacheThreadPool差不多，也是能reuse就用，但不能随时建新的线程
     * 任意时间点，最多只能有固定数目的活动线程存在，此时如果有新的线程要建立，只能放在另外的队列中等待，直到当前的线程中某个线程终止直接被移出池子
     */
    private ExecutorService offlineMsgExecutor = null;

    public void stopOfflineTask() {
        if (offlineMsgExecutor != null) {
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
        }
        offlineMsgExecutor = null;

    }

    public void dispatchMsg(MsgBean.UniversalMessage bean, Realm realm) {
        boolean result = true;
        try {
            List<MsgBean.UniversalMessage.WrapMessage> msgList = bean.getWrapMsgList();
            if (msgList != null && msgList.size() > 0) {
                for (int i = 0; i < msgList.size(); i++) {
                    MsgBean.UniversalMessage.WrapMessage msg = msgList.get(i);
                    MsgBean.UniversalMessage.WrapMessage wrapMessage = msg;
                    //开始处理消息
                    boolean toDOResult = toDoMsg(realm, wrapMessage, bean.getRequestId(), bean.getMsgFrom() == 1, msgList.size(),
                            i == msgList.size() - 1);
                    //有一个失败，表示接收全部失败
                    if (!toDOResult) result = false;
                }
                //接收完离线消息的处理
                recieveOfflineFinished(realm, bean.getMsgFrom() == 1, msgList.size());
            }

        } catch (Exception e) {
            DaoUtil.reportException(e);
        }
        if (result)
            SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(bean.getRequestId(), null, bean.getMsgFrom(), false, true), null, bean.getRequestId());
    }

    /**
     * 处理离线消息
     *
     * @param bean
     */
    public void dispatchOfflineMsg(MsgBean.UniversalMessage bean) {
        LogUtil.writeLog("dispatchMsg start" + bean.getRequestId());
        try {
            List<MsgBean.UniversalMessage.WrapMessage> msgList = bean.getWrapMsgList();
            if (msgList != null && msgList.size() > 0) {
                saveOfflineMessageSuccessCount.put(bean.getRequestId(), new ArrayList<>());
                int index = 0;
                int page = 20;//每次处理消息的数量，可自己调整，以提高接收离线消息的速度
                int maxIndex = Math.min(msgList.size(), page);
                while (index < msgList.size()) {
                    //开启并发任务
                    startConcurrentTask(msgList, index, maxIndex, bean.getRequestId(), bean.getMsgFrom());
                    if (maxIndex == msgList.size()) {
                        break;
                    } else {
                        index = maxIndex;
                        maxIndex = Math.min(msgList.size(), index + page);
                    }
                }
            } else {//空消息 回执
                SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(bean.getRequestId(), null, bean.getMsgFrom(), false, true), null, bean.getRequestId());
            }
        } catch (Exception e) {
            DaoUtil.reportException(e);
        }
    }

    /**
     * 开启接收离线消息的并发任务
     * 一个任务最多20个消息处理
     *
     * @param msgList
     * @param mIndex
     * @param mMaxIndex
     * @param requestId
     * @param msgFrom
     */
    private void startConcurrentTask(List<MsgBean.UniversalMessage.WrapMessage> msgList, int mIndex, int mMaxIndex,
                                     String requestId, int msgFrom) {
        if (offlineMsgExecutor == null) offlineMsgExecutor = Executors.newFixedThreadPool(3);
        offlineMsgExecutor.execute(() -> {
            Realm realm = DaoUtil.open();
            for (int i = mIndex; i < mMaxIndex; i++) {
                MsgBean.UniversalMessage.WrapMessage wrapMessage = msgList.get(i);
                //是否为本批消息的最后一条消息
                boolean isLastMessage = saveOfflineMessageSuccessCount.get(requestId).size() == msgList.size() - 1;
                //开始处理消息
                boolean toDOResult = toDoMsg(realm, wrapMessage, requestId, msgFrom == 1, msgList.size(),
                        isLastMessage);
                if (toDOResult) {//成功，保存记录
                    saveOfflineMessageSuccessCount.get(requestId).add(wrapMessage.getMsgId());
                    LogUtil.writeLog("dispatchMsg scuss" + saveOfflineMessageSuccessCount.get(requestId).size());
                } else { //有一个失败，表示接收全部失败
                    saveOfflineMessageSuccessCount.remove(requestId);
                }
            }
            if (!saveOfflineMessageSuccessCount.containsKey(requestId) ||
                    saveOfflineMessageSuccessCount.get(requestId).size() == msgList.size()) {
                LogUtil.writeLog("dispatchMsg end" + requestId +
                        ",r=" + saveOfflineMessageSuccessCount.get(requestId).size() + ",m=" + msgList.size());
                //接收完离线消息的处理
                recieveOfflineFinished(realm, msgFrom == 1, msgList.size());
                if (saveOfflineMessageSuccessCount.get(requestId).size() == msgList.size()) {
                    //全部保存成功，消息回执
                    SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(requestId, null, msgFrom, false, true), null, requestId);
                }
                saveOfflineMessageSuccessCount.remove(requestId);
            }
            DaoUtil.close(realm);
        });
    }

    /**
     * @param realm
     * @param wrapMessage
     * @param requestId
     * @param isOfflineMsg  是否是离线消息
     * @param batchMsgCount 本次批量消息数量
     * @param isLastMessage 是否为本批消息的最后一条消息
     * @return
     */
    private boolean toDoMsg(Realm realm, MsgBean.UniversalMessage.WrapMessage wrapMessage, String requestId, boolean isOfflineMsg, int batchMsgCount
            , boolean isLastMessage) {
        boolean result = true;
        boolean isFromSelf = UserAction.getMyId() != null && wrapMessage.getFromUid() == UserAction.getMyId().intValue() && wrapMessage.getFromUid() != wrapMessage.getToUid();
        boolean dealResult = dealWithMsg(wrapMessage, requestId, isOfflineMsg,
                realm);
        //有一个没有保存成功，则整体接收失败
        if (!dealResult) {
            result = false;
            // 上报后的Crash会显示该标签
            CrashReport.setUserSceneTag(MyAppLication.getInstance().getApplicationContext(), BUGLY_TAG_SAVE_DATA);
            // 上传异常数据
            CrashReport.putUserData(MyAppLication.getInstance().getApplicationContext(), BuglyTag.BUGLY_TAG_1,
                    "requestId:" + requestId + ";MsgType:" + wrapMessage.getMsgType());
        }
        MessageManager.getInstance().checkServerTimeInit(wrapMessage);
        //消息震动
        if (!isFromSelf && isLastMessage) {
            MessageManager.getInstance().checkNotifyVoice(wrapMessage, batchMsgCount > 0, true);
        }
        return result;
    }

    /**
     * 接收完离线消息
     *
     * @param realm
     * @param isOfflineMsg  是否是离线消息
     * @param batchMsgCount 本次批量消息数量
     */
    private void recieveOfflineFinished(Realm realm, boolean isOfflineMsg, int batchMsgCount) {
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

    /**
     * 丢弃双向清除消息
     *
     * @param uid
     * @param timestamp
     */
    private boolean discardHistoryCleanMessage(Long uid, Long timestamp) {
        boolean result = false;
        if (repository.historyCleanMsg.containsKey(uid) && repository.historyCleanMsg.get(uid) >= timestamp) {
            if (repository.historyCleanMsg.get(uid) >= timestamp) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 处理接收到的消息
     * 分两类处理，一类是需要产生本地消息记录的，一类是相关指令，无需产生消息记录
     *
     * @param wrapMessage  接收到的消息
     * @param requestId
     * @param isOfflineMsg 是否是离线消息
     * @return
     */
    public boolean dealWithMsg(MsgBean.UniversalMessage.WrapMessage wrapMessage, String requestId, boolean isOfflineMsg
            , Realm realm) {
        if (wrapMessage.getMsgType() == MsgBean.MessageType.UNRECOGNIZED) {
            return true;
        }
        /******丢弃离线消息-执行过双向删除，在指令之前的消息 2020/4/28****************************************/
        if (isOfflineMsg) {
            if (TextUtils.isEmpty(wrapMessage.getGid()) && repository.historyCleanMsg.size() > 0) {//单聊
                if (discardHistoryCleanMessage(wrapMessage.getFromUid(), wrapMessage.getTimestamp()) ||
                        discardHistoryCleanMessage(wrapMessage.getToUid(), wrapMessage.getTimestamp())) {
                    return true;
                }
            }
        }
        LogUtil.getLog().e(TAG, "接收到消息: " + wrapMessage.getMsgId() + "--type=" + wrapMessage.getMsgType());
        if (!TextUtils.isEmpty(wrapMessage.getMsgId())) {
            //有已保存成功的消息，则不再处理
            if (oldMsgId.contains(wrapMessage.getMsgId())) {
                LogUtil.getLog().e(TAG, ">>>>>重复消息: " + wrapMessage.getMsgId());
                return true;
            }
        }
        repository.updateUserAvatarAndNick(wrapMessage, realm);

        boolean isFromSelf = false;
        if (UserAction.getMyId() != null) {
            isFromSelf = wrapMessage.getFromUid() == UserAction.getMyId().intValue() && wrapMessage.getFromUid() != wrapMessage.getToUid();
        }
        boolean result = true;
        switch (wrapMessage.getMsgType()) {
            case CHAT://文本
            case IMAGE://图片
            case STAMP://戳一戳
            case VOICE://语音
            case SHORT_VIDEO://短视频
//            case TRANSFER://转账
            case BUSINESS_CARD://名片
            case RED_ENVELOPER://红包
            case RECEIVE_RED_ENVELOPER://领取红包
            case SNAPSHOT_LOCATION://位置
            case ASSISTANT://小助手消息
            case BALANCE_ASSISTANT://零钱助手消息
            case CHANGE_VICE_ADMINS:// 管理员变更通知
            case SHIPPED_EXPRESSION:// 动画表情
            case TAKE_SCREENSHOT:// 截屏通知
            case SEND_FILE:// 文件消息
            case TRANS_NOTIFY:// 转账提醒通知
                result = repository.toDoChat(wrapMessage, requestId, realm);
                break;
            case HISTORY_CLEAN://双向清除
                repository.toDoHistoryCleanMsg(wrapMessage, isOfflineMsg, realm);
                break;
            case P2P_AU_VIDEO:// 音视频消息
                result = repository.toDoP2PAUVideo(wrapMessage, realm);
                break;
            case ACCEPT_BE_FRIENDS://接受成为好友,需要产生消息后面在处理
                result = repository.toDoAcceptBeFriends(wrapMessage, realm);
                break;
            case REQUEST_FRIEND://请求添加为好友
                repository.toDoRequestFriendMsg(wrapMessage, realm);
                break;
            case REMOVE_FRIEND:
                MessageManager.getInstance().notifyRefreshFriend(true, isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid(), CoreEnum.ERosterAction.REMOVE_FRIEND);
                break;
            case CHANGE_GROUP_MASTER://转让群主
                result = repository.toDoChangeGroupMaster(wrapMessage, realm);
                break;
            case OUT_GROUP://退出群聊，如果该群是已保存群聊，需要改为未保存
                result = repository.toDoOutGroup(wrapMessage, realm);
                break;
            case REMOVE_GROUP_MEMBER://自己被移除群聊，如果该群是已保存群聊，需要改为未保存
                result = repository.toDoRemoveGroupMember(wrapMessage, realm);
                break;
            case REMOVE_GROUP_MEMBER2://其他群成员被移除群聊，可能会有群主退群，涉及群主迭代,所以需要从服务器重新拉取数据
                repository.toDoRemoveGroupMember2(wrapMessage, realm);
                break;
            case ACCEPT_BE_GROUP://接受入群，
                result = repository.toDoAcceptBeGroup(wrapMessage, realm);
                break;
            case REQUEST_GROUP://群主会收到成员进群的请求的通知
                repository.toDoRequestGroup(wrapMessage, realm);
                break;
            case CHANGE_GROUP_META://修改群属性
                result = repository.toDoChangeGroupMeta(wrapMessage, realm);
                break;
            case DESTROY_GROUP://销毁群
                repository.toDoDestroyGroup(wrapMessage, realm);
                break;
            case FORCE_OFFLINE://强制退出，登录冲突
                repository.toDoForceOffline(wrapMessage);
                break;
            case AT://@消息
            case GROUP_ANNOUNCEMENT://群公告
                result = repository.toDoGroupAnnouncement(wrapMessage, realm);
                break;
            case ACTIVE_STAT_CHANGE://在线状态改变
                repository.todoActiveStatChange(wrapMessage, realm);
                break;
            case CANCEL://撤销消息
                result = repository.toDoCancel(wrapMessage, realm);
                break;
            case RESOURCE_LOCK://资源锁定
                repository.toDoResourceLock(wrapMessage, realm);
                break;
            case CHANGE_SURVIVAL_TIME: //阅后即焚
                result = repository.toDoChangeSurvivalTime(wrapMessage, realm);
                break;
            case READ://已读消息
                repository.toDoRead(wrapMessage, isOfflineMsg, realm);
                break;
            case SWITCH_CHANGE: //开关变更
                result = repository.toDoSwitchChange(wrapMessage, realm);
                break;
            case P2P_AU_VIDEO_DIAL:// 音视频通知
                break;
            case PAY_RESULT://支付结果
                repository.toDoPayResult(wrapMessage);
                break;
            case TRANSFER://转账消息
                result = repository.toDoTransfer(wrapMessage, realm);
                break;
            case REPLY_SPECIFIC:// 回复消息
                result = repository.toDoReplySpecific(wrapMessage, realm);
                break;
            case MULTI_TERMINAL_SYNC:// PC端同步 更改信息，只同步自己的操作
                repository.toDoMultiTerminalSync(wrapMessage, realm);
                break;
        }
        //记录已保存成功的消息
        if (result && !TextUtils.isEmpty(wrapMessage.getMsgId())) {
            if (oldMsgId.size() >= 500) {
                oldMsgId.remove(0);
            }
            oldMsgId.add(wrapMessage.getMsgId());
        }
        return result;
    }
}
