package com.yanlong.im;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.repository.MainRepository;
import com.yanlong.im.user.bean.UserInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    //会话列表
    public RealmResults<Session> sessions = null;

    //通讯录好友
    public RealmResults<UserInfo> friends = null;

    //当前删除操作位置,为数据源中的位置
    public MutableLiveData<String> currentDeleteSid = new MutableLiveData();
    //保存sessionDetial 位置sid/position
    public Map<String, Integer> sessionMoresPositions = new HashMap<>();

    //判断网络状态 true在线 false离线
    public MutableLiveData<Boolean> onlineState = new MutableLiveData<>();
    //是否要主动关闭展开的删除按钮
    public MutableLiveData<Boolean> isNeedCloseSwipe = new MutableLiveData<>();
    public Set<String> allSids = new HashSet<>();
    public MutableLiveData<Boolean> isAllSidsChange = new MutableLiveData<>();

    //是否进主页显示加载动画
    public MutableLiveData<Boolean> isShowLoadAnim = new MutableLiveData<>();

    public MainViewModel() {
        onlineState.setValue(true);
        isNeedCloseSwipe.setValue(false);
        repository = new MainRepository();
        isShowLoadAnim.setValue(true);
    }

    public void initSession(List<String> sids) {
        repository.checkRealmStatus();
        //指向内存堆中同一个对象,session数据变化时，Application中会自动更新session详情
        if (MyAppLication.INSTANCE().iSSessionsLoad()) {
            sessions = MyAppLication.INSTANCE().getSessions();
            if (sids == null) {
                if (sessions.size() > 0) {
                    for (Session session : sessions) {
                        allSids.add(session.getSid());
                    }
                    isAllSidsChange.setValue(true);
                }else{
                    isShowLoadAnim.setValue(false);
                }
            } else {
                if (sids.size() > 0) {
                    allSids.addAll(sids);
                    isAllSidsChange.setValue(true);
                }else{
                    isShowLoadAnim.setValue(false);
                }
            }
        }
    }

    public void updateSessionMore() {
        try {
            if (sessionMores != null) sessionMores.removeAllChangeListeners();
            sessionMores = repository.getSessionMore(allSids.toArray(new String[allSids.size()]));
        }catch (Exception e){}
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

    public String getSessionJson() {
        return sessions == null ? "" : repository.getSessionJson(sessions);
    }

    /***
     * 获取红点的值
     * @param type
     * @return
     */
    public int getRemindCount(String type) {
        return repository.getRemindCount(type);
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

    public void onDestory(LifecycleOwner owner) {
        currentDeleteSid.removeObservers(owner);
        onlineState.removeObservers(owner);
        isNeedCloseSwipe.removeObservers(owner);
        isAllSidsChange.removeObservers(owner);
        if (sessionMores != null)
            sessionMores.removeAllChangeListeners();
        sessionMores = null;
        repository.onDestory();
    }
}
