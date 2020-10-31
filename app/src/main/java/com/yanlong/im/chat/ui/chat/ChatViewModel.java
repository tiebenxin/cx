package com.yanlong.im.chat.ui.chat;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.repository.ChatRepository;
import com.yanlong.im.user.bean.UserInfo;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;

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
    public String toGid;
    public long toUId;

    //待添加到数据库阅后即焚消息
    private RealmResults<MsgAllBean> toAddBurnForDBMsgs = null;

    public ChatViewModel() {
        repository = new ChatRepository();
    }

    public void init(String toGid, Long toUId) {
        //主线程使用setValue,子线程使用postValue赋值
        isInputText.setValue(false);
        isOpenEmoj.setValue(false);
        isOpenFuction.setValue(false);
        isOpenSpeak.setValue(false);
        adapterCount.setValue(0);
        isReplying.setValue(false);
        this.toGid = toGid;
        this.toUId = toUId == null ? 0 : toUId;
        //观察需要添加到数据库的阅后即焚的消息
        observerToAddBurnMsgs();
    }

    /**
     * 观察
     * 1.群聊或单聊
     * 2.好友发送的消息
     * 3.未添加到阅后即焚的消息
     * 打开聊天界面说明 已读，有新消息则立即加入到阅后即焚队列
     */
    public void observerToAddBurnMsgs() {
        if (toAddBurnForDBMsgs != null) {
            toAddBurnForDBMsgs.removeAllChangeListeners();
            toAddBurnForDBMsgs = null;
        }
        toAddBurnForDBMsgs = repository.getToAddBurnForDBMsgs(toGid, toUId);
        toAddBurnForDBMsgs.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<MsgAllBean>>() {
            @Override
            public void onChange(RealmResults<MsgAllBean> msgAllBeans, OrderedCollectionChangeSet changeSet) {
                if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL || changeSet.getInsertions().length > 0) {
                    //初始化或新增时，进行数据库操作，添加到阅后即焚队列
                    repository.dealToBurnMsgs(toGid, toUId);
                }
            }
        });
    }

    /**
     * 加载群信息、好友信息
     */
    public void loadData(RealmObjectChangeListener<RealmModel> groupInfoChangeListener,
                         RealmObjectChangeListener<RealmModel> userInfoChangeListener) {
        if (TextUtils.isEmpty(toGid)) {
            if (userInfo != null) {
                userInfo.removeAllChangeListeners();
                userInfo = null;
            }
            userInfo = repository.getFriend(toUId);
            if (userInfo != null) userInfo.addChangeListener(userInfoChangeListener);
        } else {
            if (groupInfo != null) {
                groupInfo.removeAllChangeListeners();
                groupInfo = null;
            }
            groupInfo = repository.getGroup(toGid);
            if (groupInfo != null) groupInfo.addChangeListener(groupInfoChangeListener);
        }
    }

    /**
     * 重置其他状态值
     * 1.data＝null，所有状态值重置为false
     * 2.data不为null,data本身不重置
     *
     * @param data
     */
    public void recoveryOtherValue(MutableLiveData<Boolean> data) {
        recoveryValue(data, isInputText);
        recoveryValue(data, isOpenEmoj);
        recoveryValue(data, isOpenFuction);
        recoveryValue(data, isOpenSpeak);
    }

    /**
     * 重置其他状态值
     * 1.data＝null，所有状态值重置为false
     * 2.data不为null,data本身不重置
     *
     * @param data
     */
    public void recoveryPartValue(MutableLiveData<Boolean> data) {
        recoveryValue(data, isInputText);
        recoveryValue(data, isOpenEmoj);
        recoveryValue(data, isOpenFuction);
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
                isOpenFuction.getValue() || isOpenSpeak.getValue();

    }

    /**
     * onResume检查realm状态,避免系统奔溃后，主页重新启动realm对象已被关闭，需重新连接
     */
    public boolean checkRealmStatus() {
        return repository.checkRealmStatus();
    }

    public void onDestroy() {
        if (toAddBurnForDBMsgs != null) {
            toAddBurnForDBMsgs.removeAllChangeListeners();
        }

        if (userInfo != null) {
            userInfo.removeAllChangeListeners();
            userInfo = null;
        }
        if (toGid != null) {
            toGid = null;
        }
        if (groupInfo != null) {
            groupInfo.removeAllChangeListeners();
            groupInfo = null;
        }

        if (repository != null) {
            repository.onDestory();
        }
    }

    public void dealBurnMessage() {
        repository.dealToBurnMsgs(toGid, toUId);
    }

    //是否有阅后即焚消息
    public boolean hasBurnMsg() {
        if (toAddBurnForDBMsgs != null && toAddBurnForDBMsgs.size() > 0) {
            return true;
        }
        return false;
    }
}
