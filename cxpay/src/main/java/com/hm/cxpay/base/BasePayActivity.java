package com.hm.cxpay.base;

import android.support.v7.widget.Toolbar;

import net.cb.cb.library.view.AppActivity;

/**
 * @anthor Liszt
 * @data 2019/11/27
 * Description
 */
public class BasePayActivity extends AppActivity {


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
}
