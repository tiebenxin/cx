package com.hm.cxpay.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.hm.cxpay.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

/**
 * @类名：零钱->支付密码管理
 * @Date：2019/12/2
 * @by zjy
 * @备注：
 */
public class ManagePaywordActivity extends AppActivity {

    private HeadView headView;
    private ActionbarView actionbar;
    private LinearLayout layoutSetPayword;//设置支付密码
    private LinearLayout layoutModifyPayword;//修改支付密码
    private Context activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_payword);
        activity = this;
        initView();
        initData();
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        layoutSetPayword = findViewById(R.id.layout_set_payword);
        layoutModifyPayword = findViewById(R.id.layout_modify_payword);
        actionbar = headView.getActionbar();
    }

    private void initData() {
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        layoutSetPayword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go(SetPaywordActivity.class);
            }
        });
        layoutModifyPayword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 修改支付密码UI还没出
                go(SetPaywordActivity.class);
            }
        });

    }


}
