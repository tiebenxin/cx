package com.yanlong.im.chat.task;

import com.tencent.bugly.crashreport.CrashReport;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.repository.MessageRepository;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.utils.socket.MsgBean;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.constant.BuglyTag;
import net.cb.cb.library.utils.LogUtil;

import io.realm.Realm;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/10 0010
 * @description
 */
public abstract class DispatchMessage {
    protected final String TAG = DispatchMessage.class.getSimpleName();
    // Bugly数据保存异常标签
    protected final int BUGLY_TAG_SAVE_DATA = 139066;

    protected MessageRepository repository;

    public DispatchMessage(boolean isOffline) {
        repository = new MessageRepository(isOffline);
    }

    public abstract void clear();

    /**
     * 消息分发处理
     *
     * @param bean
     * @param realm
     */
    public abstract void dispatch(MsgBean.UniversalMessage bean, Realm realm);

    /**
     * 过滤消息
     *
     * @param wrapMessage
     */
    public abstract boolean filter(MsgBean.UniversalMessage.WrapMessage wrapMessage);


    /**
     * 消息处理
     *
     * @param realm
     * @param wrapMessage
     * @param requestId
     * @param isOfflineMsg  是否是离线消息
     * @param batchMsgCount 本次批量消息数量
     * @param isLastMessage 是否为本批消息的最后一条消息
     * @return
     */
    public synchronized boolean handlerMessage(Realm realm, MsgBean.UniversalMessage.WrapMessage wrapMessage, String requestId, boolean isOfflineMsg, int batchMsgCount
            , boolean isLastMessage) {
        boolean result = true;
        boolean isFromSelf = UserAction.getMyId() != null && wrapMessage.getFromUid() == UserAction.getMyId().intValue() && wrapMessage.getFromUid() != wrapMessage.getToUid();
        /***开始保存处理消息 保存到数据库*********************************************/
        boolean saveDBResult = dealWithMsg(wrapMessage, requestId, isOfflineMsg, realm);
        /***saveDBResult 保存结果*********************************************/
        if (!saveDBResult) {//有一个没有保存成功，则整体接收失败
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
     * 处理接收到的消息
     * 分两类处理，一类是需要产生本地消息记录的，一类是相关指令，无需产生消息记录
     *
     * @param wrapMessage  接收到的消息
     * @param requestId
     * @param isOfflineMsg 是否是离线消息
     * @return
     */
    public synchronized boolean dealWithMsg(MsgBean.UniversalMessage.WrapMessage wrapMessage, String requestId, boolean isOfflineMsg
            , Realm realm) {
        //过滤消息
        if (filter(wrapMessage)) {
            return true;
        }
        if (wrapMessage.getMsgType() != MsgBean.MessageType.ACTIVE_STAT_CHANGE) {
            LogUtil.getLog().e(TAG, "消息LOG: " + wrapMessage.getMsgId() + "--type=" + wrapMessage.getMsgType());
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
            case ASSISTANT_PROMOTION:// 小助手推广消息
                result = repository.handlerDoChat(wrapMessage, requestId, realm);
                break;
            case HISTORY_CLEAN://双向清除
                repository.handlerHistoryCleanMsg(wrapMessage, isOfflineMsg, realm);
                break;
            case P2P_AU_VIDEO:// 音视频消息
                result = repository.handlerP2PAUVideo(wrapMessage, realm);
                break;
            case ACCEPT_BE_FRIENDS://接受成为好友,需要产生消息后面在处理
                result = repository.handlerAcceptBeFriends(wrapMessage, realm);
                break;
            case REQUEST_FRIEND://请求添加为好友
                repository.handlerRequestFriendMsg(wrapMessage, realm);
                break;
            case REMOVE_FRIEND:
                MessageManager.getInstance().notifyRefreshFriend(true, isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid(), CoreEnum.ERosterAction.REMOVE_FRIEND);
                break;
            case CHANGE_GROUP_MASTER://转让群主
                result = repository.handlerChangeGroupMaster(wrapMessage, realm);
                break;
            case OUT_GROUP://退出群聊，如果该群是已保存群聊，需要改为未保存
                result = repository.handlerOutGroup(wrapMessage, realm);
                break;
            case REMOVE_GROUP_MEMBER://自己被移除群聊，如果该群是已保存群聊，需要改为未保存
                result = repository.handlerRemoveGroupMember(wrapMessage, realm);
                break;
            case REMOVE_GROUP_MEMBER2://其他群成员被移除群聊，可能会有群主退群，涉及群主迭代,所以需要从服务器重新拉取数据
                result = repository.handlerRemoveGroupMember2(wrapMessage, realm);
                break;
            case ACCEPT_BE_GROUP://接受入群，
                result = repository.handlerAcceptBeGroup(wrapMessage, realm);
                break;
            case REQUEST_GROUP://群主会收到成员进群的请求的通知
                repository.handlerRequestGroup(wrapMessage, realm);
                break;
            case CHANGE_GROUP_META://修改群属性
                result = repository.handlerChangeGroupMeta(wrapMessage, realm);
                break;
            case DESTROY_GROUP://销毁群
                repository.handlerDestroyGroup(wrapMessage, realm);
                break;
            case FORCE_OFFLINE://强制退出，登录冲突
                repository.toDoForceOffline(wrapMessage);
                break;
            case AT://@消息
            case GROUP_ANNOUNCEMENT://群公告
                result = repository.handlerGroupAnnouncement(wrapMessage, realm);
                break;
            case ACTIVE_STAT_CHANGE://在线状态改变
                repository.handlerActiveStatChange(wrapMessage, realm);
                break;
            case CANCEL://撤销消息
                result = repository.handlerCancel(wrapMessage, realm);
                break;
            case RESOURCE_LOCK://资源锁定
                repository.handlerResourceLock(wrapMessage, realm);
                break;
            case CHANGE_SURVIVAL_TIME: //阅后即焚
                result = repository.toDoChangeSurvivalTime(wrapMessage, realm);
                break;
            case READ://已读消息
                repository.handlerRead(wrapMessage, isOfflineMsg, realm);
                break;
            case SWITCH_CHANGE: //开关变更
                result = repository.handlerSwitchChange(wrapMessage, realm);
                break;
            case P2P_AU_VIDEO_DIAL:// 音视频通知
                break;
            case PAY_RESULT://支付结果
                repository.handlerPayResult(wrapMessage);
                break;
            case TRANSFER://转账消息
                result = repository.handlerTransfer(wrapMessage, realm);
                break;
            case REPLY_SPECIFIC:// 回复消息
                result = repository.handlerReplySpecific(wrapMessage, realm);
                break;
            case MULTI_TERMINAL_SYNC:// PC端同步 更改信息，只同步自己的操作
                repository.handlerMultiTerminalSync(wrapMessage, realm);
                break;
        }

        return result;
    }


}
