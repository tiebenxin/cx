package com.yanlong.im.chat.bean;

import android.text.TextUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 收藏->语音实体类
 */
public class CollectVoiceMessage extends RealmObject implements IMsgContent {
    @PrimaryKey
    private String msgId;
    private String voiceURL;//语音url
    private int voiceDuration;//语音时长
    private int playStatus;
    private String localUrl;//本地路径

    public String getVoiceURL() {
        return TextUtils.isEmpty(voiceURL) ? "" : voiceURL;
    }

    public void setVoiceURL(String voiceURL) {
        this.voiceURL = voiceURL;
    }

    public int getVoiceDuration() {
        return voiceDuration;
    }

    public void setVoiceDuration(int voiceDuration) {
        this.voiceDuration = voiceDuration;
    }


    @Override
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(int playStatus) {
        this.playStatus = playStatus;
    }

    public String getLocalUrl() {
        return TextUtils.isEmpty(localUrl) ? "" : localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }
}
