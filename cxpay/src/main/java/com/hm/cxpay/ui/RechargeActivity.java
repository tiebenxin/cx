package com.hm.cxpay.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hm.cxpay.R;
import com.hm.cxpay.widget.PswView;

import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

/**
 * @类名：零钱->充值
 * @Date：2019/11/29
 * @by zjy
 * @备注：
 */
public class RechargeActivity extends AppActivity {

    private HeadView headView;
    private ActionbarView actionbar;
    private TextView tvBalance;//我的余额
    private EditText etRecharge;//充值金额
    private TextView tvSubmit;//支付
    private TextView tvSelectOne;//选中10
    private TextView tvSelectTwo;//选中20
    private TextView tvSelectThree;//选中30
    private TextView tvSelectFour;//选中100
    private TextView tvSelectFive;//选中200
    private TextView tvSelectSix;//选中500
    private Context activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_recharge);
        initView();
        initData();
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        tvBalance = findViewById(R.id.tv_balance);
        etRecharge = findViewById(R.id.et_recharge);
        tvSubmit = findViewById(R.id.tv_submit);
        tvSelectOne = findViewById(R.id.tv_select_one);
        tvSelectTwo = findViewById(R.id.tv_select_two);
        tvSelectThree = findViewById(R.id.tv_select_three);
        tvSelectFour = findViewById(R.id.tv_select_four);
        tvSelectFive = findViewById(R.id.tv_select_five);
        tvSelectSix = findViewById(R.id.tv_select_six);
        actionbar = headView.getActionbar();
    }

    private void initData() {
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(etRecharge.getText().toString())){
                    if(Double.valueOf(etRecharge.getText().toString()) >=1.0){
                        //TODO 判断是否添加过银行卡
                        boolean ifAddBankcard = false;
                        //1 已经添加过银行卡
                        if(ifAddBankcard){
                            showRechargeDialog(2);
                        }else {
                            //2 没有添加过银行卡
                            showRechargeDialog(1);
                        }
                    }else {
                        ToastUtil.show(context,"最低充值金额1元");
                    }
                }else {
                    ToastUtil.show(context,"充值金额不能为空");
                }

            }
        });
        tvSelectOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRecharge.setText("10");
                etRecharge.setSelection(etRecharge.getText().length());
                clearSelectedStatus();
                tvSelectOne.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectOne.setTextColor(getResources().getColor(R.color.white));
            }
        });
        tvSelectTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRecharge.setText("20");
                etRecharge.setSelection(etRecharge.getText().length());
                clearSelectedStatus();
                tvSelectTwo.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectTwo.setTextColor(getResources().getColor(R.color.white));
            }
        });
        tvSelectThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRecharge.setText("30");
                etRecharge.setSelection(etRecharge.getText().length());
                clearSelectedStatus();
                tvSelectThree.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectThree.setTextColor(getResources().getColor(R.color.white));
            }
        });
        tvSelectFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRecharge.setText("100");
                etRecharge.setSelection(etRecharge.getText().length());
                clearSelectedStatus();
                tvSelectFour.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectFour.setTextColor(getResources().getColor(R.color.white));
            }
        });
        tvSelectFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRecharge.setText("200");
                etRecharge.setSelection(etRecharge.getText().length());
                clearSelectedStatus();
                tvSelectFive.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectFive.setTextColor(getResources().getColor(R.color.white));
            }
        });
        tvSelectSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRecharge.setText("500");
                etRecharge.setSelection(etRecharge.getText().length());
                clearSelectedStatus();
                tvSelectSix.setBackgroundResource(R.drawable.shape_5radius_solid_517da2);
                tvSelectSix.setTextColor(getResources().getColor(R.color.white));
            }
        });

    }

    /**
     * 清除其他选中状态
     */
    public void clearSelectedStatus(){
        tvSelectOne.setBackgroundResource(R.drawable.shape_5radius_stroke_517da2);
        tvSelectTwo.setBackgroundResource(R.drawable.shape_5radius_stroke_517da2);
        tvSelectThree.setBackgroundResource(R.drawable.shape_5radius_stroke_517da2);
        tvSelectFour.setBackgroundResource(R.drawable.shape_5radius_stroke_517da2);
        tvSelectFive.setBackgroundResource(R.drawable.shape_5radius_stroke_517da2);
        tvSelectSix.setBackgroundResource(R.drawable.shape_5radius_stroke_517da2);
        tvSelectOne.setTextColor(getResources().getColor(R.color.c_517da2));
        tvSelectTwo.setTextColor(getResources().getColor(R.color.c_517da2));
        tvSelectThree.setTextColor(getResources().getColor(R.color.c_517da2));
        tvSelectFour.setTextColor(getResources().getColor(R.color.c_517da2));
        tvSelectFive.setTextColor(getResources().getColor(R.color.c_517da2));
        tvSelectSix.setTextColor(getResources().getColor(R.color.c_517da2));
    }

    /**
     *   两种类型弹框
     * 1 添加银行卡->没绑定过
     * 2 输入支付密码->绑定过
     */
    public void showRechargeDialog(int type) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setCancelable(false);//取消点击外部消失弹窗
        if(type==1){
            final AlertDialog dialog = dialogBuilder.create();
            //获取界面
            View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_add_bankcard, null);
            //初始化控件
            ImageView ivClose = dialogView.findViewById(R.id.iv_close);
            TextView tvRechargeValue = dialogView.findViewById(R.id.tv_recharge_value);
            LinearLayout layoutAddBankcard = dialogView.findViewById(R.id.layout_add_bankcard);
            //显示和点击事件
            tvRechargeValue.setText("￥"+etRecharge.getText().toString());
            ivClose.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            layoutAddBankcard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 跳 添加银行卡
                    ToastUtil.show(activity,"添加银行卡");
                }
            });
            //展示界面
            dialog.show();
            //解决圆角shape背景无效问题
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置宽高
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.height = DensityUtil.dip2px(activity, 195);
            lp.width = DensityUtil.dip2px(activity, 277);
            dialog.getWindow().setAttributes(lp);
            dialog.setContentView(dialogView);
        }else if(type==2){
            final AlertDialog dialog = dialogBuilder.create();
            View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_pay_psw, null);
            ImageView ivClose = dialogView.findViewById(R.id.iv_close);
            TextView tvRechargeValue = dialogView.findViewById(R.id.tv_recharge_value);
            LinearLayout layoutChangeBankcard = dialogView.findViewById(R.id.layout_change_bankcard);
            final PswView pswView = dialogView.findViewById(R.id.psw_view);
            //充值金额
            tvRechargeValue.setText("￥"+etRecharge.getText().toString());
            //关闭弹框
            ivClose.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            //输入支付密码
            pswView.setOnPasswordChangedListener(new PswView.onPasswordChangedListener() {
                @Override
                public void setPasswordChanged(String password) {
                    //TODO 验证支付密码 + 充值成功/失败
                    ToastUtil.show(activity,"支付密码是"+password);
                }
            });
            //切换其他银行卡来支付
            layoutChangeBankcard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 跳 我的银行卡
                    ToastUtil.show(activity,"切换其他银行卡来支付");
                }
            });
            dialog.show();
            //强制唤起软键盘
            if(pswView!=null){
                pswView.setFocusable(true);
                pswView.setFocusableInTouchMode(true);
                pswView.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) pswView
                        .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(pswView, 0);
            }
            //解决dialog里edittext不响应键盘的问题
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.height = DensityUtil.dip2px(activity, 277);
            lp.width = DensityUtil.dip2px(activity, 277);
            dialog.getWindow().setAttributes(lp);
            dialog.setContentView(dialogView);
        }
    }


}
