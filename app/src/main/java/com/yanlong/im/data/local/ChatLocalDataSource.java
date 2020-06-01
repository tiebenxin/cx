package com.yanlong.im.data.local;

import android.os.Handler;
import android.text.TextUtils;

import com.luck.picture.lib.tools.DateUtils;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

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
     *
     * @param gid
     * @return
     */
    public Group getGroup(String gid) {
        return realm.where(Group.class).equalTo("gid", gid).findFirst();
    }

    /**
     * 获取好友信息
     *
     * @param uid
     * @return
     */
    public UserInfo getFriend(Long uid) {
        return realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
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

    /** 查询条件
     *  获取群聊、单聊接收对方发的消息，且是未加入到阅后即焚队列的消息对象
     * @param realm
     * @param toGid
     * @param toUid
     * @return
     */
    private RealmQuery<MsgAllBean> getToAddBurnForDBMsgsRealmQuery(Realm realm, String toGid, Long toUid){
        if (!TextUtils.isEmpty(toGid)) {//群聊-好友发送的，未加入到阅后即焚队列的消息
            return realm.where(MsgAllBean.class)
                    .equalTo("gid", toGid)
                    .greaterThan("survival_time", 0)
                    .lessThanOrEqualTo("endTime", 0);
        } else {//单聊-好友发送的消息，未加入到阅后即焚队列的消息
            return realm.where(MsgAllBean.class)
                    .beginGroup()
                    .isEmpty("gid").or().isNull("gid")
                    .endGroup()
                    .and()
                    .beginGroup()
                    .equalTo("from_uid", toUid)
                    .greaterThan("survival_time", 0)
                    .lessThanOrEqualTo("endTime", 0)
                    .endGroup();
        }
    }

    /**
     * 异步获取好友发送的待焚消息
     */
    public RealmResults<MsgAllBean> getToAddBurnForDBMsgsAsync(String toGid, Long toUid) {
      return getToAddBurnForDBMsgsRealmQuery(realm,toGid,toUid).findAllAsync();
    }

    private boolean isFinishedToBurn = true;
    private Handler handler = new Handler();

    /**
     * 阅后即焚消息批量添加到数据库，异步事务
     * @param toGid
     * @param toUid
     */
    public void dealToBurnMsgs(String toGid, Long toUid){
        if(!isFinishedToBurn){//等待事务完成，1秒后重试
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dealToBurnMsgs(toGid, toUid);
                }
            },1000);
            return ;
        }
        isFinishedToBurn = false;
        DaoUtil.executeTransactionAsync(realm, new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<MsgAllBean> realmResults = getToAddBurnForDBMsgsRealmQuery(realm,toGid,toUid).findAll();
                //对方发的消息，当前时间为起点
                long now = DateUtils.getSystemTime();
                if (realmResults != null&&realmResults.size()>0) {
                    for (MsgAllBean msg : realmResults) {
                        if (msg.getEndTime() == 0) {
                            msg.setStartTime(now);
                            msg.setEndTime(now + (msg.getSurvival_time() * 1000));
                        }
                    }
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                isFinishedToBurn = true;
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                isFinishedToBurn = true;
                dealToBurnMsgs(toGid,toUid);
            }
        });
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
