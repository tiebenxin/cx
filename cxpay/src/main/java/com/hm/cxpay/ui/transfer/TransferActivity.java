package com.hm.cxpay.ui.transfer;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.bean.BankBean;
import com.hm.cxpay.bean.SendResultBean;
import com.hm.cxpay.dailog.DialogErrorPassword;
import com.hm.cxpay.dailog.DialogInputTransferPassword;
import com.hm.cxpay.dailog.DialogSelectPayStyle;
import com.hm.cxpay.databinding.ActivityTransferBinding;
import com.hm.cxpay.global.PayEnum;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.bank.BindBankActivity;
import com.hm.cxpay.ui.redenvelope.AdapterSelectPayStyle;
import com.hm.cxpay.utils.BankUtils;
import com.hm.cxpay.utils.UIUtils;

import net.cb.cb.library.utils.ToastUtil;

/**
 * @author Liszt
 * @date 2019/12/19
 * Description
 */
public class TransferActivity extends BasePayActivity {

    private ActivityTransferBinding ui;
    private DialogInputTransferPassword dialogPassword;
    private long toUid;
    private String name;
    private DialogSelectPayStyle dialogSelectPayStyle;
    private DialogErrorPassword dialogErrorPassword;
    private long money;


    public static Intent newIntent(Context context, long uid, String name) {
        Intent intent = new Intent(context, TransferActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("name", name);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_transfer);
        Intent intent = getIntent();
        toUid = intent.getLongExtra("uid", 0);
        name = intent.getStringExtra("name");
        initView();
    }

    private void initView() {
        ui.tvTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String moneyTxt = ui.edMoney.getText().toString();
                money = UIUtils.getFen(moneyTxt);
                if (money > 1) {
                    showInputPasswordDialog(money);
                }
            }
        });
    }


    public void httpSendTransfer(String actionId, long money, String psw, long toUid, String note, long banCardId) {
        PayHttpUtils.getInstance().sendTransfer(actionId, money, psw, toUid, note, banCardId)
                .compose(RxSchedulers.<BaseResponse<SendResultBean>>compose())
                .compose(RxSchedulers.<BaseResponse<SendResultBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<SendResultBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<SendResultBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            SendResultBean sendBean = baseResponse.getData();
                            if (sendBean != null) {
                                if (sendBean.getCode() == 1) {//成功
                                    setResultOk();
                                } else if (sendBean.getCode() == 2) {//失败
                                    ToastUtil.show(getContext(), sendBean.getErrMsg());
                                } else if (sendBean.getCode() == 99) {//待处理
                                    showLoadingDialog();
                                } else if (sendBean.getCode() == -21000) {//密码错误
                                    dialogPassword.clearPsw();
                                    showPswErrorDialog();
                                } else {
                                    ToastUtil.show(getContext(), baseResponse.getMessage());
                                }
                            }
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        super.onHandleError(baseResponse);
                        if (baseResponse.getCode() == -21000) {//密码错误
                            showPswErrorDialog();
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }
                    }
                });
    }

    private void setResultOk() {

    }


    //输入密码弹窗
    private void showInputPasswordDialog(final long money) {
        dialogPassword = new DialogInputTransferPassword(this);
        if (BankUtils.isLooseEnough(money)) {
            dialogPassword.init(money, PayEnum.EPayStyle.LOOSE, null);
        } else {
            BankBean bank = PayEnvironment.getInstance().getFirstBank();
            if (bank != null) {
                dialogPassword.init(money, PayEnum.EPayStyle.BANK, bank);
            } else {
                dialogPassword.init(money, PayEnum.EPayStyle.LOOSE, null);
            }
        }
        dialogPassword.setPswListener(new DialogInputTransferPassword.IPswListener() {
            @Override
            public void onCompleted(String psw, long bankCardId) {
                dialogPassword.dismiss();
                String note = UIUtils.getRedEnvelopeContent(ui.etDescription);
                String actionId = UIUtils.getUUID();
                httpSendTransfer(actionId, money, psw, toUid, note, bankCardId);
            }

            @Override
            public void selectPayStyle() {
                showSelectPayStyleDialog();
            }
        });
        dialogPassword.show();
        showSoftKeyword(dialogPassword.getPswView());
    }

    private void showSelectPayStyleDialog() {
        dialogSelectPayStyle = new DialogSelectPayStyle(this, R.style.MyDialogTheme);
        BankBean selectBank = null;
        if (dialogPassword != null) {
            selectBank = dialogPassword.getSelectedBank();
        }
        dialogSelectPayStyle.bindData(PayEnvironment.getInstance().getBanks(), selectBank);
        dialogSelectPayStyle.setListener(new AdapterSelectPayStyle.ISelectPayStyleListener() {
            @Override
            public void onSelectPay(int style, BankBean bank) {
                dialogSelectPayStyle.dismiss();
                if (dialogPassword != null) {
                    dialogPassword.init(money, style, bank);
                    dialogPassword.show();
                }
            }

            @Override
            public void onAddBank() {
                dialogSelectPayStyle.dismiss();
                Intent intent = new Intent(TransferActivity.this, BindBankActivity.class);
                startActivity(intent);
            }

            @Override
            public void onBack() {
                resetShowDialogPayPassword();
            }
        });
        dialogSelectPayStyle.show();

    }

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
        if (dialogPassword != null) {
            dialogPassword.clearPsw();
            dialogPassword.show();
            showSoftKeyword(dialogPassword.getPswView());
        }
    }

}
