package com.yanlong.im.utils.socket;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.AssistantMessage;
import com.yanlong.im.chat.bean.AtMessage;
import com.yanlong.im.chat.bean.BusinessCardMessage;
import com.yanlong.im.chat.bean.ChatMessage;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.IMsgContent;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgCancel;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.chat.bean.MsgNotice;
import com.yanlong.im.chat.bean.RedEnvelopeMessage;
import com.yanlong.im.chat.bean.StampMessage;
import com.yanlong.im.chat.bean.TransferMessage;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.server.ChatServer;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.utils.ImgSizeUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.yanlong.im.utils.socket.MsgBean.MessageType.ACCEPT_BE_FRIENDS;

public class SocketData {
    private static final String TAG = "SocketData";

    private static long preServerAckTime;//前一个服务器回执时间
    private static long preSendLocalTime;//前一个本地消息发送的时间


    private static MsgDao msgDao = new MsgDao();
    public static long CLL_ASSITANCE_ID = 1L;//常信小助手id


    /***
     * 处理一些统一的数据,用于发送消息时获取
     * @return
     */
    public static MsgBean.UniversalMessage.Builder getMsgBuild() {
        MsgBean.UniversalMessage.Builder msg = MsgBean.UniversalMessage.newBuilder();
        MsgBean.UniversalMessage.WrapMessage.Builder wp = MsgBean.UniversalMessage.WrapMessage.newBuilder();
        msg.setRequestId("" + getSysTime());
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

        return SocketPact.getPakage(SocketPact.DataType.AUTH, auth.toByteArray());

    }

    /***
     * 回执,可以不发送msgId
     * @return
     */
    public static byte[] msg4ACK(String rid, List<String> msgids) {

        MsgBean.AckMessage ack;
        MsgBean.AckMessage.Builder amsg = MsgBean.AckMessage.newBuilder().setRequestId(rid);
        if (msgids != null) {
            for (int i = 0; i < msgids.size(); i++) {
                amsg.addMsgId(msgids.get(i));
            }
        }
        ack = amsg.build();

        return SocketPact.getPakage(SocketPact.DataType.ACK, ack.toByteArray());

    }
//------------------------收-----------------------------

