package com.yanlong.im.chat.action;

import com.google.gson.Gson;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.ReturnGroupInfoBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.server.MsgServer;
import com.yanlong.im.test.server.TestServer;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

public class MsgAction {
    private MsgServer server;
    private MsgDao dao;
    private Gson gson = new Gson();

    public MsgAction() {
        server = NetUtil.getNet().create(MsgServer.class);
        dao = new MsgDao();
    }


    public void groupCreate(final String name, final String avatar, final List<UserInfo> listDataTop, final CallBack<ReturnBean<ReturnGroupInfoBean>> callback) {
        /*List<Long> ulist = new ArrayList<>();

        for (UserInfo userInfo:listDataTop){
            ulist.add(userInfo.getUid());
        }*/

        NetUtil.getNet().exec(server.groupCreate(name, avatar, gson.toJson(listDataTop)), new CallBack<ReturnBean<ReturnGroupInfoBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<ReturnGroupInfoBean>> call, Response<ReturnBean<ReturnGroupInfoBean>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {//存库
                    String id = response.body().getData().getGid();
                    dao.sessionCreate(id, null);
                    Group group = new Group();
                    group.setAvatar(avatar);
                    group.setGid(id);
                    group.setName(name);
                    RealmList<UserInfo> users = new RealmList();
                    users.addAll(listDataTop);
                    group.setUsers(users);
                    DaoUtil.update(group);
                }
                callback.onResponse(call, response);
            }
        });

    }

    public void groupQuit(final String id, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.groupQuit(id), new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    dao.sessionDel(null, id);
                }
                callback.onResponse(call, response);
            }
        });
    }

    public void groupRemove(String id, List<UserInfo> members, CallBack<ReturnBean> callback) {
        List<Long> ulist = new ArrayList<>();

        for (UserInfo userInfo : members) {
            ulist.add(userInfo.getUid());
        }
        NetUtil.getNet().exec(server.groupRemove(id, gson.toJson(ulist)), callback);
    }

    public void groupAdd(String id, List<UserInfo> members, CallBack<ReturnBean> callback) {

        NetUtil.getNet().exec(server.groupAdd(id, gson.toJson(members)), callback);
    }

    public void groupDestroy(final String id, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.groupDestroy(id), new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    dao.sessionDel(null, id);
                }
                callback.onResponse(call, response);
            }
        });

    }


    /***
     * 获取某个用户的数据
     * @return
     */
    public List<MsgAllBean> getMsg4User(String gid, Long uid, Integer page) {
        if (StringUtil.isNotNull(gid)) {
            return dao.getMsg4Group(gid, page);
        }
        return dao.getMsg4User(uid, page);
    }


    /***
     * 获取群信息
     * @param gid
     * @param callback
     */
    public void groupInfo(final String gid, final Callback<ReturnBean<ReturnGroupInfoBean>> callback) {


        NetUtil.getNet().exec(server.groupInfo(gid), new CallBack<ReturnBean<ReturnGroupInfoBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<ReturnGroupInfoBean>> call, Response<ReturnBean<ReturnGroupInfoBean>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {//保存群友信息到数据库
                    dao.groupNumberSave(response.body().getData());

                    response.body().getData().setMembers(DaoUtil.findOne(Group.class, "gid", gid).getUsers());
                }
                callback.onResponse(call, response);


            }
        });
    }

    /***
     * 根据key查询群
     */
    public List<Group> searchGroup4key(String key) {
        Realm realm = DaoUtil.open();
        List<Group> ret = new ArrayList<>();
        RealmResults<Group> users = realm.where(Group.class)
                .contains("name", key).findAll();
        if (users != null)
            ret = realm.copyFromRealm(users);
        realm.close();
        return ret;
    }

    /***
     * 根据key查询消息
     */
    public List<MsgAllBean> searchMsg4key(String key, String gid, Long uid) {


        Realm realm = DaoUtil.open();
        List<MsgAllBean> ret = new ArrayList<>();
        RealmResults<MsgAllBean> msg;
        if (StringUtil.isNotNull(gid)) {//群
            msg = realm.where(MsgAllBean.class)
                    .equalTo("gid", gid).and()
                    .contains("chat.msg", key)
                    .sort("timestamp", Sort.DESCENDING)
                    .findAll();
        } else {//单人
            msg = realm.where(MsgAllBean.class)
                    .equalTo("from_uid", uid).or().equalTo("to_uid", uid).and()
                    .equalTo("gid", "").and()
                    .contains("chat.msg", key)
                    .sort("timestamp", Sort.DESCENDING)
                    .findAll();
        }

        if (msg != null)
            ret = realm.copyFromRealm(msg);
        realm.close();
        return ret;
    }


    /***
     * 群详情开关
     * @param gid
     * @param notNotify
     * @param saved
     * @param needVerification
     */
    public void groupSwitch(final String gid, final Integer istop, final Integer notNotify, final Integer saved, final Integer needVerification, final Callback<ReturnBean> cb){

        if(istop!=null){
            dao. saveSession4Switch(gid,istop,notNotify,saved,needVerification);
            return;
        }

        Callback<ReturnBean> callback=new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if(response.body()==null)
                    return;
                if(response.body().isOk()){//存库
                   dao. saveSession4Switch(gid,istop,notNotify,saved,needVerification);
                }

                cb.onResponse(call,response);

            }
        };

        if(needVerification!=null){
            NetUtil.getNet().exec(server.groupSwitch(gid,needVerification),callback);
        }else {
            NetUtil.getNet().exec(server.groupSwitch(gid,notNotify,saved),callback);
        }


    }


    public void getMySaved(Callback<ReturnBean<ReturnGroupInfoBean>> callback){
       // NetUtil
    }



}
