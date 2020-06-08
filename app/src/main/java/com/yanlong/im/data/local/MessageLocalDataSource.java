package com.yanlong.im.data.local;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.hm.cxpay.global.PayEnum;
import com.luck.picture.lib.tools.DateUtils;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.ApplyBean;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Remind;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.TransferMessage;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DB;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/5 0005
 * @description
 */
public class MessageLocalDataSource {
    private Realm realm;

    //重试时间
    private long RETRY_DELAY = 100;
    private Handler handler = null;

    public void initRealm(Realm realm) {
        this.realm = realm;
        if (handler != null) handler = null;
        handler = new Handler();
    }


    /**
     * 数据库开始事务处理
     */
    public void beginTransaction() {
        realm.beginTransaction();
    }

    /**
     * 数据库提交事务处理
     */
    public void commitTransaction() {
        realm.commitTransaction();
    }


    public Realm getRealm() {
        return realm;
    }

    /**
     * 检查是否在写入
     *
     * @param runnable
     * @return
     */
    private boolean checkInTranction(Runnable runnable) {
        if (realm.isInTransaction()) {
            Log.e("raleigh_test", "checkInTranction start");
            try {
                Thread.currentThread().wait(RETRY_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runnable.run();
            //正在事务，100毫秒后重试
            Log.e("raleigh_test", "checkInTranction");
            return true;
        } else {
            return false;
        }
    }


    /**
     * 保存申请消息 添加好友、申请入群
     *
     * @param bean
     */
    public void saveApplyBean(ApplyBean bean) {
        try {
            if (checkInTranction(() -> saveApplyBean(bean)))
                return;
            realm.beginTransaction();
            bean.setTime(System.currentTimeMillis());
            realm.insertOrUpdate(bean);
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.reportException(e);
        }
    }

    /***
     * 红点数量加一
     * @param type
     */
    public void addRemindCount(String type) {
        try {
            if (checkInTranction(() -> addRemindCount(type)))
                return;
            realm.beginTransaction();
            Remind remind = realm.where(Remind.class).equalTo("remid_type", type).findFirst();
            int readnum = remind == null ? 1 : remind.getNumber() + 1;
            Remind newreamid = new Remind();
            newreamid.setNumber(readnum);
            newreamid.setRemid_type(type);
            realm.insertOrUpdate(newreamid);
            realm.commitTransaction();
        } catch (Exception e) {
            DaoUtil.reportException(e);
        }
    }


    /***双向删除
     * 删除好友某时间戳之前的聊天记录-单聊
     * @param fromUid 发的指令对方
     * @param beforeTimestamp 最后时间戳
     */
    public void messageHistoryClean(Long fromUid, long beforeTimestamp) {
        try {
            if (checkInTranction(() -> messageHistoryClean(fromUid, beforeTimestamp)))
                return;
            realm.beginTransaction();
            RealmResults<MsgAllBean> list = realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                    .and()
                    .beginGroup().notEqualTo("msg_type", ChatEnum.EMessageType.LOCK).endGroup()
                    .and()
                    .beginGroup().equalTo("from_uid", fromUid).or().equalTo("to_uid", fromUid).endGroup()
                    .lessThan("timestamp", beforeTimestamp)
                    .findAll();
//            //因为msg对象 uid有两个，都得添加
            List<Long> uids = new ArrayList<Long>();

            int deleteUnReadCount = 0;
            //删除前先把子表数据干掉!!切记
            if (list != null) {
                for (MsgAllBean msg : list) {
                    if (!msg.isRead()) {
                        deleteUnReadCount++;
                    }
                    uids.add(msg.getTo_uid());
                    uids.add(msg.getFrom_uid());
                    DB.deleteRealmMsg(msg);
                }
                list.deleteAllFromRealm();

                if (deleteUnReadCount > 0) {
                    /***更新未读数-Session更新，自动会更新sessionDetail****/
                    Session session = realm.where(Session.class)
                            .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                            .and()
                            .beginGroup().equalTo("from_uid", fromUid).or().equalTo("from_uid", new UserAction().getMyInfo().getUid()).endGroup()
                            .findFirst();
                    int unreadCount = session.getUnread_count() - deleteUnReadCount;
                    session.setUnread_count(unreadCount > 0 ? unreadCount : 0);
                }

                realm.commitTransaction();
                //更新session
                if (uids.size() > 0 && deleteUnReadCount == 0) {//没有更新session,则需手动更新sessiondetail
                    /********通知更新sessionDetail************************************/
                    MyAppLication.INSTANCE().repository.updateSessionDetail(null, uids);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.reportException(e);
        }
    }


    /***
     * 群解散,退出的配置
     * @param gid
     * @param isExit
     */
    public void groupExit(String gid, String gname, String gicon, int isExit) {
        if (checkInTranction(() -> groupExit(gid, gname, gicon, isExit)))
            return;
        DB.groupExit(realm, gid, gname, gicon, isExit);
    }

    /**
     * 在线状态改变
     *
     * @param uid
     * @param type
     * @param time
     */
    public void updateUserOnlineStatus(Long uid, int type, long time) {
        if (checkInTranction(() -> updateUserOnlineStatus(uid, type, time)))
            return;
        DB.updateUserOnlineStatus(realm, uid, type, time);
    }

    /**
     * 更新用户信息-自己
     *
     * @param userBean
     */
    public void updateUserBean(UserBean userBean) {
        if (checkInTranction(() -> updateUserBean(userBean)))
            return;
        DB.updateUserBean(realm, userBean);
    }

    /**
     * 更新好友个人信息
     *
     * @param userInfo
     */
    public void updateUserInfo(UserInfo userInfo) {
        if (checkInTranction(() -> updateUserInfo(userInfo)))
            return;
        DB.updateUserInfo(realm, userInfo);
    }

    /**
     * 更新session  置顶和免打扰状态
     *
     * @param gid
     * @param from_uid
     * @param top
     * @param disturb
     */
    public void updateSessionTopAndDisturb(String gid, Long from_uid, int top, int disturb) {
        if (checkInTranction(() -> updateSessionTopAndDisturb(gid, from_uid, top, disturb)))
            return;
        DB.updateSessionTopAndDisturb(realm, gid, from_uid, top, disturb);
    }

    /**
     * 更新群信息
     */
    public void updateGroup(Group group) {
        try {
            if (checkInTranction(() -> updateGroup(group)))
                return;
            DB.updateGroup(realm, group);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将群成员从群里移除
     *
     * @param gid
     * @param uid
     */
    public void removeGroupMember(String gid, long uid) {
        if (checkInTranction(() -> removeGroupMember(gid, uid)))
            return;
        DB.removeGroupMember(realm, gid, uid);
    }

    /**
     * 移除群成员
     *
     * @param gid
     * @param uids
     */


    public void removeGroupMember(String gid, List<Long> uids) {
        if (checkInTranction(() -> removeGroupMember(gid, uids)))
            return;
        DB.removeGroupMember(realm, gid, uids);
    }

    /**
     * 自己退群
     *
     * @param gid
     */
    public void deleteGroup(String gid) {
        if (checkInTranction(() -> deleteGroup(gid)))
            return;
        DB.deleteGroup(realm, gid);
    }

    /**
     * 自己删除好友
     *
     * @param uid
     */
    public void deleteFriend(long uid) {
        if (checkInTranction(() -> deleteFriend(uid)))
            return;
        DB.deleteFriend(realm, uid);
    }

    /***
     * 好友头像,昵称更新
     * @param uid
     * @param portrait 头像
     * @param name 昵称
     */
    public boolean updateFriendPortraitAndName(Long uid, String portrait, String name) {
        if (checkInTranction(() -> updateFriendPortraitAndName(uid, portrait, name)))
            return false;
        return DB.updateFriendPortraitAndName(realm, uid, portrait, name);
    }

    /**
     * 更新对象
     *
     * @param obj
     * @return
     */
    public boolean updateObject(RealmModel obj) {
        if (checkInTranction(() -> updateObject(obj)))
            return false;
        return DB.updateObject(realm, obj);
    }

    /**
     * 更新从自己PC端发过来的session,只更新时间
     *
     * @param bean
     * @return
     */
    public void updateFromSelfPCSession(MsgAllBean bean) {
        if (checkInTranction(() -> updateFromSelfPCSession(bean)))
            return;
        DB.updateFromSelfPCSession(realm, bean);
    }

    /**
     * 获取好友个人信息
     *
     * @param uid
     * @return
     */
    public UserInfo getFriend(long uid) {
        return DB.getFriend(realm, uid);
    }

    /**
     * 获取群
     *
     * @param gid
     * @return
     */
    public Group getGroup(String gid) {
        return DB.getGroup(realm, gid);
    }

    /**
     * 自己是否群中成员
     *
     * @param group
     * @return
     */
    public boolean isMemberInGroup(Group group) {
        if (group == null || group.getUsers() == null) return false;
        List<MemberUser> users = group.getUsers();
        MemberUser member = new MemberUser();
        Long myUid = UserAction.getMyId();
        member.setUid(myUid == null ? 0 : myUid);
        if (users.contains(member)) {
            return true;
        } else return false;
    }

    /**
     * 同意添加为好友
     *
     * @param aid
     */
    public void acceptFriendRequest(String aid) {
        if (checkInTranction(() -> acceptFriendRequest(aid)))
            return;
        realm.executeTransaction(realm -> {
            ApplyBean applyBean1 = realm.where(ApplyBean.class).equalTo("aid", aid).findFirst();
            applyBean1.setStat(2);
            applyBean1.setTime(System.currentTimeMillis());
        });
    }

    /**
     * 更新新的群邀请 状态
     *
     * @param gid
     * @param inviter
     * @param status
     */
    public void updateGroupApply(String gid, long inviter, int status) {
        if (checkInTranction(() -> updateGroupApply(gid, inviter, status)))
            return;
        DB.updateGroupApply(realm, gid, inviter, status);
    }

    public void updateGroup(String gid, String name, Boolean intimately, Integer screenshotNotification,
                            Integer stat) {
        if (checkInTranction(() -> updateGroup(gid, name, intimately, screenshotNotification, stat)))
            return;
        Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
        if (group != null) {
            realm.beginTransaction();
            if (name != null) group.setName(name);
            if (intimately != null) group.setContactIntimately(intimately ? 1 : 0);
            if (screenshotNotification != null)
                group.setScreenshotNotification(screenshotNotification);
            if (stat != null) group.setStat(stat);
            realm.commitTransaction();
        }
    }

    /**
     * 撤回消息
     *
     * @param msgid       消息ID
     * @param msgCancelId
     */
    public void deleteMsg4Cancel(String msgid, String msgCancelId) {
        if (checkInTranction(() -> deleteMsg4Cancel(msgid, msgCancelId)))
            return;
        DB.deleteMsg4Cancel(realm, msgid, msgCancelId);
    }

    /***
     * 更新阅后即焚状态
     */
    public void updateSurvivalTime(String gid, Long uid, int type) {
        if (checkInTranction(() -> updateSurvivalTime(gid, uid, type)))
            return;
        DB.updateSurvivalTime(realm, gid, uid, type);
    }

    /***
     * 更新好友截屏通知开关
     */
    public void updateFriendSnapshot(Long uid, int type) {
        if (checkInTranction(() -> updateFriendSnapshot(uid, type)))
            return;
        DB.updateFriendSnapshot(realm, uid, type);
    }


    /**
     * 判断当前用户是否群主或者群管理员
     *
     * @param gid
     * @param uid
     * @return
     */
    public boolean isGroupMasterOrManager(String gid, long uid) {
        return DB.isGroupMasterOrManager(realm, gid, uid);
    }

    /**
     * 更新已读状态和阅后即焚
     * 单聊发送：自己发送成功且对方已读，立即加入阅后即焚
     */
    public void updateFriendMsgReadAndSurvivalTime(long uid, long timestamp) {
        try {
            if (checkInTranction(() -> updateFriendMsgReadAndSurvivalTime(uid, timestamp)))
                return;
            //查询出单聊和未读状态的消息
            RealmResults<MsgAllBean> friendChatMessages = realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                    .and()
                    .beginGroup().equalTo("to_uid", uid).endGroup()
                    .and()
                    .beginGroup().notEqualTo("read", 1).endGroup()
                    .findAll();
            if (friendChatMessages != null) {
                //每次修改后，friendChatMessages的size 会变化，直到全部修改完，friendChatMessages的size 为0
                while (friendChatMessages.size() != 0) {
                    MsgAllBean msgAllBean = friendChatMessages.get(0);
                    //防止手机上的时间与PC时间不一致情况，只处理手机比PC时间早的情况，即手机时间调慢了
                    long startTime = Math.min(timestamp, DateUtils.getSystemTime());
                    long endTime = startTime + msgAllBean.getSurvival_time() * 1000;

                    realm.beginTransaction();
                    if (msgAllBean.getSurvival_time() > 0) {//有设置阅后即焚
//                        if (endTime > DateUtils.getSystemTime()) {//还未到阅后即焚时间点，记录已读
                        msgAllBean.setRead(1);
                        msgAllBean.setReadTime(timestamp);
                        /**处理需要阅后即焚的消息***********************************/
                        msgAllBean.setStartTime(startTime);
                        msgAllBean.setEndTime(endTime);
//                        }
                    } else {//普通消息，记录已读状态和时间
                        msgAllBean.setRead(1);
                        msgAllBean.setReadTime(timestamp);
                    }
                    realm.commitTransaction();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.reportException(e);
        }
    }

    //更新转账状态
    public void updateTransferStatus(String tradeId, int opType) {
        try {
            if (checkInTranction(() -> updateTransferStatus(tradeId, opType)))
                return;
            realm.beginTransaction();
            TransferMessage transfer = realm.where(TransferMessage.class)
                    .beginGroup().equalTo("id", tradeId).endGroup()
                    .and()
                    .beginGroup().equalTo("opType", PayEnum.ETransferOpType.TRANS_SEND).endGroup()
                    .findFirst();
            if (transfer == null) {
                return;
            }
            transfer.setOpType(opType);
            realm.commitTransaction();
        } catch (Exception e) {
            DaoUtil.reportException(e);
        }
    }

    /**
     * 存session at消息
     */
    public void updateSessionAtMessage(String gid, String atMessage, int type) {
        if (checkInTranction(() -> updateSessionAtMessage(gid, atMessage, type)))
            return;
        try {
            realm.beginTransaction();
            Session session = realm.where(Session.class).equalTo("gid", gid).findFirst();

            if (session != null) {
                session.setAtMessage(atMessage);
                session.setMessageType(type);
                realm.insertOrUpdate(session);
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.reportException(e);
        }
    }

    /**同步自己PC端好友发送消息的已读状态和阅后即焚
     * 对方发送的消息-自己接收的消息，更新为已读
     * 更新消息已读
     */
    public void updateRecivedMsgReadForPC(String gid, Long uid, long timestamp) {
        if (checkInTranction(() -> updateRecivedMsgReadForPC(gid, uid, timestamp)))
            return;
        //查出已读前的消息，设置为已读
        RealmResults<MsgAllBean> msgAllBeans = TextUtils.isEmpty(gid) ?
                realm.where(MsgAllBean.class).equalTo("gid", gid)
                        .lessThanOrEqualTo("timestamp", timestamp)
                        .equalTo("isRead", false)
                        .findAll()
                :
                //查出已读前的消息，设置为已读,好友发送的消息
                realm.where(MsgAllBean.class)
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
        //校正session未读数
        correctSessionCount(gid, uid, timestamp);
    }

    /**
     * 校正session未读数
     * 前提条件：已知最后一条已读消息时间
     * @param gid
     * @param uid
     * @param timestamp
     */
    private void correctSessionCount(String gid, Long uid, long timestamp){
        if (checkInTranction(() -> correctSessionCount(gid, uid, timestamp)))
            return;
        Session session = StringUtil.isNotNull(gid) ? realm.where(Session.class).equalTo("gid", gid).findFirst() :
                realm.where(Session.class).equalTo("from_uid", uid).findFirst();
        if (session != null) {
            //好友之后发送的未读消息数量
            long unReadCount = TextUtils.isEmpty(gid) ?
                    realm.where(MsgAllBean.class).equalTo("gid", gid)
                            .greaterThan("timestamp", timestamp)
                            .equalTo("isRead", false)
                            .count()
                    :
                    realm.where(MsgAllBean.class)
                            .beginGroup().isEmpty("gid").or().isNull("gid").endGroup()
                            .equalTo("from_uid", uid)
                            .greaterThan("timestamp", timestamp)
                            .equalTo("isRead", false)
                            .count();
            realm.beginTransaction();
            //取最小值  剩余消息数量和当前未读数
            session.setUnread_count((int) Math.min(unReadCount,session.getUnread_count()));
            session.setAtMessage(null);
            realm.commitTransaction();
        }
    }

    /*
     * 更新或者创建session
     *
     * */
    public boolean updateSessionRead(String gid, Long from_uid, boolean canChangeUnread, MsgAllBean bean, String firstFlag) {
        if (checkInTranction(() -> updateSessionRead(gid, from_uid, canChangeUnread, bean, firstFlag)))
            return false;
        //是否是 撤回
        String cancelId = null;
        if (bean != null) {
            boolean isCancel = bean.getMsg_type() == ChatEnum.EMessageType.MSG_CANCEL;
            if (isCancel && bean.getMsgCancel() != null) {
                cancelId = bean.getMsgCancel().getMsgidCancel();
            }
        }
        boolean isGroup = !TextUtils.isEmpty(gid);
        //isCancel 是否是撤回消息  ，  canChangeUnread 不在聊天页面 注意true表示不在聊天页面
        realm.beginTransaction();
        Session session = isGroup ? realm.where(Session.class).equalTo("gid", gid).findFirst()
                : realm.where(Session.class).equalTo("from_uid", from_uid).findFirst();
        if (session == null) {//session不存在，创建新会话
            session = new Session();
            session.setSid(UUID.randomUUID().toString());
            if (isGroup) {
                session.setGid(gid);
                session.setType(1);
                Group group = getGroup(gid);
                if (group != null) {
                    session.setIsTop(group.getIsTop());
                    session.setIsMute(group.getNotNotify());
                }
            } else {
                session.setFrom_uid(from_uid);
                session.setType(0);
                UserInfo user = realm.where(UserInfo.class).equalTo("uid", from_uid).findFirst();
                if (user != null) {
                    session.setIsTop(user.getIstop());
                    session.setIsMute(user.getDisturb());
                }
            }

            if (canChangeUnread) {//增加一条记录
                if (session.getIsMute() == 1) {//免打扰
                    session.setUnread_count(0);
                } else {
                    if (StringUtil.isNotNull(cancelId)) {
                        session.setUnread_count(0);
                    } else {
                        session.setUnread_count(1);
                    }
                }
            }
        } else {//已存在的session
            if (canChangeUnread) {
                if (session.getIsMute() != 1) {//非免打扰
                    int num = 0;
                    if (StringUtil.isNotNull(cancelId)) {//撤销消息
                        MsgAllBean cancel = realm.where(MsgAllBean.class).equalTo("msg_id", cancelId).findFirst();
//                            LogUtil.getLog().e("群==isRead===="+cancel.isRead()+"==getRead="+cancel.getRead());
                        if (cancel != null && !cancel.isRead()) {//撤回的是未读消息 红点-1
                            num = session.getUnread_count() - 1;
                        } else {
                            num = session.getUnread_count();
                        }

                    } else {
                        num = session.getUnread_count() + 1;
                    }
                    num = num < 0 ? 0 : num;
                    session.setUnread_count(num);
                } else {
                    session.setUnread_count(0);
                }
            }
        }
        session.setUp_time(System.currentTimeMillis());
        if (StringUtil.isNotNull(cancelId)) {//如果是撤回at消息,星哥说把类型给成这个,at就会去掉
            session.setMessageType(1000);
        } else if ("first".equals(firstFlag) && bean != null && bean.getAtMessage() != null && bean.getAtMessage().getAt_type() != 1000) {
            //对at消息处理 而且不是撤回消息
            int messageType = bean.getAtMessage().getAt_type();
            String atMessage = bean.getAtMessage().getMsg();
            session.setMessageType(messageType);
            session.setAtMessage(atMessage);
        }
        realm.insertOrUpdate(session);
        realm.commitTransaction();
        LogUtil.getLog().e("更新session未读数", "msgDao");
        return true;
    }


}
