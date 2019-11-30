package com.hm.cxpay.ui.bank;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.databinding.ActivityBankDetailBinding;

/**
 * @anthor Liszt
 * @data 2019/11/30
 * Description
 */
public class BankDetailActivity extends BasePayActivity {

    private ActivityBankDetailBinding ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_bank_detail);
        BankBean bank = getIntent().getParcelableExtra("bank");
        if (bank != null) {
            Glide.with(this).load(bank.getLogo()).into(ui.ivIcon);
            ui.tvBankName.setText(bank.getBankName());
            ui.tvBankNum.setText(bank.getCardNo());
            ui.tvBankType.setText("借记卡");
        }


    }
}
