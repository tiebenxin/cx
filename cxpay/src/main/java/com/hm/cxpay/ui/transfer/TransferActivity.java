package com.hm.cxpay.ui.transfer;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.bean.CxTransferBean;
import com.hm.cxpay.bean.UrlBean;
import com.hm.cxpay.databinding.ActivityTransferBinding;
import com.hm.cxpay.eventbus.PayResultEvent;
import com.hm.cxpay.eventbus.TransferSuccessEvent;
import com.hm.cxpay.global.PayEnum;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.YiBaoWebActivity;
import com.hm.cxpay.utils.UIUtils;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NumRangeInputFilter;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.hm.cxpay.global.PayConstants.MIN_AMOUNT;
import static com.hm.cxpay.global.PayConstants.REQUEST_PAY;
import static com.hm.cxpay.global.PayConstants.RESULT;
import static com.hm.cxpay.global.PayConstants.TOTAL_TRANSFER_MAX_AMOUNT;
import static com.hm.cxpay.global.PayConstants.WAIT_TIME;

/**
 * @author Liszt
 * @date 2019/12/19
 * Description 转账界面
 */
public class TransferActivity extends BasePayActivity {

    private ActivityTransferBinding ui;
    private long toUid;
    private String name;
    private long money;
    private String avatar;
    boolean isSending = false;
    public final Handler handler = new Handler();
    public final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ToastUtil.show(getContext(), "转账失败");
            dismissLoadingDialog();
            finish();
        }
    };
    private CxTransferBean cxTransferBean;
    private String note;
    private String actionId;


    public static Intent newIntent(Context context, long uid, String name, String avatar) {
        Intent intent = new Intent(context, TransferActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("name", name);
        intent.putExtra("avatar", avatar);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_transfer);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        Intent intent = getIntent();
        toUid = intent.getLongExtra("uid", 0);
        name = intent.getStringExtra("name");
        avatar = intent.getStringExtra("avatar");
        getHisInfo(toUid);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventPayResult(PayResultEvent event) {
        payFailed();
        cxTransferBean = createTransferBean(actionId, money, PayEnum.ETransferOpType.TRANS_SEND, note, event.getTradeId(), event.getSign());
        if (cxTransferBean != null && !TextUtils.isEmpty(cxTransferBean.getActionId()) && !TextUtils.isEmpty(event.getActionId())
                && cxTransferBean.getActionId().equals(event.getActionId())) {
            if (event.getResult() == PayEnum.EPayResult.SUCCESS) {
                eventTransferSuccess();
                toTransferResult(money);
            } else if (event.getResult() == PayEnum.EPayResult.FAIL) {
//                if (!TextUtils.isEmpty(event.getErrMsg())) {
//                    ToastUtil.show(this, event.getErrMsg());
//                } else {
//                    ToastUtil.show(this, R.string.transfer_fail_note);
//                }
            } else {
                ToastUtil.show(this, R.string.transfer_fail_note);
            }
        }
    }

    private void initView() {
        UIUtils.loadAvatar(avatar, ui.ivAvatar);
//        ui.headView.getActionbar().setChangeStyleBg();
//        ui.headView.getAppBarLayout().setBackgroundResource(R.color.c_c85749);
        ui.tvName.setText(name);
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
            }
        });
        ui.tvTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String moneyTxt = ui.edMoney.getText().toString();
                money = UIUtils.getFen(moneyTxt);
                if (money >= MIN_AMOUNT) {
                    note = ui.etDescription.getText().toString().trim();
                    actionId = UIUtils.getUUID();
                    httpSendTransfer(actionId, money, toUid, note);
                } else {
                    ToastUtil.show(getString(R.string.transfer_min_amount_notice));
                }
            }
        });
        ui.edMoney.setFilters(new InputFilter[]{new NumRangeInputFilter(this, Integer.MAX_VALUE)});
        ui.edMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString().trim();
                long money = UIUtils.getFen(string);
                updateUI(money);
            }
        });
    }

    private void updateUI(long money) {
        if (money < MIN_AMOUNT) {
            ui.tvTransfer.setEnabled(false);
            ui.tvNotice.setVisibility(View.VISIBLE);
            ui.tvNotice.setText(getString(R.string.transfer_min_amount_notice));
        } else if (money > TOTAL_TRANSFER_MAX_AMOUNT) {
            ui.tvTransfer.setEnabled(false);
            ui.tvNotice.setVisibility(View.VISIBLE);
            ui.tvNotice.setText(getString(R.string.total_transfer_max_amount_notice));
        } else {
            ui.tvTransfer.setEnabled(true);
            ui.tvNotice.setVisibility(View.GONE);
        }
    }


    public void httpSendTransfer(final String actionId, final long money, final long toUid, final String note) {
        isSending = true;
        showLoadingDialog();
        cxTransferBean = createTransferBean(actionId, money, PayEnum.ETransferOpType.TRANS_SEND, note, -1, "");
        PayHttpUtils.getInstance().sendTransfer(actionId, money, toUid, note)
                .compose(RxSchedulers.<BaseResponse<UrlBean>>compose())
                .compose(RxSchedulers.<BaseResponse<UrlBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<UrlBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<UrlBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            LogUtil.writeLog("支付--转账--actionId=" + actionId + "--time" + System.currentTimeMillis());
                            UrlBean urlBean = baseResponse.getData();
                            if (urlBean != null) {
                                Intent intent = new Intent(TransferActivity.this, YiBaoWebActivity.class);
                                intent.putExtra(YiBaoWebActivity.AGM_URL, urlBean.getUrl());
                                startActivityForResult(intent, REQUEST_PAY);
                            }
                        } else {
                            isSending = false;
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        payFailed();
                        ToastUtil.show(getContext(), baseResponse.getMessage());
                    }
                });
    }

    private void toTransferResult(long money) {
        Intent intent = TransferResultActivity.newIntent(TransferActivity.this, money, 1, name);
        startActivity(intent);
        finish();
    }

    private void payFailed() {
        dismissLoadingDialog();
        if (isSending) {
            isSending = false;
            if (handler != null && runnable != null) {
                handler.removeCallbacks(runnable);
            }
        }
    }

    public CxTransferBean createTransferBean(String actionId, long money, @PayEnum.ETransferOpType int type, String info, long tradeId, String sign) {
        CxTransferBean transferBean = new CxTransferBean();
        transferBean.setActionId(actionId);
        transferBean.setUid(toUid);
        transferBean.setAmount(money);
        transferBean.setOpType(type);
        transferBean.setInfo(info);
        if (!TextUtils.isEmpty(sign)) {
            transferBean.setSign(sign);
        }
        if (tradeId > 0) {
            transferBean.setTradeId(tradeId);
        }
        return transferBean;
    }

    public void eventTransferSuccess() {
        if (cxTransferBean != null) {
            EventBus.getDefault().post(new TransferSuccessEvent(cxTransferBean));
        }
        PayEnvironment.getInstance().notifyRefreshBalance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PAY) {
            if (resultCode == RESULT_OK) {
                int result = data.getIntExtra(RESULT, 0);
                if (result == 99) {
                    showLoadingDialog();
                    if (handler != null) {
                        handler.postDelayed(runnable, WAIT_TIME);
                    }
                } else {
                    dismissLoadingDialog();
                }
            } else {
                dismissLoadingDialog();
            }
        }
    }

    private void getHisInfo(long uid) {
        PayHttpUtils.getInstance().getHisUserInfo(uid)
                .compose(RxSchedulers.<BaseResponse<String>>compose())
                .compose(RxSchedulers.<BaseResponse<String>>handleResult())
                .subscribe(new FGObserver<BaseResponse<String>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<String> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            if (baseResponse.getData() != null) {
                                String realName = baseResponse.getData();
                                setName(name, realName);
                            }
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse<String> baseResponse) {
                    }
                });
    }

    private void setName(String nick, String realName) {
        if (!TextUtils.isEmpty(nick)) {
            String name = nick;
            if (!TextUtils.isEmpty(realName)) {
                name = nick + "(" + realName + ")";
            }
            ui.tvName.setText(name);
        }
    }
}
