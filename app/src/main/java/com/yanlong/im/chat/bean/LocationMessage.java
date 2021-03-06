package com.yanlong.im.chat.bean;


import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * 位置消息
 */
public class LocationMessage extends RealmObject implements IMsgContent {
    @PrimaryKey
    private String msgId;
    private int latitude=-1;//纬度
    private int longitude=-1;//经度
    private String address;//地址
    private String addressDescribe;//地址描述
    private String img;//地图图片路径

    @Ignore
    private double distance=0d;//本地字段，与中心点的距离，两点之间的距离
    @Ignore
    private boolean isCheck;

    public LocationMessage() {

    }

    @Override
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressDescribe() {
        return addressDescribe;
    }

    public void setAddressDescribe(String addressDescribe) {
        this.addressDescribe = addressDescribe;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
