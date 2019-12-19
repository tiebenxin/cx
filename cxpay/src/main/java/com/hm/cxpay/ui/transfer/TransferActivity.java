package com.hm.cxpay.ui.transfer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.databinding.ActivityTransferBinding;

/**
 * @author Liszt
 * @date 2019/12/19
 * Description
 */
public class TransferActivity extends BasePayActivity {

    private ActivityTransferBinding ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_transfer);
    }
}
