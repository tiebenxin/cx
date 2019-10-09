package net.cb.cb.library.bean;

/***
 * 刷新首页的消息
 */
public class EventRefreshMainMsg {
    private int type;
    private String id;
    private String refreshItem;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRefreshItem() {
        return refreshItem;
    }

    public void setRefreshItem(String refreshItem) {
        this.refreshItem = refreshItem;
    }
}
