package com.hm.cxpay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.controller.ControllerPaySetting;
import com.hm.cxpay.ui.bank.BankSettingActivity;

import net.cb.cb.library.utils.IntentUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.HeadView;

/*
 * 零钱首页
 * */
public class LooseChangeActivity extends BasePayActivity {

    private ControllerPaySetting viewSettingOfPsw;
    private ControllerPaySetting viewRecordOfTransaction;
    private ControllerPaySetting viewMyCard;
    private ControllerPaySetting viewMyRedEnvelope;

    private HeadView mHeadView;
    private TextView tvMoney;//余额
    private Button btnRecharge;//充值
    private Button btnWithdrawDeposit;//提现

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loose_change);
        initView();
        initEvent();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        tvMoney = findViewById(R.id.tv_money);
        btnRecharge = findViewById(R.id.btn_recharge);
        btnWithdrawDeposit = findViewById(R.id.btn_withdraw_deposit);
    }


    private void initEvent() {
        //标题栏事件
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        //充值
        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LooseChangeActivity.this,RechargeActivity.class)
                        .putExtra("balance",1));
            }
        });
        //提现
        btnWithdrawDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //红包明细
        viewMyRedEnvelope = new ControllerPaySetting(findViewById(R.id.viewMyRedEnvelope));
        viewMyRedEnvelope.init(R.mipmap.ic_red_packet_info, R.string.my_red_envelope, "");
        viewMyRedEnvelope.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {

            }
        });
        //账户信息
        ControllerPaySetting viewAccountInfo = new ControllerPaySetting(findViewById(R.id.viewAccountInfo));
        viewAccountInfo.init(R.mipmap.ic_account_info, R.string.account_info, "");
        viewAccountInfo.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {
                IntentUtil.gotoActivity(LooseChangeActivity.this, IdentificationInfoActivity.class);
            }
        });
        //我的银行卡
        viewMyCard = new ControllerPaySetting(findViewById(R.id.viewBankSetting));
        int count = 0;
        viewMyCard.init(R.mipmap.ic_bank_card, R.string.settings_of_bank, count + "张");
        viewMyCard.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {
                IntentUtil.gotoActivity(LooseChangeActivity.this, BankSettingActivity.class);
            }
        });
        //支付密码管理
        viewSettingOfPsw = new ControllerPaySetting(findViewById(R.id.viewSettingOfPsw));
        viewSettingOfPsw.init(R.mipmap.ic_paypsw_manage, R.string.settings_of_psw, "");
        viewSettingOfPsw.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {

            }
        });
    }

}
