package com.yanlong.im.circle.follow;

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
}
