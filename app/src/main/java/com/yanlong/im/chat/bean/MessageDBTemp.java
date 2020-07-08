package com.yanlong.im.chat.bean;


import androidx.annotation.Nullable;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.ui.cell.IChatModel;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.utils.DaoUtil;

import java.util.Arrays;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 红包消息备份表,只备份红包消息
 */
public class MessageDBTemp extends RealmObject implements IChatModel {
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
    private Long to_uid;
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

    private EnvelopeTemp envelopeMessage;


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


    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }


    public String getFrom_group_nickname() {
        return from_group_nickname;
    }

    public void setFrom_group_nickname(String from_group_nickname) {
        this.from_group_nickname = from_group_nickname;
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
        this.timestamp = timestamp;
    }

    public EnvelopeTemp getRedEnvelope() {
        return envelopeMessage;
    }

    public void setRedEnvelope(EnvelopeTemp red_envelope) {
        this.envelopeMessage = red_envelope;
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
        if (from_uid == null) {
            return false;
        }
        return from_uid == UserAction.getMyInfo().getUid().longValue();
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof MessageDBTemp) {
            if (((MessageDBTemp) obj).msg_id.equals(this.msg_id)) {
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
                ", envelopeMessage=" + envelopeMessage +
                '}';
    }

    @Override
    public ChatEnum.EChatCellLayout getChatCellLayoutId() {
        return null;
    }
}

