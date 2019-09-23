package com.yanlong.im.chat.ui.chat;

import android.util.Log;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.base.IModel;
import net.cb.cb.library.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @anthor Liszt
 * @data 2019/9/19
 * Description
 */
public class ChatModel implements IModel {
    private MsgDao msgDao = new MsgDao();
    private UserDao userDao = new UserDao();
    private MsgAction msgAction = new MsgAction();
    private List<MsgAllBean> listData = new ArrayList<>();
    private String gid;
    private long uid;
    private Map<String, UserInfo> mks = new HashMap<>();
    private Group group;
    private UserInfo userInfo;
    private Session session;


    public void init(String gid, long uid) {
        this.gid = gid;
        this.uid = uid;
    }

    public Observable<List<MsgAllBean>> loadMessages() {
        long time = -1L;
        int length = 0;
        if (listData != null && listData.size() > 0) {
            length = listData.size();
            MsgAllBean bean = listData.get(length - 1);
            if (bean != null && bean.getTimestamp() != null) {
                time = bean.getTimestamp();
            }
        }
        final long finalTime = time;
        if (length < 20) {
            length += 20;
        }
        final int finalLength = length;
        return Observable.create(new ObservableOnSubscribe<List<MsgAllBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<MsgAllBean>> e) throws Exception {
                if (msgDao == null) {
                    msgDao = new MsgDao();
                }
                List<MsgAllBean> list = null;
                if (finalTime > 0) {
                    list = msgAction.getMsg4User(gid, uid, null, finalLength);
                } else {
                    list = msgAction.getMsg4User(gid, uid, null, 20);
                }
                taskMkName(list);
                e.onNext(list);
            }
        });
    }

    /***
     * 获取统一的昵称
     * @param msgListData
     */
    private void taskMkName(List<MsgAllBean> msgListData) {
        mks.clear();
        for (MsgAllBean msg : msgListData) {
            if (msg.getMsg_type() == ChatEnum.EMessageType.NOTICE || msg.getMsg_type() == ChatEnum.EMessageType.MSG_CENCAL || msg.getMsg_type() == ChatEnum.EMessageType.LOCK) {  //通知类型的不处理
                continue;
            }
            String k = msg.getFrom_uid() + "";
            String nkname = "";
            String head = "";

            UserInfo userInfo;
            if (mks.containsKey(k)) {
                userInfo = mks.get(k);
            } else {
                userInfo = msg.getFrom_user();
                if (userInfo == null) {
                    userInfo = new UserInfo();
                    userInfo.setName(StringUtil.isNotNull(msg.getFrom_group_nickname()) ? msg.getFrom_group_nickname() : msg.getFrom_nickname());
                    userInfo.setHead(msg.getFrom_avatar());
                } else {
                    if (isGroup()) {
                        String gname = "";//获取对方最新的群昵称
                        MsgAllBean gmsg = msgDao.msgGetLastGroup4Uid(gid, msg.getFrom_uid());
                        if (gmsg != null) {
                            gname = gmsg.getFrom_group_nickname();
                        }
                        if (StringUtil.isNotNull(gname)) {
                            userInfo.setName(gname);
                        }
                    }
                }
                mks.put(k, userInfo);
            }
            nkname = userInfo.getName();
            if (StringUtil.isNotNull(userInfo.getMkName())) {
                nkname = userInfo.getMkName();
            }
            head = userInfo.getHead();
            msg.setFrom_nickname(nkname);
            msg.setFrom_avatar(head);
        }
    }

    public void checkLockMessage() {
        if (!msgDao.isMsgLockExist(gid, uid)) {
            msgDao.insertOrUpdateMessage(msgAction.createMessageLock(gid, uid));
        }
    }

    public boolean isGroup() {
        return StringUtil.isNotNull(gid);
    }

    public String getUnreadCount() {
        long count = msgDao.getUnreadCount(gid, uid);
        String s = "";
        if (count > 0 && count <= 99) {
            s = count + "";
        } else if (count > 99) {
            s = 99 + "+";
        }
        return s;
    }

    public String getGid() {
        return gid;
    }

    public long getUid() {
        return uid;
    }

    public List<MsgAllBean> getListData() {
        return listData;
    }

    public void updateSendStatus(String msgId, int status) {
        msgDao.fixStataMsg(msgId, status);
    }

    public boolean isHaveDraft() {
        return msgDao.isSaveDraft(gid);
    }

    public Group getGroup() {
        if (group == null) {
            group = msgDao.getGroup4Id(gid);
        }
        return group;
    }

    public UserInfo getUserInfo() {
        if (userInfo == null) {
            userInfo = userDao.findUserInfo(uid);
        }
        return userInfo;
    }


    public Observable<List<MsgAllBean>> loadMoreMessages() {
        long time = -1L;
        int length = 0;
        if (listData != null && listData.size() > 0) {
            length = listData.size();
            if (length >= 20) {
                MsgAllBean bean = listData.get(length - 1);
                if (bean != null && bean.getTimestamp() != null) {
                    time = bean.getTimestamp();
                }
            }
        }
        final long finalTime = time;
        if (length < 20) {
            length += 20;
        }
        final int finalLength = length;
        return Observable.create(new ObservableOnSubscribe<List<MsgAllBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<MsgAllBean>> e) throws Exception {
                if (msgDao == null) {
                    msgDao = new MsgDao();
                }
                if (finalLength >= 20) {
                    listData.addAll(0, msgAction.getMsg4User(gid, uid, finalTime, false));
                } else {
                    listData = msgAction.getMsg4User(gid, uid, null, false);
                }
                taskMkName(listData);
                e.onNext(listData);
            }
        });
    }

    public int getTotalSize() {
        return listData != null ? listData.size() : 0;
    }

    public Session getSession() {
        if (session == null) {
            msgDao.sessionGet(gid, uid);
        }
        return session;
    }

    public void updateDraft(String draft) {
        msgDao.sessionDraft(gid, uid, draft);
    }

    /***
     * 清理未读
     */
    public void clearUnreadCount() {
        if (isGroup()) {
            msgDao.sessionReadClean(gid, null);
        } else {
            msgDao.sessionReadClean(null, uid);
        }

    }


}
