package com.yanlong.im.circle.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @类名：朋友圈用户资料
 * @Date：2020/9/23
 * @by zjy
 * @备注：
 */
public class FriendUserBean extends RealmObject {

    @PrimaryKey
    private long uid;//id
    private String nickname;//昵称
    private String avatar;//头像
    private String alias;//备注名
    private String content;//最后一条说说的内容
    private int stat;//关注状态 (1 A关注了B  2 A被B关注 3 相互关注)
    private int followStat;//关注状态 (1 A关注了B  2 A被B关注 3 相互关注)  TODO 谁看过我 单独字段
    private long lastTime;//最近访问对方主页时间 (谁看过我/我看过谁)
    private String imid;//常信号
    private String bgImage;//主页背景图



    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public String getImid() {
        return imid;
    }

    public void setImid(String imid) {
        this.imid = imid;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public int getFollowStat() {
        return followStat;
    }

    public void setFollowStat(int followStat) {
        this.followStat = followStat;
    }
}
