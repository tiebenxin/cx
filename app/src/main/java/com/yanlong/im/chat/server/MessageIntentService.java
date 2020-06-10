package com.yanlong.im.chat.server;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

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

import java.util.List;

import io.realm.Realm;

import static com.yanlong.im.utils.socket.SocketData.oldMsgId;

/**
 * 消息处理IntentService 处理完成，会自动stopservice
 *
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/5 0005
 * @description
 */
public class MessageIntentService extends IntentService {

    private final String TAG = MessageIntentService.class.getSimpleName();
    private MessageRepository repository;
    // Bugly数据保存异常标签
    private final int BUGLY_TAG_SAVE_DATA = 139066;

    public MessageIntentService() {
        super("MessageIntentService");
        repository = new MessageRepository();
    }


    @Override
    public void onDestroy() {
        if (repository != null) repository.onDestory();
        MessageManager.getInstance().clear();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("CheckResult")
    protected void onHandleIntent(@Nullable Intent intent) {

        if (MessageManager.getInstance().getToDoMsgCount() == 0) return;
//            //初始化数据库对象
        //子线程
        while (MessageManager.getInstance().getToDoMsgCount() > 0) {
            try {
                MsgBean.UniversalMessage bean = MessageManager.getInstance().poll();
                if (dispatchMsg(bean)) {
                    SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(bean.getRequestId(), null, bean.getMsgFrom(), false, true), null, bean.getRequestId());
                }
//
//                Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
//                    emitter.onNext(dispatchMsg(bean));
//                    emitter.onComplete();
//                })
//                        .subscribeOn(Schedulers.io())
//                        .subscribe(aBoolean -> {
//
//                            //移除处理过的当前消息
//                            MessageManager.getInstance().pop();
//                        });
            } catch (Exception e) {
                LogUtil.writeError(e);
            }
        }

    }

