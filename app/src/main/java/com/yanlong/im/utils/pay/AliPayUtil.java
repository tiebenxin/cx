package com.yanlong.im.utils.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

public class AliPayUtil {
    private static final String APPID = "";
    private static final String RSA2_PRIVATE = "";

    /***
     * 从支付宝冲钱红包
     */
    public boolean redEnvelopePay(Activity activity, String amout, String msg) {


        Map<String, String> params = OrderInfoUtil2_0.buildRedParamMap(APPID, amout, msg, true);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = RSA2_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, true);

        //orderInfo来自服务端
        String orderInfo = orderParam + "&" + sign;


        PayTask alipay = new PayTask(activity);



        PayResult payResult = new PayResult(alipay.payV2(orderInfo, true));
        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
        String resultStatus = payResult.getResultStatus();
        // 判断resultStatus 为9000则代表支付成功
        if (TextUtils.equals(resultStatus, "9000")) {
            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。

            return true;
        } else {
            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
            return false;
        }

    }



    public void h5Pay(Activity activity,String url) {
        WebView.setWebContentsDebuggingEnabled(true);
        Intent intent = new Intent(activity, H5PayActivity.class);
        Bundle extras = new Bundle();

        /*
         * URL 是要测试的网站，在 Demo App 中会使用 H5PayActivity 内的 WebView 打开。
         *
         * 可以填写任一支持支付宝支付的网站（如淘宝或一号店），在网站中下订单并唤起支付宝；
         * 或者直接填写由支付宝文档提供的“网站 Demo”生成的订单地址
         * （如 https://mclient.alipay.com/h5Continue.htm?h5_route_token=303ff0894cd4dccf591b089761dexxxx）
         * 进行测试。
         *
         * H5PayActivity 中的 MyWebViewClient.shouldOverrideUrlLoading() 实现了拦截 URL 唤起支付宝，
         * 可以参考它实现自定义的 URL 拦截逻辑。
         */

        extras.putString("url", url);
        intent.putExtras(extras);
        activity.startActivity(intent);
    }


    /***
     * 领取红包
     */
    public void redEnvelopeGet() {

    }


}
