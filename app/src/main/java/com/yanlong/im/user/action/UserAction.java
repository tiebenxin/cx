package com.yanlong.im.user.action;

import com.yanlong.im.test.bean.Test2Bean;
import com.yanlong.im.test.server.TestServer;
import com.yanlong.im.user.bean.LoginBean;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.server.UserServer;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;

import retrofit2.Call;
import retrofit2.Response;

/***
 * @author jyj
 * @date 2016/12/20
 */
public class UserAction {
    private UserServer server;

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


    public void login(Long phone, String pwd, final CallBack<ReturnBean<TokenBean>> callback) {
 /*
        LoginBean bean = new LoginBean();
        bean.setPassword(pwd);
        bean.setPhone(phone);

      NetUtil.getNet().exec(server.login(bean), new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if(response.body().isOk()&& StringUtil.isNotNull(response.body().getData().getAccessToken())){//保存token
                    new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).save2Json(response.body().getData());
                }

                callback.onResponse(call,response);
            }
        });*/

        NetUtil.getNet().exec(server.login(pwd,phone), new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if(response.body().isOk()&& StringUtil.isNotNull(response.body().getData().getAccessToken())){//保存token
                    new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).save2Json(response.body().getData());
                }

                callback.onResponse(call,response);
            }
        });
    }

}

