package com.yanlong.im.repository;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.example.nim_lib.controll.AVChatProfile;
import com.google.common.base.Function;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.ApplyBean;
import com.yanlong.im.chat.bean.AtMessage;
import com.yanlong.im.chat.bean.ChatMessage;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.chat.bean.ReadDestroyBean;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.data.local.MessageLocalDataSource;
import com.yanlong.im.data.remote.MessageRemoteDataSource;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.EventIsShowRead;
import net.cb.cb.library.bean.EventLoginOut4Conflict;
import net.cb.cb.library.bean.EventRefreshFriend;
import net.cb.cb.library.bean.RefreshApplyEvent;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.manager.Constants;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.TimeToString;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.Realm;
import io.realm.Sort;

import static com.yanlong.im.utils.socket.MsgBean.MessageType.ACCEPT_BE_FRIENDS;
import static com.yanlong.im.utils.socket.MsgBean.MessageType.ACTIVE_STAT_CHANGE;
import static com.yanlong.im.utils.socket.MsgBean.MessageType.REMOVE_FRIEND;
import static com.yanlong.im.utils.socket.MsgBean.MessageType.REQUEST_FRIEND;
import static com.yanlong.im.utils.socket.MsgBean.MessageType.REQUEST_GROUP;
import static com.yanlong.im.utils.socket.SocketData.createMsgBean;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/5 0005
 * @description
 */
public class MessageRepository {
    private final String TAG = MessageRepository.class.getSimpleName();
    public MessageLocalDataSource localDataSource;
    private MessageRemoteDataSource remoteDataSource;

    /**
     * 保存接收了双向清除指令的from_uid-待最后一条消息时间戳
     * 用于丢弃在此时间戳之前的消息
     */
    public Map<Long, Long> historyCleanMsg = new ConcurrentHashMap<>();

    /**
     * 处理群聊 接收离线消息 自己PC端发送的已读消息
     * 保存已读消息 gid-消息时间戳
     * 用于处理自己已读和阅后即焚消息状态
     */
    public Map<String, Long> offlineMySelfPCGroupReadMsg = new ConcurrentHashMap<>();
    /**
     * 处理单聊 接收离线消息 自己PC端发送的已读消息
     * 保存已读消息 from_uid-消息时间戳
     * 用于处理自己已读和阅后即焚消息状态
     */
    public Map<Long, Long> offlineMySelfPCFriendReadMsg = new ConcurrentHashMap<>();
    /**
     * 处理单聊 接收离线消息 对方发送的已读消息
     * 保存已读消息 to_uid-消息时间戳
     * 用于自己发送的消息对方已读 和阅后即焚消息状态
     */
    public Map<Long, Long> offlineFriendReadMsg = new ConcurrentHashMap<>();
    private boolean isOffline;
    //    private Map<String, MsgAllBean> offlineMsgAllBean = null;
    private List<MsgAllBean> offlineMsgAllBean = null;
    private List<String> offlineMsgIds = null;


    public MessageRepository(boolean isOffline) {
        localDataSource = new MessageLocalDataSource();
        remoteDataSource = new MessageRemoteDataSource();
        this.isOffline = isOffline;

    }

    public void clear() {
        remoteDataSource.clear();
        if (offlineMsgAllBean != null) {
            offlineMsgAllBean.clear();
            offlineMsgAllBean = null;
        }
        if (offlineMsgIds != null) {
            offlineMsgIds.clear();
            offlineMsgIds = null;
        }
    }


    /**
     * 群主会收到成员进群的请求的通知
     *
     * @param wrapMessage
     */
    public void handlerRequestGroup(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        //自己邀请的，不需要显示
        if (UserAction.getMyId() != null && wrapMessage.getRequestGroup().getInviter() > 0 && wrapMessage.getRequestGroup().getInviter() == UserAction.getMyId().longValue()) {
            return;
        }
        for (MsgBean.GroupNoticeMessage ntm : wrapMessage.getRequestGroup().getNoticeMessageList()) {
            ApplyBean applyBean = new ApplyBean();
            applyBean.setAid(wrapMessage.getGid() + ntm.getUid());
            applyBean.setChatType(CoreEnum.EChatType.GROUP);
            applyBean.setGid(wrapMessage.getGid());
            applyBean.setGroupName(localDataSource.getGroupName(realm, wrapMessage.getGid()));
            applyBean.setJoinType(wrapMessage.getRequestGroup().getJoinType().getNumber());
            applyBean.setInviter(wrapMessage.getRequestGroup().getInviter());
            applyBean.setInviterName(wrapMessage.getRequestGroup().getInviterName());
            applyBean.setUid(ntm.getUid());
            applyBean.setNickname(ntm.getNickname());
            applyBean.setAvatar(ntm.getAvatar());
            applyBean.setStat(1);
            localDataSource.saveApplyBean(realm, applyBean);
        }
        localDataSource.addRemindCount(realm, "friend_apply");
        MessageManager.getInstance().notifyRefreshFriend(true, -1L, CoreEnum.ERosterAction.DEFAULT);//刷新首页 通讯录底部小红点
    }


    /**
     * 处理双向清除消息
     *
     * @param wrapMessage
     * @param isOfflineMsg 是否是离线消息
     */
    public void handlerHistoryCleanMsg(MsgBean.UniversalMessage.WrapMessage wrapMessage, boolean isOfflineMsg, Realm realm) {
        boolean isFromSelf = UserAction.getMyId() != null && wrapMessage.getFromUid() == UserAction.getMyId().intValue() && wrapMessage.getFromUid() != wrapMessage.getToUid();
        //最后一条需要清除的聊天记录时间戳
        long lastNeedCleanTimestamp = wrapMessage.getTimestamp();
        //接收离线消息时，保存双向清除指令发送方和时间戳，离线消息接收完成前，丢弃在此时间戳之前的消息
        if (isOfflineMsg) {
            historyCleanMsg.put(isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid(), lastNeedCleanTimestamp);
        }
        //清除好友历史记录
        localDataSource.messageHistoryClean(realm, isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid(), lastNeedCleanTimestamp);
        //通知UI刷新
        MessageManager.getInstance().notifyRefreshChat(wrapMessage.getGid(), isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid());
    }

