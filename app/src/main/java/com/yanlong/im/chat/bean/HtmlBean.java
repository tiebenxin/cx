package com.yanlong.im.chat.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @创建人 shenxin
 * @创建时间 2019/8/22 0022 16:09
 */
public class HtmlBean {

    private String gid;
    private List<HtmlBeanList> list = new ArrayList<>();

    public List<HtmlBeanList> getList() {
        return list;
    }

    public void setList(List<HtmlBeanList> list) {
        this.list = list;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

}
