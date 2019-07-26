package com.yanlong.im.chat.bean;


import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class MsgAllBean extends RealmObject {
    @PrimaryKey
    private String msg_id;
    private Long timestamp;
    //0:正常,1:错误,2:发送中
    private int send_state = 0;
    //重发的数据对象
    private byte[] send_data;
    private boolean isRead=false;
    private String request_id;
    private Long from_uid;
    private String from_nickname;
    private String from_avatar;
    //  private UserInfo from_user;
    private Long to_uid;
 //   private UserInfo to_user;
    private String gid;

    private Integer msg_type;


    private ChatMessage chat;

    private ImageMessage image;

    private RedEnvelopeMessage red_envelope;

    private ReceiveRedEnvelopeMessage receive_red_envelope;

    private TransferMessage transfer;

    private StampMessage stamp;

    private BusinessCardMessage business_card;

    private MsgNotice msgNotice;

    private VoiceMessage voiceMessage;

    private AtMessage atMessage;

    //private RequestFriendMessage request_friend;

   // private AcceptBeFriendsMessage accept_be_friends;

    //private AckMessage ack;

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public UserInfo getFrom_user() {
        return DaoUtil.findOne(UserInfo.class,"uid",from_uid);
    }
    public UserInfo getTo_user() {
        return DaoUtil.findOne(UserInfo.class,"uid",to_uid);
    }
    public UserInfo getShow_user() {
        if(from_uid.longValue()==UserAction.getMyId().longValue()){
            return getTo_user();
        }else{
            return getFrom_user();
        }

    }

  /*  public void setFrom_user(UserInfo from_user) {
        this.from_user = from_user;
    }


    public void setTo_user(UserInfo to_user) {
        this.to_user = to_user;
    }*/

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
    public Group getGroup(){
        return DaoUtil.findOne(Group.class,"gid",gid);
    }


    public void setGid(String gid) {
        this.gid = gid;
    }

    public Integer getMsg_type() {
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
        if (msg_type == 0) {
            //公告:
            str = ""+getMsgNotice().getNote();

        }
        if (msg_type == 1) {//普通消息
            str = getChat().getMsg();

        }
        if (msg_type == 2) {
            str = "[戳一下]" + getStamp().getComment();

        }
        if (msg_type == 3) {
            str = "[常聊红包]"+getRed_envelope().getComment();
        }
        if (msg_type == 4) {
            str = "[图片]";
        }
        if (msg_type == 5) {
            str = "[名片]";// + getBusiness_card().getNickname();
        }
        if (msg_type == 6) {
            str = "[收款]" ;
        }
        if (msg_type == 7) {
            str = "[语音]" ;
        }
        if (msg_type == 8) {
            if(isMe()){
                str = "[有人@我]"+getAtMessage().getMsg();
            }else{
                str = getAtMessage().getMsg();
            }


        }
        return str;
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
        if (timestamp == null)
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

   /* public RequestFriendMessage getRequest_friend() {
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
*/

    public AtMessage getAtMessage() {
        return atMessage;
    }

    public void setAtMessage(AtMessage atMessage) {
        this.atMessage = atMessage;
    }

    public MsgNotice getMsgNotice() {
        return msgNotice;
    }

    public void setMsgNotice(MsgNotice msgNotice) {
        this.msgNotice = msgNotice;
    }

    public int getSend_state() {
        return send_state;
    }

    /***
     * //0:正常,1:错误,2:发送中
     * @param send_state
     */
    public void setSend_state(int send_state) {
        this.send_state = send_state;
    }

    public byte[] getSend_data() {
        return send_data;
    }

    public void setSend_data(byte[] send_data) {
        this.send_data = send_data;
    }


    /***
     * 是否为自己
     * @return
     */
    public boolean isMe() {

        return from_uid == UserAction.getMyInfo().getUid().longValue();
    }
}

