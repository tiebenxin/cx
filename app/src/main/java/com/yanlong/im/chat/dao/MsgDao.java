package com.yanlong.im.chat.dao;

import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Remind;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
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
        if(time==null){
            time=99999999999999l;
        }
        List<MsgAllBean> beans = new ArrayList<>();
        Realm realm = DaoUtil.open();

        RealmResults list = realm.where(MsgAllBean.class).equalTo("gid", "").beginGroup()
                .equalTo("from_uid", userid).or().equalTo("to_uid", userid).endGroup()
                .lessThan("timestamp",time)

                .sort("timestamp", Sort.DESCENDING)
                .limit(20)
                .findAll();

        beans = realm.copyFromRealm(list);;


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


    public List<MsgAllBean> getMsg4Group(String gid, Long time) {
        if(time==null){
            time=99999999999999l;
        }
        List<MsgAllBean> beans = new ArrayList<>();
        Realm realm = DaoUtil.open();

        RealmResults list = realm.where(MsgAllBean.class)
                .equalTo("gid", gid)
                .lessThan("timestamp",time)

                .sort("timestamp", Sort.DESCENDING)
                .limit(20)
                .findAll();

        beans = realm.copyFromRealm(list);;


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
                .greaterThanOrEqualTo("timestamp",stime)

                .sort("timestamp", Sort.DESCENDING)

                .findAll();

        beans = realm.copyFromRealm(list);;


        //翻转列表
        Collections.reverse(beans);
        realm.close();
        return beans;
    }
    public List<MsgAllBean> getMsg4UserHistory(Long userid, Long stime) {

       // Long  time=99999999999999l;

        List<MsgAllBean> beans = new ArrayList<>();
        Realm realm = DaoUtil.open();

        RealmResults list = realm.where(MsgAllBean.class).equalTo("gid", "").beginGroup()
                .equalTo("from_uid", userid).or().equalTo("to_uid", userid).endGroup()
             //   .lessThan("timestamp",time)
                .greaterThanOrEqualTo("timestamp",stime)
                .sort("timestamp", Sort.DESCENDING)

                .findAll();

        beans = realm.copyFromRealm(list);;


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

        }
        //更新自己的群昵称
        ginfo.getMygroupName();

        ginfo.setUsers(nums);
        realm.insertOrUpdate(ginfo);


        realm.commitTransaction();
        realm.close();

    }

    /***
     * 离线获取群信息
     * @param gid
     * @return
     */
    public Group groupNumberGet(String gid){
        Group groupInfoBean=new Group();
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        Group group = realm.where(Group.class).equalTo("gid", gid).findFirst();
        if(group!=null){
            group=realm.copyFromRealm(group);
            groupInfoBean.setAvatar(group.getAvatar());
            groupInfoBean.setGid(group.getGid());
            groupInfoBean.setMaster(group.getMaster());
            groupInfoBean.setUsers(group.getUsers());
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
        if (StringUtil.isNotNull(gid)) {
            realm.where(MsgAllBean.class).equalTo("gid", gid).findAll().deleteAllFromRealm();
        } else {

            realm.where(MsgAllBean.class).equalTo("gid", "").and().equalTo("from_uid", toUid).or().equalTo("to_uid", toUid).findAll().deleteAllFromRealm();
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
                    .equalTo("gid", gid).and().equalTo("msg_type",1).and()
                    .contains("chat.msg", key)
                    .sort("timestamp", Sort.DESCENDING)
                    .findAll();
        } else {//单人
            msg = realm.where(MsgAllBean.class).equalTo("gid", "").equalTo("msg_type",1)
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
        Session session;
        if (StringUtil.isNotNull(gid)) {//群消息
            session = DaoUtil.findOne(Session.class, "gid", gid);
            if (session == null) {
                session = new Session();
                session.setSid(UUID.randomUUID().toString());
                session.setGid(gid);
                session.setType(1);
                session.setUnread_count(1);

            } else {
                session.setUnread_count(session.getUnread_count() + 1);
            }
            session.setUp_time(System.currentTimeMillis());


        } else {//个人消息
            session = DaoUtil.findOne(Session.class, "from_uid", from_uid);
            if (session == null) {
                session = new Session();
                session.setSid(UUID.randomUUID().toString());
                session.setFrom_uid(from_uid);
                session.setType(0);
                session.setUnread_count(1);

            } else {
                session.setUnread_count(session.getUnread_count() + 1);
            }
            session.setUp_time(System.currentTimeMillis());


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
        int readnum = remind == null ? 1 : remind.getNumber()+1;
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
     * @return
     */
    public List<Session> sessionGetAll() {
        List<Session> rts;
        Realm realm = DaoUtil.open();
        realm.beginTransaction();

        RealmResults<Session> list = realm.where(Session.class).sort("up_time", Sort.DESCENDING).findAll();
        //6.5 优先读取单独表的配置

        for(Session l:list){
            int top=0;

            try {
                if(l.getType()==1){
                    top=realm.where(Group.class).equalTo("gid",l.getGid()).findFirst().getIsTop();
                }else{
                    top=realm.where(UserInfo.class).equalTo("uid",l.getFrom_uid()).findFirst().getIstop();
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        if (notNotify != null)
            session.setIsMute(notNotify);

        if (isTop != null)
            session.setIsTop(isTop);

        realm.insertOrUpdate(session);

        realm.commitTransaction();
        realm.close();
    }


}
