package com.yanlong.im.circle.mycircle;

import com.yanlong.im.circle.bean.CircleTrendsBean;
import com.yanlong.im.circle.bean.FriendUserBean;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.NetUtil;

import java.util.List;

/**
 * @类名：临时用网络请求，后续可合并共用一个
 * @Date：2020/9/23
 * @by zjy
 * @备注：
 */

public class TempAction {
    private TempServer server;

    public TempAction() {
        server = NetUtil.getNet().create(TempServer.class);
    }

    /**
     * 获取我关注的人列表
     */
    public void httpGetMyFollowList(final int currentPage, final int pageSize, final CallBack<ReturnBean<List<FriendUserBean>>>  callback) {
        NetUtil.getNet().exec(server.httpGetMyFollowList(currentPage,pageSize), callback);
    }

    /**
     * 获取关注我的人列表
     */
    public void httpGetFollowMeList(final int currentPage, final int pageSize, final CallBack<ReturnBean<List<FriendUserBean>>>  callback) {
        NetUtil.getNet().exec(server.httpGetFollowMeList(currentPage,pageSize), callback);
    }

    /**
     * 关注用户
     */
    public void httpToFollow(final long uid, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.httpToFollow(uid), callback);
    }

    /**
     * 取消关注用户
     */
    public void httpCancelFollow(final long uid, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.httpCancelFollow(uid), callback);
    }

    /**
     * 获取谁看过我列表
     * @param type 1 我看过谁 2 谁看过我
     */

    public void httpGetWhoSeeMeList(final int currentPage, final int pageSize, final int type, final CallBack<ReturnBean<List<FriendUserBean>>>  callback) {
        NetUtil.getNet().exec(server.httpGetWhoSeeMeList(currentPage,pageSize,type), callback);
    }

    /**
     * 删除访问记录 (我看过谁)
     * @param uid 对方uid
     */

    public void httpDeleteVisitRecord(final long uid, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.httpDeleteVisitRecord(uid), callback);
    }


    /**
     * 获取我的动态(说说主页及列表)
     */

    public void httpGetMyTrends(final int currentPage, final int pageSize, final CallBack<ReturnBean<CircleTrendsBean>> callback) {
        NetUtil.getNet().exec(server.httpGetMyTrends(currentPage,pageSize), callback);
    }

    /**
     * 更新背景图
     */
    public void httpSetBackground(final String url, final CallBack<ReturnBean> callback) {
        NetUtil.getNet().exec(server.httpSetBackground(url), callback);
    }
}
