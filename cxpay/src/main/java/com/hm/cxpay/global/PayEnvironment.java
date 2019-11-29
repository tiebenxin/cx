package com.hm.cxpay.global;

import android.content.Context;

import com.hm.cxpay.bean.UserBean;

/**
 * @anthor Liszt
 * @data 2019/11/29
 * Description 支付环境：token,context, 用户信息
 */
public class PayEnvironment {
    private static PayEnvironment INSTANCE;
    private UserBean user;
    private String token;
    private Context context;

    public static PayEnvironment getIntance() {
        if (INSTANCE == null) {
            INSTANCE = new PayEnvironment();
        }
        return INSTANCE;
    }



    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
