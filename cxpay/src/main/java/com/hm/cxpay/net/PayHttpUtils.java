package com.hm.cxpay.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hm.cxpay.rx.data.BaseResponse;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @anthor Liszt
 * @data 2019/11/28
 * Description
 */
public class PayHttpUtils {

    private static PayHttpUtils instance;

    public static PayHttpUtils getInstance() {
        if (instance == null) {
            instance = new PayHttpUtils();
        }
        return instance;
    }

    //用户认证
    public Observable<BaseResponse> authUserInfo(String idNum, String realName) {
        Map<String, String> map = new HashMap<>();
        map.put("idNumber", idNum);
        map.put("realName", realName);
        map.put("idType", 1 + "");
        return HttpChannel.getInstance().getPayService().authUserInfo(getRequestBody(map));
    }




    private static RequestBody getRequestBody(Map<String, String> map) {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), mapToJSON(map));
    }

    /**
     * 将Map转化为Json
     */
    private static String mapToJSON(Map<String, String> map) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.toJson(map);
    }
}
