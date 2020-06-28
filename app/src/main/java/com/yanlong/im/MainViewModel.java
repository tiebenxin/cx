package com.yanlong.im;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.repository.MainRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/3/26 0026
 * @description MainActivity viewModel层
 */
public class MainViewModel extends ViewModel {
    private MainRepository repository;
    //会话详情
    public RealmResults<SessionDetail> sessionMores = null;

    //当前删除操作位置,为数据源中的位置
    public MutableLiveData<String> currentDeleteSid = new MutableLiveData();
    //保存sessionDetial 位置sid/position
    public Map<String, Integer> sessionMoresPositions = new HashMap<>();

    //判断网络状态 true在线 false离线
    public MutableLiveData<Boolean> onlineState = new MutableLiveData<>();
    //是否要主动关闭展开的删除按钮
    public MutableLiveData<Boolean> isNeedCloseSwipe = new MutableLiveData<>();

    //是否进主页显示加载动画
    public MutableLiveData<Boolean> isShowLoadAnim = new MutableLiveData<>();
    private OrderedRealmCollectionChangeListener sessionMoresListener = null;

    public MainViewModel(OrderedRealmCollectionChangeListener sessionMoresListener) {
        onlineState.setValue(true);
        isNeedCloseSwipe.setValue(false);
        repository = new MainRepository();
        isShowLoadAnim.setValue(true);
        this.sessionMoresListener = sessionMoresListener;
    }

    public int getSessionSize() {
        int size = 0;
        if (MyAppLication.INSTANCE().repository != null) {
            size = MyAppLication.INSTANCE().repository.sessionSidPositons.size();
        }
        return size;
    }

    public RealmResults<Session> getSession() {
        if (MyAppLication.INSTANCE().repository != null) {
            return MyAppLication.INSTANCE().repository.sessions;
        } else {
            return null;
        }
    }


    public void updateSessionMore() {
        try {
            repository.checkRealmStatus();
            if (MyAppLication.INSTANCE().repository != null) {
                //做一层保护，可能会有事务冲突或其他奔溃,引起的无法进行异步查询
                Set<String> allSids = MyAppLication.INSTANCE().repository.sessionSidPositons.keySet();
                RealmResults<SessionDetail> temp = repository.getSessionMore(allSids.toArray(new String[allSids.size()]));
                if (temp != null) {
                    if (sessionMores != null) sessionMores.removeAllChangeListeners();
                    sessionMores = temp;
                    //监听列表数据变化
                    if (sessionMoresListener != null)
                        sessionMores.addChangeListener(sessionMoresListener);
                }
            }
        } catch (Exception e) {
        }

    }


    /**
     * 获取群信息
     *
     * @param gid
     * @return
     */
    public Group getGroup4Id(String gid) {
        return repository.getGroup4Id(gid);
    }


//    /***
//     * 获取红点的值
//     * @param type
//     * @return
//     */
//    public int getRemindCount(String type) {
//        return repository.getRemindCount(type);
//    }
//
//    /***
//     * 清除红点的值
//     * @param type
//     * @return
//     */
//    public void clearRemindCount(String type) {
//        repository.clearRemindCount(type);
//    }
//
//    /****远程请求*********************************************************************************/
//    /***
//     * 获取单个用户信息并且缓存到数据库
//     * @param usrid
//     */
//    public void requestUserInfoAndSave(Long usrid, @ChatEnum.EUserType int type) {
//        repository.requestUserInfoAndSave(usrid, type);
//    }
//
//    /**
//     * 设置为陌生人
//     *
//     * @param uid
//     */
//    public void setToStranger(long uid) {
//        repository.setToStranger(uid);
//    }
//
//    /**
//     * 获取通讯录好友在线状态
//     */
//    public void requestUsersOnlineStatus() {
//        repository.requestUsersOnlineStatus();
//    }

    public void onDestroy(LifecycleOwner owner) {
        currentDeleteSid.removeObservers(owner);
        onlineState.removeObservers(owner);
        isNeedCloseSwipe.removeObservers(owner);
        if (sessionMores != null)
            sessionMores.removeAllChangeListeners();
        sessionMoresPositions.clear();
        sessionMoresPositions = null;
        sessionMores = null;
        repository.onDestory();
    }
}
