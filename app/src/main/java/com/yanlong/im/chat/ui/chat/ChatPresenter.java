package com.yanlong.im.chat.ui.chat;

import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.ui.cell.ICellEventListener;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.base.BasePresenter;
import net.cb.cb.library.base.DBOptionObserver;
import net.cb.cb.library.bean.EventRefreshChat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.Observable;

/**
 * @anthor Liszt
 * @data 2019/9/19
 * Description
 */
public class ChatPresenter extends BasePresenter<ChatModel, ChatView> implements SocketEvent {
    public void loadAndSetData() {
        Observable<List<MsgAllBean>> observable = model.loadMessages();
        observable.subscribe(new DBOptionObserver<List<MsgAllBean>>() {
            @Override
            public void onOptionSuccess(List<MsgAllBean> list) {
                getView().setAndRefreshData(list);
            }
        });
    }

    public void checkLockMessage() {
        model.checkLockMessage();
    }

    public void registerIMListener() {
        SocketUtil.getSocketUtil().addEvent(this);
    }

    public void unregisterIMListener() {
        SocketUtil.getSocketUtil().removeEvent(this);
    }

    @Override
    protected void onViewDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onViewStart() {
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void taskRefreshMessageEvent(EventRefreshChat event) {
        loadAndSetData();
    }

    @Override
    public void onHeartbeat() {

    }

    @Override
    public void onACK(MsgBean.AckMessage bean) {

    }

    @Override
    public void onMsg(MsgBean.UniversalMessage bean) {

    }

    @Override
    public void onSendMsgFailure(MsgBean.UniversalMessage.Builder bean) {

    }

    @Override
    public void onLine(boolean state) {

    }
}
