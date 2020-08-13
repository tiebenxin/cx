package net.cb.cb.library.netty.handler;


import net.cb.cb.library.netty.Transmission;

/**
 * @author lijia
 */
public interface ResponseHandler {
    /**
     * 连接建立时回调
     * @param trs 传输器
     */
    void whenConnected(Transmission trs);

    /**
     * 连接断开时回调
     * @param trs 传输器
     */
    void whenClosed(Transmission trs);

    /**
     * 收到鉴权响应时回调
     * @param trs 传输器
     * @param rsp
     */
    void whenAuthResponse(Transmission trs, byte[] rsp);

    /**
     * 收到心跳时回调
     * @param trs
     */
    void whenHeartbeat(Transmission trs);

    /**
     * 收到消息时回调
     * @param trs 传输器
     * @param msg
     */
    void whenReceiveMsg(Transmission trs, byte[] msg);

    /**
     * 收到ack时回调
     * @param trs 传输器
     * @param ack
     */
    void whenAck(Transmission trs, byte[] ack);

    /**
     * 发生异常时回调
     * @param trs 传输器
     * @param cause
     */
    void whenException(Transmission trs, Throwable cause);
}
