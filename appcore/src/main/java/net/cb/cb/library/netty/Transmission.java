package net.cb.cb.library.netty;


import net.cb.cb.library.netty.codec.LooseBinDecoder;
import net.cb.cb.library.netty.codec.ProtoConsts;
import net.cb.cb.library.netty.handler.ResponseHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

import javax.net.ssl.SSLEngine;

import java.util.concurrent.TimeUnit;

/**
 * @author lijia
 */
public class Transmission {
    private boolean withSsl = false;
    private Channel channel;

    private static NioEventLoopGroup bossGroup;

    static {
        bossGroup = new NioEventLoopGroup(1);
    }

    public static Transmission create(boolean withSsl) {
        Transmission instance = new Transmission();

        instance.withSsl = withSsl;

        return instance;
    }

    private LooseBinDecoder decoder;

    private Transmission() {
    }

    /**
     * 连接服务器（同步）
     *
     * @param host    地址
     * @param port    端口
     * @param timeout 连接超时时间（秒）
     * @return
     */
    public void connect(String host, int port, int timeout, ResponseHandler rspHandler) {
        Bootstrap bs = new Bootstrap();
        decoder = new LooseBinDecoder(rspHandler);

        if (null != channel) {
            channel.close();
            channel = null;
        }

        ChannelFuture cf = bs.group(bossGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout * 1000)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pl = ch.pipeline();

                        if (withSsl) {
                            SslContext sslContext = SslContextBuilder.forClient().build();
                            SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
                            sslEngine.setUseClientMode(true);
                            pl.addLast("ssl-handler", new SslHandler(sslEngine));
                        }
                        pl.addLast("idleStateHandler",
                                new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS)
                        );
                        pl.addLast("loose-bin", decoder);
                    }
                }).connect(host, port);
        cf.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                channel = future.channel();
                channel.attr(AttributeKey.valueOf(Transmission.class.getName())).set(this);
                return;
            }

            rspHandler.whenException(this, future.cause());
        });
    }

    /**
     * 启用心跳
     */
    public void enableHeartbeat() {
        decoder.enableHeartbeat();
    }

    /**
     * 发送数据
     *
     * @param pkgType 包类型
     * @param message 消息
     */
    public void sendMsg(ProtoConsts.PackageType pkgType, byte[] message, ChannelFutureListener listener) {
        ByteBuf buf = LooseBinDecoder.encodeBytesToByteBuf(
                (byte) pkgType.getVal(), message, channel.alloc().heapBuffer()
        );

        channel.writeAndFlush(buf).addListener(listener);
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (null != channel) {
            channel.close();
            channel = null;
        }
    }
}
