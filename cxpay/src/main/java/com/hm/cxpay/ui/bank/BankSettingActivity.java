package com.hm.cxpay.ui.bank;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.databinding.ActivityBankSettingBinding;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;

import net.cb.cb.library.utils.IntentUtil;
import net.cb.cb.library.utils.ToastUtil;

import java.util.List;

/**
 * @anthor Liszt
 * @data 2019/11/30
 * Description
 */
public class BankSettingActivity extends BasePayActivity {

    private ActivityBankSettingBinding ui;
    private AdapterBankList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_bank_setting);
        adapter = new AdapterBankList(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        ui.recyclerView.setLayoutManager(manager);
        ui.recyclerView.setAdapter(adapter);
        ui.llAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.gotoActivity(BankSettingActivity.this,BindBankActivity.class);
            }
        });

        getBankList();

    }

    private void getBankList() {
        PayHttpUtils.getInstance().getBankList()
                .compose(RxSchedulers.<BaseResponse<List<BankBean>>>compose())
                .compose(RxSchedulers.<BaseResponse<List<BankBean>>>handleResult())
                .subscribe(new FGObserver<BaseResponse<List<BankBean>>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<List<BankBean>> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            List<BankBean> info = baseResponse.getData();
                            if (info != null) {
                                adapter.bindData(info);
                            }

                        } else {

                            ToastUtil.show(BankSettingActivity.this, baseResponse.getMessage());
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        super.onHandleError(baseResponse);
                    }
                });
    }
}
