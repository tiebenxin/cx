package com.hm.cxpay.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.controller.ControllerPaySetting;
import com.hm.cxpay.databinding.ActivityLooseChangeBinding;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;

import net.cb.cb.library.utils.IntentUtil;
import net.cb.cb.library.view.AppActivity;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/*
 * 零钱首页
 * */
public class LooseChangeActivity extends BasePayActivity {

    private ActivityLooseChangeBinding ui;
    private ControllerPaySetting viewSettingOfPsw;
    private ControllerPaySetting viewRecordOfTransaction;
    private ControllerPaySetting viewMyCard;
    private ControllerPaySetting viewMyRedEnvelope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_loose_change);
        setContentView(R.layout.activity_loose_change);
        initToolBar(ui.viewTitleBar, true);
        ui.viewTitleBar.setTitleText("零钱");
        initView();
        initEvent();
    }

    private void initView() {
        //红包明细
        viewMyRedEnvelope = new ControllerPaySetting(findViewById(R.id.viewMyRedEnvelope));
        viewMyRedEnvelope.init(R.mipmap.ic_red_packet_info, R.string.my_red_envelope, "");
        viewMyRedEnvelope.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {

            }
        });
        //账户信息 icon?
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

            }
        });
        //交易记录
        viewRecordOfTransaction = new ControllerPaySetting(findViewById(R.id.viewRecordOfTransaction));
        viewRecordOfTransaction.init(R.mipmap.ic_pay_record, R.string.record_of_transaction, "");
        viewRecordOfTransaction.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {

            }
        });
        //支付密码管理 icon?
        viewSettingOfPsw = new ControllerPaySetting(findViewById(R.id.viewSettingOfPsw));
        viewSettingOfPsw.init(R.mipmap.ic_psw_manage, R.string.settings_of_psw, "");
        viewSettingOfPsw.setOnClickListener(new ControllerPaySetting.OnControllerClickListener() {
            @Override
            public void onClick() {

            }
        });


    }


    private void initEvent() {
        //充值
        ui.btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //提现
        ui.btnWithdrawDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
