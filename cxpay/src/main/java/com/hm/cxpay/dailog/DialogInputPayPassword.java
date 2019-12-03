package com.hm.cxpay.dailog;

import android.content.Context;
import android.media.Image;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hm.cxpay.R;
import com.hm.cxpay.global.PayEnum;
import com.hm.cxpay.ui.bank.BankBean;
import com.hm.cxpay.ui.bank.BankInfo;
import com.hm.cxpay.utils.UIUtils;
import com.hm.cxpay.widget.PswView;

import net.cb.cb.library.base.BaseDialog;

/**
 * @anthor Liszt
 * @data 2019/12/3
 * Description 输入支付密码dialog
 */
public class DialogInputPayPassword extends BaseDialog {

    private TextView tvMoney;
    private TextView tvPayer;
    private ImageView ivIcon;
    private PswView pswView;
    private IPswListener listener;
    private int payStyle;
    private BankBean bankBean;

    public DialogInputPayPassword(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_input_pay_pwd);
        tvMoney = findViewById(R.id.tv_money);
        tvPayer = findViewById(R.id.tv_payer);
        ivIcon = findViewById(R.id.iv_icon);
        pswView = findViewById(R.id.psw_view);
        pswView.setOnPasswordChangedListener(new PswView.onPasswordChangedListener() {
            @Override
            public void setPasswordChanged(String password) {
                if (!TextUtils.isEmpty(password)) {
                    if (listener != null) {
                        if (payStyle == PayEnum.EPayStyle.LOOSE){
                            listener.onCompleted(password,-1);
                        }else if (payStyle == PayEnum.EPayStyle.BANK && bankBean != null){
//                            listener.onCompleted(password,bankBean.getCardNo());

                        }
                    }
                }
            }
        });
    }

    @Override
    public void processClick(View view) {

    }

    public void init(String money, @PayEnum.EPayStyle int payStyle, BankBean info) {
        this.payStyle = payStyle;
        if (info != null) {
            bankBean = info;
        }
        tvMoney.setText("￥" + money);
        if (payStyle == PayEnum.EPayStyle.LOOSE) {
            tvPayer.setText("零钱");
            ivIcon.setImageDrawable(UIUtils.getDrawable(getContext(), R.mipmap.ic_loose));
        } else {
            if (info != null) {
                tvPayer.setText(info.getBankName());
                Glide.with(getContext()).load(info.getLogo()).into(ivIcon);
            }
        }
    }

    public void setPswListener(IPswListener l) {
        listener = l;
    }

    public interface IPswListener {
        /**
         * 支付密码完成
         */
        void onCompleted(String psw, long bankCardId);
    }
}
