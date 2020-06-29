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
import com.hm.cxpay.databinding.ActivityMultiRedPacketBinding;
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
import static com.hm.cxpay.global.PayConstants.MIN_AMOUNT;
import static com.hm.cxpay.global.PayConstants.REQUEST_PAY;
import static com.hm.cxpay.global.PayConstants.RESULT;
import static com.hm.cxpay.global.PayConstants.TOTAL_MAX_AMOUNT;
import static com.hm.cxpay.global.PayConstants.WAIT_TIME;

//发送群红包界面
public class MultiRedPacketActivity extends BaseSendRedEnvelopeActivity implements View.OnClickListener {
    private String[] strings = {"红包记录", "取消"};
    private PopupSelectView popupSelectView;
    @PayEnum.ERedEnvelopeType
    private int redPacketType = PayEnum.ERedEnvelopeType.LUCK;
    private ActivityMultiRedPacketBinding ui;
    private String gid;
    private int memberCount;
    private String money;
    private DialogErrorPassword dialogErrorPassword;
    private CxEnvelopeBean envelopeBean;
    private String note;
    private String actionId;
    private int count;


    /**
     * @param gid         群id
     * @param memberCount 群成员数
     */
    public static Intent newIntent(Context context, String gid, int memberCount) {
        Intent intent = new Intent(context, MultiRedPacketActivity.class);
        intent.putExtra("gid", gid);
        intent.putExtra("count", memberCount);
        return intent;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_multi_red_packet);
        Intent intent = getIntent();
        gid = intent.getStringExtra("gid");
        memberCount = intent.getIntExtra("count", 0);
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
        ui.btnCommit.setEnabled(false);
        ui.headView.getActionbar().setTxtLeft("取消");
        ui.headView.getActionbar().getBtnLeft().setVisibility(View.GONE);
        ui.headView.getActionbar().getBtnRight().setImageResource(R.mipmap.ic_more);
        ui.headView.getActionbar().getBtnRight().setVisibility(View.VISIBLE);
        ui.edMoney.setFilters(new InputFilter[]{new NumRangeInputFilter(this)});
        ui.edRedPacketNum.setFilters(new InputFilter[]{new NumRangeInputFilter(this, Integer.MAX_VALUE)});
        ui.tvPeopleNumber.setText("本群共" + memberCount + "人");
        intRedPacketType(redPacketType);
        ui.tvNotice.setVisibility(View.GONE);

    }

    private void initEvent() {
        ui.btnCommit.setOnClickListener(this);
        ui.tvRedPacketType.setOnClickListener(this);
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                if (!isSending) {
                    onBackPressed();
                }
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
                int count = UIUtils.getRedEnvelopeCount(ui.edRedPacketNum.getText().toString().trim());
                updateCommitUI(money, count);
            }
        });

        ui.edRedPacketNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = ui.edMoney.getText().toString().trim();
                int count = UIUtils.getRedEnvelopeCount(s.toString().trim());
                long money = UIUtils.getFen(string);
                updateCommitUI(money, count);
            }
        });
    }

    private void updateCommitUI(long money, int count) {
        long totalMoney;
        double singleMoney;
        if (redPacketType == PayEnum.ERedEnvelopeType.NORMAL) {
            totalMoney = money * count;
            singleMoney = money;
        } else {
            totalMoney = money;
            if (count > 0) {
                singleMoney = money * 1.00 / count;
            } else {
                singleMoney = 0;
            }
        }

        if (totalMoney > TOTAL_MAX_AMOUNT) {
            ui.btnCommit.setEnabled(false);
            ui.tvMoney.setText(UIUtils.getYuan(totalMoney));
            ui.tvNotice.setVisibility(View.VISIBLE);
            ui.tvNotice.setText(getString(R.string.total_max_amount_notice));
        } else {
           /* if (count <= 0) {
                ui.btnCommit.setEnabled(false);
                ui.tvMoney.setText("0.00");
                ui.tvNotice.setVisibility(View.GONE);
            } else {*/
            if (memberCount > 0 && memberCount <= 100 && count > memberCount) {
                ui.btnCommit.setEnabled(false);
                ui.tvMoney.setText(UIUtils.getYuan(totalMoney));
                ui.tvNotice.setVisibility(View.VISIBLE);
                ui.tvNotice.setText(getString(R.string.more_than_member_count));
            } else if (count > 100) {
                ui.btnCommit.setEnabled(false);
                ui.tvMoney.setText(UIUtils.getYuan(totalMoney));
                ui.tvNotice.setVisibility(View.VISIBLE);
                ui.tvNotice.setText("单次最多发送100个红包");
            } else {
                if (singleMoney == 0) {
                    if (redPacketType == PayEnum.ERedEnvelopeType.NORMAL) {
                        ui.tvNotice.setVisibility(View.VISIBLE);
                        ui.tvNotice.setText(getString(R.string.min_amount_notice));
                        ui.tvMoney.setText("0.00");
                    } else {
                        ui.tvNotice.setVisibility(View.GONE);
                        ui.tvMoney.setText(UIUtils.getYuan(totalMoney));
                    }
                    ui.btnCommit.setEnabled(false);
                } else if (singleMoney > MAX_AMOUNT) {
                    ui.btnCommit.setEnabled(false);
                    ui.tvMoney.setText(UIUtils.getYuan(totalMoney));
                    ui.tvNotice.setVisibility(View.VISIBLE);
                    ui.tvNotice.setText(getString(R.string.group_max_amount_notice));
                } else if (singleMoney > 0 && singleMoney < MIN_AMOUNT) {
                    ui.btnCommit.setEnabled(false);
                    ui.tvMoney.setText(UIUtils.getYuan(totalMoney));
                    ui.tvNotice.setVisibility(View.VISIBLE);
                    ui.tvNotice.setText(getString(R.string.min_amount_notice));
                } else {
                    ui.btnCommit.setEnabled(true);
                    ui.tvMoney.setText(UIUtils.getYuan(totalMoney));
                    ui.tvNotice.setVisibility(View.GONE);
                }
            }
//            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == ui.tvRedPacketType.getId()) {
            resetRedEnvelope(redPacketType);
            resetMoney();
        } else if (id == ui.btnCommit.getId()) {
            if (ViewUtils.isFastDoubleClick()) {
                return;
            }
            money = ui.edMoney.getText().toString();
            count = UIUtils.getRedEnvelopeCount(ui.edRedPacketNum.getText().toString().trim());
            long totalMoney = 0;
            if (redPacketType == PayEnum.ERedEnvelopeType.NORMAL) {
                totalMoney = UIUtils.getFen(money) * count;
            } else {
                totalMoney = UIUtils.getFen(money);
            }
            if (totalMoney > 0) {
                note = UIUtils.getRedEnvelopeContent(ui.edContent);
                actionId = UIUtils.getUUID();
                sendRedEnvelope(actionId, totalMoney, count, redPacketType, note);
            }
        }
    }

    private void intRedPacketType(int type) {
        if (type == PayEnum.ERedEnvelopeType.LUCK) {
            ui.tvRedPacketTypeTitle.setText("当前为拼手气红包，改为");
            ui.tvRedPacketType.setText("普通红包");
            ui.tvMoneyTitle.setText("总金额");
        } else {
            ui.tvRedPacketTypeTitle.setText("当前为普通红包，改为");
            ui.tvRedPacketType.setText("拼手气红包");
            ui.tvMoneyTitle.setText("单个金额");
        }
    }

    private void resetRedEnvelope(int type) {
        if (type == PayEnum.ERedEnvelopeType.LUCK) {
            redPacketType = PayEnum.ERedEnvelopeType.NORMAL;
            intRedPacketType(redPacketType);
        } else {
            redPacketType = PayEnum.ERedEnvelopeType.LUCK;
            intRedPacketType(redPacketType);
        }
    }

    private void resetMoney() {
        long money = UIUtils.getFen(ui.edMoney.getText().toString().trim());
        int count = UIUtils.getRedEnvelopeCount(ui.edRedPacketNum.getText().toString().trim());
        updateCommitUI(money, count);
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
                        break;
                }
                popupSelectView.dismiss();
            }
        });
    }


    /**
     * 发送群红包
     */
    private void sendRedEnvelope(final String actionId, long money, final int count, int type, final String note) {
        if (TextUtils.isEmpty(gid)) {
            return;
        }
        envelopeBean = initEnvelopeBean(envelopeBean, actionId, -1, System.currentTimeMillis(), type, note, count, "");
        ui.btnCommit.setEnabled(false);
        setSending(true);
        showLoadingDialog();
        handler.postDelayed(runnable, WAIT_TIME);
        PayHttpUtils.getInstance().sendRedEnvelopeToGroup(actionId, money, count, type, note, gid)
                .compose(RxSchedulers.<BaseResponse<UrlBean>>compose())
                .compose(RxSchedulers.<BaseResponse<UrlBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<UrlBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<UrlBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            UrlBean urlBean = baseResponse.getData();
                            if (urlBean != null) {
                                Intent intent = new Intent(MultiRedPacketActivity.this, YiBaoWebActivity.class);
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
