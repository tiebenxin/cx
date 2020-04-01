package com.yanlong.im;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.repository.MainRepository;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/3/26 0026
 * @description MainActivity viewModel层
 */
public class MainViewModel extends ViewModel {
    private MainRepository repository;
    public RealmResults<Session> sessions;
    public RealmResults<SessionDetail> sessionMores;
    //当前删除操作位置,为数据源中的位置
    public MutableLiveData<Integer> currentDeletePosition = new MutableLiveData();
    //保存session 位置
    public Map<String, Integer> sessionMoresPositions = new HashMap<>();
    //保存session数量
    public int sessionOriginalSize = 0;
    //判断网络状态 true在线 false离线
    public MutableLiveData<Boolean> onlineState = new MutableLiveData<>();

    public MainViewModel() {
        repository = new MainRepository();
        sessions = repository.getSesisons();
        sessionOriginalSize = sessions.size();
        //session数据变化时，更新session详情
        sessionMores = repository.getSessionMore();
        sessions.addChangeListener(new RealmChangeListener<RealmResults<Session>>() {
            @Override
            public void onChange(RealmResults<Session> sessions) {
                Log.e("raleigh_test", "sessions" + sessions.size());
//                if(sessionOriginalSize<sessions.size()){
                //session数据变化时，更新session详情：旧数据收到/发送消息，删除，新数据收到/发送消息
                repository.updateSessionDetail();
//                }
            }
        });
        sessionMores.addChangeListener(new RealmChangeListener<RealmResults<SessionDetail>>() {
            @Override
            public void onChange(RealmResults<SessionDetail> sessionMores) {
                Log.e("raleigh_test", "sessionMores=" + sessionMores.size());
                sessionMoresPositions.clear();
                for (int i = 0; i < sessionMores.size(); i++) {
                    sessionMoresPositions.put(sessionMores.get(i).getSid(), i);
                }
            }
        });
    }
    /**
     * 获取群信息
     * @param gid
     * @return
     */
    public Group getGroup4Id(String gid){
        return repository.getGroup4Id(gid);
    }
    public void updateItemSessionDetail() {
        repository.updateSessionDetail();
    }

    /**
     * 删除指定项数据
     *
     * @param position
     */
    public void deleteItem(int position) {
        try {
            //开始删除事务
            repository.beginTransaction();
            String sid = sessions.get(position).getSid();
            sessions.get(position).deleteFromRealm();
            if (sessionMoresPositions.containsKey(sid)) {
                //删除session详情
                sessionMores.get(sessionMoresPositions.get(sid)).deleteFromRealm();
                //删除位置信息
                sessionMoresPositions.remove(sid);
            }
            repository.commitTransaction();
        } catch (Exception e) {
        }

    }

    public void onDestory() {
        sessions.removeAllChangeListeners();
        sessionMores.removeAllChangeListeners();
        repository.onDestory();
    }
}
