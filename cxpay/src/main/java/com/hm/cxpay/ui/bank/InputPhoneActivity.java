package com.hm.cxpay.ui.bank;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.databinding.ActivityInputPhoneBinding;
import com.hm.cxpay.net.Route;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.WebPageActivity;


/**
 * 添加需要绑定银行卡的预留手机号
 */
public class InputPhoneActivity extends BasePayActivity {

    private ActivityInputPhoneBinding ui;
    private BankInfo bankInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_input_phone);
        bankInfo = getIntent().getParcelableExtra("bank");
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ui.tvNext.setEnabled(true);
    }

    private void initView() {
        if (bankInfo != null) {
            ui.tvBankName.setText(bankInfo.getBankName());
            ui.tvBankNum.setText(bankInfo.getBankNumber());
            ui.tvName.setText(bankInfo.getOwnerName());
            ui.tvCardId.setText(bankInfo.getOwnerId());
        }
        ui.ivCheck.setSelected(true);//默认选中
        ui.tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = ui.etPhoneNum.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.show(InputPhoneActivity.this, "手机号不能为空");
                    return;
                }
                if (bankInfo == null || TextUtils.isEmpty(bankInfo.getBankNumber())) {
                    ToastUtil.show(InputPhoneActivity.this, "银行卡号不能为空");
                    return;
                }
                bankInfo.setPhone(phone);
                Intent intent = new Intent(InputPhoneActivity.this, BindBankFinishActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("bank", bankInfo);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        ui.ivCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ui.ivCheck.setSelected(ui.ivCheck.isSelected() ? false : true);
            }
        });

        ui.tvAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputPhoneActivity.this, WebPageActivity.class);
                intent.putExtra(WebPageActivity.AGM_URL, Route.URL_USER_AUTH);
                startActivity(intent);
                ui.tvAgreement.setEnabled(false);
            }
        });

    }


    private void initEvent() {
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

    }


}


























