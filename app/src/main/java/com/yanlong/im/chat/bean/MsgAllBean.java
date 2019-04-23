package com.yanlong.im.chat.bean;

import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.yanlong.im.utils.socket.MsgBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import com.yanlong.im.gen.DaoSession;
import com.yanlong.im.gen.AckMessageDao;
import com.yanlong.im.gen.AcceptBeFriendsMessageDao;
import com.yanlong.im.gen.RequestFriendMessageDao;
import com.yanlong.im.gen.BusinessCardMessageDao;
import com.yanlong.im.gen.StampMessageDao;
import com.yanlong.im.gen.TransferMessageDao;
import com.yanlong.im.gen.ReceiveRedEnvelopeMessageDao;
import com.yanlong.im.gen.RedEnvelopeMessageDao;
import com.yanlong.im.gen.ImageMessageDao;
import com.yanlong.im.gen.ChatMessageDao;
import com.yanlong.im.gen.MsgAllBeanDao;

@Entity
public class MsgAllBean {
    @Id
    private String request_id;
    private Long from_uid;
    private Long to_uid;
    private Long to_gid;
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
    @Unique
    private String msg_id;
    private Long timestamp;
    @ToOne(joinProperty = "msg_id")
    private ChatMessage chat;
    @ToOne(joinProperty = "msg_id")
    private ImageMessage image;
    @ToOne(joinProperty = "msg_id")
    private RedEnvelopeMessage red_envelope;
    @ToOne(joinProperty = "msg_id")
    private ReceiveRedEnvelopeMessage receive_red_envelope;
    @ToOne(joinProperty = "msg_id")
    private TransferMessage transfer;
    @ToOne(joinProperty = "msg_id")
    private StampMessage stamp;
    @ToOne(joinProperty = "msg_id")
    private BusinessCardMessage business_card;
    @ToOne(joinProperty = "msg_id")
    private RequestFriendMessage request_friend;
    @ToOne(joinProperty = "msg_id")
    private AcceptBeFriendsMessage accept_be_friends;
    @ToOne(joinProperty = "msg_id")
    private AckMessage ack;


