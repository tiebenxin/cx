package com.yanlong.im.chat.bean;


import net.cb.cb.library.base.BaseBean;

/**
 * 位置消息
 */
public class LocationCircleMessage extends BaseBean {
    private String msgId;
    private double latitude = -1;//纬度
    private double longitude = -1;//经度
    private String address;//地址
    private String addressDescribe;//地址描述
    private String img;//地图图片路径
    private double distance = 0d;//本地字段，与中心点的距离，两点之间的距离
    private boolean isCheck;

    public LocationCircleMessage() {

    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
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
