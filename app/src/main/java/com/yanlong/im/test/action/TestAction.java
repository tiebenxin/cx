package com.yanlong.im.test.action;

import com.yanlong.im.test.bean.Test2Bean;
import com.yanlong.im.test.server.TestServer;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.NetUtil;

/***
 * @author jyj
 * @date 2016/12/20
 */
public class TestAction {
    private TestServer server;

    public TestAction() {
        server = NetUtil.getNet().create(TestServer.class);
    }


    public void testMtd(CallBack<ReturnBean<Test2Bean>> callback, String id) {


        NetUtil.getNet().exec( server.testMtd( id),callback);
    }

}

