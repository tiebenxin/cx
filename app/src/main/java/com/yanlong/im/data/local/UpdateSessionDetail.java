package com.yanlong.im.data.local;

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

    public UpdateSessionDetail(@NonNull Realm realm) {
        this.realm = realm;
        update();
    }

    public void update() {
        try {
            //通过使用异步事务，Realm 会在后台线程中进行写入操作，并在事务完成时将结果传回调用线程。
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {//异步线程更新更新
                        //获取session列表-本地数据
                        RealmResults<Session> sessions = realm.where(Session.class).sort("up_time", Sort.DESCENDING).findAll();
                        for (int i = 0; i < sessions.size(); i++) {
                            Session session = sessions.get(i);
//                        realm.beginTransaction();
                            if (session.getType() == 1) {//群聊
                                synchGroupMsgSession(realm, session);
                            } else {//单聊
                                synchFriendMsgSession(realm, session);
                            }
                        }

                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    LogUtil.writeLog("UpdateSessionDetail executeTransactionAsync Success");
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    LogUtil.writeLog("UpdateSessionDetail error");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 同步群聊数据
     */
    private void synchGroupMsgSession(Realm realm, Session session) {
        try {
            Group group = realm.where(Group.class).equalTo("gid", session.getGid()).findFirst();
            SessionDetail sessionMore = new SessionDetail();
            sessionMore.setSid(session.getSid());
            if (group != null) {
                sessionMore.setName(getGroupName(group));
                if (!TextUtils.isEmpty(group.getAvatar())) {
                    sessionMore.setAvatar(group.getAvatar());
                } else {
                    if (group.getUsers() != null) {
                        int i = group.getUsers().size();
                        i = i > 9 ? 9 : i;
                        //头像地址
                        List<String> headList = new RealmList<>();
                        for (int j = 0; j < i; j++) {
                            MemberUser userInfo = group.getUsers().get(j);
                            headList.add(userInfo.getHead().length()==0?"-":userInfo.getHead());
                        }
                        //将list转string,逗号分隔的字符串
                        sessionMore.setAvatarList(Joiner.on(",").join(headList));
                    }
                }
            } else {
//            sessionMore.setName(getGroupName(group));
            }
            MsgAllBean msg = session.getMessage();
            if (msg == null) {
                msg = realm.where(MsgAllBean.class).equalTo("gid", session.getGid())
                        .sort("timestamp", Sort.DESCENDING).findFirst();
            }
            if (msg != null) {
                sessionMore.setMessage(msg);
                if (msg.getMsg_type() == ChatEnum.EMessageType.NOTICE || msg.getMsg_type() == ChatEnum.EMessageType.MSG_CANCEL) {//通知不要加谁发的消息
                    sessionMore.setSenderName("");
                } else {
                    if (msg.getFrom_uid().longValue() != UserAction.getMyId().longValue()) {//自己的不加昵称
                        //8.9 处理群昵称
                        String name = getUsername4Show(msg.getGid(), msg.getFrom_uid(), msg.getFrom_nickname(), msg.getFrom_group_nickname()) + " : ";
                        sessionMore.setSenderName(name);
                    }
                }
            }
            realm.copyToRealmOrUpdate(sessionMore);
        }catch (Exception e){
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

    /*
     * 动态获取群名
     * */
    public String getGroupName(Group group) {
        String result = "";
        result = group.getName();
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

    /**
     * 同步单聊数据-session
     *
     * @param realm
     */
    private void synchFriendMsgSession(Realm realm, Session session) {
        UserInfo info = realm.where(UserInfo.class).equalTo("uid", session.getFrom_uid()).findFirst();
        SessionDetail sessionMore = new SessionDetail();
        sessionMore.setSid(session.getSid());
        if (info != null) {
            sessionMore.setName(info.getName4Show());
            sessionMore.setAvatar(info.getHead());
        }

        MsgAllBean msg = session.getMessage();
        if (msg == null) {
            msg = realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("gid", "").or().isNull("gid").endGroup()
                    .and()
                    .beginGroup().equalTo("from_uid", session.getFrom_uid()).or().equalTo("to_uid", session.getFrom_uid()).endGroup()
                    .sort("timestamp", Sort.DESCENDING).findFirst();

            sessionMore.setMessage(msg);
        }
        realm.copyToRealmOrUpdate(sessionMore);

    }
}
