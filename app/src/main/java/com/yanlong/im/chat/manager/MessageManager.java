package com.yanlong.im.chat.manager;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

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
                    MsgBean.UniversalMessage.WrapMessage wmsg = msgList.get(0);
                    dealWithMsg(wmsg);
                } else {//收到多条消息（离线）
                    for (int i = 0; i < length; i++) {
                        MsgBean.UniversalMessage.WrapMessage wmsg = msgList.get(i);
                        dealWithMsg(wmsg);
                    }
                }
            }
        }
    }

    /*
     * 处理接收到的消息
     * 分两类处理，一类是需要产生本地消息记录的，一类是相关指令，无需产生消息记录
     * */
    private void dealWithMsg(MsgBean.UniversalMessage.WrapMessage wrapMessage) {
        if (oldMsgId.contains(wrapMessage.getMsgId())){
            return;
        }
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
            case ACCEPT_BE_FRIENDS://接受成为好友
            case ACCEPT_BE_GROUP://接受入群
            case REMOVE_GROUP_MEMBER://被移除群聊
            case CHANGE_GROUP_MASTER://转让群主
            case OUT_GROUP://退出群聊
            case CHANGE_GROUP_META://修改群信息
            case AT://@消息
            case ASSISTANT://小消息
            case CANCEL://小消息
                MsgAllBean bean = MsgConversionBean.ToBean(wrapMessage);
                if (bean != null) {

                }
                break;
            case REMOVE_FRIEND:
                break;
        }


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

    public List<Session> getCacheSession() {
        return cacheSessions;
    }
}
