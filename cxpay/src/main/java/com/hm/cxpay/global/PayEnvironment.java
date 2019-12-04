package com.hm.cxpay.global;

import android.content.Context;

import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.ui.bank.BankBean;

import java.util.List;

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
    private List<BankBean> banks;//绑定银行卡

    public static PayEnvironment getInstance() {
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

    public List<BankBean> getBanks() {
        return banks;
    }

    public void setBanks(List<BankBean> banks) {
        this.banks = banks;
    }

    //获取默认第一顺位支付银行卡
    public BankBean getFirstBank() {
        if (banks != null && banks.size() > 0) {
            return banks.get(0);
        }
        return null;
    }
}
