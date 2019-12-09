package com.hm.cxpay.ui;

import android.content.Context;
import android.os.Bundle;

import com.hm.cxpay.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

/**
 * @类名：账单明细
 * @Date：2019/12/9
 * @by zjy
 * @备注：
 */

public class BillDetailListActivity extends AppActivity {

    private HeadView headView;
    private ActionbarView actionbar;
    private Context activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_bill_list);
        initView();
        initData();
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

    }

    private void initData() {

    }

}
