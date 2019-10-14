package com.yanlong.im.chat.manager;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.ChatMessage;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.task.TaskDealWithMsgList;
import com.yanlong.im.chat.ui.ChatActionActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.MediaBackUtil;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventLoginOut4Conflict;
import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.bean.EventRefreshFriend;

import com.yanlong.im.chat.eventbus.EventRefreshMainMsg;

import net.cb.cb.library.bean.EventUserOnlineChange;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

import static com.yanlong.im.utils.socket.MsgBean.MessageType.ACCEPT_BE_FRIENDS;
import static com.yanlong.im.utils.socket.MsgBean.MessageType.ACTIVE_STAT_CHANGE;
import static com.yanlong.im.utils.socket.MsgBean.MessageType.REMOVE_FRIEND;
import static com.yanlong.im.utils.socket.MsgBean.MessageType.REQUEST_FRIEND;
import static com.yanlong.im.utils.socket.MsgBean.MessageType.REQUEST_GROUP;
import static com.yanlong.im.utils.socket.SocketData.createMsgBean;
import static com.yanlong.im.utils.socket.SocketData.oldMsgId;

/**
 * @anthor Liszt
 * @data 2019/9/24
 * Description 消息管理类
 */
public class MessageManager {
    private final String TAG = MessageManager.class.getSimpleName();

    private static int SESSION_TYPE = 0;//无会话,1:单人;2群,3静音模式
    private static Long SESSION_FUID;//单人会话id
    private static String SESSION_SID;//会话id

    private static MessageManager INSTANCE;
    private MsgDao msgDao = new MsgDao();
    private UserDao userDao = new UserDao();
    private static boolean isMessageChange;//是否有聊天消息变化

    private static List<String> loadGids = new ArrayList<>();//需要异步加载群数据的群id
    private static List<Long> loadUids = new ArrayList<>();//需要异步记载用户数据的用户id
    private static Map<Long, UserInfo> cacheUsers = new HashMap<>();//用户信息缓存
    private static Map<String, Group> cacheGroups = new HashMap<>();//群信息缓存
    private static List<Session> cacheSessions = new ArrayList<>();//Session缓存

    private long playTimeOld = 0;//当前声音播放时间
    private long playVBTimeOld = 0; //当前震动时间
    private TaskDealWithMsgList taskMsgList;//处理批量接收消息异步任务


