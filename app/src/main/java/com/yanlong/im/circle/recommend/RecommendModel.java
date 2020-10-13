package com.yanlong.im.circle.recommend;

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
public class RecommendModel extends BaseModel implements RecommendApi {

    private CircleServer server;

    public RecommendModel() {
        server = NetUtil.getNet().create(CircleServer.class);
    }

    @Override
    public void getRecommendList(WeakHashMap<String, Object> params, CallBack<ReturnBean<List<MessageInfoBean>>> callback) {
        NetUtil.getNet().exec(server.getRecommendList(params), callback);
    }

    @Override
    public void comentLike(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.comentLike(params), callback);
    }

    @Override
    public void comentCancleLike(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.comentCancleLike(params), callback);
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
    public void addSee(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.addSee(params), callback);
    }

    @Override
    public void queryById(WeakHashMap<String, Object> params, CallBack<ReturnBean<MessageInfoBean>> callback) {
        NetUtil.getNet().exec(server.queryById(params), callback);
    }
}
