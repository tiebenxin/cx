package com.hm.cxpay.base;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.hm.cxpay.R;
import com.hm.cxpay.eventbus.PayResultEvent;

import net.cb.cb.library.utils.StatusBarUtils;
import net.cb.cb.library.view.AlertWait;
import net.cb.cb.library.view.AppActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @anthor Liszt
 * @data 2019/11/27
 * Description
 */
public class BasePayActivity extends AppActivity {
    AlertWait payWaitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        payWaitDialog = new AlertWait(this);

//        StatusBarUtils.with(this)
//                .setIsActionBar(false)
//                .clearActionBarShadow()
//                .setDrawable(getResources().getDrawable(R.drawable.bg_action))
//                .init();
    }

    /*
     * 初始化toolbar
     * @param isHasBack 是否有返回键
     * */
    public void initToolBar(Toolbar bar, boolean isHasBack) {
        if (getSupportActionBar() == null) {
            setSupportActionBar(bar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(isHasBack);
    }

    public void showWaitDialog() {
        if (payWaitDialog == null) {
            payWaitDialog = new AlertWait(this);
        }
        payWaitDialog.show();
    }

    public void dismissWaitDialog() {
        if (payWaitDialog != null) {
            payWaitDialog.dismiss();
        }
    }
}