    /**
     * 处理申请加好友消息
     *
     * @param wrapMessage
     */
    public void handlerRequestFriendMsg(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        boolean isFromSelf = UserAction.getMyId() != null && wrapMessage.getFromUid() == UserAction.getMyId().intValue() && wrapMessage.getFromUid() != wrapMessage.getToUid();
        //增加好友申请红点数
        localDataSource.addRemindCount(realm, "friend_apply");
        remoteDataSource.getRequestFriends(wrapMessage.getRequestFriend().getContactName(), applyBean -> {
            //网络请求后，都在主线程回调，不需要关闭Realm,Applicaiton中会自动关闭
            Realm realm1 = DaoUtil.open();
            localDataSource.saveApplyBean(realm1, applyBean);
            return true;
        });
        //通知UI刷新
        MessageManager.getInstance().notifyRefreshFriend(true, isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid(), CoreEnum.ERosterAction.REQUEST_FRIEND);
    }

    /**
     * 销毁群消息
     *
     * @param wrapMessage
     */
    public void handlerDestroyGroup(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        String groupName = wrapMessage.getDestroyGroup().getName();
        String icon = wrapMessage.getDestroyGroup().getAvatar();
        localDataSource.groupExit(realm, wrapMessage.getGid(), groupName, icon, 1);
    }

    /*
     * 修正本地时间与服务器时间差值，暂时没考虑时区问题
     * */
    private void fetchTimeDiff(long timestamp) {
        long current = System.currentTimeMillis();//本地系统当前时间
        TimeToString.DIFF_TIME = timestamp - current;
    }

    /**
     * 在线状态改变
     *
     * @param wrapMessage
     */
    public void handlerActiveStatChange(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        long fromUid = wrapMessage.getFromUid();
        MsgBean.ActiveStatChangeMessage message = wrapMessage.getActiveStatChange();
        if (message != null) {
            LogUtil.getLog().d(TAG, ">>>在线状态改变---uid=" + wrapMessage.getFromUid() + "--onlineType=" + message.getActiveTypeValue());
            fetchTimeDiff(message.getTimestamp());
            if (message.getActiveTypeValue() == 1) {
                SocketData.setPreServerAckTime(message.getTimestamp());
            }
            //更新数据库
            localDataSource.updateUserOnlineStatus(realm, fromUid, message.getActiveTypeValue(), message.getTimestamp());
        }
    }

    /**
     * 资源锁定
     *
     * @param wrapMessage
     */
    public void handlerResourceLock(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        MsgBean.ResourceLockMessage lock = wrapMessage.getResourceLock();
        if (lock != null) {
            MsgBean.ResourceLockMessage.ResourceLockType type = lock.getResourceLockType();
            switch (type) {
                case CLOUDREDENVELOPE:
                    UserBean info = (UserBean) UserAction.getMyInfo();
                    if (info != null) {
                        info.setLockCloudRedEnvelope(lock.getLock());
                        localDataSource.updateUserBean(realm, info);
                    }
                    break;
            }
        }
    }

    /**
     * 处理消息已读
     *
     * @param wrapMessage
     * @param isOfflineMsg 是否为离线消息
     */
    public void handlerRead(MsgBean.UniversalMessage.WrapMessage wrapMessage, boolean isOfflineMsg, Realm realm) {
        boolean isFromSelf = UserAction.getMyId() != null && wrapMessage.getFromUid() == UserAction.getMyId().intValue() && wrapMessage.getFromUid() != wrapMessage.getToUid();
        long uids = isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid();
        if (!isFromSelf) {
            if (TextUtils.isEmpty(wrapMessage.getGid())) {//单聊
                //自己PC端发送给好友的消息，有离线消息，则保存先,离线消息处理完之后，进行再次更新
                if (isOfflineMsg) {//离线消息
                    //对方已读我发的消息
                    offlineFriendReadMsg.put(uids, wrapMessage.getTimestamp());
                } else {
                    localDataSource.updateFriendMsgReadAndSurvivalTime(realm, uids, wrapMessage.getTimestamp());
                }
            }
        }
        LogUtil.getLog().d(TAG, "已读消息:" + wrapMessage.getTimestamp());
        if (isFromSelf) {//自己PC端已读，则清除未读消息
            String gid = wrapMessage.getGid();
            gid = gid == null ? "" : gid;
            //有离线消息(批量消息)，则保存先,离线消息处理完之后，进行再次更新
            if (isOfflineMsg) {
                //保存消息信息
                if (TextUtils.isEmpty(gid))
                    offlineMySelfPCFriendReadMsg.put(uids, wrapMessage.getTimestamp());
                else
                    offlineMySelfPCGroupReadMsg.put(gid, wrapMessage.getTimestamp());
            } else { //同步自己PC端好友发送消息的已读状态和阅后即焚
                localDataSource.updateRecivedMsgReadForPC(realm, gid, uids, wrapMessage.getTimestamp());
            }
        }
        MessageManager.getInstance().notifyRefreshChat(wrapMessage.getGid(), uids);
    }

    /**
     * 支付结果
     *
     * @param wrapMessage
     */
    public void handlerPayResult(MsgBean.UniversalMessage.WrapMessage wrapMessage) {
        MsgBean.PayResultMessage payResult = wrapMessage.getPayResult();
        System.out.println(TAG + "--支付结果=" + payResult.getResult());
        //通知UI更新
        MessageManager.getInstance().notifyPayResult(payResult);
    }

