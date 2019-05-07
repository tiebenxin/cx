package com.yanlong.im.chat.dao;

import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MsgDao {
    //分页数量
    private int pSize = 10;

    /***
     * 单用户消息列表
     * @param userid
     * @return
     */
    public List<MsgAllBean> getMsg4User(Long userid, int page) {
        List<MsgAllBean> beans = new ArrayList<>();
        Realm realm = DaoUtil.open();

        RealmResults list = realm.where(MsgAllBean.class)
                .equalTo("from_uid", userid).or().equalTo("to_uid", userid)
                .and().equalTo("gid", "").and()
                .notEqualTo("msg_type", 0)
                .sort("timestamp", Sort.DESCENDING)
                .findAll();

        beans = DaoUtil.page(page, list, realm);


        //翻转列表
        Collections.reverse(beans);
        realm.close();
        return beans;
    }


    /***
     * 创建会话数量
     * @param gid
     * @param from_uid
     */
    public void sessionCreate(String gid, Long from_uid) {
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
            session = DaoUtil.findOne(Session.class, "from_uid", from_uid);
            if (session == null) {
                session = new Session();
                session.setSid(UUID.randomUUID().toString());
                session.setFrom_uid(from_uid);
                session.setType(0);
            }
        }

        session.setUnread_count(0);
        session.setUp_time(System.currentTimeMillis());
        DaoUtil.update(session);
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
        }
        session.setUp_time(System.currentTimeMillis());
        DaoUtil.update(session);
    }

    /***
     * 查询会话所有未读消息
     * @return
     */
    public int sessionReadGetAll() {
        int sum = 0;
        Realm realm = DaoUtil.open();
        List<Session> list = realm.where(Session.class).sort("up_time", Sort.DESCENDING).findAll();

        if (list != null) {
            for (Session s : list) {
                sum += s.getUnread_count();
            }
        }

        realm.close();
        return sum;
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
        return DaoUtil.findAll(Session.class);
    }


    /***
     * 获取最后的消息
     * @param uid
     * @return
     */
    public MsgAllBean msgGetLast4FUid(Long uid) {
        MsgAllBean ret;
        Realm realm = DaoUtil.open();
        MsgAllBean bean = realm.where(MsgAllBean.class).equalTo("from_uid", uid).or().equalTo("to_uid", uid)
                .sort("timestamp", Sort.DESCENDING).findFirst();
        ret= realm.copyFromRealm(bean);
        realm.close();
      return  ret;
    }

    /***
     * 获取最后的群消息
     * @param gid
     * @return
     */
    public MsgAllBean msgGetLast4Gid(String gid) {
        MsgAllBean ret;
        Realm realm = DaoUtil.open();
        MsgAllBean bean = realm.where(MsgAllBean.class).equalTo("gid", gid)
                .sort("timestamp", Sort.DESCENDING).findFirst();
        ret= realm.copyFromRealm(bean);
        realm.close();
        return  ret;
    }


}
