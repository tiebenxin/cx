package com.hm.cxpay.ui.redenvelope;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.bean.CxEnvelopeBean;
import com.hm.cxpay.bean.FromUserBean;
import com.hm.cxpay.bean.SendResultBean;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.dailog.DialogDefault;
import com.hm.cxpay.global.PayEnum;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.umeng.commonsdk.debug.E;

import net.cb.cb.library.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author Liszt
 * @date 2019/12/4
 * Description
 */
public class BaseSendRedEnvelopeActivity extends BasePayActivity {
    boolean isSending;
    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler();
    public final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ToastUtil.show(getContext(), "红包发送失败");
            dismissLoadingDialog();
            setResult(RESULT_CANCELED);
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        httpGetUserInfo();//更新余额
        PayEnvironment.getInstance().notifyStampUpdate(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyword();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        PayEnvironment.getInstance().notifyStampUpdate(true);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (isSending) {
                showBackNoticeDialog();
            } else {
                finish();
            }
            return true;
        }
        //继续执行父类其他点击事件
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 请求->获取用户信息
     */
    public void httpGetUserInfo() {
        if (PayEnvironment.getInstance().getUser() == null) {
            return;
        }
        PayHttpUtils.getInstance().getUserInfo(PayEnvironment.getInstance().getUser().getUid())
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


    public CxEnvelopeBean initEnvelopeBean(CxEnvelopeBean envelopeBean, String actionId, long tradeId, long createTime, @PayEnum.ERedEnvelopeType int redType, String info, int count, String sign, List<FromUserBean> allowUsers) {
        if (envelopeBean == null && !TextUtils.isEmpty(actionId)) {
            envelopeBean = new CxEnvelopeBean();
            envelopeBean.setActionId(actionId);
            envelopeBean.setEnvelopeType(redType);
            envelopeBean.setCreateTime(createTime);
            envelopeBean.setMessage(info);
            envelopeBean.setEnvelopeAmount(count);
            if (tradeId > 0) {
                envelopeBean.setTradeId(tradeId);
            }
            if (!TextUtils.isEmpty(sign)) {
                envelopeBean.setSign(sign);
            }
        } else {
            envelopeBean.setTradeId(tradeId);
            envelopeBean.setActionId(actionId);
            envelopeBean.setSign(sign);
        }
        if (allowUsers != null && allowUsers.size() > 0) {
            envelopeBean.setAllowUses(allowUsers);
        }
        return envelopeBean;
    }

    //是否正在发送红包
    public boolean isSending() {
        return isSending;
    }

    public void setSending(boolean b) {
        isSending = b;
    }

    public void showBackNoticeDialog() {
        final DialogDefault dialogBack = new DialogDefault(this);
        dialogBack.setTitleAndSure(false, true)
                .setContent("红包正在发送中，此时返回，将导致红包发送失败。如已扣款，将会在24小时内自动退回你的零钱账户", true)
                .setLeft("取消").setRight("继续返回")
                .setListener(new DialogDefault.IDialogListener() {
                    @Override
                    public void onSure() {
                        setResult(RESULT_CANCELED);
                        finish();
                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();
    }
}
