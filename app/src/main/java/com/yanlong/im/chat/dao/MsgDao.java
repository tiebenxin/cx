package com.yanlong.im.chat.dao;

import android.text.TextUtils;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.AssistantMessage;
import com.yanlong.im.chat.bean.AtMessage;
import com.yanlong.im.chat.bean.BusinessCardMessage;
import com.yanlong.im.chat.bean.ChatMessage;
import com.yanlong.im.chat.bean.ContactNameBean;
import com.yanlong.im.chat.bean.GropLinkInfo;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.GroupAccept;
import com.yanlong.im.chat.bean.GroupConfig;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgCancel;
import com.yanlong.im.chat.bean.MsgNotice;
import com.yanlong.im.chat.bean.ReceiveRedEnvelopeMessage;
import com.yanlong.im.chat.bean.RedEnvelopeMessage;
import com.yanlong.im.chat.bean.Remind;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.StampMessage;
import com.yanlong.im.chat.bean.TransferMessage;
import com.yanlong.im.chat.bean.UserSeting;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class MsgDao {
    //分页数量
    private int pSize = 10;

    public Group getGroup4Id(String gid) {
        return DaoUtil.findOne(Group.class, "gid", gid);
    }

    /***
     * 保存群
     * @param group
     */
    public void groupSave(Group group) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        Group g = realm.where(Group.class).equalTo("gid", group.getGid()).findFirst();
        if (g != null) {//已经存在
            g.setName(group.getName());
            g.setAvatar(group.getAvatar());
            if (group.getUsers() != null)
                g.setUsers(group.getUsers());

            realm.insertOrUpdate(group);
        } else {//不存在
            realm.insertOrUpdate(group);
            // sessionCreate(group.getGid(),null);
        }


        realm.commitTransaction();
        realm.close();
        //return DaoUtil.findOne(Group.class, "gid", gid);
    }

    /***
     * 单用户消息列表
     * @param userid
     * @return
     */
    public List<MsgAllBean> getMsg4User(Long userid, int page) {
        List<MsgAllBean> beans = new ArrayList<>();
        Realm realm = DaoUtil.open();

        RealmResults list = realm.where(MsgAllBean.class).equalTo("gid", "")
                //   .notEqualTo("msg_type", 0)
                .and()
                .equalTo("from_uid", userid).or().equalTo("to_uid", userid)

                .sort("timestamp", Sort.DESCENDING)
                .findAll();

        beans = DaoUtil.page(page, list, realm);


        //翻转列表
        Collections.reverse(beans);
        realm.close();
        return beans;
    }

    public List<MsgAllBean> getMsg4User(Long userid, Long time) {
        if (time == null) {
            time = 99999999999999l;
        }
        List<MsgAllBean> beans = new ArrayList<>();
        Realm realm = DaoUtil.open();

        RealmResults list = realm.where(MsgAllBean.class).beginGroup().equalTo("gid", "").or().isNull("gid").endGroup().and().beginGroup()
                .equalTo("from_uid", userid).or().equalTo("to_uid", userid).endGroup()
                .lessThan("timestamp", time)
                .sort("timestamp", Sort.DESCENDING)
                .limit(20)
                .findAll();

        beans = realm.copyFromRealm(list);
        //翻转列表
        Collections.reverse(beans);
        realm.close();
        return beans;
    }

    public List<MsgAllBean> getMsg4User(Long userid, Long time, boolean isNew) {
        if (time == null) {
            time = 99999999999999l;
        }
        List<MsgAllBean> beans = new ArrayList<>();
        Realm realm = DaoUtil.open();
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
                    .limit(20)
                    .findAll();
        }
        beans = realm.copyFromRealm(list);
        //翻转列表
        Collections.reverse(beans);
        realm.close();
        return beans;
    }

    public List<MsgAllBean> getMsg4User(Long userid, Long time, int size) {
        if (time == null) {
            time = 99999999999999l;
        }
        List<MsgAllBean> beans = new ArrayList<>();
        Realm realm = DaoUtil.open();
        RealmResults list = realm.where(MsgAllBean.class)
                .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                .and()
                .beginGroup().equalTo("from_uid", userid).or().equalTo("to_uid", userid).endGroup()
                .lessThan("timestamp", time)
                .sort("timestamp", Sort.DESCENDING)
                .limit(size)
                .findAll();

        beans = realm.copyFromRealm(list);
        //翻转列表
        Collections.reverse(beans);
        realm.close();
        return beans;
    }

    public List<MsgAllBean> getMsg4UserImg(Long userid) {

        List<MsgAllBean> beans = new ArrayList<>();
        Realm realm = DaoUtil.open();

        RealmResults list = realm.where(MsgAllBean.class).equalTo("gid", "").beginGroup()
                .equalTo("from_uid", userid).or().equalTo("to_uid", userid).endGroup()

                .equalTo("msg_type", 4)
                .sort("timestamp", Sort.DESCENDING)


                .findAll();

        beans = realm.copyFromRealm(list);
        ;


        //翻转列表
        Collections.reverse(beans);
        realm.close();
        return beans;
    }


    /***
     * 获取群消息
     * @param gid
     * @param page
     * @return
     */
    public List<MsgAllBean> getMsg4Group(String gid, int page) {
        List<MsgAllBean> beans = new ArrayList<>();
        Realm realm = DaoUtil.open();

        RealmResults list = realm.where(MsgAllBean.class)
                .equalTo("gid", gid).and()
                //  .notEqualTo("msg_type", 0)
                .sort("timestamp", Sort.DESCENDING)
                .findAll();

        beans = DaoUtil.page(page, list, realm);


        //翻转列表
        Collections.reverse(beans);
        realm.close();
        return beans;
    }


