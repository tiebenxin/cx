package com.hm.cxpay.ui.bank;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.bean.BankBean;
import com.hm.cxpay.dailog.CommonSelectDialog;
import com.hm.cxpay.dailog.DialogErrorPassword;
import com.hm.cxpay.databinding.ActivityBankDetailBinding;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.payword.ForgetPswStepOneActivity;
import com.hm.cxpay.widget.PswView;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;

/**
 * @author Liszt
 * @date 2019/11/30
 * Description
 */
public class BankDetailActivity extends BasePayActivity {

    private ActivityBankDetailBinding ui;
    private Context activity;

    private long bankcardId = 0l;//银行卡id
    private AlertDialog checkPaywordDialog;
    private DialogErrorPassword dialogErrorPassword;
    private PswView pswView;
    private CommonSelectDialog.Builder builder;
    private CommonSelectDialog dialogOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        ui = DataBindingUtil.setContentView(this, R.layout.activity_bank_detail);
        BankBean bank = getIntent().getParcelableExtra("bank");
        if (bank != null) {
            if(!TextUtils.isEmpty(bank.getLogo())){
                Glide.with(this).load(bank.getLogo()).into(ui.ivIcon);
            }else {
                ui.ivIcon.setImageResource(R.mipmap.ic_bank_zs);
            }
            ui.tvBankName.setText(bank.getBankName());
            bankcardId =  bank.getId();
            ui.tvBankNum.setText(bank.getCardNo());
            ui.tvBankType.setText("借记卡");
        }

        ui.headView.getActionbar().getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        ui.headView.getActionbar().getBtnRight().setVisibility(View.VISIBLE);
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                showBottomDialog();
            }
        });
        builder = new CommonSelectDialog.Builder(activity);
    }

    /**
     * 底部选择弹框
     */
    private void showBottomDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setCancelable(true);
        final AlertDialog dialog = dialogBuilder.create();
        //获取界面
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_delete_bankcard, null);
        //初始化控件
        TextView tvDeleteBankcard = dialogView.findViewById(R.id.tv_delete_bankcard);
        TextView tvCancle = dialogView.findViewById(R.id.tv_cancle);
        //显示和点击事件
        tvDeleteBankcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteBankcardDialog();
                dialog.dismiss();
            }
        });
        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //展示界面
        dialog.show();
        //解决圆角shape背景无效问题
        Window window = dialog.getWindow();
        //底部弹出+动画效果
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.bottom_dialog_anim);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置宽高，高度自适应，宽度填满屏幕
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.setContentView(dialogView);
    }

    @Override
    protected void onDestroy() {
        if (dialogErrorPassword != null) {
            dialogErrorPassword.dismiss();
            dialogErrorPassword = null;
        }
        super.onDestroy();
    }

    /**
     * 提示弹框->是否确认删除
     */
    private void showDeleteBankcardDialog(){
        dialogOne = builder.setTitle("如果存在未结算的交易，24小时之内将\n无法绑定新的银行卡，确定继续解绑银\n行卡?")
                  .setLeftText("取消")
                  .setRightText("确认")
                  .setLeftOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          //取消
                          dialogOne.dismiss();
                      }
                  })
                  .setRightOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          //确认
                          dialogOne.dismiss();
                          showCheckPaywordDialog();
                      }
                  })
                  .build();
        dialogOne.show();
    }

    /**
     * 提示弹框->校验支付密码(特殊样式，暂不复用)
     */
    private void showCheckPaywordDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setCancelable(false);
        checkPaywordDialog = dialogBuilder.create();
        //获取界面
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_check_payword, null);
        //初始化控件
        ImageView ivClose = dialogView.findViewById(R.id.iv_close);
        pswView = dialogView.findViewById(R.id.psw_view);
        //显示和点击事件
        //关闭弹框
        ivClose.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPaywordDialog.dismiss();
            }
        });
        //输入支付密码
        pswView.setOnPasswordChangedListener(new PswView.onPasswordChangedListener() {
            @Override
            public void setPasswordChanged(String payword) {
                httpCheckPayword(payword,pswView);
            }
        });
        //展示界面
        checkPaywordDialog.show();
        //强制唤起软键盘
        showSoftKeyword(pswView);
        //解决dialog里edittext不响应键盘的问题
        checkPaywordDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //解决圆角shape背景无效问题
        Window window = checkPaywordDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //相关配置
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        WindowManager manager = window.getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        //设置宽高，高度自适应，宽度屏幕0.8
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.width = (int) (metrics.widthPixels*0.8);
        checkPaywordDialog.getWindow().setAttributes(lp);
        checkPaywordDialog.setContentView(dialogView);
    }

    /**
     * 发请求->检查支付密码（是否正确）
     */
    private void httpCheckPayword(final String payword, final PswView pswView) {
        PayHttpUtils.getInstance().checkPayword(payword)
                .compose(RxSchedulers.<BaseResponse>compose())
                .compose(RxSchedulers.<BaseResponse>handleResult())
                .subscribe(new FGObserver<BaseResponse>() {
                    @Override
                    public void onHandleSuccess(BaseResponse baseResponse) {
                        //密码正确->开始删除
                        httpDeleteBankcard();
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        if (baseResponse.getCode() == -21000) {
                            showPswErrorDialog(true, baseResponse.getMessage());
                        } else if (baseResponse.getCode() == -21001) {
                            showPswErrorDialog(false, baseResponse.getMessage());
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }
                        pswView.clear();
                    }
                });
    }

    /**
     * 发请求->解绑银行卡
     */
    private void httpDeleteBankcard() {
        //long直接强转int会失精度导致变负数
//        PayHttpUtils.getInstance().deleteBankcard(bankcardId+"")
//                .compose(RxSchedulers.<BaseResponse>compose())
//                .compose(RxSchedulers.<BaseResponse>handleResult())
//                .subscribe(new FGObserver<BaseResponse>() {
//                    @Override
//                    public void onHandleSuccess(BaseResponse baseResponse) {
//                        ToastUtil.show(context, "解绑成功!");
//                        setResult(RESULT_OK);
//                        checkPaywordDialog.dismiss();
//                        finish();
//                    }
//
//                    @Override
//                    public void onHandleError(BaseResponse baseResponse) {
//                        ToastUtil.show(context, baseResponse.getMessage());
//                    }
//                });
    }

    //显示密码错误弹窗
    private void showPswErrorDialog(boolean canRetry, String msg) {
        dialogErrorPassword = new DialogErrorPassword(this, R.style.MyDialogTheme);
        dialogErrorPassword.setCanceledOnTouchOutside(false);
        dialogErrorPassword.setCanRetry(canRetry);
        dialogErrorPassword.setContent(msg);
        dialogErrorPassword.setListener(new DialogErrorPassword.IErrorPasswordListener() {
            @Override
            public void onForget() {
                startActivity(new Intent(activity, ForgetPswStepOneActivity.class).putExtra("from", 5));
            }

            @Override
            public void onTry() {
                //强制唤起软键盘
                showSoftKeyword(pswView);
            }
        });
        dialogErrorPassword.show();
    }



}
