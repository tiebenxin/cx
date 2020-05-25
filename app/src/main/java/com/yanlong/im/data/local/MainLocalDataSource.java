package com.yanlong.im.data.local;

import com.yanlong.im.MyAppLication;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.Remind;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.bean.OnlineBean;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/3/26 0026
 * @description MainActivity 本地数据源
 */
public class MainLocalDataSource {
    private Realm realm = null;

    public MainLocalDataSource() {
        realm = DaoUtil.open();
    }


    /**
     * 获取群信息
     *
     * @param gid
     * @return
     */
    public Group getGroup4Id(String gid) {
        return DaoUtil.findOne(Group.class, "gid", gid);
    }

    public RealmResults<SessionDetail> getSessionMore(String[] sids) {
        return realm.where(SessionDetail.class).in("sid",sids).findAllAsync();
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

    /***
     * 获取红点的值
     * @param type
     * @return
     */
    public int getRemindCount(String type) {
        Remind remind = realm.where(Remind.class).equalTo("remid_type", type).findFirst();
        int num = remind == null ? 0 : remind.getNumber();
        return num;
    }
    /***
     * 清除红点的值
     * @param type
     * @return
     */
    public void clearRemindCount(String type) {
        Remind remind = realm.where(Remind.class).equalTo("remid_type", type).findFirst();
        if (remind != null) {
            beginTransaction();
            remind.setNumber(0);
            commitTransaction();
        }
    }

    /**
     * 更新通讯录好友信息
     * @param userInfo
     */
    public void updateFriend(UserInfo userInfo){
        beginTransaction();
        realm.copyToRealmOrUpdate(userInfo);
        commitTransaction();
    }
    /***
     * 更新好友
     * @param list
     */
    public void updateUsersOnlineStatus(List<OnlineBean> list) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try{
                    if (list != null && list.size() > 0) {
                        int len = list.size();
                        for (int i = 0; i < len; i++) {
                            OnlineBean bean = list.get(i);
                            if (bean == null) {
                                continue;
                            }
                            UserInfo user = realm.where(UserInfo.class).equalTo("uid", bean.getUid()).findFirst();
                            if (user == null) {//拉黑用户数据库没有？
                                continue;
                            }
                            UserInfo userInfo = realm.copyFromRealm(user);
                            if (userInfo == null) {
                                continue;
                            }
                            if (bean.getLastonline() > userInfo.getLastonline()) {//更新数据时间大于本地数据时间，才更新
                                userInfo.setLastonline(bean.getLastonline());
                            }
                            userInfo.setActiveType(bean.getActiveType());
                            realm.insertOrUpdate(userInfo);
                        }
                    }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


    }

    /**
     * 设置为陌生人
     * @param uid
     */
    public void setToStranger(long uid){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UserInfo userInfo = realm.where(UserInfo.class).equalTo("uid", uid).findFirst();
                if (userInfo != null) {
                    //设置为陌生人
                    userInfo.setuType(0);
                    //关闭阅后即焚
                    userInfo.setDestroy(0);
                    //// 更新置顶状态
                    userInfo.setIstop(0);
                }
                //// session会话更新置顶状态
                Session session = realm.where(Session.class).equalTo("from_uid", uid).findFirst();
                if (session != null) {
                    session.setIsTop(0);
                }
              }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                /********通知更新sessionDetail************************************/
                //因为msg对象 uid有两个，都得添加
                List<Long> uids = new ArrayList<>();
                uids.add(uid);
                //回主线程调用更新session详情
                if(MyAppLication.INSTANCE().repository!=null)MyAppLication.INSTANCE().repository.updateSessionDetail(null, uids);
                /********通知更新sessionDetail end************************************/
            }
        });
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