    /**
     * 多端同步
     *
     * @param wrapMessage
     */
    public void handlerMultiTerminalSync(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        switch (wrapMessage.getMultiTerminalSync().getSyncType()) {
            case MY_SELF_CHANGED://自己的个人信息变更
                remoteDataSource.getMyInfo(UserAction.getMyId(), null, new Function<UserBean, Boolean>() {
                    @NullableDecl
                    @Override
                    public Boolean apply(@NullableDecl UserBean user) {
                        //网络请求后，都在主线程回调，不需要关闭Realm,Applicaiton中会自动关闭
                        Realm realm1 = DaoUtil.open();
                        //uType=自己
                        user.setuType(1);
                        //更新数据库信息
                        localDataSource.updateUserBean(realm1, user);
                        //更新内存对象信息
                        UserAction.refreshMyInfo();
                        //通知UI更新用户信息
                        MessageManager.getInstance().notifyRefreshUser();
                        return true;
                    }
                });
                break;
            case MY_FRIEND_CHANGED://更改我的好友信息（备注名等）
                remoteDataSource.getFriend(wrapMessage.getMultiTerminalSync().getUid(), new Function<UserInfo, Boolean>() {
                    @NullableDecl
                    @Override
                    public Boolean apply(@NullableDecl UserInfo user) {
                        //网络请求后，都在主线程回调，不需要关闭Realm,Applicaiton中会自动关闭
                        Realm realm1 = DaoUtil.open();
                        localDataSource.updateUserInfo(realm1, user);
                        localDataSource.updateSessionTopAndDisturb(realm1, null, user.getUid(), user.getIstop(), user.getDisturb());
                        /********通知更新sessionDetail************************************/
                        List<Long> fUids = new ArrayList<>();
                        fUids.add(wrapMessage.getMultiTerminalSync().getUid());
                        //回主线程调用更新session详情
                        if (MyAppLication.INSTANCE().repository != null)
                            MyAppLication.INSTANCE().repository.updateSessionDetail(null, fUids);
                        /********通知更新sessionDetail end************************************/
                        MessageManager.getInstance().notifyRefreshFriend(true, user.getUid(), CoreEnum.ERosterAction.UPDATE_INFO);
                        return true;
                    }
                });
                break;
            case MY_GROUP_CHANGED://更改我所在的群信息变更（备注名等）
                String gid = wrapMessage.getMultiTerminalSync().getGid();
                //请求获取群信息
                requestGroupInfo(gid);
                break;
            case MY_GROUP_QUIT://自己退群-TODO 验证  -业务更改为删除整个群对象
                gid = wrapMessage.getMultiTerminalSync().getGid();
                //自己退群，删除群 和 会话等信息
                localDataSource.deleteGroup(realm, gid);
                EventBus.getDefault().post(new EventExitChat(gid, null));
                break;
            case MY_FRIEND_DELETED://删除好友
                long uid = wrapMessage.getMultiTerminalSync().getUid();
                localDataSource.deleteFriend(realm, uid);
                MessageManager.getInstance().setMessageChange(true);
                EventRefreshFriend eventRefreshFriend = new EventRefreshFriend();
                eventRefreshFriend.setLocal(true);
                eventRefreshFriend.setUid(uid);
                eventRefreshFriend.setRosterAction(CoreEnum.ERosterAction.REMOVE_FRIEND);
                EventBus.getDefault().post(eventRefreshFriend);
                EventBus.getDefault().post(new EventExitChat(null, uid));
                break;
        }
    }

    private void requestGroupInfo(String gid) {
        remoteDataSource.getGroupInfo(gid, new Function<Group, Boolean>() {
            @NullableDecl
            @Override
            public Boolean apply(@NullableDecl Group group) {
                //网络请求后，都在主线程回调，不需要关闭Realm,Applicaiton中会自动关闭
                Realm realm1 = DaoUtil.open();
                saveGoupToDB(group, realm1);
                //通知更新UI
                MessageManager.getInstance().notifyGroupChange(gid);
                /********通知更新sessionDetail************************************/
                List<String> gids = new ArrayList<>();
                if (!TextUtils.isEmpty(gid)) {
                    gids.add(gid);
                }
                //回主线程调用更新session详情
                if (MyAppLication.INSTANCE().repository != null)
                    MyAppLication.INSTANCE().repository.updateSessionDetail(gids, null);
                /********通知更新sessionDetail end************************************/
                return true;
            }
        });
    }

    /**
     * 更新、保存群聊
     *
     * @param group
     */
    private void saveGoupToDB(@NonNull Group group, Realm realm) {
        if (localDataSource.isMemberInGroup(group)) {//在群中，更新群信息
            localDataSource.updateGroup(realm, group);
        } else {//不在群中，不更新了，直接把自己移除
            localDataSource.removeGroupMember(realm, group.getGid(), UserAction.getMyId());
        }
        //更新session免打扰和置顶状态
        localDataSource.updateSessionTopAndDisturb(realm, group.getGid(), null, group.getIsTop(), group.getNotNotify());
    }

    /**
     * 回复消息
     *
     * @param wrapMessage
     */
    public boolean handlerReplySpecific(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
        boolean result = true;
        if (bean != null) {
            result = saveMessageNew(bean, realm);
            if (!TextUtils.isEmpty(bean.getGid()) && bean.getReplyMessage() != null && bean.getReplyMessage().getAtMessage() != null) {
                AtMessage atMessage = bean.getReplyMessage().getAtMessage();
                String gid = wrapMessage.getGid();
                dealAtMessage(gid, atMessage.getAt_type(), atMessage.getMsg(), atMessage.getUid(), realm);
            }
        }
        return result;
    }

    private boolean dealAtMessage(String gid, int atType, String message, List<Long> list, Realm realm) {
        boolean isAt = false;
        if (atType == 0) {
            if (list != null) {
                Long uid = UserAction.getMyId();
                for (int i = 0; i < list.size(); i++) {
                    if (uid.equals(list.get(i))) {
                        LogUtil.getLog().e(TAG, "有人@我" + uid);
                        if (!MessageManager.getInstance().isMsgFromCurrentChat(gid, null)) {
                            localDataSource.updateSessionAtMessage(realm, gid, message, atType);
                            MessageManager.getInstance().playDingDong();
                        }
                        isAt = true;
                    }
                }
            }
        } else {
            if (list == null || list.size() == 0) {//是群公告
                requestGroupInfo(gid);
            }
            LogUtil.getLog().e(TAG, "@所有人");
            if (!MessageManager.getInstance().isMsgFromCurrentChat(gid, null)) {
                localDataSource.updateSessionAtMessage(realm, gid, message, atType);
                MessageManager.getInstance().playDingDong();
            }
            isAt = true;
        }
        return isAt;
    }

