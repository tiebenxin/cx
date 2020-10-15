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
    Call<ReturnBean> careateNewCircle(@Body WeakHashMap<String, Object> params);

    @POST("square/moment/follow-moment-list")
    Call<ReturnBean<List<MessageInfoBean>>> getFollowMomentList(@Body WeakHashMap<String, Object> params);

    @POST("square/moment/recommend-list")
    Call<ReturnBean<List<MessageInfoBean>>> getRecommendList(@Body WeakHashMap<String, Object> params);

    @POST("square/moment/query-by-id")
    Call<ReturnBean<MessageInfoBean>> queryById(@Body WeakHashMap<String, Object> params);

    @POST("square/vote/answer")
    Call<ReturnBean> voteAnswer(@Body WeakHashMap<String, Object> params);

    @POST("square/moment/like")
    Call<ReturnBean> comentLike(@Body WeakHashMap<String, Object> params);

    @POST("square/moment/cancel-like")
    Call<ReturnBean> comentCancleLike(@Body WeakHashMap<String, Object> params);

    @POST("follow/add")
    Call<ReturnBean> followAdd(@Body WeakHashMap<String, Object> params);

    @POST("follow/cancel")
    Call<ReturnBean> followCancle(@Body WeakHashMap<String, Object> params);

    @POST("square/moment/comment")
    Call<ReturnBean> circleComment(@Body WeakHashMap<String, Object> params);

    @POST("square/moment/comment-list")
    Call<ReturnBean<CircleCommentBean>> circleCommentList(@Body WeakHashMap<String, Object> params);

    @POST("square/moment/del-comment")
    Call<ReturnBean> delComment(@Body WeakHashMap<String, Object> params);

    @POST("square/forbid-see/add")
    Call<ReturnBean> addSee(@Body WeakHashMap<String, Object> params);
}
