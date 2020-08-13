package net.cb.cb.library.netty.codec;

/**
 * 协议相关常量
 * @author lijia
 */
public class ProtoConsts {
    public enum PackageType {
        /**
         * 普通包、鉴权包、ack包、请求包、上报包
         */
        NORMAL(0x00), AUTH(0x02), ACK(0x03), REQ(0x04), REPORT(0x05);

        PackageType(int v) {
            this.val = v;
        }
        private int val;
        public int getVal() {
            return val;
        }
    }

    static final short MAGIC_NUM = 0x2020;

    /**
     * 魔数长度
     */
    static final int MAGIC_NUM_LENGTH = 2;

    static final int PROTO_VER_MAJOR = 0x00000001;
    static final int PROTO_VER_MINOR = 0x00000001;

    /**
     * 版本字段的长度
     */
    static final long LENGTH_VERSION = 2L;

    /**
     * 长度字段的长度
     */
    static final long LENGTH_FIELD_LENGTH = 4L;

    /**
     * 选项字段的长度
     */
    static final long LENGTH_OPTIONS = 2L;

    /**
     * 包长度有效范围
     */
    static final int PKG_MIN_LEN = 3;
    static final int PKG_MAX_LEN = 256 * 1024 * 1024;
}
