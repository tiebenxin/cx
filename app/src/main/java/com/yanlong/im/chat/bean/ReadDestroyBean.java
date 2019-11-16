package com.yanlong.im.chat.bean;

/**
 * @创建人 shenxin
 * @创建时间 2019/10/12 0012 16:06
 */
public class ReadDestroyBean {

    public int survivaltime;
    public String gid;
    public long uid;

    public ReadDestroyBean(int survivaltime, String gid, long uid) {
        this.survivaltime = survivaltime;
        this.gid = gid;
        this.uid = uid;
    }

}
