package com.yanlong.im.chat.manager;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.hm.cxpay.eventbus.PayResultEvent;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.ApplyBean;
import com.yanlong.im.chat.bean.AtMessage;
import com.yanlong.im.chat.bean.ChatMessage;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MessageDBTemp;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.chat.bean.ReadDestroyBean;
import com.yanlong.im.chat.bean.RedEnvelopeMessage;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.eventbus.EventReceiveImage;
import com.yanlong.im.chat.eventbus.EventRefreshGroup;
import com.yanlong.im.chat.eventbus.EventRefreshMainMsg;
import com.yanlong.im.chat.eventbus.EventRefreshUser;
import com.yanlong.im.chat.eventbus.EventReportGeo;
import com.yanlong.im.chat.eventbus.EventSwitchSnapshot;
import com.yanlong.im.chat.task.DispatchMessage;
import com.yanlong.im.chat.task.OfflineMessage;
import com.yanlong.im.chat.task.TaskDealWithMsgList;
import com.yanlong.im.chat.ui.ChatActionActivity;
import com.yanlong.im.circle.recommend.RecommendFragment;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.UserBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.MediaBackUtil;
import com.yanlong.im.utils.audio.AudioPlayManager;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.EventGroupChange;
import net.cb.cb.library.bean.EventIsShowRead;
import net.cb.cb.library.bean.EventLoginOut4Conflict;
import net.cb.cb.library.bean.EventOnlineStatus;
import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.bean.EventRefreshFriend;
import net.cb.cb.library.bean.EventSwitchDisturb;
import net.cb.cb.library.bean.EventUserOnlineChange;
import net.cb.cb.library.bean.GroupStatusChangeEvent;
import net.cb.cb.library.bean.RefreshApplyEvent;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.SpUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

import static com.yanlong.im.utils.socket.MsgBean.MessageType.ACCEPT_BE_FRIENDS;
import static com.yanlong.im.utils.socket.MsgBean.MessageType.ACTIVE_STAT_CHANGE;
import static com.yanlong.im.utils.socket.MsgBean.MessageType.P2P_AU_VIDEO_DIAL;
import static com.yanlong.im.utils.socket.MsgBean.MessageType.REMOVE_FRIEND;
import static com.yanlong.im.utils.socket.MsgBean.MessageType.REQUEST_FRIEND;
import static com.yanlong.im.utils.socket.MsgBean.MessageType.REQUEST_GROUP;
import static com.yanlong.im.utils.socket.SocketData.createMsgBean;
import static com.yanlong.im.utils.socket.SocketData.oldMsgId;

/**
 * @author Liszt
 * @date 2019/9/24
 * Description 消息管理类  MessageRepository
 */
public class MessageManager {
    private final String TAG = MessageManager.class.getSimpleName();

    private int SESSION_TYPE = 0;//无会话,1:单人;2群,3静音模式
    public static Long SESSION_FUID;//单人会话id
    public static String SESSION_GID;//群会话id
    private Long PREVIEW_UID;//预览uid
    private String PREVIEW_GID;//预览群id

    private static MessageManager INSTANCE;
    private MsgDao msgDao = new MsgDao();
    private UserDao userDao = new UserDao();
    private boolean isMessageChange;//是否有聊天消息变化
    @Deprecated
    private List<String> loadGids = new ArrayList<>();//需要异步加载群数据的群id
    @Deprecated
    private List<Long> loadUids = new ArrayList<>();//需要异步记载用户数据的用户id

    //缓存
    private static List<Group> saveGroups = new ArrayList<>();//已保存群信息缓存
    private static Map<String, TaskDealWithMsgList> taskMaps = new HashMap<>();//批量消息的处理

    private long playTimeOld = 0;//当前声音播放时间
    private long playVBTimeOld = 0; //当前震动时间

    private Boolean CAN_STAMP = true;//true 允许戳一戳弹窗 ,false 不允许
    /**
     * 保存接收了双向清除指令的from_uid-待最后一条消息时间戳
     * 用于丢弃在此时间戳之前的消息
     */
    @Deprecated
    public Map<Long, Long> historyCleanMsg = new HashMap<>();

    /**
     * 处理群聊 接收离线消息 自己PC端发送的已读消息
     * 保存已读消息 gid-消息时间戳
     * 用于处理自己已读和阅后即焚消息状态
     */
    @Deprecated
    private Map<String, Long> offlineGroupReadMsg = new HashMap<>();
    /**
     * 处理单聊 接收离线消息 自己PC端发送的已读消息
     * 保存已读消息 from_uid-消息时间戳
     * 用于处理自己已读和阅后即焚消息状态
     */
    @Deprecated
    private Map<Long, Long> offlineFriendReadMsg = new HashMap<>();

    /**
     * 保存单聊发送已读消息时间戳
     */
    private Map<Long, Long> readTimeMap = new HashMap<>();

    private boolean isMicrophoneUsing = false;

    //使用线程安全的
    private List<MsgBean.UniversalMessage> toDoMsg = new CopyOnWriteArrayList<>();


    //是否正在处理消息
    private boolean isDealingMsg = false;
    //是否正在处理消息
    private boolean isReceiveOffline = false;
    //是否来自推送
    private boolean isFromPush = false;


    //取第一个添加的消息,并且移除
    public synchronized MsgBean.UniversalMessage poll() {
        if (toDoMsg.size() > 0) {
            MsgBean.UniversalMessage message = toDoMsg.get(toDoMsg.size() - 1);
            toDoMsg.remove(message);
            LogUtil.getLog().i(TAG, "消息LOG--poll--" + message.getRequestId() + "--剩余--size=" + toDoMsg.size());
            isDealingMsg = false;
            return message;
        } else {
            isDealingMsg = false;
            return null;
        }
    }

    public int getToDoMsgCount() {
        return toDoMsg.size();
    }

    /**
     * 移除
     */
    public void pop() {
        if (toDoMsg.size() > 0) {
            MsgBean.UniversalMessage bean = toDoMsg.get(toDoMsg.size() - 1);
            toDoMsg.remove(bean);
        }
        isDealingMsg = false;
    }

    /**
     * 添加
     *
     * @param receiveMsg
     */
    public synchronized void push(MsgBean.UniversalMessage receiveMsg) {
        toDoMsg.add(receiveMsg);
        LogUtil.getLog().i(TAG, "消息LOG--add 队列--" + receiveMsg.getRequestId() + "--size=" + toDoMsg.size());
    }

    public void clear() {
        LogUtil.getLog().i(TAG, "消息LOG-- 队列 clear");
        this.toDoMsg.clear();
        this.isDealingMsg = false;
    }

    public static MessageManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessageManager();
        }
        return INSTANCE;
    }


    /**
     * 离线消息分发处理器
     */
    public DispatchMessage offlineMsgDispatch = new OfflineMessage();


    /**
     * 停止离线消息处理
     */
    public void stopOfflineTask() {
        offlineMsgDispatch.clear();
    }

    /*
     * 消息接收流程
     * */
    public synchronized void onReceive(MsgBean.UniversalMessage bean) {
        boolean isOfflineMsg = bean.getMsgFrom() == 1;
        if (!isOfflineMsg) {//在线消息
            push(bean);
            if (!isDealingMsg) {//上一个处理完成，再启动处理消息sevice
                isDealingMsg = true;
                MyAppLication.INSTANCE().startMessageIntentService();
            } else {
                //在线消息线程处理不过来了，启动旧消息通道来处理
                LogUtil.getLog().i(TAG, "消息LOG--在线--但是Dealing");
//                LogUtil.writeLog("接收到在线消息--但是Dealing==" + bean.getRequestId());
                try {
                    Thread.sleep(100);
                    MyAppLication.INSTANCE().startMessageIntentService();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LogUtil.getLog().i(TAG, "消息LOG--在线--睡眠出错");
                }
            }
        } else {
            offlineMsgDispatch.dispatch(bean, null);
            isReceiveOffline = true;
        }
    }

