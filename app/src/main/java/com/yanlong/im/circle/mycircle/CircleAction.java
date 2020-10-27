package com.yanlong.im.circle.mycircle;

import com.yanlong.im.circle.bean.CircleTrendsBean;
import com.yanlong.im.circle.bean.FriendUserBean;
import com.yanlong.im.circle.bean.NewTrendDetailsBean;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.NetUtil;

import java.util.List;

/**
 * @类名：临时用网络请求，后续可合并共用一个
 * @Date：2020/9/23
 * @by zjy
 * @备注：
 */

public class CircleAction {
    private CircleServer server;

    public CircleAction() {
        server = NetUtil.getNet().create(CircleServer.class);
    }

    /**
     * 获取我关注的人列表
     */
    public void httpGetMyFollowList(final int currentPage, final int pageSize, final CallBack<ReturnBean<List<FriendUserBean>>>  callback) {
        NetUtil.getNet().exec(server.httpGetMyFollowList(currentPage,pageSize), callback);
    }

    /**
     * 获取关注我的人列表
     */
    public void httpGetFollowMeList(final int currentPage, final int pageSize, final CallBack<ReturnBean<List<FriendUserBean>>>  callback) {
        NetUtil.getNet().exec(server.httpGetFollowMeList(currentPage,pageSize), callback);
    }

    /**
     * 关注用户
     */
    public void httpToFollow(final long uid, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.httpToFollow(uid), callback);
    }

    /**
     * 取消关注用户
     */
    public void httpCancelFollow(final long uid, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.httpCancelFollow(uid), callback);
    }

    /**
     * 获取谁看过我列表
     * @param type 1 我看过谁 2 谁看过我
     */

    public void httpGetWhoSeeMeList(final int currentPage, final int pageSize, final int type, final CallBack<ReturnBean<List<FriendUserBean>>>  callback) {
        NetUtil.getNet().exec(server.httpGetWhoSeeMeList(currentPage,pageSize,type), callback);
    }

    /**
     * 删除访问记录 (我看过谁)
     * @param uid 对方uid
     */

    public void httpDeleteVisitRecord(final long uid, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.httpDeleteVisitRecord(uid), callback);
    }


    /**
     * 获取我的动态(说说主页及列表)
     */

    public void httpGetMyTrends(final int currentPage, final int pageSize, final CallBack<ReturnBean<CircleTrendsBean>> callback) {
        NetUtil.getNet().exec(server.httpGetMyTrends(currentPage,pageSize), callback);
    }

    /**
     * 获取好友的动态(说说主页及列表)
     */

    public void httpGetFriendTrends(final int currentPage, final int pageSize,final long uid, final CallBack<ReturnBean<CircleTrendsBean>> callback) {
        NetUtil.getNet().exec(server.httpGetFriendTrends(currentPage,pageSize,uid), callback);
    }

    /**
     * 更新背景图
     */
    public void httpSetBackground(final String url, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.httpSetBackground(url), callback);
    }

    /**
     * 点赞
     */
    public void httpLike(final long id,final long uid, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.httpLike(id,uid), callback);
    }

    /**
     * 取消点赞
     */
    public void httpCancleLike(final long id,final long uid, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.httpCancleLike(id,uid), callback);
    }

    /**
     * 置顶/取消置顶
     */
    public void httpIsTop(final long id,final int isTop, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.httpIsTop(id,isTop), callback);
    }

    /**
     * 设置可见度
     */
    public void httpSetVisibility(final long id,final int visibility, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.httpSetVisibility(id,visibility), callback);
    }


    /**
     * 删除一条动态
     */
    public void httpDeleteTrend(final long id, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.httpDeleteTrend(id), callback);
    }

    /**
     * 获取我不看的人列表
     */
    public void httpGetNotSeeList(final int currentPage, final int pageSize, final CallBack<ReturnBean<List<FriendUserBean>>>  callback) {
        NetUtil.getNet().exec(server.httpGetNotSeeList(currentPage,pageSize), callback);
    }

    /**
     * 取消不看TA
     */
    public void httpDeleteNotSee(final long id, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.httpDeleteNotSee(id), callback);
    }

    /**
     * 广场投诉
     */
    public void httpCircleComplaint(long momentUid,long commentId, int complaintType, long defendantUid, String illegalDescription, String illegalImage, long momentId, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.httpCircleComplaint(momentUid,commentId,complaintType,defendantUid,illegalDescription,illegalImage,momentId), callback);
    }

    /**
     * 评论点赞/取消赞
     */
    public void httpCommentLike(final long commentId,final int isLike,final long momentId,final long momentUid, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.httpCommentLike(commentId,isLike,momentId,momentUid), callback);
    }

    /**
     * 获取新的动态详情接口(包括头像、评论列表)
     */
    public void httpGetNewDetails(final long momentId,final long momentUid, final CallBack<ReturnBean<NewTrendDetailsBean>> callback) {
        NetUtil.getNet().exec(server.httpGetNewDetails(momentId,momentUid), callback);
    }

}
