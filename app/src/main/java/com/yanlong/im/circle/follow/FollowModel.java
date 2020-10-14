package com.yanlong.im.circle.follow;

import com.yanlong.im.circle.bean.CircleCommentBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.server.CircleServer;

import net.cb.cb.library.base.bind.BaseModel;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.NetUtil;

import java.util.List;
import java.util.WeakHashMap;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020/9/7 0007
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class FollowModel extends BaseModel implements FollowApi {

    private CircleServer server;

    public FollowModel() {
        server = NetUtil.getNet().create(CircleServer.class);
    }

    @Override
    public void getFollowMomentList(WeakHashMap<String, Object> params, CallBack<ReturnBean<List<MessageInfoBean>>> callback) {
        NetUtil.getNet().exec(server.getFollowMomentList(params), callback);
    }

    @Override
    public void queryById(WeakHashMap<String, Object> params, CallBack<ReturnBean<MessageInfoBean>> callback) {
        NetUtil.getNet().exec(server.queryById(params), callback);
    }

    @Override
    public void voteAnswer(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.voteAnswer(params), callback);
    }

    @Override
    public void comentLike(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.comentLike(params), callback);
    }

    @Override
    public void followAdd(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.followAdd(params), callback);
    }

    @Override
    public void followCancle(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.followCancle(params), callback);
    }

    @Override
    public void comentCancleLike(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.comentCancleLike(params), callback);
    }

    @Override
    public void circleComment(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.circleComment(params), callback);
    }

    @Override
    public void delComment(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.delComment(params), callback);
    }

    @Override
    public void circleCommentList(WeakHashMap<String, Object> params, CallBack<ReturnBean<CircleCommentBean>> callback) {
        NetUtil.getNet().exec(server.circleCommentList(params), callback);
    }

}
