package com.yanlong.im.chat.task;

import android.text.TextUtils;

import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.utils.LogUtil;

import java.util.List;

import io.realm.Realm;

import static com.yanlong.im.utils.socket.SocketData.oldMsgId;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/11 0011
 * @description
 */
public class OnlineMessage extends DispatchMessage {
    public OnlineMessage() {
        super(false);
    }

    @Override
    public void clear() {
        repository.clear();
    }
    /**
     * 过滤消息 -不接收或不接收重复消息
     * @param wrapMessage
     * @return
     */
    @Override
    public boolean filter(MsgBean.UniversalMessage.WrapMessage wrapMessage) {
        if (wrapMessage.getMsgType() == MsgBean.MessageType.UNRECOGNIZED) {
            return true;
        } else if (!TextUtils.isEmpty(wrapMessage.getMsgId()) && oldMsgId.contains(wrapMessage.getMsgId())) {
            //有已保存成功的消息，则不再处理
            LogUtil.getLog().e(TAG, ">>>>>重复消息: " + wrapMessage.getMsgId());
            return true;
        } else {
            return false;
        }
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
                    boolean toDOResult = handlerMessage(realm, wrapMessage, bean.getRequestId(), bean.getMsgFrom() == 1, msgList.size(),
                            i == msgList.size() - 1);
                    if (toDOResult) {
                        //记录已保存成功的消息,用于剔除重复消息
                        if (result && !TextUtils.isEmpty(wrapMessage.getMsgId())) {
                            if (oldMsgId.size() >= 500) {
                                oldMsgId.remove(0);
                            }
                            oldMsgId.add(wrapMessage.getMsgId());
                        }
                    } else {
                        //有一个失败，表示接收全部失败
                        result = false;
                    }
                }
            }
        } catch (Exception e) {
            DaoUtil.reportException(e);
        }
        if (result)
            SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(bean.getRequestId(), null, bean.getMsgFrom(), false, true), null, bean.getRequestId());
    }


}
