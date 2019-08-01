package com.yanlong.im.chat.bean;

import net.cb.cb.library.utils.StringUtil;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ImageMessage extends RealmObject {
    @PrimaryKey
    private String msgid;
    private String origin; //原图
    private String preview; //预览图
    private String thumbnail; //缩略图

    private String localimg;//本地图

    private boolean isReadOrigin=false;//是否已经阅读原图

    private long  width = 0; //图宽
    private long height = 0; //图高
    private long size=0;//文件大小

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isReadOrigin() {
        return isReadOrigin;
    }

    public void setReadOrigin(boolean readOrigin) {
        isReadOrigin = readOrigin;
    }

    public String getLocalimg() {
        return localimg;
    }

    public void setLocalimg(String localimg) {
        this.localimg = localimg;
    }

    public String getOrigin() {
        return origin;
    }
    public String getOriginShow() {
        String img=origin;
        if(StringUtil.isNotNull(origin)&&StringUtil.isNotNull(localimg)){
            img=localimg;
        }

        return  img;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getPreview() {
        return preview;
    }
    public String getPreviewShow() {
        String img=preview;
        if(StringUtil.isNotNull(preview)&&StringUtil.isNotNull(localimg)){
            img=localimg;
        }

        return  img;

    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getThumbnailShow() {
        String img=thumbnail;
        if(StringUtil.isNotNull(thumbnail)&&StringUtil.isNotNull(localimg)){
            img=localimg;
        }

        return  img;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
}
