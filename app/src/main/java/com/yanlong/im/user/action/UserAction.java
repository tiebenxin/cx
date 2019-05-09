package com.yanlong.im.user.action;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.server.UserServer;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.Installation;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
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

    public static UserInfo getMyInfo() {
        if (myInfo == null) {
            myInfo = new UserDao().myInfo();
        }

        return myInfo;
    }


    public static Long getMyId() {

        return getMyInfo().getUid();
    }


    public static String getUid(Context context) {
        String uid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.UUID).get4Json(String.class);
        if (TextUtils.isEmpty(uid)) {
            uid = Installation.id(context);
            new SharedPreferencesUtil(SharedPreferencesUtil.SPName.UUID).save2Json(uid);
            return uid;
        }
        return uid;
    }


    public void updateUserinfo(UserInfo userInfo) {
        dao.updateUserinfo(userInfo);
    }


    public void login(Long phone, String pwd, String devid, final CallBack<ReturnBean<TokenBean>> callback) {

        NetUtil.getNet().exec(server.login(pwd, phone, devid, "android"), new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if (response.body().isOk() && StringUtil.isNotNull(response.body().getData().getAccessToken())) {//保存token
                    new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).save2Json(response.body().getData());
                }

                callback.onResponse(call, response);
            }
        });
    }

}

