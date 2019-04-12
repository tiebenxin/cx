package com.yanlong.im.test.server;

import com.yanlong.im.test.bean.Test2Bean;

import net.cb.cb.library.bean.ReturnBean;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/***
 * test
 * @author jyj
 * @date 2016/12/20
 */
public interface TestServer {




    @POST("/api/pad/v1/hwParam")
    @FormUrlEncoded
    Call<ReturnBean<Test2Bean>> testMtd(@Field("id")String id);
}
