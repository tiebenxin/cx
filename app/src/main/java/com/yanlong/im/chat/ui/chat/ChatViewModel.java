package com.yanlong.im.chat.ui.chat;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.repository.ChatRepository;
import com.yanlong.im.user.bean.UserInfo;

import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;

public class ChatViewModel extends ViewModel {
    private ChatRepository repository;
    //是否触发了输入框
    public MutableLiveData<Boolean> isInputText = new MutableLiveData<>();
    //是否打开了表情面板
    public MutableLiveData<Boolean> isOpenEmoj = new MutableLiveData<>();
    //是否打开了功能面板
    public MutableLiveData<Boolean> isOpenFuction = new MutableLiveData<>();
    //是否打开了点击说话
    public MutableLiveData<Boolean> isOpenSpeak = new MutableLiveData<>();
    //列表数据数量
    public MutableLiveData<Integer> adapterCount = new MutableLiveData<>();
    //是否正在回复
    public MutableLiveData<Boolean> isReplying = new MutableLiveData<>();

    public Group groupInfo = null;
    public UserInfo userInfo = null;
    public String toGid ;
    public long toUId ;
    public ChatViewModel(){
        repository = new ChatRepository();
    }
    public void init(String toGid,Long toUId){
        //主线程使用setValue,子线程使用postValue赋值
        isInputText.setValue(false);
        isOpenEmoj.setValue(false);
        isOpenFuction.setValue(false);
        isOpenSpeak.setValue(false);
        adapterCount.setValue(0);
        isReplying.setValue(false);
        this.toGid = toGid;
        this.toUId = toUId == null?0:toUId;
    }

    /**
     * 加载群信息、好友信息
     */
    public void loadData( RealmObjectChangeListener<RealmModel> groupInfoChangeListener,
                          RealmObjectChangeListener<RealmModel> userInfoChangeListener){
        if(TextUtils.isEmpty(toGid)){
            if(userInfo != null)userInfo.removeAllChangeListeners();
            userInfo = repository.getFriend(this.toUId);
            if(userInfo != null )userInfo.addChangeListener(userInfoChangeListener);
        }else{
            if(groupInfo != null)groupInfo.removeAllChangeListeners();
            groupInfo = repository.getGroup(this.toGid);
            if(groupInfo != null )groupInfo.addChangeListener(groupInfoChangeListener);
        }
    }

    /**
     * 重置其他状态值
      1.data＝null，所有状态值重置为false
     * 2.data不为null,data本身不重置
     *
     * @param data
     */
    public void recoveryOtherValue(MutableLiveData<Boolean> data) {
        recoveryValue(data, isInputText);
        recoveryValue(data, isOpenEmoj);
        recoveryValue(data, isOpenFuction);
        recoveryValue(data, isOpenSpeak);
        recoveryValue(data, isReplying);
    }

    private void recoveryValue(MutableLiveData<Boolean> data, MutableLiveData<Boolean> orignalData) {
        if ((data == null || data != orignalData) && orignalData.getValue()) {
            orignalData.setValue(false);
        }
    }

    /**
     * 是否有事件触发
     *
     * @return
     */
    public boolean isOpenValue() {
        return isInputText.getValue() || isOpenEmoj.getValue() ||
                isOpenFuction.getValue() || isOpenSpeak.getValue() || isReplying.getValue();

    }
    /**
     * onResume检查realm状态,避免系统奔溃后，主页重新启动realm对象已被关闭，需重新连接
     */
    public boolean checkRealmStatus(){
        return repository.checkRealmStatus();
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        if(userInfo!=null)userInfo.removeAllChangeListeners();
        if(groupInfo!=null)groupInfo.removeAllChangeListeners();
        if(repository!=null)repository.onDestory();

    }
}
