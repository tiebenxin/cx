package com.yanlong.im.chat.bean;


import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * 收藏->位置实体类
 */
public class CollectLocationMessage extends RealmObject implements IMsgContent {
    @PrimaryKey
    private String msgId;
    private int lat=-1;//纬度
    private int lon=-1;//经度
    private String addr;//地址
    private String addressDesc;//地址描述
    private String img;//地图图片路径

    @Ignore
    private double colletDistance=0d;//本地字段，与中心点的距离，两点之间的距离


    public CollectLocationMessage() {

    }

    @Override
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public int getLon() {
        return lon;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getAddressDesc() {
        return addressDesc;
    }

    public void setAddressDesc(String addressDesc) {
        this.addressDesc = addressDesc;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public double getColletDistance() {
        return colletDistance;
    }

    public void setColletDistance(double colletDistance) {
        this.colletDistance = colletDistance;
    }
}
