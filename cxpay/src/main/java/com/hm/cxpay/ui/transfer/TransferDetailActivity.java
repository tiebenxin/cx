package com.hm.cxpay.ui.transfer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.dailog.DialogDefault;
import com.hm.cxpay.databinding.ActivityReceiveTransferBinding;
import com.hm.cxpay.databinding.ActivityTransferDetailBinding;

/**
 * @author Liszt
 * @date 2019/12/19
 * Description 转账详情
 */
public class TransferDetailActivity extends BasePayActivity {

    private ActivityTransferDetailBinding ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_transfer_detail);
        initView();
    }

    private void initView() {

    }

}