    private boolean dispatchMsg(MsgBean.UniversalMessage bean) {
        Realm realm = DaoUtil.open();
        boolean result = true;
        try {
            List<MsgBean.UniversalMessage.WrapMessage> msgList = bean.getWrapMsgList();
            if (msgList != null) {
                for (MsgBean.UniversalMessage.WrapMessage msg : msgList) {
                    MsgBean.UniversalMessage.WrapMessage wrapMessage = msg;
                    boolean isFromSelf = UserAction.getMyId() != null && wrapMessage.getFromUid() == UserAction.getMyId().intValue() && wrapMessage.getFromUid() != wrapMessage.getToUid();
                    boolean dealResult = dealWithMsg(wrapMessage, bean.getRequestId(), bean.getMsgFrom() == 1,
                            realm);
                    //有一个没有保存成功，则整体接收失败
                    if (!dealResult) {
                        result = false;
                        // 上报后的Crash会显示该标签
                        CrashReport.setUserSceneTag(MyAppLication.getInstance().getApplicationContext(), BUGLY_TAG_SAVE_DATA);
                        // 上传异常数据
                        CrashReport.putUserData(MyAppLication.getInstance().getApplicationContext(), BuglyTag.BUGLY_TAG_1,
                                "requestId:" + bean.getRequestId() + ";MsgType:" + wrapMessage.getMsgType());
                    }
                    MessageManager.getInstance().checkServerTimeInit(wrapMessage);
                    //消息震动
                    if (!isFromSelf) {
                        //本次消息数量大于1条，为批量消息
                        MessageManager.getInstance().checkNotifyVoice(wrapMessage, msgList.size() > 0, true);
                    }
                }

            }
            //本次离线消息是否接收完成
            boolean isReceivedOfflineCompleted = SocketData.isEnough(msgList.size());
            if (bean.getMsgFrom() == 1 && isReceivedOfflineCompleted) {//离线消息接收完了
                //清空双向清除数据
                if (repository.historyCleanMsg.size() > 0) {
                    repository.historyCleanMsg.clear();
                }
                //更正离线已读消息-已读状态、未读数量、阅后即焚
                repository.updateOfflineReadMsg(realm);
            }
        } catch (Exception e) {
            result = false;
            DaoUtil.reportException(e);
        } finally {
            DaoUtil.close(realm);
        }
        return result;
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
        /******end 丢弃消息-执行过双向删除，在指令之前的消息 2020/4/28****************************************/
        LogUtil.getLog().e(TAG, "接收到消息: " + wrapMessage.getMsgId() + "--type=" + wrapMessage.getMsgType());
        if (!TextUtils.isEmpty(wrapMessage.getMsgId())) {
            if (oldMsgId.contains(wrapMessage.getMsgId())) {
                LogUtil.getLog().e(TAG, ">>>>>重复消息: " + wrapMessage.getMsgId());
                return true;
            } else {
                if (oldMsgId.size() >= 500) {
                    oldMsgId.remove(0);
                }
                oldMsgId.add(wrapMessage.getMsgId());
            }
        }
        repository.updateUserAvatarAndNick(wrapMessage,realm);

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
                result = repository.toDoChat(wrapMessage, requestId,realm);
                break;
            case HISTORY_CLEAN://双向清除
                repository.toDoHistoryCleanMsg(wrapMessage, isOfflineMsg,realm);
                break;
            case P2P_AU_VIDEO:// 音视频消息
                result = repository.toDoP2PAUVideo(wrapMessage,realm);
                break;
            case ACCEPT_BE_FRIENDS://接受成为好友,需要产生消息后面在处理
                result = repository.toDoAcceptBeFriends(wrapMessage,realm);
                break;
            case REQUEST_FRIEND://请求添加为好友
                repository.toDoRequestFriendMsg(wrapMessage,realm);
                break;
            case REMOVE_FRIEND:
                MessageManager.getInstance().notifyRefreshFriend(true, isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid(), CoreEnum.ERosterAction.REMOVE_FRIEND);
                break;
            case CHANGE_GROUP_MASTER://转让群主
                result = repository.toDoChangeGroupMaster(wrapMessage,realm);
                break;
            case OUT_GROUP://退出群聊，如果该群是已保存群聊，需要改为未保存
                result = repository.toDoOutGroup(wrapMessage,realm);
                break;
            case REMOVE_GROUP_MEMBER://自己被移除群聊，如果该群是已保存群聊，需要改为未保存
                result = repository.toDoRemoveGroupMember(wrapMessage,realm);
                break;
            case REMOVE_GROUP_MEMBER2://其他群成员被移除群聊，可能会有群主退群，涉及群主迭代,所以需要从服务器重新拉取数据
                repository.toDoRemoveGroupMember2(wrapMessage,realm);
                break;
            case ACCEPT_BE_GROUP://接受入群，
                result = repository.toDoAcceptBeGroup(wrapMessage,realm);
                break;
            case REQUEST_GROUP://群主会收到成员进群的请求的通知
                repository.toDoRequestGroup(wrapMessage,realm);
                break;
            case CHANGE_GROUP_META://修改群属性
                result = repository.toDoChangeGroupMeta(wrapMessage,realm);
                break;
            case DESTROY_GROUP://销毁群
                repository.toDoDestroyGroup(wrapMessage,realm);
                break;
            case FORCE_OFFLINE://强制退出，登录冲突
                repository.toDoForceOffline(wrapMessage);
                break;
            case AT://@消息
            case GROUP_ANNOUNCEMENT://群公告
                result = repository.toDoGroupAnnouncement(wrapMessage,realm);
                break;
            case ACTIVE_STAT_CHANGE://在线状态改变
                repository.todoActiveStatChange(wrapMessage,realm);
                break;
            case CANCEL://撤销消息
                result = repository.toDoCancel(wrapMessage,realm);
                break;
            case RESOURCE_LOCK://资源锁定
                repository.toDoResourceLock(wrapMessage,realm);
                break;
            case CHANGE_SURVIVAL_TIME: //阅后即焚
                result = repository.toDoChangeSurvivalTime(wrapMessage,realm);
                break;
            case READ://已读消息
                repository.toDoRead(wrapMessage, isOfflineMsg,realm);
                break;
            case SWITCH_CHANGE: //开关变更
                result = repository.toDoSwitchChange(wrapMessage,realm);
                break;
            case P2P_AU_VIDEO_DIAL:// 音视频通知
                break;
            case PAY_RESULT://支付结果
                repository.toDoPayResult(wrapMessage);
                break;
            case TRANSFER://转账消息
                result = repository.toDoTransfer(wrapMessage,realm);
                break;
            case REPLY_SPECIFIC:// 回复消息
                result = repository.toDoReplySpecific(wrapMessage,realm);
                break;
            case MULTI_TERMINAL_SYNC:// PC端同步 更改信息，只同步自己的操作
                repository.toDoMultiTerminalSync(wrapMessage,realm);
                break;
        }
        return result;
    }

}
