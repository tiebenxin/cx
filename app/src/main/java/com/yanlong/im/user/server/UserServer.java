package com.yanlong.im.user.server;

import com.yanlong.im.test.bean.Test2Bean;
import com.yanlong.im.user.bean.LoginBean;
import com.yanlong.im.user.bean.SmsBean;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.bean.ReturnBean;

import java.util.List;

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

    @POST("/user/refresh-access-token")
    Call<ReturnBean<TokenBean>> login4token();


    @POST("/user/get-user-info")
    Call<ReturnBean<UserInfo>> getMyInfo();

    @POST("/user/get-user-info-by-uid")
    @FormUrlEncoded
    Call<ReturnBean<UserInfo>> getUserInfo(@Field("uid")Long uid);

    @POST("/pub/退出")
    Call<ReturnBean> loginOut();


    @POST("/friends/set-friend-stat")
    @FormUrlEncoded
    Call<ReturnBean> friendStat(@Field("friend") Long uid,@Field("opFlag") Integer opFlag);

    @POST("/friends/del-friend")
    @FormUrlEncoded
    Call<ReturnBean> friendDel(@Field("friend") Long uid);

    @POST("/friends/set-friend-alias")
    @FormUrlEncoded
    Call<ReturnBean> friendMkName(@Field("friend") Long uid,@Field("alias")String mkName);

    @POST("/friends/get-friends")
    @FormUrlEncoded
    Call<ReturnBean<List<UserInfo>>> friendGet(@Field("opFlag") Integer opFlag);

    @POST("/user/set-user-info")
    @FormUrlEncoded
    Call<ReturnBean> userInfoSet(@Field("imid") String imid,@Field("avatar") String avatar,
                                                   @Field("nickname") String nickname,@Field("gender") Integer gender);

    @POST("user/set-user-mask")
    @FormUrlEncoded
    Call<ReturnBean> userMaskSet(@Field("switchval") Integer switchval,@Field("opFlag") Integer avatar);

    @POST("pub/get-sms-captcha")
    @FormUrlEncoded
    Call<ReturnBean> smsCaptchaGet(@Field("phone") Long phone, @Field("businessType") String businessType);

    @POST("pub/register")
    @FormUrlEncoded
    Call<ReturnBean> register(@Field("phone") Long phone,@Field("password") String password,@Field("captcha") String captcha,@Field("nickname") String nickname);

    @POST("pub/login-by-phone-captcha")
    @FormUrlEncoded
    Call<ReturnBean<TokenBean>> login4Captch(@Field("phone") Long phone,@Field("captcha") String captcha);

    @POST("user/get-user-info-by-imid")
    @FormUrlEncoded
    Call<ReturnBean<UserInfo>> getUserInfoByImid(@Field("imid") String imid);

    @POST("user/get-user-info-by-keyword")
    @FormUrlEncoded
    Call<ReturnBean<List<UserInfo>>> getUserInfoByKeyword(@Field("keyWord") String keyWord);

    @POST("user/set-user-password")
    @FormUrlEncoded
    Call<ReturnBean> setUserPassword(@Field("newPassword") String newPassword,@Field("oldPassword") String oldPassword);

}