    public static MessageManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessageManager();
        }
        return INSTANCE;
    }

    /*
     * 消息接收流程
     * */
    public synchronized void onReceive(MsgBean.UniversalMessage bean) {
        List<MsgBean.UniversalMessage.WrapMessage> msgList = bean.getWrapMsgList();
        if (msgList != null) {
            int length = msgList.size();
            if (length > 0) {
                if (length == 1) {//收到单条消息
                    MsgBean.UniversalMessage.WrapMessage wrapMessage = msgList.get(0);
                    dealWithMsg(wrapMessage, false, true);

                } else {//收到多条消息（如离线）
                    taskMsgList = new TaskDealWithMsgList(msgList);
                    taskMsgList.execute();
                }
            }
        }
    }

    /*
     * 处理接收到的消息
     * 分两类处理，一类是需要产生本地消息记录的，一类是相关指令，无需产生消息记录
     * @param wrapMessage 接收到的消息
     * @param isList 是否是批量消息
     * */
    public boolean dealWithMsg(MsgBean.UniversalMessage.WrapMessage wrapMessage, boolean isList, boolean canNotify) {
        boolean result = false;
        if (oldMsgId.contains(wrapMessage.getMsgId())) {
            LogUtil.getLog().e(TAG, ">>>>>重复消息: " + wrapMessage.getMsgId());
            return false;
        } else {
            if (oldMsgId.size() >= 500) {
                oldMsgId.remove(0);
            }
            oldMsgId.add(wrapMessage.getMsgId());
        }
        updateUserAvatarAndNick(wrapMessage);
        MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
        switch (wrapMessage.getMsgType()) {
            case CHAT://文本
            case IMAGE://图片
            case STAMP://戳一戳
            case VOICE://语音
            case SHORT_VIDEO://短视频
            case TRANSFER://转账
            case BUSINESS_CARD://名片
            case RED_ENVELOPER://红包
            case RECEIVE_RED_ENVELOPER://领取红包
            case ACCEPT_BE_GROUP://接受入群
            case REMOVE_GROUP_MEMBER://被移除群聊
            case CHANGE_GROUP_MASTER://转让群主
            case OUT_GROUP://退出群聊
            case ASSISTANT://小消息
                if (bean != null) {
                    result = saveMessage(bean, isList);
                }
                break;
            case ACCEPT_BE_FRIENDS://接受成为好友,需要产生消息后面在处理
                checkDoubleMessage(wrapMessage);//检测双黄蛋消息
                if (bean != null) {
                    result = saveMessage(bean, isList);
                }
                notifyRefreshFriend(false, wrapMessage.getFromUid(), CoreEnum.ERosterAction.ACCEPT_BE_FRIENDS);
                break;
            case REQUEST_FRIEND://请求添加为好友
                if (!TextUtils.isEmpty(wrapMessage.getRequestFriend().getContactName())) {
                    msgDao.userAcceptAdd(wrapMessage.getFromUid(), wrapMessage.getRequestFriend().getContactName());
                }
                msgDao.remidCount("friend_apply");
                notifyRefreshFriend(true, wrapMessage.getFromUid(), CoreEnum.ERosterAction.REQUEST_FRIEND);
                break;
            case REMOVE_FRIEND:
                notifyRefreshFriend(false, wrapMessage.getFromUid(), CoreEnum.ERosterAction.REMOVE_FRIEND);
                break;
            case REQUEST_GROUP://群主会收到成员进群的请求的通知
                msgDao.remidCount("friend_apply");
                for (MsgBean.GroupNoticeMessage ntm : wrapMessage.getRequestGroup().getNoticeMessageList()) {
                    msgDao.groupAcceptAdd(wrapMessage.getRequestGroup().getJoinType().getNumber(), wrapMessage.getRequestGroup().getInviter(), wrapMessage.getRequestGroup().getInviterName(), wrapMessage.getGid(), ntm.getUid(), ntm.getNickname(), ntm.getAvatar());
                }
                notifyRefreshFriend(true, -1L, CoreEnum.ERosterAction.DEFAULT);
                break;
            case CHANGE_GROUP_META://修改群属性
                MsgBean.ChangeGroupMetaMessage.RealMsgCase realMsgCase = wrapMessage.getChangeGroupMeta().getRealMsgCase();
                switch (realMsgCase) {
                    case NAME://群名
                        if (bean != null) {
                            result = saveMessage(bean, isList);
                        }
                        msgDao.groupNameUpadte(wrapMessage.getGid(), wrapMessage.getChangeGroupMeta().getName());
                        break;
                    case PROTECT_MEMBER://群成员保护
                        msgDao.groupContactIntimatelyUpdate(wrapMessage.getGid(), wrapMessage.getChangeGroupMeta().getProtectMember());
                        break;
                    case AVATAR://群头像
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
                    eventLoginOut4Conflict.setMsg("您的账号" + phone + "已经在另一台设备上登录。如果不是您本人操作,请尽快修改密码");
                } else if (wrapMessage.getForceOffline().getForceOfflineReason() == MsgBean.ForceOfflineReason.LOCKED) {//被冻结
                    eventLoginOut4Conflict.setMsg("你已被限制登录");
                }
                EventBus.getDefault().post(eventLoginOut4Conflict);
                break;
            case AT://@消息
                if (bean != null) {
                    result = saveMessage(bean, isList);
                }
                updateAtMessage(wrapMessage);
                break;
            case ACTIVE_STAT_CHANGE://在线状态改变
                updateUserOnlineStatus(wrapMessage);
                notifyRefreshFriend(true, wrapMessage.getFromUid(), CoreEnum.ERosterAction.UPDATE_INFO);
                EventBus.getDefault().post(new EventUserOnlineChange());
                break;
            case CANCEL://撤销消息
                if (bean != null) {
                    result = saveMessage(bean, isList);
                }
                String gid = wrapMessage.getGid();
                if (!StringUtil.isNotNull(gid)) {
                    gid = null;
                }
                long fromUid = wrapMessage.getFromUid();
                MessageManager.getInstance().updateSessionUnread(gid, fromUid, true);
                msgDao.msgDel4Cancel(wrapMessage.getMsgId(), wrapMessage.getCancel().getMsgId(), "", "");
                EventBus.getDefault().post(new EventRefreshChat());
                MessageManager.getInstance().setMessageChange(true);
                break;
            case RESOURCE_LOCK://资源锁定
                updateUserLockCloudRedEnvelope(wrapMessage);
                break;
        }
        //刷新单个
        if (result && !isList && bean != null) {
            setMessageChange(true);
            notifyRefreshMsg(CoreEnum.EChatType.PRIVATE, wrapMessage.getFromUid(), wrapMessage.getGid(), CoreEnum.ESessionRefreshTag.SINGLE, bean);
        }
        checkNotifyVoice(wrapMessage, isList, canNotify);
        return result;
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
                    UserDao userDao = new UserDao();
                    userDao.updateUserLockRedEnvelope(UserAction.getMyId(), lock.getLock());
                    UserInfo info = UserAction.getMyInfo();
                    if (info != null) {
                        info.setLockCloudRedEnvelope(lock.getLock());
                    }
                    break;
            }
        }
    }

    /*
     * 保存消息
     * @param msgAllBean 消息
     * @isList 是否是批量消息
     * */
    private boolean saveMessage(MsgAllBean msgAllBean, boolean isList) {
        msgAllBean.setRead(false);//设置未读
        msgAllBean.setTo_uid(msgAllBean.getTo_uid());
        boolean result = false;
        //收到直接存表
        DaoUtil.update(msgAllBean);
        if (!TextUtils.isEmpty(msgAllBean.getGid()) && !msgDao.isGroupExist(msgAllBean.getGid()) && !loadGids.contains(msgAllBean.getGid())) {
            loadGids.add(msgAllBean.getGid());
            loadGroupInfo(msgAllBean.getGid(), msgAllBean.getFrom_uid(), isList, msgAllBean);
        } else if (TextUtils.isEmpty(msgAllBean.getGid()) && msgAllBean.getFrom_uid() != null && msgAllBean.getFrom_uid() > 0 && !loadUids.contains(msgAllBean.getFrom_uid())) {
            loadUids.add(msgAllBean.getFrom_uid());
            loadUserInfo(msgAllBean.getGid(), msgAllBean.getFrom_uid(), isList, msgAllBean);
        } else {
            MessageManager.getInstance().updateSessionUnread(msgAllBean.getGid(), msgAllBean.getFrom_uid(), false);
            if (isList) {
                MessageManager.getInstance().setMessageChange(true);
            }
            result = true;
        }
        return result;
    }

    /*
     * 网络加载用户信息
     * */
    private synchronized void loadUserInfo(final String gid, final Long uid, boolean isList, MsgAllBean bean) {
//        System.out.println("加载数据--loadUserInfo" + "--gid =" + gid + "--uid =" + uid);
        new UserAction().getUserInfoAndSave(uid, ChatEnum.EUserType.STRANGE, new CallBack<ReturnBean<UserInfo>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
                updateSessionUnread(gid, uid, false);
                if (isList) {
                    if (taskMsgList != null) {
                        taskMsgList.updateTaskCount();
                    }
                } else {
                    setMessageChange(true);
                    notifyRefreshMsg(CoreEnum.EChatType.PRIVATE, uid, gid, CoreEnum.ESessionRefreshTag.SINGLE, bean);

                }
            }
        });
    }

    /*
     * 网络加载群信息
     * */
    private synchronized void loadGroupInfo(final String gid, final long uid, boolean isList, MsgAllBean bean) {
//        System.out.println("加载数据--loadGroupInfo" + "--gid =" + gid + "--uid =" + uid);
        new MsgAction().groupInfo(gid, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                super.onResponse(call, response);
                updateSessionUnread(gid, uid, false);
                if (isList) {
                    if (taskMsgList != null) {
                        taskMsgList.updateTaskCount();
                    }
                } else {
                    setMessageChange(true);
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

    public void createSession(String gid, Long uid) {
        msgDao.sessionCreate(gid, uid);
    }

    /*
     * 更新session未读数
     * */
    public synchronized void updateSessionUnread(String gid, Long from_uid, boolean isCancel) {
        System.out.println(TAG + "--更新Session--updateSessionUnread");
        msgDao.sessionReadUpdate(gid, from_uid, isCancel);
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
    public void notifyRefreshMsg(@CoreEnum.EChatType int chatType, Long uid, String gid, @CoreEnum.ESessionRefreshTag int refreshTag, MsgAllBean msg) {
        EventRefreshMainMsg eventRefreshMainMsg = new EventRefreshMainMsg();
        eventRefreshMainMsg.setType(chatType);
        eventRefreshMainMsg.setUid(uid);
        eventRefreshMainMsg.setGid(gid);
        eventRefreshMainMsg.setRefreshTag(refreshTag);
        if (msg != null) {
            eventRefreshMainMsg.setMsgAllBean(msg);
        }
        EventBus.getDefault().post(eventRefreshMainMsg);
    }

    public void deleteSessionAndMsg(Long uid, String gid) {
        msgDao.sessionDel(uid, gid);
        msgDao.msgDel(uid, gid);

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

    /*
     * 获取缓存信息中用户信息
     * */
    public UserInfo getCacheUserInfo(Long uid) {
        UserInfo info = null;
        if (uid != null && uid > 0) {
            info = cacheUsers.get(uid);
            if (info == null) {
                info = userDao.findUserInfo(uid);
                if (info != null) {
                    cacheUsers.put(uid, info);
                }
            }
        }
        return info;
    }

    /*
     * 获取缓存数据中群信息
     * */
    public Group getCacheGroup(String gid) {
        Group group = null;
        if (!TextUtils.isEmpty(gid)) {
            group = cacheGroups.get(gid);
            if (group == null) {
                group = msgDao.getGroup4Id(gid);
                if (group != null) {
                    cacheGroups.put(gid, group);
                }
            }
        }
        return group;
    }

    /*
     * 更新用户头像和昵称
     * */
    public boolean updateUserAvatarAndNick(long uid, String avatar, String nickName) {
        boolean hasChange = userDao.userHeadNameUpdate(uid, avatar, nickName);
        if (hasChange) {
            updateCacheUserAvatarAndName(uid, avatar, nickName);
        }
        return hasChange;
    }

    /*
     * 更新缓存用户头像及昵称
     * */
    private void updateCacheUserAvatarAndName(long uid, String avatar, String nickName) {
        if (cacheUsers != null) {
            UserInfo info = getCacheUserInfo(uid);
            if (info != null) {
                info.setHead(avatar);
                info.setName(nickName);
                cacheUsers.remove(info);
                cacheUsers.put(uid, info);
            }
        }
    }

    /*
     * 更新缓存用户在线状态及最后在线时间
     * */
    public void updateCacheUserOnlineStatus(long uid, int onlineType, long time) {
        if (cacheUsers != null) {
            UserInfo info = getCacheUserInfo(uid);
            if (info != null) {
                info.setLastonline(time);
                info.setActiveType(onlineType);
                cacheUsers.remove(info);
                cacheUsers.put(uid, info);
            }
        }
    }

    /*
     * 获取内存缓存中session数据
     * */
    public List<Session> getCacheSession() {
        return cacheSessions;
    }

    //检测是否是双重消息，及一条消息需要产生两条本地消息记录,回执在通知消息中发送
    private static void checkDoubleMessage(MsgBean.UniversalMessage.WrapMessage wmsg) {
        if (wmsg.getMsgType() == ACCEPT_BE_FRIENDS) {
            MsgBean.AcceptBeFriendsMessage receiveMessage = wmsg.getAcceptBeFriends();
            if (receiveMessage != null && !TextUtils.isEmpty(receiveMessage.getSayHi())) {
                ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), receiveMessage.getSayHi());
                MsgAllBean message = createMsgBean(wmsg, ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
                DaoUtil.save(message);
//                MessageManager.getInstance().updateSessionUnread(message.getGid(), message.getFrom_uid(),false);//不更新未读，只需要一条即可
//                MessageManager.getInstance().setMessageChange(true);
            }
        }
    }

    private boolean updateAtMessage(MsgBean.UniversalMessage.WrapMessage msg) {
        boolean isAt = false;
        String gid = msg.getGid();
        String message = msg.getAt().getMsg();
        int atType = msg.getAt().getAtType().getNumber();
        if (atType == 0) {
            List<Long> list = msg.getAt().getUidList();
            if (list == null)
                isAt = false;

            Long uid = UserAction.getMyId();
            for (int i = 0; i < list.size(); i++) {
                if (uid.equals(list.get(i))) {
                    Log.v(TAG, "有人@我" + uid);
                    msgDao.atMessage(gid, message, atType);
                    playDingDong();
                    isAt = true;
                }
            }
        } else {
            Log.v(TAG, "@所有人");
            msgDao.atMessage(gid, message, atType);
            playDingDong();
            isAt = true;
        }
        return isAt;
    }

    private void playDingDong() {
        if (System.currentTimeMillis() - playTimeOld < 500) {
            return;
        }
        playTimeOld = System.currentTimeMillis();
        MediaBackUtil.palydingdong(AppConfig.getContext());
    }

    private void updateUserOnlineStatus(MsgBean.UniversalMessage.WrapMessage msg) {
        long fromUid = msg.getFromUid();
        MsgBean.ActiveStatChangeMessage message = msg.getActiveStatChange();
        if (message == null) {
            return;
        }
        fetchTimeDiff(message.getTimestamp());
        userDao.updateUserOnlineStatus(fromUid, message.getActiveTypeValue(), message.getTimestamp());
        MessageManager.getInstance().updateCacheUserOnlineStatus(fromUid, message.getActiveTypeValue(), message.getTimestamp());
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
        SESSION_SID = sid;
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
        SESSION_SID = null;
    }

    /***
     * 根据接收到的消息内容，更新用户头像昵称等资料
     * @param msg
     */
    private void updateUserAvatarAndNick(MsgBean.UniversalMessage.WrapMessage msg) {
        if (msg.getMsgType().getNumber() > 100) {//通知类消息
            return;
        }
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

    /*
     * 检测接收消息是否发出通知或者震动
     * @param isList 是否是批量消息
     * @param canNotify 是否能发出通知声音后震动，批量消息只要通知一声
     * */
    private void checkNotifyVoice(MsgBean.UniversalMessage.WrapMessage msg, boolean isList, boolean canNotify) {
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
        if (isGroup && SESSION_TYPE == 2 && SESSION_SID.equals(msg.getGid())) { //群
            //当前会话是本群不提示

        } else if (SESSION_TYPE == 1 && SESSION_FUID != null && SESSION_FUID.longValue() == msg.getFromUid()) {//单人
            if (msg.getMsgType() == MsgBean.MessageType.STAMP) {
                playVibration();
            }
        } else if (SESSION_TYPE == 3) {//静音模式

        } else if (SESSION_TYPE == 0 && msg.getMsgType() == MsgBean.MessageType.STAMP) {//戳一戳
            AppConfig.getContext().startActivity(new Intent(AppConfig.getContext(), ChatActionActivity.class)
                    .putExtra(ChatActionActivity.AGM_DATA, msg.toByteArray())
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            );
        } else {
            playDingDong();
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
}
