package com.hm.cxpay.base;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.hm.cxpay.R;

import net.cb.cb.library.utils.StatusBarUtils;
import net.cb.cb.library.view.AppActivity;

/**
 * @anthor Liszt
 * @data 2019/11/27
 * Description
 */
public class BasePayActivity extends AppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
