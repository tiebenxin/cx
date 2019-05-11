package com.yanlong.im.chat.action;

import com.google.gson.Gson;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.server.MsgServer;
import com.yanlong.im.test.server.TestServer;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.NetUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;
import retrofit2.http.Field;

public class MsgAction {
    private MsgServer server;
    private MsgDao dao;
    private Gson gson=new Gson();

    public MsgAction() {
        server = NetUtil.getNet().create(MsgServer.class);
        dao=new MsgDao();
    }


    public void groupCreate(String id,String name,String avatar,List<UserInfo> listDataTop, CallBack<ReturnBean> callback) {
        List<Long> ulist = new ArrayList<>();

        for (UserInfo userInfo:listDataTop){
            ulist.add(userInfo.getUid());
        }

        NetUtil.getNet().exec(server.groupCreate(id,name,avatar,gson.toJson(ulist)), callback);
        dao.sessionCreate(id,null);
        Group group=new Group();
        group.setAvatar(avatar);
        group.setGid(id);
        group.setName(name);
        RealmList<UserInfo> users=new RealmList();
        users.addAll(listDataTop);
        group.setUsers(users);
        DaoUtil.update(group);
    }
    public void groupQuit(String id, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.groupQuit(id), callback);
    }
    public void groupRemove(String id,List<Long> members, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.groupRemove(id,gson.toJson(members)), callback);
    }
    public void groupDestroy(String id, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.groupDestroy(id), callback);
    }



    /***
     * 获取某个用户的数据
     * @return
     */
    public List<MsgAllBean> getMsg4User(Long uid,Integer page){
        return dao.getMsg4User(uid,page);
    }

}
