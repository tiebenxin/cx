package com.hm.cxpay.base;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.hm.cxpay.dailog.DialogLoadingProgress;

import net.cb.cb.library.view.AlertWait;
import net.cb.cb.library.view.AppActivity;

/**
 * @author Liszt
 * @date 2019/11/27
 * Description
 */
public class BasePayActivity extends AppActivity {
    DialogLoadingProgress payWaitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void showLoadingDialog() {
        if (payWaitDialog == null) {
            payWaitDialog = new DialogLoadingProgress(this);
        }
        payWaitDialog.show();
    }

    public void dismissLoadingDialog() {
        if (payWaitDialog != null) {
            payWaitDialog.dismiss();
        }
    }
}
