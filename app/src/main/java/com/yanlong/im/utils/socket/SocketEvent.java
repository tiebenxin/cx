package com.yanlong.im.utils.socket;

public interface SocketEvent {
    void onHeartbeat();
    void onMsg(MsgBean.UniversalMessage bean);
    void onSendMsgFailure(MsgBean.UniversalMessage.Builder bean);
}
