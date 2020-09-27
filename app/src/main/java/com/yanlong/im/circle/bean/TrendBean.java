package com.yanlong.im.circle.bean;

import net.cb.cb.library.base.BaseBean;

import java.util.List;

/**
 * @类名：单条说说实体类
 * @Date：2020/9/26
 * @by zjy
 * @备注：
 */
public class TrendBean extends BaseBean {
    private String attachment;//说说附件列表
    private String city;//说说发布时定位城市
    private int commentCount;//评论数量
    private String content;//说说内容
    private String createTime;//说说发布时间
    private long id;//说说ID
    private int isTop;//是否置顶 0:否|1:是
    private int like;//我是否点了赞 (0:否|1:是)
    private int likeCount;//点赞数量
    private String position;//说说发布时详细位置
    private long uid;//说说发布者ID
    private String vote;//投票信息
    private int visibility;//可见度(0:广场可见|1:好友可见|2:陌生人可见|3:自己可见)
    private VoteAnswerBean voteAnswer;//投票结果

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIsTop() {
        return isTop;
    }

    public void setIsTop(int isTop) {
        this.isTop = isTop;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
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

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public static class VoteAnswerBean {

        private int selfAnswerItem;//自己投票的选项，未投票-1，其他则为itemId:1-4
        private List<SumDataListBean> sumDataList;//各ItemId投票汇总数据

        public int getSelfAnswerItem() {
            return selfAnswerItem;
        }

        public void setSelfAnswerItem(int selfAnswerItem) {
            this.selfAnswerItem = selfAnswerItem;
        }

        public List<SumDataListBean> getSumDataList() {
            return sumDataList;
        }

        public void setSumDataList(List<SumDataListBean> sumDataList) {
            this.sumDataList = sumDataList;
        }

        public static class SumDataListBean {
            private int cnt;//投票数量
            private int id;//选项ID:1-4

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