    /**
     * 转账消息
     *
     * @param wrapMessage
     */
    public boolean handlerTransfer(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
        boolean result = true;
        if (bean != null) {
            MsgBean.TransferMessage transferMessage = wrapMessage.getTransfer();
            if (transferMessage != null) {
                //领取或退还转账,先更新历史转账消息状态，后存消息
                if (transferMessage.getOpType() == MsgBean.TransferMessage.OpType.RECEIVE || transferMessage.getOpType() == MsgBean.TransferMessage.OpType.REJECT) {
                    localDataSource.updateTransferStatus(realm, transferMessage.getId(), transferMessage.getOpTypeValue(), wrapMessage.getFromUid());
                }
            }
            result = saveMessageNew(bean, realm);
        }
        return result;
    }

    /**
     * //开关变更
     *
     * @param wrapMessage
     */
    public boolean handlerSwitchChange(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        boolean result = true;
        boolean isFromSelf = UserAction.getMyId() != null && wrapMessage.getFromUid() == UserAction.getMyId().intValue() && wrapMessage.getFromUid() != wrapMessage.getToUid();
        // TODO　处理老版本不兼容问题
        if (wrapMessage.getSwitchChange().getSwitchType() == MsgBean.SwitchChangeMessage.SwitchType.UNRECOGNIZED) {
            return result;
        }
        LogUtil.getLog().d(TAG, "开关变更:" + wrapMessage.getSwitchChange().getSwitchType());

        int switchType = wrapMessage.getSwitchChange().getSwitchType().getNumber();
        int switchValue = wrapMessage.getSwitchChange().getSwitchValue();
        long uid = isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid();
        UserInfo userInfo = localDataSource.getFriend(realm, uid);
        UserInfo userInfoCopy = realm.copyFromRealm(userInfo);
        if (userInfo == null) {
            return result;
        }
        switch (switchType) {
            case ChatEnum.ESwitchType.READ: // 单聊已读
                userInfoCopy.setFriendRead(switchValue);
                localDataSource.updateUserInfo(realm, userInfoCopy);
                EventBus.getDefault().post(new EventIsShowRead(uid, EventIsShowRead.EReadSwitchType.SWITCH_FRIEND, switchValue));
                break;
            case ChatEnum.ESwitchType.VIP: //vip
                UserBean userBean = (UserBean) UserAction.getMyInfo();
                if (userBean != null) {
                    userBean.setVip(wrapMessage.getSwitchChange().getSwitchValue() + "");
                    localDataSource.updateUserBean(realm, userBean);
                }
                // 刷新用户信息
                EventFactory.FreshUserStateEvent event = new EventFactory.FreshUserStateEvent();
                event.vip = wrapMessage.getSwitchChange().getSwitchValue() + "";
                EventBus.getDefault().post(event);
                break;
            case ChatEnum.ESwitchType.MASTER_READ:  //已读总开关
                userInfoCopy.setMasterRead(switchValue);
                localDataSource.updateUserInfo(realm, userInfoCopy);
                EventBus.getDefault().post(new EventIsShowRead(uid, EventIsShowRead.EReadSwitchType.SWITCH_MASTER, switchValue));
                break;
            case ChatEnum.ESwitchType.SHUT_UP: // 单人禁言
            case ChatEnum.ESwitchType.OPEN_UP_RED_ENVELOPER: // 领取群红包
                MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
                result = saveMessageNew(bean, realm);
                break;
            case ChatEnum.ESwitchType.SCREENSHOT: // 截屏通知开关
                bean = MsgConversionBean.ToBean(wrapMessage);
                result = saveMessageNew(bean, realm);
                localDataSource.updateFriendSnapshot(realm, wrapMessage.getFromUid(), switchValue);
                MessageManager.getInstance().notifySwitchSnapshot("", wrapMessage.getFromUid(), switchValue);
                break;
            case ChatEnum.ESwitchType.FRIEND_LOCKED: // 好友锁状态变更
                userInfoCopy.setLockedstatus(switchValue);
                localDataSource.updateUserInfo(realm, userInfoCopy);
                break;
        }
        return result;
    }

    /**
     * 阅后即焚
     *
     * @param wrapMessage
     */
    public boolean toDoChangeSurvivalTime(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        boolean result = true;
        MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
        boolean isFromSelf = UserAction.getMyId() != null && wrapMessage.getFromUid() == UserAction.getMyId().intValue() && wrapMessage.getFromUid() != wrapMessage.getToUid();

        result = saveMessageNew(bean, realm);
        int survivalTime = wrapMessage.getChangeSurvivalTime().getSurvivalTime();
        localDataSource.updateSurvivalTime(realm, wrapMessage.getGid(), isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid(), survivalTime);
        EventBus.getDefault().post(new ReadDestroyBean(survivalTime, wrapMessage.getGid(), wrapMessage.getFromUid()));
        return result;
    }

