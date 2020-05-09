package com.yanlong.im.utils.socket;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hm.cxpay.global.PayEnum;
import com.yalantis.ucrop.util.FileUtils;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.AssistantMessage;
import com.yanlong.im.chat.bean.AtMessage;
import com.yanlong.im.chat.bean.BusinessCardMessage;
import com.yanlong.im.chat.bean.ChatMessage;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.IMsgContent;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.LocationMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgCancel;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.chat.bean.MsgNotice;
import com.yanlong.im.chat.bean.P2PAuVideoMessage;
import com.yanlong.im.chat.bean.QuotedMessage;
import com.yanlong.im.chat.bean.ReadMessage;
import com.yanlong.im.chat.bean.RedEnvelopeMessage;
import com.yanlong.im.chat.bean.ReplyMessage;
import com.yanlong.im.chat.bean.SendFileMessage;
import com.yanlong.im.chat.bean.ShippedExpressionMessage;
import com.yanlong.im.chat.bean.StampMessage;
import com.yanlong.im.chat.bean.TransferMessage;
import com.yanlong.im.chat.bean.TransferNoticeMessage;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.bean.WebMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.utils.DeviceUtils;
import net.cb.cb.library.utils.ImgSizeUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.RealmList;

public class SocketData {
    private static final String TAG = "SocketData";

    private static long preServerAckTime;//前一个服务器回执时间
    private static long preSendLocalTime;//前一个本地消息发送的时间
    private static int offlineCount = -1;//一批离线消息总数
    private static MsgDao msgDao = new MsgDao();
    public static long CLL_ASSITANCE_ID = 1L;//常信小助手id
    private static String fileLocalUrl = "";//文件消息本地路径


    /***
     * 处理一些统一的数据,用于发送消息时获取
     * @return
     */
    public static MsgBean.UniversalMessage.Builder getMsgBuild(String requestId) {
        MsgBean.UniversalMessage.Builder msg = MsgBean.UniversalMessage.newBuilder();
        MsgBean.UniversalMessage.WrapMessage.Builder wp = MsgBean.UniversalMessage.WrapMessage.newBuilder();
        if (TextUtils.isEmpty(requestId)) {
            msg.setRequestId("" + getSysTime());
        } else {
            msg.setRequestId(requestId);
        }
        msg.addWrapMsg(0, wp.build());

        return msg;
    }

