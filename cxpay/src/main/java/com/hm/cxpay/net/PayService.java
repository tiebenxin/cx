package com.hm.cxpay.net;

import com.hm.cxpay.rx.data.BaseResponse;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @anthor Liszt
 * @data 2019/11/28
 * Description
 */
public interface PayService {

    //用户认证接口,idNumber 身份证号码；idType 目前只有身份证，传1即可；realName 真实姓名
    @POST(Route.URL_USER_AUTH)
    @FormUrlEncoded
    Observable<BaseResponse> authUserInfo(@Field("idNumber") String idNumber, @Field("idType") int idType, @Field("realName") String name);

}
