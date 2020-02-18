package com.yanlong.im.utils;

import com.kye.net.IKyeVolleyInit;
import com.kye.net.LoginFailBean;

import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-02-18
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class IVolleyInitImp implements IKyeVolleyInit {
    @Override
    public void LoginOut(String s) {

    }

    @Override
    public boolean isCanOneKeyLogin() {
        return false;
    }

    @Override
    public void onkeyLogin() {

    }

    @Override
    public String getUUID() {
        return null;
    }

    @Override
    public void compatibleOldNetRequest(List<LoginFailBean> list) {

    }
}
