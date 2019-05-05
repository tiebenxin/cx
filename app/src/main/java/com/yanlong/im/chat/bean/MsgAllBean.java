package com.yanlong.im.chat.bean;


import net.cb.cb.library.utils.StringUtil;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class MsgAllBean extends RealmObject {

    private String request_id;
    private Long from_uid;
    private MsgUserInfo from_user;
    private Long to_uid;
    private MsgUserInfo to_user;
    private String gid;
    /***
     *  CHAT = 0; // 普通聊天消息
     *   IMAGE = 1; // 图片消息
     *   RED_ENVELOPER = 2; // 单聊红包消息
     *   RECEIVE_RED_ENVELOPER = 3; // 领取红包消息
     *   TRANSFER = 4; // 转账消息
     *   STAMP = 5; // 戳一下消息
     *   BUSINESS_CARD = 6; // 名片消息
     *   REQUEST_FRIEND = 7; // 请求加好友消息
     *   ACCEPT_BE_FRIENDS = 8; // 接收好友请求
     *   ACK = 100;
     */
    private Integer msg_type;
    @PrimaryKey
    private String msg_id;
    private Long timestamp;

    private ChatMessage chat;

    private ImageMessage image;

    private RedEnvelopeMessage red_envelope;

    private ReceiveRedEnvelopeMessage receive_red_envelope;

    private TransferMessage transfer;

    private StampMessage stamp;

    private BusinessCardMessage business_card;

    private RequestFriendMessage request_friend;

    private AcceptBeFriendsMessage accept_be_friends;

    private AckMessage ack;

    public MsgUserInfo getFrom_user() {
        return from_user;
    }

    public void setFrom_user(MsgUserInfo from_user) {
        this.from_user = from_user;
    }

    public MsgUserInfo getTo_user() {
        return to_user;
    }

    public void setTo_user(MsgUserInfo to_user) {
        this.to_user = to_user;
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

    public void setGid(String gid) {
        this.gid = gid;
    }

    public Integer getMsg_type() {
        return this.msg_type;
    }

    public void setMsg_type(Integer msg_type) {
        this.msg_type = msg_type;
    }

    public String getMsg_id() {
        return this.msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public Long getTimestamp() {
        if(timestamp==null)
            return 0l;
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
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

    public RequestFriendMessage getRequest_friend() {
        return request_friend;
    }

    public void setRequest_friend(RequestFriendMessage request_friend) {
        this.request_friend = request_friend;
    }

    public AcceptBeFriendsMessage getAccept_be_friends() {
        return accept_be_friends;
    }

    public void setAccept_be_friends(AcceptBeFriendsMessage accept_be_friends) {
        this.accept_be_friends = accept_be_friends;
    }

    public AckMessage getAck() {
        return ack;
    }

    public void setAck(AckMessage ack) {
        this.ack = ack;
    }

    /***
     * 是否为自己
     * @return
     */
    public boolean isMe() {

        return from_uid==100102l;
    }
}

