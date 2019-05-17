package com.yanlong.im.chat.bean;



import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import java.util.List;

/***
 * 返回的群信息
 */
public class ReturnGroupInfoBean {
    private String gid;
    private String name;
    private String avatar;
    private String master;

    private Integer ceiling;
    private Integer saved;// 是否已保存
    private Integer notNotify;// 消息免打扰
    private Integer needVerification;//是否需要群验证


    private List<UserInfo> members;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public Integer getCeiling() {
        return ceiling;
    }

    public void setCeiling(Integer ceiling) {
        this.ceiling = ceiling;
    }

    public Integer getSaved() {
        return saved==null?0:saved;
    }

    public void setSaved(Integer saved) {
        this.saved = saved;
    }

    public Integer getNotnotify() {
        return notNotify==null?0:notNotify;
    }

    public void setNotnotify(Integer notnotify) {
        this.notNotify = notnotify;
    }

    public List<UserInfo> getMembers() {
        return members;
    }

    public void setMembers(List<UserInfo> members) {
        this.members = members;
    }

    public Integer getNeedVerification() {
        return needVerification==null?0:needVerification;
    }

    public void setNeedVerification(Integer needVerification) {
        this.needVerification = needVerification;
    }
    public Integer getIsTop(){
        Session session=   DaoUtil.findOne(Session.class,"gid",gid);
        if(session==null)
            return 0;

       return session.getIsTop();
    }
}
