package com.yanlong.im.chat.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class AckMessage {
    @Id
    private String mid;
    private String request_id;
    private String msg_id; // 消息id



    @Generated(hash = 83668412)
    public AckMessage(String mid, String request_id, String msg_id) {
        this.mid = mid;
        this.request_id = request_id;
        this.msg_id = msg_id;
    }

    @Generated(hash = 788995535)
    public AckMessage() {
    }



    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getMid() {
        return this.mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

}