//    public synchronized void onReceive(MsgBean.UniversalMessage bean) {
//        List<MsgBean.UniversalMessage.WrapMessage> msgList = bean.getWrapMsgList();
//        if (msgList != null) {
//            int length = msgList.size();
//            if (length > 0) {
//                if (length == 1) {//收到单条消息
//                    MsgBean.UniversalMessage.WrapMessage wrapMessage = msgList.get(0);
//                    dealWithMsg(wrapMessage, false, true, bean.getRequestId());
//                    checkServerTimeInit(wrapMessage);
//
//                } else {//收到多条消息（如离线）
//                    LogUtil.getLog().d("a=", "--总任务数=" + length + "--from=" + bean.getMsgFrom());
//                    TaskDealWithMsgList taskMsgList = getMsgTask(bean.getRequestId());
//                    if (taskMsgList == null) {
//                        taskMsgList = new TaskDealWithMsgList(msgList, bean.getRequestId(), bean.getMsgFrom(), length);
//                        taskMaps.put(bean.getRequestId(), taskMsgList);
//                    } else {
//                        taskMsgList.clearPendingList();
//                    }
//                    taskMsgList.execute();
//                }
//            }
//        }
//    }


    public synchronized void testReceiveMsg() {
        MsgBean.UniversalMessage.Builder builder = MsgBean.UniversalMessage.newBuilder();
        builder.setRequestId(SocketData.getUUID());
//        builder.setToUid(100105);
        for (int i = 0; i < 5; i++) {
            MsgBean.UniversalMessage.WrapMessage.Builder wrapMsg = MsgBean.UniversalMessage.WrapMessage.newBuilder();
            wrapMsg.setMsgId(SocketData.getUUID());
            wrapMsg.setFromUid(100804);
            wrapMsg.setTimestamp(SocketData.getSysTime());
            wrapMsg.setNickname("Liszt");
            wrapMsg.setAvatar("http://zx-im-img.zhixun6.com/product-environment/avatar/99a5614b-6648-4f45-a512-5e03e0d0dd6e.jpg");
            wrapMsg.setToUid(100105);
            MsgBean.ChatMessage.Builder msg = MsgBean.ChatMessage.newBuilder();
            msg.setMsg("测试第" + i + "条数据");
            wrapMsg.setChat(msg.build());
            builder.addWrapMsg(wrapMsg.build());
        }
        builder.setMsgFrom(1);
        onReceive(builder.build());
    }

    //接收到的单条消息作为服务器时间
    public void checkServerTimeInit(MsgBean.UniversalMessage.WrapMessage wrapMessage) {
//        if (SocketData.getPreServerAckTime() <= 0) {
        SocketData.setPreServerAckTime(wrapMessage.getTimestamp());
//        }
    }

    /**
     * 丢弃双向清除消息
     *
     * @param uid
     * @param timestamp
     */
    private boolean discardHistoryCleanMessage(Long uid, Long timestamp) {
        boolean result = false;
        if (historyCleanMsg.containsKey(uid) && historyCleanMsg.get(uid) >= timestamp) {
            if (historyCleanMsg.get(uid) >= timestamp) {
                result = true;
            } else if (timestamp - historyCleanMsg.get(uid) > 10 * 60 * 1000)
                //historyCleanMsg的消息时间，比当前接收消息时间超过10分钟的消息，从historyCleanMsg移除
                historyCleanMsg.remove(uid);
        }
        return result;
    }


    /**
     * 已过期，请使用  DispatchMessage中的dealWithMsg
     * 处理接收到的消息
     * * 分两类处理，一类是需要产生本地消息记录的，一类是相关指令，无需产生消息记录
     * * @param wrapMessage 接收到的消息
     * * @param isList 是否是批量消息
     * * @return 返回结果，不需要处理逻辑的消息，默认处理成功
     *
     * @param wrapMessage
     * @param isList
     * @param canNotify
     * @param requestId
     * @return 已过期，请使用  DispatchMessage中的dealWithMsg
     */
    @Deprecated
    public boolean dealWithMsg(MsgBean.UniversalMessage.WrapMessage wrapMessage, boolean isList, boolean canNotify, String requestId) {
        if (wrapMessage.getMsgType() == MsgBean.MessageType.UNRECOGNIZED) {
            return true;
        }
        /******丢弃消息-执行过双向删除，在指令之前的消息 2020/4/28****************************************/
        if (TextUtils.isEmpty(wrapMessage.getGid()) && historyCleanMsg.size() > 0) {//单聊
            if (discardHistoryCleanMessage(wrapMessage.getFromUid(), wrapMessage.getTimestamp()) ||
                    discardHistoryCleanMessage(wrapMessage.getToUid(), wrapMessage.getTimestamp())) {
                return true;
            }
        }
        /******end 丢弃消息-执行过双向删除，在指令之前的消息 2020/4/28****************************************/
        LogUtil.getLog().e(TAG, "消息LOG: " + wrapMessage.getMsgId() + "--type=" + wrapMessage.getMsgType());
        boolean result = true;
        boolean hasNotified = false;//已经通知刷新了
        boolean isCancelValid = false;//是否是有效撤销信息
        boolean isFromSelf = false;
        UserBean userBean = null;//自己的用户信息
        if (UserAction.getMyId() != null) {
            userBean = (UserBean) UserAction.getMyInfo();
            isFromSelf = wrapMessage.getFromUid() == UserAction.getMyId().intValue() && wrapMessage.getFromUid() != wrapMessage.getToUid();
        }
        if (!TextUtils.isEmpty(wrapMessage.getMsgId())) {
            if (oldMsgId.contains(wrapMessage.getMsgId())) {
                LogUtil.getLog().e(TAG, ">>>>>重复消息: " + wrapMessage.getMsgId());
                return true;
            } else {
                if (oldMsgId.size() >= 500) {
                    oldMsgId.remove(0);
                }
                LogUtil.getLog().e(TAG, ">>>>>消息LOG--add: " + wrapMessage.getMsgId());
                oldMsgId.add(wrapMessage.getMsgId());
            }
        }
        updateUserAvatarAndNick(wrapMessage, isList, requestId);
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
        switch (wrapMessage.getMsgType()) {
            case CHAT://文本

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

                break;
            case IMAGE://图片
                if (bean != null) {
                    result = saveMessageNew(bean, isList);
                    if (result && !isList) {
                        doRefreshPreviewImage(wrapMessage);
                    }
                }
                break;
            case HISTORY_CLEAN://双向清除
                //最后一条需要清除的聊天记录时间戳
                long lastNeedCleanTimestamp = wrapMessage.getTimestamp();
                //保存双向清除指令发送方和时间戳，用于丢弃在此时间戳之前的消息
                historyCleanMsg.put(isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid(), lastNeedCleanTimestamp);
                //清除好友历史记录
                msgDao.msgDel(isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid(), lastNeedCleanTimestamp);
                notifyRefreshChat(wrapMessage.getGid(), isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid());
                break;
            case P2P_AU_VIDEO:// 音视频消息
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
                    result = saveMessageNew(bean, isList);
                }
                break;
            case ACCEPT_BE_FRIENDS://接受成为好友,需要产生消息后面在处理
                checkDoubleMessage(wrapMessage);//检测双重消息
                if (bean != null) {
                    result = saveMessageNew(bean, isList);
                }
                notifyRefreshFriend(false, isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid(), CoreEnum.ERosterAction.ACCEPT_BE_FRIENDS);
                // TODO 双方互添加好友的情况
                EventBus.getDefault().post(new RefreshApplyEvent(wrapMessage.getFromUid(), CoreEnum.EChatType.PRIVATE, 1));
                ApplyBean applyBean1 = msgDao.getApplyBean(wrapMessage.getFromUid() + "");
                if (applyBean1 != null) {
                    applyBean1.setStat(2);
                    msgDao.applyFriend(applyBean1);
                }
                break;
            case REQUEST_FRIEND://请求添加为好友
                msgDao.remidCount("friend_apply");
                UserAction userAction = new UserAction();
                userAction.friendGet4Apply(new CallBack<ReturnBean<List<ApplyBean>>>() {
                    @Override
                    public void onResponse(Call<ReturnBean<List<ApplyBean>>> call, Response<ReturnBean<List<ApplyBean>>> response) {
                        if (response.body() == null || !response.body().isOk()) {
                            return;
                        }
                        List<ApplyBean> applyBeanList = response.body().getData();
                        for (int i = 0; i < applyBeanList.size(); i++) {
                            ApplyBean applyBean = applyBeanList.get(i);
                            applyBeanList.get(i).setAid(applyBean.getUid() + "");
                            applyBeanList.get(i).setChatType(CoreEnum.EChatType.PRIVATE);
                            if (!TextUtils.isEmpty(wrapMessage.getRequestFriend().getContactName())) {
                                applyBeanList.get(i).setAlias(wrapMessage.getRequestFriend().getContactName());
                            }
                            applyBeanList.get(i).setStat(1);
                            msgDao.applyFriend(applyBean);
                        }
                    }
                });
                notifyRefreshFriend(true, isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid(), CoreEnum.ERosterAction.REQUEST_FRIEND);
                break;
            case REMOVE_FRIEND:
                notifyRefreshFriend(true, isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid(), CoreEnum.ERosterAction.REMOVE_FRIEND);
                break;
            case CHANGE_GROUP_MASTER://转让群主
                if (bean != null) {
                    result = saveMessageNew(bean, isList);
                    refreshGroupInfo(bean.getGid());
                    hasNotified = true;
                }
                break;
            case OUT_GROUP://退出群聊，如果该群是已保存群聊，需要改为未保存
                if (wrapMessage.getFromUid() != UserAction.getMyId()) {//不是自己退群，才更新（自己退群，session信息已经被删除）
                    if (bean != null) {
                        if (msgDao.isMemberInCharge(wrapMessage.getGid(), UserAction.getMyId())) {
                            result = saveMessageNew(bean, isList);
                            hasNotified = true;
                        }
                        refreshGroupInfo(bean.getGid());
                    }
                } else {
                    MemberUser memberUser = userToMember(userBean, bean.getGid());
                    msgDao.removeGroupMember(bean.getGid(), memberUser);
                    notifyGroupChange(false);
                    hasNotified = true;
                }
                break;
            case REMOVE_GROUP_MEMBER://自己被移除群聊，如果该群是已保存群聊，需要改为未保存
                if (bean != null && !isFromSelf) {//除去自己PC端移除
                    result = saveMessageNew(bean, isList);
                    MemberUser memberUser = userToMember(userBean, bean.getGid());
                    msgDao.removeGroupMember(bean.getGid(), memberUser);
                    changeGroupAvatar(bean.getGid());
                    notifyGroupChange(false);
                    hasNotified = true;
                }
                break;
            case REMOVE_GROUP_MEMBER2://其他群成员被移除群聊，可能会有群主退群，涉及群主迭代,所以需要从服务器重新拉取数据
                removeGroupMember(wrapMessage);
                refreshGroupInfo(wrapMessage.getGid());
                notifyGroupChange(false);
                hasNotified = true;
                break;
            case ACCEPT_BE_GROUP://接受入群，
                if (bean != null) {
                    result = saveMessageNew(bean, isList);
                    updateGroupApply(wrapMessage);
                    refreshGroupInfo(bean.getGid());
                    hasNotified = true;
                }
                notifyGroupChange(true);
                break;
            case REQUEST_GROUP://群主会收到成员进群的请求的通知
                //自己邀请的，不需要显示
                if (UserAction.getMyId() != null && wrapMessage.getRequestGroup().getInviter() > 0 && wrapMessage.getRequestGroup().getInviter() == UserAction.getMyId().longValue()) {
                    return true;
                }
                for (MsgBean.GroupNoticeMessage ntm : wrapMessage.getRequestGroup().getNoticeMessageList()) {
                    ApplyBean applyBean = new ApplyBean();
                    applyBean.setAid(wrapMessage.getGid() + ntm.getUid());
                    applyBean.setChatType(CoreEnum.EChatType.GROUP);
                    applyBean.setGid(wrapMessage.getGid());
                    Realm realm = DaoUtil.open();
                    realm.beginTransaction();
                    Group group = realm.where(Group.class).equalTo("gid", wrapMessage.getGid()).findFirst();
                    if (group != null) {
                        if (StringUtil.isNotNull(group.getName())) {
                            applyBean.setGroupName(group.getName());
                        } else {
                            applyBean.setGroupName(msgDao.getGroupName(group));
                        }
                    }
                    realm.close();

                    applyBean.setJoinType(wrapMessage.getRequestGroup().getJoinType().getNumber());
                    applyBean.setInviter(wrapMessage.getRequestGroup().getInviter());
                    applyBean.setInviterName(wrapMessage.getRequestGroup().getInviterName());
                    applyBean.setUid(ntm.getUid());
                    applyBean.setNickname(ntm.getNickname());
                    applyBean.setAvatar(ntm.getAvatar());
                    applyBean.setStat(1);

                    msgDao.applyGroup(applyBean);
                }
                msgDao.remidCount("friend_apply");
                notifyRefreshFriend(true, -1L, CoreEnum.ERosterAction.DEFAULT);//刷新首页 通讯录底部小红点
                break;
            case CHANGE_GROUP_META://修改群属性
                MsgBean.ChangeGroupMetaMessage.RealMsgCase realMsgCase = wrapMessage.getChangeGroupMeta().getRealMsgCase();
                switch (realMsgCase) {
                    case NAME://群名
                        if (bean != null) {
                            result = saveMessageNew(bean, isList);
                        }
                        msgDao.groupNameUpadte(wrapMessage.getGid(), wrapMessage.getChangeGroupMeta().getName());
                        break;
                    case PROTECT_MEMBER://群成员保护
                        msgDao.groupContactIntimatelyUpdate(wrapMessage.getGid(), wrapMessage.getChangeGroupMeta().getProtectMember());
                        break;
                    case AVATAR://群头像
                        break;
                    case SHUT_UP:// 是否开启全群禁言
                        // 更新群禁言状态
                        if (bean != null) {
                            result = saveMessageNew(bean, isList);
                        }
                        break;
                    case SCREENSHOT_NOTIFICATION:
                        // 更新群截屏状态
                        if (bean != null) {
                            result = saveMessageNew(bean, isList);
                        }
                        msgDao.updateGroupSnapshot(wrapMessage.getGid(), wrapMessage.getChangeGroupMeta().getScreenshotNotification() ? 1 : 0);
                        notifySwitchSnapshot(wrapMessage.getGid(), 0, wrapMessage.getChangeGroupMeta().getScreenshotNotification() ? 1 : 0);
                        break;
                    case FORBBIDEN://封群
                        if (bean != null) {
                            result = saveMessageNew(bean, isList);
                        }
                        LogUtil.getLog().d(TAG, ">>>群状态改变---uid=" + wrapMessage.getFromUid() + "--isForbid=" + wrapMessage.getChangeGroupMeta().getForbbiden());
                        Group group = msgDao.updateGroupStatus(wrapMessage.getGid(), wrapMessage.getChangeGroupMeta().getForbbiden() ? ChatEnum.EGroupStatus.BANED : ChatEnum.EGroupStatus.NORMAL);
                        if (group != null) {
                            if (isChatAlive()) {
                                notifyGroupMetaChange(group);
                            }
                        }
                        break;
                }
                break;
            case DESTROY_GROUP://销毁群
                String groupName = wrapMessage.getDestroyGroup().getName();
                String icon = wrapMessage.getDestroyGroup().getAvatar();
                msgDao.groupExit(wrapMessage.getGid(), groupName, icon, 1);
                break;
            case FORCE_OFFLINE://强制退出，登录冲突
                EventLoginOut4Conflict eventLoginOut4Conflict = new EventLoginOut4Conflict();
                if (wrapMessage.getForceOffline().getForceOfflineReason() == MsgBean.ForceOfflineReason.CONFLICT) {// 登录冲突
                    String phone = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.PHONE).get4Json(String.class);
                    eventLoginOut4Conflict.setMsg("您的账号" + phone + "已经在另外一台设备上登录。如果不是您本人操作,请尽快修改密码");
                } else if (wrapMessage.getForceOffline().getForceOfflineReason() == MsgBean.ForceOfflineReason.LOCKED) {//被冻结
                    eventLoginOut4Conflict.setMsg("你已被限制登录");
                } else if (wrapMessage.getForceOffline().getForceOfflineReason() == MsgBean.ForceOfflineReason.PASSWORD_CHANGED) {//修改密码
                    eventLoginOut4Conflict.setMsg("您已成功重置密码，请使用新密码重新登录");
                } else if (wrapMessage.getForceOffline().getForceOfflineReason() == MsgBean.ForceOfflineReason.USER_DEACTIVATING) {//注销账号
                    eventLoginOut4Conflict.setMsg("工作人员将在30天内处理您的申请并删除账号下所有数据。在此期间，请不要登录常信。");
                } else if (wrapMessage.getForceOffline().getForceOfflineReason() == MsgBean.ForceOfflineReason.BOUND_PHONE_CHANGED) {//修改手机
                    eventLoginOut4Conflict.setMsg("更换绑定手机号成功，请新手机号重新登录");
                } else if (wrapMessage.getForceOffline().getForceOfflineReason() == MsgBean.ForceOfflineReason.APPEAL_PASS) {//申诉通过
                    eventLoginOut4Conflict.setMsg("您的账号申诉已通过，请重新登录以恢复功能使用");
                }
                EventBus.getDefault().post(eventLoginOut4Conflict);
                break;
            case AT://@消息
            case GROUP_ANNOUNCEMENT://群公告
                if (bean != null) {
                    result = saveMessageNew(bean, isList);
                    updateAtMessage(bean.getGid(), bean.getAtMessage().getAt_type(), bean.getAtMessage().getMsg(), bean.getAtMessage().getUid());
                }
                updateAtMessage(wrapMessage);
                break;
            case ACTIVE_STAT_CHANGE://在线状态改变
                UserInfo user = updateUserOnlineStatus(wrapMessage);
                if (user != null) {
                    notifyOnlineChange(user);
                }
                break;
            case CANCEL://撤销消息
                if (bean != null) {
                    String cancelMsgId = wrapMessage.getCancel().getMsgId();
                    if (isList) {
                        TaskDealWithMsgList task = getMsgTask(requestId);
                        if (task != null) {
                            Map<String, MsgAllBean> pendingMessages = task.getPendingMessagesMap();
                            Map<String, MsgAllBean> pendingCancelMessages = task.getPendingCancelMap();
                            if (pendingMessages != null && pendingCancelMessages != null) {
                                if (pendingMessages.containsKey(cancelMsgId)) {
                                    result = saveMessageNew(bean, isList);
                                    pendingCancelMessages.put(bean.getMsg_id(), bean);
                                    isCancelValid = true;
                                } else {
                                    MsgAllBean msgAllBean = msgDao.getMsgById(cancelMsgId);
                                    if (msgAllBean != null) {
                                        result = saveMessageNew(bean, isList);
                                        pendingCancelMessages.put(bean.getMsg_id(), bean);
                                        isCancelValid = true;
                                    }
                                }
                            }
                        }
                    } else {
                        //TODO:saveMessageNew的有更新未读数
                        // 判断消息是否存在，不存在则不保存
                        MsgAllBean msgAllBean = msgDao.getMsgById(cancelMsgId);
                        if (msgAllBean != null) {
                            result = saveMessageNew(bean, isList);
                            msgDao.msgDel4Cancel(wrapMessage.getMsgId(), cancelMsgId);
                            isCancelValid = true;
                        }
                    }
                    notifyRefreshChat(bean.getGid(), isFromSelf ? bean.getTo_uid() : bean.getFrom_uid());
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
                    MessageManager.getInstance().setMessageChange(true);
                }
                break;
            case RESOURCE_LOCK://资源锁定
                updateUserLockCloudRedEnvelope(wrapMessage);
                break;
            case CHANGE_SURVIVAL_TIME: //阅后即焚
                if (bean != null) {
                    result = saveMessageNew(bean, isList);
                }
                int survivalTime = wrapMessage.getChangeSurvivalTime().getSurvivalTime();
                if (!TextUtils.isEmpty(wrapMessage.getGid())) {
                    userDao.updateGroupReadDestroy(wrapMessage.getGid(), survivalTime);
                } else {
                    userDao.updateReadDestroy(isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid(), survivalTime);
                }
                EventBus.getDefault().post(new ReadDestroyBean(survivalTime, wrapMessage.getGid(), wrapMessage.getFromUid()));
                break;
            case READ://已读消息
                long uids = isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid();
                if (!isFromSelf) {
                    if (TextUtils.isEmpty(wrapMessage.getGid())) {//单聊
                        msgDao.setUpdateRead(uids, wrapMessage.getTimestamp());
                        //自己PC端发送给好友的消息，有离线消息，则保存先,离线消息处理完之后，进行再次更新
                        TaskDealWithMsgList task = getMsgTask(bean.getRequest_id());
                        if (task != null && (task.getPendingMessagesMap().size() > 0)) {
                            //保存消息信息
                            offlineFriendReadMsg.put(uids, wrapMessage.getTimestamp());
                        }
                    }
                }

                LogUtil.getLog().d(TAG, "已读消息:" + wrapMessage.getTimestamp());
                if (isFromSelf) {//自己PC端已读，则清除未读消息
                    String gid = wrapMessage.getGid();
                    gid = gid == null ? "" : gid;
                    msgDao.sessionReadCleanAndToBurn(gid, uids, wrapMessage.getTimestamp());
                    //有离线消息，则保存先,离线消息处理完之后，进行再次更新
                    TaskDealWithMsgList task = getMsgTask(bean.getRequest_id());
                    if (task != null && (task.getPendingMessagesMap().size() > 0
                            || task.getPendingGroupUnreadMap().size() > 0 || task.getPendingUserUnreadMap().size() > 0)) {
                        //清除队列未读数量
                        clearPendingSessionUnreadCount(gid, uids, bean.getRequest_id());
                        //保存消息信息
                        if (TextUtils.isEmpty(gid))
                            offlineFriendReadMsg.put(uids, wrapMessage.getTimestamp());
                        else
                            offlineGroupReadMsg.put(gid, wrapMessage.getTimestamp());
                    }
                }
                notifyRefreshChat(wrapMessage.getGid(), uids);
                break;
            case SWITCH_CHANGE: //开关变更
                // TODO　处理老版本不兼容问题
                if (wrapMessage.getSwitchChange().getSwitchType() == MsgBean.SwitchChangeMessage.SwitchType.UNRECOGNIZED) {
                    return true;
                }
                LogUtil.getLog().d(TAG, "开关变更:" + wrapMessage.getSwitchChange().getSwitchType());

                int switchType = wrapMessage.getSwitchChange().getSwitchType().getNumber();
                int switchValue = wrapMessage.getSwitchChange().getSwitchValue();
                long uid = isFromSelf ? wrapMessage.getToUid() : wrapMessage.getFromUid();
                UserInfo userInfo = userDao.findUserInfo(uid);
                if (userInfo == null) {
                    break;
                }
                switch (switchType) {
                    case ChatEnum.ESwitchType.READ: // 单聊已读
                        userInfo.setFriendRead(switchValue);
                        userDao.updateUserinfo(userInfo);
                        EventBus.getDefault().post(new EventIsShowRead(uid, EventIsShowRead.EReadSwitchType.SWITCH_FRIEND, switchValue));
                        break;
                    case ChatEnum.ESwitchType.VIP: //vip
                        if (userBean != null) {
                            userBean.setVip(wrapMessage.getSwitchChange().getSwitchValue() + "");
                            userDao.updateUserBean(userBean);
                        }
                        // 刷新用户信息
                        EventFactory.FreshUserStateEvent event = new EventFactory.FreshUserStateEvent();
                        event.vip = wrapMessage.getSwitchChange().getSwitchValue() + "";
                        EventBus.getDefault().post(event);
                        break;
                    case ChatEnum.ESwitchType.MASTER_READ:  //已读总开关
                        userInfo.setMasterRead(switchValue);
                        userDao.updateUserinfo(userInfo);
                        EventBus.getDefault().post(new EventIsShowRead(uid, EventIsShowRead.EReadSwitchType.SWITCH_MASTER, switchValue));
                        break;
                    case ChatEnum.ESwitchType.SHUT_UP: // 单人禁言
                    case ChatEnum.ESwitchType.OPEN_UP_RED_ENVELOPER: // 领取群红包
                        if (bean != null) {
                            result = saveMessageNew(bean, isList);
                        }
                        break;
                    case ChatEnum.ESwitchType.SCREENSHOT: // 截屏通知开关
                        if (bean != null) {
                            result = saveMessageNew(bean, isList);
                        }
                        userDao.updateUserSnapshot(wrapMessage.getFromUid(), switchValue);
                        notifySwitchSnapshot("", wrapMessage.getFromUid(), switchValue);
                        break;
                }
                break;

            case P2P_AU_VIDEO_DIAL:// 音视频通知
                break;
            case PAY_RESULT://支付结果
                MsgBean.PayResultMessage payResult = wrapMessage.getPayResult();
                System.out.println(TAG + "--支付结果=" + payResult.getResult());
                notifyPayResult(payResult);
                break;
            case TRANSFER://转账消息
                if (bean != null) {
                    MsgBean.TransferMessage transferMessage = wrapMessage.getTransfer();
                    if (transferMessage != null) {
                        //领取或退还转账,先更新历史转账消息状态，后存消息
                        if (transferMessage.getOpType() == MsgBean.TransferMessage.OpType.RECEIVE || transferMessage.getOpType() == MsgBean.TransferMessage.OpType.REJECT) {
                            msgDao.updateTransferStatus(transferMessage.getId(), transferMessage.getOpTypeValue(), bean.getFrom_uid());
                        }
                    }
                    result = saveMessageNew(bean, isList);
                }
                break;
            case REPLY_SPECIFIC:// 回复消息
                if (bean != null) {
                    result = saveMessageNew(bean, isList);
                    if (!TextUtils.isEmpty(bean.getGid()) && bean.getReplyMessage() != null && bean.getReplyMessage().getAtMessage() != null) {
                        AtMessage atMessage = bean.getReplyMessage().getAtMessage();
                        updateAtMessage(bean.getGid(), atMessage.getAt_type(), atMessage.getMsg(), atMessage.getUid());
                    }
                }
                break;
            case MULTI_TERMINAL_SYNC:// PC端同步 更改信息，只同步自己的操作
                userAction = new UserAction();
                switch (wrapMessage.getMultiTerminalSync().getSyncType()) {
                    case MY_SELF_CHANGED://自己的个人信息变更
                        userAction.getMyInfo4Web(UserAction.getMyId(), null, null);
                        break;
                    case MY_FRIEND_CHANGED://更改我的好友信息（备注名等）
                        userAction.updateUserInfo4Id(wrapMessage.getMultiTerminalSync().getUid(), new CallBack<ReturnBean<UserInfo>>() {
                            @Override
                            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
                                super.onResponse(call, response);
                                if (response.body().isOk() && response.body().getData() != null) {
                                    UserInfo user = response.body().getData();
                                    if (user != null)//更新session设置
                                        msgDao.updateSessionTopAndDisturb(null, user.getUid(), user.getIstop(), user.getDisturb());
                                    /********通知更新sessionDetail************************************/
                                    List<Long> fUids = new ArrayList<>();
                                    fUids.add(wrapMessage.getMultiTerminalSync().getUid());
                                    //回主线程调用更新session详情
                                    if (MyAppLication.INSTANCE().repository != null)
                                        MyAppLication.INSTANCE().repository.updateSessionDetail(null, fUids);
                                    /********通知更新sessionDetail end************************************/
                                }
                            }
                        });

                        break;
                    case MY_GROUP_CHANGED://更改我所在的群信息变更（备注名等）
                        MsgAction msgAction = new MsgAction();
                        String gid = wrapMessage.getMultiTerminalSync().getGid();
                        msgAction.groupInfo(gid, false, new CallBack<ReturnBean<Group>>() {
                            @Override
                            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                                super.onResponse(call, response);
                                if (response.body().isOk() && response.body().getData() != null) {
                                    Group group = response.body().getData();
                                    if (group != null)//更新session设置
                                        msgDao.updateSessionTopAndDisturb(gid, null, group.getIsTop(), group.getNotNotify());
                                    //通知更新UI
                                    notifyGroupChange(gid);

                                    /********通知更新sessionDetail************************************/
                                    List<String> gids = new ArrayList<>();
                                    if (!TextUtils.isEmpty(gid)) {
                                        gids.add(gid);
                                    }
                                    //回主线程调用更新session详情
                                    if (MyAppLication.INSTANCE().repository != null)
                                        MyAppLication.INSTANCE().repository.updateSessionDetail(gids, null);
                                    /********通知更新sessionDetail end************************************/
                                }

                            }
                        });

                        break;
                    case MY_GROUP_QUIT://自己退群
                        gid = wrapMessage.getMultiTerminalSync().getGid();
                        //回主线程调用更新session详情
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (MyAppLication.INSTANCE().repository != null)
                                    MyAppLication.INSTANCE().repository.deleteSession(null, gid);
                            }
                        });
                        //删除群成员及秀阿贵群保存逻辑
                        MemberUser memberUser = MessageManager.getInstance().userToMember(UserAction.getMyInfo(), gid);
                        msgDao.removeGroupMember(gid, memberUser);
                        EventBus.getDefault().post(new EventExitChat(gid, null));
                        break;
                    case MY_FRIEND_DELETED://删除好友
                        uid = wrapMessage.getMultiTerminalSync().getUid();
                        //删除好友后 取消阅后即焚状态
                        userDao.updateReadDestroy(uid, 0);
                        // 删除好友后，取消置顶状态
                        msgDao.updateUserSessionTop(uid, 0);
                        //回主线程调用更新session详情
                        mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (MyAppLication.INSTANCE().repository != null)
                                    MyAppLication.INSTANCE().repository.deleteSession(uid, "");
                            }
                        });
                        MessageManager.getInstance().setMessageChange(true);
                        EventRefreshFriend eventRefreshFriend = new EventRefreshFriend();
                        eventRefreshFriend.setLocal(true);
                        eventRefreshFriend.setUid(uid);
                        eventRefreshFriend.setRosterAction(CoreEnum.ERosterAction.REMOVE_FRIEND);
                        EventBus.getDefault().post(eventRefreshFriend);
                        EventBus.getDefault().post(new EventExitChat(null, uid));
                        break;
                }
                break;
        }
        //刷新单个,接收到音视频通话消息不需要刷新
        if (result && !hasNotified && !isList && bean != null && wrapMessage.getMsgType() != P2P_AU_VIDEO_DIAL) {
            setMessageChange(true);
            boolean isGroup = isGroup(wrapMessage.getFromUid(), bean.getGid());
            long chatterId = wrapMessage.getFromUid();
            if (!isGroup && isFromSelf) {
                chatterId = wrapMessage.getToUid();
            }
        }

        //记录批量信息来源
        TaskDealWithMsgList taskMsgList = null;
        if (bean != null) {
            taskMsgList = getMsgTask(bean.getRequest_id());
        }
        if (isList && taskMsgList != null) {
            String gid = wrapMessage.getGid();
            if (TextUtils.isEmpty(gid) && bean != null) {
                gid = bean.getGid();
            }
            if (isGroup(wrapMessage.getFromUid(), gid)) {
                taskMsgList.addGid(gid);
            } else {
                taskMsgList.addUid(wrapMessage.getFromUid());

            }
        }
        if (!isFromSelf) {
            checkNotifyVoice(wrapMessage, isList, canNotify);
        }
        return result;
    }

    /**
     * 已过期
     * 清除双向删除消息
     */
    @Deprecated
    public void clearHistoryMsg() {
        if (historyCleanMsg.size() > 0) {
            for (Long key : historyCleanMsg.keySet()) {
                msgDao.msgDel(key, historyCleanMsg.get(key));
            }
        }
    }

    /**
     * 已过期
     * 更新离线接收自己PC端发送的已读消息
     */
    @Deprecated
    public void updateOfflineReadMsg() {
        Realm realm = DaoUtil.open();
        try {
            for (String gid : offlineGroupReadMsg.keySet()) {
                long timestamp = offlineGroupReadMsg.get(gid);
                //查出已读前的消息，设置为已读
                RealmResults<MsgAllBean> msgAllBeans = realm.where(MsgAllBean.class).equalTo("gid", gid)
                        .lessThanOrEqualTo("timestamp", timestamp)
                        .equalTo("isRead", false)
                        .findAll();
                realm.beginTransaction();
                for (MsgAllBean msgAllBean : msgAllBeans) {
                    long endTime = timestamp + msgAllBean.getSurvival_time() * 1000;
                    if (msgAllBean.getSurvival_time() > 0 && msgAllBean.getEndTime() <= 0) {//有设置阅后即焚
                        msgAllBean.setRead(true);
                        msgAllBean.setReadTime(timestamp);
                        /**处理需要阅后即焚的消息***********************************/
                        msgAllBean.setStartTime(timestamp);
                        msgAllBean.setEndTime(endTime);
                    } else {//普通消息，记录已读状态和时间
                        msgAllBean.setRead(true);
                        msgAllBean.setReadTime(timestamp);
                    }
                }
                realm.commitTransaction();
            }

            for (Long uid : offlineFriendReadMsg.keySet()) {
                long timestamp = offlineFriendReadMsg.get(uid);
                //查出已读前的消息，设置为已读,好友发送的消息
                RealmResults<MsgAllBean> msgAllBeans = realm.where(MsgAllBean.class)
                        .beginGroup().isEmpty("gid").or().isNull("gid").endGroup()
                        .equalTo("from_uid", uid)
                        .lessThanOrEqualTo("timestamp", timestamp)
                        .equalTo("isRead", false)
                        .findAll();
                realm.beginTransaction();
                for (MsgAllBean msgAllBean : msgAllBeans) {
                    long endTime = timestamp + msgAllBean.getSurvival_time() * 1000;
                    if (msgAllBean.getSurvival_time() > 0 && msgAllBean.getEndTime() <= 0) {//有设置阅后即焚
                        msgAllBean.setRead(true);//自己已读
                        msgAllBean.setReadTime(timestamp);
                        /**处理需要阅后即焚的消息***********************************/
                        msgAllBean.setStartTime(timestamp);
                        msgAllBean.setEndTime(endTime);
                    } else {//普通消息，记录已读状态和时间
                        msgAllBean.setRead(true);//自己已读
                        msgAllBean.setReadTime(timestamp);
                    }
                }
                realm.commitTransaction();
            }
            offlineGroupReadMsg.clear();
            offlineFriendReadMsg.clear();
        } catch (Exception e) {
        } finally {
            DaoUtil.close(realm);
        }
    }

    private void updateGroupApply(MsgBean.UniversalMessage.WrapMessage wrapMessage) {
        //被邀请进群，表示已经同意了
        if (wrapMessage.getAcceptBeGroup() != null) {
            List<MsgBean.GroupNoticeMessage> noticeMessageList = wrapMessage.getAcceptBeGroup().getNoticeMessageList();
            if (noticeMessageList != null && noticeMessageList.size() > 0) {
                for (int i = 0; i < noticeMessageList.size(); i++) {
                    MsgBean.GroupNoticeMessage message = noticeMessageList.get(i);
                    long uid = message.getUid();
                    msgDao.updateNewApply(wrapMessage.getGid(), uid, 2);
                }
            }
        }
    }

    private void notifyOnlineChange(UserInfo info) {
        EventUserOnlineChange event = new EventUserOnlineChange();
        event.setObject(info);
        EventBus.getDefault().post(event);

    }

    //重新生成群头像
    public void changeGroupAvatar(String gid) {
        //撤回消息更新session详情
        List<String> gids = new ArrayList<>();
        if (!TextUtils.isEmpty(gid))
            gids.add(gid);
        //回主线程调用更新sessionDetial
        if (MyAppLication.INSTANCE().repository != null)
            MyAppLication.INSTANCE().repository.updateSessionDetail(gids, null);
    }

    private void removeGroupMember(MsgBean.UniversalMessage.WrapMessage wrapMessage) {
        MsgBean.RemoveGroupMember2Message removeGroupMember2 = wrapMessage.getRemoveGroupMember2();
        msgDao.removeGroupMember(wrapMessage.getGid(), removeGroupMember2.getUidList());
    }

    private boolean isGroup(Long uid, String gid) {
        if (!TextUtils.isEmpty(gid)) {
            return true;
        }
        return false;
    }

    /*
     * 更新用户红包锁定功能
     * */
    private void updateUserLockCloudRedEnvelope(MsgBean.UniversalMessage.WrapMessage wrapMessage) {
        MsgBean.ResourceLockMessage lock = wrapMessage.getResourceLock();
        if (lock != null) {
            MsgBean.ResourceLockMessage.ResourceLockType type = lock.getResourceLockType();
            switch (type) {
                case CLOUDREDENVELOPE:
//                    UserDao userDao = new UserDao();
//                    userDao.updateUserLockRedEnvelope(UserAction.getMyId(), lock.getLock());
                    UserBean info = (UserBean) UserAction.getMyInfo();
                    if (info != null) {
                        info.setLockCloudRedEnvelope(lock.getLock());
                        userDao.updateUserBean(info);
                    }
                    break;
            }
        }
    }

    /**
     * 已过期  请使用MessageRepository中的saveMessageNew
     *
     * @param msgAllBean
     * @param isList
     * @return 已过期  请使用MessageRepository中的saveMessageNew
     */
    @Deprecated
    private boolean saveMessageNew(MsgAllBean msgAllBean, boolean isList) {
        boolean result = false;
        boolean isFromSelf = false;
        if (UserAction.getMyId() != null) {
            isFromSelf = msgAllBean.getFrom_uid() == UserAction.getMyId().intValue();
        }

        Long uid = msgAllBean.getFrom_uid();
        if (isFromSelf) {
            uid = msgAllBean.getTo_uid();
            if (!TextUtils.isEmpty(msgAllBean.getGid()) && msgAllBean.getSurvival_time() > 0) {//自己PC端发送的群聊消息，阅后即焚消息，立即加入
                msgAllBean.setStartTime(msgAllBean.getTimestamp());
                msgAllBean.setEndTime(msgAllBean.getTimestamp() + (msgAllBean.getSurvival_time() * 1000));
            }
        }

        try {
            msgAllBean.setTo_uid(msgAllBean.getTo_uid());
            //收到直接存表
            if (isList) {
                TaskDealWithMsgList task = getMsgTask(msgAllBean.getRequest_id());
                if (task != null) {
                    task.getPendingMessagesMap().put(msgAllBean.getMsg_id(), msgAllBean);
                }
            } else {
                DaoUtil.update(msgAllBean);
                if (isMsgFromCurrentChat(msgAllBean.getGid(), isFromSelf ? msgAllBean.getTo_uid() : msgAllBean.getFrom_uid())) {
                    notifyRefreshChat(msgAllBean, CoreEnum.ERefreshType.ADD);
                }
            }
            boolean isCancel = msgAllBean.getMsg_type() == ChatEnum.EMessageType.MSG_CANCEL;
            if (!TextUtils.isEmpty(msgAllBean.getGid()) && !msgDao.isGroupExist(msgAllBean.getGid())) {
                if (!loadGids.contains(msgAllBean.getGid())) {
                    loadGids.add(msgAllBean.getGid());
                    loadGroupInfo(msgAllBean.getGid(), msgAllBean.getFrom_uid(), isList, msgAllBean, isFromSelf);
                } else {
                    if (!isList) {
                        //非自己发过来的消息，才存储为未读状态
                        if (!isFromSelf)
                            updateSessionUnread(msgAllBean.getGid(), msgAllBean.getFrom_uid(), msgAllBean, null, isFromSelf);
                        setMessageChange(true);
                    } else {
                        updatePendingSessionUnreadCount(msgAllBean.getGid(), msgAllBean.getFrom_uid(), false, isCancel, msgAllBean.getRequest_id(), isFromSelf);
                    }
                    result = true;
                }
            } else if (TextUtils.isEmpty(msgAllBean.getGid()) && uid != null && uid > 0 && !userDao.isUserExist(uid)) {//单聊
                long chatterId = -1;//对方的Id
                if (isFromSelf) {
                    chatterId = msgAllBean.getTo_uid();
                } else {
                    chatterId = msgAllBean.getFrom_uid();
                }
                if (!loadUids.contains(chatterId)) {
                    loadUids.add(chatterId);
                    loadUserInfo(msgAllBean.getGid(), chatterId, isList, msgAllBean, isFromSelf);
                    LogUtil.getLog().d("a=", TAG + "--需要加载用户信息");
                } else {
                    LogUtil.getLog().d("a=", TAG + "--异步加载用户信息更新未读数");
                    if (!isList) {
                        if (!isFromSelf)
                            updateSessionUnread(msgAllBean.getGid(), chatterId, msgAllBean, null, isFromSelf);
                        setMessageChange(true);
                    } else {
                        updatePendingSessionUnreadCount(msgAllBean.getGid(), chatterId, false, isCancel, msgAllBean.getRequest_id(), isFromSelf);
                    }
                    result = true;
                }
            } else {
                if (!TextUtils.isEmpty(msgAllBean.getGid())) {
                    if (!isList) {
                        if (!isFromSelf)
                            updateSessionUnread(msgAllBean.getGid(), msgAllBean.getFrom_uid(), msgAllBean, null, isFromSelf);
                        setMessageChange(true);
                    } else {
                        updatePendingSessionUnreadCount(msgAllBean.getGid(), msgAllBean.getFrom_uid(), false, isCancel, msgAllBean.getRequest_id(), isFromSelf);
                    }
                } else {
                    long chatterId = isFromSelf ? msgAllBean.getTo_uid() : msgAllBean.getFrom_uid();
                    if (!isList) {
                        if (!isFromSelf)
                            updateSessionUnread(msgAllBean.getGid(), chatterId, msgAllBean, null, isFromSelf);
                        setMessageChange(true);
                    } else {
                        updatePendingSessionUnreadCount(msgAllBean.getGid(), chatterId, false, isCancel, msgAllBean.getRequest_id(), isFromSelf);
                    }
                }
                result = true;
            }
        } catch (
                Exception e) {
            e.printStackTrace();
            LogUtil.getLog().d("a=", TAG + "--消息存储失败--msgId=" + msgAllBean.getMsg_id() + "--msgType=" + msgAllBean.getMsg_type());
        }
        if (isFromSelf) {
            //自己PC 端发的消息刷新session
            /********通知更新或创建session ************************************/
            msgDao.updateFromSelfPCSession(msgAllBean);
            /********通知更新或创建session end************************************/
        }

        return result;
    }

    /*已过期
     * 网络加载用户信息,只能接受来自好友的信息
     * */
    @Deprecated
    private synchronized void loadUserInfo(final String gid, final Long uid, boolean isList, MsgAllBean bean, boolean isFromSelf) {
        if (UserAction.getMyId() != null && uid.equals(UserAction.getMyId())) {
            return;
        }
        new UserAction().getUserInfoAndSave(uid, ChatEnum.EUserType.FRIEND, new CallBack<ReturnBean<UserInfo>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
//                updateSessionUnread(gid, uid, false);
                if (isList) {
                    UserInfo user = response.body().getData();
                    boolean isDisturb = false;
                    if (user != null) {
                        isDisturb = user.getDisturb() == 1;
                    }
                    updatePendingSessionUnreadCount(gid, uid, isDisturb, false, bean.getRequest_id(), isFromSelf);
                    TaskDealWithMsgList taskMsgList = getMsgTask(bean.getRequest_id());
                    if (taskMsgList != null) {
                        taskMsgList.updateTaskCount();
                    }
                } else {
                    updateSessionUnread(gid, uid, bean, null, isFromSelf);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<UserInfo>> call, Throwable t) {
                super.onFailure(call, t);
                if (isList) {
                    updatePendingSessionUnreadCount(gid, uid, false, false, bean.getRequest_id(), isFromSelf);
                    TaskDealWithMsgList taskMsgList = getMsgTask(bean.getRequest_id());
                    if (taskMsgList != null) {
                        taskMsgList.updateTaskCount();
                    }
                } else {
                    updateSessionUnread(gid, uid, bean, null, isFromSelf);
                }
            }
        });
    }

    /*已过期
     * 网络加载群信息
     * */
    @Deprecated
    private void loadGroupInfo(final String gid, final long uid, boolean isList, MsgAllBean bean, boolean isFromSelf) {
        if (TextUtils.isEmpty(gid)) {
            return;
        }
        new MsgAction().groupInfo(gid, false, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                super.onResponse(call, response);
                if (isList) {
                    Group group = response.body().getData();
                    boolean isDisturb = false;
                    if (group != null) {
                        isDisturb = group.getNotNotify() == 1;
                    }
                    updatePendingSessionUnreadCount(gid, uid, isDisturb, false, bean.getRequest_id(), isFromSelf);
                    TaskDealWithMsgList taskMsgList = getMsgTask(bean.getRequest_id());
                    if (taskMsgList != null) {
                        taskMsgList.updateTaskCount();
                    }
                } else {
                    updateSessionUnread(gid, uid, bean, "first", isFromSelf);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<Group>> call, Throwable t) {
//                super.onFailure(call, t);
                if (isList) {
                    updatePendingSessionUnreadCount(gid, uid, false, false, bean.getRequest_id(), isFromSelf);
                    TaskDealWithMsgList taskMsgList = getMsgTask(bean.getRequest_id());
                    if (taskMsgList != null) {
                        taskMsgList.updateTaskCount();
                    }
                } else {
                    updateSessionUnread(gid, uid, bean, null, isFromSelf);
                    notifyRefreshMsg(CoreEnum.EChatType.GROUP, uid, gid, CoreEnum.ESessionRefreshTag.SINGLE, bean);
                }
            }
        });
    }

    public boolean isMessageChange() {
        return isMessageChange;
    }

    public void setMessageChange(boolean isChange) {
        this.isMessageChange = isChange;
    }

    /*
     * 更新session未读数
     * */
    @Deprecated
    public synchronized boolean updateSessionUnread(String gid, Long from_uid, MsgAllBean bean, String firstFlag, boolean isFromSelf) {
//        LogUtil.getLog().d("a=", TAG + "--更新Session--updateSessionUnread" + "--isCancel=" + isCancel);
        boolean canChangeUnread = true;
        if (!TextUtils.isEmpty(gid)) {
            if (!TextUtils.isEmpty(SESSION_GID) && SESSION_GID.equals(gid)) {
                canChangeUnread = false;
            }
        } else {
            if (SESSION_FUID != null && from_uid != null && SESSION_FUID.equals(from_uid)) {
                canChangeUnread = false;
            }

        }
        if (isFromSelf) canChangeUnread = false;
        return msgDao.sessionReadUpdate(gid, from_uid, canChangeUnread, bean, firstFlag);
    }

    /*
     * 更新session未读数
     * */
    @Deprecated
    public synchronized void updateSessionUnread(String gid, Long from_uid, int count) {
//        LogUtil.getLog().d("a=", TAG + "--更新Session--updateSessionUnread--gid=" + gid + "--uid=" + from_uid + "--count=" + count);
        msgDao.sessionReadUpdate(gid, from_uid, count);
    }

    /*接收自己PC端离线消息已读消息
     * 清除session未读数
     */
    @Deprecated
    public synchronized void clearPendingSessionUnreadCount(String gid, Long uid, String requestId) {
        if (TextUtils.isEmpty(requestId)) {
            return;
        }
        TaskDealWithMsgList task = getMsgTask(requestId);
        if (task == null) {
            return;
        }
        if (TextUtils.isEmpty(gid)) {//单聊
            Map<Long, Integer> pendingUserUnread = task.getPendingUserUnreadMap();
            pendingUserUnread.put(uid, 0);
        } else {
            Map<String, Integer> pendingGroupUnread = task.getPendingGroupUnreadMap();
            pendingGroupUnread.put(gid, 0);
        }

    }

    /*
     * @param isDisturb 是否免打扰
     * @param isCancel 是否是撤销消息
     * */
    @Deprecated
    public synchronized void updatePendingSessionUnreadCount(String gid, Long uid, boolean isDisturb, boolean isCancel, String requestId, boolean isFromSelf) {
//        LogUtil.getLog().d("a=", TAG + "--更新Session--updatePendingSessionUnreadCount--gid=" + gid + "--uid=" + uid + "--isCancel=" + isCancel);
        if (TextUtils.isEmpty(requestId)) {
            return;
        }
        TaskDealWithMsgList task = getMsgTask(requestId);
        if (task == null) {
            return;
        }
        Map<String, Integer> pendingGroupUnread = task.getPendingGroupUnreadMap();
        Map<Long, Integer> pendingUserUnread = task.getPendingUserUnreadMap();
        if (isCancel) {
            if (!TextUtils.isEmpty(gid)) {
                if (pendingGroupUnread.containsKey(gid)) {
                    int count = pendingGroupUnread.get(gid);
                    if (isDisturb) {
                        count = 0;
                        pendingGroupUnread.put(gid, count);
                    } else {
                        count--;
                        pendingGroupUnread.put(gid, count);
                    }
                } else {
                    pendingGroupUnread.put(gid, -1);
                }
            } else {
                if (pendingUserUnread.containsKey(uid)) {
                    int count = pendingUserUnread.get(uid);
                    if (isDisturb) {
                        count = 0;
                        pendingUserUnread.put(uid, count);
                    } else {
                        count--;
                        pendingUserUnread.put(uid, count);
                    }
                } else {
                    pendingUserUnread.put(uid, -1);
                }
            }
        } else {

            if (!TextUtils.isEmpty(gid)) {
                if (pendingGroupUnread.containsKey(gid)) {
                    int count = pendingGroupUnread.get(gid);
                    if (isDisturb) {
                        count = 0;
                        pendingGroupUnread.put(gid, count);
                    } else {
                        if (!isFromSelf && (TextUtils.isEmpty(SESSION_GID) || !SESSION_GID.equals(gid))) {//不是当前会话
                            count++;
                        }
                        pendingGroupUnread.put(gid, count);
                    }
                } else {
                    int count = 0;
                    if (!isFromSelf && (TextUtils.isEmpty(SESSION_GID) || !SESSION_GID.equals(gid))) {//不是当前会话
                        count = isDisturb ? 0 : 1;
                    }
                    pendingGroupUnread.put(gid, count);

                }
            } else {
                if (pendingUserUnread.containsKey(uid)) {
                    int count = pendingUserUnread.get(uid);
                    if (isDisturb) {
                        count = 0;
                        pendingUserUnread.put(uid, count);
                    } else {
                        if (!isFromSelf && (SESSION_FUID == null || !SESSION_FUID.equals(uid))) {//不是当前会话
                            count++;
                        }
                        pendingUserUnread.put(uid, count);
                    }
                } else {
                    int count = 0;
                    if (!isFromSelf && (SESSION_FUID == null || !SESSION_FUID.equals(uid))) {//不是当前会话
                        count = isDisturb ? 0 : 1;
                    }
                    pendingUserUnread.put(uid, count);
                }
            }

        }
    }

    /*
     * 通知刷新消息列表，及未读数，未设置及整体刷新
     * */
    public void notifyRefreshMsg() {
        EventBus.getDefault().post(new EventRefreshMainMsg());
    }


    /*
     * 通知刷新消息列表，及未读数
     * @param chatType 单聊群聊
     * @param uid 单聊即用户id，群聊为null
     * @param gid 群聊即群id，单聊为""
     * @param msg,最后一条消息，也要刷新时间
     * */
    public void notifyRefreshMsg(@CoreEnum.EChatType int chatType, Long uid, String gid, @CoreEnum.ESessionRefreshTag int refreshTag, Object object) {
        setMessageChange(true);
        EventRefreshMainMsg eventRefreshMainMsg = new EventRefreshMainMsg();
        eventRefreshMainMsg.setType(chatType);
        eventRefreshMainMsg.setUid(uid);
        eventRefreshMainMsg.setGid(gid);
        eventRefreshMainMsg.setRefreshTag(refreshTag);
        if (object != null) {
            if (object instanceof MsgAllBean) {
                eventRefreshMainMsg.setMsgAllBean((MsgAllBean) object);
            } else if (object instanceof Session) {
                eventRefreshMainMsg.setSession((Session) object);

            }
        }
        EventBus.getDefault().post(eventRefreshMainMsg);
    }

    /**
     * 通过sid 刷新某个session
     *
     * @param chatType
     * @param sid
     * @param refreshTag
     */
    public void notifyRefreshMsg(@CoreEnum.EChatType int chatType, String sid, @CoreEnum.ESessionRefreshTag int refreshTag) {
        setMessageChange(true);
        EventRefreshMainMsg eventRefreshMainMsg = new EventRefreshMainMsg();
        eventRefreshMainMsg.setType(chatType);
        eventRefreshMainMsg.setSid(sid);
        eventRefreshMainMsg.setRefreshTag(refreshTag);
        EventBus.getDefault().post(eventRefreshMainMsg);
    }

    /*
     * 通知刷新消息列表，及未读数
     * @param chatType 单聊群聊
     * @param uid 单聊即用户id，群聊为null
     * @param gid 群聊即群id，单聊为""
     * @param msg,最后一条消息，也要刷新时间
     * */
    public void notifyRefreshMsg(@CoreEnum.EChatType int chatType, Long uid, String gid, @CoreEnum.ESessionRefreshTag int refreshTag, Object object, boolean isRefreshTop) {
        EventRefreshMainMsg eventRefreshMainMsg = new EventRefreshMainMsg();
        eventRefreshMainMsg.setType(chatType);
        eventRefreshMainMsg.setUid(uid);
        eventRefreshMainMsg.setGid(gid);
        eventRefreshMainMsg.setRefreshTag(refreshTag);
        eventRefreshMainMsg.setRefreshTop(isRefreshTop);
        if (object != null) {
            if (object instanceof MsgAllBean) {
                eventRefreshMainMsg.setMsgAllBean((MsgAllBean) object);
            } else if (object instanceof Session) {
                eventRefreshMainMsg.setSession((Session) object);

            }
        }
        EventBus.getDefault().post(eventRefreshMainMsg);
    }

    public void notifySwitchDisturb() {
        EventBus.getDefault().post(new EventSwitchDisturb());

    }

    /*
     * 通知刷新聊天界面
     * */
    public void notifyRefreshChat(String gid, Long uid) {
        if (!isMsgFromCurrentChat(gid, uid)) {
            return;
        }
        EventRefreshChat event = new EventRefreshChat();
        EventBus.getDefault().post(event);
    }

    /*
     * 通知刷新聊天界面
     * */
    public void notifyRefreshChat() {
        EventRefreshChat event = new EventRefreshChat();
        EventBus.getDefault().post(event);
    }

    /*
     * 刷新通讯录
     * @param isLocal 是否是本地刷新
     * @param uid 需要刷新的用户id
     * @param action 花名册操作类型
     * */
    public void notifyRefreshFriend(boolean isLocal, long uid, @CoreEnum.ERosterAction int action) {
        EventRefreshFriend event = new EventRefreshFriend();
        event.setLocal(isLocal);
        if (action != CoreEnum.ERosterAction.DEFAULT) {
            event.setUid(uid);
            event.setRosterAction(action);
        }
        EventBus.getDefault().post(event);
    }


    public void notifyReportGeo() {
        EventBus.getDefault().post(new EventReportGeo());
    }

    /*
     * 更新用户头像和昵称
     * */
    @Deprecated
    public boolean updateUserAvatarAndNick(long uid, String avatar, String nickName) {
        boolean hasChange = userDao.userHeadNameUpdate(uid, avatar, nickName);
        return hasChange;
    }


    //检测是否是双重消息，及一条消息需要产生两条本地消息记录,回执在通知消息中发送,招呼语消息要在好友通知前面
    private void checkDoubleMessage(MsgBean.UniversalMessage.WrapMessage wmsg) {
        if (wmsg.getMsgType() == ACCEPT_BE_FRIENDS) {
            MsgBean.AcceptBeFriendsMessage receiveMessage = wmsg.getAcceptBeFriends();
            if (receiveMessage != null && !TextUtils.isEmpty(receiveMessage.getSayHi())) {
                ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), receiveMessage.getSayHi());
                MsgAllBean message = createMsgBean(wmsg, ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.NORMAL, wmsg.getTimestamp() - 1, chatMessage);
                DaoUtil.save(message);
            }
        }
    }

    @Deprecated
    private boolean updateAtMessage(MsgBean.UniversalMessage.WrapMessage msg) {
        boolean isAt = false;
        String gid = msg.getGid();
        MsgBean.AtMessage atMessage = msg.getAt();
        String message = atMessage.getMsg();
        int atType = msg.getAt().getAtType().getNumber();
        if (atType == 0) {
            List<Long> list = msg.getAt().getUidList();
            if (list == null)
                isAt = false;

            Long uid = UserAction.getMyId();
            for (int i = 0; i < list.size(); i++) {
                if (uid.equals(list.get(i))) {
                    LogUtil.getLog().e(TAG, "有人@我" + uid);
                    if (!gid.equals(SESSION_GID)) {
                        msgDao.atMessage(gid, message, atType);
                        playDingDong();
                    }

                    isAt = true;
                }
            }
        } else {
            if (atMessage.getUidList() == null || atMessage.getUidList().size() == 0) {//是群公告
                refreshGroupInfo(msg.getGid());
            }
            LogUtil.getLog().e(TAG, "@所有人");
            if (!gid.equals(SESSION_GID)) {
                msgDao.atMessage(gid, message, atType);
                playDingDong();
            }
            isAt = true;
        }
        return isAt;
    }

    /**
     * 已过期
     *
     * @param gid
     * @param atType
     * @param message
     * @param list
     * @return
     */
    @Deprecated
    private boolean updateAtMessage(String gid, int atType, String message, List<Long> list) {
        boolean isAt = false;
        if (atType == 0) {
            if (list == null)
                isAt = false;

            Long uid = UserAction.getMyId();
            for (int i = 0; i < list.size(); i++) {
                if (uid.equals(list.get(i))) {
                    LogUtil.getLog().e(TAG, "有人@我" + uid);
                    if (!gid.equals(SESSION_GID)) {
                        msgDao.atMessage(gid, message, atType);
                        playDingDong();
                    }

                    isAt = true;
                }
            }
        } else {
            if (list == null || list.size() == 0) {//是群公告
                refreshGroupInfo(gid);
            }
            LogUtil.getLog().e(TAG, "@所有人");
            if (!gid.equals(SESSION_GID)) {
                msgDao.atMessage(gid, message, atType);
                playDingDong();
            }
            isAt = true;
        }
        return isAt;
    }

    public void playDingDong() {
        if (System.currentTimeMillis() - playTimeOld < 500) {
            return;
        }
        if (isMicrophoneUsing) {
            return;
        }
        LogUtil.getLog().i(TAG, "--使用麦克风-playDingDong");
        playTimeOld = System.currentTimeMillis();
        MediaBackUtil.playDingDong(AppConfig.getContext());
    }

    private UserInfo updateUserOnlineStatus(MsgBean.UniversalMessage.WrapMessage msg) {
        long fromUid = msg.getFromUid();
        MsgBean.ActiveStatChangeMessage message = msg.getActiveStatChange();
        if (message == null) {
            return null;
        }
//        LogUtil.getLog().d(TAG, ">>>在线状态改变---uid=" + msg.getFromUid() + "--onlineType=" + message.getActiveTypeValue());
        fetchTimeDiff(message.getTimestamp());
        if (message.getActiveTypeValue() == 1) {
            SocketData.setPreServerAckTime(message.getTimestamp());
        }
        return userDao.updateUserOnlineStatus(fromUid, message.getActiveTypeValue(), message.getTimestamp());
//        MessageManager.getInstance().updateCacheUserOnlineStatus(fromUid, message.getActiveTypeValue(), message.getTimestamp());
    }

    /*
     * 修正本地时间与服务器时间差值，暂时没考虑时区问题
     * */
    private void fetchTimeDiff(long timestamp) {
        long current = System.currentTimeMillis();//本地系统当前时间
        TimeToString.DIFF_TIME = timestamp - current;
    }

    /***
     * 群
     * @param sid 群id
     */
    public void setSessionGroup(String sid) {
        if (SESSION_TYPE == 3)
            return;
        SESSION_TYPE = 2;
        SESSION_FUID = null;
        SESSION_GID = sid;
    }

    /***
     * 单人
     * @param fuid
     */
    public void setSessionSolo(Long fuid) {
        if (SESSION_TYPE == 3)
            return;
        SESSION_TYPE = 1;
        SESSION_FUID = fuid;
        SESSION_GID = null;
    }

    /***
     * 无会话
     */
    public void setSessionNull() {
        if (SESSION_TYPE == 3)
            return;
        SESSION_TYPE = 0;
        SESSION_FUID = null;
        SESSION_GID = null;
    }

    //允许戳一戳弹窗
    public void setCanStamp(Boolean canStamp) {
        CAN_STAMP = canStamp;
        LogUtil.getLog().e("==CAN_STAMP==" + CAN_STAMP);
    }

    /***
     * 根据接收到的消息内容，更新用户头像昵称等资料
     * @param msg
     */
    @Deprecated
    private void updateUserAvatarAndNick(MsgBean.UniversalMessage.WrapMessage msg, boolean isList, String requestId) {
        if (msg.getMsgType() == MsgBean.MessageType.UNRECOGNIZED || msg.getMsgType().getNumber() > 100) {//通知类消息
            return;
        }
        if (isList) {
            if (TextUtils.isEmpty(requestId)) {
                return;
            }
            TaskDealWithMsgList task = getMsgTask(requestId);
            if (task == null) {
                return;
            }
            Map<Long, UserInfo> pendingUsers = task.getUserMap();
            if (pendingUsers == null) {
                return;
            }
            UserInfo info = new UserInfo();
            info.setUid(msg.getFromUid());
            info.setHead(msg.getAvatar());
            info.setName(msg.getNickname());
            pendingUsers.put(msg.getFromUid(), info);
        } else {
            boolean hasChange = updateUserAvatarAndNick(msg.getFromUid(), msg.getAvatar(), msg.getNickname());
            //避免重复刷新通讯录
            if (msg.getMsgType() == REQUEST_FRIEND || msg.getMsgType() == ACCEPT_BE_FRIENDS
                    || msg.getMsgType() == REMOVE_FRIEND || msg.getMsgType() == REQUEST_GROUP
                    || msg.getMsgType() == ACTIVE_STAT_CHANGE) {
                return;
            }
            if (hasChange) {
                notifyRefreshFriend(true, msg.getFromUid(), CoreEnum.ERosterAction.UPDATE_INFO);
            }
        }
    }

    /*
     * 检测接收消息是否发出通知或者震动
     * @param isList 是否是批量消息
     * @param canNotify 是否能发出通知声音后震动，批量消息只要通知一声
     * */
    public void checkNotifyVoice(MsgBean.UniversalMessage.WrapMessage msg, boolean isList, boolean canNotify) {
        if (msg.getMsgType() != null && msg.getMsgType().getNumber() > 100) {
            return;
        }
        if (!isList) {
            doNotify(msg);
        } else {
            if (canNotify) {
                doNotify(msg);
            }
        }
    }

    /*
     * 发出通知声音或者震动
     * */
    private void doNotify(MsgBean.UniversalMessage.WrapMessage msg) {
        boolean isGroup = StringUtil.isNotNull(msg.getGid());
        //会话已经静音
        Session session = isGroup ? DaoUtil.findOne(Session.class, "gid", msg.getGid()) : DaoUtil.findOne(Session.class, "from_uid", msg.getFromUid());
        if (session != null && session.getIsMute() == 1) {

            return;
        }
        if (isGroup && SESSION_TYPE == 2 && SESSION_GID.equals(msg.getGid())) { //群
            //当前会话是本群，仅震动
            if (msg.getMsgType() == MsgBean.MessageType.STAMP) {
                playVibration();
            }
        } else if (SESSION_TYPE == 1 && SESSION_FUID != null && SESSION_FUID.longValue() == msg.getFromUid()) {//单人
            if (msg.getMsgType() == MsgBean.MessageType.STAMP) {
                if (isGroup && CAN_STAMP) {
                    //如果是处于单聊会话，但收到了群戳一戳，则需要弹框
                    AppConfig.getContext().startActivity(new Intent(AppConfig.getContext(), ChatActionActivity.class)
                            .putExtra(ChatActionActivity.AGM_DATA, msg.toByteArray())
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    //如果是处于单聊会话，仅震动
                    playVibration();
                }
            }
        } else if (SESSION_TYPE == 3) {//静音模式

        } else if (msg.getMsgType() == MsgBean.MessageType.STAMP && CAN_STAMP) {//戳一戳
            if (UserAction.getMyId() != null && msg.getFromUid() == UserAction.getMyId().longValue()) {
                return;
            }
            //不在聊天页 或 在聊天页，当前聊天人不是这个人
            AppConfig.getContext().startActivity(new Intent(AppConfig.getContext(), ChatActionActivity.class)
                    .putExtra(ChatActionActivity.AGM_DATA, msg.toByteArray())
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            );
        } else {
            if (!AudioPlayManager.getInstance().isPlayingVoice()) {
                playDingDong();
            }
        }
    }

    //振动
    private void playVibration() {
        if (System.currentTimeMillis() - playVBTimeOld < 500) {
            return;
        }
        playVBTimeOld = System.currentTimeMillis();
        MediaBackUtil.playVibration(AppConfig.getContext(), 200);
    }


    /*
     * 更新session 置顶及免打扰字段
     * */
    public void updateSessionTopAndDisturb(String gid, Long uid, int top, int disturb) {
        msgDao.updateSessionTopAndDisturb(gid, uid, top, disturb);
    }

    @Deprecated
    public void removeLoadGids(String gid) {
        if (loadGids != null && !TextUtils.isEmpty(gid)) {
            loadGids.remove(gid);
        }
    }

    @Deprecated
    public void removeLoadUids(Long uid) {
        if (loadUids != null && uid != null) {
            loadUids.remove(uid);
        }
    }

    @Deprecated
    public void addSavedGroup(List<Group> list) {
        if (list != null && list.size() > 0) {
            saveGroups.addAll(list);
        }
    }

    @Deprecated
    public List<Group> getSavedGroups() {
        return saveGroups;
    }

    /*
     * 群成员数据转变为UserInfo
     * */
    public UserInfo memberToUser(MemberUser user) {
        UserInfo info = null;
        if (user != null) {
            info = new UserInfo();
            info.setUid(user.getUid());
            info.setName(user.getName());
            info.setHead(user.getHead());
            info.setMembername(user.getMembername());
            info.setInviter(user.getInviter());
            info.setInviterName(user.getInviterName());
            info.setJoinTime(user.getJoinTime());
            info.setJoinType(user.getJoinType());
            info.setImid(user.getImid());
            info.setSex(user.getSex());
            info.setTag(user.getTag());
        }
        return info;
    }

    /*
     * UserInfo 转变为 MemberUser
     * */
    public MemberUser userToMember(IUser user, String gid) {
        MemberUser info = null;
        if (user != null) {
            info = new MemberUser();
            info.setUid(user.getUid());
            info.setName(user.getName());
            info.setHead(user.getHead());
            info.setMembername(user.getMembername());
            info.setInviter(user.getInviter());
            info.setInviterName(user.getInviterName());
            info.setJoinTime(user.getJoinTime());
            info.setJoinType(user.getJoinType());
            info.setImid(user.getImid());
            info.setSex(user.getSex());
            info.init(gid);
        }
        return info;
    }


    /*
     * UserInfo 转变为 MemberUser
     * */
    public List<MemberUser> getMemberList(List<IUser> list, String gid) {
        List<MemberUser> memberUsers = null;
        if (list == null) {
            return memberUsers;
        }
        int len = list.size();
        if (len > 0) {
            memberUsers = new ArrayList<>();
        }
        for (int i = 0; i < len; i++) {
            IUser user = list.get(i);
            if (user != null) {
                memberUsers.add(userToMember(user, gid));
            }
        }
        return memberUsers;
    }


    /*
     * 检测该群是否还有效，即自己是否还在该群中,有效为true，无效为false
     * */
    @Deprecated
    public boolean isGroupValid(Group group) {
        if (group != null) {
            if (group.getStat() != ChatEnum.EGroupStatus.NORMAL) {
                return false;
            } else {
                List<MemberUser> users = group.getUsers();
                if (users != null) {
                    MemberUser member = MessageManager.getInstance().userToMember(UserAction.getMyInfo(), group.getGid());
                    if (member != null && !users.contains(member)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /*
     * 检测该群是否还有效，即自己是否还在该群中,有效为true，无效为false
     * */
    @Deprecated
    public boolean isGroupValid2(Group group) {
        if (group != null) {
            List<MemberUser> users = group.getUsers();
            if (users != null) {
                MemberUser member = MessageManager.getInstance().userToMember(UserAction.getMyInfo(), group.getGid());
                if (member != null && !users.contains(member)) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * 通知群变化
     * */
    public void notifyGroupChange(boolean isNeedLoad) {
        EventGroupChange event = new EventGroupChange();
        event.setNeedLoad(isNeedLoad);
        EventBus.getDefault().post(event);
    }

    //登出时需要清除缓存数据
    public void clearCache() {
        if (loadUids != null) {
            loadUids.clear();
        }
        if (loadGids != null) {
            loadGids.clear();
        }
        if (oldMsgId != null) {
            oldMsgId.clear();
        }
        if (taskMaps != null) {
            taskMaps.clear();
        }
        if (readTimeMap != null) {
            readTimeMap.clear();
        }
        //用户退出登录需清除阅后即焚数据
//        BurnManager.getInstance().clear();
        // 清除广场添加数据
        SpUtil.getSpUtil().putSPValue(RecommendFragment.REFRESH_COUNT, "");
    }

    /*
     * 群成员数据变化时，更新群信息
     * */
    @Deprecated
    public synchronized void refreshGroupInfo(final String gid) {
        new MsgAction().loadGroupMember(gid, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                super.onResponse(call, response);
                notifyGroupChange(false);
                List<String> gids = new ArrayList<>();
                if (!TextUtils.isEmpty(gid)) {
                    gids.add(gid);
                }
                //回主线程调用更新sessionDetial
                if (MyAppLication.INSTANCE().repository != null)
                    MyAppLication.INSTANCE().repository.updateSessionDetail(gids, null);
            }

            @Override
            public void onFailure(Call<ReturnBean<Group>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    public void notifyRefreshUser() {
        EventRefreshUser eventRefreshUser = new EventRefreshUser();
        EventBus.getDefault().post(eventRefreshUser);
    }

    public void removeMsgTask(String requestId) {
        taskMaps.remove(requestId);
    }

    public TaskDealWithMsgList getMsgTask(String requestId) {
        return taskMaps.get(requestId);
    }

    //通知支付结果
    public void notifyPayResult(MsgBean.PayResultMessage resultMessage) {
        if (resultMessage == null) {
            return;
        }
        LogUtil.writeLog("支付--接收到结果--actionId=" + resultMessage.getActionId() + "--tradeId=" + resultMessage.getTradeId());
        PayResultEvent event = new PayResultEvent();
        MsgBean.PayResultMessage.PayResult result = resultMessage.getResult();
        event.setActionId(resultMessage.getActionId());
        event.setTradeId(resultMessage.getTradeId());
        event.setErrMsg(resultMessage.getErrorMsg());
        event.setResult(result.getNumber());
        event.setSign(resultMessage.getSign());
        EventBus.getDefault().post(event);
    }

    public void saveMessage(MsgAllBean msgAllBean) {
        DaoUtil.update(msgAllBean);
    }

    //通知切换截屏开关
    public void notifySwitchSnapshot(String gid, long uid, int flag) {
        EventSwitchSnapshot event = new EventSwitchSnapshot();
        event.setGid(gid);
        event.setUid(uid);
        event.setFlag(flag);
        EventBus.getDefault().post(event);
    }

    public void notifyOnlineStatus(boolean status) {
        EventOnlineStatus eventOnlineStatus = new EventOnlineStatus();
        eventOnlineStatus.setOn(status);
        EventBus.getDefault().post(eventOnlineStatus);
    }

    /*
     * 通知刷新聊天界面
     * */
    public void notifyRefreshChat(List<MsgAllBean> list, @CoreEnum.ERefreshType int type) {
        if (list == null || type < 0) {
            return;
        }
        EventRefreshChat event = new EventRefreshChat();
        event.setList(list);
        event.setRefreshType(type);
        EventBus.getDefault().post(event);
    }

    /*
     * 通知刷新聊天界面
     * */
    public void notifyRefreshChat(MsgAllBean bean, @CoreEnum.ERefreshType int type) {
        if (bean == null || type < 0) {
            return;
        }
        EventRefreshChat event = new EventRefreshChat();
        event.setObject(bean);
        event.setRefreshType(type);
        EventBus.getDefault().post(event);
    }

    //群属性变化
    public void notifyGroupMetaChange(Group group) {
        GroupStatusChangeEvent event = new GroupStatusChangeEvent();
        event.setData(group);
        EventBus.getDefault().post(event);
    }

    //群变化
    public void notifyGroupChange(String gid) {
        EventRefreshGroup event = new EventRefreshGroup();
        event.setGid(gid);
        EventBus.getDefault().post(event);
    }

    //聊天界面是否存活
    public boolean isChatAlive() {
        if (SESSION_TYPE == 1 || SESSION_TYPE == 2) {
            return true;
        }
        return false;
    }

    //是否消息来自当前会话
    public boolean isMsgFromCurrentChat(String gid, Long fromUid) {
        if (!TextUtils.isEmpty(gid)) {
            if (TextUtils.isEmpty(SESSION_GID)) {
                return false;
            }
            if (gid.equals(SESSION_GID)) {
                return true;
            }
        } else {
            if (fromUid == null || SESSION_FUID == null) {
                return false;
            }
            if (fromUid.longValue() == SESSION_FUID.longValue()) {
                return true;
            }
        }
        return false;
    }

    public int getFileIconRid(String format) {
        if (TextUtils.isEmpty(format)) {
            return R.mipmap.ic_unknow;
        }
        //不同类型
        if (format.equals("txt")) {
            return R.mipmap.ic_txt;
        } else if (format.equals("xls") || format.equals("xlsx")) {
            return R.mipmap.ic_excel;
        } else if (format.equals("ppt") || format.equals("pptx") || format.equals("pdf")) { //PDF暂用此图标
            return R.mipmap.ic_ppt;
        } else if (format.equals("doc") || format.equals("docx")) {
            return R.mipmap.ic_word;
        } else if (format.equals("rar") || format.equals("zip")) {
            return R.mipmap.ic_zip;
        } else if (format.equals("exe")) {
            return R.mipmap.ic_exe;
        } else {
            return R.mipmap.ic_unknow;
        }
    }

    public void addReadTime(Long uid, long time) {
        if (uid != null) {
            readTimeMap.put(uid, time);
        }
    }

    //是否已读时间有效,已读时间小于或者等于缓存的已读时间，都是无效时间
    public boolean isReadTimeValid(Long uid, long time) {
        if (readTimeMap.containsKey(uid)) {
            long oldTime = readTimeMap.get(uid);
            if (oldTime < time) {
                return true;
            }
        } else if (!readTimeMap.containsKey(uid)) {
            return true;
        }
        return false;
    }

    public boolean isFromSelf(Long uid) {
        if (uid != null && UserAction.getMyId() != null) {
            if (uid.longValue() == UserAction.getMyId().longValue()) {
                return true;
            }
        }
        return false;
    }

    public void setMicrophoneUsing(boolean b) {
        isMicrophoneUsing = b;
        LogUtil.getLog().i(TAG, "--改变麦克风使用状态-isMicrophoneUsing=" + isMicrophoneUsing);

    }

    public void initPreviewID(String gid, Long toUid) {
        PREVIEW_GID = gid;
        PREVIEW_UID = toUid;
    }

    public void clearPreviewId() {
        PREVIEW_GID = "";
        PREVIEW_UID = null;
    }

    //是否图片消息来自正在预览的会话
    public boolean isImageFromCurrent(String gid, long uid) {
        if (!TextUtils.isEmpty(PREVIEW_GID) && !TextUtils.isEmpty(gid) && gid.equals(PREVIEW_GID)) {
            return true;
        } else if (PREVIEW_UID != null && PREVIEW_UID.longValue() == uid) {
            return true;
        }
        return false;
    }

    //通知刷新图片消息
    public void notifyReceiveImage(String gid, long uid) {
        EventReceiveImage event = new EventReceiveImage(gid, uid);
        EventBus.getDefault().post(event);
    }

    private void doRefreshPreviewImage(MsgBean.UniversalMessage.WrapMessage wrapMessage) {
        if (wrapMessage.getMsgType() == MsgBean.MessageType.IMAGE) {
            long toUid;
            if (!TextUtils.isEmpty(wrapMessage.getGid())) {
                toUid = 0;
            } else {
                if (UserAction.getMyId() != null && UserAction.getMyId().longValue() == wrapMessage.getFromUid()) {
                    toUid = wrapMessage.getToUid();
                } else {
                    toUid = wrapMessage.getFromUid();
                }
            }
            if (MessageManager.getInstance().isImageFromCurrent(wrapMessage.getGid(), toUid)) {
                MessageManager.getInstance().notifyReceiveImage(wrapMessage.getGid(), toUid);
            }
        }
    }


    //备份表数据转换
    public List<MsgAllBean> getMsgList(List<MessageDBTemp> list) {
        List<MsgAllBean> messageList = null;
        try {
            if (list != null) {
                messageList = new ArrayList<>();
                for (MessageDBTemp msg : list) {
                    if (msg.getRedEnvelope() == null) {
                        continue;
                    }
                    RedEnvelopeMessage envelope = new RedEnvelopeMessage();
                    envelope.setMsgid(msg.getRedEnvelope().getMsgId());
                    envelope.setAccessToken(msg.getRedEnvelope().getAccessToken());
                    envelope.setId(msg.getRedEnvelope().getId());
                    envelope.setActionId(msg.getRedEnvelope().getActionId());
                    envelope.setComment(msg.getRedEnvelope().getComment());
                    envelope.setEnvelopStatus(msg.getRedEnvelope().getEnvelopStatus());
                    envelope.setIsInvalid(msg.getRedEnvelope().getIsInvalid());
                    envelope.setRe_type(msg.getRedEnvelope().getRe_type());
                    envelope.setStyle(msg.getRedEnvelope().getStyle());
                    envelope.setSign(msg.getRedEnvelope().getSign());
                    envelope.setTraceId(msg.getRedEnvelope().getTraceId());
                    if (msg.getRedEnvelope().getAllowUsers() != null && msg.getRedEnvelope().getAllowUsers().size() > 0) {
                        envelope.setAllowUsers(msg.getRedEnvelope().getAllowUsers());
                    }
                    envelope.setCanReview(msg.getRedEnvelope().getCanReview());
                    envelope.setHasPermission(msg.getRedEnvelope().isHasPermission());

                    MsgAllBean message = new MsgAllBean();
                    message.setMsg_id(msg.getMsg_id());
                    message.setRequest_id(msg.getRequest_id());
                    message.setMsg_type(msg.getMsg_type());
                    message.setTimestamp(msg.getTimestamp());
                    message.setGid(msg.getGid());
                    message.setFrom_uid(msg.getFrom_uid());
                    message.setTo_uid(msg.getTo_uid());
                    message.setFrom_avatar(msg.getFrom_avatar());
                    message.setFrom_nickname(msg.getFrom_nickname());
                    message.setFrom_group_nickname(msg.getFrom_group_nickname());
                    message.setSend_state(msg.getSend_state());
                    message.setSend_data(msg.getSend_data());
                    message.setIsLocal(msg.getIsLocal());
                    message.setRead(msg.isRead());
                    message.setRead(msg.getRead());
                    message.setSurvival_time(msg.getSurvival_time());
                    message.setStartTime(msg.getStartTime());
                    message.setReadTime(msg.getReadTime());
                    message.setEndTime(msg.getEndTime());
                    message.setServerTime(msg.getServerTime());
                    message.setIsReplying(msg.getIsReplying());
                    message.setRed_envelope(envelope);
                    messageList.add(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageList;
    }

    public boolean isReceivingOffline() {
        return isReceiveOffline;
    }

    public void setReceiveOffline(boolean b) {
        LogUtil.getLog().i(TAG, "接收离线状态改变--" + b);
        isReceiveOffline = b;
    }

    public boolean isFromPush() {
        return isFromPush;
    }

    public void setFromPush(boolean b) {
        isFromPush = b;
    }

    public String[] getMemberIds(List<MemberUser> members) {
        if (members == null || members.size() <= 0) {
            return null;
        }
        String[] memberIDs = new String[members.size()];
        for (int i = 0; i < members.size(); i++) {
            memberIDs[i] = members.get(i).getMemberId();
        }
        return memberIDs;
    }
}
