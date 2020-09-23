package com.yanlong.im.circle.mycircle;

import com.yanlong.im.circle.bean.FriendUserBean;

import net.cb.cb.library.bean.ReturnBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TempServer {

    @POST("/follow/my-follow")
    @FormUrlEncoded
    Call<ReturnBean<List<FriendUserBean>>> httpGetMyFollowList(@Field("currentPage") int page, @Field("pageSize") int pageSize);

    @POST("/follow/follow-my")
    @FormUrlEncoded
    Call<ReturnBean<List<FriendUserBean>>> httpGetFollowMeList(@Field("currentPage") int page, @Field("pageSize") int pageSize);

    @POST("/follow/add")
    @FormUrlEncoded
    Call<ReturnBean> httpToFollow(@Field("followId") long followId);

    @POST("/follow/cancel")
    @FormUrlEncoded
    Call<ReturnBean> httpCancelFollow(@Field("followId") long followId);

}
