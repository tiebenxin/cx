package com.yanlong.im.circle.server;

import com.yanlong.im.circle.bean.CircleCommentBean;
import com.yanlong.im.circle.bean.MessageInfoBean;

import net.cb.cb.library.bean.ReturnBean;

import java.util.List;
import java.util.WeakHashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-25
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public interface CircleServer {

    @POST("square/moment/publish")
    Call<ReturnBean<MessageInfoBean>> careateNewCircle(@Body WeakHashMap<String, Object> params);

    @POST("square/moment/follow-moment-list")
    Call<ReturnBean<List<MessageInfoBean>>> getFollowMomentList(@Body WeakHashMap<String, Object> params);

    @POST("square/moment/recommend-list")
    Call<ReturnBean<List<MessageInfoBean>>> getRecommendList(@Body WeakHashMap<String, Object> params);

    @POST("square/moment/query-by-id")
    Call<ReturnBean<MessageInfoBean>> queryById(@Body WeakHashMap<String, Object> params);

    @POST("square/vote/answer")
    Call<ReturnBean> voteAnswer(@Body WeakHashMap<String, Object> params);

    //说说添加点赞
//    @POST("square/moment/like")
    @POST("square/like/add")
    Call<ReturnBean> addLike(@Body WeakHashMap<String, Object> params);

    //评论添加或者取消点赞
//    @POST("square/moment/cancel-like")
    @POST("square/like/cancel")
    Call<ReturnBean> addOrCancelLikeToComment(@Body WeakHashMap<String, Object> params);


    //评论取消点赞
//    @POST("square/moment/cancel-like")
    @POST("square/like/cancel")
    Call<ReturnBean> cancelLike(@Body WeakHashMap<String, Object> params);



    @POST("follow/add")
    Call<ReturnBean> followAdd(@Body WeakHashMap<String, Object> params);

    @POST("follow/cancel")
    Call<ReturnBean> followCancle(@Body WeakHashMap<String, Object> params);

    @POST("square/comment/add")
    Call<ReturnBean> circleComment(@Body WeakHashMap<String, Object> params);

    @POST("square/comment/list")
    Call<ReturnBean<CircleCommentBean>> circleCommentList(@Body WeakHashMap<String, Object> params);

    @POST("square/comment/del")
    Call<ReturnBean> delComment(@Body WeakHashMap<String, Object> params);

    @POST("square/forbid-see/add")
    Call<ReturnBean> addSee(@Body WeakHashMap<String, Object> params);

    @POST("square/moment/del")
    Call<ReturnBean> circleDelete(@Body WeakHashMap<String, Object> params);

    @POST("square/moment/update-visibility")
    Call<ReturnBean> updateVisibility(@Body WeakHashMap<String, Object> params);

    @POST("square/user/latest-data")
    Call<ReturnBean> latestData(@Body WeakHashMap<String, Object> params);
}
