package com.yanlong.im.user.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.jrmf360.walletlib.JrmfWalletClient;
import com.umeng.socialize.utils.SocializeSpUtils;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityServiceAgreementBinding;
import com.yanlong.im.pay.action.PayAction;
import com.yanlong.im.pay.bean.SignatureBean;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.SpUtil;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import retrofit2.Call;
import retrofit2.Response;

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

    private PayAction payAction = new PayAction();
    private ActivityServiceAgreementBinding mBinding;
    private String mValue="尊敬的常聊聊用户，为了更好地保障您的合法权益，正常使用云红包服务，广州之讯网络科技有限公司依照国家法律法规，对支付账户进行实名管理、履行反洗钱职责并采取风险防控措施。您需要提交身份信息、联系方式、交易信息。\n\n" +
            "广州之讯网络科技有限公司将严格按照国家法律法规收集、存储、使用您的个人信息，确保信息安全。\n\n" +
            "        请您务必阅读并充分理解<font color='#374882'>《用户服务协议》</font>和<font color='#374882'>《隐私政策》</font>，若你同意接受前述协议，请点击“同意”并继续注册操作。点击“不同意”终止注册操作。";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_service_agreement);
        mBinding.txtContent.setText(Html.fromHtml(mValue));
        onEvent();
    }

    protected void onEvent(){
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
                finish();
                taskWallet();
            }
        });
        mBinding.txtNoAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 钱包
     */
    private void taskWallet() {
        UserInfo info = UserAction.getMyInfo();
        if (info != null && info.getLockCloudRedEnvelope() == 1) {//红包功能被锁定
            ToastUtil.show(this, "您的云红包功能已暂停使用，如有疑问请咨询官方客服号");
            return;
        }
        payAction.SignatureBean(new CallBack<ReturnBean<SignatureBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SignatureBean>> call, Response<ReturnBean<SignatureBean>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    // 记录第一次
                    SpUtil spUtil= SpUtil.getSpUtil();
                    spUtil.putSPValue("ServieAgreement","true");

                    String token = response.body().getData().getSign();
                    UserInfo minfo = UserAction.getMyInfo();
                    JrmfWalletClient.intentWallet(ServiceAgreementActivity.this, "" + UserAction.getMyId(), token, minfo.getName(), minfo.getHead());
                }
            }
        });
    }
}
