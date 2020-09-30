package com.yanlong.im.circle.bean;

import net.cb.cb.library.base.BaseBean;

import java.util.List;

/**
 * @类名：我的动态(好友动态)实体类
 * @Date：2020/9/26
 * @by zjy
 * @备注：
 */
public class CircleTrendsBean extends BaseBean {

    private int accessCount;//看过我的人总数
    private String bgImage;//主页背景图
    private int followMyCount;//关注我的人总数
    private int myFollowCount;//我关注的人总数
    private int myFollow;//我是否关注了他(0:否|1:是)
    private List<TrendBean> momentList;//说说列表

    public int getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public int getFollowMyCount() {
        return followMyCount;
    }

    public void setFollowMyCount(int followMyCount) {
        this.followMyCount = followMyCount;
    }

    public int getMyFollowCount() {
        return myFollowCount;
    }

    public void setMyFollowCount(int myFollowCount) {
        this.myFollowCount = myFollowCount;
    }

    public List<TrendBean> getMomentList() {
        return momentList;
    }

    public void setMomentList(List<TrendBean> momentList) {
        this.momentList = momentList;
    }

    public int getMyFollow() {
        return myFollow;
    }

    public void setMyFollow(int myFollow) {
        this.myFollow = myFollow;
    }
}
