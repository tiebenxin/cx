package com.yanlong.im.utils.socket;

import java.util.ArrayList;
import java.util.List;

/***
 * V2协议的定义
 */
public class SocketPacket {
    //包头2位
    private static final byte[] P_HEAD = {0x20, 0x20};

    public static final byte[] P_HEART = {0x7F};
    //长度2位
    //   private byte[] p_length = new byte[2];
    //校验位4位(未使用)
//    private static byte[] P_CHECK = new byte[4];
    //版本2位,第一字节为大版本,第二位小版本,1.1
    private static byte[] P_VERSION = {0x01, 0x01};
    //类型2位
    private static byte[] P_TYPE = new byte[2];


    //数据类型枚举
    public enum DataType {
        PROTOBUF_MSG, PROTOBUF_HEARTBEAT, AUTH, ACK, REQUEST_MSG, OTHER;
    }

    public static byte[] getPackage(DataType type, byte[] context) {
        //内容长度
        int contextSize = context == null ? 0 : context.length;
        //长度后面的包长
        byte[] d_length = int2Byte(P_TYPE.length + contextSize);

        //类型
        byte[] d_option = new byte[2];

        switch (type) {
            case PROTOBUF_MSG://普通消息
                d_option = new byte[]{0x00, tobyte(1, 0)};
                break;
            case PROTOBUF_HEARTBEAT://心跳
                d_option = new byte[]{0x00, tobyte(0, 1)};
                break;
            case AUTH://鉴权
                d_option = new byte[]{0x00, tobyte(1, 2)};
                break;
            case ACK://回馈
                d_option = new byte[]{0x00, tobyte(1, 3)};
                break;
            case REQUEST_MSG://请求数据包
                d_option = new byte[]{0x00, tobyte(1, 4)};
                break;

        }

        //包大小
        int d_size = P_HEAD.length + P_VERSION.length + d_length.length + d_option.length + contextSize;

        byte[] rtData = new byte[d_size];
        System.arraycopy(P_HEAD, 0, rtData, 0, 2);
        System.arraycopy(P_VERSION, 0, rtData, 2, 2);
        System.arraycopy(d_length, 0, rtData, 4, 4);
        System.arraycopy(d_option, 0, rtData, 8, 2);
        if (context != null) {
            System.arraycopy(context, 0, rtData, 10, contextSize);
        }


        return rtData;
    }

    /***
     * 时候是包头
     * @param data
     * @return
     */
    public static boolean isHead(byte[] data) {
        if (data == null || data.length < 2)
            return false;

/*        byte[] d = new byte[2];
        d[0] = data[0];
        d[1] = data[1];*/

        return P_HEAD[0] == data[0] && P_HEAD[1] == data[1];
    }


    /***
     * 获取长度
     * @return
     */
    public static int getLength(byte[] data) {
        byte[] d = new byte[4];
        d[0] = data[4];
        d[1] = data[5];
        d[2] = data[6];
        d[3] = data[7];
        return byte2Int(d);
    }

    /***
     * 获取消息类型，option  高位表示传输数据类型，地位 表示option类型
     * @return
     */
    public static DataType getType(byte[] data) {
        if (data.length >= 10) {
            byte[] d = new byte[2];
            d[0] = data[8];//暂时不用
            d[1] = data[9];

            int h = byteH4(d[1]);
            int l = byteL4(d[1]);
            //TODO:没必要判断高位，因为没必要知道数据解析类型
            if (l == 0) {
                return DataType.PROTOBUF_MSG;
            } else if (l == 1) {
                return DataType.PROTOBUF_HEARTBEAT;
            } else if (l == 3) {
                return DataType.ACK;
            } else if (l == 2) {
                return DataType.AUTH;
            } else if (l == 4) {
                return DataType.REQUEST_MSG;
            }
//            if (h == 1 && l == 0) {
//                return DataType.PROTOBUF_MSG;
//            } else if (h == 0 && l == 1) {
//                return DataType.PROTOBUF_HEARTBEAT;
//            } else if (h == 1 && l == 3) {
//                return DataType.ACK;
//            } else if (h == 1 && l == 2) {
//                return DataType.AUTH;
//            } else if (h == 1 && l == 4) {
//                return DataType.REQUEST_MSG;
//            }
        }
        return DataType.OTHER;


    }


    //------------------------转换工具-------------------

    /***
     * 合并数组
     * @param values
     * @return
     */
    public static byte[] listToBytes(List<byte[]> values) {
        int length_byte = 0;
        for (int i = 0; i < values.size(); i++) {
            length_byte += values.get(i).length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.size(); i++) {
            byte[] b = values.get(i);
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

    /***
     * 拆分数组
     * @return
     */
    public static List<byte[]> bytesToLists(byte[] data, int... sp_length) {
        List<byte[]> list = new ArrayList<>();
        int i = 0;
        for (int l : sp_length) {
            byte[] t = new byte[l];
            System.arraycopy(data, i, t, 0, t.length);
            list.add(t);
            i = l;
        }

        int exl = data.length - i;
        byte[] ex = new byte[exl];
        System.arraycopy(data, i, ex, 0, exl);
        if (ex.length > 0) {
            list.add(ex);
        }

        return list;
    }

    /***
     * 合并数组
     * @param values
     * @return
     */
    public static byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
        for (int i = 0; i < values.length; i++) {
            length_byte += values[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

    /**
     * 将int型的数据转换成byte数组，四个字节
     *
     * @param intValue
     * @return
     */
    public static byte[] int2Byte(int intValue) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (intValue >> 8 * (3 - i) & 0xFF);
        }
        return b;
    }

    /**
     * 将byte数组转换成int型，4个字节的数组
     *
     * @param b
     * @return
     */
    public static int byte2Int(byte[] b) {
        int intValue = 0, tempValue = 0xFF;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & tempValue) << (8 * (3 - i));
        }
        return intValue;
    }


    /***
     * 合并字节
     * @param h
     * @param l
     * @return
     */
    private static byte tobyte(int h, int l) {
        return (byte) ((h << 4) & 0xf0 | (l & 0x0f));
    }

    //高4位
    public static int byteH4(byte bt) {
        int val = (bt & 0xf0) >> 4;
        return val;
    }

    //低4位
    public static int byteL4(byte bt) {
        int val = bt & 0x0f;
        return val;
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append("" + hex + " ");
        }
        return sb.toString();
    }
}
