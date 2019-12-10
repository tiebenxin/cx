package com.hm.cxpay.ui.redenvelope;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.bean.CxEnvelopeBean;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.global.PayEnum;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;

import net.cb.cb.library.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * @anthor Liszt
 * @data 2019/12/4
 * Description
 */
public class BaseSendRedEnvelopeActivity extends BasePayActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        httpGetUserInfo();//更新余额
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 请求->获取用户信息
     */
    public void httpGetUserInfo() {
        PayHttpUtils.getInstance().getUserInfo()
                .compose(RxSchedulers.<BaseResponse<UserBean>>compose())
                .compose(RxSchedulers.<BaseResponse<UserBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<UserBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<UserBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            UserBean userBean = null;
                            if (baseResponse.getData() != null) {
                                userBean = baseResponse.getData();
                            } else {
                                userBean = new UserBean();
                            }
                            PayEnvironment.getInstance().setUser(userBean);
                        } else {
                            ToastUtil.show(context, baseResponse.getMessage());
                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse<UserBean> baseResponse) {
                        super.onHandleError(baseResponse);
                        ToastUtil.show(context, baseResponse.getMessage());
                    }
                });
    }

    public void showSoftKeyword(final View view) {
        if (view == null) {
            return;
        }
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(view, 0);
                }
            }
        }, 100);

    }

    public void hideSoftKeyword() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (v != null && imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        }
    }


    public CxEnvelopeBean convertToEnvelopeBean(SendResultBean bean, @PayEnum.ERedEnvelopeType int redType, String info, int count) {
        CxEnvelopeBean envelopeBean = new CxEnvelopeBean();
        envelopeBean.setActionId(bean.getActionId());
        envelopeBean.setTradeId(bean.getTradeId());
        envelopeBean.setCreateTime(bean.getCreateTime());
        envelopeBean.setMessage(info);
        envelopeBean.setEnvelopeType(redType);
        envelopeBean.setEnvelopeAmount(count);
        return envelopeBean;
    }
}
