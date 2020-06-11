package com.yanlong.im.chat.task;

import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketUtil;

import java.util.List;

import io.realm.Realm;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/11 0011
 * @description
 */
public class OnlineMessage extends DispatchMessage {
    @Override
    public void clear() {
    }

    /**
     * 处理在线消息
     *
     * @param bean
     */
    @Override
    public void dispatch(MsgBean.UniversalMessage bean, Realm realm) {
        boolean result = true;
        try {
            List<MsgBean.UniversalMessage.WrapMessage> msgList = bean.getWrapMsgList();
            if (msgList != null && msgList.size() > 0) {
                for (int i = 0; i < msgList.size(); i++) {
                    MsgBean.UniversalMessage.WrapMessage msg = msgList.get(i);
                    MsgBean.UniversalMessage.WrapMessage wrapMessage = msg;
                    //开始处理消息
                    boolean toDOResult = toDoMsg(realm, wrapMessage, bean.getRequestId(), bean.getMsgFrom() == 1, msgList.size(),
                            i == msgList.size() - 1);
                    //有一个失败，表示接收全部失败
                    if (!toDOResult) result = false;
                }
            }
        } catch (Exception e) {
            DaoUtil.reportException(e);
        }
        if (result)
            SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(bean.getRequestId(), null, bean.getMsgFrom(), false, true), null, bean.getRequestId());
    }
}
