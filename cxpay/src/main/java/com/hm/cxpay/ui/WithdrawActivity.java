package com.hm.cxpay.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hm.cxpay.R;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

/**
 * @类名：零钱->提现
 * @Date：2019/11/30
 * @by zjy
 * @备注：
 */
public class WithdrawActivity extends AppActivity {

    private HeadView headView;
    private ActionbarView actionbar;
    private RelativeLayout layoutChangeBankcard;//切换或新增银行卡
    private EditText etWithdraw;//提现金额输入框
    private TextView tvBalance;//余额
    private TextView tvSubmit;//提现
    private TextView tvAccountTime;//预计x小时后到账
    private TextView tvQuestion;//常见问题
    private Context activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        activity = this;
        initView();
        initData();
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        layoutChangeBankcard = findViewById(R.id.layout_change_bankcard);
        etWithdraw = findViewById(R.id.et_withdraw);
        tvBalance = findViewById(R.id.tv_balance);
        tvSubmit = findViewById(R.id.tv_submit);
        tvAccountTime = findViewById(R.id.tv_account_time);
        tvQuestion = findViewById(R.id.tv_question);
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
        //新增或切换银行卡
        layoutChangeBankcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //提现
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(etWithdraw.getText().toString())){
                    if(Double.valueOf(etWithdraw.getText().toString()) >=10){
                        //TODO 判断是否添加过银行卡
                        boolean ifAddBankcard = false;
                        //1 已经添加过银行卡，点击选择银行卡
                        if(ifAddBankcard){
                            ToastUtil.show(WithdrawActivity.this,"选择银行卡");
                        }else {
                            //2 没有添加过银行卡，则新增一张银行卡
                            ToastUtil.show(WithdrawActivity.this,"新增银行卡");
                        }
                    }else {
                        ToastUtil.show(context,"最小提现金额不低于10元");
                    }
                }else {
                    ToastUtil.show(context,"提现金额不能为空");
                }
            }
        });
        tvAccountTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        tvQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
