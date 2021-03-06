package com.yanlong.im.data.local;

import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.common.base.Joiner;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/3/26 0026
 * @description 更新session详情
 */
public class UpdateSessionDetail {
    private Realm realm = null;
    private Handler handler = new Handler();
    //最大重试次数,刚启动Application时，数据库事务无法立即建立，会出现事务异常
    private final int MAX_UPDATE_RETRY_TIMES = 3;
    //update(String[] sids) 重试次数
    private int updateSidsTimes = 0;

    public UpdateSessionDetail(@NonNull Realm realm) {
        this.realm = realm;
    }

    public void update(String[] sids) {
        //通过使用异步事务，Realm 会在后台线程中进行写入操作，并在事务完成时将结果传回调用线程。
        DaoUtil.executeTransactionAsync(realm, new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {//异步线程更新更新
                //获取session列表-本地数据
                RealmResults<Session> sessions = realm.where(Session.class).in("sid", sids).sort("up_time", Sort.DESCENDING).findAll();
                for (int i = 0; i < sessions.size(); i++) {
                    Session session = sessions.get(i);
//                        realm.beginTransaction();
                    if (session.getType() == 1) {//群聊
                        synchGroupMsgSession(realm, session, null);
                    } else {//单聊
                        synchFriendMsgSession(realm, session, null);
                    }
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                updateSidsTimes = 0;
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                if (updateSidsTimes < MAX_UPDATE_RETRY_TIMES) {
                    //1，秒后重试
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            update(sids);
                        }
                    }, 1000);
                    updateSidsTimes++;
                } else {//超过最大次数，不再重试
                    updateSidsTimes = 0;
                }

            }
        });
    }

    public void update(String[] gids, Long[] fromUids) {
        DaoUtil.executeTransactionAsync(realm, new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (gids != null && gids.length > 0) {
                    RealmResults<Session> groupSessions = realm.where(Session.class).in("gid", gids).findAll();
                    for (Session session : groupSessions) {
                        synchGroupMsgSession(realm, session, null);
                    }
                }
                if (fromUids != null && fromUids.length > 0) {
                    RealmResults<Session> friendSessions = realm.where(Session.class).in("from_uid", fromUids).findAll();
                    for (Session session : friendSessions) {
                        synchFriendMsgSession(realm, session, null);
                    }
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {

            }
        });

    }

    /**
     * 更新我自己的session会话的所有群聊
     */
    public void updateSelfGroup() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Session> groupSessions = realm.where(Session.class).isNotEmpty("gid").findAll();
                for (Session session : groupSessions) {
                    synchGroupMsgSession(realm, session, null);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
            }
        });
    }

    /**
     * 清除会话详情的内容
     *
     * @param
     */
    public void clearContent(String[] gids, Long[] fromUids) {
        DaoUtil.executeTransactionAsync(realm, new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (gids != null && gids.length > 0) {
                    RealmResults<Session> groupSessions = realm.where(Session.class).in("gid", gids).findAll();
                    for (Session session : groupSessions) {
                        SessionDetail sessionDetail = realm.where(SessionDetail.class).equalTo("sid", session.getSid()).findFirst();
                        sessionDetail.setMessage(null);
                        sessionDetail.setMessageContent(null);
                        sessionDetail.setSenderName(null);
                    }
                }
                if (fromUids != null && fromUids.length > 0) {
                    RealmResults<Session> friendSessions = realm.where(Session.class).in("from_uid", fromUids).findAll();
                    for (Session session : friendSessions) {
                        SessionDetail sessionDetail = realm.where(SessionDetail.class).equalTo("sid", session.getSid()).findFirst();
                        sessionDetail.setMessage(null);
                        sessionDetail.setMessageContent(null);
                        sessionDetail.setSenderName(null);
                    }
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {

            }
        });
    }

    /**
     * //异步数据库线程事务中调用，当前即将被删除，更新为不包含当前消息的最新一条消息
     *
     * @param realm
     * @param gid
     */
    public void updateLastDetail(Realm realm, String gid, String[] msgIds) {
        if (!TextUtils.isEmpty(gid)) {
            Session session = realm.where(Session.class).equalTo("gid", gid).findFirst();
            if (session != null) synchGroupMsgSession(realm, session, msgIds);
        }
    }

    /**
     * //异步数据库线程事务中调用，当前即将被删除，更新为不包含当前消息的最新一条消息
     *
     * @param realm
     * @param fromUid
     */
    public void updateLastDetail(Realm realm, Long fromUid, String[] msgIds) {
        Session session = realm.where(Session.class)
                .equalTo("from_uid", fromUid)
                .findFirst();
        if (session != null) synchFriendMsgSession(realm, session, msgIds);
    }

    /**
     * 同步群聊数据
     */
    private void synchGroupMsgSession(Realm realm, Session session, String[] msgIds) {
        try {
            Group group = realm.where(Group.class).equalTo("gid", session.getGid()).findFirst();
            /**
             * 注意：异步线程中只能查询已存在的，或者用createObject方式新建的方式更新对象，否则报错
             * 已存在的对象不能createObject
             */
            SessionDetail sessionMore = realm.where(SessionDetail.class).equalTo("sid", session.getSid()).findFirst();
            if (sessionMore == null)
                sessionMore = realm.createObject(SessionDetail.class, session.getSid());
            if (group != null) {
                sessionMore.setName(getGroupName(group));
                if (!TextUtils.isEmpty(group.getAvatar())) {
                    sessionMore.setAvatar(group.getAvatar());
                } else {
                    if (group.getStat() == 1) {//群已被解散，不显示头像
                        sessionMore.setAvatarList(null);
                    } else {
                        if (group.getUsers() != null) {
                            int i = group.getUsers().size();
                            i = i > 9 ? 9 : i;
                            //头像地址
                            List<String> headList = new RealmList<>();
                            for (int j = 0; j < i; j++) {
                                MemberUser userInfo = group.getUsers().get(j);
                                String userHead = userInfo.getHead();
                                if (userInfo.getUid() == UserAction.getMyId()) {
                                    //我自己，使用本地数据
                                    userHead = UserAction.getMyInfo().getHead();
                                }
                                headList.add(userHead.length() == 0 ? "-" : userHead);
                            }
                            //将list转string,逗号分隔的字符串
                            sessionMore.setAvatarList(Joiner.on(",").join(headList));
                        }
                    }

                }
            }
            MsgAllBean msg = null;
            if (msgIds == null || msgIds.length == 0) {//最新一条
                msg = realm.where(MsgAllBean.class).equalTo("gid", session.getGid())
                        .sort("timestamp", Sort.DESCENDING).findFirst();
            } else {//过滤指定消息后的最新一条-解决阅后即焚更新问题
                msg = realm.where(MsgAllBean.class).equalTo("gid", session.getGid())
                        .and()
                        .not().in("msg_id", msgIds)
                        .sort("timestamp", Sort.DESCENDING).findFirst();

            }

            if (msg != null) {
                sessionMore.setMessage(msg);
                sessionMore.setMessageContent(msg.getMsg_typeStr());
                if (msg.getMsg_type() == ChatEnum.EMessageType.MSG_CANCEL) {//最后一条是撤销消息，去掉红色标志
                    if (session.getMessageType() != 1000) session.setMessageType(1000);
                }

                //若消息时间大于session时间，则更新（为处理本地创建的消息）
                if (msg.getTimestamp() > session.getUp_time()) {
                    session.setUp_time(msg.getTimestamp());
                }
                if (msg.getMsg_type() == ChatEnum.EMessageType.NOTICE || msg.getMsg_type() == ChatEnum.EMessageType.MSG_CANCEL) {//通知不要加谁发的消息
                    sessionMore.setSenderName("");
                } else {
                    if (msg.getFrom_uid().longValue() != UserAction.getMyId().longValue()) {//自己的不加昵称
                        //8.9 处理群昵称
                        String name = getUsername4Show(realm, msg.getGid(), msg.getFrom_uid(), msg.getFrom_nickname(), msg.getFrom_group_nickname()) + " : ";
                        sessionMore.setSenderName(name);
                    } else {
                        sessionMore.setSenderName("");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步单聊数据-session
     *
     * @param realm
     */
    private void synchFriendMsgSession(Realm realm, Session session, String[] msgIds) {
        try {
            UserInfo info = realm.where(UserInfo.class).equalTo("uid", session.getFrom_uid()).findFirst();
            /**
             * 注意：异步线程中只能查询已存在的，或者用createObject方式新建的方式更新对象，否则报错
             * 已存在的对象不能createObject
             */
            SessionDetail sessionMore = realm.where(SessionDetail.class).equalTo("sid", session.getSid()).findFirst();
            if (sessionMore == null)
                sessionMore = realm.createObject(SessionDetail.class, session.getSid());
            if (info != null) {
                sessionMore.setName(info.getName4Show());
                sessionMore.setAvatar(info.getHead());
            }

            MsgAllBean msg = null;
            if (msgIds == null || msgIds.length == 0) {//最新一条
                msg = realm.where(MsgAllBean.class)
                        .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                        .and()
                        .beginGroup().equalTo("from_uid", session.getFrom_uid()).or().equalTo("to_uid", session.getFrom_uid()).endGroup()
                        .sort("timestamp", Sort.DESCENDING).findFirst();
            } else {//过滤指定消息后的最新一条-解决阅后即焚更新问题
                msg = realm.where(MsgAllBean.class)
                        .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                        .and()
                        .beginGroup().equalTo("from_uid", session.getFrom_uid()).or().equalTo("to_uid", session.getFrom_uid()).endGroup()
                        .and()
                        .not().in("msg_id", msgIds)
                        .sort("timestamp", Sort.DESCENDING).findFirst();
            }

            if (msg != null) {
                //若消息时间大于session时间，则更新（为处理本地创建的消息）
                if (msg.getTimestamp() > session.getUp_time()) {
                    session.setUp_time(msg.getTimestamp());
                }
                sessionMore.setMessage(msg);
                sessionMore.setMessageContent(msg.getMsg_typeStr());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * 获取用户需要展示的群名字
     * @param gid
     * @param uid
     * @param uname 用户最新的昵称
     * @param groupName 群最新的昵称
     * @return
     */
    public String getUsername4Show(Realm realm, String gid, Long uid, String uname, String groupName) {
        String name = "";
        try {
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
        } catch (Exception e) {
        }
        return name;
    }

    /*
     * 动态获取群名
     * */
    public String getGroupName(Group group) {
        String result = "";
        try {
            result = group.getName();
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
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 标记session已读未读功能，0为已读，1为未读
     *
     * @param
     */
    public void markSessionRead(String sid, int read, String msgId) {
        DaoUtil.executeTransactionAsync(realm, new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Session session = realm.where(Session.class).equalTo("sid", sid).findFirst();
                if (session != null) {
                    //设置已读
                    session.setMarkRead(read);
                    if (read == 0) {
                        session.setUnread_count(0);
                    }
                }
                MsgAllBean msg = realm.where(MsgAllBean.class).equalTo("msg_id", msgId).findFirst();
                if (msg != null) {
                    if (read == 0) {
                        msg.setRead(true);
                        SessionDetail sessionMore = realm.where(SessionDetail.class).equalTo("sid", session.getSid()).findFirst();
                        if (sessionMore != null) {
                            sessionMore.setMessage(msg);
                        }
                    }
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
//                LogUtil.getLog().i("update-Liszt", "markSessionRead -- 成功");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
//                LogUtil.getLog().i("update-Liszt", "markSessionRead -- 失败");
            }
        });
    }

    /**
     * 标记session已读未读功能,0未读，1已读
     *
     * @param
     */
    public void updateMsgRead(String sid, String msgId, int read) {
        DaoUtil.executeTransactionAsync(realm, new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MsgAllBean msg = realm.where(MsgAllBean.class).equalTo("msg_id", msgId).findFirst();
                if (msg != null) {
                    //设置为陌生人
                    msg.setRead(read);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
//                LogUtil.getLog().i("update-Liszt", "updateMsgRead -- 成功");
                update(new String[]{sid});
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
//                LogUtil.getLog().i("update-Liszt", "updateMsgRead -- 失败");
            }
        });
    }

}
