package com.yanlong.im.data.remote;

import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.server.UserServer;

import net.cb.cb.library.bean.OnlineBean;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.NetUtil;

import java.util.List;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/4/11 0011
 * @description
 */
public class MainRemoteDataSource {
    private UserServer server;
    public MainRemoteDataSource(){
        server = NetUtil.getNet().create(UserServer.class);
    }

    /***
     * 获取单个用户信息并且缓存到数据库
     * @param usrid
     */
    public void requestUserInfoAndSave(Long usrid, CallBack<ReturnBean<UserInfo>> callBack) {
        NetUtil.getNet().exec(server.getUserInfo(usrid), callBack);
    }
    /**
     * 获取通讯录好友在线状态
     */
    public void requestUsersOnlineStatus(CallBack<ReturnBean<List<OnlineBean>>> callback){
        NetUtil.getNet().exec(server.getUsersOnlineStatus(), callback);
    }

}
