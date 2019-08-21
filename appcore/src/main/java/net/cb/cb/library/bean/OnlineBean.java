package net.cb.cb.library.bean;

/**
 * @anthor Liszt
 * @data 2019/8/21
 * Description
 */
public class OnlineBean {
    long uid;
    long lastonline;//最后在线时间
    int activeType;//永华活跃状态（0离线|1在线）

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getLastonline() {
        return lastonline;
    }

    public void setLastonline(long lastonline) {
        this.lastonline = lastonline;
    }

    public int getActiveType() {
        return activeType;
    }

    public void setActiveType(int activeType) {
        this.activeType = activeType;
    }
}
