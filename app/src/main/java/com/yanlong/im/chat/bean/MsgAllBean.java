package com.yanlong.im.chat.bean;


import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.hm.cxpay.global.PayEnum;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.ui.cell.IChatModel;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;

import java.util.Arrays;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class MsgAllBean extends RealmObject implements IChatModel {
    @PrimaryKey
    private String msg_id;
    private Long timestamp;
    //0:正常,1:错误,2:发送中
    @ChatEnum.ESendStatus
    private int send_state = 0;
    //重发的数据对象
    private byte[] send_data;
    //自己是否已读
    private boolean isRead = false;
    private String request_id;
    private Long from_uid;
    private String from_nickname;
    private String from_avatar;
    private String from_group_nickname;
    //  private UserInfo from_user;
    private Long to_uid;
    //   private UserInfo to_user;
    private String gid;
    //对方已读
    private int read = 0; //0 未读  ， 1  已读

    @ChatEnum.EMessageType
    private Integer msg_type;

    private int survival_time;
    //阅后即焚结束时间
    private long endTime;

    private long readTime; //已读时间

    private long startTime;

    private long serverTime; //服务器时间
    private int isLocal;//是否是本地创建消息：0，不是本地，1 是本地消息
    private int isReplying;//是否是正在回复的消息，0，未被回复或已经回复过了， 1正在回复

    private ChatMessage chat;
    private ImageMessage image;
    private VideoMessage videoMessage;
    private RedEnvelopeMessage red_envelope;
    private ReceiveRedEnvelopeMessage receive_red_envelope;
    private TransferMessage transfer;
    private StampMessage stamp;
    private BusinessCardMessage business_card;
    private MsgNotice msgNotice;
    private MsgCancel msgCancel;
    private VoiceMessage voiceMessage;
    private AtMessage atMessage;
    private AssistantMessage assistantMessage;
    private ChangeSurvivalTimeMessage changeSurvivalTimeMessage;
    private P2PAuVideoMessage p2PAuVideoMessage;
    private P2PAuVideoDialMessage p2PAuVideoDialMessage;
    private BalanceAssistantMessage balanceAssistantMessage;
    private LocationMessage locationMessage;
    private TransferNoticeMessage transferNoticeMessage;
    private ShippedExpressionMessage shippedExpressionMessage;
    private SendFileMessage sendFileMessage;
    private WebMessage webMessage;
    @Ignore
    private ReadMessage readMessage;//备注已读消息，不存，不显示
    private ReplyMessage replyMessage;//回复消息
    private AdMessage adMessage;//小助手广告消息

    public ReplyMessage getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(ReplyMessage replyMessage) {
        this.replyMessage = replyMessage;
    }

    public WebMessage getWebMessage() {
        return webMessage;
    }

    public void setWebMessage(WebMessage webMessage) {
        this.webMessage = webMessage;
    }

    public SendFileMessage getSendFileMessage() {
        return sendFileMessage;
    }

    public void setSendFileMessage(SendFileMessage sendFileMessage) {
        this.sendFileMessage = sendFileMessage;
    }

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getReadTime() {
        return readTime;
    }

    public void setReadTime(long readTime) {
        this.readTime = readTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getSurvival_time() {
        return survival_time;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public void setSurvival_time(int survival_time) {
        this.survival_time = survival_time;
    }

    public VideoMessage getVideoMessage() {
        return videoMessage;
    }

    public void setVideoMessage(VideoMessage videoMessage) {
        this.videoMessage = videoMessage;
    }

    public ChangeSurvivalTimeMessage getChangeSurvivalTimeMessage() {
        return changeSurvivalTimeMessage;
    }

    public void setChangeSurvivalTimeMessage(ChangeSurvivalTimeMessage changeSurvivalTimeMessage) {
        this.changeSurvivalTimeMessage = changeSurvivalTimeMessage;
    }


    public P2PAuVideoMessage getP2PAuVideoMessage() {
        return p2PAuVideoMessage;
    }

    public void setP2PAuVideoMessage(P2PAuVideoMessage p2PAuVideoMessage) {
        this.p2PAuVideoMessage = p2PAuVideoMessage;
    }

    public P2PAuVideoDialMessage getP2PAuVideoDialMessage() {
        return p2PAuVideoDialMessage;
    }

    public void setP2PAuVideoDialMessage(P2PAuVideoDialMessage p2PAuVideoDialMessage) {
        this.p2PAuVideoDialMessage = p2PAuVideoDialMessage;
    }

    public BalanceAssistantMessage getBalanceAssistantMessage() {
        return balanceAssistantMessage;
    }

    public void setBalanceAssistantMessage(BalanceAssistantMessage balanceAssistantMessage) {
        this.balanceAssistantMessage = balanceAssistantMessage;
    }

    public LocationMessage getLocationMessage() {
        return locationMessage;
    }

    public void setLocationMessage(LocationMessage locationMessage) {
        this.locationMessage = locationMessage;
    }

    public ShippedExpressionMessage getShippedExpressionMessage() {
        return shippedExpressionMessage;
    }

    public void setShippedExpressionMessage(ShippedExpressionMessage shippedExpressionMessage) {
        this.shippedExpressionMessage = shippedExpressionMessage;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public UserInfo getFrom_user() {
        return DaoUtil.findOne(UserInfo.class, "uid", from_uid);
    }

    public UserInfo getTo_user() {
        return DaoUtil.findOne(UserInfo.class, "uid", to_uid);
    }

    public UserInfo getShow_user() {
        if (from_uid.longValue() == UserAction.getMyId().longValue()) {
            return getTo_user();
        } else {
            return getFrom_user();
        }

    }

  /*  public void setFrom_user(UserInfo from_user) {
        this.from_user = from_user;
    }


    public void setTo_user(UserInfo to_user) {
        this.to_user = to_user;
    }*/

    public String getFrom_group_nickname() {
        return from_group_nickname;
    }

    public void setFrom_group_nickname(String from_group_nickname) {
        this.from_group_nickname = from_group_nickname;
    }

    public MsgCancel getMsgCancel() {
        return msgCancel;
    }

    public void setMsgCancel(MsgCancel msgCancel) {
        this.msgCancel = msgCancel;
    }

    public String getFrom_nickname() {
        return from_nickname;
    }

    public void setFrom_nickname(String from_nickname) {
        this.from_nickname = from_nickname;
    }

    public String getFrom_avatar() {
        return from_avatar;
    }

    public void setFrom_avatar(String from_avatar) {
        this.from_avatar = from_avatar;
    }

    public String getRequest_id() {
        return this.request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public Long getFrom_uid() {
        return this.from_uid;
    }

    public void setFrom_uid(Long from_uid) {
        this.from_uid = from_uid;
    }

    public Long getTo_uid() {
        return this.to_uid;
    }

    public void setTo_uid(Long to_uid) {
        this.to_uid = to_uid;
    }

    public String getGid() {
        return gid;
    }

    public Group getGroup() {
        return DaoUtil.findOne(Group.class, "gid", gid);
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public int getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(int isLocal) {
        this.isLocal = isLocal;
    }

    public int getIsReplying() {
        return isReplying;
    }

    public void setIsReplying(int isReplying) {
        this.isReplying = isReplying;
    }

    @ChatEnum.EMessageType
    public Integer getMsg_type() {
        if (msg_type == null) {
            msg_type = ChatEnum.EMessageType.UNRECOGNIZED;
        }
        return this.msg_type;
    }

    public VoiceMessage getVoiceMessage() {
        return voiceMessage;
    }

    public void setVoiceMessage(VoiceMessage voiceMessage) {
        this.voiceMessage = voiceMessage;
    }

    /***
     * 把类型转换为消息
     * @return
     */
    public String getMsg_typeStr() {
        String str = "";
        try {
            if (msg_type == ChatEnum.EMessageType.NOTICE) {
                //公告:
                //8.9 过滤拉人通知里面的颜色标签
                if (getMsgNotice() != null) {
                    str = "" + StringUtil.delHTMLTag(getMsgNotice().getNote());
                }
            } else if (msg_type == ChatEnum.EMessageType.TEXT) {//普通消息
                str = getChat().getMsg();
            } else if (msg_type == ChatEnum.EMessageType.STAMP) {
                str = "[戳一下]" + getStamp().getComment();
            } else if (msg_type == ChatEnum.EMessageType.RED_ENVELOPE) {
                RedEnvelopeMessage envelopeMessage = getRed_envelope();
                if (envelopeMessage != null) {
                    int reType = envelopeMessage.getRe_type();
                    if (reType == 1) {
                        str = "[零钱红包]" + getRed_envelope().getComment();
                    }
                }
            } else if (msg_type == ChatEnum.EMessageType.IMAGE) {
                str = "[图片]";
            } else if (msg_type == ChatEnum.EMessageType.BUSINESS_CARD) {
                str = "[名片]";// + getBusiness_card().getNickname();
            } else if (msg_type == ChatEnum.EMessageType.TRANSFER) {
                TransferMessage transferMessage = getTransfer();
                if (transferMessage.getOpType() == PayEnum.ETransferOpType.TRANS_SEND) {
                    if (isMe()) {
                        str = "[转账]等待朋友收款";
                    } else {
                        str = "[转账]等待你收款";
                    }
                } else if (transferMessage.getOpType() == PayEnum.ETransferOpType.TRANS_RECEIVE) {
                    if (isMe()) {
                        str = "[转账]已收款";
                    } else {
                        str = "[转账]朋友已确认收款";
                    }
                } else if (transferMessage.getOpType() == PayEnum.ETransferOpType.TRANS_REJECT) {
                    if (isMe()) {
                        str = "[转账]你已将转账退还";
                    } else {
                        str = "[转账]转账已被朋友退还";
                    }
                } else if (transferMessage.getOpType() == PayEnum.ETransferOpType.TRANS_PAST) {
                    if (isMe()) {
                        str = "[转账]已过期";
                    } else {
                        str = "[转账]已过期";
                    }
                } else {
                    if (isMe()) {
                        str = "[转账]";
                    } else {
                        str = "[转账]";
                    }
                }
            } else if (msg_type == ChatEnum.EMessageType.VOICE) {
                str = "[语音]";
            } else if (msg_type == ChatEnum.EMessageType.AT) {
                str = getAtMessage().getMsg();
            } else if (msg_type == ChatEnum.EMessageType.ASSISTANT || msg_type == ChatEnum.EMessageType.ASSISTANT_NEW) {
                if (getAssistantMessage() != null && !TextUtils.isEmpty(getAssistantMessage().getTitle())) {
                    str = getAssistantMessage().getTitle();
                } else {
                    str = "常信通知";
                }
            } else if (msg_type == ChatEnum.EMessageType.MSG_CANCEL) {//撤回消息
                str = "" + StringUtil.delHTMLTag(getMsgCancel().getNote());
            } else if (msg_type == ChatEnum.EMessageType.MSG_VIDEO) {//撤回消息
                str = "[视频]";
            } else if (msg_type == ChatEnum.EMessageType.MSG_VOICE_VIDEO) {// 音视频消息
                if (getP2PAuVideoMessage().getAv_type() == MsgBean.AuVideoType.Vedio.getNumber()) {
                    str = "[视频通话]";
                } else {
                    str = "[语音通话]";
                }
            } else if (msg_type == ChatEnum.EMessageType.CHANGE_SURVIVAL_TIME) {//阅后即焚
                str = getMsgCancel().getNote();
            } else if (msg_type == ChatEnum.EMessageType.LOCATION) {//位置
                str = "[位置]";
            } else if (msg_type == ChatEnum.EMessageType.BALANCE_ASSISTANT) {//阅后即焚
                str = getBalanceAssistantMessage().getTitle();
            } else if (msg_type == ChatEnum.EMessageType.FILE) {//文件
                if (getSendFileMessage() != null) {
                    if (!TextUtils.isEmpty(getSendFileMessage().getFile_name())) {
                        str = "[文件]" + getSendFileMessage().getFile_name();
                    } else {
                        str = "[文件]";
                    }
                }
            } else if (msg_type == ChatEnum.EMessageType.SHIPPED_EXPRESSION) {
                str = "[动态表情]";
            } else if (msg_type == ChatEnum.EMessageType.WEB) {
                str = "[链接]" + getWebMessage().getTitle();
            } else if (msg_type == ChatEnum.EMessageType.TRANSFER_NOTICE) {//转账提醒消息
                str = "你有一笔等待收款的转账";
            } else if (msg_type == ChatEnum.EMessageType.REPLY) {//回复消息
                ReplyMessage reply = getReplyMessage();
                String content = "";
                if (reply.getAtMessage() != null) {
                    content = reply.getAtMessage().getMsg();
                } else if (reply.getChatMessage() != null) {
                    content = reply.getChatMessage().getMsg();
                }
                str = content;
            } else if (msg_type == ChatEnum.EMessageType.ASSISTANT_PROMOTION) {
                AdMessage adMessage = getAdMessage();
                String content = "";
                if (adMessage != null) {
                    if (!TextUtils.isEmpty(adMessage.getSummary())) {
                        content = adMessage.getSummary();
                    } else if (!TextUtils.isEmpty(adMessage.getTitle())) {
                        content = adMessage.getTitle();
                    }
                }
                str = "[必看]" + content;
            }
        } catch (Exception e) {
        }

        return str;
    }

    /***
     * 根据类型获取标题
     * @return
     */
    public String getMsg_typeTitle() {
        String str = "";
        try {
            if (msg_type == ChatEnum.EMessageType.BUSINESS_CARD) {
                str = "[名片]";// + getBusiness_card().getNickname();
            } else if (msg_type == ChatEnum.EMessageType.LOCATION) {//位置
                str = "[位置]";
            } else if (msg_type == ChatEnum.EMessageType.FILE) {//文件
                if (getSendFileMessage() != null) {
                    str = "[文件]";
                }
            } else if (msg_type == ChatEnum.EMessageType.WEB) {
                str = "[链接]";
            }
        } catch (Exception e) {
        }

        return str;
    }

    public void setMsg_type(@ChatEnum.EMessageType Integer msg_type) {
        if (msg_type == null) {
            msg_type = ChatEnum.EMessageType.UNRECOGNIZED;
        }
        this.msg_type = msg_type;
    }

    public String getMsg_id() {
        return this.msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public Long getTimestamp() {
        if (timestamp == null)
            return 0l;
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
//        LogUtil.getLog().i(MsgAllBean.class.getSimpleName(), timestamp + "\r\n" + Log.getStackTraceString(new Throwable()));
        this.timestamp = timestamp;
    }

    public ChatMessage getChat() {
        return chat;
    }

    public void setChat(ChatMessage chat) {
        this.chat = chat;
    }

    public ImageMessage getImage() {
        return image;
    }

    public void setImage(ImageMessage image) {
        this.image = image;
    }

    public RedEnvelopeMessage getRed_envelope() {
        return red_envelope;
    }

    public void setRed_envelope(RedEnvelopeMessage red_envelope) {
        this.red_envelope = red_envelope;
    }

    public ReceiveRedEnvelopeMessage getReceive_red_envelope() {
        return receive_red_envelope;
    }

    public void setReceive_red_envelope(ReceiveRedEnvelopeMessage receive_red_envelope) {
        this.receive_red_envelope = receive_red_envelope;
    }

    public TransferMessage getTransfer() {
        return transfer;
    }

    public void setTransfer(TransferMessage transfer) {
        this.transfer = transfer;
    }

    public StampMessage getStamp() {
        return stamp;
    }

    public void setStamp(StampMessage stamp) {
        this.stamp = stamp;
    }

    public BusinessCardMessage getBusiness_card() {
        return business_card;
    }

    public void setBusiness_card(BusinessCardMessage business_card) {
        this.business_card = business_card;
    }

    public TransferNoticeMessage getTransferNoticeMessage() {
        return transferNoticeMessage;
    }

    public void setTransferNoticeMessage(TransferNoticeMessage transferNoticeMessage) {
        this.transferNoticeMessage = transferNoticeMessage;
    }


    public AtMessage getAtMessage() {
        return atMessage;
    }

    public void setAtMessage(AtMessage atMessage) {
        this.atMessage = atMessage;
    }

    public AssistantMessage getAssistantMessage() {
        return assistantMessage;
    }

    public void setAssistantMessage(AssistantMessage assistantMessage) {
        this.assistantMessage = assistantMessage;
    }

    public MsgNotice getMsgNotice() {
        return msgNotice;
    }

    public void setMsgNotice(MsgNotice msgNotice) {
        this.msgNotice = msgNotice;
    }

    @ChatEnum.ESendStatus
    public int getSend_state() {
        return send_state;
    }

    /***
     * //0:正常,1:错误,2:发送中
     * @param send_state
     */
    public void setSend_state(@ChatEnum.ESendStatus int send_state) {
//        LogUtil.getLog().i(MsgAllBean.class.getSimpleName(), send_state + "\r\n" + Log.getStackTraceString(new Throwable()));
        this.send_state = send_state;
    }

    public byte[] getSend_data() {
        return send_data;
    }


    public void setSend_data(byte[] send_data) {
        this.send_data = send_data;
    }

    public ReadMessage getReadMessage() {
        return readMessage;
    }

    public void setReadMessage(ReadMessage readMessage) {
        this.readMessage = readMessage;
    }

    public AdMessage getAdMessage() {
        return adMessage;
    }

    public void setAdMessage(AdMessage adMessage) {
        this.adMessage = adMessage;
    }

    /***
     * 是否为自己
     * @return
     */
    public boolean isMe() {
        if (from_uid == null || UserAction.getMyInfo() == null || UserAction.getMyInfo().getUid() == null) {
            return false;
        }
        return from_uid == UserAction.getMyInfo().getUid().longValue();
    }


    /*
     * 2. 根据messageType绑定布局
     * */
    @Override
    public final ChatEnum.EChatCellLayout getChatCellLayoutId() {
        @ChatEnum.EMessageType int msgType = getMsg_type();
        boolean isMe = isMe();
        ChatEnum.EChatCellLayout layout = null;
        switch (msgType) {
            case ChatEnum.EMessageType.NOTICE://通知
            case ChatEnum.EMessageType.MSG_CANCEL://撤回
            case ChatEnum.EMessageType.CHANGE_SURVIVAL_TIME://阅后即焚
                layout = ChatEnum.EChatCellLayout.NOTICE;
                break;
            case ChatEnum.EMessageType.TEXT://文本
            case ChatEnum.EMessageType.TRANSFER_NOTICE://转账提醒
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.TEXT_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.TEXT_RECEIVED;
                }
                break;
            case ChatEnum.EMessageType.IMAGE://图片
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.IMAGE_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.IMAGE_RECEIVED;
                }
                break;
            case ChatEnum.EMessageType.MSG_VIDEO://视频
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.VIDEO_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.VIDEO_RECEIVED;
                }
                break;
            case ChatEnum.EMessageType.BUSINESS_CARD://名片
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.CARD_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.CARD_RECEIVED;
                }
                break;
            case ChatEnum.EMessageType.RED_ENVELOPE://红包
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.RED_ENVELOPE_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.RED_ENVELOPE_RECEIVED;
                }
                break;
            case ChatEnum.EMessageType.TRANSFER://转账
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.TRANSFER_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.TRANSFER_RECEIVED;
                }
                break;
            case ChatEnum.EMessageType.VOICE://语音
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.VOICE_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.VOICE_RECEIVED;
                }
                break;
            case ChatEnum.EMessageType.STAMP://戳一下
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.STAMP_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.STAMP_RECEIVED;
                }
                break;
            case ChatEnum.EMessageType.AT://@消息
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.AT_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.AT_RECEIVED;
                }
                break;
            case ChatEnum.EMessageType.ASSISTANT://小助手
                layout = ChatEnum.EChatCellLayout.ASSISTANT;
                break;
            case ChatEnum.EMessageType.ASSISTANT_NEW://小助手
                layout = ChatEnum.EChatCellLayout.ASSISTANT_NEW;
                break;
            case ChatEnum.EMessageType.LOCK://端对端加密消息
                layout = ChatEnum.EChatCellLayout.LOCK;
                break;
            case ChatEnum.EMessageType.FILE:
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.FILE_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.FILE_RECEIVED;
                }
                break;
            case ChatEnum.EMessageType.LOCATION:
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.MAP_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.MAP_RECEIVED;
                }
                break;
            case ChatEnum.EMessageType.MSG_VOICE_VIDEO:
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.CALL_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.CALL_RECEIVED;
                }
                break;
            case ChatEnum.EMessageType.SHIPPED_EXPRESSION://表情
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.EXPRESS_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.EXPRESS_RECEIVED;
                }
                break;
            case ChatEnum.EMessageType.REPLY:
                if (replyMessage == null) {
                    if (isMe) {
                        layout = ChatEnum.EChatCellLayout.UNRECOGNIZED_SEND;
                    } else {
                        layout = ChatEnum.EChatCellLayout.UNRECOGNIZED_RECEIVED;
                    }
                    return layout;
                }
                QuotedMessage quotedMessage = replyMessage.getQuotedMessage();
                if (quotedMessage == null) {
                    if (isMe) {
                        layout = ChatEnum.EChatCellLayout.UNRECOGNIZED_SEND;
                    } else {
                        layout = ChatEnum.EChatCellLayout.UNRECOGNIZED_RECEIVED;
                    }
                    return layout;
                }
                layout = getReplyLayout(quotedMessage.getMsgType(), isMe);
                if (layout == null) {
//                    LogUtil.writeLog("MsgAllBean--" + "--不能识别回复消息--UNRECOGNIZED--" + quotedMessage.getMsgType());
                    if (isMe) {
                        layout = ChatEnum.EChatCellLayout.UNRECOGNIZED_SEND;
                    } else {
                        layout = ChatEnum.EChatCellLayout.UNRECOGNIZED_RECEIVED;
                    }
                }
                break;
            case ChatEnum.EMessageType.WEB://web消息
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.WEB_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.WEB_RECEIVED;
                }
                break;
            case ChatEnum.EMessageType.ASSISTANT_PROMOTION://广告消息
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.UNRECOGNIZED_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.ADVERTISEMENT;
                }
                break;
            case ChatEnum.EMessageType.BALANCE_ASSISTANT://零钱助手
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.UNRECOGNIZED_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.BALANCE_ASSISTANT;
                }
                break;
            default://未识别
                //  LogUtil.writeLog("MsgAllBean--" + "--不能识别消息--UNRECOGNIZED--" + msg_type);
                if (isMe) {
                    layout = ChatEnum.EChatCellLayout.UNRECOGNIZED_SEND;
                } else {
                    layout = ChatEnum.EChatCellLayout.UNRECOGNIZED_RECEIVED;
                }
                break;
        }
        return layout;
    }

    //获取回复消息layout
    private final ChatEnum.EChatCellLayout getReplyLayout(int msgType, boolean isMe) {
        ChatEnum.EChatCellLayout layout = null;
        switch (msgType) {
            case ChatEnum.EMessageType.TEXT:
            case ChatEnum.EMessageType.AT:
            case ChatEnum.EMessageType.STAMP:
            case ChatEnum.EMessageType.REPLY:
                layout = isMe ? ChatEnum.EChatCellLayout.REPLY_TEXT_SEND : ChatEnum.EChatCellLayout.REPLY_TEXT_RECEIVED;
                break;
            case ChatEnum.EMessageType.IMAGE:
            case ChatEnum.EMessageType.SHIPPED_EXPRESSION:
            case ChatEnum.EMessageType.MSG_VIDEO:
            case ChatEnum.EMessageType.VOICE:
            case ChatEnum.EMessageType.BUSINESS_CARD:
            case ChatEnum.EMessageType.FILE:
                layout = isMe ? ChatEnum.EChatCellLayout.REPLY_IMAGE_SEND : ChatEnum.EChatCellLayout.REPLY_IMAGE_RECEIVED;
                break;
        }
        return layout;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof MsgAllBean) {
            if (((MsgAllBean) obj).msg_id.equals(this.msg_id)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return "MsgAllBean{" +
                "msg_id='" + msg_id + '\'' +
                ", timestamp=" + timestamp +
                ", send_state=" + send_state +
                ", send_data=" + Arrays.toString(send_data) +
                ", isRead=" + isRead +
                ", request_id='" + request_id + '\'' +
                ", from_uid=" + from_uid +
                ", from_nickname='" + from_nickname + '\'' +
                ", from_avatar='" + from_avatar + '\'' +
                ", from_group_nickname='" + from_group_nickname + '\'' +
                ", to_uid=" + to_uid +
                ", gid='" + gid + '\'' +
                ", read=" + read +
                ", msg_type=" + msg_type +
                ", survival_time=" + survival_time +
                ", endTime=" + endTime +
                ", readTime=" + readTime +
                ", startTime=" + startTime +
                ", serverTime=" + serverTime +
                ", chat=" + chat +
                ", image=" + image +
                ", videoMessage=" + videoMessage +
                ", red_envelope=" + red_envelope +
                ", receive_red_envelope=" + receive_red_envelope +
                ", transfer=" + transfer +
                ", stamp=" + stamp +
                ", business_card=" + business_card +
                ", msgNotice=" + msgNotice +
                ", msgCancel=" + msgCancel +
                ", voiceMessage=" + voiceMessage +
                ", atMessage=" + atMessage +
                ", assistantMessage=" + assistantMessage +
                ", changeSurvivalTimeMessage=" + changeSurvivalTimeMessage +
                ", p2PAuVideoMessage=" + p2PAuVideoMessage +
                ", p2PAuVideoDialMessage=" + p2PAuVideoDialMessage +
                '}';
    }

    public final IMsgContent getMsgContent() {
        IMsgContent content = null;
        if (msg_type == null || msg_type == ChatEnum.EMessageType.UNRECOGNIZED) {
            return null;
        }
        switch (msg_type) {
            case ChatEnum.EMessageType.NOTICE://通知
                break;
            case ChatEnum.EMessageType.MSG_CANCEL://撤回
                content = msgCancel;
                break;
            case ChatEnum.EMessageType.CHANGE_SURVIVAL_TIME://阅后即焚
                content = changeSurvivalTimeMessage;
                break;
            case ChatEnum.EMessageType.TRANSFER_NOTICE://转账提醒
                content = transferNoticeMessage;
                break;
            case ChatEnum.EMessageType.TEXT://文本
                content = chat;
                break;
            case ChatEnum.EMessageType.IMAGE://图片
                content = image;
                break;
            case ChatEnum.EMessageType.MSG_VIDEO://视频
                content = videoMessage;
                break;
            case ChatEnum.EMessageType.BUSINESS_CARD://名片
                content = business_card;
                break;
            case ChatEnum.EMessageType.RED_ENVELOPE://红包
                content = red_envelope;
                break;
            case ChatEnum.EMessageType.TRANSFER://转账
                content = transfer;
                break;
            case ChatEnum.EMessageType.VOICE://语音
                content = voiceMessage;
                break;
            case ChatEnum.EMessageType.STAMP://戳一下
                content = stamp;
                break;
            case ChatEnum.EMessageType.AT://@消息
                content = atMessage;
                break;
            case ChatEnum.EMessageType.ASSISTANT://小助手
            case ChatEnum.EMessageType.ASSISTANT_NEW://小助手
                break;
            case ChatEnum.EMessageType.LOCK://端对端加密消息
                break;
            case ChatEnum.EMessageType.FILE:
                content = sendFileMessage;
                break;
            case ChatEnum.EMessageType.LOCATION:
                content = locationMessage;
                break;
            case ChatEnum.EMessageType.MSG_VOICE_VIDEO:
                content = p2PAuVideoMessage;
                break;
            case ChatEnum.EMessageType.SHIPPED_EXPRESSION://表情
                content = shippedExpressionMessage;
                break;
            default://未识别
                break;
        }
        return content;
    }
}

