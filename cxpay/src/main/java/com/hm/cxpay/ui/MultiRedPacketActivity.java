package com.hm.cxpay.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.hm.cxpay.R;
import com.hm.cxpay.dailog.DialogErrorPassword;
import com.hm.cxpay.dailog.DialogInputPayPassword;
import com.hm.cxpay.dailog.DialogSelectPayStyle;
import com.hm.cxpay.databinding.ActivityMultiRedPacketBinding;
import com.hm.cxpay.global.PayEnum;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.bank.BankBean;
import com.hm.cxpay.ui.bank.BindBankActivity;
import com.hm.cxpay.ui.redenvelope.AdapterSelectPayStyle;
import com.hm.cxpay.ui.redenvelope.BaseSendRedEnvelopeActivity;
import com.hm.cxpay.ui.redenvelope.RedSendBean;
import com.hm.cxpay.utils.BankUtils;
import com.hm.cxpay.utils.UIUtils;

import net.cb.cb.library.utils.NumRangeInputFilter;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.PopupSelectView;

import static com.hm.cxpay.global.PayConstants.MAX_AMOUNT;

//发送群红包界面
public class MultiRedPacketActivity extends BaseSendRedEnvelopeActivity implements View.OnClickListener {
    private String[] strings = {"红包记录", "取消"};
    private PopupSelectView popupSelectView;
    @PayEnum.ERedEnvelopeType
    private int redPacketType = PayEnum.ERedEnvelopeType.LUCK;
    private ActivityMultiRedPacketBinding ui;
    private DialogInputPayPassword dialogPayPassword;
    private String gid;
    private int memberCount;
    private DialogSelectPayStyle dialogSelectPayStyle;
    private String money;
    private DialogErrorPassword dialogErrorPassword;

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
        if (dialogPayPassword != null) {
            dialogPayPassword.dismiss();
            dialogPayPassword = null;
        }
        if (dialogSelectPayStyle != null) {
            dialogSelectPayStyle.dismiss();
            dialogSelectPayStyle = null;
        }
        if (dialogErrorPassword != null) {
            dialogErrorPassword.dismiss();
            dialogErrorPassword = null;
        }
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
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
                int count = UIUtils.getRedEnvelopeCount(ui.edRedPacketNum.getText().toString().trim());
                if (money > 0 && money <= MAX_AMOUNT && count > 0) {
                    ui.btnCommit.setEnabled(true);
                    ui.tvMoney.setText(string);
                    ui.tvNotice.setVisibility(View.GONE);
                } else if (money > MAX_AMOUNT) {
                    ui.btnCommit.setEnabled(false);
                    ui.tvMoney.setText(string);
                    ui.tvNotice.setVisibility(View.VISIBLE);
                } else {
                    ui.btnCommit.setEnabled(false);
                    ui.tvMoney.setText("0.00");
                    ui.tvNotice.setVisibility(View.GONE);
                }
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
                if (!TextUtils.isEmpty(string) && count > 0) {
                    ui.btnCommit.setEnabled(true);
                    ui.tvMoney.setText(string);
                } else {
                    ui.btnCommit.setEnabled(false);
                    ui.tvMoney.setText("0.00");
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == ui.tvRedPacketType.getId()) {
            resetRedEnvelope(redPacketType);
        } else if (id == ui.btnCommit.getId()) {
            money = ui.edMoney.getText().toString();
            if (!TextUtils.isEmpty(money)) {
                showInputPasswordDialog(UIUtils.getFen(money));
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


    private void initPopup() {
        hideKeyboard();
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(ui.headView.getActionbar(), Gravity.BOTTOM, 0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                switch (postsion) {
                    case 0:
                        Intent intent = new Intent(MultiRedPacketActivity.this, RedPacketDetailsActivity.class);
                        startActivity(intent);
                        break;
                }
                popupSelectView.dismiss();
            }
        });
    }


