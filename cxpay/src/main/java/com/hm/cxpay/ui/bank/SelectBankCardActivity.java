package com.hm.cxpay.ui.bank;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.databinding.ActivitySelectBankcardBinding;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;

import net.cb.cb.library.base.AbstractRecyclerAdapter;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;

import java.util.List;


/**
 * @类名：充值->选择银行卡
 * @Date：2019/11/29
 * @by zjy
 * @备注：
 */

public class SelectBankCardActivity extends BasePayActivity {

    private ActivitySelectBankcardBinding ui;
    private AdapterBankList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_select_bankcard);
        adapter = new AdapterBankList(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        ui.rcBankcardList.setLayoutManager(manager);
        ui.rcBankcardList.setAdapter(adapter);
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        getBankList();

        adapter.setItemClickListener(new AbstractRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object bean) {
                if (bean instanceof BankBean) {
                    BankBean bankBean = (BankBean) bean;
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("bank_card", bankBean);
                    Intent intent = new Intent();//携带选中的银行卡数据返回
                    intent.putExtras(bundle);
                    setResult(RESULT_OK,intent);
                    finish();
                }

            }
        });

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

                            ToastUtil.show(SelectBankCardActivity.this, baseResponse.getMessage());
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        super.onHandleError(baseResponse);
                    }
                });
    }
}
