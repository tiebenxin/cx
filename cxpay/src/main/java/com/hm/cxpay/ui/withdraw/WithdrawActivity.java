package com.hm.cxpay.ui.withdraw;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hm.cxpay.R;
import com.hm.cxpay.bean.CommonBean;
import com.hm.cxpay.bean.UrlBean;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.YiBaoWebActivity;
import com.hm.cxpay.ui.bank.SelectBankCardActivity;
import com.hm.cxpay.utils.UIUtils;

import net.cb.cb.library.utils.BigDecimalUtils;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import static com.hm.cxpay.global.PayConstants.REQUEST_PAY;
import static com.hm.cxpay.global.PayConstants.RESULT;
import static com.hm.cxpay.ui.recharege.RechargeActivity.SELECT_BANKCARD;

/**
 * @类名：零钱->提现
 * @Date：2019/11/30
 * @by zjy
 * @备注：
 */
public class WithdrawActivity extends AppActivity {
    private HeadView headView;
    private ActionbarView actionbar;
    private RelativeLayout layoutChangeBankcard;//切换或新增银行卡
    private EditText etWithdraw;//提现金额输入框
    private TextView tvBalance;//余额
    private TextView tvSubmit;//提现
    private TextView tvAccountTime;//预计x小时后到账
    private TextView tvQuestion;//常见问题
    private TextView tvBankName;//银行卡名+尾号
    private TextView tvRateNotice;//服务费提示
    private TextView tvWithdrawAll;//全部提现
    private Activity activity;
    private CommonBean rateBean;//银行卡费率

