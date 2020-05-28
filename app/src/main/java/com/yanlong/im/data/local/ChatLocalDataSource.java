package com.yanlong.im.data.local;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import java.util.List;

import io.realm.Realm;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/23 0023
 * @description
 */
public class ChatLocalDataSource {
    private Realm realm = null;

    public ChatLocalDataSource() {
        realm = DaoUtil.open();
    }

    /**
     * 获取群信息
     * @param gid
     * @return
     */
    public Group getGroup(String gid){
        return realm.where(Group.class).equalTo("gid",gid).findFirst();
    }

    /**
     * 获取好友信息
     * @param uid
     * @return
     */
    public UserInfo getFriend(Long uid){
        return realm.where(UserInfo.class).equalTo("uid",uid).findFirst();
    }

    /*
     * 检测该群是否还有效，即自己是否还在该群中,有效为true，无效为false
     * */
    public boolean isGroupValid(Group group) {
        if (group != null) {
            if (group.getStat() != ChatEnum.EGroupStatus.NORMAL) {
                return false;
            } else {
                List<MemberUser> users = group.getUsers();
                if (users != null) {
                    MemberUser member = MessageManager.getInstance().userToMember(UserAction.getMyInfo(), group.getGid());
                    if (member != null && !users.contains(member)) {
                        return false;
                    }
                }
            }
        }
        return true;
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
