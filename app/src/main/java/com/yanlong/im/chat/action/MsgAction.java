package com.yanlong.im.chat.action;

import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.server.MsgServer;
import com.yanlong.im.test.server.TestServer;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.NetUtil;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MsgAction {
    private MsgServer server;
    private MsgDao dao;

    public MsgAction() {
        server = NetUtil.getNet().create(MsgServer.class);
        dao=new MsgDao();
    }


    public void test(String id, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.test(id), callback);
    }

    /***
     * 获取
     * @return
     */
    public List<MsgAllBean> getMsg4User(Long uid,Integer page){
        return dao.getMsg4User(uid,page);
    }

}
