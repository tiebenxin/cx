package com.yanlong.im.chat.ui.chat;

import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;

import net.cb.cb.library.base.BasePresenter;
import net.cb.cb.library.base.DBOptionObserver;

import java.util.List;

import io.reactivex.Observable;

/**
 * @anthor Liszt
 * @data 2019/9/19
 * Description
 */
public class ChatPresenter extends BasePresenter<ChatModel, ChatView> {
    public void loadAndSetData() {
        Observable<List<MsgAllBean>> observable = model.loadMessages();
        observable.subscribe(new DBOptionObserver<List<MsgAllBean>>() {
            @Override
            public void onOptionSuccess(List<MsgAllBean> list) {
                getView().setAndRefreshData(list);
            }
        });
    }


    @Override
    protected void onViewDestroy() {

    }
}
