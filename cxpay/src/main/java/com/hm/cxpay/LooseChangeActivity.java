package com.hm.cxpay;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

public class LooseChangeActivity extends AppActivity implements View.OnClickListener {

    private HeadView mHeadView;
    private TextView mTvMoney;
    private Button mBtnVoucher;
    private Button mBtnWithdrawDeposit;
    private LinearLayout mViewLooseChangeInfo;
    private LinearLayout mViewRedPacketInfo;
    private LinearLayout mViewBankCard;
    private TextView mTvBankCardNum;
    private LinearLayout mViewPayManage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loose_change);
        initView();
        initEvent();
    }


    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mTvMoney = findViewById(R.id.tv_money);
        mBtnVoucher = findViewById(R.id.btn_voucher);
        mBtnWithdrawDeposit = findViewById(R.id.btn_withdraw_deposit);
        mViewLooseChangeInfo = findViewById(R.id.view_loose_change_info);
        mViewRedPacketInfo = findViewById(R.id.view_red_packet_info);
        mViewBankCard = findViewById(R.id.view_bank_card);
        mTvBankCardNum = findViewById(R.id.tv_bank_card_num);
        mViewPayManage = findViewById(R.id.view_pay_manage);
    }

    private void initEvent() {
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        mBtnVoucher.setOnClickListener(this);
        mBtnWithdrawDeposit.setOnClickListener(this);
        mViewLooseChangeInfo.setOnClickListener(this);
        mViewRedPacketInfo.setOnClickListener(this);
        mViewBankCard.setOnClickListener(this);
        mViewPayManage.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_voucher) {
        } else if (id == R.id.btn_withdraw_deposit) {
        } else if (id == R.id.view_loose_change_info) {
        } else if (id == R.id.view_red_packet_info) {
        } else if (id == R.id.view_bank_card) {
        } else if (id == R.id.view_pay_manage) {
        }
    }
}
