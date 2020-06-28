package com.yanlong.im;

import android.arch.lifecycle.ViewModel;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.repository.MainRepository;
import com.yanlong.im.user.bean.UserInfo;

import io.realm.RealmResults;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/4/26 0026
 * @description
 */
public class FriendViewModel extends ViewModel {
    private MainRepository repository;
    //通讯录好友
    public FriendViewModel() {
        repository = new MainRepository();
    }

    public RealmResults<UserInfo> getFriends(){
        if (MyAppLication.INSTANCE().repository != null) {
            return MyAppLication.INSTANCE().repository.friends;
        }else{
            return null;
        }
    }
    public int getFriendSize(){
        return getFriends() == null ? 0 : getFriends().size();
    }

    /***
     * 清除红点的值
     * @param type
     * @return
     */
    public void clearRemindCount(String type) {
        repository.clearRemindCount(type);
    }

    /****远程请求*********************************************************************************/
    /***
     * 获取单个用户信息并且缓存到数据库
     * @param usrid
     */
    public void requestUserInfoAndSave(Long usrid, @ChatEnum.EUserType int type) {
        repository.requestUserInfoAndSave(usrid, type);
    }

    /**
     * 设置为陌生人
     *
     * @param uid
     */
    public void setToStranger(long uid) {
        repository.setToStranger(uid);
    }

    /**
     * 获取通讯录好友在线状态
     */
    public void requestUsersOnlineStatus() {
        repository.requestUsersOnlineStatus();
    }
    /***
     * 获取红点的值
     * @param type
     * @return
     */
    public int getRemindCount(String type) {
        return repository.getRemindCount(type);
    }

    public void onDestroy() {
        repository.onDestroy();
    }


}
