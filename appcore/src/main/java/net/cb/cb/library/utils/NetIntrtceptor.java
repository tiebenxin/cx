package net.cb.cb.library.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.bean.EventLoginOut;
import net.cb.cb.library.bean.EventLoginOut4Conflict;
import net.cb.cb.library.utils.encrypt.AESEncrypt;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/***
 * @author jyj
 * @date 2016/11/29
 */
public class NetIntrtceptor implements Interceptor {
    private static final String TAG = "NetIntrtceptor";
    private static Gson gson = new Gson();
    private static MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
    public static Headers headers=Headers.of();


    @Override
    public Response intercept(Chain chain) throws IOException {
        if (AppConfig.DEBUG)
            Log.i(TAG, "<<进入拦截器");
        Request request = chain.request().newBuilder()
                  .headers(headers)
                .build();

        request = interceptor4Front(chain, request);

        Response resp = chain.proceed(request);

        resp = interceptor4After(resp);


        return resp;
    }

    //前拦截
    private Request interceptor4Front(Chain chain, Request request) {
        String url = request.url().encodedPath();



        //post自动追加platform 参数
        RequestBody reqbody = request.body();

        //  if(request.method().equals("POST")){

        if (reqbody instanceof FormBody) {
            Gson gson= new GsonBuilder().create();
            FormBody gb = (FormBody) reqbody;

            Map<String, Object> objs = new HashMap<>();

             for (int i = 0; gb != null && i < gb.size(); i++) {
                //放在一个map里面,然后转json

                 if(gb.name(i).startsWith("@")){//直接存对象
                     objs.put(gb.name(i).substring(1), gson.fromJson(gb.value(i),Object.class));

                 }else{
                     objs.put(gb.name(i), gb.value(i));
                 }




            }

            String json = gson.toJson(objs);


            RequestBody nbody = RequestBody.create(mediaType, json);
            request=  request.newBuilder()
                    .method(request.method(), nbody)
                    .build();

        }


        //  }

        return request;

    }

    //后拦截
    private Response interceptor4After(Response resp) {

        switch (resp.code()) {
            case 200:
                //resp.body().string()

                break;
            case 401:
                Log.e(TAG, "<<拦截器:401 url:" + resp.request().url().url().toString());

                EventBus.getDefault().post(new EventLoginOut());
              /*  if(token==null||token.getToken()==null){//没登录,在当前页面弹登录
                    EventBus.getDefault().post(new EventLoginOut());
                }else{//有登录,被踢了

                    EventBus.getDefault().post(new EventReLogin());
                }*/


                break;

            case 403:
                Log.e(TAG, "<<拦截器:403 url:" + resp.request().url().url().toString());

                EventBus.getDefault().post(new EventLoginOut());
                break;
            case 404:
                Log.e(TAG, "<<拦截器:404 url:" + resp.request().url().url().toString());
                break;
            case 500:
                Log.e(TAG, "<<拦截器:500 url:" + resp.request().url().url().toString());
                break;
        }

        return resp;
    }

}
