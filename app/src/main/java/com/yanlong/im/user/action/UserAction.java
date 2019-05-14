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

import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

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
                if (response.body() != null && response.body().isOk() && StringUtil.isNotNull(response.body().getData().getAccessToken())) {//保存token
                    initDB("" + response.body().getData().getUid());
                    setToken(response.body().getData());
                    getMyInfo4Web(response.body().getData().getUid());

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
    private void getMyInfo4Web(Long usrid) {
        NetUtil.getNet().exec(server.getUserInfo(usrid), new CallBack<ReturnBean<UserInfo>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {

                if (response.body() != null && response.body().isOk()) {
                    UserInfo userInfo = response.body().getData();
                    userInfo.setName(userInfo.getName());
                    userInfo.setuType(1);
                    updateUserinfo2DB(userInfo);
                }


            }
        });

    }

    /**
     * 获取用户信息
     */
    public void getUserInfo4Id(Long usrid, CallBack<ReturnBean<UserInfo>> callBack) {
        NetUtil.getNet().exec(server.getUserInfo(usrid), callBack);
    }


    public void login4token(final Callback<ReturnBean<TokenBean>> callback) {
        //判断有没有token信息
        TokenBean token = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).get4Json(TokenBean.class);
        if (token == null || !StringUtil.isNotNull(token.getAccessToken())) {
            callback.onFailure(null, null);
            return;
        }

        //设置token
        NetIntrtceptor.headers = Headers.of("X-Access-Token", token.getAccessToken());
        //或者把token传给后端

        NetUtil.getNet().exec(server.login4token(), new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if (response.body() != null && response.body().isOk() && StringUtil.isNotNull(response.body().getData().getAccessToken())) {//保存token
                    initDB("" + response.body().getData().getUid());

                    setToken(response.body().getData());
                    getMyInfo4Web(response.body().getData().getUid());
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
    public void loginOut() {
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
    private void initDB(String uuid) {
        DaoUtil.get().initConfig("db_user_" + uuid);
    }

    /***
     * 好友添加
     */
    public void friendApply(Long uid, CallBack<ReturnBean> callback) {

        NetUtil.getNet().exec(server.friendStat(uid, 1), callback);

    }

    /***
     * 好友同意
     */
    public void friendAgree(Long uid, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.friendStat(uid, 0), callback);

    }

    /***
     * 加黑名单
     */
    public void friendBlack(Long uid, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.friendStat(uid, 2), callback);

    }

    /***
     * 移除黑名单
     */
    public void friendBlackRemove(Long uid, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.friendStat(uid, 3), callback);
    }

    /***
     * 删除好友
     */
    public void friendDel(Long uid, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.friendDel(uid), callback);

    }

    /***
     * 好友备注
     */
    public void friendMark(Long uid, String mkn, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.friendMkName(uid, mkn), callback);
    }

    /**
     * 通讯录
     */
    public void friendGet4Me(final CallBack<ReturnBean<List<UserInfo>>> callback) {
        NetUtil.getNet().exec(server.friendGet(0), new CallBack<ReturnBean<List<UserInfo>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<UserInfo>>> call, Response<ReturnBean<List<UserInfo>>> response) {

                if (response == null)
                    return;

                if (response.body().isOk()) {
                    List<UserInfo> list = response.body().getData();
                    //更新库
                    dao.friendMeDel();
                    for (UserInfo userInfo : list) {
                        userInfo.setName(userInfo.getName());
                        userInfo.setuType(2);
                        DaoUtil.update(userInfo);
                    }


                }
                callback.onResponse(call, response);

            }

            @Override
            public void onFailure(Call<ReturnBean<List<UserInfo>>> call, Throwable t) {
                super.onFailure(call, t);
                callback.onResponse(call, null);
            }
        });
    }

    /***
     * 申请列表
     */
    public void friendGet4Apply(CallBack<ReturnBean<List<UserInfo>>> callback) {
        NetUtil.getNet().exec(server.friendGet(1), callback);
    }

    /***
     * 黑名单
     */
    public void friendGet4Black(CallBack<ReturnBean<List<UserInfo>>> callback) {
        NetUtil.getNet().exec(server.friendGet(2), callback);
    }

    /**
     * 设置用户个人资料
     */
    public void myInfoSet(final String imid, final String avatar, final String nickname, final Integer gender, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.userInfoSet(imid, avatar, nickname, gender), new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    myInfo = dao.findUserInfo(getMyId());
                    if(imid!=null)
                    myInfo.setImid(imid);
                    if(avatar!=null)
                    myInfo.setHead(avatar);
                    if(nickname!=null)
                    myInfo.setName(nickname);
                    if(gender!=null)
                    myInfo.setSex(gender);
                    dao.updateUserinfo(myInfo);
                }
                callback.onResponse(call, response);
            }
        });

    }


}

