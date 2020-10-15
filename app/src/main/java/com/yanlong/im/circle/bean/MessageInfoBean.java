package com.yanlong.im.circle.bean;

import net.cb.cb.library.base.BaseBean;

import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-18
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class MessageInfoBean extends BaseBean {

    /**
     * id : 507562580437504049
     * uid : 113921
     * content : 4433333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333
     * attachment :
     * vote :
     * voteAnswer : null
     * position :
     * likeCount : null
     * commentCount : null
     * like : 0
     * nickname : 阳光
     * avatar : http://zx-im-img.zhixun6.com/test-environment/avatar/862748e0-4545-43d7-a57b-107b41d34f64.JPEG
     * createTime : 1601178814864
     */

    private Long id;// 说说ID
    private Long uid;//
    private String content;// 说说内容
    private String attachment;// 说说附件
    private String vote;// 投票信息
    private VoteAnswerBean voteAnswer;// 投票结果
    private String position;// 说说发布时定位城市
    private Integer likeCount;// 点赞数量
    private Integer commentCount;// 评论数量
    private Integer like;// 我是否点了赞(0:否|1:是)
    private String nickname;// 发布者昵称
    private String avatar;// 发布者头像
    private long createTime;// 说说发布时间
    private Integer type;// 说说类型(0:无|1:图片|2:语音|4:视频|5:包含图片和视频|8:投票|9:包含图片和投票|10:包含语音和投票|12:包含视频和投票|13:包含图片、视频和投票)
    private boolean isShowAll;// 是否展开
    private boolean isRowsMore;// 是否超过3行
    //TODO 我的/好友动态单独字段
    private String city;//说说发布时定位城市
    private int isTop;//是否置顶 0:否|1:是
    private int visibility;//可见度(0:广场可见|1:好友可见|2:陌生人可见|3:自己可见)
    private boolean isFollow;// 是否关注
    private int refreshCount;// 刷新次数

    public int getRefreshCount() {
        return refreshCount;
    }

    public void setRefreshCount(int refreshCount) {
        this.refreshCount = refreshCount;
    }

    public int getIsTop() {
        return isTop;
    }

    public void setIsTop(int isTop) {
        this.isTop = isTop;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public VoteAnswerBean getVoteAnswer() {
        return voteAnswer;
    }

    public void setVoteAnswer(VoteAnswerBean voteAnswer) {
        this.voteAnswer = voteAnswer;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isShowAll() {
        return isShowAll;
    }

    public void setShowAll(boolean showAll) {
        isShowAll = showAll;
    }

    public boolean isRowsMore() {
        return isRowsMore;
    }

    public void setRowsMore(boolean rowsMore) {
        isRowsMore = rowsMore;
    }

    public static class VoteAnswerBean extends BaseBean {
        /**
         * selfAnswerItem : 0
         * sumDataList : [{"cnt":0,"id":0}]
         */

        private int selfAnswerItem;// 自己投票的选项，未投票-1，其他则为itemId:1-4
        private List<VoteAnswerBean.SumDataListBean> sumDataList;

        public int getSelfAnswerItem() {
            return selfAnswerItem;
        }

        public void setSelfAnswerItem(int selfAnswerItem) {
            this.selfAnswerItem = selfAnswerItem;
        }

        public List<VoteAnswerBean.SumDataListBean> getSumDataList() {
            return sumDataList;
        }

        public void setSumDataList(List<VoteAnswerBean.SumDataListBean> sumDataList) {
            this.sumDataList = sumDataList;
        }

        public static class SumDataListBean extends BaseBean {
            /**
             * cnt : 0
             * id : 0
             */

            private int cnt; // 投票数量
            private int id;// 选项ID

            public int getCnt() {
                return cnt;
            }

            public void setCnt(int cnt) {
                this.cnt = cnt;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }
        }
    }
}
