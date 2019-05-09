package com.yanlong.im.user.server;

import com.yanlong.im.test.bean.Test2Bean;
import com.yanlong.im.user.bean.LoginBean;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.bean.ReturnBean;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

/***
 * test
 * @author jyj
 * @date 2016/12/20
 */
public interface UserServer {




    @POST("/pub/login-by-phone-password")
    Call<ReturnBean<TokenBean>> login(@Body LoginBean loginBean);


    @POST("/pub/login-by-phone-password")
    @FormUrlEncoded
    Call<ReturnBean<TokenBean>> login(@Field("password") String password,@Field("phone")Long phone,@Field("devid")String devid,@Field("platform")String platform);

    @POST("/pub/get-login")
    Call<ReturnBean<TokenBean>> login4token();


    @POST("/user/get-user-info")
    Call<ReturnBean<UserInfo>> getUserinfo();





}
