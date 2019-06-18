package com.yanlong.im.chat.server;

import com.yanlong.im.chat.bean.GroupJoinBean;
import com.yanlong.im.chat.bean.Group;

import net.cb.cb.library.bean.ReturnBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MsgServer {
    @POST("/group/create")
    @FormUrlEncoded
    Call<ReturnBean<Group>> groupCreate(@Field("name") String name, @Field("avatar") String avatar, @Field("@members") String membersJson);

    @POST("/group/quit")
    @FormUrlEncoded
    Call<ReturnBean> groupQuit(@Field("gid") String gid);


    @POST("/group/remove-members")
    @FormUrlEncoded
    Call<ReturnBean> groupRemove(@Field("gid") String gid, @Field("@members") String membersJson);

    @POST("/group/append-members")
    @FormUrlEncoded
    Call<ReturnBean> groupAdd(@Field("gid") String gid, @Field("@members") String membersJson);

    @POST("/group/destroy")
    @FormUrlEncoded
    Call<ReturnBean> groupDestroy(@Field("gid") String gid);

    @POST("/group/get-group-data")
    @FormUrlEncoded
    Call<ReturnBean<Group>> groupInfo(@Field("gid") String gid);

    @POST("/group/change-member-switch")
    @FormUrlEncoded
    Call<ReturnBean> groupSwitch(@Field("gid") String gid, @Field("toTop") Integer isTop, @Field("notNotify") Integer notNotify, @Field("saved") Integer saved);

    @POST("/group/change-group-switch")
    @FormUrlEncoded
    Call<ReturnBean> groupSwitch(@Field("gid") String gid, @Field("needVerification") Integer needVerification);

    @POST("group/get-my-saved")
    Call<ReturnBean<List<Group>>> getMySaved();

    @POST("/friends/set-friend-disturb")
    @FormUrlEncoded
    Call<ReturnBean> friendMute(@Field("friend") Long uid, @Field("disturb") Integer isMute);

    @POST("/friends/set-friend-top")
    @FormUrlEncoded
    Call<ReturnBean> friendTop(@Field("friend") Long uid, @Field("istop") Integer istop);

    @POST("/group/request-join")
    @FormUrlEncoded
    Call<ReturnBean<GroupJoinBean>> joinGroup(@Field("gid") String gid, @Field("uid") Long uid, @Field("membername") String membername);

    @POST("/group/change-group-name")
    @FormUrlEncoded
    Call<ReturnBean> changeGroupName(@Field("gid") String gid, @Field("name") String name);

    @POST("/group/change-member-name")
    @FormUrlEncoded
    Call<ReturnBean> changeMemberName(@Field("gid") String gid, @Field("name") String name);

    @POST("/group/accept-request")
    @FormUrlEncoded
    Call<ReturnBean> groupRequest(@Field("gid") String gid, @Field("uid") String uid, @Field("membername") String nickname);

    @POST("/group/edit-announcement")
    @FormUrlEncoded
    Call<ReturnBean> changeGroupAnnouncement(@Field("gid") String gid,@Field("announcement") String announcement);
}
