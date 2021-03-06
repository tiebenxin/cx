package com.hm.cxpay.net;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hm.cxpay.bean.BankBean;
import com.hm.cxpay.bean.BankInfo;
import com.hm.cxpay.bean.BillBean;
import com.hm.cxpay.bean.BindBankInfo;
import com.hm.cxpay.bean.CommonBean;
import com.hm.cxpay.bean.EnvelopeDetailBean;
import com.hm.cxpay.bean.GrabEnvelopeBean;
import com.hm.cxpay.bean.OpenEnvelopeBean;
import com.hm.cxpay.bean.RedDetailsBean;
import com.hm.cxpay.bean.SendResultBean;
import com.hm.cxpay.bean.TransferDetailBean;
import com.hm.cxpay.bean.TransferResultBean;
import com.hm.cxpay.bean.UrlBean;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.utils.PayUtils;
import com.hm.cxpay.utils.UIUtils;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.utils.GsonUtils;
import net.cb.cb.library.utils.encrypt.MD5;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.http.Url;

/**
 * @author Liszt
 * @date 2019/11/28
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

    private static RequestBody getRequestBody(String content) {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
    }

    /**
     * 将Map转化为Json
     */
    private static String mapToJSON(Map<String, String> map) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.toJson(map);
    }

    /**
     * 新增认证签名
     *
     * @return
     */
    private Map<String, String> getAuthMap() {
        String rand = PayUtils.getRandomNumber() + "";//随机数
        String ts = PayEnvironment.getInstance().getFixTime() + "";//时间戳
        String uid = "";//uid
        if (PayEnvironment.getInstance().getUserId() > 0) {
            uid = PayEnvironment.getInstance().getUserId() + "";
        }
        if (TextUtils.isEmpty(uid)) {
            if (PayEnvironment.getInstance().getUser() != null) {
                if (PayEnvironment.getInstance().getUser().getUid() != 0) {
                    uid = PayEnvironment.getInstance().getUser().getUid() + "";
                }
            }
        }

        String key = "";//密钥
        if (!TextUtils.isEmpty(PayEnvironment.getInstance().getBankSign())) {
            key = PayEnvironment.getInstance().getBankSign();
        }
        Map<String, String> authMap = new HashMap<>();
        authMap.put("rand", rand);
        authMap.put("ts", ts);
        authMap.put("sign", PayUtils.getSignature(rand + "." + ts + "." + uid, key));
        return authMap;
    }

    /**
     * 新增认证签名 - 获取用户信息接口专用
     *
     * @return
     */
    private Map<String, String> getUserInfoAuthMap(long uid) {
        String rand = PayUtils.getRandomNumber() + "";//随机数
        String ts = PayEnvironment.getInstance().getFixTime() + "";//时间戳
        String key = "";//密钥
        if (!TextUtils.isEmpty(PayEnvironment.getInstance().getBankSign())) {
            key = PayEnvironment.getInstance().getBankSign();
        }
        if (uid <= 0) {
            uid = PayEnvironment.getInstance().getUserId();
        }
        Map<String, String> authMap = new HashMap<>();
        authMap.put("rand", rand);
        authMap.put("ts", ts);
        authMap.put("sign", PayUtils.getSignature(rand + "." + ts + "." + uid, key));
        return authMap;
    }


    //用户认证
    public Observable<BaseResponse> authUserInfo(String idNum, String realName) {
        Map<String, String> map = new HashMap<>();
        map.put("idNumber", idNum);
        map.put("realName", realName);
        map.put("idType", 1 + "");
        return HttpChannel.getInstance().getPayService().authUserInfo(getRequestBody(map), getAuthMap());
    }

    //银行卡检测
    public Observable<BaseResponse<BankInfo>> checkBankCard(String bankCardNo) {
        Map<String, String> map = new HashMap<>();
        map.put("bankCardNo", bankCardNo);
        return HttpChannel.getInstance().getPayService().checkBankCard(getRequestBody(map), getAuthMap());
    }


    //申请绑定银行卡 银行卡号，银行预留手机号
    public Observable<BaseResponse<BindBankInfo>> applyBindBank(String bankCardNo, String phone) {
        Map<String, String> map = new HashMap<>();
        map.put("bankCardNo", bankCardNo);
        map.put("phone", phone);
        return HttpChannel.getInstance().getPayService().applyBindBankCard(getRequestBody(map), getAuthMap());
    }


    //获取已经绑定银行卡列表
    public Observable<BaseResponse<UrlBean>> getBankUrl() {
        return HttpChannel.getInstance().getPayService().getBankUrl(getAuthMap());
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
        return HttpChannel.getInstance().getPayService().bindBank(getRequestBody(map), getAuthMap());
    }

    //获取用户信息
    public Observable<BaseResponse<UserBean>> getUserInfo(long uid) {
        return HttpChannel.getInstance().getPayService().getUserInfo(getUserInfoAuthMap(uid));
    }

    //设置支付密码
    public Observable<BaseResponse> setPayword(String pwd) {
        Map<String, String> map = new HashMap<>();
        map.put("pwd", MD5.md5(pwd));
        return HttpChannel.getInstance().getPayService().setPayword(getRequestBody(map), getAuthMap());
    }

    //修改支付密码
    public Observable<BaseResponse> modifyPayword(String oldPayword, String newPayword, String token) {
        Map<String, String> map = new HashMap<>();
        if (!TextUtils.isEmpty(oldPayword)) {
            map.put("currentPwd", MD5.md5(oldPayword));
        }
        if (!TextUtils.isEmpty(token)) {
            map.put("token", token);
        }
        map.put("newPwd", MD5.md5(newPayword));
        return HttpChannel.getInstance().getPayService().modifyPayword(getRequestBody(map), getAuthMap());
    }

    //修改支付密码-忘记支付密码
    public Observable<BaseResponse> forgetPayword(String oldPayword, String newPayword, String token) {
        Map<String, String> map = new HashMap<>();
        if (!TextUtils.isEmpty(oldPayword)) {
            map.put("currentPwd", MD5.md5(oldPayword));
        }
        if (!TextUtils.isEmpty(token)) {
            map.put("token", token);
        }
        map.put("newPwd", MD5.md5(newPayword));
        return HttpChannel.getInstance().getPayService().forgetPayword(getRequestBody(map), getAuthMap());
    }

    //检查支付密码
    public Observable<BaseResponse> checkPayword(String pwd) {
        Map<String, String> map = new HashMap<>();
        map.put("pwd", MD5.md5(pwd));
        return HttpChannel.getInstance().getPayService().checkPayword(getRequestBody(map), getAuthMap());
    }

    //解绑银行卡
