package com.yanlong.im.circle.bean;

import net.cb.cb.library.base.BaseBean;

import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-30
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class CircleCommentBean extends BaseBean {

    /**
     * browseCount : 0
     * commentList : [{"avatar":"","content":"","createTime":"","id":0,"nickname":"","replyAvatar":"","replyNickname":"","replyUid":0,"uid":0}]
     */
    private int browseCount;
    private List<CommentListBean> commentList;

    public int getBrowseCount() {
        return browseCount;
    }

    public void setBrowseCount(int browseCount) {
        this.browseCount = browseCount;
    }

    public List<CommentListBean> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentListBean> commentList) {
        this.commentList = commentList;
    }

    public static class CommentListBean extends BaseBean{
        /**
         * id : 508712863167287316
         * content : 测试数据
         * uid : 101521
         * nickname : 好好学习吧
         * avatar : http://zx-im-img.oss-accelerate.aliyuncs.com/test-environment/avatar/android/101521/228a8b1b-8318-4fb3-996e-437ef273730b.jpg
         * replyUid : 0
         * replyNickname : null
         * replyAvatar : null
         * createTime : 1601453064000
         */

        private Long id;// 评论id
        private String content;// 评论内容
        private Long uid;// 评论人id
        private String nickname;// 昵称
        private String avatar;// 头像
        private Long replyUid;// 回复id
        private String replyNickname;// 回复人昵称
        private String replyAvatar;// 回复人头像
        private Long createTime;// 创建时间

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Long getUid() {
            return uid;
        }

        public void setUid(Long uid) {
            this.uid = uid;
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

        public Long getReplyUid() {
            return replyUid;
        }

        public void setReplyUid(Long replyUid) {
            this.replyUid = replyUid;
        }

        public String getReplyNickname() {
            return replyNickname;
        }

        public void setReplyNickname(String replyNickname) {
            this.replyNickname = replyNickname;
        }

        public String getReplyAvatar() {
            return replyAvatar;
        }

        public void setReplyAvatar(String replyAvatar) {
            this.replyAvatar = replyAvatar;
        }

        public Long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }
    }
}
