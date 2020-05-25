package com.yanlong.im.data.local;

import android.text.TextUtils;

import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import io.realm.Realm;
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

    /**
     * 搜索好友昵称、备注名
     *
     * @param searchKey
     * @return
     */
    public RealmResults<UserInfo> searchFriends(String searchKey) {
        return realm.where(UserInfo.class).like("name", searchKey).or().like("mkName", searchKey)
                .findAll();
    }

    /**
     * 搜索群名 和群成员名
     *
     * @param searchKey
     * @return
     */
    public RealmResults<Group> searchGroups(String searchKey) {
        return realm.where(Group.class).like("name", searchKey).or()
                .like("members.membername", searchKey).or().like("members.name", searchKey)
                .findAll();
    }
    /**
     * 搜索所有session
     *
     * @return
     */
    public RealmResults<SessionDetail> searchSessions() {
        return realm.where(SessionDetail.class).findAllAsync();
    }

    /**
     * 搜索聊天记录
     *
     * @param searchKey
     * @return
     */
    public long searchMessagesCount(String searchKey,String gid,long uid) {
        if(TextUtils.isEmpty(gid)){
            return realm.where(MsgAllBean.class)
                    .beginGroup().equalTo("from_uid",uid).or().equalTo("to_uid",uid).endGroup()
                    .like("chat.msg", searchKey).or()//文本聊天
                    .like("atMessage.msg", searchKey).or()//@消息
                    .like("msgNotice.note", searchKey).or()//通知消息
                    .like("assistantMessage.msg", searchKey).or()//小助手消息
                    .like("locationMessage.addressDescribe", searchKey).or()//位置消息
                    .like("transferNoticeMessage.content", searchKey).or()//转账消息
                    .like("sendFileMessage.file_name", searchKey).or()//文件消息
                    .like("webMessage.title", searchKey).or()//链接消息
                    .like("replyMessage.chatMessage.msg", searchKey).or()//回复消息
                    .like("replyMessage.atMessage.msg", searchKey)//回复@消息
                    .count();
        }else{
            return realm.where(MsgAllBean.class)
                    .equalTo("gid",gid)
                    .like("chat.msg", searchKey).or()//文本聊天
                    .like("atMessage.msg", searchKey).or()//@消息
                    .like("msgNotice.note", searchKey).or()//通知消息
                    .like("assistantMessage.msg", searchKey).or()//小助手消息
                    .like("locationMessage.addressDescribe", searchKey).or()//位置消息
                    .like("transferNoticeMessage.content", searchKey).or()//转账消息
                    .like("sendFileMessage.file_name", searchKey).or()//文件消息
                    .like("webMessage.title", searchKey).or()//链接消息
                    .like("replyMessage.chatMessage.msg", searchKey).or()//回复消息
                    .like("replyMessage.atMessage.msg", searchKey)//回复@消息
                    .count();
        }
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