    /**
     * 撤销消息
     *
     * @param wrapMessage
     */
    public boolean handlerCancel(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        boolean result = true;
        MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
        boolean isFromSelf = UserAction.getMyId() != null && wrapMessage.getFromUid() == UserAction.getMyId().intValue() && wrapMessage.getFromUid() != wrapMessage.getToUid();
        if (bean != null) {
            String cancelMsgId = wrapMessage.getCancel().getMsgId();
            //TODO:saveMessageNew的有更新未读数
            // 判断消息是否存在，不存在则不保存
            MsgAllBean msgAllBean = realm.where(MsgAllBean.class).equalTo("msg_id", cancelMsgId).findFirst();
            if (filterDeleteCancel(wrapMessage.getFromUid())) {
                if (msgAllBean != null) {
                    result = saveMessageNew(bean, realm);
                    localDataSource.deleteMsg(realm, cancelMsgId);
                } else {
                    int position = offlineMsgIds.indexOf(cancelMsgId);
                    if (position >= 0 && position < offlineMsgAllBean.size()) {
                        msgAllBean = offlineMsgAllBean.get(position);
                        if (msgAllBean != null) {
                            offlineMsgAllBean.remove(msgAllBean);
                        }
                    }
                    int currentId = offlineMsgIds.indexOf(wrapMessage.getMsgId());
                    if (currentId >= 0 && currentId < offlineMsgAllBean.size()) {
                        msgAllBean = offlineMsgAllBean.get(position);
                        if (msgAllBean != null) {
                            offlineMsgAllBean.remove(msgAllBean);
                        }
                    }
                }
            } else {
                if (msgAllBean != null) {
                    result = saveMessageNew(bean, realm);
                    localDataSource.deleteMsg4Cancel(realm, wrapMessage.getMsgId(), cancelMsgId);
//                    MessageManager.getInstance().notifyRefreshChat(bean.getGid(), isFromSelf ? bean.getTo_uid() : bean.getFrom_uid());
                    // 处理图片撤回，在预览弹出提示
                    EventFactory.ClosePictureEvent event = new EventFactory.ClosePictureEvent();
                    event.msg_id = bean.getMsgCancel().getMsgidCancel();
                    event.name = bean.getFrom_nickname();
                    EventBus.getDefault().post(event);
                    // 处理语音撤回，对方在播放时停止播放
                    EventFactory.StopVoiceeEvent eventVoice = new EventFactory.StopVoiceeEvent();
                    eventVoice.msg_id = bean.getMsgCancel().getMsgidCancel();
                    EventBus.getDefault().post(eventVoice);
                    // 处理视频撤回，对方在播放时停止播放
                    EventFactory.StopVideoEvent eventVideo = new EventFactory.StopVideoEvent();
                    eventVideo.msg_id = bean.getMsgCancel().getMsgidCancel();
                    eventVideo.name = bean.getFrom_nickname();
                    EventBus.getDefault().post(eventVideo);
                } else {
                    int position = offlineMsgIds.indexOf(cancelMsgId);
                    if (position >= 0 && position < offlineMsgAllBean.size()) {
                        msgAllBean = offlineMsgAllBean.get(position);
                        if (msgAllBean != null) {
                            result = saveMessageNew(bean, realm);
                            offlineMsgAllBean.remove(msgAllBean);
                        }
                    }
                }
            }

        }
        return result;
    }

    /**
     * 群公告
     *
     * @param wrapMessage
     */
    public boolean handlerGroupAnnouncement(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        boolean result = true;
        MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
        String gid = wrapMessage.getGid();
        int atType = wrapMessage.getAt().getAtType().getNumber();
        String message = wrapMessage.getAt().getMsg();
        result = saveMessageNew(bean, realm);
        dealAtMessage(gid, atType, message, null, realm);
        return result;
    }

