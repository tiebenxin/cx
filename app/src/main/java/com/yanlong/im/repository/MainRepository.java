package com.yanlong.im.repository;

import android.text.TextUtils;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.data.local.MainLocalDataSource;
import com.yanlong.im.data.remote.MainRemoteDataSource;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.bean.OnlineBean;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;

import java.util.List;

import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/3/26 0026
 * @description MainActivity 数据仓库
 */
public class MainRepository {
    private MainLocalDataSource localDataSource;
    private MainRemoteDataSource remoteDataSource;

    public MainRepository() {
        localDataSource = new MainLocalDataSource();
        remoteDataSource = new MainRemoteDataSource();
    }

    /**
     * 获取群信息
     *
     * @param gid
     * @return
     */
    public Group getGroup4Id(String gid) {
        return localDataSource.getGroup4Id(gid);
    }


    /**
     * 获取session 详情
     *
     * @return
     */
    public RealmResults<SessionDetail> getSessionMore(String[] sids) {
        return localDataSource.getSessionMore(sids);
    }


    /**
     * 数据库开始事务处理
     */
    public void beginTransaction() {
        localDataSource.beginTransaction();
    }

    /**
     * 数据库提交事务处理
     */
    public void commitTransaction() {
        localDataSource.commitTransaction();
    }

    public void onDestory() {
        localDataSource.onDestroy();
    }

    /**
     * onResume检查realm状态,避免系统奔溃后，主页重新启动realm对象已被关闭，需重新连接
     */
    public boolean checkRealmStatus() {
        return localDataSource.checkRealmStatus();
    }

    /***
     * 获取红点的值
     * @param type
     * @return
     */
    public int getRemindCount(String type) {
        return localDataSource.getRemindCount(type);
    }

    /***
     * 清除红点的值
     * @param type
     * @return
     */
    public void clearRemindCount(String type) {
        localDataSource.clearRemindCount(type);
    }

    /**
     * 设置为陌生人
     *
     * @param uid
     */
    public void setToStranger(long uid) {
        localDataSource.setToStranger(uid);
    }

    /***
     * 获取单个用户信息并且缓存到数据库
     * @param usrid
     */
    public void requestUserInfoAndSave(Long usrid, @ChatEnum.EUserType int type) {
        remoteDataSource.requestUserInfoAndSave(usrid, new CallBack<ReturnBean<UserInfo>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
                if (response.body() != null && response.body().isOk()) {
                    UserInfo userInfo = response.body().getData();
                    userInfo.toTag();
                    if (userInfo.getStat() != 0) {//优先设置为好友
                        userInfo.setuType(type);
                    } else {
                        userInfo.setuType(ChatEnum.EUserType.FRIEND);
                    }

                    if (TextUtils.isEmpty(userInfo.getTag())) {
                        userInfo.toTag();
                    }
                    localDataSource.updateFriend(userInfo);
                    MessageManager.getInstance().updateSessionTopAndDisturb("", usrid, userInfo.getIstop(), userInfo.getDisturb());
                    MessageManager.getInstance().updateCacheUser(userInfo);
                } else {
                    MessageManager.getInstance().removeLoadUids(usrid);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<UserInfo>> call, Throwable t) {
                super.onFailure(call, t);
                MessageManager.getInstance().removeLoadUids(usrid);
            }
        });
    }

    /**
     * 获取通讯录好友在线状态
     */
    public void requestUsersOnlineStatus() {
        remoteDataSource.requestUsersOnlineStatus(new CallBack<ReturnBean<List<OnlineBean>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<OnlineBean>>> call, Response<ReturnBean<List<OnlineBean>>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    List<OnlineBean> list = response.body().getData();
                    localDataSource.updateUsersOnlineStatus(list);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<OnlineBean>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    /**
     * 设置为陌生人
     *
     * @param uid
     */
    public void markSessionRead(String sid, int read) {
        localDataSource.markSessionRead(sid, read);
    }


}
