package net.cb.cb.library.bean;

/***
 * 刷新好友
 */
public class EventRefreshFriend {
    private boolean isLocal=false;//冲本地刷新好友列表

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }
}
