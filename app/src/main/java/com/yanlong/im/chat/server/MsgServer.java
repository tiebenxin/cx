package com.yanlong.im.chat.server;

import com.yanlong.im.test.bean.Test2Bean;

import net.cb.cb.library.bean.ReturnBean;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MsgServer {
    @POST("/api/xxx")
    @FormUrlEncoded
    Call<ReturnBean> test(@Field("id")String id);
}
