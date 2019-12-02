package com.hm.cxpay.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.bank.BankBean;
import com.hm.cxpay.ui.bank.BankInfo;
import com.hm.cxpay.ui.bank.BindBankInfo;

import net.cb.cb.library.utils.encrypt.MD5;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @anthor Liszt
 * @data 2019/11/28
 * Description
 */
public class PayHttpUtils {

    private static PayHttpUtils instance;

    public static PayHttpUtils getInstance() {
        if (instance == null) {
            instance = new PayHttpUtils();
        }
        return instance;
    }

    private static RequestBody getRequestBody(Map<String, String> map) {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), mapToJSON(map));
    }

    /**
     * 将Map转化为Json
     */
    private static String mapToJSON(Map<String, String> map) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.toJson(map);
    }


    //用户认证
    public Observable<BaseResponse> authUserInfo(String idNum, String realName) {
        Map<String, String> map = new HashMap<>();
        map.put("idNumber", idNum);
        map.put("realName", realName);
        map.put("idType", 1 + "");
        return HttpChannel.getInstance().getPayService().authUserInfo(getRequestBody(map));
    }

    //银行卡检测
    public Observable<BaseResponse<BankInfo>> checkBankCard(String bankCardNo) {
        Map<String, String> map = new HashMap<>();
        map.put("bankCardNo", bankCardNo);
        return HttpChannel.getInstance().getPayService().checkBankCard(getRequestBody(map));
    }


    //申请绑定银行卡 银行卡号，银行预留手机号
    public Observable<BaseResponse<BindBankInfo>> applyBindBank(String bankCardNo, String phone) {
        Map<String, String> map = new HashMap<>();
        map.put("bankCardNo", bankCardNo);
        map.put("phone", phone);
        return HttpChannel.getInstance().getPayService().applyBindBankCard(getRequestBody(map));
    }


    //申请绑定银行卡 银行卡号，银行预留手机号
    public Observable<BaseResponse<List<BankBean>>> getBankList() {
        return HttpChannel.getInstance().getPayService().getBindBankCardList();
    }


    //绑定银行卡
    public Observable<BaseResponse> bindBank(String applySign, String bankCardNo, String bankName, String phone, String tranceNum, String transDate, String verificationCode) {
        Map<String, String> map = new HashMap<>();
        map.put("applySign", applySign);
        map.put("bankCardNo", bankCardNo);
        map.put("bankName", bankName);
        map.put("phone", phone);
        map.put("tranceNum", tranceNum);
        map.put("transDate", transDate);
        map.put("verificationCode", verificationCode);
        return HttpChannel.getInstance().getPayService().bindBank(getRequestBody(map));
    }

    //获取用户信息
    public Observable<BaseResponse<UserBean>> getUserInfo() {
        return HttpChannel.getInstance().getPayService().getUserInfo();
    }

    //设置支付密码
    public Observable<BaseResponse> setPayword(String pwd) {
        Map<String, String> map = new HashMap<>();
        map.put("pwd", MD5.md5(pwd));
        return HttpChannel.getInstance().getPayService().setPayword(getRequestBody(map));
    }


}
