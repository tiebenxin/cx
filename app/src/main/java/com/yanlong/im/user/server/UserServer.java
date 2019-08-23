package com.yanlong.im.user.server;

import android.content.Intent;

import com.yanlong.im.user.bean.FriendInfoBean;
import com.yanlong.im.user.bean.IdCardBean;
import com.yanlong.im.user.bean.LoginBean;
import com.yanlong.im.user.bean.NewVersionBean;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.bean.OnlineBean;
import net.cb.cb.library.bean.ReturnBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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
    Call<ReturnBean<TokenBean>> login(@Field("password") String password,@Field("phone")String phone,@Field("devid")String devid,@Field("platform")String platform);

    @POST("/user/refresh-access-token")
    @FormUrlEncoded
    Call<ReturnBean<TokenBean>> login4token(@Field("devid")String devid,@Field("platform")String platform);

    @POST("/user/get-user-info")
    Call<ReturnBean<UserInfo>> getMyInfo();

    @POST("/user/get-user-info-by-uid")
    @FormUrlEncoded
    Call<ReturnBean<UserInfo>> getUserInfo(@Field("uid")Long uid);

    @POST("/user/logout")
    Call<ReturnBean> loginOut();

    @POST("/friends/set-friend-stat")
    @FormUrlEncoded
    Call<ReturnBean> friendStat(@Field("friend") Long uid,@Field("opFlag") Integer opFlag,@Field("sayHi") String sayHi);

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
    Call<ReturnBean> smsCaptchaGet(@Field("phone") String phone, @Field("businessType") String businessType);

    @POST("pub/register")
    @FormUrlEncoded
    Call<ReturnBean<TokenBean>> register(@Field("phone") String phone,@Field("captcha") String captcha,@Field("platform") String platform,@Field("devid") String devid);

    @POST("pub/login-by-phone-captcha")
    @FormUrlEncoded
    Call<ReturnBean<TokenBean>> login4Captch(@Field("phone") String phone,@Field("captcha") String captcha,@Field("platform") String platform,@Field("devid") String devid);

    @POST("user/get-user-info-by-imid")
    @FormUrlEncoded
    Call<ReturnBean<UserInfo>> getUserInfoByImid(@Field("imid") String imid);

    @POST("user/get-user-info-by-keyword")
    @FormUrlEncoded
    Call<ReturnBean<List<UserInfo>>> getUserInfoByKeyword(@Field("keyWord") String keyWord);

    @POST("user/set-user-password")
    @FormUrlEncoded
    Call<ReturnBean> setUserPassword(@Field("newPassword") String newPassword,@Field("oldPassword") String oldPassword);

    @POST("user/get-user-matchphone")
    @FormUrlEncoded
    Call<ReturnBean<List<FriendInfoBean>>> getUserMatchPhone(@Field("@phoneList") String phoneList);

    @POST("/pub/change-password-by-sms-captcha")
    @FormUrlEncoded
    Call<ReturnBean> changePasswordBySms(@Field("phone") String phone, @Field("captcha") Integer captcha, @Field("password") String password);

    @POST("/card/get-id-card-info")
    Call<ReturnBean<IdCardBean>> getIdCardInfo();

    @POST("/card/real-name-auth")
    @FormUrlEncoded
    Call<ReturnBean> realNameAuth(@Field("idNumber") String idNumber,@Field("idType") String idType,@Field("name") String name);

    @POST("/card/set-job-type")
    @FormUrlEncoded
    Call<ReturnBean> setJobType(@Field("jobType") String jobType);

    @POST("/card/set-expiry-date")
    @FormUrlEncoded
    Call<ReturnBean> setExpiryDate(@Field("expiryDate") String expiryDate);

    @POST("/card/set-card-photo")
    @FormUrlEncoded
    Call<ReturnBean> setCardPhoto(@Field("cardBack") String cardBack,@Field("cardFront") String cardFront);

    @POST("/pub/get-new-version")
    @FormUrlEncoded
    Call<ReturnBean<NewVersionBean>> getNewVersion(@Field("platform") String platform);

    @POST("friends/del-request-friend")
    @FormUrlEncoded
    Call<ReturnBean> delRequestFriend(@Field("friend") Long friend);

    @POST("user/init-user-password")
    @FormUrlEncoded
    Call<ReturnBean> initUserPassword(@Field("password") String password);

    @POST("complaint/user-complaint")
    @FormUrlEncoded
    Call<ReturnBean> userComplaint(@Field("complaintType") Integer complaintType,@Field("illegalDescription") String illegalDescription,
                                   @Field("illegalImage") String illegalImage,@Field("respondentGid") String respondentGid,@Field("respondentUid") String respondentUid);


    @POST("friends/get-friends-online")
    Call<ReturnBean<List<OnlineBean>>> getUsersOnlineStatus();

    @POST("opinion/user-opinion")
    @FormUrlEncoded
    Call<ReturnBean> userOpinion(@Field("opinionDescription") String opinionDescription,@Field("opinionImage") String opinionImage);

}
