package com.yanlong.im;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.repository.MainRepository;

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
    public RealmResults<SessionDetail> sessionMores = null;
    public RealmResults<Session> sessions = null;

    //当前删除操作位置,为数据源中的位置
    public MutableLiveData<Integer> currentDeletePosition = new MutableLiveData();
    //保存session 位置sid/position
    public Map<String, Integer> sessionMoresPositions = new HashMap<>();
    //判断网络状态 true在线 false离线
    public MutableLiveData<Boolean> onlineState = new MutableLiveData<>();
    //是否要主动关闭展开的删除按钮
    public MutableLiveData<Boolean> isNeedCloseSwipe = new MutableLiveData<>();
    //session数据是否已经加载
    public MutableLiveData<Boolean> isSessionDetailsLoad = new MutableLiveData<>();
    public Set<String> allSids = new HashSet<>();
    public MutableLiveData<Boolean> isAllSidsChange = new MutableLiveData<>();

    public MainViewModel() {
        repository = new MainRepository();
        isSessionDetailsLoad.setValue(false);
    }

    public  void initSession(List<String> sids) {
        repository.checkRealmStatus();
        //指向内存堆中同一个对象,session数据变化时，Application中会自动更新session详情
        if (MyAppLication.INSTANCE().iSSessionsLoad()) {
            sessions = MyAppLication.INSTANCE().getSessions();
            if(sids==null){
                if (sessions.size()>0) {
                    for (Session session : sessions) {
                        allSids.add(session.getSid()) ;
                    }
                    isAllSidsChange.setValue(true);
                }else{
                    isSessionDetailsLoad.setValue(true);
                }
            }else{
                if(sids.size()>0){
                    allSids.addAll(sids);
                    isAllSidsChange.setValue(true);
                }else{
                    isSessionDetailsLoad.setValue(true);
                }
            }
        }
    }
    public void updateSessionMore(){
        if(sessionMores!=null)sessionMores.removeAllChangeListeners();
        sessionMores = repository.getSessionMore(allSids.toArray(new String[allSids.size()]));
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

    public void updateItemSessionDetail() {
        //更新当前sessionDetail对象的所有数据
        repository.updateSessionDetail(allSids.toArray(new String[allSids.size()]));
    }

    /**
     * 删除指定项数据
     *
     * @param position
     */
    public void deleteItem(int position) {
        try {
            long uid = MyAppLication.INSTANCE().getSessions().get(position).getFrom_uid();
            String gid = MyAppLication.INSTANCE().getSessions().get(position).getGid();
            //开始删除事务
            repository.beginTransaction();
            String sid = MyAppLication.INSTANCE().getSessions().get(position).getSid();
            MyAppLication.INSTANCE().getSessions().get(position).deleteFromRealm();
            if (sessionMoresPositions.containsKey(sid)) {
                int index = sessionMoresPositions.get(sid);
                if (index >= 0 && index < sessionMores.size()) {
                    //删除session详情
                    sessionMores.get(index).deleteFromRealm();
                }
                //删除位置信息
                sessionMoresPositions.remove(sid);
            }
            repository.commitTransaction();
            repository.deleteAllMsg(uid, gid);
        } catch (Exception e) {
        }
    }

    public String getSessionJson() {
        return sessions == null ? "" : repository.getSessionJson(sessions);
    }

    public void onDestory(LifecycleOwner owner) {
        if (sessionMores != null)
            sessionMores.removeAllChangeListeners();
        sessionMores = null;
        repository.onDestory();
        currentDeletePosition.removeObservers(owner);
        onlineState.removeObservers(owner);
        isNeedCloseSwipe.removeObservers(owner);
        isSessionDetailsLoad.removeObservers(owner);
        isAllSidsChange.removeObservers(owner);
    }
}
