package com.hm.cxpay.ui.bank;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.databinding.ActivityIdentificationCentreBinding;
import com.hm.cxpay.databinding.ActivityInputPhoneBinding;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;


/**
 * 添加需要绑定银行卡的预留手机号
 */
public class InputPhoneActivity extends BasePayActivity {

    private ActivityInputPhoneBinding ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_input_phone);
        initView();
        initEvent();
    }

    private void initView() {
        ui.tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idCard = ui.etIdcard.getText().toString().trim();
                String name = ui.etName.getText().toString().trim();
                if (TextUtils.isEmpty(idCard)) {
                    ToastUtil.show(InputPhoneActivity.this, "身份证号码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    ToastUtil.show(InputPhoneActivity.this, "真实姓名不能为空");
                    return;
                }
                //TODO:检测身份证号码是否正确
                authUser(idCard, name);
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

    public void authUser(String idNum, String realName) {
        ui.tvNext.setEnabled(false);
        PayHttpUtils.getInstance().authUserInfo(idNum, realName)
                .compose(RxSchedulers.<BaseResponse>compose())
                .compose(RxSchedulers.<BaseResponse>handleResult())
                .subscribe(new FGObserver<BaseResponse>() {
                    @Override
                    public void onHandleSuccess(BaseResponse baseResponse) {
                        if (baseResponse.isSuccess()) {
                            ToastUtil.show(InputPhoneActivity.this, "认证成功");
                        } else {
                            ui.tvNext.setEnabled(true);
                            ToastUtil.show(InputPhoneActivity.this, baseResponse.getMessage());
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        super.onHandleError(baseResponse);
                        ui.tvNext.setEnabled(true);
                    }
                });
    }


}


























