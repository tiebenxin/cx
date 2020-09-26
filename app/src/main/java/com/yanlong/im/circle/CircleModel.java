package com.yanlong.im.circle;

import com.yanlong.im.circle.server.CircleServer;

import net.cb.cb.library.base.bind.BaseModel;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.NetUtil;

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
public class CircleModel extends BaseModel implements CircleApi {

    private CircleServer server;

    public CircleModel() {
        server = NetUtil.getNet().create(CircleServer.class);
    }

    @Override
    public void createNewCircle(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.careateNewCircle(params), callback);
    }
}
