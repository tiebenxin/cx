package com.yanlong.im.chat.bean;

import android.text.TextUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Liszt
 * @date 2020/3/16
 * Description 网页消息
 */
public class WebMessage extends RealmObject implements IMsgContent {
    @PrimaryKey
    String msgId;
    String appName = "";
    String title = "";
    String description = "";
    String webUrl = "";
    String iconUrl = "";


    @Override
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTitle() {
        return TextUtils.isEmpty(title) ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return TextUtils.isEmpty(description) ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return TextUtils.isEmpty(iconUrl) ? "" : iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getWebUrl() {
        return TextUtils.isEmpty(webUrl) ? "" : webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
