package com.yanlong.im.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yanlong.im.MyAppLication;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.ApplyBean;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.GroupConfig;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgCancel;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * 数据库查询
 * 定义需要多个场景调用的数据操作，单个场景不建议写入
 *
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/5 0005
 * @description
 */
public class DB {
    /**
     * 更新好友截屏通知开关
     *
     * @param type 0:未开启,1:开启
     */
    public static void updateFriendSnapshot(Realm realm, long uid, int type) {
        try {
            UserInfo user = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
            if (user != null) {
                realm.beginTransaction();
                user.setScreenshotNotification(type);
                realm.commitTransaction();
            }
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
    }

    /***
     * 更新阅后即焚状态
     */
    public static void updateSurvivalTime(Realm realm, String gid, Long uid, int type) {
        try {
            realm.beginTransaction();
            if (TextUtils.isEmpty(gid)) {
                UserInfo userInfo = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
                if (userInfo != null) {
                    userInfo.setDestroy(type);
                }
            } else {
                Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
                if (group != null) {
                    group.setSurvivaltime(type);
                }
            }


            realm.commitTransaction();
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }

    }

    /**
     * 撤回消息
     *
     * @param msgid       消息ID
     * @param msgCancelId
     */
    public static void deleteMsg4Cancel(Realm realm, String msgid, String msgCancelId) {
        MsgAllBean msgAllBean = null;
        try {
            realm.beginTransaction();
            RealmResults<MsgAllBean> list = null;

            list = realm.where(MsgAllBean.class).equalTo("msg_id", msgCancelId).findAll();
            MsgAllBean cancel = realm.where(MsgAllBean.class).equalTo("msg_id", msgid).findFirst();
            boolean isFromRealm = true;
            if (cancel == null && list != null && list.size() > 0) {
                MsgAllBean bean = list.get(0);
                if (TextUtils.isEmpty(bean.getMsg_id())) {
                    return;
                }
                isFromRealm = false;
                cancel = new MsgAllBean();
                cancel.setMsg_id(msgid);
                cancel.setRequest_id("" + System.currentTimeMillis());
                cancel.setFrom_uid(bean.getTo_uid());
                cancel.setTo_uid(UserAction.getMyId());
                cancel.setGid(bean.getGid());
                cancel.setMsg_type(ChatEnum.EMessageType.MSG_CANCEL);
                int survivaltime = new UserDao().getReadDestroy(bean.getTo_uid(), bean.getGid());
                MsgCancel msgCel = new MsgCancel();
                msgCel.setMsgid(msgid);
                msgCel.setNote("你撤回了一条消息");
                msgCel.setMsgidCancel(msgCancelId);
                cancel.setSurvival_time(survivaltime);
                cancel.setMsgCancel(msgCel);
            }

            //删除前先把子表数据干掉!!切记
            if (list != null) {
                for (MsgAllBean msg : list) {
                    deleteRealmMsg(msg);
                    if (cancel != null) {
                        cancel.setTimestamp(msg.getTimestamp());
                        realm.insertOrUpdate(cancel);
                    }
                }
                list.deleteAllFromRealm();
            }
            if (cancel != null) {
                if (isFromRealm) {
                    msgAllBean = realm.copyFromRealm(cancel);
                } else {
//                    msgAllBean = cancel;
                }
            }
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
        if (msgAllBean != null) {
            /********通知更新sessionDetail************************************/
            //因为msg对象 uid有两个，都得添加
            List<String> gids = new ArrayList<>();
            List<Long> uids = new ArrayList<>();
            //gid存在时，不取uid
            if (TextUtils.isEmpty(msgAllBean.getGid())) {
                uids.add(msgAllBean.getTo_uid());
                uids.add(msgAllBean.getFrom_uid());
            } else {
                gids.add(msgAllBean.getGid());
            }
            //回主线程调用更新session详情
            if (MyAppLication.INSTANCE().repository != null)
                MyAppLication.INSTANCE().repository.updateSessionDetail(gids, uids);
            /********通知更新sessionDetail end************************************/
        }

    }

    /**
     * 更新新的群邀请 状态
     *
     * @param realm
     * @param gid
     * @param inviter
     * @param status
     */
    public static void updateGroupApply(Realm realm, String gid, long inviter, int status) {
        try {
            realm.beginTransaction();
            ApplyBean bean = realm.where(ApplyBean.class)
                    .beginGroup().equalTo("gid", gid).endGroup()
                    .and()
                    .beginGroup().equalTo("uid", inviter).endGroup()
                    .findFirst();
            if (bean != null && bean.getStat() == 1) {
                bean.setStat(status);
            }
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
    }

    /**
     * 移除群成员
     *
     * @param realm
     * @param gid
     * @param uids
     */
    public static void removeGroupMember(Realm realm, String gid, List<Long> uids) {
        try {
            if (uids == null) {
                return;
            }
            Long[] uidArr = uids.toArray(new Long[uids.size()]);
            if (uidArr == null) {
                return;
            }
            realm.beginTransaction();
            Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
            if (group != null) {
                RealmList<MemberUser> list = group.getUsers();
                if (list != null) {
                    RealmResults<MemberUser> results = list.where().in("uid", uidArr).findAll();
                    if (results != null) {
                        results.deleteAllFromRealm();
                    }
                }
            }
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
    }

    /**
     * 判断当前用户是否群主或者群管理员
     *
     * @param gid
     * @param uid
     * @return
     */
    public static boolean isGroupMasterOrManager(@NonNull Realm realm, String gid, long uid) {
        if (TextUtils.isEmpty(gid) || uid <= 0) {
            return false;
        }
        boolean result = false;
        try {
            Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
            if (group != null) {
                if (!TextUtils.isEmpty(group.getMaster()) && group.getMaster().equals(uid + "")) {
                    result = true;
                } else {
                    RealmList<Long> viceAdmins = group.getViceAdmins();
                    if (viceAdmins != null && viceAdmins.contains(uid)) {
                        result = true;
                    }
                }
            }
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
        return result;
    }

    /**
     * 获取群
     *
     * @param realm
     * @param gid
     * @return
     */
    public static Group getGroup(@NonNull Realm realm, String gid) {
        if (!TextUtils.isEmpty(gid)) {
            return realm.where(Group.class).equalTo("gid", gid).findFirst();
        } else {
            return null;
        }
    }

    /**
     * 获取好友个人信息
     *
     * @param realm
     * @param uid
     * @return
     */
    public static UserInfo getFriend(@NonNull Realm realm, long uid) {
        return realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
    }

    /**
     * 更新对象
     *
     * @param realm
     * @param obj
     * @return
     */
    public static boolean updateObject(@NonNull Realm realm, RealmModel obj) {
        if (obj == null)
            return false;
        try {
            realm.beginTransaction();
            realm.insertOrUpdate(obj);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
        return false;
    }

    /***
     * 更新从自己PC端发过来的session,只更新时间
     */
    public static void updateFromSelfPCSession(@NonNull Realm realm, MsgAllBean bean) {
        try {
            Session session = null;
            if (StringUtil.isNotNull(bean.getGid())) {//群消息
                session = realm.where(Session.class).equalTo("gid", bean.getGid()).findFirst();
            } else { //单聊-touid才是对方id
                session = realm.where(Session.class).equalTo("from_uid", bean.getTo_uid()).findFirst();
            }
            realm.beginTransaction();
            if (session != null) {//已存在的session，只更新时间
                session.setUp_time(System.currentTimeMillis());
            } else {//新session
                if (StringUtil.isNotNull(bean.getGid())) {//群消息
                    session = new Session();
                    session.setSid(UUID.randomUUID().toString());
                    session.setGid(bean.getGid());
                    session.setType(1);
                    Group group = realm.where(Group.class).equalTo("gid", bean.getGid()).findFirst();
                    if (group != null) {
                        //因getIsTop有写入操作，beginTransaction得写在前面
                        session.setIsTop(group.getIsTop());
                        session.setIsMute(group.getNotNotify());
                    }
                } else {//个人消息
                    session = new Session();
                    session.setSid(UUID.randomUUID().toString());
                    session.setFrom_uid(bean.getTo_uid());
                    session.setType(0);
                    UserInfo user = realm.where(UserInfo.class).equalTo("uid", bean.getTo_uid()).findFirst();
                    if (user != null) {
                        //因getIsTop有写入操作，beginTransaction得写在前面
                        session.setIsTop(user.getIstop());
                        session.setIsMute(user.getDisturb());
                    }
                }
                session.setUnread_count(0);
                session.setUp_time(System.currentTimeMillis());
                realm.insertOrUpdate(session);

            }
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
    }

    /***
     * 好友头像,昵称更新
     * @param uid
     * @param portrait 头像
     * @param name 昵称
     */
    public static boolean updateFriendPortraitAndName(@NonNull Realm realm, Long uid, String portrait, String name) {
        boolean hasChange = false;
        if (uid == null)
            return false;
        try {
            UserInfo u = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
            if (u != null) {
                if (!u.getHead().equals(portrait) || !u.getName().equals(name)) {
                    hasChange = true;
                    realm.beginTransaction();
                    u.setHead(portrait);
                    u.setName(name);
                    realm.insertOrUpdate(u);
                    realm.commitTransaction();
                }
            }
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
        return hasChange;
    }


    /**
     * 删除好友---自己删好友
     *
     * @param realm
     * @param uid
     */
    public static void deleteFriend(@NonNull Realm realm, long uid) {
        try {
            //删除session会话
            Session session = realm.where(Session.class).equalTo("from_uid", uid).findFirst();
            realm.beginTransaction();
            if (session != null) {
                String sid = session.getSid();
                session.deleteFromRealm();
                //删除某个session detial
                SessionDetail sessionDetail = realm.where(SessionDetail.class).equalTo("sid", sid).findFirst();
                if (sessionDetail != null) sessionDetail.deleteFromRealm();
            }
            //删除所有聊天记录
            RealmResults<MsgAllBean> list = realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                    .and()
                    .beginGroup().equalTo("from_uid", uid).or().equalTo("to_uid", uid).endGroup()
                    .findAll();
            //删除前先把子表数据
            if (list != null) {
                MsgDao msgDao = new MsgDao();
                for (MsgAllBean msg : list) {
                    msgDao.deleteRealmMsg(msg);
                }
                list.deleteAllFromRealm();
            }
            //删除好友
            UserInfo user = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
            if (user != null) {
                user.deleteFromRealm();
            }
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
    }

    /**
     * 删除群聊 --自己退群
     *
     * @param realm
     * @param gid
     */
    public static void deleteGroup(@NonNull Realm realm, String gid) {
        try {
            //删除session会话
            Session session = realm.where(Session.class).equalTo("gid", gid).findFirst();
            realm.beginTransaction();
            if (session != null) {
                String sid = session.getSid();
                session.deleteFromRealm();
                //删除某个session detial
                SessionDetail sessionDetail = realm.where(SessionDetail.class).equalTo("sid", sid).findFirst();
                if (sessionDetail != null) sessionDetail.deleteFromRealm();
            }
            //删除所有聊天记录
            RealmResults<MsgAllBean> list = realm.where(MsgAllBean.class)
                    .equalTo("gid", gid)
                    .findAll();
            //删除前先把子表数据
            if (list != null) {
                MsgDao msgDao = new MsgDao();
                for (MsgAllBean msg : list) {
                    msgDao.deleteRealmMsg(msg);
                }
                list.deleteAllFromRealm();
            }
            //删除群
            Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
            if (group != null) {
                group.deleteFromRealm();
            }
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
    }

    //移出群成员
    public static void removeGroupMember(@NonNull Realm realm, String gid, long uid) {
        try {

            Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
            if (group != null) {
                RealmList<MemberUser> list = group.getUsers();
                MemberUser memberUser = list.where().equalTo("uid", uid).findFirst();
                if (memberUser != null) {
                    realm.beginTransaction();
                    list.remove(memberUser);
                    realm.commitTransaction();
                }
            }
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
    }

    /***
     * 保存群成员到数据库
     * @param
     */
    public static void updateGroup(@NonNull Realm realm, Group ginfo) {
        try {
            if (ginfo.getUsers() != null) {
                //更新信息到用户表
                for (MemberUser sv : ginfo.getUsers()) {
                    sv.init(ginfo.getGid());
                }
            }
            realm.beginTransaction();
            realm.insertOrUpdate(ginfo);
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
    }

    /*
     * 更新群，或用户信息更新，更新session置顶免打扰字段
     * */
    public static void updateSessionTopAndDisturb(@NonNull Realm realm, String gid, Long from_uid, int top, int disturb) {
        try {
            if (StringUtil.isNotNull(gid)) {//群消息
                Session session = realm.where(Session.class).equalTo("gid", gid).findFirst();
                if (session != null) {
                    realm.beginTransaction();
                    session.setIsMute(disturb);
                    session.setIsTop(top);
                    if (disturb == 1) {
                        session.setUnread_count(0);
                    }
                    realm.commitTransaction();
                }
            } else {//个人消息
                Session session = realm.where(Session.class).equalTo("from_uid", from_uid).findFirst();
                if (session != null) {
                    realm.beginTransaction();
                    session.setIsMute(disturb);
                    session.setIsTop(top);
                    if (disturb == 1) {
                        session.setUnread_count(0);
                    }
                    realm.commitTransaction();
                }
            }
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
    }

    /***
     * 纯更好友个人信息
     * @param userInfo
     */
    public static void updateUserInfo(@NonNull Realm realm, UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        try {
            realm.beginTransaction();
            if (TextUtils.isEmpty(userInfo.getTag())) {
                userInfo.toTag();
            }
            realm.copyToRealmOrUpdate(userInfo);
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
    }

    /***
     * 纯更新用户信息
     * @param userInfo
     */
    public static void updateUserBean(@NonNull Realm realm, UserBean userInfo) {
        if (userInfo == null) {
            return;
        }
        try {
            realm.beginTransaction();
            if (TextUtils.isEmpty(userInfo.getTag())) {
                userInfo.toTag();
            }
            realm.copyToRealmOrUpdate(userInfo);
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
    }

    /**
     * 更新好友在线状态
     *
     * @param type 0:不在线,1:在线
     * @param time 离线需要更新离线时间
     */
    public static void updateUserOnlineStatus(@NonNull Realm realm, Long uid, int type, long time) {
        try {
            realm.beginTransaction();
            UserInfo user = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
            if (user != null) {
                if (user != null) {
                    user.setActiveType(type);
                    if (type == CoreEnum.ESureType.NO) {
                        user.setLastonline(time);
                    }
                    realm.insertOrUpdate(user);
                }
            }
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
    }

    /**
     * 获取群显示名称
     *
     * @param realm 数据库对象，不能为null
     * @param gid
     * @param group 可为null,则从数据库查找
     * @return
     */
    public static String getGroupName(Realm realm, String gid, Group group) {
        if (group == null) {
            if (realm == null) return "";
            group = realm.where(Group.class).equalTo("gid", gid).findFirst();
        }
        if (group == null) {
            return "";
        }
        String result = group.getName();
        if (TextUtils.isEmpty(result)) {
            List<MemberUser> users = group.getUsers();
            if (users != null && users.size() > 0) {
                int len = users.size();
                for (int i = 0; i < len; i++) {
                    MemberUser info = users.get(i);
                    if (i == len - 1) {
                        result += StringUtil.getUserName("", info.getMembername(), info.getName(), info.getUid());
                    } else {
                        result += StringUtil.getUserName(/*info.getMkName()*/"", info.getMembername(), info.getName(), info.getUid()) + "、";
                    }
                }
                result = result.length() > 14 ? StringUtil.splitEmojiString2(result, 0, 14) : result;
                result += "的群";
            }
        }
        return result;
    }


    /***
     * 群解散,退出的配置
     * @param gid
     * @param isExit
     */
    public static void groupExit(@NonNull Realm realm, final String gid, final String gname, final String gicon, final int isExit) {
        try {
            realm.beginTransaction();
            GroupConfig groupConfig = realm.where(GroupConfig.class).equalTo("gid", gid).findFirst();
            if (groupConfig == null) {
                groupConfig = new GroupConfig();
                groupConfig.setGid(gid);
            }
            groupConfig.setIsExit(isExit);
            realm.insertOrUpdate(groupConfig);

            Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
            if (group == null) {
                group = new Group();
                group.setGid(gid);
            }
            if (gname != null)
                group.setName(gname);
            if (gicon != null)
                group.setAvatar(gicon);
            realm.insertOrUpdate(group);
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
    }

    public static void deleteRealmMsg(@NonNull MsgAllBean msg) {
        if (msg.getReceive_red_envelope() != null)
            msg.getReceive_red_envelope().deleteFromRealm();
        if (msg.getMsgNotice() != null)
            msg.getMsgNotice().deleteFromRealm();
        if (msg.getBusiness_card() != null)
            msg.getBusiness_card().deleteFromRealm();
        if (msg.getStamp() != null)
            msg.getStamp().deleteFromRealm();
        if (msg.getChat() != null)
            msg.getChat().deleteFromRealm();
        if (msg.getImage() != null)
            msg.getImage().deleteFromRealm();
        if (msg.getRed_envelope() != null)
            msg.getRed_envelope().deleteFromRealm();
        if (msg.getTransfer() != null)
            msg.getTransfer().deleteFromRealm();
        if (msg.getMsgCancel() != null)
            msg.getMsgCancel().deleteFromRealm();
        if (msg.getVoiceMessage() != null)
            msg.getVoiceMessage().deleteFromRealm();
        if (msg.getVideoMessage() != null)
            msg.getVideoMessage().deleteFromRealm();
        if (msg.getAtMessage() != null)
            msg.getAtMessage().deleteFromRealm();
        if (msg.getAssistantMessage() != null)
            msg.getAssistantMessage().deleteFromRealm();
        if (msg.getChangeSurvivalTimeMessage() != null)
            msg.getChangeSurvivalTimeMessage().deleteFromRealm();
        if (msg.getP2PAuVideoDialMessage() != null)
            msg.getP2PAuVideoDialMessage().deleteFromRealm();
        if (msg.getP2PAuVideoMessage() != null)
            msg.getP2PAuVideoMessage().deleteFromRealm();
        if (msg.getBalanceAssistantMessage() != null)
            msg.getBalanceAssistantMessage().deleteFromRealm();
        if (msg.getLocationMessage() != null)
            msg.getLocationMessage().deleteFromRealm();
        if (msg.getTransferNoticeMessage() != null)
            msg.getTransferNoticeMessage().deleteFromRealm();
        if (msg.getShippedExpressionMessage() != null)
            msg.getShippedExpressionMessage().deleteFromRealm();
        if (msg.getSendFileMessage() != null)
            msg.getSendFileMessage().deleteFromRealm();
        if (msg.getWebMessage() != null)
            msg.getWebMessage().deleteFromRealm();
        if (msg.getAdMessage() != null)
            msg.getAdMessage().deleteFromRealm();
        if (msg.getReplyMessage() != null) {
            if (msg.getReplyMessage().getAtMessage() != null)
                msg.getReplyMessage().getAtMessage().deleteFromRealm();
            if (msg.getReplyMessage().getChatMessage() != null)
                msg.getReplyMessage().getChatMessage().deleteFromRealm();
            if (msg.getReplyMessage().getQuotedMessage() != null)
                msg.getReplyMessage().getQuotedMessage().deleteFromRealm();
            msg.getReplyMessage().deleteFromRealm();
        }
    }

    /**
     * 撤回消息
     *
     * @param msgid       消息ID
     * @param msgCancelId
     */
    public static void deleteMsg(Realm realm, String msgId) {
        MsgAllBean msgAllBean = null;
        try {
            realm.beginTransaction();
            RealmResults<MsgAllBean> list = null;
            list = realm.where(MsgAllBean.class).equalTo("msg_id", msgId).findAll();
            if (list != null) {
                for (MsgAllBean msg : list) {
                    msgAllBean = realm.copyFromRealm(msg);
                    deleteRealmMsg(msg);
                }
                list.deleteAllFromRealm();
            }
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            DaoUtil.reportException(e);
            LogUtil.writeError(e);
        }
        if (msgAllBean != null) {
            /********通知更新sessionDetail************************************/
            //因为msg对象 uid有两个，都得添加
            List<String> gids = new ArrayList<>();
            List<Long> uids = new ArrayList<>();
            //gid存在时，不取uid
            if (TextUtils.isEmpty(msgAllBean.getGid())) {
                uids.add(msgAllBean.getTo_uid());
                uids.add(msgAllBean.getFrom_uid());
            } else {
                gids.add(msgAllBean.getGid());
            }
            //回主线程调用更新session详情
            if (MyAppLication.INSTANCE().repository != null)
                MyAppLication.INSTANCE().repository.updateSessionDetail(gids, uids);
        }

    }


}
