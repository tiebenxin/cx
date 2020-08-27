package com.yanlong.im.data.remote;

import android.text.TextUtils;

import com.google.common.base.Function;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.ApplyBean;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.server.MsgServer;
import com.yanlong.im.user.bean.UserBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.server.UserServer;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 网络请求
 * 注意：请求回调中需要用数据库时，使用同步请求，或创建新数据库对象（异步回调配置在主线程，非当前线程）
 *
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/5 0005
 * @description
 */
public class MessageRemoteDataSource {
    private UserServer userService = NetUtil.getNet().create(UserServer.class);
    private MsgServer msgService = NetUtil.getNet().create(MsgServer.class);

    /**
     * 切换环境时，service得重置
     */
    public void clear() {
        userService = null;
        msgService = null;
    }

    /**
     * 检查sevice 是否需要重新构建
     */
    public void checkService() {
        if (userService == null) userService = NetUtil.getNet().create(UserServer.class);
        if (msgService == null) msgService = NetUtil.getNet().create(MsgServer.class);
    }

    /**
     * 获取申请添加好友的列表
     *
     * @param friendContactName 好友备注名，可为null
     * @param saveToDB          保存好友信息到数据库
     */
    public void getRequestFriends(String friendContactName, long uid, List<ApplyBean> listData,
                                  Function<ApplyBean, Boolean> saveToDB) {
        checkService();
        //同步请求
        NetUtil.getNet().exec(userService.requestFriendsGet(), new CallBack<ReturnBean<List<ApplyBean>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<ApplyBean>>> call, Response<ReturnBean<List<ApplyBean>>> response) {
                super.onResponse(call, response);
                if (response.body() == null || !response.body().isOk()) {
                    return;
                }
                List<ApplyBean> applyBeanList = response.body().getData();
                for (int i = 0; i < applyBeanList.size(); i++) {
                    ApplyBean applyBean = applyBeanList.get(i);
                    applyBean.setAid(applyBean.getUid() + "");
                    applyBean.setChatType(CoreEnum.EChatType.PRIVATE);
                    if (!TextUtils.isEmpty(friendContactName)) {
                        applyBean.setAlias(friendContactName);
                    }
                    // 每次只更新本次请求的好友，方便后面排序
                    if (uid == applyBean.getUid()) {
                        applyBean.setTime(System.currentTimeMillis());
                    } else if (listData != null && listData.size() > 0) {// 取出原来的时间
                        for (ApplyBean bean : listData) {
                            if (applyBean.getUid() == bean.getUid()) {
                                applyBean.setTime(bean.getTime());
                                break;
                            }
                        }
                    }
                    applyBean.setStat(1);
                    saveToDB.apply(applyBean);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<ApplyBean>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });

    }

    /***
     * 拉取服务器的自己的信息到数据库
     */
    public void getMyInfo(Long usrid, String imid, Function<UserBean, Boolean> saveToDB) {
        checkService();
        //同步请求
        NetUtil.getNet().exec(userService.getUserBean(usrid), new CallBack<ReturnBean<UserBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserBean>> call, Response<ReturnBean<UserBean>> response) {
                super.onResponse(call, response);
                UserBean userInfo = response.body().getData();
                if (userInfo != null) {
                    new SharedPreferencesUtil(SharedPreferencesUtil.SPName.IMAGE_HEAD).save2Json(userInfo.getHead() + "");
                    //保存手机或常信号登录
                    if (StringUtil.isNotNull(imid)) {
                        new SharedPreferencesUtil(SharedPreferencesUtil.SPName.IM_ID).save2Json(imid);
                    }
                    new SharedPreferencesUtil(SharedPreferencesUtil.SPName.PHONE).save2Json(userInfo.getPhone());
                    new SharedPreferencesUtil(SharedPreferencesUtil.SPName.UID).save2Json(userInfo.getUid());
                    userInfo.toTag();
                    saveToDB.apply(userInfo);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<UserBean>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    private List<Long> loadingFriends = new ArrayList<>();

    /**
     * 更新好友信息
     */
    public void getFriend(Long usrid, Function<UserInfo, Boolean> saveToDB) {
        checkService();
        if (usrid == null)
            return;
        if (loadingFriends.contains(usrid))
            return;
        loadingFriends.add(usrid);
        //同步请求
        NetUtil.getNet().exec(userService.getUserInfo(usrid), new CallBack<ReturnBean<UserInfo>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
                super.onResponse(call, response);
                //写入用户信息到数据库
                if (response.body() != null) {
                    UserInfo userInfo = response.body().getData();
                    if (userInfo != null && userInfo.getUid() != null) {
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
                        //更新到数据库
                        saveToDB.apply(userInfo);
                    }
                }
                loadingFriends.remove(usrid);
            }

            @Override
            public void onFailure(Call<ReturnBean<UserInfo>> call, Throwable t) {
                super.onFailure(call, t);
                loadingFriends.remove(usrid);
            }
        });
    }

    private List<String> loadingGroups = new ArrayList<>();

    /***
     * 获取群信息,并缓存
     * @param gid
     */
    public void getGroupInfo(final String gid, Function<Group, Boolean> saveToDB) {
        checkService();
        if (TextUtils.isEmpty(gid)) {
            return;
        }
        if (loadingGroups.contains(gid))
            return;
        loadingGroups.add(gid);
        //同步请求
        NetUtil.getNet().exec(msgService.groupInfo(gid), new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    LogUtil.getLog().d("a=", "MessageManager--加载群信息后的失败 response=null--gid=" + gid);
                    return;
                }
                if (response.body().isOk() && response.body().getData() != null) {//保存群友信息到数据库
                    Group newGroup = response.body().getData();
                    if (newGroup != null) {
                        //生成群昵称
                        newGroup.getMygroupName();
                        saveToDB.apply(newGroup);
                    }
                } else {
                    LogUtil.getLog().d("a=", "MessageManager--加载群信息后的失败--gid=" + gid);
                }
                loadingGroups.remove(gid);
            }

            @Override
            public void onFailure(Call<ReturnBean<Group>> call, Throwable t) {
                super.onFailure(call, t);
                loadingGroups.remove(gid);
            }
        });
    }
}
