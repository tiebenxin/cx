package com.hm.cxpay.ui.redenvelope;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hm.cxpay.R;
import com.hm.cxpay.bean.CxEnvelopeBean;
import com.hm.cxpay.bean.UrlBean;
import com.hm.cxpay.dailog.DialogErrorPassword;
import com.hm.cxpay.databinding.ActivitySingleRedPacketBinding;
import com.hm.cxpay.eventbus.PayResultEvent;
import com.hm.cxpay.global.PayEnum;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.YiBaoWebActivity;
import com.hm.cxpay.utils.UIUtils;

import net.cb.cb.library.utils.NumRangeInputFilter;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.PopupSelectView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.hm.cxpay.global.PayConstants.MAX_AMOUNT;
import static com.hm.cxpay.global.PayConstants.REQUEST_PAY;
import static com.hm.cxpay.global.PayConstants.RESULT;
import static com.hm.cxpay.global.PayConstants.WAIT_TIME;

//发送单个红包界面
public class SingleRedPacketActivity extends BaseSendRedEnvelopeActivity {
    private String[] strings = {"红包记录", "取消"};
    private PopupSelectView popupSelectView;
    private ActivitySingleRedPacketBinding ui;
    private long uid;
    private String money;
    private DialogErrorPassword dialogErrorPassword;
    private CxEnvelopeBean envelopeBean;
    private String note;
    private String actionId;


    public static Intent newIntent(Context context, long uid) {
        Intent intent = new Intent(context, SingleRedPacketActivity.class);
        intent.putExtra("uid", uid);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_single_red_packet);
        Intent intent = getIntent();
        uid = intent.getLongExtra("uid", -1);
        initView();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        if (dialogErrorPassword != null) {
            dialogErrorPassword.dismiss();
            dialogErrorPassword = null;
        }
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventPayResult(PayResultEvent event) {
        payFailed();
        envelopeBean = initEnvelopeBean(envelopeBean, actionId, event.getTradeId(), System.currentTimeMillis(), PayEnum.ERedEnvelopeType.NORMAL, note, 1, event.getSign());
        if (envelopeBean != null && !TextUtils.isEmpty(event.getActionId()) && !TextUtils.isEmpty(envelopeBean.getActionId()) && !TextUtils.isEmpty(event.getSign()) && event.getActionId().equals(envelopeBean.getActionId())) {
            if (event.getResult() == PayEnum.EPayResult.SUCCESS) {
                setResultOk();
                PayEnvironment.getInstance().notifyRefreshBalance();
            } else {
                ToastUtil.show(this, R.string.send_fail_note);
            }
        }
    }

    private void payFailed() {
        dismissLoadingDialog();
        if (isSending()) {
            setSending(false);
            if (handler != null && runnable != null) {
                handler.removeCallbacks(runnable);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        ui.headView.setTitle("发送零钱红包");
        ui.headView.getActionbar().setChangeStyleBg();
        ui.headView.getAppBarLayout().setBackgroundResource(R.color.c_c85749);
        ui.headView.getActionbar().setTxtLeft("取消");
        ui.btnCommit.setEnabled(false);//默认不能点击
        ui.headView.getActionbar().getBtnLeft().setVisibility(View.GONE);
        ui.headView.getActionbar().getBtnRight().setImageResource(R.mipmap.ic_more);
        ui.headView.getActionbar().getBtnRight().setVisibility(View.VISIBLE);
        ui.edMoney.setFilters(new InputFilter[]{new NumRangeInputFilter(this, Integer.MAX_VALUE)});
        ui.tvNotice.setVisibility(View.GONE);
    }

    private void initEvent() {
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                initPopup();
            }
        });

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
                if (money > 0 && money <= MAX_AMOUNT) {
                    ui.btnCommit.setEnabled(true);
                    ui.tvMoney.setText(string);
                    ui.tvNotice.setVisibility(View.GONE);
                } else if (money > MAX_AMOUNT) {
                    ui.btnCommit.setEnabled(false);
                    ui.tvMoney.setText(string);
                    ui.tvNotice.setVisibility(View.VISIBLE);
                    ui.tvNotice.setText(getString(R.string.max_amount_notice));
                } else {
                    ui.btnCommit.setEnabled(false);
                    ui.tvMoney.setText("0.00");
                    ui.tvNotice.setVisibility(View.VISIBLE);
                    ui.tvNotice.setText(getString(R.string.min_amount_notice));
                }

            }
        });

        ui.btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                money = ui.edMoney.getText().toString();
                if (!TextUtils.isEmpty(money)) {
                    note = UIUtils.getRedEnvelopeContent(ui.edContent);
                    actionId = UIUtils.getUUID();
                    sendRedEnvelope(actionId, UIUtils.getFen(money), note);
                }
            }
        });

    }

    private void initPopup() {
        hideKeyboard();
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(ui.headView.getActionbar(), Gravity.BOTTOM, 0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                switch (postsion) {
                    case 0:
                        ARouter.getInstance().build("/app/redEnvelopeDetailsActivity").navigation();
//
//                        Intent intent = new Intent(SingleRedPacketActivity.this, RedPacketDetailsActivity.class);
//                        startActivity(intent);
                        break;
                }
                popupSelectView.dismiss();
            }
        });
    }

    /**
     * 发送单个红包
     */
    private void sendRedEnvelope(final String actionId, long money, final String note) {
        if (uid <= 0) {
            return;
        }
        envelopeBean = initEnvelopeBean(envelopeBean, actionId, -1, System.currentTimeMillis(), PayEnum.ERedEnvelopeType.NORMAL, note, 1, "");
        setSending(true);
        ui.btnCommit.setEnabled(false);
        handler.postDelayed(runnable, WAIT_TIME);
        PayHttpUtils.getInstance().sendRedEnvelopeToUser(actionId, money, 1, 0, note, uid)
                .compose(RxSchedulers.<BaseResponse<UrlBean>>compose())
                .compose(RxSchedulers.<BaseResponse<UrlBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<UrlBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<UrlBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            UrlBean urlBean = baseResponse.getData();
                            if (urlBean != null) {
                                Intent intent = new Intent(SingleRedPacketActivity.this, YiBaoWebActivity.class);
                                intent.putExtra(YiBaoWebActivity.AGM_URL, urlBean.getUrl());
                                startActivityForResult(intent, REQUEST_PAY);

                            }
                        } else {
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

    private void setResultOk() {
        if (envelopeBean != null) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putParcelable("envelope", envelopeBean);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ui.btnCommit.setEnabled(true);
        if (requestCode == REQUEST_PAY && data != null) {
            int result = data.getIntExtra(RESULT, 0);
            if (result == 99) {
                showLoadingDialog();
            }
        }
    }
}
