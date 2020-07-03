package com.yanlong.im.chat.task;

import android.text.TextUtils;

import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.action.UserAction;
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
    private static OnlineMessage INSTANCE;

    public OnlineMessage() {
        super(false);
    }

    public static OnlineMessage getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OnlineMessage();
        }
        return INSTANCE;
    }

    @Override
    public void clear() {
        repository.clear();
    }

    /**
     * 过滤消息 -不接收或不接收重复消息
     *
     * @param wrapMessage
     * @return
     */
    @Override
    public synchronized boolean filter(MsgBean.UniversalMessage.WrapMessage wrapMessage) {
        boolean result = false;
        if (wrapMessage.getMsgType() == MsgBean.MessageType.UNRECOGNIZED) {
            result = true;
        } else if (!TextUtils.isEmpty(wrapMessage.getMsgId()) && oldMsgId.contains(wrapMessage.getMsgId())) {
            //有已保存成功的消息，则不再处理
            LogUtil.getLog().e(TAG, ">>>>>接收到消息--重复消息: " + wrapMessage.getMsgId());
            result = true;
        } else {
            if (!TextUtils.isEmpty(wrapMessage.getMsgId())) {
                if (oldMsgId.size() >= 500) {
                    oldMsgId.remove(0);
                }
                oldMsgId.add(wrapMessage.getMsgId());
            }
        }
        return result;

    }

    /**
     * 处理在线消息
     *
     * @param bean
     */
    @Override
    public synchronized void dispatch(MsgBean.UniversalMessage bean, Realm realm) {
        boolean result = true;
        try {
            List<MsgBean.UniversalMessage.WrapMessage> msgList = bean.getWrapMsgList();
            if (msgList != null && msgList.size() > 0) {
                int size = msgList.size();
                for (int i = 0; i < size; i++) {
                    MsgBean.UniversalMessage.WrapMessage msg = msgList.get(i);
                    MsgBean.UniversalMessage.WrapMessage wrapMessage = msg;
                    //开始处理消息
                    boolean toDOResult = handlerMessage(realm, wrapMessage, bean.getRequestId(), bean.getMsgFrom() == 1, msgList.size(),
                            i == msgList.size() - 1);
                    if (toDOResult) {
                        if (size == 1 && wrapMessage.getMsgType() != MsgBean.MessageType.ACTIVE_STAT_CHANGE) {
                            LogUtil.writeLog("--发送回执1--requestId=" + bean.getRequestId() + " msgType:" + bean.getWrapMsg(0).getMsgType() + "--msgTypeValue=" + bean.getWrapMsg(0).getMsgTypeValue() + " msgID:" + bean.getWrapMsg(0).getMsgId());
                            SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(bean.getRequestId(), null, bean.getMsgFrom(), false, true), null, bean.getRequestId());
                        }
                        //刷新可能正在预览的图片界面
                        doRefreshPreviewImage(wrapMessage);
                    } else {
                        //有一个失败，表示接收全部失败
                        result = false;
                    }
                }
            }
        } catch (Exception e) {
            DaoUtil.reportException(e);
        }
        if (result && bean.getWrapMsgCount() > 1) {
            LogUtil.writeLog("--发送回执3在线--requestId=" + bean.getRequestId() + "--count=" + bean.getWrapMsgCount());
            SocketUtil.getSocketUtil().sendData(SocketData.msg4ACK(bean.getRequestId(), null, bean.getMsgFrom(), false, true), null, bean.getRequestId());
        }
    }

    private void doRefreshPreviewImage(MsgBean.UniversalMessage.WrapMessage wrapMessage) {
        if (wrapMessage.getMsgType() == MsgBean.MessageType.IMAGE) {
            long toUid;
            if (!TextUtils.isEmpty(wrapMessage.getGid())) {
                toUid = 0;
            } else {
                if (UserAction.getMyId() != null && UserAction.getMyId().longValue() == wrapMessage.getFromUid()) {
                    toUid = wrapMessage.getToUid();
                } else {
                    toUid = wrapMessage.getFromUid();
                }
            }
            if (MessageManager.getInstance().isImageFromCurrent(wrapMessage.getGid(), toUid)) {
                MessageManager.getInstance().notifyReceiveImage(wrapMessage.getGid(), toUid);
            }
        }
    }
}
