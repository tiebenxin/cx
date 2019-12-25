package com.hm.cxpay.ui.transfer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.dailog.DialogDefault;
import com.hm.cxpay.databinding.ActivityReceiveTransferBinding;

/**
 * @author Liszt
 * @date 2019/12/19
 * Description 领取转账
 */
public class ReceiveTransferActivity extends BasePayActivity {

    private ActivityReceiveTransferBinding ui;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_receive_transfer);
    }


    //退还确定弹窗
    public void showReturnDialog() {
        DialogDefault dialogReturn = new DialogDefault(this);
        dialogReturn.setTitleAndSure(false, true)
                .setRight("退还")
                .setLeft("取消")
                .setListener(new DialogDefault.IDialogListener() {
                    @Override
                    public void onSure() {

                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();
    }
}
