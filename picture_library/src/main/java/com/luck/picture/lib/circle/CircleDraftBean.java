package com.luck.picture.lib.circle;

import com.luck.picture.lib.entity.LocalMedia;

import java.io.Serializable;
import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-11-17
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class CircleDraftBean implements Serializable {

    private String content;// 内容
    private String attachment;// 附件 type 对应 momentType bgUrl 为 视频的第一帧图片 isOrigin 为 是否原文件 duration 时长 [{"type":1,"url":"http://zx-im-img.zhixun6.com/image/a.png","bgUrl":"http://zx-im-img.zhixun6.com/image/b.png",\n "isOrigin":0,"duration":10},...]
    private int gender;// 性别 0:未知|1:男|2:女
    private String latitude;
    private String longitude;
    private String position;//定位 详细地址
    private String locationDesc;// 位置描述
    private int type;// 说说类型(0:无|1:图片|2:语音|4:视频|5:包含图片和视频|8:投票|9:包含图片和投票|10:包含语音和投票|12:包含视频和投票|13:包含图片、视频和投票)
    private int visibility;// 可见度(0:广场可见|1:好友可见|2:陌生人可见|3:自己可见)
    private String vote;//投票信息{"type":2, //1:文本|2:图片 items:[{"item":"" //文本内容或图片URL，根据type值判断}]}
    private String city;// 定位城市
    private List<LocalMedia> list;// 图片、视频列表
    private List<LocalMedia> voteList;// 图片投票列表
    private String audioFile;// 语音文件
    private long audioDuration;// 语音时长

    public String getLocationDesc() {
        return locationDesc;
    }

    public void setLocationDesc(String locationDesc) {
        this.locationDesc = locationDesc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getLatitude() {
        return latitude;
    }

    public List<LocalMedia> getVoteList() {
        return voteList;
    }

    public void setVoteList(List<LocalMedia> voteList) {
        this.voteList = voteList;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<LocalMedia> getList() {
        return list;
    }

    public void setList(List<LocalMedia> list) {
        this.list = list;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public long getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(long audioDuration) {
        this.audioDuration = audioDuration;
    }
}
