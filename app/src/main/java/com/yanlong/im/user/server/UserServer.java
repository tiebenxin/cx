package com.yanlong.im.user.server;

import com.yanlong.im.test.bean.Test2Bean;
import com.yanlong.im.user.bean.LoginBean;
import com.yanlong.im.user.bean.TokenBean;

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


}
