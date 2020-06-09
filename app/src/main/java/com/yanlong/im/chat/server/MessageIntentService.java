package com.yanlong.im.chat.server;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.repository.MessageRepository;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.utils.LogUtil;

import java.util.List;

import io.realm.Realm;

import static com.yanlong.im.utils.socket.SocketData.oldMsgId;

/**
 * 消息处理sevice
 *
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/5 0005
 * @description
 */
public class MessageIntentService extends IntentService {
    private final String TAG = MessageIntentService.class.getSimpleName();
    public static String START_SERVICE_ACTION = "start_service_action";
    public static String STOP_SERVICE_ACTION = "stop_service_action";
    private MessageRepository repository;

    public MessageIntentService() {
        super("MessageIntentService");
        repository = new MessageRepository();
    }

    @Override
    public void onDestroy() {
        if (repository != null) repository.onDestory();
        MessageManager.getInstance().clear();
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Realm realm = DaoUtil.open();
        if (intent.getAction().equals(STOP_SERVICE_ACTION)) {
            /**停止服务，关闭数据库，onHandleIntent每次都是同一个线程
             * 适用场景
             *1.退出登录/挤下线
             * 2.application终止
             */
            DaoUtil.close(realm);
            stopSelf();
        } else {
            //初始化数据库对象
            repository.initRealm(realm);
            MsgBean.UniversalMessage bean = MessageManager.getInstance().poll();
            //子线程
            while (bean != null) {
                try {
                    List<MsgBean.UniversalMessage.WrapMessage> msgList = bean.getWrapMsgList();
                    if (msgList != null) {
                        for (MsgBean.UniversalMessage.WrapMessage msg : msgList) {
                            MsgBean.UniversalMessage.WrapMessage wrapMessage = msg;
                            dealWithMsg(wrapMessage, bean.getRequestId(), msgList.size());
                            MessageManager.getInstance().checkServerTimeInit(wrapMessage);
                        }
                    }
                    //本次消息是否接收完成
                    boolean isReceivedOfflineCompleted = SocketData.isEnough(msgList.size());
                    if (isReceivedOfflineCompleted) {//离线消息接收完了
                        //清空双向清除数据
                        if (repository.historyCleanMsg.size() > 0) {
                            repository.historyCleanMsg.clear();
                        }
                        //更正离线已读消息-已读状态、未读数量、阅后即焚
                        repository.updateOfflineReadMsg();
                    }
                    //消息回执
                    SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(bean.getRequestId(), null, bean.getMsgFrom(), false, true), null, bean.getRequestId());
                } catch (Exception e) {}
                //移除处理过的当前消息
                MessageManager.getInstance().pop();
                //取下一个待处理的消息对象
                bean = MessageManager.getInstance().poll();
            }
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


    /*
     * 处理接收到的消息
     * 分两类处理，一类是需要产生本地消息记录的，一类是相关指令，无需产生消息记录
     * @param wrapMessage 接收到的消息
     * @param isList 是否是批量消息
     * @return 返回结果，不需要处理逻辑的消息，默认处理成功
     * */

    /**
     * 处理接收到的消息
     * 分两类处理，一类是需要产生本地消息记录的，一类是相关指令，无需产生消息记录
     *
     * @param wrapMessage       接收到的消息
     * @param requestId
     * @param batchMessageTotal 本批消息数量
     * @return
     */
    public void dealWithMsg(MsgBean.UniversalMessage.WrapMessage wrapMessage, String requestId,
                            int batchMessageTotal) {
        if (wrapMessage.getMsgType() == MsgBean.MessageType.UNRECOGNIZED) {
            return;
        }
        //本次离线消息是否接收完成
        boolean isReceivedOfflineCompleted = SocketData.isEnough(batchMessageTotal);
        /******丢弃消息-执行过双向删除，在指令之前的消息 2020/4/28****************************************/
        if (TextUtils.isEmpty(wrapMessage.getGid()) && repository.historyCleanMsg.size() > 0) {//单聊
            if (discardHistoryCleanMessage(wrapMessage.getFromUid(), wrapMessage.getTimestamp()) ||
                    discardHistoryCleanMessage(wrapMessage.getToUid(), wrapMessage.getTimestamp())) {
                return;
            }
        }

        /******end 丢弃消息-执行过双向删除，在指令之前的消息 2020/4/28****************************************/
        LogUtil.getLog().e(TAG, "接收到消息: " + wrapMessage.getMsgId() + "--type=" + wrapMessage.getMsgType());
        if (!TextUtils.isEmpty(wrapMessage.getMsgId())) {
            if (oldMsgId.contains(wrapMessage.getMsgId())) {
                LogUtil.getLog().e(TAG, ">>>>>重复消息: " + wrapMessage.getMsgId());
                return;
            } else {
                if (oldMsgId.size() >= 500) {
                    oldMsgId.remove(0);
                }
                oldMsgId.add(wrapMessage.getMsgId());
            }
        }
        repository.updateUserAvatarAndNick(wrapMessage, requestId);

        boolean isFromSelf = false;
        if (UserAction.getMyId() != null) {
            isFromSelf = wrapMessage.getFromUid() == UserAction.getMyId().intValue() && wrapMessage.getFromUid() != wrapMessage.getToUid();
        }
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
                repository.toDoChat(wrapMessage, requestId);
                break;
            case HISTORY_CLEAN://双向清除
                repository.toDoHistoryCleanMsg(wrapMessage, isReceivedOfflineCompleted);
                break;
            case P2P_AU_VIDEO:// 音视频消息
                repository.toDoP2PAUVideo(wrapMessage);
                break;
            case ACCEPT_BE_FRIENDS://接受成为好友,需要产生消息后面在处理
                repository.toDoAcceptBeFriends(wrapMessage);
                break;
            case REQUEST_FRIEND://请求添加为好友
                repository.toDoRequestFriendMsg(wrapMessage);
                break;
            case REMOVE_FRIEND:
                MessageManager.getInstance().notifyRefreshFriend(true, isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid(), CoreEnum.ERosterAction.REMOVE_FRIEND);
                break;
            case CHANGE_GROUP_MASTER://转让群主
                repository.toDoChangeGroupMaster(wrapMessage);
                break;
            case OUT_GROUP://退出群聊，如果该群是已保存群聊，需要改为未保存
                repository.toDoOutGroup(wrapMessage);
                break;
            case REMOVE_GROUP_MEMBER://自己被移除群聊，如果该群是已保存群聊，需要改为未保存
                repository.toDoRemoveGroupMember(wrapMessage);
                break;
            case REMOVE_GROUP_MEMBER2://其他群成员被移除群聊，可能会有群主退群，涉及群主迭代,所以需要从服务器重新拉取数据
                repository.toDoRemoveGroupMember2(wrapMessage);
                break;
            case ACCEPT_BE_GROUP://接受入群，
                repository.toDoAcceptBeGroup(wrapMessage);
                break;
            case REQUEST_GROUP://群主会收到成员进群的请求的通知
                repository.toDoRequestGroup(wrapMessage);
                break;
            case CHANGE_GROUP_META://修改群属性
                repository.toDoChangeGroupMeta(wrapMessage);
                break;
            case DESTROY_GROUP://销毁群
                repository.toDoDestroyGroup(wrapMessage);
                break;
            case FORCE_OFFLINE://强制退出，登录冲突
                repository.toDoForceOffline(wrapMessage);
                break;
            case AT://@消息
            case GROUP_ANNOUNCEMENT://群公告
                repository.toDoGroupAnnouncement(wrapMessage);
                break;
            case ACTIVE_STAT_CHANGE://在线状态改变
                repository.todoActiveStatChange(wrapMessage);
                break;
            case CANCEL://撤销消息
                repository.toDoCancel(wrapMessage);
                break;
            case RESOURCE_LOCK://资源锁定
                repository.toDoResourceLock(wrapMessage);
                break;
            case CHANGE_SURVIVAL_TIME: //阅后即焚
                repository.toDoChangeSurvivalTime(wrapMessage);
                break;
            case READ://已读消息
                repository.toDoRead(wrapMessage, batchMessageTotal>1);
                break;
            case SWITCH_CHANGE: //开关变更
                repository.toDoSwitchChange(wrapMessage);
                break;
            case P2P_AU_VIDEO_DIAL:// 音视频通知
                break;
            case PAY_RESULT://支付结果
                repository.toDoPayResult(wrapMessage);
                break;
            case TRANSFER://转账消息
                repository.toDoTransfer(wrapMessage);
                break;
            case REPLY_SPECIFIC:// 回复消息
                repository.toDoReplySpecific(wrapMessage);
                break;
            case MULTI_TERMINAL_SYNC:// PC端同步 更改信息，只同步自己的操作
                repository.toDoMultiTerminalSync(wrapMessage);
                break;
        }

        if (!isFromSelf) {
            //本次消息数量大于1条，为批量消息
            MessageManager.getInstance().checkNotifyVoice(wrapMessage, batchMessageTotal > 1, true);
        }
    }

}
