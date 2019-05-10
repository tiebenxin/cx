package com.yanlong.im.chat.server;

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
    Call<ReturnBean> groupCreate(@Field("gid") String gid, @Field("name") String name, @Field("avatar") String avatar, @Field("members") List<Long> members);

    @POST("/group/quit")
    @FormUrlEncoded
    Call<ReturnBean> groupQuit(@Field("gid") String gid);


    @POST("/group/remove-members")
    @FormUrlEncoded
    Call<ReturnBean> groupRemove(@Field("gid") String gid, @Field("members") List<Long> members);

    @POST("/group/destroy")
    @FormUrlEncoded
    Call<ReturnBean> groupDestroy(@Field("gid") String gid);
}