    /**
     * 强制退出，登录冲突
     *
     * @param wrapMessage
     */
    public void toDoForceOffline(MsgBean.UniversalMessage.WrapMessage wrapMessage) {
        EventLoginOut4Conflict eventLoginOut4Conflict = new EventLoginOut4Conflict();
        if (wrapMessage.getForceOffline().getForceOfflineReason() == MsgBean.ForceOfflineReason.CONFLICT) {// 登录冲突
            String phone = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.PHONE).get4Json(String.class);
            eventLoginOut4Conflict.setMsg("您的账号" + phone + "已经在另外一台设备上登录。如果不是您本人操作,请尽快修改密码");
        } else if (wrapMessage.getForceOffline().getForceOfflineReason() == MsgBean.ForceOfflineReason.LOCKED) {//被冻结
            eventLoginOut4Conflict.setMsg("你已被限制登录");
        } else if (wrapMessage.getForceOffline().getForceOfflineReason() == MsgBean.ForceOfflineReason.PASSWORD_CHANGED) {//修改密码
            eventLoginOut4Conflict.setMsg("您已成功重置密码，请使用新密码重新登录");
        } else if (wrapMessage.getForceOffline().getForceOfflineReason() == MsgBean.ForceOfflineReason.USER_DEACTIVATING) {//修改密码
            eventLoginOut4Conflict.setMsg("工作人员将在30天内处理您的申请并删除账号下所有数据。在此期间，请不要登录常信。");
        } else if (wrapMessage.getForceOffline().getForceOfflineReason() == MsgBean.ForceOfflineReason.BOUND_PHONE_CHANGED) {//修改手机
            eventLoginOut4Conflict.setMsg("更换绑定手机号成功，\n请新手机号重新登录");
        } else if (wrapMessage.getForceOffline().getForceOfflineReason() == MsgBean.ForceOfflineReason.APPEAL_PASS) {//申诉通过
            eventLoginOut4Conflict.setMsg("您的账号申诉已通过，请重新登录以恢复功能使用");
        }
        EventBus.getDefault().post(eventLoginOut4Conflict);
    }

    /**
     * 修改群属性
     *
     * @param wrapMessage
     */
    public boolean handlerChangeGroupMeta(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        boolean result = true;
        MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
        MsgBean.ChangeGroupMetaMessage.RealMsgCase realMsgCase = wrapMessage.getChangeGroupMeta().getRealMsgCase();
        switch (realMsgCase) {
            case NAME://群名
                result = saveMessageNew(bean, realm);
                localDataSource.updateGroup(realm, wrapMessage.getGid(), wrapMessage.getChangeGroupMeta().getName()
                        , null, null, null);
                break;
            case PROTECT_MEMBER://群成员保护
                localDataSource.updateGroup(realm, wrapMessage.getGid(), null
                        , wrapMessage.getChangeGroupMeta().getProtectMember(), null, null);
                break;
            case AVATAR://群头像
                break;
            case SHUT_UP:// 是否开启全群禁言
                result = saveMessageNew(bean, realm);
                break;
            case SCREENSHOT_NOTIFICATION:
                // 更新群截屏状态
                result = saveMessageNew(bean, realm);
                localDataSource.updateGroup(realm, wrapMessage.getGid(), null
                        , null, wrapMessage.getChangeGroupMeta().getScreenshotNotification() ? 1 : 0, null);
                MessageManager.getInstance().notifySwitchSnapshot(wrapMessage.getGid(), 0, wrapMessage.getChangeGroupMeta().getScreenshotNotification() ? 1 : 0);
                break;
            case FORBBIDEN://封群
                result = saveMessageNew(bean, realm);
                LogUtil.getLog().d(TAG, ">>>群状态改变---uid=" + wrapMessage.getFromUid() + "--isForbid=" + wrapMessage.getChangeGroupMeta().getForbbiden());
                localDataSource.updateGroup(realm, wrapMessage.getGid(), null
                        , null, null, wrapMessage.getChangeGroupMeta().getForbbiden() ? ChatEnum.EGroupStatus.BANED : ChatEnum.EGroupStatus.NORMAL);
                Group group = localDataSource.getGroup(realm, wrapMessage.getGid());
                if (group != null) {
                    if (MessageManager.getInstance().isMsgFromCurrentChat(wrapMessage.getGid(), null)) {
                        MessageManager.getInstance().notifyGroupMetaChange(realm.copyFromRealm(group));
                    }
                }
                break;
        }
        return result;
    }

    /**
     * 接受入群，
     *
     * @param wrapMessage
     */
    public boolean handlerAcceptBeGroup(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        boolean result = true;
        MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
        result = saveMessageNew(bean, realm);
        //被邀请进群，表示已经同意了
        if (wrapMessage.getAcceptBeGroup() != null) {
            List<MsgBean.GroupNoticeMessage> noticeMessageList = wrapMessage.getAcceptBeGroup().getNoticeMessageList();
            if (noticeMessageList != null && noticeMessageList.size() > 0) {
                for (int i = 0; i < noticeMessageList.size(); i++) {
                    MsgBean.GroupNoticeMessage message = noticeMessageList.get(i);
                    long uid = message.getUid();
                    localDataSource.updateGroupApply(realm, wrapMessage.getGid(), uid, 2);
                }
            }
        }
        //获取群信息
        requestGroupInfo(wrapMessage.getGid());
        MessageManager.getInstance().notifyGroupChange(true);
        return result;
    }

    /**
     * 其他群成员被移除群聊，可能会有群主退群，涉及群主迭代,所以需要从服务器重新拉取数据
     *
     * @param wrapMessage
     */
    public boolean handlerRemoveGroupMember2(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        boolean result = true;
        // 判断是否是管理员 群主 是就显示被剔的消息
        if (localDataSource.isGroupMasterOrManager(realm, wrapMessage.getGid(), UserAction.getMyId())) {
            // 保存被剔消息
            if (wrapMessage.getRemoveGroupMember2().getNoticeMessageList() != null &&
                    wrapMessage.getRemoveGroupMember2().getNoticeMessageList().size() > 0) {
                MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
                result = saveMessageNew(bean, realm);
            }
        }
        MsgBean.RemoveGroupMember2Message removeGroupMember2 = wrapMessage.getRemoveGroupMember2();
        localDataSource.removeGroupMember(realm, wrapMessage.getGid(), removeGroupMember2.getUidList());
        requestGroupInfo(wrapMessage.getGid());
        MessageManager.getInstance().notifyGroupChange(false);
        return result;
    }

    /**
     * 自己被移除群聊，如果该群是已保存群聊，需要改为未保存
     *
     * @param wrapMessage
     */
    public boolean handlerRemoveGroupMember(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
        boolean result = true;
        boolean isFromSelf = UserAction.getMyId() != null && wrapMessage.getFromUid() == UserAction.getMyId().intValue() && wrapMessage.getFromUid() != wrapMessage.getToUid();
        if (bean != null && !isFromSelf) {//除去自己PC端移除
            result = saveMessageNew(bean, realm);
            localDataSource.removeGroupMember(realm, bean.getGid(), UserAction.getMyId());
            //重新生成群头像
            List<String> gids = new ArrayList<>();
            if (!TextUtils.isEmpty(bean.getGid()))
                gids.add(bean.getGid());
            //回主线程调用更新sessionDetial
            if (MyAppLication.INSTANCE().repository != null)
                MyAppLication.INSTANCE().repository.updateSessionDetail(gids, null);
            //通知UI更新
            MessageManager.getInstance().notifyGroupChange(false);
        }
        return result;
    }

    /**
     * 退出群聊，如果该群是已保存群聊，需要改为未保存
     *
     * @param wrapMessage
     */
    public boolean handlerOutGroup(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
        boolean result = true;
        if (wrapMessage.getFromUid() != UserAction.getMyId()) {//不是自己退群，才更新（自己退群，session信息已经被删除）
            if (localDataSource.isGroupMasterOrManager(realm, wrapMessage.getGid(), UserAction.getMyId())) {
                result = saveMessageNew(bean, realm);
            }
            //请求获取群信息
            requestGroupInfo(wrapMessage.getGid());
        } else {//自己退群
            localDataSource.deleteGroup(realm, bean.getGid());
            MessageManager.getInstance().notifyGroupChange(false);
        }
        return result;
    }

    /**
     * 转让群主
     *
     * @param wrapMessage
     */
    public boolean handlerChangeGroupMaster(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        boolean result = true;
        MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
        result = saveMessageNew(bean, realm);
        //请求获取群信息
        requestGroupInfo(wrapMessage.getGid());
        return result;
    }

    /**
     * 接受成为好友,需要产生消息后面在处理
     *
     * @param wrapMessage
     */
    public boolean handlerAcceptBeFriends(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        boolean result = true;
        boolean isFromSelf = UserAction.getMyId() != null && wrapMessage.getFromUid() == UserAction.getMyId().intValue() && wrapMessage.getFromUid() != wrapMessage.getToUid();
        MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
        //检测双重消息
        if (wrapMessage.getMsgType() == ACCEPT_BE_FRIENDS) {
            MsgBean.AcceptBeFriendsMessage receiveMessage = wrapMessage.getAcceptBeFriends();
            if (receiveMessage != null && !TextUtils.isEmpty(receiveMessage.getSayHi())) {
                ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), receiveMessage.getSayHi());
                MsgAllBean message = createMsgBean(wrapMessage, ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.NORMAL, wrapMessage.getTimestamp() - 1, chatMessage);
                DaoUtil.save(message);
            }
        }
        result = saveMessageNew(bean, realm);
        MessageManager.getInstance().notifyRefreshFriend(false, isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid(), CoreEnum.ERosterAction.ACCEPT_BE_FRIENDS);
        // TODO 双方互添加好友的情况
        EventBus.getDefault().post(new RefreshApplyEvent(wrapMessage.getFromUid(), CoreEnum.EChatType.PRIVATE, 1));
        localDataSource.acceptFriendRequest(realm, wrapMessage.getFromUid() + "");
        return result;
    }

    /**
     * 音视频消息
     *
     * @param wrapMessage
     */
    public boolean handlerP2PAUVideo(MsgBean.UniversalMessage.WrapMessage wrapMessage, Realm realm) {
        MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
        boolean result = true;
        if (bean != null) {
            if (bean.getP2PAuVideoMessage() != null && "cancel".equals(bean.getP2PAuVideoMessage().getOperation())) {
                bean.getP2PAuVideoMessage().setDesc("对方" + bean.getP2PAuVideoMessage().getDesc());
            } else if (bean.getP2PAuVideoMessage() != null && "reject".equals(bean.getP2PAuVideoMessage().getOperation())) {
                bean.getP2PAuVideoMessage().setDesc(bean.getP2PAuVideoMessage().getDesc().replace("对方", ""));
            } else if (bean.getP2PAuVideoMessage() != null && "notaccpet".equals(bean.getP2PAuVideoMessage().getOperation())) {
                bean.getP2PAuVideoMessage().setDesc("对方已取消");
            } else if (bean.getP2PAuVideoMessage() != null && "interrupt".equals(bean.getP2PAuVideoMessage().getOperation())) {
                bean.getP2PAuVideoMessage().setDesc("通话中断");
            }
            // TODO　收到消息标记通话已结束，处理部分机型在最小化时，收到对方音视频通话，对方过了3、4秒挂断，一直提示正在通话问题 处理Bugly: 4622问题；
            AVChatProfile.getInstance().setCallIng(false);
            result = saveMessageNew(bean, realm);
        }
        return result;
    }

    /**
     * @param wrapMessage
     */
    public boolean handlerDoChat(MsgBean.UniversalMessage.WrapMessage wrapMessage, String requestId, Realm realm) {
        boolean result = true;
        boolean isFromSelf = UserAction.getMyId() != null && wrapMessage.getFromUid() == UserAction.getMyId().intValue() && wrapMessage.getFromUid() != wrapMessage.getToUid();
        MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
        if (bean != null) {
            if (!TextUtils.isEmpty(requestId)) {
                bean.setRequest_id(requestId);
            }
            //判断是否是文件传输助手
            if (UserAction.getMyId() != null && wrapMessage.getFromUid() == UserAction.getMyId().intValue() && !isFromSelf) {
                bean.setFrom_uid(-wrapMessage.getFromUid());
            }
        }
        result = saveMessageNew(bean, realm);
        return result;
    }

    /***
     * 根据接收到的消息内容，更新用户头像昵称等资料
     * @param msg
     */
    public void updateUserAvatarAndNick(MsgBean.UniversalMessage.WrapMessage msg, Realm realm) {
        if (msg.getMsgType() == MsgBean.MessageType.UNRECOGNIZED || msg.getMsgType().getNumber() > 100) {//通知类消息
            return;
        }
        boolean hasChange = localDataSource.updateFriendPortraitAndName(realm, msg.getFromUid(), msg.getAvatar(), msg.getNickname());
        //避免重复刷新通讯录
        if (msg.getMsgType() == REQUEST_FRIEND || msg.getMsgType() == ACCEPT_BE_FRIENDS
                || msg.getMsgType() == REMOVE_FRIEND || msg.getMsgType() == REQUEST_GROUP
                || msg.getMsgType() == ACTIVE_STAT_CHANGE) {
            return;
        }
        if (hasChange) {
            MessageManager.getInstance().notifyRefreshFriend(true, msg.getFromUid(), CoreEnum.ERosterAction.UPDATE_INFO);
        }
    }

    /*
     * 保存消息
     * @param msgAllBean 消息
     *
     * */
    private boolean saveMessageNew(MsgAllBean msgAllBean, Realm realm) {
        if (msgAllBean == null) return false;
        boolean result = false;
        boolean isFromSelf = UserAction.getMyId() != null && msgAllBean.getFrom_uid() == UserAction.getMyId().intValue();
        Long friendUid = msgAllBean.getFrom_uid();
        if (isFromSelf) {
            friendUid = msgAllBean.getTo_uid();
            if (!TextUtils.isEmpty(msgAllBean.getGid()) && msgAllBean.getSurvival_time() > 0) {//自己PC端发送的群聊消息，阅后即焚消息，立即加入
                msgAllBean.setStartTime(msgAllBean.getTimestamp());
                msgAllBean.setEndTime(msgAllBean.getTimestamp() + (msgAllBean.getSurvival_time() * 1000));
            }
        }
        try {
            //检查是否需要加载信息
            checkNeedRequestData(msgAllBean, isFromSelf, realm);
            msgAllBean.setTo_uid(msgAllBean.getTo_uid());
            //是否是对方 @所有人或@自己的消息
            if (!isOffline || localDataSource.isAtMe(msgAllBean)) {//在线消息 或@自己的消息，及时更新session，离线消息等批量结束后更新
                if (msgAllBean.getMsg_type() == ChatEnum.EMessageType.MSG_CANCEL && filterDeleteCancel(msgAllBean.getFrom_uid())) {
                    //来自小助手消息的撤销消息不需要存储
                } else {
                    LogUtil.getLog().i("Liszt_test", "--消息LOG--updateObject--" + msgAllBean.getMsg_type());
                    localDataSource.updateObject(realm, msgAllBean);
                }
                LogUtil.getLog().i(TAG, "--消息LOG--存储在线消息--" + msgAllBean.getMsg_type());
                if (MessageManager.getInstance().isMsgFromCurrentChat(msgAllBean.getGid(), isFromSelf ? msgAllBean.getTo_uid() : msgAllBean.getFrom_uid())) {
                    MessageManager.getInstance().notifyRefreshChat(msgAllBean, CoreEnum.ERefreshType.ADD);
                }
                //非自己发过来的消息，才存储为未读状态
                if (!isFromSelf) {
                    boolean canChangeUnread = !MessageManager.getInstance().isMsgFromCurrentChat(msgAllBean.getGid(), null);
                    localDataSource.updateSessionRead(realm, msgAllBean.getGid(), friendUid, canChangeUnread, msgAllBean);
                } else {
                    //自己PC 端发的消息刷新session
                    /********通知更新或创建session ************************************/
                    localDataSource.updateFromSelfPCSession(realm, msgAllBean);
                    /********通知更新或创建session end************************************/
                }
            } else {//离线消息，先存储后批量更新
//                if (offlineMsgAllBean == null) offlineMsgAllBean = new ConcurrentHashMap<>();
//                offlineMsgAllBean.put(msgAllBean.getMsg_id(), msgAllBean);
                if (offlineMsgIds == null) {
                    offlineMsgIds = new CopyOnWriteArrayList<>();
                }
                offlineMsgIds.add(msgAllBean.getMsg_id());

                if (offlineMsgAllBean == null) {
                    offlineMsgAllBean = new CopyOnWriteArrayList<>();
                }
                offlineMsgAllBean.add(msgAllBean);

            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getLog().d("a=", TAG + "--消息存储失败--msgId=" + msgAllBean.getMsg_id() + "--msgType=" + msgAllBean.getMsg_type());
        }
        return result;
    }


    /**
     * 批量保存消息对象
     *
     * @param realm
     * @return
     */
    public boolean insertOfflineMessages(Realm realm) {
        boolean result = false;
        if (offlineMsgAllBean != null && offlineMsgAllBean.size() > 0) {
//            List<MsgAllBean> msgList = new ArrayList<>();
//            Iterator iterator = offlineMsgAllBean.keySet().iterator();
//            while (iterator.hasNext()) {
//                msgList.add(offlineMsgAllBean.get(iterator.next().toString()));
//            }
            result = localDataSource.insertOfflineMessages(realm, offlineMsgAllBean);
            offlineMsgAllBean.clear();
            offlineMsgIds.clear();
        }

        return result;
    }

    private void checkNeedRequestData(MsgAllBean msgAllBean, boolean isFromSelf, Realm realm) {
        Long friendUid = isFromSelf ? msgAllBean.getTo_uid() : msgAllBean.getFrom_uid();//对方的Id
        //群
        if (!TextUtils.isEmpty(msgAllBean.getGid()) && localDataSource.getGroup(realm, msgAllBean.getGid()) == null) {
            requestGroupInfo(msgAllBean.getGid());
        } else if (TextUtils.isEmpty(msgAllBean.getGid()) && friendUid != null && friendUid > 0 && localDataSource.getFriend(realm, friendUid) == null) {//单聊
            remoteDataSource.getFriend(friendUid, new Function<UserInfo, Boolean>() {
                @NullableDecl
                @Override
                public Boolean apply(@NullableDecl UserInfo user) {
                    //网络请求后，都在主线程回调，不需要关闭Realm,Applicaiton中会自动关闭
                    Realm realm1 = DaoUtil.open();
                    localDataSource.updateUserInfo(realm1, user);
                    localDataSource.updateSessionTopAndDisturb(realm1, null, user.getUid(), user.getIstop(), user.getDisturb());
                    return true;
                }
            });
            LogUtil.getLog().d("a=", TAG + "--需要加载用户信息");
        }
    }

    /**
     * 更新离线同步自己PC端发送的已读消息
     * 将好友发送的消息已读、阅后即焚状态更改
     */
    public void updateOfflineReadMsg(Realm realm) {
        try {
            if (offlineMySelfPCGroupReadMsg.size() > 0) {//自己PC已读对方-群
                for (String gid : offlineMySelfPCGroupReadMsg.keySet()) {
                    long timestamp = offlineMySelfPCGroupReadMsg.get(gid);
                    if (gid != null)
                        localDataSource.updateRecivedMsgReadForPC(realm, gid, null, timestamp);
                }
            }

            if (offlineMySelfPCFriendReadMsg.size() > 0) {//自己PC已读对方-好友
                for (Long uid : offlineMySelfPCFriendReadMsg.keySet()) {
                    long timestamp = offlineMySelfPCFriendReadMsg.get(uid);
                    if (uid != null)
                        localDataSource.updateRecivedMsgReadForPC(realm, null, uid, timestamp);
                }
            }

            if (offlineFriendReadMsg.size() > 0) {//对方已读自己发送的消息
                for (Long uid : offlineFriendReadMsg.keySet()) {
                    long timestamp = offlineFriendReadMsg.get(uid);
                    if (uid != null)
                        localDataSource.updateFriendMsgReadAndSurvivalTime(realm, uid, timestamp);
                }
            }
            offlineFriendReadMsg.clear();
            offlineMySelfPCGroupReadMsg.clear();
            offlineMySelfPCFriendReadMsg.clear();
        } catch (Exception e) {
        }

    }

    /**
     * 接收离线消息后 更新session
     *
     * @param realm
     * @param gid
     * @param uid
     */
    public void offlineUpdateSession(Realm realm, String gid, Long uid) {
        //最新消息
        MsgAllBean lastMessage = null;
        if (TextUtils.isEmpty(gid)) {
            lastMessage = realm.where(MsgAllBean.class)
                    .beginGroup().isEmpty("gid").or().isNull("gid").endGroup()
                    .and()
                    .beginGroup()
                    .equalTo("from_uid", uid).or().equalTo("to_uid", uid)
                    .endGroup()
                    .sort("timestamp", Sort.DESCENDING).findFirst();
        } else {
            lastMessage = realm.where(MsgAllBean.class).equalTo("gid", gid).sort("timestamp", Sort.DESCENDING).findFirst();
        }
        if (lastMessage != null) {
            boolean isFromSelf = UserAction.getMyId() != null && lastMessage.getFrom_uid() == UserAction.getMyId().intValue();
            long chatterId = isFromSelf ? lastMessage.getTo_uid() : lastMessage.getFrom_uid();
            //非自己发过来的消息，才存储为未读状态
            if (!isFromSelf) {
                boolean canChangeUnread = !MessageManager.getInstance().isMsgFromCurrentChat(lastMessage.getGid(), null);
                localDataSource.updateSessionRead(realm, lastMessage.getGid(), chatterId, canChangeUnread, lastMessage);
                //矫正未读数
                localDataSource.correctSessionCount(realm, lastMessage.getGid(), chatterId);
            } else {
                //自己PC 端发的消息刷新session
                /********通知更新或创建session ************************************/
                localDataSource.updateFromSelfPCSession(realm, lastMessage);
                /********通知更新或创建session end************************************/
            }
        }
    }

    //过滤撤销消息中只删除消息，不产生消息记录的消息
    private boolean filterDeleteCancel(long fromUid) {
        return fromUid == Constants.CX_HELPER_UID;
    }
}
