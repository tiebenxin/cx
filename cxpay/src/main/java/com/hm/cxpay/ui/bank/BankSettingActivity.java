package com.hm.cxpay.ui.bank;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.databinding.ActivityBankSettingBinding;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;

import net.cb.cb.library.base.AbstractRecyclerAdapter;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;

import java.util.List;

import static com.hm.cxpay.ui.LooseChangeActivity.REFRESH_BANKCARD_NUM;

/**
 * @anthor Liszt
 * @data 2019/11/30
 * Description
 */
public class BankSettingActivity extends BasePayActivity {

    public static final int REQUEST_BIND = 1;

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
//                IntentUtil.gotoActivity(BankSettingActivity.this, BindBankActivity.class);
                Intent intent = new Intent(BankSettingActivity.this, BindBankActivity.class);
                startActivityForResult(intent, REQUEST_BIND);
            }
        });

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
                    Intent intent = new Intent(BankSettingActivity.this, BankDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("bank", bankBean);
                    intent.putExtras(bundle);
                    startActivity(intent);

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

                            ToastUtil.show(BankSettingActivity.this, baseResponse.getMessage());
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        super.onHandleError(baseResponse);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BIND) {
            if (resultCode == RESULT_OK) {
                getBankList();//重新获取银行列表
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isFinishing()){
            setResult(REFRESH_BANKCARD_NUM);
        }
    }
}
