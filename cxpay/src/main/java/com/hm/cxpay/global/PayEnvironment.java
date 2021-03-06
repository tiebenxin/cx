package com.hm.cxpay.global;

import android.content.Context;
import android.text.TextUtils;

import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.bean.BankBean;
import com.hm.cxpay.eventbus.NoticeReceiveEvent;
import com.hm.cxpay.eventbus.RefreshBalanceEvent;

import net.cb.cb.library.bean.CanStampEvent;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.encrypt.EncrypUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author Liszt
 * @date 2019/11/29
 * Description 支付环境：token,context, 用户信息
 */
public class PayEnvironment {
    private static PayEnvironment INSTANCE;
    private UserBean user;
    private String token;
    private Context context;
    private List<BankBean> banks;//绑定银行卡
    private String phone;//用户手机号
    private String nick;//用户昵称
    private String bankSign;//银行签名
    private long userId = 0;
    private long serverTime;//初始服务器时间
    private long localTime;//初始本地时间

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
        if (TextUtils.isEmpty(token)) {
            token = "";
        }
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getBankSign() {
        if (TextUtils.isEmpty(bankSign)) {
            String json = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.BANK_SIGN).get4Json(String.class);
            if (!TextUtils.isEmpty(json)) {
                bankSign = EncrypUtil.aesDecode(json);
            }
        }
        return bankSign;
    }

    public void setBankSign(String bankSign) {
        this.bankSign = bankSign;
    }

    //账号退出的时候，需要清除缓存
    public void clear() {
        user = null;
        banks = null;
        token = null;
        phone = null;
        nick = null;
        bankSign = null;
        userId = 0;
        new SharedPreferencesUtil(SharedPreferencesUtil.SPName.BANK_SIGN).clear();
    }

    //通知刷新余额，发出红包，拆红包成功，转账成功，都需要及时刷新
    public void notifyRefreshBalance() {
        EventBus.getDefault().post(new RefreshBalanceEvent());
    }

    //通知更改是否能显示戳一戳，发红包，充值，提现 均不能显示戳一戳
    public void notifyStampUpdate(boolean canStamp) {
        EventBus.getDefault().post(new CanStampEvent(canStamp));
    }

    //提醒对方收款
    public void notifyReceive(String rid) {
        EventBus.getDefault().post(new NoticeReceiveEvent(rid));
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    //tcp认证的时候，初始化服务器时间，及本地时间
    public void initTime(long serverTime, long localTime) {
        this.serverTime = serverTime;
        this.localTime = localTime;
    }

    //获取当前的服务器时间,单位：s
    public long getFixTime() {
        long result = 0;
        if (serverTime > 0 && localTime > 0) {
            result = serverTime + (System.currentTimeMillis() - localTime);
        }
        if (result <= 0) {
            result = System.currentTimeMillis();
        }
        result = result / 1000;
        return result;
    }
}
