package com.yanlong.im.chat.bean;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.utils.StringUtil;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/***
 * 群
 */
public class Group  extends RealmObject {
    @PrimaryKey
    private String gid;
    private String name;
    private String avatar;

    private String master;
    @SerializedName("members")
    private RealmList<UserInfo> users;

    private Integer ceiling;
    private Integer saved;// 是否已保存
    private Integer notNotify;// 消息免打扰
    private Integer needVerification;//是否需要群验证
    @SerializedName("toTop")
    private Integer isTop;

    private String mygroupName;//我的群昵称
    private String announcement;//群公告
    //机器人id
    private String robotid;
    //名称
    private String robotname;

    private String inviter;

    private int joinType; //0.扫码  1.拉人

    private String joinTime;

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    public int getJoinType() {
        return joinType;
    }

    public void setJoinType(int joinType) {
        this.joinType = joinType;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public String getRobotname() {
        return robotname;
    }

    public void setRobotname(String robotname) {
        this.robotname = robotname;
    }

    public String getRobotid() {
        return robotid;
    }

    public void setRobotid(String robotid) {
        this.robotid = robotid;
    }

    public String getMygroupName() {
        if(!StringUtil.isNotNull(mygroupName)&&users!=null){
            for (UserInfo user:users) {
                if(UserAction.getMyId().longValue()==user.getUid().longValue()){
                    mygroupName=user.getMembername();
                    break;
                }
            }
        }
        return mygroupName;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public void setMygroupName(String mygroupName) {
        this.mygroupName = mygroupName;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public RealmList<UserInfo> getUsers() {
        users= users==null?new RealmList<UserInfo>():users;
        return users;
    }

    public void setUsers(RealmList<UserInfo> users) {
        this.users = users;
    }

    public String getName() {
        name=name==null?"":name;
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

    public Integer getNotNotify() {
        return notNotify==null?0:notNotify;
    }

    public void setNotNotify(Integer notNotify) {
        this.notNotify = notNotify;
    }

    public Integer getNeedVerification() {
        return needVerification==null?0:needVerification;
    }

    public void setNeedVerification(Integer needVerification) {
        this.needVerification = needVerification;
    }

    public Integer getIsTop() {
        return isTop==null?0:isTop;
    }

    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }
}
