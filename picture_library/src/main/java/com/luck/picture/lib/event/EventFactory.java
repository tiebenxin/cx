package com.luck.picture.lib.event;

import android.content.Context;

import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-25
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class EventFactory extends BaseEvent {

    /**
     * 发布广场动态
     */
    public static class CreateCircleEvent extends BaseEvent {
        public Context context;

        public CircleBean circleBean;

        private List<LocalMedia> list;

        public List<LocalMedia> getList() {
            return list;
        }

        public void setList(List<LocalMedia> list) {
            this.list = list;
        }

        public static class CircleBean {
            private String content;
            private String attachment;// 附件 type 对应 momentType bgUrl 为 视频的第一帧图片 isOrigin 为 是否原文件 duration 时长 [{"type":1,"url":"http://zx-im-img.zhixun6.com/image/a.png","bgUrl":"http://zx-im-img.zhixun6.com/image/b.png",\n "isOrigin":0,"duration":10},...]
            private int gender;// 性别 0:未知|1:男|2:女
            private String latitude;
            private String longitude;
            private String position;//定位 详细地址
            private int type;// 说说类型(0:无|1:图片|2:语音|4:视频|5:包含图片和视频|8:投票|9:包含图片和投票|10:包含语音和投票|12:包含视频和投票|13:包含图片、视频和投票)
            private int visibility;// 可见度(0:广场可见|1:好友可见|2:陌生人可见|3:自己可见)
            private String vote;//投票信息{"type":2, //1:文本|2:图片 items:[{"item":"" //文本内容或图片URL，根据type值判断}]}
            private String city;// 定位城市

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
        }
    }

    /**
     * 动态发布成功
     */
    public static class CreateSuccessEvent extends BaseEvent {

    }

    /**
     * 刷新关注列表 单条
     */
    public static class RefreshSignFollowEvent extends BaseEvent {
        public int postion;// 位置
        public Long id;// 动态ID
        public Long uid;// 发布者ID
    }

    /**
     * 刷新关注列表 全部
     */
    public static class RefreshFollowEvent extends BaseEvent {
    }

    /**
     * 刷新推荐列表 单条
     */
    public static class RefreshSignRecomendEvent extends BaseEvent {
        public int postion;// 位置
        public Long id;// 动态ID
        public Long uid;// 发布者ID
    }

    /**
     * 刷新推荐列表 全部
     */
    public static class RefreshRecomendEvent extends BaseEvent {
    }

    /**
     * 添加评论
     */
    public static class AddRecomendEvent extends BaseEvent {
        public String content;
        public int type;
    }

    /**
     * 检查未读互动消息
     */
    public static class CheckUnreadMsgEvent<T> extends BaseEvent {
        public T data;
    }

    /**
     * 首页显示未读互动消息数
     */
    public static class HomePageShowUnreadMsgEvent extends BaseEvent {
        public int num;
    }

    /**
     * 首页显示我关注的人有新动态
     */
    public static class HomePageRedDotEvent extends BaseEvent {
        public boolean ifShow;//是否显示小红点
    }

    /**
     * 清空广场未读互动消息
     */
    public static class ClearHomePageShowUnreadMsgEvent extends BaseEvent {
    }

    /**
     * 更新关注红点
     */
    public static class UpdateRedEvent extends BaseEvent {
    }

    /**
     * 更新新消息
     */
    public static class UpdateNewMsgEvent extends BaseEvent {
    }

    /**
     * 更新关注状态
     */
    public static class UpdateFollowStateEvent extends BaseEvent {
        public int type;//状态更改为 0 未关注 1 已关注
        public long uid;//目标uid
        public String from;//从哪个界面跳转过来
    }

    /**
     * 删除某一条动态
     */
    public static class DeleteItemTrend extends BaseEvent {
        public int position;//位置
        public String fromWhere;//从哪里跳转过来
    }

    /**
     * 不看他的动态
     */
    public static class NoSeeEvent extends BaseEvent {
        public long uid;
    }

    /**
     * 刷新单条动态
     */
    public static class UpdateOneTrendEvent extends BaseEvent {
        public int position;//位置
        public String fromWhere;//从哪里跳转过来
    }
}
