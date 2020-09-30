package com.yanlong.im.circle.bean;

import net.cb.cb.library.base.BaseBean;

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

    private Long id;
    private String content;
    private Long uid;
    private String nickname;
    private String avatar;
    private Long replyUid;
    private String replyNickname;
    private String replyAvatar;
    private Long createTime;

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
