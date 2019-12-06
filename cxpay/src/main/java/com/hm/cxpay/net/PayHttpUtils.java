package com.hm.cxpay.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hm.cxpay.bean.CommonBean;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.bank.BankBean;
import com.hm.cxpay.ui.bank.BankInfo;
import com.hm.cxpay.ui.bank.BindBankInfo;
import com.hm.cxpay.ui.redenvelope.RedDetailsBean;
import com.hm.cxpay.ui.redenvelope.RedSendBean;
import com.hm.cxpay.utils.UIUtils;

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


    //获取已经绑定银行卡列表
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

    //修改支付密码
    public Observable<BaseResponse> modifyPayword(String oldPayword,String newPayword) {
        Map<String, String> map = new HashMap<>();
        map.put("currentPwd", MD5.md5(oldPayword));
        map.put("newPwd", MD5.md5(newPayword));
        return HttpChannel.getInstance().getPayService().modifyPayword(getRequestBody(map));
    }

    //检查支付密码
    public Observable<BaseResponse> checkPayword(String pwd) {
        Map<String, String> map = new HashMap<>();
        map.put("pwd", MD5.md5(pwd));
        return HttpChannel.getInstance().getPayService().checkPayword(getRequestBody(map));
    }

    //解绑银行卡
    public Observable<BaseResponse> deleteBankcard(String bankCardId) {
        Map<String, String> map = new HashMap<>();
        map.put("bankCardId", bankCardId);
        return HttpChannel.getInstance().getPayService().deleteBankcard(getRequestBody(map));
    }

    //充值
    public Observable<BaseResponse<CommonBean>> toRecharge(int amt, long bankCardId, String payPwd) {
        Map<String, String> map = new HashMap<>();
        map.put("amt", UIUtils.getFen(amt+"")+"");
        map.put("bankCardId", bankCardId + "");
        map.put("payPwd", MD5.md5(payPwd));
        map.put("actionId", UIUtils.getUUID());
        return HttpChannel.getInstance().getPayService().toRecharge(getRequestBody(map));
    }

    //提现
    public Observable<BaseResponse<CommonBean>> toWithdraw(int amt, long bankCardId, String payPwd) {
        Map<String, String> map = new HashMap<>();
        map.put("amt", UIUtils.getFen(amt+"")+"");
        map.put("bankCardId", bankCardId + "");
        map.put("payPwd", MD5.md5(payPwd));
        map.put("actionId", UIUtils.getUUID());
        return HttpChannel.getInstance().getPayService().toWithdraw(getRequestBody(map));
    }

    //获取系统费率
    public Observable<BaseResponse<CommonBean>> getRate() {
        return HttpChannel.getInstance().getPayService().getRate();
    }


    //获取红包明细  type—— 7：收到红包； 2 —— 发出红包
    public Observable<BaseResponse<RedDetailsBean>> getRedEnvelopeDetails(int pageNum, long startTime, int type) {
        Map<String, String> map = new HashMap<>();
        map.put("pageNum", pageNum + "");
        map.put("pageSize", 20 + "");
        map.put("startTime", startTime + "");
        map.put("type", type + "");
        return HttpChannel.getInstance().getPayService().getRedEnvelopeDetails(getRequestBody(map));
    }

    /**
     * 发送红包给单人:
     * amt——发送金额，单位：分；count——发送个数；payPwd——支付密码；type——红包类型，拼手气 1或者普通红包 0，
     * bankCardId——当发送金额大于零钱余额，必填；note——恭喜发财，大吉大利，uid-红包发送给谁
     */
    public Observable<BaseResponse<RedSendBean>> sendRedEnvelopeToUser(String actionId, long amt, int count, String payPwd, int type, long bankCardId, String note, long uid) {
        Map<String, String> map = new HashMap<>();
        map.put("actionId", actionId);
        map.put("amt", amt + "");
        map.put("cnt", count + "");
        map.put("payPwd", MD5.md5(payPwd));
        if (bankCardId > 0) {
            map.put("bankCardId", bankCardId + "");
        }
        map.put("note", note);
        map.put("type", type + "");
        map.put("toUid", uid + "");
        return HttpChannel.getInstance().getPayService().sendRedEnvelope(getRequestBody(map));
    }

    /**
     * 发送红包给群:
     * amt——发送金额，单位：分；count——发送个数；payPwd——支付密码；type——红包类型，拼手气1或者普通红包0，
     * bankCardId——当发送金额大于零钱余额，必填；note——恭喜发财，大吉大利，uid-红包发送给谁
     */
    public Observable<BaseResponse<RedSendBean>> sendRedEnvelopeToGroup(String actionId, long amt, int count, String payPwd, int type, long bankCardId, String note, String gid) {
        Map<String, String> map = new HashMap<>();
        map.put("actionId", actionId);
        map.put("amt", amt + "");
        map.put("cnt", count + "");
        map.put("payPwd", MD5.md5(payPwd));
        if (bankCardId > 0) {
            map.put("bankCardId", bankCardId + "");
        }
        map.put("note", note);
        map.put("type", type + "");
        map.put("toGid", gid);
        return HttpChannel.getInstance().getPayService().sendRedEnvelope(getRequestBody(map));
    }


}
