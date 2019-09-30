package com.yanlong.im.chat.manager;

import android.text.TextUtils;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;

import net.cb.cb.library.bean.EventRefreshMainMsg;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

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

    public static MessageManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessageManager();
        }
        return INSTANCE;
    }


    public synchronized void onReceive(MsgBean.UniversalMessage bean) {
        List<MsgBean.UniversalMessage.WrapMessage> msgList = bean.getWrapMsgList();
        List<String> msgIds = new ArrayList<>();
        //1.先进行数据分割
        for (MsgBean.UniversalMessage.WrapMessage wmsg : msgList) {
            //2.存库:1.存消息表,存会话表
            MsgAllBean msgAllBean = MsgConversionBean.ToBean(wmsg);
            //5.28 如果为空就不保存这类消息
            if (msgAllBean != null) {
                msgAllBean.setTo_uid(bean.getToUid());
                LogUtil.getLog().d(TAG, ">>>>>magSaveAndACK: " + wmsg.getMsgId());
                //收到直接存表
                DaoUtil.update(msgAllBean);

                //6.6 为后端擦屁股
//                if (!oldMsgId.contains(wmsg.getMsgId())) {
//                    if (oldMsgId.size() >= 500)
//                        oldMsgId.remove(0);
//                    oldMsgId.add(wmsg.getMsgId());
                if (!TextUtils.isEmpty(msgAllBean.getGid()) && !msgDao.isGroupExist(msgAllBean.getGid())) {
                    loadGroupInfo(msgAllBean.getGid(), msgAllBean.getFrom_uid());
                } else if (TextUtils.isEmpty(msgAllBean.getGid()) && msgAllBean.getFrom_uid() != null && msgAllBean.getFrom_uid() > 0) {
                    loadUserInfo(msgAllBean.getGid(), msgAllBean.getFrom_uid());

                } else {
                    msgDao.sessionReadUpdate(msgAllBean.getGid(), msgAllBean.getFrom_uid());

                }
                LogUtil.getLog().e(TAG, ">>>>>累计 ");
//                } else {
//                    LogUtil.getLog().e(TAG, ">>>>>重复消息: " + wmsg.getMsgId());
//                }


                msgIds.add(wmsg.getMsgId());
            } else {
                LogUtil.getLog().e(TAG, ">>>>>忽略保存消息: " + wmsg.getMsgId());
            }


        }
    }

    private synchronized void loadUserInfo(final String gid, final Long uid) {
        System.out.println("加载数据--loadUserInfo" + "--gid =" + gid + "--uid =" + uid);
        new UserAction().getUserInfoAndSave(uid, ChatEnum.EUserType.STRANGE, new CallBack<ReturnBean<UserInfo>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
                msgDao.sessionReadUpdate(gid, uid);
            }
        });
    }

    private synchronized void loadGroupInfo(final String gid, final long uid) {
        System.out.println("加载数据--loadGroupInfo" + "--gid =" + gid + "--uid =" + uid);
        new MsgAction().groupInfo(gid, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                super.onResponse(call, response);
                msgDao.sessionReadUpdate(gid, uid);
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

    public void updateSessionUnread(String gid, Long from_uid) {
        msgDao.sessionReadUpdate(gid,from_uid);
    }

    public void nootifyRefreshMsg() {
        EventBus.getDefault().post(new EventRefreshMainMsg());
    }
}
