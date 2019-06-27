package com.yanlong.im.chat.action;

import com.google.gson.Gson;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.GroupJoinBean;
import com.yanlong.im.chat.bean.GroupUserInfo;
import com.yanlong.im.chat.bean.MsgAllBean;

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


    public void groupCreate(final String name, final String avatar, final List<UserInfo> listDataTop, final CallBack<ReturnBean<Group>> callback) {
        /*List<Long> ulist = new ArrayList<>();

         */
        List<GroupUserInfo> listDataTop2 = new ArrayList<>();
        for (int i = 0; i < listDataTop.size(); i++) {
            GroupUserInfo userInfo = new GroupUserInfo();
            userInfo.setUid(listDataTop.get(i).getUid()+"");
            userInfo.setMembername(listDataTop.get(i).getName());
            userInfo.setAvatar(listDataTop.get(i).getHead());
            listDataTop2.add(userInfo);

        }

        NetUtil.getNet().exec(server.groupCreate(name, avatar, gson.toJson(listDataTop2)), new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {//存库
                    String id = response.body().getData().getGid();
                    dao.groupCreate(id, avatar, name, listDataTop);

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
        List<GroupUserInfo> groupUserInfos = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            GroupUserInfo groupUserInfo = new GroupUserInfo();
            groupUserInfo.setUid(members.get(i).getUid()+"");
            groupUserInfo.setAvatar(members.get(i).getHead());
            groupUserInfo.setMembername(members.get(i).getName());
            groupUserInfos.add(groupUserInfo);
        }
        NetUtil.getNet().exec(server.groupAdd(id, gson.toJson(groupUserInfos)), callback);
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
/*    public List<MsgAllBean> getMsg4User(String gid, Long uid, Integer page) {
        if (StringUtil.isNotNull(gid)) {
            return dao.getMsg4Group(gid, page);
        }
        return dao.getMsg4User(uid, page);
    }*/
    public List<MsgAllBean> getMsg4User(String gid, Long uid, Long time) {
        if (StringUtil.isNotNull(gid)) {
            return dao.getMsg4Group(gid, time);
        }
        return dao.getMsg4User(uid, time);
    }

    public List<MsgAllBean> getMsg4UserHistory(String gid, Long uid, Long stime) {
        if (StringUtil.isNotNull(gid)) {
            return dao.getMsg4GroupHistory(gid, stime);
        }
        return dao.getMsg4UserHistory(uid, stime);
    }

    /***
     * 获取群信息,并缓存
     * @param gid
     * @param callback
     */
    public void groupInfo(final String gid, final Callback<ReturnBean<Group>> callback) {

        if (NetUtil.isNetworkConnected()) {
            NetUtil.getNet().exec(server.groupInfo(gid), new CallBack<ReturnBean<Group>>() {
                @Override
                public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                    if (response.body() == null)
                        return;
                    if (response.body().isOk()) {//保存群友信息到数据库
                        response.body().getData().getMygroupName();
                        dao.groupNumberSave(response.body().getData());

                        response.body().getData().setUsers(DaoUtil.findOne(Group.class, "gid", gid).getUsers());
                    }
                    callback.onResponse(call, response);


                }
            });
        } else {//从缓存中读
            Group rdata = dao.groupNumberGet(gid);

            ReturnBean<Group> body = new ReturnBean<>();
            body.setCode(0l);
            body.setData(rdata);
            Response<ReturnBean<Group>> response = Response.success(body);
            callback.onResponse(null, response);
        }

    }

    /***
     * 根据key查询群
     */
    public List<Group> searchGroup4key(String key) {

        return dao.searchGroup4key(key);
    }

    /***
     * 根据key查询消息
     */
    public List<MsgAllBean> searchMsg4key(String key, String gid, Long uid) {

        return dao.searchMsg4key(key, gid, uid);
    }

    /***
     * 群详情开关
     * @param gid
     * @param notNotify
     * @param saved
     * @param needVerification
     */
    public void groupSwitch(final String gid, final Integer istop, final Integer notNotify, final Integer saved, final Integer needVerification, final Callback<ReturnBean> cb) {

        //存服务器
    /*    if (istop != null) {
            dao.saveSession4Switch(gid, istop, notNotify, saved, needVerification);
            return;
        }*/

        Callback<ReturnBean> callback = new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {//存库
                    dao.saveSession4Switch(gid, istop, notNotify, saved, needVerification);
                }

                cb.onResponse(call, response);

            }
        };

        if (needVerification != null) {
            NetUtil.getNet().exec(server.groupSwitch(gid, needVerification), callback);
        } else {
            NetUtil.getNet().exec(server.groupSwitch(gid, istop, notNotify, saved), callback);
        }


    }

    /***
     * 单人详情的开关
     * @param isMute
     * @param istop
     */
    public void sessionSwitch(Long uid, Integer isMute, Integer istop, Callback<ReturnBean> callback) {
        if (isMute != null)
            NetUtil.getNet().exec(server.friendMute(uid, isMute), callback);
        if (istop != null)
            NetUtil.getNet().exec(server.friendTop(uid, istop), callback);
    }

    /***
     * 清理所有的消息
     */
    public void msgDelAll() {
        dao.msgDelAll();
    }


    /**
     * 查询已保存的群聊
     */
    public void getMySaved(final Callback<ReturnBean<List<Group>>> callback) {

        NetUtil.getNet().exec(server.getMySaved(), new CallBack<ReturnBean<List<Group>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<Group>>> call, Response<ReturnBean<List<Group>>> response) {
                if (response.body() == null)
                    return;
                callback.onResponse(call, response);


                for (Group ginfo : response.body().getData()) {
                    //保存群信息到本地
                    Group group = new Group();
                    group.setGid(ginfo.getGid());
                    group.setAvatar(ginfo.getAvatar());
                    group.setName(ginfo.getName());
                    dao.groupSave(group);
                }

            }
        });
    }

    /**
     * 加入群聊
     */
    public void joinGroup(String gid, Long uid, String membername, Callback<ReturnBean<GroupJoinBean>> callback) {
        NetUtil.getNet().exec(server.joinGroup(gid, uid, membername), callback);
    }


    /**
     * 修改群名称
     */
    public void changeGroupName(String gid, String name, Callback<ReturnBean> callback) {
        NetUtil.getNet().exec(server.changeGroupName(gid, name), callback);
    }

    /**
     * 修改群成员昵称
     */
    public void changeMemberName(String gid, String name, Callback<ReturnBean> callback) {
        NetUtil.getNet().exec(server.changeMemberName(gid, name), callback);
    }


    /**
     * 同意进群
     */
    public void groupRequest(final String aid, String gid, String uid, String name, final Callback<ReturnBean> callback) {
        NetUtil.getNet().exec(server.groupRequest(gid, uid, name), new Callback<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null)
                    return;
                callback.onResponse(call, response);
                if (response.body().isOk()) {
                    dao.groupAcceptRemove(aid);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }


    /**
     * 删除群申请
     * */
    public void groupRequestDelect(String aid){
        dao.groupAcceptRemove(aid);
    }




    /**
     * 修改群公告
     * */
    public void changeGroupAnnouncement(String gid, String announcement, Callback<ReturnBean> callback){
        NetUtil.getNet().exec(server.changeGroupAnnouncement(gid, announcement), callback);
    }


}
