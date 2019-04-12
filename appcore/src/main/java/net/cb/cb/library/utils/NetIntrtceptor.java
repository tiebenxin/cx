package net.cb.cb.library.utils;

import android.text.TextUtils;
import android.util.Log;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.utils.encrypt.AESEncrypt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
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
    private static final String TAG="NetIntrtceptor";




    @Override
    public Response intercept(Chain chain) throws IOException {
        if(AppConfig.DEBUG)
            Log.i(TAG,"<<进入拦截器");
        Request request=  chain.request().newBuilder()
              //  .headers(headers)
                .build();

        request = interceptor4Front(chain, request);

        Response resp=chain.proceed(request);

        resp = interceptor4After(resp);



        return resp;
    }

    //前拦截
    private Request interceptor4Front(Chain chain, Request request) {
        String url = request.url().encodedPath();

/*        if (url.contains("/oauth/oauthlogin/verify/code")
                ) {
            //如果是验证码界面那么不传token
            request = chain.request().newBuilder().build();
            return request;
        }*/


        //处理head 加密
       /* String param = request.url().query();
        Map<String,String> map = new HashMap<>();
        if(!TextUtils.isEmpty(param)){
            map.clear();
            if (param.contains("&")) {
                String[] str = param.split("&");
                for (int i = 0; i < str.length; i++) {
                    String[] s = str[i].split("=");
                    if(s.length>=2){
                        map.put(s[0],s[1]);
                    }
                }
            }else{
                String[] s = param.split("=");
                if(s.length>=2) {
                    map.put(s[0], s[1]);
                }
            }
        }

        //post自动追加platform 参数
        RequestBody reqbody = request.body();
        RequestBody temp=null;
        if(request.method().equals("POST")){
            FormBody gb = null;
            if(reqbody instanceof FormBody){
                 gb=(FormBody)reqbody;
            }
            FormBody.Builder tpbd = new FormBody.Builder();

            tpbd.add("platform","PAD");
            map.put("platform","PAD");
            for(int i=0;gb!=null&&i<gb.size();i++){
                map.put(gb.name(i),gb.value(i));
                tpbd.add(gb.name(i),gb.value(i));
            }
            String encryptParam = AESEncrypt.encrypt(map);
            tpbd.add("D",encryptParam);
            temp=tpbd.build();
        }

        if(temp!=null){
            request = request.newBuilder()
                 //   .header("ciphertext", encryptParam)
                    .method(request.method(), temp)
                    .build();
        }else{
            request = request.newBuilder()
                    .method(request.method(), request.body())
                    .build();
        }
*/


        return request;

    }

//后拦截
    private Response interceptor4After(Response resp) {

        switch (resp.code()) {
            case 200:

                break;
            case 401:
                Log.e(TAG, "<<拦截器:401 url:"+resp.request().url().url().toString());
             /*   SharedPreferencesUtil sp=new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN);
                TokenBean token= sp.get4Json(TokenBean.class);
                EventBus.getDefault().post(new EventLoginOut());*/
              /*  if(token==null||token.getToken()==null){//没登录,在当前页面弹登录
                    EventBus.getDefault().post(new EventLoginOut());
                }else{//有登录,被踢了

                    EventBus.getDefault().post(new EventReLogin());
                }*/




                break;

            case 403:
                Log.e(TAG, "<<拦截器:403 url:"+resp.request().url().url().toString());

//                EventBus.getDefault().post(logOutBean);
                break;
            case 404:
                Log.e(TAG, "<<拦截器:404 url:"+resp.request().url().url().toString());
                break;
            case 500:
                Log.e(TAG, "<<拦截器:500 url:"+resp.request().url().url().toString());
                break;
        }

        return resp;
    }

}
