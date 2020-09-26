package com.yanlong.im.circle.server;

import net.cb.cb.library.bean.ReturnBean;

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
}
