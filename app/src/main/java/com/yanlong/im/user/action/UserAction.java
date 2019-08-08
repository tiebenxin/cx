package com.yanlong.im.user.action;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.jrmf360.rplib.JrmfRpClient;
import com.jrmf360.rplib.http.model.BaseModel;
import com.jrmf360.tools.http.OkHttpModelCallBack;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.pay.action.PayAction;
import com.yanlong.im.pay.bean.SignatureBean;
import com.yanlong.im.user.bean.FriendInfoBean;
import com.yanlong.im.user.bean.IdCardBean;
import com.yanlong.im.user.bean.NewVersionBean;
import com.yanlong.im.user.bean.SmsBean;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.server.UserServer;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.PhoneListUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.Installation;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetIntrtceptor;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
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
        // Log.v("ssss","getMyInfo");
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
        // Log.v("ssss","getMyId");
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
        userInfo.setuType(1);
        dao.updateUserinfo(userInfo);
    }

    /**
     * 账号密码登录
     */
    public void login(final String phone, String pwd, String devid, final CallBack<ReturnBean<TokenBean>> callback) {
        cleanInfo();
        NetUtil.getNet().exec(server.login(pwd, phone, devid, "android"), new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if (response.body() != null && response.body().isOk() && StringUtil.isNotNull(response.body().getData().getAccessToken())) {//保存token
                    initDB("" + response.body().getData().getUid());
                    setToken(response.body().getData());
                    getMyInfo4Web(response.body().getData().getUid());
                }

                callback.onResponse(call, response);
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
                    userInfo.toTag();
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

    /***
     * 获取[普通]用户信息并且缓存到数据库
     * @param usrid
     */
    public void getUserInfoAndSave(Long usrid, final CallBack<ReturnBean<UserInfo>> cb) {
        NetUtil.getNet().exec(server.getUserInfo(usrid), new CallBack<ReturnBean<UserInfo>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {

                if (response.body() != null && response.body().isOk()) {
                    UserInfo userInfo = response.body().getData();
                    userInfo.toTag();
                    userInfo.setuType(0);
                    dao.updateUserinfo(userInfo);
                    cb.onResponse(call, response);
                }
            }
        });

    }

    /***
     * 无网登录
     */
    public void login4tokenNotNet(TokenBean token) {

        initDB("" + token.getUid());
        NetIntrtceptor.headers = Headers.of("X-Access-Token", token.getAccessToken());
    }

    public void login4token(String dev_id, final Callback<ReturnBean<TokenBean>> callback) {
        //判断有没有token信息
        TokenBean token = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).get4Json(TokenBean.class);
        if (token == null || !StringUtil.isNotNull(token.getAccessToken())) {
            callback.onFailure(null, null);
            return;
        }

        //设置token
        NetIntrtceptor.headers = Headers.of("X-Access-Token", token.getAccessToken());
        //或者把token传给后端

        NetUtil.getNet().exec(server.login4token(dev_id, "android"), new CallBack<ReturnBean<TokenBean>>() {
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
        cleanInfo();
        NetUtil.getNet().exec(server.loginOut(), new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                LogUtil.getLog().d("logout", response.body().getMsg());
            }
        });
    }

    /***
     * 清理信息
     */
    public void cleanInfo() {
        //Log.v("ssss","cleanInfo");
        myInfo = null;
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
        LogUtil.getLog().i("dbinfo", ">>>>>>>>>>>>>>>>>>>初始数据库:" + "db_user_" + uuid);
        DaoUtil.get().initConfig("db_user_" + uuid);
    }

    /***
     * 好友添加
     */
    public void friendApply(Long uid, String sayHi, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.friendStat(uid, 1, sayHi), callback);
    }

    /***
     * 好友同意
     */
    public void friendAgree(Long uid, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.friendStat(uid, 0, null), callback);

    }

    /***
     * 加黑名单
     */
    public void friendBlack(Long uid, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.friendStat(uid, 2, null), callback);

    }

    /***
     * 移除黑名单
     */
    public void friendBlackRemove(Long uid, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.friendStat(uid, 3, null), callback);
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
     * 删除待同意好友
     */
    public void delRequestFriend(Long uid, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.delRequestFriend(uid), callback);
    }

    /**
     * 通讯录
     */
    public void friendGet4Me(final CallBack<ReturnBean<List<UserInfo>>> callback) {
        NetUtil.getNet().exec(server.friendGet(0), new CallBack<ReturnBean<List<UserInfo>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<UserInfo>>> call, Response<ReturnBean<List<UserInfo>>> response) {

                if (response.body() == null)
                    return;

                if (response.body().isOk()) {
                    List<UserInfo> list = response.body().getData();
                    //更新库
                    dao.friendMeUpdate(list);

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

    private PayAction payAction = new PayAction();

    private void upMyinfoToPay() {
        payAction.SignatureBean(new CallBack<ReturnBean<SignatureBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SignatureBean>> call, Response<ReturnBean<SignatureBean>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    SignatureBean sign = response.body().getData();
                    String token = sign.getSign();
                    JrmfRpClient.updateUserInfo(myInfo.getUid() + "", token, myInfo.getName(), myInfo.getHead(), new OkHttpModelCallBack<BaseModel>() {
                        @Override
                        public void onSuccess(BaseModel baseModel) {

                        }

                        @Override
                        public void onFail(String s) {

                        }
                    });


                }
            }
        });
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
                    if (!TextUtils.isEmpty(imid))
                        myInfo.setImid(imid);
                    if (!TextUtils.isEmpty(avatar))
                        myInfo.setHead(avatar);
                    if (!TextUtils.isEmpty(nickname))
                        myInfo.setName(nickname);
                    if (gender != null)
                        myInfo.setSex(gender);
                    updateUserinfo2DB(myInfo);
                    upMyinfoToPay();
                }
                callback.onResponse(call, response);
            }
        });
    }

    /**
     * 修改用户组合开关
     */
    public void userMaskSet(Integer switchval, Integer avatar, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.userMaskSet(switchval, avatar), callback);
    }


    /**
     * 获取短信验证码
     *
     * @param businessType 登录login  注册register  修改密码password
     */
    public void smsCaptchaGet(String phone, String businessType, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.smsCaptchaGet(phone, businessType), callback);
    }

    /***
     * 根据key搜索所有的好友
     */
    public List<UserInfo> searchUser4key(String key) {

        return dao.searchUser4key(key);
    }


    /**
     * 用户注册
     */
    public void register(String phone, String captcha, String devid, final CallBack<ReturnBean<TokenBean>> callback) {
        cleanInfo();
        NetUtil.getNet().exec(server.register(phone, captcha, "android", devid), new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                super.onResponse(call, response);
                if (response.body() != null && response.body().isOk() && StringUtil.isNotNull(response.body().getData().getAccessToken())) {//保存token
                    initDB("" + response.body().getData().getUid());
                    setToken(response.body().getData());
                    getMyInfo4Web(response.body().getData().getUid());
                }
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ReturnBean<TokenBean>> call, Throwable t) {
                super.onFailure(call, t);
                callback.onFailure(call, t);
            }
        });
    }


    /**
     * 手机号验证码登录
     */
    public void login4Captch(final String phone, String captcha, String devid, final CallBack<ReturnBean<TokenBean>> callback) {
        cleanInfo();
        NetUtil.getNet().exec(server.login4Captch(phone, captcha, "android", devid), new Callback<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if (response.body() != null && response.body().isOk() && StringUtil.isNotNull(response.body().getData().getAccessToken())) {//保存token
                    initDB("" + response.body().getData().getUid());
                    setToken(response.body().getData());
                    getMyInfo4Web(response.body().getData().getUid());
                }
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ReturnBean<TokenBean>> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }


    /**
     * 根据产品号获取个人资料
     */
    public void getUserInfoByImid(String imid, CallBack<ReturnBean<UserInfo>> callback) {
        NetUtil.getNet().exec(server.getUserInfoByImid(imid), callback);
    }


    /**
     * 根据关键字匹配产品号或手机号获取用户信息
     */
    public void getUserInfoByKeyword(String keyWord, CallBack<ReturnBean<List<UserInfo>>> callback) {
        NetUtil.getNet().exec(server.getUserInfoByKeyword(keyWord), callback);
    }

    /**
     * 修改用户密码
     */
    public void setUserPassword(String newPassword, String oldPassword, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.setUserPassword(newPassword, oldPassword), callback);
    }

    /**
     * 通讯录匹配
     */
    public void getUserMatchPhone(String phoneList, CallBack<ReturnBean<List<FriendInfoBean>>> callback) {
        NetUtil.getNet().exec(server.getUserMatchPhone(phoneList), callback);
    }

    /**
     * 手机号验证码重置密码
     */
    public void changePasswordBySms(String phone, Integer captcha, String password, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.changePasswordBySms(phone, captcha, password), callback);
    }

    /**
     * 获取认证信息
     */
    public void getIdCardInfo(CallBack<ReturnBean<IdCardBean>> callback) {
        NetUtil.getNet().exec(server.getIdCardInfo(), callback);
    }

    /**
     * 实名认证
     */
    public void realNameAuth(String idNumber, String idType, String name, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.realNameAuth(idNumber, idType, name), new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    myInfo = dao.findUserInfo(getMyId());
                    myInfo.setAuthStat(1);
                    updateUserinfo2DB(myInfo);
                }
                callback.onResponse(call, response);
            }
        });
    }


    /**
     * 更新职业类别
     */
    public void setJobType(String jobType, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.setJobType(jobType), callback);
    }


    /**
     * 更新证件有效期
     */
    public void setExpiryDate(String expiryDate, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.setExpiryDate(expiryDate), callback);
    }

    /**
     * 更新证件照片
     */
    public void setCardPhoto(String cardBack, String cardFront, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.setCardPhoto(cardBack, cardFront), new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    myInfo = dao.findUserInfo(getMyId());
                    myInfo.setAuthStat(2);
                    updateUserinfo2DB(myInfo);
                }
                callback.onResponse(call, response);
            }
        });
    }


    /**
     * 版本更新
     */
    public void getNewVersion(CallBack<ReturnBean<NewVersionBean>> callback) {
        NetUtil.getNet().exec(server.getNewVersion("android"), callback);
    }

    /*
     * 检测本地常聊小助手是否存在
     * */
    public boolean checkAssitantUserExist() {
        UserInfo info = dao.findUserInfo(1L);
        if (info != null) {
            return true;
        }
        return false;
    }

    public UserInfo createAssitantUser() {
        UserInfo info = new UserInfo();
        info.setUid(1L);
        info.setName("常聊小助手");
        info.setMkName("常聊小助手");
        info.setuType(ChatEnum.EUserType.ASSISTANT);
        info.setFriendvalid(CoreEnum.ESureType.NO);
        info.setAuthStat(ChatEnum.EAuthStatus.AUTH_SECOND);
        info.setActiveType(CoreEnum.ESureType.YES);
        info.setDisturb(CoreEnum.ESureType.NO);
        info.setLastonline(System.currentTimeMillis());
        return info;
    }


    /**
     * 初始化用户密码
     */
    public void initUserPassword(String password, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.initUserPassword(password), callback);
    }

}

