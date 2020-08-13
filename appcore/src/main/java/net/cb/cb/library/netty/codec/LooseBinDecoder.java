package net.cb.cb.library.netty.codec;

import net.cb.cb.library.netty.Transmission;
import net.cb.cb.library.netty.handler.ResponseHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

import java.util.List;

import static net.cb.cb.library.netty.codec.ProtoConsts.*;


/**
 * @author lijia
 */
public class LooseBinDecoder extends ByteToMessageDecoder {
    private enum DecodeStatus {
        /**
         * 等待解析魔数
         */
        EXPECT_MAGIC_NO,
        /**
         * 等待解析协议版本
         */
        EXPECT_VERSION,
        /**
         * 等待解析长度
         */
        EXPECT_LENGTH,
        /**
         * 等待解析包体
         */
        EXPECT_BODY,
    }

    /**
     * 心跳包缓冲区
     */
    private static final byte[] HEARTBEAT_PKG = new byte[] {
            // 魔数        版本              长度                     选项           内容
            0x20,0x20,    0x01,0x01,        0x00,0x00,0x00,0x03,    0x00,0x01,    0x7F,
    };
    private ByteBuf heartbeatBuf;

    public LooseBinDecoder(ResponseHandler handler) {
        this.handler = handler;
    }

    private ResponseHandler handler;

    /**
     * 当前解析状态
     */
    private DecodeStatus fsm = DecodeStatus.EXPECT_MAGIC_NO;

    /**
     * 当前包长度
     */
    private long pkgLen;

    /**
     * 心跳启用标记
     */
    private volatile boolean heartbeatEnable = false;

    /**
     * 引用复用
     */
    private Transmission trs;

    public static ByteBuf encodeBytesToByteBuf(byte msgType, byte[] msg, ByteBuf out) {
        // 参考协议文档
        out.writeShort(MAGIC_NUM);
        out.writeShort((PROTO_VER_MAJOR<<8) | PROTO_VER_MINOR);
        out.writeInt((int)LENGTH_OPTIONS + msg.length);
        out.writeShort(0x10 | msgType);
        out.writeBytes(msg);

        return out;
    }

    public void enableHeartbeat() {
        this.heartbeatEnable = true;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        for (;;) {
            // 等待魔数
            if (DecodeStatus.EXPECT_MAGIC_NO == fsm) {
                while (true) {
                    if (in.readableBytes() < MAGIC_NUM_LENGTH) {
                        return;
                    }

                    if (in.getShort(in.readerIndex()) == MAGIC_NUM) {
                        in.readShort();
                        // finish reading
                        fsm = DecodeStatus.EXPECT_VERSION;
                        break;
                    }

                    // drop one byte
                    in.readByte();
                }
            }

            // 等待解析协议版本
            if (DecodeStatus.EXPECT_VERSION == fsm) {
                if (in.readableBytes() < LENGTH_VERSION) {
                    return;
                }

                int ver = in.readUnsignedShort();
                if (((ver >>> 8) != PROTO_VER_MAJOR) || ((ver & 0x00FF) < PROTO_VER_MINOR)) {
                    throw new Exception(String.format("unsupported version:%x", ver));
                }

                // finish reading
                fsm = DecodeStatus.EXPECT_LENGTH;
            }

            // 等待包长度
            if (DecodeStatus.EXPECT_LENGTH == fsm) {
                if (in.readableBytes() < LENGTH_FIELD_LENGTH) {
                    return;
                }

                pkgLen = in.readUnsignedInt();

                if ((pkgLen < PKG_MIN_LEN) || (pkgLen > PKG_MAX_LEN)) {
                    throw new Exception("invalid package length:" + pkgLen);
                }

                // finish reading
                fsm = DecodeStatus.EXPECT_BODY;
            }

            // 等待包体
            if (DecodeStatus.EXPECT_BODY == fsm) {
                if (in.readableBytes() < pkgLen) {
                    return;
                }

                short options = in.readShort();
                int pkgType = options & 0x0F;

                byte[] msgBody = new byte[(int)(pkgLen - LENGTH_OPTIONS)];
                in.readBytes(msgBody);
                // finish reading
                fsm = DecodeStatus.EXPECT_MAGIC_NO;

                if (pkgType == 0x00) {
                    handler.whenReceiveMsg(trs, msgBody);
                } else if (pkgType == 0x01) {
                    handler.whenHeartbeat(trs);
                } else if (pkgType == 0x02) {
                    handler.whenAuthResponse(trs, msgBody);
                } else if (pkgType == 0x03) {
                    handler.whenAck(trs, msgBody);
                }
            }
        }
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();

        trs = (Transmission) ch.attr(AttributeKey.valueOf(Transmission.class.getName())).get();
        heartbeatBuf = ch.alloc().buffer(HEARTBEAT_PKG.length);
        heartbeatBuf.writeBytes(HEARTBEAT_PKG);

        handler.whenConnected(trs);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 只关心IdleStateEvent
        if (!(evt instanceof IdleStateEvent)) {
            super.userEventTriggered(ctx, evt);
            return;
        }

        // 发送心跳
        if (heartbeatEnable) {
            heartbeatBuf.markReaderIndex();
            ctx.channel().writeAndFlush(heartbeatBuf.retain(1));
            heartbeatBuf.resetReaderIndex();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        handler.whenException(trs, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        handler.whenClosed(trs);
    }
}
