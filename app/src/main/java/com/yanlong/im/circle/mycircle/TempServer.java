package com.yanlong.im.circle.mycircle;

import com.yanlong.im.circle.bean.CircleTrendsBean;
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

    @POST("/square/user/access-records")
    @FormUrlEncoded
    Call<ReturnBean<List<FriendUserBean>>> httpGetWhoSeeMeList(@Field("currentPage") int page, @Field("pageSize") int pageSize, @Field("type") int type);

    @POST("/square/user/access-record-del")
    @FormUrlEncoded
    Call<ReturnBean> httpDeleteVisitRecord(@Field("uid") long uid);

    @POST("/square/moment/my-moment-home")
    @FormUrlEncoded
    Call<ReturnBean<CircleTrendsBean>> httpGetMyTrends(@Field("currentPage") int page, @Field("pageSize") int pageSize);

    @POST("/square/user/update-bg-image")
    @FormUrlEncoded
    Call<ReturnBean> httpSetBackground(@Field("bgImage") String bgImage);

    @POST("/square/moment/like")
    @FormUrlEncoded
    Call<ReturnBean> httpLike(@Field("momentId") long momentId,@Field("momentUid") long momentUid);

    @POST("/square/moment/cancel-like")
    @FormUrlEncoded
    Call<ReturnBean> httpCancleLike(@Field("momentId") long momentId,@Field("momentUid") long momentUid);

    @POST("/square/moment/update-top")
    @FormUrlEncoded
    Call<ReturnBean> httpIsTop(@Field("momentId") long momentId,@Field("isTop") int isTop);

    @POST("/square/moment/update-visibility")
    @FormUrlEncoded
    Call<ReturnBean> httpSetVisibility(@Field("momentId") long momentId,@Field("visibility") int visibility);

}
