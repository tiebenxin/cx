package com.hm.cxpay.ui.bank;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.bean.BankBean;
import com.hm.cxpay.databinding.ActivityBankSettingBinding;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.widget.PswView;

import net.cb.cb.library.base.AbstractRecyclerAdapter;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;

import java.util.List;

/**
 * @author Liszt
 * @date 2019/11/30
 * Description
 */
public class BankSettingActivity extends BasePayActivity {

    public static final int REQUEST_BIND = 1;
    public static final int DELETE_BANK_CARD = 2;

    private ActivityBankSettingBinding ui;
    private AdapterBankList adapter;
    private int cardNum = 0;//最新的银行卡数
    private AlertDialog checkPaywordDialog;
    private String bankcardId = "";//银行卡id
    private int deletePosition = 0;//删除项

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_bank_setting);
        adapter = new AdapterBankList(this,1);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        ui.recyclerView.setLayoutManager(manager);
        ui.recyclerView.setAdapter(adapter);
        ui.llAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                IntentUtil.gotoActivity(BankSettingActivity.this, BindBankActivity.class);
                Intent intent = new Intent(BankSettingActivity.this, BindBankActivity.class);
                startActivityForResult(intent, REQUEST_BIND);
            }
        });

        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        getBankList();

        adapter.setItemClickListener(new AbstractRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object bean) {
                if (bean instanceof BankBean) {
                    BankBean bankBean = (BankBean) bean;
                    Intent intent = new Intent(BankSettingActivity.this, BankDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("bank", bankBean);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,DELETE_BANK_CARD);

                }

            }
        });
        //移除银行卡
        adapter.setDeleteClickListener(new AdapterBankList.DeleteClickListener() {
            @Override
            public void onDeleteClick(String cardId,int position) {
                bankcardId = cardId;
                deletePosition = position;
                showBottomDialog();
            }
        });


    }

    private void getBankList() {
        PayHttpUtils.getInstance().getBankList()
                .compose(RxSchedulers.<BaseResponse<List<BankBean>>>compose())
                .compose(RxSchedulers.<BaseResponse<List<BankBean>>>handleResult())
                .subscribe(new FGObserver<BaseResponse<List<BankBean>>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<List<BankBean>> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            List<BankBean> info = baseResponse.getData();
                            if (info != null) {
                                adapter.bindData(info);
                                cardNum = info.size();
                            }

                        } else {

                            ToastUtil.show(BankSettingActivity.this, baseResponse.getMessage());
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BIND || requestCode == DELETE_BANK_CARD) {
            if (resultCode == RESULT_OK) {
                getBankList();//重新获取银行列表
            }
        }
    }

    /**
     * 底部选择弹框
     */
    private void showBottomDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BankSettingActivity.this);
        dialogBuilder.setCancelable(true);
        final AlertDialog dialog = dialogBuilder.create();
        //获取界面
        View dialogView = LayoutInflater.from(BankSettingActivity.this).inflate(R.layout.dialog_delete_bankcard, null);
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

    /**
     * 提示弹框->是否确认删除
     */
    private void showDeleteBankcardDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BankSettingActivity.this);
        dialogBuilder.setCancelable(false);
        final AlertDialog dialog = dialogBuilder.create();
        //获取界面
        View dialogView = LayoutInflater.from(BankSettingActivity.this).inflate(R.layout.dialog_delete_banckcard_notice, null);
        //初始化控件
        TextView tvSure = dialogView.findViewById(R.id.tv_sure);
        TextView tvCancle = dialogView.findViewById(R.id.tv_cancle);
        //显示和点击事件
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCheckPaywordDialog();
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
        dialog.getWindow().setAttributes(lp);
        dialog.setContentView(dialogView);
    }

    /**
     * 提示弹框->校验支付密码
     */
    private void showCheckPaywordDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BankSettingActivity.this);
        dialogBuilder.setCancelable(false);
        checkPaywordDialog = dialogBuilder.create();
        //获取界面
        View dialogView = LayoutInflater.from(BankSettingActivity.this).inflate(R.layout.dialog_check_payword, null);
        //初始化控件
        ImageView ivClose = dialogView.findViewById(R.id.iv_close);
        final PswView pswView = dialogView.findViewById(R.id.psw_view);
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
                        if (baseResponse.getCode() == (-21000)) {
                            ToastUtil.show(BankSettingActivity.this, "支付密码校验失败！");
                        }else {
                            ToastUtil.show(BankSettingActivity.this, baseResponse.getMessage());
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
        PayHttpUtils.getInstance().deleteBankcard(bankcardId+"")
                .compose(RxSchedulers.<BaseResponse>compose())
                .compose(RxSchedulers.<BaseResponse>handleResult())
                .subscribe(new FGObserver<BaseResponse>() {
                    @Override
                    public void onHandleSuccess(BaseResponse baseResponse) {
                        ToastUtil.show(BankSettingActivity.this, "解绑成功!");
                        checkPaywordDialog.dismiss();
                        //刷新
                        adapter.removeView(deletePosition);
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        ToastUtil.show(BankSettingActivity.this, baseResponse.getMessage());
                    }
                });
    }

}
