package com.yanlong.im.circle.bean;

import net.cb.cb.library.base.BaseBean;

import java.util.List;

/**
 * @类名：新的动态详情实体类
 * @Date：2020/10/27
 * @by zjy
 * @备注： ->主要用于解析互动消息点击进详情(单独调用)
 */
public class NewTrendDetailsBean extends BaseBean {

    private List<CircleCommentBean.CommentListBean> commentVoList;//评论列表
    private int myFollow;//我是否关注
    private MessageInfoBean otherMomentVo;//详情数据

    public List<CircleCommentBean.CommentListBean> getCommentVoList() {
        return commentVoList;
    }

    public void setCommentVoList(List<CircleCommentBean.CommentListBean> commentVoList) {
        this.commentVoList = commentVoList;
    }

    public int getMyFollow() {
        return myFollow;
    }

    public void setMyFollow(int myFollow) {
        this.myFollow = myFollow;
    }

    public MessageInfoBean getOtherMomentVo() {
        return otherMomentVo;
    }

    public void setOtherMomentVo(MessageInfoBean otherMomentVo) {
        this.otherMomentVo = otherMomentVo;
    }
}
