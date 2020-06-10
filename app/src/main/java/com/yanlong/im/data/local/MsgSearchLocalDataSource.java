package com.yanlong.im.data.local;

import android.text.TextUtils;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/22 0022
 * @description
 */
public class MsgSearchLocalDataSource {
    private Realm realm;

    public MsgSearchLocalDataSource() {
        realm = DaoUtil.open();
    }

    private String getKey(String searchKey) {
        return String.format("*%s*", searchKey);

    }

    /**
     * 搜索好友昵称、备注名
     *
     * @param key
     * @return
     */
    public RealmResults<UserInfo> searchFriends(String key) {
        String searchKey = getKey(key);
        return realm.where(UserInfo.class).like("name", searchKey).or().like("mkName", searchKey)
                .findAll();
    }

    /**
     * 搜索群名 和群成员名
     *
     * @param key
     * @return
     */
    public RealmResults<Group> searchGroups(String key) {
        String searchKey = getKey(key);
        return realm.where(Group.class).like("name", searchKey).or()
                .like("members.membername", searchKey).or().like("members.name", searchKey)
                .findAll();
    }

    /**
     * 搜索所有session
     *
     * @return
     */
    public RealmResults<Session> searchSessions() {
        return realm.where(Session.class).findAll();
    }

    /**
     * 获取满足条件的sessionDetail
     *
     * @return
     */
    public RealmResults<SessionDetail> getSessionDetails(String[] sids) {
        return realm.where(SessionDetail.class).in("sid", sids).findAll();
    }

    private RealmQuery<MsgAllBean> searchMessagesQuery(String key, String gid, long uid) {
        String searchKey = getKey(key);
        if (TextUtils.isEmpty(gid)) {
            return realm.where(MsgAllBean.class)
                    .beginGroup().isEmpty("gid").or().isNull("gid").endGroup()
                    .and()
                    .beginGroup().equalTo("from_uid", uid).or().equalTo("to_uid", uid).endGroup()
                    .and()
                    .beginGroup()
                    .notEqualTo("msg_type", ChatEnum.EMessageType.LOCK)
                    .like("chat.msg", searchKey, Case.INSENSITIVE).or()//文本聊天
                    .like("atMessage.msg", searchKey, Case.INSENSITIVE).or()//@消息
                    .like("assistantMessage.msg", searchKey, Case.INSENSITIVE).or()//小助手消息
                    .like("locationMessage.address", searchKey, Case.INSENSITIVE).or()//位置消息
                    .like("locationMessage.addressDescribe", searchKey, Case.INSENSITIVE).or()//位置消息
                    .like("transferNoticeMessage.content", searchKey, Case.INSENSITIVE).or()//转账消息
                    .like("sendFileMessage.file_name", searchKey, Case.INSENSITIVE).or()//文件消息
                    .like("webMessage.title", searchKey, Case.INSENSITIVE).or()//链接消息
                    .like("replyMessage.chatMessage.msg", searchKey, Case.INSENSITIVE).or()//回复消息
                    .like("replyMessage.atMessage.msg", searchKey, Case.INSENSITIVE)//回复@消息
                    .endGroup();
        } else {
            return realm.where(MsgAllBean.class)
                    .equalTo("gid", gid)
                    .notEqualTo("msg_type", ChatEnum.EMessageType.LOCK)
                    .like("chat.msg", searchKey, Case.INSENSITIVE).or()//文本聊天
                    .like("atMessage.msg", searchKey, Case.INSENSITIVE).or()//@消息
                    .like("assistantMessage.msg", searchKey, Case.INSENSITIVE).or()//小助手消息
                    .like("locationMessage.address", searchKey, Case.INSENSITIVE).or()//位置消息
                    .like("locationMessage.addressDescribe", searchKey, Case.INSENSITIVE).or()//位置消息
                    .like("transferNoticeMessage.content", searchKey, Case.INSENSITIVE).or()//转账消息
                    .like("sendFileMessage.file_name", searchKey, Case.INSENSITIVE).or()//文件消息
                    .like("webMessage.title", searchKey, Case.INSENSITIVE).or()//链接消息
                    .like("replyMessage.chatMessage.msg", searchKey, Case.INSENSITIVE).or()//回复消息
                    .like("replyMessage.atMessage.msg", searchKey, Case.INSENSITIVE);//回复@消息
        }
    }

    /**
     * 搜索聊天记录 数量
     *
     * @param key
     * @return
     */
    public long searchMessagesCount(String key, String gid, long uid) {
        return searchMessagesQuery(key, gid, uid).count();
    }
    /**
     * 搜索聊天记录匹配数量为1时的消息
     *
     * @param key
     * @return
     */
    public MsgAllBean searchMessages(String key, String gid, long uid) {
        return searchMessagesQuery(key, gid, uid).findFirst();
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


    /**
     * onResume检查realm状态,避免系统奔溃后，主页重新启动realm对象已被关闭，需重新连接
     */
    public boolean checkRealmStatus() {
        boolean result = true;
        if (realm == null || realm.isClosed()) {
            result = false;
            realm = DaoUtil.open();
        }
        return result;
    }

    public void onDestory() {
        if (realm != null) {
            if (realm != null) {
                if (realm.isInTransaction()) {
                    realm.cancelTransaction();
                }
                realm.close();
            }
        }
    }

}