    //-------------------------转换
    @Keep
    private static Gson gson = new Gson();
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 948226185)
    private transient MsgAllBeanDao myDao;
    @Generated(hash = 764894066)
    public MsgAllBean(String request_id, Long from_uid, Long to_uid, Long to_gid,
            Integer msg_type, String msg_id, Long timestamp) {
        this.request_id = request_id;
        this.from_uid = from_uid;
        this.to_uid = to_uid;
        this.to_gid = to_gid;
        this.msg_type = msg_type;
        this.msg_id = msg_id;
        this.timestamp = timestamp;
    }
    @Generated(hash = 33999264)
    public MsgAllBean() {
    }
    @Generated(hash = 1265534083)
    private transient String chat__resolvedKey;
    @Generated(hash = 1328453487)
    private transient String image__resolvedKey;
    @Generated(hash = 712472208)
    private transient String red_envelope__resolvedKey;
    @Generated(hash = 1860429452)
    private transient String receive_red_envelope__resolvedKey;
    @Generated(hash = 55212758)
    private transient String transfer__resolvedKey;
    @Generated(hash = 1995807600)
    private transient String stamp__resolvedKey;
    @Generated(hash = 244025199)
    private transient String business_card__resolvedKey;
    @Generated(hash = 1775574951)
    private transient String request_friend__resolvedKey;
    @Generated(hash = 1770778460)
    private transient String accept_be_friends__resolvedKey;
    @Generated(hash = 1248362357)
    private transient String ack__resolvedKey;
    @Keep
    public static MsgAllBean ToBean(MsgBean.UniversalMessage bean) {
        try {
            String json = JsonFormat.printer().print(bean.toBuilder());
            MsgAllBean msgAllBean = gson.fromJson(json, MsgAllBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;


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
    public Long getTo_gid() {
        return this.to_gid;
    }
    public void setTo_gid(Long to_gid) {
        this.to_gid = to_gid;
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
        return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 219783752)
    public ChatMessage getChat() {
        String __key = this.msg_id;
        if (chat__resolvedKey == null || chat__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ChatMessageDao targetDao = daoSession.getChatMessageDao();
            ChatMessage chatNew = targetDao.load(__key);
            synchronized (this) {
                chat = chatNew;
                chat__resolvedKey = __key;
            }
        }
        return chat;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 281342823)
    public void setChat(ChatMessage chat) {
        synchronized (this) {
            this.chat = chat;
            msg_id = chat == null ? null : chat.getMid();
            chat__resolvedKey = msg_id;
        }
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1061520506)
    public ImageMessage getImage() {
        String __key = this.msg_id;
        if (image__resolvedKey == null || image__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ImageMessageDao targetDao = daoSession.getImageMessageDao();
            ImageMessage imageNew = targetDao.load(__key);
            synchronized (this) {
                image = imageNew;
                image__resolvedKey = __key;
            }
        }
        return image;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 30544223)
    public void setImage(ImageMessage image) {
        synchronized (this) {
            this.image = image;
            msg_id = image == null ? null : image.getMid();
            image__resolvedKey = msg_id;
        }
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1663765722)
    public RedEnvelopeMessage getRed_envelope() {
        String __key = this.msg_id;
        if (red_envelope__resolvedKey == null
                || red_envelope__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RedEnvelopeMessageDao targetDao = daoSession.getRedEnvelopeMessageDao();
            RedEnvelopeMessage red_envelopeNew = targetDao.load(__key);
            synchronized (this) {
                red_envelope = red_envelopeNew;
                red_envelope__resolvedKey = __key;
            }
        }
        return red_envelope;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 476487924)
    public void setRed_envelope(RedEnvelopeMessage red_envelope) {
        synchronized (this) {
            this.red_envelope = red_envelope;
            msg_id = red_envelope == null ? null : red_envelope.getMid();
            red_envelope__resolvedKey = msg_id;
        }
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 731567080)
    public ReceiveRedEnvelopeMessage getReceive_red_envelope() {
        String __key = this.msg_id;
        if (receive_red_envelope__resolvedKey == null
                || receive_red_envelope__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ReceiveRedEnvelopeMessageDao targetDao = daoSession
                    .getReceiveRedEnvelopeMessageDao();
            ReceiveRedEnvelopeMessage receive_red_envelopeNew = targetDao
                    .load(__key);
            synchronized (this) {
                receive_red_envelope = receive_red_envelopeNew;
                receive_red_envelope__resolvedKey = __key;
            }
        }
        return receive_red_envelope;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2011515503)
    public void setReceive_red_envelope(
            ReceiveRedEnvelopeMessage receive_red_envelope) {
        synchronized (this) {
            this.receive_red_envelope = receive_red_envelope;
            msg_id = receive_red_envelope == null ? null
                    : receive_red_envelope.getMid();
            receive_red_envelope__resolvedKey = msg_id;
        }
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 290303424)
    public TransferMessage getTransfer() {
        String __key = this.msg_id;
        if (transfer__resolvedKey == null || transfer__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TransferMessageDao targetDao = daoSession.getTransferMessageDao();
            TransferMessage transferNew = targetDao.load(__key);
            synchronized (this) {
                transfer = transferNew;
                transfer__resolvedKey = __key;
            }
        }
        return transfer;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1650706626)
    public void setTransfer(TransferMessage transfer) {
        synchronized (this) {
            this.transfer = transfer;
            msg_id = transfer == null ? null : transfer.getMid();
            transfer__resolvedKey = msg_id;
        }
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 281441431)
    public StampMessage getStamp() {
        String __key = this.msg_id;
        if (stamp__resolvedKey == null || stamp__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StampMessageDao targetDao = daoSession.getStampMessageDao();
            StampMessage stampNew = targetDao.load(__key);
            synchronized (this) {
                stamp = stampNew;
                stamp__resolvedKey = __key;
            }
        }
        return stamp;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1575864265)
    public void setStamp(StampMessage stamp) {
        synchronized (this) {
            this.stamp = stamp;
            msg_id = stamp == null ? null : stamp.getMid();
            stamp__resolvedKey = msg_id;
        }
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 474395172)
    public BusinessCardMessage getBusiness_card() {
        String __key = this.msg_id;
        if (business_card__resolvedKey == null
                || business_card__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BusinessCardMessageDao targetDao = daoSession
                    .getBusinessCardMessageDao();
            BusinessCardMessage business_cardNew = targetDao.load(__key);
            synchronized (this) {
                business_card = business_cardNew;
                business_card__resolvedKey = __key;
            }
        }
        return business_card;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 965893684)
    public void setBusiness_card(BusinessCardMessage business_card) {
        synchronized (this) {
            this.business_card = business_card;
            msg_id = business_card == null ? null : business_card.getMid();
            business_card__resolvedKey = msg_id;
        }
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1464790880)
    public RequestFriendMessage getRequest_friend() {
        String __key = this.msg_id;
        if (request_friend__resolvedKey == null
                || request_friend__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RequestFriendMessageDao targetDao = daoSession
                    .getRequestFriendMessageDao();
            RequestFriendMessage request_friendNew = targetDao.load(__key);
            synchronized (this) {
                request_friend = request_friendNew;
                request_friend__resolvedKey = __key;
            }
        }
        return request_friend;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 959736715)
    public void setRequest_friend(RequestFriendMessage request_friend) {
        synchronized (this) {
            this.request_friend = request_friend;
            msg_id = request_friend == null ? null : request_friend.getMid();
            request_friend__resolvedKey = msg_id;
        }
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 124582654)
    public AcceptBeFriendsMessage getAccept_be_friends() {
        String __key = this.msg_id;
        if (accept_be_friends__resolvedKey == null
                || accept_be_friends__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AcceptBeFriendsMessageDao targetDao = daoSession
                    .getAcceptBeFriendsMessageDao();
            AcceptBeFriendsMessage accept_be_friendsNew = targetDao.load(__key);
            synchronized (this) {
                accept_be_friends = accept_be_friendsNew;
                accept_be_friends__resolvedKey = __key;
            }
        }
        return accept_be_friends;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1700591528)
    public void setAccept_be_friends(AcceptBeFriendsMessage accept_be_friends) {
        synchronized (this) {
            this.accept_be_friends = accept_be_friends;
            msg_id = accept_be_friends == null ? null : accept_be_friends.getMid();
            accept_be_friends__resolvedKey = msg_id;
        }
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 154410888)
    public AckMessage getAck() {
        String __key = this.msg_id;
        if (ack__resolvedKey == null || ack__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AckMessageDao targetDao = daoSession.getAckMessageDao();
            AckMessage ackNew = targetDao.load(__key);
            synchronized (this) {
                ack = ackNew;
                ack__resolvedKey = __key;
            }
        }
        return ack;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 888711270)
    public void setAck(AckMessage ack) {
        synchronized (this) {
            this.ack = ack;
            msg_id = ack == null ? null : ack.getMid();
            ack__resolvedKey = msg_id;
        }
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 658827802)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMsgAllBeanDao() : null;
    }


}