    private Double minMoney = 1000.0;//最低提现金额，默认10元，单位分
//    private Double maxMoney = 2000 * 1000.0;//最高提现金额，默认2000元，单位分
    private Double serviceMoney = 0.0;//服务费，单位分
    private Double extraMoney = 0.0;//额外固定费，单位分
    private Double rate = 0.005;//费率，默认0.005
    private Double doubleRate = 0.0;//显示费率
    private Double withDrawMoney = 0.0;//用户提现金额
    private double balanceValue = 0;//double类型的余额
    private Double realMoney = 0.0;//实际到账金额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        activity = this;
        initView();
        initData();
        getRates();
        PayEnvironment.getInstance().notifyStampUpdate(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PayEnvironment.getInstance().notifyStampUpdate(true);
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        layoutChangeBankcard = findViewById(R.id.layout_change_bankcard);
        etWithdraw = findViewById(R.id.et_withdraw);
        tvBalance = findViewById(R.id.tv_balance);
        tvSubmit = findViewById(R.id.tv_submit);
        tvAccountTime = findViewById(R.id.tv_account_time);
        tvQuestion = findViewById(R.id.tv_question);
        tvBankName = findViewById(R.id.tv_bank_name);
        tvRateNotice = findViewById(R.id.tv_rate_notice);
        tvWithdrawAll = findViewById(R.id.tv_withdraw_all);
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
        //可提现余额
        final String value = UIUtils.getYuan(Long.valueOf(PayEnvironment.getInstance().getUser().getBalance()));
        balanceValue = Double.valueOf(value);
        tvBalance.setText("可提现余额  ¥" + value);
        //切换银行卡
        layoutChangeBankcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(activity, SelectBankCardActivity.class), SELECT_BANKCARD);
            }
        });
        //提现
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                showLoadingDialog();
                //1 金额不能为空
                String money = etWithdraw.getText().toString();
                if (!TextUtils.isEmpty(money)) {
//                    double mm = Double.valueOf(money);
                    //2 提现金额不低于最低提现金额(默认10元),不高于2000元，由服务端控制
                    httpWithdraw(money);
//                    if (mm >= minMoney && mm <= maxMoney) {
//                        //3 不能超过余额
//                        httpWithdraw(money);
//                    } else if (mm > maxMoney) {
//                        ToastUtil.show(context, "单笔提现金额不高于" + maxMoney + "元");
//                    } else {
//                        ToastUtil.show(context, "最小提现金额不低于" + minMoney + "元");
//                    }
                } else {
                    ToastUtil.show(context, "提现金额不能为空");
                }
            }
        });
        tvAccountTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        tvQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARouter.getInstance().build("/app/HelpActivity").navigation();
            }
        });
        etWithdraw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //1 金额不为空
                if (!TextUtils.isEmpty(etWithdraw.getText().toString())) {
                    //2 格式要正确，单独小数点无法参与计算，以0的小数低于默认最低值10的也不参与计算并过滤掉
                    if (!etWithdraw.getText().toString().equals(".")
                            && !etWithdraw.getText().toString().startsWith("0")) {
                        //3 金额最高限制10000 最低取接口值
                        if (Double.valueOf(etWithdraw.getText().toString()) <= 10000) {
                            withDrawMoney = Double.valueOf(etWithdraw.getText().toString());
                            serviceMoney = Double.valueOf(BigDecimalUtils.add(BigDecimalUtils.mul(withDrawMoney + "", rate + "", 2), extraMoney + "", 2));
                            realMoney = Double.valueOf(BigDecimalUtils.sub(withDrawMoney + "", serviceMoney + "", 2));
                            doubleRate = rate * 100;
                            //实际值以分为单位，显示转为元
                            tvRateNotice.setText("服务费 " + serviceMoney + "元 (服务费=提现金额x" + doubleRate + "%+" + extraMoney + "元/笔)");
                            tvSubmit.setText("提现 (实际到账金额 " + realMoney + ")");
                        } else {
                            ToastUtil.show(activity, "单笔最高不能超过10000元");
                            etWithdraw.setText("");
                        }
                    } else {
                        ToastUtil.show(activity, "请输入正确格式的金额");
                        etWithdraw.setText("");
                    }
                } else {
                    tvRateNotice.setText("服务费 0.0元 (服务费=提现金额x" + rate * 100 + "%+" + extraMoney + "元/笔)");
                    tvSubmit.setText("提现 (实际到账金额 0.0)");
                }
            }
        });
        tvWithdrawAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (balanceValue > 0) {
                    etWithdraw.setText(balanceValue + "");
                    etWithdraw.setSelection(etWithdraw.getText().length());
                } else {
                    ToastUtil.show(context, "您的可提现余额不足");
                }
            }
        });
    }

    /**
     * 请求->提现
     */
    private void httpWithdraw(final String money) {
        PayHttpUtils.getInstance().toWithdraw(money)
                .compose(RxSchedulers.<BaseResponse<UrlBean>>compose())
                .compose(RxSchedulers.<BaseResponse<UrlBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<UrlBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<UrlBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            LogUtil.writeLog("支付--提现--money=" + money + "--time" + System.currentTimeMillis());
                            if (baseResponse.getData() != null) {
                                //1 成功 99 处理中
                                UrlBean urlBean = baseResponse.getData();
                                Intent intent = new Intent(WithdrawActivity.this, YiBaoWebActivity.class);
                                intent.putExtra(YiBaoWebActivity.AGM_URL, urlBean.getUrl());
                                startActivityForResult(intent, REQUEST_PAY);
                            }
                        } else {
                            ToastUtil.show(context, baseResponse.getMessage());
                        }
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        ToastUtil.show(context, baseResponse.getMessage());
                        dismissLoadingDialog();
                    }
                });

    }

    /**
     * 请求->获取系统费率
     */
    private void getRates() {
        PayHttpUtils.getInstance().getRate()
                .compose(RxSchedulers.<BaseResponse<CommonBean>>compose())
                .compose(RxSchedulers.<BaseResponse<CommonBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<CommonBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<CommonBean> baseResponse) {
                        rateBean = null;
                        if (baseResponse.getData() != null) {
                            rateBean = baseResponse.getData();
                            minMoney = Double.valueOf(rateBean.getMinAmt()) / 100;
                            extraMoney = Double.valueOf(rateBean.getServiceFee()) / 100;
                            etWithdraw.setHint("最小提现金额不低于" + minMoney + "元");
                            if (!TextUtils.isEmpty(rateBean.getRate())) {
                                rate = Double.valueOf(rateBean.getRate());
                                tvRateNotice.setText("服务费 0.0元 (服务费=提现金额x" + rate * 100 + "%+" + extraMoney + "元/笔)");
                            }
                        } else {
                            rateBean = new CommonBean();
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse<CommonBean> baseResponse) {
                        showFailDialog();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_PAY) {
            int result = data.getIntExtra(RESULT, 0);
            if (result == 99) {
                httpGetUserInfo();
                Intent intent = new Intent(activity, WithdrawSuccessActivity.class);
                intent.putExtra("bank_name", tvBankName.getText().toString());
                intent.putExtra("withdraw_money", etWithdraw.getText().toString());
                intent.putExtra("service_fee", serviceMoney + "");
                intent.putExtra("get_money", realMoney + "");
                startActivity(intent);
            } else {
                Toast.makeText(context, "提现失败!如有疑问，请联系客服", Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * 获取费率失败弹框 (特殊样式，暂不复用)
     */
    private void showFailDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setCancelable(false);//取消点击外部消失弹窗
        final AlertDialog dialog = dialogBuilder.create();
        //获取界面
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_get_rate_fail, null);
        //初始化控件
        TextView tvSure = dialogView.findViewById(R.id.tv_sure);
        //显示和点击事件
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
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
        lp.width = (int) (metrics.widthPixels * 0.8);
        dialog.getWindow().setAttributes(lp);
        dialog.setContentView(dialogView);
    }

    /**
     * 请求->获取用户信息
     */
    private void httpGetUserInfo() {
        long uid = PayEnvironment.getInstance().getUserId();
        if (uid <= 0) {
            return;
        }
        PayHttpUtils.getInstance().getUserInfo(uid)
                .compose(RxSchedulers.<BaseResponse<UserBean>>compose())
                .compose(RxSchedulers.<BaseResponse<UserBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<UserBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<UserBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            if (baseResponse.getData() != null) {
                                UserBean userBean = baseResponse.getData();
                                PayEnvironment.getInstance().setUser(userBean);
                                tvBalance.setText("可提现余额  ¥" + UIUtils.getYuan(Long.valueOf(userBean.getBalance())));
                            }
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse<UserBean> baseResponse) {
                        ToastUtil.show(context, baseResponse.getMessage());
                    }
                });
    }

}
