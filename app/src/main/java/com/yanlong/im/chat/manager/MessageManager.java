package com.yanlong.im.chat.manager;

import android.content.Intent;
import android.text.TextUtils;

import com.example.nim_lib.ui.VideoActivity;
import com.hm.cxpay.eventbus.PayResultEvent;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MessageDBTemp;
import com.yanlong.im.chat.bean.MsgAllBean;
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
import com.yanlong.im.chat.ui.ChatActionActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.MediaBackUtil;
import com.yanlong.im.utils.audio.AudioPlayManager;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventGroupChange;
import net.cb.cb.library.bean.EventOnlineStatus;
import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.bean.EventRefreshFriend;
import net.cb.cb.library.bean.EventSwitchDisturb;
import net.cb.cb.library.bean.GroupStatusChangeEvent;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

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

    //缓存
    private static List<Group> saveGroups = new ArrayList<>();//已保存群信息缓存

    private long playTimeOld = 0;//当前声音播放时间
    private long playVBTimeOld = 0; //当前震动时间

    private Boolean CAN_STAMP = true;//true 允许戳一戳弹窗 ,false 不允许

    /**
     * 保存单聊发送已读消息时间戳
     */
    private Map<Object, Long> readTimeMap = new HashMap<>();

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

    public boolean isMessageChange() {
        return isMessageChange;
    }

    public void setMessageChange(boolean isChange) {
        this.isMessageChange = isChange;
    }

    /*
     * 通知刷新消息列表，及未读数，未设置及整体刷新
     * */
    public void notifyRefreshMsg() {
        EventBus.getDefault().post(new EventRefreshMainMsg());
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
    public boolean updateUserAvatarAndNick(long uid, String avatar, String nickName) {
        boolean hasChange = userDao.userHeadNameUpdate(uid, avatar, nickName);
        return hasChange;
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

    public void addSavedGroup(List<Group> list) {
        if (list != null && list.size() > 0) {
            saveGroups.addAll(list);
        }
    }

    public List<Group> getSavedGroups() {
        return saveGroups;
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
        if (oldMsgId != null) {
            oldMsgId.clear();
        }

        if (readTimeMap != null) {
            readTimeMap.clear();
        }
    }


    public void notifyRefreshUser() {
        EventRefreshUser eventRefreshUser = new EventRefreshUser();
        EventBus.getDefault().post(eventRefreshUser);
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

    public void addReadTime(Object uid, long time) {
        if (uid != null) {
            readTimeMap.put(uid, time);
        }
    }

    //是否已读时间有效,已读时间小于或者等于缓存的已读时间，都是无效时间
    public boolean isReadTimeValid(Object id, long time) {
        if (readTimeMap.containsKey(id)) {
            long oldTime = readTimeMap.get(id);
            if (oldTime < time) {
                return true;
            }
        } else if (!readTimeMap.containsKey(id)) {
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

    //是否视频通话界面Live
    public boolean isCallLive() {
        return VideoActivity.returnVideoActivity;
    }

}
