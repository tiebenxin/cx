package com.yanlong.im.chat.ui.chat;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.EnvelopeInfo;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.GroupConfig;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.UserUtil;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.view.function.FunctionItemModel;

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
 * @author Liszt
 * @date 2019/9/19
 * Description
 */
public class ChatModel implements IModel {
    private final String IS_VIP = "1";// (0:普通|1:vip)

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
    private GroupConfig groupConfig;


    public final void init(String gid, long uid) {
        this.gid = gid;
        this.uid = uid;
    }

    public final Observable<List<MsgAllBean>> loadMessages() {
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
        if (length < 80) {
            length += 80;
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
                    list = msgAction.getMsg4User(gid, uid, null, 80);
                }
                if (list != null) {
                    listData.addAll(0, list);
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
    private final void taskMkName(List<MsgAllBean> msgListData) {
        mks.clear();
        for (MsgAllBean msg : msgListData) {
            if (msg.getMsg_type() == ChatEnum.EMessageType.NOTICE || msg.getMsg_type() == ChatEnum.EMessageType.MSG_CANCEL || msg.getMsg_type() == ChatEnum.EMessageType.LOCK) {  //通知类型的不处理
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

    public final void checkLockMessage() {
        if (!msgDao.isMsgLockExist(gid, uid)) {
            msgDao.insertOrUpdateMessage(SocketData.createMessageLock(gid, uid));
        }
    }

    public final boolean isGroup() {
        return StringUtil.isNotNull(gid);
    }

    public final String getUnreadCount() {
        long count = msgDao.getUnreadCount(gid, uid);
        String s = "";
        if (count > 0 && count <= 99) {
            s = count + "";
        } else if (count > 99) {
            s = 99 + "+";
        }
        return s;
    }

    public final String getGid() {
        return gid;
    }

    public final long getUid() {
        return uid;
    }

    public final List<MsgAllBean> getListData() {
        return listData;
    }

    public final void updateSendStatus(String msgId, int status) {
        msgDao.fixStataMsg(msgId, status);
    }

    public final boolean isHaveDraft() {
        return msgDao.isSaveDraft(gid);
    }

    public final Group getGroup() {
        if (group == null) {
            group = msgDao.getGroup4Id(gid);
        }
        return group;
    }

    public final void setGroup(Group g) {
        group = g;
    }

    public final UserInfo getUserInfo() {
        if (userInfo == null) {
            userInfo = userDao.findUserInfo(uid);
        }
        return userInfo;
    }


    public final Observable<List<MsgAllBean>> loadMoreMessages() {
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

    public final int getTotalSize() {
        return listData != null ? listData.size() : 0;
    }

    public final Session getSession() {
        if (session == null) {
            msgDao.sessionGet(gid, uid);
        }
        return session;
    }

    public final void updateDraft(String draft) {
        msgDao.sessionDraft(gid, uid, draft);
    }

    /***
     * 清理未读
     */
    public final void clearUnreadCount() {
        if (isGroup()) {
            msgDao.sessionReadClean(gid, null);
        } else {
            msgDao.sessionReadClean(null, uid);
        }
    }

    public final GroupConfig getGroupConfig() {
        if (groupConfig == null) {
            groupConfig = msgDao.groupConfigGet(gid);
        }
        return groupConfig;
    }

    public String getGroupName() {
        return msgDao.getGroupName(gid);
    }

    //修正msgBean, 确保msgListData中是最新的数据
    public final MsgAllBean amendMsgALlBean(int position, MsgAllBean bean) {
        if (listData != null && position < listData.size()) {
            MsgAllBean msg = listData.get(position);
            if (msg.getMsg_id().equals(bean.getMsg_id())) {
                return msg;
            } else {
                int p = listData.indexOf(bean);
                if (p >= 0) {
                    return listData.get(p);
                }
            }
        }
        return bean;
    }

    public final void updateReadStatus(String msgId, boolean isRead) {
        msgAction.msgRead(msgId, isRead);
    }

    public final void updatePlayStatus(String msgId, @ChatEnum.EPlayStatus int status) {
        msgDao.updatePlayStatus(msgId, status);
    }

    public final Observable<List<MsgAllBean>> loadHistoryMessages(final long time) {
        return Observable.create(new ObservableOnSubscribe<List<MsgAllBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<MsgAllBean>> e) throws Exception {
                if (msgDao == null) {
                    msgDao = new MsgDao();
                }
                listData = msgAction.getMsg4UserHistory(gid, uid, time);
                taskMkName(listData);
                e.onNext(listData);
            }
        });
    }

    public List<FunctionItemModel> getItemModels() {
        boolean isGroup = isGroup();
        boolean isVip = false;
        boolean isSystemUser = false;
        if (!isGroup) {
            UserInfo userInfo = UserAction.getMyInfo();
            if (userInfo != null && IS_VIP.equals(userInfo.getVip())) {
                isVip = true;
            }
            if (UserUtil.isSystemUser(uid)) {
                isSystemUser = true;
            }
        }
        List<FunctionItemModel> list = new ArrayList<>();
        list.add(createItemMode("相册", R.mipmap.ic_chat_pic, ChatEnum.EFunctionId.GALLERY));
        list.add(createItemMode("拍摄", R.mipmap.ic_chat_pt, ChatEnum.EFunctionId.TAKE_PHOTO));
        if (!isSystemUser) {
//            list.add(createItemMode("零钱红包", R.mipmap.ic_chat_rb, ChatEnum.EFunctionId.ENVELOPE_SYS));
        }
        if (!isGroup && !isSystemUser) {
//            list.add(createItemMode("零钱转账", R.mipmap.ic_chat_transfer, ChatEnum.EFunctionId.TRANSFER));
        }
        if (!isGroup && isVip) {
            list.add(createItemMode("视频通话", R.mipmap.ic_chat_video, ChatEnum.EFunctionId.VIDEO_CALL));
        }
        if (!isSystemUser) {
            list.add(createItemMode("云红包", R.mipmap.ic_chat_rb_zfb, ChatEnum.EFunctionId.ENVELOPE_MF));
        }
        list.add(createItemMode("位置", R.mipmap.location_six, ChatEnum.EFunctionId.LOCATION));
        if (!isGroup && !isSystemUser) {
            list.add(createItemMode("戳一下", R.mipmap.ic_chat_action, ChatEnum.EFunctionId.STAMP));
        }
        if (!isSystemUser) {
            list.add(createItemMode("名片", R.mipmap.ic_chat_newfrd, ChatEnum.EFunctionId.CARD));
        }
        if (isGroup) {
            //本人群主
            if (UserAction.getMyId() != null && group != null && group.getMaster().equals(UserAction.getMyId().toString())) {
                list.add(createItemMode("群助手", R.mipmap.ic_chat_robot, ChatEnum.EFunctionId.GROUP_ASSISTANT));
            }
        }
//        list.add(createItemMode("文件", R.mipmap.ic_chat_file, ChatEnum.EFunctionId.FILE));
        return list;
    }

    public FunctionItemModel createItemMode(String name, int drawableId, @ChatEnum.EFunctionId int functionId) {
        FunctionItemModel model = new FunctionItemModel();
        model.setName(name);
        model.setDrawableId(drawableId);
        model.setId(functionId);
        return model;
    }

    public EnvelopeInfo queryEnvelopeInfo() {
        return msgDao.queryEnvelopeInfo(gid, uid);
    }

    public void deleteEnvelopeInfo(String rid, String gid, long uid, boolean isDelete) {
        msgDao.deleteEnvelopeInfo(rid, gid, uid, isDelete);

    }

    public void updateEnvelopeInfo(EnvelopeInfo envelopeInfo) {
        msgDao.updateEnvelopeInfo(envelopeInfo);
    }

    //更新红包状态
    public void updateEnvelope(String rid, int envelopeStatus, int reType, String token) {
        msgDao.redEnvelopeOpen(rid, envelopeStatus, reType, token);

    }
}
