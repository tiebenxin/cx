package com.yanlong.im.chat.dao;

import android.text.TextUtils;

import com.hm.cxpay.global.PayEnum;
import com.luck.picture.lib.tools.DateUtils;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.ApplyBean;
import com.yanlong.im.chat.bean.AssistantMessage;
import com.yanlong.im.chat.bean.AtMessage;
import com.yanlong.im.chat.bean.BusinessCardMessage;
import com.yanlong.im.chat.bean.ChangeSurvivalTimeMessage;
import com.yanlong.im.chat.bean.ChatMessage;
import com.yanlong.im.chat.bean.EnvelopeInfo;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.GroupConfig;
import com.yanlong.im.chat.bean.GroupImageHead;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MessageDBTemp;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgCancel;
import com.yanlong.im.chat.bean.MsgNotice;
import com.yanlong.im.chat.bean.OfflineCollect;
import com.yanlong.im.chat.bean.OfflineDelete;
import com.yanlong.im.chat.bean.ReceiveRedEnvelopeMessage;
import com.yanlong.im.chat.bean.RedEnvelopeMessage;
import com.yanlong.im.chat.bean.Remind;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.chat.bean.StampMessage;
import com.yanlong.im.chat.bean.TransferMessage;
import com.yanlong.im.chat.bean.UserSeting;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.CollectionInfo;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.ReadDestroyUtil;
import com.yanlong.im.utils.UserUtil;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class MsgDao {

    public Group getGroup4Id(String gid) {
        return DaoUtil.findOne(Group.class, "gid", gid);
    }

    /***
     * 保存群
     * @param group
     */
    public void groupSave(Group group) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            Group g = realm.where(Group.class).equalTo("gid", group.getGid()).findFirst();
            if (null != g) {//已经存在
                try {
                    List<MemberUser> objects = g.getUsers();
                    if (null != objects && objects.size() > 0) {
                        g.setName(group.getName());
                        g.setAvatar(group.getAvatar());
                        if (group.getUsers() != null)
                            g.setUsers(group.getUsers());
                        realm.insertOrUpdate(group);
                    }
                } catch (Exception e) {
                    return;
                }
            } else {//不存在
                realm.insertOrUpdate(group);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.reportException(e);
            DaoUtil.close(realm);
        }
    }


    /***
     * 保存群
     * @param groups 群列表
     */
    public void saveGroups(List<Group> groups) {
        if (groups == null || groups.size() <= 0) {
            return;
        }
        int len = groups.size();
        for (int i = 0; i < len; i++) {
            Group group = groups.get(i);
            List<MemberUser> memberUsers = group.getUsers();
            if (memberUsers != null) {
                int size = memberUsers.size();
                for (int j = 0; j < size; j++) {
                    MemberUser memberUser = memberUsers.get(j);
                    memberUser.init(group.getGid());
                }
            }
        }
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(groups);
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.reportException(e);
            DaoUtil.close(realm);
        }
    }


    public List<MsgAllBean> getMsg4User(Long userid, Long time, boolean isNew) {
        if (time == null) {
            time = 99999999999999l;
        }
        List<MsgAllBean> beans = null;
        Realm realm = DaoUtil.open();
        try {
            beans = new ArrayList<>();
            RealmResults list;
            if (isNew) {
                list = realm.where(MsgAllBean.class)
                        .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                        .and()
                        .beginGroup().equalTo("from_uid", userid).or().equalTo("to_uid", userid).endGroup()
                        .greaterThan("timestamp", time)
                        .sort("timestamp", Sort.DESCENDING)
//                    .limit(20)
                        .findAll();
            } else {
                list = realm.where(MsgAllBean.class)
                        .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                        .and()
                        .beginGroup().equalTo("from_uid", userid).or().equalTo("to_uid", userid).endGroup()
                        .lessThan("timestamp", time)
                        .sort("timestamp", Sort.DESCENDING)
                        .limit(80)
                        .findAll();
            }
            beans = realm.copyFromRealm(list);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.reportException(e);
            DaoUtil.close(realm);
        }
        //翻转列表
        if (beans != null) {
            Collections.reverse(beans);
        }
        return beans;
    }

    public List<MsgAllBean> getMsg4User(Long userid, Long time, int size) {
        if (time == null) {
            time = 99999999999999l;
        }
        List<MsgAllBean> beans = null;
        Realm realm = DaoUtil.open();
        try {
            beans = new ArrayList<>();
            RealmResults list = realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                    .and()
                    .beginGroup().equalTo("from_uid", userid).or().equalTo("to_uid", userid).endGroup()
                    .lessThan("timestamp", time)
                    .sort("timestamp", Sort.DESCENDING)
                    .limit(size)
                    .findAll();

            beans = realm.copyFromRealm(list);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        //翻转列表
        if (beans != null) {
            Collections.reverse(beans);
        }
        return beans;
    }

    public List<MsgAllBean> getMsg4UserImg(Long userid) {
        List<MsgAllBean> beans = null;
        Realm realm = DaoUtil.open();
        try {
            beans = new ArrayList<>();

            RealmResults list = realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                    .beginGroup().equalTo("from_uid", userid).or().equalTo("to_uid", userid).endGroup()
                    .beginGroup().equalTo("msg_type", 4).endGroup()
                    .sort("timestamp", Sort.DESCENDING)
                    .findAll();
            beans = realm.copyFromRealm(list);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        //翻转列表
        if (beans != null) {
            Collections.reverse(beans);
        }
        return beans;
    }


    /*
     * @param isNew true加载最新数据，false加载更多历史数据
     * */
    public List<MsgAllBean> getMsg4Group(String gid, Long time, boolean isNew) {
        if (time == null) {
            time = 99999999999999l;
        }
        List<MsgAllBean> beans = null;
        Realm realm = DaoUtil.open();
        try {
            beans = new ArrayList<>();
            RealmResults list;
            if (isNew) {
                list = realm.where(MsgAllBean.class)
                        .equalTo("gid", gid)
                        .greaterThan("timestamp", time)
                        .sort("timestamp", Sort.DESCENDING)
                        .findAll();
            } else {
                list = realm.where(MsgAllBean.class)
                        .equalTo("gid", gid)
                        .lessThan("timestamp", time)
                        .sort("timestamp", Sort.DESCENDING)
                        .limit(80)
                        .findAll();
            }
            beans = realm.copyFromRealm(list);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        //翻转列表
        if (beans != null) {
            Collections.reverse(beans);
        }
        return beans;
    }

    public List<MsgAllBean> getMsg4Group(String gid, Long time, int size) {
        if (time == null) {
            time = 99999999999999l;
        }
        List<MsgAllBean> beans = null;
        Realm realm = DaoUtil.open();
        try {
            beans = new ArrayList<>();
            RealmResults list = realm.where(MsgAllBean.class)
                    .equalTo("gid", gid)
                    .lessThan("timestamp", time)
                    .sort("timestamp", Sort.DESCENDING)
                    .limit(size)
                    .findAll();

            beans = realm.copyFromRealm(list);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        //翻转列表
        if (beans != null) {
            Collections.reverse(beans);
        }
        return beans;
    }

    public List<MsgAllBean> getMsg4GroupImg(String gid) {
        List<MsgAllBean> beans = null;
        Realm realm = DaoUtil.open();
        try {
            beans = new ArrayList<>();
            RealmResults list = realm.where(MsgAllBean.class)
                    .equalTo("gid", gid)
                    .equalTo("msg_type", 4)
                    .sort("timestamp", Sort.DESCENDING)
                    .findAll();

            beans = realm.copyFromRealm(list);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        //翻转列表
        if (beans != null) {
            Collections.reverse(beans);
        }
        return beans;
    }

    public List<MsgAllBean> getMsg4GroupImgNew(String gid, long time) {
        List<MsgAllBean> beans = null;
        Realm realm = DaoUtil.open();
        try {
            beans = new ArrayList<>();
            RealmResults list = realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("gid", gid).endGroup()
                    .and()
                    .beginGroup().equalTo("msg_type", 4).endGroup()
                    .and()
                    .beginGroup().greaterThan("timestamp", time).endGroup()
                    .sort("timestamp", Sort.DESCENDING)
                    .findAll();
            beans = realm.copyFromRealm(list);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        //翻转列表
        if (beans != null) {
            Collections.reverse(beans);
        }
        return beans;
    }

    public List<MsgAllBean> getMsg4GroupHistory(String gid, Long stime) {
        List<MsgAllBean> beans = null;
        Realm realm = DaoUtil.open();
        try {
            beans = new ArrayList<>();
            RealmResults list = realm.where(MsgAllBean.class)
                    .equalTo("gid", gid)
                    //  .lessThan("timestamp",time)
                    .greaterThanOrEqualTo("timestamp", stime)
                    .sort("timestamp", Sort.DESCENDING)
                    .findAll();

            beans = realm.copyFromRealm(list);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        //翻转列表
        if (beans != null) {
            Collections.reverse(beans);
        }
        return beans;
    }

    public List<MsgAllBean> getMsg4UserHistory(Long userid, Long stime) {

        // Long  time=99999999999999l;
        List<MsgAllBean> beans = new ArrayList<>();
        Realm realm = DaoUtil.open();
        try {
            RealmResults list = realm.where(MsgAllBean.class).beginGroup().equalTo("gid", "").or().isNull("gid").endGroup().and().beginGroup()
                    .equalTo("from_uid", userid).or().equalTo("to_uid", userid).endGroup()
                    //   .lessThan("timestamp",time)
                    .greaterThanOrEqualTo("timestamp", stime)
                    .sort("timestamp", Sort.DESCENDING)
                    .findAll();

            beans = realm.copyFromRealm(list);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        //翻转列表
        if (beans != null) {
            Collections.reverse(beans);
        }
        return beans;
    }

    /***
     * 保存群成员到数据库
     * @param
     */
    public void groupNumberSave(Group ginfo) {
        if (ginfo == null) {
            return;
        }
        for (MemberUser sv : ginfo.getUsers()) {
            sv.init(ginfo.getGid());
        }
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            realm.insertOrUpdate(ginfo);
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    /***
     * 离线获取群信息
     * @param gid
     * @return
     */
    public Group groupNumberGet(String gid) {
        Group groupInfoBean = null;
        Realm realm = DaoUtil.open();
        try {
            groupInfoBean = new Group();
            Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
            if (group != null) {
                groupInfoBean = realm.copyFromRealm(group);
            }
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return groupInfoBean;
    }

    /***双向删除
     * 删除好友某时间戳之前的聊天记录-单聊
     * @param fromUid 发的指令对方
     * @param beforeTimestamp 最后时间戳
     */
    public void msgDel(Long fromUid, long beforeTimestamp) {
        Realm realm = DaoUtil.open();
        try {
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
                    deleteRealmMsg(msg);
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
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }

    }

    /***
     * 单删某条
     * @param msgId
     */
    public void msgDel4MsgId(String msgId) {
        Realm realm = DaoUtil.open();
        //因为msg对象 uid有两个，都得添加
        List<String> gids = new ArrayList<>();
        List<Long> uids = new ArrayList<>();
        try {
            realm.beginTransaction();
            RealmResults<MsgAllBean> list = null;


            list = realm.where(MsgAllBean.class).equalTo("msg_id", msgId).findAll();
            //删除前先把子表数据干掉!!切记
            if (list != null) {
                if (list.size() > 0) {
                    //gid存在时，不取uid
                    if (TextUtils.isEmpty(list.get(0).getGid())) {
                        uids.add(list.get(0).getFrom_uid());
                        uids.add(list.get(0).getTo_uid());
                    } else {
                        gids.add(list.get(0).getGid());
                    }
                }

                for (MsgAllBean msg : list) {
                    deleteRealmMsg(msg);
                }
                list.deleteAllFromRealm();

            }

            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }

        /********通知更新sessionDetail************************************/
        //回主线程调用更新session详情
        if (MyAppLication.INSTANCE().repository != null)
            MyAppLication.INSTANCE().repository.updateSessionDetail(gids, uids);
        /********通知更新sessionDetail end************************************/
    }


    /**
     * 撤回消息
     *
     * @param msgid       消息ID
     * @param msgCancelId
     */
    public MsgAllBean msgDel4Cancel(String msgid, String msgCancelId) {
        MsgAllBean msgAllBean = null;
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            RealmResults<MsgAllBean> list = null;

            list = realm.where(MsgAllBean.class).equalTo("msg_id", msgCancelId).findAll();
            MsgAllBean cancel = realm.where(MsgAllBean.class).equalTo("msg_id", msgid).findFirst();
            if (cancel == null && list != null && list.size() > 0) {
                MsgAllBean bean = list.get(0);
                if (TextUtils.isEmpty(bean.getMsg_id())) {
                    return null;
                }

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
                msgAllBean = realm.copyFromRealm(cancel);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
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

        return msgAllBean;
    }


    /***
     * 清除所有的聊天记录
     */
    public void msgDelAll() {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            realm.where(MsgAllBean.class).findAll().deleteAllFromRealm();
            realm.where(SessionDetail.class).findAll().deleteAllFromRealm();
            //这里要清除关联表
            realm.where(ChatMessage.class).findAll().deleteAllFromRealm();
            realm.where(ImageMessage.class).findAll().deleteAllFromRealm();
            realm.where(RedEnvelopeMessage.class).findAll().deleteAllFromRealm();
            realm.where(ReceiveRedEnvelopeMessage.class).findAll().deleteAllFromRealm();
            realm.where(TransferMessage.class).findAll().deleteAllFromRealm();
            realm.where(StampMessage.class).findAll().deleteAllFromRealm();
            realm.where(BusinessCardMessage.class).findAll().deleteAllFromRealm();
            realm.where(MsgNotice.class).findAll().deleteAllFromRealm();
            realm.where(MsgCancel.class).findAll().deleteAllFromRealm();
            realm.where(VoiceMessage.class).findAll().deleteAllFromRealm();
            realm.where(AtMessage.class).findAll().deleteAllFromRealm();
            realm.where(AssistantMessage.class).findAll().deleteAllFromRealm();
            realm.where(VideoMessage.class).findAll().deleteAllFromRealm();

            //清理角标
            RealmResults<Session> sessions = realm.where(Session.class).findAll();
            for (Session session : sessions) {
                session.setUnread_count(0);
                session.setAtMessage("");
                realm.insertOrUpdate(session);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }

    }


    /***
     * 创建群
     * @param id
     * @param avatar
     * @param name
     * @param listDataTop
     */
    public void groupCreate(String id, String avatar, String name, List<MemberUser> listDataTop) {
        if (!TextUtils.isEmpty(id)) {
            Group group = new Group();
            group.setAvatar(avatar == null ? "" : avatar);
            group.setGid(id);
            group.setName(name == null ? "" : name);
            RealmList<MemberUser> users = new RealmList();
            users.addAll(listDataTop);
            group.setUsers(users);
            DaoUtil.update(group);
        }
    }

    /***
     * 创建群头像
     * @param gid
     * @param avatar
     */
    public void groupHeadImgCreate(String gid, String avatar) {
        if (!TextUtils.isEmpty(gid) && avatar != null) {
            GroupImageHead imageHead = new GroupImageHead();
            imageHead.setGid(gid);
            imageHead.setImgHeadUrl(avatar);
            DaoUtil.update(imageHead);
        }
    }

    /***
     * 修改群头像
     * @param gid
     * @param avatar
     */
    public void groupHeadImgUpdate(String gid, String avatar) {
        if (!TextUtils.isEmpty(gid) && avatar != null) {
            GroupImageHead imageHead = new GroupImageHead();
            imageHead.setGid(gid);
            imageHead.setImgHeadUrl(avatar);
            DaoUtil.update(imageHead);
        }
    }

    /***
     * 获取本地群头像
     * @param gid
     *
     */
    public String groupHeadImgGet(String gid) {
        if (StringUtil.isNotNull(gid)) {
            GroupImageHead head = DaoUtil.findOne(GroupImageHead.class, "gid", gid);
            if (head != null) {
                return head.getImgHeadUrl();
            }
        }
        return "";
    }


    /***
     * 根据key查询群
     */
    public List<Group> searchGroup4key(String key) {
        Realm realm = DaoUtil.open();
        List<Group> ret = new ArrayList<>();
        RealmResults<Group> users = realm.where(Group.class)
                .contains("name", key).findAll();
        if (users != null)
            ret = realm.copyFromRealm(users);
        realm.close();
        return ret;
    }

    /***
     * 根据key查询消息
     *
     * 备注：新增不区分大小写模糊查询
     */
    public List<MsgAllBean> searchMsg4key(String key, String gid, Long uid) {
        String searchKey = key;
        if (!TextUtils.isEmpty(key)) {
            searchKey = String.format("*%s*", key);
        }
        Realm realm = DaoUtil.open();
        List<MsgAllBean> ret = null;
        try {
            ret = new ArrayList<>();
            RealmResults<MsgAllBean> msg;
            if (StringUtil.isNotNull(gid)) {//群
                msg = realm.where(MsgAllBean.class)
                        .equalTo("gid", gid)
                        .notEqualTo("msg_type", ChatEnum.EMessageType.LOCK)
                        .and()
                        .beginGroup()
                        .like("chat.msg", searchKey, Case.INSENSITIVE).or()//文本聊天
                        .like("atMessage.msg", searchKey, Case.INSENSITIVE).or()//@消息
                        .like("assistantMessage.msg", searchKey, Case.INSENSITIVE).or()//小助手消息
                        .like("locationMessage.address", searchKey, Case.INSENSITIVE).or()//位置消息
                        .like("locationMessage.addressDescribe", searchKey, Case.INSENSITIVE).or()//位置消息
                        .like("business_card.nickname", searchKey, Case.INSENSITIVE).or()//名片消息
                        .like("sendFileMessage.file_name", searchKey, Case.INSENSITIVE).or()//文件消息
                        .like("webMessage.title", searchKey, Case.INSENSITIVE).or()//链接消息
                        .like("replyMessage.chatMessage.msg", searchKey, Case.INSENSITIVE).or()//回复消息
                        .like("replyMessage.atMessage.msg", searchKey, Case.INSENSITIVE)//回复@消息
                        .endGroup()
                        .sort("timestamp", Sort.DESCENDING)
                        .findAll();
            } else {//单人
                msg = realm.where(MsgAllBean.class)
                        .beginGroup().isEmpty("gid").or().isNull("gid").endGroup()
                        .and()
                        .beginGroup().equalTo("from_uid", uid).or().equalTo("to_uid", uid).endGroup()
                        .and()
                        .notEqualTo("msg_type", ChatEnum.EMessageType.LOCK)
                        .and()
                        .beginGroup()
                        .like("chat.msg", searchKey, Case.INSENSITIVE).or()//文本聊天
                        .like("atMessage.msg", searchKey, Case.INSENSITIVE).or()//@消息
                        .like("assistantMessage.msg", searchKey, Case.INSENSITIVE).or()//小助手消息
                        .like("locationMessage.address", searchKey, Case.INSENSITIVE).or()//位置消息
                        .like("locationMessage.addressDescribe", searchKey, Case.INSENSITIVE).or()//位置消息
                        .like("business_card.nickname", searchKey, Case.INSENSITIVE).or()//名片消息
                        .like("sendFileMessage.file_name", searchKey, Case.INSENSITIVE).or()//文件消息
                        .like("webMessage.title", searchKey, Case.INSENSITIVE).or()//链接消息
                        .like("replyMessage.chatMessage.msg", searchKey, Case.INSENSITIVE).or()//回复消息
                        .like("replyMessage.atMessage.msg", searchKey, Case.INSENSITIVE)//回复@消息
                        .endGroup()
                        .sort("timestamp", Sort.DESCENDING)
                        .findAll();
            }
            if (msg != null)
                ret = realm.copyFromRealm(msg);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return ret;
    }

    /***
     * 创建会话数量
     * @param gid
     * @param toUid
     */
    public Session sessionCreate(String gid, Long toUid) {
        Session session;

        if (StringUtil.isNotNull(gid)) {//群消息
            session = DaoUtil.findOne(Session.class, "gid", gid);
            if (session == null) {
                session = new Session();
                session.setSid(UUID.randomUUID().toString());
                session.setGid(gid);
                session.setType(1);
                Group group = DaoUtil.findOne(Group.class, "gid", gid);
                if (group != null) {
                    session.setIsTop(group.getIsTop());
                    session.setIsMute(group.getNotNotify());
                }
            }

        } else {//个人消息
            session = DaoUtil.findOne(Session.class, "from_uid", toUid);
            if (session == null) {
                session = new Session();
                session.setSid(UUID.randomUUID().toString());
                session.setFrom_uid(toUid);
                session.setType(0);
                UserInfo user = DaoUtil.findOne(UserInfo.class, "uid", toUid);
                if (user != null) {
                    session.setIsTop(user.getIstop());
                    session.setIsMute(user.getDisturb());
                }
            }
        }

        session.setUnread_count(0);
        session.setUp_time(System.currentTimeMillis());
        DaoUtil.update(session);
        return session;
    }

    /***
     * 删除单个或者群会话
     * @param from_uid
     * @param gid
     */
    public void sessionDel(Long from_uid, String gid) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        if (StringUtil.isNotNull(gid)) {//群消息
            realm.where(Session.class).equalTo("gid", gid).findAll().deleteAllFromRealm();
        } else {
            realm.where(Session.class).equalTo("from_uid", from_uid).findAll().deleteAllFromRealm();
        }
        realm.commitTransaction();
        realm.close();
    }

    /***
     * 更新从自己PC端发过来的消息,只更新时间
     */
    public void updateFromSelfPCSession(MsgAllBean bean) {
        Realm realm = DaoUtil.open();
        try {
            Session session = null;
            if (StringUtil.isNotNull(bean.getGid())) {//群消息
                session = realm.where(Session.class).equalTo("gid", bean.getGid()).findFirst();
            } else { //单聊-touid才是对方id
                session = realm.where(Session.class).equalTo("from_uid", bean.getTo_uid()).findFirst();
            }
            if (session != null) {//已存在的session，只更新时间
                realm.beginTransaction();
                session.setUp_time(System.currentTimeMillis());
                realm.commitTransaction();
            } else {//新session
                if (StringUtil.isNotNull(bean.getGid())) {//群消息
                    session = new Session();
                    session.setSid(UUID.randomUUID().toString());
                    session.setGid(bean.getGid());
                    session.setType(1);
                    Group group = realm.where(Group.class).equalTo("gid", bean.getGid()).findFirst();
                    realm.beginTransaction();
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
                    realm.beginTransaction();
                    if (user != null) {
                        //因getIsTop有写入操作，beginTransaction得写在前面
                        session.setIsTop(user.getIstop());
                        session.setIsMute(user.getDisturb());
                    }
                }
                session.setUnread_count(0);
                session.setUp_time(System.currentTimeMillis());

                realm.insertOrUpdate(session);
                realm.commitTransaction();
            }
        } finally {
            DaoUtil.close(realm);
        }
    }

    /*
     * 更新或者创建session
     *
     * */
    public boolean sessionReadUpdate(String gid, Long from_uid, boolean canChangeUnread, MsgAllBean bean, String firstFlag) {
        //是否是 撤回
        String cancelId = null;
        if (bean != null) {
            boolean isCancel = bean.getMsg_type() == ChatEnum.EMessageType.MSG_CANCEL;
            if (isCancel && bean.getMsgCancel() != null) {
                cancelId = bean.getMsgCancel().getMsgidCancel();
            }
        }


        //isCancel 是否是撤回消息  ，  canChangeUnread 不在聊天页面 注意true表示不在聊天页面
        Session session;
        if (StringUtil.isNotNull(gid)) {//群消息
            session = DaoUtil.findOne(Session.class, "gid", gid);
            if (session == null) {
                session = new Session();
                session.setSid(UUID.randomUUID().toString());
                session.setGid(gid);
                session.setType(1);
                Group group = DaoUtil.findOne(Group.class, "gid", gid);
                if (group != null) {
                    session.setIsTop(group.getIsTop());
                    session.setIsMute(group.getNotNotify());
                }
                if (canChangeUnread) {
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
            } else {
                if (canChangeUnread) {
                    if (session.getIsMute() != 1) {//非免打扰
                        int num = 0;
                        if (StringUtil.isNotNull(cancelId)) {//撤销消息
                            MsgAllBean cancel = getMsgById(cancelId);
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

        } else {//个人消息
            session = DaoUtil.findOne(Session.class, "from_uid", from_uid);
            if (session == null) {
                session = new Session();
                session.setSid(UUID.randomUUID().toString());
                session.setFrom_uid(from_uid);
                session.setType(0);
                UserInfo user = DaoUtil.findOne(UserInfo.class, "uid", from_uid);
                if (user != null) {
                    session.setIsTop(user.getIstop());
                    session.setIsMute(user.getDisturb());
                }
                if (canChangeUnread) {
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
            } else {
                if (canChangeUnread) {
                    if (session.getIsMute() != 1) {//非免打扰
                        //没有撤回消息的id，要判断撤回的消息是已读还是未读
                        int num = 0;
                        if (StringUtil.isNotNull(cancelId)) {
                            MsgAllBean cancel = getMsgById(cancelId);
//                            LogUtil.getLog().e("==isRead===="+cancel.isRead()+"==getRead="+cancel.getRead());
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
        }

        if (StringUtil.isNotNull(cancelId)) {//如果是撤回at消息,星哥说把类型给成这个,at就会去掉
            if (StringUtil.isNotNull(gid)) {//群聊
//                if (!checkUnReadAtMsg(session, cancelId)) {//检查是否还有未读的@我的消息
                session.setMessageType(1000);
//                }
            } else {
                session.setMessageType(1000);
            }
        } else if ("first".equals(firstFlag) && bean != null && bean.getAtMessage() != null && bean.getAtMessage().getAt_type() != 1000) {
            //对at消息处理 而且不是撤回消息
            int messageType = bean.getAtMessage().getAt_type();
            String atMessage = bean.getAtMessage().getMsg();
            session.setMessageType(messageType);
            session.setAtMessage(atMessage);
        }
        LogUtil.getLog().e("更新session未读数", "msgDao");
        return DaoUtil.update(session);
    }


    /*
     * 批量更新或者创建session
     *
     * */
    public void sessionReadUpdate(String gid, Long from_uid, int count) {
        Session session;
        if (StringUtil.isNotNull(gid)) {//群消息
            session = DaoUtil.findOne(Session.class, "gid", gid);
            if (session == null) {
                session = new Session();
                session.setSid(UUID.randomUUID().toString());
                session.setGid(gid);
                session.setType(1);
                Group group = DaoUtil.findOne(Group.class, "gid", gid);
                if (group != null) {
                    session.setIsTop(group.getIsTop());
                    session.setIsMute(group.getNotNotify());
                }
                if (session.getIsMute() == 1) {//免打扰
                    session.setUnread_count(0);
                } else {
                    session.setUnread_count(count < 0 ? 0 : count);
                }
            } else {
                if (session.getIsMute() != 1) {//免打扰
                    int num = session.getUnread_count() + count;
                    num = num < 0 ? 0 : num;
                    session.setUnread_count(num);
                } else {
                    session.setUnread_count(0);
                }
            }
            session.setUp_time(System.currentTimeMillis());

        } else {//个人消息
            session = DaoUtil.findOne(Session.class, "from_uid", from_uid);
            if (session == null) {
                session = new Session();
                session.setSid(UUID.randomUUID().toString());
                session.setFrom_uid(from_uid);
                session.setType(0);
                UserInfo user = DaoUtil.findOne(UserInfo.class, "uid", from_uid);
                if (user != null) {
                    session.setIsTop(user.getIstop());
                    session.setIsMute(user.getDisturb());
                }
                if (session.getIsMute() == 1) {//免打扰
                    session.setUnread_count(0);
                } else {
                    session.setUnread_count(count < 0 ? 0 : count);
                }

            } else {
                if (session.getIsMute() != 1) {//非免打扰
                    int num = session.getUnread_count() + count;
                    num = num < 0 ? 0 : num;
                    session.setUnread_count(num);
                } else {
                    session.setUnread_count(0);
                }
            }
            session.setUp_time(System.currentTimeMillis());
        }
//        if (isCancel) {//如果是撤回at消息,星哥说把类型给成这个,at就会去掉
//            session.setMessageType(1000);
//        }

        DaoUtil.update(session);
    }

    /*
     * 跟随群信，或用户信息更新，更新session置顶免打扰字段
     * */
    public void updateSessionTopAndDisturb(String gid, Long from_uid, int top, int disturb) {
        Session session;
        if (StringUtil.isNotNull(gid)) {//群消息
            session = DaoUtil.findOne(Session.class, "gid", gid);
            if (session != null) {
                session.setIsMute(disturb);
                session.setIsTop(top);
                if (disturb == 1) {
                    session.setUnread_count(0);
                }
            }
        } else {//个人消息
            session = DaoUtil.findOne(Session.class, "from_uid", from_uid);
            if (session != null) {
                session.setIsMute(disturb);
                session.setIsTop(top);
                if (disturb == 1) {
                    session.setUnread_count(0);
                }
            }
        }
        if (session != null) {
            DaoUtil.update(session);
        }
    }

    /***
     * 清理单个会话阅读数量
     * @param gid
     * @param from_uid
     */
    public void sessionReadCleanAndToBurn(String gid, Long from_uid, long timeStamp) {
        Realm realm = DaoUtil.open();
        try {
            Session session = StringUtil.isNotNull(gid) ? realm.where(Session.class).equalTo("gid", gid).findFirst() :
                    realm.where(Session.class).equalTo("from_uid", from_uid).findFirst();
            if (session != null) {
                realm.beginTransaction();
                session.setUnread_count(0);
                session.setAtMessage(null);
                realm.commitTransaction();
            }
            RealmResults<MsgAllBean> msgs;
            if (!TextUtils.isEmpty(gid)) {//群聊-好友发送的，自己未读消息
                msgs = realm.where(MsgAllBean.class)
                        .equalTo("gid", gid)
                        .equalTo("isRead", false)
                        .findAll();
            } else {//单聊-好友发送的消息，未加入到阅后即焚队列的消息
                msgs = realm.where(MsgAllBean.class)
                        .beginGroup()
                        .isEmpty("gid").or().isNull("gid")
                        .endGroup()
                        .and()
                        .beginGroup()
                        .equalTo("from_uid", from_uid)
                        .equalTo("isRead", false)
                        .endGroup().findAll();
            }

            if (msgs != null && msgs.size() > 0) {
                realm.beginTransaction();
                //对方发的消息，当前时间为起点
                for (MsgAllBean msg : msgs) {
                    if (msg.getSurvival_time() > 0 && msg.getEndTime() <= 0) {
                        msg.setRead(true);//自己已读
                        msg.setReadTime(timeStamp);
                        msg.setStartTime(timeStamp);
                        msg.setEndTime(timeStamp + (msg.getSurvival_time() * 1000));
                    } else {
                        msg.setRead(true);//自己已读
                        msg.setReadTime(timeStamp);
                    }
                }
                realm.commitTransaction();
            }
        } catch (Exception e) {
        } finally {
            DaoUtil.close(realm);
        }
    }

    /***
     * 清理单个会话阅读数量
     * @param session
     */
    public void sessionReadClean(Session session) {
        if (session != null) {
            session.setUnread_count(0);
            session.setMarkRead(0);
            DaoUtil.update(session);
        }
    }

    /***
     * 查询会话所有未读消息,最多显示99，所以只需查询100条即可
     * @return
     */
    public int sessionReadGetAll() {
        int sum = 0;
        Realm realm = DaoUtil.open();
        List<Session> list = realm.where(Session.class)
                .beginGroup().greaterThan("unread_count", 0).endGroup()
                .or()
                .beginGroup().greaterThan("markRead", 0).and().equalTo("isMute", 0).endGroup()
                .limit(100).findAll();
        if (list != null) {
            for (Session s : list) {
                sum += (s.getUnread_count() + s.getMarkRead());
            }
        }

        realm.close();
        return sum;
    }


    /**
     * 是否有草稿
     */
    public boolean isSaveDraft(String gid) {
        boolean isSaveDraft = false;
        Session session = DaoUtil.findOne(Session.class, "gid", gid);
        if (session != null && StringUtil.isNotNull(session.getDraft())) {
            isSaveDraft = true;
        }
        return isSaveDraft;
    }

    /***
     * 获取会话
     * @param gid
     * @param uid
     * @return
     */
    public Session sessionGet(String gid, Long uid) {
        if (StringUtil.isNotNull(gid)) {
            return DaoUtil.findOne(Session.class, "gid", gid);
        } else {
            return DaoUtil.findOne(Session.class, "from_uid", uid);
        }

    }

    /***
     * 存草稿  需要更新时间
     * @param gid
     * @param uid
     * @param draft
     */
    public void sessionDraft(String gid, Long uid, String draft) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            Session session = StringUtil.isNotNull(gid) ? realm.where(Session.class).equalTo("gid", gid).findFirst() : realm.where(Session.class).equalTo("from_uid", uid).findFirst();
            if (session != null) {
                session.setDraft(draft);
                session.setMessageType(2);
                session.setUp_time(SocketData.getSysTime());
                realm.insertOrUpdate(session);
                //通知刷新某个session by sid-草稿
                MessageManager.getInstance().notifyRefreshMsg(CoreEnum.EChatType.PRIVATE, session.getSid(), CoreEnum.ESessionRefreshTag.SINGLE);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    /***
     * 更新@消息
     * @param gid
     * @param uid
     */
    public void updateSessionAtMsg(String gid, Long uid) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            Session session = StringUtil.isNotNull(gid) ? realm.where(Session.class).equalTo("gid", gid).findFirst() : realm.where(Session.class).equalTo("from_uid", uid).findFirst();
            if (session != null) {
                session.setAtMessage("");
                session.setMessageType(1000);
                realm.insertOrUpdate(session);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    /**
     * 存at消息
     */
    public void atMessage(String gid, String atMessage, int type) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            Session session = realm.where(Session.class).equalTo("gid", gid).findFirst();

            if (session != null) {
                session.setAtMessage(atMessage);
                session.setMessageType(type);
                realm.insertOrUpdate(session);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }


    /***
     * 获取红点的值
     * @param type
     * @return
     */
    public int remidGet(String type) {
        Remind remind = DaoUtil.findOne(Remind.class, "remid_type", type);
        int num = remind == null ? 0 : remind.getNumber();
        return num;
    }

    /***
     * 红点加一
     * @param type
     */
    public void remidCount(String type) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            Remind remind = realm.where(Remind.class).equalTo("remid_type", type).findFirst();
            int readnum = remind == null ? 1 : remind.getNumber() + 1;
            Remind newreamid = new Remind();
            newreamid.setNumber(readnum);
            newreamid.setRemid_type(type);
            realm.insertOrUpdate(newreamid);
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    /**
     * @param type
     * @param count
     * @param isAdd 加还是减
     */
    public void remidCount(String type, int count, boolean isAdd) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            Remind remind = realm.where(Remind.class).equalTo("remid_type", type).findFirst();
            int readnum;
            if (isAdd) {
                readnum = remind == null ? 1 : remind.getNumber() + count;
            } else {
                readnum = count;
            }
            Remind newreamid = new Remind();
            newreamid.setNumber(readnum);
            newreamid.setRemid_type(type);
            realm.insertOrUpdate(newreamid);
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }


    /***
     * 获取所有会话
     * @param isAll 是否剔除小助手，true不剔除，false剔除
     * @return
     */
    public List<Session> sessionGetAll(boolean isAll) {
        List<Session> rts = null;
        Realm realm = DaoUtil.open();
        try {
            RealmResults<Session> list;
            if (isAll) {
                list = realm.where(Session.class).sort("up_time", Sort.DESCENDING).findAll();
            } else {
                list = realm.where(Session.class).beginGroup().notEqualTo("from_uid", 1L).and().isNotNull("from_uid").endGroup().
                        or().isNotNull("gid").sort("up_time", Sort.DESCENDING).findAll();
            }
            list = list.sort("isTop", Sort.DESCENDING);
            rts = realm.copyFromRealm(list);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return rts;
    }

    /***
     * 获取所有有效会话，去除被踢群聊
     * @return
     */
    public List<Session> sessionGetAllValid() {
        List<Session> rts = null;
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            RealmResults<Session> list = realm.where(Session.class)
                    .beginGroup().notEqualTo("from_uid", 1L).and().isNotNull("from_uid").endGroup().
                            or().isNotNull("gid").sort("up_time", Sort.DESCENDING).findAll();
            //6.5 优先读取单独表的配置
            List<Session> removes = new ArrayList<>();
            if (list != null) {
                int len = list.size();
                for (int i = 0; i < len; i++) {
                    Session l = list.get(i);
                    Session session = null;
                    int top = 0;
                    if (l.getType() == 1) {
                        GroupConfig config = realm.where(GroupConfig.class).equalTo("gid", l.getGid()).findFirst();
                        if (config != null && config.getIsExit() == 1) {
                            session = realm.copyFromRealm(l);
                            removes.add(session);
                        } else {
                            Group group = realm.where(Group.class).equalTo("gid", l.getGid()).findFirst();
                            if (group != null) {
                                if (group.getStat() != ChatEnum.EGroupStatus.NORMAL) {
                                    session = realm.copyFromRealm(l);
                                    removes.add(session);
                                } else {
                                    top = group.getIsTop();
                                    List<MemberUser> users = realm.copyFromRealm(group.getUsers());
                                    MemberUser member = MessageManager.getInstance().userToMember(UserAction.getMyInfo(), group.getGid());
                                    if (users != null && member != null && !users.contains(member)) {
                                        session = realm.copyFromRealm(l);
                                        removes.add(session);
                                    }
                                }
                            }
                        }
                    } else {
                        Long uid = l.getFrom_uid();
                        if (UserUtil.isSystemUser(uid)) {
                            session = realm.copyFromRealm(l);
                            removes.add(session);
                        } else {
                            UserInfo info = realm.where(UserInfo.class).equalTo("uid", l.getFrom_uid()).findFirst();
                            if (info != null) {
                                top = info.getIstop();
                            }
                        }
                    }
                    l.setIsTop(top);
                }
            }
            realm.copyToRealmOrUpdate(list);
            list = list.sort("isTop", Sort.DESCENDING);
            rts = realm.copyFromRealm(list);
            if (removes.size() > 0) {
                int len = removes.size();
                for (int i = 0; i < len; i++) {
                    rts.remove(removes.get(i));
                }
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }

        return rts;
    }


    /***
     * 获取最后的消息
     * @param uid
     * @return
     */
    public MsgAllBean msgGetLast4FUid(Long uid) {
        MsgAllBean ret = null;
        Realm realm = DaoUtil.open();
        try {
            MsgAllBean bean = realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                    .and()
                    .beginGroup().equalTo("from_uid", uid).or().equalTo("to_uid", uid).endGroup()
                    .sort("timestamp", Sort.DESCENDING).findFirst();
            if (bean != null) {
                ret = realm.copyFromRealm(bean);
            }
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return ret;
    }

    /**
     * 获取最后一条收到的消息
     */
    public MsgAllBean msgGetLast4FromUid(Long uid) {
        MsgAllBean ret = null;
        Realm realm = DaoUtil.open();
        try {
            MsgAllBean bean = realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                    .and()
                    .beginGroup().equalTo("from_uid", uid).endGroup()
                    .and()
                    .beginGroup().equalTo("isLocal", 0).endGroup()
                    .sort("timestamp", Sort.DESCENDING).findFirst();
            if (bean != null) {
                ret = realm.copyFromRealm(bean);
            }
//            LogUtil.getLog().e("==msg=ret=="+ GsonUtils.optObject(ret));
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
        }
        return ret;
    }


    /***
     * 获取群最后的消息(注意一定是有效消息，即排除通知类型消息)
     * @param uid
     * @return
     */
    public MsgAllBean msgGetLastGroup4Uid(String gid, Long uid) {
        MsgAllBean ret = null;
        Realm realm = DaoUtil.open();
        try {
            MsgAllBean bean = realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("gid", gid).endGroup()
                    .and()
                    .beginGroup().equalTo("from_uid", uid).or().equalTo("to_uid", uid).endGroup()
                    .and()
                    .beginGroup().notEqualTo("msg_type", 0).endGroup()
                    .sort("timestamp", Sort.DESCENDING).findFirst();
            if (bean != null) {
                ret = realm.copyFromRealm(bean);
            }
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return ret;
    }

    /***
     * 获取最后的群消息
     * @param gid
     * @return
     */
    public MsgAllBean msgGetLast4Gid(String gid) {
        MsgAllBean ret = null;
        Realm realm = DaoUtil.open();
        MsgAllBean bean = realm.where(MsgAllBean.class).equalTo("gid", gid)
                .sort("timestamp", Sort.DESCENDING).findFirst();
        if (bean != null) {
            ret = realm.copyFromRealm(bean);
        }

        realm.close();
        return ret;
    }

    /**
     * 更新已读状态和阅后即焚
     * 单聊发送：自己发送成功且对方已读，立即加入阅后即焚
     */
    public void setUpdateRead(long uid, long timestamp) {
        Realm realm = DaoUtil.open();
        try {
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
                        msgAllBean.setRead(1);
                        msgAllBean.setReadTime(timestamp);
                        /**处理需要阅后即焚的消息***********************************/
                        msgAllBean.setStartTime(startTime);
                        msgAllBean.setEndTime(endTime);
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
        } finally {
            DaoUtil.close(realm);
        }
    }


    /**
     * 设置已读
     */
    public void setRead(String msgid) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        try {
            MsgAllBean msgAllBean = realm.where(MsgAllBean.class)
                    .equalTo("msg_id", msgid).findFirst();
            if (msgAllBean != null) {
                msgAllBean.setRead(1);
                realm.insertOrUpdate(msgAllBean);
            }

            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
        }
    }

    /*
     * 更新群置顶
     * */
    public Session updateGroupAndSessionTop(String gid, int top) {
        Session session = null;
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
            if (group != null) {
                group.setIsTop(top);
                realm.insertOrUpdate(group);
            }

            Session s = realm.where(Session.class).equalTo("gid", gid).findFirst();
            if (s != null) {
                s.setIsTop(top);
                session = realm.copyFromRealm(s);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return session;

    }

    /*
     * 更新群免打扰
     * */
    public void updateGroupAndSessionDisturb(String gid, int disturb) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
            if (group != null) {
                group.setNotNotify(disturb);
                realm.insertOrUpdate(group);
            }

            Session session = realm.where(Session.class).equalTo("gid", gid).findFirst();
            if (session != null) {
                session.setIsMute(disturb);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    /***
     * 保存单聊置顶，session 和 user 一起更新
     * @param uid
     * @param isTop
     */
    public Session updateUserSessionTop(Long uid, int isTop) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        Session session = DaoUtil.findOne(Session.class, "from_uid", uid);
        if (session != null) {
            session.setIsTop(isTop);
            realm.insertOrUpdate(session);
        }

        UserInfo user = DaoUtil.findOne(UserInfo.class, "uid", uid);
        if (user != null) {
            user.setIstop(isTop);
            realm.insertOrUpdate(user);
        }
        realm.commitTransaction();
        realm.close();
        return session;
    }


    /***
     * 保存单聊免打扰，session 和user 一起更新
     * @param uid
     * @param disturb
     */
    public Session updateUserSessionDisturb(Long uid, int disturb) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        Session session = DaoUtil.findOne(Session.class, "from_uid", uid);
        if (session != null) {
            session.setIsMute(disturb);
            realm.insertOrUpdate(session);
        }

        UserInfo user = DaoUtil.findOne(UserInfo.class, "uid", uid);
        if (user != null) {
            user.setDisturb(disturb);
            realm.insertOrUpdate(user);
        }
        realm.commitTransaction();
        realm.close();
        return session;
    }


    //申请加好友
    public void applyFriend(ApplyBean bean) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        bean.setTime(System.currentTimeMillis());
        realm.insertOrUpdate(bean);
        realm.commitTransaction();
        realm.close();
    }

    //申请加群
    public void applyGroup(ApplyBean bean) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        bean.setTime(System.currentTimeMillis());
        realm.insertOrUpdate(bean);
        realm.commitTransaction();
        realm.close();
    }


    //查询申请列表
    public List<ApplyBean> getApplyBeanList() {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        List<ApplyBean> beans = new ArrayList<>();
        //删除错误数据
        RealmResults<ApplyBean> resTemp = realm.where(ApplyBean.class).equalTo("uid", 0).findAll();
        if (resTemp != null) {
            resTemp.deleteAllFromRealm();
        }

        RealmResults<ApplyBean> res = realm.where(ApplyBean.class)
                .isNotNull("aid")
                .sort("stat", Sort.ASCENDING, "time", Sort.DESCENDING).findAll();

        if (res != null) {
            beans = realm.copyFromRealm(res);
        }
        realm.commitTransaction();
        realm.close();
        return beans;
    }

    //根据aid查询申请人
    public ApplyBean getApplyBean(String aid) {
        Realm realm = DaoUtil.open();
        ApplyBean bean = new ApplyBean();
        ApplyBean applyBean = realm.where(ApplyBean.class).equalTo("aid", aid).findFirst();
        if (applyBean != null) {
            bean = realm.copyFromRealm(applyBean);
        }
        realm.close();
        return bean;
    }

    // 移除这条群申请
    public void applyRemove(String aid) {
        DaoUtil.deleteOne(ApplyBean.class, "aid", aid);

    }


    /***
     * 修改群名
     * @param gid
     * @param name
     */
    public void groupNameUpadte(final String gid, final String name) {
        DaoUtil.start(new DaoUtil.EventTransaction() {
            @Override
            public void run(Realm realm) {
                Group ginfo = realm.where(Group.class).equalTo("gid", gid).findFirst();
                if (ginfo != null) {
                    ginfo.setName(name);
                    realm.insertOrUpdate(ginfo);
                }

            }
        });
    }


    /***
     * 群解散,退出的配置
     * @param gid
     * @param isExit
     */
    public void groupExit(final String gid, final String gname, final String gicon, final int isExit) {
        DaoUtil.start(new DaoUtil.EventTransaction() {
            @Override
            public void run(Realm realm) {
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


            }
        });
    }

    /***
     * 获取群配置
     * @param gid
     * @return
     */
    public GroupConfig groupConfigGet(String gid) {
        return DaoUtil.findOne(GroupConfig.class, "gid", gid);
    }

    /**
     * 红包开
     *
     * @param rid
     * @param
     */
    public void redEnvelopeOpen(String rid, int envelopeStatus, int reType, String token) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            RedEnvelopeMessage envelopeMessage = null;
            if (reType == MsgBean.RedEnvelopeType.MFPAY_VALUE) {
                envelopeMessage = realm.where(RedEnvelopeMessage.class).equalTo("id", rid).findFirst();
            } else if (reType == MsgBean.RedEnvelopeType.SYSTEM_VALUE) {
                long traceId = Long.parseLong(rid);
                envelopeMessage = realm.where(RedEnvelopeMessage.class).equalTo("traceId", traceId).findFirst();
                if (envelopeMessage
                        != null) {
                    if (!TextUtils.isEmpty(token)) {
                        envelopeMessage.setAccessToken(token);
                    }
                    envelopeMessage.setEnvelopStatus(envelopeStatus);
                }
            }
            if (envelopeMessage != null) {
                if (envelopeMessage.getIsInvalid() == 0) {//没拆才更新，已经拆过了不更新
                    envelopeMessage.setIsInvalid(envelopeStatus != PayEnum.EEnvelopeStatus.NORMAL ? 1 : 0);
                }
                envelopeMessage.setEnvelopStatus(envelopeStatus);
                realm.insertOrUpdate(envelopeMessage);
            }

            //删除红包备份消息
            if (envelopeStatus > 0) {
                long traceId = Long.parseLong(rid);
                MessageDBTemp msgTemp = realm.where(MessageDBTemp.class).equalTo("envelopeMessage.traceId", traceId).findFirst();
                if (msgTemp != null) {
                    if (msgTemp.getRedEnvelope() != null) {
                        msgTemp.getRedEnvelope().deleteFromRealm();
                    }
                    msgTemp.deleteFromRealm();
                }
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }


    /***
     * 个人配置修改,为空不修改
     */
    public void userSetingUpdate(Boolean shake, Boolean voice) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        UserSeting userSeting = realm.where(UserSeting.class).equalTo("uid", UserAction.getMyId()).findFirst();
        if (userSeting == null) {
            userSeting = new UserSeting();
            userSeting.setUid(UserAction.getMyId());

        }
        if (shake != null) {
            userSeting.setShake(shake);
        }

        if (voice != null) {
            userSeting.setVoice(voice);
        }

        realm.insertOrUpdate(userSeting);

        realm.commitTransaction();
        realm.close();

    }


    /**
     * 修改语音播放模式 0.扬声器  1.听筒
     */
    public void userSetingVoicePlayer(int voicePlayer) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        UserSeting userSeting = realm.where(UserSeting.class).equalTo("uid", UserAction.getMyId()).findFirst();
        if (userSeting == null) {
            userSeting = new UserSeting();
            userSeting.setUid(UserAction.getMyId());

        }
        userSeting.setVoicePlayer(voicePlayer);
        realm.insertOrUpdate(userSeting);
        realm.commitTransaction();
        realm.close();

    }


    /**
     * 修改聊天背景图片
     */
    public void userSetingImage(int image) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        UserSeting userSeting = realm.where(UserSeting.class).equalTo("uid", UserAction.getMyId()).findFirst();
        if (userSeting == null) {
            userSeting = new UserSeting();
            userSeting.setUid(UserAction.getMyId());

        }
        userSeting.setImageBackground(image);
        realm.insertOrUpdate(userSeting);
        realm.commitTransaction();
        realm.close();

    }


    /**
     * 获取用户配置
     */
    public UserSeting userSetingGet() {
        UserSeting userSeting = DaoUtil.findOne(UserSeting.class, "uid", UserAction.getMyId());
        if (userSeting == null) {//数据库中无用户配置信息，则为默认
            userSeting = new UserSeting();
            userSeting.setUid(UserAction.getMyId());
        }
        return userSeting;
    }

    /***
     * 获取原始图的已读状态
     * @param originUrl
     * @return
     */
    public boolean ImgReadStatGet(String originUrl) {
        if (!StringUtil.isNotNull(originUrl)) {
            return false;
        }
        if (originUrl.startsWith("file:")) {
            return true;
        }

        ImageMessage img = DaoUtil.findOne(ImageMessage.class, "origin", originUrl);
        if (img != null) {
            return img.isReadOrigin();
        }
        return false;
    }

    /***
     * 图片已读写入
     * @param originUrl
     * @param isread
     */
    public void ImgReadStatSet(String originUrl, boolean isread) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        ImageMessage img = realm.where(ImageMessage.class).equalTo("origin", originUrl).findFirst();
        if (img != null) {
            img.setReadOrigin(isread);
            realm.insertOrUpdate(img);
        }
        realm.commitTransaction();
        realm.close();

    }

    //修改消息状态
    public MsgAllBean fixStataMsg(String msgid, int sendState) {
        MsgAllBean ret = null;
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        MsgAllBean msgAllBean = realm.where(MsgAllBean.class).equalTo("msg_id", msgid).findFirst();
        if (msgAllBean != null) {
            msgAllBean.setSend_state(sendState);
            realm.insertOrUpdate(msgAllBean);
            ret = realm.copyFromRealm(msgAllBean);
        }
        realm.commitTransaction();
        realm.close();

        return ret;

    }


    //修改消息状态
    public VideoMessage fixVideoLocalUrl(String msgid, String localUrl) {
        VideoMessage ret = null;
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        VideoMessage msgAllBean = realm.where(VideoMessage.class).equalTo("msgId", msgid).findFirst();
        if (msgAllBean != null) {
            msgAllBean.setLocalUrl(localUrl);
            realm.insertOrUpdate(msgAllBean);
            ret = realm.copyFromRealm(msgAllBean);
        }
        realm.commitTransaction();
        realm.close();

        return ret;

    }

    /***
     * 获取用户需要展示的群名字
     * @param gid
     * @param uid
     * @return
     */
    public String getUsername4Show(String gid, Long uid) {
        return getUsername4Show(gid, uid, null, null);
    }

    /***
     * 获取用户需要展示的群名字
     * @param gid
     * @param uid
     * @param uname 用户最新的昵称
     * @param groupName 群最新的昵称
     * @return
     */
    public String getUsername4Show(String gid, Long uid, String uname, String groupName) {
        String name = "";
        Realm realm = DaoUtil.open();
        UserInfo userInfo = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
        if (userInfo != null) {
            //1.获取本地用户昵称
            name = userInfo.getName();
            //1.5如果有带过来的昵称先显示昵称
            name = StringUtil.isNotNull(uname) ? uname : name;

            //1.8  如果有带过来的群昵称先显示群昵称
            if (StringUtil.isNotNull(groupName)) {
                name = groupName;
            } else {
                MemberUser memberUser = realm.where(MemberUser.class)
                        .beginGroup().equalTo("uid", uid).endGroup()
                        .beginGroup().equalTo("gid", gid).endGroup()
                        .findFirst();
                if (memberUser != null) {
                    name = StringUtil.isNotNull(memberUser.getMembername()) ? memberUser.getMembername() : name;
                }
            }
            //3.获取用户备注名
            name = StringUtil.isNotNull(userInfo.getMkName()) ? userInfo.getMkName() : name;
        } else {
            MemberUser memberUser = realm.where(MemberUser.class)
                    .beginGroup().equalTo("uid", uid).endGroup()
                    .beginGroup().equalTo("gid", gid).endGroup()
                    .findFirst();
            if (memberUser != null) {
                name = StringUtil.isNotNull(memberUser.getMembername()) ? memberUser.getMembername() : memberUser.getName();
            }

        }
        realm.close();

        return name;
    }


    /***
     *
     * @param msgid
     * @param note
     * @return
     */
    public MsgAllBean noteMsgAddRb(String msgid, Long toUid, String gid, MsgNotice note) {
        MsgAllBean ret = null;
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        MsgAllBean msgAllBean = realm.where(MsgAllBean.class).equalTo("msg_id", msgid).findFirst();
        if (msgAllBean == null) {
            msgAllBean = new MsgAllBean();
            msgAllBean.setMsg_id(msgid);
            gid = gid == null ? "" : gid;
            msgAllBean.setGid(gid);
            IUser userinfo = UserAction.getMyInfo();
            msgAllBean.setFrom_uid(toUid);
            msgAllBean.setTo_uid(userinfo.getUid());
        }

        int survivaltime = new UserDao().getReadDestroy(toUid, gid);

        msgAllBean.setSurvival_time(survivaltime);
        msgAllBean.setMsg_type(ChatEnum.EMessageType.NOTICE);
        msgAllBean.setMsgNotice(note);
        msgAllBean.setTimestamp(new Date().getTime());
        msgAllBean.setIsLocal(1);
        realm.insertOrUpdate(msgAllBean);
        realm.commitTransaction();
        realm.close();
        return ret;
    }


    /**
     * 自己修改退出即焚系统消息
     */
    public MsgAllBean noteMsgAddSurvivaltime(Long toUid, String gid) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        String msgid = SocketData.getUUID();
        MsgAllBean msgAllBean = new MsgAllBean();
        msgAllBean.setMsg_id(msgid);
        msgAllBean.setGid(gid);
        IUser userinfo = UserAction.getMyInfo();
        msgAllBean.setFrom_uid(toUid);
        msgAllBean.setTo_uid(userinfo.getUid());
        int survivaltime = new UserDao().getReadDestroy(toUid, gid);
        msgAllBean.setSurvival_time(survivaltime);
        String survivaNotice = "";
        if (survivaltime == -1) {
            survivaNotice = "你设置了退出即焚.";
        } else if (survivaltime == 0) {
            survivaNotice = "你取消了阅后即焚.";
        } else {
            survivaNotice = "你设置了消息" +
                    new ReadDestroyUtil().getDestroyTimeContent(survivaltime) + "后消失.";
        }
        MsgCancel survivaMsgCel = new MsgCancel();
        survivaMsgCel.setMsgid(msgid);
        survivaMsgCel.setNote(survivaNotice);
        msgAllBean.setMsgCancel(survivaMsgCel);
        ChangeSurvivalTimeMessage changeSurvivalTimeMessage = new ChangeSurvivalTimeMessage();
        changeSurvivalTimeMessage.setSurvival_time(survivaltime);
        changeSurvivalTimeMessage.setMsgid(msgid);
        msgAllBean.setChangeSurvivalTimeMessage(changeSurvivalTimeMessage);
        msgAllBean.setMsg_type(ChatEnum.EMessageType.CHANGE_SURVIVAL_TIME);
        msgAllBean.setMsgCancel(survivaMsgCel);
//        msgAllBean.setTimestamp(new Date().getTime());
        msgAllBean.setTimestamp(SocketData.getFixTime());
        realm.insertOrUpdate(msgAllBean);

        realm.commitTransaction();
        realm.close();
        EventBus.getDefault().post(new EventRefreshChat());
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
        return msgAllBean;
    }


    /***
     * 把发送中的状态修改为发送失败
     */
    public void msgSendStateToFail() {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            RealmResults<MsgAllBean> list = realm.where(MsgAllBean.class).equalTo("send_state", ChatEnum.ESendStatus.SENDING).or().equalTo("send_state", ChatEnum.ESendStatus.PRE_SEND).findAll();
            if (list != null) {
                for (MsgAllBean ls : list) {
                    ls.setSend_state(ChatEnum.ESendStatus.ERROR);
                }
                realm.insertOrUpdate(list);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }

    }

    //是否存在该消息,getChat=null 需要删除旧消息
    public boolean isMsgLockExist(String gid, Long uid) {
        MsgAllBean ret = null;
        MsgAllBean bean = null;
        Realm realm = DaoUtil.open();
        if (!TextUtils.isEmpty(gid)) {
            ret = realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("msg_type", ChatEnum.EMessageType.LOCK).endGroup()
                    .and()
                    .beginGroup().equalTo("gid", gid).endGroup()
                    .findFirst();
        } else if (uid != null) {
            ret = realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("msg_type", ChatEnum.EMessageType.LOCK).endGroup()
                    .and()
                    .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                    .and()
                    .beginGroup().equalTo("from_uid", uid).or().equalTo("to_uid", uid).endGroup()
                    .findFirst();
        }
        if (ret != null) {
            bean = realm.copyFromRealm(ret);
        }
        realm.close();
        if (bean != null && bean.getChat() != null) {
            return true;
        }
        if (bean != null) {
            DaoUtil.deleteOne(MsgAllBean.class, "msg_id", bean.getMsg_id());
        }
        return false;
    }

    public void insertOrUpdateMessage(MsgAllBean bean) {
        if (bean == null) {
            return;
        }
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        realm.insertOrUpdate(bean);
        realm.commitTransaction();
        realm.close();
    }


    /***
     * 模糊搜索群聊
     * @return
     */
    public List<Group> getGroupByKey(String key) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        List<Group> ret = new ArrayList<>();
        RealmResults<Group> groups = realm.where(Group.class).findAll();
        RealmResults<Group> keyGroups = groups.where().contains("name", key).findAll();
        if (keyGroups != null) {
            ret = realm.copyFromRealm(keyGroups);
        }
        for (int i = 0; i < groups.size(); i++) {
            Group g = groups.get(i);
            if (ret.contains(g)) {
                continue;
            } else {
                RealmList<MemberUser> userInfos = g.getUsers();
                MemberUser userInfo = userInfos.where()
                        .beginGroup().contains("name", key).endGroup()
                        .or()
                        .beginGroup().contains("membername", key).endGroup()
                        .findFirst();
                if (userInfo != null) {
                    Group group = realm.copyFromRealm(g);
                    MemberUser info = realm.copyFromRealm(userInfo);
                    group.setKeyUser(info);
                    ret.add(group);
                }
            }
        }
        realm.commitTransaction();
        realm.close();
        return ret;
    }

    //修改播放消息状态
    public void updatePlayStatus(String msgId, @ChatEnum.EPlayStatus int playStatus, boolean isRead) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        MsgAllBean msgBean = realm.where(MsgAllBean.class).equalTo("msg_id", msgId).findFirst();
        if (msgBean != null && msgBean.getVoiceMessage() != null) {
            if (isRead) {
                msgBean.setRead(true);
            }
            msgBean.getVoiceMessage().setPlayStatus(playStatus);
            realm.insertOrUpdate(msgBean);
        }
        realm.commitTransaction();
        realm.close();
    }

    /***
     * 群成员保护的开关
     * @param intimately
     */
    public void groupContactIntimatelyUpdate(String gid, boolean intimately) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
        if (group != null) {
            group.setContactIntimately(intimately ? 1 : 0);
            realm.insertOrUpdate(group);
        }

        realm.commitTransaction();
        realm.close();
    }


    public MsgAllBean getMsgById(String msgId) {
        MsgAllBean ret = null;
        MsgAllBean bean = null;
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        ret = realm.where(MsgAllBean.class).equalTo("msg_id", msgId).findFirst();
        if (ret != null) {
            bean = realm.copyFromRealm(ret);
        }
        realm.commitTransaction();
        realm.close();
        return bean;
    }

    /*
     * 动态获取群名
     * */
    public String getGroupName(String gid) {
        Group group = getGroup4Id(gid);
        if (group == null) {
            return "";
        }
        String result = group.getName();
        if (TextUtils.isEmpty(result)) {
            result = "";
            List<MemberUser> users = group.getUsers();
            if (users != null && users.size() > 0) {
                int len = users.size();
                for (int i = 0; i < len; i++) {
                    if (result.length() >= 14) {
                        break;
                    }
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

    /*
     * 动态获取群名
     * */
    public String getGroupName(Group group) {
        if (group == null) {
            return "";
        }
        String result = group.getName();
        if (TextUtils.isEmpty(result)) {
            result = "";
            List<MemberUser> users = group.getUsers();
            if (users != null && users.size() > 0) {
                int len = users.size();
                for (int i = 0; i < len; i++) {
                    if (result.length() >= 14) {
                        break;
                    }
                    MemberUser info = users.get(i);
                    if (i == len - 1) {
                        result += StringUtil.getUserName("", info.getMembername(), info.getName(), info.getUid());
                    } else {
                        result += StringUtil.getUserName("", info.getMembername(), info.getName(), info.getUid()) + "、";
                    }

                }
                result = result.length() > 14 ? StringUtil.splitEmojiString2(result, 0, 14) : result;
                result += "的群";
            }
        }
        return result;
    }


    /***
     * 获取除当前会话的未读消息数量
     * @param gid
     * @param uid
     */
    public int getUnreadCount(String gid, Long uid) {
        Realm realm = DaoUtil.open();
        int sum = 0;
        try {
            realm.beginTransaction();
            RealmResults<Session> list;
            if (!TextUtils.isEmpty(gid)) {
                list = realm.where(Session.class)
                        .notEqualTo("gid", gid)
                        .findAll();
            } else {
                list = realm.where(Session.class)
                        .notEqualTo("from_uid", uid)
                        .findAll();

            }
            List<Session> sessions = realm.copyFromRealm(list);
            int len = sessions.size();
            for (int i = 0; i < len; i++) {
                sum += sessions.get(i).getUnread_count();
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return sum;
    }

    //判断群是否已存在
    public boolean isGroupExist(String groupId) {
        boolean exist = false;
        if (!TextUtils.isEmpty(groupId)) {
            Realm realm = DaoUtil.open();
            try {
                Group g = realm.where(Group.class).equalTo("gid", groupId).findFirst();
                if (g != null) {
                    exist = true;
                }
                realm.close();
            } catch (Exception e) {
                e.printStackTrace();
                DaoUtil.close(realm);
                DaoUtil.reportException(e);
            }
        }
        return exist;
    }


    /***
     * 修改群名
     * @param gid 群id
     * @param name 群名
     */
    public boolean updateGroupName(String gid, String name) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        Group g = realm.where(Group.class).equalTo("gid", gid).findFirst();
        if (g != null) {//已经存在
            g.setName(name);
            realm.insertOrUpdate(g);
        } else {//不存在
            realm.commitTransaction();
            realm.close();
            return false;
        }
        realm.commitTransaction();
        realm.close();
        return true;
    }

    /***
     * 修改群头像
     * @param gid 群id
     * @param head 群名
     */
    public boolean updateGroupHead(String gid, String head) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        Group g = realm.where(Group.class).equalTo("gid", gid).findFirst();
        if (g != null) {//已经存在
            g.setAvatar(head);
            realm.insertOrUpdate(g);
        } else {//不存在
            realm.commitTransaction();
            realm.close();
            return false;
        }
        realm.commitTransaction();
        realm.close();
        return true;
    }

    /***
     * 修改我在本群昵称
     * @param gid 群id
     * @param name 群名
     */
    public boolean updateMyGroupName(String gid, String name) {
        if (UserAction.getMyId() == null) {
            return false;
        }
        Realm realm = DaoUtil.open();
        try {
            Group g = realm.where(Group.class).equalTo("gid", gid).findFirst();
            realm.beginTransaction();
            if (g != null) {//已经存在
                g.setMygroupName(name);
                RealmList<MemberUser> users = g.getUsers();
                MemberUser memberUser = users.where().equalTo("uid", UserAction.getMyId().longValue()).findFirst();
                if (memberUser != null) {
                    memberUser.setMembername(name);
                }
            } else {//不存在
                return false;
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.reportException(e);
            DaoUtil.close(realm);
        }
        return true;
    }


    /***
     * 更新非保存群
     * @param groupList 群列表
     */
    public void updateNoSaveGroup(List<Group> groupList) {
        Realm realm = DaoUtil.open();
        try {
            List<Group> resultList = new ArrayList<>();
            if (groupList == null || groupList.size() <= 0) {
                List<Group> groups = realm.where(Group.class).equalTo("saved", 1).findAll();
                List<Group> temp = realm.copyFromRealm(groups);
                if (temp != null) {
                    int len = temp.size();
                    if (len > 0) {
                        for (int i = 0; i < len; i++) {
                            Group group = temp.get(i);
                            group.setSaved(0);
                        }
                        resultList.addAll(temp);
                    }
                }
            } else {
                List<Group> groups = realm.where(Group.class).equalTo("saved", 1).findAll();
                List<Group> temp = realm.copyFromRealm(groups);
                if (temp != null) {
                    int len = temp.size();
                    if (len > 0) {
                        for (Iterator<Group> it = temp.iterator(); it.hasNext(); ) {
                            Group group = it.next();
                            if (!groupList.contains(group)) {
                                group.setSaved(0);
                                resultList.addAll(temp);
                            }
                        }
                    }
                }
            }
            if (resultList != null && resultList.size() > 0) {
                realm.beginTransaction();
                realm.insertOrUpdate(resultList);
                realm.commitTransaction();
            }
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }


    /***
     * 获取保存群,是否能展示被封群
     */
    public List<Group> getMySavedGroup(boolean canShowForbid) {
        List<Group> results = null;
        Realm realm = DaoUtil.open();
        try {
            List<Group> groups = null;
            if (canShowForbid) {
                groups = realm.where(Group.class).equalTo("saved", 1).findAll();
            } else {
                groups = realm.where(Group.class).equalTo("saved", 1).and().equalTo("stat", 0).findAll();
            }
            int len = groups.size();
            if (len > 0) {
                results = realm.copyFromRealm(groups);
            }
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return results;
    }

    /**
     * 保存群聊
     */
    public void setSavedGroup(String gid, int saved) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
            if (group != null) {
                group.setSaved(saved);
                realm.insertOrUpdate(group);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }


    /***
     * 保存批量消息
     */
    public boolean insertOrUpdateMsgList(List<MsgAllBean> list) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            realm.insertOrUpdate(list);
            realm.commitTransaction();
            realm.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return false;
    }

    //移出群成员
    public void removeGroupMember(String gid, List<Long> uids) {
        if (uids == null) {
            return;
        }
        Long[] uidArr = uids.toArray(new Long[uids.size()]);
        if (uidArr == null) {
            return;
        }
        Realm realm = DaoUtil.open();
        try {
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
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    //适用：自己被移出群成员
    public void removeGroupMember(String gid, MemberUser user) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
            if (group != null) {
                RealmList<MemberUser> list = group.getUsers();
                if (list != null) {
                    list.remove(user);
                }
                if (group.getSaved() != null && group.getSaved().intValue() == 1) {//已保存设置为非保存群
                    group.setSaved(0);
                }
            }
            // TODO　被移出群时要先清除草稿
            Session session = realm.where(Session.class).equalTo("gid", gid).findFirst();
            if (session != null) {
                session.setDraft("");
                session.setMessageType(2);
                session.setUp_time(SocketData.getSysTime());
                realm.insertOrUpdate(session);
            }

            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    //移出群成员
    public void removeGroupMember(String gid, long uid) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
            if (group != null) {
                RealmList<MemberUser> list = group.getUsers();
                MemberUser memberUser = list.where().equalTo("uid", uid).findFirst();
                if (memberUser != null) {
                    list.remove(memberUser);
                }
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    /**
     * 动态获取用户群昵称   能查到群备注
     *
     * @param gid
     * @param uid
     * @param uname
     * @param groupName
     * @return
     */
    public String getGroupMemberName(String gid, long uid, String uname, String groupName) {
        Realm realm = DaoUtil.open();
        String name = "";
        try {
            realm.beginTransaction();
            Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
            if (group == null) {
                return "";
            }
            RealmList<MemberUser> users = group.getUsers();
            if (users != null) {
                MemberUser memberUser = users.where().equalTo("uid", uid).findFirst();
                if (memberUser != null) {
                    name = !TextUtils.isEmpty(memberUser.getMembername()) ? memberUser.getMembername() : memberUser.getName();
                }
            }

            if (TextUtils.isEmpty(name)) {
                UserInfo userInfo = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
                if (userInfo != null) {
                    //1.获取本地用户昵称
                    name = userInfo.getName();
                    //1.5如果有带过来的昵称先显示昵称
                    name = StringUtil.isNotNull(uname) ? uname : name;

                    //1.8  如果有带过来的群昵称先显示群昵称
                    if (StringUtil.isNotNull(groupName)) {
                        name = groupName;
                    } else {
                        MemberUser memberUser = realm.where(MemberUser.class)
                                .beginGroup().equalTo("uid", uid).endGroup()
                                .beginGroup().equalTo("gid", gid).endGroup()
                                .findFirst();
                        if (memberUser != null) {
                            name = StringUtil.isNotNull(memberUser.getMembername()) ? memberUser.getMembername() : name;
                        }
                    }
                    //3.获取用户备注名
                    name = StringUtil.isNotNull(userInfo.getMkName()) ? userInfo.getMkName() : name;
                } else {
                    MemberUser memberUser = realm.where(MemberUser.class)
                            .beginGroup().equalTo("uid", uid).endGroup()
                            .beginGroup().equalTo("gid", gid).endGroup()
                            .findFirst();
                    if (memberUser != null) {
                        name = StringUtil.isNotNull(memberUser.getMembername()) ? memberUser.getMembername() : memberUser.getName();
                    }
                }
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }

        return name;
    }

    /**
     * 动态获取用户群昵称 不能查到群备注
     *
     * @param gid
     * @param uid
     * @return
     */
    public String getGroupMemberName2(String gid, long uid) {
        Realm realm = DaoUtil.open();
        String result = "";
        try {
            realm.beginTransaction();
            Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
            if (group == null) {
                return "";
            }
            RealmList<MemberUser> users = group.getUsers();
            if (users != null) {
                MemberUser memberUser = users.where().equalTo("uid", uid).findFirst();
                if (memberUser != null) {
                    result = memberUser.getMembername();
                }
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return result;
    }

    public EnvelopeInfo queryEnvelopeInfo(String gid, long uid) {
        EnvelopeInfo envelopeInfo = null;
        Realm realm = DaoUtil.open();
        try {
            EnvelopeInfo info;
            if (!TextUtils.isEmpty(gid)) {
                info = realm.where(EnvelopeInfo.class).equalTo("gid", gid).findFirst();
            } else {
                info = realm.where(EnvelopeInfo.class).equalTo("uid", uid).findFirst();
            }
            if (info != null) {
                envelopeInfo = realm.copyFromRealm(info);
            }
            realm.close();
        } catch (Exception e) {
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return envelopeInfo;
    }

    public List<EnvelopeInfo> queryEnvelopeInfoList() {
        List<EnvelopeInfo> list = null;
        Realm realm = DaoUtil.open();
        try {
            RealmResults<EnvelopeInfo> realmList = realm.where(EnvelopeInfo.class).equalTo("sendStatus", 0).findAll();
            if (realmList != null) {
                list = realm.copyFromRealm(realmList);
            }

            realm.close();
        } catch (Exception e) {
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return list;
    }

    //删除发送失败红包信息
    public void deleteEnvelopeInfo(String rid, String gid, long uid, boolean deleInfo) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            EnvelopeInfo info = realm.where(EnvelopeInfo.class).equalTo("rid", rid).findFirst();
            if (info != null) {
                if (deleInfo) {
                    info.deleteFromRealm();
                }
                Session session;
                if (!TextUtils.isEmpty(gid)) {
                    session = realm.where(Session.class).equalTo("gid", gid).findFirst();
                } else {
                    session = realm.where(Session.class).equalTo("from_uid", uid).findFirst();
                }
                if (session != null) {
                    session.setMessageType(ChatEnum.ESessionType.DEFAULT);
                }
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    //更新转账状态
    public void updateTransferStatus(String tradeId, int opType, long uid) {
        Realm realm = DaoUtil.open();
        try {
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
            realm.close();
        } catch (Exception e) {
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    //查询群聊中未领取的有效红包,红包状态是未领取，且非普通红包
    public List<MsgAllBean> selectValidEnvelopeMsg(String gid, long mId) {
        Realm realm = DaoUtil.open();
        long time = SocketData.getFixTime();
        if (time <= 0) {
            time = System.currentTimeMillis();
        }
        //超过10分钟未领取，且未超过24小时
        long diff = TimeToString.DAY;
        long ten = TimeToString.MINUTE * 10;
        try {
            List<MsgAllBean> msgAllBeans = new ArrayList<>();
            RealmResults<MsgAllBean> realmResults = realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("gid", gid).endGroup()
                    .and()
                    .beginGroup().isNotNull("red_envelope").endGroup()
                    .and()
                    .beginGroup().equalTo("red_envelope.envelopStatus", 0).endGroup()
                    .and()
                    .beginGroup()
                    .beginGroup().equalTo("red_envelope.style", 1).endGroup()
                    .or()
                    .beginGroup().equalTo("red_envelope.style", 0).and().notEqualTo("from_uid", mId).endGroup()
                    .endGroup()
                    .and()
                    .beginGroup().greaterThan("timestamp", time - diff).endGroup()
                    .and()
                    .beginGroup().lessThan("timestamp", time - ten).endGroup()
                    .sort("timestamp", Sort.DESCENDING)
                    .findAll();

            RealmResults<MessageDBTemp> realmTemps = realm.where(MessageDBTemp.class)
                    .beginGroup().equalTo("gid", gid).endGroup()
                    .and()
                    .beginGroup().isNotNull("envelopeMessage").endGroup()
                    .and()
                    .beginGroup().equalTo("envelopeMessage.envelopStatus", 0).endGroup()
                    .and()
                    .beginGroup()
                    .beginGroup().equalTo("envelopeMessage.style", 1).endGroup()
                    .or()
                    .beginGroup().equalTo("envelopeMessage.style", 0).and().notEqualTo("from_uid", mId).endGroup()
                    .endGroup()
                    .and()
                    .beginGroup().greaterThan("timestamp", time - diff).endGroup()
                    .and()
                    .beginGroup().lessThan("timestamp", time - ten).endGroup()
                    .sort("timestamp", Sort.DESCENDING)
                    .findAll();
            if (realmResults != null) {
                msgAllBeans = realm.copyFromRealm(realmResults);
            }
            if (realmTemps != null) {
                List<MessageDBTemp> temps = realm.copyFromRealm(realmTemps);
                if (temps != null) {
                    List<MsgAllBean> msgList = MessageManager.getInstance().getMsgList(temps);
                    if (msgList != null) {
                        msgAllBeans.addAll(msgList);
                        Collections.sort(msgAllBeans, new Comparator<MsgAllBean>() {
                            @Override
                            public int compare(MsgAllBean o1, MsgAllBean o2) {
                                if (o1 == null || o2 == null || o1.getTimestamp() == null || o2.getTimestamp() == null) {
                                    return 0;
                                }
                                return (int) (o2.getTimestamp().longValue() - o1.getTimestamp().longValue());
                            }
                        });
                    }
                }
            }
            realm.close();
            return msgAllBeans;
        } catch (Exception e) {
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return null;
    }

    public void updateGroupSnapshot(String gid, int value) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
            if (group != null) {
                group.setScreenshotNotification(value);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    public void deleteRealmMsg(MsgAllBean msg) {
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
        if (msg.getReplyMessage() != null) {
            if (msg.getReplyMessage().getAtMessage() != null)
                msg.getReplyMessage().getAtMessage().deleteFromRealm();
            if (msg.getReplyMessage().getChatMessage() != null)
                msg.getReplyMessage().getChatMessage().deleteFromRealm();
            if (msg.getReplyMessage().getQuotedMessage() != null)
                msg.getReplyMessage().getQuotedMessage().deleteFromRealm();
            msg.getReplyMessage().deleteFromRealm();
        }
        if (msg.getAdMessage() != null)
            msg.getAdMessage().deleteFromRealm();
    }

    //判断当前用户是否群主或者群管理员
    public boolean isMemberInCharge(String gid, long uid) {
        if (TextUtils.isEmpty(gid) || uid <= 0) {
            return false;
        }
        boolean result = false;
        Realm realm = DaoUtil.open();
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
            realm.close();
        } catch (Exception e) {
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return result;
    }

    //3天以内的消息
    public List<MsgAllBean> getMsgIn3Day() {
        List<MsgAllBean> list = new ArrayList<>();
        Realm realm = DaoUtil.open();
        long time = SocketData.getFixTime() - TimeToString.DAY * 3;
        try {
            //群聊消息,1000 过滤红包转账消息
            RealmResults<MsgAllBean> groupMsgs = realm.where(MsgAllBean.class)
                    .beginGroup().isNotEmpty("gid").and().isNotNull("gid").endGroup()
                    .and()
                    .beginGroup().greaterThan("timestamp", time).endGroup()
                    .and()
                    .beginGroup().equalTo("isLocal", 0).endGroup()
                    .and()
                    .beginGroup().equalTo("send_state", 0).endGroup()
                    .and()
                    .beginGroup().notEqualTo("msg_type", 3).endGroup()
                    .and()
                    .beginGroup().notEqualTo("msg_type", 6).endGroup()
                    .limit(1000)
                    .sort("timestamp", Sort.DESCENDING)
                    .findAll();
            //单聊消息,3000，过滤红包3 转账 6消息
            RealmResults<MsgAllBean> privateMsgs = realm.where(MsgAllBean.class)
                    .beginGroup().isEmpty("gid").or().isNull("gid").endGroup()
                    .and()
                    .beginGroup().greaterThan("timestamp", time).endGroup()
                    .and()
                    .beginGroup().equalTo("isLocal", 0).endGroup()
                    .and()
                    .beginGroup().equalTo("send_state", 0).endGroup()
                    .and()
                    .beginGroup().notEqualTo("msg_type", 3).endGroup()
                    .and()
                    .beginGroup().notEqualTo("msg_type", 6).endGroup()
                    .limit(3000)
                    .sort("timestamp", Sort.DESCENDING)
                    .findAll();

            RealmList<MsgAllBean> results = new RealmList<>();
            if (groupMsgs != null) {
                results.addAll(groupMsgs);
            }
            if (privateMsgs != null) {
                results.addAll(privateMsgs);
            }
            list = realm.copyFromRealm(results);
            realm.close();
        } catch (Exception e) {
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return list;

    }

    //更新新的群邀请
    public void updateNewApply(String gid, long inviter, int status) {
        Realm realm = DaoUtil.open();
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
            realm.close();
        } catch (Exception e) {
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    //更新群状态
    public Group updateGroupStatus(String gid, int value) {
        Group result = null;
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
            if (group != null) {
                group.setStat(value);
                result = realm.copyFromRealm(group);
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return result;
    }

    //获取该会话正在被回复消息
    public MsgAllBean getReplyingMsg(String gid, Long uid) {
        MsgAllBean ret = null;
        Realm realm = DaoUtil.open();
        try {
            MsgAllBean bean;
            if (TextUtils.isEmpty(gid)) {
                bean = realm.where(MsgAllBean.class)
                        .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                        .and()
                        .beginGroup().equalTo("from_uid", uid).or().equalTo("to_uid", uid).endGroup()
                        .and()
                        .beginGroup().equalTo("isReplying", 1).endGroup()
                        .findFirst();
            } else {
                bean = realm.where(MsgAllBean.class)
                        .beginGroup().equalTo("gid", gid).endGroup()
                        .and()
                        .beginGroup().equalTo("isReplying", 1).endGroup()
                        .findFirst();
            }
            if (bean != null) {
                ret = realm.copyFromRealm(bean);
            }
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return ret;
    }


    /***
     * 添加一条收藏消息->本地收藏列表
     * @param
     */
    public void addLocalCollection(CollectionInfo collectionInfo) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            realm.insertOrUpdate(collectionInfo);
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    /**
     * 删除一条收藏消息->本地收藏列表
     *
     * @param msgId
     */
    public void deleteLocalCollection(String msgId) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            //找到的第一条删除，由于已经做了去重，不会出现msgId重复多条的情况
            realm.where(CollectionInfo.class).equalTo("msgId", msgId).findAll().deleteFirstFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            DaoUtil.reportException(e);
        } finally {
            realm.close();
        }
    }

    /**
     * 查询某一条收藏消息->本地收藏列表
     *
     * @param msgId
     * @return
     */
    public CollectionInfo findLocalCollection(String msgId) {
        Realm realm = DaoUtil.open();
        CollectionInfo bean;
        CollectionInfo info = realm.where(CollectionInfo.class).equalTo("msgId", msgId).findFirst();
        if (info != null) {
            bean = realm.copyFromRealm(info);
        } else {
            bean = null;
        }
        realm.close();
        return bean;
    }

    /**
     * 查询所有收藏消息->本地收藏列表
     *
     * @return
     */
    public List<CollectionInfo> getAllCollections() {
        List<CollectionInfo> list = null;
        Realm realm = DaoUtil.open();
        try {
            RealmResults<CollectionInfo> realmList = realm.where(CollectionInfo.class).findAll();
            if (realmList != null) {
                list = realm.copyFromRealm(realmList);
            }
            realm.close();
        } catch (Exception e) {
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return list;
    }

    /**
     * 获取服务端的收藏列表，替换掉本地收藏列表(同步到本地/保持一致性)
     *
     * @param list
     */
    public void updateLocalCollection(List<CollectionInfo> list) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            realm.where(CollectionInfo.class).findAll().deleteAllFromRealm();
            realm.insertOrUpdate(list);
            realm.commitTransaction();
        } catch (Exception e) {
            DaoUtil.reportException(e);
        } finally {
            realm.close();
        }
    }

    /***
     * 添加一条收藏操作记录->离线收藏记录表
     * @param collectionInfo
     */
    public void addOfflineCollectRecord(OfflineCollect collectionInfo) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            realm.insertOrUpdate(collectionInfo);
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    /**
     * 删除一条收藏操作记录->离线收藏记录表
     *
     * @param msgId
     */
    public void deleteOfflineCollectRecord(String msgId) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            realm.where(OfflineCollect.class).equalTo("msgId", msgId).findAll().deleteFirstFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            DaoUtil.reportException(e);
        } finally {
            realm.close();
        }
    }

    /**
     * 清空全部收藏操作记录->离线收藏记录表
     */
    public void deleteAllOfflineCollectRecords() {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            realm.where(OfflineCollect.class).findAll().deleteAllFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            DaoUtil.reportException(e);
        } finally {
            realm.close();
        }
    }

    /**
     * 查询一条收藏操作记录->离线收藏记录表
     *
     * @param msgId
     */
    public OfflineCollect findOfflineCollectRecord(String msgId) {
        Realm realm = DaoUtil.open();
        OfflineCollect bean;
        OfflineCollect info = realm.where(OfflineCollect.class).equalTo("msgId", msgId).findFirst();
        if (info != null) {
            bean = realm.copyFromRealm(info);
        } else {
            bean = null;
        }
        realm.close();
        return bean;
    }

    /**
     * 查询全部收藏操作记录->离线收藏记录表
     */
    public List<OfflineCollect> getAllOfflineCollectRecords() {
        List<OfflineCollect> list = null;
        Realm realm = DaoUtil.open();
        try {
            RealmResults<OfflineCollect> realmList = realm.where(OfflineCollect.class).findAll();
            if (realmList != null) {
                list = realm.copyFromRealm(realmList);
            }
            realm.close();
        } catch (Exception e) {
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return list;
    }

    /**
     * 添加一条删除操作记录->离线删除记录表
     *
     * @param bean
     */
    public void addOfflineDeleteRecord(OfflineDelete bean) {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            realm.insertOrUpdate(bean);
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
    }

    /**
     * 清空全部删除操作记录->离线删除记录表
     */
    public void deleteAllOfflineDeleteRecords() {
        Realm realm = DaoUtil.open();
        try {
            realm.beginTransaction();
            realm.where(OfflineDelete.class).findAll().deleteAllFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            DaoUtil.reportException(e);
        } finally {
            realm.close();
        }
    }

    /**
     * 查询全部删除操作记录->离线删除记录表
     */
    public List<OfflineDelete> getAllOfflineDeleteRecords() {
        List<OfflineDelete> list = null;
        Realm realm = DaoUtil.open();
        try {
            RealmResults<OfflineDelete> realmList = realm.where(OfflineDelete.class).findAll();
            if (realmList != null) {
                list = realm.copyFromRealm(realmList);
            }
            realm.close();
        } catch (Exception e) {
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return list;
    }

    //根据红包id，获取MsgAllBean
    public MsgAllBean getMsgByRid(long rid) {
        MsgAllBean ret = null;
        MsgAllBean bean = null;
        Realm realm = DaoUtil.open();
        try {
            ret = realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("red_envelope.traceId", rid).endGroup()
                    .or()
                    .beginGroup().equalTo("transfer.id", rid + "").endGroup()
                    .findFirst();
            if (ret != null) {
                bean = realm.copyFromRealm(ret);
            }
            realm.close();
        } catch (Exception e) {
            DaoUtil.close(realm);
            DaoUtil.reportException(e);
        }
        return bean;
    }

    public List<MemberUser> getMembers(String gid, String[] memberIds) {
        if (TextUtils.isEmpty(gid) || memberIds == null) {
            return null;
        }
        List<MemberUser> memberUsers = null;
        Realm realm = DaoUtil.open();
        Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
        if (group != null && group.getUsers() != null) {
            String[] orderFiled = {"tag"};
            Sort[] sorts = {Sort.ASCENDING};
            RealmResults<MemberUser> members = group.getUsers().where().in("memberId", memberIds).sort(orderFiled, sorts).findAll();
            if (members != null) {
                memberUsers = realm.copyFromRealm(members);
            }
        }
        realm.close();
        return memberUsers;
    }

    public List<MemberUser> getMembers(String gid) {
        if (TextUtils.isEmpty(gid)) {
            return null;
        }
        List<MemberUser> memberUsers = null;
        Realm realm = DaoUtil.open();
        Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
        if (group != null && group.getUsers() != null) {
            String[] orderFiled = {"tag"};
            Sort[] sorts = {Sort.ASCENDING};
            RealmResults<MemberUser> members = group.getUsers().sort(orderFiled, sorts);
            if (members != null) {
                memberUsers = realm.copyFromRealm(members);
            }
        }
        realm.close();
        return memberUsers;
    }

    //获取群成员被首字母排序的group
    public Group getSortGroup(String gid) {
        if (TextUtils.isEmpty(gid)) {
            return null;
        }
        List<MemberUser> memberUsers = null;
        Realm realm = DaoUtil.open();
        Group resultGroup = null;
        Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
        if (group != null) {
            resultGroup = realm.copyFromRealm(group);
            if (group.getUsers() != null) {
                String[] orderFiled = {"tag"};
                Sort[] sorts = {Sort.ASCENDING};
                RealmResults<MemberUser> members = group.getUsers().sort(orderFiled, sorts);
                if (members != null) {
                    memberUsers = realm.copyFromRealm(members);
                    RealmList<MemberUser> resultMembers = new RealmList<>();
                    resultMembers.addAll(memberUsers);
                    resultGroup.setUsers(resultMembers);
                }
            }
        }
        realm.close();
        return resultGroup;
    }
}
