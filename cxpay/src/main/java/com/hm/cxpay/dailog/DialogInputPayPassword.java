package com.hm.cxpay.dailog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hm.cxpay.R;
import com.hm.cxpay.global.PayEnum;
import com.hm.cxpay.ui.bank.BankBean;
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
    private ImageView ivClose;
    private LinearLayout llPayStyle;

    public DialogInputPayPassword(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_input_pay_pwd);
        ivClose = findViewById(R.id.iv_close);
        tvMoney = findViewById(R.id.tv_money);
        tvPayer = findViewById(R.id.tv_payer);
        ivIcon = findViewById(R.id.iv_icon);
        pswView = findViewById(R.id.psw_view);
        llPayStyle = findViewById(R.id.ll_pay_style);
        pswView.setOnPasswordChangedListener(new PswView.onPasswordChangedListener() {
            @Override
            public void setPasswordChanged(String password) {
                if (!TextUtils.isEmpty(password)) {
                    if (listener != null) {
                        if (payStyle == PayEnum.EPayStyle.LOOSE) {
                            listener.onCompleted(password, -1);
                        } else if (payStyle == PayEnum.EPayStyle.BANK && bankBean != null) {
                            listener.onCompleted(password, bankBean.getId());
                        }
                    }
                }
            }
        });

        ivClose.setOnClickListener(this);
        llPayStyle.setOnClickListener(this);

//        pswView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                pswView.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                int width = pswView.getMeasuredWidth();
//                int height = width / 6;
//                params.weight = width;
//                params.height = height;
//                pswView.setLayoutParams(params);
//            }
//        }, 100);


    }

    @Override
    public void processClick(View view) {
        int id = view.getId();
        if (id == ivClose.getId()) {
            dismiss();
        } else if (id == llPayStyle.getId()) {
            dismiss();
            if (listener != null) {
                listener.selectPayStyle();
            }
        }
    }

    public void init(long money, @PayEnum.EPayStyle int payStyle, BankBean info) {
        this.payStyle = payStyle;
        if (info != null) {
            bankBean = info;
        }
        tvMoney.setText("￥" + UIUtils.getYuan(money));
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

    //清空密码，重新输入
    public void clearPsw() {
        if (pswView != null) {
            pswView.clear();
        }
    }

    public View getPswView() {
        return pswView;
    }

    public void setPswListener(IPswListener l) {
        listener = l;
    }

    public interface IPswListener {
        /**
         * 支付密码完成
         * 注意：bankCardId 银行卡id，不是卡号
         */
        void onCompleted(String psw, long bankCardId);

        //选择支付方式
        void selectPayStyle();
    }
}