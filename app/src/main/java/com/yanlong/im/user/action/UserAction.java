package com.yanlong.im.user.action;

import android.content.Context;
import android.text.TextUtils;

import com.example.nim_lib.config.Preferences;
import com.example.nim_lib.controll.AVChatProfile;
import com.hm.cxpay.global.PayEnvironment;
import com.jrmf360.rplib.JrmfRpClient;
import com.jrmf360.rplib.http.model.BaseModel;
import com.jrmf360.tools.http.OkHttpModelCallBack;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.yanlong.im.MainActivity;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.ApplyBean;
import com.yanlong.im.chat.bean.SingleMeberInfoBean;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.pay.action.PayAction;
import com.yanlong.im.pay.bean.SignatureBean;
import com.yanlong.im.user.bean.FriendInfoBean;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.IdCardBean;
import com.yanlong.im.user.bean.NewVersionBean;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.bean.UserBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.server.UserServer;
import com.yanlong.im.utils.DaoUtil;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.OnlineBean;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.Installation;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetIntrtceptor;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.SpUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.VersionUtil;
import net.cb.cb.library.utils.encrypt.EncrypUtil;
import net.cb.cb.library.utils.encrypt.MD5;

import java.util.List;

import cn.jpush.android.api.JPushInterface;
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
    private static UserBean myInfo;

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
    public static IUser getMyInfo() {
        if (myInfo == null) {
            myInfo = new UserDao().myInfo();
        }
        if (myInfo == null) {
            Long uid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.UID).get4Json(Long.class);
            if (uid != null) {
                UserInfo info = new UserDao().findUserInfoOfMe(uid);
                //不是文件小助手
                if (info != null) {
                    myInfo = convertToUserBean(info);
                    new UserDao().updateUserBean(myInfo);
                }
            }
        }
        return myInfo;
    }


    /***
     * 获取个人id
     * @return
     */
    public static Long getMyId() {
        if (getMyInfo() == null) {
            return -1l;// 处理 intValue、longValue 空指针问题
        }
        return getMyInfo().getUid();
    }


    /***
     * 获取设备id
     * @param context
     * @return
     */
    public static String getDevId(Context context) {
        String uid = JPushInterface.getRegistrationID(context);
        if (TextUtils.isEmpty(uid)) {
//            uid = Installation.id(context);
//            new SharedPreferencesUtil(SharedPreferencesUtil.SPName.DEV_ID).save2Json(uid);
//            return uid;
            return "pushToken";
        }
        LogUtil.getLog().i("getDevId", uid + "");
        return uid;
    }

  /*  public void getDevId(EventDevID event){

        int reTime = 0;
        String uid = null;
        try {
            while (reTime < 5*10) {
                uid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.DEV_ID).get4Json(String.class);
                if (uid != null) {
                    event.onDevId(uid);
                    break;
                } else {
                    LogUtil.getLog().i("youmeng", "等待DevId"+reTime);
                    Thread.sleep(200);
                }
                reTime++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/


    public void updateUser2DB(UserBean user) {
        user.setuType(1);
        dao.updateUserBean(user);
    }

    /**
     * 账号密码登录
     */
    public void login(final String phone, String pwd, String devid, final CallBack<ReturnBean<TokenBean>> callback) {

        cleanInfo();
        NetUtil.getNet().exec(server.login(MD5.md5(pwd), phone, devid, "android", VersionUtil.getPhoneModel(), StringUtil.getChannelName(AppConfig.getContext())), new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if (response.body() != null && response.body().isOk() && StringUtil.isNotNull(response.body().getData().getAccessToken())) {//保存token
                    if (response.body().getData() != null) {
                        doNeteaseLogin(response.body().getData().getNeteaseAccid(), response.body().getData().getNeteaseToken());
                        saveNeteaseAccid(response.body().getData().getNeteaseAccid(), response.body().getData().getNeteaseToken());
                    }
                    LogUtil.writeLog("账号密码登录获取token" + "--token=" + response.body().getData().getAccessToken());
                    initDB("" + response.body().getData().getUid());
                    setToken(response.body().getData(), true);
                    //如果是手机号码登录，则删除上次常信号登陆的账号
                    new SharedPreferencesUtil(SharedPreferencesUtil.SPName.IM_ID).save2Json("");
                    getMyInfo4Web(response.body().getData().getUid(), "");
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
     * 常信号密码登录
     */
    public void login4Imid(final String imid, String pwd, String devid, final CallBack<ReturnBean<TokenBean>> callback) {

        cleanInfo();
        NetUtil.getNet().exec(server.login4Imid(MD5.md5(pwd), imid, devid, "android", VersionUtil.getPhoneModel(), StringUtil.getChannelName(AppConfig.getContext())), new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if (response.body() != null && response.body().isOk() && StringUtil.isNotNull(response.body().getData().getAccessToken())) {//保存token
                    if (response.body().getData() != null) {
                        doNeteaseLogin(response.body().getData().getNeteaseAccid(), response.body().getData().getNeteaseToken());
                        saveNeteaseAccid(response.body().getData().getNeteaseAccid(), response.body().getData().getNeteaseToken());
                    }
                    LogUtil.writeLog("常信号登录获取token" + "--token=" + response.body().getData().getAccessToken());
                    initDB("" + response.body().getData().getUid());
                    setToken(response.body().getData(), true);
                    getMyInfo4Web(response.body().getData().getUid(), imid);
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
    private void getMyInfo4Web(Long usrid, String imid) {
        NetUtil.getNet().exec(server.getUserBean(usrid), new CallBack<ReturnBean<UserBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserBean>> call, Response<ReturnBean<UserBean>> response) {
                if (response.body() != null && response.body().isOk()) {
                    UserBean userInfo = response.body().getData();
                    new SharedPreferencesUtil(SharedPreferencesUtil.SPName.IMAGE_HEAD).save2Json(userInfo.getHead() + "");
                    //保存手机或常信号登录
                    if (StringUtil.isNotNull(imid)) {
                        new SharedPreferencesUtil(SharedPreferencesUtil.SPName.IM_ID).save2Json(imid);
                    }
                    new SharedPreferencesUtil(SharedPreferencesUtil.SPName.PHONE).save2Json(userInfo.getPhone());
                    new SharedPreferencesUtil(SharedPreferencesUtil.SPName.UID).save2Json(userInfo.getUid());
                    userInfo.toTag();
                    updateUser2DB(userInfo);
                }
            }
        });

    }

    /**
     * 获取用户信息
     */
    public void getUserInfo4Id(Long usrid, final CallBack<ReturnBean<UserInfo>> callBack) {
        NetUtil.getNet().exec(server.getUserInfo(usrid), new CallBack<ReturnBean<UserInfo>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
                super.onResponse(call, response);
                //写入用户信息到数据库
                if (response.body() != null) {
                    UserInfo userInfo = response.body().getData();
                    if (userInfo != null && userInfo.getUid() != null) {
                        UserInfo local = dao.findUserInfo(usrid);
                        if (local == null) {
                            if (userInfo.getStat() == 0) {
                                userInfo.setuType(ChatEnum.EUserType.FRIEND);
                            } else if (userInfo.getStat() == 2) {
                                userInfo.setuType(ChatEnum.EUserType.BLACK);
                            } else if (userInfo.getStat() == 1) {
                                userInfo.setuType(ChatEnum.EUserType.STRANGE);
                            } else if (userInfo.getStat() == 9) {
                                userInfo.setuType(ChatEnum.EUserType.ASSISTANT);
                            }
                            userInfo.toTag();
                            dao.updateUserinfo(userInfo);
                        }
                        boolean hasChange = MessageManager.getInstance().updateUserAvatarAndNick(userInfo.getUid(), userInfo.getHead(), userInfo.getName());
                        if (hasChange) {
                            MessageManager.getInstance().notifyRefreshFriend(true, userInfo.getUid(), CoreEnum.ERosterAction.UPDATE_INFO);
                        }
                        callBack.onResponse(call, response);
                    } else {
                        callBack.onFailure(call, new Throwable());
                    }
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<UserInfo>> call, Throwable t) {
                super.onFailure(call, t);
                callBack.onFailure(call, t);
            }
        });
    }

    /***
     * 获取单个用户信息并且缓存到数据库
     * @param usrid
     */
    public void getUserInfoAndSave(Long usrid, @ChatEnum.EUserType final int type, final CallBack<ReturnBean<UserInfo>> cb) {
        NetUtil.getNet().exec(server.getUserInfo(usrid), new CallBack<ReturnBean<UserInfo>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
                if (response.body() != null && response.body().isOk()) {
                    UserInfo userInfo = response.body().getData();
                    userInfo.toTag();
                    if (userInfo.getStat() != 0) {//优先设置为好友
                        userInfo.setuType(type);
                    } else {
                        userInfo.setuType(ChatEnum.EUserType.FRIEND);
                    }
                    dao.updateUserinfo(userInfo);
                    MessageManager.getInstance().updateSessionTopAndDisturb("", usrid, userInfo.getIstop(), userInfo.getDisturb());
                    MessageManager.getInstance().updateCacheUser(userInfo);
                    cb.onResponse(call, response);
                } else {
                    cb.onFailure(call, new Throwable());
                    MessageManager.getInstance().removeLoadUids(usrid);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<UserInfo>> call, Throwable t) {
                super.onFailure(call, t);
                cb.onFailure(call, new Throwable());
                MessageManager.getInstance().removeLoadUids(usrid);
            }
        });

    }

    /***
     * 无网登录
     */
    public void login4tokenNotNet(TokenBean token) {
        initDB("" + token.getUid());
        setToken(token, false);
    }

    /*
     * 用就token刷新新token
     * */
    public void updateToken(String dev_id, final Callback<ReturnBean<TokenBean>> callback) {
        //判断有没有token信息
        TokenBean token = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).get4Json(TokenBean.class);
        if (token == null || !StringUtil.isNotNull(token.getAccessToken())) {
            callback.onFailure(null, null);
            return;
        }

        //设置token
        setToken(token, false);
        NetUtil.getNet().exec(server.updateToken(dev_id, "android"), new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if (response.body() != null && response.body().isOk() && StringUtil.isNotNull(response.body().getData().getAccessToken())) {//保存token
                    initDB("" + response.body().getData().getUid());
                    TokenBean newToken = response.body().getData();
                    token.setAccessToken(newToken.getAccessToken());
                    token.setBankReqSignKey(EncrypUtil.aesDecode(token.getBankReqSignKey()));
                    setToken(token, true);
                    LogUtil.getLog().i("updateToken--成功", "--token=" + response.body().getData().getAccessToken());
                    LogUtil.writeLog("updateToken--成功" + "--token=" + response.body().getData().getAccessToken() + "--time=" + System.currentTimeMillis());
                    getMyInfo4Web(response.body().getData().getUid(), "");
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, null);
                }


            }

            @Override
            public void onFailure(Call<ReturnBean<TokenBean>> call, Throwable t) {
                super.onFailure(call, t);
                callback.onFailure(call, t);
                LogUtil.getLog().i("updateToken--失败", "--token=" + token.getAccessToken() + "--msg=" + t.getMessage());
                LogUtil.writeLog("updateToken--失败" + "--token=" + token.getAccessToken() + "--msg=" + t.getMessage());
            }
        });

    }

    /***
     * 登出
     */
    public void loginOut() {
        cleanInfo();
        NetUtil.getNet().exec(server.loginOut("android"), new CallBack<ReturnBean>() {
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
        myInfo = null;
        new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).clear();
        LogUtil.writeLog("清除token");
    }


    /***
     * 应用和保存token,添加到http请求头
     * 服务端有效期是7天，客户端3天刷新一次
     */
    private void setToken(TokenBean token, boolean isUpdate) {
        if (isUpdate) {
            long validTime = System.currentTimeMillis() + TimeToString.DAY * 3;
            token.setValidTime(validTime);
            //银行签名，加密存储
            if (!TextUtils.isEmpty(token.getBankReqSignKey())) {
                String key = token.getBankReqSignKey();
                PayEnvironment.getInstance().setBankSign(key);
                String result = EncrypUtil.aesEncode(key);
                token.setBankReqSignKey(result);
                new SharedPreferencesUtil(SharedPreferencesUtil.SPName.BANK_SIGN).save2Json(result);
            }
        }
        new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).save2Json(token);
        NetIntrtceptor.headers = Headers.of("X-Access-Token", token.getAccessToken());
        PayEnvironment.getInstance().setToken(token.getAccessToken());
        LogUtil.getLog().i("设置token", "--token=" + token.getAccessToken());
        LogUtil.writeLog("设置token" + "--token=" + token.getAccessToken() + "--time=" + System.currentTimeMillis() + "--isUpdate=" + isUpdate);
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
    public void friendApply(Long uid, String sayHi, String contactName, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.requestFriend(uid, sayHi, contactName), callback);
    }

    /***
     * 好友同意
     */
    public void friendAgree(Long uid, String contactName, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.acceptFriend(uid, contactName), callback);
    }

    /***
     * 加黑名单
     */
    public void friendBlack(Long uid, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.addBlackList(uid), callback);

    }

    /***
     * 移除黑名单
     */
    public void friendBlackRemove(Long uid, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.removeBlackList(uid), callback);
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
        NetUtil.getNet().exec(server.normalFriendsGet(), new CallBack<ReturnBean<List<UserInfo>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<UserInfo>>> call, Response<ReturnBean<List<UserInfo>>> response) {

                if (response.body() == null)
                    return;

                if (response.body().isOk()) {
                    List<UserInfo> list = response.body().getData();
                    //TODO zjy 模拟新增文件小助手项 id=3，展示在通讯录，暂无接口
//                    UserInfo tempUser = new UserInfo();
//                    tempUser.setName("常信文件传输助手");
//                    tempUser.setUid(Constants.CX_FILE_HELPER_UID);
//                    tempUser.setuType(ChatEnum.EUserType.ASSISTANT);
//                    tempUser.setHead("http://zx-im-img.zhixun6.com/static/%E5%B8%B8%E4%BF%A1%E5%B0%8F%E5%8A%A9%E6%89%8B.png");
//                    tempUser.setStat(9);
//                    list.add(tempUser);
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
    public void friendGet4Apply(CallBack<ReturnBean<List<ApplyBean>>> callback) {
        NetUtil.getNet().exec(server.requestFriendsGet(), callback);
    }

    /***
     * 黑名单
     */
    public void friendGet4Black(CallBack<ReturnBean<List<UserInfo>>> callback) {
        NetUtil.getNet().exec(server.blackListFriendsGet(), callback);
    }

    /**
     * 获取所有好友列表请求
     */
    public void friendGet4All(CallBack<ReturnBean<List<UserInfo>>> callback) {
        NetUtil.getNet().exec(server.getAllFriendsGet(), callback);
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
                    myInfo = dao.findUserBean(getMyId());
                    if (!TextUtils.isEmpty(imid))
                        myInfo.setImid(imid);
                    if (!TextUtils.isEmpty(avatar))
                        myInfo.setHead(avatar);
                    if (!TextUtils.isEmpty(nickname))
                        myInfo.setName(nickname);
                    if (gender != null)
                        myInfo.setSex(gender);
                    updateUser2DB(myInfo);
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
        NetUtil.getNet().exec(server.register(phone, captcha, "android", devid, VersionUtil.getPhoneModel(), StringUtil.getChannelName(AppConfig.getContext())), new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                super.onResponse(call, response);
                if (response.body() != null && response.body().isOk() && StringUtil.isNotNull(response.body().getData().getAccessToken())) {//保存token
                    initDB("" + response.body().getData().getUid());
                    setToken(response.body().getData(), true);
                    getMyInfo4Web(response.body().getData().getUid(), "");
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
        NetUtil.getNet().exec(server.login4Captch(phone, captcha, "android", devid, VersionUtil.getPhoneModel(), StringUtil.getChannelName(AppConfig.getContext())), new Callback<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                if (response.body() != null && response.body().isOk() && StringUtil.isNotNull(response.body().getData().getAccessToken())) {//保存token
                    LogUtil.writeLog("手机验证码登录获取token" + "--token=" + response.body().getData().getAccessToken());
                    initDB("" + response.body().getData().getUid());
                    setToken(response.body().getData(), true);
                    getMyInfo4Web(response.body().getData().getUid(), "");
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
     * 根据常信号获取个人资料
     */
    public void getUserInfoByImid(String imid, CallBack<ReturnBean<UserInfo>> callback) {
        NetUtil.getNet().exec(server.getUserInfoByImid(imid), callback);
    }


    /**
     * 根据关键字匹配常信号或手机号获取用户信息
     */
    public void getUserInfoByKeyword(String keyWord, CallBack<ReturnBean<List<UserInfo>>> callback) {
        NetUtil.getNet().exec(server.getUserInfoByKeyword(keyWord), callback);
    }

    /**
     * 修改用户密码
     */
    public void setUserPassword(String newPassword, String oldPassword, CallBack<ReturnBean> callback) {

        NetUtil.getNet().exec(server.setUserPassword(MD5.md5(newPassword), MD5.md5(oldPassword)), callback);
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

        NetUtil.getNet().exec(server.changePasswordBySms(phone, captcha, MD5.md5(password)), callback);
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
                    myInfo = dao.findUserBean(getMyId());
                    myInfo.setAuthStat(1);
                    updateUser2DB(myInfo);
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
                    myInfo = dao.findUserBean(getMyId());
                    myInfo.setAuthStat(2);
                    updateUser2DB(myInfo);
                }
                callback.onResponse(call, response);
            }
        });
    }


    /**
     * 版本更新
     */
    public void getNewVersion(String channelName, CallBack<ReturnBean<NewVersionBean>> callback) {
        NetUtil.getNet().exec(server.getNewVersion("android", channelName), callback);
    }

    /*
     * 获取本地用户信息
     * */
    public UserInfo getUserInfoInLocal(Long uid) {
        if (uid == null) {
            return null;
        }
        return dao.findUserInfo(uid);
    }


    /**
     * 初始化用户密码
     */
    public void initUserPassword(String password, CallBack<ReturnBean> callback) {

        NetUtil.getNet().exec(server.initUserPassword(MD5.md5(password)), callback);
    }

    /**
     * 投诉
     */
    public void userComplaint(int complaintType, String illegalDescription, String illegalImage, String respondentGid, String respondentUid, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.userComplaint(complaintType, illegalDescription, illegalImage, respondentGid, respondentUid), callback);
    }


    /**
     * 获取通讯录好友在线状态
     */
    public void getUsersOnlineStatus(final CallBack<ReturnBean<List<OnlineBean>>> callback) {
        NetUtil.getNet().exec(server.getUsersOnlineStatus(), new CallBack<ReturnBean<List<OnlineBean>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<OnlineBean>>> call, Response<ReturnBean<List<OnlineBean>>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    List<OnlineBean> list = response.body().getData();
                    dao.updateUsersOnlineStatus(list);
                }
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ReturnBean<List<OnlineBean>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    /**
     * 初始化用户意见反馈
     */
    public void userOpinion(String opinionDescription, String opinionImage, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.userOpinion(opinionDescription, opinionImage), callback);
    }

    /**
     * 保存网易云登录账号与Token，用于在网易初始化的时候自动登录
     *
     * @param neteaseAccid
     * @param neteaseToken
     */
    private void saveNeteaseAccid(String neteaseAccid, String neteaseToken) {
        SpUtil spUtil = SpUtil.getSpUtil();
        spUtil.putSPValue(Preferences.KEY_USER_ACCOUNT, neteaseAccid);
        spUtil.putSPValue(Preferences.KEY_USER_TOKEN, neteaseToken);
    }

    /**
     * 网易云登录
     *
     * @param neteaseAccid 账号
     * @param neteaseToken Token
     */
    public void doNeteaseLogin(String neteaseAccid, String neteaseToken) {
        LoginInfo info = new LoginInfo(neteaseAccid, neteaseToken);
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo param) {
                        if (param != null) {
                            AVChatProfile.setAccount(param.getAccount());
                        }
                        LogUtil.getLog().d("MainActivity", "网易云登录onSuccess");
                        LogUtil.writeLog(">>>>>>>>>网易云登录onSuccess>>>>>>>>>>>> ");
                    }

                    @Override
                    public void onFailed(int code) {
                        LogUtil.getLog().d("MainActivity", "网易云登录onFailed:" + code);
                        LogUtil.writeLog(">>>>>>>>>网易云登录onFailed>>>>>>>>>>>> code:" + code);
                    }

                    @Override
                    public void onException(Throwable exception) {
                        LogUtil.getLog().d("MainActivity", "网易云登录exception:" + exception.getMessage());
                        LogUtil.writeLog(">>>>>>>>>网易云登录exception>>>>>>>>>>>> exception:" + exception.getMessage());
                    }
                    // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
                };
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(callback);
    }


    /**
     * 设置已读开关
     */
    public void friendsSetRead(long uid, int read, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.friendsSetRead(uid, read), callback);
    }

    /**
     * 获取单个群成员信息
     *
     * @param gid      群id
     * @param uid      群成员ID
     * @param callback
     */
    public void getSingleMemberInfo(String gid, int uid, Callback<ReturnBean<SingleMeberInfoBean>> callback) {
        NetUtil.getNet().exec(server.getSingleMemberInfo(gid, uid), callback);
    }

    /**
     * 获取用户信息
     */
    public void getUserInfoById(Long userId, final CallBack<ReturnBean<UserInfo>> callBack) {
        NetUtil.getNet().exec(server.getUserInfo(userId), callBack);
    }

    /**
     * 上报用户地理位置信息
     */
    public void postLocation(String city, String country, String lat, String lon, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.postLocation(city, country, lat, lon), callback);
    }

    /**
     * 二维码登录 - 扫描提交
     */
    public void sweepCodeLoginCommit(String code, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.sweepCodeLoginCommit(code), callback);
    }


    /**
     * 二维码登录 - 确认登录
     *
     * @param code
     * @param sync     1 同步 0 不同步
     * @param callback
     */
    public void sweepCodeLoginSure(String code, String sync, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.sweepCodeLoginSure(code, sync), callback);
    }

    /**
     * 二维码登录 - 取消登录
     */
    public void sweepCodeLoginCancel(String code, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.sweepCodeLoginCancel(code), callback);
    }


    /**
     * 上报IP
     */
    public void reportIP(String ip, CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.reportIPChange(ip), callback);
    }

    private static UserBean convertToUserBean(UserInfo info) {
        if (info == null) {
            return null;
        }
        UserBean userBean = new UserBean();
        userBean.setUid(info.getUid());
        userBean.setName(info.getName());
        userBean.setMkName(info.getMkName());
        userBean.setVip(info.getVip());
        userBean.setHead(info.getHead());
        userBean.setActiveType(info.getActiveType());
        userBean.setAuthStat(info.getAuthStat());
        userBean.setDisturb(info.getDisturb());
        userBean.setTag(info.getTag());
        userBean.setIstop(info.getIstop());
        userBean.setDestroy(info.getDestroy());
        userBean.setSex(info.getSex());
        userBean.setEmptyPassword(info.isEmptyPassword());
        userBean.setLockCloudRedEnvelope(info.getLockCloudRedEnvelope());
        userBean.setDisplaydetail(info.getDisplaydetail());
        userBean.setFriendRead(info.getFriendRead());
        userBean.setMasterRead(info.getMasterRead());
        userBean.setFriendvalid(info.getFriendvalid());
        userBean.setGroupvalid(info.getGroupvalid());
        userBean.setImid(info.getImid());
        userBean.setOldimid(info.getOldimid());
        userBean.setInviter(info.getInviter());
        userBean.setInviterName(info.getInviterName());
        userBean.setMessagenotice(info.getMessagenotice());
        userBean.setJoinTime(info.getJoinTime());
        userBean.setJoinType(info.getJoinType());
        userBean.setImidfind(info.getImidfind());
        userBean.setDestroyTime(info.getDestroyTime());
        userBean.setMyRead(info.getMyRead());
        userBean.setNeteaseAccid(info.getNeteaseAccid());
        userBean.setLastonline(info.getLastonline());
        userBean.setPhone(info.getPhone());
        userBean.setPhonefind(info.getPhonefind());
        userBean.setSayHi(info.getSayHi());
        userBean.setScreenshotNotification(info.getScreenshotNotification());
        userBean.setStat(info.getStat());
        userBean.setuType(info.getuType());
        userBean.setBankReqSignKey(info.getBankReqSignKey());
        return userBean;
    }


}
