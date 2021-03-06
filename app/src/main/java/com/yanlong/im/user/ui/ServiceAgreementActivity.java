package com.yanlong.im.user.ui;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.hm.cxpay.ui.identification.IdentificationUserActivity;
import com.luck.picture.lib.tools.DoubleUtils;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityServiceAgreementBinding;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.WebPageActivity;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2019-10-09
 * @updateAuthor
 * @updateDate
 * @description 云红包 服务协议
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class ServiceAgreementActivity extends AppActivity {
    private ActivityServiceAgreementBinding mBinding;
    private final String USER_SERICE = "《用户服务协议》";
    private final String SERCICE_SERICE = "《隐私政策》";
    private String fromShop = "";

    private String mValue = "        尊敬的常信用户，为了更好地保障您的合法权益，正常使用云红包服务，广州之讯网络科技有限公司依照国家法律法规，对支付账户进行实名管理、履行反洗钱职责并采取风险防控措施。您需要提交身份信息、联系方式、交易信息。\n" +
            "        广州之讯网络科技有限公司将严格按照国家法律法规收集、存储、使用您的个人信息，确保信息安全。\n" +
            "        请您务必阅读并充分理解《用户服务协议》和《隐私政策》，若你同意接受前述协议，请点击“同意”并继续注册操作。点击“不同意”终止注册操作。";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_service_agreement);

        int startIndex = mValue.indexOf(USER_SERICE);
        int endIndex = startIndex + USER_SERICE.length();

        int startIndexs = mValue.indexOf(SERCICE_SERICE);
        int endIndexs = startIndexs + SERCICE_SERICE.length();
        final SpannableStringBuilder style = new SpannableStringBuilder();
        style.append(mValue);
        //设置部分文字点击事件
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (DoubleUtils.isFastDoubleClick()) {//防止快速点击弹出两个界面
                    return;
                }
                Intent intent = new Intent(ServiceAgreementActivity.this, WebPageActivity.class);
                intent.putExtra(WebPageActivity.AGM_URL, AppConfig.USER_AGREEMENT);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@androidx.annotation.NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (DoubleUtils.isFastDoubleClick()) {//防止快速点击弹出两个界面
                    return;
                }
                Intent intent = new Intent(ServiceAgreementActivity.this, WebPageActivity.class);
                intent.putExtra(WebPageActivity.AGM_URL, AppConfig.USER_PRIVACY);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@androidx.annotation.NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        style.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(clickableSpan1, startIndexs, endIndexs, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //设置部分文字颜色
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#374882"));
        ForegroundColorSpan foregroundColorSpan1 = new ForegroundColorSpan(Color.parseColor("#374882"));
        style.setSpan(foregroundColorSpan, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        style.setSpan(foregroundColorSpan1, startIndexs, endIndexs, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        mBinding.txtContent.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.txtContent.setText(style);

        onEvent();
    }

    protected void onEvent() {
        if(getIntent()!=null){
            fromShop = getIntent().getStringExtra("from_shop");
        }
        mBinding.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        mBinding.txtAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                taskWallet();
                startActivity(new Intent(ServiceAgreementActivity.this,IdentificationUserActivity.class).putExtra("from_shop",fromShop));
                finish();
            }
        });
        mBinding.txtNoAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