    /**
     * 发送单个红包
     */
    private void sendRedEnvelope(String actionId, long money, int count, String psw, int type, String note, long bankCardId) {
        if (TextUtils.isEmpty(gid)) {
            return;
        }
        PayHttpUtils.getInstance().sendRedEnvelopeToGroup(actionId, money, count, psw, type, bankCardId, note, gid)
                .compose(RxSchedulers.<BaseResponse<RedSendBean>>compose())
                .compose(RxSchedulers.<BaseResponse<RedSendBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<RedSendBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<RedSendBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            RedSendBean sendBean = baseResponse.getData();
                            if (sendBean != null) {
                                if (sendBean.getCode() == 1) {//code  1表示成功，2失败，99处理中

                                } else {
                                    ToastUtil.show(getContext(), sendBean.getErrMsg());
                                }
                            }
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        super.onHandleError(baseResponse);
                        if (baseResponse.getCode() == -21000) {
                            showPswErrorDialog();
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }
                    }
                });
    }

    //输入密码弹窗
    private void showInputPasswordDialog(final long money) {
        dialogPayPassword = new DialogInputPayPassword(this, R.style.MyDialogTheme);
        if (BankUtils.isLooseEnough(money)) {
            dialogPayPassword.init(money, PayEnum.EPayStyle.LOOSE, null);
        } else {
            BankBean bank = PayEnvironment.getInstance().getFirstBank();
            if (bank != null) {
                dialogPayPassword.init(money, PayEnum.EPayStyle.BANK, bank);
            } else {
                dialogPayPassword.init(money, PayEnum.EPayStyle.LOOSE, null);
            }
        }
        dialogPayPassword.setPswListener(new DialogInputPayPassword.IPswListener() {
            @Override
            public void onCompleted(String psw, long bankCardId) {
                String note = UIUtils.getRedEnvelopeContent(ui.edContent);
                int count = UIUtils.getRedEnvelopeCount(ui.edRedPacketNum.getText().toString().trim());
                String actionId = UIUtils.getUUID();
                sendRedEnvelope(actionId, money, count, psw, redPacketType, note, bankCardId);
            }

            @Override
            public void selectPayStyle() {
                showSelectPayStyleDialog();
            }
        });
        dialogPayPassword.show();
        showSoftKeyword(dialogPayPassword.getPswView());
    }

    private void showSelectPayStyleDialog() {
        dialogSelectPayStyle = new DialogSelectPayStyle(this, R.style.MyDialogTheme);
        dialogSelectPayStyle.bindData(PayEnvironment.getInstance().getBanks());
        dialogSelectPayStyle.setListener(new AdapterSelectPayStyle.ISelectPayStyleListener() {
            @Override
            public void onSelectPay(int style, BankBean bank) {
                if (dialogPayPassword != null) {
                    dialogPayPassword.init(UIUtils.getFen(money), style, bank);
                }
            }

            @Override
            public void onAddBank() {
                Intent intent = new Intent(MultiRedPacketActivity.this, BindBankActivity.class);
                startActivity(intent);
            }

            @Override
            public void onBack() {
                resetShowDialogPayPassword();
            }
        });
        dialogSelectPayStyle.show();

    }

    //显示密码错误弹窗
    private void showPswErrorDialog() {
        dialogErrorPassword = new DialogErrorPassword(this, R.style.MyDialogTheme);
        dialogErrorPassword.setListener(new DialogErrorPassword.IErrorPasswordListener() {
            @Override
            public void onForget() {
            }

            @Override
            public void onTry() {

                resetShowDialogPayPassword();
            }
        });
        dialogErrorPassword.show();
    }

    //重新显示输入密码弹窗
    private void resetShowDialogPayPassword() {
        if (dialogPayPassword != null) {
            dialogPayPassword.clearPsw();
            dialogPayPassword.show();
            showSoftKeyword(dialogPayPassword.getPswView());
        }
    }


}