//    public List<MsgAllBean> getMsg4Group(String gid, Long time) {
//        if (time == null) {
//            time = 99999999999999l;
//        }
//        List<MsgAllBean> beans = new ArrayList<>();
//        Realm realm = DaoUtil.open();
//
//        RealmResults list = realm.where(MsgAllBean.class)
//                .equalTo("gid", gid)
//                .lessThan("timestamp", time)
//
//                .sort("timestamp", Sort.DESCENDING)
//                .limit(20)
//                .findAll();
//
//        beans = realm.copyFromRealm(list);
//        //翻转列表
//        Collections.reverse(beans);
//        realm.close();
//        return beans;
//    }

    /*
     * @param isNew true加载最新数据，false加载更多历史数据
     * */
    public List<MsgAllBean> getMsg4Group(String gid, Long time, boolean isNew) {
        if (time == null) {
            time = 99999999999999l;
        }
        List<MsgAllBean> beans = new ArrayList<>();
        Realm realm = DaoUtil.open();
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
                    .limit(20)
                    .findAll();
        }

        beans = realm.copyFromRealm(list);
        //翻转列表
        Collections.reverse(beans);
        realm.close();
        return beans;
    }

    public List<MsgAllBean> getMsg4Group(String gid, Long time, int size) {
        if (time == null) {
            time = 99999999999999l;
        }
        List<MsgAllBean> beans = new ArrayList<>();
        Realm realm = DaoUtil.open();

        RealmResults list = realm.where(MsgAllBean.class)
                .equalTo("gid", gid)
                .lessThan("timestamp", time)
                .sort("timestamp", Sort.DESCENDING)
                .limit(size)
                .findAll();

        beans = realm.copyFromRealm(list);
        ;


        //翻转列表
        Collections.reverse(beans);
        realm.close();
        return beans;
    }