    /***
     * 授权
     * @return
     */
    public static byte[] msg4Auth() {

        TokenBean tokenBean = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).get4Json(TokenBean.class);
        if (tokenBean == null || !StringUtil.isNotNull(tokenBean.getAccessToken())) {
            return null;
        }
        LogUtil.getLog().i("tag", ">>>>发送token" + tokenBean.getAccessToken());
        MsgBean.AuthRequestMessage auth = MsgBean.AuthRequestMessage.newBuilder()
                .setAccessToken(tokenBean.getAccessToken()).build();
        return SocketPacket.getPackage(SocketPacket.DataType.AUTH, auth.toByteArray());
    }

    /***
     * 回执,可以不发送msgId
     * @param from 0 在线消息， 1离线消息
     * @param latest 是否需要最新消息
     * @return
     */
    public static byte[] msg4ACK(String rid, List<String> msgids, int from, boolean latest, boolean isEnd) {
        LogUtil.getLog().i(TAG, "发送ACK====" + "--from=" + from + "--latest=" + latest + "--isEnd=" + isEnd);
        MsgBean.AckMessage ack;
        MsgBean.AckMessage.Builder amsg = MsgBean.AckMessage.newBuilder().setRequestId(rid);
        if (msgids != null) {
            for (int i = 0; i < msgids.size(); i++) {
                amsg.addMsgId(msgids.get(i));
            }
        }
        //离线消息，回执需要添加下一次需要多少数据
        if (from == 1) {
            LogUtil.getLog().i(TAG, "--请求离线--历史");
            if (isEnd) {
                MsgBean.OfflineMsgRequest.Builder request = MsgBean.OfflineMsgRequest.newBuilder();
                request.setReqCount(-1);
                request.setLatest(false);
                amsg.setMergedNextReq(request);
            } else {
                MsgBean.OfflineMsgRequest.Builder request = MsgBean.OfflineMsgRequest.newBuilder();
                request.setReqCount(offlineCount);
                request.setLatest(false);
                amsg.setMergedNextReq(request);
            }
        }
        ack = amsg.build();
        //添加到消息队中监听
        SendList.addSendList(ack.getRequestId(), amsg);
        return SocketPacket.getPackage(SocketPacket.DataType.ACK, ack.toByteArray());

    }

    /***
     * 消息转换
     * @param data
     * @return
     */
    public static MsgBean.UniversalMessage msgConversion(byte[] data) {
        try {
            MsgBean.UniversalMessage msg = MsgBean.UniversalMessage.parseFrom(SocketPacket.bytesToLists(data, 10).get(1));
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
            MsgBean.AckMessage msg = MsgBean.AckMessage.parseFrom(SocketPacket.bytesToLists(data, 10).get(1));
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
            MsgBean.AuthResponseMessage ruthmsg = MsgBean.AuthResponseMessage.parseFrom(SocketPacket.bytesToLists(data, 10).get(1));
            return ruthmsg;
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    //6.6 为后端擦屁股
    public static CopyOnWriteArrayList<String> oldMsgId = new CopyOnWriteArrayList<>();


    /***
     * 在服务器接收到自己发送的消息后,本地保存
     * @param bean
     */
    public static void msgSave4Me(MsgBean.AckMessage bean) {
        //普通消息
        MsgBean.UniversalMessage.Builder msg = SendList.findMsgById(bean.getRequestId());
        //6.25 排除通知存库
        //6.25 移除重发列队
        SendList.removeSendListJust(bean.getRequestId());

        if (msg != null && msgSendSave4filter(msg.getWrapMsg(0).toBuilder())) {
            //存库处理
            MsgBean.UniversalMessage.WrapMessage wmsg = msg.getWrapMsgBuilder(0)
                    .setMsgId(bean.getMsgIdList().get(0))
                    //时间要和ack一起返回
                    .setTimestamp(getSysTime())
                    .build();
            //  LogUtil.getLog().d(TAG, "msgSave4Me2: msg" + msg.toString());

            MsgAllBean msgAllBean = MsgConversionBean.ToBean(wmsg, msg, false);
            if (msgAllBean == null) {
                return;
            }
            msgAllBean.setRead(true);//自己发送的消息是已读
            msgAllBean.setMsg_id(msgAllBean.getMsg_id());
            msgAllBean.setTimestamp(bean.getTimestamp());

            //7.16 如果是收到先自己发图图片的消息

            //移除旧消息
            DaoUtil.deleteOne(MsgAllBean.class, "request_id", msgAllBean.getRequest_id());

            if (msgAllBean.getVideoMessage() != null) {
                msgAllBean.getVideoMessage().setLocalUrl(videoLocalUrl);
            }
            //文件消息，保存本地路径 （这里收到回执后回再次本地保存消息，发送给后端的文件消息和SendList不含有localUrl字段，会覆盖掉数据，所以客户端这里自行补上）
            if (msgAllBean.getSendFileMessage() != null) {
                msgAllBean.getSendFileMessage().setLocalPath(fileLocalUrl);
            }
            //收到直接存表,创建会话
            DaoUtil.update(msgAllBean);
            MsgDao msgDao = new MsgDao();

            msgDao.sessionCreate(msgAllBean.getGid(), msgAllBean.getTo_uid());
            MessageManager.getInstance().setMessageChange(true);

        }
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
            LogUtil.getLog().d(TAG, "msgSave4Me1: msg" + msg.toString());
            MsgAllBean msgAllBean = MsgConversionBean.ToBean(wmsg, msg, false);
            msgAllBean.setMsg_id(msgAllBean.getMsg_id());
            //时间戳
            // msgAllBean.setTimestamp(bean.getTimestamp());
            //是发送给群助手的消息直接发送成功
            if (isNoAssistant(msgAllBean.getTo_uid(), msgAllBean.getGid())) {
                msgAllBean.setSend_state(state);
            }
            msgAllBean.setSend_data(msg.build().toByteArray());

            //移除旧消息// 7.16 通过msgid 判断唯一
            DaoUtil.deleteOne(MsgAllBean.class, "request_id", msgAllBean.getRequest_id());
            if (msgAllBean.getVideoMessage() != null) {
                msgAllBean.getVideoMessage().setLocalUrl(videoLocalUrl);
            }
            // 撤消内容 与内容类型写入数据库
//            if (msgAllBean.getMsgCancel() != null) {
//                msgAllBean.getMsgCancel().setCancelContent(mCancelContent);
//                msgAllBean.getMsgCancel().setCancelContentType(mCancelContentType);
//            }
            //文件消息，保存本地路径
            if (msgAllBean.getSendFileMessage() != null) {
                msgAllBean.getSendFileMessage().setLocalPath(fileLocalUrl);
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
        //移除重发列队
        SendList.removeSendListJust(bean.getRequestId());
        if (msg != null && msgSendSave4filter(msg.getWrapMsg(0).toBuilder())) {
            //存库处理
            MsgBean.UniversalMessage.WrapMessage wmsg = msg.getWrapMsgBuilder(0)
                    .setMsgId(bean.getMsgIdList().get(0))
                    //时间要和ack一起返回
//                    .setTimestamp(getSysTime())
                    .build();
            MsgAllBean msgAllBean = MsgConversionBean.ToBean(wmsg, msg, true);
            if (msgAllBean == null) {
                return;
            }
            msgAllBean.setMsg_id(msgAllBean.getMsg_id());
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
     * 保存并发送消息
     * @param toId
     * @param toGid
     * @param type
     * @param value
     * @return
     */
    private static MsgAllBean send4Base(boolean isSave, Long toId, String toGid, MsgBean.MessageType type, Object value) {
        return send4Base(isSave, true, null, toId, toGid, -1, type, value);
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

    /*
     * @time time > 0
     * */
    private static MsgAllBean send4Base(boolean isSave, boolean isSend, String msgId, Long toId, String toGid, long time, MsgBean.MessageType type, Object value) {
        LogUtil.getLog().i(TAG, ">>>---发送到toid" + toId + "--gid" + toGid);
        MsgBean.UniversalMessage.Builder msg = toMsgBuilder("", msgId, toId, toGid, time > 0 ? time : getFixTime(), type, value);

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
    private static MsgBean.UniversalMessage.Builder toMsgBuilder(String requestId, String msgId, Long toId, String toGid, long time, MsgBean.MessageType type, Object value) {
        MsgBean.UniversalMessage.Builder msg = SocketData.getMsgBuild(requestId);
        MsgBean.UniversalMessage.WrapMessage.Builder wrap = msg.getWrapMsgBuilder(0);
        IUser userInfo = UserAction.getMyInfo();
        if (userInfo == null) {
            return null;
        }
        //是否是发送给文件传输助手
        if (toId != null && toId.longValue() == -userInfo.getUid()) {
            toId = userInfo.getUid().longValue();
        }
        initWrapMessage(msgId, userInfo.getUid(), userInfo.getHead(), userInfo.getName(), toId, toGid, time, type, value, wrap);
        if (wrap == null) {
            return null;
        }
        MsgBean.UniversalMessage.WrapMessage wm = wrap.build();
        msg.setWrapMsg(0, wm);
        return msg;
    }

    private static void initWrapMessage(String msgId, Long fromId, String avatar, String nickName, Long toId, String toGid, long time, MsgBean.MessageType type, Object value, MsgBean.UniversalMessage.WrapMessage.Builder wrap) {
        wrap.setFromUid(fromId);
        wrap.setAvatar(avatar);
        wrap.setNickname(nickName);
        //自动生成uuid
        wrap.setMsgId(msgId == null ? getUUID() : msgId);
        //添加阅后即焚状态
        int survivalTime = new UserDao().getReadDestroy(toId, toGid);
        wrap.setSurvivalTime(survivalTime);
        wrap.setTimestamp(time);
        if (toId != null && toId > 0) {//给个人发
//            msg.setToUid(toId);
            wrap.setToUid(toId);
        }
        if (toGid != null && toGid.length() > 0) {//给群发
            wrap.setGid(toGid);
            Group group = msgDao.getGroup4Id(toGid);
            if (group != null) {
                String name = group.getMygroupName();
                if (StringUtil.isNotNull(name)) {
                    wrap.setMembername(name);
                }
            }
        }

        wrap.setMsgType(type);
        switch (type) {
            case CHAT:
                wrap.setChat((MsgBean.ChatMessage) value);
                break;
            case IMAGE:
                wrap.setImage((MsgBean.ImageMessage) value);
                break;
            case RED_ENVELOPER:
                wrap.setRedEnvelope((MsgBean.RedEnvelopeMessage) value);
                break;
            case RECEIVE_RED_ENVELOPER:
                wrap.setReceiveRedEnvelope((MsgBean.ReceiveRedEnvelopeMessage) value);
                break;
            case TRANSFER:
                wrap.setTransfer((MsgBean.TransferMessage) value);
                break;
            case STAMP:
                wrap.setStamp((MsgBean.StampMessage) value);
                break;
            case BUSINESS_CARD:
                wrap.setBusinessCard((MsgBean.BusinessCardMessage) value);
                break;
            case ACCEPT_BE_FRIENDS:
                wrap.setAcceptBeFriends((MsgBean.AcceptBeFriendsMessage) value);
                break;
            case REQUEST_FRIEND:
                wrap.setRequestFriend((MsgBean.RequestFriendMessage) value);
                break;
            case VOICE:
                wrap.setVoice((MsgBean.VoiceMessage) value);
                break;
            case AT:
                wrap.setAt((MsgBean.AtMessage) value);
                break;
            case CANCEL:
                wrap.setCancel((MsgBean.CancelMessage) value);
                break;
            case SHORT_VIDEO:
                wrap.setShortVideo((MsgBean.ShortVideoMessage) value);
                break;
            case P2P_AU_VIDEO:
                wrap.setP2PAuVideo((MsgBean.P2PAuVideoMessage) value);
                break;
            case P2P_AU_VIDEO_DIAL:
                wrap.setP2PAuVideoDial((MsgBean.P2PAuVideoDialMessage) value);
                break;
            case READ:
                wrap.setRead((MsgBean.ReadMessage) value);
                break;
            case SNAPSHOT_LOCATION://位置
                wrap.setSnapshotLocation((MsgBean.SnapshotLocationMessage) value);
                break;
            case SHIPPED_EXPRESSION:
                wrap.setShippedExpression((MsgBean.ShippedExpressionMessage) value);
                break;
            case TAKE_SCREENSHOT://截屏
                wrap.setTakeScrennshot((MsgBean.TakeScreenshotMessage) value);
                break;
            case SEND_FILE://文件
                wrap.setSendFile((MsgBean.SendFileMessage) value);
                break;
            case REPLY://消息回复
                wrap.setReply((MsgBean.ReplyMessage) value);
                break;
            case UNRECOGNIZED:
                break;

        }
    }

    /***
     * 忽略存库的消息
     * @return false 需要忽略
     */
    private static boolean msgSendSave4filter(MsgBean.UniversalMessage.WrapMessage.Builder wmsg) {
        if (wmsg.getMsgType() == MsgBean.MessageType.RECEIVE_RED_ENVELOPER || wmsg.getMsgType() == MsgBean.MessageType.P2P_AU_VIDEO_DIAL
                || wmsg.getMsgType() == MsgBean.MessageType.TAKE_SCREENSHOT || wmsg.getMsgType() == MsgBean.MessageType.READ) {
            return false;
        }
        return true;
    }

    /***
     * 过滤不需要存储消息
     * @return false 需要忽略
     */
    private static boolean filterNoSaveMsg(@ChatEnum.EMessageType int msgType) {
        if (msgType == ChatEnum.EMessageType.MSG_VOICE_VIDEO_NOTICE || msgType == ChatEnum.EMessageType.READ) {
            return false;
        }
        return true;
    }

    /*
     * 过滤发送失败后不需要存储消息
     *
     * */
    public static boolean filterNoSaveMsgForFailed(@ChatEnum.EMessageType int msgType) {
        if (msgType == ChatEnum.EMessageType.MSG_VOICE_VIDEO_NOTICE || msgType == ChatEnum.EMessageType.READ
                || msgType == ChatEnum.EMessageType.UNRECOGNIZED || msgType == ChatEnum.EMessageType.MSG_CANCEL) {
            return false;
        }
        return true;
    }


    public static P2PAuVideoMessage createCallMessage(String msgId, int auType, String option, String desc) {
        P2PAuVideoMessage message = new P2PAuVideoMessage();
        message.setMsgId(msgId);
        message.setAv_type(auType);
        message.setOperation(option);
        message.setDesc(desc);
        return message;
    }

    /**
     * 发送一条音视频通知
     *
     * @param toId
     * @param toGid
     * @param auVideoType 语音、视频
     * @return
     */
    public static MsgAllBean send4VoiceOrVideoNotice(Long toId, String toGid, MsgBean.AuVideoType auVideoType) {
        MsgBean.P2PAuVideoDialMessage chat = MsgBean.P2PAuVideoDialMessage.newBuilder()
                .setAvType(auVideoType)
                .build();

        return send4Base(false, toId, toGid, MsgBean.MessageType.P2P_AU_VIDEO_DIAL, chat);

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
     * @param url
     * @return
     */
    private static String videoLocalUrl = null;


    public static MsgAllBean send4Image(Long toId, String toGid, String url, ImgSizeUtil.ImageSize imgSize, long time) {

        return send4Image(getUUID(), toId, toGid, url, false, imgSize, time);
    }


    //预发送需文件（图片，语音）上传消息,保存消息及更新session
    public static <T> MsgAllBean sendFileUploadMessagePre(String msgId, Long toId, String toGid, long time, T t, @ChatEnum.EMessageType int type) {
        //前保存
        MsgAllBean msgAllBean = new MsgAllBean();
        msgAllBean.setMsg_id(msgId);
        IUser mInfo = UserAction.getMyInfo();
        msgAllBean.setFrom_uid(mInfo.getUid());
        msgAllBean.setFrom_avatar(mInfo.getHead());
        msgAllBean.setFrom_nickname(mInfo.getName());
        msgAllBean.setRequest_id(getSysTime() + "");
        msgAllBean.setTimestamp(time);
        msgAllBean.setMsg_type(type);

        int survivalTime = new UserDao().getReadDestroy(toId, toGid);
        msgAllBean.setSurvival_time(survivalTime);

        msgAllBean.setRead(true);//自己发送时已读的
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
            case ChatEnum.EMessageType.FILE:
                SendFileMessage file = (SendFileMessage) t;
                msgAllBean.setSendFileMessage(file);
                break;
        }

        msgAllBean.setTo_uid(toId);
        msgAllBean.setGid(toGid == null ? "" : toGid);
        msgAllBean.setSend_state(ChatEnum.ESendStatus.PRE_SEND);
        DaoUtil.update(msgAllBean);
        msgDao.sessionCreate(msgAllBean.getGid(), msgAllBean.getTo_uid());
        MessageManager.getInstance().setMessageChange(true);
        return msgAllBean;
    }

    public static VoiceMessage createVoiceMessage(String msgId, String url, int duration) {
        VoiceMessage message = new VoiceMessage();
        message.setPlayStatus(ChatEnum.EPlayStatus.NO_PLAY);
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
        image.setSize(img.getSize());
        if (isOriginal) {
            image.setOrigin(url);
        }
        return image;
    }

    @NonNull
    public static ImageMessage createImageMessage(String msgId, String url, String previewUrl, String thumUrl, long width, long height, boolean isOriginal, boolean isOriginRead, long size) {
        ImageMessage image = new ImageMessage();
        image.setLocalimg(url);
        image.setPreview(previewUrl);
        image.setThumbnail(thumUrl);
        image.setMsgid(msgId);
        image.setWidth(width);
        image.setHeight(height);
        image.setSize(size);
        if (isOriginal) {
            image.setOrigin(url);
        }
        image.setReadOrigin(isOriginRead);
        return image;
    }

    @NonNull
    public static ImageMessage createImageMessage(String msgId, String local, String originUrl, long width, long height, boolean isOriginal, boolean isOriginRead, long size) {
        ImageMessage image = new ImageMessage();
        String extTh = "/below-20k";
        String extPv = "/below-200k";
        boolean isGif = FileUtils.isGif(originUrl);
        image.setLocalimg(local);
        //gif未被压缩
        if (!TextUtils.isEmpty(originUrl)) {
            if (isGif) {
                image.setPreview(originUrl);
                image.setThumbnail(originUrl);
            } else {
                image.setPreview(originUrl + extPv);
                image.setThumbnail(originUrl + extTh);
            }
            if (isOriginal) {
                image.setOrigin(originUrl);
            }
        }
        image.setMsgid(msgId);
        image.setWidth(width);
        image.setHeight(height);
        image.setSize(size);
        image.setReadOrigin(isOriginRead);
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
     * 创建消息
     *
     * @param msgId       消息id
     * @param filePath    文件路径
     * @param url         文件上传后的url
     * @param fileName    文件名
     * @param size        文件大小
     * @param format      文件后缀
     * @param isFromOther 是否为转发
     * @return
     */
    @NonNull
    public static SendFileMessage createFileMessage(String msgId, String filePath, String url, String fileName, long size, String format, boolean isFromOther) {
        SendFileMessage fileMessage = new SendFileMessage();
        fileMessage.setMsgId(msgId);
        fileMessage.setLocalPath(filePath);
        fileMessage.setFile_name(fileName);
        fileMessage.setSize(size);
        fileMessage.setFormat(format);
        fileMessage.setUrl(url);//默认url为空，上传成功后拥有下载地址
        fileMessage.setFromOther(isFromOther);
        return fileMessage;
    }


    /***
     * 收红包
     * @param toId
     * @param toGid
     * @param rid
     * @return
     */
    public static MsgAllBean send4RbRev(Long toId, String toGid, String rid, int reType) {
        msgDao.redEnvelopeOpen(rid, PayEnum.EEnvelopeStatus.RECEIVED, reType, "");
        MsgBean.ReceiveRedEnvelopeMessage msg = MsgBean.ReceiveRedEnvelopeMessage.newBuilder()
                .setId(rid)
                .build();

        if (toId.longValue() == UserAction.getMyId().longValue()) {//自己的不发红包通知,只保存
            MsgBean.UniversalMessage.Builder umsg = toMsgBuilder("", null, toId, toGid, getFixTime(), MsgBean.MessageType.RECEIVE_RED_ENVELOPER, msg);
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


    public static MsgCancel createCancelMsg(MsgAllBean cancelMsg) {
        if (cancelMsg == null) {
            return null;
        }
        String msg = "";
        Integer msgType = 0;
        if (cancelMsg.getChat() != null) {
            msg = cancelMsg.getChat().getMsg();
        } else if (cancelMsg.getAtMessage() != null) {
            msg = cancelMsg.getAtMessage().getMsg();
        }
        msgType = cancelMsg.getMsg_type();
        MsgCancel cancel = new MsgCancel();
        cancel.setMsgid(SocketData.getUUID());
        cancel.setNote("你撤回了一条消息");
        cancel.setCancelContent(msg);
        cancel.setCancelContentType(msgType);
        cancel.setMsgidCancel(cancelMsg.getMsg_id());
        cancel.setTime(cancelMsg.getTimestamp());
        return cancel;
    }
    /*
     * 发送及保存消息
     * */

    public static void sendAndSaveMessage(MsgAllBean bean) {
        LogUtil.getLog().i(TAG, ">>>---发送到toid" + bean.getTo_uid() + "--gid" + bean.getGid());
        sendAndSaveMessage(bean, true);
    }

    /**
     * 常信小助手发的消息不需要上会服务器，待完善
     *
     * @param bean
     * @param isSend 是否需要发送服务器
     */
    public static void sendAndSaveMessage(MsgAllBean bean, boolean isSend) {
        if (TextUtils.isEmpty(bean.getRequest_id())) {
            bean.setRequest_id(getUUID());
        }
        int msgType = bean.getMsg_type();
        InitMsgContentAndType initMsgContentAndType = new InitMsgContentAndType(bean, msgType).invoke();
        MsgBean.MessageType type = initMsgContentAndType.getType();
        Object value = initMsgContentAndType.getValue();
        boolean needSave = initMsgContentAndType.isNeedSave();
        //已读消息，撤销消息不需要保存
        if (needSave) {
            saveMessage(bean);
        }
        if (SocketUtil.getSocketUtil().getOnLineState()) {
            if (type != null && value != null && isSend) {
                SendList.addMsgToSendSequence(bean.getRequest_id(), bean);//添加到发送队列
                MsgBean.UniversalMessage.Builder msg = toMsgBuilder(bean.getRequest_id(), bean.getMsg_id(), bean.getTo_uid(), bean.getGid(), bean.getTimestamp(), type, value);
                SocketUtil.getSocketUtil().sendData4Msg(msg);
            }
        }
    }

    //自己邀请进群消息
    public static void createMsgGroupOfNotice(String gid, List<UserInfo> listDataTop) {
        MsgAllBean msg = new MsgAllBean();
        String msgId = SocketData.getUUID();
        msg.setMsg_id(msgId);
        msg.setMsg_type(ChatEnum.EMessageType.NOTICE);
        msg.setFrom_uid(UserAction.getMyId());
        msg.setTimestamp(System.currentTimeMillis());

        int survivalTime = new UserDao().getReadDestroy(null, gid);
        msg.setSurvival_time(survivalTime);
        msg.setRead(1);

        msg.setTo_uid(UserAction.getMyId());
        msg.setGid(gid);
        msg.setFrom_nickname(UserAction.getMyInfo().getName());
//        msg.setFrom_group_nickname(bean.getFrom_group_nickname());
        MsgNotice note = new MsgNotice();
        note.setMsgid(msgId);
        note.setMsgType(ChatEnum.ENoticeType.INVITED);
        String inviterName = "<font value ='3'>你</font>";
        String names = "";
        for (UserInfo user : listDataTop) {
            names += "\"<font id='" + user.getUid() + "' value ='2'>" + user.getName() + "</font>\"、";
        }
        //去掉最后一个逗号
        names = names.length() > 0 ? names.substring(0, names.length() - 1) : names;
        String node = inviterName + "邀请" + names + "加入了群聊" + "<div id='" + gid + "'></div>";
        note.setNote(node);
        msg.setMsgNotice(note);
        msg.setIsLocal(1);
        DaoUtil.save(msg);
    }

    //消息被拒
    public static MsgAllBean createMsgBeanOfNotice(MsgBean.AckMessage ack, MsgAllBean msgAllBean, @ChatEnum.ENoticeType int type) {
        MsgAllBean bean = msgAllBean;
        if (bean == null) {
            bean = msgDao.getMsgById(ack.getMsgId(0));
        }
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

            if (type == ChatEnum.ENoticeType.BLACK_ERROR) {
                int survivalTime = new UserDao().getReadDestroy(bean.getTo_uid(), null);
                msg.setSurvival_time(survivalTime);
                msg.setRead(1);
            }
            msg.setTo_uid(bean.getTo_uid());
            msg.setGid(bean.getGid());
            msg.setFrom_nickname(bean.getFrom_nickname());
            msg.setFrom_group_nickname(bean.getFrom_group_nickname());
            msg.setMsgNotice(createMsgNotice(msgId, type, getNoticeString(bean, type)));
            msg.setIsLocal(1);
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


    public static MsgNotice createMsgNoticeOfRb(String msgId, Long uid, String gid, String rid) {
        MsgNotice note = new MsgNotice();
        note.setMsgid(msgId);
        if (uid != null && uid.longValue() == UserAction.getMyId().longValue()) {
            note.setMsgType(ChatEnum.ENoticeType.SYS_ENVELOPE_RECEIVED_SELF);
            note.setNote("你领取了自己的<envelope id=" + rid + ">零钱红包</envelope>");
        } else {
            note.setMsgType(ChatEnum.ENoticeType.RECEIVE_SYS_ENVELOPE);
            String name = msgDao.getUsername4Show(gid, uid);
            String user = "<user id='" + uid + "' gid= " + gid + ">" + name + "</user>";
            note.setNote("你领取了\"" + user + "\"的" + "<envelope id=" + rid + ">零钱红包</envelope>");
        }
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
        if (preServerAckTime > preSendLocalTime && preServerAckTime > currentTime) {//服务器回执时间最新
            currentTime = preServerAckTime + 1;
            preServerAckTime = currentTime;
        } else if (preSendLocalTime > preServerAckTime && preSendLocalTime > currentTime) {//本地发送时间最新
            currentTime = preSendLocalTime + 1;
            preSendLocalTime = currentTime;
        } else {//本地系统时间最新
            preSendLocalTime = currentTime;
        }
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
        msg.setTimestamp(time > 0 ? time : getFixTime());//撤回消息时间需要以原消息时间为准
        msg.setTo_uid(uid);
        msg.setGid(gid);
        msg.setSend_state(sendStatus);
        msg.setFrom_uid(UserAction.getMyId());
        msg.setFrom_avatar(UserAction.getMyInfo().getHead());
        msg.setFrom_nickname(UserAction.getMyInfo().getName());
        int survivaltime = new UserDao().getReadDestroy(uid, gid);
        msg.setSurvival_time(survivaltime);
        //是否是本地构建的消息
        if (msgType != ChatEnum.EMessageType.NOTICE) {
            msg.setIsLocal(0);
        } else {
            msg.setIsLocal(1);
        }
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
            case ChatEnum.EMessageType.MSG_CANCEL:
                if (obj instanceof MsgCancel) {
                    MsgCancel cancel = (MsgCancel) obj;
                    msg.setMsgCancel(cancel);
                    if (cancel.getTime() > 0) {
                        msg.setTimestamp(cancel.getTime());
                    }
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.MSG_VIDEO:
                if (obj instanceof VideoMessage) {
                    msg.setVideoMessage((VideoMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.LOCATION:
                if (obj instanceof LocationMessage) {
                    msg.setLocationMessage((LocationMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.SHIPPED_EXPRESSION:
                if (obj instanceof ShippedExpressionMessage) {
                    msg.setShippedExpressionMessage((ShippedExpressionMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.FILE:
                if (obj instanceof SendFileMessage) {
                    msg.setSendFileMessage((SendFileMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.WEB:
                if (obj instanceof WebMessage) {
                    msg.setWebMessage((WebMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.TRANSFER_NOTICE:
                if (obj instanceof TransferNoticeMessage) {
                    msg.setTransferNoticeMessage((TransferNoticeMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.MSG_VOICE_VIDEO:
                if (obj instanceof P2PAuVideoMessage) {
                    msg.setP2PAuVideoMessage((P2PAuVideoMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.READ:
                if (obj instanceof ReadMessage) {
                    msg.setReadMessage((ReadMessage) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.REPLY:
                if (obj instanceof ReplyMessage) {
                    msg.setReplyMessage((ReplyMessage) obj);
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
        int survivaltime = new UserDao().getReadDestroy(wrap.getFromUid(), wrap.getGid());
        msg.setSurvival_time(survivaltime);
        //是否是本地构建的消息
        if (msgType != ChatEnum.EMessageType.NOTICE) {
            msg.setIsLocal(0);
        } else {
            msg.setIsLocal(1);
        }
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
            case ChatEnum.EMessageType.MSG_CANCEL:
                if (obj instanceof MsgCancel) {
                    msg.setMsgCancel((MsgCancel) obj);
                } else {
                    return null;
                }
                break;
            case ChatEnum.EMessageType.FILE:
                if (obj instanceof SendFileMessage) {
                    msg.setSendFileMessage((SendFileMessage) obj);
                } else {
                    return null;
                }
                break;

        }

        return msg;
    }

    //@消息
    public static AtMessage createAtMessage(String msgId, String content, @ChatEnum.EAtType int atType, List<Long> userIds) {
        AtMessage message = new AtMessage();
        message.setMsgId(msgId);
        message.setAt_type(atType);
        message.setMsg(content);
        if (userIds != null) {
            RealmList<Long> realmList = new RealmList<>();
            realmList.addAll(userIds);
            message.setUid(realmList);
        }
        return message;
    }

    //文本消息
    public static ChatMessage createChatMessage(String msgId, String content) {
        ChatMessage message = new ChatMessage();
        message.setMsgid(msgId);
        message.setMsg(content);
        return message;
    }

    //戳一戳消息
    public static StampMessage createStampMessage(String msgId, String content) {
        StampMessage message = new StampMessage();
        message.setMsgid(msgId);
        message.setComment(content);
        return message;
    }

    // 动画表情消息
    public static ShippedExpressionMessage createFaceMessage(String msgId, String id) {
        ShippedExpressionMessage message = new ShippedExpressionMessage();
        message.setMsgid(msgId);
        message.setId(id);
        return message;
    }

    //名片消息
    public static BusinessCardMessage createCardMessage(String msgId, String avatar, String nick, String info, long uid) {
        BusinessCardMessage message = new BusinessCardMessage();
        message.setMsgid(msgId);
        message.setUid(uid);
        message.setAvatar(avatar);
        message.setNickname(nick);
        message.setComment(info);
        return message;
    }

    //分享web或游戏消息
    public static WebMessage createWebMessage(String msgId, String appName, String iconUrl, String title, String description, String webUrl) {
        WebMessage message = new WebMessage();
        message.setMsgId(msgId);
        message.setAppName(appName);
        message.setIconUrl(iconUrl);
        message.setTitle(title);
        message.setDescription(description);
        message.setWebUrl(webUrl);
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

    //端到端加密消息
    public static MsgAllBean createMessageLock(String gid, Long uid) {
        MsgAllBean bean = null;
        try {
            if(UserAction.getMyInfo()!=null) {
                bean = new MsgAllBean();
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
                bean.setIsLocal(1);
                bean.setTimestamp(0L);
                ChatMessage message = SocketData.createChatMessage(bean.getMsg_id(), getNoticeString(bean, ChatEnum.ENoticeType.LOCK));
                bean.setChat(message);
            }
        }catch (Exception e){
            bean = null;
        }
        return bean;
    }

    //小视频消息
    @NonNull
    public static VideoMessage createVideoMessage(String msgId, String bgUrl, String url, long duration, long width, long height, boolean isReadOrigin) {
        VideoMessage videoMessage = new VideoMessage();
        videoMessage.setMsgId(msgId);
        videoMessage.setBg_url(bgUrl);
        videoMessage.setUrl(url);
        videoMessage.setDuration(duration);
        videoMessage.setWidth(width);
        videoMessage.setHeight(height);
        videoMessage.setReadOrigin(isReadOrigin);
        return videoMessage;
    }

    //转账消息
    @NonNull
    public static TransferMessage createTransferMessage(String msgId, String rowId, String money, String comment) {
        TransferMessage message = new TransferMessage();
        message.setMsgid(msgId);
        message.setComment(comment);
        message.setId(rowId);
        message.setTransaction_amount(money);
        return message;
    }

    //

    /**
     * 创建发送红包消息
     *
     * @param reType, 红包运营商，如支付宝红包，魔方红包
     * @param style   红包玩法风格，0 普通玩法 ； 1 拼手气玩法
     */
    public static RedEnvelopeMessage createRbMessage(String msgId, String rid, String info, int reType, int style) {
        RedEnvelopeMessage message = new RedEnvelopeMessage();
        message.setMsgid(msgId);
        message.setId(rid);
        message.setComment(info);
        message.setRe_type(reType);
        message.setStyle(style);
        return message;
    }

    //创建系统红包消息
    public static RedEnvelopeMessage createSystemRbMessage(String msgId, long traceId, String actionId, String info, int reType, int style, String sign) {
        RedEnvelopeMessage message = new RedEnvelopeMessage();
        message.setMsgid(msgId);
        message.setTraceId(traceId);
        message.setActionId(actionId);
        message.setComment(info);
        message.setRe_type(reType);
        message.setStyle(style);
        message.setSign(sign);
        return message;
    }

    //更新发送状态，根据ack
    public static MsgAllBean updateMsgSendStatusByAck(MsgBean.AckMessage ackMessage, boolean isSuccess) {
        MsgAllBean msgAllBean = SendList.getMsgFromSendSequence(ackMessage.getRequestId());
        if (msgAllBean != null) {
            SendList.removeMsgFromSendSequence(ackMessage.getRequestId());
            SendList.removeSendListJust(ackMessage.getRequestId());
            if (filterNoSaveMsg(msgAllBean.getMsg_type())) {
                return msgAllBean;
            }
            if (isSuccess) {
                msgAllBean.setSend_state(ChatEnum.ESendStatus.NORMAL);
                if (msgAllBean.getVideoMessage() != null && !TextUtils.isEmpty(videoLocalUrl)) {
                    msgAllBean.getVideoMessage().setLocalUrl(videoLocalUrl);
                }
                if (msgAllBean.getMsg_type() != ChatEnum.EMessageType.MSG_CANCEL) {
                    msgAllBean.setTimestamp(ackMessage.getTimestamp());
                } else {
                    //cancel消息，需要将msgId赋予给新的消息，以便替换源消息
                    String cancelId = msgAllBean.getMsgCancel().getMsgidCancel();
                    if (!TextUtils.isEmpty(cancelId)) {
                        msgAllBean.setMsg_id(cancelId);
                        msgAllBean.getMsgCancel().setMsgid(cancelId);
                    }

                }
                DaoUtil.update(msgAllBean);
                if (msgAllBean.getMsg_type() == ChatEnum.EMessageType.MSG_CANCEL) {
                    /********通知更新sessionDetail************************************/
                    //因为msg对象 uid有两个，都得添加
                    List<String> gids = new ArrayList<>();
                    List<Long> uids = new ArrayList<>();
                    //gid存在时，不取uid
                    if (TextUtils.isEmpty(msgAllBean.getGid())) {
                        uids.add(msgAllBean.getTo_uid());
                        uids.add(msgAllBean.getFrom_uid());
                    } else {
                        gids.add(msgAllBean.getGid());
                    }
                    //回主线程调用更新session详情
                    if(MyAppLication.INSTANCE().repository!=null)MyAppLication.INSTANCE().repository.updateSessionDetail(gids, uids);
                    /********通知更新sessionDetail end************************************/
                }
            } else {
                msgAllBean.setSend_state(ChatEnum.ESendStatus.ERROR);
                msgAllBean.setTimestamp(ackMessage.getTimestamp());
                DaoUtil.update(msgAllBean);
            }
            return msgAllBean;
        }
        return null;
    }

    /***
     * 发送领取红包消息, 不会加入发送队列，没有重发机制
     * @param toId
     * @param toGid
     * @param rid
     * @return
     */
    public static void sendReceivedEnvelopeMsg(Long toId, String toGid, String rid, int reType) {
        //自己抢自己的红包，不需要发送
        if (toId != null && UserAction.getMyId() != null && toId.longValue() == UserAction.getMyId().longValue()) {
            return;
        }
        MsgBean.RedEnvelopeType type = MsgBean.RedEnvelopeType.forNumber(reType);
        MsgBean.ReceiveRedEnvelopeMessage contentMsg = MsgBean.ReceiveRedEnvelopeMessage.newBuilder()
                .setId(rid)
                .setReType(type)
                .build();
        MsgBean.UniversalMessage.Builder msg = toMsgBuilder("", SocketData.getUUID(), toId, toGid, SocketData.getFixTime(), MsgBean.MessageType.RECEIVE_RED_ENVELOPER, contentMsg);
        //立即发送
        SocketUtil.getSocketUtil().sendData4Msg(msg);
    }

    //位置消息
    public static LocationMessage createLocationMessage(String msgId, LocationMessage messageTemp) {
        LocationMessage message = new LocationMessage();
        message.setMsgId(msgId);
        message.setLatitude(messageTemp.getLatitude());
        message.setLongitude(messageTemp.getLongitude());
        message.setImg(messageTemp.getImg());
        message.setAddress(messageTemp.getAddress());
        message.setAddressDescribe(messageTemp.getAddressDescribe());
        //message.setImg("http://e7-test.oss-cn-beijing.aliyuncs.com/Android/20190730/2dfe5997-68a5-4545-8099-712982b765c9.jpg");
        return message;
    }

    //创建转账消息
    public static TransferMessage createTransferMessage(String msgId, long traceId, long money, String info, String sign, int opType) {
        TransferMessage message = new TransferMessage();
        message.setMsgid(msgId);
        message.setId(traceId + "");
        message.setComment(info);
        message.setTransaction_amount(money + "");
        message.setSign(sign);
        message.setOpType(opType);
        return message;
    }

    //创建转账提醒消息
    public static TransferNoticeMessage createTransferNoticeMessage(String msgId, String traceId) {
        TransferNoticeMessage message = new TransferNoticeMessage();
        message.setMsgId(msgId);
        message.setRid(traceId);
        message.setContent("你有一笔等待收款的<transfer id=" + traceId + ">转账</transfer>");
        return message;
    }

    public static MsgNotice createMsgNoticeOfSnapshot(String msgId) {
        MsgNotice note = new MsgNotice();
        note.setMsgid(msgId);
        note.setMsgType(ChatEnum.ENoticeType.SNAPSHOT_SCREEN);
        note.setNote("你截取了当前聊天信息");
        return note;
    }

    public static MsgNotice createMsgNoticeOfSnapshotSwitch(String msgId, int flag) {
        MsgNotice note = new MsgNotice();
        note.setMsgid(msgId);
        note.setMsgType(ChatEnum.ENoticeType.SNAPSHOT_SCREEN);
        if (flag == 1) {
            note.setNote("你开启了截屏通知");
        } else {
            note.setNote("你关闭了截屏通知");
        }
        return note;
    }


    //发送截屏通知消息
    public static void sendSnapshotMsg(Long toId, String toGid) {
        MsgBean.TakeScreenshotMessage contentMsg = MsgBean.TakeScreenshotMessage.newBuilder().build();
        MsgBean.UniversalMessage.Builder msg = toMsgBuilder("", SocketData.getUUID(), toId, toGid, SocketData.getFixTime(), MsgBean.MessageType.TAKE_SCREENSHOT, contentMsg);
        //立即发送
        SocketUtil.getSocketUtil().sendData4Msg(msg);
    }


    public static int getOfflineCount() {
        if (offlineCount <= 0) {
            int ramSize = DeviceUtils.getTotalRam();
            if (ramSize <= 2) {//运行内存小于等于2GB
                offlineCount = 500;
            } else {
                offlineCount = 1000;
            }
        }
        return offlineCount;
    }

    //判断离线消息是否满额收取
    public static boolean isEnough(int msgCount) {
        return msgCount <= getOfflineCount();
    }


    /***
     * 请求获取离线消息
     * @return
     */
    public static byte[] requestOffline() {
        LogUtil.getLog().i(TAG, "--请求离线--最新" + getOfflineCount());
        MsgBean.OfflineMsgRequest.Builder request = MsgBean.OfflineMsgRequest.newBuilder();
        request.setReqCount(getOfflineCount());
        request.setLatest(true);
        //添加到消息队中监听
        return SocketPacket.getPackage(SocketPacket.DataType.REQUEST_MSG, request.build().toByteArray());
    }

    //是否是需要上传的消息类型：图片，语音，视频，文件等
    public static boolean isUploadType(int msgType) {
        if (msgType == ChatEnum.EMessageType.IMAGE || msgType == ChatEnum.EMessageType.VOICE || msgType == ChatEnum.EMessageType.MSG_VIDEO || msgType == ChatEnum.EMessageType.FILE) {
            return true;
        }
        return false;
    }

    public static MsgBean.UniversalMessage createUniversalMessage(List<MsgAllBean> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }
        int len = list.size();
        MsgBean.UniversalMessage.Builder msg = MsgBean.UniversalMessage.newBuilder();
        msg.setRequestId(getUUID());
        for (int i = 0; i < len; i++) {
            MsgAllBean bean = list.get(i);
            InitMsgContentAndType initMsgContentAndType = new InitMsgContentAndType(bean, bean.getMsg_type()).invoke();
            Object value = initMsgContentAndType.getValue();
            MsgBean.MessageType type = initMsgContentAndType.getType();
            if (value != null && type != null) {
                MsgBean.UniversalMessage.WrapMessage.Builder wrapBuild = MsgBean.UniversalMessage.WrapMessage.newBuilder();
                initWrapMessage(bean.getMsg_id(), bean.getFrom_uid(), bean.getFrom_avatar(), bean.getFrom_nickname(), bean.getTo_uid(), bean.getGid(), bean.getTimestamp(), type, value, wrapBuild);
                msg.addWrapMsg(wrapBuild);
            }
        }
        return msg.build();
    }

    //初始化msgContent和msgType帮助类
    private static class InitMsgContentAndType {
        private MsgAllBean bean;
        private int msgType;
        private MsgBean.MessageType type = null;
        private Object value = null;
        private boolean needSave = true;

        public InitMsgContentAndType(MsgAllBean bean, int msgType) {
            this.bean = bean;
            this.msgType = msgType;
        }

        public MsgBean.MessageType getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }

        public boolean isNeedSave() {
            return needSave;
        }

        public InitMsgContentAndType invoke() {
            switch (msgType) {
                //            case ChatEnum.EMessageType.NOTICE:
                //                ChatMessage chat = bean.getChat();
                //                MsgBean.ChatMessage.Builder txtBuilder = MsgBean.ChatMessage.newBuilder();
                //                txtBuilder.setMsg(chat.getMsg());
                //                value = txtBuilder.build();
                //                type = MsgBean.MessageType.CHAT;
                //                break;
                case ChatEnum.EMessageType.TEXT://文本
                    ChatMessage chat = bean.getChat();
                    MsgBean.ChatMessage.Builder txtBuilder = MsgBean.ChatMessage.newBuilder();
                    txtBuilder.setMsg(chat.getMsg());
                    value = txtBuilder.build();
                    type = MsgBean.MessageType.CHAT;
                    break;
                case ChatEnum.EMessageType.IMAGE://图片
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
                case ChatEnum.EMessageType.VOICE://语音
                    VoiceMessage voice = bean.getVoiceMessage();
                    MsgBean.VoiceMessage.Builder voiceBuilder = MsgBean.VoiceMessage.newBuilder();
                    voiceBuilder.setDuration(voice.getTime());
                    voiceBuilder.setUrl(voice.getUrl());
                    value = voiceBuilder.build();
                    type = MsgBean.MessageType.VOICE;
                    break;
                case ChatEnum.EMessageType.MSG_VIDEO://小视频
                    VideoMessage video = bean.getVideoMessage();
                    MsgBean.ShortVideoMessage.Builder videoBuilder = MsgBean.ShortVideoMessage.newBuilder();
                    videoBuilder.setBgUrl(video.getBg_url()).setDuration((int) video.getDuration()).setUrl(video.getUrl()).setWidth((int) video.getWidth()).setHeight((int) video.getHeight());
                    value = videoBuilder.build();
                    type = MsgBean.MessageType.SHORT_VIDEO;
                    break;
                case ChatEnum.EMessageType.AT://@
                    AtMessage at = bean.getAtMessage();
                    MsgBean.AtMessage.Builder atBuilder = MsgBean.AtMessage.newBuilder();
                    atBuilder.setAtTypeValue(at.getAt_type()).setMsg(at.getMsg()).addAllUid(at.getUid());
                    value = atBuilder.build();
                    type = MsgBean.MessageType.AT;
                    break;
                case ChatEnum.EMessageType.BUSINESS_CARD://名片
                    BusinessCardMessage card = bean.getBusiness_card();
                    MsgBean.BusinessCardMessage.Builder cardBuilder = MsgBean.BusinessCardMessage.newBuilder();
                    cardBuilder.setUid(card.getUid()).setAvatar(card.getAvatar()).setNickname(card.getNickname()).setComment(card.getComment());
                    value = cardBuilder.build();
                    type = MsgBean.MessageType.BUSINESS_CARD;
                    break;
                case ChatEnum.EMessageType.STAMP://戳一戳
                    StampMessage stamp = bean.getStamp();
                    MsgBean.StampMessage.Builder stampBuilder = MsgBean.StampMessage.newBuilder();
                    stampBuilder.setComment(stamp.getComment());
                    value = stampBuilder.build();
                    type = MsgBean.MessageType.STAMP;
                    break;
                case ChatEnum.EMessageType.TRANSFER://转账
                    TransferMessage transfer = bean.getTransfer();
                    MsgBean.TransferMessage.Builder transferBuild = MsgBean.TransferMessage.newBuilder();

                    transferBuild.setTransactionAmount(transfer.getTransaction_amount());
                    transferBuild.setComment(transfer.getComment());
                    transferBuild.setId(transfer.getId());
                    transferBuild.setOpType(MsgBean.TransferMessage.OpType.forNumber(transfer.getOpType()));
                    transferBuild.setSign(transfer.getSign());
                    value = transferBuild.build();
                    type = MsgBean.MessageType.TRANSFER;
                    break;
                case ChatEnum.EMessageType.RED_ENVELOPE://红包
                    RedEnvelopeMessage red = bean.getRed_envelope();
                    int reType = red.getRe_type().intValue();
                    MsgBean.RedEnvelopeMessage.Builder redBuild = null;
                    if (reType == MsgBean.RedEnvelopeType.MFPAY_VALUE) {
                        redBuild = MsgBean.RedEnvelopeMessage.newBuilder()
                                .setId(red.getId())
                                .setComment(red.getComment())
                                .setReType(MsgBean.RedEnvelopeType.forNumber(red.getRe_type()))
                                .setStyle(MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.forNumber(red.getStyle()));
                    } else if (reType == MsgBean.RedEnvelopeType.SYSTEM_VALUE) {
                        redBuild = MsgBean.RedEnvelopeMessage.newBuilder()
                                .setId(red.getTraceId() + "")
                                .setComment(red.getComment())
                                .setReType(MsgBean.RedEnvelopeType.forNumber(red.getRe_type()))
                                .setStyle(MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.forNumber(red.getStyle()))
                                .setSign(red.getSign());
                    }
                    if (redBuild != null) {
                        value = redBuild.build();
                        type = MsgBean.MessageType.RED_ENVELOPER;
                    }
                    break;
                case ChatEnum.EMessageType.READ://已读消息，不需要保存
                    ReadMessage readMessage = bean.getReadMessage();
                    MsgBean.ReadMessage.Builder readBuilder = MsgBean.ReadMessage.newBuilder();
                    readBuilder.setTimestamp(readMessage.getTime());
                    value = readBuilder.build();
                    type = MsgBean.MessageType.READ;
                    needSave = false;
                    break;
                case ChatEnum.EMessageType.MSG_CANCEL://撤销消息
                    MsgCancel cancel = bean.getMsgCancel();
                    MsgBean.CancelMessage.Builder cancelBuilder = MsgBean.CancelMessage.newBuilder();
                    cancelBuilder.setMsgId(cancel.getMsgidCancel());
                    value = cancelBuilder.build();
                    type = MsgBean.MessageType.CANCEL;
                    needSave = false;
                    break;
                case ChatEnum.EMessageType.LOCATION://位置
                    LocationMessage locationMessage = bean.getLocationMessage();
                    MsgBean.SnapshotLocationMessage.Builder locationBuilder = MsgBean.SnapshotLocationMessage.newBuilder();
                    locationBuilder.setLat(locationMessage.getLatitude());
                    locationBuilder.setLon(locationMessage.getLongitude());
                    locationBuilder.setImg(locationMessage.getImg());
                    locationBuilder.setAddr(locationMessage.getAddress());
                    locationBuilder.setDesc(locationMessage.getAddressDescribe());
                    value = locationBuilder.build();
                    type = MsgBean.MessageType.SNAPSHOT_LOCATION;
                    break;
                case ChatEnum.EMessageType.SHIPPED_EXPRESSION:// 大表情
                    ShippedExpressionMessage seMessage = bean.getShippedExpressionMessage();
                    MsgBean.ShippedExpressionMessage.Builder semBuilder = MsgBean.ShippedExpressionMessage.newBuilder();
                    semBuilder.setId(seMessage.getId());
                    value = semBuilder.build();
                    type = MsgBean.MessageType.SHIPPED_EXPRESSION;
                    break;
                case ChatEnum.EMessageType.FILE:// 文件
                    SendFileMessage fileMessage = bean.getSendFileMessage();
                    MsgBean.SendFileMessage.Builder fileBuilder = MsgBean.SendFileMessage.newBuilder();
                    fileBuilder.setFileName(fileMessage.getFile_name())
                            .setUrl(fileMessage.getUrl())
                            .setSize(new Long(fileMessage.getSize()).intValue())
                            .setFormat(fileMessage.getFormat());
                    value = fileBuilder.build();
                    type = MsgBean.MessageType.SEND_FILE;
                    break;
                case ChatEnum.EMessageType.TRANSFER_NOTICE://转发提醒
                    TransferNoticeMessage noticeMessage = bean.getTransferNoticeMessage();
                    long tradeId = StringUtil.getLong(noticeMessage.getRid());
                    if (tradeId > 0) {
                        MsgBean.TransNotifyMessage.Builder noticeBuilder = MsgBean.TransNotifyMessage.newBuilder();
                        noticeBuilder.setTradeId(tradeId);
                        value = noticeBuilder.build();
                        type = MsgBean.MessageType.TRANS_NOTIFY;
                    }
                    break;
                case ChatEnum.EMessageType.MSG_VOICE_VIDEO:
                    P2PAuVideoMessage p2pMessage = bean.getP2PAuVideoMessage();
                    MsgBean.P2PAuVideoMessage.Builder p2pBuild = MsgBean.P2PAuVideoMessage.newBuilder();
                    p2pBuild.setAvType(p2pMessage.getAv_type() == 1 ? MsgBean.AuVideoType.Vedio : MsgBean.AuVideoType.Audio);
                    p2pBuild.setDesc(p2pMessage.getDesc());
                    p2pBuild.setOperation(p2pMessage.getOperation());
                    value = p2pBuild.build();
                    type = MsgBean.MessageType.P2P_AU_VIDEO;
                    break;
                case ChatEnum.EMessageType.REPLY:
                    ReplyMessage replyMessage = bean.getReplyMessage();
                    MsgBean.ReplyMessage.Builder replyBuild = MsgBean.ReplyMessage.newBuilder();
                    boolean isValid;
                    QuotedMessage quotedMessage = replyMessage.getQuotedMessage();
                    MsgBean.RefMessage.Builder refBuild = MsgBean.RefMessage.newBuilder();
                    refBuild.setMsgId(quotedMessage.getMsgId());
                    refBuild.setFromUid(quotedMessage.getFromUid());
                    refBuild.setAvatar(quotedMessage.getAvatar());
                    refBuild.setNickname(quotedMessage.getNickName());
                    refBuild.setMsgType(getMessageType(quotedMessage.getMsgType()));
                    refBuild.setTimestamp(quotedMessage.getTimestamp());
                    refBuild.setMsg(quotedMessage.getMsg());
                    refBuild.setUrl(quotedMessage.getUrl());
                    replyBuild.setRefMsg(refBuild.build());
                    if (replyMessage.getChatMessage() != null) {
                        MsgBean.ChatMessage.Builder txtReplyBuilder = MsgBean.ChatMessage.newBuilder();
                        txtReplyBuilder.setMsg(replyMessage.getChatMessage().getMsg());
                        replyBuild.setChatMsg(txtReplyBuilder.build());
                        isValid = true;
                    } else if (replyMessage.getAtMessage() != null) {
                        AtMessage atMessage = replyMessage.getAtMessage();
                        MsgBean.AtMessage.Builder atReplyBuilder = MsgBean.AtMessage.newBuilder();
                        atReplyBuilder.setAtTypeValue(atMessage.getAt_type()).setMsg(atMessage.getMsg()).addAllUid(atMessage.getUid());
                        replyBuild.setAtMsg(atReplyBuilder.build());
                        isValid = true;
                    } else {
                        isValid = false;
                    }
                    if (isValid) {
                        value = replyBuild.build();
                        type = MsgBean.MessageType.REPLY;
                    }
                    break;
            }
            return this;
        }
    }

    public static MsgNotice createMsgNoticeOfBanWords(String msgId, int flag) {
        MsgNotice note = new MsgNotice();
        note.setMsgid(msgId);
        note.setMsgType(ChatEnum.ENoticeType.GROUP_BAN_WORDS);
        if (flag == 1) {
            note.setNote("你开启了全员禁言");
        } else {
            note.setNote("你解除了全员禁言");
        }
        return note;
    }


    public static ReadMessage createReadMessage(String msgId, long time) {
        ReadMessage message = new ReadMessage();
        message.setMsgId(msgId);
        message.setTime(time);
        return message;
    }

    /**
     * 创建单条回复消息
     *
     * @param message 被引用消息
     * @param content 回复内容
     * @param atType  AT类型
     * @param userIds userIds
     */
    public static ReplyMessage createReplyMessage(MsgAllBean message, String msgId, String content, @ChatEnum.EAtType int atType, List<Long> userIds) {
        ReplyMessage replyMessage = new ReplyMessage();
        replyMessage.setMsgId(msgId);
        QuotedMessage quotedMessage = createQuoted(message);
        replyMessage.setQuotedMessage(quotedMessage);
        if (atType == ChatEnum.EAtType.DEFAULT) {
            ChatMessage chat = createChatMessage(SocketData.getUUID(), content);
            replyMessage.setChatMessage(chat);
        } else {
            AtMessage atMessage = createAtMessage(SocketData.getUUID(), content, atType, userIds);
            replyMessage.setAtMessage(atMessage);
        }
        return replyMessage;
    }

    public static QuotedMessage createQuoted(MsgAllBean msgAllBean) {
        QuotedMessage message = new QuotedMessage();
        int msgType = msgAllBean.getMsg_type();
        message.setMsgId(msgAllBean.getMsg_id());
        message.setTimestamp(msgAllBean.getTimestamp());
        message.setMsgType(msgType);
        message.setFromUid(msgAllBean.getFrom_uid().longValue());
        message.setNickName(msgAllBean.getFrom_nickname());
        message.setAvatar(msgAllBean.getFrom_avatar());
        InitMsgAndUrl initMsgAndUrl = new InitMsgAndUrl(msgAllBean, msgType).invoke();
        String msg = initMsgAndUrl.getMsg();
        String url = initMsgAndUrl.getUrl();
        message.setMsg(TextUtils.isEmpty(msg) ? "" : msg);
        message.setUrl(TextUtils.isEmpty(url) ? "" : url);
        return message;
    }


    //初始化回复消息内容及url
    private static class InitMsgAndUrl {
        private MsgAllBean bean;
        private int msgType;
        private String msg = "";
        private String url = "";

        public InitMsgAndUrl(MsgAllBean bean, int msgType) {
            this.bean = bean;
            this.msgType = msgType;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public InitMsgAndUrl invoke() {
            switch (msgType) {

                case ChatEnum.EMessageType.TEXT://文本
                    ChatMessage chat = bean.getChat();
                    msg = chat.getMsg();
                    break;
                case ChatEnum.EMessageType.IMAGE://图片
                    ImageMessage image = bean.getImage();
                    url = image.getThumbnail();
                    break;
                case ChatEnum.EMessageType.VOICE://语音

                    break;
                case ChatEnum.EMessageType.MSG_VIDEO://小视频
                    VideoMessage video = bean.getVideoMessage();
                    url = video.getBg_url();
                    break;
                case ChatEnum.EMessageType.AT://@
                    AtMessage at = bean.getAtMessage();
                    msg = at.getMsg();
                    break;
                case ChatEnum.EMessageType.BUSINESS_CARD://名片

                    break;
                case ChatEnum.EMessageType.STAMP://戳一戳
                    StampMessage stamp = bean.getStamp();
                    msg = stamp.getComment();
                    break;
                case ChatEnum.EMessageType.TRANSFER://转账

                    break;
                case ChatEnum.EMessageType.RED_ENVELOPE://红包

                    break;
                case ChatEnum.EMessageType.READ://已读消息，不需要保存

                    break;
                case ChatEnum.EMessageType.MSG_CANCEL://撤销消息

                    break;
                case ChatEnum.EMessageType.LOCATION://位置

                    break;
                case ChatEnum.EMessageType.SHIPPED_EXPRESSION:// 大表情
                    ShippedExpressionMessage seMessage = bean.getShippedExpressionMessage();
                    url = seMessage.getId();
                    break;
                case ChatEnum.EMessageType.FILE:// 文件
                    SendFileMessage fileMessage = bean.getSendFileMessage();
                    url = fileMessage.getUrl();
                    break;
                case ChatEnum.EMessageType.TRANSFER_NOTICE://转账提醒

                    break;
                case ChatEnum.EMessageType.MSG_VOICE_VIDEO:
                    P2PAuVideoMessage p2pMessage = bean.getP2PAuVideoMessage();
                    msg = p2pMessage.getDesc();
                    break;
                case ChatEnum.EMessageType.REPLY:
                    ReplyMessage replyMessage = bean.getReplyMessage();
                    if (replyMessage.getChatMessage() != null) {
                        msg = replyMessage.getChatMessage().getMsg();
                    } else if (replyMessage.getAtMessage() != null) {
                        msg = replyMessage.getAtMessage().getMsg();
                    }
                    break;
            }
            return this;
        }
    }


    @ChatEnum.EMessageType
    public static int getEMsgType(MsgBean.MessageType type) {
        //默认不识别
        @ChatEnum.EMessageType int messageType = ChatEnum.EMessageType.UNRECOGNIZED;
        switch (type) {
            case CHAT:
                messageType = ChatEnum.EMessageType.TEXT;
                break;
            case IMAGE:
                messageType = ChatEnum.EMessageType.IMAGE;
                break;
            case RED_ENVELOPER:
                messageType = ChatEnum.EMessageType.RED_ENVELOPE;
                break;
            case TRANSFER:
                messageType = ChatEnum.EMessageType.TRANSFER;
                break;
            case STAMP:
                messageType = ChatEnum.EMessageType.STAMP;
                break;
            case BUSINESS_CARD:
                messageType = ChatEnum.EMessageType.BUSINESS_CARD;
                break;
            case VOICE:
                messageType = ChatEnum.EMessageType.VOICE;
                break;
            case ASSISTANT:
                messageType = ChatEnum.EMessageType.ASSISTANT;
                break;
            case CANCEL:
                messageType = ChatEnum.EMessageType.MSG_CANCEL;
                break;
            case SHORT_VIDEO:
                messageType = ChatEnum.EMessageType.MSG_VIDEO;
                break;
            case SNAPSHOT_LOCATION:
                messageType = ChatEnum.EMessageType.LOCATION;
                break;
            case SHIPPED_EXPRESSION:
                messageType = ChatEnum.EMessageType.SHIPPED_EXPRESSION;
                break;
            case REPLY:
                messageType = ChatEnum.EMessageType.REPLY;
                break;
            case BALANCE_ASSISTANT:
                messageType = ChatEnum.EMessageType.BALANCE_ASSISTANT;
                break;
            case TRANS_NOTIFY:
                messageType = ChatEnum.EMessageType.TRANSFER_NOTICE;
                break;
        }
        return messageType;
    }

    public static MsgBean.MessageType getMessageType(@ChatEnum.EMessageType int type) {
        //默认不识别
        MsgBean.MessageType messageType = MsgBean.MessageType.UNRECOGNIZED;
        switch (type) {
            case ChatEnum.EMessageType.TEXT:
                messageType = MsgBean.MessageType.CHAT;
                break;
            case ChatEnum.EMessageType.IMAGE:
                messageType = MsgBean.MessageType.IMAGE;
                break;
            case ChatEnum.EMessageType.RED_ENVELOPE:
                messageType = MsgBean.MessageType.RED_ENVELOPER;
                break;
            case ChatEnum.EMessageType.TRANSFER:
                messageType = MsgBean.MessageType.TRANSFER;
                break;
            case ChatEnum.EMessageType.STAMP:
                messageType = MsgBean.MessageType.STAMP;
                break;
            case ChatEnum.EMessageType.BUSINESS_CARD:
                messageType = MsgBean.MessageType.BUSINESS_CARD;
                break;
            case ChatEnum.EMessageType.VOICE:
                messageType = MsgBean.MessageType.VOICE;
                break;
            case ChatEnum.EMessageType.ASSISTANT:
                messageType = MsgBean.MessageType.ASSISTANT;
                break;
            case ChatEnum.EMessageType.MSG_CANCEL:
                messageType = MsgBean.MessageType.CANCEL;
                break;
            case ChatEnum.EMessageType.MSG_VIDEO:
                messageType = MsgBean.MessageType.SHORT_VIDEO;
                break;
            case ChatEnum.EMessageType.LOCATION:
                messageType = MsgBean.MessageType.SNAPSHOT_LOCATION;
                break;
            case ChatEnum.EMessageType.SHIPPED_EXPRESSION:
                messageType = MsgBean.MessageType.SHIPPED_EXPRESSION;
                break;
            case ChatEnum.EMessageType.REPLY:
                messageType = MsgBean.MessageType.REPLY;
                break;
            case ChatEnum.EMessageType.BALANCE_ASSISTANT:
                messageType = MsgBean.MessageType.BALANCE_ASSISTANT;
                break;
            case ChatEnum.EMessageType.TRANSFER_NOTICE:
                messageType = MsgBean.MessageType.TRANS_NOTIFY;
                break;
        }
        return messageType;
    }


}