    /***
     * 消息转换
     * @param data
     * @return
     */
    public static MsgBean.UniversalMessage msgConversion(byte[] data) {
        try {

            MsgBean.UniversalMessage msg = MsgBean.UniversalMessage.parseFrom(SocketPact.bytesToLists(data, 12).get(1));
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

            MsgBean.AckMessage msg = MsgBean.AckMessage.parseFrom(SocketPact.bytesToLists(data, 12).get(1));
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
            MsgBean.AuthResponseMessage ruthmsg = MsgBean.AuthResponseMessage.parseFrom(SocketPact.bytesToLists(data, 12).get(1));


            return ruthmsg;
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    //6.6 为后端擦屁股
    public static CopyOnWriteArrayList<String> oldMsgId = new CopyOnWriteArrayList<>();

    /***
     * 保存接收到的消息及发送消息回执
     */
    /*public static void magSaveAndACK(MsgBean.UniversalMessage bean) {
        List<MsgBean.UniversalMessage.WrapMessage> msgList = bean.getWrapMsgList();


        List<String> msgIds = new ArrayList<>();
        //1.先进行数据分割
        for (MsgBean.UniversalMessage.WrapMessage wmsg : msgList) {
            checkDoubleMessage(wmsg);
            //2.存库:1.存消息表,存会话表
            MsgAllBean msgAllBean = MsgConversionBean.ToBean(wmsg);
            //5.28 如果为空就不保存这类消息
            if (msgAllBean != null) {
                if (!oldMsgId.contains(wmsg.getMsgId())) {//不是重复消息才更新
                    msgAllBean.setRead(false);//设置未读
                    msgAllBean.setTo_uid(bean.getToUid());
                    LogUtil.getLog().d(TAG, ">>>>>magSaveAndACK: " + wmsg.getMsgId());
                    //收到直接存表
                    DaoUtil.update(msgAllBean);

                    //6.6 为后端擦屁股
                    if (oldMsgId.size() >= 500)
                        oldMsgId.remove(0);
                    oldMsgId.add(wmsg.getMsgId());
                    if (!TextUtils.isEmpty(msgAllBean.getGid()) && !msgDao.isGroupExist(msgAllBean.getGid()) && !loadGids.contains(msgAllBean.getGid())) {
                        loadGids.add(msgAllBean.getGid());
                        loadGroupInfo(msgAllBean.getGid(), msgAllBean.getFrom_uid());
//                        MessageManager.getInstance().updateSessionUnread(msgAllBean.getGid(), msgAllBean.getFrom_uid(), false);
//                        MessageManager.getInstance().setMessageChange(true);
                    } else if (TextUtils.isEmpty(msgAllBean.getGid()) && msgAllBean.getFrom_uid() != null && msgAllBean.getFrom_uid() > 0 && !loadUids.contains(msgAllBean.getFrom_uid())) {
                        loadUids.add(msgAllBean.getFrom_uid());
                        loadUserInfo(msgAllBean.getGid(), msgAllBean.getFrom_uid());
                    } else {
                        MessageManager.getInstance().updateSessionUnread(msgAllBean.getGid(), msgAllBean.getFrom_uid(), false);
                        MessageManager.getInstance().setMessageChange(true);
                    }
                    LogUtil.getLog().e(TAG, ">>>>>累计 ");
                } else {
                    LogUtil.getLog().e(TAG, ">>>>>重复消息: " + wmsg.getMsgId());
                }
                msgIds.add(wmsg.getMsgId());
            } else {
                LogUtil.getLog().e(TAG, ">>>>>忽略保存消息: " + wmsg.getMsgId());
            }


        }


        //3.发送回执
        LogUtil.getLog().d(TAG, ">>>>>发送回执: " + bean.getRequestId());
//        SocketUtil.getSocketUtil().sendData(msg4ACK(bean.getRequestId(), msgIds), null);


    }*/

    //检测是否是双重消息，及一条消息需要产生两条本地消息记录,回执在通知消息中发送
    private static void checkDoubleMessage(MsgBean.UniversalMessage.WrapMessage wmsg) {
        if (wmsg.getMsgType() == ACCEPT_BE_FRIENDS) {
            MsgBean.AcceptBeFriendsMessage receiveMessage = wmsg.getAcceptBeFriends();
            if (receiveMessage != null && !TextUtils.isEmpty(receiveMessage.getSayHi())) {
                ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), receiveMessage.getSayHi());
                MsgAllBean message = createMsgBean(wmsg, ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
                DaoUtil.save(message);
//                MessageManager.getInstance().updateSessionUnread(message.getGid(), message.getFrom_uid(),false);//不更新未读，只需要一条即可
                MessageManager.getInstance().setMessageChange(true);
            }
        }
    }

    /***
     * 在服务器接收到自己发送的消息后,本地保存
     * @param bean
     */
    public static void msgSave4Me(MsgBean.AckMessage bean) {
        //普通消息
        MsgBean.UniversalMessage.Builder msg = SendList.findMsgById(bean.getRequestId());
        //6.25 排除通知存库

        if (msg != null && msgSendSave4filter(msg.getWrapMsg(0).toBuilder())) {
            //存库处理
            MsgBean.UniversalMessage.WrapMessage wmsg = msg.getWrapMsgBuilder(0)
                    .setMsgId(bean.getMsgIdList().get(0))
                    //时间要和ack一起返回
                    .setTimestamp(getSysTime())
                    .build();
            //  Log.d(TAG, "msgSave4Me2: msg" + msg.toString());

            MsgAllBean msgAllBean = MsgConversionBean.ToBean(wmsg, msg, false);

            msgAllBean.setMsg_id(msgAllBean.getMsg_id());
            //时间戳
            /*if(wmsg.getTimestamp()!=0){
                msgAllBean.setTimestamp(wmsg.getTimestamp());
            }else{*/
            msgAllBean.setTimestamp(bean.getTimestamp());
            /*}*/

            msgAllBean.setSend_state(ChatEnum.ESendStatus.NORMAL);
            //7.16 如果是收到先自己发图图片的消息

            //移除旧消息
            DaoUtil.deleteOne(MsgAllBean.class, "request_id", msgAllBean.getRequest_id());

            if (msgAllBean.getVideoMessage()!=null){
                msgAllBean.getVideoMessage().setLocalUrl(videoLocalUrl);
            }
            //收到直接存表,创建会话
            DaoUtil.update(msgAllBean);
            MsgDao msgDao = new MsgDao();

            msgDao.sessionCreate(msgAllBean.getGid(), msgAllBean.getTo_uid());
            MessageManager.getInstance().setMessageChange(true);

        }
        //6.25 移除重发列队
        SendList.removeSendListJust(bean.getRequestId());


    }

    //6.26 消息直接存库
    public static void msgSave4Me(MsgBean.UniversalMessage.Builder msg, int state) {
        //普通消息

        if (msg != null) {
            //存库处理
            MsgBean.UniversalMessage.WrapMessage wmsg = msg.getWrapMsgBuilder(0)
                    // .setMsgId(bean.getMsgIdList().get(0))
                    //时间要和ack一起返回
                    // .setTimestamp(System.currentTimeMillis())
                    .build();
            Log.d(TAG, "msgSave4Me1: msg" + msg.toString());
            MsgAllBean msgAllBean = MsgConversionBean.ToBean(wmsg, msg, false);

            msgAllBean.setMsg_id(msgAllBean.getMsg_id());
            //时间戳
            // msgAllBean.setTimestamp(bean.getTimestamp());
            //是发送给群助手的消息直接发送成功
            if (isNoAssistant(msgAllBean.getTo_uid(), msgAllBean.getGid())) {
                msgAllBean.setSend_state(state);
            } else {
                msgAllBean.setSend_state(ChatEnum.ESendStatus.NORMAL);
            }
            msgAllBean.setSend_data(msg.build().toByteArray());

            //移除旧消息// 7.16 通过msgid 判断唯一
            DaoUtil.deleteOne(MsgAllBean.class, "request_id", msgAllBean.getRequest_id());
            // DaoUtil.deleteOne(MsgAllBean.class, "msg_id", msgAllBean.getMsg_id());
            if (msgAllBean.getVideoMessage()!=null){
                msgAllBean.getVideoMessage().setLocalUrl(videoLocalUrl);
            }
            //收到直接存表,创建会话
            DaoUtil.update(msgAllBean);
            MsgDao msgDao = new MsgDao();

            msgDao.sessionCreate(msgAllBean.getGid(), msgAllBean.getTo_uid());
            MessageManager.getInstance().setMessageChange(true);
        }
    }

    /***
     * 发送失败
     * @param bean
     * 发送失败的消息不更新时间
     */
    public static void msgSave4MeFail(MsgBean.AckMessage bean) {
        //普通消息
        MsgBean.UniversalMessage.Builder msg = SendList.findMsgById(bean.getRequestId());
        if (msg != null) {
            //存库处理
            MsgBean.UniversalMessage.WrapMessage wmsg = msg.getWrapMsgBuilder(0)
                    .setMsgId(bean.getMsgIdList().get(0))
                    //时间要和ack一起返回
//                    .setTimestamp(getSysTime())
                    .build();
            MsgAllBean msgAllBean = MsgConversionBean.ToBean(wmsg, msg, true);

            msgAllBean.setMsg_id(msgAllBean.getMsg_id());
            //时间戳
//            msgAllBean.setTimestamp(bean.getTimestamp());
            msgAllBean.setTimestamp(msg.getWrapMsg(0).getTimestamp());
            msgAllBean.setSend_state(ChatEnum.ESendStatus.ERROR);
            msgAllBean.setSend_data(msg.build().toByteArray());

            //移除旧消息
            DaoUtil.deleteOne(MsgAllBean.class, "request_id", msgAllBean.getRequest_id());

            //收到直接存表,创建会话
            DaoUtil.update(msgAllBean);
            MsgDao msgDao = new MsgDao();

            msgDao.sessionCreate(msgAllBean.getGid(), msgAllBean.getTo_uid());
            MessageManager.getInstance().setMessageChange(true);

            //移除重发列队
            SendList.removeSendListJust(bean.getRequestId());


        }
    }

    //5.27 发送前保存到库
    public static void msgSave4MeSendFront(MsgBean.UniversalMessage.Builder msg) {
        msgSave4Me(msg, 2);
    }


    /***
     * 保存并发送消息
     * @param toId
     * @param toGid
     * @param type
     * @param value
     * @return
     */
    private static MsgAllBean send4Base(Long toId, String toGid, MsgBean.MessageType type, Object value) {
        return send4Base(true, true, null, toId, toGid, -1, type, value);
    }

    /***
     * 根据消息id保存发送数据
     * @param msgId
     * @param toId
     * @param toGid
     * @param type
     * @param value
     * @return
     */
    private static MsgAllBean send4BaseById(String msgId, Long toId, String toGid, long time, MsgBean.MessageType type, Object value) {
        return send4Base(true, true, msgId, toId, toGid, time, type, value);
    }

    /***
     * 只保存消息,不缓存
     * @param msgId
     * @param toId
     * @param toGid
     * @param type
     * @param value
     * @return
     */
    private static MsgAllBean send4BaseJustSave(String msgId, Long toId, String toGid, MsgBean.MessageType type, Object value) {
        return send4Base(true, false, msgId, toId, toGid, -1L, type, value);
    }

    /*
     * @time time > 0
     * */
    private static MsgAllBean send4Base(boolean isSave, boolean isSend, String msgId, Long toId, String toGid, long time, MsgBean.MessageType type, Object value) {
        LogUtil.getLog().i(TAG, ">>>---发送到toid" + toId + "--gid" + toGid);
        MsgBean.UniversalMessage.Builder msg = toMsgBuilder(msgId, toId, toGid, time > 0 ? time : getFixTime(), type, value);
        if (isSave && msgSendSave4filter(msg.getWrapMsg(0).toBuilder())) {
            msgSave4MeSendFront(msg); //5.27 发送前先保存到库,
        }
        //立即发送
        if (isSend && isNoAssistant(toId, toGid)) {
            SocketUtil.getSocketUtil().sendData4Msg(msg);
        }
        MsgAllBean msgAllbean = MsgConversionBean.ToBean(msg.getWrapMsg(0));
        return msgAllbean;
    }

    //不是常信小助手id
    public static boolean isNoAssistant(Long uid, String gid) {
        if (TextUtils.isEmpty(gid) && (uid != null && uid == CLL_ASSITANCE_ID)) {
            return false;
        }
        return true;
    }


    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /***
     * 6.26消息构建
     * @param toId
     * @param toGid
     * @param type
     * @param value
     * @return
     */
    private static MsgBean.UniversalMessage.Builder toMsgBuilder(String msgid, Long toId, String toGid, long time, MsgBean.MessageType type, Object value) {
        MsgBean.UniversalMessage.Builder msg = SocketData.getMsgBuild();
        if (toId != null && toId > 0) {//给个人发
            msg.setToUid(toId);
        }


        MsgBean.UniversalMessage.WrapMessage.Builder wmsg = msg.getWrapMsgBuilder(0);
        UserInfo userInfo = UserAction.getMyInfo();
        wmsg.setFromUid(userInfo.getUid());
        wmsg.setAvatar(userInfo.getHead());


        wmsg.setNickname(userInfo.getName());

        //自动生成uuid

        wmsg.setMsgId(msgid == null ? getUUID() : msgid);


//        wmsg.setTimestamp(getSysTime());
        wmsg.setTimestamp(time);

        if (toGid != null && toGid.length() > 0) {//给群发
            wmsg.setGid(toGid);
            Group group = msgDao.getGroup4Id(toGid);
            if (group != null) {
                String name = group.getMygroupName();
                if (StringUtil.isNotNull(name)) {
                    wmsg.setMembername(name);
                }
            }

        }

        wmsg.setMsgType(type);
        switch (type) {
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
            case VOICE:
                wmsg.setVoice((MsgBean.VoiceMessage) value);
                break;
            case AT:
                wmsg.setAt((MsgBean.AtMessage) value);
                break;
            case CANCEL:
                wmsg.setCancel((MsgBean.CancelMessage) value);
                break;
            case SHORT_VIDEO:
                wmsg.setShortVideo((MsgBean.ShortVideoMessage) value);
                break;
            case P2P_AU_VIDEO:
                wmsg.setP2PAuVideo((MsgBean.P2PAuVideoMessage) value);
                break;
            case P2P_AU_VIDEO_DIAL:
                wmsg.setP2PAuVideoDial((MsgBean.P2PAuVideoDialMessage) value);
                break;
            case UNRECOGNIZED:
                break;

        }


        MsgBean.UniversalMessage.WrapMessage wm = wmsg.build();
        msg.setWrapMsg(0, wm);
        return msg;
    }

    /***
     * 忽略存库的消息
     * @return false 需要忽略
     */
    private static boolean msgSendSave4filter(MsgBean.UniversalMessage.WrapMessage.Builder wmsg) {
        if (wmsg.getMsgType() == MsgBean.MessageType.RECEIVE_RED_ENVELOPER || wmsg.getMsgType() == MsgBean.MessageType.CANCEL) {
            return false;
        }


        return true;

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

        return send4Base(toId, toGid, MsgBean.MessageType.CHAT, chat);

    }

    /**
     * 发送一条音视频消息
     *
     * @param toId
     * @param toGid
     * @param txt         操作加时长
     * @param auVideoType 语音、视频
     * @param operation   操作
     * @return
     */
    public static MsgAllBean send4VoiceOrVideo(Long toId, String toGid, String txt, MsgBean.AuVideoType auVideoType, String operation) {
        MsgBean.P2PAuVideoMessage chat = MsgBean.P2PAuVideoMessage.newBuilder()
                .setAvType(auVideoType)
                .setOperation(operation)
                .setDesc(txt)
                .build();

        return send4Base(toId, toGid, MsgBean.MessageType.P2P_AU_VIDEO, chat);

    }

    /**
     * 发送一条音视频通知
     *
     * @param toId
     * @param toGid
     * @param auVideoType 语音、视频
     * @return
     */
    public static MsgAllBean send4VoiceOrVideoNotice(Long toId, String toGid,MsgBean.AuVideoType auVideoType) {
        MsgBean.P2PAuVideoDialMessage chat = MsgBean.P2PAuVideoDialMessage.newBuilder()
                .setAvType(auVideoType)
                .build();

        return send4Base(toId, toGid, MsgBean.MessageType.P2P_AU_VIDEO_DIAL, chat);

    }


    /**
     * @param toId
     * @param txt
     * @param atType 0. @多个 1. @所有人
     * @param list   @用户集合
     * @return
     * @消息
     */
    public static MsgAllBean send4At(Long toId, String toGid, String txt, int atType, List<Long> list) {
        MsgBean.AtMessage atMessage = MsgBean.AtMessage.newBuilder()
                .setMsg(txt)
                .setAtTypeValue(atType)
                .addAllUid(list)
                .build();
        return send4Base(toId, toGid, MsgBean.MessageType.AT, atMessage);
    }


    /**
     * 戳一戳消息
     *
     * @param toId
     * @param toGid
     * @param txt
     * @return
     */
    public static MsgAllBean send4action(Long toId, String toGid, String txt) {


        MsgBean.StampMessage action = MsgBean.StampMessage.newBuilder()
                .setComment(txt)
                .build();

        return send4Base(toId, toGid, MsgBean.MessageType.STAMP, action);

    }

    /***
     * 发送图片
     * @param toId
     * @param toGid
     * @param url
     * @return
     */
    public static MsgAllBean send4Image(String msgId, Long toId, String toGid, String url, boolean isOriginal, ImgSizeUtil.ImageSize imageSize, long time) {
        MsgBean.ImageMessage.Builder msg;
        String extTh = "/below-20k";
        String extPv = "/below-200k";
        if (url.toLowerCase().contains(".gif")) {
            extTh = "";
            extPv = "";
        }
        if (isOriginal) {
            msg = MsgBean.ImageMessage.newBuilder()
                    .setOrigin(url)
                    .setPreview(url + extPv)
                    .setThumbnail(url + extTh);

        } else {
            msg = MsgBean.ImageMessage.newBuilder()
                    .setPreview(url)
                    .setThumbnail(url + extTh);

        }
        MsgBean.ImageMessage msgb;
        if (imageSize != null) {
            msgb = msg.setWidth(imageSize.getWidth())
                    .setHeight(imageSize.getHeight())
                    .setSize(new Long(imageSize.getSize()).intValue())
                    .build();
        } else {
            msgb = msg.build();
        }


        return send4BaseById(msgId, toId, toGid, time, MsgBean.MessageType.IMAGE, msgb);
    }

    /***
     * 发送视频
     * @param toId
     * @param toGid
     * @param videoMessage
     * @return
     */
    public static MsgAllBean 发送视频整体信息(Long toId, String toGid, VideoMessage videoMessage) {
        String bg_URL = videoMessage.getBg_url();
        long time = videoMessage.getDuration();
        String url = videoMessage.getUrl();
        long width = videoMessage.getWidth();
        long height = videoMessage.getHeight();
        String msgId = videoMessage.getMsgId();


        MsgBean.ShortVideoMessage msg;
        msg = MsgBean.ShortVideoMessage.newBuilder().setBgUrl(bg_URL).setDuration((int) time).setUrl(url).setWidth((int) width).setHeight((int) height).build();
        return send4BaseById(msgId, toId, toGid, time, MsgBean.MessageType.SHORT_VIDEO, msg);
    }

    public static MsgAllBean 转发送视频整体信息(Long toId, String toGid, VideoMessage videoMessage) {
        String bg_URL = videoMessage.getBg_url();
        long time = videoMessage.getDuration();
        String url = videoMessage.getUrl();
        long width = videoMessage.getWidth();
        long height = videoMessage.getHeight();
        String msgId = videoMessage.getMsgId();


        MsgBean.ShortVideoMessage msg;
        msg = MsgBean.ShortVideoMessage.newBuilder().setBgUrl(bg_URL).setDuration((int) time).setUrl(url).setWidth((int) width).setHeight((int) height).build();
        return send4Base(toId, toGid, MsgBean.MessageType.SHORT_VIDEO, msg);
    }

    /***
     * 发送视频
     * @param toId
     * @param toGid
     * @param url
     * @return
     */
    private static String videoLocalUrl=null;
    public static MsgAllBean 发送视频信息(String msgId, Long toId, String toGid, String url, String bg_URL, boolean isOriginal, long time, int width, int height,String videoLocalPath) {
        MsgBean.ShortVideoMessage msg;
        videoLocalUrl=videoLocalPath;
//        String extTh = "/below-20k";
//        String extPv = "/below-200k";
//        if (url.toLowerCase().contains(".gif")) {
//            extTh = "";
//            extPv = "";
//        }
//        if (isOriginal) {
//            msg = MsgBean.ShortVideoMessage.newBuilder().set
//                    .setOrigin(url)
//                    .setPreview(url + extPv)
//                    .setThumbnail(url + extTh);
//
//        } else {
//            msg = MsgBean.ShortVideoMessage.newBuilder()
//                    .setPreview(url)
//                    .setThumbnail(url + extTh);
//
//        }
//        MsgBean.ImageMessage msgb;
//        if (imageSize != null) {
//            msgb = msg.setWidth((int)imageSize.getWidth())
//                    .setHeight((int)imageSize.getHeight())
//                    .setSize((int)size)
//                    .build();
//        } else {
//            msgb = msg.build();
//        }

        msg = MsgBean.ShortVideoMessage.newBuilder().setBgUrl(bg_URL).setDuration((int) time).setUrl(url).setWidth(width).setHeight(height).build();
        return send4BaseById(msgId, toId, toGid, -1, MsgBean.MessageType.SHORT_VIDEO, msg);
    }

    public static MsgAllBean 转发送视频信息(String msgId, Long toId, String toGid, String url, String bg_URL, boolean isOriginal, long time, int width, int height) {
        MsgBean.ShortVideoMessage msg;
        msg = MsgBean.ShortVideoMessage.newBuilder().setBgUrl(bg_URL).setDuration((int) time).setUrl(url).setWidth(width).setHeight(height).build();
        return send4Base(toId, toGid, MsgBean.MessageType.SHORT_VIDEO, msg);
    }

    /***
     * 转发处理
     * @param toId
     * @param toGid
     * @param url
     * @param url1
     * @param url2
     * @return
     */
    public static MsgAllBean send4Image(Long toId, String toGid, String url, String url1, String url2, int w, int h, int size) {
        MsgBean.ImageMessage msg = MsgBean.ImageMessage.newBuilder()
                .setOrigin(url)
                .setPreview(url1)
                .setThumbnail(url2)
                .setWidth(w)
                .setHeight(h)
                .setSize(size)
                .build();


        return send4Base(toId, toGid, MsgBean.MessageType.IMAGE, msg);
    }

    public static MsgAllBean send4Image(Long toId, String toGid, String url, ImgSizeUtil.ImageSize imgSize, long time) {

        return send4Image(getUUID(), toId, toGid, url, false, imgSize, time);
    }


    //预发送需文件（图片，语音）上传消息,保存消息及更新session
    public static <T> MsgAllBean sendFileUploadMessagePre(String msgId, Long toId, String toGid, long time, T t, @ChatEnum.EMessageType int type) {
        //前保存
        MsgAllBean msgAllBean = new MsgAllBean();
        msgAllBean.setMsg_id(msgId);
        UserInfo myinfo = UserAction.getMyInfo();
        msgAllBean.setFrom_uid(myinfo.getUid());
        msgAllBean.setFrom_avatar(myinfo.getHead());
        msgAllBean.setFrom_nickname(myinfo.getName());
        msgAllBean.setRequest_id(getSysTime() + "");
        msgAllBean.setTimestamp(time);
        msgAllBean.setMsg_type(type);
        switch (type) {
            case ChatEnum.EMessageType.IMAGE:
                ImageMessage image = (ImageMessage) t;
                msgAllBean.setImage(image);
                break;
            case ChatEnum.EMessageType.VOICE:
                VoiceMessage voice = (VoiceMessage) t;
                msgAllBean.setVoiceMessage(voice);
                break;
            case ChatEnum.EMessageType.MSG_VIDEO:
                VideoMessage video = (VideoMessage) t;
                msgAllBean.setVideoMessage(video);
                break;
        }

        msgAllBean.setTo_uid(toId);
        msgAllBean.setGid(toGid == null ? "" : toGid);
        msgAllBean.setSend_state(ChatEnum.ESendStatus.PRE_SEND);

        Log.d(TAG, "sendFileUploadMessagePre: msgId" + msgId);

        DaoUtil.update(msgAllBean);
        msgDao.sessionCreate(msgAllBean.getGid(), msgAllBean.getTo_uid());
        MessageManager.getInstance().setMessageChange(true);
        return msgAllBean;
    }

    public static VoiceMessage createVoiceMessage(String msgId, String url, int duration) {
        VoiceMessage message = new VoiceMessage();
        message.setPlayStatus(ChatEnum.EPlayStatus.NO_DOWNLOADED);
        message.setMsgid(msgId);
        message.setTime(duration);
        message.setLocalUrl(url);
        return message;
    }

    @NonNull
    public static ImageMessage createImageMessage(String msgId, String url, boolean isOriginal) {
        ImageMessage image = new ImageMessage();
        image.setLocalimg(url);
        image.setPreview(url);
        image.setThumbnail(url);
        image.setMsgid(msgId);
        ImgSizeUtil.ImageSize img = ImgSizeUtil.getAttribute(url);
        image.setWidth(img.getWidth());
        image.setHeight(img.getHeight());
        if (isOriginal) {
            image.setOrigin(url);
        }
        return image;
    }

    @NonNull
    public static ImageMessage createImageMessage(String msgId, String url, boolean isOriginal, ImgSizeUtil.ImageSize imageSize) {
        ImageMessage image = new ImageMessage();
        image.setLocalimg(url);
        image.setPreview(url);
        image.setThumbnail(url);
        image.setMsgid(msgId);
        image.setWidth(imageSize.getWidth());
        image.setHeight(imageSize.getHeight());
        if (isOriginal) {
            image.setOrigin(url);
        }
        return image;
    }

    @NonNull
    public static VideoMessage createVideoMessage(String msgId, String url, String bgUrl, boolean isOriginal, long duration, long width, long height, String localUrl) {
        VideoMessage videoMessage = new VideoMessage();
        videoMessage.setMsgId(msgId);
        videoMessage.setUrl(url);
        videoMessage.setBg_url(bgUrl);
        videoMessage.setDuration(duration);
        videoMessage.setHeight(height);
        videoMessage.setWidth(width);
        videoMessage.setLocalUrl(localUrl);
        if (isOriginal) {
            videoMessage.setReadOrigin(isOriginal);
        }
        return videoMessage;
    }


    /**
     * 图片发送失败
     *
     * @param msgId
     * @return
     */
    public static MsgAllBean send4ImageFail(String msgId) {
        return msgDao.fixStataMsg(msgId, 1);

    }

    /***
     * 发送语音
     * @param toId
     * @param toGid
     * @param url
     * @param time
     * @return
     */
    public static MsgAllBean send4Voice(Long toId, String toGid, String url, int time) {
        MsgBean.VoiceMessage msg = MsgBean.VoiceMessage.newBuilder()
                .setUrl(url)
                .setDuration(time)
                .build();
        return send4Base(toId, toGid, MsgBean.MessageType.VOICE, msg);
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
    public static MsgAllBean send4card(Long toId, String toGid, Long uid, String iconUrl, String nkName, String info) {
        MsgBean.BusinessCardMessage msg = MsgBean.BusinessCardMessage.newBuilder()
                .setAvatar(iconUrl)
                .setNickname(nkName)
                .setComment(info)
                //uid
                .setUid(uid)
                .build();
        return send4Base(toId, toGid, MsgBean.MessageType.BUSINESS_CARD, msg);
    }


    /***
     * 发送红包
     * @param toId
     * @param toGid
     * @param rid
     * @param info
     * @return
     */
    public static MsgAllBean send4Rb(Long toId, String toGid, String rid, String info, MsgBean.RedEnvelopeMessage.RedEnvelopeStyle style) {

        MsgBean.RedEnvelopeMessage msg = MsgBean.RedEnvelopeMessage.newBuilder()
                .setId(rid)
                .setComment(info)
                .setReType(MsgBean.RedEnvelopeMessage.RedEnvelopeType.MFPAY)
                .setStyle(style)
                .build();
        return send4Base(toId, toGid, MsgBean.MessageType.RED_ENVELOPER, msg);
    }

    /***
     * 收红包
     * @param toId
     * @param toGid
     * @param rid
     * @return
     */
    public static MsgAllBean send4RbRev(Long toId, String toGid, String rid) {
        msgDao.redEnvelopeOpen(rid, true);
        MsgBean.ReceiveRedEnvelopeMessage msg = MsgBean.ReceiveRedEnvelopeMessage.newBuilder()
                .setId(rid)
                .build();

        if (toId.longValue() == UserAction.getMyId().longValue()) {//自己的不发红包通知,只保存
            MsgBean.UniversalMessage.Builder umsg = toMsgBuilder(null, toId, toGid, getFixTime(), MsgBean.MessageType.RECEIVE_RED_ENVELOPER, msg);
            msgSave4Me(umsg, 0);
            return MsgConversionBean.ToBean(umsg.getWrapMsg(0));
        }

        //8.19 收到红包给自己增加一条消息
        String mid = getUUID();
        MsgNotice note = new MsgNotice();
        note.setMsgid(mid);
        note.setMsgType(8);
        String name = msgDao.getUsername4Show(toGid, toId);
        String rname = "<font color='#276baa' id='" + toId + "'>" + name + "</font>";
        if (toId.longValue() == UserAction.getMyId().longValue()) {
            rname = "自己";
        }
        note.setNote("你领取了\"" + rname + "的云红包" + "<div id= '" + toGid + "'></div>");
        msgDao.noteMsgAddRb(mid, toId, toGid, note);
        return send4Base(toId, toGid, MsgBean.MessageType.RECEIVE_RED_ENVELOPER, msg);
    }

    /***
     *发转账
     * @return
     */
    public static MsgAllBean send4Trans(Long toId, String rid, String info, String money) {

        MsgBean.TransferMessage msg = MsgBean.TransferMessage.newBuilder()
                .setId(rid)
                .setComment(info)
                .setTransactionAmount(money)
                .build();
        return send4Base(toId, null, MsgBean.MessageType.TRANSFER, msg);
    }

    /**
     * 撤回消息
     *
     * @param toId
     * @param toGid
     * @param msgId      消息ID
     * @param msgContent 撤回内容
     * @param msgType    撤回的消息类型
     * @return
     */
    public static MsgAllBean send4CancelMsg(Long toId, String toGid, String msgId, String msgContent, Integer msgType) {

        MsgBean.CancelMessage msg = MsgBean.CancelMessage.newBuilder()
                .setMsgId(msgId)
                .build();

        String id = getUUID();
        MsgAllBean msgAllBean = send4Base(false, true, id, toId, toGid, -1, MsgBean.MessageType.CANCEL, msg);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMsg(msgContent);
        chatMessage.setMsgid(msgType + "");// 暂时用来存放撤回的消息类型
        msgAllBean.setChat(chatMessage);
        ChatServer.addCanceLsit(id, msgAllBean);

        return msgAllBean;
    }

    public static void sendAndSaveMessage(MsgAllBean bean) {
        LogUtil.getLog().i(TAG, ">>>---发送到toid" + bean.getTo_uid() + "--gid" + bean.getGid());

        int msgType = bean.getMsg_type();
        MsgBean.MessageType type = null;
        Object value = null;
        switch (msgType) {
            case ChatEnum.EMessageType.TEXT:
                ChatMessage chat = bean.getChat();
                MsgBean.ChatMessage.Builder txtBuilder = MsgBean.ChatMessage.newBuilder();
                txtBuilder.setMsg(chat.getMsg());
                value = txtBuilder.build();
                type = MsgBean.MessageType.CHAT;
                break;
            case ChatEnum.EMessageType.IMAGE:
                ImageMessage image = bean.getImage();
                MsgBean.ImageMessage.Builder imgBuilder = MsgBean.ImageMessage.newBuilder();
                imgBuilder.setOrigin(image.getOrigin())
                        .setPreview(image.getPreview())
                        .setThumbnail(image.getThumbnail())
                        .setHeight((int) image.getHeight())
                        .setWidth((int) image.getWidth())
                        .setSize((int) image.getSize());
                value = imgBuilder.build();
                type = MsgBean.MessageType.IMAGE;
                break;
            case ChatEnum.EMessageType.VOICE:
                VoiceMessage voice = bean.getVoiceMessage();
                MsgBean.VoiceMessage.Builder voiceBuilder = MsgBean.VoiceMessage.newBuilder();
                voiceBuilder.setDuration(voice.getTime());
                voiceBuilder.setUrl(voice.getUrl());
                value = voiceBuilder.build();
                type = MsgBean.MessageType.VOICE;
                break;
        }

        saveMessage(bean);
        if (type != null && value != null) {
            MsgBean.UniversalMessage.Builder msg = toMsgBuilder(bean.getMsg_id(), bean.getTo_uid(), bean.getGid(), bean.getTimestamp(), type, value);
            //立即发送
            SocketUtil.getSocketUtil().sendData4Msg(msg);
        }

    }


    //消息被拒
    public static MsgAllBean createMsgBeanOfNotice(MsgBean.AckMessage ack, @ChatEnum.ENoticeType int type) {
        MsgAllBean bean = msgDao.getMsgById(ack.getMsgId(0));
        MsgAllBean msg = null;
        if (bean != null && TextUtils.isEmpty(bean.getGid())) {
            msg = new MsgAllBean();
            String msgId = SocketData.getUUID();
            msg.setMsg_id(msgId);
            msg.setMsg_type(ChatEnum.EMessageType.NOTICE);
            msg.setFrom_uid(bean.getFrom_uid());
            long time = getSysTime();
            if (ack.getTimestamp() >= time) {
                msg.setTimestamp(bean.getTimestamp() + 1);
            } else {
                msg.setTimestamp(time);
            }
            msg.setTo_uid(bean.getTo_uid());
            msg.setGid(bean.getGid());
            msg.setFrom_nickname(bean.getFrom_nickname());
            msg.setFrom_group_nickname(bean.getFrom_group_nickname());
            msg.setMsgNotice(createMsgNotice(msgId, type, getNoticeString(bean, type)));
        }
        return msg;
    }

    public static MsgNotice createMsgNotice(String msgId, @ChatEnum.ENoticeType int type, String content) {
        MsgNotice note = new MsgNotice();
        note.setMsgid(msgId);
        note.setMsgType(type);
        note.setNote(content);
        return note;
    }

    public static String getNoticeString(MsgAllBean bean, @ChatEnum.ENoticeType int type) {
        String note = "";
        if (bean != null) {
            switch (type) {
                case ChatEnum.ENoticeType.BLACK_ERROR:
                    note = "消息发送成功，但对方已拒收";
                    break;
                case ChatEnum.ENoticeType.NO_FRI_ERROR:
                    String name = "";
                    if (bean.getTo_user() != null) {
                        name = bean.getTo_user().getName4Show();
                    }
                    note = "你已不是" + "\"<font color='#276baa' id='" + bean.getTo_uid() + "'>" + name + "</font>\"" + "的好友, 请先" + "<font color='#276baa' id='" + bean.getTo_uid() + "'>" + "添加对方为好友" + "</font>";
                    break;
                case ChatEnum.ENoticeType.LOCK:
                    note = "聊天中所有信息已进行" + "<font color='#1f5305' tag=" + ChatEnum.ETagType.LOCK + ">" + "端对端加密" + "</font>" + "保护";
                    break;
            }
        }
        return note;
    }

    public static long getPreServerAckTime() {
        return preServerAckTime;
    }

    public static void setPreServerAckTime(long preServerAckTime) {
//        LogUtil.getLog().i(TAG, "时间戳--preServerAckTime=" + preServerAckTime);
        SocketData.preServerAckTime = preServerAckTime;
    }

    public static long getPreSendLocalTime() {
        return preSendLocalTime;
    }

    public static void setPreSendLocalTime(long preSendLocalTime) {
        SocketData.preSendLocalTime = preSendLocalTime;
    }

    //获取修正时间
    public static long getFixTime() {
        long currentTime = System.currentTimeMillis();
//        LogUtil.getLog().i(TAG, "时间戳--currentTime=" + currentTime + "--preServerAckTime=" + preServerAckTime + "--preSendLocalTime=" + preSendLocalTime);
        if (preServerAckTime > preSendLocalTime && preServerAckTime > currentTime) {//服务器回执时间最新
            currentTime = preServerAckTime + 1;
            preServerAckTime = currentTime;
        } else if (preSendLocalTime > preServerAckTime && preSendLocalTime > currentTime) {//本地发送时间最新
            currentTime = preSendLocalTime + 1;
            preSendLocalTime = currentTime;
        } else {//本地系统时间最新
            preSendLocalTime = currentTime;
        }
//        LogUtil.getLog().i(TAG, "时间戳--currentTime=" + currentTime);
        return currentTime;
    }

    public static long getSysTime() {
        return System.currentTimeMillis();
    }

    /*
     * 创建自己发送的消息bean
     * @param uid Long 用户Id,私聊即to_uid,群聊为null
     * @gid 群id，私聊为空，群聊不能为空
     * @msgType int 消息类型
     * @sendStatus int 发送状态
     * @obj IMsgContent MsgAllBean二级关联表bean
     * */
    public static MsgAllBean createMessageBean(Long uid, String gid, @ChatEnum.EMessageType int msgType, @ChatEnum.ESendStatus int sendStatus, long time, IMsgContent obj) {
        if (UserAction.getMyInfo() == null) {
            return null;
        }
        boolean isGroup = false;
        if (uid == null && !TextUtils.isEmpty(gid)) {
            isGroup = true;
        }

        MsgAllBean msg = new MsgAllBean();
        msg.setMsg_id(obj.getMsgId());
        msg.setMsg_type(msgType);
        msg.setTimestamp(time > 0 ? time : getFixTime());
        msg.setTo_uid(uid);
        msg.setGid(gid);
        msg.setSend_state(sendStatus);
        msg.setFrom_uid(UserAction.getMyId());
        msg.setFrom_avatar(UserAction.getMyInfo().getHead());
        msg.setFrom_nickname(UserAction.getMyInfo().getName());
        msg.setRead(true);//已读
        if (isGroup) {
            Group group = msgDao.getGroup4Id(gid);
            if (group != null) {
                String name = group.getMygroupName();
                if (StringUtil.isNotNull(name)) {
                    msg.setFrom_group_nickname(name);
                }
            }
        }
        switch (msgType) {
            case ChatEnum.EMessageType.NOTICE:
                if (obj instanceof MsgNotice) {
                    msg.setMsgNotice((MsgNotice) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.TEXT:
                if (obj instanceof ChatMessage) {
                    msg.setChat((ChatMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.STAMP:
                if (obj instanceof StampMessage) {
                    msg.setStamp((StampMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.RED_ENVELOPE:
                if (obj instanceof RedEnvelopeMessage) {
                    msg.setRed_envelope((RedEnvelopeMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.IMAGE:
                if (obj instanceof ImageMessage) {
                    msg.setImage((ImageMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.BUSINESS_CARD:
                if (obj instanceof BusinessCardMessage) {
                    msg.setBusiness_card((BusinessCardMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.TRANSFER:
                if (obj instanceof TransferMessage) {
                    msg.setTransfer((TransferMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.VOICE:
                if (obj instanceof VoiceMessage) {
                    msg.setVoiceMessage((VoiceMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.AT:
                if (obj instanceof AtMessage) {
                    msg.setAtMessage((AtMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.ASSISTANT:
                if (obj instanceof AssistantMessage) {
                    msg.setAssistantMessage((AssistantMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.MSG_CENCAL:
                if (obj instanceof MsgCancel) {
                    msg.setMsgCancel((MsgCancel) obj);
                } else {
                    return null;
                }
                break;

        }

        return msg;
    }

    /*
     * 创建接收到的消息bean
     * @param uid Long 用户Id,私聊即to_uid,群聊为null
     * @gid 群id，私聊为空，群聊不能为空
     * @msgType int 消息类型
     * @sendStatus int 发送状态
     * @obj IMsgContent MsgAllBean二级关联表bean
     * */
    public static MsgAllBean createMsgBean(MsgBean.UniversalMessage.WrapMessage wrap, @ChatEnum.EMessageType int msgType, @ChatEnum.ESendStatus int sendStatus, long time, IMsgContent obj) {
        if (wrap == null) {
            return null;
        }
        boolean isGroup = false;
        if (wrap.getFromUid() <= 0 && !TextUtils.isEmpty(wrap.getGid())) {
            isGroup = true;
        }

        MsgAllBean msg = new MsgAllBean();
        msg.setMsg_id(obj.getMsgId());
        msg.setMsg_type(msgType);
        msg.setTimestamp(time > 0 ? time : getFixTime());
        msg.setFrom_uid(wrap.getFromUid());
        msg.setFrom_avatar(wrap.getAvatar());
        msg.setFrom_nickname(wrap.getNickname());
        msg.setFrom_group_nickname(wrap.getMembername());
        msg.setGid(wrap.getGid());
        msg.setSend_state(sendStatus);
        msg.setRead(false);
        if (isGroup) {
            Group group = msgDao.getGroup4Id(wrap.getGid());
            if (group != null) {
                String name = group.getMygroupName();
                if (StringUtil.isNotNull(name)) {
                    msg.setFrom_group_nickname(name);
                }
            }
        }
        switch (msgType) {
            case ChatEnum.EMessageType.NOTICE:
                if (obj instanceof MsgNotice) {
                    msg.setMsgNotice((MsgNotice) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.TEXT:
                if (obj instanceof ChatMessage) {
                    msg.setChat((ChatMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.STAMP:
                if (obj instanceof StampMessage) {
                    msg.setStamp((StampMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.RED_ENVELOPE:
                if (obj instanceof RedEnvelopeMessage) {
                    msg.setRed_envelope((RedEnvelopeMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.IMAGE:
                if (obj instanceof ImageMessage) {
                    msg.setImage((ImageMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.BUSINESS_CARD:
                if (obj instanceof BusinessCardMessage) {
                    msg.setBusiness_card((BusinessCardMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.TRANSFER:
                if (obj instanceof TransferMessage) {
                    msg.setTransfer((TransferMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.VOICE:
                if (obj instanceof VoiceMessage) {
                    msg.setVoiceMessage((VoiceMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.AT:
                if (obj instanceof AtMessage) {
                    msg.setAtMessage((AtMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.ASSISTANT:
                if (obj instanceof AssistantMessage) {
                    msg.setAssistantMessage((AssistantMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.MSG_CENCAL:
                if (obj instanceof MsgCancel) {
                    msg.setMsgCancel((MsgCancel) obj);
                } else {
                    return null;
                }
                break;

        }

        return msg;
    }


    public static AtMessage createAtMessage(String msgId, String content, @ChatEnum.EAtType int atType) {
        AtMessage message = new AtMessage();
        message.setMsgId(msgId);
        message.setAt_type(atType);
        message.setMsg(content);
        return message;
    }

    public static ChatMessage createChatMessage(String msgId, String content) {
        ChatMessage message = new ChatMessage();
        message.setMsgid(msgId);
        message.setMsg(content);
        return message;
    }

    public static void saveMessage(MsgAllBean bean) {
        DaoUtil.update(bean);
        if (msgDao == null) {
            msgDao = new MsgDao();
        }
        msgDao.sessionCreate(bean.getGid(), bean.getTo_uid());
        MessageManager.getInstance().setMessageChange(true);
    }

    public static MsgAllBean createMessageLock(String gid, Long uid) {
        MsgAllBean bean = new MsgAllBean();
        if (!TextUtils.isEmpty(gid)) {
            bean.setGid(gid);
            bean.setFrom_uid(UserAction.getMyInfo().getUid());
        } else if (uid != null) {
            bean.setFrom_uid(uid);
        } else {
            return null;
        }
        bean.setMsg_type(ChatEnum.EMessageType.LOCK);
        bean.setMsg_id(SocketData.getUUID());
        bean.setTimestamp(0L);
        ChatMessage message = SocketData.createChatMessage(bean.getMsg_id(), getNoticeString(bean, ChatEnum.ENoticeType.LOCK));
        bean.setChat(message);
        return bean;
    }

}