//    public List<MsgAllBean> getMsg4Group(String gid, Long time) {
//        if (time == null) {
//            time = 99999999999999l;
//        }
//        List<MsgAllBean> beans = new ArrayList<>();
//        Realm realm = DaoUtil.open();
//
//        RealmResults list = realm.where(MsgAllBean.class)
//                .equalTo("gid", gid)
//                .lessThan("timestamp", time)
//
//                .sort("timestamp", Sort.DESCENDING)
//                .limit(20)
//                .findAll();
//
//        beans = realm.copyFromRealm(list);
//        ;
//
//
//        //翻转列表
//        Collections.reverse(beans);
//        realm.close();
//        return beans;
//    }

    public List<MsgAllBean> getMsg4GroupImg(String gid) {

        List<MsgAllBean> beans = new ArrayList<>();
        Realm realm = DaoUtil.open();

        RealmResults list = realm.where(MsgAllBean.class)
                .equalTo("gid", gid)
                .equalTo("msg_type", 4)
                .sort("timestamp", Sort.DESCENDING)
                .findAll();

        beans = realm.copyFromRealm(list);


        //翻转列表
        Collections.reverse(beans);
        realm.close();
        return beans;
    }

    public List<MsgAllBean> getMsg4GroupHistory(String gid, Long stime) {

        //   Long time=99999999999999l;

        List<MsgAllBean> beans = new ArrayList<>();
        Realm realm = DaoUtil.open();

        RealmResults list = realm.where(MsgAllBean.class)
                .equalTo("gid", gid)
                //  .lessThan("timestamp",time)
                .greaterThanOrEqualTo("timestamp", stime)

                .sort("timestamp", Sort.DESCENDING)

                .findAll();

        beans = realm.copyFromRealm(list);
        ;


        //翻转列表
        Collections.reverse(beans);
        realm.close();
        return beans;
    }

    public List<MsgAllBean> getMsg4UserHistory(Long userid, Long stime) {

        // Long  time=99999999999999l;

        List<MsgAllBean> beans = new ArrayList<>();
        Realm realm = DaoUtil.open();

        RealmResults list = realm.where(MsgAllBean.class).beginGroup().equalTo("gid", "").or().isNull("gid").endGroup().and().beginGroup()
                .equalTo("from_uid", userid).or().equalTo("to_uid", userid).endGroup()
                //   .lessThan("timestamp",time)
                .greaterThanOrEqualTo("timestamp", stime)
                .sort("timestamp", Sort.DESCENDING)

                .findAll();

        beans = realm.copyFromRealm(list);
        ;


        //翻转列表
        Collections.reverse(beans);
        realm.close();
        return beans;
    }

    /***
     * 保存群成员到数据库
     * @param
     */
    public void groupNumberSave(Group ginfo) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        //更新信息到群成员列表
       /* Group group = realm.where(Group.class).equalTo("gid", ginfo.getGid()).findFirst();
        if (group == null) {
            group = new Group();
            group.setGid(ginfo.getGid());
            group.setAvatar(ginfo.getAvatar());
            group.setName(ginfo.getName());
            group.setMaster(ginfo.getMaster());
        }*/

        RealmList<UserInfo> nums = new RealmList<>();
        //更新信息到用户表
        for (UserInfo sv : ginfo.getUsers()) {
            UserInfo ui = realm.where(UserInfo.class).equalTo("uid", sv.getUid()).findFirst();
            if (ui == null) {
                sv.toTag();
                sv.setuType(0);
                sv = realm.copyToRealmOrUpdate(sv);
                nums.add(sv);
            } else {
                nums.add(ui);
            }
            //8.8把群的成员信息存链接表
            GropLinkInfo gropLinkInfo = realm.where(GropLinkInfo.class).equalTo("gid", ginfo.getGid()).equalTo("uid", sv.getUid()).findFirst();
            if (gropLinkInfo == null) {
                gropLinkInfo = new GropLinkInfo();
                gropLinkInfo.setLid(UUID.randomUUID().toString());
                gropLinkInfo.setGid(ginfo.getGid());
                gropLinkInfo.setUid(sv.getUid());
                gropLinkInfo.setMembername(sv.getMembername());
            } else {
                gropLinkInfo.setMembername(sv.getMembername());
            }
            realm.insertOrUpdate(gropLinkInfo);

        }
        //更新自己的群昵称
        ginfo.getMygroupName();
        ginfo.setUsers(nums);
        realm.insertOrUpdate(ginfo);
        realm.commitTransaction();
        realm.close();

    }

    /***
     * 获取群和用户的连接信息
     * @return
     */
    public GropLinkInfo getGropLinkInfo(String gid, Long uid) {
        GropLinkInfo gropLinkInfo = null;
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        GropLinkInfo info = realm.where(GropLinkInfo.class).equalTo("gid", gid).equalTo("uid", uid).findFirst();
        if (info != null) {
            gropLinkInfo = realm.copyFromRealm(info);
        }


        realm.commitTransaction();
        realm.close();
        return gropLinkInfo;
    }


    /***
     * 离线获取群信息
     * @param gid
     * @return
     */
    public Group groupNumberGet(String gid) {
        Group groupInfoBean = new Group();
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
        if (group != null) {
            groupInfoBean = realm.copyFromRealm(group);
           /* groupInfoBean.setAvatar(group.getAvatar());
            groupInfoBean.setGid(group.getGid());
            groupInfoBean.setMaster(group.getMaster());
            groupInfoBean.setUsers(group.getUsers());*/
        }


        realm.commitTransaction();
        realm.close();
        return groupInfoBean;
    }

    /***
     * 删除聊天记录
     * @param toUid
     * @param gid
     */
    public void msgDel(Long toUid, String gid) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        RealmResults<MsgAllBean> list = null;
        if (StringUtil.isNotNull(gid)) {
            list = realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("gid", gid).endGroup()
                    .and()
                    .beginGroup().notEqualTo("msg_type", ChatEnum.EMessageType.LOCK).endGroup()
                    .findAll();
        } else {
            list = realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                    .and()
                    .beginGroup().notEqualTo("msg_type", ChatEnum.EMessageType.LOCK).endGroup()
                    .and()
                    .beginGroup().equalTo("from_uid", toUid).or().equalTo("to_uid", toUid).endGroup()
                    .findAll();

        }

        //删除前先把子表数据干掉!!切记
        if (list != null) {
            for (MsgAllBean msg : list) {
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


            }
            list.deleteAllFromRealm();
        }


        realm.commitTransaction();
        realm.close();
    }

    /***
     * 单删某条
     * @param msgId
     */
    public void msgDel4MsgId(String msgId) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        RealmResults<MsgAllBean> list = null;

        list = realm.where(MsgAllBean.class).equalTo("msg_id", msgId).findAll();


        //删除前先把子表数据干掉!!切记
        if (list != null) {
            for (MsgAllBean msg : list) {
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


            }
            list.deleteAllFromRealm();
        }


        realm.commitTransaction();
        realm.close();
    }

    /***
     * 撤回消息
     * @param msgCancelId
     */
    public void msgDel4Cancel(String msgid, String msgCancelId) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        RealmResults<MsgAllBean> list = null;

        list = realm.where(MsgAllBean.class).equalTo("msg_id", msgCancelId).findAll();
        MsgAllBean cancel = realm.where(MsgAllBean.class).equalTo("msg_id", msgid).findFirst();
        if (cancel == null && list != null && list.size() > 0) {
            cancel = new MsgAllBean();
            MsgAllBean bean = list.get(0);
            cancel.setMsg_id(msgid);
            cancel.setRequest_id("" + System.currentTimeMillis());
            cancel.setFrom_uid(bean.getTo_uid());
            cancel.setTo_uid(UserAction.getMyId());
            cancel.setGid(bean.getGid());
            cancel.setMsg_type(ChatEnum.EMessageType.MSG_CENCAL);
            MsgCancel msgCel = new MsgCancel();
            msgCel.setMsgid(msgid);
            msgCel.setNote("你撤回了一条消息");
            msgCel.setMsgidCancel(msgCancelId);
            cancel.setMsgCancel(msgCel);
        }

        //删除前先把子表数据干掉!!切记
        if (list != null) {
            for (MsgAllBean msg : list) {
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

                if (cancel != null) {
                    cancel.setTimestamp(msg.getTimestamp());
                    realm.insertOrUpdate(cancel);
                }
            }
            list.deleteAllFromRealm();
        }


        realm.commitTransaction();
        realm.close();
    }

    /***
     * 清除所有的聊天记录
     */
    public void msgDelAll() {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        realm.where(MsgAllBean.class).findAll().deleteAllFromRealm();
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

        //清理角标
        RealmResults<Session> sessions = realm.where(Session.class).findAll();
        for (Session session : sessions) {
            session.setUnread_count(0);
            realm.insertOrUpdate(session);
        }


        realm.commitTransaction();
        realm.close();

    }


    /***
     * 创建群
     * @param id
     * @param avatar
     * @param name
     * @param listDataTop
     */
    public void groupCreate(String id, String avatar, String name, List<UserInfo> listDataTop) {
        sessionCreate(id, null);
        Group group = new Group();
        group.setAvatar(avatar);
        group.setGid(id);
        group.setName(name);
        RealmList<UserInfo> users = new RealmList();
        users.addAll(listDataTop);
        group.setUsers(users);
        DaoUtil.update(group);
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
     */
    public List<MsgAllBean> searchMsg4key(String key, String gid, Long uid) {


        Realm realm = DaoUtil.open();
        List<MsgAllBean> ret = new ArrayList<>();
        RealmResults<MsgAllBean> msg;
        if (StringUtil.isNotNull(gid)) {//群
            msg = realm.where(MsgAllBean.class)
                    .equalTo("gid", gid).and().equalTo("msg_type", 1).and()
                    .contains("chat.msg", key)
                    .sort("timestamp", Sort.DESCENDING)
                    .findAll();
        } else {//单人
            msg = realm.where(MsgAllBean.class).equalTo("gid", "").equalTo("msg_type", 1)
                    .contains("chat.msg", key).beginGroup()
                    .equalTo("from_uid", uid).or().equalTo("to_uid", uid).endGroup()


                    .sort("timestamp", Sort.DESCENDING)
                    .findAll();
        }

        if (msg != null)
            ret = realm.copyFromRealm(msg);
        realm.close();
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
            }

        } else {//个人消息
            session = DaoUtil.findOne(Session.class, "from_uid", toUid);
            if (session == null) {
                session = new Session();
                session.setSid(UUID.randomUUID().toString());
                session.setFrom_uid(toUid);
                session.setType(0);
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
     * 会话未读数量+1
     * @param gid 群id
     * @param from_uid 单人id
     */
    public void sessionReadUpdate(String gid, Long from_uid) {
        sessionReadUpdate(gid, from_uid, false);
    }

    public void sessionReadUpdate(String gid, Long from_uid, boolean isCancel) {
        Session session;
        if (StringUtil.isNotNull(gid)) {//群消息
            session = DaoUtil.findOne(Session.class, "gid", gid);
            if (session == null) {
                session = new Session();
                session.setSid(UUID.randomUUID().toString());
                session.setGid(gid);
                session.setType(1);

                session.setUnread_count(isCancel ? 0 : 1);

            } else {
                int num = isCancel ? session.getUnread_count() - 2 : session.getUnread_count() + 1;
                num = num < 0 ? 0 : num;
                session.setUnread_count(num);
            }
            session.setUp_time(System.currentTimeMillis());


        } else {//个人消息
            session = DaoUtil.findOne(Session.class, "from_uid", from_uid);
            if (session == null) {
                session = new Session();
                session.setSid(UUID.randomUUID().toString());
                session.setFrom_uid(from_uid);
                session.setType(0);
                session.setUnread_count(isCancel ? 0 : 1);

            } else {
                int num = isCancel ? session.getUnread_count() - 2 : session.getUnread_count() + 1;
                num = num < 0 ? 0 : num;
                session.setUnread_count(num);
            }
            session.setUp_time(System.currentTimeMillis());


        }
        if (isCancel) {//如果是撤回at消息,星哥说把类型给成这个,at就会去掉
            session.setMessageType(1000);
        }

        DaoUtil.update(session);
    }

    /***
     * 清理单个会话阅读数量
     * @param gid
     * @param from_uid
     */
    public void sessionReadClean(String gid, Long from_uid) {
        Session session = StringUtil.isNotNull(gid) ? DaoUtil.findOne(Session.class, "gid", gid) :
                DaoUtil.findOne(Session.class, "from_uid", from_uid);
        if (session != null) {
            session.setUnread_count(0);
            //  session.setUp_time(System.currentTimeMillis());
            DaoUtil.update(session);
        }

    }

    /***
     * 查询会话所有未读消息
     * @return
     */
    public int sessionReadGetAll() {
        int sum = 0;
        Realm realm = DaoUtil.open();
        List<Session> list = realm.where(Session.class).findAll();

        if (list != null) {
            for (Session s : list) {
                sum += s.getUnread_count();
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
     * 存草稿
     * @param gid
     * @param uid
     * @param draft
     */
    public void sessionDraft(String gid, Long uid, String draft) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        Session session = StringUtil.isNotNull(gid) ? realm.where(Session.class).equalTo("gid", gid).findFirst() : realm.where(Session.class).equalTo("from_uid", uid).findFirst();


        if (session != null) {
            session.setDraft(draft);
            session.setMessageType(2);
            realm.insertOrUpdate(session);
        }
        realm.commitTransaction();
        realm.close();
    }

    /**
     * 存at消息
     */
    public void atMessage(String gid, String atMessage, int type) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        Session session = realm.where(Session.class).equalTo("gid", gid).findFirst();

        if (session != null) {
            session.setAtMessage(atMessage);
            session.setMessageType(type);
            realm.insertOrUpdate(session);
        }
        realm.commitTransaction();
        realm.close();
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
     * 清理红点
     * @param type
     */
    public void remidClear(String type) {
        Remind remind = DaoUtil.findOne(Remind.class, "remid_type", type);
        if (remind != null) {
            remind.setNumber(0);
            DaoUtil.update(remind);
        }
    }

    /***
     * 红点加一
     * @param type
     */
    public void remidCount(String type) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        Remind remind = realm.where(Remind.class).equalTo("remid_type", type).findFirst();
        int readnum = remind == null ? 1 : remind.getNumber() + 1;
        Remind newreamid = new Remind();
        newreamid.setNumber(readnum);
        newreamid.setRemid_type(type);
        realm.insertOrUpdate(newreamid);
        realm.commitTransaction();
        realm.close();

    }


    /***
     * 获取单个会话阅读量
     * @param gid
     * @param from_uid
     * @return
     */
    public int sessionReadGet(String gid, Long from_uid) {
        int sum = 0;
        Session session = StringUtil.isNotNull(gid) ? DaoUtil.findOne(Session.class, "gid", gid) :
                DaoUtil.findOne(Session.class, "from_uid", from_uid);
        if (session != null) {
            sum = session.getUnread_count();
        }


        return sum;
    }

    /***
     * 获取所有会话
     * @param isAll 是否剔除小助手，true不剔除，false剔除
     * @return
     */
    public List<Session> sessionGetAll(boolean isAll) {
        List<Session> rts;
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        RealmResults<Session> list;
        if (isAll) {
            list = realm.where(Session.class).sort("up_time", Sort.DESCENDING).findAll();
        } else {
            list = realm.where(Session.class).beginGroup().notEqualTo("from_uid", 1L).and().isNotNull("from_uid").endGroup().
                    or().isNotNull("gid").sort("up_time", Sort.DESCENDING).findAll();
        }
        //6.5 优先读取单独表的配置

        for (Session l : list) {
            int top = 0;

            try {
                if (l.getType() == 1) {
                    top = realm.where(Group.class).equalTo("gid", l.getGid()).findFirst().getIsTop();
                } else {
                    top = realm.where(UserInfo.class).equalTo("uid", l.getFrom_uid()).findFirst().getIstop();
                }
            } catch (Exception e) {
                //   e.printStackTrace();
            }

            l.setIsTop(top);


        }
        realm.copyToRealmOrUpdate(list);


        list = list.sort("isTop", Sort.DESCENDING);
        rts = realm.copyFromRealm(list);

        realm.commitTransaction();
        realm.close();

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
        MsgAllBean bean = realm.where(MsgAllBean.class).equalTo("gid", "").and().equalTo("from_uid", uid).or().equalTo("to_uid", uid)
                .sort("timestamp", Sort.DESCENDING).findFirst();
        if (bean != null) {
            ret = realm.copyFromRealm(bean);
        }

        realm.close();
        return ret;
    }

    /***
     * 获取群最后的消息
     * @param uid
     * @return
     */
    public MsgAllBean msgGetLastGroup4Uid(String gid, Long uid) {
        MsgAllBean ret = null;
        Realm realm = DaoUtil.open();
        MsgAllBean bean = realm.where(MsgAllBean.class).equalTo("gid", gid).and().equalTo("from_uid", uid).or().equalTo("to_uid", uid)
                .sort("timestamp", Sort.DESCENDING).findFirst();
        if (bean != null) {
            ret = realm.copyFromRealm(bean);
        }

        realm.close();
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

    /***
     * 保存群状态
     * @param gid
     * @param notNotify
     * @param saved
     * @param needVerification
     */
    public void saveSession4Switch(String gid, Integer isTop, Integer notNotify, Integer saved, Integer needVerification) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        Session session = DaoUtil.findOne(Session.class, "gid", gid);
        if (session == null)
            return;
        if (notNotify != null)
            session.setIsMute(notNotify);

        if (isTop != null)
            session.setIsTop(isTop);

        realm.insertOrUpdate(session);

        realm.commitTransaction();
        realm.close();
    }


    /**
     * 通讯录好友申请添加好友
     */
    public void userAcceptAdd(Long uid, String contactName) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        ContactNameBean contactNameBean = realm.where(ContactNameBean.class).equalTo("uid", uid).findFirst();
        if (contactNameBean == null) {
            contactNameBean = new ContactNameBean();
            contactNameBean.setUid(uid);
            contactNameBean.setContactName(contactName);
        } else {
            contactNameBean.setContactName(contactName);
        }

        realm.insertOrUpdate(contactNameBean);
        realm.commitTransaction();
        realm.close();
    }

    /**
     * 获取contactName
     */
    public ContactNameBean getContactName(Long uid) {
        Realm realm = DaoUtil.open();
        ContactNameBean contactNameBean = realm.where(ContactNameBean.class).equalTo("uid", uid).findFirst();

        return contactNameBean;
    }


    /***
     * 群申请
     * @param gid
     * @param fromUid
     * @param nickname
     */
    public void groupAcceptAdd(int joinType, long inviter, String inviterName, String gid, long fromUid, String nickname, String head) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();


        GroupAccept accept = realm.where(GroupAccept.class).equalTo("gid", gid).equalTo("uid", fromUid).findFirst();
        if (accept == null) {
            accept = new GroupAccept();
            accept.setAid(UUID.randomUUID().toString());
            accept.setGid(gid);
        }
        accept.setTime(System.currentTimeMillis());

        Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
        if (group != null) {
            accept.setGroupName(group.getName());
        }

        accept.setUid(fromUid);
        accept.setUname(nickname);
        accept.setHead(head);
        accept.setInviter(inviter);
        accept.setInviterName(inviterName);
        accept.setJoinType(joinType);

        realm.insertOrUpdate(accept);

        realm.commitTransaction();
        realm.close();
    }

    public List<GroupAccept> groupAccept() {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        List<GroupAccept> beans = new ArrayList<>();
        RealmResults<GroupAccept> res = realm.where(GroupAccept.class).sort("time", Sort.DESCENDING).findAll();
        if (res != null) {
            beans = realm.copyFromRealm(res);
        }
        realm.commitTransaction();
        realm.close();
        return beans;
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

    /**
     * 移除这条群申请
     *
     * @param aid
     */
    public void groupAcceptRemove(String aid) {
        DaoUtil.deleteOne(GroupAccept.class, "aid", aid);

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
     * @param isOpen
     */
    public void redEnvelopeOpen(String rid, boolean isOpen) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        RedEnvelopeMessage envelopeMessage = realm.where(RedEnvelopeMessage.class).equalTo("id", rid).findFirst();
        if (envelopeMessage != null) {
            envelopeMessage.setIsInvalid(isOpen ? 1 : 0);
            realm.insertOrUpdate(envelopeMessage);
        }

        realm.commitTransaction();
        realm.close();
    }


    //7.8 要写语音已读的处理
    public void msgRead(String msgid, boolean isRead) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        MsgAllBean msgBean = realm.where(MsgAllBean.class).equalTo("msg_id", msgid).findFirst();
        if (msgBean != null) {
            msgBean.setRead(isRead);
            realm.insertOrUpdate(msgBean);
        }

        realm.commitTransaction();
        realm.close();
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
        if (userSeting == null) {
            userSeting = new UserSeting();
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
        realm.beginTransaction();
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
                GropLinkInfo gropLinkInfo = null;
                if (StringUtil.isNotNull(gid)) {
                    gropLinkInfo = realm.where(GropLinkInfo.class).equalTo("gid", gid).equalTo("uid", uid).findFirst();
                }

                if (gropLinkInfo != null) {
                    //2.获取群成员昵称
                    name = StringUtil.isNotNull(gropLinkInfo.getMembername()) ? gropLinkInfo.getMembername() : name;
                }
            }


            //3.获取用户备注名
            name = StringUtil.isNotNull(userInfo.getMkName()) ? userInfo.getMkName() : name;


        } else {
            name = StringUtil.isNotNull(uname) ? uname : name;
            name = StringUtil.isNotNull(groupName) ? groupName : name;

        }


        realm.commitTransaction();
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
            UserInfo userinfo = UserAction.getMyInfo();
            msgAllBean.setFrom_uid(toUid);

            msgAllBean.setTo_uid(userinfo.getUid());


        }

        msgAllBean.setMsg_type(ChatEnum.EMessageType.NOTICE);
        msgAllBean.setMsgNotice(note);
        msgAllBean.setTimestamp(new Date().getTime());

        realm.insertOrUpdate(msgAllBean);

        realm.commitTransaction();
        realm.close();

        return ret;

    }

    /***
     * 把发送中的状态修改为发送失败
     */
    public void msgSendStateToFail() {
        Realm realm = DaoUtil.open();
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
    }

    //是否存在该消息
    public boolean isMsgLockExist(String gid, Long uid) {
        MsgAllBean ret = null;
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
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

        realm.commitTransaction();
        realm.close();
        if (ret != null) {
            return true;
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
//        List<Group> allGroups = new ArrayList<>();

        RealmResults<Group> groups = realm.where(Group.class).findAll();
        RealmResults<Group> keyGroups = groups.where().contains("name", key).findAll();
        if (keyGroups != null) {
            ret = realm.copyFromRealm(keyGroups);
        }
//        if (groups != null) {
//            allGroups = realm.copyFromRealm(groups);
//        }
        for (int i = 0; i < groups.size(); i++) {
            Group g = groups.get(i);
            if (ret.contains(g)) {
                continue;
            } else {
                RealmList<UserInfo> userInfos = g.getUsers();
                UserInfo userInfo = userInfos.where()
                        .beginGroup().contains("name", key).endGroup()
                        .or()
                        .beginGroup().contains("mkName", key).endGroup()
                        .findFirst();
                if (userInfo != null) {
                    Group group = realm.copyFromRealm(g);
                    UserInfo info = realm.copyFromRealm(userInfo);
                    group.setKeyUser(info);
                    ret.add(group);
                } else {
                    GropLinkInfo gropLinkInfo = realm.where(GropLinkInfo.class)
                            .beginGroup().equalTo("gid", g.getGid()).endGroup()
                            .and()
                            .beginGroup().contains("membername", key).endGroup()
                            .findFirst();
                    if (gropLinkInfo != null) {
                        userInfo = userInfos.where()
                                .beginGroup().equalTo("uid", gropLinkInfo.getUid()).endGroup()
                                .findFirst();
                        if (userInfo != null) {
                            Group group = realm.copyFromRealm(g);
                            UserInfo info = realm.copyFromRealm(userInfo);
                            info.setMembername(gropLinkInfo.getMembername());
                            group.setKeyUser(info);
                            ret.add(group);
                        }
                    }
                }
            }
        }
        realm.commitTransaction();
        realm.close();
        return ret;


    }


}
