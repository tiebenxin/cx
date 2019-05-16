package com.yanlong.im.chat.server;

import com.yanlong.im.chat.bean.ReturnGroupInfoBean;
import com.yanlong.im.test.bean.Test2Bean;

import net.cb.cb.library.bean.ReturnBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MsgServer {
    @POST("/group/create")
    @FormUrlEncoded
    Call<ReturnBean<ReturnGroupInfoBean>> groupCreate(@Field("name") String name, @Field("avatar") String avatar, @Field("@members") String membersJson);

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
    Call<ReturnBean<ReturnGroupInfoBean>> groupInfo(@Field("gid") String gid);



    @POST("/group/change-member-switch")
    @FormUrlEncoded
    Call<ReturnBean> groupSwitch(@Field("gid") String gid,@Field("notNotify") Integer notNotify,@Field("saved") Integer saved);

    @POST("/group/change-group-switch")
    @FormUrlEncoded
    Call<ReturnBean> groupSwitch(@Field("gid") String gid,@Field("needVerification") Integer needVerification);


    @POST("group/get-my-saved")
    Call<ReturnBean<ReturnGroupInfoBean>> getMySaved();

}
