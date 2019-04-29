package com.yanlong.im.utils.socket;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SocketData {
    private static final String TAG = "SocketData";
    //包头2位
    private static final byte[] P_HEAD = {0x20, 0x19};
    //长度2位
    //   private byte[] p_length = new byte[2];
    //校验位4位(未使用)
    private static byte[] P_CHECK = new byte[4];
    //版本2位,第一字节为大版本,第二位小版本
    private static byte[] P_VERSION = {0x01, 0x00};
    //类型2位
    private static byte[] P_TYPE = new byte[2];


    //数据类型枚举
    public enum DataType {
        PROTOBUF_MSG, PROTOBUF_HEARTBEAT, AUTH, ACK, OTHER;
    }

    public static byte[] getPakage(DataType type, byte[] context) {

        //内容长度
        int contextSize = context == null ? 0 : context.length;

        //长度后面的包长
        byte[] d_length = intTobyte2(P_CHECK.length + P_VERSION.length + P_TYPE.length + contextSize);

        //类型
        byte[] d_type = new byte[2];

        switch (type) {
            case PROTOBUF_MSG://普通消息
                d_type = new byte[]{0x00, tobyte(1, 0)};
                break;
            case PROTOBUF_HEARTBEAT://心跳
                d_type = new byte[]{0x00, tobyte(1, 1)};
                break;
            case AUTH://鉴权
                d_type = new byte[]{0x00, tobyte(1, 2)};
                break;
            case ACK://回馈
                d_type = new byte[]{0x00, tobyte(1, 3)};
                break;

        }

        //包大小
        int d_size = P_HEAD.length + d_length.length + P_CHECK.length + P_VERSION.length + d_type.length + contextSize;

        byte[] rtData = new byte[d_size];
        System.arraycopy(P_HEAD, 0, rtData, 0, 2);
        System.arraycopy(d_length, 0, rtData, 2, 2);
        System.arraycopy(P_CHECK, 0, rtData, 4, 4);
        System.arraycopy(P_VERSION, 0, rtData, 8, 2);
        System.arraycopy(d_type, 0, rtData, 10, 2);
        if (context != null) {
            System.arraycopy(context, 0, rtData, 12, contextSize);
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
        byte[] d = new byte[2];
        d[0] = data[2];
        d[1] = data[3];

        return byte2Toint(d);
    }

    /***
     * 获取消息类型
     * @return
     */
    public static DataType getType(byte[] data) {
        if (data.length >= 12) {
            byte[] d = new byte[2];
            d[0] = data[10];//暂时不用
            d[1] = data[11];

            int h = byteH4(d[1]);
            int l = byteL4(d[1]);

            if (h == 1 && l == 0) {
                return DataType.PROTOBUF_MSG;
            } else if (h == 1 && l == 1) {

                return DataType.PROTOBUF_HEARTBEAT;
            }else if(h == 1 && l == 3){
                return DataType.ACK;
            } else if (h == 1 && l == 2) {
                return DataType.AUTH;
            }
        }


        return DataType.OTHER;


    }

    //------------消息内容处理----------------
    private static MsgAllBean send4Base(Long toId, String toGid, MsgBean.MessageType type, Object value) {
        MsgBean.UniversalMessage.Builder msg = SocketData.getMsgBuild();
        if (toId != null) {//给个人发
            msg.setToUid(toId);
        }


        MsgBean.UniversalMessage.WrapMessage.Builder wmsg = msg.getWrapMsgBuilder(0);

        if (toGid != null) {//给群发
            wmsg.setGid(toGid);
        }

        wmsg.setMsgType(type);
        switch (type){
            case CHAT:
                wmsg.setChat((MsgBean.ChatMessage) value);
                break;
            case IMAGE:
                wmsg.setImage((MsgBean.ImageMessage) value);
                break;
            case RED_ENVELOPER:
                wmsg.setRedEnvelope((MsgBean.RedEnvelopeMessage) value);
                break;
            case RECEIVE_RED_ENVELOPER:
                wmsg.setReceiveRedEnvelope((MsgBean.ReceiveRedEnvelopeMessage) value);
                break;
            case TRANSFER:
                wmsg.setTransfer((MsgBean.TransferMessage) value);
                break;
            case STAMP:
                wmsg.setStamp((MsgBean.StampMessage) value);
                break;
            case BUSINESS_CARD:
                wmsg.setBusinessCard((MsgBean.BusinessCardMessage) value);
                break;
            case ACCEPT_BE_FRIENDS:
                wmsg.setAcceptBeFriends((MsgBean.AcceptBeFriendsMessage) value);
                break;
            case REQUEST_FRIEND:
                wmsg.setRequestFriend((MsgBean.RequestFriendMessage) value);
                break;
            case UNRECOGNIZED:
                break;
        }



                //这里设置自己的id从配置中获取
        wmsg.setFromUid(100102l)
                //test
                .setMsgId(UUID.randomUUID().toString());
        MsgBean.UniversalMessage.WrapMessage wm = wmsg.build();
        msg.setWrapMsg(0,wm);
        SocketUtil.getSocketUtil().sendData4Msg(msg);
        return MsgConversionBean.ToBean(wm);
    }

    /***
     * 普通消息
     * @param toId
     * @param txt
     * @return
     */
    public static MsgAllBean send4Chat(Long toId, String toGid, String txt) {


        MsgBean.ChatMessage chat = MsgBean.ChatMessage.newBuilder()
                .setMsg(txt)
                .build();

        return send4Base(toId,toGid, MsgBean.MessageType.CHAT,chat);

    }

    /**
     * 戳一戳消息
     * @param toId
     * @param toGid
     * @param txt
     * @return
     */
    public static MsgAllBean send4action(Long toId, String toGid, String txt) {


        MsgBean.StampMessage action = MsgBean.StampMessage.newBuilder()
                .setComment(txt)
                .build();

        return send4Base(toId,toGid, MsgBean.MessageType.STAMP,action);

    }

    /***
     * 发送图片
     * @param toId
     * @param toGid
     * @param url
     * @return
     */
    public static MsgAllBean send4Image(Long toId, String toGid, String url) {
        MsgBean.ImageMessage  msg = MsgBean.ImageMessage .newBuilder()
                .setUrl(url)
                .build();
        return send4Base(toId,toGid, MsgBean.MessageType.IMAGE,msg);
    }

    /****
     * 发送名片
     * @param toId
     * @param toGid
     * @param iconUrl
     * @param nkName
     * @param info
     * @return
     */
    public static MsgAllBean send4card(Long toId, String toGid, String iconUrl,String nkName,String info) {
        MsgBean.BusinessCardMessage   msg = MsgBean.BusinessCardMessage  .newBuilder()
                .setAvatar(iconUrl)
                .setNickname(nkName)
                .setComment(info)
                .build();
        return send4Base(toId,toGid, MsgBean.MessageType.BUSINESS_CARD,msg);
    }



    /***
     * 处理一些统一的数据,用于发送消息时获取
     * @return
     */
    public static MsgBean.UniversalMessage.Builder getMsgBuild() {
        MsgBean.UniversalMessage.Builder msg = MsgBean.UniversalMessage.newBuilder();
        MsgBean.UniversalMessage.WrapMessage.Builder wp = MsgBean.UniversalMessage.WrapMessage.newBuilder();
        msg.setRequestId("" + System.currentTimeMillis());
        msg.addWrapMsg(0, wp.build());

        return msg;
    }

    /***
     * 授权
     * @return
     */
    public static byte[] msg4Auth() {

        TokenBean tokenBean = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).get4Json(TokenBean.class);
        //  tokenBean = new TokenBean();
        // tokenBean.setAccessToken("2N0qG3CHBxVNQfPjIbbCA/YUY48erDHVTBXZHK1JQAOfAxi86DKcvYKqLwxLfINN");
        LogUtil.getLog().i("tag", ">>>>发送token" + tokenBean.getAccessToken());

        if (tokenBean == null || !StringUtil.isNotNull(tokenBean.getAccessToken())) {
            return null;
        }


        MsgBean.AuthRequestMessage auth = MsgBean.AuthRequestMessage.newBuilder()
                .setAccessToken(tokenBean.getAccessToken()).build();

        return SocketData.getPakage(DataType.AUTH, auth.toByteArray());

    }

    /***
     * 回执
     * @return
     */
    public static byte[] msg4ACK(String rid) {


        MsgBean.AckMessage ack = MsgBean.AckMessage.newBuilder()
                .setRequestId(rid).build();

        return SocketData.getPakage(DataType.ACK, ack.toByteArray());

    }
//------------------------收-----------------------------

    /***
     * 消息转换
     * @param data
     * @return
     */
    public static MsgBean.UniversalMessage msgConversion(byte[] data) {
        try {

            MsgBean.UniversalMessage msg = MsgBean.UniversalMessage.parseFrom(bytesToLists(data, 12).get(1));
            return msg;
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * ack转换
     * @param data
     * @return
     */
    public static MsgBean.AckMessage ackConversion(byte[] data) {
        try {

            MsgBean.AckMessage msg = MsgBean.AckMessage.parseFrom(bytesToLists(data, 12).get(1));
            return msg;
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 鉴权消息转换
     * @param data
     * @return
     */
    public static MsgBean.AuthResponseMessage authConversion(byte[] data) {
        try {
            MsgBean.AuthResponseMessage ruthmsg = MsgBean.AuthResponseMessage.parseFrom(SocketData.bytesToLists(data, 12).get(1));


            return ruthmsg;
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 保存消息和发送消息回执
     */
    public static void magSaveAndACQ(MsgBean.UniversalMessage bean) {
        List<MsgBean.UniversalMessage.WrapMessage> msgList = bean.getWrapMsgList();


        //1.先进行数据分割
        for (MsgBean.UniversalMessage.WrapMessage wmsg : msgList) {
            //2.存库
            MsgAllBean saveBean = MsgConversionBean.ToBean(wmsg);
            LogUtil.getLog().d(TAG, ">>>>>magSaveAndACQ: " + wmsg.getMsgId());
            //收到直接存表
            DaoUtil.save(saveBean);
        }


        //3.发送回执
        SocketUtil.getSocketUtil().sendData(msg4ACK(bean.getRequestId()), null);


    }
//---------------------------------

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


    /***
     * int转为2byte
     * @param val
     * @return
     */
    private static byte[] intTobyte2(int val) {
        byte[] data = new byte[2];
        data[0] = (byte) ((val >> 8) & 0xff);
        data[1] = (byte) (val & 0xff);
        return data;
    }

    /***
     * 2byt转为int,读取长度
     * @param data
     * @return
     */
    public static int byte2Toint(byte[] data) {
        int i = ((data[0] << 8) & 0x0000ff00) | (data[1] & 0x000000ff);
        return i;

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
