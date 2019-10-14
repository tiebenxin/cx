package com.yanlong.im.chat.manager;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventRefreshFriend;
import net.cb.cb.library.bean.EventRefreshMainMsg;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

import static com.yanlong.im.utils.socket.SocketData.oldMsgId;

/**
 * @anthor Liszt
 * @data 2019/9/24
 * Description
 */
public class MessageManager {
    private final String TAG = MessageManager.class.getSimpleName();
    private static MessageManager INSTANCE;
    private MsgDao msgDao = new MsgDao();
    private UserDao userDao = new UserDao();
    private static boolean isMessageChange;//是否有聊天消息变化

    private static List<String> loadGids = new ArrayList<>();
    private static List<Long> loadUids = new ArrayList<>();
    private static Map<Long, UserInfo> cacheUsers = new HashMap<>();//用户信息缓存
    private static Map<String, Group> cacheGroups = new HashMap<>();//群信息缓存
    private static List<Session> cacheSessions = new ArrayList<>();//Session缓存

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
        boolean isSameMesasge = false;
        List<MsgBean.UniversalMessage.WrapMessage> msgList = bean.getWrapMsgList();
        List<String> msgIds = new ArrayList<>();
        //1.先进行数据分割
        for (MsgBean.UniversalMessage.WrapMessage wmsg : msgList) {
            if (!oldMsgId.contains(wmsg.getMsgId())) {
                //2.存库:1.存消息表,存会话表
                MsgAllBean msgAllBean = MsgConversionBean.ToBean(wmsg);
                //5.28 如果为空就不保存这类消息
                if (msgAllBean != null) {
                    msgAllBean.setTo_uid(bean.getToUid());
                    LogUtil.getLog().d(TAG, ">>>>>magSaveAndACK: " + wmsg.getMsgId());
                    //收到直接存表
                    DaoUtil.update(msgAllBean);

                    //6.6 为后端擦屁股
                    if (oldMsgId.size() >= 500)
                        oldMsgId.remove(0);
                    oldMsgId.add(wmsg.getMsgId());
                    if (!TextUtils.isEmpty(msgAllBean.getGid()) && !msgDao.isGroupExist(msgAllBean.getGid())) {
                        loadGroupInfo(msgAllBean.getGid(), msgAllBean.getFrom_uid());
                    } else if (TextUtils.isEmpty(msgAllBean.getGid()) && msgAllBean.getFrom_uid() != null && msgAllBean.getFrom_uid() > 0) {
                        loadUserInfo(msgAllBean.getGid(), msgAllBean.getFrom_uid());
                    } else {
                        updateSessionUnread(msgAllBean.getGid(), msgAllBean.getFrom_uid(), false);
                    }
                    LogUtil.getLog().e(TAG, ">>>>>累计 ");
                } else {
                    LogUtil.getLog().e(TAG, ">>>>>重复消息: " + wmsg.getMsgId());
                }


                msgIds.add(wmsg.getMsgId());
            } else {
                LogUtil.getLog().e(TAG, ">>>>>忽略保存消息: " + wmsg.getMsgId());
                isSameMesasge = true;
            }
        }
        //发送回执
        SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(bean.getRequestId(), msgIds), null);


    }

    /*
     * 网络加载用户信息
     * */
    private synchronized void loadUserInfo(final String gid, final Long uid) {
//        System.out.println("加载数据--loadUserInfo" + "--gid =" + gid + "--uid =" + uid);
        new UserAction().getUserInfoAndSave(uid, ChatEnum.EUserType.STRANGE, new CallBack<ReturnBean<UserInfo>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
                updateSessionUnread(gid, uid, false);
            }
        });
    }

    /*
     * 网络加载群信息
     * */
    private synchronized void loadGroupInfo(final String gid, final long uid) {
//        System.out.println("加载数据--loadGroupInfo" + "--gid =" + gid + "--uid =" + uid);
        new MsgAction().groupInfo(gid, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                super.onResponse(call, response);
                updateSessionUnread(gid, uid, false);
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
    public void updateSessionUnread(String gid, Long from_uid, boolean isCancel) {
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
     * */
    public void notifyRefreshMsg(int chatType, Long uid, String gid, int refreshTag) {
        EventRefreshMainMsg eventRefreshMainMsg = new EventRefreshMainMsg();
        eventRefreshMainMsg.setType(chatType);
        eventRefreshMainMsg.setUid(uid);
        eventRefreshMainMsg.setGid(gid);
        eventRefreshMainMsg.setRefreshTag(refreshTag);
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
    @RequiresApi(api = Build.VERSION_CODES.N)
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
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateCacheUserAvatarAndName(long uid, String avatar, String nickName) {
        if (cacheUsers != null) {
            UserInfo info = getCacheUserInfo(uid);
            if (info != null) {
                info.setHead(avatar);
                info.setName(nickName);
                cacheUsers.remove(info);
                cacheUsers.put(uid, info);            }
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

    public List<Session> getCacheSession() {
        return cacheSessions;
    }
}
