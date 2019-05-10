package com.yanlong.im.user.action;

import android.content.Context;
import android.text.TextUtils;

import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.server.UserServer;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.Installation;
import net.cb.cb.library.utils.NetIntrtceptor;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/***
 * @author jyj
 * @date 2016/12/20
 */
public class UserAction {
    private UserServer server;
    private UserDao dao = new UserDao();
    private static UserInfo myInfo;

    public UserAction() {
        server = NetUtil.getNet().create(UserServer.class);
    }
    //以下是演示
    /*public void login( Long phone, String pwd,CallBack<ReturnBean<TokenBean>> callback) {

        LoginBean bean = new LoginBean();
        bean.setPassword(pwd);
        bean.setPhone(phone);
        NetUtil.getNet().exec(server.login(bean), callback);
    }*/

    /***
     * 获取我的信息
     * @return
     */
    public static UserInfo getMyInfo() {
        if (myInfo == null) {
            myInfo = new UserDao().myInfo();
        }

        return myInfo;
    }


    /***
     * 获取个人id
     * @return
     */
    public static Long getMyId() {

        return getMyInfo().getUid();
    }


    /***
     * 获取设备id
     * @param context
     * @return
     */
    public static String getDevId(Context context) {
        String uid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.DEV_ID).get4Json(String.class);
        if (TextUtils.isEmpty(uid)) {
            uid = Installation.id(context);
            new SharedPreferencesUtil(SharedPreferencesUtil.SPName.DEV_ID).save2Json(uid);
            return uid;
        }
        return uid;
    }


    public void updateUserinfo2DB(UserInfo userInfo) {

        dao.updateUserinfo(userInfo);


    }


    public void login(Long phone, String pwd, String devid, final CallBack<ReturnBean<TokenBean>> callback) {

        NetUtil.getNet().exec(server.login(pwd, phone, devid, "android"), new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if (response.body().isOk() && StringUtil.isNotNull(response.body().getData().getAccessToken())) {//保存token
                    initDB(response.body().getData().getUid());
                    setToken(response.body().getData());
                    getMyInfo4Web();
                    
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, null);
                }


            }

            @Override
            public void onFailure(Call<ReturnBean<TokenBean>> call, Throwable t) {
                super.onFailure(call, t);
                callback.onFailure(call, t);
            }
        });
    }

    /***
     * 拉取服务器的自己的信息到数据库
     */
    private void getMyInfo4Web() {
        NetUtil.getNet().exec(server.getMyInfo(), new CallBack<ReturnBean<UserInfo>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {

                UserInfo userInfo = response.body().getData();
                userInfo.setName(userInfo.getName());
                userInfo.setuType(1);
                updateUserinfo2DB(userInfo);

            }
        });

    }


    public void login4token(final Callback<ReturnBean<TokenBean>> callback) {
        //判断有没有token信息
        TokenBean token = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).get4Json(TokenBean.class);
        if (token==null||!StringUtil.isNotNull(token.getAccessToken())) {
            callback.onFailure(null, null);
            return;
        }

        //设置token
        NetIntrtceptor.headers = Headers.of("X-Access-Token", token.getAccessToken());
        //或者把token传给后端

        NetUtil.getNet().exec(server.login4token(), new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if (response.body().isOk() && StringUtil.isNotNull(response.body().getData().getAccessToken())) {//保存token
                    initDB(response.body().getData().getUid());

                    setToken(response.body().getData());
                    getMyInfo4Web();
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, null);
                }


            }

            @Override
            public void onFailure(Call<ReturnBean<TokenBean>> call, Throwable t) {
                super.onFailure(call, t);
                callback.onFailure(call, t);
            }
        });

    }

    /***
     * 登出
     */
    public void loginOut(){
        new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).clear();

    }


    /***
     * 应用和保存token
     */
    private void setToken(TokenBean token) {
        new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).save2Json(token);
        NetIntrtceptor.headers = Headers.of("X-Access-Token", token.getAccessToken());
    }

    /***
     * 配置要使用的DB
     */
    private void initDB(String uuid){
        DaoUtil.get().initConfig("db_user_"+uuid);
    }

}