//    public Observable<BaseResponse> deleteBankcard(String bankCardId) {
//        Map<String, String> map = new HashMap<>();
//        map.put("bankCardId", bankCardId);
//        return HttpChannel.getInstance().getPayService().deleteBankcard(getRequestBody(map), getAuthMap());
//    }

    //跳转充值
    public Observable<BaseResponse<UrlBean>> toRecharge(double amt/*, long bankCardId, String payPwd*/) {
        Map<String, String> map = new HashMap<>();
        map.put("amt", UIUtils.getFen(amt + "") + "");
//        map.put("bankCardId", bankCardId + "");
//        map.put("payPwd", MD5.md5(payPwd));
        map.put("actionId", UIUtils.getUUID());
        return HttpChannel.getInstance().getPayService().toRecharge(getRequestBody(map), getAuthMap());
    }

    //跳转提现
    public Observable<BaseResponse<UrlBean>> toWithdraw(String amt/*, long bankCardId, String payPwd*/) {
        Map<String, String> map = new HashMap<>();
        map.put("amt", UIUtils.getFen(amt) + "");
//        map.put("bankCardId", bankCardId + "");
//        map.put("payPwd", MD5.md5(payPwd));
        map.put("actionId", UIUtils.getUUID());
        return HttpChannel.getInstance().getPayService().toWithdraw(getRequestBody(map), getAuthMap());
    }

    //获取系统费率
    public Observable<BaseResponse<CommonBean>> getRate() {
        return HttpChannel.getInstance().getPayService().getRate(getAuthMap());
    }

    //绑定手机-获取验证码
    public Observable<BaseResponse> getCode(String phoneNum) {
        Map<String, String> map = new HashMap<>();
        if (!TextUtils.isEmpty(phoneNum)) {
            map.put("phone", phoneNum);
        }
        return HttpChannel.getInstance().getPayService().getCode(getRequestBody(map), getAuthMap());
    }

    //绑定手机-获取当前用户IM手机号
    public Observable<BaseResponse<CommonBean>> getMyPhone() {
        return HttpChannel.getInstance().getPayService().getMyPhone(getAuthMap());
    }

    //绑定手机号
    public Observable<BaseResponse> bindPhoneNum(String phone, String verificationCode) {
        Map<String, String> map = new HashMap<>();
        if (!TextUtils.isEmpty(phone)) {
            map.put("phone", phone);
        }
        map.put("verificationCode", verificationCode);
        return HttpChannel.getInstance().getPayService().bindPhoneNum(getRequestBody(map), getAuthMap());
    }

    //获取账单明细
    public Observable<BaseResponse<BillBean>> getBillDetailsList(int pageNum, long startTime, int type, String id) {
        Map<String, String> map = new HashMap<>();
        map.put("pageNum", pageNum + "");
        map.put("pageSize", 20 + "");
        map.put("startTime", startTime + "");
        map.put("type", type + "");
        if (!TextUtils.isEmpty(id)) {
            map.put("id", id);
        }
        return HttpChannel.getInstance().getPayService().getBillDetailsList(getRequestBody(map), getAuthMap());
    }

    //获取零钱明细
    public Observable<BaseResponse<BillBean>> getChangeDetailsList(int pageNum, long startTime) {
        Map<String, String> map = new HashMap<>();
        map.put("pageNum", pageNum + "");
        map.put("pageSize", 20 + "");
        map.put("startTime", startTime + "");
        return HttpChannel.getInstance().getPayService().getChangeDetailsList(getRequestBody(map), getAuthMap());
    }

    //验证实名信息-忘记密码辅助验证第一步 (第二步为检查银行卡)
    public Observable<BaseResponse<CommonBean>> checkRealNameInfo(String idNumber, String realName) {
        Map<String, String> map = new HashMap<>();
        map.put("idNumber", idNumber);
        map.put("idType", "1");
        map.put("realName", realName);
        return HttpChannel.getInstance().getPayService().checkRealNameInfo(getRequestBody(map), getAuthMap());
    }

    //绑定银行卡-忘记密码辅助验证第三步
    public Observable<BaseResponse<CommonBean>> bindBankCard(String bankCardNo, String bankName, String phone, String token) {
        Map<String, String> map = new HashMap<>();
        map.put("bankCardNo", bankCardNo);
        map.put("bankName", bankName);
        map.put("phone", phone);
        map.put("token", token);
        return HttpChannel.getInstance().getPayService().bindBankCard(getRequestBody(map), getAuthMap());
    }

    //验证短信验证码-忘记密码辅助验证第四步
    public Observable<BaseResponse<CommonBean>> checkCode(String token, String verificationCode) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("verificationCode", verificationCode);
        return HttpChannel.getInstance().getPayService().checkCode(getRequestBody(map), getAuthMap());
    }

    //获取红包明细  type—— 7：收到红包； 2 —— 发出红包
    public Observable<BaseResponse<RedDetailsBean>> getRedEnvelopeDetails(int pageNum, long startTime, int type) {
        Map<String, String> map = new HashMap<>();
        map.put("pageNum", pageNum + "");
        map.put("pageSize", 20 + "");
        map.put("startTime", startTime + "");
        map.put("type", type + "");
        return HttpChannel.getInstance().getPayService().getRedEnvelopeDetails(getRequestBody(map), getAuthMap());
    }

    /**
     * 发送红包给单人:
     * amt——发送金额，单位：分；count——发送个数；payPwd——支付密码；type——红包类型，拼手气 1或者普通红包 0，
     * bankCardId——当发送金额大于零钱余额，必填；note——恭喜发财，好运连连，uid-红包发送给谁
     */
    public Observable<BaseResponse<UrlBean>> sendRedEnvelopeToUser(String actionId, long amt, int count, int type, String note, long uid) {
        Map<String, String> map = new HashMap<>();
        map.put("actionId", actionId);
        map.put("amt", amt + "");
        map.put("cnt", count + "");
        map.put("note", note);
        map.put("type", type + "");
        map.put("toUid", uid + "");
        return HttpChannel.getInstance().getPayService().sendRedEnvelope(getRequestBody(map), getAuthMap());
    }

    /**
     * 发送红包给群:
     * amt——发送金额，单位：分；count——发送个数；payPwd——支付密码；type——红包类型，拼手气1或者普通红包0，
     * bankCardId——当发送金额大于零钱余额，必填；note——恭喜发财，好运连连，uid-红包发送给谁
     */
    public Observable<BaseResponse<UrlBean>> sendRedEnvelopeToGroup(String actionId, long amt, int count, int type, String note, String gid, JSONArray allowUids) {
        try {
            JSONObject object = new JSONObject();
            object.put("actionId", actionId);
            object.put("amt", amt + "");
            object.put("cnt", count + "");
            object.put("note", note);
            object.put("type", type + "");
            object.put("toGid", gid);
            if (allowUids != null && allowUids.length() > 0) {
                object.put("allowUids", allowUids);
            }
            return HttpChannel.getInstance().getPayService().sendRedEnvelope(getRequestBody(object.toString()), getAuthMap());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 抢红包
     *
     * @param rid 红包id 及 tradeId
     */
    public Observable<BaseResponse<GrabEnvelopeBean>> grabRedEnvelope(long rid, String from) {
        Map<String, String> map = new HashMap<>();
        map.put("rid", rid + "");
        map.put("from", from);
        return HttpChannel.getInstance().getPayService().grabRedEnvelope(getRequestBody(map), getAuthMap());
    }

    /**
     * 拆红包
     *
     * @param rid 红包id 及 tradeId
     */
    public Observable<BaseResponse<OpenEnvelopeBean>> openRedEnvelope(long rid, String token) {
        Map<String, String> map = new HashMap<>();
        map.put("rid", rid + "");
        if (!TextUtils.isEmpty(token)) {
            map.put("accessToken", token);
        }
        return HttpChannel.getInstance().getPayService().openRedEnvelope(getRequestBody(map), getAuthMap());
    }

    /**
     * 查看红包记录
     *
     * @param rid      红包id 及 tradeId
     * @param fromType 1表示来源于零钱助手，0 则表示从红包
     */
    public Observable<BaseResponse<EnvelopeDetailBean>> getEnvelopeDetail(long rid, String token, int fromType) {
        Map<String, String> map = new HashMap<>();
        map.put("rid", rid + "");
        if (!TextUtils.isEmpty(token)) {
            map.put("accessToken", token);
        }
        if (fromType == 1) {
            map.put("src", 1 + "");
        }
        return HttpChannel.getInstance().getPayService().getEnvelopeDetail(getRequestBody(map), getAuthMap());
    }


    /**
     * 发送转账
     *
     * @param toUid 转账接受者id
     */
    public Observable<BaseResponse<UrlBean>> sendTransfer(String actionId, long money, long toUid, String note) {
        Map<String, String> map = new HashMap<>();
        map.put("actionId", actionId);
        map.put("amt", money + "");
        map.put("toUid", toUid + "");
        if (!TextUtils.isEmpty(note)) {
            map.put("note", note);
        }
        //        map.put("payPwd", MD5.md5(psw));
//        if (bankCardId > 0) {
//            map.put("bankCardId", bankCardId + "");
//        }
        return HttpChannel.getInstance().getPayService().sendTransfer(getRequestBody(map), getAuthMap());
    }


    /**
     * 发送转账
     *
     * @param tradeId 转账接受者id
     */
    public Observable<BaseResponse<TransferDetailBean>> getTransferDetail(String tradeId) {
        Map<String, String> map = new HashMap<>();
        map.put("tradeId", tradeId);
        return HttpChannel.getInstance().getPayService().getTransferDetail(getRequestBody(map), getAuthMap());
    }

    /**
     * 领取转账
     *
     * @param fromUid 转账发送者id
     */
    public Observable<BaseResponse<TransferResultBean>> receiveTransfer(String actionId, String tradeId, long fromUid) {
        Map<String, String> map = new HashMap<>();
        map.put("actionId", actionId);
        map.put("tradeId", tradeId);
        map.put("fromUid", fromUid + "");
        return HttpChannel.getInstance().getPayService().receiveTransfer(getRequestBody(map), getAuthMap());
    }

    /**
     * 拒收转账
     *
     * @param fromUid 转账发送者id
     */
    public Observable<BaseResponse<TransferResultBean>> returnTransfer(String actionId, String tradeId, long fromUid) {
        Map<String, String> map = new HashMap<>();
        map.put("actionId", actionId);
        map.put("tradeId", tradeId);
        map.put("fromUid", fromUid + "");
        return HttpChannel.getInstance().getPayService().returnTransfer(getRequestBody(map), getAuthMap());
    }

    //商城->获取免登陆商城URL
    public Observable<BaseResponse> getShopUrl() {
        if (AppConfig.DEBUG) {
            return HttpChannel.getInstance().getPayService().getShopUrlDebug(getAuthMap());
        } else {
            return HttpChannel.getInstance().getPayService().getShopUrlRelease(getAuthMap());
        }
    }


    //商城->消费
    public Observable<BaseResponse<CommonBean>> shopConsumption(String amt) {
        Map<String, String> map = new HashMap<>();
        map.put("amt", amt);
        return HttpChannel.getInstance().getPayService().shopConsumption(getRequestBody(map), getAuthMap());
    }

    /**
     * 获取密码管理URL
     */
    public Observable<BaseResponse<UrlBean>> getPswManager() {
        return HttpChannel.getInstance().getPayService().getPasswordManager(getAuthMap());
    }

    //获取用户信息
    public Observable<BaseResponse<String>> getHisUserInfo(long uid) {
        Map<String, String> map = new HashMap<>();
        map.put("recvUserId", uid + "");
        return HttpChannel.getInstance().getPayService().getHisInfo(getRequestBody(map), getAuthMap());
    }

}
